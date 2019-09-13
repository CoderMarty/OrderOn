package com.orderon.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.commons.OnlineOrderingPortals;
import com.orderon.interfaces.ICustomer;
import com.orderon.interfaces.IEmployee;
import com.orderon.interfaces.IInventory;
import com.orderon.interfaces.IMenuItem;
import com.orderon.interfaces.INotification;
import com.orderon.interfaces.IOnlineOrderingPortal;
import com.orderon.interfaces.IOrder;
import com.orderon.interfaces.IOrderAddOn;
import com.orderon.interfaces.IOrderItem;
import com.orderon.interfaces.IOutlet;
import com.orderon.interfaces.IPayment;
import com.orderon.interfaces.IService;
import com.orderon.interfaces.ITable;

public class OrderManager extends AccessManager implements IOrder, IOrderItem{

	public OrderManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<Order> getAllOrders(String systemId, String outletId, String serviceDate, int orderTypeFilter, String query) {
		
		String sql = "SELECT Orders.*, cashPayment+cardPayment+appPayment+walletPayment AS totalPayment, foodDiscount+barDiscount AS discount, "
				+ "creditAmount, paymentType AS paymentType, firstName, name AS outletName FROM Orders " + 
				"LEFT OUTER JOIN Payments ON Orders.orderId = Payments.orderId " + 
				"LEFT OUTER JOIN Employee ON Orders.deliveryBoy == Employee.employeeId " + 
				"LEFT OUTER JOIN Outlets ON Orders.outletId == Outlets.outletId " + 
				"WHERE Orders.systemId = '"+ systemId + "' ";
		
		if(!outletId.isEmpty()) {
			sql += " AND Orders.outletId = '" + outletId + "' ";
		}
		
		ArrayList<Order> orders = new ArrayList<Order>();
		if(orderTypeFilter!=100) {
			sql += " AND Orders.orderType = " + orderTypeFilter;
		}
		if(query.isEmpty()) {
			sql += " AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			return db.getRecords(sql, Order.class, systemId);
		}
		String sql2;
		
		IEmployee employeeDao = new EmployeeManager(false);
		Employee employee = employeeDao.getEmployeeByName(systemId, query);
		if(employee != null) {
			sql2 = sql + " AND deliveryBoy = '" + employee.getEmployeeId() + "' AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, systemId));
			return orders;
		} else if (query.trim().length() > 0) {
			sql2 = sql + " AND Orders.billNo = '" + query + "' AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, systemId));
			sql2 = sql + " AND tableId LIKE '%" + query + "%' AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, systemId));
			sql2 = sql + " AND customerName LIKE '" + query + "%' AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, systemId));
			sql2 = sql + " AND reference LIKE '" + query + "%' AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, systemId));
			return orders;
		}
		
		sql += " AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
		return db.getRecords(sql, Order.class, systemId);
	}
	
	private ArrayList<Order> getCashOrders(String systemId, String serviceDate) {
		
		String sql = "SELECT Orders.orderId, Orders.billNo, cashPayment, Payments.foodBill, Payments.barBill " +
					"FROM Orders LEFT OUTER JOIN Payments ON Orders.orderId == Payments.orderId " + 
					"WHERE Orders.orderDate='" + serviceDate + "' " + 
					"AND Payments.paymentType LIKE '%CASH%' " +
					"AND Orders.customerGST isNull " +
					"AND Orders.state != " + ORDER_STATE_HIDDEN + " " +
					"ORDER BY cashPayment DESC;";

		return db.getRecords(sql, Order.class, systemId);
	}
	
	private ArrayList<Order> getCashOrders(String systemId, String serviceDate, double foodBillDeduction, double barBillDeduction) {
		
		String sql = "SELECT Orders.orderId, Orders.billNo, cashPayment, Payments.foodBill, Payments.barBill " + 
					"FROM Orders LEFT OUTER JOIN Payments ON Orders.orderId == Payments.orderId " + 
					"WHERE Orders.orderDate='" + serviceDate + "' " +
					"AND Payments.paymentType LIKE '%CASH%' " +
					"AND Orders.customerGST isNull " +
					"AND Orders.state != " + ORDER_STATE_HIDDEN;
		
		if(barBillDeduction == 0) 
			sql += " AND Payments.barBill == 0 ORDER BY Payments.foodBill DESC";
		else if(foodBillDeduction == 0)
			sql += " AND Payments.foodBill == 0 ORDER BY Payments.barBill DESC";
		else  if(barBillDeduction>foodBillDeduction) {
			if(foodBillDeduction > 30.0)
				sql += " ORDER BY Payments.barBill DESC";
			else
				sql += " ORDER BY Payments.foodBill , Payments.barBill DESC";
				
		}else if(foodBillDeduction>barBillDeduction) {
			if(barBillDeduction > 30.0)
				sql += " ORDER BY Payments.foodBill DESC";
			else
				sql += " ORDER BY Payments.barBill , Payments.foodBill DESC";
		}
			
		return db.getRecords(sql, Order.class, systemId);
	}
	
	private ArrayList<Order> getAllVisibleOrders(String systemId, String startDate) {
		
		IService serviceDao = new ServiceManager(false);
		String sql = "SELECT Orders.orderId, Orders.billNo " + 
					"FROM Orders " + 
					"WHERE Orders.orderDate BETWEEN '" + startDate +
					"' AND '" + serviceDao.getServiceDate(systemId) + "' " + 
					"AND Orders.state != " + ORDER_STATE_HIDDEN + " " +
					"ORDER BY id;";

		return db.getRecords(sql, Order.class, systemId);
	}
	
	private ArrayList<Order> getAllOrdersForMonth(String systemId, String month) {
		
		String sql = "SELECT id, orderId, billNo, state " + 
					"FROM Orders " + 
					"WHERE orderDate LIKE '" + month + "%' " + 
					"ORDER BY id;";

		return db.getRecords(sql, Order.class, systemId);
	}
	
	private ArrayList<Order> getAllOrdersForPeriod(String systemId, String month) {
		
		IService serviceDao = new ServiceManager(false);
		String serviceDate = serviceDao.getServiceDate(systemId);
		String sql = "SELECT id, orderId, billNo, state " + 
					"FROM Orders " + 
					"WHERE systemId='" + escapeString(systemId) + "' AND orderDate BETWEEN '" + month + "/01' " + 
					"AND '" + serviceDate + "' ORDER BY id;";

		return db.getRecords(sql, Order.class, systemId);
	}
	
	private ArrayList<Order> getInVisibleOrders(String systemId, String serviceDate) {
		
		String sql = "SELECT Orders.orderId, Orders.billNo " + 
					"FROM Orders " + 
					"WHERE Orders.orderDate = '" + serviceDate +
					"' AND Orders.state == " + ORDER_STATE_HIDDEN + " " +
					"ORDER BY id;";

		return db.getRecords(sql, Order.class, systemId);
	}
	
	@Override
	public ArrayList<Order> getCompletedOrders(String systemId) {

		IService serviceDao = new ServiceManager(false);
		String sql = "SELECT * FROM Orders WHERE systemId='" + escapeString(systemId) +
					"' AND orderDate='" + serviceDao.getServiceDate(systemId) +
					"' AND Orders.state = "+ORDER_STATE_BILLING+" ORDER BY id DESC;";

		return db.getRecords(sql, Order.class, systemId);
	}

	@Override
	public OrderItem getOrderedItem(String systemId, String orderId, String subOrderId, String menuId) {

		String sql = "SELECT OrderItems.subOrderDate AS subOrderDate, OrderItems.quantity AS quantity, OrderItems.waiterId AS waiterId, "
				+ "MenuItems.title AS title, MenuItems.collection AS collection, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.rate AS rate, "
				+ "OrderItems.itemIsMoved AS itemIsMoved, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, "
				+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE OrderItems.orderId='" + orderId
				+ "' AND OrderItems.subOrderId='" + subOrderId + "' AND OrderItems.menuId=='" + menuId
				+ "' AND OrderItems.menuId==MenuItems.menuId;";
		return db.getOneRecord(sql, OrderItem.class, systemId);
	}

