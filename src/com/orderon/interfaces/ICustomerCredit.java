package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONObject;

import com.orderon.dao.AccessManager.CustomerCreditLog;

public interface ICustomerCredit {

	public JSONObject addCustomerCredit(String outletId, String mobileNumber, BigDecimal amount, String orderId);
	
	public JSONObject settleCustomerCredit(String outletId, String mobileNumber, String orderId, String paymentType);
	
	public ArrayList<CustomerCreditLog> getCreditLog(String outletId, String startDate);
	
	public ArrayList<CustomerCreditLog> getCreditLog(String outletId, String startDate, String endDate);
}
