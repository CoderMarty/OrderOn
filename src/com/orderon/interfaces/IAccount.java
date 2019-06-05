package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONObject;

import com.orderon.dao.AccessManager.Account;

public interface IAccount {
	
	public ArrayList<Account> getBankAccounts(String outletId);
	
	public Account getBankAccount(String hotelId, int accountNumber);

	public JSONObject addAccount(String outletId, int accountNumber, String accountName,
			String accountType, String bankName, BigDecimal initialBalance, String section);
}
