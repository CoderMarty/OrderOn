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
import com.orderon.dao.AccessManager.OnlineOrderingSalesReport;
import com.orderon.dao.AccessManager.Order;
import com.orderon.dao.AccessManager.PaymentWiseSalesReport;
import com.orderon.dao.AccessManager.Report;
import com.orderon.dao.AccessManager.ReturnedItemsReport;
import com.orderon.dao.AccessManager.YearlyReport;

public interface IReport {
	
	public MonthReport getTotalOrdersForCurMonth(String systemId, String duration);
	
	public BigDecimal getPendingSale(String systemId);

	public MonthReport getBestWaiter(String systemId, String duration);

	public ArrayList<MonthReport> getWeeklyRevenue(String systemId);

	public ArrayList<YearlyReport> getYearlyOrders(String systemId);

	public MonthReport getMostOrderedItem(String systemId, String duration);

	public ArrayList<Expense> getExpenseReport(String systemId, String startDate, String endDate, String filter);
	
	public ArrayList<Expense> getPayInReport(String systemId, String startDate, String endDate);

	public ArrayList<DailyDiscountReport> getDailyDiscountReport(String systemId, String startDate, String endDate);

	public ArrayList<GrossSaleReport> getGrossSalesReport(String systemId, String outletId, String startDate, String endDate);

	public ArrayList<CollectionWiseReportA> getCollectionWiseReportA(String systemId, String outletId, String startDate, String endDate);

	public ArrayList<CollectionWiseReportB> getCollectionWiseReportB(String systemId, String outletId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport1(String systemId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport2(String systemId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport3(String systemId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport4(String systemId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport5(String systemId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport6(String systemId, String startDate, String endDate);

	public ArrayList<DailyOperationReport> getDailyOperationReport7(String systemId, String startDate, String endDate);

	public ArrayList<DiscountReport> getDiscountReport(String systemId, String startDate, String endDate);

	public ArrayList<ItemWiseReport> getItemwiseReport(String systemId, String outletId, String startDate, String endDate);

	public ArrayList<ItemWiseReport> getLiquorReport(String systemId, String outletId, String startDate, String endDate);

	public ArrayList<Order> getVoidOrderReport(String systemId, String outletId, String startDate, String endDate);

	public ArrayList<Order> getNCOrderReport(String systemId, String outletId, String startDate, String endDate);
	
	public ArrayList<ReturnedItemsReport> getReturnedItemsReport(String systemId, String outletId, String startDate, String endDate);
	
	public ArrayList<ReturnedItemsReport> getComplimentaryItemsReport(String systemId, String outletId, String startDate, String endDate);

	public PaymentWiseSalesReport getPaymentWiseSalesReport(String systemId, String outletId, String startDate, String endDate,
			Integer i);

	public ArrayList<Attendance> getAttendanceReport(String systemId, String startDate, String endDate);

	public ArrayList<Attendance> getAttendanceReportB(String systemId, String startDate, String endDate);

	public Report getTotalSalesForService(String systemId, String outletId, String serviceDate, String serviceType);

	public Report getTotalSalesForDay(String systemId, String serviceDate, String serviceType);

	public Report getTotalSalesForMonth(String systemId, String month);

	public ArrayList<Expense> getCashExpenses(String systemId, String outletId, String serviceDate, String serviceType);

	public BigDecimal getCardPaymentByType(String systemId, String outletId, String serviceDate, String serviceType, String cardType);

	public int getVoidTransactions(String systemId, String outletId, String serviceDate, String serviceType);

	public ArrayList<Report> getSaleSummaryReport(String systemId, String outletId, String startDate, String endDate, int orderType, int portalId);

	public ArrayList<CustomerReport> getCustomerReport(String systemId, String startDate, String endDate);

	public ArrayList<CustomerReport> getCustomerReviewReport(String systemId, String startDate, String endDate);

	public Report getDailyIncome(String systemId, String outletId, String startDate, String endDate, int inhouse);

	public ArrayList<Expense> getDailyExpense(String systemId, String startDate, String endDate);

	public ArrayList<IncentiveReport> getIncentivisedItemReport(String systemId, String outletId);
	
	public IncentiveReport getIncentiveForEmployee(String systemId, String outletId, String userId, boolean isBar, String startDate,
			String endDate);

	public ArrayList<IncentiveReport> getItemwiseIncentiveReport(String systemId, String outletId, String userId, String startDate,
			String endDate, boolean isBar);

	public ArrayList<DeliveryReport> getDeliveryReport(String systemId, String outletId, String userId, String startDate,
			String endDate);
	
	public ArrayList<ConsumptionReport> getConsumptionReport(String systemId, String outletId, String startDate, String endDate, int department);
	
	public ArrayList<OnlineOrderingSalesReport> getOnlineOrderingSalesReport(String systemId, String outletId, String startDate, String endDate);
	
	public BigDecimal getBarComplimentary(String systemId, String outletId, String serviceDate, String serviceType);
	
	public Report getAnalytics(String systemId, String outletId, String startDate, String endDate);
	
	public int getAppPaymentByType(String systemId, String outletId, String startDate, String endDate, String serviceType, String cardType);
}
