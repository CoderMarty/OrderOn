package com.orderon.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Charge;
import com.orderon.dao.ChargeManager;
import com.orderon.interfaces.ICharge;

public class ChargeCalculator {

	private BigDecimal chargeAmount;
	private ICharge dao;
	private ArrayList<Charge> charges;
	private JSONArray chargeDetails;
	private JSONObject chargeObj;
	
	public ChargeCalculator() {
		chargeAmount = new BigDecimal(0.0);
		chargeDetails = new JSONArray();
		dao = new ChargeManager(false);
	}
	
	public JSONArray getChargeDetails() {
		return chargeDetails;
	}

	public void calculateCharge(String systemId, String outletId, BigDecimal subTotal, JSONArray excludedCharges
			, boolean isApplicableOnItem, String orderType) {

		charges = dao.getActiveCharges(systemId, outletId);
		
		BigDecimal hundred = new BigDecimal(100.0);
		boolean isChargeAdded = false;
		boolean isExcluded = false;
		boolean isApplicable = false;
		JSONObject tempObj = new JSONObject();
		JSONArray tempChargeDetails = null;
		String isApplicableOn = "ITEM";
		if(isApplicableOnItem) {
			isApplicableOn = "ORDER";
		}

		try {
			for (Charge charge : charges) {
				isApplicable = false;
				if(charge.getApplicableOn().equals(isApplicableOn)) {
					continue;
				}
				isChargeAdded = false;
				
				for (int i = 0; i < excludedCharges.length(); i++) {
					if(excludedCharges.getInt(i) == charge.getId()) {
						isExcluded = true;
						break;
					}
				}
				if(isExcluded) {
					continue;
				}
				for (int i = 0; i < charge.getOrderType().length(); i++) {
					if(charge.getOrderType().getString(i).equals(orderType)) {
						isApplicable = true;
						break;
					}
				}
				if(!isApplicable) {
					continue;
				}
				if(charge.getType().equals("PERCENTAGE")) {
					chargeAmount = charge.getValue().multiply(subTotal).divide(hundred).setScale(2, RoundingMode.HALF_UP);
				}else {
					chargeAmount = charge.getValue();
				}
				
				tempChargeDetails = new JSONArray();
				for (int i = 0; i < chargeDetails.length(); i++) {
					tempObj = chargeDetails.getJSONObject(i);
					if(tempObj.getInt("id") == charge.getId()) {
						
						tempObj.put("amount", chargeAmount.add(new BigDecimal(tempObj.getDouble("amount")).setScale(2, RoundingMode.HALF_UP)));
						tempObj.put("chargeableTotal", subTotal.add(new BigDecimal(tempObj.getDouble("chargeableTotal")).setScale(2, RoundingMode.HALF_UP)));
						
						isChargeAdded = true;
					}
					tempChargeDetails.put(tempObj);
				}
				
				if(!isChargeAdded) {
					chargeObj = new JSONObject();
					chargeObj.put("id", charge.getId());
					chargeObj.put("name", charge.getName());
					chargeObj.put("amount", chargeAmount);
					chargeObj.put("value", charge.getValue());
					chargeObj.put("type", charge.getType());
					chargeObj.put("chargeableTotal", subTotal);
					chargeObj.put("applicableOn", charge.getApplicableOn());
					chargeObj.put("isBar", charge.getApplicableOn().contains("BAR"));
					
					chargeDetails.put(chargeObj);
				}else {
					chargeDetails = tempChargeDetails;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

