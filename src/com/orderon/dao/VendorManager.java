package com.orderon.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.IVendor;
import com.orderon.interfaces.IVendorTransaction;

public class VendorManager extends AccessManager implements IVendor, IVendorTransaction{

	public VendorManager(boolean transactionBased) {
		super(transactionBased);
	}

	@Override
	public JSONObject addVendor(String corporateId, String outletId, String name, String address, JSONArray poc, 
			String emailId, String GSTNumber, BigDecimal balance) {
		
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			
			if(!validateEmail(emailId)) {
				outObj.put("message", "Please enter a valid company email Id.");
				return outObj;
			}
			JSONObject inobj = new JSONObject();
			for (int i=0; i<poc.length(); i++) {
				inobj = poc.getJSONObject(i);
				if(!validateEmail(inobj.getString("emailId"))) {
					outObj.put("message", "Please enter a valid company email Id for POC "+(i+1)+".");
					return outObj;
				}else if(!validateMobileNumber(inobj.getString("mobileNumber"))) {
					outObj.put("message", "Please enter a valid mobileNumber for POC "+(i+1)+".");
					return outObj;
				}
			}
			
			String sql = "INSERT INTO Vendors (corporateId, outletId, name, address, poc, emailId, GSTNumber, balance) "
					+ "VALUES ('"+escapeString(corporateId)+"', '"+escapeString(outletId)+"', '"+escapeString(name)+"', '"
					+escapeString(address)+"', '"+poc.toString() + "', '" 
					+escapeString(emailId)+"', '"+escapeString(GSTNumber)+"', "+balance+");";
			
			if(db.executeUpdate(sql, outletId, true)) {
				outObj.put("status", true);
			}else {
				outObj.put("status", false);
				outObj.put("message", "Vendor could not be added. Please try again.");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public JSONObject editVendor(int vendorId, String outletId, String name, String address,
			JSONArray poc, String emailId, String GSTNumber) {
		
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			
			if(!validateEmail(emailId)) {
				outObj.put("message", "Please enter a valid company email Id.");
				return outObj;
			}
			JSONObject inobj = new JSONObject();
			for (int i=0; i<poc.length(); i++) {
				inobj = poc.getJSONObject(i);
				if(!validateEmail(inobj.getString("emailId"))) {
					outObj.put("message", "Please enter a valid company email Id for POC "+(i+1)+".");
					return outObj;
				}else if(!validateMobileNumber(inobj.getString("mobileNumber"))) {
					outObj.put("message", "Please enter a valid mobileNumber for POC "+(i+1)+".");
					return outObj;
				}
			}
			
			String sql = "UPDATE Vendors SET name = '"+escapeString(name)+"', "
					+ "address = '"+address+"', "
					+ "poc = '"+poc.toString()+"', "
					+ "emailId = '"+escapeString(emailId)+"', "
					+ "GSTNumber = '"+escapeString(GSTNumber)+"' "
					+ "WHERE id = "+vendorId+" AND outletId = '"+outletId+"';";
			
			if(db.executeUpdate(sql, outletId, true)) {
				outObj.put("status", true);
			}else {
				outObj.put("status", false);
				outObj.put("message", "Vendor details could not be update. Please try again.");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public boolean updateVendorBalance(int vendorId, String outletId, BigDecimal transAmount, String transType) {
		
		Vendor vendor = this.getVendorById(outletId, vendorId);
		BigDecimal balance = vendor.getBalance();
		
		if(transType.equals(TRANSASCTION_CREDIT)) {
			balance = balance.subtract(transAmount);
		}else {
			balance = balance.add(transAmount);
		}
		
		String sql = "UPDATE Vendors SET balance = "+balance+" "
				+ "WHERE id = "+vendorId+" AND outletId = '"+outletId+"';";
		
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public Vendor getVendorById(String outletId, int vendorId) {
		String sql = "SELECT * FROM Vendors WHERE id = "+vendorId+" AND outletId = '"+outletId+"';";
		return db.getOneRecord(sql, Vendor.class, outletId);
	}

	@Override
	public Vendor getVendorByName(String outletId, String vendorName) {
		String sql = "SELECT * FROM Vendors WHERE name = '"+vendorName+"' AND outletId = '"+outletId+"';";
		return db.getOneRecord(sql, Vendor.class, outletId);
	}

	@Override
	public ArrayList<Vendor> getVendors(String outletId) {
		String sql = "SELECT * FROM Vendors WHERE outletId = '"+outletId+"';";
		return db.getRecords(sql, Vendor.class, outletId);
	}

	@Override
	public boolean deleteVendor(String outletId, int vendorId) {
		String sql = "DELETE FROM Vendors WHERE outletId = '"+outletId+"' AND id = "+vendorId+";";
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public JSONObject addVendorTransaction(String corporateId, String outletId, int vendorId, String transType,
			BigDecimal transAmount, String paymentType, String paymentDate, String account, String userId) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			
			String sql = "INSERT INTO VendorTransactions (corporateId, outletId, vendorId, transType, transAmount, paymentType, account, dateTime, paymentDate, userId) "
					+ "VALUES ('"+escapeString(corporateId)+"', '"+escapeString(outletId)+"', "+vendorId+", '"+transType+"', " + transAmount+ ", '"
					+paymentType+"', '"+account + "', '"+LocalDateTime.now() + "', '"+paymentDate + "', '" +userId+"');";
			
			if(db.executeUpdate(sql, outletId, true)) {
				outObj.put("status", true);
			}else {
				outObj.put("status", false);
				outObj.put("message", "Transation could not be made. Please try again.");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}
}
