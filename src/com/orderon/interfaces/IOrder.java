package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.EntityString;
import com.orderon.dao.AccessManager.HomeDelivery;
import com.orderon.dao.AccessManager.OnlineOrder;
import com.orderon.dao.AccessManager.Order;
import com.orderon.dao.AccessManager.ServiceLog;
import com.orderon.dao.AccessManager.Settings;

public interface IOrder extends IAccess{
	
	public ArrayList<Order> getAllOrders(String systemId, String outletId, String date, int orderTypeFilter, String query, JSONObject orderBy);
	
	public ArrayList<Order> getCompletedOrders(String systemId);
	
	public JSONObject newOnlineOrder(String jsonObj, JSONObject orderObj, int portalId);
	
	public ArrayList<OnlineOrder> getOnlineOrders(String systemId);
	
	public OnlineOrder getOnlineOrder(String systemId, int externalRestauranId, int externalOrderId);

	public boolean updateOnlineOrderStatus(String systemId, int externalRestauranId, int externalOrderId, int status, String orderId, int orderNumber);

	public boolean editOnlineOrder(String systemId, int externalOrderId, int status);
	
	public boolean updateOnlineRiderData(String systemId, int externalOrderId, String riderName, String riderNumber, String riderStatus);
	
	public ArrayList<OnlineOrder> getOrderDeliveryUpdate(String systemId);
	
	public boolean markOnlineOrderComplete(String systemId, int orderNumber);
	
	public JSONObject newOrder(String systemId, String outletId, String hotelType, String userId, String[] tableIds, int peopleCount, String customer,
			String customerNumber, String address, String section, String remarks, ServiceLog currentService, String waiter);
	
	public JSONObject deleteOrdersMonthWise(String systemId, String month, double foodSalePercent, double barSalePercent);

	public JSONObject deleteOrdersDayWise(String systemId, double deductionAmount, double foodSalePercent, 
			double barSalePercent, String serviceDate, String serviceType);

	public JSONObject hideOrder(String systemId, String serviceDate, String serviceType, BigDecimal cashAmount);
	
	public void AlignBillNumbersContinuous(String systemId, int billNo, String startDate);
	
	public void AlignBillNumbersMonthWise(String systemId, String month);
	
	public void AlignAllBillNumbers(String systemId, String startMonth);
	
	public void AlignBillNumbersDayWise(String systemId, String serviceDate);
	
	public JSONObject newNCOrder(String systemId, String outletId, String userId, String reference, String section, String remarks);

	public JSONObject newQsrOrder(String systemId, String outletId, String userId, String customer, String customerNumber,
			String allergyInfo, int orderType, String emailId, String referenceForReview);

	public JSONObject placeOrder(String systemId, String outletId, String userId, String customer, JSONObject customerDetails, String customerNumber, String address,
			int orderType, int takeAwayType, String allergyInfo, String reference, String remarks, 
			String externalOrderId, JSONArray discountCodes, String emailId, String referenceForReview, String section,
			Double cashToBeCollected, Double zomatoVoucherAmount, Double goldDiscount, Double piggyBank, BigDecimal amountReceivable,
			int orderPreparationTime, JSONObject onlineOrderData);

	public JSONObject newHomeDeliveryOrder(String systemId, String outletId, String userId, String customer, String customerNumber, String address,
			String allergyInfo, String remarks, String section);

	public JSONObject newTakeAwayOrder(String systemId, String outletId, String userId, String customer, JSONObject customerDetails, String customerNumber,
			String externalId, String allergyInfo, String remarks, String externalOrderId, JSONArray discountCodes,
			String section, Double cashToBeCollected, Double goldDiscount, Double zomatoVoucherAmount, Double piggyBank, BigDecimal amountReceivable,
			int orderPreparationTime, JSONObject onlineOrderData);

	public JSONObject newBarOrder(String systemId, String outletId, String userId, String reference, String remarks, String section);

	public Boolean unCheckOutOrder(String systemId, String orderId);

