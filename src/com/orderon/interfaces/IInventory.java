package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONArray;

import com.orderon.dao.AccessManager.Inventory;

public interface IInventory {

	public void manageInventory(String outletId, String menuId, String orderId, int quantity);

	public ArrayList<Inventory> getInventoryLog(String outletId);

	public ArrayList<Inventory> getInventoryRunningOut(String outletId);

	public Inventory getMaterialHistory(String outletId, String sku);

	public Inventory getMaterialHistoryForMenuItem(String outletId, String menuId, String sku);

	public ArrayList<Inventory> getInventoryLogForItem(String outletId, String orderId, String menuId);

	public ArrayList<Inventory> getInventoryLogForOrder(String outletId, String orderId);

	//DELETE
	public boolean deleteInventoryItem(String outletId, String sku);

	//SPOILAGE
	public boolean addSpoilage(int sku, BigDecimal quantity, BigDecimal ratePerUnit, String outletId, JSONArray logId);

	//USEDUP
	public boolean addUsedUpInventory(int sku, BigDecimal quantity, BigDecimal ratePerUnit, String outletId, String orderId, String menuId, JSONArray logId);

	//RETURNED
	public boolean addReturnedInventory(int sku, BigDecimal quantity, BigDecimal ratePerUnit, String outletId, String orderId, String menuId, JSONArray logId);

	//ADDED
	public boolean addNewMaterialToInventory(int sku, BigDecimal quantity, BigDecimal ratePerUnit, String outletId);

	//UPDATE
	public boolean addMaterialAfterPurchase(String purchaseId, int sku, BigDecimal quantity, BigDecimal ratePerUnit, String outletId, 
			BigDecimal gst, double gstValue, BigDecimal discount, BigDecimal subTotal, BigDecimal totalAmount);
	
	public void revertInventoryForReturn(String outletId, String orderId, String menuId, int itemQuantity);
	
	public void revertInventoryForVoid(String outletId, String orderId);
	
	public ArrayList<Inventory> getInventoryLogForPurchase(String outletId, String purchaseId);
}
