package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.MenuItem;
import com.orderon.dao.AccessManager.Order;
import com.orderon.dao.AccessManager.OrderAddOn;

public interface IOrderAddOn extends IAccess{

	public boolean updateOrderAddOnLog(String hotelId, String orderId, String subOrderId, String subOrderDate, String menuId, 
			int itemId, String type, int quantity, BigDecimal rate, String addOnId);

	public boolean complimentaryAddOn(String hotelId, String orderId, String addOnId, String authId, String menuId,
			String subOrderId, String subOrderDate, int itemId, BigDecimal rate, int qty);

	public Boolean addOrderAddon(String hotelId, Order order, MenuItem menu, int qty, String addOnId, String subOrderId,
			int itemId, BigDecimal rate);

	public OrderAddOn getOrderedAddOnById(String hotelId, String orderId, String subOrderId, String menuId, int itemId,
			String addOnId);
	
	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String menuId, boolean getReturnedItems);
	
	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String subOrderId, String menuId, boolean getReturnedItems);

	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String subOrderId, String menuId,
			int itemId, boolean getReturnedItems);

	public ArrayList<OrderAddOn> getCanceledOrderedAddOns(String hotelId, String orderId, String subOrderId,
			String menuId, int itemId);

	public ArrayList<OrderAddOn> getReturnedAddOns(String hotelId, String orderId, String subOrderId, String menuId,
			int itemId);

	public ArrayList<OrderAddOn> getAllOrderedAddOns(String hotelId, String orderId);

	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String subOrderId, String menuId, 
			String itemId);

	public Boolean removeAddOns(String hotelId, String orderId, String subOrderId, String menuId, int qty);

	public Boolean removeAddOnsFromItem(String hotelId, String orderId, String subOrderId, String menuId, int itemId);

	public Boolean removeAddOn(String hotelId, String orderId, String subOrderId, String menuId, int qty, String addOnId,
			int itemId);
}
