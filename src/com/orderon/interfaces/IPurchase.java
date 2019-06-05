package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONObject;

import com.orderon.dao.AccessManager.Purchase;

public interface IPurchase {
	
	public JSONObject addPurchase(String billNo, String challanNo, int vendorId, String outletId, 
			BigDecimal additionalDiscount, BigDecimal totalDiscount, BigDecimal charge, BigDecimal roundOff, BigDecimal totalGst, 
			BigDecimal grandTotal, String purchaseDate, String paymentType, String account, String remark);
	
	public ArrayList<Purchase> getPurchaseHistory(String outletId, String startDate, String endDate);
	
	public ArrayList<Purchase> getCreditPurchasesByVendor(String outletId, int vendorId);
	
	public Purchase getPurchaseHistory(String outletId, String purchaseId);
	
	public String getNextPurchaseId(String outletId, String transType);
	
	public boolean settleCredit(String outletId, String purchaseId, BigDecimal amount, String paymentType, String account);
}
