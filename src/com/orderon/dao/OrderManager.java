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
	public ArrayList<Order> getAllOrders(String hotelId, String serviceDate, int orderTypeFilter, String query, boolean visible) {
		
		String sql = "SELECT Orders.*, cashPayment+cardPayment+appPayment+walletPayment AS totalPayment, foodDiscount+barDiscount AS discount, "
				+ "creditAmount, cardType AS paymentType, firstName FROM Orders " + 
				"LEFT OUTER JOIN Payment ON Orders.orderId = Payment.orderId " + 
				"LEFT OUTER JOIN Employee ON Orders.deliveryBoy == Employee.employeeId " + 
				"WHERE Orders.hotelId='" + escapeString(hotelId) + "' ";
		if(!visible) 
			sql += " AND Orders.state!=" + ORDER_STATE_HIDDEN + " ";
		ArrayList<Order> orders = new ArrayList<Order>();
		if(orderTypeFilter!=100) {
			sql += " AND Orders.inhouse = " + orderTypeFilter;
		}
		if(query.isEmpty()) {
			sql += " AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			System.out.println(sql);
			return db.getRecords(sql, Order.class, hotelId);
		}
		String sql2;
		
		IEmployee employeeDao = new EmployeeManager(false);
		Employee employee = employeeDao.getEmployeeByName(hotelId, query);
		if(employee != null) {
			sql2 = sql + " AND deliveryBoy = '" + employee.getEmployeeId() + "' AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			return orders;
		} else if (query.trim().length() > 0) {
			sql2 = sql + " AND Orders.billNo = '" + query + "' AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			sql2 = sql + " AND tableId LIKE '%" + query + "%' AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			sql2 = sql + " AND customerName LIKE '" + query + "%' AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			sql2 = sql + " AND reference LIKE '" + query + "%' AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			return orders;
		}
		
		sql += " AND Orders.orderDate='" + serviceDate + "' ORDER BY Orders.id DESC;";
		return db.getRecords(sql, Order.class, hotelId);
	}
	
	private ArrayList<Order> getCashOrders(String hotelId, String serviceDate) {
		
		String sql = "SELECT Orders.orderId, Orders.billNo, cashPayment, Payment.foodBill, Payment.barBill " +
					"FROM Orders LEFT OUTER JOIN Payment ON Orders.orderId == Payment.orderId " + 
					"WHERE Orders.hotelId='" + escapeString(hotelId) + "' AND Orders.orderDate='" + serviceDate + "' " + 
					"AND Payment.cardType LIKE '%CASH%' " +
					"AND Orders.customerGST isNull " +
					"AND Orders.state != " + ORDER_STATE_HIDDEN + " " +
					"ORDER BY cashPayment DESC;";

		return db.getRecords(sql, Order.class, hotelId);
	}
	
	private ArrayList<Order> getCashOrders(String hotelId, String serviceDate, double foodBillDeduction, double barBillDeduction) {
		
		String sql = "SELECT Orders.orderId, Orders.billNo, cashPayment, Payment.foodBill, Payment.barBill " + 
					"FROM Orders LEFT OUTER JOIN Payment ON Orders.orderId == Payment.orderId " + 
					"WHERE Orders.hotelId='" + escapeString(hotelId) + "' AND Orders.orderDate='" + serviceDate + "' " +
					"AND Payment.cardType LIKE '%CASH%' " +
					"AND Orders.customerGST isNull " +
					"AND Orders.state != " + ORDER_STATE_HIDDEN;
		
		if(barBillDeduction == 0) 
			sql += " AND Payment.barBill == 0 ORDER BY Payment.foodBill DESC";
		else if(foodBillDeduction == 0)
			sql += " AND Payment.foodBill == 0 ORDER BY Payment.barBill DESC";
		else  if(barBillDeduction>foodBillDeduction) {
			if(foodBillDeduction > 30.0)
				sql += " ORDER BY Payment.barBill DESC";
			else
				sql += " ORDER BY Payment.foodBill , Payment.barBill DESC";
				
		}else if(foodBillDeduction>barBillDeduction) {
			if(barBillDeduction > 30.0)
				sql += " ORDER BY Payment.foodBill DESC";
			else
				sql += " ORDER BY Payment.barBill , Payment.foodBill DESC";
		}
			
		return db.getRecords(sql, Order.class, hotelId);
	}
	
	private ArrayList<Order> getAllVisibleOrders(String hotelId, String startDate) {
		
		IService serviceDao = new ServiceManager(false);
		String sql = "SELECT Orders.orderId, Orders.billNo " + 
					"FROM Orders " + 
					"WHERE Orders.hotelId='" + escapeString(hotelId) + "' AND Orders.orderDate BETWEEN '" + startDate +
					"' AND '" + serviceDao.getServiceDate(hotelId) + "' " + 
					"AND Orders.state != " + ORDER_STATE_HIDDEN + " " +
					"ORDER BY id;";

		return db.getRecords(sql, Order.class, hotelId);
	}
	
	private ArrayList<Order> getAllOrdersForMonth(String hotelId, String month) {
		
		String sql = "SELECT id, orderId, billNo, state " + 
					"FROM Orders " + 
					"WHERE hotelId='" + escapeString(hotelId) + "' AND orderDate LIKE '" + month + "%' " + 
					"ORDER BY id;";

		return db.getRecords(sql, Order.class, hotelId);
	}
	
	private ArrayList<Order> getAllOrdersForPeriod(String hotelId, String month) {
		
		IService serviceDao = new ServiceManager(false);
		String serviceDate = serviceDao.getServiceDate(hotelId);
		String sql = "SELECT id, orderId, billNo, state " + 
					"FROM Orders " + 
					"WHERE hotelId='" + escapeString(hotelId) + "' AND orderDate BETWEEN '" + month + "/01' " + 
					"AND '" + serviceDate + "' ORDER BY id;";

		return db.getRecords(sql, Order.class, hotelId);
	}
	
	private ArrayList<Order> getInVisibleOrders(String hotelId, String serviceDate) {
		
		String sql = "SELECT Orders.orderId, Orders.billNo " + 
					"FROM Orders " + 
					"WHERE Orders.hotelId='" + escapeString(hotelId) + "' AND Orders.orderDate = '" + serviceDate +
					"' AND Orders.state == " + ORDER_STATE_HIDDEN + " " +
					"ORDER BY id;";

		return db.getRecords(sql, Order.class, hotelId);
	}
	
	@Override
	public ArrayList<Order> getCompletedOrders(String hotelId) {

		IService serviceDao = new ServiceManager(false);
		String sql = "SELECT * FROM Orders WHERE hotelId='" + escapeString(hotelId) +
					"' AND orderDate='" + serviceDao.getServiceDate(hotelId) +
					"' AND Orders.state = "+ORDER_STATE_BILLING+" ORDER BY id DESC;";

		return db.getRecords(sql, Order.class, hotelId);
	}

	@Override
	public BigDecimal getTaxableFoodBill(String hotelId, String orderId) {

		String sql = "SELECT SUM(OrderItems.rate*OrderItems.qty) AS entityId FROM OrderItems, MenuItems WHERE orderId = '"
				+ orderId + "' AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId"
				+ " AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '" + hotelId
				+ "' AND (MenuItems.vegType == 1 OR MenuItems.vegType == 2);";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		BigDecimal totalBill = entity.getId();

		sql = "SELECT SUM(OrderAddOns.rate*OrderAddOns.qty) AS entityId FROM OrderAddOns, OrderItems, MenuItems"
				+ " WHERE OrderAddOns.orderId = '" + orderId + "'"
				+ " AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId"
				+ " AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '" + hotelId + "'"
				+ " AND (MenuItems.vegType == 1 OR MenuItems.vegType == 2)"
				+ " AND OrderAddOns.orderId == OrderItems.orderId"
				+ " AND OrderAddOns.subOrderId == OrderItems.subOrderId"
				+ " AND OrderAddOns.menuId == OrderItems.menuId;";

		entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		totalBill.add(entity.getId());

		return totalBill;
	}

	@Override
	public BigDecimal getTaxableBarBill(String hotelId, String orderId) {

		String sql = "SELECT SUM(OrderItems.rate*OrderItems.qty) AS entityId FROM OrderItems, MenuItems WHERE orderId = '"
				+ orderId + "' AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId"
				+ " AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '" + hotelId
				+ "' AND (MenuItems.vegType == 3 OR MenuItems.vegType == 4);";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		BigDecimal totalBill = entity.getId();

		sql = "SELECT SUM(OrderAddOns.rate*OrderAddOns.qty) AS entityId FROM OrderAddOns, OrderItems, MenuItems"
				+ " WHERE OrderAddOns.orderId = '" + orderId + "'"
				+ " AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId"
				+ " AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '" + hotelId + "'"
				+ " AND (MenuItems.vegType == 3 OR MenuItems.vegType == 4)"
				+ " AND OrderAddOns.orderId == OrderItems.orderId"
				+ " AND OrderAddOns.subOrderId == OrderItems.subOrderId"
				+ " AND OrderAddOns.menuId == OrderItems.menuId;";

		entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		totalBill.add(entity.getId());

		return totalBill;
	}

	@Override
	public OrderItem getOrderedItem(String hotelId, String orderId, String subOrderId, String menuId) {

		String sql = "SELECT OrderItems.subOrderDate AS subOrderDate, OrderItems.qty AS qty, OrderItems.waiterId AS waiterId, "
				+ "MenuItems.title AS title, MenuItems.vegType AS vegType, MenuItems.collection AS collection, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.rate AS rate, "
				+ "MenuItems.isTaxable AS isTaxable, OrderItems.itemIsMoved AS itemIsMoved, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, "
				+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE OrderItems.orderId='" + orderId
				+ "' AND OrderItems.subOrderId='" + subOrderId + "' AND OrderItems.menuId=='" + menuId
				+ "' AND OrderItems.hotelId='" + hotelId + "' AND OrderItems.menuId==MenuItems.menuId;";
		return db.getOneRecord(sql, OrderItem.class, hotelId);
	}

	@Override
	public ArrayList<OrderItem> getOrderedItems(String hotelId, String orderId, boolean showReturned) {
		return getOrderedItems(hotelId, orderId, "menuId", showReturned);
	}
	
	
	/**
	 * @param hotelId
	 * OutletManager Id of the current OutletManager.
	 * @param orderId
	 * Order Id of the current order.
	 * @param orderBy
	 * Sort by this field. You can provide multiple fields in a comma seperated format.
	 * @return
	 * Returns all the items in the order, sorted by the given orderBy field.
	 */
	private ArrayList<OrderItem> getOrderedItems(String hotelId, String orderId, String orderBy, boolean showReturned) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.Id AS Id, OrderItems.menuId AS menuId, OrderItems.qty AS qty, OrderItems.waiterId AS waiterId, "
				+ "MenuItems.title AS title, MenuItems.vegType AS vegType, MenuItems.collection AS collection, OrderItems.qty*OrderItems.rate AS finalAmount, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.specs AS reason, OrderItems.rate AS rate, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, "
				+ "MenuItems.isTaxable AS isTaxable, OrderItems.state AS state, MenuItems.taxes AS taxes, MenuItems.charges AS charges, "
				+ "substr(OrderItems.subOrderDate, 12, 5) AS time  FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId AND OrderItems.hotelId='" + hotelId + "' ";
		if(showReturned) {
			sql += "UNION ALL "
				+ "SELECT OrderItemLog.subOrderId AS subOrderId, OrderItemLog.subOrderDate AS subOrderDate, "
				+ "OrderItemLog.Id AS Id, OrderItemLog.menuId AS menuId, OrderItemLog.quantity AS qty, OrderItemLog.subOrderId AS waiterId, "
				+ "MenuItems.title AS title, MenuItems.vegType AS vegType, MenuItems.collection AS collection, 0 AS finalAmount, "
				+ "MenuItems.station AS station, (SELECT specs FROM OrderItems WHERE OrderItems.orderId = '" + orderId
				+ "') AS specs, OrderItemLog.reason AS reason, OrderItemLog.rate AS rate, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, "
				+ "MenuItems.isTaxable AS isTaxable, "
				+ "OrderItemLog.state AS state, MenuItems.taxes AS taxes, MenuItems.charges AS charges, substr(OrderItemLog.datetime, 12, 5) AS time "
				+ "FROM MenuItems, OrderItemLog WHERE OrderItemLog.orderId='" + orderId
				+ "' AND OrderItemLog.menuId==MenuItems.menuId AND OrderItemLog.hotelId='" + hotelId + "' "
				+ "AND (OrderItemLog.state = " + AccessManager.SUBORDER_STATE_RETURNED 
				+ " OR OrderItemLog.state = "+ AccessManager.SUBORDER_STATE_VOIDED
				+ " OR OrderItemLog.state = "+ AccessManager.ORDER_STATE_CANCELED+") ";
		}
		sql += "ORDER BY "+orderBy+";";
		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	@Override
	public ArrayList<OrderItem> getOrderedItemForVoid(String hotelId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.Id AS Id, OrderItems.menuId AS menuId, OrderItems.qty AS qty, "
				+ "MenuItems.title AS title, MenuItems.vegType AS vegType, MenuItems.collection AS collection, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.rate AS rate, "
				+ "MenuItems.isTaxable AS isTaxable, OrderItems.state AS state, "
				+ "substr(OrderItems.subOrderDate, 12, 5) AS time  FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId AND OrderItems.hotelId='" + hotelId + "' ORDER BY menuId;";

		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	@Override
	public ArrayList<EntityString> getUniqueMenuIdForComplimentaryOrder(String hotelId, String orderId) {
		String sql = "SELECT distinct menuId AS entityId FROM OrderItemLog WHERE OrderItemLog.orderId='" + orderId
				+ "'; AND hotelId='" + hotelId + "' AND state=" + ORDER_STATE_COMPLIMENTARY + ";";

		return db.getRecords(sql, EntityString.class, hotelId);
	}

	@Override
	public ArrayList<OrderItem> getOrderedItemForBill(String hotelId, String orderId, boolean showReturned) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.Id AS Id, OrderItems.menuId AS menuId, OrderItems.waiterId AS waiterId, SUM(OrderItems.qty) AS qty, "
				+ "MenuItems.title AS title, MenuItems.vegType AS vegType, MenuItems.taxes AS taxes, MenuItems.charges AS charges, "
				+ "MenuItems.collection AS collection, MenuItems.station AS station, SUM(OrderItems.qty*OrderItems.rate) AS finalAmount, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, MenuItems.collection AS collection, "
				+ "OrderItems.rate AS rate, OrderItems.specs AS specs, MenuItems.isTaxable AS isTaxable, "
				+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE OrderItems.orderId='" + orderId
				+ "' AND OrderItems.hotelId='" + hotelId + "' AND OrderItems.menuId == MenuItems.menuId AND MenuItems.flags NOT LIKE '%19%' "
				+ "GROUP BY OrderItems.menuId ";
		
		if(showReturned) {
			sql += "UNION ALL "
				+ "SELECT OrderItemLog.subOrderId AS subOrderId, OrderItemLog.subOrderDate AS subOrderDate, "
				+ "OrderItemLog.Id AS Id, OrderItemLog.menuId AS menuId, OrderItemLog.subOrderId AS waiterId, SUM(OrderItemLog.quantity) AS qty, "
				+ "MenuItems.title AS title, MenuItems.vegType AS vegType, MenuItems.taxes AS taxes, MenuItems.charges AS charges, "
				+ "MenuItems.collection AS collection, MenuItems.station AS station, SUM(OrderItemLog.quantity*OrderItemLog.rate) AS finalAmount, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, MenuItems.collection AS collection, "
				+ "OrderItemLog.rate AS rate, OrderItemLog.subOrderId AS specs, MenuItems.isTaxable AS isTaxable, "
				+ "OrderItemLog.state AS state FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId='" + orderId
				+ "' AND OrderItemLog.hotelId='" + hotelId + "' AND OrderItemLog.menuId == MenuItems.menuId AND "
				+ "MenuItems.flags NOT LIKE '%19%' AND "
				+ "(OrderItemLog.state == "+AccessManager.SUBORDER_STATE_CANCELED+" OR  OrderItemLog.state == "+AccessManager.SUBORDER_STATE_RETURNED+") "
				+ "GROUP BY OrderItemLog.menuId";
		}
		
		sql += ";";
		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	@Override
	public ArrayList<OrderItem> getOrderedItemForBillCI(String hotelId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.menuId AS menuId, OrderItems.qty AS qty, OrderItems.waiterId AS waiterId, MenuItems.title AS title, "
				+ "MenuItems.vegType AS vegType, MenuItems.collection AS collection, MenuItems.taxes AS taxes, "
				+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.rate AS rate, OrderItems.qty*OrderItems.rate AS finalAmount, "
				+ "OrderItems.specs AS specs, MenuItems.isTaxable AS isTaxable, MenuItems.charges AS charges, "
				+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId AND OrderItems.hotelId='" + hotelId + "' "
				+ "AND MenuItems.flags LIKE '%19%'";

		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	@Override
	public ArrayList<OrderItem> getComplimentaryOrderedItemForBill(String hotelId, String orderId) {

		ArrayList<EntityString> menuIds = this.getUniqueMenuIdForComplimentaryOrder(hotelId, orderId);
		ArrayList<OrderItem> orderItems = new ArrayList<OrderItem>();
		OrderItem tempItem = null;
		for (EntityString menuId : menuIds) {

			String sql = "SELECT OrderItemLog.subOrderId AS subOrderId, "
					+ "OrderItemLog.subOrderDate AS subOrderDate, OrderItemLog.Id AS Id, "
					+ "OrderItemLog.menuId AS menuId, SUM(OrderItemLog.quantity) AS qty, 0 AS finalAmount, "
					+ "MenuItems.title AS title, MenuItems.vegType AS vegType, MenuItems.taxes AS taxes, "
					+ "MenuItems.discountType AS discountType, MenuItems.discountValue AS discountValue, "
					+ "MenuItems.collection AS collection, MenuItems.station AS station, "
					+ "OrderItemLog.rate AS rate, MenuItems.isTaxable AS isTaxable, MenuItems.charges AS charges, "
					+ "OrderItemLog.state AS state FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId='" + orderId
					+ "' AND OrderItemLog.menuId='" + menuId.getEntity() + "' AND MenuItems.menuId='"
					+ menuId.getEntity() + "' AND OrderItemLog.hotelId='" + hotelId + "' AND OrderItemLog.state = "+SUBORDER_STATE_COMPLIMENTARY+";";

			tempItem = db.getOneRecord(sql, OrderItem.class, hotelId);
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
			//System.out.println(jsonObj);
			outObj.put("status", "failed");
			String sql = "SELECT * FROM OnlineOrders WHERE orderId = '" + orderObj.getInt("order_id") +
					"' AND restaurantId = '" + orderObj.getInt("restaurant_id") + "';";
			
			if(db.hasRecords(sql, orderObj.getString("outlet_id"))) {
				outObj.put("message", "Order already exists.");
				outObj.put("code", "501");
				return outObj;
			}
			jsonObj = jsonObj.replaceAll("'", "");
			sql = "INSERT INTO OnlineOrders (hotelId, restaurantId, externalOrderId, portalId, data, status, dateTime) " +
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
	public ArrayList<OnlineOrder> getOnlineOrders(String hotelId){
		
		String sql = "SELECT name AS entityId FROM sqlite_master WHERE type='table' AND name='OnlineOrders';";
		
		EntityString entity = db.getOneRecord(sql, EntityString.class, hotelId);
		if(entity==null) {
			return null;
		}
		
		sql = "SELECT * FROM OnlineOrders WHERE hotelId = '" + hotelId + "' AND status = "+ONLINE_ORDER_NEW+";";
		
		return db.getRecords(sql, OnlineOrder.class, hotelId);
	}
	
	@Override
	public OnlineOrder getOnlineOrder(String hotelId, int externalRestauranId, int externalOrderId){
		
		String sql = "SELECT * FROM OnlineOrders WHERE restaurantId = " + externalRestauranId + " AND externalOrderId = "+externalOrderId+";";
		
		return db.getOneRecord(sql, OnlineOrder.class, hotelId);
	}
	
	@Override
	public ArrayList<OnlineOrder> getOrderDeliveryUpdate(String hotelId){
		
		String sql = "SELECT orderNumber, orderId, riderNumber, riderName, riderStatus FROM OnlineOrders WHERE hotelId = '" + hotelId + "' AND status = "+ONLINE_ORDER_ACCEPTED + ";";
		
		return db.getRecords(sql, OnlineOrder.class, hotelId);
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
		
		String sql = "UPDATE OnlineOrders SET status = "+status+" WHERE hotelId = '" 
				+ outletId + "' AND externalOrderId = "+externalOrderId+";";
		System.out.println(sql);
		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public boolean markOnlineOrderComplete(String outletId, int orderNumber){
		
		String sql = "UPDATE OnlineOrders SET status = "+AccessManager.ONLINE_ORDER_DELIVERED+" WHERE hotelId = '" 
		+ outletId + "' AND orderNumber = "+orderNumber+";";
		System.out.println(sql);
		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public boolean updateOnlineRiderData(String outletId, int externalOrderId, String riderName, String riderNumber, String riderStatus){
		
		String sql = "UPDATE OnlineOrders SET riderName = '"+riderName+"', riderNumber = '"+riderNumber+ "', riderStatus = '"+ riderStatus.toUpperCase() + 
				"' WHERE hotelId = '" + outletId + "' AND externalOrderId = "+externalOrderId+";";
		System.out.println(sql);
		return db.executeUpdate(sql, outletId, false);
	}
	
	@Override
	public JSONObject newOrder(String hotelId, String hotelType, String userId, String[] tableIds, int peopleCount, String customer,
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
				sql = "SELECT * FROM OrderTables WHERE tableId='" + tableIds[i] + "' AND hotelId='"
						+ escapeString(hotelId) + "';";
				TableUsage table = db.getOneRecord(sql, TableUsage.class, hotelId);
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
			orderId = getNextOrderId(hotelId, userId);
			sql = "INSERT INTO Orders (hotelId, orderId, orderDate, orderDateTime, customerName, "
					+ "customerNumber, customerAddress, isSmsSent, waiterId, numberOfGuests, "
					+ "state, inhouse, takeAwayType, tableId, serviceType, foodBill, barBill, section, discountCode, remarks, excludedCharges, excludedTaxes) values ('" 
					+ hotelId + "', '" + orderId + "', '" + serviceDate + "', '" + (System.currentTimeMillis() / 1000L) + "','" + customer + "', '" + mobileNumber + "', '" 
					+ address+"', 0,'" + userId + "', " + Integer.toString(peopleCount) + ", ";

			if (hotelType.equals("PREPAID")) {
				sql += Integer.toString(ORDER_STATE_BILLING) + "," + DINE_IN + "," + ONLINE_ORDERING_PORTAL_NONE + ",'" + tableId.toString() + "','"
						+ currentService.getServiceType() + "',0,0, '"+section+"','[]', '"+remarks+"', '[]', '[]');";
			} else {
				sql += Integer.toString(ORDER_STATE_SERVICE) + "," + DINE_IN + "," + ONLINE_ORDERING_PORTAL_NONE + ",'" + tableId.toString() + "','"
						+ currentService.getServiceType() + "',0,0,'"+section+"','[]', '"+remarks+"', '[]', '[]');";
			}
			for (int i = 0; i < tableIds.length; i++) {
				sql = sql + "INSERT INTO OrderTables (hotelId, tableId, orderId) values('" + hotelId + "','"
						+ tableIds[i] + "','" + orderId + "');";
			}
			if (!db.executeUpdate(sql, true)) {
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
	public JSONObject deleteOrdersMonthWise(String hotelId, String month, double foodSalePercent, double barSalePercent) {
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
			ArrayList<ServiceLog> serviceLog = db.getRecords(sql, ServiceLog.class, hotelId);
			ServiceLog service = null;

			for(int i=0; i<serviceLog.size(); i++) {
				service = serviceLog.get(i);
				ArrayList<Order> allOrders = this.getCashOrders(hotelId, service.getServiceDate(), foodSalePercent, barSalePercent);
				if(allOrders.isEmpty())
					continue;
				IPayment paymentDao = new PaymentManager(false);
				Report sale = paymentDao.getCashCardSales(hotelId, service.getServiceDate(), service.getServiceType()); 
				//Calculate the deletable food sale value.
				deletableFoodSale = deletedFoodSaleDec = (sale.getFoodBill().doubleValue()*foodSalePercent)/100;
				//Calculate the deletable bar sale value. 
				deletableBarSale = deletedBarSaleDec = (sale.getBarBill().doubleValue()*barSalePercent)/100;
				deletedCashAmount = 0.0;
				
				Order order = null;
				if(foodSalePercent > 0.0) {
					for(int j=0; j<allOrders.size(); j++) {
						order = allOrders.get(j);
						if(order.getFoodBill().doubleValue() > deletedFoodSaleDec)
							continue;
						this.deleteOrder(hotelId, order.getOrderId());
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
					allOrders = this.getCashOrders(hotelId, service.getServiceDate(), foodSalePercent, barSalePercent);
					for(int j=0; j<allOrders.size(); j++) {
						order = allOrders.get(j);
						if(order.getBarBill().doubleValue() > deletedBarSaleDec)
							continue;
						this.deleteOrder(hotelId, order.getOrderId());
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
				
				cashReported = db.getOneRecord(sql, EntityDouble.class, hotelId);
				if(cashReported!=null) {
					sql = "UPDATE TotalRevenue SET cash = " + (cashReported.getId()-deletedCashAmount)
						+ " WHERE serviceDate = '"+service.getServiceDate()+"' AND serviceType = '"+service.getServiceType()+"';";
					
					db.executeUpdate(sql, true);
				}
			}
			this.AlignBillNumbersMonthWise(hotelId, month);
			
			outObj.put("status", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public JSONObject deleteOrdersDayWise(String hotelId, double deductionAmount, double foodSalePercent, 
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
			Report sale = paymentDao.getCashCardSales(hotelId, serviceDate, serviceType); 
			//Calculate the deletable food sale value.
			deletableFoodSale = deletedFoodSaleDec = (sale.getFoodBill().doubleValue()*foodSalePercent)/100;
			//Calculate the deletable bar sale value. 
			deletableBarSale = deletedBarSaleDec = (sale.getBarBill().doubleValue()*barSalePercent)/100;
			deletedCashAmount = 0.0;
			
			Order order = null;
			if(foodSalePercent > 30.0 && barSalePercent > 30.0) {
				allOrders = this.getCashOrders(hotelId, serviceDate, foodSalePercent, barSalePercent);
				for(int j=0; j<allOrders.size(); j++) {
					if(deductionAmount < deletedCashAmount)
						break;
					order = allOrders.get(j);
					if(order.getFoodBill().doubleValue() > deletedFoodSaleDec)
						continue;
					if(order.getBarBill().doubleValue() > deletedBarSaleDec)
						continue;
					this.deleteOrder(hotelId, order.getOrderId());
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
				allOrders = this.getCashOrders(hotelId, serviceDate, foodSalePercent, barSalePercent);
				for(int j=0; j<allOrders.size(); j++) {
					if(deductionAmount < deletedCashAmount)
						break;
					order = allOrders.get(j);
					if(order.getFoodBill().doubleValue() > deletedFoodSaleDec)
						continue;
					this.deleteOrder(hotelId, order.getOrderId());
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
				allOrders = this.getCashOrders(hotelId, serviceDate, foodSalePercent, barSalePercent);
				for(int j=0; j<allOrders.size(); j++) {
					if(deductionAmount < deletedCashAmount)
						break;
					order = allOrders.get(j);
					if(order.getBarBill().doubleValue() > deletedBarSaleDec)
						continue;
					this.deleteOrder(hotelId, order.getOrderId());
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
			
			cashReported = db.getOneRecord(sql, EntityDouble.class, hotelId);
			if(cashReported!=null) {
				sql = "UPDATE TotalRevenue SET cash = " + (cashReported.getId()-deletedCashAmount)
					+ " WHERE serviceDate = '"+serviceDate+"' AND serviceType = '"+serviceDate+"';";
				
				db.executeUpdate(sql, true);
			}
			sql = "UPDATE ServiceLog SET deductionState = 1 WHERE serviceDate = '"+serviceDate+"' "
					+ "AND serviceType = '"+serviceDate+"';";
			db.executeUpdate(sql, true);
			outObj.put("status", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public JSONObject hideOrder(String hotelId, String date, String serviceType, BigDecimal cashAmount) {
		JSONObject outObj = new JSONObject();
		ArrayList<Order> allOrders = this.getCashOrders(hotelId, date);
		IPayment paymentDao = new PaymentManager(false);
		BigDecimal cashSale = paymentDao.getCashCardSales(hotelId, date, serviceType).getCashPayment();
		BigDecimal cashDeducted = new BigDecimal("0.0");
		IService serviceDao = new ServiceManager(false);
		ServiceLog service = serviceDao.getCurrentService(hotelId);
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
				this.changeOrderStateToHidden(hotelId, allOrders.get(i).getOrderId());
				cashDeducted.add(allOrders.get(i).getCashPayment());
				cashSale.subtract(allOrders.get(i).getCashPayment());
			}
			
			String sql = "SELECT cash FROM TotalRevenue WHERE serviceDate = '"+date+"' AND serviceType = '"+serviceType+"';";
			
			EntityBigDecimal cashReported = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
			if(cashReported!=null) {
				sql = "UPDATE TotalRevenue SET cash2 = " + cashReported.getId() + ", "
					+ "cash = " + (cashReported.getId().subtract(cashDeducted))
					+ ", deductedCash = " + cashAmount 
					+ " WHERE serviceDate = '"+date+"' AND serviceType = '"+serviceType+"';";
				
				db.executeUpdate(sql, true);
			}
			
			sql = "SELECT Orders.billNo AS entityId FROM Orders WHERE orderDate = '"+date+"' AND billNo != ''  order by billNo asc LIMIT 1";
			EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
			int billNo = entity.getId();
			this.updateBillNo2(hotelId, date);
			
			allOrders = this.getAllVisibleOrders(hotelId, date);
			for(int i=0; i<allOrders.size(); i++) {
				this.updateBillNo(hotelId, allOrders.get(i).getOrderId(), Integer.toString(billNo));
				billNo++;
			}
			
			allOrders = this.getInVisibleOrders(hotelId, date);
			for(int i=0; i<allOrders.size(); i++) {
				sql = "UPDATE OrderItems SET billNo2 = billNo, billNo = '' WHERE orderId = '"+allOrders.get(i).getOrderId()+"' AND hotelId = '"+hotelId+"'; ";
				
				db.executeUpdate(sql, true);
			}
			
			outObj.put("status", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}
	
	@Override
	public void AlignBillNumbersContinuous(String hotelId, int billNo, String startDate) {
		
		ArrayList<Order> allOrders = this.getAllVisibleOrders(hotelId, startDate);
		for(int i=0; i<allOrders.size(); i++) {
			this.updateBillNo(hotelId, allOrders.get(i).getOrderId(), Integer.toString(billNo));
			billNo++;
		}
	}
	
	@Override
	public void AlignBillNumbersMonthWise(String hotelId, String month) {
		
		ArrayList<Order> allOrders = this.getAllOrdersForMonth(hotelId, month);
		this.updateBillNoMonthWiseForDelete(hotelId, allOrders);
	}
	
	@Override
	public void AlignAllBillNumbers(String hotelId, String startMonth) {
		
		ArrayList<Order> allOrders = this.getAllOrdersForPeriod(hotelId, startMonth);
		this.updateBillNoPeriodForDelete(hotelId, allOrders, startMonth);
	}
	
	@Override
	public void AlignBillNumbersDayWise(String hotelId, String serviceDate) {
		
		ArrayList<Order> allOrders = this.getAllOrdersForMonth(hotelId, serviceDate);
		this.updateBillNoMonthWiseForDelete(hotelId, allOrders);
	}
	
	private boolean updateBillNo2(String hotelId, String serviceDate) {
		
		String sql = "UPDATE Orders SET billNo = '' WHERE orderDate = '"+serviceDate+"' AND hotelId = '"+hotelId+"'; "
				+ "UPDATE Payment SET billNo = '' WHERE orderDate = '"+serviceDate+"' AND hotelId = '"+hotelId+"'; ";
		
		return db.executeUpdate(sql, true);
	}
	
	private boolean updateBillNo(String hotelId, String orderId, String billNo) {
		
		String sql = "UPDATE Orders SET billNo = '"+billNo+"' WHERE orderId = '"+orderId+"' AND hotelId = '"+hotelId+"'; "
				+ "UPDATE OrderItems SET billNo2 = billNo, billNo = '"+billNo+"' WHERE orderId = '"+orderId+"' AND hotelId = '"+hotelId+"'; "
				+ "UPDATE Payment SET billNo = '"+billNo+"' WHERE orderId = '"+orderId+"' AND hotelId = '"+hotelId+"'; ";
		
		return db.executeUpdate(sql, true);
	}
	
	private boolean updateBillNoMonthWiseForDelete(String hotelId, ArrayList<Order> orders) {
		
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
			sql += "UPDATE Orders SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND hotelId = '"+hotelId+"'; "
					+ "UPDATE OrderItems SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND hotelId = '"+hotelId+"'; "
					+ "UPDATE Payment SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND hotelId = '"+hotelId+"'; ";
		}
		
		return db.executeUpdate(sql, true);
	}
	
	private boolean updateBillNoPeriodForDelete(String hotelId, ArrayList<Order> orders, String serviceDate) {
		
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
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
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
			sql += "UPDATE Orders SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND hotelId = '"+hotelId+"'; "
					+ "UPDATE OrderItems SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND hotelId = '"+hotelId+"'; "
					+ "UPDATE Payment SET billNo = '"+billNoStr+"' WHERE orderId = '"+order.getOrderId()+"' AND hotelId = '"+hotelId+"'; ";
		}
		return db.executeUpdate(sql, true);
	}

	@Override
	public JSONObject newNCOrder(String hotelId, String userId, String reference, String section, String remarks) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			IService serviceDao = new ServiceManager(false);
			ServiceLog service = serviceDao.getCurrentService(hotelId);
			if (service.getServiceDate() == null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started");
				return outObj;
			}
			
			orderId = getNextOrderId(hotelId, userId);
			sql = "INSERT INTO Orders (hotelId, orderId, orderDate, orderDateTime, waiterId, numberOfGuests, "
					+ "state, inhouse, serviceType, foodBill, barBill, section, reference, remarks, discountCode, excludedCharges, excludedTaxes) values ('" 
					+ hotelId + "', '" + orderId + "', '" + service.getServiceDate() + "', '" + (System.currentTimeMillis() / 1000L) 
					+ "', '" + userId + "', 1, " + Integer.toString(ORDER_STATE_SERVICE) + "," + NON_CHARGEABLE + ",'" 
					+ service.getServiceType() + "',0,0,'"+section+"', '"+reference+"', '"+remarks+"', '[]', '[]', '[]');";
			
			if (!db.executeUpdate(sql, true)) {
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
	public JSONObject newQsrOrder(String hotelId, String userId, String customer, String mobileNumber,
			String allergyInfo, int orderType, String emailId, String referenceForReview) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			IService serviceDao = new ServiceManager(false);
			ServiceLog service = serviceDao.getCurrentService(hotelId);
			if (service.getServiceDate() == null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started");
				return outObj;
			}
			orderId = getNextOrderId(hotelId, userId);
			sql = "INSERT INTO Orders (hotelId, orderId, orderDate, orderDateTime, customerName, "
					+ "customerNumber, customerAddress, waiterId, numberOfGuests, state, inhouse, serviceType, foodBill, barBill, discountCode, excludedCharges, excludedTaxes) values ('"
					+ hotelId + "', '" + orderId + "', '" + service.getServiceDate() + "', '" + (System.currentTimeMillis() / 1000L) 
					+ "','" + customer + "', '" + mobileNumber + "', '', '" + userId + "', " + 1 + ", " + Integer.toString(ORDER_STATE_COMPLETE) 
					+ "," + orderType + ",'" + service.getServiceType() + "',0,0, '[]', '[]', '[]');";

			String[] name = customer.split(" ");
			String surName = "";
			if(name.length>1) {
				surName = name[1];
			}
			ICustomer customerDao = new CustomerManager(false);
			if (!customerDao.hasCustomer(hotelId, mobileNumber)) {
				customerDao.addCustomer(hotelId, name[0], surName, mobileNumber, "", "", "", allergyInfo, Boolean.FALSE, Boolean.FALSE, emailId, referenceForReview);
			} else {
				customerDao.updateCustomer(hotelId, null, name[0], surName, mobileNumber, "", "", "", allergyInfo, "", Boolean.FALSE, "", "");
			}
			db.executeUpdate(sql, true);
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
			outObj.put("orderNumber", this.getOrderNumber(hotelId, orderId));
			return outObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject placeOrder(String outletId, String userId, String customer, JSONObject customerDetails, String phone, String address,
			int orderType, int takeAwayType, String allergyInfo, String reference, String remarks, 
			String externalOrderId, JSONArray discountCodes, String emailId, String referenceForReview, String section,
			Double cashToBeCollected, Double goldDiscount, Double zomatoVoucherAmount, Double piggyBank, BigDecimal amountReceivable) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			IOutlet outletDao = new OutletManager(false);
			Settings settings  = outletDao.getSettings(outletId);
			IService serviceDao = new ServiceManager(false);
			ServiceLog service = serviceDao.getCurrentService(outletId);
			if (service == null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started for the day");
				return outObj;
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
				if (!customerDao.hasCustomer(outletId, phone)) {
					customerDao.addCustomer(outletId, name[0], surName, phone, address, "", "", allergyInfo, 
							Boolean.FALSE, Boolean.FALSE, emailId, referenceForReview);
				} else {
					customerDao.updateCustomer(outletId, null, name[0], surName, phone, "", "", "", allergyInfo, address, Boolean.FALSE, emailId, "");
				}
			}else if (!phone.equals("")) {
				String[] name = customer.split(" ");
				String surName = "";
				if(name.length>1) {
					surName = name[1];
				}
				ICustomer customerDao = new CustomerManager(false);
				if (!customerDao.hasCustomer(outletId, phone)) {
					customerDao.addCustomer(outletId, name[0], surName, phone, address, "", "", allergyInfo, Boolean.FALSE, Boolean.FALSE, emailId, referenceForReview);
				} else {
					customerDao.updateCustomer(outletId, null, name[0], surName, phone, "", "", "", allergyInfo, address, Boolean.FALSE, "", "");
				}
			}

			String orderState = Integer.toString(ORDER_STATE_SERVICE);
			if (settings.getHotelType().equals("PREPAID"))
				orderState = Integer.toString(ORDER_STATE_BILLING);

			orderId = getNextOrderId(outletId, userId);
			sql = "INSERT INTO Orders (hotelId, orderId, orderDate, orderDateTime, customerName, "
					+ "customerNumber, customerAddress, isSmsSent,  waiterId, numberOfGuests, state, inhouse, "
					+ "takeAwayType, serviceType, reference, remarks, discountCode, externalOrderId, excludedCharges, excludedTaxes, section,"
					+ "cashToBeCollected, goldDiscount, zomatoVoucherAmount, piggyBank, amountReceivable, isFoodReady)"
					+ " values ('" + outletId + "', '" + orderId + "', '" + service.getServiceDate() + "', '" + (System.currentTimeMillis() / 1000L)
					+ "','" + escapeString(customer) + "', '" + escapeString(phone) + "', '" + escapeString(address) + "',0, '" 
					+ userId + "', 1, " + orderState + "," + orderType + "," + takeAwayType + ",'" + service.getServiceType() + "', '"+reference
					+ "', '"+remarks+"', '"+discountCodes.toString()+"', '"+externalOrderId+"', '[]', '[]', '"+section
					+"', "+ cashToBeCollected + "," + goldDiscount + "," +zomatoVoucherAmount+ "," +piggyBank+ ", "+amountReceivable+", 'false');";
			if (!db.executeUpdate(sql, true)) {
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
	public JSONObject newHomeDeliveryOrder(String hotelId, String userId, String customer, String phone, String address,
			String allergyInfo, String remarks, String section) {
		return this.placeOrder(hotelId, userId, customer, null, phone, address, HOME_DELIVERY, ONLINE_ORDERING_PORTAL_NONE, allergyInfo, "", remarks, "", new JSONArray()
				, "", "", section, 0.0, 0.0, 0.0, 0.0, new BigDecimal("0.0"));
	}

	@Override
	public JSONObject newTakeAwayOrder(String hotelId, String userId, String customer, JSONObject customerDetails, String phone,
			String externalId, String allergyInfo, String remarks, String externalOrderId, JSONArray discountCodes,
			String section, Double cashToBeCollected, Double goldDiscount, Double zomatoVoucherAmount, Double piggyBank, BigDecimal amountReceivable) {
		int takeAwaytype = COUNTER_PARCEL_ORDER;
		IOnlineOrderingPortal protalDao = new OnlineOrderingPortalManager(false);
		ArrayList<OnlineOrderingPortal> portals = protalDao.getOnlineOrderingPortals(hotelId);
		if(!externalId.equals("")) {
			for (OnlineOrderingPortal portal : portals) {
				if(portal.getPortal().equals(customer)) {
					takeAwaytype = portal.getId();
				}
			}
			customer = "";
		}
		return this.placeOrder(hotelId, userId, customer, customerDetails, phone, "", TAKE_AWAY, takeAwaytype, allergyInfo, 
				externalId, remarks, externalOrderId, discountCodes, "", "", section, 
				cashToBeCollected, zomatoVoucherAmount, goldDiscount, piggyBank, amountReceivable);
	}

	@Override
	public JSONObject newBarOrder(String hotelId, String userId, String reference, String remarks, String section) {
		return this.placeOrder(hotelId, userId, "", null, "", "", BAR, ONLINE_ORDERING_PORTAL_NONE, "", reference, 
				remarks, "", new JSONArray(), "", "", section, 0.0, 0.0, 0.0, 0.0, new BigDecimal("0.0"));
	}

	@Override
	public Boolean unCheckOutOrder(String outletId, String orderId) {
		IOutlet outletDao = new OutletManager(false);
		Settings settings  = outletDao.getSettings(outletId);
		String sql = "";
		
		if (settings.getHotelType().equals("PREPAID"))
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_OFFKDS) + " WHERE orderId='" + orderId
					+ "' AND hotelId='" + outletId + "';";
		else
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_SERVICE) + " WHERE orderId='" + orderId
					+ "' AND hotelId='" + outletId + "';";

		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean checkOutOrder(String outletId, String orderId) {
		IOutlet outletDao = new OutletManager(false);
		Settings settings  = outletDao.getSettings(outletId);
		String sql = "";
		
		if (settings.getHotelType().equals("PREPAID"))
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_COMPLETE) + " WHERE orderId='" + orderId
					+ "' AND hotelId='" + outletId + "'; UPDATE OrderItems SET state="
					+ Integer.toString(SUBORDER_STATE_COMPLETE) + " WHERE orderId='" + orderId + "' AND hotelId='"
					+ outletId + "'";
		else
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_BILLING) + " WHERE orderId='" + orderId
					+ "' AND hotelId='" + outletId + "';";

		String billNo = "";
		Order order = this.getOrderById(outletId, orderId);
		if(order.getBillNo().equals("")) {
			if (settings.getBillType() == BILLTYPE_NUMBER) {
				billNo = this.getNextBillNoNumberFormat(outletId);
			}else if(settings.getBillType() == BILLTYPE_NUMBER_REFRESH) {
				billNo = this.getNextBillNoNumberFormatDaywise(outletId);
			}else if(settings.getBillType() == BILLTYPE_MONTHLY_REFRESH) {
				billNo = this.getNextBillNoNumberFormatMonthwise(outletId);
			}
			sql += "UPDATE Orders SET billNo = '"+billNo+"', billNo2 = '"+billNo+"' WHERE orderId = '"+orderId+"' AND hotelId = '"+outletId+"';"
				+ "UPDATE OrderItems SET billNo = '"+billNo+"', billNo2 = '"+billNo+"' WHERE orderId = '"+orderId+"' AND hotelId = '"+outletId+"';";
		}
		return db.executeUpdate(sql, true);
	}
	
	@Override
	public Boolean updateDeliveryTime(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET deliveryTimeStamp = '"+parseTime("HH:mm")+ "' WHERE "
				+ "hotelId = '" + hotelId + "' AND orderId = '"+ orderId + "';";
		
		return db.executeUpdate(sql, true);
	}
	
	@Override
	public Boolean updateCompleteTime(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET completeTimeStamp = '"+parseTime("HH:mm")+ "' WHERE "
				+ "hotelId = '" + hotelId + "' AND orderId = '"+ orderId + "';";
		
		return db.executeUpdate(sql, true);
	}
	
	@Override
	public Boolean updateDeliveryBoy(String hotelId, String orderId, String employeeName) {
		String sql = "UPDATE Orders SET deliveryBoy = '"+employeeName+ "' WHERE "
				+ "hotelId = '" + hotelId + "' AND orderId = '"+ orderId + "';";
		
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean markFoodReady(String outletId, String orderId) {
		String sql = "UPDATE Orders SET isFoodReady='true' WHERE orderId='" + orderId
				+ "' AND hotelId='" + outletId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean changeOrderStatus(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_OFFKDS) + " WHERE orderId='" + orderId
				+ "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean changeOrderStateToHidden(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_HIDDEN) + " WHERE orderId='" + orderId
				+ "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean changeOrderStateToCancelled(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET state = "+ORDER_STATE_CANCELED+" WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
				+ "DELETE FROM OrderTables WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
				+ "DELETE FROM OrderSpecifications WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; ";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean markPaymentComplete(String hotelId, String orderId) {
		String sql = "DELETE FROM OrderTables WHERE orderId='" + orderId + "' AND hotelId='" + hotelId + "';"
				+ "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_COMPLETE) + " WHERE orderId='" + orderId
				+ "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean changeOrderStatusToService(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_SERVICE) + " WHERE orderId='" + orderId
				+ "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean editSubOrder(String hotelId, String orderId, String subOrderId, String menuId, int qty) {
		String sql = null;
		if (qty == 0) {
			sql = "DELETE FROM OrderItems WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
					+ "' AND menuId='" + menuId + "' AND hotelId='" + hotelId + "' AND state="
					+ Integer.toString(SUBORDER_STATE_PENDING) + ";";
		} else {
			sql = "UPDATE OrderItems SET qty=" + Integer.toString(qty) + " WHERE orderId='" + orderId
					+ "' AND subOrderId=='" + subOrderId + "' AND menuId='" + menuId + "' AND hotelId='" + hotelId
					+ "' AND state=" + Integer.toString(SUBORDER_STATE_PENDING) + ";";
		}
		int itemId = qty + 1;
		IOrderAddOn addOnDao = new OrderAddOnManager(false);
		addOnDao.removeAddOns(hotelId, orderId, subOrderId, menuId, itemId);
		this.removeOrderedSpecification(hotelId, orderId, subOrderId, menuId, itemId);
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean removeOrderedSpecification(String hotelId, String orderId, String subOrderId, String menuId,
			int itemId) {
		String sql = "DELETE FROM OrderSpecifications WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
				+ "' AND menuId='" + menuId + "' AND hotelId='" + hotelId + "' AND itemId=" + itemId + ";";

		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean removeSubOrder(String hotelId, String orderId, String subOrderId, String menuId, int qty) {
		String sql = null;
		if (qty == 0) {
			sql = "DELETE FROM OrderItems WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
					+ "' AND menuId='" + menuId + "' AND hotelId='" + hotelId + "';";
		} else {
			sql = "UPDATE OrderItems SET qty=" + qty + " WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
					+ "' AND menuId='" + menuId + "' AND hotelId='" + hotelId + "';";
		}
		this.removeOrderedSpecification(hotelId, orderId, subOrderId, menuId, qty + 1);
		return db.executeUpdate(sql, true);
	}

	@Override
	public JSONObject voidOrder(String hotelId, String orderId, String reason, String authId, String section) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);

			IMenuItem menuDao = new MenuItemManager(false);
			Order order = getOrderById(hotelId, orderId);

			String sql = "UPDATE Orders SET state=" + ORDER_STATE_VOIDED + ", reason = '"
					+ reason + "', authId = '" + authId + "' WHERE orderId='" + orderId + "' AND hotelId='" + hotelId
					+ "';";

			if (!db.executeUpdate(sql, true)) {
				outObj.put("message", "Failed to void the order. Please try again.");
				return outObj;
			}

			if (order.getState() == ORDER_STATE_BILLING || order.getState() == ORDER_STATE_OFFKDS
					|| order.getState() == ORDER_STATE_SERVICE) {

				sql = "DELETE FROM OrderTables WHERE orderId='" + orderId + "' AND hotelId='" + hotelId + "';";
				if (!db.executeUpdate(sql, true)) {
					outObj.put("message", "Failed to delete Order table. Please try again");
					return outObj;
				}
			}

			ArrayList<OrderItem> orderitems = this.getOrderedItemForVoid(hotelId, orderId);

			MenuItem menu = null;
			Double totalFoodBill = 0.0;
			Double totalBarBill = 0.0;
			for (OrderItem orderItem : orderitems) {
				menu = menuDao.getMenuById(hotelId, orderItem.getMenuId());
				if(menu.getStation().equals("Bar")) {
					totalBarBill += orderItem.getRate().doubleValue()*orderItem.getQty();
				}else {
					totalFoodBill += orderItem.getRate().doubleValue()*orderItem.getQty();
				}
				if (!this.updateOrderItemLog(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), "Void",
						"void", orderItem.getQty(), orderItem.getRate(), 0)) {
					outObj.put("message", "Failed to update OrderItem Log. Please try again.");
					return outObj;
				}
				if (!this.removeSubOrder(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0)) {
					outObj.put("message", "Failed to remove Ordered Item. Please try again.");
					return outObj;
				}
				ArrayList<OrderAddOn> addOns = this.getAllOrderedAddOns(hotelId, orderId);
				for (OrderAddOn orderAddOn : addOns) {
					menu = menuDao.getMenuById(hotelId, orderItem.getMenuId());
					if(menu.getStation().equals("Bar")) {
						totalBarBill += orderItem.getRate().doubleValue()*orderItem.getQty();
					}else {
						totalFoodBill += orderItem.getRate().doubleValue()*orderItem.getQty();
					}
					if (!this.updateOrderAddOnLog(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getSubOrderDate(), orderItem.getMenuId(),
							orderAddOn.getItemId(), "void", orderAddOn.getQty(), orderAddOn.getRate(),
							orderAddOn.getAddOnId())) {
						outObj.put("message", "Failed to update Addon Log. Please try again.");
						return outObj;
					}
					if (!this.removeAddOns(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0)) {
						outObj.put("message", "Failed to remove Ordered Addon. Please try again.");
						return outObj;
					}
				}
			}
			
			sql = "UPDATE Orders SET foodBill = "+ totalFoodBill+ ", barBill = "+ totalBarBill + " WHERE hotelId = '" + hotelId
					+ "' AND orderId = '"+ orderId + "';";
			
			sql = "UPDATE OrderItemLog SET state=" + ORDER_STATE_VOIDED + ", reason = 'Void' WHERE orderId='" + orderId + "' AND hotelId='" + hotelId
					+ "';";

			if (!db.executeUpdate(sql, true)) {
				outObj.put("message", "Failed to void the order. Please try again.");
				return outObj;
			}
			//IInventory inventoryDao = new InventoryManager(false);
			//inventoryDao.revertInventoryForVoid(hotelId, orderId);
			
			outObj.put("status", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			db.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public Boolean complimentaryOrder(String hotelId, String orderId, String authId) {

		String sql = "UPDATE Orders SET state=" + ORDER_STATE_COMPLIMENTARY + ", foodBill = 0, barBill = 0, "
				+ "authId = '" + authId + "' WHERE orderId='" + orderId + "' AND hotelId='" + hotelId + "';";

		ArrayList<OrderItem> orderitems = this.getOrderedItems(hotelId, orderId, true);
		IOrderAddOn addOnDao = new OrderAddOnManager(false);

		for (OrderItem orderItem : orderitems) {
			this.updateOrderItemLog(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), "Complimentary",
					"comp", orderItem.getQty(), new BigDecimal("0.0"), 0);
			this.removeSubOrder(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0);
			ArrayList<OrderAddOn> addOns = addOnDao.getAllOrderedAddOns(hotelId, orderId);
			for (OrderAddOn orderAddOn : addOns) {
				addOnDao.updateOrderAddOnLog(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getSubOrderDate(), orderItem.getMenuId(),
						orderAddOn.getItemId(), "comp", orderAddOn.getQty(), orderAddOn.getRate(),
						orderAddOn.getAddOnId());
				addOnDao.removeAddOns(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0);
			}
		}
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean complimentaryItem(String hotelId, String orderId, String menuId, String authId, String subOrderId,
			BigDecimal rate, int qty, String reason) {

		if (this.updateOrderItemLog(hotelId, orderId, subOrderId, menuId, reason, "comp", 1, rate, 0)) {
			this.removeSubOrder(hotelId, orderId, subOrderId, menuId, qty - 1);
			return true;
		}
		return false;
	}

	@Override
	public JSONObject newSubOrder(String outletId, Settings settings, Order order, MenuItem menu, Integer qty, String specs,
			String subOrderId, String waiterId, BigDecimal rate, String tableId) {
		JSONObject outObj = new JSONObject();
		String sql = null;
		ITable tableDao = new TableManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		ArrayList<OnlineOrderingPortal> portals = portalDao.getOnlineOrderingPortals(outletId);
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
				}else if(order.getInHouse() == DINE_IN) {
					String tableType = tableDao.getTableType(outletId, tableId);
					rate = tableType.equals(TABLE_TYPE_AC)?menu.getDineInRate():menu.getDineInNonAcRate();
				}else if(order.getInHouse() == NON_CHARGEABLE || order.getInHouse() == BAR) {
					rate = menu.getDineInRate();
				}
				else if(order.getInHouse() == HOME_DELIVERY || (order.getInHouse() == TAKE_AWAY && order.getTakeAwayType() == COUNTER_PARCEL_ORDER))
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
			if(order.getInHouse() == AccessManager.DINE_IN) {
				if(!settings.getIsCaptainBasedOrdering()) {
					Table table = tableDao.getTableById(outletId, tableId);
					if(!table.getWaiterId().equals(""))
						waiterId = table.getWaiterId();
				}
			}
			int kotNumber = 0;
			int botNumber = 0;
			if(menu.getStation().equals("Bar"))
				botNumber = this.getNextBOTNumber(outletId);
			else
				kotNumber = this.getNextKOTNumber(outletId);
			
			sql = "INSERT INTO OrderItems (hotelId, subOrderId, subOrderDate, orderId, menuId, qty, rate, specs, state, isKotPrinted, waiterId, kotNumber, botNumber) values ('"
					+ outletId + "', '" + subOrderId + "', '"
					+ (new SimpleDateFormat("yyyy/MM/dd HH:mm")).format(new Date()) + "','" + order.getOrderId() + "', '" + menu.getMenuId()
					+ "', " + Integer.toString(qty) + ", " + (new DecimalFormat("0.00")).format(rate) + ", '" + specs
					+ "', " + orderState + ", " + kotPrinting + ", '" + waiterId + "', '"+kotNumber+"', '"+botNumber+"');";
			if (!db.executeUpdate(sql, true)) {
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
	public Integer getOrderCount(String hotelId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT count(orderId) AS entityId FROM Orders WHERE waiterId=='" + userId + "' AND orderDate=='"
				+ (new SimpleDateFormat("yyyy/MM/dd")).format(dt) + "' AND hotelId='" + hotelId + "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	@Override
	public Boolean updateSpecifications(String hotelId, String orderId, String subOrderId, String menuId,
			String specs) {

		String sql = "UPDATE OrderItems SET specs='" + specs + "' WHERE subOrderId=='" + subOrderId + "' AND menuId='"
				+ menuId + "' AND orderId='" + orderId + "' AND hotelId='" + hotelId + "';";

		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean changeOrderStatus(String outletId, String orderId, String subOrderId, String menuId) {
		String sql = null;
		OrderItem item = getOrderStatus(outletId, orderId, subOrderId, menuId);
		int curState = item.getState();
		
		IOutlet outletDao = new OutletManager(false);
		Settings settings = outletDao.getSettings(outletId);
		
		ITable tableDao = new TableManager(false);
		ArrayList<Table> tables = tableDao.getJoinedTables(outletId, orderId);
		String tableId = "";
		if (tables.size() > 0) {
			tableId = tables.get(0).getTableId();
		}
		String userId = orderId.split(":")[0];

		if (curState == SUBORDER_STATE_PENDING) {
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_PROCESSING) + " WHERE subOrderId=='"
					+ subOrderId + "' AND menuId='" + menuId + "' AND orderId='" + orderId + "' AND hotelId='" + outletId
					+ "';";
		} else if (curState == SUBORDER_STATE_PROCESSING) {
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_RECIEVED) + " WHERE subOrderId=='"
					+ subOrderId + "' AND menuId='" + menuId + "' AND orderId='" + orderId + "' AND hotelId='" + outletId
					+ "';";
			INotification notificationDao = new NotificationManager(false);
			Boolean retval = db.executeUpdate(sql, true);
			if (!retval)
				return retval;
			if (allItemsProcessedOrReceived(outletId, orderId)) {
				int notId = notificationDao.getNextNotificationId(userId, outletId);
				String target = tableId.equals("") ? "Home Delivery" : "Table " + tableId;
				if (settings.getHotelType().equals("KDS")) {
					String msg = "Order of " + target + " is ready.";
					sql += "INSERT INTO Notification (notId, hotelId, orderId, msg) VALUES (" + Integer.toString(notId)
							+ ", '" + outletId + "','" + orderId + "', '" + msg + "');";
				}
				db.executeUpdate(sql, true);
			} else if (!tableId.equals("")) {
				int notId = notificationDao.getNextNotificationId(userId, outletId);
				if (settings.getHotelType().equals("KDS")) {
					String msg = item.getTitle() + " of Table " + tableId + " is ready.";
					sql += "INSERT INTO Notification (notId, hotelId, orderId, msg) VALUES (" + Integer.toString(notId)
							+ ", '" + outletId + "','" + orderId + "', '" + msg + "');";
				}
				db.executeUpdate(sql, true);
			}
			IInventory inventoryDao = new InventoryManager(false); 
			inventoryDao.manageInventory(outletId, menuId, orderId, item.getQty());

			return retval;
		} else if (curState == SUBORDER_STATE_RECIEVED) {
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_COMPLETE) + " WHERE subOrderId=='"
					+ subOrderId + "' AND menuId='" + menuId + "' AND orderId='" + orderId + "' AND hotelId='" + outletId
					+ "';";
		} else {
			return false;
		}
		return db.executeUpdate(sql, true);
	}

	private Boolean allItemsProcessedOrReceived(String hotelId, String orderId) {
		String sql = "SELECT * FROM OrderItems WHERE hotelId=='" + hotelId + "' AND orderId=='" + orderId
				+ "' AND state<>" + Integer.toString(SUBORDER_STATE_RECIEVED) + " AND state <> "
				+ Integer.toString(SUBORDER_STATE_COMPLETE);
		return !db.hasRecords(sql, hotelId);
	}

	@Override
	public OrderItem getOrderStatus(String hotelId, String orderId, String subOrderId, String menuId) {
		String sql = "SELECT MenuItems.title as title,OrderItems.state FROM OrderItems,MenuItems WHERE MenuItems.menuId==OrderItems.menuId AND OrderItems.menuId='"
				+ menuId + "' AND OrderItems.subOrderId='" + subOrderId + "' AND OrderItems.orderId='" + orderId
				+ "' AND OrderItems.hotelId='" + hotelId + "';";
		return db.getOneRecord(sql, OrderItem.class, hotelId);
	}

	@Override
	public ArrayList<OrderItem> getReturnedItems(String hotelId, String orderId) {

		String sql = "SELECT OrderItemLog.subOrderId AS subOrderId, OrderItemLog.subOrderDate AS subOrderDate, "
				+ "OrderItemLog.menuId AS menuId, OrderItemLog.quantity AS qty, "
				+ "OrderItemLog.itemId AS itemId, MenuItems.title AS title, OrderItemLog.rate AS rate, "
				+ "OrderItemLog.reason AS reason, "
				+ "OrderItemLog.state AS state FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId='" + orderId
				+ "' AND OrderItemLog.menuId==MenuItems.menuId AND OrderItemLog.hotelId='" + hotelId + "';";
		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	@Override
	public Order getOrderById(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND hotelId='" + hotelId + "';";

		return db.getOneRecord(sql, Order.class, hotelId);
	}

	@Override
	public Boolean hasCheckedOutOrders(String hotelId, String serviceDate) {
		String sql = "SELECT * FROM Orders WHERE (state == 0 OR state == 1 OR state == 2) AND hotelId = '" + hotelId
				+ "' AND orderDate == '" + serviceDate + "';";
		return db.hasRecords(sql, hotelId);
	}

	private String getNextOrderId(String hotelId, String userId) {
		String sql = "SELECT MAX(CAST(SUBSTR(orderId," + Integer.toString(userId.length() + 2)
				+ ") AS integer)) AS entityId FROM Orders WHERE orderId LIKE '" + userId + ":%'  AND hotelId='"
				+ hotelId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return userId + ":" + (entity.getId() + 1);
		}
		return userId + ":0";
	}

	private int getOrderNumber(String hotelId, String orderId) {
		String sql = "SELECT Id AS entityId FROM Orders WHERE orderId = '" + orderId + "' AND hotelId='" + hotelId
				+ "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		return entity.getId();
	}

	@Override
	public int getNextKOTNumber(String hotelId) {

		int kotNumber = 1;
		IService serviceDao = new ServiceManager(false);
		String serviceDate = serviceDao.getServiceDate(hotelId);
		String sql = "SELECT MAX(OrderItems.kotNumber) AS entityId FROM OrderItems, Orders WHERE OrderItems.hotelId='"
				+ hotelId + "' AND Orders.orderId == OrderItems.orderId AND Orders.orderDate=='" + serviceDate + "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		
		kotNumber = entity.getId()+1;
		return kotNumber;
	}

	@Override
	public int getNextBOTNumber(String hotelId) {

		int botNumber = 1;
		IService serviceDao = new ServiceManager(false);
		String serviceDate = serviceDao.getServiceDate(hotelId);
		String sql = "SELECT MAX(OrderItems.botNumber) AS entityId FROM OrderItems, Orders WHERE OrderItems.hotelId='"
				+ hotelId + "' AND Orders.orderId == OrderItems.orderId AND Orders.orderDate=='" + serviceDate + "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		
		botNumber = entity.getId()+1;
		return botNumber;
	}

	@Override
	public String getNextBillNo(String hotelId, String station) {

		StringBuilder billNo = new StringBuilder();
		if (station.equals("BAR") || station.equals("Bar"))
			billNo.append("B");
		else
			billNo.append("F");

		String sql = "SELECT MAX(CAST(REPLACE(billNo, '" + billNo.toString() + "', '') AS integer)) AS entityId "
				+ "FROM Orders WHERE hotelId='" + hotelId + "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		int billNum = entity.getId();

		billNo.append(billNum + 1);

		return billNo.toString();
	}

	@Override
	public String getNextBillNoNumberFormat(String hotelId) {

		StringBuilder billNo = new StringBuilder();
		int billNum = 0;

		LocalDateTime date = LocalDateTime.now();

		String sql = "SELECT MAX(CAST(billNo AS integer)) AS entityId FROM Orders WHERE hotelId='" + hotelId + "' AND orderDate > '"+date.getYear()+"/03/31';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		if(entity==null) {
			billNum = 1;
		}else
			billNum = entity.getId();

		billNo.append(billNum + 1);

		return billNo.toString();
	}

	@Override
	public String getNextBillNoNumberFormatDaywise(String hotelId) {

		StringBuilder billNo = new StringBuilder();
		IService serviceDao = new ServiceManager(false);
		String serviceDate = serviceDao.getServiceDate(hotelId);
		String sql ="SELECT MAX(CAST(billNo AS integer)) AS entityId FROM Orders WHERE hotelId='" + hotelId 
				+ "' AND Orders.orderDate=='" + serviceDate + "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		int billNum = entity==null?1:entity.getId();

		billNo.append(billNum + 1);

		return billNo.toString();
	}

	@Override
	public String getNextBillNoNumberFormatMonthwise(String hotelId) {

		StringBuilder billNo = new StringBuilder();
		IService serviceDao = new ServiceManager(false);
		String serviceDate = serviceDao.getServiceDate(hotelId);
		String sql = "SELECT MAX(CAST(billNo AS integer)) AS entityId FROM Orders WHERE hotelId='" + hotelId 
				+ "' AND Orders.orderDate LIKE '" + serviceDate.substring(0,8) + "%';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		int billNum = entity==null?1:entity.getId();
		
		billNo.append(billNum + 1);

		return billNo.toString();
	}

	@Override
	public String getNextSubOrderId(String hotelId, String orderId) {
		String sql = "SELECT MAX(CAST(subOrderId AS integer)) AS entityId FROM OrderItems WHERE orderId == '" + orderId
				+ "' AND hotelId='" + hotelId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return Integer.toString(entity.getId() + 1);
		}
		return "0";
	}

	@Override
	public int getTotalBillAmount(String hotelId, String orderId) {
		String sql = "SELECT SUM(rate*qty) AS entityId FROM OrderItems WHERE hotelId='" + hotelId + "' AND orderId='"
				+ orderId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	@Override
	public ArrayList<HomeDelivery> getActiveHomeDeliveries(Settings settings, String userId) {
		
		String sql = "SELECT Orders.state, Orders.billNo, Orders.remarks, Orders.customerAddress as address, "
				+ "Orders.customerName as customer, Orders.customerNumber as mobileNumber, Orders.orderId, Orders.takeAwayType "
				+ ", Orders.orderDateTime FROM Orders WHERE inhouse="
				+ HOME_DELIVERY + " AND ";

		if (settings.getHotelType().equals("PREPAID") && !settings.getHasKds())
			sql += "Orders.state=" + Integer.toString(ORDER_STATE_BILLING);
		else
			sql += "(Orders.state=" + Integer.toString(ORDER_STATE_SERVICE) + " OR Orders.state=" + Integer.toString(ORDER_STATE_BILLING) + ")";

		sql += " AND hotelId='" + settings.getOutletId() + "' GROUP BY Orders.orderId ORDER BY Orders.id DESC;";
		return db.getRecords(sql, HomeDelivery.class, settings.getOutletId());
	}

	@Override
	public ArrayList<HomeDelivery> getActiveTakeAway(Settings settings, String userId) {
		
		String sql = "SELECT Orders.Id AS orderNumber, Orders.state, Orders.billNo, Orders.reference, Orders.remarks, Orders.customerName as customer, "
				+ "Orders.customerNumber as mobileNumber, Orders.orderId, Orders.isFoodReady, Orders.takeAwayType, Orders.orderDateTime "
				+ " FROM Orders WHERE inhouse = " + TAKE_AWAY + " AND ";

		if (settings.getHotelType().equals("PREPAID") && !settings.getHasKds())
			sql += "Orders.state=" + Integer.toString(ORDER_STATE_BILLING);
		else
			sql += "(Orders.state=" + Integer.toString(ORDER_STATE_SERVICE) + " OR Orders.state=" + Integer.toString(ORDER_STATE_BILLING) + ")";

		sql += " AND hotelId='" + settings.getOutletId() + "' GROUP BY Orders.orderId ORDER BY Orders.id DESC;";
		return db.getRecords(sql, HomeDelivery.class, settings.getOutletId());
	}

	@Override
	public ArrayList<HomeDelivery> getActiveBarOrders(Settings settings,  String userId) {

		String sql = "SELECT Orders.state AS state, Orders.customerName as customer, Orders.customerNumber as mobileNumber, "
				+ "Orders.customerAddress as address, Orders.orderId, Orders.reference, Orders.remarks, Orders.orderDateTime "
				+ " FROM Orders WHERE inhouse=" + BAR + " AND ";

		if (settings.getHotelType().equals("PREPAID") && !settings.getHasKds())
			sql += "Orders.state=" + Integer.toString(ORDER_STATE_BILLING);
		else
			sql += "(Orders.state=" + Integer.toString(ORDER_STATE_SERVICE) + " OR Orders.state=" + Integer.toString(ORDER_STATE_BILLING) + ")";

		sql += " AND hotelId='" + settings.getOutletId() + "' GROUP BY Orders.orderId ORDER BY Orders.id DESC;";

		return db.getRecords(sql, HomeDelivery.class, settings.getOutletId());
	}

	@Override
	public BigDecimal getOrderTotal(String hotelId, String orderId) {
		String sql = "SELECT TOTAL(OrderItems.qty*OrderItems.rate) AS entityId FROM OrderItems WHERE OrderItems.orderId='" + orderId
				+ "' AND OrderItems.hotelId='" + hotelId + "'";
		EntityBigDecimal amount = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		if (amount == null) {
			return new BigDecimal("0.0");
		}
		return amount.getId();
	}

	@Override
	public Boolean isHomeDeliveryOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND inhouse==0 AND hotelId='" + hotelId
				+ "'";
		return db.hasRecords(sql, hotelId);
	}

	@Override
	public Boolean isTakeAwayOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND inhouse==2 AND hotelId='" + hotelId
				+ "'";
		return db.hasRecords(sql, hotelId);
	}

	@Override
	public Boolean isBarOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND inhouse==3 AND hotelId='" + hotelId
				+ "'";
		return db.hasRecords(sql, hotelId);
	}
	
	@Override
	public ArrayList<OrderItem> getReturnedOrders(String hotelId, String orderId){
		String sql = null;
		
		sql = "SELECT * FROM OrderItemLog WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'";
		
		return db.getRecords(sql, OrderItem.class, hotelId);
	}
	
	@Override
	public ArrayList<OrderItem> getCancellableOrderedItems(String hotelId, String orderId){
		String sql = null;

		sql = "SELECT * FROM OrderItems WHERE orderId=='" + orderId + "' AND state!="
				+ Integer.toString(SUBORDER_STATE_PENDING) + " AND hotelId='" + hotelId + "'";
		
		return db.getRecords(sql, OrderItem.class, hotelId);
	}
	
	@Override
	public Boolean deleteOrder(String hotelId, String orderId) {
		String sql = null;
		sql = "DELETE FROM OrderItems WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
					+ "DELETE FROM Orders WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
					+ "DELETE FROM OrderTables WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
					+ "DELETE FROM OrderSpecifications WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
					+ "DELETE FROM OrderAddons WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; ";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean updateOrderItemLog(String hotelId, String orderId, String subOrderId, String menuId, String reason,
			String type, int quantity, BigDecimal rate, int itemId) {

		int state = SUBORDER_STATE_RETURNED;
		if (type.equals("void"))
			state = SUBORDER_STATE_VOIDED;
		else if (type.equals("comp"))
			state = SUBORDER_STATE_COMPLIMENTARY;
		else if (type.equals("cancel"))
			state = SUBORDER_STATE_CANCELED;

		OrderItem orderedItem = this.getOrderedItem(hotelId, orderId, subOrderId, menuId);
		if(orderedItem == null)
			return false;

		String sql = "INSERT INTO OrderItemLog "
				+ "(hotelId, orderId, subOrderId, subOrderDate, menuId, state, reason, dateTime, quantity, rate, itemId) "
				+ "VALUES('" + escapeString(hotelId) + "', '" + escapeString(orderId) + "', '"
				+ escapeString(subOrderId) + "', '" + escapeString(orderedItem.getSubOrderDate()) + "', '"
				+ escapeString(menuId) + "', " + state + ", '" + reason + "', '"
				+ new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + "', " + quantity + ", " + rate + ", "
				+ itemId + ");";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean updateOrderPrintCount(String hotelId, String orderId) {

		String sql = "SELECT printCount AS entityId FROM Orders WHERE hotelId = '" + hotelId + "' AND orderId = '"
				+ orderId + "';";

		int printCount = getOrderPrintCount(hotelId, orderId) + 1;

		sql = "UPDATE Orders SET printCount = " + printCount + " WHERE hotelId = '" + hotelId + "' AND orderId = '"
				+ orderId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean updateOrderSMSStatusDone(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET isSmsSent = 1 WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId
				+ "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public int getOrderPrintCount(String hotelId, String orderId) {

		String sql = "SELECT printCount AS entityId FROM Orders WHERE hotelId = '" + hotelId + "' AND orderId = '"
				+ orderId + "';";

		return db.getOneRecord(sql, EntityId.class, hotelId).getId();
	}

	@Override
	public boolean updateKOTStatus(String hotelId, String orderId) {

		String sql = "UPDATE OrderItems SET isKotPrinted = 1 WHERE hotelId = '" + hotelId + "' AND orderId = '"
				+ orderId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean updateItemRatesInOrder(String hotelId, String orderId, String newTableType) {
		
		ArrayList<OrderItem> orderItems = this.getOrderedItemForBill(hotelId, orderId, false);
		BigDecimal rate = new BigDecimal("0.0");
		String sql = "";
		MenuItem item = null;
		Double totalFoodBill = 0.0;
		Double totalBarBill = 0.0;
		IMenuItem menuDao = new MenuItemManager(false);
		
		for (OrderItem orderItem : orderItems) {
			item = menuDao.getMenuById(hotelId, orderItem.getMenuId());
			rate = newTableType.equals("AC")?item.getDineInRate():item.getDineInNonAcRate();
			sql += "UPDATE OrderItems SET rate = " + rate + " WHERE hotelId = '"+hotelId
					+ "' AND orderId = '" +orderId+ "' AND menuId = '"+item.getMenuId()+"';" ;
			if(item.getStation().equals("Bar")) {
				totalBarBill += rate.doubleValue()*orderItem.getQty();
			}else {
				totalFoodBill += rate.doubleValue()*orderItem.getQty();
			}
		}
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean applyCustomerGST(String hotelId, String orderId, String gst) {

		String sql = "UPDATE Orders SET customerGST = '" + escapeString(gst) + "' WHERE hotelId = '" + hotelId
				+ "' AND orderId = '" + orderId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean applyOrderRemark(String hotelId, String orderId, String remark) {

		String sql = "UPDATE Orders SET remarks = '" + escapeString(remark) + "' WHERE hotelId = '" + hotelId
				+ "' AND orderId = '" + orderId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public ArrayList<EntityString> getCaptainOrderService(String hotelId, String startDate, String endDate) {
		String sql = "SELECT DISTINCT waiterId AS entityId FROM OrderItems WHERE OrderItems.subOrderDate";
		if (startDate.equals(endDate))
			sql += " LIKE '" + startDate + "%';";
		else
			sql += " BETWEEN '" + startDate + "' AND '" + endDate + "';";

		System.out.println(sql);
		return db.getRecords(sql, EntityString.class, hotelId);
	}
	
	// ----------------------------------------------KOT
	
	@Override
	public ArrayList<OrderItem> getOrderedItemsForKOT(String hotelId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.orderId AS orderId, "
				+ "OrderItems.subOrderDate AS subOrderDate, OrderItems.qty AS qty, MenuItems.title AS title, "
				+ "MenuItems.menuId AS menuId, MenuItems.vegType AS vegType, MenuItems.station AS station, "
				+ "OrderItems.specs AS specs, OrderItems.kotNumber, OrderItems.botNumber FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId AND OrderItems.isKotPrinted = 0 "
				+ "AND OrderItems.hotelId='" + hotelId + "' ORDER BY MenuItems.collection;";
		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	@Override
	public ArrayList<OrderItem> getOrderedItemsForReprintKOT(String hotelId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.orderId AS orderId, "
				+ "OrderItems.subOrderDate AS subOrderDate, OrderItems.qty AS qty, MenuItems.title AS title, "
				+ "MenuItems.menuId AS menuId, MenuItems.vegType AS vegType, MenuItems.station AS station, "
				+ "OrderItems.specs AS specs, OrderItems.kotNumber, OrderItems.botNumber FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId "
				+ "AND OrderItems.hotelId='" + hotelId + "' ORDER BY MenuItems.collection;";
		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	@Override
	public ArrayList<OrderItem> checkKOTPrinting(String hotelId) {

		String sql = "SELECT distinct orderId FROM OrderItems WHERE OrderItems.hotelId = '" + hotelId + "' "
				+ "AND isKotPrinted == 0;";

		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	@Override
	public ArrayList<Order> getOrdersOfOneCustomer(String hotelId, String mobileNumber) {
		String sql = "SELECT Orders.*, Payment.foodDiscount, Payment.barDiscount, Payment.cardType AS paymentType, Payment.total, Payment.creditAmount, " + 
				"(Payment.cashPayment+Payment.cardPayment+Payment.appPayment+Payment.walletPayment) AS totalPayment, Payment.loyaltyAmount AS loyaltyPaid " + 
				"FROM Orders, Payment WHERE customerNumber = '" + mobileNumber + "' AND Orders.hotelId = '" + hotelId + "'" +
				" AND Orders.orderId = Payment.orderId;";
		return db.getRecords(sql, Order.class, hotelId);
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
	public boolean toggleChargeInOrder(String hotelId, String orderId, int chargeId) {
		Order order = this.getOrderById(hotelId, orderId);
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
		String sql = "UPDATE Orders SET excludedCharges = '"+excludedCharges.toString()+"' WHERE hotelId = '"+hotelId+"' AND orderId = '"+order.getOrderId()+"';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean toggleTaxInOrder(String hotelId, String orderId, int taxId) {
		Order order = this.getOrderById(hotelId, orderId);
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
		String sql = "UPDATE Orders SET excludedTaxes = '"+excludedTaxes.toString()+"' WHERE hotelId = '"+hotelId+"' AND orderId = '"+order.getOrderId()+"';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public EntityString getMobileNoFromOrderId(String hotelId, String orderId) {
		String sql = "SELECT customerNumber AS entityId FROM Orders WHERE orderId='" + orderId + "' AND hotelId='"
				+ hotelId + "'";
		return db.getOneRecord(sql, EntityString.class, hotelId);
	}@Override
	public boolean complimentaryAddOn(String hotelId, String orderId, String addOnId, String authId, String menuId,
			String subOrderId, String subOrderDate, int itemId, BigDecimal rate, int qty) {

		if (this.updateOrderAddOnLog(hotelId, orderId, subOrderId, subOrderDate, menuId, itemId, "comp", 1, rate, addOnId)) {
			this.removeAddOn(hotelId, orderId, subOrderId, menuId, qty - 1, addOnId, itemId);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean updateOrderAddOnLog(String hotelId, String orderId, String subOrderId, String subOrderDate, String menuId, 
			int itemId, String type, int quantity, BigDecimal rate, String addOnId) {

		int state = SUBORDER_STATE_RETURNED;
		if (type.equals("void"))
			state = SUBORDER_STATE_VOIDED;
		else if (type.equals("comp"))
			state = SUBORDER_STATE_COMPLIMENTARY;
		else if (type.equals("comp"))
			state = SUBORDER_STATE_CANCELED;
		
		String sql = "INSERT INTO OrderAddOnLog "
				+ "(hotelId, orderId, subOrderId, subOrderDate, menuId, state, itemId, quantity, rate, addOnId) VALUES('"
				+ escapeString(hotelId) + "', '" + escapeString(orderId) + "', " + escapeString(subOrderId) + ", '"
				+ escapeString(subOrderDate) + "', '" + escapeString(menuId) + "', '" + state + "', " + itemId + ", " 
				+ quantity + ", " + rate + ", '" + addOnId + "');";
		if(state == SUBORDER_STATE_CANCELED) {
			IInventory inventoryDao = new InventoryManager(false);
			inventoryDao.revertInventoryForReturn(hotelId, orderId, menuId, quantity);
		}
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean addOrderAddon(String hotelId, Order order, MenuItem menu, int qty, String addOnId, String subOrderId,
			int itemId, BigDecimal rate) {

		String sql = "INSERT INTO OrderAddOns (hotelId, subOrderId, subOrderDate, addOnId, orderId, menuId, itemId, qty, rate, state) values ('"
				+ hotelId + "', '" + subOrderId + "', '" + (new SimpleDateFormat("yyyy/MM/dd HH:mm")).format(new Date()) + "', '"
				+ addOnId + "','" + order.getOrderId() + "', '" + menu.getMenuId() + "', " + itemId
				+ ", " + Integer.toString(qty) + ", " + (new DecimalFormat("0.00")).format(rate) + ", " +SUBORDER_STATE_COMPLETE+ ");";
		if (!db.executeUpdate(sql, true)) {
			return false;
		}

		return true;
	}

	@Override
	public OrderAddOn getOrderedAddOnById(String hotelId, String orderId, String subOrderId, String menuId, int itemId,
			String addOnId) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId AS menuId, "
				+ "OrderAddOns.qty, OrderAddOns.itemId, OrderAddOns.rate AS rate, "
				+ "OrderAddOns.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.menuId='" + menuId + "' AND MenuItems.menuId == OrderAddOns.addOnId "
				+ "AND OrderAddOns.subOrderId='" + subOrderId + "' AND OrderAddOns.itemId == " + itemId + " "
				+ "AND OrderAddOns.addOnId == '" + addOnId + "' AND OrderAddOns.hotelId='" + hotelId + "';";
		return db.getOneRecord(sql, OrderAddOn.class, hotelId);
	}

	@Override
	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String menuId, boolean getReturnedItems) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId, OrderAddOns.state, "
				+ "OrderAddOns.qty, OrderAddOns.itemId, OrderAddOns.rate, "
				+ "OrderAddOns.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.menuId='" + menuId + "' "
				+ "AND OrderAddOns.addOnId == MenuItems.menuId AND OrderAddOns.hotelId='" + hotelId + "'";
		if(getReturnedItems) {
			sql += " UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId, OrderAddOnLog.state, "
				+ "OrderAddOnLog.quantity AS qty, OrderAddOnLog.itemId, OrderAddOnLog.rate, "
				+ "OrderAddOnLog.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOnLog, MenuItems "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' AND OrderAddOnLog.menuId='" + menuId + "' "
				+ "AND OrderAddOnLog.addOnId == MenuItems.menuId AND OrderAddOnLog.hotelId='" + hotelId + "';";
		}
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	@Override
	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String subOrderId, String menuId, boolean getReturnedItems) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId, OrderAddOns.state, "
				+ "OrderAddOns.qty, OrderAddOns.itemId, OrderAddOns.rate, "
				+ "OrderAddOns.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND  OrderAddOns.subOrderId='" + subOrderId + "' AND OrderAddOns.menuId='" + menuId + "' "
				+ "AND OrderAddOns.addOnId == MenuItems.menuId AND OrderAddOns.hotelId='" + hotelId + "'";
		if(getReturnedItems) {
			sql += " UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId, OrderAddOnLog.state, "
				+ "OrderAddOnLog.quantity AS qty, OrderAddOnLog.itemId, OrderAddOnLog.rate, "
				+ "OrderAddOnLog.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOnLog, MenuItems "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' AND OrderAddOnLog.menuId='" + menuId + "' "
				+ "AND OrderAddOnLog.subOrderId='" + subOrderId + "' "
				+ "AND OrderAddOnLog.addOnId == MenuItems.menuId AND OrderAddOnLog.hotelId='" + hotelId + "';";
		}
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	@Override
	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String subOrderId, String menuId,
			int itemId, boolean getReturnedItems) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId, OrderAddOns.state, "
				+ "OrderAddOns.qty, OrderAddOns.itemId, OrderAddOns.rate, "
				+ "OrderAddOns.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.menuId='" + menuId + "' "
				+ "AND OrderAddOns.subOrderId='" + subOrderId + "' AND OrderAddOns.itemId == " + itemId + " "
				+ "AND OrderAddOns.addOnId == MenuItems.menuId AND OrderAddOns.hotelId='" + hotelId + "'";
		if(getReturnedItems) {
			sql += " UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId, OrderAddOnLog.state, "
				+ "OrderAddOnLog.quantity AS qty, OrderAddOnLog.itemId, OrderAddOnLog.rate, "
				+ "OrderAddOnLog.subOrderId, MenuItems.title, MenuItems.taxes, MenuItems.charges, MenuItems.station FROM OrderAddOnLog, MenuItems "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' AND OrderAddOnLog.menuId='" + menuId + "' "
				+ "AND OrderAddOnLog.subOrderId='" + subOrderId + "' AND OrderAddOnLog.itemId == " + itemId + " "
				+ "AND OrderAddOnLog.addOnId == MenuItems.menuId AND OrderAddOnLog.hotelId='" + hotelId + "';";
		}
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	@Override
	public ArrayList<OrderAddOn> getCanceledOrderedAddOns(String hotelId, String orderId, String subOrderId,
			String menuId, int itemId) {

		String sql = "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.quantity AS qty, OrderAddOnLog.itemId, "
				+ "OrderAddOnLog.rate, OrderAddOnLog.subOrderId, MenuItems.title "
				+ "FROM OrderAddOnLog, MenuItems WHERE OrderAddOnLog.orderId='" + orderId + "' "
				+ "AND OrderAddOnLog.menuId='" + menuId + "' AND OrderAddOnLog.subOrderId='" + subOrderId + "' "
				+ "AND OrderAddOnLog.itemId == " + itemId + " AND OrderAddOnLog.addOnId == MenuItems.menuId "
				+ "AND OrderAddOnLog.hotelId='" + hotelId + "';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	@Override
	public ArrayList<OrderAddOn> getReturnedAddOns(String hotelId, String orderId, String subOrderId, String menuId,
			int itemId) {

		String sql = "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.quantity AS qty, OrderAddOnLog.rate, OrderAddOnLog.itemId, OrderAddOnLog.subOrderId, "
				+ "MenuItems.title FROM OrderAddOnLog, MenuItems WHERE OrderAddOnLog.orderId='" + orderId
				+ "' AND OrderAddOnLog.menuId='" + menuId + "' AND OrderAddOnLog.subOrderId='" + subOrderId
				+ "' AND OrderAddOnLog.itemId == " + itemId + " AND OrderAddOnLog.addOnId == MenuItems.menuId "
				+ "AND OrderAddOnLog.hotelId='" + hotelId + "';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	@Override
	public ArrayList<OrderAddOn> getAllOrderedAddOns(String hotelId, String orderId) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId AS menuId, "
				+ "OrderAddOns.subOrderDate, OrderAddOns.state, OrderAddOns.qty, OrderAddOns.rate, OrderAddOns.itemId, "
				+ "OrderAddOns.subOrderId, MenuItems.title FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.addOnId == MenuItems.menuId "
				+ "AND OrderAddOns.hotelId='" + hotelId + "' UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.subOrderDate, OrderAddOnLog.state, OrderAddOnLog.quantity AS qty, OrderAddOnLog.rate, "
				+ "OrderAddOnLog.itemId, OrderAddOnLog.subOrderId, "
				+ "MenuItems.title FROM OrderAddOnLog, MenuItems WHERE OrderAddOnLog.orderId='" + orderId
				+ "' AND OrderAddOnLog.addOnId == MenuItems.menuId AND OrderAddOnLog.state == " + SUBORDER_STATE_COMPLIMENTARY
				+ " AND OrderAddOnLog.hotelId='" + hotelId + "';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	@Override
	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String subOrderId, String menuId, String itemId) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.qty, OrderAddOns.rate "
				+ ", MenuItems.title, OrderAddOnLog.state FROM OrderAddOns, MenuItems "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.addOnId == MenuItems.menuId "
				+ "AND OrderAddOns.hotelId='" + hotelId + "' AND OrderAddOnLog.subOrderId='" + subOrderId
				+ "' AND OrderAddOnLog.menuId == '"+menuId+"' AND OrderAddOnLog.itemId == '" + itemId + "';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	@Override
	public Boolean removeAddOns(String hotelId, String orderId, String subOrderId, String menuId, int qty) {
		String sql = "DELETE FROM OrderAddOns WHERE orderId='" + orderId + "' AND subOrderId =" + subOrderId
				+ " AND menuId='" + menuId + "' AND hotelId='" + hotelId + "' ";

		if (qty > 0)
			sql += "AND itemId =" + (qty + 1) + ";";

		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean removeAddOnsFromItem(String hotelId, String orderId, String subOrderId, String menuId, int itemId) {
		String sql = "DELETE FROM OrderAddOns WHERE orderId='" + orderId + "' AND subOrderId =" + subOrderId
				+ " AND menuId='" + menuId + "' AND hotelId='" + hotelId + "' AND itemId = " + itemId + ";";

		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean removeAddOn(String hotelId, String orderId, String subOrderId, String menuId, int qty, String addOnId,
			int itemId) {
		String sql = null;
		if (qty == 0) {
			sql = "DELETE FROM OrderAddOns WHERE orderId='" + orderId + "' AND subOrderId =" + subOrderId
					+ " AND menuId='" + menuId + "' AND hotelId='" + hotelId + "' AND addOnId='" + addOnId
					+ "' AND itemId =" + itemId + ";";
		} else {
			sql = "UPDATE OrderAddOns SET qty=" + Integer.toString(qty) + " WHERE orderId='" + orderId
					+ "' AND subOrderId =" + subOrderId + " AND menuId='" + menuId + "' AND hotelId='" + hotelId
					+ "' AND addOnId='" + addOnId + "' AND itemId =" + itemId + ";";
		}
		return db.executeUpdate(sql, true);
	}
	
	@Override
	public Boolean addOrderSpecification(String hotelId, String orderId, String subOrderId, String menuId, int itemId,
			String specification) {

		String sql = "INSERT INTO OrderSpecifications (hotelId, orderId, subOrderId, menuId, itemId, specification) values ('"
				+ hotelId + "', '" + orderId + "', '" + subOrderId + "', '" + menuId + "', " + itemId + ", '"
				+ escapeString(specification) + "');";
		return db.executeUpdate(sql, true);
	}

	@Override
	public ArrayList<Specifications> getSpecifications(String hotelId) {
		String sql = "SELECT * FROM Specifications ORDER BY specification;";
		return db.getRecords(sql, Specifications.class, hotelId);
	}

	@Override
	public boolean addSpecification(String name) {

		String sql = "INSERT INTO Specifications (specification) VALUES('" + escapeString(name) + "');";
		return db.executeUpdate(sql, true);
	}

	@Override
	public ArrayList<OrderSpecification> getOrderedSpecification(String hotelId, String orderId, String menuId,
			String subOrderId, int itemId) {

		String sql = "SELECT * FROM OrderSpecifications WHERE orderId='" + orderId + "' AND itemId == " + itemId
				+ " AND menuId='" + menuId + "' AND subOrderId='" + subOrderId + "';";
		return db.getRecords(sql, OrderSpecification.class, hotelId);
	}

	@Override
	public ArrayList<OrderSpecification> getOrderedSpecification(String hotelId, String orderId, String menuId) {

		String sql = "SELECT * FROM OrderSpecifications WHERE orderId='" + orderId + "' AND menuId='" + menuId + "';";
		return db.getRecords(sql, OrderSpecification.class, hotelId);
	}

	@Override
	public Boolean updateWalletTransactionId(String outletId, String orderId, int walletTransactionId) {
		
		String sql = "UPDATE Orders set walletTransactionId = "+ walletTransactionId+ " WHERE orderId = '"+orderId
				+ "' AND hotelId = '"+outletId+"';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean removeCustomerFromOrder(String outletId, String orderId) {
		
		String sql = "UPDATE Orders SET customerNumber = '', customerName = '', customerAddress = '' WHERE hotelId = '"+outletId
				+"' AND orderId = '"+orderId+"';";
		
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean redeemPromotionalCash(String outletId, String orderId, BigDecimal promotionalCash) {
		
		String sql = "UPDATE Orders SET promotionalCash = "+promotionalCash+" WHERE hotelId = '"+outletId
				+"' AND orderId = '"+orderId+"';";
		
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean updateRiderDetails(String outletId, String orderId, String riderName, String riderNumber, String riderStatus) {
		
		if(riderName.isEmpty() && riderNumber.isEmpty() && riderStatus.isEmpty() && orderId.isEmpty()) {
			return false;
		}
		Order order = this.getOrderById(outletId, orderId);
		if(order == null) {
			return false;
		}
		if(order.getRiderStatus().equals(riderStatus)) {
			return false;
		}
		String sql = "UPDATE Orders SET riderName = '"+riderName+"', riderNumber = '"+riderNumber+"', riderStatus='"+riderStatus+
				"' WHERE hotelId = '"+outletId+"' AND orderId = '"+orderId+"';";
		
		return db.executeUpdate(sql, true);
	}
	
	public boolean updateZoamtoVoucherInOrder(String hotelId, String orderId, String discountName, BigDecimal discountAmount) {
		
		String sql = "UPDATE Orders SET discountCode = '"+discountName+"', zomatoVoucherAmount = "+discountAmount+" WHERE orderId = '"+orderId+"' AND hotelId = '"+hotelId+"';";
		
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean updateEWardsOfferDetails(String outletId, String orderId, int points, String couponCode,
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
		Order order = this.getOrderById(outletId, orderId);
		JSONArray discountArr = order.getDiscountCode();
		
		String sql = "UPDATE Orders SET eWards = '"+eWards.toString() + "', ";
		
		if(offerType == 1) {
			discountArr.put("EWARDS");
			sql += "discountCode = '"+discountArr.toString()+"', fixedRupeeDiscount = " + points + " WHERE hotelId = '" + outletId + "' AND orderId = '" + orderId + "';";
		}else {
			discountArr.put(couponCode);
			sql += "discountCode = '" + discountArr.toString() + "' WHERE hotelId = '" + outletId + "' AND orderId = '" + orderId + "';";
		}
		return db.executeUpdate(sql, true);
	}
}
