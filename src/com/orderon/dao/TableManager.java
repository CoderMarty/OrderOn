package com.orderon.dao;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.IOrder;
import com.orderon.interfaces.IOrderItem;
import com.orderon.interfaces.ITable;

public class TableManager extends AccessManager implements ITable {

	public TableManager(Boolean transactionBased) {
		super(transactionBased);
	}

	@Override
	public ArrayList<TableUsage> getTableUsage(Settings settings) {
		String sql = "SELECT Tables.tableId, Tables.type, Tables.showTableView, Tables.section, OrderTables.orderId, Orders.waiterId, Orders.state " + 
				"FROM Tables " + 
				"LEFT OUTER JOIN OrderTables ON OrderTables.tableId == Tables.tableId " + 
				"LEFT OUTER JOIN Orders ON OrderTables.tableId == Tables.tableId AND OrderTables.orderId == Orders.orderId " +
				"WHERE Tables.hotelId = '"+settings.getOutletId()+"'";
		
		if(settings.getShowOccupiedTablesOnly()) {
			sql += " ORDER BY OrderTables.orderId DESC, Tables.id;";
		}else {
			sql += ";";
		}
		return db.getRecords(sql, TableUsage.class, settings.getOutletId());
	}

	@Override
	public String getTableType(String hotelId, String tableId) {
		String sql = "SELECT type AS entityId FROM Tables WHERE hotelId = '"+hotelId+"' AND tableId = '"+tableId+"'";
		
		EntityString tableType = db.getOneRecord(sql, EntityString.class, hotelId);
		return tableType.getEntity();
	}

	@Override
	public ArrayList<TableUsage> getTables(String hotelId) {
		String sql = "SELECT * FROM Tables WHERE hotelId='" + escapeString(hotelId) + "';";
		return db.getRecords(sql, TableUsage.class, hotelId);
	}

	@Override
	public ArrayList<TableUsage> getTablesNoSubTables(String hotelId) {
		String sql = "SELECT * FROM Tables WHERE hotelId='" + escapeString(hotelId) + "' AND subTables != '[]';";
		return db.getRecords(sql, TableUsage.class, hotelId);
	}

