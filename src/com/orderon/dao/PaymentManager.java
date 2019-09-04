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
	public ArrayList<Report> getPaymentDetails(String systemId, String outletId, String sDate, String eDate) {
		String sql = "SELECT * FROM Payments WHERE outletId= '" + outletId + "' AND orderDate BETWEEN '" + sDate
				+ "' AND '" + eDate + "'";
		return db.getRecords(sql, Report.class, systemId);
	}

	@Override
	public BigDecimal getTotalCashIn(String systemId, String outletId, String serviceDate) {
		String sql = "SELECT SUM(cashPayment) FROM Payments WHERE outletId= '" + outletId + "' AND orderDate = '"
				+ serviceDate.replace("/", "-") + "';";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, systemId);

		return entity.getId();
	}

	@Override
	public boolean addPayment(String systemId, String outletId, String orderId, BigDecimal foodBill, BigDecimal barBill, BigDecimal foodDiscount,
			BigDecimal barDiscount, BigDecimal loyalty, BigDecimal total, BigDecimal serviceCharge, BigDecimal packagingCharge, 
			BigDecimal deliveryCharge, BigDecimal gst, BigDecimal vat, BigDecimal tip, BigDecimal cashPayment,
			BigDecimal cardPayment, BigDecimal appPayment, BigDecimal walletPayment, BigDecimal creditAmount, BigDecimal promotionalCash,
			String paymentType, BigDecimal complimentary, String section) {

		if(paymentType.equals("")) {
			return false;
		}
		
		paymentType = paymentType.toUpperCase().replace(' ', '_');
		
		IService serviceDao = new ServiceManager(false);
		String orderDate = serviceDao.getServiceDate(systemId);
		BigDecimal roundOff = new BigDecimal("0");
		
		if(!(paymentType.equals("VOID") || paymentType.equals("NON_CHARGEABLE"))) {
			roundOff = cashPayment.add(cardPayment).add(appPayment).add(walletPayment).add(creditAmount).add(promotionalCash).subtract(total);
		}
		
		if(paymentType.equals("NON_CHARGEABLE")) {
			total = new BigDecimal("0.0");
			serviceCharge = new BigDecimal("0.0");
			packagingCharge = new BigDecimal("0.0");
			foodDiscount = new BigDecimal("0.0");
			barDiscount = new BigDecimal("0.0");
			deliveryCharge = new BigDecimal("0.0");
			gst = new BigDecimal("0.0");
			vat = new BigDecimal("0.0");
		}

		String sql = "SELECT * FROM Payments WHERE outletId= '" + outletId + "' AND orderId='" + orderId
				+ "' AND orderDate='" + orderDate + "';";
		//add app payment type.
		Report payment = db.getOneRecord(sql, Report.class, systemId);

		if (payment != null)
			return false;

		IOrder orderDao = new OrderManager(false);
		Order order = orderDao.getOrderById(systemId, orderId);
		
		sql = "INSERT INTO Payments (systemId, outletId, billNo, billNo2, orderId, orderDate, foodBill, barBill, foodDiscount, barDiscount, loyaltyAmount, total, "
				+ "serviceCharge, packagingCharge, deliveryCharge, gst, vat, tip, cashPayment, cardPayment, appPayment, walletPayment, "
				+ "creditAmount, promotionalCash, discountCodes, paymentType, complimentary, section, roundOff) "
				+ "VALUES('" + systemId + "', '" + outletId + "', '" + order.getBillNo() + "', '" + order.getBillNo() + "', '" + orderId + "', '" + orderDate + "', "
				+ foodBill + ", " + barBill + ", " + foodDiscount + ", "+ barDiscount + ", " + loyalty + ", "+ total + ", " + serviceCharge + ", " 
				+ packagingCharge+ ", " + deliveryCharge+ ", " + gst + ", " + vat + ", "+ tip + ", " + cashPayment + ", " 
				+ cardPayment + ", " + appPayment + ", " + walletPayment+ ", " + creditAmount+ ", " + promotionalCash+ ", '" 
				+ order.getDiscountCodes() + "', '" + paymentType + "', " + complimentary + ", '"+section+"', "+roundOff+");";

		return db.executeUpdate(sql, systemId, true);
	}

	public boolean addPaymentForVoidOrder(String systemId, String outletId, String orderId, BigDecimal foodBill, BigDecimal barBill, 
			String billNo, String section, String orderDate) {

		String sql = "SELECT * FROM Payments WHERE outletId= '" + outletId + "' AND orderId='" + orderId
				+ "' AND orderDate='" + orderDate + "';";
		Report payment = db.getOneRecord(sql, Report.class, systemId);

		if (payment != null) {
			sql = "UPDATE Payments SET cashPayment = 0, foodDiscount = 0,  barDiscount = 0, gst = 0, vat = 0, complimentary = 0, loyaltyAmount = 0, cardPayment = 0, appPayment = 0, paymentType = 'VOID', total = 0 WHERE orderId = '" + orderId
					+ "' AND outletId= '" + outletId + "';";
			return db.executeUpdate(sql, systemId, true);
		}
		
		sql = "INSERT INTO Payments (systemId, outletId, billNo, billNo2, orderId, orderDate, foodBill, barBill, total, paymentType, foodDiscount, barDiscount, gst, cashPayment, cardPayment, appPayment"
				+ ", vat, complimentary, loyaltyAmount) "
				+ "VALUES('" + systemId + "', '" + outletId + "', '" + billNo + "', '" + billNo + "', '"
				+ orderId + "', '" + orderDate + "', "
				+ foodBill + ", " + barBill + ", 0, 'VOID',0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);";
		
		return db.executeUpdate(sql, systemId, true);
	} 

	@Override
	public boolean makeOrderNonChargeable(String systemId, String outletId, String orderId, String guest) {
		String sql = "UPDATE Orders SET reference = '" + guest + "', orderType = "+NON_CHARGEABLE+" WHERE orderId == '"+orderId+"' AND outletId= '" + outletId+"';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean updateOrderType(String systemId, String outletId, String orderId, int orderType) {
		String sql = "UPDATE Orders SET orderType = " + orderType + " WHERE orderId == '"+orderId+"' AND outletId= '" + outletId+"';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean deletePayment(String systemId, String outletId, String orderId) {
		String sql = "DELETE FROM Payments WHERE outletId= '" + outletId+"' AND orderId == '"+orderId+"';";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean editPayment(String systemId, String outletId, String orderId, BigDecimal cashPayment, BigDecimal cardPayment, BigDecimal appPayment,
			BigDecimal walletPayment, String paymentType, BigDecimal grandTotal) {

		paymentType = paymentType.toUpperCase().replace(' ', '_');
		String nc = "";
		if(paymentType.equals("NON_CHARGEABLE")) {
			nc = ", total = 0.0";
		}
		String sql = "UPDATE Payments SET cashPayment = " + cashPayment 
				+ ", cardPayment = "+ cardPayment 
				+ ", appPayment = " + appPayment 
				+ ", walletPayment = " + walletPayment 
				+ ", creditAmount = 0" 
				+ nc
				+ ", paymentType = '" + escapeString(paymentType) + "' " + "WHERE orderId = '"
				+ orderId + "' AND outletId= '" + outletId + "';";
		
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Report getPayment(String systemId, String outletId, String orderId) {
		String sql = "SELECT * FROM Payments WHERE outletId= '" + outletId + "' AND orderId = '" + orderId + "';";

		return db.getOneRecord(sql, Report.class, systemId);
	}

	@Override
	public Report getCashCardSales(String systemId, String serviceDate, String serviceType) {

		String sql = "SELECT ROUND(SUM(Payments.cashPayment)*100)/100 AS cashPayment, "
				+ "ROUND(SUM(Payments.cardPayment)*100)/100 AS cardPayment, "
				+ "ROUND(SUM(Payments.foodBill)*100)/100 AS foodBill, "
				+ "ROUND(SUM(Payments.barBill)*100)/100 AS barBill, Orders.orderId FROM Payments, Orders WHERE Orders.orderDate = '" + serviceDate + "' "
				+ "AND Orders.orderId == Payments.orderId AND Orders.customerGST isNULL "
				+ "AND Orders.serviceType = '" + serviceType + "'  AND Payments.paymentType LIKE '%CASH%' AND Orders.state != "+ORDER_STATE_HIDDEN+" ;";

		return db.getOneRecord(sql, Report.class, systemId);
	}

	public boolean rectifyZamatoPayment(String systemId, String outletId, String orderId, BigDecimal total, BigDecimal discountAmount, BigDecimal gst,
			BigDecimal appPayment, String discountCodes) {

		String sql = "UPDATE Payments SET total = " + total 
				+ ", discountCodes = '"+ discountCodes 
				+ "', foodDiscount = " + discountAmount 
				+ ", gst = " + gst 
				+ ", appPayment = "+ total 
				+ ", roundOff = 0 "
				+ ", packagingCharge = 0 WHERE orderId = '"
				+ orderId + "' AND outletId= '" + outletId + "';";

		return db.executeUpdate(sql, systemId, true);
	}

}
