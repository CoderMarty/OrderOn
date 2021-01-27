package com.orderon.dao;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.IAccount;

public class AccountManager extends AccessManager implements IAccount{

	public AccountManager(Boolean transactionBased) {
		super(transactionBased);
	}
	
	@Override
	public ArrayList<Account> getBankAccounts(String systemId) {
		String sql = "SELECT * FROM Bank WHERE systemId='" + systemId + "';";
		return db.getRecords(sql, Account.class, systemId);
	}
	
	@Override
	public Account getBankAccount(String systemId, int accountNumber) {
		String sql = "SELECT * FROM Bank WHERE systemId='" + systemId + "' AND accountNumber = "+accountNumber+";";
		return db.getOneRecord(sql, Account.class, systemId);
	}

	@Override
	public JSONObject addAccount(String corporateId, String systemId, String outletId, int accountNumber, String accountName, String accountType,
			String bankName, BigDecimal initialBalance, String section) {

		JSONObject outObj = new JSONObject();
		
		String sql = "INSERT INTO Bank "
				+ "(corporateId, systemId, outletId, accountNumber, accountName, accountType, bankName, initialBalance, section) VALUES('"
				+ escapeString(corporateId) + "', '" 
				+ escapeString(systemId) 	+ "', '" 
				+ escapeString(outletId) 	+ "', " 
				+ accountNumber 			+ ", '" 
				+ escapeString(accountName) + "', '"
				+ accountType 				+ "', '"
				+ escapeString(bankName) 	+ "', "
				+ initialBalance 			+ ", '"
				+ section 					+ "');";
		
		try {
			outObj.put("status", db.executeUpdate(sql, outletId, true));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}
}
