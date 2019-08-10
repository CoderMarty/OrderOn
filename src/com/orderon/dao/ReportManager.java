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
import com.orderon.interfaces.IOutlet;
import com.orderon.interfaces.IReport;
import com.orderon.interfaces.IService;

public class ReportManager extends AccessManager implements IReport{

	public ReportManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public MonthReport getTotalOrdersForCurMonth(String hotelId, String duration, boolean visible) {
		String sql = "SELECT COUNT(orderId) as count FROM Orders WHERE orderDate LIKE'" + escapeString(duration)
				+ "%' AND hotelId='" + hotelId + "' ";
		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN + ";";
		return db.getOneRecord(sql, MonthReport.class, hotelId);
	}
	
	@Override
	public BigDecimal getPendingSale(String hotelId) {
		
		IService serviceDao = new ServiceManager(false);
		
		String sql = "Select SUM(subTotal+tax1+tax2 ) AS entityId FROM " + 
				"(Select subTotal, (subTotal*taxPercentValue)/100 AS tax1, taxFixedValue AS tax2 FROM " + 
				"(Select SUM(rate*qty) AS subTotal, MenuItems.taxes as taxes," + 
				"IFNULL((SELECT SUM(value) FROM Taxes WHERE LIKE('%' || Taxes.id|| '%', taxes)=1 AND Taxes.type == 'PERCENTAGE' AND Taxes.isActive = 'true'), 0) AS taxPercentValue," + 
				"IFNULL((SELECT SUM(value) FROM Taxes WHERE LIKE('%' || Taxes.id|| '%', taxes)=1 AND Taxes.type == 'FIXED' AND Taxes.isActive = 'true'), 0) AS taxFixedValue " + 
				"FROM OrderItems, MenuItems, Orders " + 
				"WHERE OrderItems.menuId == MenuItems.menuId " + 
				"AND OrderItems.orderId == Orders.orderId " + 
				"AND Orders.orderDate = '" + serviceDao.getServiceDate(hotelId) + "' " +
				"AND (Orders.state == 1 OR Orders.state == 0) " + 
				"GROUP BY OrderItems.menuId))";
		
		return db.getOneRecord(sql, EntityBigDecimal.class, hotelId).getId();
	}

	@Override
	public MonthReport getBestWaiter(String hotelId, String duration, boolean visible) {

		String sql = "SELECT RTRIM(orderId, '0123456789:') AS user, SUBSTR(subOrderDate, 1, " + duration.length()
				+ ") AS duration, count(*) AS waitersOrders, employeeID FROM OrderItems, Users WHERE duration = '"
				+ escapeString(duration) + "' AND Users.userId = user ";
		if(!visible)
			sql += "AND OrderItems.billNo != '' ";
		sql += "GROUP BY user ORDER BY count(*) desc  LIMIT 1;";
		return db.getOneRecord(sql, MonthReport.class, hotelId);
	}

	@Override
	public ArrayList<MonthReport> getWeeklyRevenue(String hotelId, boolean visible) {

		ArrayList<MonthReport> weeklyRevenue = new ArrayList<MonthReport>();

		String duration = "";

		for (int i = 0; i < 7; i++) {

			duration = this.getPreviousDateString(i);
			String sql = "SELECT SUM(total) AS totalSales FROM Payment WHERE orderDate = '" + duration + "' ";
			if(!visible)
				sql += "AND billNo != '';";
			MonthReport report = db.getOneRecord(sql, MonthReport.class, hotelId);
			weeklyRevenue.add(report);
		}
		return weeklyRevenue;
	}

