package com.orderon.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.IInventory;
import com.orderon.interfaces.IMaterial;
import com.orderon.interfaces.IOrderItem;
import com.orderon.interfaces.IOutlet;
import com.orderon.interfaces.IPurchase;
import com.orderon.interfaces.IRecipe;
import com.orderon.interfaces.IService;

public class InventoryManager extends AccessManager implements IInventory, IPurchase {
	public InventoryManager(Boolean transactionBased) {
		super(transactionBased);
	}

	@Override
	public JSONObject addPurchase(String billNo, String challanNo, int vendorId, String outletId,
			BigDecimal additionalDiscount, BigDecimal totalDiscount, BigDecimal charge, BigDecimal roundOff,
			BigDecimal totalGst, BigDecimal grandTotal, String purchaseDate, String paymentType, String account,
			String remark) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			String purchaseId = getNextPurchaseId(outletId, INVENTORY_PURCHASED);

			String sql = "INSERT INTO PurchaseLog (purchaseId, billNo, challanNo, vendorId, outletId, additionalDiscount, totalDiscount, charge, roundOff, totalGst, grandTotal, creditBalance, purchaseDate, dateTime, paymentType, account, remark) VALUES ('"
					+ purchaseId + "', '" + escapeString(billNo) + "', '" + escapeString(challanNo) + "', " + vendorId
					+ ", '" + escapeString(outletId) + "', " + additionalDiscount + ", " + totalDiscount + ", " + charge
					+ ", " + roundOff + ", " + totalGst + ", " + grandTotal + ", " + grandTotal + ", '"
					+ escapeString(purchaseDate) + "', '" + LocalDateTime.now() + "', '" + paymentType + "', '"
					+ account + "', '" + escapeString(remark) + "')";

			if (db.executeUpdate(sql, outletId, true).booleanValue()) {
				outObj.put("status", true);
				outObj.put("message", "Purchase Added Successfully.");
				outObj.put("purchaseId", purchaseId);
			} else {
				outObj.put("message",
						"Could not add purchase. Please try again. If problem persists, contact support.");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj;
	}

	public ArrayList<Purchase> getPurchaseHistory(String outletId, String startDate, String endDate) {

		String dateQuery = "";
		if (!endDate.isEmpty()) {
			dateQuery = " AND purchaseDate BETWEEN '" + startDate + "' AND '" + endDate + "' ";
		} else {
			dateQuery = " AND purchaseDate = '" + startDate + "' ";
		}

		String sql = "SELECT PurchaseLog.*, Vendors.name AS vendorName, Outlet.name AS outletName FROM PurchaseLog "
				+ "LEFT OUTER JOIN Vendors ON Vendors.id == PurchaseLog.vendorId "
				+ "Left outer join Outlet on Outlet.outletId == PurchaseLog.outletId " + "WHERE PurchaseLog.outletId='"
				+ escapeString(outletId) + "' " + dateQuery + "ORDER BY purchaseId DESC;";
		return db.getRecords(sql, Purchase.class, outletId);
	}

	public Purchase getPurchaseHistory(String outletId, String purchaseId) {
		String sql = "SELECT PurchaseLog.*, Vendors.name AS vendorName FROM PurchaseLog LEFT OUTER JOIN Vendors ON Vendors.id == PurchaseLog.vendorId LEFT OUTER JOIN Outlet ON Outlet.outletId == PurchaseLog.outletId WHERE PurchaseLog.outletId='"
				+ escapeString(outletId) + "' AND purchaseId = '" + purchaseId + "';";
		return (Purchase) db.getOneRecord(sql, Purchase.class, outletId);
	}

	public ArrayList<Purchase> getCreditPurchasesByVendor(String outletId, int vendorId) {
		String sql = "SELECT * FROM PurchaseLog WHERE outletId='" + escapeString(outletId) + "' AND vendorId = "
				+ vendorId + " AND paymentType = 'CREDIT';";
		return db.getRecords(sql, Purchase.class, outletId);
	}

