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
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.commons.Configurator;
import com.orderon.dao.AccessManager.OrderItem;
import com.orderon.dao.AccessManager.Outlet;
import com.orderon.dao.AccessManager.Settings;
import com.orderon.dao.DiscountManager;
import com.orderon.dao.OrderManager;
import com.orderon.dao.OutletManager;
import com.orderon.dao.ReportBufferManager;
import com.orderon.interfaces.IDiscount;
import com.orderon.interfaces.IOrderItem;
import com.orderon.interfaces.IOutlet;
import com.orderon.interfaces.IReportBuffer;

@Path("/Ewards/Services")
public class EwardsServices {

	public static String url = "http://www.myewards.com";
	//public static String url = "http://13.127.190.250";
	//public static String url = "http://internal.myewards.com";
	
	@GET
	@Path("/v1/heartbeat")
	@Produces(MediaType.APPLICATION_JSON)
	public String hearbeat() {
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("systemId", Configurator.getSystemId());
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
		IOutlet dao = new OutletManager(false);
		
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("mobileNumber")) {
				outObj.put("message", "Ewards: Mobile Number not found.");
				return outObj.toString();
			}else if(!inObj.has("systemId")) {
				outObj.put("message", "SystemId not found.");
				return outObj.toString();
			}
			
			Settings settings = dao.getSettings(inObj.getString("systemId"));

			if(!settings.getIsInternetAvailable()) {
				outObj.put("isInternetAvailable", false);
				outObj.put("message", "Internet is unavailable. Points from EWards cannot be fetched right now.");
				return outObj.toString();
			}
			outObj.put("isInternetAvailable", true);
			
			JSONObject urlParameters = new JSONObject();
			JSONObject credentials = settings.getEWardsCredentials();
			urlParameters.put("customer_key", credentials.getString("key"));
			urlParameters.put("merchant_id", credentials.getInt("id"));
			urlParameters.put("customer_mobile", inObj.getString("mobileNumber"));
			
