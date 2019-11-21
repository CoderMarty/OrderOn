package com.orderon.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.orderon.commons.CardType;
import com.orderon.commons.OnlineOrderingPortals;
import com.orderon.commons.OnlinePaymentType;
import com.orderon.interfaces.IOnlineOrderingPortal;
import com.orderon.interfaces.IReport;
import com.orderon.interfaces.IService;

public class ReportManager extends AccessManager implements IReport{

	public ReportManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public MonthReport getTotalOrdersForCurMonth(String systemId, String duration) {
		String sql = "SELECT COUNT(orderId) as count FROM Orders WHERE orderDate LIKE'" + escapeString(duration)
				+ "%' AND systemId='" + systemId + "' ";

		return db.getOneRecord(sql, MonthReport.class, systemId);
	}
	
	@Override
	public BigDecimal getPendingSale(String systemId) {
		
		IService serviceDao = new ServiceManager(false);
		
		String sql = "Select SUM(subTotal+tax1+tax2 ) AS entityId FROM " + 
				"(Select subTotal, (subTotal*taxPercentValue)/100 AS tax1, taxFixedValue AS tax2 FROM " + 
				"(Select SUM(rate*quantity) AS subTotal, MenuItems.taxes as taxes," + 
				"IFNULL((SELECT SUM(value) FROM Taxes WHERE LIKE('%' || Taxes.id|| '%', taxes)=1 AND Taxes.type == 'PERCENTAGE' AND Taxes.isActive = 'true'), 0) AS taxPercentValue," + 
				"IFNULL((SELECT SUM(value) FROM Taxes WHERE LIKE('%' || Taxes.id|| '%', taxes)=1 AND Taxes.type == 'FIXED' AND Taxes.isActive = 'true'), 0) AS taxFixedValue " + 
				"FROM OrderItems, MenuItems, Orders " + 
				"WHERE OrderItems.menuId == MenuItems.menuId " + 
				"AND OrderItems.orderId == Orders.orderId " + 
				"AND Orders.orderDate = '" + serviceDao.getServiceDate(systemId) + "' " +
				"AND (Orders.state == 1 OR Orders.state == 0) " + 
				"GROUP BY OrderItems.menuId))";
		
		return db.getOneRecord(sql, EntityBigDecimal.class, systemId).getId();
	}

	@Override
	public MonthReport getBestWaiter(String systemId, String duration) {

		String sql = "SELECT waiterId AS user, SUBSTR(subOrderDate, 1, " + duration.length()
				+ ") AS duration, count(*) AS waitersOrders, employeeID FROM OrderItems, Users WHERE duration = '"
				+ escapeString(duration) + "' AND Users.userId = user "
				+ "GROUP BY user ORDER BY count(*) desc  LIMIT 1;";
		return db.getOneRecord(sql, MonthReport.class, systemId);
	}

	@Override
	public ArrayList<MonthReport> getWeeklyRevenue(String systemId) {

		ArrayList<MonthReport> weeklyRevenue = new ArrayList<MonthReport>();

		String duration = "";

		for (int i = 0; i < 7; i++) {

			duration = this.getPreviousDateString(i);
			String sql = "SELECT SUM(total) AS totalSales FROM Payments WHERE orderDate = '" + duration + "' ";
			
			MonthReport report = db.getOneRecord(sql, MonthReport.class, systemId);
			weeklyRevenue.add(report);
		}
		return weeklyRevenue;
	}

	@Override
	public ArrayList<YearlyReport> getYearlyOrders(String systemId) {

		ArrayList<YearlyReport> out = new ArrayList<YearlyReport>();

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		String duration = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM");

		for (int i = 0; i < 9; i++) {
			duration = dateFormat.format(cal.getTime());
			String sql = "SELECT count(Id) AS totalOrders FROM Orders WHERE orderDate LIKE '" + duration + "%' ";
			
			YearlyReport report = db.getOneRecord(sql, YearlyReport.class, systemId);
			report.month = month + 1;
			out.add(report);
			cal.add(Calendar.MONTH, -1);
			month = cal.get(Calendar.MONTH);

		}
		return out;
	}

	@Override
	public MonthReport getMostOrderedItem(String systemId, String duration) {

		String sql = "SELECT OrderItems.menuId as itemId, SUBSTR(subOrderDate, 1, " + duration.length()
				+ ") AS duration, MenuItems.title AS title, SUM(quantity) AS orderCount, imgUrl FROM OrderItems, MenuItems "
				+ "WHERE duration = '" + escapeString(duration) + "' AND MenuItems.menuId = OrderItems.menuId "
				+ " AND MenuItems.collection != 'Roti' "
				+ " GROUP BY itemId ORDER BY orderCount desc LIMIT 1;";
		
		return db.getOneRecord(sql, MonthReport.class, systemId);
	}

	// Reports
	// expense-ap
	@Override
	public ArrayList<Expense> getExpenseReport(String systemId, String startDate, String endDate, String filter) {

		String filterText = ";";
		if(filter.equals("CASH")) {
			filterText = " AND paymentType = 'CASH';";
		}else if(filter.equals("BANK")){
			filterText = " AND paymentType != 'CASH';";
		}
		
		String sql = "SELECT *, "
			+ "REPLACE(REPLACE(REPLACE(Expenses.memo,CHAR(13),''),CHAR(10),''),',','|') AS description "
			+ "FROM Expenses WHERE type != 'PAYIN'" 
			+ "AND serviceDate " + getDateString(startDate, appendEndDate(endDate)) + filterText;
		return db.getRecords(sql, Expense.class, systemId);
	}
	
	// expense-ap
	@Override
	public ArrayList<Expense> getPayInReport(String systemId, String startDate, String endDate) {

		String sql = "SELECT *, "
			+ "REPLACE(REPLACE(REPLACE(Expenses.memo,CHAR(13),''),CHAR(10),''),',','|') AS description "
			+ "FROM Expenses WHERE type == 'PAYIN'" 
			+ "AND serviceDate " + getDateString(startDate, appendEndDate(endDate)) + ";";
		return db.getRecords(sql, Expense.class, systemId);
	}

	// new-ap(in progress)
	@Override
	public ArrayList<DailyDiscountReport> getDailyDiscountReport(String systemId, String startDate, String endDate) {
		
		endDate = appendEndDate(endDate);
		String dateStr = getDateString(startDate, endDate);
		
		String sql = "SELECT *, ROUND(ordersAffected/totalOrders*10000)/100 AS ordersDiscountedPer, "
				+ "(ROUND(sumDiscount/sumTotal*10000)/100)||' %' AS discountPer,"
				+ "(ROUND(avgDiscount/avgTotal*10000)/100)||' %' AS avgDiscountPer,"
				+ "(ROUND(grossDiscount/grossSale*10000)/100)||' %' AS grossDiscountPer "
				+ " FROM "
				+ "(SELECT Discounts.name AS name, "
				+ "CASE Discounts.type WHEN 1 THEN 'Rs '||Discounts.foodValue else Discounts.foodValue||' %' END AS foodValue, "
				+ "CASE Discounts.type WHEN 1 THEN 'Rs '||Discounts.barValue else Discounts.barValue||' %' END AS barValue, "
				+ "Discounts.description AS description, "
				+ "(ROUND(SUM(foodBill+barBill)*100)/100) AS sumTotal, "
				+ "(ROUND(AVG(foodBill+barBill)*100)/100) AS avgTotal, "
				+ "(ROUND(AVG(foodDiscount+barDiscount)*100)/100) AS avgDiscount, "
				+ "(ROUND(SUM(foodDiscount+barDiscount)*100)/100) AS sumDiscount, "
				+ "(ROUND(SUM(foodBill+barBill-foodDiscount-barDiscount)*100)/100) AS sumDiscountedTotal, "
				+ "COUNT(Payments.Id) AS ordersAffected, "
				+ "(SELECT COUNT(orderId) FROM Payments WHERE orderDate " 
				+ dateStr + " AND paymentType != 'VOID') AS totalOrders, " 
				+ "(SELECT ROUND(SUM(foodBill+barBill)*100)/100 FROM Payments WHERE orderDate " 
				+ dateStr + " AND paymentType != 'VOID') AS grossSale, " 
				+ "(SELECT ROUND(SUM(foodDiscount+barDiscount)*100)/100 FROM Payments WHERE orderDate " 
				+ dateStr + " AND paymentType != 'VOID') AS grossDiscount " 
				+ "FROM Payments, Discounts WHERE Payments.orderDate " + dateStr
				+ " AND Payments.discountCodes LIKE '%\"'||Discounts.name||'\"%' "
				+ " GROUP BY Discounts.name)";
		
		return db.getRecords(sql, DailyDiscountReport.class, systemId);
	}