	public boolean settleCredit(String outletId, String purchaseId, BigDecimal amount, String paymentType,
			String account) {
		Purchase purchase = getPurchaseHistory(outletId, purchaseId);

		String sql = "";
		if (purchase.getCreditBalance().compareTo(amount) == 0) {
			sql = "UPDATE PurchaseLog SET creditBalance = 0, ";
			if (purchase.getCreditBalance().compareTo(purchase.getGrandTotal()) == -1) {
				String[] paymentTypes = purchase.getPaymentType().split("/");
				for (String p : paymentTypes) {
					if (!p.equals("CREDIT")) {
						paymentType = paymentType + "/" + p;
					}
				}
				String[] accounts = purchase.getAccount().split("/");
				for (String p : accounts) {
					account = account + "/" + p;
				}
				sql = sql + "paymentType = '" + paymentType + "', account = '" + account + "' ";
			} else {
				sql = sql + "paymentType = '" + paymentType + "', account = '" + account + "' ";
			}
		} else {
			sql = "UPDATE PurchaseLog SET creditBalance = " + purchase.getCreditBalance().subtract(amount)
					+ ", paymentType = 'CREDIT/" + paymentType + "', account = '" + account + "' ";
		}
		sql = sql + "WHERE outletId='" + escapeString(outletId) + "' AND purchaseId = '" + purchaseId + "';";

		return db.executeUpdate(sql, outletId, true).booleanValue();
	}

	public String getNextPurchaseId(String outletId, String transType) {

		IOutlet dao = new OutletManager(Boolean.valueOf(false));
		Outlet outlet = dao.getOutlet(outletId);
		String code = "";

		String type = "";
		if (transType.equals(INVENTORY_PURCHASED)) {
			type = "PU";
		} else if (transType.equals(INVENTORY_SPOILAGE)) {
			type = "SP";
		} else if (transType.equals(INVENTORY_TRANSFERED)) {
			type = "TR";
		}
		code = outlet.getOutletCode() + type;

		String sql = "SELECT MAX(CAST(SUBSTR(purchaseId," + Integer.toString(code.length() + 1)
				+ ") AS integer)) AS entityId FROM PurchaseLog WHERE purchaseId LIKE '" + code + "%'  AND outletId='"
				+ outletId + "'";

		EntityId entity = (EntityId) db.getOneRecord(sql, EntityId.class,
				outletId);
		if (entity != null) {
			return code + String.format("%04d", new Object[] { Integer.valueOf(entity.getId().intValue() + 1) });
		}
		return code + "0000";
	}

	public void manageInventory(String outletId, String menuId, String orderId, int itemQuantity) {

		IMaterial materialDao = new MaterialManager(Boolean.valueOf(false));
		IRecipe recipeDao = new RecipeManager(Boolean.valueOf(false));
		ArrayList<Recipe> recipeItems = recipeDao.getRecipe(outletId, menuId);
		if(recipeItems == null) {
			return;
		}
		Material material = null;
		Recipe recipe = null;
		ArrayList<Inventory> inventoryLog = null;
		BigDecimal newQuantity = new BigDecimal("0.0");
		BigDecimal newLogQuantity = new BigDecimal("0.0");
		BigDecimal recipeQuantity = new BigDecimal("0.0");
		JSONArray logIds = null;

		for (int i = 0; i < recipeItems.size(); i++) {
			recipe = (Recipe) recipeItems.get(i);
			material = materialDao.getMaterialBySku(outletId, recipe.getSku());

			inventoryLog = getInventoryForDeduction(outletId, recipe.getSku());

			newLogQuantity = recipe.getQuantity().multiply(new BigDecimal(itemQuantity));

			recipeQuantity = recipe.getQuantity().multiply(new BigDecimal(itemQuantity));

			logIds = new JSONArray();
			for (int j = 0; j < inventoryLog.size(); j++) {

				if (newLogQuantity
						.compareTo(((Inventory) inventoryLog.get(j)).getBalanceQuantity()) == -1) {
					newLogQuantity = ((Inventory) inventoryLog.get(j)).getBalanceQuantity()
							.subtract(newLogQuantity);
					updateBalanceQuantity(((Inventory) inventoryLog.get(j)).getId(), outletId,
							newLogQuantity);
					newLogQuantity = new BigDecimal("0");
					logIds.put(((Inventory) inventoryLog.get(j)).getId());
					break;
				}
				newLogQuantity = newLogQuantity
						.subtract(((Inventory) inventoryLog.get(j)).getBalanceQuantity());
				updateBalanceQuantity(((Inventory) inventoryLog.get(j)).getId(), outletId,
						new BigDecimal("0"));
				logIds.put(((Inventory) inventoryLog.get(j)).getId());
			}

			newQuantity = material.getQuantity().subtract(recipeQuantity);

			materialDao.updateQuantity(outletId, recipe.getSku(), newQuantity);

			addUsedUpInventory(recipe.getSku(), recipeQuantity, material.getRatePerUnit(), outletId, orderId, menuId,
					logIds);
		}
	}

