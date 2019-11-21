package com.orderon.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Customer;
import com.orderon.dao.AccessManager.LoyaltyOffer;
import com.orderon.dao.AccessManager.Order;
import com.orderon.dao.AccessManager.OrderAddOn;
import com.orderon.dao.AccessManager.OrderItem;
import com.orderon.dao.AccessManager.Outlet;
import com.orderon.dao.AccessManager.Settings;
import com.orderon.dao.CustomerManager;
import com.orderon.dao.LoyaltyManager;
import com.orderon.dao.OrderManager;
import com.orderon.dao.OutletManager;
import com.orderon.dao.PaymentManager;
import com.orderon.interfaces.ICustomer;
import com.orderon.interfaces.ILoyalty;
import com.orderon.interfaces.IOrder;
import com.orderon.interfaces.IOrderAddOn;
import com.orderon.interfaces.IOrderItem;
import com.orderon.interfaces.IOutlet;
import com.orderon.interfaces.IPayment;

public class BillGenerator {

	final BigDecimal zero = new BigDecimal(0.0);
	
	private StringBuilder out;
	
	private BigDecimal foodBill;
	private BigDecimal grandTotal;
	private BigDecimal chargeAmount;
	private BigDecimal taxAmount;
	
	private String tableSize = "table-xs";
	private String formatedAmount = "";
	private String outletId;
	
	private Settings hotelDetails;
	private Outlet outlet;
	
	
	private JSONObject calculatedPayment;
	
	private ArrayList<OrderItem> orderItems;
	private ArrayList<OrderAddOn> orderAddOns;
	
	private IOrder orderDao;
	private IOrderItem itemDao;
	private IOrderAddOn addOnsDao;
	private IOutlet outletDao;
	private IPayment paymentDao;
	private ILoyalty loyaltyDao;
	
	public BillGenerator(String systemId, String outletId) {
		
		out = new StringBuilder();
		
		foodBill = zero;
		chargeAmount = zero;
		taxAmount = zero;
		grandTotal= zero;
		
		this.outletId = outletId;
		
		loyaltyDao = new LoyaltyManager(false);
		outletDao = new OutletManager(false);
		
		hotelDetails = outletDao.getSettings(outletId);
		outlet = outletDao.getOutletForSystem(systemId, outletId);
		
		calculatedPayment = new JSONObject();
		
		orderItems = new ArrayList<OrderItem>();
		orderAddOns = new ArrayList<OrderAddOn>();
		
		orderDao = new OrderManager(false);
		itemDao = new OrderManager(false);
		addOnsDao = new OrderManager(false);
		outletDao = new OutletManager(false);
		paymentDao = new PaymentManager(false);
	}
	
