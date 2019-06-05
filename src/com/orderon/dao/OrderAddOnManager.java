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
}