	public ArrayList<Inventory> getInventoryRunningOut(String outletId) {
		return null;
	}

	public ArrayList<Inventory> getInventoryLogForItem(String outletId, String orderId, String menuId) {
		return null;
	}

	private ArrayList<Inventory> getInventoryForDeduction(String outletId, int sku) {
		String sql = "SELECT * FROM InventoryLog WHERE sku = " + sku + " AND (type == '" + INVENTORY_ADDED
				+ "' OR type == '" + INVENTORY_PURCHASED + "' AND balanceQuantity > 0) ORDER BY id;";
		return db.getRecords(sql, Inventory.class, outletId);
	}

	public ArrayList<Inventory> getInventoryLogForOrder(String outletId, String orderId) {
		return null;
	}

	public ArrayList<Inventory> getInventoryLogForPurchase(String outletId, String purchaseId) {
		String sql = "SELECT InventoryLog.*, Materials.name AS materialName, Materials.displayableUnit AS displayableUnit, Materials.isCountable AS isCountable, Materials.countableConversion AS countableConversion, Materials.countableUnit AS countableUnit FROM InventoryLog LEFT OUTER JOIN Materials ON InventoryLog.sku == Materials.sku WHERE purchaseId = '"
				+

				purchaseId + "' AND InventoryLog.outletId = '" + outletId + "';";
		return db.getRecords(sql, Inventory.class, outletId);
	}

	public boolean deleteInventoryItem(String outletId, String sku) {
		return false;
	}

	public boolean addSpoilage(int sku, BigDecimal quantity, BigDecimal ratePerUnit, String outletId, JSONArray logId) {
		BigDecimal amount = ratePerUnit.multiply(quantity);
		String sql = "INSERT INTO InventoryLog (sku, type, quantity, ratePerUnit, amount, outletId, dateTime) VALUES ("
				+ sku + ", '" + INVENTORY_SPOILAGE + "', " + quantity + ", " + ratePerUnit + ", " + amount + ", '"
				+ outletId + "', '" + LocalDateTime.now() + "')";

		return db.executeUpdate(sql, outletId, true).booleanValue();
	}

	public boolean addUsedUpInventory(int sku, BigDecimal quantity, BigDecimal ratePerUnit, String outletId,
			String orderId, String menuId, JSONArray logId) {
		BigDecimal amount = ratePerUnit.multiply(quantity);
		String sql = "INSERT INTO InventoryLog (sku, type, quantity, ratePerUnit, amount, outletId, orderId, menuId, dateTime, logId) VALUES ("
				+ sku + ", '" + INVENTORY_USEDUP + "', " + quantity + ", " + ratePerUnit + ", " + amount + ", '"
				+ outletId + "', '" + orderId + "', '" + menuId + "', '" + LocalDateTime.now() + "', '"
				+ logId.toString() + "')";

		return db.executeUpdate(sql, outletId, true).booleanValue();
	}

	public boolean addReturnedInventory(int sku, BigDecimal quantity, BigDecimal ratePerUnit, String outletId,
			String orderId, String menuId, JSONArray logId) {
		BigDecimal amount = ratePerUnit.multiply(quantity);
		String sql = "INSERT INTO InventoryLog (sku, type, quantity, balanceQuantity, ratePerUnit, amount, outletId, orderId, menuId, dateTime, logId) VALUES ("
				+ sku + ", '" + INVENTORY_RETURNED + "', " + quantity + ", " + quantity + ", " + ratePerUnit + ", "
				+ amount + ", '" + outletId + "', '" + orderId + "', '" + menuId + "', '" + LocalDateTime.now() + "', '"
				+ logId.toString() + "')";

		return db.executeUpdate(sql, outletId, true).booleanValue();
	}

	public boolean addNewMaterialToInventory(int sku, BigDecimal quantity, BigDecimal ratePerUnit, String outletId) {
		String sql = "INSERT INTO InventoryLog (sku, type, quantity, ratePerUnit, amount, outletId, balanceQuantity, dateTime) VALUES ("
				+ sku + ", '" + INVENTORY_ADDED + "', " + quantity + ", " + ratePerUnit + ", "
				+ ratePerUnit.multiply(quantity).setScale(3, BigDecimal.ROUND_HALF_UP) + ", '" + outletId + "', " + quantity + ", '"
				+ LocalDateTime.now() + "')";

		return db.executeUpdate(sql, outletId, true).booleanValue();
	}