			JSONObject response = new JSONObject(Services.executePost(url+"/api/v1/merchant/posCustomerCheck", urlParameters));
			
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
			}else if(!inObj.has("systemId")) {
				outObj.put("message", "systemId not found.");
				return outObj.toString();
			}

			Settings settings = dao.getSettings(inObj.getString("systemId"));
			
			if(!settings.getIsInternetAvailable()) {
				outObj.put("message", "Internet is unavailable. Customer cannot be added.");
				return outObj.toString();
			}
			
			Outlet outlet = dao.getOutletForSystem(inObj.getString("systemId"), inObj.getString("outletId"));
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
			JSONObject credentials = settings.getEWardsCredentials();
			urlParameters.put("customer_key", credentials.getString("key"));
			urlParameters.put("merchant_id", credentials.getInt("id"));
			
			JSONObject response = new JSONObject(Services.executePost(url+"/api/v1/merchant/posAddCustomer", urlParameters));
			
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

			IOutlet outletDao = new OutletManager(false);
			Settings settings = outletDao.getSettings(inObj.getString("systemId"));
			
			JSONObject urlParameters = new JSONObject();
			JSONObject credentials = settings.getEWardsCredentials();
			urlParameters.put("customer_key", credentials.getString("key"));
			urlParameters.put("merchant_id", credentials.getInt("id"));
			urlParameters.put("customer_mobile", inObj.getString("mobileNumber"));
			urlParameters.put("coupon_code", inObj.getString("couponCode"));
			
			JSONObject response = new JSONObject(Services.executePost(url+"/api/v1/merchant/posCouponDetails",
						urlParameters));
			
			System.out.println(response);
			
			JSONObject coupon = null;
			if(response.has("status_code")) {
				int statusCode = response.getInt("status_code");
				coupon = response.getJSONObject("response");
				if(statusCode == 200) {

					IDiscount dao = new DiscountManager(false);
					Outlet outlet = outletDao.getOutletForSystem(inObj.getString("systemId"), inObj.getString("outletId"));
					if(!dao.discountExists(inObj.getString("systemId"), inObj.getString("outletId"), coupon.getString("coupon_code"))) {
						dao.addDiscount(outlet.getCorporateId(), outlet.getRestaurantId(), inObj.getString("systemId"), inObj.getString("outletId"), coupon.getString("coupon_code"), coupon.getString("coupon_name"), 
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
			}else if(!inObj.has("orderNumber")) {
				outObj.put("message", "Ewards: Order Number not found.");
				return outObj.toString();
			}

			IOutlet outletDao = new OutletManager(false);
			Settings settings = outletDao.getSettings(inObj.getString("systemId"));
			
			JSONObject urlParameters = new JSONObject();
			urlParameters.put("customer_mobile", inObj.getString("mobileNumber"));
			urlParameters.put("coupon_code", inObj.getString("couponCode"));
			urlParameters.put("bill_number", inObj.getInt("orderNumber"));
			JSONObject credentials = settings.getEWardsCredentials();
			urlParameters.put("customer_key", credentials.getString("key"));
			urlParameters.put("merchant_id", credentials.getInt("id"));
			
			JSONObject response = new JSONObject(Services.executePost(url+"/api/v1/merchant/posCouponDetails", urlParameters));
			
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
		return this.makeTransactionJSON(jsonObject, url+"/api/v1/merchant/posRedeemPointOtpCheck", true).toString();
	}
	
	@POST
	@Path("/v1/redeemPointRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String redeemPointRequest(String jsonObject) {
		JSONObject response = this.makeTransactionJSON(jsonObject, url+"/api/v1/merchant/posRedeemPointRequest", false);
		try {
			if(response.getBoolean("status")) {
				if(!response.getBoolean("authentication")) {
					return this.makeTransactionJSON(jsonObject, url+"/api/v1/merchant/posRedeemPointOtpCheck", false).toString();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.toString();
	}
	
	private JSONObject makeTransactionJSON(String jsonObject, String targetUrl, boolean isOtpRequired) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("mobileNumber")) {
				outObj.put("message", "Ewards: Mobile Number not found.");
				return outObj;
			}else if(!inObj.has("points")) {
				outObj.put("message", "Ewards: Redeemable Points not found.");
				return outObj;
			}else if(!inObj.has("orderNumber")) {
				outObj.put("message", "Ewards: Bill Number not found.");
				return outObj;
			}else if(!inObj.has("grossAmount")) {
				outObj.put("message", "Ewards: Gross Amount not found.");
				return outObj;
			}else if(!inObj.has("netAmount")) {
				outObj.put("message", "Ewards: Net Amount not found.");
				return outObj;
			}else if(!inObj.has("orderId")) {
				outObj.put("message", "Ewards: OrderId not found.");
				return outObj;
			}else if(!inObj.has("systemId")) {
				outObj.put("message", "Ewards: SystemId not found.");
				return outObj;
			}
			if(isOtpRequired) {
				if(!inObj.has("otp")) {
					outObj.put("message", "Ewards: OTP not found.");
					return outObj;
				}
			}
			
			IOrderItem dao = new OrderManager(false);
			ArrayList<OrderItem> orderItems = dao.getOrderedItemForBill(inObj.getString("systemId"), inObj.getString("orderId"), false);

			IOutlet outletDao = new OutletManager(false);
			Settings settings = outletDao.getSettings(inObj.getString("systemId"));
			
			JSONArray items = new JSONArray();
			JSONObject itemObj = null;
			for (OrderItem orderItem : orderItems) {
				itemObj = new JSONObject();
				itemObj.put("name", orderItem.getTitle());
				itemObj.put("id", orderItem.getMenuId());
				itemObj.put("rate", orderItem.getRate());
				itemObj.put("quantity", orderItem.getQuantity());
				itemObj.put("subTotal", orderItem.getFinalAmount());
				itemObj.put("category", orderItem.getCollection());
				items.put(itemObj);
			}

			JSONObject transactions = new JSONObject();
			transactions.put("id", inObj.getInt("orderNumber"));
			transactions.put("gross_amount", inObj.getDouble("grossAmount"));
			transactions.put("amount", inObj.getDouble("grossAmount"));
			transactions.put("net_amount", inObj.getDouble("netAmount"));
			transactions.put("number", inObj.getInt("orderNumber"));
			transactions.put("type", "DINE-IN");
			transactions.put("items", items);
			
			
			JSONObject urlParameters = new JSONObject();
			JSONObject credentials = settings.getEWardsCredentials();
			urlParameters.put("customer_key", credentials.getString("key"));
			urlParameters.put("merchant_id", credentials.getInt("id"));
			urlParameters.put("customer_mobile", inObj.getString("mobileNumber"));
			urlParameters.put("points", inObj.getInt("points"));
			urlParameters.put("merchant_email", "support@orderon.co.in");
			urlParameters.put("transaction", transactions);
			
			if(isOtpRequired)
				urlParameters.put("otp", inObj.getString("otp"));
			else {
				urlParameters.put("otp", "");
			}
			
			JSONObject response = new JSONObject(Services.executePost(targetUrl, urlParameters));
			
			System.out.println(response);
			
			if(response.has("status_code")) {
				JSONObject internalResponse = response.getJSONObject("response");
				int statusCode = response.getInt("status_code");
				if(statusCode == 200) {
					outObj.put("status", true);
					if(internalResponse.has("authentication")) {
						outObj.put("authentication", internalResponse.getBoolean("authentication"));
					}else {
						outObj.put("authentication", true);
					}
				}else {
					outObj.put("message", internalResponse.getString("message"));
				}
			}else {
				outObj.put("message", "No respone received from EWards.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj;
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
			}else if(!inObj.has("orderNumber")) {
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
			}else if(!inObj.has("systemId")) {
				outObj.put("message", "Ewards: SystemId not found.");
				return outObj.toString();
			}else if(!inObj.has("taxes")) {
				outObj.put("message", "Ewards: Taxes not found.");
				return outObj.toString();
			}else if(!inObj.has("charges")) {
				outObj.put("message", "Ewards: Charges not found.");
				return outObj.toString();
			}

			IOutlet outletDao = new OutletManager(false);
			Settings settings = outletDao.getSettings(inObj.getString("systemId"));
			Outlet outlet = outletDao.getOutletForSystem(inObj.getString("systemId"), inObj.getString("outletId"));
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
			ArrayList<OrderItem> orderItems = dao.getOrderedItemForBill(inObj.getString("systemId"), inObj.getString("orderId"), false);
			
			JSONArray items = new JSONArray();
			JSONObject itemObj = null;
			for (OrderItem orderItem : orderItems) {
				itemObj = new JSONObject();
				itemObj.put("name", orderItem.getTitle());
				itemObj.put("id", orderItem.getMenuId());
				itemObj.put("rate", orderItem.getRate());
				itemObj.put("quantity", orderItem.getQuantity());
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
			
			transactions.put("id", inObj.getInt("orderNumber"));
			transactions.put("number", inObj.getInt("orderNumber"));
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
			JSONObject credentials = settings.getEWardsCredentials();
			urlParameters.put("customer_key", credentials.getString("key"));
			urlParameters.put("merchant_id", credentials.getInt("id"));
			urlParameters.put("customer", customerData);
			urlParameters.put("transaction", transactions);
			
			System.out.println(urlParameters);
			
			JSONObject response = new JSONObject();
			
			if(settings.getIsInternetAvailable()) {
				response = new JSONObject(Services.executePost(url+"/api/v1/merchant/posAddPoint", urlParameters));
			}else {
				IReportBuffer bufferDao = new ReportBufferManager(false);
				bufferDao.addEWardsToBuffer(inObj.getString("systemId"), urlParameters.toString());
				outObj.put("status", true);
				return outObj.toString();
			}
			
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
