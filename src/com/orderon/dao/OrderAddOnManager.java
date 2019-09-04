package com.orderon.dao;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.orderon.interfaces.IInventory;
import com.orderon.interfaces.IOrderAddOn;

public class OrderAddOnManager extends AccessManager implements IOrderAddOn{

	public OrderAddOnManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean complimentaryAddOn(String systemId, String outletId, String orderId, String addOnId, String authId, String menuId,
			String subOrderId, String subOrderDate, int itemId, BigDecimal rate, int quantity) {

		if (this.updateOrderAddOnLog(systemId, outletId, orderId, subOrderId, subOrderDate, menuId, itemId, "comp", 1, rate, addOnId)) {
			this.removeAddOn(systemId, orderId, subOrderId, menuId, quantity - 1, addOnId, itemId);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean updateOrderAddOnLog(String systemId, String outletId, String orderId, String subOrderId, String subOrderDate, String menuId, 
			int itemId, String type, int quantity, BigDecimal rate, String addOnId) {

		int state = SUBORDER_STATE_RETURNED;
		if (type.equals("void"))
			state = SUBORDER_STATE_VOIDED;
		else if (type.equals("comp"))
			state = SUBORDER_STATE_COMPLIMENTARY;
		else if (type.equals("comp"))
			state = SUBORDER_STATE_CANCELED;
		
		String sql = "INSERT INTO OrderAddOnLog "
				+ "(systemId, outletId, orderId, subOrderId, subOrderDate, menuId, state, itemId, quantity, rate, addOnId) VALUES('"
				+ systemId + "', '" + outletId + "', '" + escapeString(orderId) + "', " + escapeString(subOrderId) + ", '"
				+ escapeString(subOrderDate) + "', '" + escapeString(menuId) + "', '" + state + "', " + itemId + ", " 
				+ quantity + ", " + rate + ", '" + addOnId + "');";
		if(state == SUBORDER_STATE_CANCELED) {
			IInventory inventoryDao = new InventoryManager(false);
			inventoryDao.revertInventoryForReturn(systemId, orderId, menuId, quantity);
		}
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean addOrderAddon(String systemId, String outletId, Order order, MenuItem menu, int quantity, String addOnId, String subOrderId,
			int itemId, BigDecimal rate) {

		String sql = "INSERT INTO OrderAddOns (systemId, outletId, subOrderId, subOrderDate, addOnId, orderId, menuId, itemId, quantity, rate, state) values ('"
				+ systemId + "', '" + outletId + "', '" + subOrderId + "', '" + (new SimpleDateFormat("yyyy/MM/dd HH:mm")).format(new Date()) + "', '"
				+ addOnId + "','" + order.getOrderId() + "', '" + menu.getMenuId() + "', " + itemId
				+ ", " + Integer.toString(quantity) + ", " + (new DecimalFormat("0.00")).format(rate) + ", " +SUBORDER_STATE_COMPLETE+ ");";
		if (!db.executeUpdate(sql, systemId, true)) {
			return false;
		}

		return true;
	}

	@Override
	public OrderAddOn getOrderedAddOnById(String systemId, String orderId, String subOrderId, String menuId, int itemId,
			String addOnId) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId AS menuId, "
				+ "OrderAddOns.quantity, OrderAddOns.itemId, OrderAddOns.rate AS rate, "
				+ "OrderAddOns.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.menuId='" + menuId + "' AND MenuItems.menuId == OrderAddOns.addOnId "
				+ "AND OrderAddOns.subOrderId='" + subOrderId + "' AND OrderAddOns.itemId == " + itemId + " "
				+ "AND OrderAddOns.addOnId == '" + addOnId + "';";
		return db.getOneRecord(sql, OrderAddOn.class, systemId);
	}

	@Override
	public ArrayList<OrderAddOn> getOrderedAddOns(String systemId, String orderId, String menuId, boolean getReturnedItems) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId, OrderAddOns.state, "
				+ "OrderAddOns.quantity, OrderAddOns.itemId, OrderAddOns.rate, "
				+ "OrderAddOns.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.menuId='" + menuId + "' "
				+ "AND OrderAddOns.addOnId == MenuItems.menuId";
		if(getReturnedItems) {
			sql += " UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId, OrderAddOnLog.state, "
				+ "OrderAddOnLog.quantity AS quantity, OrderAddOnLog.itemId, OrderAddOnLog.rate, "
				+ "OrderAddOnLog.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOnLog, MenuItems "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' AND OrderAddOnLog.menuId='" + menuId + "' "
				+ "AND OrderAddOnLog.addOnId == MenuItems.menuId";
		}
		sql += ";";
		return db.getRecords(sql, OrderAddOn.class, systemId);
	}

	@Override
	public ArrayList<OrderAddOn> getOrderedAddOns(String systemId, String orderId, String subOrderId, String menuId, boolean getReturnedItems) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId, OrderAddOns.state, "
				+ "OrderAddOns.quantity, OrderAddOns.itemId, OrderAddOns.rate, "
				+ "OrderAddOns.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND  OrderAddOns.subOrderId='" + subOrderId + "' AND OrderAddOns.menuId='" + menuId + "' "
				+ "AND OrderAddOns.addOnId == MenuItems.menuId";
		if(getReturnedItems) {
			sql += " UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId, OrderAddOnLog.state, "
				+ "OrderAddOnLog.quantity AS quantity, OrderAddOnLog.itemId, OrderAddOnLog.rate, "
				+ "OrderAddOnLog.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOnLog, MenuItems "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' AND OrderAddOnLog.menuId='" + menuId + "' "
				+ "AND OrderAddOnLog.subOrderId='" + subOrderId + "' "
				+ "AND OrderAddOnLog.addOnId == MenuItems.menuId";
		}
		sql += ";";
		return db.getRecords(sql, OrderAddOn.class, systemId);
	}