	private void defineBillHeader(Order order){
		
		String logoPath = "http://"+Configurator.getIp()+"/Images" + "/hotels/" + hotelDetails.getSystemId() + "/logo.png";					
		
		if (order.getState() == 99) {
			out.append("<h3 class='centered'>VOID ORDER</h3>");
		} else if(order.getPrintCount()>1){
			out.append("<h4 class='centered'>**DUPLICATE BILL**</h4>");
		}
		if(hotelDetails.getPrintLogo()){
			out.append("<img src='").append(logoPath).append("' width='70%' style='margin-left:15%;'></img>");
		}else{
			if(hotelDetails.getHasConciseBill()){
				out.append("<h3 class='centered mt0'>").append(outlet.getName()).append("</h3>");
			}else{
				out.append("<h3 class='centered mt-1'>").append(outlet.getName()).append("</h3>");
			}
		}
	
		if (outlet.getCompanyName().length() > 0)
			out.append("<p class='billHeader'>").append(outlet.getCompanyName()).append("</p>");
		//if (hotelDetails.getWebsite().length() > 0)
			//out.append("<p class='billHeader'>").append(hotelDetails.getWebsite()).append("</p>");
	
		out.append("<p class='billHeader' style='margin-left: 20px; margin-right: 20px;'>").append(outlet.getAddress()).append("</p>")
			.append("<p class='billHeader' style='margin-left: 20px; margin-right: 20px;'>").append(outlet.getContact()).append("</p>");
		
		if (outlet.getGstNumber().length() > 0)
			out.append("<p class='billHeader2'>GSTIN: ").append(outlet.getGstNumber()).append("</p>");
		else
			out.append("<p class='billHeader2'>G.S.T NO.: AWAITED</p>");
		if (outlet.getVatNumber().length() > 0)
			out.append("<p class='billHeader2'>VAT: ").append(outlet.getVatNumber()).append("</p>");
		else
			out.append("");
	
		if(order.getOrderType() == 4){
			out.append("<p class='billHeader2'>NON-CHARGEABLE ORDER for ").append(order.getReference()).append("</p>");
		}else if(order.getOrderType() == 1){
			if(outlet.getOutletId().equals("kvd0002")){
				out.append("<table style='width: 90%; padding: 0px; margin: 5px 25px 0px 25px; font-size:18px;'><tbody>")
					.append("<tr><td>INVOICE NO. <b>").append(order.getBillNo()).append("</b></td>")
					.append("<td style='text-align: right; padding-right:10px;'>TABLE NO. <b>").append(order.getTableId()).append("</b></td>")
					.append("</tr></tbody></table>");
			}else{
				out.append("<table style='width: 90%; padding: 0px; margin: 5px 25px 0px 25px; font-size:15px;'><tbody>")
					.append("<tr><td>INVOICE NO. <b>").append(order.getBillNo()).append("</b></td>")
					.append("<td style='text-align: right; padding-right:10px;'>TABLE NO. <b>").append(order.getTableId()).append("</b></td>")
					.append("</tr></tbody></table>");
			}
		}else if(order.getOrderType() == 0){
			out.append("<table style='width: 90%; padding: 0px; margin: 5px 25px 0px 25px; font-size:15px;'><tbody>")
				.append("<tr><td>INVOICE NO. <b>").append(order.getBillNo()).append("</b></td>")
				.append("<td style='text-align: right; padding-right:10px;'>HOME DELIVERY ORDER</td>")
				.append("</tr></tbody></table>");
		}else {
			out.append("<p class='billHeader2'>INVOICE NO: ").append(order.getBillNo()).append("</p>");
		}
	
		if(hotelDetails.getIsCaptainBasedOrdering()){
			out.append("<hr class='eod-hr-small'>")
				.append("<table style='width: 90%; padding: 0px; margin-left: 25px;'><thead>")
				.append("<tr><th>Date</th><th>Time</th><th>Captain</th><th>Pax</th></tr>")
				.append("</thead><tbody>");
		}else{
			out.append("<hr class='eod-hr-small'>")
				.append("<table style='width: 90%; padding: 0px; margin-left: 25px;'><thead>")
				.append("<tr><th>Date</th><th>Time</th><th>Waiter</th><th>Pax</th></tr>")
				.append("</thead><tbody>");
		}
	
		long unixSeconds = order.getOrderDateTime();
		// convert seconds to milliseconds
		Date date = new java.util.Date(unixSeconds*1000L); 
		// the format of your date
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy"); 
		// give a timezone reference for formatting (see comment at the bottom)
		sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
		
		out.append("<tr><td>").append(sdf.format(date)).append("</td><td>");

		sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm"); 
		
		out.append(sdf.format(date)).append("</td>");
	
	
		out.append("<td>").append(order.getWaiterId()).append("</td><td>").append(order.getNumberOfGuests())
			.append("</td></tr>").append("</tbody></table>").append("<hr class='eod-hr-small'>");
	}
	