	@Override
	public boolean isTableOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM OrderTables WHERE orderId=='" + orderId + "' AND hotelId=='" + hotelId + "';";
		return db.hasRecords(sql, hotelId);
	}

	@Override
	public String getOrderIdFromTables(String hotelId, String tableId) {
		String sql = "SELECT orderId FROM OrderTables WHERE tableId=='" + tableId + "' AND  hotelId='" + hotelId + "';";
		TableUsage table = db.getOneRecord(sql, TableUsage.class, hotelId);

		if (table == null)
			return null;
		else
			return table.getOrderId();
	}

	@Override
	public ArrayList<Table> getJoinedTables(String hotelId, String orderId) {
		String sql = "SELECT * FROM OrderTables WHERE orderId == '" + orderId + "' AND hotelId='" + hotelId + "'";
		return db.getRecords(sql, Table.class, hotelId);
	}

	@Override
	public boolean transferTable(String hotelId, String oldTableId, String newTableId, String orderId) {
		Table oldTable = this.getTableById(hotelId, oldTableId);
		Table newTable = this.getTableById(hotelId, oldTableId);
		IOrder orderDao = new OrderManager(false);
		
		if(!oldTable.getType().equals(newTable.getType())) {
			orderDao.updateItemRatesInOrder(hotelId, orderId, newTable.getType());
		}
		String sql = "UPDATE OrderTables SET tableId='" + newTableId + "' WHERE hotelId='" + hotelId + "' AND tableId='"
				+ oldTableId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public JSONObject moveItem(String outletId, String oldTableNumber, String newTableNumber, JSONArray orderItemIds, boolean isMergingTables) {

		JSONObject outObj = new JSONObject();
		IOrderItem orderItemDao = new OrderManager(false);
		try {
			outObj.put("status", false);

			Table oldTable = this.getOrderTableById(outletId, oldTableNumber);

			String sql = "SELECT COUNT(id) AS entityId FROM OrderItems WHERE orderId = '" + oldTable.getOrderId()
					+ "' AND hotelId='" + outletId + "';";
			int itemCount = db.getOneRecord(sql, EntityId.class, outletId).getId();
			if (!isMergingTables && itemCount == orderItemIds.length()) {
				outObj.put("message", "Cannot move all item. Please cancel or void order.");
				return outObj;
			}

			Table newTable = this.getOrderTableById(outletId, newTableNumber);

			for (int i = 0; i < orderItemIds.length(); i++) {
				JSONObject orderItemId = orderItemIds.getJSONObject(i);
				sql = "SELECT * FROM OrderItems WHERE Id = " + orderItemId.getInt("id");
				OrderItem item = db.getOneRecord(sql, OrderItem.class, outletId);
				String subOrderId = orderItemDao.getNextSubOrderId(outletId, newTable.getOrderId());
				sql = "UPDATE OrderItems SET orderId = '" + newTable.getOrderId() 
						+ "', subOrderId = '" + subOrderId + "', itemIsMoved = 'true' WHERE Id = " + orderItemId.getInt("id") + ";";
				if (!db.executeUpdate(sql, true)) {
					outObj.put("message", "Failed to move order. Please try again.");
					return outObj;
				}
				sql = "UPDATE OrderAddOns SET orderId = '" + newTable.getOrderId() + "', subOrderId = '"
						+ subOrderId + "' WHERE orderId = '" + item.getOrderId() + "' AND subOrderId = '"
						+ item.getSubOrderId() + "' AND menuId = '" + item.getMenuId() + "';";
				if (!db.executeUpdate(sql, true)) {
					outObj.put("message", "Failed to move AddOn. Please try again.");
					return outObj;
				}
				sql = "UPDATE OrderSpecifications SET orderId = '" + newTable.getOrderId() + "', subOrderId = '"
						+ subOrderId + "' WHERE orderId = '" + item.getOrderId() + "' AND subOrderId = '"
						+ item.getSubOrderId() + "' AND menuId = '" + item.getMenuId() + "';";
				if (!db.executeUpdate(sql, true)) {
					outObj.put("message", "Failed to move Specifications. Please try again.");
					return outObj;
				}
			}
			outObj.put("status", true);
		} catch (JSONException e1) {
			db.rollbackTransaction();
			e1.printStackTrace();
		}
		return outObj;
	}

	@Override
	public JSONObject mergeTables(String hotelId, String oldTableNumber, String newTableNumber) {

		IOrder orderDao = new OrderManager(false);
		IOrderItem itemDao = new OrderManager(false);
		
		JSONObject outObj = new JSONObject();
		String sql = "";
		
		Table oldTable = this.getOrderTableById(hotelId, oldTableNumber);
		
		try {
			outObj.put("status", false);

			Order order = orderDao.getOrderById(hotelId, oldTable.getOrderId());
			if(order.getPrintCount()>0) {
				outObj.put("message", "Tables can be merged only before printing bill.");
				return outObj;
			}
			
			JSONArray orderedItemIds = new JSONArray();
			JSONObject itemObj = null;
			
			ArrayList<OrderItem> items = itemDao.getOrderedItems(hotelId, order.getOrderId(), true);
			
			for (OrderItem orderItem : items) {
				itemObj = new JSONObject();
				itemObj.put("id", orderItem.getId());
				orderedItemIds.put(itemObj);
			}
			
			outObj = this.moveItem(hotelId, oldTableNumber, newTableNumber, orderedItemIds, true);
			
			if(!outObj.getBoolean("status")) {
				outObj.put("message", "Could not merge tables.");
				return outObj;
			}
			sql = "DELETE FROM OrderTables WHERE hotelId='" + hotelId + "' AND tableId='" + oldTableNumber + "';"
				+ "DELETE FROM Orders WHERE orderId = '"+ order.getOrderId() + "' AND hotelId = '"+ hotelId + "';";
			db.executeUpdate(sql, true);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public boolean switchTable(String hotelId, String orderId, String oldTableNumber, JSONArray newTableNumbers) {

		String sql = "SELECT orderId FROM OrderTables WHERE tableId = '" + oldTableNumber + "' AND hotelId='" + hotelId
				+ "';";
		
		Table table = db.getOneRecord(sql, Table.class, hotelId);
		if(table == null && orderId.isEmpty()) {
			return false;
		}
		if(table != null) {
			orderId = table.getOrderId();
			sql = "DELETE FROM OrderTables WHERE orderId = '" + orderId + "' AND hotelId = '" + hotelId + "';";
			db.executeUpdate(sql, true);
		}
		String tableId = "";
		String currentTableId = "";
		ArrayList<String> tables = new ArrayList<String>();

		for (int i = 0; i < newTableNumbers.length(); i++) {
			try {
				currentTableId = newTableNumbers.getJSONObject(i).getString("tableId");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (!tables.contains(currentTableId))
				tables.add(currentTableId);
		}

		for (int i = 0; i < tables.size(); i++) {
			currentTableId = tables.get(i);
			sql = "INSERT INTO OrderTables (hotelId, tableId, orderId) values('" + hotelId + "','" + currentTableId
					+ "','" + orderId + "');";
			db.executeUpdate(sql, true);
			tableId += currentTableId;
			if (i < tables.size() - 1)
				tableId += ", ";
		}
		sql = "UPDATE Orders SET tableId = '" + tableId + "', inhouse = " + DINE_IN + " WHERE orderId = '" + orderId + "' AND hotelId='"
				+ hotelId + "';";

		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean switchFromBarToTable(String hotelId, String orderId, JSONArray newTableNumbers) {

		String tableId = "";
		int currentTableId = 0;
		ArrayList<Integer> tables = new ArrayList<Integer>();
		for (int i = 0; i < newTableNumbers.length(); i++) {
			try {
				currentTableId = newTableNumbers.getJSONObject(i).getInt("tableId");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (!tables.contains(currentTableId))
				tables.add(currentTableId);
		}

		String sql = "";

		for (int i = 0; i < tables.size(); i++) {
			currentTableId = tables.get(i);
			sql = "INSERT INTO OrderTables (hotelId, tableId, orderId) values('" + hotelId + "','" + currentTableId
					+ "','" + orderId + "');";
			db.executeUpdate(sql, true);
			tableId += currentTableId;
			if (i < tables.size() - 1)
				tableId += ", ";
		}
		sql = "UPDATE Orders SET tableId = '" + tableId + "', inHouse = " + DINE_IN + " WHERE orderId = '" + orderId
				+ "' AND hotelId='" + hotelId + "';";

		return db.executeUpdate(sql, true);
	}

	@Override
	public Table getTableById(String hotelId, String tableId) {
		String sql = "SELECT * FROM Tables WHERE tableId = '" + tableId + "' AND hotelID = '" + hotelId + "';";

		return db.getOneRecord(sql, Table.class, hotelId);
	}

	@Override
	public Table getOrderTableById(String hotelId, String tableId) {
		String sql = "SELECT * FROM OrderTables WHERE tableId = '" + tableId + "' AND hotelID = '" + hotelId + "';";

		return db.getOneRecord(sql, Table.class, hotelId);
	}

	@Override
	public Boolean assignWaiterToTable(String hotelId, String waiterId, int tableId) {

		String sql = "UPDATE Tables SET waiterId = '" + waiterId + "' WHERE hotelId = '" + hotelId + "' AND tableId LIKE '"
				+ tableId + "%';";

		return db.executeUpdate(sql, true);
	}
}
