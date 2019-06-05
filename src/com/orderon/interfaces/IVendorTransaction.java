package com.orderon.interfaces;

import java.math.BigDecimal;

import org.json.JSONObject;

public interface IVendorTransaction {

	public JSONObject addVendorTransaction(String corporateId, String outletId, int vendorId, String transType, BigDecimal transAmount, 
			String paymentType, String paymentDate, String account, String userId);
}