	public boolean addMaterialAfterPurchase(String purchaseId, int sku, BigDecimal quantity, BigDecimal ratePerUnit,
			String outletId, BigDecimal gst, double gstValue, BigDecimal discount, BigDecimal subTotal,
			BigDecimal totalAmount) {
		String sql = "INSERT INTO InventoryLog (purchaseId, sku, type, quantity, ratePerUnit, outletId, balanceQuantity, gst, gstValue, discount, amount, totalAmount, dateTime) VALUES ('"
				+

				purchaseId + "', " + sku + ", '" + INVENTORY_PURCHASED + "', " + quantity + ", " + ratePerUnit + ", '"
				+ outletId + "', " + quantity + ", " + gst + ", " + gstValue + ", " + discount + ", " + subTotal + ", "
				+ totalAmount + ", '" + LocalDateTime.now() + "')";

		return db.executeUpdate(sql, outletId, true).booleanValue();
	}

	private boolean updateBalanceQuantity(int id, String outletId, BigDecimal quantity) {
		String sql = "UPDATE InventoryLog SET balanceQuantity = " + quantity + " WHERE id = " + id + " AND outletId = '"
				+ outletId + "';";
		return db.executeUpdate(sql, outletId, true).booleanValue();
	}

	public void revertInventoryForReturn(String outletId, String orderId, String menuId, int itemQuantity) {
		IMaterial materialDao = new MaterialManager(Boolean.valueOf(false));
		IRecipe recipeDao = new RecipeManager(Boolean.valueOf(false));
		ArrayList<Recipe> recipeItems = recipeDao.getRecipe(outletId, menuId);
		Material material = null;
		Recipe recipe = null;
		BigDecimal recipeQuantity = new BigDecimal("0.0");

		for (int i = 0; i < recipeItems.size(); i++) {
			recipe = (Recipe) recipeItems.get(i);
			material = materialDao.getMaterialBySku(outletId, recipe.getSku());

			recipeQuantity = recipe.getQuantity().multiply(new BigDecimal(itemQuantity));

			materialDao.updateQuantity(outletId, recipe.getSku(), material.getQuantity().add(recipeQuantity));

			addReturnedInventory(recipe.getSku(), recipeQuantity, material.getRatePerUnit(), outletId, orderId, menuId,
					new JSONArray());
		}
	}

	public void revertInventoryForVoid(String outletId, String orderId) {
		IOrderItem dao = new OrderManager(Boolean.valueOf(false));
		ArrayList<OrderItem> orderItems = dao.getOrderedItems(outletId, orderId, false);
		for (OrderItem orderItem : orderItems) {
			revertInventoryForReturn(outletId, orderId, orderItem.getMenuId(), orderItem.getQuantity());
		}
	}

	public ArrayList<Inventory> getInventoryLog(String outletId) {
		return null;
	}

	public Inventory getMaterialHistory(String outletId, String sku) {
		return null;
	}

	public Inventory getMaterialHistoryForMenuItem(String outletId, String menuId, String sku) {
		return null;
	}

	@Override
	public boolean inventoryCheck(String outletId, JSONArray materialData, String userId) {
		IService serviceDao = new ServiceManager(false);
		String sql = "INSERT INTO InventoryCheckLog (dateTime, serviceDate, materialData, userId) "
				+ "VALUES ('"+LocalDateTime.now()+"', '"+serviceDao.getServiceDate(outletId)+"', '"+materialData.toString()+"', "
				+ "'"+userId+"')";
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public ArrayList<InventoryCheckLog> getInventoryCheckLog(String outletId, String startDate, String endDate) {

		String dateQuery = "";
		if (!endDate.isEmpty()) {
			dateQuery = " AND serviceDate BETWEEN '" + startDate + "' AND '" + endDate + "' ";
		} else {
			dateQuery = " AND serviceDate = '" + startDate + "' ";
		}

		String sql = "SELECT * FROM InventoryCheckLog WHERE outletId='"
				+ escapeString(outletId) + "' " + dateQuery + "ORDER BY id DESC;";
		
		return db.getRecords(sql, InventoryCheckLog.class, outletId);
	}
}