	@Override
	public ArrayList<OrderItem> getOrderedItems(String systemId, String orderId, boolean showReturned) {
		return getOrderedItems(systemId, orderId, "menuId", showReturned);
	}
	
	
	/**
	 * @param systemId
	 * OutletManager Id of the current OutletManager.
	 * @param orderId
	 * Order Id of the current order.
	 * @param orderBy
	 * Sort by this field. You can provide multiple fields in a comma seperated format.
	 * @return
	 * Returns all the items in the order, sorted by the given orderBy field.
	 */
	private ArrayList<OrderItem> getOrderedItems(String systemId, String orderId, String orderBy, boolean showReturned) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.Id AS Id, OrderItems.menuId AS menuId, OrderItems.quantity AS quantity, OrderItems.waiterId AS waiterId, "
				+ "MenuItems.title AS title, MenuItems.collection AS collection, OrderItems.quantity*OrderItems.rate AS finalAmount, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.specs AS reason, OrderItems.rate AS rate, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, "
				+ "OrderItems.state AS state, MenuItems.taxes AS taxes, MenuItems.charges AS charges, "
				+ "substr(OrderItems.subOrderDate, 12, 5) AS time  FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId  ";
		if(showReturned) {
			sql += "UNION ALL "
				+ "SELECT OrderItemLog.subOrderId AS subOrderId, OrderItemLog.subOrderDate AS subOrderDate, "
				+ "OrderItemLog.Id AS Id, OrderItemLog.menuId AS menuId, OrderItemLog.quantity AS quantity, OrderItemLog.subOrderId AS waiterId, "
				+ "MenuItems.title AS title, MenuItems.collection AS collection, 0 AS finalAmount, "
				+ "MenuItems.station AS station, (SELECT specs FROM OrderItems WHERE OrderItems.orderId = '" + orderId
				+ "') AS specs, OrderItemLog.reason AS reason, OrderItemLog.rate AS rate, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, "
				+ ""
				+ "OrderItemLog.state AS state, MenuItems.taxes AS taxes, MenuItems.charges AS charges, substr(OrderItemLog.datetime, 12, 5) AS time "
				+ "FROM MenuItems, OrderItemLog WHERE OrderItemLog.orderId='" + orderId
				+ "' AND OrderItemLog.menuId==MenuItems.menuId "
				+ "AND (OrderItemLog.state = " + AccessManager.SUBORDER_STATE_RETURNED 
				+ " OR OrderItemLog.state = "+ AccessManager.SUBORDER_STATE_VOIDED
				+ " OR OrderItemLog.state = "+ AccessManager.ORDER_STATE_CANCELED+") ";
		}
		sql += "ORDER BY "+orderBy+";";
		return db.getRecords(sql, OrderItem.class, systemId);
	}

	@Override
	public ArrayList<OrderItem> getOrderedItemForVoid(String systemId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.Id AS Id, OrderItems.menuId AS menuId, OrderItems.quantity AS quantity, "
				+ "MenuItems.title AS title, MenuItems.collection AS collection, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.rate AS rate, "
				+ "OrderItems.state AS state, "
				+ "substr(OrderItems.subOrderDate, 12, 5) AS time  FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId  ORDER BY menuId;";

		return db.getRecords(sql, OrderItem.class, systemId);
	}

	@Override
	public ArrayList<EntityString> getUniqueMenuIdForComplimentaryOrder(String systemId, String orderId) {
		String sql = "SELECT distinct menuId AS entityId FROM OrderItemLog WHERE OrderItemLog.orderId='" + orderId
				+ "' AND state=" + ORDER_STATE_COMPLIMENTARY + ";";

		return db.getRecords(sql, EntityString.class, systemId);
	}

	@Override
	public ArrayList<OrderItem> getOrderedItemForBill(String systemId, String orderId, boolean showReturned) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.Id AS Id, OrderItems.menuId AS menuId, OrderItems.waiterId AS waiterId, SUM(OrderItems.quantity) AS quantity, "
				+ "MenuItems.title AS title, MenuItems.taxes AS taxes, MenuItems.charges AS charges, "
				+ "MenuItems.collection AS collection, MenuItems.station AS station, SUM(OrderItems.quantity*OrderItems.rate) AS finalAmount, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, MenuItems.collection AS collection, "
				+ "OrderItems.rate AS rate, OrderItems.specs AS specs, "
				+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE OrderItems.orderId='" + orderId
				+ "'  AND OrderItems.menuId == MenuItems.menuId AND MenuItems.flags NOT LIKE '%19%' "
				+ "GROUP BY OrderItems.menuId ";
		
		if(showReturned) {
			sql += "UNION ALL "
				+ "SELECT OrderItemLog.subOrderId AS subOrderId, OrderItemLog.subOrderDate AS subOrderDate, "
				+ "OrderItemLog.Id AS Id, OrderItemLog.menuId AS menuId, OrderItemLog.subOrderId AS waiterId, SUM(OrderItemLog.quantity) AS quantity, "
				+ "MenuItems.title AS title, MenuItems.taxes AS taxes, MenuItems.charges AS charges, "
				+ "MenuItems.collection AS collection, MenuItems.station AS station, SUM(OrderItemLog.quantity*OrderItemLog.rate) AS finalAmount, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, MenuItems.collection AS collection, "
				+ "OrderItemLog.rate AS rate, OrderItemLog.subOrderId AS specs, "
				+ "OrderItemLog.state AS state FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId='" + orderId
				+ "' AND OrderItemLog.menuId == MenuItems.menuId AND "
				+ "MenuItems.flags NOT LIKE '%19%' AND "
				+ "(OrderItemLog.state == "+AccessManager.SUBORDER_STATE_CANCELED+" OR  OrderItemLog.state == "+AccessManager.SUBORDER_STATE_RETURNED+") "
				+ "GROUP BY OrderItemLog.menuId";
		}
		
		sql += ";";
		return db.getRecords(sql, OrderItem.class, systemId);
	}

	@Override
	public ArrayList<OrderItem> getOrderedItemForBillCI(String systemId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.menuId AS menuId, OrderItems.quantity AS quantity, OrderItems.waiterId AS waiterId, MenuItems.title AS title, "
				+ "MenuItems.collection AS collection, MenuItems.taxes AS taxes, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.rate AS rate, OrderItems.quantity*OrderItems.rate AS finalAmount, "
				+ "OrderItems.specs AS specs, MenuItems.charges AS charges, "
				+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId  "
				+ "AND MenuItems.flags LIKE '%19%'";

		return db.getRecords(sql, OrderItem.class, systemId);
	}

	@Override
	public ArrayList<OrderItem> getComplimentaryOrderedItemForBill(String systemId, String orderId) {

		ArrayList<EntityString> menuIds = this.getUniqueMenuIdForComplimentaryOrder(systemId, orderId);
		ArrayList<OrderItem> orderItems = new ArrayList<OrderItem>();
		OrderItem tempItem = null;
		for (EntityString menuId : menuIds) {

			String sql = "SELECT OrderItemLog.subOrderId AS subOrderId, "
					+ "OrderItemLog.subOrderDate AS subOrderDate, OrderItemLog.Id AS Id, "
					+ "OrderItemLog.menuId AS menuId, SUM(OrderItemLog.quantity) AS quantity, 0 AS finalAmount, "
					+ "MenuItems.title AS title, MenuItems.taxes AS taxes, "
					+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, "
					+ "MenuItems.collection AS collection, MenuItems.station AS station, "
					+ "OrderItemLog.rate AS rate, MenuItems.charges AS charges, "
					+ "OrderItemLog.state AS state FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId='" + orderId
					+ "' AND OrderItemLog.menuId='" + menuId.getEntity() + "' AND MenuItems.menuId='"
					+ menuId.getEntity() + "' AND OrderItemLog.state = "+SUBORDER_STATE_COMPLIMENTARY+";";

			tempItem = db.getOneRecord(sql, OrderItem.class, systemId);
			if(tempItem==null)
				return orderItems;
			if(tempItem.getMenuId().length()>0)
				orderItems.add(tempItem);
		}
		return orderItems;
	}
	
	@Override
	public JSONObject newOnlineOrder(String jsonObj, JSONObject orderObj, int portalId) {
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", "failed");
			String sql = "SELECT * FROM OnlineOrders WHERE orderId = '" + orderObj.getInt("order_id") +
					"' AND restaurantId = '" + orderObj.getInt("restaurant_id") + "';";
			
			if(db.hasRecords(sql, orderObj.getString("outlet_id"))) {
				outObj.put("message", "Order already exists.");
				outObj.put("code", "501");
				return outObj;
			}
			jsonObj = jsonObj.replaceAll("'", "");
			sql = "INSERT INTO OnlineOrders (systemId, restaurantId, externalOrderId, portalId, data, status, dateTime) " +
					"VALUES ('" + orderObj.getString("outlet_id") + "', " + orderObj.getInt("restaurant_id") + 
					", " + orderObj.getInt("order_id") + ", " + portalId + ", '"+jsonObj+"', "+ONLINE_ORDER_NEW+", '" + LocalDateTime.now() + "');";

			db.executeUpdate(sql, orderObj.getString("outlet_id"), false);
			
			outObj.put("status", "success");
			outObj.put("code", "200");
			outObj.put("message", "Order registered.");
			System.out.println("Order Registered from "+portalId+ " order, for OutletId : "+  orderObj.getString("outlet_id") + ". Zomato OrderId : "+ orderObj.getInt("order_id"));
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			db.rollbackTransaction();
			e.printStackTrace();
		}
		
		return outObj;
	}
	
	@Override
	public ArrayList<OnlineOrder> getOnlineOrders(String systemId){
		
		String sql = "SELECT name AS entityId FROM sqlite_master WHERE type='table' AND name='OnlineOrders';";
		
		EntityString entity = db.getOneRecord(sql, EntityString.class, systemId);
		if(entity==null) {
			return null;
		}
		
		sql = "SELECT * FROM OnlineOrders WHERE status = "+ONLINE_ORDER_NEW+";";
		
		return db.getRecords(sql, OnlineOrder.class, systemId);
	}
	
	@Override
	public OnlineOrder getOnlineOrder(String systemId, int externalRestauranId, int externalOrderId){
		
		String sql = "SELECT * FROM OnlineOrders WHERE restaurantId = " + externalRestauranId + " AND externalOrderId = "+externalOrderId+";";
		
		return db.getOneRecord(sql, OnlineOrder.class, systemId);
	}
	
	@Override
	public ArrayList<OnlineOrder> getOrderDeliveryUpdate(String systemId){
		
		String sql = "SELECT orderNumber, orderId, riderNumber, riderName, riderStatus FROM OnlineOrders WHERE status = "+ONLINE_ORDER_ACCEPTED + ";";
		
		return db.getRecords(sql, OnlineOrder.class, systemId);
	}
	
	@Override
	public boolean updateOnlineOrderStatus(String outletId, int externalRestauranId, int externalOrderId, int status, String orderId, int orderNumber){
		String sql = "UPDATE OnlineOrders SET status = "+status+", orderId = '"+orderId+"', orderNumber="+orderNumber+" WHERE restaurantId = " 
		+ externalRestauranId + " AND externalOrderId = "+externalOrderId+";";
		System.out.println(sql);
		return db.executeUpdate(sql, outletId, false);
	}
	
	@Override
	public boolean editOnlineOrder(String outletId, int externalOrderId, int status){
		
		String sql = "UPDATE OnlineOrders SET status = "+status+" WHERE systemId = '" 
				+ outletId + "' AND externalOrderId = "+externalOrderId+";";
		System.out.println(sql);
		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public boolean markOnlineOrderComplete(String outletId, int orderNumber){
		
		String sql = "UPDATE OnlineOrders SET status = "+AccessManager.ONLINE_ORDER_DELIVERED+" WHERE systemId = '" 
		+ outletId + "' AND orderNumber = "+orderNumber+";";
		System.out.println(sql);
		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public boolean updateOnlineRiderData(String outletId, int externalOrderId, String riderName, String riderNumber, String riderStatus){
		
		String sql = "UPDATE OnlineOrders SET riderName = '"+riderName+"', riderNumber = '"+riderNumber+ "', riderStatus = '"+ riderStatus.toUpperCase() + 
				"' WHERE systemId = '" + outletId + "' AND externalOrderId = "+externalOrderId+";";
		System.out.println(sql);
		return db.executeUpdate(sql, outletId, false);
	}
	
	@Override
	public JSONObject newOrder(String systemId, String outletId, String hotelType, String userId, String[] tableIds, int peopleCount, String customer,
			String mobileNumber, String address, String section, String remarks, ServiceLog currentService) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			String serviceDate = currentService.getServiceDate();
			if(tableIds.length == 0) {  
				outObj.put("status", -1);
				outObj.put("message", "Cannot place Dine-In order without table no.");
				return outObj;
			}
			for (int i = 0; i < tableIds.length; i++) {
				sql = "SELECT * FROM Tables WHERE tableId='" + tableIds[i] + "' AND orderId == NULL;";
				TableUsage table = db.getOneRecord(sql, TableUsage.class, systemId);
				if (table != null) {
					outObj.put("status", -1);
					outObj.put("message", "Table " + tableIds[i] + " not free");
					return outObj;
				}
			}
			StringBuilder tableId = new StringBuilder();
			for (int i = 0; i < tableIds.length; i++) {
				tableId.append(tableIds[i]);
				if (i != tableIds.length)
					tableId.append(",");
			}
			orderId = getNextOrderId(systemId, outletId, userId);
			sql = "INSERT INTO Orders (systemId, outletId, orderId, orderDate, orderDateTime, customerName, "
					+ "customerNumber, customerAddress, isSmsSent, waiterId, numberOfGuests, "
					+ "state, orderType, takeAwayType, tableId, serviceType, section, discountCodes, remarks, excludedCharges, excludedTaxes) values ('" 
					+ systemId + "', '" + outletId + "', '" + orderId + "', '" + serviceDate + "', '" + (System.currentTimeMillis() / 1000L) + "','" + customer + "', '" + mobileNumber + "', '" 
					+ address+"', 0,'" + userId + "', " + Integer.toString(peopleCount) + ", ";

			if (hotelType.equals("PREPAID")) {
				sql += Integer.toString(ORDER_STATE_BILLING) + "," + DINE_IN + "," + ONLINE_ORDERING_PORTAL_NONE + ",'" + tableId.toString() + "','"
						+ currentService.getServiceType() + "', '"+section+"','[]', '"+remarks+"', '[]', '[]');";
			} else {
				sql += Integer.toString(ORDER_STATE_SERVICE) + "," + DINE_IN + "," + ONLINE_ORDERING_PORTAL_NONE + ",'" + tableId.toString() + "','"
						+ currentService.getServiceType() + "','"+section+"','[]', '"+remarks+"', '[]', '[]');";
			}
			for (int i = 0; i < tableIds.length; i++) {
				sql = sql + "UPDATE Tables SET orderId = '"+orderId+"' WHERE tableId = '" + tableIds[i] + "' AND outletId = '"
						+ outletId + "';";
			}
			if (!db.executeUpdate(sql, systemId, true)) {
				db.rollbackTransaction();
				outObj.put("status", 1);
				outObj.put("message", "Could Not place Order");
				return outObj;
			}
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
		} catch (Exception e) {
			db.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public JSONObject deleteOrdersMonthWise(String systemId, String month, double foodSalePercent, double barSalePercent) {
		JSONObject outObj = new JSONObject();
		Double deletableFoodSale = 0.0;
		Double deletableBarSale = 0.0;
		Double deletedFoodSale = 0.0;
		Double deletedBarSale = 0.0;
		Double deletedFoodSaleDec = 0.0;
		Double deletedBarSaleDec = 0.0;
		Double deletedCashAmount = 0.0;
		try {
			outObj.put("status", false);
			String sql = "SELECT * FROM ServiceLog WHERE serviceDate LIKE '"+month+"%';";
			ArrayList<ServiceLog> serviceLog = db.getRecords(sql, ServiceLog.class, systemId);
			ServiceLog service = null;

			for(int i=0; i<serviceLog.size(); i++) {
				service = serviceLog.get(i);
				ArrayList<Order> allOrders = this.getCashOrders(systemId, service.getServiceDate(), foodSalePercent, barSalePercent);
				if(allOrders.isEmpty())
					continue;
				IPayment paymentDao = new PaymentManager(false);
				Report sale = paymentDao.getCashCardSales(systemId, service.getServiceDate(), service.getServiceType()); 
				//Calculate the deletable food sale value.
				deletableFoodSale = deletedFoodSaleDec = (sale.getFoodBill().doubleValue()*foodSalePercent)/100;
				//Calculate the deletable bar sale value. 
				deletableBarSale = deletedBarSaleDec = (sale.getBarBill().doubleValue()*barSalePercent)/100;
				deletedCashAmount = 0.0;
				
				Order order = null;
				if(foodSalePercent > 0.0) {
					for(int j=0; j<allOrders.size(); j++) {
						order = allOrders.get(j);
						if(sale.getFoodBill().doubleValue() > deletedFoodSaleDec)
							continue;
						this.deleteOrder(systemId, order.getOrderId());
						deletedFoodSale += order.getFoodBill().doubleValue();
						deletedBarSale += order.getBarBill().doubleValue();
						deletedFoodSaleDec -= order.getFoodBill().doubleValue();
						deletedBarSaleDec -= order.getBarBill().doubleValue();
						deletedCashAmount += order.getCashPayment().doubleValue();
						if(deletableFoodSale <= deletedFoodSale)
							break;
					}
				}
				if(barSalePercent > 0.0) {
					allOrders = this.getCashOrders(systemId, service.getServiceDate(), foodSalePercent, barSalePercent);
					for(int j=0; j<allOrders.size(); j++) {
						order = allOrders.get(j);
						if(order.getBarBill().doubleValue() > deletedBarSaleDec)
							continue;
						this.deleteOrder(systemId, order.getOrderId());
						deletedFoodSale += order.getFoodBill().doubleValue();
						deletedBarSale += order.getBarBill().doubleValue();
						deletedFoodSaleDec -= order.getFoodBill().doubleValue();
						deletedBarSaleDec -= order.getBarBill().doubleValue();
						deletedCashAmount += order.getCashPayment().doubleValue();
						if(deletableBarSale <= deletedBarSale)
							break;
					}
				}
				
				EntityDouble cashReported = null;
				sql = "SELECT cash AS entityId FROM TotalRevenue WHERE serviceDate = '"+service.getServiceDate()+"' "
						+ "AND serviceType = '"+service.getServiceType()+"';";
				
				cashReported = db.getOneRecord(sql, EntityDouble.class, systemId);
				if(cashReported!=null) {
					sql = "UPDATE TotalRevenue SET cash = " + (cashReported.getId()-deletedCashAmount)
						+ " WHERE serviceDate = '"+service.getServiceDate()+"' AND serviceType = '"+service.getServiceType()+"';";
					
					db.executeUpdate(sql, systemId, true);
				}
			}
			this.AlignBillNumbersMonthWise(systemId, month);
			
			outObj.put("status", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public JSONObject deleteOrdersDayWise(String systemId, double deductionAmount, double foodSalePercent, 
			double barSalePercent, String serviceDate, String serviceType) {
		JSONObject outObj = new JSONObject();
		Double deletableFoodSale = 0.0;
		Double deletableBarSale = 0.0;
		Double deletedFoodSale = 0.0;
		Double deletedBarSale = 0.0;
		Double deletedFoodSaleDec = 0.0;
		Double deletedBarSaleDec = 0.0;
		Double deletedCashAmount = 0.0;
		try {
			outObj.put("status", false);
			
			ArrayList<Order> allOrders = null;
			IPayment paymentDao = new PaymentManager(false);
			Report sale = paymentDao.getCashCardSales(systemId, serviceDate, serviceType); 
			//Calculate the deletable food sale value.
			deletableFoodSale = deletedFoodSaleDec = (sale.getFoodBill().doubleValue()*foodSalePercent)/100;
			//Calculate the deletable bar sale value. 
			deletableBarSale = deletedBarSaleDec = (sale.getBarBill().doubleValue()*barSalePercent)/100;
			deletedCashAmount = 0.0;
			
			Order order = null;
			if(foodSalePercent > 30.0 && barSalePercent > 30.0) {
				allOrders = this.getCashOrders(systemId, serviceDate, foodSalePercent, barSalePercent);
				for(int j=0; j<allOrders.size(); j++) {
					if(deductionAmount < deletedCashAmount)
						break;
					order = allOrders.get(j);
					if(order.getFoodBill().doubleValue() > deletedFoodSaleDec)
						continue;
					if(order.getBarBill().doubleValue() > deletedBarSaleDec)
						continue;
					this.deleteOrder(systemId, order.getOrderId());
					deletedFoodSale += order.getFoodBill().doubleValue();
					deletedBarSale += order.getBarBill().doubleValue();
					deletedFoodSaleDec -= order.getFoodBill().doubleValue();
					deletedBarSaleDec -= order.getBarBill().doubleValue();
					deletedCashAmount += order.getCashPayment().doubleValue();
					if(deletableFoodSale <= deletedFoodSale) {
						foodSalePercent = 0.0;
						break;
					}if(deletableBarSale <= deletedBarSale) {
						barSalePercent = 0.0;
						break;
					}
				}
			}
			if(foodSalePercent > 0.0) {
				allOrders = this.getCashOrders(systemId, serviceDate, foodSalePercent, barSalePercent);
				for(int j=0; j<allOrders.size(); j++) {
					if(deductionAmount < deletedCashAmount)
						break;
					order = allOrders.get(j);
					if(order.getFoodBill().doubleValue() > deletedFoodSaleDec)
						continue;
					this.deleteOrder(systemId, order.getOrderId());
					deletedFoodSale += order.getFoodBill().doubleValue();
					deletedBarSale += order.getBarBill().doubleValue();
					deletedFoodSaleDec -= order.getFoodBill().doubleValue();
					deletedBarSaleDec -= order.getBarBill().doubleValue();
					deletedCashAmount += order.getCashPayment().doubleValue();
					if(deletableFoodSale <= deletedFoodSale) {
						foodSalePercent = 0.0;
						break;
					}
				}
			}
			if(barSalePercent > 0.0) {
				allOrders = this.getCashOrders(systemId, serviceDate, foodSalePercent, barSalePercent);
				for(int j=0; j<allOrders.size(); j++) {
					if(deductionAmount < deletedCashAmount)
						break;
					order = allOrders.get(j);
					if(order.getBarBill().doubleValue() > deletedBarSaleDec)
						continue;
					this.deleteOrder(systemId, order.getOrderId());
					deletedFoodSale += order.getFoodBill().doubleValue();
					deletedBarSale += order.getBarBill().doubleValue();
					deletedFoodSaleDec -= order.getFoodBill().doubleValue();
					deletedBarSaleDec -= order.getBarBill().doubleValue();
					deletedCashAmount += order.getCashPayment().doubleValue();
					if(deletableBarSale <= deletedBarSale) {
						barSalePercent = 0.0;
						break;
					}
				}
			}
			if(allOrders.isEmpty()) {
				outObj.put("message", "Orders not found.");
				return outObj;
			}
			
			EntityDouble cashReported = null;
			String sql = "SELECT cash AS entityId FROM TotalRevenue WHERE serviceDate = '"+serviceDate+"' "
					+ "AND serviceType = '"+serviceDate+"';";
			
			cashReported = db.getOneRecord(sql, EntityDouble.class, systemId);
			if(cashReported!=null) {
				sql = "UPDATE TotalRevenue SET cash = " + (cashReported.getId()-deletedCashAmount)
					+ " WHERE serviceDate = '"+serviceDate+"' AND serviceType = '"+serviceDate+"';";
				
				db.executeUpdate(sql, systemId, true);
			}
			sql = "UPDATE ServiceLog SET deductionState = 1 WHERE serviceDate = '"+serviceDate+"' "
					+ "AND serviceType = '"+serviceDate+"';";
			db.executeUpdate(sql, systemId, true);
			outObj.put("status", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public JSONObject hideOrder(String systemId, String date, String serviceType, BigDecimal cashAmount) {
		JSONObject outObj = new JSONObject();
		ArrayList<Order> allOrders = this.getCashOrders(systemId, date);
		IPayment paymentDao = new PaymentManager(false);
		BigDecimal cashSale = paymentDao.getCashCardSales(systemId, date, serviceType).getCashPayment();
		BigDecimal cashDeducted = new BigDecimal("0.0");
		IService serviceDao = new ServiceManager(false);
		ServiceLog service = serviceDao.getCurrentService(systemId);
		try {
			outObj.put("status", false);
			if(service.getServiceDate().equals(date) && service.getServiceType().equals(serviceType)) {
				outObj.put("message", "This process cannot be performed for the Current Service. Please continue after Service is ended.");
				return outObj;
			}
			if(allOrders.isEmpty()) {
				outObj.put("message", "The Selected Service Date and Type have no cash orders.");
				return outObj;
			}
	 	
			for(int i=0; i<allOrders.size(); i++) {
				if(allOrders.get(i).getCashPayment().compareTo((cashSale.subtract(cashAmount))) == 1)
					continue;
				this.changeOrderStateToHidden(systemId, allOrders.get(i).getOrderId());
				cashDeducted.add(allOrders.get(i).getCashPayment());
				cashSale.subtract(allOrders.get(i).getCashPayment());
			}
			
			String sql = "SELECT cash FROM TotalRevenue WHERE serviceDate = '"+date+"' AND serviceType = '"+serviceType+"';";
			
			EntityBigDecimal cashReported = db.getOneRecord(sql, EntityBigDecimal.class, systemId);
			if(cashReported!=null) {
				sql = "UPDATE TotalRevenue SET cash2 = " + cashReported.getId() + ", "
					+ "cash = " + (cashReported.getId().subtract(cashDeducted))
					+ ", deductedCash = " + cashAmount 
					+ " WHERE serviceDate = '"+date+"' AND serviceType = '"+serviceType+"';";
				
				db.executeUpdate(sql, systemId, true);
			}
			
			sql = "SELECT Orders.billNo AS entityId FROM Orders WHERE orderDate = '"+date+"' AND billNo != ''  order by billNo asc LIMIT 1";
			EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);
			int billNo = entity.getId();
			this.updateBillNo2(systemId, date);
			
			allOrders = this.getAllVisibleOrders(systemId, date);
			for(int i=0; i<allOrders.size(); i++) {
				this.updateBillNo(systemId, allOrders.get(i).getOrderId(), Integer.toString(billNo));
				billNo++;
			}
			
			allOrders = this.getInVisibleOrders(systemId, date);
			for(int i=0; i<allOrders.size(); i++) {
				sql = "UPDATE OrderItems SET billNo2 = billNo, billNo = '' WHERE orderId = '"+allOrders.get(i).getOrderId()+"' AND systemId = '"+systemId+"'; ";
				
				db.executeUpdate(sql, systemId, true);
			}
			
			outObj.put("status", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}
	
	@Override
	public void AlignBillNumbersContinuous(String systemId, int billNo, String startDate) {
		
		ArrayList<Order> allOrders = this.getAllVisibleOrders(systemId, startDate);
		for(int i=0; i<allOrders.size(); i++) {
			this.updateBillNo(systemId, allOrders.get(i).getOrderId(), Integer.toString(billNo));
			billNo++;
		}
	}
	
	@Override
	public void AlignBillNumbersMonthWise(String systemId, String month) {
		
		ArrayList<Order> allOrders = this.getAllOrdersForMonth(systemId, month);
		this.updateBillNoMonthWiseForDelete(systemId, allOrders);
	}
	
	@Override
	public void AlignAllBillNumbers(String systemId, String startMonth) {
		
		ArrayList<Order> allOrders = this.getAllOrdersForPeriod(systemId, startMonth);
		this.updateBillNoPeriodForDelete(systemId, allOrders, startMonth);
	}
	
	@Override
	public void AlignBillNumbersDayWise(String systemId, String serviceDate) {
		
		ArrayList<Order> allOrders = this.getAllOrdersForMonth(systemId, serviceDate);
		this.updateBillNoMonthWiseForDelete(systemId, allOrders);
	}
	
	private boolean updateBillNo2(String systemId, String serviceDate) {
		
		String sql = "UPDATE Orders SET billNo = '' WHERE orderDate = '"+serviceDate+"' AND systemId = '"+systemId+"'; "
				+ "UPDATE Payments SET billNo = '' WHERE orderDate = '"+serviceDate+"' AND systemId = '"+systemId+"'; ";
		
		return db.executeUpdate(sql, systemId, true);
	}
	
	private boolean updateBillNo(String systemId, String orderId, String billNo) {
		
		String sql = "UPDATE Orders SET billNo = '"+billNo+"' WHERE orderId = '"+orderId+"' AND systemId = '"+systemId+"'; "
				+ "UPDATE OrderItems SET billNo2 = billNo, billNo = '"+billNo+"' WHERE orderId = '"+orderId+"' AND systemId = '"+systemId+"'; "
				+ "UPDATE Payments SET billNo = '"+billNo+"' WHERE orderId = '"+orderId+"' AND systemId = '"+systemId+"'; ";
		
		return db.executeUpdate(sql, systemId, true);
	}
	
	private boolean updateBillNoMonthWiseForDelete(String systemId, ArrayList<Order> orders) {
		
		int billNo = 0;
		String billNoStr = "";
		Order order = null;
		String sql = "";
		for(int i=0; i<orders.size(); i++) {
			order = orders.get(i);
			if(order.getState() == ORDER_STATE_VOIDED || order.getState() == ORDER_STATE_CANCELED || order.getState() == ORDER_STATE_HIDDEN)
				billNoStr = "";
			else {
				billNo++;
				billNoStr = Integer.toString(billNo);
			}
			sql += "UPDATE Orders SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND systemId = '"+systemId+"'; "
					+ "UPDATE OrderItems SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND systemId = '"+systemId+"'; "
					+ "UPDATE Payments SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND systemId = '"+systemId+"'; ";
		}
		
		return db.executeUpdate(sql, systemId, true);
	}
	
	private boolean updateBillNoPeriodForDelete(String systemId, ArrayList<Order> orders, String serviceDate) {
		
		int billNo = 0;
		String billNoStr = "";
		Order order = null;
		
		//Get the last day of the previous month.
		Date dt = new Date();
    	Calendar c = Calendar.getInstance();
    	try {
			dt = new SimpleDateFormat("yyyy/MM/dd").parse(serviceDate+"/01");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.setTime(dt);
		System.out.println("Selected Month" + c.getTime().toString());// Selected Month
    	c.add(Calendar.MONTH, -1);
		System.out.println("Previous Month " + c.getTime().toString());
    	
		dt = c.getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM");
		String exDate = dateFormat.format(dt);
		
		//Get the last bill of the previous month.
		String sql = "SELECT billNo AS entityId FROM Orders WHERE orderdate LIKE '"+exDate+"%' AND billNo != '' ORDER BY billNo DESC LIMIT 1;";
		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);
		if(entity == null) {
			billNo = 0;
		}else {
			billNo = entity.getId();
		}
		sql = "";
		
		for(int i=0; i<orders.size(); i++) {
			order = orders.get(i);
			if(order.getState() == ORDER_STATE_VOIDED || order.getState() == ORDER_STATE_CANCELED || order.getState() == ORDER_STATE_HIDDEN)
				billNoStr = "";
			else {
				billNo++;
				billNoStr = Integer.toString(billNo);
			}
			sql += "UPDATE Orders SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND systemId = '"+systemId+"'; "
					+ "UPDATE OrderItems SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND systemId = '"+systemId+"'; "
					+ "UPDATE Payments SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND systemId = '"+systemId+"'; ";
		}
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public JSONObject newNCOrder(String systemId, String outletId, String userId, String reference, String section, String remarks) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			IService serviceDao = new ServiceManager(false);
			ServiceLog service = serviceDao.getCurrentService(systemId);
			if (service.getServiceDate() == null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started");
				return outObj;
			}
			
			orderId = getNextOrderId(systemId, outletId, userId);
			sql = "INSERT INTO Orders (systemId, outletId, orderId, orderDate, orderDateTime, waiterId, numberOfGuests, "
					+ "state, orderType, serviceType, section, reference, remarks, discountCodes, excludedCharges, excludedTaxes) values ('" 
					+ systemId + "', '" + outletId + "', '" + orderId + "', '" + service.getServiceDate() + "', '" + (System.currentTimeMillis() / 1000L) 
					+ "', '" + userId + "', 1, " + Integer.toString(ORDER_STATE_SERVICE) + "," + NON_CHARGEABLE + ",'" 
					+ service.getServiceType() + "','"+section+"', '"+reference+"', '"+remarks+"', '[]', '[]', '[]');";
			
			if (!db.executeUpdate(sql, systemId, true)) {
				db.rollbackTransaction();
				outObj.put("status", 1);
				outObj.put("message", "Could Not place Order");
				return outObj;
			}
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
			return outObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject newQsrOrder(String systemId, String outletId, String userId, String customer, String mobileNumber,
			String allergyInfo, int orderType, String emailId, String referenceForReview) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			IService serviceDao = new ServiceManager(false);
			ServiceLog service = serviceDao.getCurrentService(systemId);
			if (service.getServiceDate() == null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started");
				return outObj;
			}
			orderId = getNextOrderId(systemId, outletId, userId);
			sql = "INSERT INTO Orders (systemId, outletId, orderId, orderDate, orderDateTime, customerName, "
					+ "customerNumber, customerAddress, waiterId, numberOfGuests, state, orderType, serviceType, discountCodes, excludedCharges, excludedTaxes) values ('"
					+ systemId + "', '" + outletId + "', '" + orderId + "', '" + service.getServiceDate() + "', '" + (System.currentTimeMillis() / 1000L) 
					+ "','" + customer + "', '" + mobileNumber + "', '', '" + userId + "', " + 1 + ", " + Integer.toString(ORDER_STATE_COMPLETE) 
					+ "," + orderType + ",'" + service.getServiceType() + "', '[]', '[]', '[]');";

			String[] name = customer.split(" ");
			String surName = "";
			if(name.length>1) {
				surName = name[1];
			}
			ICustomer customerDao = new CustomerManager(false);
			if (!customerDao.hasCustomer(systemId, mobileNumber)) {
				customerDao.addCustomer(systemId, name[0], surName, mobileNumber, "", "", "", allergyInfo, Boolean.FALSE, Boolean.FALSE, emailId, "NONE", referenceForReview);
			} else {
				customerDao.updateCustomer(systemId, null, name[0], surName, mobileNumber, "", "", "", allergyInfo, "", Boolean.FALSE, "", "");
			}
			db.executeUpdate(sql, systemId, true);
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
			outObj.put("orderNumber", this.getOrderNumber(systemId, orderId));
			return outObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject placeOrder(String systemId, String outletId, String userId, String customer, JSONObject customerDetails, String phone, String address,
			int orderType, int takeAwayType, String allergyInfo, String reference, String remarks, 
			String externalOrderId, JSONArray discountCodes, String emailId, String referenceForReview, String section,
			Double cashToBeCollected, Double goldDiscount, Double zomatoVoucherAmount, Double piggyBank, BigDecimal amountReceivable, 
			int orderPreparationTime, JSONObject onlineOrderData) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			IOutlet outletDao = new OutletManager(false);
			Settings settings  = outletDao.getSettings(systemId);
			IService serviceDao = new ServiceManager(false);
			ServiceLog service = serviceDao.getCurrentService(systemId);
			if (service == null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started for the day");
				return outObj;
			}
			if(!externalOrderId.isEmpty()) {
				if(this.checkIfOnlineOrderExists(systemId, outletId, externalOrderId, service.getServiceDate())){
					outObj.put("message", "Cannot place the same order again. This Online Order already exists.");
					outObj.put("status", -1);
					return outObj;
				}
			}
			
			if(!(customerDetails== null || customerDetails.length()==0)) {
				customer = customerDetails.getString("name");
				phone = customerDetails.getString("phone_number");
				address = customerDetails.has("address")?customerDetails.getString("address"):"";
				emailId = customerDetails.has("email")?customerDetails.getString("email"):"";
				String[] name = customer.split(" ");
				String surName = "";
				if(name.length>1) {
					surName = name[1];
				}
				ICustomer customerDao = new CustomerManager(false);
				if (!customerDao.hasCustomer(systemId, phone)) {
					customerDao.addCustomer(systemId, name[0], surName, phone, address, "", "", allergyInfo, 
							Boolean.FALSE, Boolean.FALSE, emailId, "NONE", referenceForReview);
				} else {
					customerDao.updateCustomer(systemId, null, name[0], surName, phone, "", "", "", allergyInfo, address, Boolean.FALSE, emailId, "");
				}
			}else if (!phone.equals("")) {
				String[] name = customer.split(" ");
				String surName = "";
				if(name.length>1) {
					surName = name[1];
				}
				ICustomer customerDao = new CustomerManager(false);
				if (!customerDao.hasCustomer(systemId, phone)) {
					customerDao.addCustomer(systemId, name[0], surName, phone, address, "", "", allergyInfo, Boolean.FALSE, Boolean.FALSE, emailId, "NONE", referenceForReview);
				} else {
					customerDao.updateCustomer(systemId, null, name[0], surName, phone, "", "", "", allergyInfo, address, Boolean.FALSE, "", "");
				}
			}

			String orderState = Integer.toString(ORDER_STATE_SERVICE);
			if (settings.getHotelType().equals("PREPAID"))
				orderState = Integer.toString(ORDER_STATE_BILLING);

			orderId = getNextOrderId(systemId, outletId, userId);
			sql = "INSERT INTO Orders (systemId, outletId, orderId, orderDate, orderDateTime, customerName, "
					+ "customerNumber, customerAddress,  waiterId, state, orderType, "
					+ "takeAwayType, serviceType, reference, remarks, discountCodes, externalOrderId, section,"
					+ "cashToBeCollected, goldDiscount, zomatoVoucherAmount, piggyBank, amountReceivable, orderPreparationTime, onlineOrderData)"
					+ " values ('" + systemId + "', '" + outletId + "', '" + orderId + "', '" + service.getServiceDate() + "', '" + (System.currentTimeMillis() / 1000L)
					+ "','" + escapeString(customer) + "', '" + escapeString(phone) + "', '" + escapeString(address) + "', '" 
					+ userId + "', " + orderState + "," + orderType + "," + takeAwayType + ",'" + service.getServiceType() + "', '"+reference
					+ "', '"+remarks+"', '"+discountCodes.toString()+"', '"+externalOrderId+"', '"+section
					+"', "+ cashToBeCollected + "," + goldDiscount + "," +zomatoVoucherAmount+ "," +piggyBank+ ", "+amountReceivable+", "
					+orderPreparationTime+", '"+onlineOrderData.toString()+"');";
			if (!db.executeUpdate(sql, systemId, true)) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to add order");
				return outObj;
			}
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
			return outObj;
		} catch (Exception e) {
			db.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public JSONObject newHomeDeliveryOrder(String systemId, String outletId, String userId, String customer, String phone, String address,
			String allergyInfo, String remarks, String section) {
		return this.placeOrder(systemId, outletId, userId, customer, null, phone, address, HOME_DELIVERY, ONLINE_ORDERING_PORTAL_NONE, allergyInfo, "", remarks, "", new JSONArray()
				, "", "", section, 0.0, 0.0, 0.0, 0.0, new BigDecimal("0.0"), 0, new JSONObject());
	}

	@Override
	public JSONObject newTakeAwayOrder(String systemId, String outletId, String userId, String customer, JSONObject customerDetails, String phone,
			String externalId, String allergyInfo, String remarks, String externalOrderId, JSONArray discountCodes,
			String section, Double cashToBeCollected, Double goldDiscount, Double zomatoVoucherAmount, Double piggyBank, BigDecimal amountReceivable,
			int orderPreparationTime, JSONObject onlineOrderData) {
		int takeAwaytype = COUNTER_PARCEL_ORDER;
		IOnlineOrderingPortal protalDao = new OnlineOrderingPortalManager(false);
		ArrayList<OnlineOrderingPortal> portals = protalDao.getOnlineOrderingPortals(systemId);
		if(!externalId.equals("")) {
			for (OnlineOrderingPortal portal : portals) {
				if(portal.getPortal().equals(customer)) {
					takeAwaytype = portal.getId();
				}
			}
			customer = "";
		}
		return this.placeOrder(systemId, outletId, userId, customer, customerDetails, phone, "", TAKE_AWAY, takeAwaytype, allergyInfo, 
				externalId, remarks, externalOrderId, discountCodes, "", "", section, 
				cashToBeCollected, zomatoVoucherAmount, goldDiscount, piggyBank, amountReceivable, orderPreparationTime, onlineOrderData);
	}

	@Override
	public JSONObject newBarOrder(String systemId, String outletId, String userId, String reference, String remarks, String section) {
		return this.placeOrder(systemId, outletId, userId, "", null, "", "", BAR, ONLINE_ORDERING_PORTAL_NONE, "", reference, 
				remarks, "", new JSONArray(), "", "", section, 0.0, 0.0, 0.0, 0.0, new BigDecimal("0.0"), 0, new JSONObject());
	}

	@Override
	public Boolean unCheckOutOrder(String systemId, String orderId) {
		IOutlet outletDao = new OutletManager(false);
		Settings settings  = outletDao.getSettings(systemId);
		String sql = "";
		
		if (settings.getHotelType().equals("PREPAID"))
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_OFFKDS) + " WHERE orderId='" + orderId
					+ "';";
		else
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_SERVICE) + " WHERE orderId='" + orderId
					+ "';";
		sql += " UPDATE Tables set isInBilling = 'false' WHERE orderId = '"+orderId+"';";

		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean checkOutOrder(String systemId, String outletId, String orderId) {
		IOutlet outletDao = new OutletManager(false);
		Settings settings  = outletDao.getSettings(systemId);
		String sql = "";
		
		if (settings.getHotelType().equals("PREPAID"))
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_COMPLETE) + " WHERE orderId='" + orderId
					+ "'; UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_COMPLETE) + " WHERE orderId='" + orderId + "';";
		else
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_BILLING) + " WHERE orderId='" + orderId + "';";

		sql += " UPDATE Tables set isInBilling = 'true' WHERE orderId = '"+orderId+"';";
		String billNo = "";
		String billNo2 = "";
		Order order = this.getOrderById(systemId, orderId);
		if(order.getBillNo().equals("")) {
			if (settings.getBillType().equals(BILLTYPE_YEARLY_REFRESH)) {
				billNo = this.getNextBillNoNumberFormatYearly(systemId, outletId);
			}else if(settings.getBillType().equals(BILLTYPE_DAILY_REFRESH)) {
				billNo = this.getNextBillNoNumberFormatDaywise(systemId, outletId);
			}else if(settings.getBillType().equals(BILLTYPE_MONTHLY_REFRESH)) {
				billNo = this.getNextBillNoNumberFormatMonthwise(systemId, outletId);
			}
			
			if (settings.getBillType().equals(BILLTYPE_YEARLY_REFRESH)) {
				billNo2 = billNo;
			}else {
				billNo2 = this.getNextBillNoNumberFormatYearly(systemId, outletId);
			}
			
			sql += "UPDATE Orders SET billNo = '"+billNo+"', billNo2 = '"+billNo2+"' WHERE orderId = '"+orderId+"';"
				+ "UPDATE OrderItems SET billNo = '"+billNo+"', billNo2 = '"+billNo2+"' WHERE orderId = '"+orderId+"';";
		}
		return db.executeUpdate(sql, systemId, true);
	}
	
	@Override
	public Boolean updateDeliveryTime(String systemId, String orderId) {
		String sql = "UPDATE Orders SET deliveryTimeStamp = '"+parseTime("HH:mm")+ "' WHERE "
				+ "systemId = '" + systemId + "' AND orderId = '"+ orderId + "';";
		
		return db.executeUpdate(sql, systemId, true);
	}
	
	@Override
	public Boolean updateCompleteTime(String systemId, String orderId) {
		String sql = "UPDATE Orders SET completeTimeStamp = '"+parseTime("HH:mm")+ "' WHERE "
				+ "systemId = '" + systemId + "' AND orderId = '"+ orderId + "';";
		
		return db.executeUpdate(sql, systemId, true);
	}
	
	@Override
	public Boolean updateDeliveryBoy(String systemId, String orderId, String employeeName) {
		String sql = "UPDATE Orders SET deliveryBoy = '"+employeeName+ "' WHERE orderId = '"+ orderId + "';";
		
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean markFoodReady(String systemId, String orderId) {
		String sql = "UPDATE Orders SET isFoodReady='true' WHERE orderId='" + orderId + "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean changeOrderStatus(String systemId, String orderId) {
		String sql = "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_OFFKDS) + " WHERE orderId='" + orderId
				+ "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean changeOrderStateToHidden(String systemId, String orderId) {
		String sql = "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_HIDDEN) + " WHERE orderId='" + orderId
				+ "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean changeOrderStateToCancelled(String systemId, String orderId) {
		String sql = "UPDATE Orders SET state = "+ORDER_STATE_CANCELED+" WHERE orderId=='" + orderId + "'; "
				+ "UPDATE TABLES SET orderId = NULL WHERE orderId=='" + orderId + "'; "
				+ "DELETE FROM OrderSpecifications WHERE orderId=='" + orderId + "'; ";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean markPaymentComplete(String systemId, String orderId) {
		String sql = "UPDATE TABLES SET orderId = NULL, isInBilling = 'false' WHERE orderId='" + orderId + "';"
				+ "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_COMPLETE) + " WHERE orderId='" + orderId
				+ "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean changeOrderStatusToService(String systemId, String orderId) {
		String sql = "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_SERVICE) + " WHERE orderId='" + orderId
				+ "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean editSubOrder(String systemId, String orderId, String subOrderId, String menuId, int quantity) {
		String sql = null;
		if (quantity == 0) {
			sql = "DELETE FROM OrderItems WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
					+ "' AND menuId='" + menuId + "' AND state="
					+ Integer.toString(SUBORDER_STATE_PENDING) + ";";
		} else {
			sql = "UPDATE OrderItems SET quantity=" + Integer.toString(quantity) + " WHERE orderId='" + orderId
					+ "' AND subOrderId=='" + subOrderId + "' AND menuId='" + menuId + "' AND systemId='" + systemId
					+ "' AND state=" + Integer.toString(SUBORDER_STATE_PENDING) + ";";
		}
		int itemId = quantity + 1;
		IOrderAddOn addOnDao = new OrderAddOnManager(false);
		addOnDao.removeAddOns(systemId, orderId, subOrderId, menuId, itemId);
		this.removeOrderedSpecification(systemId, orderId, subOrderId, menuId, itemId);
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean removeOrderedSpecification(String systemId, String orderId, String subOrderId, String menuId,
			int itemId) {
		String sql = "DELETE FROM OrderSpecifications WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
				+ "' AND menuId='" + menuId + "' AND itemId=" + itemId + ";";

		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean removeSubOrder(String systemId, String orderId, String subOrderId, String menuId, int quantity) {
		String sql = null;
		if (quantity == 0) {
			sql = "DELETE FROM OrderItems WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
					+ "' AND menuId='" + menuId + "';";
		} else {
			sql = "UPDATE OrderItems SET quantity=" + quantity + " WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
					+ "' AND menuId='" + menuId + "';";
		}
		this.removeOrderedSpecification(systemId, orderId, subOrderId, menuId, quantity + 1);
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public JSONObject voidOrder(String systemId, String outletId, String orderId, String reason, String authId, String section) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);

			IMenuItem menuDao = new MenuItemManager(false);
			Order order = getOrderById(systemId, orderId);

			String sql = "UPDATE Orders SET state=" + ORDER_STATE_VOIDED + ", reason = '"
					+ reason + "', authId = '" + authId + "' WHERE orderId='" + orderId + "' AND systemId='" + systemId
					+ "';";

			if (!db.executeUpdate(sql, systemId, true)) {
				outObj.put("message", "Failed to void the order. Please try again.");
				return outObj;
			}

			if (order.getState() == ORDER_STATE_BILLING || order.getState() == ORDER_STATE_OFFKDS
					|| order.getState() == ORDER_STATE_SERVICE) {

				sql = "UPDATE TABLES SET orderId = NULL WHERE orderId='" + orderId + "';";
				if (!db.executeUpdate(sql, systemId, true)) {
					outObj.put("message", "Failed to delete Order table. Please try again");
					return outObj;
				}
			}

			ArrayList<OrderItem> orderitems = this.getOrderedItemForVoid(systemId, orderId);

			MenuItem menu = null;
			Double totalFoodBill = 0.0;
			Double totalBarBill = 0.0;
			for (OrderItem orderItem : orderitems) {
				menu = menuDao.getMenuById(systemId, orderItem.getMenuId());
				if(menu.getStation().equals("Bar")) {
					totalBarBill += orderItem.getRate().doubleValue()*orderItem.getQuantity();
				}else {
					totalFoodBill += orderItem.getRate().doubleValue()*orderItem.getQuantity();
				}
				if (!this.updateOrderItemLog(systemId, outletId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), "Void",
						"void", orderItem.getQuantity(), orderItem.getRate(), 0)) {
					outObj.put("message", "Failed to update OrderItem Log. Please try again.");
					return outObj;
				}
				if (!this.removeSubOrder(systemId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0)) {
					outObj.put("message", "Failed to remove Ordered Item. Please try again.");
					return outObj;
				}
				ArrayList<OrderAddOn> addOns = this.getAllOrderedAddOns(systemId, orderId);
				for (OrderAddOn orderAddOn : addOns) {
					menu = menuDao.getMenuById(systemId, orderItem.getMenuId());
					if(menu.getStation().equals("Bar")) {
						totalBarBill += orderItem.getRate().doubleValue()*orderItem.getQuantity();
					}else {
						totalFoodBill += orderItem.getRate().doubleValue()*orderItem.getQuantity();
					}
					if (!this.updateOrderAddOnLog(systemId, outletId, orderId, orderItem.getSubOrderId(), orderItem.getSubOrderDate(), orderItem.getMenuId(),
							orderAddOn.getItemId(), "void", orderAddOn.getQuantity(), orderAddOn.getRate(),
							orderAddOn.getAddOnId())) {
						outObj.put("message", "Failed to update Addon Log. Please try again.");
						return outObj;
					}
					if (!this.removeAddOns(systemId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0)) {
						outObj.put("message", "Failed to remove Ordered Addon. Please try again.");
						return outObj;
					}
				}
			}
			
			sql = "UPDATE OrderItemLog SET state=" + ORDER_STATE_VOIDED + ", reason = 'Void' WHERE orderId='" + orderId + "' AND systemId='" + systemId
					+ "';";

			if (!db.executeUpdate(sql, systemId, true)) {
				outObj.put("message", "Failed to void the order. Please try again.");
				return outObj;
			}
			//IInventory inventoryDao = new InventoryManager(false);
			//inventoryDao.revertInventoryForVoid(systemId, orderId);
			
			outObj.put("status", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			db.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public Boolean complimentaryOrder(String systemId, String outletId, String orderId, String authId) {

		String sql = "UPDATE Orders SET state=" + ORDER_STATE_COMPLIMENTARY + ", "
				+ "authId = '" + authId + "' WHERE orderId='" + orderId + "';";

		ArrayList<OrderItem> orderitems = this.getOrderedItems(systemId, orderId, true);
		IOrderAddOn addOnDao = new OrderAddOnManager(false);

		for (OrderItem orderItem : orderitems) {
			this.updateOrderItemLog(systemId, outletId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), "Complimentary",
					"comp", orderItem.getQuantity(), new BigDecimal("0.0"), 0);
			this.removeSubOrder(systemId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0);
			ArrayList<OrderAddOn> addOns = addOnDao.getAllOrderedAddOns(systemId, orderId);
			for (OrderAddOn orderAddOn : addOns) {
				addOnDao.updateOrderAddOnLog(systemId, outletId, orderId, orderItem.getSubOrderId(), orderItem.getSubOrderDate(), orderItem.getMenuId(),
						orderAddOn.getItemId(), "comp", orderAddOn.getQuantity(), orderAddOn.getRate(),
						orderAddOn.getAddOnId());
				addOnDao.removeAddOns(systemId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0);
			}
		}
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean complimentaryItem(String systemId, String outletId, String orderId, String menuId, String authId, String subOrderId,
			BigDecimal rate, int quantity, String reason) {

		if (this.updateOrderItemLog(systemId, outletId, orderId, subOrderId, menuId, reason, "comp", 1, rate, 0)) {
			this.removeSubOrder(systemId, orderId, subOrderId, menuId, quantity - 1);
			return true;
		}
		return false;
	}

	@Override
	public JSONObject newSubOrder(String systemId, String outletId, Settings settings, Order order, MenuItem menu, Integer quantity, String specs,
			String subOrderId, String waiterId, BigDecimal rate, String tableId) {
		JSONObject outObj = new JSONObject();
		String sql = null;
		ITable tableDao = new TableManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		ArrayList<OnlineOrderingPortal> portals = portalDao.getOnlineOrderingPortals(systemId);
		try {
			outObj.put("status", -1);
			outObj.put("message", "Unknown error!");
			
			boolean isChoiceItem = false;
			boolean isZomatoEasyItem = false;
			JSONArray flags = new JSONArray();
			if(!menu.getFlags().toString().equals("[null]")) {
				flags = menu.getFlags();
			}
			for(int i=0; i< flags.length(); i++) {
				int flag = Integer.parseInt(flags.get(i).toString());
				if(flag == 19 ) {
					isChoiceItem = true;
				}else if(flag >= 40) {
					isZomatoEasyItem = true;
				}
			}
			if(!isChoiceItem) {
				if(menu.getIsCombo()) {
					rate = menu.getComboPrice();
				}else if(order.getOrderType() == DINE_IN) {
					String tableType = tableDao.getTableType(systemId, outletId, tableId);
					rate = tableType.equals(TABLE_TYPE_AC)?menu.getDineInRate():menu.getDineInNonAcRate();
				}else if(order.getOrderType() == NON_CHARGEABLE || order.getOrderType() == BAR) {
					rate = menu.getDineInRate();
				}
				else if(order.getOrderType() == HOME_DELIVERY || (order.getOrderType() == TAKE_AWAY && order.getTakeAwayType() == COUNTER_PARCEL_ORDER))
					rate = menu.getDeliveryRate();
				else {
					int menuAssociation = 0;
					boolean hasIntegration = false;
					for (OnlineOrderingPortal onlineOrderingPortal : portals) {
						if(order.getTakeAwayType() == onlineOrderingPortal.getId()){
							menuAssociation = onlineOrderingPortal.getMenuAssociation();
							if(onlineOrderingPortal.getHasIntegration())
								hasIntegration = true;
							break;
						}
					}
					if(!hasIntegration) {
						if(menuAssociation == 0) {
							rate = menu.getOnlineRate();
						}else if(menuAssociation == 1) {
							rate = menu.getOnlineRate1();
						}else if(menuAssociation == 2) {
							rate = menu.getOnlineRate2();
						}else if(menuAssociation == 3) {
							rate = menu.getOnlineRate3();
						}else if(menuAssociation == 4) {
							rate = menu.getOnlineRate4();
						}else if(menuAssociation == 5) {
							rate = menu.getOnlineRate5();
						}
					}else {
						if(order.getTakeAwayType() == OnlineOrderingPortals.ZOMATO.getValue()) {
							if(isZomatoEasyItem) {
								rate = menu.getComboReducedPrice();
							}
						}
					}
				}
			}
			
			String orderState = Integer.toString(SUBORDER_STATE_PENDING);
			boolean hasKds = settings.getHasKds();
			int kotPrinting = 1;
			if (!hasKds) {
				orderState = Integer.toString(SUBORDER_STATE_COMPLETE);
				kotPrinting = 0;
			}
			if(order.getOrderType() == AccessManager.DINE_IN) {
				if(!settings.getIsCaptainBasedOrdering()) {
					Table table = tableDao.getTableById(systemId, outletId, tableId);
					if(!table.getWaiterId().equals(""))
						waiterId = table.getWaiterId();
				}
			}
			int kotNumber = 0;
			int botNumber = 0;
			if(menu.getStation().equals("Bar"))
				botNumber = this.getNextBOTNumber(systemId, outletId);
			else
				kotNumber = this.getNextKOTNumber(systemId, outletId);
			
			sql = "INSERT INTO OrderItems (systemId, outletId, subOrderId, subOrderDate, orderId, menuId, quantity, rate, specs, state, isKotPrinted, waiterId, kotNumber, botNumber) values ('"
					+ systemId + "', '" + outletId + "', '" + subOrderId + "', '"
					+ (new SimpleDateFormat("yyyy/MM/dd HH:mm")).format(new Date()) + "','" + order.getOrderId() + "', '" + menu.getMenuId()
					+ "', " + Integer.toString(quantity) + ", " + (new DecimalFormat("0.00")).format(rate) + ", '" + specs
					+ "', " + orderState + ", " + kotPrinting + ", '" + waiterId + "', '"+kotNumber+"', '"+botNumber+"');";
			if (!db.executeUpdate(sql, systemId, true)) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to add suborder");
				return outObj;
			}
			outObj.put("status", 0);
			outObj.put("subOrderId", subOrderId);
		} catch (Exception e) {
			db.rollbackTransaction();
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public Integer getOrderCount(String systemId, String outletId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT count(orderId) AS entityId FROM Orders WHERE waiterId=='" + userId + "' AND orderDate=='"
				+ (new SimpleDateFormat("yyyy/MM/dd")).format(dt) + "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	@Override
	public Boolean updateSpecifications(String systemId, String orderId, String subOrderId, String menuId,
			String specs) {

		String sql = "UPDATE OrderItems SET specs='" + specs + "' WHERE subOrderId=='" + subOrderId + "' AND menuId='"
				+ menuId + "' AND orderId='" + orderId + "';";

		String[] specifications = specs.split(",");
		for (String specification : specifications) {

			specification = specification.trim();
			if(specification.trim().isEmpty())
				continue;
			Specifications spec = this.getSpecification(systemId, specification);
			
			if(spec == null) {
				this.addSpecification(systemId, specification);
			}
			
		}
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean changeOrderStatus(String systemId, String orderId, String subOrderId, String menuId) {
		String sql = null;
		OrderItem item = getOrderStatus(systemId, orderId, subOrderId, menuId);
		int curState = item.getState();
		
		IOutlet outletDao = new OutletManager(false);
		Settings settings = outletDao.getSettings(systemId);
		
		ITable tableDao = new TableManager(false);
		ArrayList<Table> tables = tableDao.getJoinedTables(systemId, orderId);
		String tableId = "";
		if (tables.size() > 0) {
			tableId = tables.get(0).getTableId();
		}
		String userId = orderId.split(":")[0];

		if (curState == SUBORDER_STATE_PENDING) {
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_PROCESSING) + " WHERE subOrderId=='"
					+ subOrderId + "' AND menuId='" + menuId + "' AND orderId='" + orderId + "';";
		} else if (curState == SUBORDER_STATE_PROCESSING) {
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_RECIEVED) + " WHERE subOrderId=='"
					+ subOrderId + "' AND menuId='" + menuId + "' AND orderId='" + orderId + "';";
			INotification notificationDao = new NotificationManager(false);
			Boolean retval = db.executeUpdate(sql, systemId, true);
			if (!retval)
				return retval;
			if (allItemsProcessedOrReceived(systemId, orderId)) {
				int notId = notificationDao.getNextNotificationId(userId, systemId);
				String target = tableId.equals("") ? "Home Delivery" : "Table " + tableId;
				if (settings.getHotelType().equals("KDS")) {
					String msg = "Order of " + target + " is ready.";
					sql += "INSERT INTO Notification (notId, systemId, orderId, msg) VALUES (" + Integer.toString(notId)
							+ ", '" + systemId + "','" + orderId + "', '" + msg + "');";
				}
				db.executeUpdate(sql, systemId, true);
			} else if (!tableId.equals("")) {
				int notId = notificationDao.getNextNotificationId(userId, systemId);
				if (settings.getHotelType().equals("KDS")) {
					String msg = item.getTitle() + " of Table " + tableId + " is ready.";
					sql += "INSERT INTO Notification (notId, systemId, orderId, msg) VALUES (" + Integer.toString(notId)
							+ ", '" + systemId + "','" + orderId + "', '" + msg + "');";
				}
				db.executeUpdate(sql, systemId, true);
			}
			IInventory inventoryDao = new InventoryManager(false); 
			inventoryDao.manageInventory(systemId, menuId, orderId, item.getQuantity());

			return retval;
		} else if (curState == SUBORDER_STATE_RECIEVED) {
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_COMPLETE) + " WHERE subOrderId=='"
					+ subOrderId + "' AND menuId='" + menuId + "' AND orderId='" + orderId + "';";
		} else {
			return false;
		}
		return db.executeUpdate(sql, systemId, true);
	}

	private Boolean allItemsProcessedOrReceived(String systemId, String orderId) {
		String sql = "SELECT * FROM OrderItems WHERE orderId=='" + orderId
				+ "' AND state<>" + Integer.toString(SUBORDER_STATE_RECIEVED) + " AND state <> "
				+ Integer.toString(SUBORDER_STATE_COMPLETE);
		return !db.hasRecords(sql, systemId);
	}

	@Override
	public OrderItem getOrderStatus(String systemId, String orderId, String subOrderId, String menuId) {
		String sql = "SELECT MenuItems.title as title,OrderItems.state FROM OrderItems,MenuItems WHERE MenuItems.menuId==OrderItems.menuId AND OrderItems.menuId='"
				+ menuId + "' AND OrderItems.subOrderId='" + subOrderId + "' AND OrderItems.orderId='" + orderId
				+ "' ;";
		return db.getOneRecord(sql, OrderItem.class, systemId);
	}

	@Override
	public ArrayList<OrderItem> getReturnedItems(String systemId, String orderId) {

		String sql = "SELECT OrderItemLog.subOrderId AS subOrderId, OrderItemLog.subOrderDate AS subOrderDate, "
				+ "OrderItemLog.menuId AS menuId, OrderItemLog.quantity AS quantity, "
				+ "OrderItemLog.itemId AS itemId, MenuItems.title AS title, OrderItemLog.rate AS rate, "
				+ "OrderItemLog.reason AS reason, "
				+ "OrderItemLog.state AS state FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId='" + orderId
				+ "' AND OrderItemLog.menuId==MenuItems.menuId;";
		return db.getRecords(sql, OrderItem.class, systemId);
	}

	@Override
	public Order getOrderById(String systemId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "';";

		return db.getOneRecord(sql, Order.class, systemId);
	}

	@Override
	public Boolean hasCheckedOutOrders(String systemId, String serviceDate) {
		String sql = "SELECT * FROM Orders WHERE (state == 0 OR state == 1 OR state == 2) "
				+ "AND orderDate == '" + serviceDate + "';";
		return db.hasRecords(sql, systemId);
	}

	private String getNextOrderId(String systemId, String outletId, String userId) {
		String sql = "SELECT MAX(CAST(SUBSTR(orderId," + Integer.toString(userId.length() + 2)
				+ ") AS integer)) AS entityId FROM Orders WHERE orderId LIKE '" + userId + ":%'  AND outletId='"
				+ outletId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);
		if (entity != null) {
			return userId + ":" + (entity.getId() + 1) + ":" + outletId;
		}
		return userId + ":0" + outletId;
	}

	private int getOrderNumber(String systemId, String orderId) {
		String sql = "SELECT Id AS entityId FROM Orders WHERE orderId = '" + orderId + "' AND systemId='" + systemId
				+ "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);
		return entity.getId();
	}

	@Override
	public int getNextKOTNumber(String systemId, String outletId) {

		int kotNumber = 1;
		IService serviceDao = new ServiceManager(false);
		String serviceDate = serviceDao.getServiceDate(systemId);
		String sql = "SELECT MAX(OrderItems.kotNumber) AS entityId FROM OrderItems, Orders WHERE OrderItems.outletId='"
				+ outletId + "' AND Orders.orderId == OrderItems.orderId AND Orders.orderDate=='" + serviceDate + "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);
		
		kotNumber = entity.getId()+1;
		return kotNumber;
	}

	@Override
	public int getNextBOTNumber(String systemId, String outletId) {

		int botNumber = 1;
		IService serviceDao = new ServiceManager(false);
		String serviceDate = serviceDao.getServiceDate(systemId);
		String sql = "SELECT MAX(OrderItems.botNumber) AS entityId FROM OrderItems, Orders WHERE OrderItems.outletId='"
				+ outletId + "' AND Orders.orderId == OrderItems.orderId AND Orders.orderDate=='" + serviceDate + "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);
		
		botNumber = entity.getId()+1;
		return botNumber;
	}

	@Override
	public String getNextBillNoNumberFormatYearly(String systemId, String outletId) {

		StringBuilder billNo = new StringBuilder();
		int billNum = 0;

		LocalDateTime date = LocalDateTime.now();

		String sql = "SELECT MAX(CAST(billNo AS integer)) AS entityId FROM Orders WHERE systemId='" + systemId + "' AND orderDate > '"+date.getYear()+"/03/31';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);

		if(entity==null) {
			billNum = 1;
		}else
			billNum = entity.getId();

		billNo.append(billNum + 1);

		return billNo.toString();
	}

	@Override
	public String getNextBillNoNumberFormatDaywise(String systemId, String outletId) {

		StringBuilder billNo = new StringBuilder();
		IService serviceDao = new ServiceManager(false);
		String serviceDate = serviceDao.getServiceDate(systemId);
		String sql ="SELECT MAX(CAST(billNo AS integer)) AS entityId FROM Orders WHERE systemId='" + systemId 
				+ "' AND Orders.orderDate=='" + serviceDate + "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);

		int billNum = entity==null?1:entity.getId();

		billNo.append(billNum + 1);

		return billNo.toString();
	}

	@Override
	public String getNextBillNoNumberFormatMonthwise(String systemId, String outletId) {

		StringBuilder billNo = new StringBuilder();
		IService serviceDao = new ServiceManager(false);
		String serviceDate = serviceDao.getServiceDate(systemId);
		String sql = "SELECT MAX(CAST(billNo AS integer)) AS entityId FROM Orders WHERE systemId='" + systemId 
				+ "' AND Orders.orderDate LIKE '" + serviceDate.substring(0,8) + "%';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);

		int billNum = entity==null?1:entity.getId();
		
		billNo.append(billNum + 1);

		return billNo.toString();
	}

	@Override
	public String getNextSubOrderId(String systemId, String orderId) {
		String sql = "SELECT MAX(CAST(subOrderId AS integer)) AS entityId FROM OrderItems WHERE orderId == '" + orderId
				+ "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);
		if (entity != null) {
			return Integer.toString(entity.getId() + 1);
		}
		return "0";
	}

	@Override
	public int getTotalBillAmount(String systemId, String orderId) {
		String sql = "SELECT SUM(rate*quantity) AS entityId FROM OrderItems WHERE orderId='" + orderId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	@Override
	public ArrayList<HomeDelivery> getActiveHomeDeliveries(String systemId, Settings settings, String outletId, String userId) {
		
		String sql = "SELECT Orders.state, Orders.billNo, Orders.remarks, Orders.customerAddress as address, "
				+ "Orders.customerName as customer, Orders.customerNumber as mobileNumber, Orders.orderId, Orders.takeAwayType "
				+ ", Orders.orderDateTime FROM Orders WHERE orderType="
				+ HOME_DELIVERY + " AND ";

		if (settings.getHotelType().equals("PREPAID") && !settings.getHasKds())
			sql += "Orders.state=" + Integer.toString(ORDER_STATE_BILLING);
		else
			sql += "(Orders.state=" + Integer.toString(ORDER_STATE_SERVICE) + " OR Orders.state=" + Integer.toString(ORDER_STATE_BILLING) + ")";

		sql += " AND outletId='" + outletId + "' GROUP BY Orders.orderId ORDER BY Orders.id DESC;";
		return db.getRecords(sql, HomeDelivery.class, systemId);
	}

	@Override
	public ArrayList<HomeDelivery> getActiveTakeAway(String systemId, Settings settings, String outletId, String userId) {
		
		String sql = "SELECT Id AS orderNumber, state, billNo, reference, remarks, customerName as customer, "
				+ "customerNumber as mobileNumber, orderId, isFoodReady, takeAwayType, orderDateTime, orderPreparationTime, onlineOrderData "
				+ " FROM Orders WHERE orderType = " + TAKE_AWAY + " AND ";

		if (settings.getHotelType().equals("PREPAID") && !settings.getHasKds())
			sql += "state=" + Integer.toString(ORDER_STATE_BILLING);
		else
			sql += "(state=" + Integer.toString(ORDER_STATE_SERVICE) + " OR state=" + Integer.toString(ORDER_STATE_BILLING) + ")";

		sql += " AND outletId='" + outletId + "' GROUP BY orderId ORDER BY id DESC;";
		return db.getRecords(sql, HomeDelivery.class, systemId);
	}

	@Override
	public ArrayList<HomeDelivery> getActiveBarOrders(String systemId, Settings settings, String outletId, String userId) {

		String sql = "SELECT Orders.state AS state, Orders.customerName as customer, Orders.customerNumber as mobileNumber, "
				+ "Orders.customerAddress as address, Orders.orderId, Orders.reference, Orders.remarks, Orders.orderDateTime "
				+ " FROM Orders WHERE orderType=" + BAR + " AND ";

		if (settings.getHotelType().equals("PREPAID") && !settings.getHasKds())
			sql += "Orders.state=" + Integer.toString(ORDER_STATE_BILLING);
		else
			sql += "(Orders.state=" + Integer.toString(ORDER_STATE_SERVICE) + " OR Orders.state=" + Integer.toString(ORDER_STATE_BILLING) + ")";

		sql += " AND outletId='" + outletId + "' GROUP BY Orders.orderId ORDER BY Orders.id DESC;";

		return db.getRecords(sql, HomeDelivery.class, systemId);
	}

	@Override
	public BigDecimal getOrderTotal(String systemId, String orderId) {
		String sql = "SELECT TOTAL(OrderItems.quantity*OrderItems.rate) AS entityId FROM OrderItems WHERE OrderItems.orderId='" + orderId
				+ "' ";
		EntityBigDecimal amount = db.getOneRecord(sql, EntityBigDecimal.class, systemId);
		if (amount == null) {
			return new BigDecimal("0.0");
		}
		return amount.getId();
	}

	@Override
	public Boolean isHomeDeliveryOrder(String systemId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND orderType==0 AND systemId='" + systemId
				+ "'";
		return db.hasRecords(sql, systemId);
	}

	@Override
	public Boolean isTakeAwayOrder(String systemId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND orderType==2 AND systemId='" + systemId
				+ "'";
		return db.hasRecords(sql, systemId);
	}

	@Override
	public Boolean isBarOrder(String systemId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND orderType==3 AND systemId='" + systemId
				+ "'";
		return db.hasRecords(sql, systemId);
	}
	
	@Override
	public ArrayList<OrderItem> getReturnedOrders(String systemId, String orderId){
		String sql = null;
		
		sql = "SELECT * FROM OrderItemLog WHERE orderId=='" + orderId + "'";
		
		return db.getRecords(sql, OrderItem.class, systemId);
	}
	
	@Override
	public ArrayList<OrderItem> getCancellableOrderedItems(String systemId, String orderId){
		String sql = null;

		sql = "SELECT * FROM OrderItems WHERE orderId=='" + orderId + "' AND state!="
				+ Integer.toString(SUBORDER_STATE_PENDING) + "";
		
		return db.getRecords(sql, OrderItem.class, systemId);
	}
	
	@Override
	public Boolean deleteOrder(String systemId, String orderId) {
		String sql = null;
		sql = "DELETE FROM OrderItems WHERE orderId=='" + orderId + "'; "
					+ "DELETE FROM Orders WHERE orderId=='" + orderId + "'; "
					+ "UPDATE TABLES SET orderId = NULL WHERE orderId=='" + orderId + "'; "
					+ "DELETE FROM OrderSpecifications WHERE orderId=='" + orderId + "'; "
					+ "DELETE FROM OrderAddons WHERE orderId=='" + orderId + "'; ";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean updateOrderItemLog(String systemId, String outletId, String orderId, String subOrderId, String menuId, String reason,
			String type, int quantity, BigDecimal rate, int itemId) {

		int state = SUBORDER_STATE_RETURNED;
		if (type.equals("void"))
			state = SUBORDER_STATE_VOIDED;
		else if (type.equals("comp"))
			state = SUBORDER_STATE_COMPLIMENTARY;
		else if (type.equals("cancel"))
			state = SUBORDER_STATE_CANCELED;

		OrderItem orderedItem = this.getOrderedItem(systemId, orderId, subOrderId, menuId);
		if(orderedItem == null)
			return false;

		String sql = "INSERT INTO OrderItemLog "
				+ "(systemId, outletId, orderId, subOrderId, subOrderDate, menuId, state, reason, dateTime, quantity, rate, itemId) "
				+ "VALUES('" + escapeString(systemId) + "', '" + escapeString(outletId) + "', '" + escapeString(orderId) + "', '"
				+ escapeString(subOrderId) + "', '" + escapeString(orderedItem.getSubOrderDate()) + "', '"
				+ escapeString(menuId) + "', " + state + ", '" + reason + "', '"
				+ new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + "', " + quantity + ", " + rate + ", "
				+ itemId + ");";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean updateOrderPrintCount(String systemId, String orderId) {

		String sql = "SELECT printCount AS entityId FROM Orders WHERE orderId = '"
				+ orderId + "';";

		int printCount = getOrderPrintCount(systemId, orderId) + 1;

		sql = "UPDATE Orders SET printCount = " + printCount + " WHERE orderId = '"
				+ orderId + "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean updateOrderSMSStatusDone(String systemId, String orderId) {
		String sql = "UPDATE Orders SET isSmsSent = 1 WHERE orderId = '" + orderId
				+ "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public int getOrderPrintCount(String systemId, String orderId) {

		String sql = "SELECT printCount AS entityId FROM Orders WHERE orderId = '"
				+ orderId + "';";

		return db.getOneRecord(sql, EntityId.class, systemId).getId();
	}

	@Override
	public boolean updateKOTStatus(String systemId, String orderId) {

		String sql = "UPDATE OrderItems SET isKotPrinted = 1 WHERE orderId = '"
				+ orderId + "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean updateItemRatesInOrder(String systemId, String orderId, String newTableType) {
		
		ArrayList<OrderItem> orderItems = this.getOrderedItemForBill(systemId, orderId, false);
		BigDecimal rate = new BigDecimal("0.0");
		String sql = "";
		MenuItem item = null;
		Double totalFoodBill = 0.0;
		Double totalBarBill = 0.0;
		IMenuItem menuDao = new MenuItemManager(false);
		
		for (OrderItem orderItem : orderItems) {
			item = menuDao.getMenuById(systemId, orderItem.getMenuId());
			rate = newTableType.equals("AC")?item.getDineInRate():item.getDineInNonAcRate();
			sql += "UPDATE OrderItems SET rate = " + rate + " WHERE systemId = '"+systemId
					+ "' AND orderId = '" +orderId+ "' AND menuId = '"+item.getMenuId()+"';" ;
			if(item.getStation().equals("Bar")) {
				totalBarBill += rate.doubleValue()*orderItem.getQuantity();
			}else {
				totalFoodBill += rate.doubleValue()*orderItem.getQuantity();
			}
		}
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean applyCustomerGST(String systemId, String orderId, String gst) {

		String sql = "UPDATE Orders SET customerGST = '" + escapeString(gst) + "' WHERE systemId = '" + systemId
				+ "' AND orderId = '" + orderId + "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean applyOrderRemark(String systemId, String orderId, String remark) {

		String sql = "UPDATE Orders SET remarks = '" + escapeString(remark) + "' WHERE systemId = '" + systemId
				+ "' AND orderId = '" + orderId + "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public ArrayList<EntityString> getCaptainOrderService(String systemId, String startDate, String endDate) {
		String sql = "SELECT DISTINCT waiterId AS entityId FROM OrderItems WHERE OrderItems.subOrderDate";
		if (startDate.equals(endDate))
			sql += " LIKE '" + startDate + "%';";
		else
			sql += " BETWEEN '" + startDate + "' AND '" + endDate + "';";

		System.out.println(sql);
		return db.getRecords(sql, EntityString.class, systemId);
	}
	
	// ----------------------------------------------KOT
	
	@Override
	public ArrayList<OrderItem> getOrderedItemsForKOT(String systemId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.orderId AS orderId, "
				+ "OrderItems.subOrderDate AS subOrderDate, OrderItems.quantity AS quantity, MenuItems.title AS title, "
				+ "MenuItems.menuId AS menuId, MenuItems.station AS station, "
				+ "OrderItems.specs AS specs, OrderItems.kotNumber, OrderItems.botNumber FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId AND OrderItems.isKotPrinted = 0 "
				+ " ORDER BY MenuItems.collection;";
		return db.getRecords(sql, OrderItem.class, systemId);
	}

	@Override
	public ArrayList<OrderItem> getOrderedItemsForReprintKOT(String systemId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.orderId AS orderId, "
				+ "OrderItems.subOrderDate AS subOrderDate, OrderItems.quantity AS quantity, MenuItems.title AS title, "
				+ "MenuItems.menuId AS menuId, MenuItems.station AS station, "
				+ "OrderItems.specs AS specs, OrderItems.kotNumber, OrderItems.botNumber FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId "
				+ " ORDER BY MenuItems.collection;";
		return db.getRecords(sql, OrderItem.class, systemId);
	}

	@Override
	public ArrayList<Order> getOrdersOfOneCustomer(String systemId, String mobileNumber) {
		String sql = "SELECT Orders.*, Payments.foodDiscount, Payments.barDiscount, Payments.paymentType AS paymentType, Payments.total, Payments.creditAmount, " + 
				"(Payments.cashPayment+Payments.cardPayment+Payments.appPayment+Payments.walletPayment) AS totalPayment, Payments.loyaltyAmount AS loyaltyPaid " + 
				"FROM Orders, Payments WHERE customerNumber = '" + mobileNumber + "' AND Orders.systemId = '" + systemId + "'" +
				" AND Orders.orderId = Payments.orderId;";
		return db.getRecords(sql, Order.class, systemId);
	}

	@Override
	public String getOrderType(int orderTypeCode){
		if(orderTypeCode == DINE_IN)
			return "DINEIN";
		else if(orderTypeCode == HOME_DELIVERY)
			return "DELIVERY";
		else if(orderTypeCode == BAR)
			return "BAR";
		else if(orderTypeCode == NON_CHARGEABLE)
			return "NON CHARGEABLE";
		else
			return "TAKEAWAY";
	}

	@Override
	public boolean toggleChargeInOrder(String systemId, String orderId, int chargeId) {
		Order order = this.getOrderById(systemId, orderId);
		JSONArray excludedCharges = order.getExcludedCharges();
		boolean chargeExists = false;
		for (int i=0; i<excludedCharges.length(); i++) {
			chargeExists = false;
			try {
				if(excludedCharges.get(i).equals(chargeId)) {
					excludedCharges.remove(i);
					chargeExists = true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!chargeExists)
			excludedCharges.put(chargeId);
		String sql = "UPDATE Orders SET excludedCharges = '"+excludedCharges.toString()+"' WHERE systemId = '"+systemId+"' AND orderId = '"+order.getOrderId()+"';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean toggleTaxInOrder(String systemId, String orderId, int taxId) {
		Order order = this.getOrderById(systemId, orderId);
		JSONArray excludedTaxes = order.getExcludedTaxes();
		boolean taxExists = false;
		for (int i=0; i<excludedTaxes.length(); i++) {
			taxExists = false;
			try {
				if(excludedTaxes.get(i).equals(taxId)) {
					excludedTaxes.remove(i);
					taxExists = true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!taxExists)
			excludedTaxes.put(taxId);
		String sql = "UPDATE Orders SET excludedTaxes = '"+excludedTaxes.toString()+"' WHERE systemId = '"+systemId+"' AND orderId = '"+order.getOrderId()+"';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public EntityString getMobileNoFromOrderId(String systemId, String orderId) {
		String sql = "SELECT customerNumber AS entityId FROM Orders WHERE orderId='" + orderId + "' AND systemId='"
				+ systemId + "'";
		return db.getOneRecord(sql, EntityString.class, systemId);
	}@Override
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
				+ "AND OrderAddOnLog.addOnId == MenuItems.menuId;";
		}
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
				+ "AND OrderAddOnLog.addOnId == MenuItems.menuId;";
		}
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
				+ "AND OrderAddOnLog.addOnId == MenuItems.menuId;";
		}
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
				+ "AND OrderAddOns.systemId='" + systemId + "' UNION ALL "
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
				+ "AND OrderAddOns.systemId='" + systemId + "' AND OrderAddOnLog.subOrderId='" + subOrderId
				+ "' AND OrderAddOnLog.menuId == '"+menuId+"' AND OrderAddOnLog.itemId == '" + itemId + "';";
		return db.getRecords(sql, OrderAddOn.class, systemId);
	}

	@Override
	public Boolean removeAddOns(String systemId, String orderId, String subOrderId, String menuId, int quantity) {
		String sql = "DELETE FROM OrderAddOns WHERE orderId='" + orderId + "' AND subOrderId =" + subOrderId
				+ " AND menuId='" + menuId + "' ";

		if (quantity > 0)
			sql += "AND itemId =" + (quantity + 1) + ";";

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
					+ "' AND subOrderId =" + subOrderId + " AND menuId='" + menuId + "' AND systemId='" + systemId
					+ "' AND addOnId='" + addOnId + "' AND itemId =" + itemId + ";";
		}
		return db.executeUpdate(sql, systemId, true);
	}
	
	@Override
	public Boolean addOrderSpecification(String systemId, String outletId, String orderId, String subOrderId, String menuId, int itemId,
			String specification) {

		String sql = "INSERT INTO OrderSpecifications (systemId, outletId, orderId, subOrderId, menuId, itemId, specification) values ('"
				+ systemId + "', '" + outletId + "', '" + orderId + "', '" + subOrderId + "', '" + menuId + "', " + itemId + ", '"
				+ escapeString(specification) + "');";

		Specifications spec = this.getSpecification(systemId, specification);
		
		if(spec == null) {
			this.addSpecification(systemId, specification);
		}

		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Specifications getSpecification(String hotelId, String spec) {
		String sql = "SELECT * FROM Specifications WHERE  specification = '"+spec+"';";
		return db.getOneRecord(sql, Specifications.class, hotelId);
	}

	@Override
	public boolean addSpecification(String outletId, String specification) {

		int type = 1;
		if(specification.toLowerCase().contains("no")) {
			type = 0;
		}
		String sql = "INSERT INTO Specifications (specification, type, isDisplayable) VALUES('" 
				+ escapeString(specification) + "', "
				+ type+", 'true');";
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public ArrayList<Specifications> getSpecifications(String systemId, String outletId) {
		String sql = "SELECT * FROM Specifications WHERE outletId = '"+outletId+"' ORDER BY specification;";
		return db.getRecords(sql, Specifications.class, systemId);
	}

	@Override
	public ArrayList<OrderSpecification> getOrderedSpecification(String systemId, String orderId, String menuId,
			String subOrderId, int itemId) {

		String sql = "SELECT * FROM OrderSpecifications WHERE orderId='" + orderId + "' AND itemId == " + itemId
				+ " AND menuId='" + menuId + "' AND subOrderId='" + subOrderId + "';";
		return db.getRecords(sql, OrderSpecification.class, systemId);
	}

	@Override
	public ArrayList<OrderSpecification> getOrderedSpecification(String systemId, String orderId, String menuId) {

		String sql = "SELECT * FROM OrderSpecifications WHERE orderId='" + orderId + "' AND menuId='" + menuId + "';";
		return db.getRecords(sql, OrderSpecification.class, systemId);
	}

	@Override
	public Boolean updateWalletTransactionId(String systemId, String orderId, int walletTransactionId) {
		
		String sql = "UPDATE Orders set walletTransactionId = "+ walletTransactionId+ " WHERE orderId = '"+orderId
				+ "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean removeCustomerFromOrder(String systemId, String orderId) {
		
		String sql = "UPDATE Orders SET customerNumber = '', customerName = '', customerAddress = '' WHERE orderId = '"+orderId+"';";
		
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean redeemPromotionalCash(String systemId, String orderId, BigDecimal promotionalCash) {
		
		String sql = "UPDATE Orders SET promotionalCash = "+promotionalCash+" WHERE orderId = '"+orderId+"';";
		
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean updateRiderDetails(String systemId, String orderId, String riderName, String riderNumber, String riderStatus) {
		
		if(riderName.isEmpty() && riderNumber.isEmpty() && riderStatus.isEmpty() && orderId.isEmpty()) {
			return false;
		}
		Order order = this.getOrderById(systemId, orderId);
		if(order == null) {
			return false;
		}
		if(order.getRiderStatus().equals(riderStatus)) {
			return false;
		}
		String sql = "UPDATE Orders SET riderName = '"+riderName+"', riderNumber = '"+riderNumber+"', riderStatus='"+riderStatus+
				"' WHERE orderId = '"+orderId+"';";
		
		return db.executeUpdate(sql, systemId, true);
	}
	
	public boolean updateZoamtoVoucherInOrder(String systemId, String orderId, String discountName, BigDecimal discountAmount) {
		
		String sql = "UPDATE Orders SET discountCodes = '"+discountName+"', zomatoVoucherAmount = "+discountAmount+" WHERE orderId = '"+orderId+"' AND systemId = '"+systemId+"';";
		
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean updateEWardsOfferDetails(String systemId, String orderId, int points, String couponCode,
			int offerType) {
		
		JSONObject eWards = new JSONObject();
		try {
			eWards.put("points", points);
			eWards.put("couponCode", couponCode);
			eWards.put("offerType", offerType);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Order order = this.getOrderById(systemId, orderId);
		JSONArray discountArr = order.getDiscountCodes();
		
		String sql = "UPDATE Orders SET eWards = '"+eWards.toString() + "', ";
		
		if(offerType == 1) {
			discountArr.put("EWARDS");
			sql += "discountCodes = '"+discountArr.toString()+"', fixedRupeeDiscount = " + points + " WHERE systemId = '" + systemId + "' AND orderId = '" + orderId + "';";
		}else {
			discountArr.put(couponCode);
			sql += "discountCodes = '" + discountArr.toString() + "' WHERE systemId = '" + systemId + "' AND orderId = '" + orderId + "';";
		}
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean checkIfOnlineOrderExists(String systemId, String outletId, String externalOrderId, String serviceDate) {
		
		String sql = "SELECT * FROM Orders WHERE outletId = '"+outletId+"' AND '" + externalOrderId + "' AND orderDate = '"+serviceDate+"';";
		
		return db.hasRecords(sql, systemId);
	}

	@Override
	public void deleteBlankOrders(String systemId, String serviceDate) {
		
		String sql = "SELECT orderId FROM Orders WHERE orderDate = '"+serviceDate+"';";
		
		ArrayList<Order> orders = db.getRecords(sql, Order.class, systemId);
		
		for (Order order : orders) {
			
			sql = "SELECT * FROM OrderItems WHERE orderId = '"+order.getOrderId()+"';";
			
			if(!db.hasRecords(sql, systemId)) {
				this.deleteOrder(systemId, order.getOrderId());
			}
		}
	}
	
	@Override
	public boolean assignBillNumberToOrder(String systemId, String outletId, String orderId) {
		IOutlet outletDao = new OutletManager(false);
		Settings settings  = outletDao.getSettings(outletId);
		String sql = "";
		
		String billNo = "";
		Order order = this.getOrderById(outletId, orderId);
		if(order.getBillNo().equals("")) {
			if (settings.getBillType().equals(BILLTYPE_YEARLY_REFRESH)) {
				billNo = this.getNextBillNoNumberFormatYearly(systemId, outletId);
			}else if(settings.getBillType().equals(BILLTYPE_DAILY_REFRESH)) {
				billNo = this.getNextBillNoNumberFormatDaywise(systemId, outletId);
			}else if(settings.getBillType().equals(BILLTYPE_MONTHLY_REFRESH)) {
				billNo = this.getNextBillNoNumberFormatMonthwise(systemId, outletId);
			}
			
			String billNo2 = "";
			if (settings.getBillType().equals(BILLTYPE_YEARLY_REFRESH)) {
				billNo2 = billNo;
			}else {
				billNo2 = this.getNextBillNoNumberFormatYearly(systemId, outletId);
			}
			
			sql += "UPDATE Orders SET billNo = '"+billNo+"', billNo2 = '"+billNo2+"' WHERE orderId = '"+orderId+"';"
				+ "UPDATE OrderItems SET billNo = '"+billNo+"', billNo2 = '"+billNo2+"' WHERE orderId = '"+orderId+"';";
		}
		
		return db.executeUpdate(sql, outletId, true);
	}
}
