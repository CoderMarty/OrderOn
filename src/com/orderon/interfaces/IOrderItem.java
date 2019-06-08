package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONObject;

import com.orderon.dao.AccessManager.EntityString;
import com.orderon.dao.AccessManager.MenuItem;
import com.orderon.dao.AccessManager.Order;
import com.orderon.dao.AccessManager.OrderItem;
import com.orderon.dao.AccessManager.Settings;

public interface IOrderItem extends IAccess, IOrderAddOn, ISpecification{

	public boolean updateKOTStatus(String outletId, String orderId);

	public boolean updateOrderItemLog(String outletId, String orderId, String subOrderId, String menuId, String reason,
			String type, int quantity, BigDecimal rate, int itemId);
	
	public ArrayList<OrderItem> getCancellableOrderedItems(String outletId, String orderId);
	
	public ArrayList<OrderItem> getReturnedOrders(String outletId, String orderId);

	public BigDecimal getOrderTotal(String outletId, String orderId);

	public int getTotalBillAmount(String outletId, String orderId);

	public String getNextSubOrderId(String outletId, String orderId);

	public String getNextBillNoNumberFormatMonthwise(String outletId);

	public ArrayList<OrderItem> getReturnedItems(String outletId, String orderId);

	public OrderItem getOrderStatus(String outletId, String orderId, String subOrderId, String menuId);

	public Boolean changeOrderStatus(String outletId, String orderId, String subOrderId, String menuId);

	public Boolean updateSpecifications(String outletId, String orderId, String subOrderId, String menuId,
			String specs);

	public JSONObject newSubOrder(String outletId, Settings settings, Order order, MenuItem menu, Integer qty, String specs,
			String subOrderId, String waiterId, BigDecimal rate, String tableId);
	
	public BigDecimal getTaxableFoodBill(String outletId, String orderId);

	public BigDecimal getTaxableBarBill(String outletId, String orderId);

	public OrderItem getOrderedItem(String outletId, String orderId, String subOrderId, String menuId);

	public ArrayList<OrderItem> getOrderedItems(String outletId, String orderId, boolean showReturned);
	
	public ArrayList<OrderItem> getOrderedItemForVoid(String outletId, String orderId);

	public Boolean editSubOrder(String outletId, String orderId, String subOrderId, String menuId, int qty);

	public ArrayList<EntityString> getUniqueMenuIdForComplimentaryOrder(String outletId, String orderId);

	public boolean complimentaryItem(String outletId, String orderId, String menuId, String authId, String subOrderId,
			BigDecimal rate, int qty, String reason);

	public Boolean removeSubOrder(String outletId, String orderId, String subOrderId, String menuId, int qty);

	public ArrayList<OrderItem> getOrderedItemForBill(String outletId, String orderId, boolean showReturned);

	public ArrayList<OrderItem> getOrderedItemForBillCI(String outletId, String orderId);

	public ArrayList<OrderItem> getComplimentaryOrderedItemForBill(String outletId, String orderId);

	public Boolean updateItemRatesInOrder(String outletId, String orderId, String newTableType);

	public ArrayList<EntityString> getCaptainOrderService(String outletId, String startDate, String endDate);
	
	public ArrayList<OrderItem> getOrderedItemsForKOT(String outletId, String orderId);
	
	public ArrayList<OrderItem> getOrderedItemsForReprintKOT(String outletId, String orderId);

	public ArrayList<OrderItem> checkKOTPrinting(String outletId);
}
