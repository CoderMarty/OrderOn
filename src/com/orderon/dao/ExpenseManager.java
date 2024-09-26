package com.orderon.dao;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.commons.ExpenseType;
import com.orderon.interfaces.IExpense;
import com.orderon.interfaces.IService;
import com.orderon.interfaces.ITransactionHistory;

public class ExpenseManager extends AccessManager implements IExpense {

	public ExpenseManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean addExpense(String hotelId, BigDecimal expenseAmount, String details, String payeeName, String invoiceNumber, int cheque,
			String paymentType, String expenseType, String bankAccount, String userId, String employeeId, String date, String serviceType) {

		String sql = "INSERT INTO Expenses "
				+ "(hotelId, type, serviceDate, serviceType, amount, userId, payee, invoiceNumber, memo, chequeNo, accountName, "
				+ "paymentType, employeeId) "
				+ "VALUES('" + escapeString(hotelId) + "', '" + escapeString(expenseType) + "', '"
				+ date + "', '" + serviceType + "', " + expenseAmount
				+ ", '" + escapeString(userId) + "', '" + escapeString(payeeName) + "', '" + escapeString(invoiceNumber) 
				+ "', '" + escapeString(details) + "', " + Integer.toString(cheque) + ", '" + bankAccount 
				+ "', '" + paymentType + "', '" + escapeString(employeeId) + "');";
		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public ArrayList<Expense> getExpenses(String hotelId) {

		IService serviceDao = new ServiceManager(false);
		String sql = "SELECT * FROM Expenses WHERE hotelId = '" + hotelId + "' AND type != 'PAYIN' AND serviceDate = '"
				+ serviceDao.getServiceDate(hotelId) + "';";
		return db.getRecords(sql, Expense.class, hotelId);
	}

	@Override
	public Expense getExpense(String hotelId, int expenseId) {

		String sql = "SELECT * FROM Expenses WHERE hotelId = '" + hotelId + "' AND id = " + expenseId;
		
		return db.getOneRecord(sql, Expense.class, hotelId);
	}

	@Override
	public ArrayList<Expense> getPayIns(String hotelId) {

		IService serviceDao = new ServiceManager(false);
		String sql = "SELECT * FROM Expenses WHERE hotelId = '" + hotelId + "' AND type == 'PAYIN' AND serviceDate = '"
				+ serviceDao.getServiceDate(hotelId) + "';";
		return db.getRecords(sql, Expense.class, hotelId);
	}

	@Override
	public boolean deleteExpense(String hotelId , int expenseId, String section, String paymentType, BigDecimal amount) {

		String sql = "DELETE FROM Expenses WHERE hotelId = '"+hotelId+"' AND id = "+ expenseId;
		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public boolean clearFloat(String hotelId , int expenseId, String userId, String authoriser, String section
			, String accountName, String paymentType, BigDecimal amount, String employeeId) {

		this.deleteExpense(hotelId, expenseId, section, paymentType, amount);
		ITransactionHistory transHistoryDao = new TransactionHistoryManager(false);
		
		return transHistoryDao.updateTransactionHistory(hotelId, "DEBIT", ExpenseType.FLOAT.toString(), accountName, paymentType, 
				amount, employeeId, userId, authoriser);
	}

	@Override
	public BigDecimal getTotalCashExpenses(String outletId, ServiceLog service) {
		
		String sql = "SELECT IFNULL(SUM(amount), 0) AS entityId FROM Expenses WHERE hotelId = '"+outletId+"' "
				+ "AND serviceDate = '"+service.getServiceDate() + "' "
				+ "AND serviceType = '"+service.getServiceType()+"' "
				+ "AND type != 'PAYIN'"
				+ "AND paymentType == 'CASH'";
		
		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, outletId);
		
		return entity.getId();
	}

	@Override
	public BigDecimal getTotalCashPayIns(String outletId, ServiceLog service) {
		
		String sql = "SELECT IFNULL(SUM(amount), 0) AS entityId FROM Expenses WHERE hotelId = '"+outletId+"' "
				+ "AND serviceDate = '"+service.getServiceDate() + "' "
				+ "AND serviceType = '"+service.getServiceType()+"' "
				+ "AND type == 'PAYIN'"
				+ "AND paymentType == 'CASH'";
		
		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, outletId);
		
		return entity.getId();
	}
}
