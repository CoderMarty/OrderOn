package com.orderon.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.orderon.interfaces.IService;
import com.orderon.interfaces.ITransactionHistory;

public class TransactionHistoryManager extends AccessManager implements ITransactionHistory{

	public TransactionHistoryManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ArrayList<TransactionHistory> getTransactionHistory(String hotelId) {
		
		IService serviceDao = new ServiceManager(false);

		String sql = "SELECT * FROM TransactionHistory WHERE hotelId = '" + hotelId + "' AND serviceDate = '"
				+ serviceDao.getServiceDate(hotelId) + "';";
		return db.getRecords(sql, TransactionHistory.class, hotelId);
	}

	@Override
	public BigDecimal getBalanceForDeliveryBoy(String hotelId, String employeeId) {
		
		IService serviceDao = new ServiceManager(false);
		
		String sql = "SELECT amount AS entityId FROM TransactionHistory WHERE hotelId = '" + hotelId + "' AND serviceDate = '"
				+ serviceDao.getServiceDate(hotelId) + "' AND employeeId = '"+employeeId+"';";
		
		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		return entity==null?new BigDecimal("0.0"):entity.getId();
	}

	@Override
	public Boolean updateTransactionHistory(String hotelId, String trType, String trDetail, String trAccountName, 
			String paymentType, BigDecimal amount, String employeeId, String userId, String authoriser) {

		BigDecimal accountBalance = this.getAccountBalance(hotelId, employeeId);
		if (trType.equals("CREDIT"))
			accountBalance.add(amount);
		else
			accountBalance.add(amount);

		IService serviceDao = new ServiceManager(false);

		String sql = "INSERT INTO TransactionHistory ('trType', 'trDetail', 'trAccountName', 'paymentType', 'amount', 'balance',"
				+ "'trDate', 'userId', 'authoriser', 'employeeId', 'hotelId', 'serviceDate') VALUES ('" + trType + "', '" + escapeString(trDetail) + "', '" 
				+ trAccountName + "', '" + paymentType + "', " + amount + ", " + accountBalance + ", '" + LocalDateTime.now() + "', '" 
				+ userId + "', '" + authoriser + "', '" + employeeId + "', '" + escapeString(hotelId) + "', '" + serviceDao.getServiceDate(hotelId) + "');";

		db.executeUpdate(sql, hotelId, true);
		
		sql = "UPDATE Employee SET accountBalance = "+accountBalance+ " WHERE hotelId = '"+hotelId+"' AND employeeId = '"+employeeId+"';";
		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public BigDecimal getAccountBalance(String hotelId, String employeeId) {
		String sql = "SELECT accountBalance as entityId FROM Employee WHERE hotelId = '" + hotelId
				+ "' AND employeeId = '" + employeeId + "'";

		return db.getOneRecord(sql, EntityBigDecimal.class, hotelId).getId();
	}
}
