package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.TransactionHistory;

public interface ITransactionHistory {
	
	public ArrayList<TransactionHistory> getTransactionHistory(String hotelId);
	
	public BigDecimal getBalanceForDeliveryBoy(String hotelId, String employeeId);
	
	public Boolean updateTransactionHistory(String hotelId, String trType, String trDetail, String trAccountName, 
			String paymentType, BigDecimal amount, String employeeId, String userId, String authoriser);

	public BigDecimal getAccountBalance(String hotelId, String employeeId);
}
