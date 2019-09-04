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

	public boolean updateKOTStatus(String systemId, String orderId);

	public boolean updateOrderItemLog(String systemId, String outletId, String orderId, String subOrderId, String menuId, String reason,
			String type, int quantity, BigDecimal rate, int itemId);
	
	public ArrayList<OrderItem> getCancellableOrderedItems(String systemId, String orderId);
	
	public ArrayList<OrderItem> getReturnedOrders(String systemId, String orderId);

	public BigDecimal getOrderTotal(String systemId, String orderId);

	public int getTotalBillAmount(String systemId, String orderId);

	public String getNextSubOrderId(String systemId, String orderId);

	public String getNextBillNoNumberFormatMonthwise(String systemId, String outletId);

	public ArrayList<OrderItem> getReturnedItems(String systemId, String orderId);

	public OrderItem getOrderStatus(String systemId, String orderId, String subOrderId, String menuId);

	public Boolean changeOrderStatus(String systemId, String orderId, String subOrderId, String menuId);

	public Boolean updateSpecifications(String systemId, String orderId, String subOrderId, String menuId,
			String specs);

	public JSONObject newSubOrder(String systemId, String outletId, Settings settings, Order order, MenuItem menu, Integer qty, String specs,
			String subOrderId, String waiterId, BigDecimal rate, String tableId);

	public OrderItem getOrderedItem(String systemId, String orderId, String subOrderId, String menuId);

	public ArrayList<OrderItem> getOrderedItems(String systemId, String orderId, boolean showReturned);
	
	public ArrayList<OrderItem> getOrderedItemForVoid(String systemId, String orderId);

	public Boolean editSubOrder(String systemId, String orderId, String subOrderId, String menuId, int qty);

	public ArrayList<EntityString> getUniqueMenuIdForComplimentaryOrder(String systemId, String orderId);

	public boolean complimentaryItem(String systemId, String outletId, String orderId, String menuId, String authId, String subOrderId,
			BigDecimal rate, int qty, String reason);

	public Boolean removeSubOrder(String systemId, String orderId, String subOrderId, String menuId, int qty);

	public ArrayList<OrderItem> getOrderedItemForBill(String systemId, String orderId, boolean showReturned);

	public ArrayList<OrderItem> getOrderedItemForBillCI(String systemId, String orderId);

	public ArrayList<OrderItem> getComplimentaryOrderedItemForBill(String systemId, String orderId);

	public Boolean updateItemRatesInOrder(String systemId, String orderId, String newTableType);

	public ArrayList<EntityString> getCaptainOrderService(String systemId, String startDate, String endDate);
	
	public ArrayList<OrderItem> getOrderedItemsForKOT(String systemId, String orderId);
	
	public ArrayList<OrderItem> getOrderedItemsForReprintKOT(String systemId, String orderId);
}
