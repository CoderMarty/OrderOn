package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Settings;
import com.orderon.dao.AccessManager.Table;
import com.orderon.dao.AccessManager.TableUsage;

public interface ITable extends IAccess{

	public Table getTableById(String hotelId, String tableId);
	
	public Table getOrderTableById(String hotelId, String tableId);

	public ArrayList<TableUsage> getTableUsage(Settings settings, String userId);
	
	public String getTableType(String hotelId, String tableId);

	public ArrayList<TableUsage> getTables(String hotelId);
	
	public ArrayList<TableUsage> getTablesNoSubTables(String hotelId);

	public boolean isTableOrder(String hotelId, String orderId);

	public String getOrderIdFromTables(String hotelId, String tableId);

	public ArrayList<Table> getJoinedTables(String hotelId, String orderId);

	public boolean transferTable(String hotelId, String oldTableId, String newTableId, String orderId);

	public JSONObject moveItem(String hotelId, String oldTableNumber, String newTableNumber, JSONArray orderItemIds, boolean isMergingTables);

	public JSONObject mergeTables(String hotelId, String oldTableNumber, String newTableNumber);
	
	public boolean switchTable(String hotelId, String oldTableNumber, JSONArray newTableNumbers);

	public boolean switchFromBarToTable(String hotelId, String orderId, JSONArray newTableNumbers);

	public Boolean assignWaiterToTable(String hotelId, String waiterId, int tableId);
}