	// new-ap
	// refere googlesheets for logic...
	@Override
	public ArrayList<GrossSaleReport> getGrossSalesReport(String systemId, String outletId, String startDate, String endDate) {
		endDate = appendEndDate(endDate);
		String dateStr = getDateString(startDate, endDate);
		
		String outletStr = this.outletFilter(outletId, "OrderItemLog");
		String outletStr2 = this.outletFilter(outletId, "Orders");
		
		String sql = "SELECT ROUND(SUM(Payments.foodBill+Payments.barBill+Payments.gst+Payments.vat+Payments.serviceCharge+Payments.deliveryCharge+Payments.packagingCharge)*100)/100 as grossTotal, "
				+ "ROUND(SUM(Payments.foodDiscount)*100)/100 as foodDiscount, "
				+ "ROUND(SUM(Payments.barDiscount)*100)/100 as barDiscount, "
				+ "ROUND(SUM(Payments.foodDiscount+Payments.barDiscount)*100)/100 as totalDiscount, "
				+ "ROUND(SUM(Payments.loyaltyAmount)*100)/100 as grossLoyalty, "
				+ "ROUND(SUM(Payments.complimentary)*100)/100 as grossComplimentary, "
				+ "ROUND(SUM(Payments.gst)*100)/100 as grossGst, "
				+ "ROUND(SUM(Payments.vat)*100)/100 as grossVatBar, "
				+ "ROUND(SUM(Payments.roundOff)*100)/100 as roundOff, "
				+ "ROUND(SUM(Payments.deliveryCharge)*100)/100 as grossDeliveryCharge, "
				+ "ROUND(SUM(Payments.packagingCharge)*100)/100 as grossPackagingCharge, "
				+ "ROUND(SUM(Payments.foodBill)*100)/100 as foodBill, "
				+ "ROUND(SUM(Payments.barBill)*100)/100 as barBill, "
				+ "ROUND(SUM(Payments.cardPayment)*100)/100 as cardPayment, "
				+ "ROUND(SUM(Payments.appPayment)*100)/100 as appPayment, "
				+ "ROUND(SUM(Payments.cashPayment)*100)/100 as cashPayment, "
				+ "ROUND(SUM(Payments.walletPayment)*100)/100 as walletPayment, "
				+ "ROUND(SUM(Payments.promotionalCash)*100)/100 as promotionalCash, "
				+ "ROUND(SUM(Payments.cashPayment + Payments.cardPayment + Payments.appPayment + Payments.walletPayment + Payments.creditAmount + Payments.promotionalCash - Payments.total)*100)/100 as roundOffDifference, "
				+ "ROUND(SUM(Payments.cashPayment + Payments.cardPayment + Payments.appPayment + Payments.walletPayment + Payments.promotionalCash)*100)/100 as totalPayment, "
				+ "ROUND(SUM(Payments.creditAmount)*100)/100 as creditAmount, "
				+ "ROUND(SUM(Payments.serviceCharge)*100)/100 as grossServiceCharge, "
				+ "ROUND(SUM(Payments.total)*100)/100 as totalSale, "
				+ "ROUND((SUM(Payments.total)"
						+ "-(Select ifnull(SUM(Expenses.amount),0) From Expenses Where (expenses.type!='CASH_LIFT' OR expenses.type!='PAYIN') AND expenses.serviceDate "+ dateStr + " AND expenses.hotelId='" + systemId + "')"
						+ "+(Select ifnull(SUM(Expenses.amount),0) From Expenses Where expenses.type=='PAYIN' AND expenses.serviceDate "+ dateStr + " AND expenses.hotelId='" + systemId + "')"
						+ ")*100)/100 as netSales, "
				+ "ROUND((Select ifnull(SUM(Expenses.amount),0) From Expenses Where (expenses.type!='CASH_LIFT' OR expenses.type!='PAYIN') AND expenses.serviceDate "
				+ dateStr + " AND expenses.hotelId='" + systemId + "')*100)/100 as grossExpenses, "
				+ "ROUND((Select ifnull(SUM(Expenses.amount),0) From Expenses Where expenses.type=='PAYIN' AND expenses.serviceDate "
				+ dateStr + " AND expenses.hotelId='" + systemId + "')*100)/100 as totalPayIns, "
				+ "ROUND((Select SUM(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='99' "
				+ outletStr +" AND OrderItemLog.dateTime " + dateStr + ")*100)/100  as sumVoids,  "
				+ "(Select COUNT(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='99' "
				+ outletStr + " AND OrderItemLog.dateTime " + dateStr + ")  as countVoids,  "
				+ "ROUND((Select SUM(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='100' "
				+ outletStr + " AND OrderItemLog.dateTime " + dateStr + ")*100)/100  as sumReturns, "
				+ "(Select COUNT(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='100' "
				+ outletStr + " AND OrderItemLog.dateTime " + dateStr
				+ ")  as countReturns FROM Orders, Payments Where Orders.orderId == Payments.orderId " + outletStr2
				+ "AND Orders.orderDate " + dateStr + " AND Payments.paymentType != 'VOID'";
		
		return db.getRecords(sql, GrossSaleReport.class, systemId);
	}
	
	// newCollectionWiseReportA-ap (gross and totals...)
	@Override
	public ArrayList<CollectionWiseReportA> getCollectionWiseReportA(String systemId, String outletId, String startDate, String endDate) {
		endDate = appendEndDate(endDate);
		String dateStr = getDateString(startDate, endDate);
		
		String outletStr = this.outletFilter(outletId, "OrderItems");
		
		String sql = "Select DISTINCT(MenuItems.collection) AS collection, "
				+ "ROUND(SUM(OrderItems.quantity*OrderItems.rate*100))/100 AS grossTotal, "
				+ "ROUND(AVG(OrderItems.quantity*OrderItems.rate)*100)/100 AS averagePrice, "
				+ "COUNT(OrderItems.menuId) AS noOrdersAffected, "
				+ "(ROUND(CAST(COUNT(OrderItems.menuId) AS FLOAT)/(SELECT CAST(SUM(OrderItems.quantity) AS FLOAT) FROM OrderItems WHERE "
				+ " OrderItems.subOrderDate " + outletStr + dateStr
				+ ")*100*100)/100)||' %' AS noOrdersAffectedPer, SUM(OrderItems.quantity) AS totalQuantityOrdered, "
				+ "(ROUND(CAST(SUM(OrderItems.quantity) AS FLOAT)/(SELECT CAST(SUM(OrderItems.quantity) AS FLOAT) FROM OrderItems WHERE "
				+ " OrderItems.subOrderDate " + outletStr + dateStr
				+ ")*100*100)/100)||' %' AS totalQuantityOrderedPer FROM OrderItems,MenuItems "
				+ "WHERE OrderItems.menuId=MenuItems.menuId " + outletStr
				+ "AND OrderItems.subOrderDate " + dateStr
				+ "GROUP BY MenuItems.collection;";
		
		return db.getRecords(sql, CollectionWiseReportA.class, systemId);
	}

	// newCollectionWiseReportB-ap (top/hot selling item!)
	@Override
	public ArrayList<CollectionWiseReportB> getCollectionWiseReportB(String systemId, String outletId, String startDate, String endDate) {
		endDate = appendEndDate(endDate);
		
		String sql = "SELECT title AS topItemTitle, max(SUM) "
				+ "FROM (SELECT MenuItems.collection, MenuItems.title, OrderItems.menuId, SUM(OrderItems.quantity) AS SUM "
				+ "FROM OrderItems, MenuItems "
				+ "WHERE OrderItems.menuId = MenuItems.menuId AND OrderItems.subOrderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "OrderItems")
				+ " GROUP BY MenuItems.menuId "
				+ "ORDER BY MenuItems.title) GROUP BY collection;";
		
		return db.getRecords(sql, CollectionWiseReportB.class, systemId);
	}
	
	public void inventoryReprt() {
		/*
		 * select group_concat(menuId), ROUND(SUM(quantity)*100)/100,* FROM 
(select StockLog.menuId AS menuId, StockLog.quantity AS quantity, StockLog.sku AS sku, Material.name AS name from StockLog, orders, Material 
where crud = 'USEDUP' AND StockLog.orderId == Orders.orderId AND Orders.orderDate between '2018/10/01' AND '2018/12/30'
 AND Material.sku == StockLog.sku 
 Order BY StockLog.menuId) 
 GROUP BY sku
		 */
	}