	@Override
	public ArrayList<YearlyReport> getYearlyOrders(String hotelId, boolean visible) {

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
			if(!visible)
				sql += "AND Orders.state != " + ORDER_STATE_HIDDEN + ";";
			YearlyReport report = db.getOneRecord(sql, YearlyReport.class, hotelId);
			report.month = month + 1;
			out.add(report);
			cal.add(Calendar.MONTH, -1);
			month = cal.get(Calendar.MONTH);

		}
		return out;
	}

	@Override
	public MonthReport getMostOrderedItem(String hotelId, String duration, boolean visible) {

		String sql = "SELECT OrderItems.menuId as itemId, SUBSTR(subOrderDate, 1, " + duration.length()
				+ ") AS duration, MenuItems.title AS title, SUM(qty) AS orderCount, img FROM OrderItems, MenuItems "
				+ "WHERE duration = '" + escapeString(duration) + "' AND MenuItems.menuId = OrderItems.menuId "
				+ " AND MenuItems.collection != 'Roti' ";
		if(!visible)
			sql += "AND OrderItems.billNo != '' ";
		sql += " GROUP BY itemId ORDER BY orderCount desc LIMIT 1;";
		
		return db.getOneRecord(sql, MonthReport.class, hotelId);
	}

	// Reports
	// expense-ap
	@Override
	public ArrayList<Expense> getExpenseReport(String hotelId, String startDate, String endDate, String filter) {

		String filterText = ";";
		if(filter.equals("CASH")) {
			filterText = " AND paymentType = 'CASH';";
		}else if(filter.equals("BANK")){
			filterText = " AND paymentType != 'CASH';";
		}
		
		String sql = "SELECT *, "
				+ "REPLACE(REPLACE(REPLACE(Expenses.memo,CHAR(13),''),CHAR(10),''),',','|') AS description "
				+ "FROM Expenses WHERE hotelId = '" + hotelId + "' AND type != 'PAYIN'" 
				+ "AND serviceDate " + getDateString(startDate, appendEndDate(endDate)) + filterText;
		return db.getRecords(sql, Expense.class, hotelId);
	}
	
	// expense-ap
	@Override
	public ArrayList<Expense> getPayInReport(String hotelid, String startDate, String endDate) {

		String sql = "SELECT *, "
				+ "REPLACE(REPLACE(REPLACE(Expenses.memo,CHAR(13),''),CHAR(10),''),',','|') AS description "
				+ "FROM Expenses WHERE hotelId = '" + hotelid + "' AND type == 'PAYIN'" 
				+ "AND serviceDate " + getDateString(startDate, appendEndDate(endDate)) + ";";
		return db.getRecords(sql, Expense.class, hotelid);
	}

	// new-ap(in progress)
	@Override
	public ArrayList<DailyDiscountReport> getDailyDiscountReport(String hotelId, String startDate, String endDate, boolean visible) {
		
		endDate = appendEndDate(endDate);
		String dateStr = getDateString(startDate, endDate);
		
		String sql = "SELECT *, ROUND(ordersAffected/totalOrders*10000)/100 AS ordersDiscountedPer, "
				+ "(ROUND(sumDiscount/sumTotal*10000)/100)||' %' AS discountPer,"
				+ "(ROUND(avgDiscount/avgTotal*10000)/100)||' %' AS avgDiscountPer,"
				+ "(ROUND(grossDiscount/grossSale*10000)/100)||' %' AS grossDiscountPer "
				+ " FROM "
				+ "(SELECT discount.name AS name, "
				+ "CASE discount.type WHEN 1 THEN 'Rs '||discount.foodValue else discount.foodValue||' %' END AS foodValue, "
				+ "CASE discount.type WHEN 1 THEN 'Rs '||discount.barValue else discount.barValue||' %' END AS barValue, "
				+ "discount.description AS description, "
				+ "(ROUND(SUM(foodBill+barBill)*100)/100) AS sumTotal, "
				+ "(ROUND(AVG(foodBill+barBill)*100)/100) AS avgTotal, "
				+ "(ROUND(AVG(foodDiscount+barDiscount)*100)/100) AS avgDiscount, "
				+ "(ROUND(SUM(foodDiscount+barDiscount)*100)/100) AS sumDiscount, "
				+ "(ROUND(SUM(foodBill+barBill-foodDiscount-barDiscount)*100)/100) AS sumDiscountedTotal, "
				+ "COUNT(payment.Id) AS ordersAffected, "
				+ "(SELECT COUNT(orderId) FROM Payment WHERE hotelId='" + hotelId + "' AND orderDate " 
				+ dateStr + " AND cardType != 'VOID') AS totalOrders, " 
				+ "(SELECT ROUND(SUM(foodBill+barBill)*100)/100 FROM Payment WHERE hotelId='" + hotelId + "' AND orderDate " 
				+ dateStr + " AND cardType != 'VOID') AS grossSale, " 
				+ "(SELECT ROUND(SUM(foodDiscount+barDiscount)*100)/100 FROM Payment WHERE hotelId='" + hotelId + "' AND orderDate " 
				+ dateStr + " AND cardType != 'VOID') AS grossDiscount " 
				+ "FROM payment, discount WHERE payment.hotelId='" + hotelId
				+ "' AND payment.orderDate " + dateStr
				+ " AND payment.discountName LIKE '%\"'||discount.name||'\"%' ";

		if(!visible)
			sql += " AND payment.billNo != '" + "'";
		
				sql += " GROUP BY discount.name)";
		
		return db.getRecords(sql, DailyDiscountReport.class, hotelId);
	}

	// new-ap
	// refere googlesheets for logic...
	@Override
	public ArrayList<GrossSaleReport> getGrossSalesReport(String hotelId, String startDate, String endDate, boolean visible) {
		endDate = appendEndDate(endDate);
		String dateStr = getDateString(startDate, endDate);
		
		String sql = "SELECT ROUND(SUM(payment.foodBill+payment.barBill+payment.gst+payment.VATBAR+payment.serviceCharge+payment.deliveryCharge+payment.packagingCharge)*100)/100 as grossTotal, "
				+ "ROUND(SUM(payment.foodDiscount)*100)/100 as foodDiscount, "
				+ "ROUND(SUM(payment.barDiscount)*100)/100 as barDiscount, "
				+ "ROUND(SUM(payment.foodDiscount+payment.barDiscount)*100)/100 as totalDiscount, "
				+ "ROUND(SUM(payment.loyaltyAmount)*100)/100 as grossLoyalty, "
				+ "ROUND(SUM(payment.complimentary)*100)/100 as grossComplimentary, "
				+ "ROUND(SUM(payment.gst)*100)/100 as grossGst, "
				+ "ROUND(SUM(payment.VATBAR)*100)/100 as grossVatBar, "
				+ "ROUND(SUM(payment.roundOff)*100)/100 as roundOff, "
				+ "ROUND(SUM(payment.deliveryCharge)*100)/100 as grossDeliveryCharge, "
				+ "ROUND(SUM(payment.packagingCharge)*100)/100 as grossPackagingCharge, "
				+ "ROUND(SUM(payment.foodBill)*100)/100 as foodBill, "
				+ "ROUND(SUM(payment.barBill)*100)/100 as barBill, "
				+ "ROUND(SUM(payment.cardPayment)*100)/100 as cardPayment, "
				+ "ROUND(SUM(payment.appPayment)*100)/100 as appPayment, "
				+ "ROUND(SUM(payment.cashPayment)*100)/100 as cashPayment, "
				+ "ROUND(SUM(payment.walletPayment)*100)/100 as walletPayment, "
				+ "ROUND(SUM(payment.promotionalCash)*100)/100 as promotionalCash, "
				+ "ROUND(SUM(payment.cashPayment + payment.cardPayment + payment.appPayment + payment.walletPayment + payment.creditAmount + payment.promotionalCash - payment.total)*100)/100 as roundOffDifference, "
				+ "ROUND(SUM(payment.cashPayment + payment.cardPayment + payment.appPayment + payment.walletPayment + payment.promotionalCash)*100)/100 as totalPayment, "
				+ "ROUND(SUM(payment.creditAmount)*100)/100 as creditAmount, "
				+ "ROUND(SUM(payment.serviceCharge)*100)/100 as grossServiceCharge, "
				+ "ROUND(SUM(payment.total)*100)/100 as totalSale, "
				+ "ROUND((SUM(payment.total)"
						+ "-(Select ifnull(SUM(Expenses.amount),0) From Expenses Where (expenses.type!='CASH_LIFT' OR expenses.type!='PAYIN') AND expenses.serviceDate "+ dateStr + " AND expenses.hotelId='" + hotelId + "')"
						+ "+(Select ifnull(SUM(Expenses.amount),0) From Expenses Where expenses.type=='PAYIN' AND expenses.serviceDate "+ dateStr + " AND expenses.hotelId='" + hotelId + "')"
						+ ")*100)/100 as netSales, "
				+ "ROUND((Select ifnull(SUM(Expenses.amount),0) From Expenses Where (expenses.type!='CASH_LIFT' OR expenses.type!='PAYIN') AND expenses.serviceDate "
				+ dateStr + " AND expenses.hotelId='" + hotelId + "')*100)/100 as grossExpenses, "
				+ "ROUND((Select ifnull(SUM(Expenses.amount),0) From Expenses Where expenses.type=='PAYIN' AND expenses.serviceDate "
				+ dateStr + " AND expenses.hotelId='" + hotelId + "')*100)/100 as totalPayIns, "
				+ "ROUND((Select SUM(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='99' AND OrderItemLog.hotelId='"
				+ hotelId + "' AND OrderItemLog.dateTime " + dateStr + ")*100)/100  as sumVoids,  "
				+ "(Select COUNT(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='99' AND OrderItemLog.hotelId='"
				+ hotelId + "' AND OrderItemLog.dateTime " + dateStr + ")  as countVoids,  "
				+ "ROUND((Select SUM(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='100' AND OrderItemLog.hotelId='"
				+ hotelId + "' AND OrderItemLog.dateTime " + dateStr + ")*100)/100  as sumReturns, "
				+ "(Select COUNT(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='100' AND OrderItemLog.hotelId='"
				+ hotelId + "' AND OrderItemLog.dateTime " + dateStr
				+ ")  as countReturns FROM Orders, Payment Where Orders.orderId == Payment.orderId AND Orders.hotelId='" + hotelId + "' "
				+ "AND Orders.orderDate " + dateStr + " AND Payment.cardType != 'VOID'";
		if(!visible) 
			sql += " AND Orders.state != 102";
		
		return db.getRecords(sql, GrossSaleReport.class, hotelId);
	}
	
	// newCollectionWiseReportA-ap (gross and totals...)
	@Override
	public ArrayList<CollectionWiseReportA> getCollectionWiseReportA(String hotelId, String startDate, String endDate) {
		endDate = appendEndDate(endDate);
		String dateStr = getDateString(startDate, endDate);
		
		String sql = "Select DISTINCT(MenuItems.collection) AS collection, "
				+ "ROUND(SUM(OrderItems.qty*OrderItems.rate*100))/100 AS grossTotal, "
				+ "ROUND(AVG(OrderItems.qty*OrderItems.rate)*100)/100 AS averagePrice, "
				+ "COUNT(OrderItems.menuId) AS noOrdersAffected, "
				+ "(ROUND(CAST(COUNT(OrderItems.menuId) AS FLOAT)/(SELECT CAST(SUM(OrderItems.qty) AS FLOAT) FROM OrderItems WHERE OrderItems.hotelId='"
				+ hotelId + "' AND OrderItems.subOrderDate " + dateStr
				+ ")*100*100)/100)||' %' AS noOrdersAffectedPer, SUM(OrderItems.qty) AS totalQuantityOrdered, "
				+ "(ROUND(CAST(SUM(OrderItems.qty) AS FLOAT)/(SELECT CAST(SUM(OrderItems.qty) AS FLOAT) FROM OrderItems WHERE OrderItems.hotelId='"
				+ hotelId + "' AND OrderItems.subOrderDate " + dateStr
				+ ")*100*100)/100)||' %' AS totalQuantityOrderedPer FROM OrderItems,MenuItems "
				+ "WHERE OrderItems.menuId=MenuItems.menuId AND OrderItems.hotelId='" + hotelId + "' "
				+ "AND OrderItems.subOrderDate " + dateStr
				+ "GROUP BY MenuItems.collection;";
		
		return db.getRecords(sql, CollectionWiseReportA.class, hotelId);
	}

	// newCollectionWiseReportB-ap (top/hot selling item!)
	@Override
	public ArrayList<CollectionWiseReportB> getCollectionWiseReportB(String hotelId, String startDate, String endDate) {
		endDate = appendEndDate(endDate);
		String sql = "SELECT title AS topItemTitle, max(SUM) "
				+ "FROM (SELECT MenuItems.collection, MenuItems.title, OrderItems.menuId, SUM(OrderItems.qty) AS SUM "
				+ "FROM OrderItems, MenuItems "
				+ "WHERE OrderItems.menuId = MenuItems.menuId AND OrderItems.subOrderDate " + getDateString(startDate, endDate)
				+ " GROUP BY MenuItems.menuId "
				+ "ORDER BY MenuItems.title) GROUP BY collection;";
		
		return db.getRecords(sql, CollectionWiseReportB.class, hotelId);
	}
	
	public void inventoryReprt() {
		/*
		 * select group_concat(menuId), ROUND(SUM(qty)*100)/100,* FROM 
(select StockLog.menuId AS menuId, StockLog.quantity AS qty, StockLog.sku AS sku, Material.name AS name from StockLog, orders, Material 
where crud = 'USEDUP' AND StockLog.orderId == Orders.orderId AND Orders.orderDate between '2018/10/01' AND '2018/12/30'
 AND Material.sku == StockLog.sku 
 Order BY StockLog.menuId) 
 GROUP BY sku
		 */
	}

	// total operating cost-ap
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport1(String hotelId, String startDate, String endDate) {
		endDate = appendEndDate(endDate);
		String sql = "SELECT ROUND(SUM(Expenses.amount)*100)/100 AS  totalOperatingCost, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='INVENTORY' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId + "')*100)/100 AS INVENTORY, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='LABOUR' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId + "')*100)/100 AS LABOUR, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='RENT' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId + "')*100)/100 AS RENT, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='ELECTRICITY_BILL' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId
				+ "')*100)/100 AS ELECTRICITY_BILL, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='GAS_BILL' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId + "')*100)/100 AS GAS_BILL, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='PETROL' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId + "')*100)/100 AS PETROL, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='TELEPHONE_BILL' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId
				+ "')*100)/100 AS TELEPHONE_BILL, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='MOBILE_RECHARGE' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId
				+ "')*100)/100 AS MOBILE_RECHARGE, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='INTERNET' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId + "')*100)/100 AS INTERNET, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='SOFTWARE' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId + "')*100)/100 AS SOFTWARE, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='COMPUTER_HARDWARE' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId
				+ "')*100)/100 AS COMPUTER_HARDWARE, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='REPAIRS' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId + "')*100)/100 AS REPAIRS, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='OTHERS' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId + "')*100)/100 AS OTHERS, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='CASH_LIFT' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND Expenses.hotelId='" + hotelId + "')*100)/100 AS CASH_LIFT "
				+ "FROM Expenses WHERE Expenses.hotelId='" + hotelId + "' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "';";
		
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// total revenue-ap
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport2(String hotelId, String startDate, String endDate) {
		String sql = "SELECT (SELECT ROUND(SUM(TotalRevenue.total)*100)/100 FROM TotalRevenue WHERE TotalRevenue.hotelId='"
				+ hotelId + "' AND TotalRevenue.serviceDate BETWEEN '" + startDate + "' AND '" + endDate
				+ "' ) AS totalRevenue, ROUND(SUM(payment.total)*100)/100 as grossTotal, "
				+ "ROUND(SUM(payment.foodDiscount+ payment.barDiscount)*100)/100 as grossDiscount, "
				+ "ROUND(SUM(payment.gst)*100)/100 as grossTaxes, "
				+ "ROUND(SUM(payment.serviceCharge)*100)/100 as grossServiceCharge, "
				+ "ROUND(ROUND((Select SUM(payment.total) FROM payment WHERE payment.hotelId='" + hotelId
				+ "' AND Payment.orderDate " + getDateString(startDate, endDate)
				+ ")*100)/100 - ROUND((Select SUM(Expenses.amount) From Expenses WHERE expenses.type!='CASH_LIFT' AND Expenses.serviceDate "
				+ getDateString(startDate, endDate) + " AND expenses.hotelId='" + hotelId
				+ "')*100)/100)*100/100 as NetSales FROM Payment WHERE payment.hotelId='" + hotelId + "' "
				+ "AND payment.orderDate "+getDateString(startDate, endDate)+";";
		
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// Total Operating Margin-ap //implementvisible
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport3(String hotelId, String startDate, String endDate) {
		String sql = "SELECT ROUND((SUM(TotalRevenue.total) - (SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.hotelId='"
				+ hotelId + "' AND Expenses.serviceDate BETWEEN '" + startDate + "' AND '" + appendEndDate(endDate)
				+ "'))*100)/100 AS totalOperatingMargin, "
				+ "ROUND((SELECT SUM(Payment.Total) FROM Payment WHERE Payment.hotelId='" + hotelId
				+ "' AND Payment.orderDate " + getDateString(startDate, endDate) + ")*100)/100 AS paidIn, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type!='CASH_LIFT' AND expenses.hotelId='"
				+ hotelId + "' AND Expenses.serviceDate " + getDateString(startDate, endDate)
				+ ")*100)/100 AS paidOut FROM TotalRevenue Where hotelId='" + hotelId + "' "
				+ "AND TotalRevenue.serviceDate BETWEEN '" + startDate + "' AND '" + endDate + "';";
		
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// Operating Metrics-ap
	// main 3
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport4(String hotelId, String startDate, String endDate, boolean visible) {

		String billNo = visible?"Payment.billNo2":"Payment.billNo";
		
		String sql = "SELECT Distinct Orders.serviceType AS serviceType, "
				+ "SUM(Payment.total)/SUM(Orders.numberOfGuests) AS AvgAmountPerGuest, "
				+ "(SUM(Payment.total)/COUNT("+billNo+")) AS AvgAmountPerCheck, SUM(Payment.total) AS Total, "
				+ "SUM(Orders.numberOfGuests) AS noOfGuests, COUNT("+billNo+") AS noOfBills "
				+ "FROM Orders, Payment WHERE Orders.hotelId='" + hotelId + "' "
				+ "AND Orders.orderId=Payment.orderId AND Orders.orderDate " + getDateString(startDate, endDate);

		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		sql += "GROUP BY Orders.serviceType ";
		
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// tables turned-ap
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport5(String hotelId, String startDate, String endDate, boolean visible) {
		
		String billNo = visible?"Payment.billNo2":"Payment.billNo";
		
		String sql = "SELECT SUM(Payment.total)/COUNT(distinct "+billNo+") as AvgAmountPerTableTurned "
				+ "FROM Payment,Orders WHERE payment.orderId=orders.orderId AND orders.inhouse='1' "
				+ "AND orders.serviceType=serviceType AND orders.hotelId='" + hotelId + "' "
				+ "AND orders.orderDate " + getDateString(startDate, endDate);

		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		
		sql += "GROUP BY Orders.serviceType ";
		
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// voids-ap
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport6(String hotelId, String startDate, String endDate) {
		String sql = "SELECT COUNT(orders.orderId) AS voids FROM orders WHERE orders.state='99' "
				+ "AND orders.hotelId='" + hotelId + "' AND orders.orderDate " + getDateString(startDate, endDate)
				+ " GROUP BY Orders.serviceType;";
		
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// returns-ap
	@Override
	public ArrayList<DailyOperationReport> getDailyOperationReport7(String hotelId, String startDate, String endDate) {
		String sql = "SELECT COUNT(Distinct OrderItemLog.orderId) AS returns FROM orderitemlog, orders "
				+ "WHERE orderitemlog.state='100' AND orderitemlog.orderId=orders.orderId "
				+ "AND orderitemlog.hotelId='" + hotelId + "' AND orderitemlog.dateTime " + getDateString(startDate, endDate)
				+ "' GROUP BY Orders.serviceType;";
		
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// Jason
	// Discount Report-ap(edited)
	@Override
	public ArrayList<DiscountReport> getDiscountReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT payment.discountName, payment.orderDate, "
				+ "ROUND(payment.foodDiscount*100)/100 AS foodDiscount, "
				+ "ROUND(payment.barDiscount*100)/100 AS barDiscount, "
				+ "ROUND((payment.foodDiscount+payment.barDiscount)*100)/100 AS totalDiscount, "
				+ "ROUND((payment.total+payment.foodDiscount+payment.barDiscount)*100)/100 AS total, "
				+ "customerName ,"
				+ "payment.total AS discountedTotal "
				+ "FROM payment, orders WHERE payment.orderid = orders.orderid AND discountName!='[]' "
				+ "AND payment.orderDate " + getDateString(startDate, endDate) + " AND payment.hotelId='"
				+ hotelId + "';";
		
		return db.getRecords(sql, DiscountReport.class, hotelId);
	}

	// Jason
	// itemwise-ap(hot selling items/menu collection)
	@Override
	public ArrayList<ItemWiseReport> getItemwiseReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT menuId, station, collection, title, SUM(qty) AS qty FROM ("
				+ "SELECT Menuitems.menuid AS menuId, station, collection, title, "
				+ "SUM(qty) AS qty FROM MenuItems, Orderitems, Orders WHERE Menuitems.menuid = Orderitems.menuid "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ "AND Orderitems.hotelId ='" + hotelId + "' AND MenuItems.station!='Bar' "
				+ "AND Orderitems.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid UNION ALL "
				+ "SELECT Menuitems.menuid AS menuId, station, collection, title, "
				+ "SUM(OrderItemLog.quantity) AS qty FROM MenuItems, OrderItemLog, Orders WHERE menuitems.menuid = OrderItemLog.menuid "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ "AND OrderItemLog.hotelId='" + hotelId + "' AND menuItems.station!='Bar' AND OrderItemLog.state == 50 "
				+ "AND OrderItemLog.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid) "
				+ "GROUP BY menuId ORDER BY station, collection, title;";
		
		return db.getRecords(sql, ItemWiseReport.class, hotelId);
	}

	// liquor-ap(hot selling items/menu collection)
	@Override
	public ArrayList<ItemWiseReport> getLiquorReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT menuId, station, collection, title, SUM(qty) AS qty FROM ("
				+ "SELECT Menuitems.menuid, station, collection, title, "
				+ "SUM(qty) AS qty FROM MenuItems, Orderitems, Orders WHERE menuitems.menuid = orderitems.menuid "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ "AND Orderitems.hotelId='" + hotelId + "' AND menuItems.station='Bar' "
				+ "AND Orderitems.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid UNION ALL "
				+ "SELECT Menuitems.menuid, station, collection, title, "
				+ "SUM(OrderItemLog.quantity) AS qty FROM MenuItems, OrderItemLog, Orders WHERE menuitems.menuid = OrderItemLog.menuid "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ "AND OrderItemLog.hotelId='" + hotelId + "' AND menuItems.station='Bar' AND OrderItemLog.state == 50 "
				+ "AND OrderItemLog.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid) "
				+ "GROUP BY menuId ORDER BY station, collection, title;";
		
		return db.getRecords(sql, ItemWiseReport.class, hotelId);
	}

	//Void Report
	@Override
	public ArrayList<Order> getVoidOrderReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT Orders.id, Orders.billNo, Orders.orderDate, Orders.waiterId, Orders.inhouse, "
				+ "Orders.reason, Payment.foodBill, Payment.barBill, Payment.foodBill + Payment.barBill AS total FROM Orders, Payment WHERE Orders.state = 99 "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ "AND Orders.hotelId='" + hotelId + "' AND Orders.orderId == Payment.orderId ORDER BY Orders.id;";
		
		return db.getRecords(sql, Order.class, hotelId);
	}

	//Void Report
	@Override
	public ArrayList<Order> getNCOrderReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT Orders.id, Orders.billNo, Orders.orderDate, Orders.waiterId, Orders.inhouse, "
				+ "Orders.reference, Payment.foodBill, Payment.barBill, Payment.foodBill + Payment.barBill AS total FROM Orders, Payment WHERE Orders.inhouse = 4 "
				+ "AND Orders.orderId == Payment.orderId "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate)
				+ "AND Orders.hotelId='" + hotelId + "' ORDER BY Orders.id;";
		
		return db.getRecords(sql, Order.class, hotelId);
	}
	
	//Returned Item  Report
	@Override
	public ArrayList<ReturnedItemsReport> getReturnedItemsReport(String hotelId, String startDate, String endDate, boolean visible) {
		
		String billNo = visible?"Orders.billNo2 AS billNo":"Orders.billNo";
		
		String sql = "SELECT Orders.id, "+billNo+", Orders.orderDate, Orders.waiterId, Orders.inhouse, "
				+ "MenuItems.title, OrderItemLog.dateTime, OrderItemLog.quantity, OrderItemLog.reason, OrderItemLog.rate "
				+ ", (OrderItemLog.rate*OrderItemLog.quantity) AS total FROM Orders, OrderItemlog, MenuItems WHERE (OrderItemlog.state = 100 OR OrderItemlog.state = 101) "
				+ "AND Orders.hotelId =='" + hotelId + "' AND OrderItemlog.orderId == Orders.orderId "
				+ "AND OrderItemLog.menuId == MenuItems.menuId AND Orders.orderDate " + getDateString(startDate, endDate);
		
		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		
		sql += " ORDER BY OrderItemlog.id;";
		
		return db.getRecords(sql, ReturnedItemsReport.class, hotelId);
	}
	
	//Returned Item  Report
	@Override
	public ArrayList<ReturnedItemsReport> getComplimentaryItemsReport(String hotelId, String startDate, String endDate, boolean visible) {

		String billNo = visible?"Orders.billNo2 AS billNo":"Orders.billNo";
		
		String sql = "SELECT Orders.id, "+billNo+", Orders.orderDate, Orders.waiterId, Orders.inhouse, "
				+ "MenuItems.title, OrderItemLog.dateTime, OrderItemLog.quantity, OrderItemLog.rate "
				+ ", (OrderItemLog.rate*OrderItemLog.quantity) AS total FROM Orders, OrderItemlog, MenuItems WHERE OrderItemlog.state = 50 "
				+ "AND OrderItemlog.dateTime " + getDateString(startDate, endDate)
				+ "AND Orders.hotelId =='" + hotelId + "' AND OrderItemlog.orderId == Orders.orderId "
				+ "AND OrderItemLog.menuId == MenuItems.menuId ";
		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		sql += " ORDER BY OrderItemlog.id;";
		
		return db.getRecords(sql, ReturnedItemsReport.class, hotelId);
	}

	@Override
	public PaymentWiseSalesReport getPaymentWiseSalesReport(String hotelId, String startDate, String endDate,
			Integer i, boolean visible) {

		String[] cardTypes = this.getEnums(CardType.class);
		String[] onlinePaymentTypes = this.getEnums(OnlinePaymentType.class);
		
		String sql = "SELECT ROUND(SUM(Payment.foodBill)*100)/100 AS foodBill, ROUND(SUM(Payment.barBill)*100)/100 AS barBill, "
				+ "ROUND(SUM(Payment.total)*100)/100 AS total, "
				+ "SUM(Orders.numberOfGuests) AS cover, "
				+ "ROUND(SUM(Payment.cashPayment)*100)/100 AS cash, "
				+ "ROUND(SUM(Payment.cardPayment)*100)/100 AS card, "
				+ "ROUND(SUM(Payment.appPayment)*100)/100 AS app, "
				+ "ROUND(SUM(Payment.walletPayment)*100)/100 AS wallet, "
				+ "ROUND(SUM(Payment.promotionalCash)*100)/100 AS promotionalCash, "
				+ "ROUND(SUM(Payment.creditAmount)*100)/100 AS credit";
		
		for (String paymentType : cardTypes) {
			sql += ", (SELECT ifnull(ROUND(SUM(Payment.cardPayment)*100)/100, 0) FROM Payment,Orders WHERE Payment.cardType LIKE '%"+paymentType+"%' AND Payment.hotelId='"
				+ hotelId + "' AND Payment.orderDate " + getDateString(startDate, endDate)
				+ " AND Orders.inhouse='" + i + "' AND Payment.orderId = Orders.orderId ) AS "+paymentType;
		}
		for (String paymentType : onlinePaymentTypes) {
			sql += ", (SELECT ifnull(ROUND(SUM(Payment.appPayment)*100)/100, 0) FROM Payment,Orders WHERE (Payment.cardType LIKE '%"+paymentType+"' OR Payment.cardType LIKE '%"+paymentType+"/%') AND Payment.hotelId='"
				+ hotelId + "' AND Payment.orderDate " + getDateString(startDate, endDate)
				+ " AND Orders.inhouse='" + i + "' AND Payment.orderId = Orders.orderId ) AS "+paymentType;
		}
		sql += " FROM Payment, Orders WHERE Payment.orderId = Orders.orderId AND Payment.hotelId='" + hotelId
				+ "' AND Payment.orderdate " + getDateString(startDate, endDate) + "AND Orders.inhouse='"
				+ i + "' ";
		
		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		
		System.out.println(sql);
		return db.getOneRecord(sql, PaymentWiseSalesReport.class, hotelId);
	}

	@Override
	public ArrayList<OnlineOrderingSalesReport> getOnlineOrderingSalesReport(String hotelId, String startDate, String endDate) {

		String sql = "SELECT OnlineOrderingPortals.name AS portalName, "
				+ "OnlineOrderingPortals.id AS portalId, "
				+ "ROUND(SUM(Payment.foodBill)*100)/100 AS totalBillAmount, "
				+ "ROUND(SUM(Payment.total)*100)/100 AS netAmount, "
				+ "ROUND(SUM(Payment.gst)*100)/100 AS gst, "
				+ "ROUND(SUM(Payment.foodDiscount)*100)/100 AS discount, "
				+ "ROUND(SUM(Payment.appPayment)*100)/100 AS app, "
				+ "ROUND(SUM(Payment.packagingCharge)*100)/100 AS packagingCharge, "
				+ "ROUND(SUM(Payment.serviceCharge)*100)/100 AS serviceCharge, "
				+ "ROUND(SUM(Payment.deliveryCharge)*100)/100 AS deliveryCharge,"
				+ "OnlineOrderingPortals.commisionType, "
				+ "OnlineOrderingPortals.commisionValue,"
				+ "ROUND(SUM(Payment.total*commisionValue))/100 AS commisionAmount,"
				+ "SUM(Payment.foodBill)/100 AS tds "
				+ "FROM Payment, orders "
				+ "LEFT OUTER JOIN OnlineOrderingPortals ON Orders.takeAwayType == OnlineOrderingPortals.id "
				+ "WHERE Payment.orderId == Orders.orderId AND Orders.inhouse == 2 AND (Orders.takeAwayType != 100 ) "
				+ "AND Orders.orderDate "+ getDateString(startDate, endDate)
				+ "GROUP BY Orders.takeAwayType;";
		
		System.out.println(sql);
		return db.getRecords(sql, OnlineOrderingSalesReport.class, hotelId);
	}
	
	@Override
	public String[] getEnums(Class<? extends Enum<?>> e) {
	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}

	@Override
	public ArrayList<Attendance> getAttendanceReport(String hotelId, String startDate, String endDate) {

		String sql = "SELECT Employee.employeeId, Employee.salary, Employee.firstName, Employee.surName, Attendance.shift, Attendance.checkInDate, Attendance.reason, Attendance.authorisation, Attendance.checkInTime, Attendance.checkOutTIme, "
				+ "REPLACE(REPLACE(REPLACE(Attendance.isPresent, " + PRESENT + ", \"PRESENT\") , " + ABSENT
				+ ", \"ABSENT\"), " + EXCUSED + ", \"EXCUSED\") AS attendanceStr, "
				+ "	(SELECT COUNT(isPresent) FROM Attendance WHERE 	Attendance.checkInDate " 
				+ getDateString(startDate, endDate) + " AND Attendance.employeeId = Employee.employeeId"
				+ "	AND Attendance.authorisation = 1 	AND Attendance.isPresent=2 " + " AND Attendance.hotelId = '"
				+ hotelId + "' GROUP BY Attendance.employeeId ) AS excusedCount, (SELECT COUNT(isPresent) FROM Attendance "
				+ "	WHERE Attendance.checkInDate " + getDateString(startDate, endDate)
				+ "	AND Attendance.employeeId = Employee.employeeId	AND Attendance.authorisation = 1 "
				+ "	AND Attendance.isPresent=3 	AND Attendance.hotelId = '" + hotelId + "' "
				+ "	GROUP BY Attendance.employeeId ) AS absentCount,	(SELECT COUNT(isPresent) FROM Attendance "
				+ "	WHERE Attendance.checkInDate " + getDateString(startDate, endDate)
				+ "	AND Attendance.employeeId = Employee.employeeId	AND Attendance.authorisation = 1 "
				+ "	AND Attendance.isPresent=1 	AND Employee.hotelId = '" + hotelId + "' "
				+ "	GROUP BY Attendance.employeeId ) AS presentCount FROM Employee, Attendance "
				+ "WHERE Employee.employeeId = Attendance.employeeId AND Attendance.checkInDate " + getDateString(startDate, endDate)
				+ " AND Attendance.authorisation = 1 AND Employee.hotelId = '" + hotelId
				+ "' ORDER BY Attendance.employeeId, Attendance.checkInDate, Attendance.shift";
		return db.getRecords(sql, Attendance.class, hotelId);
	}

	@Override
	public ArrayList<Attendance> getAttendanceReportB(String hotelId, String startDate, String endDate) {

		String sql = "SELECT distinct Attendance.checkInDate FROM Attendance "
				+ "WHERE Attendance.checkInDate " + getDateString(startDate, endDate)
				+ "AND Attendance.authorisation = 1 AND Attendance.hotelId = '" + hotelId + "' "
				+ "ORDER BY Attendance.checkInDate";
		return db.getRecords(sql, Attendance.class, hotelId);
	}

	@Override
	public Report getTotalSalesForService(String hotelId, String serviceDate, String serviceType, String section, boolean visible) {
		
		String sql2 = "";
		if(!visible)
			sql2 = " AND Orders.state != "+ ORDER_STATE_HIDDEN;
		
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		String sql = "SELECT ROUND(SUM(Payment.foodBill+Payment.barBill+gst+VATBAR+serviceCharge+Payment.packagingCharge+Payment.deliveryCharge+Payment.complimentary)*100)/100 AS grossTotal, "
				+ "ROUND(SUM(Payment.foodBill+gst+serviceCharge+Payment.packagingCharge+Payment.deliveryCharge+Payment.complimentary)*100)/100 AS foodGross, "
				+ "ROUND(SUM(Payment.barBill+VATBAR)*100)/100 AS barGross, "
				+ "ROUND(SUM(Payment.foodBill)*100)/100 AS foodBill, "
				+ "ROUND(SUM(Payment.barBill)*100)/100 AS barBill, "
				+ "ROUND(SUM(Payment.foodBill-Payment.foodDiscount+gst)*100)/100 AS foodSale, "
				+ "ROUND(SUM(Payment.barBill-Payment.barDiscount+VATBAR)*100)/100 AS barSale, "
				+ "ROUND(SUM(Payment.total)*100)/100 AS total, "
				+ "ROUND(SUM(Payment.total-Payment.gst-Payment.serviceCharge-Payment.VATBAR-Payment.packagingCharge-Payment.deliveryCharge)*100)/100 AS netSale, "
				+ "ROUND(SUM(Payment.foodDiscount)*100)/100 AS foodDiscount, "
				+ "ROUND(SUM(Payment.barDiscount)*100)/100 AS barDiscount, "
				+ "ROUND(SUM(Payment.gst)*100)/100 AS gst, "
				+ "ROUND(SUM(Payment.serviceCharge)*100)/100 AS serviceCharge, "
				+ "ROUND(SUM(Payment.VATBAR)*100)/100 AS VATBAR, "
				+ "ROUND(SUM(Payment.complimentary)*100)/100 AS complimentary, "
				+ "ROUND(SUM(Payment.loyaltyAmount)*100)/100 AS loyalty, "
				+ "ROUND(SUM(Payment.packagingCharge)*100)/100 AS packagingCharge, "
				+ "ROUND(SUM(Payment.deliveryCharge)*100)/100 AS deliveryCharge, "
				+ "ROUND(SUM(Payment.cardPayment)*100)/100 AS cardPayment, "
				+ "ROUND(SUM(Payment.walletPayment)*100)/100 AS walletPayment, "
				+ "ROUND(SUM(Payment.appPayment)*100)/100 AS appPayment, "
				+ "ROUND(SUM(Payment.promotionalCash)*100)/100 AS promotionalCash, "
				+ "ROUND(SUM(Payment.creditAmount)*100)/100 AS creditAmount, "
				+ "ROUND(SUM(Payment.roundOff)*1000)/1000 AS roundOff, "
				+ "SUM(Orders.printCount) AS printCount, "
				+ "(SELECT SUM(Orders.printCount-1) FROM Orders WHERE printCount >1 AND orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ") AS reprints, COUNT(*) AS orderCount, "
				+ "ROUND((SELECT SUM(cashPayment) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND Orders.takeAwayType != '"
				+ portalDao.getOnlineOrderingPortalByPortal(hotelId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS cashPayment, "
				+ "ROUND((SELECT SUM(cashPayment) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND Orders.takeAwayType == '"
				+ portalDao.getOnlineOrderingPortalByPortal(hotelId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS zomatoCash, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 1 AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS inhouse, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 0 AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS homeDelivery, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND "
				+ "(takeAwayType == 5 OR takeAwayType == 6 OR takeAwayType == 100) AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS takeAway, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND takeAwayType = 1 AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS zomatoSale, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND takeAwayType = 2 AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS swiggySale, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND takeAwayType = 3 AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS uberEatsSale, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND takeAwayType = 4 AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS foodPandaSale, "
				+ "ROUND((SELECT SUM(Payment.foodBill+Payment.barBill) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2 + " AND cardType = 'NON_CHARGEABLE' "
				+ ")*100)/100 AS nc FROM Payment, Orders WHERE Payment.orderId = Orders.orderId "
				+ "AND Orders.orderDate = '" + serviceDate + "' AND Orders.hotelId = '" + hotelId + "' "
				+ "AND Orders.serviceType = '" + serviceType + "' AND Payment.cardType != 'VOID'"
				+ sql2;

		IOutlet outlet = new OutletManager(false);
		Settings setting = outlet.getSettings(hotelId);
		if(setting.hasSection())
			sql += " AND Payment.section = '" + section + "';";
		else
			sql += ";";
		
		return db.getOneRecord(sql, Report.class, hotelId);
	}

	@Override
	public Report getAnalytics(String hotelId, String startDate, String endDate) {
		
		endDate = appendEndDate(endDate);
		String dateStr = getDateString(startDate, endDate);
		
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		String sql = "SELECT ROUND(SUM(Payment.foodBill+Payment.barBill+gst+VATBAR+serviceCharge+Payment.packagingCharge+Payment.deliveryCharge+Payment.complimentary)*100)/100 AS grossTotal, "
				+ "ROUND(SUM(Payment.foodBill+gst+serviceCharge+Payment.packagingCharge+Payment.deliveryCharge+Payment.complimentary)*100)/100 AS foodGross, "
				+ "ROUND(SUM(Payment.barBill+VATBAR)*100)/100 AS barGross, "
				+ "ROUND(SUM(Payment.foodBill)*100)/100 AS foodBill, "
				+ "ROUND(SUM(Payment.barBill)*100)/100 AS barBill, "
				+ "ROUND(SUM(Payment.foodBill-Payment.foodDiscount+gst)*100)/100 AS foodSale, "
				+ "ROUND(SUM(Payment.barBill-Payment.barDiscount+VATBAR)*100)/100 AS barSale, "
				+ "ROUND(SUM(Payment.total)*100)/100 AS total, "
				+ "ROUND(SUM(Payment.total-Payment.gst-Payment.serviceCharge-Payment.VATBAR-Payment.packagingCharge-Payment.deliveryCharge)*100)/100 AS netSale, "
				+ "ROUND(SUM(Payment.foodDiscount)*100)/100 AS foodDiscount, "
				+ "ROUND(SUM(Payment.barDiscount)*100)/100 AS barDiscount, "
				+ "ROUND(SUM(Payment.gst)*100)/100 AS gst, "
				+ "ROUND(SUM(Payment.serviceCharge)*100)/100 AS serviceCharge, "
				+ "ROUND(SUM(Payment.VATBAR)*100)/100 AS VATBAR, "
				+ "ROUND(SUM(Payment.complimentary)*100)/100 AS complimentary, "
				+ "ROUND(SUM(Payment.loyaltyAmount)*100)/100 AS loyalty, "
				+ "ROUND(SUM(Payment.packagingCharge)*100)/100 AS packagingCharge, "
				+ "ROUND(SUM(Payment.deliveryCharge)*100)/100 AS deliveryCharge, "
				+ "ROUND(SUM(Payment.cardPayment)*100)/100 AS cardPayment, "
				+ "ROUND(SUM(Payment.walletPayment)*100)/100 AS walletPayment, "
				+ "ROUND(SUM(Payment.appPayment)*100)/100 AS appPayment, "
				+ "ROUND(SUM(Payment.promotionalCash)*100)/100 AS promotionalCash, "
				+ "ROUND(SUM(Payment.creditAmount)*100)/100 AS creditAmount, "
				+ "ROUND(SUM(Payment.roundOff)*1000)/1000 AS roundOff, "
				+ "SUM(Orders.printCount) AS printCount, "
				+ "(SELECT SUM(Orders.printCount-1) FROM Orders WHERE printCount >1 AND orders.orderDate "
				+ dateStr + " AND Orders.hotelId = '" + hotelId + "' "
				+ ") AS reprints, COUNT(*) AS orderCount, "
				+ "ROUND((SELECT SUM(cashPayment) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND Orders.takeAwayType != '"
				+ portalDao.getOnlineOrderingPortalByPortal(hotelId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate "
				+ dateStr + " AND Orders.hotelId = '" + hotelId + "' "
				+ ")*100)/100 AS cashPayment, "
				+ "ROUND((SELECT SUM(cashPayment) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND Orders.takeAwayType == '"
				+ portalDao.getOnlineOrderingPortalByPortal(hotelId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate "
				+ dateStr + " AND Orders.hotelId = '" + hotelId + "' "
				+ ")*100)/100 AS zomatoCash, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 1 AND Orders.orderDate "
				+ dateStr + " AND Orders.hotelId = '" + hotelId + "' "
				+ ")*100)/100 AS inhouse, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 0 AND Orders.orderDate "
				+ dateStr + " AND Orders.hotelId = '" + hotelId + "' "
				+ ")*100)/100 AS homeDelivery, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND "
				+ "(takeAwayType == 5 OR takeAwayType == 100) AND Orders.orderDate "
				+ dateStr + " AND Orders.hotelId = '" + hotelId + "' "
				+ ")*100)/100 AS takeAway, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND (takeAwayType = 1  OR takeAwayType = 6) AND Orders.orderDate "
				+ dateStr + " AND Orders.hotelId = '" + hotelId + "' "
				+ ")*100)/100 AS zomatoSale, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND takeAwayType = 2 AND Orders.orderDate "
				+ dateStr + " AND Orders.hotelId = '" + hotelId + "' "
				+ ")*100)/100 AS swiggySale, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND takeAwayType = 3 AND Orders.orderDate "
				+ dateStr + " AND Orders.hotelId = '" + hotelId + "' "
				+ ")*100)/100 AS uberEatsSale, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND takeAwayType = 4 AND Orders.orderDate "
				+ dateStr + " AND Orders.hotelId = '" + hotelId + "' "
				+ ")*100)/100 AS foodPandaSale, "
				+ "ROUND((SELECT SUM(Payment.foodBill+Payment.barBill) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND Orders.orderDate "
				+ dateStr + " AND Orders.hotelId = '" + hotelId + "' " + " AND cardType = 'NON_CHARGEABLE' "
				+ ")*100)/100 AS nc FROM Payment, Orders WHERE Payment.orderId = Orders.orderId "
				+ "AND Orders.orderDate " + dateStr + " AND Orders.hotelId = '" + hotelId
				+ "' AND Payment.cardType != 'VOID';";
		
		return db.getOneRecord(sql, Report.class, hotelId);
	}
	
	public BigDecimal getBarComplimentary(String hotelId, String serviceDate, String serviceType) {
		
		String sql = "SELECT OrderItemLog.rate AS entityId FROM OrderItemLog, Orders, MenuItems WHERE OrderItemLog.state = 50 "
				+ "AND Orders.orderId == OrderItemLog.orderId AND Orders.orderDate = '"+serviceDate
				+ "' AND Orders.serviceType = '"+serviceType+"' AND MenuItems.menuId == OrderItemLog.menuId "
				+ "AND MenuItems.flags LIKE '%5%';";
		
		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		
		if(entity == null) {
			return new BigDecimal(0.0);
		}
		return entity.getId();
	}

	@Override
	public Report getTotalSalesForDay(String hotelId, String serviceDate, String serviceType, String section, boolean visible) {
		
		String sql2 = "";
		if(!visible)
			sql2 = " AND Orders.state != "+ ORDER_STATE_HIDDEN;
		
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		String sql = "SELECT ROUND(SUM(Payment.foodBill)*100)/100 AS foodBill, "
				+ "ROUND(SUM(Payment.barBill)*100)/100 AS barBill, "
				+ "ROUND(SUM(Payment.cardPayment)*100)/100 AS cardPayment, "
				+ "ROUND(SUM(Payment.walletPayment)*100)/100 AS walletPayment, "
				+ "ROUND(SUM(Payment.appPayment)*100)/100 AS appPayment, "
				+ "ROUND(SUM(Payment.promotionalCash)*100)/100 AS promotionalCash, "
				+ "ROUND(SUM(Payment.creditAmount)*100)/100 AS creditAmount, "
				+ "ROUND((SELECT SUM(cashPayment) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND Orders.takeAwayType != '"
				+ portalDao.getOnlineOrderingPortalByPortal(hotelId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS cashPayment "
				+ " FROM Payment, Orders WHERE Payment.orderId == Orders.orderId "
				+ "AND Orders.orderDate = '" + serviceDate + "' AND Orders.hotelId = '" + hotelId + "' "
				+ "AND Payment.cardType LIKE '%CASH%' "
				+ "AND Orders.serviceType = '" + serviceType + "' AND (Payment.cardType != 'VOID' || Payment.cardType != 'NON CHARGEABLE')"
				+ sql2;

		IOutlet outlet = new OutletManager(false);
		Settings setting = outlet.getSettings(hotelId);
		if(setting.hasSection())
			sql += " AND Payment.section = '" + section + "';";
		else
			sql += ";";
		
		return db.getOneRecord(sql, Report.class, hotelId);
	}

	@Override
	public Report getTotalSalesForMonth(String hotelId, String month, String section, boolean visible) {
		
		String sql2 = "";
		if(!visible)
			sql2 = " AND Orders.state != "+ ORDER_STATE_HIDDEN;
		
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		String sql = "SELECT ROUND(SUM(Payment.complimentary+Payment.loyaltyAmount+payment.foodDiscount+payment.barDiscount+"
				+ "Payment.foodBill+Payment.barBill+gst+VATBAR+serviceCharge)*100)/100 AS grossTotal, "
				+ "ROUND(SUM(Payment.foodBill)*100)/100 AS foodBill, "
				+ "ROUND(SUM(Payment.barBill)*100)/100 AS barBill, "
				+ "ROUND((SELECT SUM(cashPayment) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND Orders.takeAwayType != '"
				+ portalDao.getOnlineOrderingPortalByPortal(hotelId, OnlineOrderingPortals.ZOMATO).getId()+"' AND Orders.orderDate LIKE '"
				+ month + "%' AND Orders.hotelId = '" + hotelId  + "' " + sql2
				+ ")*100)/100 AS cashPayment "
				+ " FROM Payment, Orders WHERE Payment.orderId = Orders.orderId "
				+ "AND Orders.orderDate LIKE '" + month + "%' AND Orders.hotelId = '" + hotelId
				+ "' AND Payment.cardType LIKE '%CASH%'"
				+ sql2;

		IOutlet outlet = new OutletManager(false);
		Settings setting = outlet.getSettings(hotelId);
		if(setting.hasSection())
			sql += " AND Payment.section = '" + section + "';";
		else
			sql += ";";
		return db.getOneRecord(sql, Report.class, hotelId);
	}

	@Override
	public ArrayList<Expense> getCashExpenses(String hotelId, String serviceDate, String serviceType, String section) {
		String sql = "SELECT * FROM Expenses WHERE accountName = 'CASH_DRAWER' AND hotelId = '" + hotelId + "' "
				+ "AND serviceDate = '" + serviceDate + "' AND serviceType = '" + serviceType;
		
		IOutlet outlet = new OutletManager(false);
		Settings setting = outlet.getSettings(hotelId);
		if(setting.hasSection())
			sql += "' AND section = '" + section + "';";
		else
			sql += "';";

		return db.getRecords(sql, Expense.class, hotelId);
	}

	@Override
	public BigDecimal getCardPaymentByType(String hotelId, String serviceDate, String serviceType, String cardType) {

		String sql = "SELECT SUM(Payment.cardPayment) AS entityId FROM Payment, Orders "
				+ "WHERE Payment.orderId = Orders.orderId AND Orders.orderDate = '" + serviceDate + "' "
				+ "AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' "
				+ "AND Payment.cardType LIKE '%" + cardType + "%';";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return new BigDecimal("0");
	}

	@Override
	public int getAppPaymentByType(String hotelId, String startDate, String endDate, String serviceType, String cardType) {

		endDate = appendEndDate(endDate);
		if(!serviceType.isEmpty()) {
			serviceType = "AND Orders.serviceType = '" + serviceType + "' ";
		}
		
		String sql = "SELECT SUM(Payment.appPayment) AS entityId FROM Payment, Orders "
				+ "WHERE Payment.orderId = Orders.orderId AND Orders.orderDate " + getDateString(startDate, endDate)
				+ " AND Orders.hotelId = '" + hotelId + "' " + serviceType
				+ "AND Payment.cardType LIKE '%" + cardType + "';";

		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	@Override
	public int getVoidTransactions(String hotelId, String serviceDate, String serviceType) {

		String sql = "SELECT COUNT(Orders.orderId) AS entityId FROM Orders "
				+ "WHERE Orders.orderDate = '" + serviceDate + "' "
				+ "AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' "
				+ "AND Orders.state = 99;";

		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}
	
	//Round off sql
	//select ROUND((total-(Payment.foodBill+Payment.barBill+gst+VATBAR-foodDiscount-barDiscount))*100)/100 AS roundOff , cardType, orderID from Payment where (cardType != 'VOID' AND cardType != 'NON_CHARGEABLE') order by roundOff desc 

	@Override
	public ArrayList<Report> getSaleSummaryReport(String hotelId, String startDate, String endDate,
			int orderType, int portalId, boolean visible) {

		String billNo = visible?"Payment.billNo2":"Payment.billNo";
		
		String sql = "SELECT ROUND(Payment.foodBill*100)/100 AS foodBill, "
				+ "ROUND(Payment.barBill*100)/100 AS barBill, "
				+ "ROUND((Payment.foodBill+Payment.barBill)*100)/100 AS totalBill, "
				+ billNo +" AS billNo, "
				+ "ROUND(Payment.total*100)/100 AS total, "
				+ "ROUND(Payment.foodDiscount*100)/100 AS foodDiscount, "
				+ "ROUND(Payment.barDiscount*100)/100 AS barDiscount, "
				+ "ROUND((Payment.foodDiscount+Payment.barDiscount)*100)/100 AS totalDiscount, "
				+ "ROUND(Payment.VATBAR*100)/100 AS vatBar, "
				+ "ROUND(Payment.serviceCharge*100)/100 AS serviceCharge, "
				+ "ROUND(Payment.tip*100)/100 AS tip, "
				+ "ROUND(Payment.gst*100)/100 AS gst, "
				+ "ROUND(Payment.roundOff*100)/100 AS roundOff, "
				+ "Orders.numberOfGuests AS covers, "
				+ "Orders.inhouse AS inhouse, "
				+ "Orders.takeAwayType AS takeAwayType, "
				+ "Orders.state AS state, "
				+ "Orders.tableId AS tableId, "
				+ "Orders.orderDate AS orderDate, "
				+ "Orders.completeTimestamp AS completeTimestamp, "
				+ "Orders.customerName AS customerName, "
				+ "Orders.externalOrderId AS externalOrderId, "
				+ "Orders.reference AS reference, "
				+ "Orders.remarks AS remarks, "
				+ "ROUND(Payment.cashPayment*100)/100 AS cashPayment, "
				+ "ROUND(Payment.cardPayment*100)/100 AS cardPayment, "
				+ "ROUND(Payment.appPayment*100)/100 AS appPayment, "
				+ "ROUND(Payment.walletPayment*100)/100 AS walletPayment, "
				+ "ROUND(Payment.creditAmount*100)/100 AS creditAmount, "
				+ "ROUND(Payment.promotionalCash*100)/100 AS promotionalCash, "
				+ "ROUND(Payment.loyaltyAmount*100)/100 AS loyalty, "
				+ "Payment.cardType AS cardType, "
				+ "Orders.section AS section "
				+ "FROM Orders, Payment WHERE Orders.orderId == Payment.orderId "
				+ "AND Orders.hotelId = '" + hotelId
				+ "' AND Orders.orderDate" + getDateString(startDate, endDate);
 
		
		if(orderType != 1000) {
			sql += "AND Orders.inhouse = " + orderType + " ";
		}
		if(portalId != 0) {
			sql += "AND Orders.takeAwayType = " + portalId + " ";
		}
		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		
		return db.getRecords(sql, Report.class, hotelId);
	}

	@Override
	public ArrayList<CustomerReport> getCustomerReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT ROUND(SUM(OrderItems.rate*qty)*100)/100 AS totalSpent, "
				+ "ROUND(SUM(OrderItems.rate*qty)/SUM(Orders.numberOfGuests)) AS spentPerPax, "
				+ "ROUND(SUM(OrderItems.rate*qty)/COUNT(Orders.orderId)) AS spentPerWalkin, "
				+ "SUM(Orders.numberOfGuests) AS totalGuests, Orders.customerName AS customerName, "
				+ "Orders.customerNumber AS mobileNumber, COUNT(Orders.orderId) AS totalWalkins "
				+ "FROM OrderItems, Orders WHERE OrderItems.orderId == Orders.orderId AND Orders.hotelId = '" + hotelId
				+ "' AND OrderItems.hotelId = '" + hotelId + "' AND orderDate "+ getDateString(startDate, endDate) +" GROUP BY Orders.customerNumber;";

		return db.getRecords(sql, CustomerReport.class, hotelId);
	}

	@Override
	public ArrayList<CustomerReport> getCustomerReviewReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT Customers.firstName, Customers.surName, Customers.mobileNumber, Orders.rating_ambiance, Orders.rating_hygiene, Orders.rating_qof, "
				+ "Orders.rating_service, REPLACE(Orders.reviewSuggestions, ',', ';') AS reviewSuggestions, payment.total, Orders.billNo, Orders.numberOfGuests "
				+ "FROM Customers, Orders, Payment "
				+ "WHERE Customers.mobileNumber == Orders.customerNumber AND Orders.hotelId == '"+hotelId+"' "
				+ "AND Orders.orderId == Payment.orderId AND Orders.orderdate " + getDateString(startDate, endDate)
				+ "AND mobileNumber != '' AND rating_ambiance is not null AND (rating_ambiance+rating_hygiene+rating_qof+rating_service) > 0";

		return db.getRecords(sql, CustomerReport.class, hotelId);
	}

	@Override
	public Report getDailyIncome(String hotelId, String startDate, String endDate, int inhouse) {

		String sql = "SUM(Payment.total) AS total, SUM(Orders.numberOfGuests) AS pax, "
				+ "COUNT(Orders.Id) AS checks, SUM(Payment.foodDiscount) AS foodDiscount, SUM(Payment.barDiscount) AS foodDiscount, "
				+ "SUM(Payment.serviceCharge) AS serviceCharge SUM(Payment.serviceTax) AS serviceTax "
				+ "SUM(Payment.gst) AS gst SUM(Payment.VATFOOD) AS VATFOOD SUM(Payment.VATBAR) AS VATBAR "
				+ "SUM(Payment.sbCess) AS sbCess SUM(Payment.kkCess) AS kkCess FROM Payment, Orders "
				+ "WHERE Payment.orderId = Orders.orderId AND Orders.inhouse = " + inhouse + "AND Orders.hotelId = '"
				+ hotelId + "' AND Orders.orderDate " + getDateString(startDate, endDate) + ";";

		return db.getOneRecord(sql, Report.class, hotelId);
	}

	@Override
	public ArrayList<Expense> getDailyExpense(String hotelId, String startDate, String endDate) {

		String sql = "SELECT SUM(Expense.amount) AS amount, SUM(Expense.type) AS type FROM Expense "
				+ "WHERE hotelId = '" + hotelId + "' AND Expense.date " + getDateString(startDate, endDate) + ";";

		return db.getRecords(sql, Expense.class, hotelId);
	}

	@Override
	public ArrayList<IncentiveReport> getIncentivisedItemReport(String hotelId) {
		String sql = "SELECT title, incentive, hasIncentive AS qty FROM MenuItems WHERE hasIncentive != 0 AND hotelId = '" + hotelId + "' "
				+ "ORDER BY title";
		
		return db.getRecords(sql, IncentiveReport.class, hotelId);
	}
	
	@Override
	public IncentiveReport getIncentiveForEmployee(String hotelId, String userId, boolean isBar, String startDate,
			String endDate) {

		String sql = "SELECT SUM(sale) AS sale, SUM(incentive) AS incentive "
				+ "FROM (SELECT (SUM(OrderItems.qty)/MenuItems.hasIncentive)*MenuItems.incentive AS incentive, "
				+ "SUM(OrderItems.rate*OrderItems.qty) AS sale FROM OrderItems, MenuItems, Orders "
				+ "WHERE OrderItems.menuId == MenuItems.menuId AND MenuItems.hasIncentive !=0 "
				+ "AND OrderItems.waiterId = '" + userId + "' AND OrderItems.hotelId = '" + hotelId + "' "
				+ "AND Orders.hotelId == OrderItems.hotelId AND Orders.orderId == OrderItems.orderId "
				+ "AND OrderItems.hotelId == MenuItems.hotelId AND Orders.orderDate " + getDateString(startDate, endDate);

		if (isBar)
			sql += "AND MenuItems.vegType = 3 ";
		else
			sql += "AND MenuItems.vegType != 3 ";

		sql += "GROUP BY Orders.orderId, MenuItems.menuId " + "ORDER BY Orders.orderId) WHERE incentive>0;";
		

		return db.getOneRecord(sql, IncentiveReport.class, hotelId);
	}

	@Override
	public ArrayList<IncentiveReport> getItemwiseIncentiveReport(String hotelId, String userId, String startDate,
			String endDate, boolean isBar) {

		String sql = "SELECT title, orderId, SUM(qty) AS qty, SUM(incentive) AS incentive FROM (SELECT MenuItems.title AS title, Orders.orderId, SUM(OrderItems.qty) AS qty,"
				+ "(SUM(OrderItems.qty)/MenuItems.hasIncentive)*MenuItems.incentive AS incentive, "
				+ "OrderItems.waiterId AS userId FROM OrderItems, MenuItems, Orders WHERE OrderItems.menuId == MenuItems.menuId AND MenuItems.hasIncentive !=0 "
				+ "AND OrderItems.waiterId = '" + userId + "' AND OrderItems.hotelId = '" + hotelId + "' "
				+ "AND Orders.hotelId == OrderItems.hotelId AND Orders.orderId == OrderItems.orderId "
				+ "AND OrderItems.hotelId == MenuItems.hotelId AND Orders.orderDate " + getDateString(startDate, endDate);

		if (isBar)
			sql += "AND MenuItems.vegType = 3 ";
		else
			sql += "AND MenuItems.vegType != 3 ";

		sql += "GROUP BY Orders.orderId, MenuItems.menuId "
				+ "ORDER BY Orders.orderId) WHERE incentive>0 GROUP BY title ORDER BY title;";

		return db.getRecords(sql, IncentiveReport.class, hotelId);
	}

	@Override
	public ArrayList<DeliveryReport> getDeliveryReport(String hotelId, String userId, String startDate,
			String endDate, boolean visible) {

		String billNo = visible?"Orders.billNo2 AS billNo":"Orders.billNo";
		
		String sql = "SELECT "+billNo+", Orders.deliveryBoy, deliveryTimeStamp AS dispatchtime, Payment.total FROM Orders, Payment " + 
				"WHERE Orders.orderId == Payment.orderId AND Orders.inhouse == 0 "
				+ "AND Orders.deliveryBoy = '" + userId + "' AND Orders.hotelId = '" + hotelId + "' "
				+ "AND Orders.orderDate " + getDateString(startDate, endDate);

		sql += "ORDER BY Orders.id;";

		return db.getRecords(sql, DeliveryReport.class, hotelId);
	}
	
	@Override
	public ArrayList<ConsumptionReport> getConsumptionReport(String hotelId, String startDate, String endDate, int department) {
		
		String depSql = "";
		if(department == DEPARTMENT_FOOD)
			depSql = " (MenuItems.vegType == 1 OR MenuItems.vegType == 2) ";
		else if(department == DEPARTMENT_NON_ALCOHOLIC_BEVERAGE)
			depSql = " (MenuItems.vegType == 4) ";
		else
			depSql = " (MenuItems.vegType == 3) ";
		
		String dateStr = getDateString(startDate, endDate);
		
		String sql ="SELECT *, (totalSaleQty+totalCompQty) AS totalQty, "
				+ "ROUND(totalAfterDiscount*100*1000/departmentSale)/1000 AS percentOfDepartmentSale, "
				+ "ROUND(totalAfterDiscount*100*1000/totalSale)/1000 AS percentOfTotalSale,"
				+ "ROUND((qty+compQty)*100*100/(totalSaleQty+totalCompQty))/100 AS percentOfTotalQty "
				+ "FROM "
				+ "(SELECT collection, station, title, vegType, IFNULL(SUM(qty), 0) AS qty, IFNULL(SUM(compQty), 0) AS compQty, rate, ROUND(SUM(total)*100)/100 AS total, ROUND(SUM(totalAfterDiscount)*100)/100 AS totalAfterDiscount,"
				+ "(SELECT ROUND(SUM(foodBill-foodDiscount)*100)/100 FROM Payment WHERE orderDate "+dateStr+") AS departmentSale,"
				+ "(SELECT ROUND(SUM(foodBill+barBill-foodDiscount-barDiscount)*100)/100 FROM Payment WHERE orderDate "+dateStr+") AS totalSale,"
				+ "(SELECT IFNULL(SUM(qty), 0) FROM OrderItems, Orders, MenuItems WHERE orderDate " + dateStr + " AND Orders.orderId == OrderItems.orderId AND MenuItems.menuId == OrderItems.menuId AND "+depSql+") AS totalSaleQty,"
				+ "(SELECT IFNULL(SUM(quantity), 0) FROM OrderItemLog, Orders, MenuItems WHERE orderDate " + dateStr + " AND "
				+ "Orders.orderId == OrderItemLog.orderId AND OrderItemLog.state == 50 AND MenuItems.menuId == OrderItemLog.menuId AND "+depSql+") AS totalCompQty "
				+ "FROM "
				+ "(SELECT collection, station, title, vegType, MenuItems.menuId, OrderItems.qty, 0 AS compQty, OrderItems.rate, OrderItems.qty*OrderItems.rate AS total, "
				+ "(CASE "
				+ "WHEN Orders.discountCode != '[]' "
				+ "THEN "
				+ "(CASE "
				+ "WHEN (SELECT type FROM Discount WHERE name LIKE '%' || Orders.discountCode || '%') == 0 "
				+ "THEN (OrderItems.qty*OrderItems.rate) - ((SELECT foodValue FROM Discount WHERE name LIKE '%' || Orders.discountCode || '%')*OrderItems.rate/100)*OrderItems.qty "
				+ "ELSE (OrderItems.qty*OrderItems.rate) - (SELECT foodValue FROM Discount WHERE name LIKE '%' || Orders.discountCode || '%')*OrderItems.qty "
				+ "END) "
				+ "ELSE "
				+ "OrderItems.qty*OrderItems.rate "
				+ "END) AS totalAfterDiscount "
				+ "FROM Orders, OrderItems, MenuItems "
				+ "WHERE Orders.orderId == OrderItems.orderId "
				+ "AND MenuItems.menuId == OrderItems.menuId "
				+ "AND Orders.orderDate " + dateStr + " "
				+ "AND " + depSql
				+ "UNION ALL "
				+ "SELECT collection, station, title, vegType, MenuItems.menuId, 0 AS qty, OrderItemLog.quantity AS compQty, OrderItemLog.rate, OrderItemLog.quantity*OrderItemLog.rate AS total, "
				+ "0 AS totalAfterDiscount "
				+ "FROM Orders, OrderItemLog, MenuItems "
				+ "WHERE Orders.orderId == OrderItemLog.orderId "
				+ "AND MenuItems.menuId == OrderItemLog.menuId "
				+ "AND Orders.orderDate " + dateStr + " "
				+ "AND " + depSql
				+ "AND OrderItemLog.state == 50) "
				+ "GROUP BY menuId "
				+ "ORDER BY collection, station, vegType, title)";

		return db.getRecords(sql, ConsumptionReport.class, hotelId);
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
}