	private void defineBillFooter(Order order){
	
		if (hotelDetails.getSystemId().equals(("ca0001")))
			out.append("<p class='billHeader2'>WE DELIVER THROUGH SWIGGY AND ZOMATO</p>");
		if (hotelDetails.getSystemId().equals("ka0001"))
			out.append("<p class='billHeader2'>WE DELIVER THROUGH SWIGGY AND ZOMATO</p>")
			.append("<p class='billHeader2'>FOLLOW US @kaffeinemumbai;</p>");
	
		out.append("<p class='billHeader2'>THANK YOU! SEE YOU SOON</p>");
	
		if (order.getOrderType() == 0) {
	
			ICustomer customerDao = new CustomerManager(false);
			Customer customer = customerDao.getCustomerDetails(hotelDetails.getSystemId(), order.getCustomerNumber());
			
			if(customer != null) {
			
				out.append("<p style='text-align: center; margin-top: 5px; margin-left: 25px;'>DELIVERY DETAILS</p>")
					.append("<table class='").append(tableSize).append("' style='padding: 0px; margin-left:20px; width: 90%;'>");
				
				out.append("<tbody><tr><th>CUSTOMER ID</th><th>:</th>").append("<th>")
					.append(customer.getId()).append("</th>");
		
				out.append("<tbody><tr><th>NAME</th><th>:</th>").append("<th>").append(customer.getFullName())
					.append("</th>").append("</tr><th>CONTACT</th><th>:</th>").append("<th>")
					.append(customer.getMobileNumber()).append("</th>").append("</tr><th>ADDRESS</th><th>:</th>")
					.append("<th>").append(customer.getAddress()).append("</th>");
					
				if(order.getRemarks().length() >0){
					out.append("</tr><th>REMARKS</th><th>:</th>").append("<th>").append(order.getRemarks()).append("</th>");
				}
				out.append("</tr></tbody></table>").append("<hr class='eod-hr-small'>");
			}
					
		}else if(order.getOrderType() == 2){
			out.append("<p style='text-align: center; margin-top: 3px;'>")
				.append(OnlineOrderingPortals.getType(order.getTakeAwayType()))
				.append(" ORDER. ID: ").append(order.getReason()).append("</p>");
			
		}
		if(order.getRemarks().length() >0){
			out.append("<p style='text-align: center; margin-top: 5px;'>REMARKS: ").append(order.getRemarks()).append("</p>");
		}
	
		if(hotelDetails.getHasConciseBill()){
			out.append("<p class='billHeader2' style='font-size:10px;'>POWERED BY ORDERON</p>");
		}else{
			out.append("<p class='billHeader2'>POWERED BY ORDERON</p>");
		}
		
		LocalDateTime now = LocalDateTime.now();
		
		out.append("<p class='billHeader2' style='font-size:10px;'>BILL PRINTED ON ")
			.append(now.getDayOfMonth()).append("/").append(now.getMonthValue()).append("/")
			.append(now.getYear()).append(" AT ").append(now.getHour()).append(":").append(now.getMinute()).append(" HRS</p>");
	}
	
