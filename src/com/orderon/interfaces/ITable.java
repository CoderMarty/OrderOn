package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Settings;
import com.orderon.dao.AccessManager.Table;
import com.orderon.dao.AccessManager.TableUsage;

public interface ITable extends IAccess{

	public Table getTableById(String systemId, String outletId, String tableId);
	
	public Table getOrderTableById(String systemId, String outletId, String tableId);

	public ArrayList<TableUsage> getTableUsage(String systemId, String outletId, Settings settings);

	public ArrayList<TableUsage> getTableUsage(String systemId, Settings settings);
	
	public String getTableType(String systemId, String outletId, String tableId);

	public ArrayList<TableUsage> getTables(String systemId, String outletId);
	
	public ArrayList<TableUsage> getTablesNoSubTables(String systemId, String outletId);

	public boolean isTableOrder(String systemId, String orderId);

	public String getOrderIdFromTables(String systemId, String outletId, String tableId);

	public ArrayList<Table> getJoinedTables(String systemId, String orderId);

	public boolean transferTable(String systemId, String outletId, String oldTableId, String newTableId, String orderId);

	public JSONObject moveItem(String systemId, String outletId, String oldTableNumber, String newTableNumber, JSONArray orderItemIds, boolean isMergingTables);

	public JSONObject mergeTables(String systemId, String outletId, String oldTableNumber, String newTableNumber);
	
	public boolean switchTable(String systemId, String outletId, String orderId, String oldTableNumber, JSONArray newTableNumbers);

	public boolean switchFromBarToTable(String systemId, String outletId, String orderId, JSONArray newTableNumbers);

	public Boolean assignWaiterToTable(String systemId, String outletId, String waiterId, int tableId);
}
