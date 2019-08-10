package com.orderon.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.commons.Configurator;
import com.orderon.dao.AccessManager.OrderItem;
import com.orderon.dao.AccessManager.Outlet;
import com.orderon.dao.DiscountManager;
import com.orderon.dao.OrderManager;
import com.orderon.dao.OutletManager;
import com.orderon.interfaces.IDiscount;
import com.orderon.interfaces.IOrderItem;
import com.orderon.interfaces.IOutlet;

@Path("/Ewards/Services")
public class EwardsServices {

	private final String customerKey = "ffab123";
	private final int merchantId = 7804;
	
	@GET
	@Path("/v1/heartbeat")
	@Produces(MediaType.APPLICATION_JSON)
	public String hearbeat() {
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("hotelId", Configurator.getOutletId());
			outObj.put("ip", Configurator.getIp());
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/customerCheck")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String customerCheck(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("mobileNumber")) {
				outObj.put("message", "Ewards: Mobile Number not found.");
				return outObj.toString();
			}
			
			JSONObject urlParameters = new JSONObject();
			urlParameters.put("customer_key", customerKey);
			urlParameters.put("merchant_id", merchantId);
			urlParameters.put("customer_mobile", inObj.getString("mobileNumber"));
			
			JSONObject response = new JSONObject(Services.executePost("http://13.127.190.250/api/v1/merchant/posCustomerCheck", urlParameters));
			
			System.out.println(response);
			