	@Override
	public ArrayList<OrderAddOn> getOrderedAddOns(String systemId, String orderId, String subOrderId, String menuId,
			int itemId, boolean getReturnedItems) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId, OrderAddOns.state, "
				+ "OrderAddOns.quantity, OrderAddOns.itemId, OrderAddOns.rate, "
				+ "OrderAddOns.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.menuId='" + menuId + "' "
				+ "AND OrderAddOns.subOrderId='" + subOrderId + "' AND OrderAddOns.itemId == " + itemId + " "
				+ "AND OrderAddOns.addOnId == MenuItems.menuId";
		if(getReturnedItems) {
			sql += " UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId, OrderAddOnLog.state, "
				+ "OrderAddOnLog.quantity AS quantity, OrderAddOnLog.itemId, OrderAddOnLog.rate, "
				+ "OrderAddOnLog.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOnLog, MenuItems "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' AND OrderAddOnLog.menuId='" + menuId + "' "
				+ "AND OrderAddOnLog.subOrderId='" + subOrderId + "' AND OrderAddOnLog.itemId == " + itemId + " "
				+ "AND OrderAddOnLog.addOnId == MenuItems.menuId";
		}
		sql += ";";
		return db.getRecords(sql, OrderAddOn.class, systemId);
	}

	@Override
	public ArrayList<OrderAddOn> getCanceledOrderedAddOns(String systemId, String orderId, String subOrderId,
			String menuId, int itemId) {

		String sql = "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.quantity AS quantity, OrderAddOnLog.itemId, "
				+ "OrderAddOnLog.rate, OrderAddOnLog.subOrderId, MenuItems.title "
				+ "FROM OrderAddOnLog, MenuItems WHERE OrderAddOnLog.orderId='" + orderId + "' "
				+ "AND OrderAddOnLog.menuId='" + menuId + "' AND OrderAddOnLog.subOrderId='" + subOrderId + "' "
				+ "AND OrderAddOnLog.itemId == " + itemId + " AND OrderAddOnLog.addOnId == MenuItems.menuId;";
		return db.getRecords(sql, OrderAddOn.class, systemId);
	}

	@Override
	public ArrayList<OrderAddOn> getReturnedAddOns(String systemId, String orderId, String subOrderId, String menuId,
			int itemId) {

		String sql = "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.quantity AS quantity, OrderAddOnLog.rate, OrderAddOnLog.itemId, OrderAddOnLog.subOrderId, "
				+ "MenuItems.title FROM OrderAddOnLog, MenuItems WHERE OrderAddOnLog.orderId='" + orderId
				+ "' AND OrderAddOnLog.menuId='" + menuId + "' AND OrderAddOnLog.subOrderId='" + subOrderId
				+ "' AND OrderAddOnLog.itemId == " + itemId + " AND OrderAddOnLog.addOnId == MenuItems.menuId;";
		return db.getRecords(sql, OrderAddOn.class, systemId);
	}

	@Override
	public ArrayList<OrderAddOn> getAllOrderedAddOns(String systemId, String orderId) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId AS menuId, "
				+ "OrderAddOns.subOrderDate, OrderAddOns.state, OrderAddOns.quantity, OrderAddOns.rate, OrderAddOns.itemId, "
				+ "OrderAddOns.subOrderId, MenuItems.title FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.addOnId == MenuItems.menuId "
				+ "UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.subOrderDate, OrderAddOnLog.state, OrderAddOnLog.quantity AS quantity, OrderAddOnLog.rate, "
				+ "OrderAddOnLog.itemId, OrderAddOnLog.subOrderId, "
				+ "MenuItems.title FROM OrderAddOnLog, MenuItems WHERE OrderAddOnLog.orderId='" + orderId
				+ "' AND OrderAddOnLog.addOnId == MenuItems.menuId AND OrderAddOnLog.state == " + SUBORDER_STATE_COMPLIMENTARY + ";";
		return db.getRecords(sql, OrderAddOn.class, systemId);
	}

	@Override
	public ArrayList<OrderAddOn> getOrderedAddOns(String systemId, String orderId, String subOrderId, String menuId, String itemId) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.quantity, OrderAddOns.rate "
				+ ", MenuItems.title, OrderAddOnLog.state FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.addOnId == MenuItems.menuId "
				+ "AND OrderAddOnLog.subOrderId='" + subOrderId
				+ "' AND OrderAddOnLog.menuId == '"+menuId+"' AND OrderAddOnLog.itemId == '" + itemId + "';";
		return db.getRecords(sql, OrderAddOn.class, systemId);
	}

	@Override
	public Boolean removeAddOns(String systemId, String orderId, String subOrderId, String menuId, int quantity) {
		String sql = "DELETE FROM OrderAddOns WHERE orderId='" + orderId + "' AND subOrderId =" + subOrderId
				+ " AND menuId='" + menuId + "'";

		if (quantity > 0)
			sql += "AND itemId =" + (quantity + 1);

		sql += ";";

		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean removeAddOnsFromItem(String systemId, String orderId, String subOrderId, String menuId, int itemId) {
		String sql = "DELETE FROM OrderAddOns WHERE orderId='" + orderId + "' AND subOrderId =" + subOrderId
				+ " AND menuId='" + menuId + "' AND itemId = " + itemId + ";";

		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean removeAddOn(String systemId, String orderId, String subOrderId, String menuId, int quantity, String addOnId,
			int itemId) {
		String sql = null;
		if (quantity == 0) {
			sql = "DELETE FROM OrderAddOns WHERE orderId='" + orderId + "' AND subOrderId =" + subOrderId
					+ " AND menuId='" + menuId + "' AND addOnId='" + addOnId
					+ "' AND itemId =" + itemId + ";";
		} else {
			sql = "UPDATE OrderAddOns SET quantity=" + Integer.toString(quantity) + " WHERE orderId='" + orderId
					+ "' AND subOrderId =" + subOrderId + " AND menuId='" + menuId
					+ "' AND addOnId='" + addOnId + "' AND itemId =" + itemId + ";";
		}
		return db.executeUpdate(sql, systemId, true);
	}
}
