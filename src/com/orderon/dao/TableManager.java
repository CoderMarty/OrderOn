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
	public ArrayList<TableUsage> getTableUsage(String systemId, Settings settings) {
		String sql = "SELECT Tables.tableId, Tables.type, Tables.showTableView, Tables.section, Tables.orderId, Orders.waiterId, "
				+ "Orders.id, Orders.state FROM Tables "
				+ "LEFT OUTER JOIN Orders ON Tables.orderId == Orders.orderId ";
		
		if(settings.getShowOccupiedTablesOnly()) {
			sql += " ORDER BY Orders.id;";
		}else {
			sql += ";";
		}
		return db.getRecords(sql, TableUsage.class, systemId);
	}

	@Override
	public ArrayList<TableUsage> getTableUsage(String systemId, String outletId, Settings settings) {
		String sql = "SELECT Tables.tableId, Tables.type, Tables.showTableView, Tables.section, Tables.orderId, Orders.waiterId, "
				+ "Tables.isInBilling, Orders.id, Orders.state FROM Tables "
				+ "LEFT OUTER JOIN Orders ON Tables.orderId == Orders.orderId "
				+ "WHERE Tables.outletId = '" + outletId + "' " ;
		
		if(settings.getShowOccupiedTablesOnly()) {
			sql += " ORDER BY Orders.id;";
		}else {
			sql += ";";
		}
		return db.getRecords(sql, TableUsage.class, systemId);
	}

	@Override
	public String getTableType(String systemId, String outletId, String tableId) {
		String sql = "SELECT type AS entityId FROM Tables WHERE outletId = '"+outletId+"' AND tableId = '"+tableId+"'";
		
		EntityString tableType = db.getOneRecord(sql, EntityString.class, systemId);
		return tableType.getEntity();
	}

	@Override
	public ArrayList<TableUsage> getTables(String systemId, String outletId) {
		String sql = "SELECT * FROM Tables WHERE outletId='" + escapeString(outletId) + "';";
		return db.getRecords(sql, TableUsage.class, systemId);
	}

	@Override
	public ArrayList<TableUsage> getTablesNoSubTables(String systemId, String outletId) {
		String sql = "SELECT * FROM Tables WHERE outletId='" + escapeString(outletId) + "' AND subTables != '[]';";
		return db.getRecords(sql, TableUsage.class, systemId);
	}

	@Override
	public boolean isTableOrder(String systemId, String orderId) {
		String sql = "SELECT * FROM Tables WHERE orderId=='" + orderId + "';";
		return db.hasRecords(sql, systemId);
	}

	@Override
	public String getOrderIdFromTables(String systemId, String outletId, String tableId) {
		String sql = "SELECT orderId FROM Tables WHERE tableId=='" + tableId + "' AND outletId='" + outletId + "';";
		TableUsage table = db.getOneRecord(sql, TableUsage.class, systemId);

		if (table == null)
			return null;
		else
			return table.getOrderId();
	}

	@Override
	public ArrayList<Table> getJoinedTables(String systemId, String orderId) {
		String sql = "SELECT * FROM Tables WHERE orderId == '" + orderId + "';";
		return db.getRecords(sql, Table.class, systemId);
	}

	@Override
	public boolean transferTable(String systemId, String outletId, String oldTableId, String newTableId, String orderId) {
		Table oldTable = this.getTableById(systemId, outletId, oldTableId);
		Table newTable = this.getTableById(systemId, outletId, oldTableId);
		IOrder orderDao = new OrderManager(false);
		
		if(!oldTable.getType().equals(newTable.getType())) {
			orderDao.updateItemRatesInOrder(systemId, orderId, newTable.getType());
		}
		String sql = "UPDATE Tables SET orderId=NULL WHERE outletId='" + outletId + "' AND tableId='"
				+ oldTableId + "';"
				+ "UPDATE Tables SET orderId='" + orderId + "' WHERE outletId='" + outletId + "' AND tableId='"
				+ newTableId + "';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public JSONObject moveItem(String systemId, String outletId, String oldTableNumber, String newTableNumber, JSONArray orderItemIds, boolean isMergingTables) {

		JSONObject outObj = new JSONObject();
		IOrderItem orderItemDao = new OrderManager(false);
		try {
			outObj.put("status", false);

			Table oldTable = this.getOrderTableById(systemId, outletId, oldTableNumber);

			String sql = "SELECT COUNT(id) AS entityId FROM OrderItems WHERE orderId = '" + oldTable.getOrderId()+ "';";
			
			int itemCount = db.getOneRecord(sql, EntityId.class, outletId).getId();
			if (!isMergingTables && itemCount == orderItemIds.length()) {
				outObj.put("message", "Cannot move all item. Please cancel or void order.");
				return outObj;
			}

			Table newTable = this.getOrderTableById(systemId, outletId, newTableNumber);

			for (int i = 0; i < orderItemIds.length(); i++) {
				JSONObject orderItemId = orderItemIds.getJSONObject(i);
				sql = "SELECT * FROM OrderItems WHERE Id = " + orderItemId.getInt("id");
				OrderItem item = db.getOneRecord(sql, OrderItem.class, outletId);
				String subOrderId = orderItemDao.getNextSubOrderId(outletId, newTable.getOrderId());
				sql = "UPDATE OrderItems SET orderId = '" + newTable.getOrderId() 
						+ "', subOrderId = '" + subOrderId + "', itemIsMoved = 'true' WHERE Id = " + orderItemId.getInt("id") + ";";
				if (!db.executeUpdate(sql, systemId, true)) {
					outObj.put("message", "Failed to move order. Please try again.");
					return outObj;
				}
				sql = "UPDATE OrderAddOns SET orderId = '" + newTable.getOrderId() + "', subOrderId = '"
						+ subOrderId + "' WHERE orderId = '" + item.getOrderId() + "' AND subOrderId = '"
						+ item.getSubOrderId() + "' AND menuId = '" + item.getMenuId() + "';";
				if (!db.executeUpdate(sql, systemId, true)) {
					outObj.put("message", "Failed to move AddOn. Please try again.");
					return outObj;
				}
				sql = "UPDATE OrderSpecifications SET orderId = '" + newTable.getOrderId() + "', subOrderId = '"
						+ subOrderId + "' WHERE orderId = '" + item.getOrderId() + "' AND subOrderId = '"
						+ item.getSubOrderId() + "' AND menuId = '" + item.getMenuId() + "';";
				if (!db.executeUpdate(sql, systemId, true)) {
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
	public JSONObject mergeTables(String systemId, String outletId, String oldTableNumber, String newTableNumber) {

		IOrder orderDao = new OrderManager(false);
		IOrderItem itemDao = new OrderManager(false);
		
		JSONObject outObj = new JSONObject();
		String sql = "";
		
		Table oldTable = this.getOrderTableById(systemId, outletId, oldTableNumber);
		
		try {
			outObj.put("status", false);

			Order order = orderDao.getOrderById(systemId, oldTable.getOrderId());
			if(order.getPrintCount()>0) {
				outObj.put("message", "Tables can be merged only before printing bill.");
				return outObj;
			}
			
			JSONArray orderedItemIds = new JSONArray();
			JSONObject itemObj = null;
			
			ArrayList<OrderItem> items = itemDao.getOrderedItems(systemId, order.getOrderId(), true);
			
			for (OrderItem orderItem : items) {
				itemObj = new JSONObject();
				itemObj.put("id", orderItem.getId());
				orderedItemIds.put(itemObj);
			}
			
			outObj = this.moveItem(systemId, outletId, oldTableNumber, newTableNumber, orderedItemIds, true);
			
			if(!outObj.getBoolean("status")) {
				outObj.put("message", "Could not merge tables.");
				return outObj;
			}
			sql = "UPDATE Tables SET orderId = NULL WHERE outletId='" + outletId + "' AND tableId='" + oldTableNumber + "';"
				+ "DELETE FROM Orders WHERE orderId = '"+ order.getOrderId() + "';";
			db.executeUpdate(sql, systemId, true);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public boolean switchTable(String systemId, String outletId, String orderId, String oldTableNumber, JSONArray newTableNumbers) {

		String sql = "SELECT * FROM Tables WHERE tableId = '" + oldTableNumber 
				+ "' AND orderId='" + orderId
				+ "' AND outletId='" + outletId + "';";
		
		Table table = db.getOneRecord(sql, Table.class, systemId);
		if(table == null && orderId.isEmpty()) {
			return false;
		}
		if(table != null) {
			orderId = table.getOrderId();
			sql = "UPDATE Tables SET orderId = NULL WHERE orderId = '" + orderId + "' AND outletId = '" + outletId + "';";
			db.executeUpdate(sql, systemId, true);
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
		sql = "";
		for (int i = 0; i < tables.size(); i++) {
			currentTableId = tables.get(i);
			sql += "UPDATE Tables SET orderId = '"+orderId+"' WHERE tableId = '" + currentTableId + "' AND outletId = '" + outletId + "';";
			
			tableId += currentTableId;
			if (i < tables.size() - 1)
				tableId += ", ";
		}
		sql += "UPDATE Orders SET tableId = '" + tableId + "', orderType = " + DINE_IN + " WHERE orderId = '" + orderId + "';";

		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean switchFromBarToTable(String systemId, String outletId, String orderId, JSONArray newTableNumbers) {

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
			sql += "UPDATE Tables SET orderId = '"+orderId+"' WHERE tableId = '" + currentTableId + "' AND outletId = '" + outletId + "';";
			tableId += currentTableId;
			if (i < tables.size() - 1)
				tableId += ", ";
		}
		sql += "UPDATE Orders SET tableId = '" + tableId + "', orderType = " + DINE_IN + " WHERE orderId = '" + orderId + "';";

		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Table getTableById(String systemId, String outletId, String tableId) {
		String sql = "SELECT * FROM Tables WHERE tableId = '" + tableId + "' AND outletId = '" + outletId + "';";

		return db.getOneRecord(sql, Table.class, systemId);
	}

	@Override
	public Table getOrderTableById(String systemId, String outletId, String tableId) {
		String sql = "SELECT * FROM Tables WHERE tableId = '" + tableId + "' AND outletId = '" + outletId + "';";

		return db.getOneRecord(sql, Table.class, systemId);
	}

	@Override
	public Boolean assignWaiterToTable(String systemId, String outletId, String waiterId, int tableId) {

		String sql = "UPDATE Tables SET waiterId = '" + waiterId + "' WHERE outletId = '" + outletId + "' AND tableId = '"
				+ tableId + "';";
		
		JSONArray tables = this.getTableById(systemId, outletId, Integer.toString(tableId)).getSubTables();

		try {
			for (int i = 0; i < tables.length(); i++) {
				sql += "UPDATE Tables SET waiterId = '" + waiterId + "' WHERE outletId = '" + outletId + "' AND tableId = '"
					+ tables.getString(i) + "';";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return db.executeUpdate(sql, systemId, true);
	}
}
