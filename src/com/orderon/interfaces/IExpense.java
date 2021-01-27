package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Expense;
import com.orderon.dao.AccessManager.ServiceLog;

public interface IExpense {

	public boolean addExpense(String hotelId, BigDecimal expenseAmount, String details, String payeeName, String invoiceNumber, int cheque,
			String paymentType, String expenseType, String bankAccount, String userId, String employeeId, String date, String serviceType);
	
	public ArrayList<Expense> getExpenses(String hotelId);
	
	public Expense getExpense(String hotelId, int expenseId);
	
	public ArrayList<Expense> getPayIns(String hotelId);
	
	public boolean deleteExpense(String hotelId , int expenseId, String section, String paymentType, BigDecimal amount);
	
	public boolean clearFloat(String hotelId , int expenseId, String userId, String authoriser, String section
			, String accountName, String paymentType, BigDecimal amount, String employeeId);
	
	public BigDecimal getTotalCashExpenses(String outletId, ServiceLog service);
	
	public BigDecimal getTotalCashPayIns(String outletId, ServiceLog service);
}