	public Boolean checkOutOrder(String systemId, String outletId, String orderId);
	
	public Boolean updateDeliveryTime(String systemId, String orderId);
	
	public Boolean updateCompleteTime(String systemId, String orderId);
	
	public Boolean updateDeliveryBoy(String systemId, String orderId, String employeeName);

	public Boolean changeOrderStatus(String systemId, String orderId);

	public Boolean changeOrderStateToHidden(String systemId, String orderId);

	public Boolean changeOrderStateToCancelled(String systemId, String orderId);

	public Boolean markPaymentComplete(String systemId, String orderId);

	public Boolean changeOrderStatusToService(String systemId, String orderId);

	public Boolean removeOrderedSpecification(String systemId, String orderId, String subOrderId, String menuId,
			int itemId);

	public JSONObject voidOrder(String systemId, String outletId, String orderId, String reason, String authId, 
			String section, String userId);

	public Boolean complimentaryOrder(String systemId, String outletId, String orderId, String authId, String userId);

	public Integer getOrderCount(String systemId, String outletId, String userId, Date dt);
	
	public Order getOrderById(String systemId, String orderId);

	public Boolean hasCheckedOutOrders(String systemId, String serviceDate);

	public int getNextKOTNumber(String systemId, String outletId);

	public int getNextBOTNumber(String systemId, String outletId);

	public String getNextBillNoNumberFormatYearly(String systemId, String outletId);

	public String getNextBillNoNumberFormatDaywise(String systemId, String outletId);

	public String getNextBillNoNumberFormatMonthwise(String systemId, String outletId);

	public ArrayList<HomeDelivery> getActiveHomeDeliveries(String systemId, Settings settings, String outletId, String userId);

	public ArrayList<HomeDelivery> getActiveTakeAway(String systemId, Settings settings, String outletId, String userId);

	public ArrayList<HomeDelivery> getActiveBarOrders(String systemId, Settings settings, String outletId, String userId);

	public Boolean isHomeDeliveryOrder(String systemId, String orderId);

	public Boolean isTakeAwayOrder(String systemId, String orderId);

	public Boolean isBarOrder(String systemId, String orderId);
	
	public Boolean deleteOrder(String systemId, String orderId);

	public boolean updateOrderPrintCount(String systemId, String orderId);

	public boolean updateOrderSMSStatusDone(String systemId, String orderId);

	public int getOrderPrintCount(String systemId, String orderId);
	
	public Boolean updateItemRatesInOrder(String systemId, String orderId, String newTableType);

	public boolean applyCustomerGST(String systemId, String orderId, String gst);

	public boolean applyOrderRemark(String systemId, String orderId, String remark);
	
	public ArrayList<Order> getOrdersOfOneCustomer(String systemId, String customerNumber);

	public String getOrderType(int orderTypeCode);
	
	public boolean toggleChargeInOrder(String systemId, String orderId, int chargeId);
	
	public boolean toggleTaxInOrder(String systemId, String orderId, int taxId);

	public EntityString getMobileNoFromOrderId(String systemId, String orderId);
	
	public Boolean markFoodReady(String systemId, String orderId);
	
	public Boolean updateWalletTransactionId(String systemId, String orderId, int walletTransactionId);
	
	public Boolean removeCustomerFromOrder(String systemId, String orderId);
	
	public Boolean redeemPromotionalCash(String systemId, String orderId, BigDecimal promotionalCash);
	
	public Boolean updateRiderDetails(String systemId, String orderId, String riderName, String riderNumber, String riderStatus);
	
	public Boolean updateEWardsOfferDetails(String systemId, String orderId, int points, String couponCode, int offerType);
	
	public Boolean checkIfOnlineOrderExists(String systemId, String outletId, String externalOrderId, String serviceDate);
	
	public void deleteBlankOrders(String systemId, String serviceDate);
	
	public boolean assignBillNumberToOrder(String systemId, String outletId, String orderId);
}
