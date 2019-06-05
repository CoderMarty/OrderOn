package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Report;

public interface IPayment extends IAccess{

	public ArrayList<Report> getPaymentDetails(String hotelId, String sDate, String eDate);

	public BigDecimal getTotalCashIn(String hotelId, String serviceDate);

	public boolean addPayment(String hotelId, String orderId, BigDecimal foodBill, BigDecimal barBill, BigDecimal foodDiscount,
			BigDecimal barDiscount, BigDecimal loyalty, BigDecimal total, BigDecimal serviceCharge, BigDecimal packagingCharge, 
			BigDecimal deliveryCharge, BigDecimal gst, BigDecimal vatBar, BigDecimal tip, BigDecimal cashPayment,
			BigDecimal cardPayment, BigDecimal appPayment, BigDecimal walletPayment, BigDecimal creditAmount, BigDecimal promotionalCash,
			String paymentType, BigDecimal complimentary, String section);
	
	public boolean addPaymentForVoidOrder(String hotelId, String orderId, BigDecimal foodBill, BigDecimal barBill, 
			String billNo, String section, String orderDate);
	
	public boolean updateGuestReference(String hotelId, String orderId, String guest);
	
	public boolean updateOrderType(String hotelId, String orderId, int orderType);
	
	public boolean deletePayment(String hotelId, String orderId);

	public boolean editPayment(String hotelId, String orderId, BigDecimal cashPayment, BigDecimal cardPayment, BigDecimal appPayment,
			BigDecimal walletPayment, String cardType, BigDecimal grandTotal);

	public Boolean updatePaymentForReturn(String hotelId, String orderId, BigDecimal foodBill, BigDecimal barBill,
			BigDecimal foodDiscount, BigDecimal barDiscount, BigDecimal total, BigDecimal serviceCharge, BigDecimal gst, BigDecimal VATBar, BigDecimal cashPayment,
			BigDecimal cardPayment, BigDecimal appPayment);

	public Report getPayment(String hotelId, String orderId);
	
	public Report getCashCardSales(String hotelId, String serviceDate, String serviceType);
}
