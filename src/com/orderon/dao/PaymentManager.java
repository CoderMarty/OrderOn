package com.orderon.dao;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.interfaces.IOrder;
import com.orderon.interfaces.IPayment;
import com.orderon.interfaces.IService;

public class PaymentManager extends AccessManager implements IPayment{

	public PaymentManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<Report> getPaymentDetails(String hotelId, String sDate, String eDate) {
		String sql = "SELECT * FROM Payment WHERE hotelId= '" + hotelId + "' AND orderDate BETWEEN '" + sDate
				+ "' AND '" + eDate + "'";
		return db.getRecords(sql, Report.class, hotelId);
	}

	@Override
	public BigDecimal getTotalCashIn(String hotelId, String serviceDate) {
		String sql = "SELECT SUM(cashPayment) FROM Payment WHERE hotelId= '" + hotelId + "' AND orderDate = '"
				+ serviceDate.replace("/", "-") + "';";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	@Override
	public boolean addPayment(String hotelId, String orderId, BigDecimal foodBill, BigDecimal barBill, BigDecimal foodDiscount,
			BigDecimal barDiscount, BigDecimal loyalty, BigDecimal total, BigDecimal serviceCharge, BigDecimal packagingCharge, 
			BigDecimal deliveryCharge, BigDecimal gst, BigDecimal vatBar, BigDecimal tip, BigDecimal cashPayment,
			BigDecimal cardPayment, BigDecimal appPayment, BigDecimal walletPayment, BigDecimal creditAmount, BigDecimal promotionalCash,
			String paymentType, BigDecimal complimentary, String section) {

		if(paymentType.equals("")) {
			return false;
		}
		
		paymentType = paymentType.toUpperCase().replace(' ', '_');
		
		IService serviceDao = new ServiceManager(false);
		String orderDate = serviceDao.getServiceDate(hotelId);
		BigDecimal roundOff = new BigDecimal("0");
		
		if(!(paymentType.equals("VOID") || paymentType.equals("NON_CHARGEABLE"))) {
			roundOff = cashPayment.add(cardPayment).add(appPayment).add(walletPayment).add(creditAmount).subtract(total);
		}
		
		if(paymentType.equals("NON_CHARGEABLE")) {
			total = new BigDecimal("0.0");
			serviceCharge = new BigDecimal("0.0");
			packagingCharge = new BigDecimal("0.0");
			foodDiscount = new BigDecimal("0.0");
			barDiscount = new BigDecimal("0.0");
			deliveryCharge = new BigDecimal("0.0");
			gst = new BigDecimal("0.0");
			vatBar = new BigDecimal("0.0");
		}

		String sql = "SELECT * FROM Payment WHERE hotelId='" + hotelId + "' AND orderId='" + orderId
				+ "' AND orderDate='" + orderDate + "';";
		//add app payment type.
		Report payment = db.getOneRecord(sql, Report.class, hotelId);

		if (payment != null)
			return false;

		IOrder orderDao = new OrderManager(false);
		Order order = orderDao.getOrderById(hotelId, orderId);
		
		sql = "INSERT INTO Payment (hotelId, billNo, billNo2, orderId, orderDate, foodBill, barBill, foodDiscount, barDiscount, loyaltyAmount, total, "
				+ "serviceCharge, packagingCharge, deliveryCharge, gst, VATBAR, tip, cashPayment, cardPayment, appPayment, walletPayment, "
				+ "creditAmount, promotionalCash, discountName, cardType, complimentary, section, roundOff) "
				+ "VALUES('" + hotelId + "', '" + order.getBillNo() + "', '" + order.getBillNo() + "', '" + orderId + "', '" + orderDate + "', "
				+ foodBill + ", " + barBill + ", " + foodDiscount + ", "+ barDiscount + ", " + loyalty + ", "+ total + ", " + serviceCharge + ", " 
				+ packagingCharge+ ", " + deliveryCharge+ ", " + gst + ", " + vatBar + ", "+ tip + ", " + cashPayment + ", " 
				+ cardPayment + ", " + appPayment + ", " + walletPayment+ ", " + creditAmount+ ", " + promotionalCash+ ", '" 
				+ order.getDiscountCode() + "', '" + paymentType + "', " + complimentary + ", '"+section+"', "+roundOff+");";

		return db.executeUpdate(sql, true);
	}

	public boolean addPaymentForVoidOrder(String hotelId, String orderId, BigDecimal foodBill, BigDecimal barBill, 
			String billNo, String section, String orderDate) {

		String sql = "SELECT * FROM Payment WHERE hotelId='" + hotelId + "' AND orderId='" + orderId
				+ "' AND orderDate='" + orderDate + "';";
		Report payment = db.getOneRecord(sql, Report.class, hotelId);

		if (payment != null) {
			sql = "UPDATE Payment SET cashPayment = 0, foodDiscount = 0,  barDiscount = 0, gst = 0, VATBAR = 0, complimentary = 0, loyaltyAmount = 0, cardPayment = 0, appPayment = 0, cardType = 'VOID', total = 0 WHERE orderId = '" + orderId
					+ "' AND hotelID = '" + hotelId + "';";
			return db.executeUpdate(sql, true);
		}
		
		sql = "INSERT INTO Payment (hotelId, billNo, billNo2, orderId, orderDate, foodBill, barBill, total, cardType, foodDiscount, barDiscount, gst, cashPayment, cardPayment, appPayment"
				+ ", VATBAR, complimentary, loyaltyAmount) "
				+ "VALUES('" + hotelId + "', '" + billNo + "', '" + billNo + "', '"
				+ orderId + "', '" + orderDate + "', "
				+ foodBill + ", " + barBill + ", 0, 'VOID',0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);";
		
		return db.executeUpdate(sql, true);
	} 

	@Override
	public boolean updateGuestReference(String hotelId, String orderId, String guest) {
		String sql = "UPDATE Orders SET reference = '" + guest + "', inhouse = "+NON_CHARGEABLE+" WHERE orderId == '"+orderId+"' AND hotelId == '"+hotelId+"';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean updateOrderType(String hotelId, String orderId, int orderType) {
		String sql = "UPDATE Orders SET inhouse = " + orderType + " WHERE orderId == '"+orderId+"' AND hotelId == '"+hotelId+"';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean deletePayment(String hotelId, String orderId) {
		String sql = "DELETE FROM Payment WHERE hotelId == '"+hotelId+"' AND orderId == '"+orderId+"';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean editPayment(String hotelId, String orderId, BigDecimal cashPayment, BigDecimal cardPayment, BigDecimal appPayment,
			BigDecimal walletPayment, String cardType, BigDecimal grandTotal) {

		String sql = "UPDATE Payment SET cashPayment = " + cashPayment 
				+ ", cardPayment = "+ cardPayment 
				+ ", appPayment = " + appPayment 
				+ ", walletPayment = " + walletPayment 
				+ ", creditAmount = 0" 
				+ ", cardType = '" + escapeString(cardType) + "' " + "WHERE orderId = '"
				+ orderId + "' AND hotelId = '" + hotelId + "';";

		IOrder orderDao = new OrderManager(false);
		Order order = orderDao.getOrderById(hotelId, orderId);
		
		if(order.getInHouse() == AccessManager.NON_CHARGEABLE) {
			sql += "UPDATE Payment SET total = " + grandTotal + " WHERE orderId = '"
						+ orderId + "' AND hotelId = '" + hotelId + "';";
		}
		
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean updatePaymentForReturn(String hotelId, String orderId, BigDecimal foodBill, BigDecimal barBill,
			BigDecimal foodDiscount, BigDecimal barDiscount, BigDecimal total, BigDecimal serviceCharge, BigDecimal gst, BigDecimal VATBar, BigDecimal cashPayment,
			BigDecimal cardPayment, BigDecimal appPayment) {

		String sql = "UPDATE Payment SET foodBill = " + foodBill + ", barBill = " + barBill + ", " + "foodDiscount = "
				+ foodDiscount + "barDiscount = " + barDiscount + ", total = " + total + ", serviceCharge = " + serviceCharge 
				+ ", gst = " + gst + ", VATBar = " + VATBar + ", cashPayment = " + cashPayment + ", " + "cardPayment = " 
				+ cardPayment + ", appPayment = " + appPayment + " WHERE orderId = '" + orderId + "' AND hotelID = '" + hotelId + "';";

		return db.executeUpdate(sql, true);
	}

	@Override
	public Report getPayment(String hotelId, String orderId) {
		String sql = "SELECT * FROM PAYMENT WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId + "';";

		return db.getOneRecord(sql, Report.class, hotelId);
	}

	@Override
	public Report getCashCardSales(String hotelId, String serviceDate, String serviceType) {

		String sql = "SELECT ROUND(SUM(Payment.cashPayment)*100)/100 AS cashPayment, "
				+ "ROUND(SUM(Payment.cardPayment)*100)/100 AS cardPayment, "
				+ "ROUND(SUM(Payment.foodBill)*100)/100 AS foodBill, "
				+ "ROUND(SUM(Payment.barBill)*100)/100 AS barBill, Orders.orderId FROM Payment, Orders WHERE Orders.orderDate = '" + serviceDate + "' "
				+ "AND Orders.orderId == Payment.orderId AND Orders.hotelId = '" + hotelId + "' AND Orders.customerGST isNULL "
				+ "AND Orders.serviceType = '" + serviceType + "'  AND Payment.cardType LIKE '%CASH%' AND Orders.state != "+ORDER_STATE_HIDDEN+" ;";

		return db.getOneRecord(sql, Report.class, hotelId);
	}
}
