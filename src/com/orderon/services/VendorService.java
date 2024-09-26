package com.orderon.services;

import java.math.BigDecimal;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.dao.AccessManager;
import com.orderon.dao.AccessManager.Vendor;
import com.orderon.dao.InventoryManager;
import com.orderon.dao.VendorManager;
import com.orderon.interfaces.IPurchase;
import com.orderon.interfaces.IVendor;
import com.orderon.interfaces.IVendorTransaction;

@Path("/VendorServices")
public class VendorService {

	@POST
	@Path("/v3/addVendor")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addVendor(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IVendor dao = new VendorManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", false);
			
			String outletId = "";
			String name = "";
			
			if(!inObj.has("outletId")) {
				outObj.put("message", "OutletId not found.");
			}else if(!inObj.has("name")) {
				outObj.put("message", "Name not found.");
			}
			
			String corporateId = inObj.has("corporateId")?inObj.getString("corporateId"):"";
			outletId = inObj.getString("outletId");
			name = inObj.getString("name");
			String address = inObj.has("address")?inObj.getString("address"):"";
			String emailId = inObj.has("emailId")?inObj.getString("emailId"):"";
			String GSTNumber = inObj.has("GSTNumber")?inObj.getString("GSTNumber"):"";
			JSONArray poc = inObj.has("poc")?inObj.getJSONArray("poc"):new JSONArray();
			BigDecimal balance = inObj.has("balance")?new BigDecimal(inObj.getDouble("balance")):new BigDecimal("0");

			Vendor vendor = dao.getVendorByName(outletId, name);
			if(vendor==null) {
				outObj = dao.addVendor(corporateId, outletId, name, address, poc, emailId, GSTNumber, balance);
			}else if(vendor.getName().equals("name")) {
				outObj.put("message", "Cannot add Vendor. Already Exists.");
				return outObj.toString();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/editVendor")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String editVendor(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IVendor dao = new VendorManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", false);
			
			String outletId = "";
			String name = "";
			int vendorId = 0;
			
			if(inObj.has("outletId")) {
				outObj.put("message", "OutletId not found.");
			}else if(inObj.has("vendorId")) {
				outObj.put("message", "VendorId not found.");
			}else if(inObj.has("name")) {
				outObj.put("message", "Name not found.");
			}
			vendorId = inObj.getInt("vendorId");
			Vendor vendor = dao.getVendorById(outletId, vendorId);
			
			outletId = inObj.getString("outletId");
			name = inObj.has("name")?inObj.getString("name"):vendor.getName();
			String address = inObj.has("address")?inObj.getString("address"):vendor.getAddress();
			String emailId = inObj.has("emailId")?inObj.getString("emailId"):vendor.getEmailId();
			String GSTNumber = inObj.has("GSTNumber")?inObj.getString("GSTNumber"):vendor.getGSTNumber();
			JSONArray poc = inObj.has("poc")?inObj.getJSONArray("poc"):vendor.getPoc();
			
			outObj=  dao.editVendor(vendorId, outletId, name, address, poc, emailId, GSTNumber);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getVendors")
	@Produces(MediaType.APPLICATION_JSON)
	public String getVendors(@QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();
		IVendor dao = new VendorManager(false);
		try {
			outObj.put("vendors", dao.getVendors(outletId));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getVendorById")
	@Produces(MediaType.APPLICATION_JSON)
	public String getVendorById(@QueryParam("outletId") String outletId, @QueryParam("vendorId") int vendorId) {
		IVendor dao = new VendorManager(false);
		return new JSONObject(dao.getVendorById(outletId, vendorId)).toString();
	}

	@GET
	@Path("/v3/getVendorByName")
	@Produces(MediaType.APPLICATION_JSON)
	public String getVendorByName(@QueryParam("outletId") String outletId, @QueryParam("vendorName") String vendorName) {
		IVendor dao = new VendorManager(false);
		return new JSONObject(dao.getVendorByName(outletId, vendorName)).toString();
	}

	@POST
	@Path("/v3/deleteVendor")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteVendor(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IVendor dao = new VendorManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteVendor(inObj.getString("outletId"), inObj.getInt("vendorId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}


	@POST
	@Path("/v1/settleVendorAccount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String settleVendorAccount(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IVendorTransaction dao = new VendorManager(false);
		IVendor vendorDao = new VendorManager(false);
		IPurchase purchaseDao = new InventoryManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", false);
			
			String outletId = "";
			int vendorId = 0;
			BigDecimal amount = new BigDecimal(inObj.getDouble("totalAmount"));
			BigDecimal creditAmount = null;
			
			if(inObj.has("outletId")) {
				outObj.put("message", "OutletId not found.");
			}else if(inObj.has("vendorId")) {
				outObj.put("message", "VendorId not found.");
			}else if(inObj.has("name")) {
				outObj.put("message", "Name not found.");
			}
			vendorId = inObj.getInt("vendorId");
			outletId = inObj.getString("outletId");
			JSONArray purchases = inObj.getJSONArray("purchases");
			
			outObj=  dao.addVendorTransaction(inObj.getString("corporateId"), outletId, vendorId, AccessManager.TRANSASCTION_DEBIT, 
					amount, inObj.getString("paymentType"), inObj.getString("paymentDate"), 
					inObj.getString("account"), inObj.getString("userId"));
			
			JSONObject purchase = null;
			for(int i=0; i<purchases.length(); i++) {
				purchase = purchases.getJSONObject(i);
				creditAmount = new BigDecimal(purchase.getDouble("creditAmount"));
				if(amount.subtract(creditAmount).compareTo(new BigDecimal("0")) == 1 ||
						amount.subtract(creditAmount).compareTo(new BigDecimal("0")) == 0) {
					amount = amount.subtract(creditAmount);
					purchaseDao.settleCredit(outletId, purchase.getString("purchaseId"), creditAmount, 
							inObj.getString("paymentType"), inObj.getString("account"));
				}else {
					creditAmount = amount;
					purchaseDao.settleCredit(outletId, purchase.getString("purchaseId"), creditAmount, 
							inObj.getString("paymentType"), inObj.getString("account"));
					break;
				}
			}
			vendorDao.updateVendorBalance(vendorId, outletId, new BigDecimal(inObj.getDouble("totalAmount")), AccessManager.TRANSASCTION_DEBIT);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
}