	// total operating cost-ap
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport1(String systemId, String startDate, String endDate) {
		endDate = appendEndDate(endDate);
		String sql = "SELECT ROUND(SUM(Expenses.amount)*100)/100 AS  totalOperatingCost, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='INVENTORY' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId + "')*100)/100 AS INVENTORY, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='LABOUR' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId + "')*100)/100 AS LABOUR, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='RENT' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId + "')*100)/100 AS RENT, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='ELECTRICITY_BILL' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId
				+ "')*100)/100 AS ELECTRICITY_BILL, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='GAS_BILL' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId + "')*100)/100 AS GAS_BILL, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='PETROL' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId + "')*100)/100 AS PETROL, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='TELEPHONE_BILL' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId
				+ "')*100)/100 AS TELEPHONE_BILL, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='MOBILE_RECHARGE' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId
				+ "')*100)/100 AS MOBILE_RECHARGE, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='INTERNET' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId + "')*100)/100 AS INTERNET, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='SOFTWARE' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId + "')*100)/100 AS SOFTWARE, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='COMPUTER_HARDWARE' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId
				+ "')*100)/100 AS COMPUTER_HARDWARE, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='REPAIRS' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId + "')*100)/100 AS REPAIRS, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='OTHERS' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId + "')*100)/100 AS OTHERS, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='CASH_LIFT' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + systemId + "')*100)/100 AS CASH_LIFT "
				+ "FROM Expenses WHERE Expenses.hotelId='" + systemId + "' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "';";
		
		return db.getRecords(sql, DailyOperationReport.class, systemId);
	}

	// total revenue-ap
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport2(String systemId, String startDate, String endDate) {
		String sql = "SELECT (SELECT ROUND(SUM(TotalRevenue.total)*100)/100 FROM TotalRevenue WHERE TotalRevenue.hotelId='"
				+ systemId + "' AND TotalRevenue.serviceDate BETWEEN '" + startDate + "' AND '" + endDate
				+ "' ) AS totalRevenue, ROUND(SUM(Payments.total)*100)/100 as grossTotal, "
				+ "ROUND(SUM(Payments.foodDiscount+ Payments.barDiscount)*100)/100 as grossDiscount, "
				+ "ROUND(SUM(Payments.gst)*100)/100 as grossTaxes, "
				+ "ROUND(SUM(Payments.serviceCharge)*100)/100 as grossServiceCharge, "
				+ "ROUND(ROUND((Select SUM(Payments.total) FROM Payments WHERE Payments.systemId='" + systemId
				+ "' AND Payments.orderDate " + getDateString(startDate, endDate)
				+ ")*100)/100 - ROUND((Select SUM(Expenses.amount) From Expenses WHERE expenses.type!='CASH_LIFT' AND Expenses.serviceDate "
				+ getDateString(startDate, endDate) + " AND expenses.hotelId='" + systemId
				+ "')*100)/100)*100/100 as NetSales FROM Payments WHERE Payments.systemId='" + systemId + "' "
				+ "AND Payments.orderDate "+getDateString(startDate, endDate)+";";
		
		return db.getRecords(sql, DailyOperationReport.class, systemId);
	}

	// Total Operating Margin-ap //implementvisible
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport3(String systemId, String startDate, String endDate) {
		String sql = "SELECT ROUND((SUM(TotalRevenue.total) - (SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.hotelId='"
				+ systemId + "' AND Expenses.serviceDate BETWEEN '" + startDate + "' AND '" + appendEndDate(endDate)
				+ "'))*100)/100 AS totalOperatingMargin, "
				+ "ROUND((SELECT SUM(Payments.Total) FROM Payments WHERE Payments.orderDate " + getDateString(startDate, endDate) 
				+ ")*100)/100 AS paidIn, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type!='CASH_LIFT' AND expenses.hotelId='"
				+ systemId + "' AND Expenses.serviceDate " + getDateString(startDate, endDate)
				+ ")*100)/100 AS paidOut FROM TotalRevenue Where hotelId='" + systemId + "' "
				+ "AND TotalRevenue.serviceDate BETWEEN '" + startDate + "' AND '" + endDate + "';";
		
		return db.getRecords(sql, DailyOperationReport.class, systemId);
	}

	// Operating Metrics-ap
	// main 3
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport4(String systemId, String startDate, String endDate) {
		
		String sql = "SELECT Distinct Orders.serviceType AS serviceType, "
				+ "SUM(Payments.total)/SUM(Orders.numberOfGuests) AS AvgAmountPerGuest, "
				+ "(SUM(Payments.total)/COUNT(Payments.billNo)) AS AvgAmountPerCheck, SUM(Payments.total) AS Total, "
				+ "SUM(Orders.numberOfGuests) AS noOfGuests, COUNT(Payments.billNo) AS noOfBills "
				+ "FROM Orders, Payments WHERE  Orders.orderId=Payments.orderId AND Orders.orderDate " + getDateString(startDate, endDate);

		sql += "GROUP BY Orders.serviceType ";
		
		return db.getRecords(sql, DailyOperationReport.class, systemId);
	}

	// tables turned-ap
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport5(String systemId, String startDate, String endDate) {
		
		String sql = "SELECT SUM(Payments.total)/COUNT(distinct Payments.billNo) as AvgAmountPerTableTurned "
				+ "FROM Payments,Orders WHERE Payments.orderId=orders.orderId AND orders.orderType='1' "
				+ "AND orders.serviceType=serviceType "
				+ "AND orders.orderDate " + getDateString(startDate, endDate)
				+ " GROUP BY Orders.serviceType ";
		
		return db.getRecords(sql, DailyOperationReport.class, systemId);
	}

	// voids-ap
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport6(String systemId, String startDate, String endDate) {
		String sql = "SELECT COUNT(orders.orderId) AS voids FROM orders WHERE orders.state='99' "
				+ "AND Orders.systemId='" + systemId + "' AND orders.orderDate " + getDateString(startDate, endDate)
				+ " GROUP BY Orders.serviceType;";
		
		return db.getRecords(sql, DailyOperationReport.class, systemId);
	}

	// returns-ap
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport7(String systemId, String startDate, String endDate) {
		String sql = "SELECT COUNT(Distinct OrderItemLog.orderId) AS returns FROM orderitemlog, orders "
				+ "WHERE orderitemlog.state='100' AND orderitemlog.orderId=orders.orderId "
				+ "AND OrderItemLog.systemId='" + systemId + "' AND orderitemlog.dateTime " + getDateString(startDate, endDate)
				+ "' GROUP BY Orders.serviceType;";
		
		return db.getRecords(sql, DailyOperationReport.class, systemId);
	}

	// Jason
	// Discount Report-ap(edited)
	@Override
	public ArrayList<DiscountReport> getDiscountReport(String systemId, String startDate, String endDate) {
		String sql = "SELECT Payments.discountCodes, Payments.orderDate, "
				+ "ROUND(Payments.foodDiscount*100)/100 AS foodDiscount, "
				+ "ROUND(Payments.barDiscount*100)/100 AS barDiscount, "
				+ "ROUND((Payments.foodDiscount+Payments.barDiscount)*100)/100 AS totalDiscount, "
				+ "ROUND((Payments.total+Payments.foodDiscount+Payments.barDiscount)*100)/100 AS total, "
				+ "customerName ,"
				+ "Payments.total AS discountedTotal "
				+ "FROM Payments, orders WHERE Payments.orderid = orders.orderid AND Payments.discountCodes!='[]' "
				+ "AND Payments.orderDate " + getDateString(startDate, endDate) + " ;";
		
		return db.getRecords(sql, DiscountReport.class, systemId);
	}

	// Jason
	// itemwise-ap(hot selling items/menu collection)
	@Override
	public ArrayList<ItemWiseReport> getItemwiseReport(String systemId, String outletId, String startDate, String endDate) {
		String sql = "SELECT menuId, station, collection, title, SUM(quantity) AS quantity FROM ("
				+ "SELECT Menuitems.menuid AS menuId, station, collection, title, "
				+ "SUM(quantity) AS quantity FROM MenuItems, Orderitems, Orders WHERE Menuitems.menuid = Orderitems.menuid "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "OrderItems") + " AND MenuItems.station!='Bar' "
				+ "AND Orderitems.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid UNION ALL "
				+ "SELECT Menuitems.menuid AS menuId, station, collection, title, "
				+ "SUM(OrderItemLog.quantity) AS quantity FROM MenuItems, OrderItemLog, Orders WHERE menuitems.menuid = OrderItemLog.menuid "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "OrderItemLog") + " AND menuItems.station!='Bar' AND OrderItemLog.state == 50 "
				+ "AND OrderItemLog.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid) "
				+ "GROUP BY menuId ORDER BY station, collection, title;";
		
		return db.getRecords(sql, ItemWiseReport.class, systemId);
	}

	// liquor-ap(hot selling items/menu collection)
	@Override
	public ArrayList<ItemWiseReport> getLiquorReport(String systemId, String outletId, String startDate, String endDate) {
		String sql = "SELECT menuId, station, collection, title, SUM(quantity) AS quantity FROM ("
				+ "SELECT Menuitems.menuid, station, collection, title, "
				+ "SUM(quantity) AS quantity FROM MenuItems, Orderitems, Orders WHERE menuitems.menuid = orderitems.menuid "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "Orders") + " AND menuItems.station='Bar' "
				+ "AND Orderitems.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid UNION ALL "
				+ "SELECT Menuitems.menuid, station, collection, title, "
				+ "SUM(OrderItemLog.quantity) AS quantity FROM MenuItems, OrderItemLog, Orders WHERE menuitems.menuid = OrderItemLog.menuid "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "OrderItemLog") + " AND menuItems.station='Bar' AND OrderItemLog.state == 50 "
				+ "AND OrderItemLog.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid) "
				+ "GROUP BY menuId ORDER BY station, collection, title;";
		
		return db.getRecords(sql, ItemWiseReport.class, systemId);
	}

	//Void Report
	@Override
	public ArrayList<Order> getVoidOrderReport(String systemId, String outletId, String startDate, String endDate) {
		String sql = "SELECT Orders.id, Orders.billNo, Orders.orderDate, Orders.waiterId, Orders.orderType, Orders.reason, "
				+ "IFNULL((SELECT rate*quantity FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId == Orders.orderId AND OrderItemLog.menuId == MenuItems.menuId AND MenuItems.station != 'Bar'), 0) AS foodBill, "
				+ "IFNULL((SELECT rate*quantity FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId == Orders.orderId AND OrderItemLog.menuId == MenuItems.menuId AND MenuItems.station == 'Bar'), 0) AS barBill, "
				+ "IFNULL((SELECT rate*quantity FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId == Orders.orderId AND OrderItemLog.menuId == MenuItems.menuId), 0) AS total "
				+ "FROM Orders, Payments WHERE Orders.state = 99 "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "Orders") + " AND Orders.orderId == Payments.orderId ORDER BY Orders.id;";
		
		return db.getRecords(sql, Order.class, systemId);
	}

	//Void Report
	@Override
	public ArrayList<Order> getNCOrderReport(String systemId, String outletId, String startDate, String endDate) {
		String sql = "SELECT Orders.id, Orders.billNo, Orders.orderDate, Orders.waiterId, Orders.orderType, Orders.reference, "
				+ "IFNULL((SELECT rate*quantity FROM OrderItems, MenuItems WHERE OrderItems.orderId == Orders.orderId AND OrderItems.menuId == MenuItems.menuId AND MenuItems.station != 'Bar'), 0) AS foodBill, "
				+ "IFNULL((SELECT rate*quantity FROM OrderItems, MenuItems WHERE OrderItems.orderId == Orders.orderId AND OrderItems.menuId == MenuItems.menuId AND MenuItems.station == 'Bar'), 0) AS barBill, "
				+ "IFNULL((SELECT rate*quantity FROM OrderItems, MenuItems WHERE OrderItems.orderId == Orders.orderId AND OrderItems.menuId == MenuItems.menuId), 0) AS total "
				+ "FROM Orders, Payments WHERE Orders.orderType = 4 "
				+ "AND Orders.orderId == Payments.orderId "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "Orders")
				+ "ORDER BY Orders.id;";
		
		return db.getRecords(sql, Order.class, systemId);
	}
	
	//Returned Item  Report
	@Override
	public ArrayList<ReturnedItemsReport> getReturnedItemsReport(String systemId, String outletId, String startDate, String endDate) {
		
		String sql = "SELECT Orders.id, Orders.billNo, Orders.orderDate, Orders.waiterId, Orders.orderType, "
				+ "MenuItems.title, OrderItemLog.dateTime, OrderItemLog.quantity, OrderItemLog.reason, OrderItemLog.rate "
				+ ", (OrderItemLog.rate*OrderItemLog.quantity) AS total FROM Orders, OrderItemlog, MenuItems WHERE (OrderItemlog.state = 100 OR OrderItemlog.state = 101) "
				+ "AND OrderItemlog.orderId == Orders.orderId "
				+ "AND OrderItemLog.menuId == MenuItems.menuId AND Orders.orderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "Orders");
		
		sql += " ORDER BY OrderItemlog.id;";
		
		return db.getRecords(sql, ReturnedItemsReport.class, systemId);
	}
	
	//Returned Item  Report
	@Override
	public ArrayList<ReturnedItemsReport> getComplimentaryItemsReport(String systemId, String outletId, String startDate, String endDate) {
		
		String sql = "SELECT Orders.id, Orders.billNo, Orders.orderDate, Orders.waiterId, Orders.orderType, "
				+ "MenuItems.title, OrderItemLog.dateTime, OrderItemLog.quantity, OrderItemLog.rate "
				+ ", (OrderItemLog.rate*OrderItemLog.quantity) AS total FROM Orders, OrderItemlog, MenuItems WHERE OrderItemlog.state = 50 "
				+ " AND OrderItemlog.dateTime " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "Orders") + "AND OrderItemlog.orderId == Orders.orderId "
				+ " AND OrderItemLog.menuId == MenuItems.menuId "
				+ " ORDER BY OrderItemlog.id;";
		
		return db.getRecords(sql, ReturnedItemsReport.class, systemId);
	}

	@Override
	public PaymentWiseSalesReport getPaymentWiseSalesReport(String systemId, String outletId, String startDate, String endDate,
			Integer i) {

		String[] cardTypes = this.getEnums(CardType.class);
		String[] onlinePaymentTypes = this.getEnums(OnlinePaymentType.class);
		
		String sql = "SELECT ROUND(SUM(Payments.foodBill)*100)/100 AS foodBill, ROUND(SUM(Payments.barBill)*100)/100 AS barBill, "
				+ "ROUND(SUM(Payments.total)*100)/100 AS total, "
				+ "SUM(Orders.numberOfGuests) AS cover, "
				+ "ROUND(SUM(Payments.cashPayment)*100)/100 AS cash, "
				+ "ROUND(SUM(Payments.cardPayment)*100)/100 AS card, "
				+ "ROUND(SUM(Payments.appPayment)*100)/100 AS app, "
				+ "ROUND(SUM(Payments.walletPayment)*100)/100 AS wallet, "
				+ "ROUND(SUM(Payments.promotionalCash)*100)/100 AS promotionalCash, "
				+ "ROUND(SUM(Payments.creditAmount)*100)/100 AS credit";
		
		for (String paymentType : cardTypes) {
			sql += ", (SELECT ifnull(ROUND(SUM(Payments.cardPayment)*100)/100, 0) FROM Payments,Orders WHERE Payments.paymentType LIKE '%"+paymentType+"%' "
				+ " AND Payments.orderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "Payments")
				+ " AND Orders.orderType='" + i + "' AND Payments.orderId = Orders.orderId ) AS "+paymentType;
		}
		for (String paymentType : onlinePaymentTypes) {
			sql += ", (SELECT ifnull(ROUND(SUM(Payments.appPayment)*100)/100, 0) FROM Payments,Orders WHERE (Payments.paymentType LIKE '%"+paymentType+"' OR Payments.paymentType LIKE '%"+paymentType+"/%') "
				+ " AND Payments.orderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "Payments")
				+ " AND Orders.orderType='" + i + "' AND Payments.orderId = Orders.orderId ) AS "+paymentType;
		}
		sql += " FROM Payments, Orders WHERE Payments.orderId = Orders.orderId "
				+ this.outletFilter(outletId, "Payments")
				+ " AND Payments.orderdate " + getDateString(startDate, endDate) + "AND Orders.orderType='"
				+ i + "' ";
		
		return db.getOneRecord(sql, PaymentWiseSalesReport.class, systemId);
	}

	@Override
	public ArrayList<OnlineOrderingSalesReport> getOnlineOrderingSalesReport(String systemId, String outletId, String startDate, String endDate) {

		String sql = "SELECT OnlineOrderingPortals.name AS portalName, "
				+ "OnlineOrderingPortals.id AS portalId, "
				+ "ROUND(SUM(Payments.foodBill)*100)/100 AS totalBillAmount, "
				+ "ROUND(SUM(Payments.total)*100)/100 AS netAmount, "
				+ "ROUND(SUM(Payments.gst)*100)/100 AS gst, "
				+ "ROUND(SUM(Payments.foodDiscount)*100)/100 AS discount, "
				+ "ROUND(SUM(Payments.appPayment)*100)/100 AS app, "
				+ "ROUND(SUM(Payments.packagingCharge)*100)/100 AS packagingCharge, "
				+ "ROUND(SUM(Payments.serviceCharge)*100)/100 AS serviceCharge, "
				+ "ROUND(SUM(Payments.deliveryCharge)*100)/100 AS deliveryCharge,"
				+ "OnlineOrderingPortals.commisionType, "
				+ "OnlineOrderingPortals.commisionValue,"
				+ "ROUND(SUM(Payments.total*commisionValue))/100 AS commisionAmount,"
				+ "ROUND(SUM(Payments.foodBill))/100 AS tds "
				+ "FROM Payments, orders "
				+ "LEFT OUTER JOIN OnlineOrderingPortals ON Orders.takeAwayType == OnlineOrderingPortals.id "
				+ "WHERE Payments.orderId == Orders.orderId AND Orders.orderType == 2 AND (Orders.takeAwayType != 100 ) "
				+ "AND Orders.orderDate "+ getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "Orders")
				+ "GROUP BY Orders.takeAwayType;";
		
		return db.getRecords(sql, OnlineOrderingSalesReport.class, systemId);
	}
	
	@Override
	public String[] getEnums(Class<? extends Enum<?>> e) {
	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}

	@Override
	public ArrayList<Attendance> getAttendanceReport(String systemId, String startDate, String endDate) {

		String sql = "SELECT Employee.employeeId, Employee.salary, Employee.firstName, Employee.surName, Attendance.shift, Attendance.checkInDate, Attendance.reason, Attendance.authorisation, Attendance.checkInTime, Attendance.checkOutTIme, "
				+ "REPLACE(REPLACE(REPLACE(Attendance.isPresent, " + PRESENT + ", \"PRESENT\") , " + ABSENT
				+ ", \"ABSENT\"), " + EXCUSED + ", \"EXCUSED\") AS attendanceStr, "
				+ "	(SELECT COUNT(isPresent) FROM Attendance WHERE 	Attendance.checkInDate " 
				+ getDateString(startDate, endDate) + " AND Attendance.employeeId = Employee.employeeId"
				+ "	AND Attendance.authorisation = 1 	AND Attendance.isPresent=2 " + " AND Attendance.hotelId = '"
				+ systemId + "' GROUP BY Attendance.employeeId ) AS excusedCount, (SELECT COUNT(isPresent) FROM Attendance "
				+ "	WHERE Attendance.checkInDate " + getDateString(startDate, endDate)
				+ "	AND Attendance.employeeId = Employee.employeeId	AND Attendance.authorisation = 1 "
				+ "	AND Attendance.isPresent=3 	AND Attendance.hotelId = '" + systemId + "' "
				+ "	GROUP BY Attendance.employeeId ) AS absentCount,	(SELECT COUNT(isPresent) FROM Attendance "
				+ "	WHERE Attendance.checkInDate " + getDateString(startDate, endDate)
				+ "	AND Attendance.employeeId = Employee.employeeId	AND Attendance.authorisation = 1 "
				+ "	AND Attendance.isPresent=1 	AND Employee.hotelId = '" + systemId + "' "
				+ "	GROUP BY Attendance.employeeId ) AS presentCount FROM Employee, Attendance "
				+ "WHERE Employee.employeeId = Attendance.employeeId AND Attendance.checkInDate " + getDateString(startDate, endDate)
				+ " AND Attendance.authorisation = 1 AND Employee.hotelId = '" + systemId
				+ "' ORDER BY Attendance.employeeId, Attendance.checkInDate, Attendance.shift";
		
		return db.getRecords(sql, Attendance.class, systemId);
	}

	@Override
	public ArrayList<Attendance> getAttendanceReportB(String systemId, String startDate, String endDate) {

		String sql = "SELECT distinct Attendance.checkInDate FROM Attendance "
				+ "WHERE Attendance.checkInDate " + getDateString(startDate, endDate)
				+ "AND Attendance.authorisation = 1 AND Attendance.hotelId = '" + systemId + "' "
				+ "ORDER BY Attendance.checkInDate";
		
		return db.getRecords(sql, Attendance.class, systemId);
	}

	@Override
	public Report getTotalSalesForService(String systemId, String outletId, String serviceDate, String serviceType) {
		
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		String sql = "SELECT ROUND(SUM(Payments.foodBill+Payments.barBill+gst+vat+serviceCharge+Payments.packagingCharge+Payments.deliveryCharge)*100)/100 AS grossTotal, "
				+ "ROUND(SUM(Payments.foodBill+gst+serviceCharge+Payments.packagingCharge+Payments.deliveryCharge)*100)/100 AS foodGross, "
				+ "ROUND(SUM(Payments.barBill+vat)*100)/100 AS barGross, "
				+ "ROUND(SUM(Payments.foodBill)*100)/100 AS foodBill, "
				+ "ROUND(SUM(Payments.barBill)*100)/100 AS barBill, "
				+ "ROUND(SUM(Payments.foodBill-Payments.foodDiscount+gst)*100)/100 AS foodSale, "
				+ "ROUND(SUM(Payments.barBill-Payments.barDiscount+vat)*100)/100 AS barSale, "
				+ "ROUND(SUM(Payments.total)*100)/100 AS total, "
				+ "ROUND(SUM(Payments.total-Payments.gst-Payments.serviceCharge-Payments.vat-Payments.packagingCharge-Payments.deliveryCharge)*100)/100 AS netSale, "
				+ "ROUND(SUM(Payments.foodDiscount)*100)/100 AS foodDiscount, "
				+ "ROUND(SUM(Payments.barDiscount)*100)/100 AS barDiscount, "
				+ "ROUND(SUM(Payments.gst)*100)/100 AS gst, "
				+ "ROUND(SUM(Payments.serviceCharge)*100)/100 AS serviceCharge, "
				+ "ROUND(SUM(Payments.vat)*100)/100 AS vat, "
				+ "ROUND(SUM(Payments.loyaltyAmount)*100)/100 AS loyalty, "
				+ "ROUND(SUM(Payments.packagingCharge)*100)/100 AS packagingCharge, "
				+ "ROUND(SUM(Payments.deliveryCharge)*100)/100 AS deliveryCharge, "
				+ "ROUND(SUM(Payments.cardPayment)*100)/100 AS cardPayment, "
				+ "ROUND(SUM(Payments.walletPayment)*100)/100 AS walletPayment, "
				+ "ROUND(SUM(Payments.appPayment)*100)/100 AS appPayment, "
				+ "ROUND(SUM(Payments.promotionalCash)*100)/100 AS promotionalCash, "
				+ "ROUND(SUM(Payments.creditAmount)*100)/100 AS creditAmount, "
				+ "ROUND(SUM(Payments.roundOff)*1000)/1000 AS roundOff, "
				+ "ROUND(SUM(Payments.tip)*1000)/1000 AS tip, "
				+ "SUM(Orders.printCount) AS printCount, "
				+ "(SELECT SUM(Orders.printCount-1) FROM Orders WHERE printCount >1 "+ this.outletFilter(outletId, "Orders") +" AND orders.orderDate = '"
				+ serviceDate + "' AND Orders.systemId = '" + systemId + "' AND Orders.serviceType = '" + serviceType + "' "
				+ ") AS reprints, COUNT(*) AS orderCount, "
				+ "ROUND((SELECT SUM(cashPayment) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND Orders.takeAwayType != '"
				+ portalDao.getOnlineOrderingPortalByPortal(systemId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate = '"
				+ serviceDate + "' "+this.outletFilter(outletId, "Orders")+" AND Orders.serviceType = '" + serviceType + "' "
				+ ")*100)/100 AS cashPayment, "
				+ "ROUND((SELECT SUM(cashPayment) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND Orders.takeAwayType == '"
				+ portalDao.getOnlineOrderingPortalByPortal(systemId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate = '"
				+ serviceDate + "' "+this.outletFilter(outletId, "Orders")+" AND Orders.serviceType = '" + serviceType + "' "
				+ ")*100)/100 AS zomatoCash, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 1 AND Orders.orderDate = '"
				+ serviceDate + "' "+this.outletFilter(outletId, "Orders")+" AND Orders.serviceType = '" + serviceType + "' "
				+ ")*100)/100 AS inhouseSale, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 0 AND Orders.orderDate = '"
				+ serviceDate + "' "+this.outletFilter(outletId, "Orders")+" AND Orders.serviceType = '" + serviceType + "' "
				+ ")*100)/100 AS homeDelivery, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 2 AND "
				+ "(takeAwayType == 5 OR takeAwayType == 6 OR takeAwayType == 100) AND Orders.orderDate = '"
				+ serviceDate + "' "+this.outletFilter(outletId, "Orders")+" AND Orders.serviceType = '" + serviceType + "' "
				+ ")*100)/100 AS takeAway, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 2 AND takeAwayType = 1 AND Orders.orderDate = '"
				+ serviceDate + "' "+this.outletFilter(outletId, "Orders")+" AND Orders.serviceType = '" + serviceType + "' "
				+ ")*100)/100 AS zomatoSale, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 2 AND takeAwayType = 2 AND Orders.orderDate = '"
				+ serviceDate + "' "+this.outletFilter(outletId, "Orders")+" AND Orders.serviceType = '" + serviceType + "' "
				+ ")*100)/100 AS swiggySale, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 2 AND takeAwayType = 3 AND Orders.orderDate = '"
				+ serviceDate + "' "+this.outletFilter(outletId, "Orders")+" AND Orders.serviceType = '" + serviceType + "' "
				+ ")*100)/100 AS uberEatsSale, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 2 AND takeAwayType = 4 AND Orders.orderDate = '"
				+ serviceDate + "' "+this.outletFilter(outletId, "Orders")+" AND Orders.serviceType = '" + serviceType + "' "
				+ ")*100)/100 AS foodPandaSale, "
				+ "ROUND((SELECT SUM(Payments.complimentary) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.serviceType = '" + serviceType + "' " + " AND paymentType != 'NON_CHARGEABLE' "
				+ ")*100)/100 AS complimentary, "
				+ "ROUND((SELECT SUM(Payments.foodBill+Payments.barBill) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND Orders.orderDate = '"
				+ serviceDate + "' "+this.outletFilter(outletId, "Orders")+" AND Orders.serviceType = '" + serviceType + "' AND paymentType = 'NON_CHARGEABLE' "
				+ ")*100)/100 AS nc FROM Payments, Orders WHERE Payments.orderId = Orders.orderId "
				+ "AND Orders.orderDate = '" + serviceDate + "' " +this.outletFilter(outletId, "Orders")
				+ "AND Orders.serviceType = '" + serviceType + "' AND Payments.paymentType != 'VOID';";

		return db.getOneRecord(sql, Report.class, systemId);
	}

	@Override
	public Report getAnalytics(String systemId, String outletId, String startDate, String endDate) {
		
		endDate = appendEndDate(endDate);
		String dateStr = getDateString(startDate, endDate);
		
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		String sql = "SELECT ROUND(SUM(Payments.foodBill+Payments.barBill+gst+vat+serviceCharge+Payments.packagingCharge+Payments.deliveryCharge+Payments.complimentary)*100)/100 AS grossTotal, "
				+ "ROUND(SUM(Payments.foodBill+gst+serviceCharge+Payments.packagingCharge+Payments.deliveryCharge+Payments.complimentary)*100)/100 AS foodGross, "
				+ "ROUND(SUM(Payments.barBill+vat)*100)/100 AS barGross, "
				+ "ROUND(SUM(Payments.foodBill)*100)/100 AS foodBill, "
				+ "ROUND(SUM(Payments.barBill)*100)/100 AS barBill, "
				+ "ROUND(SUM(Payments.foodBill-Payments.foodDiscount+gst)*100)/100 AS foodSale, "
				+ "ROUND(SUM(Payments.barBill-Payments.barDiscount+vat)*100)/100 AS barSale, "
				+ "ROUND(SUM(Payments.total)*100)/100 AS total, "
				+ "ROUND(SUM(Payments.total-Payments.gst-Payments.serviceCharge-Payments.vat-Payments.packagingCharge-Payments.deliveryCharge)*100)/100 AS netSale, "
				+ "ROUND(SUM(Payments.foodDiscount)*100)/100 AS foodDiscount, "
				+ "ROUND(SUM(Payments.barDiscount)*100)/100 AS barDiscount, "
				+ "ROUND(SUM(Payments.gst)*100)/100 AS gst, "
				+ "ROUND(SUM(Payments.serviceCharge)*100)/100 AS serviceCharge, "
				+ "ROUND(SUM(Payments.vat)*100)/100 AS vat, "
				+ "ROUND(SUM(Payments.complimentary)*100)/100 AS complimentary, "
				+ "ROUND(SUM(Payments.loyaltyAmount)*100)/100 AS loyalty, "
				+ "ROUND(SUM(Payments.packagingCharge)*100)/100 AS packagingCharge, "
				+ "ROUND(SUM(Payments.deliveryCharge)*100)/100 AS deliveryCharge, "
				+ "ROUND(SUM(Payments.cardPayment)*100)/100 AS cardPayment, "
				+ "ROUND(SUM(Payments.walletPayment)*100)/100 AS walletPayment, "
				+ "ROUND(SUM(Payments.appPayment)*100)/100 AS appPayment, "
				+ "ROUND(SUM(Payments.promotionalCash)*100)/100 AS promotionalCash, "
				+ "ROUND(SUM(Payments.creditAmount)*100)/100 AS creditAmount, "
				+ "ROUND(SUM(Payments.roundOff)*1000)/1000 AS roundOff, "
				+ "SUM(Orders.printCount) AS printCount, "
				+ "(SELECT SUM(Orders.printCount-1) FROM Orders WHERE printCount >1 AND orders.orderDate "
				+ dateStr + this.outletFilter(outletId, "Orders")
				+ ") AS reprints, COUNT(*) AS orderCount, "
				+ "ROUND((SELECT SUM(cashPayment) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND Orders.takeAwayType != '"
				+ portalDao.getOnlineOrderingPortalByPortal(systemId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate "
				+ dateStr + this.outletFilter(outletId, "Orders")
				+ ")*100)/100 AS cashPayment, "
				+ "ROUND((SELECT SUM(cashPayment) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND Orders.takeAwayType == '"
				+ portalDao.getOnlineOrderingPortalByPortal(systemId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate "
				+ dateStr + this.outletFilter(outletId, "Orders")
				+ ")*100)/100 AS zomatoCash, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 1 AND Orders.orderDate "
				+ dateStr + this.outletFilter(outletId, "Orders")
				+ ")*100)/100 AS inhouseSale, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 0 AND Orders.orderDate "
				+ dateStr + this.outletFilter(outletId, "Orders")
				+ ")*100)/100 AS homeDelivery, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 2 AND "
				+ "(takeAwayType == 5 OR takeAwayType == 100) AND Orders.orderDate "
				+ dateStr + this.outletFilter(outletId, "Orders")
				+ ")*100)/100 AS takeAway, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 2 AND (takeAwayType = 1  OR takeAwayType = 6) AND Orders.orderDate "
				+ dateStr + this.outletFilter(outletId, "Orders")
				+ ")*100)/100 AS zomatoSale, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 2 AND takeAwayType = 2 AND Orders.orderDate "
				+ dateStr + this.outletFilter(outletId, "Orders")
				+ ")*100)/100 AS swiggySale, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 2 AND takeAwayType = 3 AND Orders.orderDate "
				+ dateStr + this.outletFilter(outletId, "Orders")
				+ ")*100)/100 AS uberEatsSale, "
				+ "ROUND((SELECT SUM(total) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND orderType = 2 AND takeAwayType = 4 AND Orders.orderDate "
				+ dateStr + this.outletFilter(outletId, "Orders")
				+ ")*100)/100 AS foodPandaSale, "
				+ "ROUND((SELECT SUM(Payments.foodBill+Payments.barBill) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND Orders.orderDate "
				+ dateStr + this.outletFilter(outletId, "Orders") + " AND paymentType = 'NON_CHARGEABLE' "
				+ ")*100)/100 AS nc FROM Payments, Orders WHERE Payments.orderId = Orders.orderId "
				+ "AND Orders.orderDate " + dateStr + this.outletFilter(outletId, "Orders")
				+ "AND Payments.paymentType != 'VOID';";
		
		return db.getOneRecord(sql, Report.class, systemId);
	}
	
	@Override
	public BigDecimal getBarComplimentary(String systemId, String outletId, String serviceDate, String serviceType) {
		
		String sql = "SELECT OrderItemLog.rate AS entityId FROM OrderItemLog, Orders, MenuItems WHERE OrderItemLog.state = 50 "
				+ "AND Orders.orderId == OrderItemLog.orderId AND Orders.orderDate = '"+serviceDate
				+ "' AND Orders.serviceType = '"+serviceType+"' AND MenuItems.menuId == OrderItemLog.menuId "
				+ "AND MenuItems.flags LIKE '%5%' "
				+ this.outletFilter(outletId, "Orders");
		sql += ";";
		
		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, systemId);
		
		if(entity == null) {
			return new BigDecimal(0.0);
		}
		return entity.getId();
	}

	@Override
	public Report getTotalSalesForDay(String systemId, String serviceDate, String serviceType) {
		
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		String sql = "SELECT ROUND(SUM(Payments.foodBill)*100)/100 AS foodBill, "
				+ "ROUND(SUM(Payments.barBill)*100)/100 AS barBill, "
				+ "ROUND(SUM(Payments.cardPayment)*100)/100 AS cardPayment, "
				+ "ROUND(SUM(Payments.walletPayment)*100)/100 AS walletPayment, "
				+ "ROUND(SUM(Payments.appPayment)*100)/100 AS appPayment, "
				+ "ROUND(SUM(Payments.promotionalCash)*100)/100 AS promotionalCash, "
				+ "ROUND(SUM(Payments.creditAmount)*100)/100 AS creditAmount, "
				+ "ROUND((SELECT SUM(cashPayment) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND Orders.takeAwayType != '"
				+ portalDao.getOnlineOrderingPortalByPortal(systemId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.serviceType = '" + serviceType + "' "
				+ ")*100)/100 AS cashPayment "
				+ " FROM Payments, Orders WHERE Payments.orderId == Orders.orderId "
				+ "AND Orders.orderDate = '" + serviceDate + "' AND Payments.paymentType LIKE '%CASH%' "
				+ "AND Orders.serviceType = '" + serviceType + "' AND (Payments.paymentType != 'VOID' || Payments.paymentType != 'NON CHARGEABLE')";
		
		return db.getOneRecord(sql, Report.class, systemId);
	}

	@Override
	public Report getTotalSalesForMonth(String systemId, String month) {
		
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		String sql = "SELECT ROUND(SUM(Payments.complimentary+Payments.loyaltyAmount+Payments.foodDiscount+Payments.barDiscount+"
				+ "Payments.foodBill+Payments.barBill+gst+vat+serviceCharge)*100)/100 AS grossTotal, "
				+ "ROUND(SUM(Payments.foodBill)*100)/100 AS foodBill, "
				+ "ROUND(SUM(Payments.barBill)*100)/100 AS barBill, "
				+ "ROUND((SELECT SUM(cashPayment) from Payments, Orders WHERE Orders.orderid == Payments.orderid AND Orders.takeAwayType != '"
				+ portalDao.getOnlineOrderingPortalByPortal(systemId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate LIKE '"
				+ month + "%' "
				+ ")*100)/100 AS cashPayment "
				+ " FROM Payments, Orders WHERE Payments.orderId = Orders.orderId "
				+ "AND Orders.orderDate LIKE '" + month + "%' AND Payments.paymentType LIKE '%CASH%'";
		
		return db.getOneRecord(sql, Report.class, systemId);
	}

	@Override
	public ArrayList<Expense> getCashExpenses(String systemId, String outletId, String serviceDate, String serviceType) {
		String sql = "SELECT * FROM Expenses WHERE accountName = 'CASH_DRAWER' "
				+ "AND serviceDate = '" + serviceDate + "' AND serviceType = '" + serviceType + "';";

		return db.getRecords(sql, Expense.class, systemId);
	}

	@Override
	public BigDecimal getCardPaymentByType(String systemId, String outletId, String serviceDate, String serviceType, String paymentType) {

		String sql = "SELECT SUM(Payments.cardPayment) AS entityId FROM Payments, Orders "
				+ "WHERE Payments.orderId = Orders.orderId AND Orders.orderDate = '" + serviceDate + "' "
				+ this.outletFilter(outletId, "Orders");
		
		sql += " AND Orders.serviceType = '" + serviceType + "' "
				+ "AND Payments.paymentType LIKE '%" + paymentType + "%';";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, systemId);
		if (entity != null) {
			return entity.getId();
		}
		return new BigDecimal("0");
	}

	@Override
	public int getAppPaymentByType(String systemId, String outletId, String startDate, String endDate, String serviceType, String paymentType) {

		endDate = appendEndDate(endDate);
		if(!serviceType.isEmpty()) {
			serviceType = "AND Orders.serviceType = '" + serviceType + "' ";
		}
		
		String sql = "SELECT SUM(Payments.appPayment) AS entityId FROM Payments, Orders "
				+ "WHERE Payments.orderId = Orders.orderId AND Orders.orderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "Orders");
		
		sql += serviceType + "AND Payments.paymentType LIKE '%" + paymentType + "';";

		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	@Override
	public int getVoidTransactions(String systemId, String outletId, String serviceDate, String serviceType) {

		String sql = "SELECT COUNT(Orders.orderId) AS entityId FROM Orders "
				+ "WHERE Orders.orderDate = '" + serviceDate + "' "
				+ this.outletFilter(outletId, "Orders");
		
		sql += " AND Orders.serviceType = '" + serviceType + "' " + "AND Orders.state = 99;";

		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}
	
	//Round off sql
	//select ROUND((total-(Payments.foodBill+Payments.barBill+gst+vat-foodDiscount-barDiscount))*100)/100 AS roundOff , paymentType, orderID from Payments where (paymentType != 'VOID' AND paymentType != 'NON_CHARGEABLE') order by roundOff desc 

	@Override
	public ArrayList<Report> getSaleSummaryReport(String systemId, String outletId, String startDate, String endDate,
			int orderType, int portalId) {
		
		String sql = "SELECT ROUND(Payments.foodBill*100)/100 AS foodBill, "
				+ "ROUND(Payments.barBill*100)/100 AS barBill, "
				+ "ROUND((Payments.foodBill+Payments.barBill)*100)/100 AS totalBill, "
				+ "Payments.billNo, "
				+ "ROUND(Payments.total*100)/100 AS total, "
				+ "ROUND(Payments.foodDiscount*100)/100 AS foodDiscount, "
				+ "ROUND(Payments.barDiscount*100)/100 AS barDiscount, "
				+ "ROUND((Payments.foodDiscount+Payments.barDiscount)*100)/100 AS totalDiscount, "
				+ "ROUND(Payments.vat*100)/100 AS vatBar, "
				+ "ROUND(Payments.serviceCharge*100)/100 AS serviceCharge, "
				+ "ROUND(Payments.tip*100)/100 AS tip, "
				+ "ROUND(Payments.gst*100)/100 AS gst, "
				+ "ROUND(Payments.roundOff*100)/100 AS roundOff, "
				+ "Orders.numberOfGuests AS covers, "
				+ "Orders.orderType AS orderType, "
				+ "Orders.takeAwayType AS takeAwayType, "
				+ "Orders.orderDateTime AS orderDateTime, "
				+ "Orders.state AS state, "
				+ "Orders.tableId AS tableId, "
				+ "Orders.orderDate AS orderDate, "
				+ "Orders.completeTimestamp AS completeTimestamp, "
				+ "Orders.customerName AS customerName, "
				+ "Orders.externalOrderId AS externalOrderId, "
				+ "Orders.reference AS reference, "
				+ "Orders.remarks AS remarks, "
				+ "ROUND(Payments.cashPayment*100)/100 AS cashPayment, "
				+ "ROUND(Payments.cardPayment*100)/100 AS cardPayment, "
				+ "ROUND(Payments.appPayment*100)/100 AS appPayment, "
				+ "ROUND(Payments.walletPayment*100)/100 AS walletPayment, "
				+ "ROUND(Payments.creditAmount*100)/100 AS creditAmount, "
				+ "ROUND(Payments.promotionalCash*100)/100 AS promotionalCash, "
				+ "ROUND(Payments.loyaltyAmount*100)/100 AS loyalty, "
				+ "Payments.paymentType AS paymentType, "
				+ "Orders.section AS section "
				+ "FROM Orders, Payments WHERE Orders.orderId == Payments.orderId "
				+ this.outletFilter(outletId, "Orders");
				
		sql += " AND Orders.orderDate" + getDateString(startDate, endDate);
 
		
		if(orderType != 1000) {
			sql += "AND Orders.orderType = " + orderType + " ";
		}
		if(portalId != 0) {
			sql += "AND Orders.takeAwayType = " + portalId + " ";
		}
		sql += ";";
		
		return db.getRecords(sql, Report.class, systemId);
	}

	@Override
	public ArrayList<CustomerReport> getCustomerReport(String systemId, String startDate, String endDate) {
		String sql = "SELECT ROUND(SUM(OrderItems.rate*quantity)*100)/100 AS totalSpent, "
				+ "ROUND(SUM(OrderItems.rate*quantity)/SUM(Orders.numberOfGuests)) AS spentPerPax, "
				+ "ROUND(SUM(OrderItems.rate*quantity)/COUNT(Orders.orderId)) AS spentPerWalkin, "
				+ "SUM(Orders.numberOfGuests) AS totalGuests, Orders.customerName AS customerName, "
				+ "Orders.customerNumber AS mobileNumber, COUNT(Orders.orderId) AS totalWalkins "
				+ "FROM OrderItems, Orders WHERE OrderItems.orderId == Orders.orderId "
				+ "AND orderDate "+ getDateString(startDate, endDate) +" GROUP BY Orders.customerNumber;";

		return db.getRecords(sql, CustomerReport.class, systemId);
	}

	@Override
	public ArrayList<CustomerReport> getCustomerReviewReport(String systemId, String startDate, String endDate) {
		String sql = "SELECT Customers.firstName, Customers.surName, Customers.mobileNumber, Orders.rating_ambiance, Orders.rating_hygiene, Orders.rating_qof, "
				+ "Orders.rating_service, REPLACE(Orders.reviewSuggestions, ',', ';') AS reviewSuggestions, Payments.total, Orders.billNo, Orders.numberOfGuests "
				+ "FROM Customers, Orders, Payments "
				+ "WHERE Customers.mobileNumber == Orders.customerNumber "
				+ "AND Orders.orderId == Payments.orderId AND Orders.orderdate " + getDateString(startDate, endDate)
				+ "AND mobileNumber != '' AND rating_ambiance is not null AND (rating_ambiance+rating_hygiene+rating_qof+rating_service) > 0";

		return db.getRecords(sql, CustomerReport.class, systemId);
	}

	@Override
	public Report getDailyIncome(String systemId, String outletId, String startDate, String endDate, int orderType) {

		String sql = "SUM(Payments.total) AS total, SUM(Orders.numberOfGuests) AS pax, "
				+ "COUNT(Orders.Id) AS checks, SUM(Payments.foodDiscount) AS foodDiscount, SUM(Payments.barDiscount) AS foodDiscount, "
				+ "SUM(Payments.serviceCharge) AS serviceCharge SUM(Payments.serviceTax) AS serviceTax "
				+ "SUM(Payments.gst) AS gst SUM(Payments.VATFOOD) AS VATFOOD SUM(Payments.vat) AS vat "
				+ "SUM(Payments.sbCess) AS sbCess SUM(Payments.kkCess) AS kkCess FROM Payments, Orders "
				+ "WHERE Payments.orderId = Orders.orderId AND Orders.orderType = " + orderType + this.outletFilter(outletId, "Orders")
				+ " AND Orders.orderDate " + getDateString(startDate, endDate) + ";";

		return db.getOneRecord(sql, Report.class, systemId);
	}

	@Override
	public ArrayList<Expense> getDailyExpense(String systemId, String startDate, String endDate) {

		String sql = "SELECT SUM(Expense.amount) AS amount, SUM(Expense.type) AS type FROM Expense "
				+ "WHERE hotelId = '" + systemId + "' AND Expense.date " + getDateString(startDate, endDate) + ";";

		return db.getRecords(sql, Expense.class, systemId);
	}

	@Override
	public ArrayList<IncentiveReport> getIncentivisedItemReport(String systemId, String outletId) {
		String sql = "SELECT title, incentiveAmount, incentiveQuantity FROM MenuItems WHERE incentiveQuantity != 0 "
				+ this.outletFilter(outletId, "MenuItems") + "ORDER BY title";
		
		return db.getRecords(sql, IncentiveReport.class, systemId);
	}
	
	@Override
	public IncentiveReport getIncentiveForEmployee(String systemId, String outletId, String userId, boolean isBar, String startDate,
			String endDate) {

		String sql = "SELECT SUM(sale) AS sale, SUM(incentiveAmount) AS incentiveAmount "
				+ "FROM (SELECT (SUM(OrderItems.quantity)/MenuItems.incentiveQuantity)*MenuItems.incentiveAmount AS incentiveAmount, "
				+ "SUM(OrderItems.rate*OrderItems.quantity) AS sale FROM OrderItems, MenuItems, Orders "
				+ "WHERE OrderItems.menuId == MenuItems.menuId AND MenuItems.incentiveQuantity !=0 "
				+ "AND OrderItems.waiterId = '" + userId + "' "
				+ "AND Orders.systemId == OrderItems.systemId AND Orders.orderId == OrderItems.orderId "
				+ "AND OrderItems.systemId == MenuItems.systemId AND Orders.orderDate " + getDateString(startDate, endDate)
				+ this.outletFilter(outletId, "Orders");

		if (isBar)
			sql += "AND MenuItems.station = 'Bar' ";
		else
			sql += "AND MenuItems.station != 'Bar' ";

		sql += "GROUP BY Orders.orderId, MenuItems.menuId " + "ORDER BY Orders.orderId) WHERE incentiveAmount>0;";
		

		return db.getOneRecord(sql, IncentiveReport.class, systemId);
	}

	@Override
	public ArrayList<IncentiveReport> getItemwiseIncentiveReport(String systemId, String outletId, String userId, String startDate,
			String endDate, boolean isBar) {

		String sql = "SELECT title, orderId, SUM(quantity) AS quantity, SUM(incentiveAmount) AS incentiveAmount FROM (SELECT MenuItems.title AS title, Orders.orderId, SUM(OrderItems.quantity) AS quantity,"
				+ "(SUM(OrderItems.quantity)/MenuItems.incentiveQuantity)*MenuItems.incentiveAmount AS incentiveAmount, "
				+ "OrderItems.waiterId AS userId FROM OrderItems, MenuItems, Orders WHERE OrderItems.menuId == MenuItems.menuId AND MenuItems.incentiveQuantity !=0 "
				+ "AND OrderItems.waiterId = '" + userId + "' AND " + this.outletFilter(outletId, "Orders")
				+ "AND Orders.systemId == OrderItems.systemId AND Orders.orderId == OrderItems.orderId "
				+ "AND OrderItems.systemId == MenuItems.systemId AND Orders.orderDate " + getDateString(startDate, endDate);

		if (isBar)
			sql += "AND MenuItems.station = 'Bar' ";
		else
			sql += "AND MenuItems.station != 'Bar' ";

		sql += "GROUP BY Orders.orderId, MenuItems.menuId "
				+ "ORDER BY Orders.orderId) WHERE incentiveAmount>0 GROUP BY title ORDER BY title;";

		return db.getRecords(sql, IncentiveReport.class, systemId);
	}

	@Override
	public ArrayList<DeliveryReport> getDeliveryReport(String systemId, String outletId, String userId, String startDate,
			String endDate) {
		
		String sql = "SELECT Orders.billNo, Orders.deliveryBoy, deliveryTimeStamp AS dispatchtime, Payments.total FROM Orders, Payments " + 
				"WHERE Orders.orderId == Payments.orderId AND Orders.orderType == 0 "
				+ "AND Orders.deliveryBoy = '" + userId + "' " + this.outletFilter(outletId, "Orders")
				+ "AND Orders.orderDate " + getDateString(startDate, endDate);

		sql += "ORDER BY Orders.id;";

		return db.getRecords(sql, DeliveryReport.class, systemId);
	}
	
	@Override
	public ArrayList<ConsumptionReport> getConsumptionReport(String systemId, String outletId, String startDate, String endDate, int department) {
		
		String depSql = "";
		if(department == DEPARTMENT_FOOD)
			depSql = " (MenuItems.station == 'Kitchen' OR MenuItems.station == 'Pantry' OR MenuItems.station == 'Outdoor') ";
		else if(department == DEPARTMENT_NON_ALCOHOLIC_BEVERAGE)
			depSql = " (MenuItems.station == 'Beverage' OR MenuItems.station == 'Bar Non-Alcoholic') ";
		else
			depSql = " (MenuItems.station == 'Bar') ";
		
		String dateStr = getDateString(startDate, endDate);
		
		String sql ="SELECT *, (totalSaleQty+totalCompQty) AS totalQty, "
				+ "ROUND(totalAfterDiscount*100*1000/departmentSale)/1000 AS percentOfDepartmentSale, "
				+ "ROUND(totalAfterDiscount*100*1000/totalSale)/1000 AS percentOfTotalSale,"
				+ "ROUND((quantity+compQty)*100*100/(totalSaleQty+totalCompQty))/100 AS percentOfTotalQty "
				+ "FROM "
				+ "(SELECT collection, station, title, IFNULL(SUM(quantity), 0) AS quantity, IFNULL(SUM(compQty), 0) AS compQty, rate, ROUND(SUM(total)*100)/100 AS total, ROUND(SUM(totalAfterDiscount)*100)/100 AS totalAfterDiscount,"
				+ "(SELECT ROUND(SUM(foodBill-foodDiscount)*100)/100 FROM Payments WHERE orderDate "+dateStr+" "+ this.outletFilter(outletId, "Payments")+") AS departmentSale,"
				+ "(SELECT ROUND(SUM(foodBill+barBill-foodDiscount-barDiscount)*100)/100 FROM Payments WHERE orderDate "+dateStr+" "+this.outletFilter(outletId, "Payments")+") AS totalSale,"
				+ "(SELECT IFNULL(SUM(quantity), 0) FROM OrderItems, Orders, MenuItems WHERE orderDate " + dateStr + " "+this.outletFilter(outletId, "Orders")+" AND Orders.orderId == OrderItems.orderId AND MenuItems.menuId == OrderItems.menuId AND "+depSql+") AS totalSaleQty,"
				+ "(SELECT IFNULL(SUM(quantity), 0) FROM OrderItemLog, Orders, MenuItems WHERE orderDate " + dateStr + " "+this.outletFilter(outletId, "Orders")+" AND "
				+ "Orders.orderId == OrderItemLog.orderId AND OrderItemLog.state == 50 AND MenuItems.menuId == OrderItemLog.menuId AND "+depSql+") AS totalCompQty "
				+ "FROM "
				+ "(SELECT collection, station, title, MenuItems.menuId, OrderItems.quantity, 0 AS compQty, OrderItems.rate, OrderItems.quantity*OrderItems.rate AS total, "
				+ "(CASE "
				+ "WHEN Orders.discountCodes != '[]' "
				+ "THEN "
				+ "(CASE "
				+ "WHEN (SELECT type FROM Discounts WHERE name LIKE '%' || Orders.discountCodes || '%') == 0 "
				+ "THEN (OrderItems.quantity*OrderItems.rate) - ((SELECT foodValue FROM Discounts WHERE name LIKE '%' || Orders.discountCodes || '%')*OrderItems.rate/100)*OrderItems.quantity "
				+ "ELSE (OrderItems.quantity*OrderItems.rate) - (SELECT foodValue FROM Discounts WHERE name LIKE '%' || Orders.discountCodes || '%')*OrderItems.quantity "
				+ "END) "
				+ "ELSE "
				+ "OrderItems.quantity*OrderItems.rate "
				+ "END) AS totalAfterDiscount "
				+ "FROM Orders, OrderItems, MenuItems "
				+ "WHERE Orders.orderId == OrderItems.orderId "
				+ "AND MenuItems.menuId == OrderItems.menuId "
				+ "AND Orders.orderDate " + dateStr + " "
				+ this.outletFilter(outletId, "Orders")
				+ "AND " + depSql
				+ "UNION ALL "
				+ "SELECT collection, station, title, MenuItems.menuId, 0 AS quantity, OrderItemLog.quantity AS compQty, OrderItemLog.rate, OrderItemLog.quantity*OrderItemLog.rate AS total, "
				+ "0 AS totalAfterDiscount "
				+ "FROM Orders, OrderItemLog, MenuItems "
				+ "WHERE Orders.orderId == OrderItemLog.orderId "
				+ "AND MenuItems.menuId == OrderItemLog.menuId "
				+ "AND Orders.orderDate " + dateStr + " "
				+ this.outletFilter(outletId, "Orders")
				+ "AND " + depSql
				+ "AND OrderItemLog.state == 50) "
				+ "GROUP BY menuId "
				+ "ORDER BY collection, station, title);";

		return db.getRecords(sql, ConsumptionReport.class, systemId);
	}
	
	private String getDateString(String startDate, String endDate) {
		
		String date = "";
		if(endDate == null || endDate.isEmpty()) {
			date = " = '" + startDate + "' ";
		}else if (startDate.equals(endDate))
			date = " = '" + startDate + "' ";
		else
			date = " BETWEEN '" + startDate + "' AND '" + endDate + "' ";
		
		return date;
	}
	
	private String outletFilter(String outletId, String tableName) {
		
		String outletFilter = "";
		if(outletId != null && !outletId.isEmpty()) {
			outletFilter = " AND "+tableName+".outletId ='" + outletId + "' ";
		}
		return outletFilter;
	}
}
