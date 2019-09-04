package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Report;

public interface IPayment extends IAccess{

	public ArrayList<Report> getPaymentDetails(String systemId, String outletId, String sDate, String eDate);

	public BigDecimal getTotalCashIn(String systemId, String outletId, String serviceDate);

	public boolean addPayment(String systemId, String outletId, String orderId, BigDecimal foodBill, BigDecimal barBill, BigDecimal foodDiscount,
			BigDecimal barDiscount, BigDecimal loyalty, BigDecimal total, BigDecimal serviceCharge, BigDecimal packagingCharge, 
			BigDecimal deliveryCharge, BigDecimal gst, BigDecimal vat, BigDecimal tip, BigDecimal cashPayment,
			BigDecimal cardPayment, BigDecimal appPayment, BigDecimal walletPayment, BigDecimal creditAmount, BigDecimal promotionalCash,
			String paymentType, BigDecimal complimentary, String section);
	
	public boolean addPaymentForVoidOrder(String systemId, String outletId, String orderId, BigDecimal foodBill, BigDecimal barBill, 
			String billNo, String section, String orderDate);
	
	public boolean makeOrderNonChargeable(String systemId, String outletId, String orderId, String guest);
	
	public boolean updateOrderType(String systemId, String outletId, String orderId, int orderType);
	
	public boolean deletePayment(String systemId, String outletId, String orderId);

	public boolean editPayment(String systemId, String outletId, String orderId, BigDecimal cashPayment, BigDecimal cardPayment, BigDecimal appPayment,
			BigDecimal walletPayment, String paymentType, BigDecimal grandTotal);

	public Report getPayment(String systemId, String outletId, String orderId);
	
	public Report getCashCardSales(String systemId, String serviceDate, String serviceType);
}
