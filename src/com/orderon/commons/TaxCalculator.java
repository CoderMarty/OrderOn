package com.orderon.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.OrderItem;
import com.orderon.dao.AccessManager.Tax;
import com.orderon.dao.TaxManager;
import com.orderon.interfaces.ITax;

public class TaxCalculator {

	private BigDecimal taxAmount;
	private ITax dao;
	private ArrayList<Tax> taxes;
	private JSONArray taxDetails;
	private JSONObject taxObj;
	
	public TaxCalculator() {
		taxAmount = new BigDecimal(0.0);
		taxDetails = new JSONArray();
		dao = new TaxManager(false);
	}
	
	public JSONArray getTaxDetails() {
		return taxDetails;
	}

	public void calculateTax(String systemId, String outletId, OrderItem orderedItem, BigDecimal subTotal, JSONArray excludedTaxes) {

		taxes = dao.getActiveTaxes(systemId, outletId);
		
		BigDecimal hundred = new BigDecimal(100.0);
		boolean isTaxAdded = false;
		boolean isExcluded = false;
		JSONObject tempObj = new JSONObject();
		JSONArray tempTaxDetails = null;
		
		try {
			for (Tax tax : taxes) {
					
				isTaxAdded = false;

				for (int i = 0; i < excludedTaxes.length(); i++) {
					if(excludedTaxes.getInt(i) == tax.getId()) {
						isExcluded = true;
						break;
					}
				}
				if(isExcluded) {
					continue;
				}
				if(tax.getType().equals("PERCENTAGE")) {
					taxAmount = tax.getValue().multiply(subTotal).divide(hundred).setScale(2, RoundingMode.HALF_UP);
				}else {
					taxAmount = tax.getValue();
				}
				
				tempTaxDetails = new JSONArray();
				for (int i = 0; i < taxDetails.length(); i++) {
					tempObj = taxDetails.getJSONObject(i);
					if(tempObj.getInt("id") == tax.getId()) {
						
						tempObj.put("amount", taxAmount.add(new BigDecimal(tempObj.getDouble("amount")).setScale(2, RoundingMode.HALF_UP)));
						tempObj.put("taxableTotal", subTotal.add(new BigDecimal(tempObj.getDouble("taxableTotal")).setScale(2, RoundingMode.HALF_UP)));
						
						isTaxAdded = true;
					}
					tempTaxDetails.put(tempObj);
				}
				
				if(!isTaxAdded) {
					taxObj = new JSONObject();
					taxObj.put("id", tax.getId());
					taxObj.put("name", tax.getName());
					taxObj.put("amount", taxAmount);
					taxObj.put("value", tax.getValue());
					taxObj.put("taxableTotal", subTotal);
					taxObj.put("applicableOn", tax.getApplicableOn());
					taxObj.put("isBar", tax.getApplicableOn().contains("BAR"));
					
					taxDetails.put(taxObj);
				}else {
					taxDetails = tempTaxDetails;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
