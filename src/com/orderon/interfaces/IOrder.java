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
	
	public ArrayList<Order> getAllOrders(String hotelId, String date, int orderTypeFilter, String query, boolean visible);
	
	public ArrayList<Order> getCompletedOrders(String hotelId);
	
	public JSONObject newOnlineOrder(String jsonObj, JSONObject orderObj, int portalId);
	
	public ArrayList<OnlineOrder> getOnlineOrders(String hotelId);
	
	public OnlineOrder getOnlineOrder(String hotelId, int externalRestauranId, int externalOrderId);

	public boolean updateOnlineOrderStatus(String hotelId, int externalRestauranId, int externalOrderId, int status, String orderId, int orderNumber);

	public boolean editOnlineOrder(String outletId, int externalOrderId, int status);
	
	public boolean updateOnlineRiderData(String outletId, int externalOrderId, String riderName, String riderNumber, String riderStatus);
	
	public ArrayList<OnlineOrder> getOrderDeliveryUpdate(String hotelId);
	
	public boolean markOnlineOrderComplete(String outletId, int orderNumber);
	
	public JSONObject newOrder(String hotelId, String hotelType, String userId, String[] tableIds, int peopleCount, String customer,
			String mobileNumber, String address, String section, String remarks, ServiceLog currentService);
	
	public JSONObject deleteOrdersMonthWise(String hotelId, String month, double foodSalePercent, double barSalePercent);

	public JSONObject deleteOrdersDayWise(String hotelId, double deductionAmount, double foodSalePercent, 
			double barSalePercent, String serviceDate, String serviceType);

	public JSONObject hideOrder(String hotelId, String serviceDate, String serviceType, BigDecimal cashAmount);
	
	public void AlignBillNumbersContinuous(String hotelId, int billNo, String startDate);
	
	public void AlignBillNumbersMonthWise(String hotelId, String month);
	
	public void AlignAllBillNumbers(String hotelId, String startMonth);
	
	public void AlignBillNumbersDayWise(String hotelId, String serviceDate);
	
	public JSONObject newNCOrder(String hotelId, String userId, String reference, String section, String remarks);

	public JSONObject newQsrOrder(String hotelId, String userId, String customer, String mobileNumber,
			String allergyInfo, int orderType, String emailId, String referenceForReview);

	public JSONObject placeOrder(String hotelId, String userId, String customer, JSONObject customerDetails, String phone, String address,
			int orderType, int takeAwayType, String allergyInfo, String reference, String remarks, 
			String externalOrderId, JSONArray discountCodes, String emailId, String referenceForReview, String section,
			Double cashToBeCollected, Double zomatoVoucherAmount, Double piggyBank, BigDecimal amountReceivable);

	public JSONObject newHomeDeliveryOrder(String hotelId, String userId, String customer, String phone, String address,
			String allergyInfo, String remarks, String section);

	public JSONObject newTakeAwayOrder(String hotelId, String userId, String customer, JSONObject customerDetails, String phone,
			String externalId, String allergyInfo, String remarks, String externalOrderId, JSONArray discountCodes,
			String section, Double cashToBeCollected, Double zomatoVoucherAmount, Double piggyBank, BigDecimal amountReceivable);

	public JSONObject newBarOrder(String hotelId, String userId, String reference, String remarks, String section);

	public Boolean unCheckOutOrder(String hotelId, String orderId);

	public Boolean checkOutOrder(String hotelId, String orderId);
	
	public Boolean updateDeliveryTime(String hotelId, String orderId);
	
	public Boolean updateCompleteTime(String hotelId, String orderId);
	
	public Boolean updateDeliveryBoy(String hotelId, String orderId, String employeeName);

	public String updateBillNoInOrders(String hotelId, String orderId);

	public Boolean changeOrderStatus(String hotelId, String orderId);

	public Boolean changeOrderStateToHidden(String hotelId, String orderId);

	public Boolean changeOrderStateToCancelled(String hotelId, String orderId);

	public Boolean markPaymentComplete(String hotelId, String orderId);

	public Boolean changeOrderStatusToService(String hotelId, String orderId);

	public Boolean removeOrderedSpecification(String hotelId, String orderId, String subOrderId, String menuId,
			int itemId);

	public JSONObject voidOrder(String hotelId, String orderId, String reason, String authId, String section);

	public Boolean complimentaryOrder(String hotelId, String orderId, String authId);

	public Integer getOrderCount(String hotelId, String userId, Date dt);
	
	public Order getOrderById(String hotelId, String orderId);

	public Boolean hasCheckedOutOrders(String hotelId, String serviceDate);

	public int getNextKOTNumber(String hotelId);

	public String getNextBillNo(String hotelId, String station);

	public String getNextBillNoNumberFormat(String hotelId);

	public String getNextBillNoNumberFormatDaywise(String hotelId);

	public String getNextBillNoNumberFormatMonthwise(String hotelId);

	public ArrayList<HomeDelivery> getActiveHomeDeliveries(Settings settings, String userId);

	public ArrayList<HomeDelivery> getActiveTakeAway(Settings settings, String userId);

	public ArrayList<HomeDelivery> getActiveBarOrders(Settings settings, String userId);

	public Boolean isHomeDeliveryOrder(String hotelId, String orderId);

	public Boolean isTakeAwayOrder(String hotelId, String orderId);

	public Boolean isBarOrder(String hotelId, String orderId);
	
	public Boolean deleteOrder(String hotelId, String orderId);

	public boolean updateOrderPrintCount(String hotelId, String orderId);

	public boolean updateOrderSMSStatusDone(String hotelId, String orderId);

	public int getOrderPrintCount(String hotelId, String orderId);
	
	public Boolean updateItemRatesInOrder(String hotelId, String orderId, String newTableType);

	public boolean applyCustomerGST(String hotelId, String orderId, String gst);

	public boolean applyOrderRemark(String hotelId, String orderId, String remark);
	
	public ArrayList<Order> getOrdersOfOneCustomer(String hotelId, String mobileNumber);

	public String getOrderType(int orderTypeCode);
	
	public boolean toggleChargeInOrder(String hotelId, String orderId, int chargeId);
	
	public boolean toggleTaxInOrder(String hotelId, String orderId, int taxId);

	public EntityString getMobileNoFromOrderId(String hotelId, String orderId);
	
	public Boolean markFoodReady(String outletId, String orderId);
	
	public Boolean updateWalletTransactionId(String outletId, String orderId, int walletTransactionId);
	
	public Boolean removeCustomerFromOrder(String outletId, String orderId);
	
	public Boolean redeemPromotionalCash(String outletId, String orderId, BigDecimal promotionalCash);
	
	public Boolean updateRiderDetails(String outletId, String orderId, String riderName, String riderNumber, String riderStatus);
}