			JSONObject internalResponse = null;
			if(response.has("status_code")) {
				int statusCode = response.getInt("status_code");
				internalResponse = response.getJSONObject("response");
				if(statusCode == 200) {
					outObj.put("customerDetails", internalResponse.getJSONObject("details"));
					outObj.put("rewards", internalResponse.getJSONArray("rewards"));
					outObj.put("status", true);
				}else {
					outObj.put("message", internalResponse.getString("message"));
				}
			}else {
				outObj.put("message", "Ewards: No respone received from EWards.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v1/addCustomer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addCustomer(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IOutlet dao = new OutletManager(false);
		
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("mobileNumber")) {
				outObj.put("message", "Ewards: Mobile Number not found.");
				return outObj.toString();
			}else if(!inObj.has("name")) {
				outObj.put("message", "Ewards: Customer name not found.");
				return outObj.toString();
			}

			Outlet outlet = dao.getOutlet(inObj.getString("outletId"));
			JSONObject location = outlet.getLocation();
			String city = location.has("city")?location.getString("city"):"";
			String state = location.has("state")?location.getString("state"):"";
			
			JSONObject customerData = new JSONObject();
			customerData.put("name", inObj.getString("name"));
			customerData.put("mobile", inObj.getString("mobileNumber"));
			customerData.put("address", inObj.has("address")?inObj.getString("address"):"");
			customerData.put("email", inObj.has("emailId")?inObj.getString("emailId"):"");
			customerData.put("city", city);
			customerData.put("dob", inObj.has("dob")?inObj.getString("dob"):"");
			customerData.put("state", state);
			
			JSONObject urlParameters = new JSONObject();
			urlParameters.put("customer", customerData);
			urlParameters.put("merchant_id", merchantId);
			urlParameters.put("customer_key", customerKey);
			
			JSONObject response = new JSONObject(Services.executePost("http://13.127.190.250/api/v1/merchant/posAddCustomer", urlParameters));
			
			System.out.println(response);
			
			JSONObject internalResponse = null;
			if(response.has("status_code")) {
				int statusCode = response.getInt("status_code");
				internalResponse = response.getJSONObject("response");
				if(statusCode == 200) {
					outObj.put("status", true);
				}
				outObj.put("message", internalResponse.getString("message"));
			}else {
				outObj.put("message", "No respone received from EWards.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v1/getCouponDetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getCouponDetails(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("mobileNumber")) {
				outObj.put("message", "Ewards: Mobile Number not found.");
				return outObj.toString();
			}else if(!inObj.has("couponCode")) {
				outObj.put("message", "Ewards: Coupon Code not found.");
				return outObj.toString();
			}
			
			JSONObject urlParameters = new JSONObject();
			urlParameters.put("customer_mobile", inObj.getString("mobileNumber"));
			urlParameters.put("coupon_code", inObj.getString("couponCode"));
			urlParameters.put("merchant_id", merchantId);
			urlParameters.put("customer_key", customerKey);
			
			JSONObject response = new JSONObject(Services.executePost("http://13.127.190.250/api/v1/merchant/posCouponDetails",
						urlParameters));
			
			System.out.println(response);
			
			JSONObject coupon = null;
			if(response.has("status_code")) {
				int statusCode = response.getInt("status_code");
				coupon = response.getJSONObject("response");
				if(statusCode == 200) {

					IDiscount dao = new DiscountManager(false);
					if(!dao.discountExists(inObj.getString("outletId"), coupon.getString("coupon_code"))) {
						dao.addDiscount(inObj.getString("outletId"), coupon.getString("coupon_code"), coupon.getString("coupon_name"), 
								coupon.getString("discount_type").equals("fixed")?1:0, coupon.getInt("discount_value"), 0, "", "", "Unlimited", 
								new JSONArray(), 0, false, "DISCOUNT", false, "[]", "", "", 0, false, 0, 0);
					}
						
					outObj.put("status", true);
					outObj.put("couponDetails", coupon);
				}else {
					outObj.put("message", coupon.getString("message"));
				}
			}else {
				outObj.put("message", "No respone received from EWards.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v1/redeemCouponCode")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String redeemCouponCode(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("mobileNumber")) {
				outObj.put("message", "Ewards: Mobile Number not found.");
				return outObj.toString();
			}else if(!inObj.has("couponCode")) {
				outObj.put("message", "Ewards: Coupon Code not found.");
				return outObj.toString();
			}else if(!inObj.has("billNumber")) {
				outObj.put("message", "Ewards: Bill Number not found.");
				return outObj.toString();
			}
			
			JSONObject urlParameters = new JSONObject();
			urlParameters.put("customer_mobile", inObj.getString("mobileNumber"));
			urlParameters.put("coupon_code", inObj.getString("couponCode"));
			urlParameters.put("bill_number", inObj.getString("billNumber"));
			urlParameters.put("merchant_id", merchantId);
			urlParameters.put("customer_key", customerKey);
			
			JSONObject response = new JSONObject(Services.executePost("http://13.127.190.250/api/v1/merchant/posCouponDetails", urlParameters));
			
			System.out.println(response);
			if(response.has("status_code")) {
				int statusCode = response.getInt("status_code");
				if(statusCode == 200) {
					outObj.put("status", true);
				}else {
					JSONObject internalResponse = response.getJSONObject("response");
					outObj.put("message", internalResponse.getString("message"));
				}
			}else {
				outObj.put("message", "No respone received from EWards.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v1/verifyOtp")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String verifyOtp(String jsonObject) {
		return this.makeTransactionJSON(jsonObject, "http://13.127.190.250/api/v1/merchant/posRedeemPointOtpCheck", true);
	}
	
	@POST
	@Path("/v1/redeemPointRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String redeemPointRequest(String jsonObject) {
		return this.makeTransactionJSON(jsonObject, "http://13.127.190.250/api/v1/merchant/posRedeemPointRequest", false);
	}
	
	private String makeTransactionJSON(String jsonObject, String targetUrl, boolean isOtpRequired) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("mobileNumber")) {
				outObj.put("message", "Ewards: Mobile Number not found.");
				return outObj.toString();
			}else if(!inObj.has("points")) {
				outObj.put("message", "Ewards: Redeemable Points not found.");
				return outObj.toString();
			}else if(!inObj.has("billNumber")) {
				outObj.put("message", "Ewards: Bill Number not found.");
				return outObj.toString();
			}else if(!inObj.has("grossAmount")) {
				outObj.put("message", "Ewards: Gross Amount not found.");
				return outObj.toString();
			}else if(!inObj.has("netAmount")) {
				outObj.put("message", "Ewards: Net Amount not found.");
				return outObj.toString();
			}else if(!inObj.has("orderId")) {
				outObj.put("message", "Ewards: OrderId not found.");
				return outObj.toString();
			}else if(!inObj.has("outletId")) {
				outObj.put("message", "Ewards: OutletId not found.");
				return outObj.toString();
			}
			if(isOtpRequired) {
				if(!inObj.has("otp")) {
					outObj.put("message", "Ewards: OTP not found.");
					return outObj.toString();
				}
			}
			
			IOrderItem dao = new OrderManager(false);
			ArrayList<OrderItem> orderItems = dao.getOrderedItemForBill(inObj.getString("outletId"), inObj.getString("orderId"), false);
			
			JSONArray items = new JSONArray();
			JSONObject itemObj = null;
			for (OrderItem orderItem : orderItems) {
				itemObj = new JSONObject();
				itemObj.put("name", orderItem.getTitle());
				itemObj.put("id", orderItem.getMenuId());
				itemObj.put("rate", orderItem.getRate());
				itemObj.put("quantity", orderItem.getQty());
				itemObj.put("subTotal", orderItem.getFinalAmount());
				itemObj.put("category", orderItem.getCollection());
				items.put(itemObj);
			}

			JSONObject transactions = new JSONObject();
			transactions.put("id", inObj.getString("billNumber"));
			transactions.put("gross_amount", inObj.getDouble("grossAmount"));
			transactions.put("amount", inObj.getDouble("grossAmount"));
			transactions.put("net_amount", inObj.getDouble("netAmount"));
			transactions.put("number", inObj.getString("billNumber"));
			transactions.put("type", "DINE-IN");
			transactions.put("items", items);
			
			
			JSONObject urlParameters = new JSONObject();
			urlParameters.put("customer_mobile", inObj.getString("mobileNumber"));
			urlParameters.put("points", inObj.getInt("points"));
			urlParameters.put("merchant_id", merchantId);
			urlParameters.put("merchant_email", "support@orderon.co.in");
			urlParameters.put("customer_key", customerKey);
			urlParameters.put("transaction", transactions);
			
			if(isOtpRequired)
				urlParameters.put("otp", inObj.getInt("otp"));
			
			JSONObject response = new JSONObject(Services.executePost(targetUrl, urlParameters));
			
			System.out.println(response);
			
			if(response.has("status_code")) {
				int statusCode = response.getInt("status_code");
				if(statusCode == 200) {
					outObj.put("status", true);
				}else {
					JSONObject internalResponse = response.getJSONObject("response");
					outObj.put("message", internalResponse.getString("message"));
				}
			}else {
				outObj.put("message", "No respone received from EWards.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v1/settleBill")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String settleBill(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("mobileNumber")) {
				outObj.put("message", "Ewards: Mobile Number not found.");
				return outObj.toString();
			}else if(!inObj.has("name")) {
				outObj.put("message", "Ewards: Customer Name not found.");
				return outObj.toString();
			}else if(!inObj.has("points")) {
				outObj.put("message", "Ewards: Redeemable Points not found.");
				return outObj.toString();
			}else if(!inObj.has("billNumber")) {
				outObj.put("message", "Ewards: Bill Number not found.");
				return outObj.toString();
			}else if(!inObj.has("grossAmount")) {
				outObj.put("message", "Ewards: Gross Amount not found.");
				return outObj.toString();
			}else if(!inObj.has("netAmount")) {
				outObj.put("message", "Ewards: Net Amount not found.");
				return outObj.toString();
			}else if(!inObj.has("discountAmount")) {
				outObj.put("message", "Ewards: Discount Amount not found.");
				return outObj.toString();
			}else if(!inObj.has("orderId")) {
				outObj.put("message", "Ewards: OrderId not found.");
				return outObj.toString();
			}else if(!inObj.has("outletId")) {
				outObj.put("message", "Ewards: OutletId not found.");
				return outObj.toString();
			}else if(!inObj.has("taxes")) {
				outObj.put("message", "Ewards: Taxes not found.");
				return outObj.toString();
			}else if(!inObj.has("charges")) {
				outObj.put("message", "Ewards: Charges not found.");
				return outObj.toString();
			}

			IOutlet outletDao = new OutletManager(false);
			Outlet outlet = outletDao.getOutlet(inObj.getString("outletId"));
			JSONObject location = outlet.getLocation();
			String city = location.has("city")?location.getString("city"):"";
			String state = location.has("state")?location.getString("state"):"";

			JSONObject customerData = new JSONObject();
			customerData.put("name", inObj.getString("name"));
			customerData.put("mobile", inObj.getString("mobileNumber"));
			customerData.put("address", inObj.has("address")?inObj.getString("address"):"");
			customerData.put("email", inObj.has("emailId")?inObj.getString("emailId"):"");
			customerData.put("city", city);
			customerData.put("dob", inObj.has("dob")?inObj.getString("dob"):"");
			customerData.put("state", state);
			
			IOrderItem dao = new OrderManager(false);
			ArrayList<OrderItem> orderItems = dao.getOrderedItemForBill(inObj.getString("outletId"), inObj.getString("orderId"), false);
			
			JSONArray items = new JSONArray();
			JSONObject itemObj = null;
			for (OrderItem orderItem : orderItems) {
				itemObj = new JSONObject();
				itemObj.put("name", orderItem.getTitle());
				itemObj.put("id", orderItem.getMenuId());
				itemObj.put("rate", orderItem.getRate());
				itemObj.put("quantity", orderItem.getQty());
				itemObj.put("subtotal", orderItem.getFinalAmount());
				itemObj.put("category", orderItem.getCollection());
				items.put(itemObj);
			}
			
			//String couponCode = inObj.has("couponCode")?inObj.getString("couponCode"):"";
			int points = inObj.has("points")?inObj.getInt("points"):0;


			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			BigDecimal grossAmount = new BigDecimal(inObj.getDouble("grossAmount")).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal netAmount = new BigDecimal(inObj.getDouble("netAmount")).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			JSONObject transactions = new JSONObject();
			JSONObject redemption = new JSONObject();
			if(points>0) {
				redemption.put("redeemed_amount", inObj.getInt("points"));
				redemption.put("reward_id", "");
				netAmount = grossAmount;
			}else {
				redemption.put("redeemed_amount", inObj.getInt("points"));
				redemption.put("reward_id", "");
			}
			
			transactions.put("id", inObj.getString("billNumber"));
			transactions.put("number", inObj.getString("billNumber"));
			transactions.put("type", "DINE-IN");
			transactions.put("payment_type", "");
			transactions.put("gross_amount", grossAmount);
			transactions.put("net_amount", netAmount);
			transactions.put("amount", grossAmount);
			transactions.put("discount", inObj.getDouble("discountAmount"));
			transactions.put("order_time", now.format(dateFormat));
			transactions.put("online_bill_source", "");
			transactions.put("items", items);
			transactions.put("taxes", inObj.getJSONArray("taxes"));
			transactions.put("charges", inObj.getJSONArray("charges"));
			transactions.put("redemption", redemption);
			
			JSONObject urlParameters = new JSONObject();
			urlParameters.put("customer", customerData);
			urlParameters.put("merchant_id", merchantId);
			urlParameters.put("customer_key", customerKey);
			urlParameters.put("transaction", transactions);
			
			System.out.println(urlParameters);

			JSONObject response = new JSONObject(Services.executePost("http://13.127.190.250/api/v1/merchant/posAddPoint", urlParameters));
			
			System.out.println(response);
			
			JSONObject internalResponse = null;
			if(response.has("status_code")) {
				int statusCode = response.getInt("status_code");
				internalResponse = response.getJSONObject("response");
				if(statusCode == 200) {
					outObj.put("status", true);
				}
				outObj.put("message", internalResponse.getString("message"));
			}else {
				outObj.put("message", "No respone received from EWards.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
}