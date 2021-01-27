package com.orderon.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.commons.ChargeCalculator;
import com.orderon.commons.DiscountCalculator;
import com.orderon.commons.LoyaltyCalculator;
import com.orderon.commons.TaxCalculator;
import com.orderon.interfaces.IOrder;
import com.orderon.interfaces.IOrderItem;
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
	
	@Override
	public JSONObject getCalculatePaymentForOrder(String systemId, String outletId, String orderId) {
		return this.getCalculatePaymentForOrder(systemId, outletId, orderId, new JSONArray());
	}
	
	@Override
	public JSONObject getCalculatePaymentForOrder(String systemId, String outletId, String orderId, JSONArray menuItems) {
		
		JSONObject outObj = new JSONObject();
		IOrderItem dao = new OrderManager(false);
		ArrayList<OrderItem> orderedItems = null;
		
		if(menuItems.length()==0) {
			orderedItems = dao.getOrderedItemsForPayment(systemId, orderId);
		}else {
			orderedItems = dao.getOrderedItemsForPayment(systemId, orderId, menuItems);
		}
		
		IOrder orderDao = new OrderManager(false);
		Order order = orderDao.getOrderById(systemId, orderId);
		
		DiscountCalculator discountCalc = new DiscountCalculator();
		LoyaltyCalculator loyaltyCalc = new LoyaltyCalculator();
		TaxCalculator taxCalc = new TaxCalculator();
		ChargeCalculator chargeCalc = new ChargeCalculator();
		
		BigDecimal itemTotal = new BigDecimal(0.0);
		BigDecimal subTotal = new BigDecimal(0.0);
		BigDecimal grandTotal = new BigDecimal(0.0);
		
		BigDecimal promotionalCash = order.getPromotionalCash();
		BigDecimal totalFoodDiscount = new BigDecimal(0.0);
		BigDecimal totalBarDiscount = new BigDecimal(0.0);
		BigDecimal totalDiscount = new BigDecimal(0.0);
		
		BigDecimal totalLoyalty = new BigDecimal(0.0);

		BigDecimal taxableFoodBill = new BigDecimal(0.0);
		BigDecimal taxableBarBill = new BigDecimal(0.0);
		BigDecimal nonTaxableFoodBill = new BigDecimal(0.0);
		BigDecimal nonTaxableBarBill = new BigDecimal(0.0);

		BigDecimal totalTaxAmount = new BigDecimal(0.0);
		BigDecimal totalChargeAmount = new BigDecimal(0.0);
		
		int barItemCount = 0;
		int foodItemCount = 0;

		BigDecimal foodLoyalty = new BigDecimal(0.0);
		BigDecimal barLoyalty = new BigDecimal(0.0);
		BigDecimal foodBill = new BigDecimal(0.0);
		BigDecimal barBill = new BigDecimal(0.0);
		BigDecimal usedPromoCash = new BigDecimal(0.0);

		BigDecimal gst = new BigDecimal(0.0);
		BigDecimal vat = new BigDecimal(0.0);
		BigDecimal serviceCharge = new BigDecimal(0.0);
		BigDecimal packagingCharge = new BigDecimal(0.0);
		BigDecimal deliveryCharge = new BigDecimal(0.0);
		
		for (OrderItem orderedItem : orderedItems) {
			itemTotal = orderedItem.getFinalAmount();
			subTotal = subTotal.add(itemTotal);
			usedPromoCash = new BigDecimal(0.0);
			
			discountCalc.calculateDiscount(systemId, outletId, order.getDiscountCodes(), orderedItem, itemTotal, order);
			totalFoodDiscount = totalFoodDiscount.add(discountCalc.getFoodDiscount());
			totalBarDiscount = totalBarDiscount.add(discountCalc.getBarDiscount());
			
			totalDiscount = discountCalc.getFoodDiscount()
					.add(discountCalc.getBarDiscount());;
			
			loyaltyCalc.calculateLoyalty(systemId, order.getLoyaltyId(), orderedItem, itemTotal);
			totalLoyalty = totalLoyalty.add(loyaltyCalc.getLoyaltyAmount());
			
			itemTotal = itemTotal.subtract(totalDiscount)
					.subtract(loyaltyCalc.getLoyaltyAmount());
			
			if(promotionalCash.compareTo(itemTotal) == 1) {
				promotionalCash = promotionalCash.subtract(itemTotal);
				usedPromoCash = usedPromoCash.add(itemTotal);
			}else {
				usedPromoCash = usedPromoCash.add(promotionalCash);
				promotionalCash = new BigDecimal(0.0);
			}
			itemTotal = itemTotal.subtract(usedPromoCash);
			if(orderedItem.getTaxes().length() > 0) {
				if(orderedItem.getStation().equals("Bar")) {
					taxableBarBill = taxableBarBill.add(itemTotal);
				}else {
					taxableFoodBill = taxableFoodBill.add(itemTotal);
				}
			}else {
				if(orderedItem.getStation().equals("Bar")) {
					nonTaxableBarBill = nonTaxableBarBill.add(itemTotal);
				}else {
					nonTaxableFoodBill = nonTaxableFoodBill.add(itemTotal);
				}
			}
			
			if(orderedItem.getStation().equals("Bar")) {
				barBill = barBill.add(itemTotal);
				barLoyalty = barLoyalty.add(loyaltyCalc.getLoyaltyAmount());
				barItemCount++;
			}else {
				foodBill = foodBill.add(itemTotal);
				foodLoyalty = foodLoyalty.add(loyaltyCalc.getLoyaltyAmount());
				foodItemCount++;
			}
			
			taxCalc.calculateTax(systemId, outletId, orderedItem, itemTotal, order.getExcludedTaxes());
			chargeCalc.calculateCharge(systemId, outletId, itemTotal, order.getExcludedCharges(), true, order.getOrderTypeStr());
		}
		
		chargeCalc.calculateCharge(systemId, outletId, subTotal, order.getExcludedCharges(), false, order.getOrderTypeStr());
		
		try {
			JSONArray taxDetails = taxCalc.getTaxDetails();
			for (int i = 0; i < taxDetails.length(); i++) {
					totalTaxAmount = totalTaxAmount.add(new BigDecimal(taxDetails.getJSONObject(i).getDouble("amount"))).setScale(2, RoundingMode.HALF_UP);
			}
			
			JSONArray chargeDetails = chargeCalc.getChargeDetails();
			for (int i = 0; i < chargeDetails.length(); i++) {
				totalChargeAmount = totalChargeAmount.add(new BigDecimal(chargeDetails.getJSONObject(i).getDouble("amount"))).setScale(2, RoundingMode.HALF_UP);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			JSONObject tempObj = new JSONObject();
			for (int i = 0; i < taxCalc.getTaxDetails().length(); i++) {
				tempObj = taxCalc.getTaxDetails().getJSONObject(i);
				
				if(tempObj.getString("name").equals("CGST") || tempObj.getString("name").equals("SGST")) {
					gst = gst.add(new BigDecimal(tempObj.getDouble("amount")));
				}else if(tempObj.getString("name").equals("VATBAR") || tempObj.getString("name").equals("VAT")) {
					vat = vat.add(new BigDecimal(tempObj.getDouble("amount")));
				}
			}
			gst = gst.setScale(2, RoundingMode.HALF_UP);
			vat = vat.setScale(2, RoundingMode.HALF_UP);
			
			for (int i = 0; i < chargeCalc.getChargeDetails().length(); i++) {
				tempObj = chargeCalc.getChargeDetails().getJSONObject(i);
				
				if(tempObj.getString("name").equals("SERVICE CHARGE")) {
					serviceCharge = serviceCharge.add(new BigDecimal(tempObj.getDouble("amount")));
				}else if(tempObj.getString("name").equals("PACKAGING CHARGE")) {
					packagingCharge = packagingCharge.add(new BigDecimal(tempObj.getDouble("amount")));
				}else if(tempObj.getString("name").equals("DELIVERY CHARGE")) {
					deliveryCharge = deliveryCharge.add(new BigDecimal(tempObj.getDouble("amount")));
				}
			}
			serviceCharge = serviceCharge.setScale(2, RoundingMode.HALF_UP);
			packagingCharge = packagingCharge.setScale(2, RoundingMode.HALF_UP);
			deliveryCharge = deliveryCharge.setScale(2, RoundingMode.HALF_UP);
		
			outObj.put("foodItemCount", foodItemCount);
			outObj.put("barItemCount", barItemCount);

			outObj.put("subTotal", subTotal);
			
			totalDiscount = totalFoodDiscount
					.add(totalBarDiscount);;
			
			outObj.put("totalDiscountAmount", totalDiscount);
			outObj.put("foodDiscount", totalFoodDiscount);
			outObj.put("barDiscount", totalBarDiscount);
			outObj.put("fixedRupeeDiscount", order.getFixedRupeeDiscount());
			outObj.put("zomatoVoucherDiscount", order.getZomatoVoucherAmount());
			outObj.put("piggyBankDiscount", order.getPiggyBankAmount());

			outObj.put("totalLoyaltyAmount", totalLoyalty);
			outObj.put("foodLoyalty", foodLoyalty);
			outObj.put("barLoyalty", barLoyalty);

			outObj.put("gst", gst);
			outObj.put("vat", vat);
			outObj.put("serviceCharge", serviceCharge);
			outObj.put("packagingCharge", packagingCharge);
			outObj.put("deliveryCharge", deliveryCharge);

			outObj.put("foodBill", foodBill);
			outObj.put("foodTotal", foodBill.subtract(totalFoodDiscount).subtract(foodLoyalty).add(gst)
					.add(serviceCharge).add(packagingCharge).add(deliveryCharge));
			
			outObj.put("barBill", barBill);
			outObj.put("barTotal", barBill.subtract(totalBarDiscount).subtract(barLoyalty).add(vat));

			outObj.put("promotionalCash", order.getPromotionalCash());
			
			outObj.put("taxes", taxCalc.getTaxDetails());
			outObj.put("charges", chargeCalc.getChargeDetails());
			outObj.put("discounts", discountCalc.getDiscounts());
			
			grandTotal = subTotal.subtract(totalDiscount)
					.subtract(totalLoyalty)
					.subtract(order.getPromotionalCash())
					.add(totalTaxAmount)
					.add(totalChargeAmount)
					.setScale(0, RoundingMode.HALF_UP);

			outObj.put("grandTotal", grandTotal);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}
}
