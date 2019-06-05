package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Attendance;
import com.orderon.dao.AccessManager.CollectionWiseReportA;
import com.orderon.dao.AccessManager.CollectionWiseReportB;
import com.orderon.dao.AccessManager.ConsumptionReport;
import com.orderon.dao.AccessManager.CustomerReport;
import com.orderon.dao.AccessManager.DailyDiscountReport;
import com.orderon.dao.AccessManager.DailyOperationReport;
import com.orderon.dao.AccessManager.DeliveryReport;
import com.orderon.dao.AccessManager.DiscountReport;
import com.orderon.dao.AccessManager.Expense;
import com.orderon.dao.AccessManager.GrossSaleReport;
import com.orderon.dao.AccessManager.IncentiveReport;
import com.orderon.dao.AccessManager.ItemWiseReport;
import com.orderon.dao.AccessManager.MonthReport;
import com.orderon.dao.AccessManager.Order;
import com.orderon.dao.AccessManager.PaymentWiseSalesReport;
import com.orderon.dao.AccessManager.Report;
import com.orderon.dao.AccessManager.ReturnedItemsReport;
import com.orderon.dao.AccessManager.YearlyReport;

public interface IReport {
	
	public MonthReport getTotalOrdersForCurMonth(String hotelId, String duration, boolean visible);
	
	public BigDecimal getPendingSale(String hotelId);

	public MonthReport getBestWaiter(String hotelId, String duration, boolean visible);

	public ArrayList<MonthReport> getWeeklyRevenue(String hotelId, boolean visible);

	public ArrayList<YearlyReport> getYearlyOrders(String hotelId, boolean visible);

	public MonthReport getMostOrderedItem(String hotelId, String duration, boolean visible);

	public ArrayList<Expense> getExpenseReport(String hotelid, String startDate, String endDate, String filter);
	
	public ArrayList<Expense> getPayInReport(String hotelid, String startDate, String endDate);

	public ArrayList<DailyDiscountReport> getDailyDiscountReport(String hotelId, String startDate, String endDate, boolean visible);

	public ArrayList<GrossSaleReport> getGrossSalesReport(String hotelId, String startDate, String endDate, boolean visible);

	public ArrayList<CollectionWiseReportA> getCollectionWiseReportA(String hotelId, String startDate, String endDate);

	public ArrayList<CollectionWiseReportB> getCollectionWiseReportB(String hotelId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport1(String hotelId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport2(String hotelId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport3(String hotelId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport4(String hotelId, String startDate, String endDate, boolean visible);

	public ArrayList<DailyOperationReport> getDailyOperationReport5(String hotelId, String startDate, String endDate, boolean visible);

	public ArrayList<DailyOperationReport> getDailyOperationReport6(String hotelId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport7(String hotelId, String startDate, String endDate);

	public ArrayList<DiscountReport> getDiscountReport(String hotelId, String startDate, String endDate);

	public ArrayList<ItemWiseReport> getItemwiseReport(String hotelId, String startDate, String endDate);

	public ArrayList<ItemWiseReport> getLiquorReport(String hotelId, String startDate, String endDate);

	public ArrayList<Order> getVoidOrderReport(String hotelId, String startDate, String endDate);

	public ArrayList<Order> getNCOrderReport(String hotelId, String startDate, String endDate);
	
	public ArrayList<ReturnedItemsReport> getReturnedItemsReport(String hotelId, String startDate, String endDate, boolean visible);
	
	public ArrayList<ReturnedItemsReport> getComplimentaryItemsReport(String hotelId, String startDate, String endDate, boolean visible);

	public PaymentWiseSalesReport getPaymentWiseSalesReport(String hotelId, String startDate, String endDate,
			Integer i, boolean visible);

	public ArrayList<Attendance> getAttendanceReport(String hotelId, String startDate, String endDate);

	public ArrayList<Attendance> getAttendanceReportB(String hotelId, String startDate, String endDate);

	public Report getTotalSalesForService(String hotelId, String serviceDate, String serviceType, String section, boolean visible);

	public Report getTotalSalesForDay(String hotelId, String serviceDate, String serviceType, String section, boolean visible);

	public Report getTotalSalesForMonth(String hotelId, String month, String section, boolean visible);

	public ArrayList<Expense> getCashExpenses(String hotelId, String serviceDate, String serviceType, String section);

	public BigDecimal getCardPaymentByType(String hotelId, String serviceDate, String serviceType, String cardType);

	public int getAppPaymentByType(String hotelId, String serviceDate, String serviceType, String cardType);

	public int getVoidTransactions(String hotelId, String serviceDate, String serviceType);

	public BigDecimal getTotalCardPayment(String hotelId, String serviceDate, String serviceType);

	public BigDecimal getTotalAppPayment(String hotelId, String serviceDate, String serviceType);

	public BigDecimal getTotalWalletPayment(String hotelId, String serviceDate, String serviceType);

	public BigDecimal getTotalPromotionalCash(String hotelId, String serviceDate, String serviceType);
	
	public BigDecimal getTotalCreditAmount(String hotelId, String serviceDate, String serviceType);

	public ArrayList<Report> getSaleSummaryReport(String hotelId, String startDate, String endDate, boolean visible);

	public ArrayList<CustomerReport> getCustomerReport(String hotelId, String startDate, String endDate);

	public ArrayList<CustomerReport> getCustomerReviewReport(String hotelId, String startDate, String endDate);

	public Report getDailyIncome(String hotelId, String startDate, String endDate, int inhouse);

	public ArrayList<Expense> getDailyExpense(String hotelId, String startDate, String endDate);

	public ArrayList<IncentiveReport> getIncentivisedItemReport(String hotelId);
	
	public IncentiveReport getIncentiveForEmployee(String hotelId, String userId, boolean isBar, String startDate,
			String endDate);

	public ArrayList<IncentiveReport> getItemwiseIncentiveReport(String hotelId, String userId, String startDate,
			String endDate, boolean isBar);

	public ArrayList<DeliveryReport> getDeliveryReport(String hotelId, String userId, String startDate,
			String endDate, boolean visible);
	
	public ArrayList<ConsumptionReport> getConsumptionReport(String hotelId, String startDate, String endDate, int department);
}