	public String makeBill(String orderId) {
		
		Order order = orderDao.getOrderById(outletId, orderId);
		calculatedPayment = paymentDao.getCalculatePaymentForOrder(outletId, orderId);
		
		orderItems = itemDao.getOrderedItemForBill(outletId, orderId, false);
		
		//Page Setup End
		//Print bill
		defineBillHeader(order);
		
		try{
			
			if(calculatedPayment.getInt("foodItemCount") == 0){
				//makeBarBill(order);
				defineBillFooter(order);
				return out.toString();
			}
		
			if(!hotelDetails.getHasConciseBill()){
				tableSize = "table-xss";
				if(calculatedPayment.getDouble("barBill") > 0.0) {
					out.append("<h4 class='billHeader'>FOOD BILL</h4>");
					out.append("<hr class='eod-hr-small'>");
				}
			}
			out.append("<table class='"+tableSize+"' style='padding: 0px; margin-left:20px; width: 90%;'>");
			out.append("<thead><tr><th>PARTICULARS</th><th class='centered'>RATE</th><th class='centered'>QTY</th><th style='text-align: right;'>AMOUNT</th></tr>");
			out.append("</thead><tbody>");
			
			BigDecimal totalPerItem = zero;
			String title = "";
			BigDecimal amount = zero;
	
			for (int i = 0; i < orderItems.size(); i++) {
				if (orderItems.get(i).getStation().equals("Bar")) {
					continue;
				}
				totalPerItem = orderItems.get(i).getFinalAmount().setScale(2, RoundingMode.HALF_UP);
	
				amount = orderItems.get(i).getRate().multiply(new BigDecimal(orderItems.get(i).getQuantity()));
				
				title = orderItems.get(i).getState() == 50 ? "Comp. " + orderItems.get(i).getTitle() : orderItems.get(i).getTitle();
				if (orderItems.get(i).getState() == 100)
					continue;
				else if (orderItems.get(i).getState() == 101)
					continue;
				else if (orderItems.get(i).getState() == 99 && order.getState() != 99)
					continue;
				else if (orderItems.get(i).getState() == 99 && order.getState() == 99)
					totalPerItem = zero;
	
				out.append("<tr class='padtb-0'><td>").append(title.toUpperCase());
				out.append("</td><td class='centered'>").append(orderItems.get(i).getRate());
				out.append("</td><td class='centered'>").append(orderItems.get(i).getQuantity());
				out.append("</td><td style='text-align:right;'>").append(formatDecimal(totalPerItem)).append("</td></tr>");
	
				orderAddOns = addOnsDao.getOrderedAddOns(outletId, orderId, orderItems.get(i).getMenuId(), false);
				for (int x = 0; x < orderAddOns.size(); x++) {
					amount = orderAddOns.get(x).getRate().multiply(new BigDecimal(orderAddOns.get(x).getQuantity()));
					
					title = orderAddOns.get(x).getState() == 50 ? "Comp. " + orderAddOns.get(x).getName() : orderAddOns.get(x).getName();
					
					out.append("<tr><td class='addOn-td' style='padding-left:30px '>- ");
					out.append(orderAddOns.get(x).getName());
					out.append("</td><td class='addOn-td' style='text-align: center;'>");
					out.append(orderAddOns.get(x).getRate());
					out.append("</td><td class='addOn-td' style='text-align: center;''>");
					out.append(orderAddOns.get(x).getQuantity());
					out.append("</td><td class='addOn-td style='text-align: right;'>");
					out.append(formatDecimal(amount)).append("</td></tr>");
				}
			}
	
			out.append("</tbody></table>");
			out.append("<hr class='eod-hr-small'>");
			out.append("<table class='"+tableSize+"' style='padding: 0px; margin-left:20px; width: 90%;'>");
	
			BigDecimal taxAmount = new BigDecimal(calculatedPayment.getDouble("gst"));
			BigDecimal chargeAmount = zero;
			
			BigDecimal serviceCharge = new BigDecimal(calculatedPayment.getDouble("serviceCharge")).setScale(2, RoundingMode.HALF_UP);
			BigDecimal packagingCharge = new BigDecimal(calculatedPayment.getDouble("packagingCharge")).setScale(2, RoundingMode.HALF_UP);
			BigDecimal deliveryCharge = new BigDecimal(calculatedPayment.getDouble("deliveryCharge")).setScale(2, RoundingMode.HALF_UP);
	
			BigDecimal foodDiscount = new BigDecimal(calculatedPayment.getDouble("foodDiscount")).setScale(2, RoundingMode.HALF_UP);
			BigDecimal foodLoyalty = new BigDecimal(calculatedPayment.getDouble("foodLoyalty")).setScale(2, RoundingMode.HALF_UP);

			grandTotal = new BigDecimal(calculatedPayment.getDouble("grandTotal")).setScale(2, RoundingMode.HALF_UP);

			foodBill = new BigDecimal(calculatedPayment.getDouble("foodBill")).setScale(2, RoundingMode.HALF_UP);
			BigDecimal foodTotal = new BigDecimal(calculatedPayment.getDouble("foodTotal")).setScale(2, RoundingMode.HALF_UP);
			
			chargeAmount = serviceCharge.add(deliveryCharge).add(packagingCharge);
			
			if(taxAmount.compareTo(zero) == 1 || chargeAmount.compareTo(zero) == 1 || foodDiscount.compareTo(zero) == 1 || foodLoyalty.compareTo(zero) == 1){
				formatedAmount = formatDecimal(foodBill);
				
				if(!hotelDetails.getHasConciseBill()){
					out.append("<tbody><tr><td class='w1'>BASIC AMOUNT</td><td class='w2'></td><td class='w2'></td>");
					out.append("<td class='w3' style='text-align:right;'>").append(formatedAmount);
					out.append("</td></tr></tbody></table>");
				}else{
					out.append("<thead><tr><th class='w1'>BASIC AMOUNT</th><th class='w2'></th><th class='w2'></th>");
					out.append("<th class='w3' style='text-align:right;'>").append(formatedAmount);
					out.append("</tr></thead></table>");
					out.append("</th><hr class='eod-hr-small'>");
				}
				out.append("<table class='").append(tableSize).append("' style='padding: 0px; margin-left:20px; width: 90%;'>");
			}
	
			if (foodDiscount.compareTo(zero) == 1 || foodLoyalty.compareTo(zero) == 1){
	
				if(foodDiscount.compareTo(zero) == 1){
					JSONArray discounts = calculatedPayment.getJSONArray("discounts");
					JSONObject discountObj = new JSONObject();
					String discountPercent = "";
					BigDecimal discountValue = zero;
					
					for(int j=0; j<discounts.length(); j++) {
						discountObj = discounts.getJSONObject(j);
						if(discountObj.getInt("type")==0){
							discountPercent += discountObj.getInt("foodValue") + "%,";
						}else{
							discountValue = discountValue.add(new BigDecimal(discountObj.getInt("foodValue")));
						}
					}
					formatedAmount = formatDecimal(foodDiscount.subtract(discountValue));
					if(discountPercent.length()>0){
						discountPercent = discountPercent.substring(0, discountPercent.length());
						out.append("<tbody style='color:white; background:black;'><tr><td class='w1'>LESS DISCOUNT @").append(discountPercent)
						.append("</td><td class='w2'></td><td class='w2'></td><td class='w3' style='text-align:right;'>")
						.append(formatedAmount).append("</td></tr>");
					}
					formatedAmount = formatDecimal(discountValue);
					if(discountValue.compareTo(zero) == 1){
						out.append("<tbody style='color:white; background:black;'><tr><td class='w1'>LESS DISCOUNT @ &#x20b9")
						.append("</td><td class='w2'></td><td class='w2'></td><td class='w3' style='text-align:right;'>")
						.append(formatedAmount).append("</td></tr>");
					}
					
					if(foodLoyalty.compareTo(zero) == 0){
						out.append("</tbody></table>").append("<hr class='eod-hr-small'>").append("<table class='")
						.append(tableSize).append("' style='padding: 0px; margin-left:20px; width: 90%;'>");
					}
				}
				if(foodLoyalty.compareTo(zero) == 1){
					formatedAmount = formatDecimal(foodLoyalty);
					if(foodDiscount.compareTo(zero) == 0){
						out.append("<tbody>");
					}
					int loyaltyId = order.getLoyaltyId();
					LoyaltyOffer loyalty = loyaltyDao.getLoyaltyOfferById(outletId, loyaltyId);
					
					out.append("<tr><td class='w1'>LOYALTY:").append(loyalty.getName())
						.append("</td><td class='w2'></td><td class='w2'></td><td class='w3' style='text-align:right;'>")
						.append(formatedAmount)
						.append("</td></tr>")
						.append("</tbody></table>")
						.append("<hr class='eod-hr-small'>")
						.append("<table class='").append(tableSize)
						.append("' style='padding: 0px; margin-left:20px; width: 90%;'>");
				}
				if(taxAmount.compareTo(zero) == 1){
					formatedAmount = formatDecimal(foodBill);
	
					out.append("<thead><tr><th class='w1'>SUB TOTAL</th><th class='w2'></th><th class='w2'></th>")
						.append("<th class='w3' style='text-align:right;'>").append(formatedAmount).append("</th>")
						.append("</tr></thead></table>")
						.append("<hr class='eod-hr-small'>")
						.append("<table class='")
						.append(tableSize).append("' style='padding: 0px; margin-left:20px; width: 90%;'>");
				}
			}
			//Charges
			boolean isCharge = false;
			
			JSONArray chargesForFoodOrder = calculatedPayment.getJSONArray("charges");
			JSONObject chargeObj;
			
			for (int x = 0; x <chargesForFoodOrder.length(); x++) {
	
				if(chargeAmount.compareTo(zero) == 0){
					break;
				}
				chargeObj = chargesForFoodOrder.getJSONObject(x);
				isCharge = true;
				out.append("<tbody>");
	
				BigDecimal curChargeAmount = new BigDecimal(chargeObj.getDouble("amount"));
				
				formatedAmount = formatDecimal(curChargeAmount);
				if (curChargeAmount.compareTo(zero) == 1){
					String chargeType = chargeObj.getString("type") == "PERCENTAGE" ? "%" : "&#8377";
					
					out.append("<tr><td class='w1' colspan='3'>").append(chargeObj.getString("name"))
						.append(" @ ").append(chargeObj.getDouble("value")).append(chargeType)
						.append("</td><td class='w3' style='text-align:right;'>")
						.append(formatedAmount).append("</td></tr>");
				}
			}
			if (isCharge){
				out.append("</tbody></table>")
					.append("<hr class='eod-hr-small'>").append("<table class='")
					.append(tableSize).append("' style='padding: 0px;margin-left:20px; width: 90%;'>");
			}
	
			//Taxes
			int offset = 0;
			boolean isTax = false;
	
			JSONArray taxesForFoodOrder = calculatedPayment.getJSONArray("taxes");
			JSONObject taxObj;
			
			for (int x = 0; x <taxesForFoodOrder.length(); x++) {
	
				if(taxAmount.compareTo(zero) == 0){
					break;
				}
				taxObj = taxesForFoodOrder.getJSONObject(x);
				isTax = true;
	
				BigDecimal curTaxAmount = new BigDecimal(taxObj.getDouble("amount"));
				
				if (offset == 0 && !!hotelDetails.getHasConciseBill()){
					formatedAmount = formatDecimal(new BigDecimal(taxObj.getDouble("taxableTotal")).setScale(2, RoundingMode.HALF_UP));
					out.append("<thead><tr><th class='w1'>TAXABLE AMOUNT</th><th class='w2'></th><th class='w2'></th>")
						.append("<th class='w3' style='text-align:right;'>").append(formatedAmount).append("</th>")
						.append("</tr></thead><tbody>");
				}
	
				formatedAmount = "";
				formatedAmount = formatDecimal(curTaxAmount);
				out.append("<tr><td class='w1'>").append(taxObj.getString("name")).append(" @ ").append(taxObj.getDouble("value")).append("%")
					.append("</td><td class='w2'></td><td class='w2'></td><td class='w3' style='text-align:right;'>").append(formatedAmount)
					.append("</td></tr>");
				offset++;
			}
			if (isTax){
				out.append("</tbody></table>").append("<hr class='eod-hr-small'>")
					.append("<table class='")
					.append(tableSize)
					.append("' style='padding: 0px;margin-left:20px; width: 90%;'>");
			}
			
			if(calculatedPayment.getInt("barItemCount") == 0){
				out.append("<thead><tr><th class='w1'>GRAND TOTAL (ROUNDED OFF)</th><th class='w2'></th><th class='w2'></th>");
				
				formatedAmount = formatDecimal(grandTotal);
				if(order.getOrderType() == 4){
					formatedAmount = "0.0";
				}
				out.append("<th class='w3' style='text-align:right; font-size:20px;'>").append(formatedAmount).append("</th>");
			}else{
				out.append("<thead><tr><th class='w1'>FOOD TOTAL</th><th class='w2'></th><th class='w2'></th>");
				formatedAmount = formatDecimal(foodTotal);
				out.append("<th class='w3' style='text-align:right;'>").append(formatedAmount).append("</th>");
			}
			
			out.append("</tr></thead></table>")
				.append("<hr class='eod-hr-small'>");
			
			if (!order.getCustomerGst().isEmpty())
				out.append("<p class='centered' style='margin-bottom:10px; margin-top:5px;'>Customer GSTIN : ").append(order.getCustomerGst()).append("</p>");
	
			if(calculatedPayment.getInt("barItemCount") == 0){
				defineBillFooter(order);
			}
			
			if(calculatedPayment.getInt("barItemCount") == 1){
				//makeBarBill(order);
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		
		return out.toString();
	}

	/*private String makeBarBill(Order order) {
		
		if(!hotelDetails.getHasConciseBill()){
			if(foodBill.compareTo(zero) == 1)
				out.append( "<h4 class='billHeader'>BAR BILL</h4><hr class='eod-hr-small'>");
		
			out.append("<table class='").append(tableSize).append("' style='padding: 0px; margin-left:20px; width: 90%;'>")
				.append("<thead><tr><th>PARTICULARS</th><th class='centered'>RATE</th><th class='centered'>QTY</th><th style='text-align:right;'>AMOUNT</th></tr>")
				.append("</thead><tbody>");
		}else{
			out.append("<table class='"+tableSize+"' style='padding: 0px; margin-left:20px; width: 90%;'><tbody>");
		}

		BigDecimal barDiscount = new BigDecimal(calculatedPayment.getDouble("barDiscount")).setScale(2, RoundingMode.HALF_UP);
		BigDecimal barLoyalty = new BigDecimal(calculatedPayment.getDouble("barLoyalty")).setScale(2, RoundingMode.HALF_UP);
		
		BigDecimal barBill = new BigDecimal(calculatedPayment.getDouble("barBill")).setScale(2, RoundingMode.HALF_UP);
		BigDecimal barTotal = new BigDecimal(calculatedPayment.getDouble("barTotal")).setScale(2, RoundingMode.HALF_UP);

		BigDecimal totalPerItem = zero;
		String title = "";
		BigDecimal amount = zero;

		for (int i = 0; i < orderItems.size(); i++) {
			if (!orderItems.get(i).getStation().equals("Bar")) {
				continue;
			}
			totalPerItem = orderItems.get(i).getFinalAmount().setScale(2, RoundingMode.HALF_UP);

			amount = orderItems.get(i).getRate().multiply(new BigDecimal(orderItems.get(i).getQty()));
			
			title = orderItems.get(i).getState() == 50 ? "Comp. " + orderItems.get(i).getTitle() : orderItems.get(i).getTitle();
			if (orderItems.get(i).getState() == 100)
				continue;
			else if (orderItems.get(i).getState() == 101)
				continue;
			else if (orderItems.get(i).getState() == 99 && order.getState() != 99)
				continue;
			else if (orderItems.get(i).getState() == 99 && order.getState() == 99)
				totalPerItem = zero;

			out.append("<tr class='padtb-0'><td>").append(title.toUpperCase());
			out.append("</td><td class='centered'>").append(orderItems.get(i).getRate());
			out.append("</td><td class='centered'>").append(orderItems.get(i).getQty());
			out.append("</td><td style='text-align:right;'>").append(formatDecimal(totalPerItem)).append("</td></tr>");

			orderAddOns = addOnsDao.getOrderedAddOns(outletId, order.getOrderId(), orderItems.get(i).getMenuId(), false);
			for (int x = 0; x < orderAddOns.size(); x++) {
				amount = orderAddOns.get(x).getRate().multiply(new BigDecimal(orderAddOns.get(x).getQty()));
				
				title = orderAddOns.get(x).getState() == 50 ? "Comp. " + orderAddOns.get(x).getName() : orderAddOns.get(x).getName();
				
				out.append("<tr><td class='addOn-td' style='padding-left:30px '>- ");
				out.append(orderAddOns.get(x).getName());
				out.append("</td><td class='addOn-td' style='text-align: center;'>");
				out.append(orderAddOns.get(x).getRate());
				out.append("</td><td class='addOn-td' style='text-align: center;''>");
				out.append(orderAddOns.get(x).getQty());
				out.append("</td><td class='addOn-td style='text-align: right;'>");
				out.append(formatDecimal(amount)).append("</td></tr>");
			}
		}

		out.append("</tbody></table>");
		out.append("<hr class='eod-hr-small'>");
		out.append("<table class='"+tableSize+"' style='padding: 0px; margin-left:20px; width: 90%;'>");
		
		
		BigDecimal taxAmount = new BigDecimal(calculatedPayment.getDouble("vat"));
		
		
		if(taxAmount.compareTo(zero) == 1 || chargeAmount.compareTo(zero) == 1 || barDiscount.compareTo(zero) == 1 || barLoyalty.compareTo(zero) == 1){
			
			if(!settings.getHasConciseBill()){
				out.append("<tbody><tr><td class='w1'>BASIC AMOUNT</td><td class='w2'></td><td class='w2'></td>");
				out.append("<td class='w3' style='text-align:right;'>").append(formatedAmount);
				out.append("</td></tr></tbody></table>");
			}else{
				out.append("<thead><tr><th class='w1'>BASIC AMOUNT</th><th class='w2'></th><th class='w2'></th>");
				out.append("<th class='w3' style='text-align:right;'>").append(formatedAmount);
				out.append("</tr></thead></table>");
				out.append("</th><hr class='eod-hr-small'>");
			}
			out.append("<table class='").append(tableSize).append("' style='padding: 0px; margin-left:20px; width: 90%;'>");
		}
		barTotal = barBill;
		if (barDiscount.compareTo(zero) == 1 || barLoyalty.compareTo(zero) == 1) {
			barTotal = Math.round((barBill - barDiscount - barLoyalty) * 100) / 100;

			if(barDiscount.compareTo(zero) == 1){

				let discountPercent = '';
				let discountValue = 0;
				let discounts = order.discounts;
				discounts = order.discounts;
				discounts.forEach(discount => {
					if(discount.type==0){
						discountPercent += discount.barValue + '%,';
					}else{
						discountValue += discount.barValue;
					}
				});
				formatedAmount = formatDecimal(barDiscount-discountValue);
				if(discountPercent.length>0){
					discountPercent = discountPercent.slice(0, -1);
					out.append("<tbody style='color:white; background:black;'><tr><td class='w1'>LESS DISCOUNT @").append(discountPercent
						+	"</td><td class='w2'></td><td class='w2'></td><td class='w3' style='text-align:right;'>").append(formatedAmount
						+	"</td></tr>"
				}
				formatedAmount = formatDecimal(discountValue);
				if(discountValue.compareTo(zero) == 1){
					out.append("<tbody style='color:white; background:black;'><tr><td class='w1'>LESS DISCOUNT @ &#x20b9").append(discountValue).append(''
						+	"</td><td class='w2'></td><td class='w2'></td><td class='w3' style='text-align:right;'>").append(formatedAmount
						+	"</td></tr>"
				}
				
				if(barLoyalty.compareTo(zero) == 0){
					out	.append("</tbody></table>"
						+ "<hr class='eod-hr-small'>";
				}
			}
			if(barLoyalty.compareTo(zero) == 1){
				formatedAmount = formatDecimal(barLoyalty);
				if(barDiscount.compareTo(zero) == 0){
					out.append('<tbody>';
				}
				let loyalty = order.loyalty;
				out.append("<tr><td class='w1'>LOYALTY:").append(loyalty.name
					+ "</td><td class='w2'></td><td class='w2'></td>"
					+ "<td class='w3' style='text-align:right;'>").append(formatedAmount
					+ "</td></tr>"
					+ "</tbody></table>"
					+ "<hr class='eod-hr-small'>";
			}
			if(taxAmount.compareTo(zero) == 1){
				formatedAmount = formatDecimal(barTotal);

				out.append("<table class='"+tableSize+"' style='padding: 0px; margin-left:20px; width: 90%;'>"
					+ "<thead><tr><th class='w1'>SUB TOTAL</th><th class='w2'></th><th class='w2'></th>"
					+ "<th class='w3' style='text-align:right;'>").append(formatedAmount).append("</th>"
					+ "</tr></thead></table>"
					+ "<hr class='eod-hr-small'>";
			}
			out.append("<table class='"+tableSize+"' style='padding: 0px; margin-left:20px; width: 90%;'>";
		}
		
		//Charges
		let isCharge = false;
		for (let key in chargesForBarOrder) {

			if(chargeAmount == 0){
				break;
			}
			isCharge = true;
			out.append("<tbody>";

			formatedAmount = formatDecimal(Math.round(chargesForBarOrder[key].amount * 100) / 100);
			if (parseInt(chargesForBarOrder[key].amount) > 0){
				let chargeType = chargesForBarOrder[key].type == 'PERCENTAGE' ? '%' : '&#8377';
				out.append("<tr><td class='w1' colspan='3'>").append(chargesForBarOrder[key].name).append(" @ ").append(chargesForBarOrder[key].value).append(chargeType
					+ "</td><td class='w3' style='text-align:right;'>").append(formatedAmount
					+ "</td></tr>";
			}
		}
		if (isCharge){
			out.append("</tbody></table>"
				+ "<hr class='eod-hr-small'>"
				+ "<table class='"+tableSize+"' style='padding: 0px;margin-left:20px; width: 90%;'>";
			barTotal = chargeAmount + barTotal;
		}else{
			barTotal = chargeAmount + barTotal;
		}
		//Taxes
		let offset = 0;
		let isTax = false;
		
		for (let key in taxesForBarOrder) {

			if(taxAmount == 0){
				break;
			}
			isTax = true;
			if (offset == 0 && !!settings.getHasConciseBill()){
				formatedAmount = formatDecimal(taxesForBarOrder[key].taxableTotal);
				out.append("<thead><tr><th class='w1'>TAXABLE AMOUNT</th><th class='w2'></th><th class='w2'></th>"
					+ "<th class='w3' style='text-align:right;'>").append(formatedAmount).append("</th>"
					+ "</tr></thead><tbody>";
			}

			formatedAmount = 0;
			formatedAmount = formatDecimal(Math.round(taxesForBarOrder[key].amount * 100) / 100);
			out.append("<tr><td class='w1'>").append(taxesForBarOrder[key].name).append(" @ ").append(taxesForBarOrder[key].value).append("%"
				+ "</td><td class='w2'></td><td class='w2'></td><td class='w3' style='text-align:right;'>").append(formatedAmount
				+ "</td></tr>";
			offset++;
		}
		if (isTax){
			out.append("</tbody></table>"
				+ "<hr class='eod-hr-small'>"
				+ "<table class='"+tableSize+"' style='padding: 0px;margin-left:20px; width: 90%;'>";
			barTotal = taxAmount + barTotal;
		}else{
			barTotal = taxAmount + barTotal;
		}

		grandTotal += barTotal
		formatedAmount = formatDecimal(barTotal);
		
		out.append("<thead><tr><th class='w1'>BAR TOTAL</th><th class='w2'></th><th class='w2'></th>"
			+ "<th class='w3' style='text-align:right;'>").append(formatedAmount).append("</th>"
			+ "</tr></thead></table>"
			+ "<hr class='eod-hr-small'>";

		let finalAmount = Math.round(grandTotal);
		formatedAmount = formatDecimal(finalAmount);
		if(order.inhouse == 4){
			formatedAmount = '0.0';
		}

		out.append("<table class='"+tableSize+"' style='padding: 0px; margin-left:20px; width: 90%;'>"
			+ "<thead><tr><th class='w1'>GRAND TOTAL (ROUNDED OFF)</th><th class='w2'></th><th class='w2'></th>"
			+ "<th class='w3' style='text-align:right; font-size:20px;'>").append(formatedAmount).append("</th>"
			+ "</tr></thead></table>"
			+ "<hr class='eod-hr-small'>";
	}*/

	private String formatDecimal(BigDecimal number) {
		number = number.setScale(2, RoundingMode.HALF_UP);
		String[] split = number.toString().split("\\.");

		if (split.length == 1)
			return number.toString() + ".00";
		else if (Integer.parseInt(split[1]) > 9 || split[1].length()==2)
			return number.toString();
		else{
			return number.toString() + "0";
		}
	}
}
