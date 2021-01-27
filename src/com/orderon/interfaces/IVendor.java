package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Vendor;

public interface IVendor{

	public JSONObject addVendor(String corporateId, String outletId, String name, String address, JSONArray poc, String emailId, String GSTNumber, 
			BigDecimal balance);

	public JSONObject editVendor(int vendorId, String outletId, String name, String address, JSONArray poc, String emailId, String GSTNumber);
	
	public boolean updateVendorBalance(int vendorId, String outletId, BigDecimal transAmount, String transType);
	
	public Vendor getVendorByName(String outletId, String vendorName);
	
	public Vendor getVendorById(String outletId, int vendorId);
	
	public ArrayList<Vendor> getVendors(String outletId);
	
	public boolean deleteVendor(String outletId, int vendorId);
}
