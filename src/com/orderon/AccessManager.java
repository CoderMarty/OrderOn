package com.orderon;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.Database.OrderOnEntity;

/**
 * @author Marty
 *
 */
/**
 * @author Marty
 *
 */
/**
 * @author Marty
 *
 */
public class AccessManager {
	public static final int ADMIN_USER = 0;
	public static final int WAITER_USER = 1;
	public static final int ORDER_STATE_SERVICE = 0;
	public static final int ORDER_STATE_BILLING = 1;
	public static final int ORDER_STATE_OFFKDS = 2;
	public static final int ORDER_STATE_COMPLETE = 3;
	public static final int ORDER_STATE_VOIDED = 99;
	public static final int ORDER_STATE_CANCELED = 101;
	public static final int ORDER_STATE_HIDDEN = 102;
	public static final int ORDER_STATE_COMPLIMENTARY = 50;
	public static final int SUBORDER_STATE_PENDING = 0;
	public static final int SUBORDER_STATE_PROCESSING = 2;
	public static final int SUBORDER_STATE_COMPLETE = 1;
	public static final int SUBORDER_STATE_RECIEVED = 3;
	public static final int SUBORDER_STATE_RETURNED = 100;
	public static final int SUBORDER_STATE_CANCELED = 101;
	public static final int SUBORDER_STATE_VOIDED = 99;
	public static final int SUBORDER_STATE_COMPLIMENTARY = 50;
	public static final int VEG = 1;
	public static final int NONVEG = 2;
	public static final int ALCOHOLIC = 3;
	public static final int NONALCOHOLIC = 4;
	public static final int CASH_ACCOUNT = 1;
	public static final int MENUITEM_STATE_AVAILABLE = 1;
	public static final int MENUITEM_STATE_UNAVAILABLE = 0;
	public static final int AUTH_TOKEN = 0;
	public static final int PRESENT = 1;
	public static final int ABSENT = 3;
	public static final int EXCUSED = 2;
	public static final int AUTHORIZE = 1;
	public static final int UNAUTHORIZE = 0;
	public static final int INHOUSE = 1;
	public static final int HOME_DELIVERY = 0;
	public static final int TAKE_AWAY = 2;
	public static final int BAR = 3;
	public static final int NON_CHARGEABLE = 4;
	public static final int PERCENTAGE_LOYALTY_OFFER = 0;
	public static final int CASH_LOYALTY_OFFER = 1;
	public static final int PRODUCT_LOYALTY_OFFER = 2;
	public static final int ONLINE_ORDER_NEW = 0;
	public static final int ONLINE_ORDER_ACCEPTED = 1;
	public static final int ONLINE_ORDER_DECLINED = 2;
	public static final int BILLTYPE_NUMBER_REFRESH = 3;
	public static final int BILLTYPE_NUMBER = 2;
	public static final int BILLTYPE_BF = 1;
	public static final int DEPARTMENT_FOOD = 1;
	public static final int DEPARTMENT_NON_ALCOHOLIC_BEVERAGE = 2;
	public static final int DEPARTMENT_ALCOHOLIC_BEVRAGE = 3;
	public static final int RESERVATION_STATE_CANCELLED= 4;
	public static final int RESERVATION_STATE_BOOKED= 1;
	public static final int RESERVATION_STATE_WAITING= 2;
	public static final int RESERVATION_STATE_DELAYED= 3;
	public static final int RESERVATION_STATE_SEATED= 4;
	public static final int TYPE_RESERVATION= 0;
	public static final int TYPE_WAITLIST= 1;
	public static final int DISCOUNT_TYPE_FIXED = 1;
	public static final int DISCOUNT_TYPE_PERCENTAGE = 2;
	public static final int DISCOUNT_TYPE_FREEITEM = 3;
	/*
	 * KDS and POSTPAID
	 * 
	 * ORDER STATES 0,1,2,3; ORDERITEM STATES 0,2,3,1
	 * 
	 * 
	 * NON-KDS and POSTPAID
	 * 
	 * ORDER STATES 0,1,3; ORDERITEM STATES 1
	 * 
	 * KDS and PREPAID
	 * 
	 * ORDER STATES 1,2,3; ORDERITEM STATES 0,2,3,1
	 * 
	 * 
	 * NON-KDS and PREPAID
	 * 
	 * ORDER STATES 1,2,3 ORDERITEM STATES 1
	 */

	private Database db = null;

	public AccessManager(Boolean transactionBased) {
		db = new Database(transactionBased);
	}

	private String escapeString(String val) {
		return val.replaceAll("'", "''");
	}

	public void beginTransaction() {
		db.beginTransaction();
	}

	public void commitTransaction() {
		db.commitTransaction();
	}

	public void rollbackTransaction() {
		db.rollbackTransaction();
	}
	
	
	/**
	 * @param hotelId
	 * @param version
	 * 		The new verion of the API.
	 * 
	 * This method updates the database to accommodate any new changes in the latest API.
	 * Will change with every update.
	 */
	public void updateDatabase(String hotelId, String oldVersion, String version) {
		String sql = "";
		
		if(oldVersion.equals("3.1")) {
			
			this.initDatabase(hotelId);
			sql = "UPDATE MenuItems SET state = 1; Update Hotel SET version = '"+version+"';";
		}
		if(oldVersion.equals("3")) {
			
			this.initDatabase(hotelId);
			sql = "UPDATE MenuItems SET state = 1; Update Hotel SET version = '3.1';";
		}
		if(oldVersion.equals("2.12")) {
			sql = "ALTER TABLE Collections ADD COLUMN collectionOrder INTEGER; update Collections set collectionOrder = id;" + 
					"ALTER TABLE Collections ADD COLUMN hasSubCollection TEXT NOT NULL DEFAULT 'false';" +
					"ALTER TABLE Collections ADD COLUMN isActive TEXT NOT NULL DEFAULT 'true';" +
					"ALTER TABLE Collections ADD COLUMN name TEXT; update Collections set name = collection;" +
					"ALTER TABLE Collections ADD COLUMN scheduleId INTEGER;";
			sql += "CREATE TABLE Collections1 ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, name text NOT NULL, "
					+ "image TEXT DEFAULT (null), collectionOrder INTEGER NOT NULL, hasSubCollection TEXT NOT NULL, isActive TEXT NOT NULL, scheduleId INTEGER ); ";
			sql += "INSERT INTO Collections1 (hotelId, name, image, collectionOrder, hasSubCollection, isActive, scheduleId) " +
					"SELECT hotelId, collection, image, collectionOrder, hasSubCollection, isActive, scheduleId FROM Collections; " +
					"DROP TABLE Collections; " +
					"ALTER TABLE Collections1 RENAME TO Collections;" +
					"Update Hotel SET version = '3';";
		}
		if(oldVersion.equals("2.11")) {
			sql += "CREATE TABLE Charges ( sr INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, amount FLOAT NOT NULL, amount2 INTEGER, amount3 INTEGER );"
					+ " Update Hotel SET version = '2.12';";
		}
		if(oldVersion.equals("2.1")) {
			sql += "CREATE TABLE IF NOT EXISTS Reservations ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ "customerId INTEGER NOT NULL, maleCount INTEGER NOT NULL, femaleCount INTEGER NOT NULL, "
				+ "childrenCount INTEGER NOT NULL, bookingTime TEXT NOT NULL, bookingDate TEXT NOT NULL, timeStamp TEXT NOT NULL, state INTEGER NOT NULL, "
				+ "type INTEGER NOT NULL, orderId TEXT UNIQUE, hotelId TEXT NOT NULL, foreign key(customerId) references Customers(id) ); "
				+ "ALTER TABLE Customers ADD COLUMN isPriority TEEXT; update Customers set isPriority = 'false';" +
				"Update Hotel SET version = '2.11';";
		}
		if(oldVersion.equals("2") || oldVersion.equals("2.0")) {
			sql = "ALTER TABLE TotalRevenue ADD COLUMN zomatoPay BigDecimal; update TotalRevenue set zomatoPay = 0.0;" + 
				"ALTER TABLE TotalRevenue ADD COLUMN nearBy BigDecimal; update TotalRevenue set nearBy = 0.0;" +
				"Update Hotel SET version = '2.1';";
		}
		if(oldVersion.equals("1.02 rev.10023")) {
			sql = "ALTER TABLE Payment ADD COLUMN appPayment BigDecimal; update Payment set appPayment = 0.0 where appPayment isNull;" + 
				"update Payment set appPayment= cardpayment, cardpayment=0.0 where (cardType LIKE '%ZOMATO%') OR (cardType LIKE '%SWIGGY%') " +
				"OR (cardType LIKE '%DINEOUT%') OR (cardType LIKE '%PAYTM%') OR (cardType LIKE '%FOODPANDA%') OR (cardType LIKE '%UBEREATS%') " + 
				" OR (cardType LIKE '%FOODILOO%');"
				+ "update Payment set complimentary = 0.0 where complimentary isnull;"
				+ "update Payment set loyaltyAmount = 0.0 where complimentary isnull;"
				+ "Update Hotel SET version = '2.0';";
		}
		if(oldVersion.equals("1.01A rev.10023") || oldVersion.equals("")) {
			sql = "ALTER TABLE Hotel ADD COLUMN version TEXT;" + 
				"Update Hotel SET version = '1.02 rev.10023';" +
				"ALTER TABLE Orders ADD COLUMN takeAwaytype integer;" +
				"update Orders set takeAwayType = 100 WHERE inHouse = 2;" +
				"update orders set takeAwaytype = 2 WHERE customerName == 'Swiggy' OR customerName == 'SWIGGY';" +
				"update orders set takeAwaytype = 1 WHERE customerName == 'ZOMATO' OR customerName == 'Zomato';" +
				"update orders set takeAwaytype = 4 WHERE customerName == 'UBEREATS' OR customerName == 'UBER_EATS';" +
				"update orders set takeAwaytype = 3 WHERE customerName == 'FOODPANDA' OR customerName == 'FOOD_PANDA';" +
				"update orders set takeAwaytype = 5 WHERE customerName == 'FOODILOO';" +
				"update Orders set takeAwayType = 0 WHERE inHouse != 2;";
		}
		System.out.println(sql);
		db.executeUpdate(sql, true);
	}

	public void initDatabase(String hotelId) {
		String sql = "CREATE TABLE IF NOT EXISTS Users (  Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, userId text NOT NULL, "
			+ "userPasswd BLOB NOT NULL, employeeId text NOT NULL, userType integer NOT NULL, authToken text, timeStamp text, salt BLOB,"
		 	+ "UNIQUE(hotelId,userId,employeeId),"
		 	+ "FOREIGN KEY(employeeId) REFERENCES Employee(employeeId));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS TransactionHistory ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, trType TEXT NOT NULL, "
		+ "trDetail TEXT NOT NULL, amount FLOAT NOT NULL, balance FLOAT NOT NULL, trDate TEXT NOT NULL, userId TEXT, authoriser TEXT, "
		+ "employeeId TEXT NOT NULL, hotelId TEXT, paymentType TEXT, trAccountName TEXT, serviceDate TEXT);";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE SubCollections ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, "
				+ "subCollectionOrder INTEGER NOT NULL, collection TEXT NOT NULL, hotelId TEXT NOT NULL );";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE Schedules ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, days TEXT NOT NULL, " +
			"timeSlots INTEGER NOT NULL, hotelId TEXT NOT NULL );";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE Groups ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, itemIds INTEGER NOT NULL, name INTEGER NOT NULL, "
				+ "decription TEXT, max INTEGER NOT NULL, min INTEGER NOT NULL, isActive TEXT NOT NULL DEFAULT 'true' )";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS TotalRevenue ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, "
		+ "serviceDate TEXT NOT NULL, serviceType TEXT NOT NULL, cash FLOAT NOT NULL, card FLOAT NOT NULL, app FLOAT, visa FLOAT, "
		+ "mastercard FLOAT, maestro FLOAT, amex FLOAT, rupay FLOAT, others FLOAT, difference FLOAT DEFAULT (null), reason TEXT, "
		+ "total FLOAT NOT NULL, clearance TEXT, zomato FLOAT DEFAULT (null), swiggy FLOAT DEFAULT (null), dineOut FLOAT DEFAULT (null), "
		+ "mswipe FLOAT, paytm FLOAT, complimentary FLOAT, section TEXT, foodiloo FLOAT, uberEats FLOAT, foodPanda FLOAT, "
		+ "deductedCash FLOAT, cash2 FLOAT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Tables ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, tableId text NOT NULL, state integer, "
		+ "hotelId TEXT NOT NULL, section TEXT);";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Taxes ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name INTEGER NOT NULL, value DOUBLE NOT NULL, "
				+ "type TEXT NOT NULL, isActive TEXT NOT NULL DEFAULT 'true', hotelId TEXT NOT NULL )";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Flags ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, "
				+ "groupId INTEGER NOT NULL, hotelId TEXT NOT NULL )";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Tiers ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, value DOUBLE NOT NULL, "
				+ "chargeAlwaysApplicable TEXT NOT NULL DEFAULT 'true', minBillAmount DOUBLE, hotelId TEXT NOT NULL )";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Charges ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, type TEXT NOT NULL, "
				+ "value DOUBLE NOT NULL, isActive TEXT NOT NULL DEFAULT 'false', applicableOn TEXT, isAlwaysApplicable INTEGER DEFAULT 'false', "
				+ "minBillAmount DOUBLE, hasTierWiseValues TEXT, taxesOnCharge TEXT, hotelId TEXT NOT NULL )";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS StockLog ( Id INTEGER NOT NULL, sku TEXT NOT NULL DEFAULT (null), crud TEXT NOT NULL, quantity FLOAT NOT NULL, "
		+ "amount FLOAT NOT NULL, hotelId TEXT, PRIMARY KEY(Id));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Stock ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, sku TEXT NOT NULL, doc TEXT NOT NULL, doe TEXT, "
		+ "quantity INTEGER NOT NULL, hotelId text NOT NULL,"
	 	+ "FOREIGN KEY(sku) REFERENCES Material(sku));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Stations ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, station text NOT NULL,"
	 	+ "UNIQUE(hotelId,station));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Specifications ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, specification TEXT NOT NULL, category TEXT, "
		+ "type INTEGER NOT NULL);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS ServiceLog ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, serviceDate TEXT NOT NULL, "
		+ "startTimeStamp TEXT NOT NULL, endTimeStamp TEXT NOT NULL, serviceType TEXT NOT NULL, isCurrent INTEGER NOT NULL, cashInHand INTEGER, smsEmailSent TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS ServerLog ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, lastUpdateTime TEXT, hotelId TEXT, status INTEGER);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Roles ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, role TEXT, clearanceLevel INTEGER, hotelId TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS RoleAccess ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, role INTEGER, page INTEGER, read INTEGER, "
		+ "write INTEGER, deleteAccess INTEGER, hotelId INTEGER);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Recipe ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, sku TEXT NOT NULL, menuId TEXT NOT NULL, "
		+ "quantity INTEGER NOT NULL, hotelId TEXT NOT NULL, unit TEXT NOT NULL DEFAULT GRAM,"
	 	+ "FOREIGN KEY(menuId) REFERENCES MenuItems(menuId),"
	 	+ "FOREIGN KEY(sku) REFERENCES Stock(sku));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Payment ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, billNo TEXT NOT NULL DEFAULT (null), "
		+ "orderId text NOT NULL UNIQUE, orderDate DATETIME NOT NULL DEFAULT (null), foodBill FLOAT NOT NULL, barBill FLOAT NOT NULL, "
		+ "foodDiscount FLOAT DEFAULT (0), barDiscount FLOAT, total FLOAT NOT NULL, serviceCharge FLOAT DEFAULT (0), serviceTax FLOAT DEFAULT (0), "
		+ "VATFOOD FLOAT DEFAULT (0), VATBAR FLOAT DEFAULT (0), sbCess FLOAT DEFAULT (0), kkCess FLOAT DEFAULT (0), tip FLOAT DEFAULT (0), "
		+ "cashPayment FLOAT DEFAULT (0), cardPayment FLOAT DEFAULT (0), appPayment FLOAT DEFAULT (0), discountName text, cardType TEXT, gst FLOAT, loyaltyAmount FLOAT, "
		+ "complimentary FLOAT, section TEXT, billNo2 TEXT, FOREIGN KEY(orderId) REFERENCES Orders(orderId));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Orders ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, orderId text NOT NULL UNIQUE, "
		+ "orderDate text NOT NULL, customerName text, customerAddress text, customerNumber text, rating_ambiance integer, rating_qof integer, "
		+ "rating_service integer, rating_hygiene integer, waiterId text, numberOfGuests integer, state integer NOT NULL, inhouse integer NOT NULL, takeAwayType integer, "
		+ "tableId text, reviewSuggestions TEXT, serviceType TEXT NOT NULL, foodBill FLOAT DEFAULT (null), barBill FLOAT DEFAULT (null), "
		+ "billNo INTEGER, reason TEXT, authId TEXT, printCount INTEGER DEFAULT (0), discountCode TEXT, isSmsSent INTEGER, completeTimestamp TEXT, "
		+ "loyaltyId INTEGER, loyaltyPaid INTEGER, section TEXT, customerGst TEXT, reference TEXT, remarks TEXT, deliveryBoy TEXT, deliveryTimeStamp TEXT, billNo2 TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderTables ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, orderId text NOT NULL, "
		+ "tableId text NOT NULL);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderSpecifications ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, orderId TEXT NOT NULL, "
		+ "subOrderId TEXT NOT NULL, menuId TEXT NOT NULL, itemId INTEGER NOT NULL, specification TEXT NOT NULL, hotelId TEXT NOT NULL);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderItems ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, subOrderId text NOT NULL, "
		+ "subOrderDate text NOT NULL, orderId text NOT NULL, menuId text NOT NULL, qty int NOT NULL, rate real NOT NULL, specs text, state integer, "
		+ "billNo TEXT, isKotPrinted INTEGER, waiterId TEXT, billNo2 TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderItemLog ( Id INTEGER NOT NULL, hotelId TEXT NOT NULL, orderId TEXT NOT NULL DEFAULT (null), "
		+ "subOrderId TEXT NOT NULL, menuId TEXT NOT NULL, state INTEGER NOT NULL, reason TEXT, dateTime TEXT, quantity INTEGER, rate INTEGER, "
		+ "itemId INTEGER, subOrderDate TEXT,"
	 	+ "PRIMARY KEY(Id));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderAddOns ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, orderId TEXT NOT NULL, "
		+ "subOrderId INTEGER NOT NULL, qty INTEGER NOT NULL, menuId TEXT NOT NULL, addOnId INTEGER NOT NULL, rate INTEGER NOT NULL, itemId INTEGER, "
		+ "state TEXT, subOrderDate TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderAddOnLog ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, orderId TEXT NOT NULL, "
		+ "subOrderId TEXT NOT NULL, menuId TEXT NOT NULL, itemId INTEGER NOT NULL, quantity INTEGER NOT NULL, rate INTEGER NOT NULL, state TEXT, "
		+ "addOnId INTEGER, subOrderDate TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OnlineOrders ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT, restaurantId INTEGER, "
		+ "orderId TEXT, externalOrderId INTEGER, data TEXT, status INTEGER, dateTime INTEGER, portalId INTEGER);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Notification ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, orderId text NOT NULL, "
		+ "notId int NOT NULL, msg text NOT NULL);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS MenuItems ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, station TEXT NOT NULL, "
		+ "menuId text NOT NULL UNIQUE, title text NOT NULL, description text, category text NOT NULL, flags text, preparationTime integer, "
		+ "rate FLOAT NOT NULL, costPrice FLOAT DEFAULT Null, inhouseRate FLOAT NOT NULL DEFAULT 0, onlineRate FLOAT NOT NULL DEFAULT 0, vegType int NOT NULL, method TEXT, "
		+ "state INTEGER NOT NULL, shortForm TEXT, addOns TEXT, img text, hasIncentive INTEGER, incentive INTEGER, isTaxable INTEGER NOT NULL);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Material ( sku TEXT NOT NULL PRIMARY KEY UNIQUE, name TEXT NOT NULL, unit TEXT NOT NULL, "
		+ "ratePerUnit FLOAT NOT NULL DEFAULT (null), wastage INTEGER, minQuantity INTEGER NOT NULL DEFAULT (null), hotelId TEXT, "
		+ "displayableUnit TEXT NOT NULL DEFAULT GRAM);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS LoyaltySettings ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, userType TEXT NOT NULL UNIQUE, "
		+ "requiredPoints INTEGER NOT NULL, pointToRupee INTEGER NOT NULL, hotelId TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS LoyaltyOffers ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL UNIQUE, "
		+ "offerType INTEGER NOT NULL, points INTEGER NOT NULL, offerValue TEXT NOT NULL, usageLimit INTEGER, userType TEXT, validCollections TEXT, "
		+ "status TEXT NOT NULL, startDate TEXT, expiryDate TEXT, hotelId TEXT NOT NULL, chainId TEXT, offerQuantity INTEGER, minBill INTEGER, "
		+ "hasUsageLimit TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS LabourLog ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, salary INTEGER NOT NULL DEFAULT (null), "
		+ "employeeId TEXT NOT NULL, date TEXT NOT NULL, salaryMonth TEXT NOT NULL, bonus INTEGER, hotelId TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS 'Hotel' ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, hotelCode text NOT NULL, "
				+ "hotelName text NOT NULL, isEnabled int NOT NULL, hotelAddress TEXT, hotelContact TEXT, taxFlags TEXT, GSTNumber TEXT DEFAULT (null), "
				+ "hotelType TEXT, description TEXT, website TEXT, smsEnabled INTEGER, serverEnabled INTEGER, hasCashDrawer INTEGER, hasLoyalty INTEGER, "
				+ "hasIncentiveScheme INTEGER, billType INTEGER, printMethod TEXT, sections TEXT, integrations TEXT, onlinePlatforms TEXT, "
				+ "kotIHTBNSZSUF TEXT, kotSettings TEXT, hasKds TEXT, hasKot TEXT, hasDirectCheckout TEXT, hasNC TEXT, hasBar TEXT, loadCustomerDb TEXT, "
				+ "isMenuIdCategorySpecific TEXT, allowItemCancellationOnPhone TEXT, kotFontFamily TEXT, kotFontSize TEXT, kotFontWeight TEXT, hasEod TEXT "
				+ ", version TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Expenses ( id INTEGER NOT NULL DEFAULT (0) PRIMARY KEY AUTOINCREMENT UNIQUE, type TEXT NOT NULL, "
		+ "serviceDate TEXT NOT NULL DEFAULT (CURRENT_TIMESTAMP), amount INTEGER NOT NULL DEFAULT (0), userId TEXT NOT NULL, payee TEXT, "
		+ "memo TEXT, chequeNo INTEGER, accountName TEXT DEFAULT (null), paymentType TEXT DEFAULT (null), hotelId TEXT DEFAULT (null), "
		+ "serviceType TEXT, section TEXT, employeeId TEXT, invoiceNumber INTEGER);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Employee ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, employeeId text NOT NULL, "
		+ "firstName text NOT NULL, surName text NOT NULL, address text NOT NULL DEFAULT (null), contactNumber text DEFAULT (null), dob text, sex text, "
		+ "hiringDate text, designation text DEFAULT (null), department text, salary int, bonus int, image TEXT DEFAULT (Null), middleName TEXT, "
		+ "email TEXT, accountBalance INTEGER);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Discount ( name text NOT NULL UNIQUE, hotelId text NOT NULL, description text NOT NULL, type text NOT NULL, "
		+ "value integer NOT NULL, startDate text NOT NULL, expiryDate text, usageLimit text NOT NULL, validCollections TEXT,"
	 	+ "PRIMARY KEY(name));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Customers ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, mobileNo text NOT NULL UNIQUE, "
		+ "customer text NOT NULL, address text NOT NULL, birthdate TEXT, anniversary TEXT, userType TEXT, remarks TEXT, allergyInfo TEXT, points INTEGER, "
		+ "wantsPromotion TEXT, visitCount TEXT, dateOfLastVisit TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Collections ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, collection text NOT NULL, "
				+ "image TEXT DEFAULT (null), collectionOrder INTEGER NOT NULL, hasSubCollection TEXT NOT NULL, isActive TEXT NOT NULL );";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Bank ( accountNumber INTEGER NOT NULL UNIQUE, bankName TEXT, accountName TEXT NOT NULL UNIQUE, "
		+ "balance INTEGER NOT NULL DEFAULT 0, hotelId TEXT, section TEXT,"
	 	+ "PRIMARY KEY(accountNumber));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Attendance ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, employeeId text NOT NULL, "
		+ "checkInTime text NOT NULL, checkOutTime text, checkInDate text NOT NULL, checkOutDate text, reason text, authorisation INTEGER, isPresent INTEGER, shift INTEGER,"
	 	+ "FOREIGN KEY(employeeId) REFERENCES Employee(employeeId));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS AddOns ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, inhouseRate INTEGER, onlineRate INTEGER, "
		+ "deliveryRate INTEGER, hotelId TEXT);";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Reservations ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ "customerId INTEGER NOT NULL, maleCount INTEGER NOT NULL, femaleCount INTEGER NOT NULL, "
				+ "childrenCount INTEGER NOT NULL, bookingTime TEXT NOT NULL, bookingDate TEXT NOT NULL, timeStamp TEXT NOT NULL, state INTEGER NOT NULL, "
				+ "type INTEGER NOT NULL, orderId TEXT UNIQUE, hotelId TEXT NOT NULL, foreign key(customerId) references Customers(id) )";
		db.executeUpdate(sql, hotelId, false);
		// Create all other tables here...
	}
	
	public void restaurantSetup(String hotelId) {
		
		Hotel hotel = this.getHotelById(hotelId);
		
		String sql = "INSERT INTO BANK (accountNumber, bankName, accountName, balance, hotelId) VALUES "
			+ "(1, 'CASH', 'CASH_DRAWER', 0, '"+hotelId+"')";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "INSERT INTO Employee "
			+ "(hotelId, employeeId, firstName, surName, address, contactNumber, dob, sex, hiringDate, designation, department"
			+ ", salary, bonus, image, middleName, email) VALUES('" + hotelId 
			+ "', '"+hotel.getHotelCode()+"01', 'Martin', 'Fernandes', ' ', '9867334779', '08/08/1992', 'Male', "
			+ "'01/01/2018', 'ADMINISTRATOR', 'BACKOFFICE', 0,0,'','','');";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "INSERT INTO LoyaltySettings (hotelId, userType, requiredPoints, pointToRupee) VALUES ('" 
			+ hotelId + "', 'Prime', 0, 10), ('" + hotelId + "', 'Premium', 20000, 10), ('" + hotelId + "', 'Elite', 40000, 10), ('"
			+ hotelId + "', 'All', 0, 0);";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "INSERT INTO SERVERLOG (hotelId, status) VALUES "
				+ "('"+hotelId+"', 0)";
		db.executeUpdate(sql, hotelId, false);

		sql = "INSERT INTO STATIONS (hotelId, station) VALUES "
				+ "('"+hotelId+"', 'Kitchen'), "
				+ "('"+hotelId+"', 'Bar Non-Alcoholic'), "
				+ "('"+hotelId+"', 'Beverage');";
		db.executeUpdate(sql, hotelId, false);

		this.addUser(hotelId, "Admin", hotel.getHotelCode()+"01", 2, "9867334");
	}

	public static class Recipe implements Database.OrderOnEntity {

		public String sku;
		public String menuId;
		public int quantity;
		public String hotelId;
		public String unit;

		public String getSku() {
			return sku;
		}

		public String getMenuId() {
			return menuId;
		}

		public int getQuantity() {
			return quantity;
		}

		public String getHotelId() {
			return hotelId;
		}

		public String getUnit() {
			return unit;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.sku = Database.readRsString(rs, "sku");
			this.menuId = Database.readRsString(rs, "menuId");
			this.quantity = Database.readRsInt(rs, "quantity");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.unit = Database.readRsString(rs, "unit");
		}
	}

	public static class Stock implements Database.OrderOnEntity {

		public String sku;
		public String name;
		public String unit;
		public String displayableUnit;
		public BigDecimal ratePerUnit;
		public BigDecimal wastage;
		public BigDecimal minQuantity;
		public String hotelId;
		public BigDecimal quantity;
		public String doc;
		public String doe;
		public String method;

		public String getSku() {
			return sku;
		}

		public String getName() {
			return name;
		}

		public String getUnit() {
			return unit;
		}

		public String getDisplayableUnit() {
			return displayableUnit;
		}

		public BigDecimal getRatePerUnit() {
			return ratePerUnit;
		}

		public BigDecimal getWastage() {
			return wastage;
		}

		public BigDecimal getMinQuantity() {
			return minQuantity;
		}

		public String getHotelId() {
			return hotelId;
		}

		public BigDecimal getQuantity() {
			return quantity;
		}

		public String getDOC() {
			return doc;
		}

		public String getDOE() {
			return doe;
		}

		public String getMethod() {
			return method;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.name = Database.readRsString(rs, "name");
			this.sku = Database.readRsString(rs, "sku");
			this.unit = Database.readRsString(rs, "unit");
			this.displayableUnit = Database.readRsString(rs, "displayableUnit");
			this.ratePerUnit = Database.readRsBigDecimal(rs, "ratePerUnit");
			this.wastage = Database.readRsBigDecimal(rs, "wastage");
			this.minQuantity = Database.readRsBigDecimal(rs, "minQuantity");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.quantity = Database.readRsBigDecimal(rs, "quantity");
			this.doc = Database.readRsString(rs, "doc");
			this.doe = Database.readRsString(rs, "doe");
			this.method = Database.readRsString(rs, "method");
		}
	}

	public static class Notification implements Database.OrderOnEntity {
		private int mNotId;
		private String mHotelId;
		private String mOrderId;
		private String msg;

		public int getNotificationId() {
			return mNotId;
		}

		public String getHotelId() {
			return mHotelId;
		}

		public String getOrderId() {
			return mOrderId;
		}

		public String getMsg() {
			return msg;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.mHotelId = Database.readRsString(rs, "hotelId");
			this.mOrderId = Database.readRsString(rs, "orderId");
			this.msg = Database.readRsString(rs, "msg");
			this.mNotId = Database.readRsInt(rs, "notId");
		}
	}

	public static class User implements Database.OrderOnEntity {
		
		public byte[] getPasswd() {
			return mPasswd;
		}

		public String getHotelId() {
			return mHotelId;
		}

		public String getUserId() {
			return mUserId;
		}

		public String getName() {
			return name;
		}

		public String getEmployeeId() {
			return mEmployeeId;
		}

		public Integer getUserType() {
			return mUserType;
		}

		public String getAuthToken() {
			return mAuthToken;
		}

		public String getTimeStamp() {
			return timeStamp;
		}
		public byte[] getSalt() {
			return salt;
		}

		private String mHotelId;
		private byte[] mPasswd;
		private String mUserId;
		private String mEmployeeId;
		private Integer mUserType;
		private String mAuthToken;
		private String timeStamp;
		private byte[] salt;
		private String name;

		@Override
		public void readFromDB(ResultSet rs) {
			this.mHotelId = Database.readRsString(rs, "hotelId");
			this.mUserId = Database.readRsString(rs, "userId");
			this.mPasswd = Database.readRsBytes(rs, "userPasswd");
			this.mEmployeeId = Database.readRsString(rs, "employeeId");
			this.mUserType = Database.readRsInt(rs, "userType");
			this.mAuthToken = Database.readRsString(rs, "authToken");
			this.timeStamp = Database.readRsString(rs, "timeStamp");
			this.salt = Database.readRsBytes(rs, "salt");
			this.name = Database.readRsString(rs, "name");
		}
	}

	public static class EntityId implements Database.OrderOnEntity {
		public Integer getId() {
			return mId;
		}

		private Integer mId;

		@Override
		public void readFromDB(ResultSet rs) {
			this.mId = Database.readRsInt(rs, "entityId");
		}
	}

	public static class EntityBigDecimal implements Database.OrderOnEntity {
		public BigDecimal getId() {
			return mId;
		}

		private BigDecimal mId;

		@Override
		public void readFromDB(ResultSet rs) {
			this.mId = Database.readRsBigDecimal(rs, "entityId");
		}
	}
	
	public static class KitchenStation implements Database.OrderOnEntity {
		public String getStation() {
			return station;
		}

		private String station;

		@Override
		public void readFromDB(ResultSet rs) {
			this.station = Database.readRsString(rs, "station");
		}
	}

	public static class Hotel implements Database.OrderOnEntity {
		public String getHotelName() {
			return mHotelName;
		}
		public String getHotelId() {
			return mHotelId;
		}
		public Integer getIsEnabled() {
			return mIsEnabled;
		}
		public String getHotelCode() {
			return mHotelCode;
		}
		public String getHotelAddress() {
			return hotelAddress;
		}
		public String getHotelContact() {
			return hotelContact;
		}
		public String getFlags() {
			return flags;
		}
		public String getGstNumber() {
			return gstNumber;
		}
		public String getmHotelName() {
			return mHotelName;
		}
		public String getmHotelId() {
			return mHotelId;
		}
		public Integer getmIsEnabled() {
			return mIsEnabled;
		}
		public String getmHotelCode() {
			return mHotelCode;
		}
		public String[] getKotPrintCount() {
			return kotPrintCount;
		}
		public String[] getKotSettings() {
			return kotSettings;
		}
		public String getHotelType() {
			return hotelType;
		}
		public boolean getHasKds() {
			return Boolean.valueOf(hasKds);
		}
		public boolean getHasKot() {
			return Boolean.valueOf(hasKot);
		}
		public boolean getHasDirectCheckout() {
			return Boolean.valueOf(hasDirectCheckout);
		}
		public boolean getHasEod() {
			return Boolean.valueOf(hasEod);
		}
		public boolean getHasNC() {
			return Boolean.valueOf(hasNC);
		}
		public boolean getHasBar() {
			return Boolean.valueOf(hasBar);
		}
		public boolean getLoadCustomerDb() {
			return Boolean.valueOf(loadCustomerDb);
		}
		public boolean getIsMenuIcCategorySpecific() {
			return Boolean.valueOf(isMenuIcCategorySpecific);
		}
		public boolean getAllowItemCancellationOnPhone() {
			return Boolean.valueOf(allowItemCancellationOnPhone);
		}
		public String getDescription() {
			return description;
		}
		public String getWebsite() {
			return website;
		}
		public int getIsSmsEnabled() {
			return isSmsEnabled;
		}
		public boolean getHasSms() {
			return isSmsEnabled==1?true:false;
		}
		public int getIsServerEnabled() {
			return isServerEnabled;
		}
		public boolean getHasServer() {
			return isServerEnabled==1?true:false;
		}
		public String getServerUpdateTime() {
			return serverUpdateTime;
		}
		public boolean getHasCashDrawer() {
			return hasCashDrawer==0?false:true;
		}
		public int getHasLoyalty() {
			return hasLoyalty;
		}
		public int getHasIncentiveScheme() {
			return hasIncentiveScheme;
		}
		public int getBillType() {
			return billType;
		}
		public String getPrintMethod() {
			return printMethod;
		}
		public String[] getIntegrations() {
			return integrations.split(";");
		}
		public String[] getOnlinePlatforms() {
			return onlinePlatforms.split(";");
		}
		public String[] getSections() {
			return sections.split(";");
		}
		public String getSection() {
			return sections;
		}
		public boolean hasSection() {
			return sections.length()==0?false:true;
		}
		public int getKOTCountInhouse() {
			return Integer.parseInt(kotPrintCount[0]);
		}
		public int getKOTCountHomeDelivery() {
			return Integer.parseInt(kotPrintCount[1]);
		}
		public int getKOTCountTakeAway() {
			return Integer.parseInt(kotPrintCount[2]);
		}
		public int getKOTCountBar() {
			return Integer.parseInt(kotPrintCount[3]);
		}
		public int getKOTCountNC() {
			return Integer.parseInt(kotPrintCount[4]);
		}
		public int getKOTCountSummary() {
			return Integer.parseInt(kotPrintCount[5]);
		}
		public int getKOTCountZomato() {
			return Integer.parseInt(kotPrintCount[6]);
		}
		public int getKOTCountSwwigy() {
			return Integer.parseInt(kotPrintCount[7]);
		}
		public int getKOTCountUberEats() {
			return Integer.parseInt(kotPrintCount[8]);
		}
		public int getKOTCountFoodPanda() {
			return Integer.parseInt(kotPrintCount[9]);
		}
		public int getKOTHeight() {
			return Integer.parseInt(kotSettings[0]);
		}
		public int getKOTWidth() {
			return Integer.parseInt(kotSettings[1]);
		}
		public Double getKOTDivisor() {
			return Double.parseDouble(kotSettings[2]);
		}
		public String getKotFontFamily() {
			return kotFontFamily;
		}
		public String getKotFontSize() {
			return kotFontSize;
		}
		public String getKotFontWeight() {
			return kotFontWeight;
		}
		public String getVersion() {
			return version;
		}

		private String mHotelName;
		private String mHotelId;
		private Integer mIsEnabled;
		private String mHotelCode;
		private String hotelAddress;
		private String hotelContact;
		private String flags;
		private String gstNumber;
		private String description;
		private String website;
		private int isSmsEnabled;
		private int isServerEnabled;
		private int hasCashDrawer;
		private int hasLoyalty;
		private int hasIncentiveScheme;
		private String serverUpdateTime;
		private int billType;
		private String printMethod;
		private String integrations;
		private String onlinePlatforms;
		private String sections;
		private String[] kotPrintCount;
		private String[] kotSettings;
		private String hotelType;
		private String hasKds;
		private String hasKot;
		private String hasDirectCheckout;
		private String hasNC;
		private String hasBar;
		private String loadCustomerDb;
		private String isMenuIcCategorySpecific;
		private String allowItemCancellationOnPhone;
		private String kotFontFamily;
		private String kotFontSize;
		private String kotFontWeight;
		private String hasEod;
		private String version;

		@Override
		public void readFromDB(ResultSet rs) {
			this.mHotelName = Database.readRsString(rs, "hotelName");
			this.mHotelId = Database.readRsString(rs, "hotelId");
			this.mIsEnabled = Database.readRsInt(rs, "isEnabled");
			this.mHotelCode = Database.readRsString(rs, "hotelCode");
			this.hotelAddress = Database.readRsString(rs, "hotelAddress");
			this.hotelContact = Database.readRsString(rs, "hotelContact");
			this.flags = Database.readRsString(rs, "taxFlags");
			this.hotelType = Database.readRsString(rs, "hotelType");
			this.website = Database.readRsString(rs, "website");
			this.description = Database.readRsString(rs, "description");
			this.gstNumber = Database.readRsString(rs, "GSTNumber");
			this.isSmsEnabled = Database.readRsInt(rs, "smsEnabled");
			this.isServerEnabled = Database.readRsInt(rs, "serverEnabled");
			this.hasCashDrawer = Database.readRsInt(rs, "hasCashDrawer");
			this.hasLoyalty = Database.readRsInt(rs, "hasLoyalty");
			this.hasIncentiveScheme = Database.readRsInt(rs, "hasIncentiveScheme");
			this.billType = Database.readRsInt(rs, "billType");
			this.printMethod = Database.readRsString(rs, "printMethod");
			this.integrations = Database.readRsString(rs, "integrations");
			this.onlinePlatforms = Database.readRsString(rs, "onlinePlatforms");
			this.sections = Database.readRsString(rs, "sections");
			this.kotPrintCount = Database.readRsString(rs, "kotIHTBNSZSUF").split(":");
			this.kotSettings = Database.readRsString(rs, "kotSettings").split(":");
			this.hasKds = Database.readRsString(rs, "hasKds");
			this.hasKot = Database.readRsString(rs, "hasKot");
			this.hasDirectCheckout = Database.readRsString(rs, "hasDirectCheckout");
			this.hasEod = Database.readRsString(rs, "hasEod");
			this.hasNC = Database.readRsString(rs, "hasNC");
			this.hasBar = Database.readRsString(rs, "hasBar");
			this.loadCustomerDb = Database.readRsString(rs, "loadCustomerDb");
			this.isMenuIcCategorySpecific = Database.readRsString(rs, "isMenuIdCategorySpecific");
			this.allowItemCancellationOnPhone = Database.readRsString(rs, "allowItemCancellationOnPhone");
			this.kotFontFamily = Database.readRsString(rs, "kotFontFamily");
			this.kotFontSize = Database.readRsString(rs, "kotFontSize");
			this.kotFontWeight = Database.readRsString(rs, "kotFontWeight");
			this.version = Database.readRsString(rs, "version");
		}
	}

	public static class Table implements Database.OrderOnEntity {
		public String getTableId() {
			return mTableId;
		}

		public String getOrderId() {
			return orderId;
		}

		public String getWaiterId() {
			return waiterId;
		}

		private String mTableId;
		private String orderId;
		private String waiterId;

		@Override
		public void readFromDB(ResultSet rs) {
			this.mTableId = Database.readRsString(rs, "tableId");
			this.orderId = Database.readRsString(rs, "orderId");
			this.waiterId = Database.readRsString(rs, "waiterId");
		}
	}

	public static class EntityString implements Database.OrderOnEntity {
		public String getEntity() {
			return entity;
		}

		private String entity;

		@Override
		public void readFromDB(ResultSet rs) {
			this.entity = Database.readRsString(rs, "entityId");
		}
	}
	
	public static class Customer implements Database.OrderOnEntity {
		public int getId() {
			return id;
		}
		
		public String getCustomer() {
			return mCustomer;
		}

		public String getAddress() {
			return mAddress;
		}

		public String getMobileNo() {
			return mMobileNo;
		}

		public String getBirthdate() {
			return mBirthdate;
		}

		public String getAnniversary() {
			return mAnniversary;
		}

		public String getUserType() {
			return userType;
		}

		public String getRemarks() {
			return remarks;
		}

		public int getIsSmsSent() {
			return isSmsSent;
		}

		public String getCompleteTimestamp() {
			return completeTimestamp;
		}

		public String getOrderId() {
			return orderId;
		}

		public String getAllergyInfo() {
			return allergyInfo;
		}

		public int getPoints() {
			return points;
		}

		public int getVisitCount() {
			return visitCount;
		}

		public Boolean getWantsPromotion() {
			return Boolean.valueOf(wantsPromotion);
		}

		private int id;
		private String mCustomer;
		private String mMobileNo;
		private String mAddress;
		private String mBirthdate;
		private String mAnniversary;
		private String userType;
		private int isSmsSent;
		private String remarks;
		private String completeTimestamp;
		private String orderId;
		private String allergyInfo;
		private int points;
		private String wantsPromotion;
		private int visitCount;

		@Override
		public void readFromDB(ResultSet rs) {
			this.id = Database.readRsInt(rs, "id");
			this.mCustomer = Database.readRsString(rs, "customer");
			this.mMobileNo = Database.readRsString(rs, "mobileNo");
			this.mAddress = Database.readRsString(rs, "address");
			this.mBirthdate = Database.readRsString(rs, "birthdate");
			this.mAnniversary = Database.readRsString(rs, "anniversary");
			this.userType = Database.readRsString(rs, "userType");
			this.remarks = Database.readRsString(rs, "remarks");
			this.isSmsSent = Database.readRsInt(rs, "isSmsSent");
			this.completeTimestamp = Database.readRsString(rs, "completeTimestamp");
			this.orderId = Database.readRsString(rs, "orderId");
			this.allergyInfo = Database.readRsString(rs, "allergyInfo");
			this.points = Database.readRsInt(rs, "points");
			this.wantsPromotion = Database.readRsString(rs, "wantsPromotion");
			this.visitCount = Database.readRsInt(rs, "visitCount");
		}
	}

	public static class CustomerReport implements Database.OrderOnEntity {

		public String getCustomerName() {
			return customerName;
		}

		public String getMobileNumber() {
			return mobileNumber;
		}

		public BigDecimal getSpentPerPax() {
			return spentPerPax;
		}

		public BigDecimal getSpentPerWalkin() {
			return spentPerWalkin;
		}

		public BigDecimal getTotalSpent() {
			return totalSpent;
		}

		public int getTotalGuests() {
			return totalGuests;
		}

		public int getTotalWalkins() {
			return totalWalkins;
		}

		public String getSuggestions() {
			return suggestions;
		}

		public int getRatingAmbiane() {
			return ratingAmbiane;
		}

		public int getRatingService() {
			return ratingService;
		}

		public int getRatingFood() {
			return ratingFood;
		}

		public int getRatingHygiene() {
			return ratingHygiene;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public String getBillNo() {
			return billNo;
		}

		private String customerName;
		private String mobileNumber;
		private BigDecimal spentPerPax;
		private BigDecimal spentPerWalkin;
		private BigDecimal totalSpent;
		private int totalGuests;
		private int totalWalkins;
		private String suggestions;
		private int ratingAmbiane;
		private int ratingService;
		private int ratingFood;
		private int ratingHygiene;
		private BigDecimal total;
		private String billNo;

		@Override
		public void readFromDB(ResultSet rs) {
			this.customerName = Database.readRsString(rs, "customerName");
			this.mobileNumber = Database.readRsString(rs, "mobileNo");
			this.spentPerPax = Database.readRsBigDecimal(rs, "spentPerPax");
			this.spentPerWalkin = Database.readRsBigDecimal(rs, "spentPerWalkin");
			this.totalSpent = Database.readRsBigDecimal(rs, "totalSpent");
			this.totalGuests = Database.readRsInt(rs, "totalGuests");
			this.totalWalkins = Database.readRsInt(rs, "totalWalkins");
			this.suggestions = Database.readRsString(rs, "reviewSuggestions");
			this.ratingAmbiane = Database.readRsInt(rs, "rating_ambiance");
			this.ratingFood = Database.readRsInt(rs, "rating_qof");
			this.ratingHygiene = Database.readRsInt(rs, "rating_hygiene");
			this.ratingService = Database.readRsInt(rs, "rating_service");
			this.total = Database.readRsBigDecimal(rs, "total");
			this.billNo = Database.readRsString(rs, "billNo");
		}
	}

	public static class HomeDelivery implements Database.OrderOnEntity {
		public String getCustomer() {
			return mCustomer;
		}

		public String getAddress() {
			return mAddress;
		}

		public String getMobileNo() {
			return mMobileNo;
		}

		public String getOrderId() {
			return mOrderId;
		}

		public Integer getState() {
			return mState;
		}

		public BigDecimal getTotal() {
			return mTotal;
		}

		public String getBillNo() {
			return mBillNo;
		}

		public String getReference() {
			return reference;
		}

		public String getRemarks() {
			return remarks;
		}

		public int getTakeAwayType() {
			return takeAwayType;
		}

		private String mCustomer;
		private String mMobileNo;
		private String mAddress;
		private String mOrderId;
		private Integer mState;
		private BigDecimal mTotal;
		private String mBillNo;
		private String reference;
		private String remarks;
		private int takeAwayType;

		@Override
		public void readFromDB(ResultSet rs) {
			this.mCustomer = Database.readRsString(rs, "customer");
			this.mMobileNo = Database.readRsString(rs, "mobileNo");
			this.mAddress = Database.readRsString(rs, "address");
			this.mState = Database.readRsInt(rs, "state");
			this.mOrderId = Database.readRsString(rs, "orderId");
			this.mTotal = Database.readRsBigDecimal(rs, "total");
			this.mBillNo = Database.readRsString(rs, "billNo");
			this.reference = Database.readRsString(rs, "reference");
			this.remarks = Database.readRsString(rs, "remarks");
			this.takeAwayType = Database.readRsInt(rs, "takeAwayType");
		}
	}
	
	public static class OnlineOrder implements Database.OrderOnEntity{

		private String hotelId;
		private int restaurantId;
		private int portalId;
		private String orderId;
		private int externalOrderId;
		private String data;
		private int status;
		private String dateTime;
		
		public String getHotelId() {
			return hotelId;
		}
		public int getRestaurantId() {
			return restaurantId;
		}
		public String getOrderId() {
			return orderId;
		}
		public int getPortalId() {
			return portalId;
		}
		public int getExternalOrderId() {
			return externalOrderId;
		}
		public JSONObject getData() {
			try {
				return new JSONObject(data);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONObject();
		}
		public int getStatus() {
			return status;
		}
		public String getDateTime() {
			return dateTime;
		}
		
		@Override
		public void readFromDB(ResultSet rs) {
			this.data = Database.readRsString(rs, "data");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.portalId = Database.readRsInt(rs, "portalId");
			this.restaurantId = Database.readRsInt(rs, "restaurantId");
			this.externalOrderId = Database.readRsInt(rs, "externalOrderId");
			this.orderId = Database.readRsString(rs, "orderId");
			this.status = Database.readRsInt(rs, "status");
			this.dateTime = Database.readRsString(rs, "dateTime");
		}
	}

	public static class Order implements Database.OrderOnEntity {
		
		public String getOrderId() {
			return orderId;
		}

		public int getOrderNumber() {
			return orderNumber;
		}

		public Date getOrderDate() {
			return orderDate;
		}

		public String getCustomerName() {
			return customerName;
		}

		public String getCustomerAddress() {
			return customerAddress;
		}

		public String getCustomerNumber() {
			return customerNumber;
		}

		public String getCustomerGst() {
			return customerGst;
		}

		public Integer getAmbianceRating() {
			return rating_ambiance;
		}

		public Integer getQoFRating() {
			return rating_qof;
		}

		public Integer getServiceRating() {
			return rating_service;
		}

		public Integer getHygieneRating() {
			return rating_hygiene;
		}

		public String getWaiterId() {
			return waiterId;
		}

		public Integer getNumberOfGuests() {
			return numberOfGuests;
		}

		public Integer getState() {
			return state;
		}

		public Integer getInHouse() {
			return inHouse;
		}

		public Integer getTakeAwayType() {
			return takeAwayType;
		}

		public String getTableId() {
			return tableId;
		}

		public String getServiceType() {
			return serviceType;
		}

		public BigDecimal getFoodBill() {
			return foodBill;
		}

		public BigDecimal getBarBill() {
			return barBill;
		}

		public Integer getRating_ambiance() {
			return rating_ambiance;
		}

		public Integer getRating_qof() {
			return rating_qof;
		}

		public Integer getRating_service() {
			return rating_service;
		}

		public Integer getRating_hygiene() {
			return rating_hygiene;
		}

		public BigDecimal getFoodDiscount() {
			return foodDiscount;
		}

		public BigDecimal getBarDiscount() {
			return barDiscount;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public String getBillNo() {
			return billNo;
		}

		public String getBillNo2() {
			return billNo2;
		}

		public String getReason() {
			return reason;
		}

		public String getAuthId() {
			return authId;
		}

		public Integer getPrintCount() {
			return printCount;
		}

		public String getDiscountCode() {
			return discountCode;
		}

		public Integer getLoyaltyId() {
			return loyaltyId;
		}

		public Integer getLoyaltyPaid() {
			return loyaltyPaid;
		}

		public String getReviewSuggestions() {
			return reviewSuggestions;
		}
		
		public boolean hasTakenReview() {
			if(rating_ambiance == 0 && rating_ambiance == 0 && rating_ambiance == 0 && rating_ambiance == 0)
				return false;
			else
				return true;
		}

		public String getSection() {
			return section;
		}

		public String getReference() {
			return reference;
		}

		public String getDeliveryBoy() {
			return deliveryBoy;
		}

		public String getDeliveryTime() {
			return deliveryTime;
		}

		public String getRemarks() {
			return remarks;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getPaymentType() {
			return paymentType;
		}

		public BigDecimal getTotalPayment() {
			return totalPayment;
		}

		public BigDecimal getCashPayment() {
			return cashPayment;
		}

		public BigDecimal getCardPayment() {
			return cardPayment;
		}

		public BigDecimal getAppPayment() {
			return appPayment;
		}

		private String orderId;
		private int orderNumber;
		private Date orderDate;
		private String customerName;
		private String customerAddress;
		private String customerNumber;
		private String customerGst;
		private Integer rating_ambiance;
		private Integer rating_qof;
		private Integer rating_service;
		private Integer rating_hygiene;
		private String waiterId;
		private Integer numberOfGuests;
		private Integer state;
		private Integer inHouse;
		private int takeAwayType;
		private String tableId;
		private String serviceType;
		private BigDecimal foodBill;
		private BigDecimal barBill;
		private BigDecimal foodDiscount;
		private BigDecimal barDiscount;
		private BigDecimal total;
		private String billNo;
		private String billNo2;
		private String reason;
		private String authId;
		private Integer printCount;
		private String discountCode;
		private Integer loyaltyId;
		private Integer loyaltyPaid;
		private String reviewSuggestions;
		private String section;
		private String reference;
		private String deliveryBoy;
		private String deliveryTime;
		private String remarks;
		private String firstName;
		private String paymentType;
		private BigDecimal totalPayment;
		private BigDecimal cashPayment;
		private BigDecimal cardPayment;
		private BigDecimal appPayment;

		@Override
		public void readFromDB(ResultSet rs) {
			this.orderId = Database.readRsString(rs, "orderId");
			this.orderNumber = Database.readRsInt(rs, "Id");
			this.orderDate = Database.readRsDate(rs, "orderDate");
			this.customerName = Database.readRsString(rs, "customerName");
			this.customerAddress = Database.readRsString(rs, "customerAddress");
			this.customerNumber = Database.readRsString(rs, "customerNumber");
			this.customerGst = Database.readRsString(rs, "customerGst");
			this.rating_ambiance = Database.readRsInt(rs, "rating_ambiance");
			this.rating_qof = Database.readRsInt(rs, "rating_qof");
			this.rating_service = Database.readRsInt(rs, "rating_service");
			this.rating_hygiene = Database.readRsInt(rs, "rating_hygiene");
			this.waiterId = Database.readRsString(rs, "waiterId");
			this.numberOfGuests = Database.readRsInt(rs, "numberOfGuests");
			this.state = Database.readRsInt(rs, "state");
			this.inHouse = Database.readRsInt(rs, "inhouse");
			this.takeAwayType = Database.readRsInt(rs, "takeAwayType");
			this.tableId = Database.readRsString(rs, "tableId");
			this.serviceType = Database.readRsString(rs, "serviceType");
			this.foodBill = Database.readRsBigDecimal(rs, "foodBill");
			this.barBill = Database.readRsBigDecimal(rs, "barBill");
			this.foodDiscount = Database.readRsBigDecimal(rs, "foodDiscount");
			this.barDiscount = Database.readRsBigDecimal(rs, "barDiscount");
			this.total = Database.readRsBigDecimal(rs, "total");
			this.billNo = Database.readRsString(rs, "billNo");
			this.billNo2 = Database.readRsString(rs, "billNo2");
			this.reason = Database.readRsString(rs, "reason");
			this.authId = Database.readRsString(rs, "authId");
			this.printCount = Database.readRsInt(rs, "printCount");
			this.discountCode = Database.readRsString(rs, "discountCode");
			this.loyaltyId = Database.readRsInt(rs, "loyaltyId");
			this.loyaltyPaid = Database.readRsInt(rs, "loyaltyPaid");
			this.reviewSuggestions = Database.readRsString(rs, "reviewSuggestions");
			this.section = Database.readRsString(rs, "section");
			this.reference = Database.readRsString(rs, "reference");
			this.deliveryBoy = Database.readRsString(rs, "deliveryBoy");
			this.deliveryTime = Database.readRsString(rs, "deliveryTimeStamp");
			this.remarks = Database.readRsString(rs, "remarks");
			this.firstName = Database.readRsString(rs, "firstName");
			this.paymentType = Database.readRsString(rs, "paymentType");
			this.totalPayment = Database.readRsBigDecimal(rs, "totalPayment");
			this.cashPayment = Database.readRsBigDecimal(rs, "cashPayment");
			this.cardPayment = Database.readRsBigDecimal(rs, "cardPayment");
			this.appPayment = Database.readRsBigDecimal(rs, "appPayment");
		}
	}

	public static class TableUsage implements Database.OrderOnEntity {

		public int getTableId() {
			return mTableId;
		}

		public String getUserId() {
			return mUserId;
		}

		public String getOrderId() {
			return mOrderId;
		}

		public String getWaiterId() {
			return waiterId;
		}

		public int getState() {
			return state;
		}

		public String getSection() {
			return section;
		}

		private int mTableId;
		private String mUserId;
		private String mOrderId;
		private String waiterId;
		private int state;
		private String section;

		@Override
		public void readFromDB(ResultSet rs) {
			this.mTableId = Database.readRsInt(rs, "tableId");
			this.mUserId = Database.readRsString(rs, "userId");
			this.mOrderId = Database.readRsString(rs, "orderId");
			this.waiterId = Database.readRsString(rs, "waiterId");
			this.state = Database.readRsInt(rs, "state");
			this.section = Database.readRsString(rs, "section");
		}
	}

	public static class MenuItem implements Database.OrderOnEntity {
		public String getMenuId() {
			return menuId;
		}

		public String getTitle() {
			return title;
		}

		public String getDescription() {
			return description;
		}

		public String getStation() {
			return station;
		}

		public BigDecimal getRate() {
			return rate;
		}

		public BigDecimal getInhouseRate() {
			return inhouseRate;
		}

		public BigDecimal getOnlineRate() {
			return onlineRate;
		}

		public BigDecimal getCostPrice() {
			return costPrice;
		}

		public String getCategory() {
			return category;
		}

		public String getFlags() {
			return flags;
		}

		public int getVegType() {
			return vegType;
		}

		public int getPreparationTime() {
			return preparationTime;
		}

		public String getImage() {
			return img;
		}

		public String getShortForm() {
			return shortForm;
		}

		public int getState() {
			return state;
		}

		public int getIsTaxable() {
			return isTaxable;
		}

		public String getAddOns() {
			return addOns;
		}

		public int getHasIncentive() {
			return hasIncentive;
		}

		public int getIncentive() {
			return incentive;
		}

		public ArrayList<Integer> getAddOnIds() {
			ArrayList<Integer> addOn = new ArrayList<Integer>();
			if (addOns.equals(""))
				return null;
			String[] temp = addOns.split(",");
			for (String t : temp) {
				addOn.add(Integer.parseInt(t.trim()));
			}
			return addOn;
		}

		public String getAddOnString() {
			return addOns;
		}

		private String menuId;
		private String title;
		private String description;
		private String station;
		private BigDecimal rate;
		private BigDecimal inhouseRate;
		private BigDecimal onlineRate;
		private BigDecimal costPrice;
		private String category;
		private String flags;
		private int vegType;
		private String img;
		private int preparationTime;
		private String shortForm;
		private int state;
		private int isTaxable;
		private String addOns;
		private int hasIncentive;
		private int incentive;

		@Override
		public void readFromDB(ResultSet rs) {
			this.menuId = Database.readRsString(rs, "menuId");
			this.title = Database.readRsString(rs, "title");
			this.description = Database.readRsString(rs, "description");
			this.station = Database.readRsString(rs, "station");
			this.rate = Database.readRsBigDecimal(rs, "rate");
			this.inhouseRate = Database.readRsBigDecimal(rs, "inhouseRate");
			this.onlineRate = Database.readRsBigDecimal(rs, "onlineRate");
			this.costPrice = Database.readRsBigDecimal(rs, "costPrice");
			this.category = Database.readRsString(rs, "category");
			this.flags = Database.readRsString(rs, "flags");
			this.vegType = Database.readRsInt(rs, "vegType");
			this.img = Database.readRsString(rs, "img");
			this.preparationTime = Database.readRsInt(rs, "preparationTime");
			this.shortForm = Database.readRsString(rs, "shortForm");
			this.state = Database.readRsInt(rs, "state");
			this.isTaxable = Database.readRsInt(rs, "isTaxable");
			this.addOns = Database.readRsString(rs, "addOns");
			this.hasIncentive = Database.readRsInt(rs, "hasIncentive");
			this.incentive = Database.readRsInt(rs, "incentive");
		}
	}

	public static class OrderItem implements Database.OrderOnEntity {
		public int getId() {
			return id;
		}

		public String getOrderId() {
			return orderId;
		}

		public String getSubOrderId() {
			return subOrderId;
		}

		public String getSubOrderDate() {
			return subOrderDate;
		}

		public String getLogTime() {
			return logTime;
		}

		public String getMenuId() {
			return menuId;
		}

		public String getTitle() {
			return title;
		}

		public String getCategory() {
			return category;
		}

		public String getVegType() {
			return vegType;
		}

		public int getState() {
			return state;
		}

		public int getQty() {
			return qty;
		}

		public String getSpecifications() {
			return specs;
		}

		public BigDecimal getRate() {
			return rate;
		}

		public int getItemId() {
			return itemId;
		}

		public String getBillNo() {
			return billNo;
		}

		public String getWaiterId() {
			return waiterId;
		}

		public String getReason() {
			return reason;
		}

		public String getStation() {
			return station;
		}

		public int getIsKOTPrinted() {
			return isKOTPrinted;
		}

		public int getIsTaxable() {
			return isTaxable;
		}

		public void setQuantity(int qty) {
			this.qty = qty;
		}

		private int id;
		private String orderId;
		private String subOrderId;
		private String subOrderDate;
		private String logTime;
		private String menuId;
		private String vegType;
		private String title;
		private String category;
		private String waiterId;
		private String specs;
		private BigDecimal rate;
		private int state;
		private int qty;
		private String billNo;
		private String reason;
		private String station;
		private int isKOTPrinted;
		private int isTaxable;
		private int itemId;

		@Override
		public void readFromDB(ResultSet rs) {
			this.orderId = Database.readRsString(rs, "orderId");
			this.id = Database.readRsInt(rs, "Id");
			this.subOrderId = Database.readRsString(rs, "subOrderId");
			this.subOrderDate = Database.readRsString(rs, "subOrderDate");
			this.menuId = Database.readRsString(rs, "menuId");
			this.vegType = Database.readRsString(rs, "vegType");
			this.title = Database.readRsString(rs, "title");
			this.category = Database.readRsString(rs, "category");
			this.waiterId = Database.readRsString(rs, "waiterId");
			this.state = Database.readRsInt(rs, "state");
			this.rate = Database.readRsBigDecimal(rs, "rate");
			this.qty = Database.readRsInt(rs, "qty");
			this.specs = Database.readRsString(rs, "specs");
			this.billNo = Database.readRsString(rs, "billNo");
			this.reason = Database.readRsString(rs, "reason");
			this.station = Database.readRsString(rs, "station");
			this.isKOTPrinted = Database.readRsInt(rs, "isKotPrinted");
			this.isTaxable = Database.readRsInt(rs, "isTaxable");
			this.itemId = Database.readRsInt(rs, "itemId");
			this.logTime = Database.readRsString(rs, "time");
		}
	}

	public static class OrderAddOn implements Database.OrderOnEntity {
		public String getOrderId() {
			return orderId;
		}

		public String getSubOrderId() {
			return subOrderId;
		}

		public int getAddOnId() {
			return addOnId;
		}

		public String getMenuId() {
			return menuId;
		}

		public int getItemId() {
			return itemId;
		}

		public String getName() {
			return name;
		}

		public int getQty() {
			return qty;
		}

		public void setQty(int qty) {
			this.qty = qty;
		}

		public BigDecimal getRate() {
			return rate;
		}

		public int getState() {
			return state;
		}

		private String orderId;
		private String subOrderId;
		private int addOnId;
		private int itemId;
		private String menuId;
		private String name;
		private BigDecimal rate;
		private int qty;
		private int state;

		@Override
		public void readFromDB(ResultSet rs) {
			this.orderId = Database.readRsString(rs, "orderId");
			this.subOrderId = Database.readRsString(rs, "subOrderId");
			this.addOnId = Database.readRsInt(rs, "addOnId");
			this.itemId = Database.readRsInt(rs, "itemId");
			this.menuId = Database.readRsString(rs, "menuId");
			this.name = Database.readRsString(rs, "name");
			this.rate = Database.readRsBigDecimal(rs, "rate");
			this.qty = Database.readRsInt(rs, "qty");
			this.state = Database.readRsInt(rs, "state");
		}
	}

	public static class OrderSpecification implements Database.OrderOnEntity {
		public String getOrderId() {
			return orderId;
		}

		public String getSubOrderId() {
			return subOrderId;
		}

		public String getMenuId() {
			return menuId;
		}

		public String getSpecification() {
			return specification;
		}

		public int getItemId() {
			return itemId;
		}

		private String orderId;
		private String subOrderId;
		private String specification;
		private int itemId;
		private String menuId;

		@Override
		public void readFromDB(ResultSet rs) {
			this.orderId = Database.readRsString(rs, "orderId");
			this.subOrderId = Database.readRsString(rs, "subOrderId");
			this.specification = Database.readRsString(rs, "specification");
			this.itemId = Database.readRsInt(rs, "itemId");
			this.menuId = Database.readRsString(rs, "menuId");
		}
	}

	public static class OrderTables implements Database.OrderOnEntity {

		public String getOrderId() {
			return orderId;
		}

		public int getOrderNumber() {
			return orderNumber;
		}

		public int getPax() {
			return pax;
		}

		public int getInhouse() {
			return inhouse;
		}

		public String getTableId() {
			return tableId;
		}

		public String getCustomerName() {
			return customerName;
		}

		public String getCustomerAddress() {
			return customerAddress;
		}

		public String getCustomerNumber() {
			return customerNumber;
		}

		private String orderId;
		private String tableId;
		private String customerName;
		private String customerAddress;
		private String customerNumber;
		private int orderNumber;
		private int pax;
		private int inhouse;

		@Override
		public void readFromDB(ResultSet rs) {
			this.tableId = Database.readRsString(rs, "tableId");
			this.customerName = Database.readRsString(rs, "customerName");
			this.customerAddress = Database.readRsString(rs, "customerAddress");
			this.customerNumber = Database.readRsString(rs, "customerNumber");
			this.orderId = Database.readRsString(rs, "orderId");
			this.orderNumber = Database.readRsInt(rs, "id");
			this.pax = Database.readRsInt(rs, "pax");
			this.inhouse = Database.readRsInt(rs, "inhouse");
		}
	}

	public static class KitchenDisplayOrders implements Database.OrderOnEntity {

		public String getOrderId() {
			return orderId;
		}

		public String getMenuId() {
			return menuId;
		}

		public String getTableId() {
			return tableId;
		}

		public String getSubOrderDate() {
			return subOrderDate;
		}

		public String getSubOrderId() {
			return subOrderId;
		}

		public String getTitle() {
			return title;
		}

		public String getStation() {
			return station;
		}

		public int getOrderState() {
			return orderState;
		}

		public int getState() {
			return state;
		}

		public int getQty() {
			return qty;
		}

		public int getVegType() {
			return vegType;
		}

		public String getSpecs() {
			return specs;
		}

		public int getPrepTime() {
			return prepTime;
		}

		public String getCustomerAddress() {
			return customerAddress;
		}

		public String getCustomerName() {
			return customerName;
		}

		public int getInhouse() {
			return inhouse;
		}

		public String getBillNo() {
			return billNo;
		}

		private String orderId;
		private String tableId;
		private String subOrderDate;
		private String subOrderId;
		private String title;
		private String station;
		private int state;
		private int orderState;
		private int qty;
		private String specs;
		private int prepTime;
		private String menuId;
		private String customerAddress;
		private String customerName;
		private int inhouse;
		private String billNo;
		private int vegType;

		@Override
		public void readFromDB(ResultSet rs) {
			this.orderId = Database.readRsString(rs, "orderId");
			this.tableId = Database.readRsString(rs, "tableId");
			this.subOrderDate = Database.readRsString(rs, "subOrderDate");
			this.subOrderId = Database.readRsString(rs, "subOrderId");
			this.station = Database.readRsString(rs, "station");
			this.title = Database.readRsString(rs, "title");
			this.qty = Database.readRsInt(rs, "qty");
			this.specs = Database.readRsString(rs, "specs");
			this.state = Database.readRsInt(rs, "state");
			this.orderState = Database.readRsInt(rs, "orderState");
			this.prepTime = Database.readRsInt(rs, "prepTime");
			this.menuId = Database.readRsString(rs, "menuId");
			this.customerAddress = Database.readRsString(rs, "customerAddress");
			this.customerName = Database.readRsString(rs, "customerName");
			this.billNo = Database.readRsString(rs, "billNo");
			this.inhouse = Database.readRsInt(rs, "inhouse");
			this.vegType = Database.readRsInt(rs, "vegType");
		}
	}

	public static class Discount implements Database.OrderOnEntity {

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public int getType() {
			return type;
		}

		public int getBarValue() {
			return barValue;
		}

		public int getFoodValue() {
			return foodValue;
		}
		
		public boolean getHasDiffBarValue() {
			if(barValue == foodValue)
				return false;
			else
				return true;
		}
		
		public boolean getHasExpiryDate() {
			if(expiryDate.equals("31/12/3000"))
				return false;
			else
				return true;
		}

		public String getStartDate() {
			return startDate;
		}

		public String getExpiryDate() {
			return expiryDate;
		}

		public String getUsageLimit() {
			return usageLimit;
		}

		public Boolean getHasUsageLimit() {
			if(usageLimit.equals("Unlimited"))
				return false;
			else
				return true;
		}

		public String[] getValidCollections() {
			return validCollections.split(",");
		}

		public Boolean getHasCollections() {
			return validCollections.length() > 0;
		}

		private String name;
		private String description;
		private int type;
		private int barValue;
		private int foodValue;
		private String startDate;
		private String expiryDate;
		private String usageLimit;
		private String validCollections;

		@Override
		public void readFromDB(ResultSet rs) {
			this.name = Database.readRsString(rs, "name");
			this.description = Database.readRsString(rs, "description");
			this.type = Database.readRsInt(rs, "type");
			this.barValue = Database.readRsInt(rs, "barValue");
			this.foodValue = Database.readRsInt(rs, "foodValue");
			this.startDate = Database.readRsString(rs, "startDate");
			this.expiryDate = Database.readRsString(rs, "expiryDate");
			this.usageLimit = Database.readRsString(rs, "usageLimit");
			this.validCollections = Database.readRsString(rs, "validCollections");
		}
	}
	
	public static class OrderDiscount implements Database.OrderOnEntity {
		
		private String name;
		private String type;
		private String category;
		private BigDecimal value;
		private BigDecimal amount;
		private String code;
		private String isTaxed;
		private BigDecimal maxValue;
		private BigDecimal discountAppliedOn;
		private String isRestaurantDiscount;
		private String isDeliveryDiscount;
		private String orderId;
		
		public String getName() {
			return name;
		}
		public String getType() {
			return type;
		}
		public String getCategory() {
			return category;
		}
		public BigDecimal getValue() {
			return value;
		}
		public BigDecimal getAmount() {
			return amount;
		}
		public String getCode() {
			return code;
		}
		public String getIsTaxed() {
			return isTaxed;
		}
		public BigDecimal getMaxValue() {
			return maxValue;
		}
		public BigDecimal getDiscountAppliedOn() {
			return discountAppliedOn;
		}
		public String getIsRestaurantDiscount() {
			return isRestaurantDiscount;
		}
		public String getIsDeliveryDiscount() {
			return isDeliveryDiscount;
		}
		public String getOrderId() {
			return orderId;
		}
		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.name = Database.readRsString(rs, "name");
			this.code = Database.readRsString(rs, "code");
			this.category = Database.readRsString(rs, "category");
			this.type = Database.readRsString(rs, "type");
			this.isTaxed = Database.readRsString(rs, "isTexed");
			this.isRestaurantDiscount = Database.readRsString(rs, "isRestaurantDiscount");
			this.isDeliveryDiscount = Database.readRsString(rs, "isDeliveryDiscount");
			this.orderId = Database.readRsString(rs, "orderId");
			this.value = Database.readRsBigDecimal(rs, "value");
			this.amount = Database.readRsBigDecimal(rs, "amount");
			this.maxValue = Database.readRsBigDecimal(rs, "maxValue");
		}
	}
	
	public static class ReturnedItemsReport implements Database.OrderOnEntity {

		public int getId() {
			return id;
		}

		public String getOrderId() {
			return orderId;
		}

		public String getOrderDate() {
			return orderDate;
		}

		public String getTitle() {
			return title;
		}

		public int getRate() {
			return rate;
		}
		public int getTotal() {
			return total;
		}

		public int getQty() {
			return qty;
		}

		public String getBillNo() {
			return billNo;
		}

		public String getReason() {
			return reason;
		}

		public String getWaiterId() {
			return waiterId;
		}

		public int getInhouse() {
			return inhouse;
		}

		public String getReturnTime() {
			return returnTime;
		}

		public String getAuthorizer() {
			return authorizer;
		}

		private int id;
		private String orderId;
		private String orderDate;
		private String title;
		private int rate;
		private int qty;
		private int total;
		private String billNo;
		private String reason;
		private String waiterId;
		private int inhouse;
		private String returnTime;
		private String authorizer;

		@Override
		public void readFromDB(ResultSet rs) {
			this.orderId = Database.readRsString(rs, "orderId");
			this.id = Database.readRsInt(rs, "id");
			this.returnTime = Database.readRsString(rs, "dateTime");
			this.orderDate = Database.readRsString(rs, "orderDate");
			this.title = Database.readRsString(rs, "title");
			this.waiterId = Database.readRsString(rs, "waiterId");
			this.rate = Database.readRsInt(rs, "rate");
			this.total = Database.readRsInt(rs, "total");
			this.qty = Database.readRsInt(rs, "quantity");
			this.billNo = Database.readRsString(rs, "billNo");
			this.reason = Database.readRsString(rs, "reason");
			this.inhouse = Database.readRsInt(rs, "inhouse");
			this.authorizer = Database.readRsString(rs, "auth");
		}
	}

	// DailyDiscountReport-ap
	public static class DailyDiscountReport implements Database.OrderOnEntity {
		private String name;
		private Integer type;
		private String foodValue;
		private String barValue;
		private String description;
		// payment
		private BigDecimal sumDiscount;
		private BigDecimal sumTotal;
		private BigDecimal avgDiscount;
		private BigDecimal avgTotal;
		private Integer paymentId;
		private String orderDate;
		private int ordersAffected;
		private BigDecimal sumDiscountedTotal;
		private String ordersDiscountedPer;
		private String discountPer;
		private String avgDiscountPer;
		private String grossDiscountPer;
		private BigDecimal totalOrders;
		private BigDecimal grossSale;
		private BigDecimal grossDiscount;

		@Override
		public void readFromDB(ResultSet rs) {
			this.name = Database.readRsString(rs, "name");
			this.type = Database.readRsInt(rs, "type");
			this.foodValue = Database.readRsString(rs, "foodValue");
			this.barValue = Database.readRsString(rs, "barValue");
			this.description = Database.readRsString(rs, "description");
			// payment table
			this.sumDiscount = Database.readRsBigDecimal(rs, "sumDiscount");
			this.sumTotal = Database.readRsBigDecimal(rs, "sumTotal");
			this.avgDiscount = Database.readRsBigDecimal(rs, "avgDiscount");
			this.avgTotal = Database.readRsBigDecimal(rs, "avgTotal");
			this.paymentId = Database.readRsInt(rs, "paymentId");
			this.orderDate = Database.readRsString(rs, "orderDate");
			this.ordersDiscountedPer = Database.readRsString(rs, "ordersDiscountedPer");
			this.discountPer = Database.readRsString(rs, "discountPer");
			this.avgDiscountPer = Database.readRsString(rs, "avgDiscountPer");
			this.grossDiscountPer = Database.readRsString(rs, "grossDiscountPer");
			this.ordersAffected = Database.readRsInt(rs, "ordersAffected");
			this.sumDiscountedTotal = Database.readRsBigDecimal(rs, "sumDiscountedTotal");
			this.totalOrders = Database.readRsBigDecimal(rs, "totalOrders");
			this.grossSale = Database.readRsBigDecimal(rs, "grossSale");
			this.grossDiscount = Database.readRsBigDecimal(rs, "grossDiscount");
		}

		public String getName() {
			return name;
		}

		public Integer getType() {
			return type;
		}
		public String getFoodValue() {
			return foodValue;
		}
		public String getBarValue() {
			return barValue;
		}

		public String getDescription() {
			return description;
		}

		public BigDecimal getDiscount() {
			return sumDiscount;
		}

		public BigDecimal getTotal() {
			return sumTotal;
		}

		public String getTotalPer() {
			BigDecimal per = ((sumDiscount.divide(sumTotal)).multiply(new BigDecimal("100")));
			return per.toString() + " %";
		}

		public BigDecimal getAvgTotal() {
			return avgTotal;
		}

		public BigDecimal getAvgDiscount() {
			return avgDiscount;
		}

		public Integer getPaymentId() {
			return paymentId;
		}

		public String getDate() {
			return orderDate;
		}

		public Integer getOrdersAffected() {
			return ordersAffected;
		}

		public BigDecimal getSumDiscountedTotal() {
			return sumDiscountedTotal;
		}

		public String getDiscountPer() {
			return discountPer;
		}

		public BigDecimal getSumDiscount() {
			return sumDiscount;
		}

		public BigDecimal getSumTotal() {
			return sumTotal;
		}

		public String getOrderDate() {
			return orderDate;
		}

		public String getOrdersDiscountedPer() {
			return ordersDiscountedPer;
		}

		public String getAvgDiscountPer() {
			return avgDiscountPer;
		}

		public String getGrossDiscountPer() {
			return grossDiscountPer;
		}

		public BigDecimal getTotalOrders() {
			return totalOrders;
		}

		public BigDecimal getGrossSale() {
			return grossSale;
		}

		public BigDecimal getGrossDiscount() {
			return grossDiscount;
		}
		
	}

	// DiscountReport-ap
	public static class DiscountReport implements Database.OrderOnEntity {
		private String discountName;
		private String orderDate;
		private BigDecimal total;
		private BigDecimal foodDiscount;
		private BigDecimal barDiscount;
		private BigDecimal totalDiscount;
		private String customerName;
		private BigDecimal discountedTotal;

		@Override
		public void readFromDB(ResultSet rs) {
			this.discountName = Database.readRsString(rs, "discountName");
			this.orderDate = Database.readRsString(rs, "orderDate");
			this.total = Database.readRsBigDecimal(rs, "total");
			this.foodDiscount = Database.readRsBigDecimal(rs, "foodDiscount");
			this.barDiscount = Database.readRsBigDecimal(rs, "barDiscount");
			this.totalDiscount = Database.readRsBigDecimal(rs, "totalDiscount");
			this.customerName = Database.readRsString(rs, "customerName");
			this.discountedTotal = Database.readRsBigDecimal(rs, "discountedTotal");

		}

		public String getDiscountName() {
			return discountName;
		}

		public String getOrderDate() {
			return orderDate;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public BigDecimal getFoodDiscount() {
			return foodDiscount;
		}

		public BigDecimal getBarDiscount() {
			return barDiscount;
		}

		public BigDecimal getTotalDiscount() {
			return totalDiscount;
		}

		public String getCustomerName() {
			return customerName;
		}

		public BigDecimal getDiscountedTotal() {
			return discountedTotal;
		}
	}

	// GrossSaleReport-ap
	public static class GrossSaleReport implements Database.OrderOnEntity {
		private BigDecimal grossTotal;
		private BigDecimal grossDiscount;
		private BigDecimal grossLoyalty;
		private BigDecimal grossComplimentary;
		private BigDecimal grossGst;
		private BigDecimal appPayment;
		private BigDecimal cardPayment;
		private BigDecimal cashPayment;
		private BigDecimal grossServiceCharge;
		private BigDecimal totalSale;
		private BigDecimal NetSales;
		private BigDecimal grossExpenses;
		private BigDecimal grossPayIns;
		private BigDecimal sumVoids;
		private BigDecimal sumReturns;
		private Integer countVoids;
		private Integer countReturns;

		@Override
		public void readFromDB(ResultSet rs) {
			this.grossTotal = Database.readRsBigDecimal(rs, "grossTotal");
			this.grossDiscount = Database.readRsBigDecimal(rs, "grossDiscount");
			this.grossLoyalty = Database.readRsBigDecimal(rs, "grossLoyalty");
			this.grossComplimentary = Database.readRsBigDecimal(rs, "grossComplimentary");
			this.grossGst = Database.readRsBigDecimal(rs, "grossGst"); 
			this.appPayment = Database.readRsBigDecimal(rs, "appPayment");
			this.cardPayment = Database.readRsBigDecimal(rs, "cardPayment");
			this.cashPayment = Database.readRsBigDecimal(rs, "cashPayment");
			this.grossServiceCharge = Database.readRsBigDecimal(rs, "grossServiceCharge");
			this.NetSales = Database.readRsBigDecimal(rs, "NetSales");
			this.grossExpenses = Database.readRsBigDecimal(rs, "grossExpenses");
			this.grossPayIns = Database.readRsBigDecimal(rs, "totalPayIns");
			this.totalSale = Database.readRsBigDecimal(rs, "totalSale");
			this.sumVoids = Database.readRsBigDecimal(rs, "sumVoids");
			this.sumReturns = Database.readRsBigDecimal(rs, "sumReturns");
			this.countVoids = Database.readRsInt(rs, "countVoids");
			this.countReturns = Database.readRsInt(rs, "countReturns");

		}

		public BigDecimal getGrossLoyalty() {
			return grossLoyalty;
		}

		public BigDecimal getGrossComplimentary() {
			return grossComplimentary;
		}

		public BigDecimal getTotalSale() {
			return totalSale;
		}

		public BigDecimal getGrossPayIns() {
			return grossPayIns;
		}

		public BigDecimal getGrossTotal() {
			return grossTotal;
		}

		public BigDecimal getGrossDiscount() {
			return grossDiscount;
		}

		public BigDecimal getGrossGst() {
			return grossGst;
		}

		public BigDecimal getAppPayment() {
			return appPayment;
		}

		public BigDecimal getCardPayment() {
			return cardPayment;
		}

		public BigDecimal getCashPayment() {
			return cashPayment;
		}

		public BigDecimal getGrossServiceCharge() {
			return grossServiceCharge;
		}

		public BigDecimal getNetSales() {
			return NetSales;
		}

		public BigDecimal getGrossExpenses() {
			return grossExpenses;
		}

		public BigDecimal getSumVoids() {
			return sumVoids;
		}

		public BigDecimal getSumReturns() {
			return sumReturns;
		}

		public Integer getCountVoids() {
			return countVoids;
		}

		public Integer getCountReturns() {
			return countReturns;
		}
	}

	// CollectionWiseReportA-ap
	public static class CollectionWiseReportA implements Database.OrderOnEntity {
		private String collection;
		private BigDecimal grossTotal;
		private BigDecimal averagePrice;
		private Integer noOrdersAffected;
		private String noOrdersAffectedPer;
		private Integer totalQuantityOrdered;
		private String totalQuantityOrderedPer;

		@Override
		public void readFromDB(ResultSet rs) {
			this.collection = Database.readRsString(rs, "collection");
			this.grossTotal = Database.readRsBigDecimal(rs, "grossTotal");
			this.averagePrice = Database.readRsBigDecimal(rs, "averagePrice");
			this.noOrdersAffected = Database.readRsInt(rs, "noOrdersAffected");
			this.noOrdersAffectedPer = Database.readRsString(rs, "noOrdersAffectedPer");
			this.totalQuantityOrdered = Database.readRsInt(rs, "totalQuantityOrdered");
			this.totalQuantityOrderedPer = Database.readRsString(rs, "totalQuantityOrderedPer");
		}

		public String getCollection() {
			return collection;
		}

		public BigDecimal getGrossTotal() {
			return grossTotal;
		}

		public BigDecimal getAveragePrice() {
			return averagePrice;
		}

		public Integer getNoOrdersAffected() {
			return noOrdersAffected;
		}

		public String getNoOrdersAffectedPer() {
			return noOrdersAffectedPer;
		}

		public Integer getTotalQuantityOrdered() {
			return totalQuantityOrdered;
		}

		public String getTotalQuantityOrderedPer() {
			return totalQuantityOrderedPer;
		}
	}

	// CollectionWiseReportB-ap
	public static class CollectionWiseReportB implements Database.OrderOnEntity {
		private String topItemTitle;

		@Override
		public void readFromDB(ResultSet rs) {
			this.topItemTitle = Database.readRsString(rs, "topItemTitle");
		}

		public String getHotItem() {
			return topItemTitle;
		}
	}

	// DailyOperationReport-ap
	public static class DailyOperationReport implements Database.OrderOnEntity {
		// total Revenue
		private BigDecimal totalRevenue;
		private BigDecimal grossTotal;
		private BigDecimal grossDiscount;
		private BigDecimal grossTaxes;
		private BigDecimal grossServiceCharge;
		private BigDecimal NetSales;
		// total operating cost
		private BigDecimal totalOperatingCost;
		private BigDecimal INVENTORY;
		private BigDecimal LABOUR;
		private BigDecimal RENT;
		private BigDecimal ELECTRICITY_BILL;
		private BigDecimal GAS_BILL;
		private BigDecimal PETROL;
		private BigDecimal TELEPHONE_BILL;
		private BigDecimal MOBILE_RECHARGE;
		private BigDecimal INTERNET;
		private BigDecimal SOFTWARE;
		private BigDecimal COMPUTER_HARDWARE;
		private BigDecimal REPAIRS;
		private BigDecimal OTHERS;
		private BigDecimal CASH_LIFT;
		// Total Operating Margin
		private BigDecimal totalOperatingMargin;
		private BigDecimal paidIn;
		private BigDecimal paidOut;
		// operating metrics
		// main
		private String serviceType;
		private BigDecimal AvgAmountPerGuest;
		private BigDecimal AvgAmountPerCheck;
		private BigDecimal Total;
		private BigDecimal noOfGuests;
		private BigDecimal noOfBills;

		// extras
		private BigDecimal AvgAmountPerTableTurned;
		private BigDecimal voids;
		private BigDecimal returns;

		@Override
		public void readFromDB(ResultSet rs) {
			// total Revenue
			this.totalRevenue = Database.readRsBigDecimal(rs, "totalRevenue");
			this.grossTotal = Database.readRsBigDecimal(rs, "grossTotal");
			this.grossDiscount = Database.readRsBigDecimal(rs, "grossDiscount");
			this.grossTaxes = Database.readRsBigDecimal(rs, "grossTaxes");
			this.grossServiceCharge = Database.readRsBigDecimal(rs, "grossServiceCharge");
			this.NetSales = Database.readRsBigDecimal(rs, "NetSales");
			// total operating cost
			this.totalOperatingCost = Database.readRsBigDecimal(rs, "totalOperatingCost");
			this.INVENTORY = Database.readRsBigDecimal(rs, "INVENTORY");
			this.LABOUR = Database.readRsBigDecimal(rs, "LABOUR");
			this.RENT = Database.readRsBigDecimal(rs, "RENT");
			this.ELECTRICITY_BILL = Database.readRsBigDecimal(rs, "ELECTRICITY_BILL");
			this.GAS_BILL = Database.readRsBigDecimal(rs, "GAS_BILL");
			this.PETROL = Database.readRsBigDecimal(rs, "PETROL");
			this.TELEPHONE_BILL = Database.readRsBigDecimal(rs, "TELEPHONE_BILL");
			this.MOBILE_RECHARGE = Database.readRsBigDecimal(rs, "MOBILE_RECHARGE");
			this.INTERNET = Database.readRsBigDecimal(rs, "INTERNET");
			this.SOFTWARE = Database.readRsBigDecimal(rs, "SOFTWARE");
			this.COMPUTER_HARDWARE = Database.readRsBigDecimal(rs, "COMPUTER_HARDWARE");
			this.REPAIRS = Database.readRsBigDecimal(rs, "REPAIRS");
			this.OTHERS = Database.readRsBigDecimal(rs, "OTHERS");
			this.CASH_LIFT = Database.readRsBigDecimal(rs, "CASH_LIFT");
			// Total Operating Margin
			this.totalOperatingMargin = Database.readRsBigDecimal(rs, "totalOperatingMargin");
			this.paidIn = Database.readRsBigDecimal(rs, "paidIn");
			this.paidOut = Database.readRsBigDecimal(rs, "paidOut");
			// operating metrics
			// main
			this.serviceType = Database.readRsString(rs, "serviceType");
			this.AvgAmountPerGuest = Database.readRsBigDecimal(rs, "AvgAmountPerGuest");
			this.AvgAmountPerCheck = Database.readRsBigDecimal(rs, "AvgAmountPerCheck");
			this.totalOperatingCost = Database.readRsBigDecimal(rs, "topItemTitle");
			this.Total = Database.readRsBigDecimal(rs, "Total");
			this.noOfGuests = Database.readRsBigDecimal(rs, "noOfGuests");
			this.noOfBills = Database.readRsBigDecimal(rs, "noOfBills");
			// extras
			this.AvgAmountPerTableTurned = Database.readRsBigDecimal(rs, "AvgAmountPerTableTurned");
			this.voids = Database.readRsBigDecimal(rs, "voids");
			this.returns = Database.readRsBigDecimal(rs, "returns");
		}

		// total Revenue
		public BigDecimal getTotalRevenue() {
			return totalRevenue;
		}

		public BigDecimal getGrossTotal() {
			return grossTotal;
		}

		public BigDecimal getGrossDiscount() {
			return grossDiscount;
		}

		public BigDecimal getGrossTaxes() {
			return grossTaxes;
		}

		public BigDecimal getGrossServiceCharge() {
			return grossServiceCharge;
		}

		public BigDecimal getNetSales() {
			return NetSales;
		}

		// total operating cost
		public BigDecimal gettotalOperatingCost() {
			return totalOperatingCost;
		}

		public BigDecimal getINVENTORY() {
			return INVENTORY;
		}

		public BigDecimal getLABOUR() {
			return LABOUR;
		}

		public BigDecimal getRENT() {
			return RENT;
		}

		public BigDecimal getELECTRICITY_BILL() {
			return ELECTRICITY_BILL;
		}

		public BigDecimal getGAS_BILL() {
			return GAS_BILL;
		}

		public BigDecimal getPETROL() {
			return PETROL;
		}

		public BigDecimal getTELEPHONE_BILL() {
			return TELEPHONE_BILL;
		}

		public BigDecimal getMOBILE_RECHARGE() {
			return MOBILE_RECHARGE;
		}

		public BigDecimal getINTERNET() {
			return INTERNET;
		}

		public BigDecimal getSOFTWARE() {
			return SOFTWARE;
		}

		public BigDecimal getCOMPUTER_HARDWARE() {
			return COMPUTER_HARDWARE;
		}

		public BigDecimal getREPAIRS() {
			return REPAIRS;
		}

		public BigDecimal getOTHERS() {
			return OTHERS;
		}

		public BigDecimal getCASH_LIFT() {
			return CASH_LIFT;
		}

		// Total Operating Margin
		public BigDecimal getTotalOperatingMargin() {
			return totalOperatingMargin;
		}

		public BigDecimal getPaidIn() {
			return paidIn;
		}

		public BigDecimal getPaidOut() {
			return paidOut;
		}

		// operating metrics
		// main
		public String getServiceType() {
			return serviceType;
		}

		public BigDecimal getAvgAmountPerGuest() {
			return AvgAmountPerGuest;
		}

		public BigDecimal getAvgAmountPerCheck() {
			return AvgAmountPerCheck;
		}

		public BigDecimal getTotal() {
			return Total;
		}

		public BigDecimal getNoOfGuests() {
			return noOfGuests;
		}

		public BigDecimal getNoOfBills() {
			return noOfBills;
		}

		// extras
		public BigDecimal getAvgAmountPerTableTurned() {
			return AvgAmountPerTableTurned;
		}

		public BigDecimal getVoids() {
			return voids;
		}

		public BigDecimal getReturns() {
			return returns;
		}
	}

	// ItemWiseReport-ap (edited)
	public static class ConsumptionReport implements Database.OrderOnEntity {
		private String category;
		private String title;
		private int qty;
		private BigDecimal rate;
		private int compQty;
		private BigDecimal total;
		private BigDecimal totalAfterDiscount;
		private BigDecimal departmentSale;
		private BigDecimal totalSale;
		private int totalSaleQty;
		private int totalCompQty;
		private int totalQty;
		private BigDecimal percentOfDepartmentSale;
		private BigDecimal percentOfTotalSale;
		private BigDecimal percentOfTotalQty;
		
		public String getCategory() {
			return category;
		}
		public String getTitle() {
			return title;
		}
		public int getQty() {
			return qty;
		}
		public BigDecimal getRate() {
			return rate;
		}
		public int getCompQty() {
			return compQty;
		}
		public BigDecimal getTotal() {
			return total;
		}
		public BigDecimal getTotalAfterDiscount() {
			return totalAfterDiscount;
		}
		public BigDecimal getTotalSale() {
			return totalSale;
		}
		public BigDecimal getDepartmentSale() {
			return departmentSale;
		}
		public int getTotalSaleQty() {
			return totalSaleQty;
		}
		public int getTotalCompQty() {
			return totalCompQty;
		}
		public int getTotalQty() {
			return totalQty;
		}
		public BigDecimal getPercentOfDepartmentSale() {
			return percentOfDepartmentSale;
		}
		public BigDecimal getPercentOfTotalSale() {
			return percentOfTotalSale;
		}
		public BigDecimal getPercentOfTotalQty() {
			return percentOfTotalQty;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.title = Database.readRsString(rs, "title");
			this.qty = Database.readRsInt(rs, "qty");
			this.category = Database.readRsString(rs, "category");
			this.rate = Database.readRsBigDecimal(rs, "rate");
			this.compQty = Database.readRsInt(rs, "compQty");
			this.total = Database.readRsBigDecimal(rs, "total");
			this.totalAfterDiscount = Database.readRsBigDecimal(rs, "totalAfterDiscount");
			this.departmentSale = Database.readRsBigDecimal(rs, "departmentSale");
			this.totalSale = Database.readRsBigDecimal(rs, "totalSale");
			this.totalSaleQty = Database.readRsInt(rs, "totalSaleQty");
			this.totalCompQty = Database.readRsInt(rs, "totalCompQty");
			this.totalQty = Database.readRsInt(rs, "totalQty");
			this.percentOfDepartmentSale = Database.readRsBigDecimal(rs, "percentOfDepartmentSale");
			this.percentOfTotalSale = Database.readRsBigDecimal(rs, "percentOfTotalSale");
			this.percentOfTotalQty = Database.readRsBigDecimal(rs, "percentOfTotalQty");
		}
	}
	// ItemWiseReport-ap (edited)
	public static class ItemWiseReport implements Database.OrderOnEntity {
		private String category; // Jason
		private int qty;
		private String menuId;
		private String title;

		public String getCategory() {
			return category;
		}

		public int getQty() {
			return qty;
		}

		public String getMenuId() {
			return menuId;
		}

		public String getTitle() {
			return title;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.title = Database.readRsString(rs, "title");
			this.menuId = Database.readRsString(rs, "menuId");
			this.qty = Database.readRsInt(rs, "qty");
			this.category = Database.readRsString(rs, "category");
		}
	}

	// LunchDinnerSalesReport-ap (edited)
	public static class PaymentWiseSalesReport implements Database.OrderOnEntity {
		private BigDecimal foodBill;
		private BigDecimal barBill;
		private BigDecimal total;
		private BigDecimal cash;
		private BigDecimal card;
		private BigDecimal app;
		private BigDecimal VISA;
		private BigDecimal MASTERCARD;
		private BigDecimal MAESTRO;
		private BigDecimal AMEX;
		private BigDecimal RUPAY;
		private BigDecimal MSWIPE;
		private BigDecimal OTHERS;
		private BigDecimal ZOMATO;
		private BigDecimal ZOMATO_PAY;
		private BigDecimal SWIGGY;
		private BigDecimal PAYTM;
		private BigDecimal DINE_OUT;
		private BigDecimal FOOD_PANDA;
		private BigDecimal UBER_EATS;
		private BigDecimal FOODILOO;
		private BigDecimal NEARBY;
		private int inhouse;
		private int cover;

		@Override
		public void readFromDB(ResultSet rs) {
			this.foodBill = Database.readRsBigDecimal(rs, "foodBill");
			this.barBill = Database.readRsBigDecimal(rs, "barBill");
			this.total = Database.readRsBigDecimal(rs, "total");
			this.inhouse = Database.readRsInt(rs, "inhouse");
			this.cover = Database.readRsInt(rs, "cover");
			this.cash = Database.readRsBigDecimal(rs, "cash");
			this.card = Database.readRsBigDecimal(rs, "card");
			this.app = Database.readRsBigDecimal(rs, "app");
			this.VISA = Database.readRsBigDecimal(rs, "VISA");
			this.MASTERCARD = Database.readRsBigDecimal(rs, "MASTERCARD");
			this.MAESTRO = Database.readRsBigDecimal(rs, "MAESTRO");
			this.AMEX = Database.readRsBigDecimal(rs, "AMEX");
			this.RUPAY = Database.readRsBigDecimal(rs, "RUPAY");
			this.MSWIPE = Database.readRsBigDecimal(rs, "MSWIPE");
			this.OTHERS = Database.readRsBigDecimal(rs, "OTHERS");
			this.ZOMATO = Database.readRsBigDecimal(rs, "ZOMATO");
			this.ZOMATO_PAY = Database.readRsBigDecimal(rs, "ZOMATOPAY");
			this.SWIGGY = Database.readRsBigDecimal(rs, "SWIGGY");
			this.PAYTM = Database.readRsBigDecimal(rs, "PAYTM");
			this.DINE_OUT = Database.readRsBigDecimal(rs, "DINEOUT");
			this.FOOD_PANDA = Database.readRsBigDecimal(rs, "FOODPANDA");
			this.UBER_EATS = Database.readRsBigDecimal(rs, "UBEREATS");
			this.FOODILOO = Database.readRsBigDecimal(rs, "FOODILOO");
			this.NEARBY = Database.readRsBigDecimal(rs, "NEARBY");
		}

		public BigDecimal getFoodBill() {
			return foodBill;
		}

		public BigDecimal getBarBill() {
			return barBill;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public int getCover() {
			return cover;
		}

		public int getInhouse() {
			return inhouse;
		}

		public BigDecimal getCash() {
			return cash;
		}

		public BigDecimal getCard() {
			return card;
		}

		public BigDecimal getApp() {
			return app;
		}

		public BigDecimal getVISA() {
			return VISA;
		}

		public BigDecimal getMASTERCARD() {
			return MASTERCARD;
		}

		public BigDecimal getMAESTRO() {
			return MAESTRO;
		}

		public BigDecimal getAMEX() {
			return AMEX;
		}

		public BigDecimal getRUPAY() {
			return RUPAY;
		}

		public BigDecimal getMSWIPE() {
			return MSWIPE;
		}

		public BigDecimal getZOMATO() {
			return ZOMATO;
		}

		public BigDecimal getPAYTM() {
			return PAYTM;
		}

		public BigDecimal getSWIGGY() {
			return SWIGGY;
		}

		public BigDecimal getDINE_OUT() {
			return DINE_OUT;
		}

		public BigDecimal getOTHERS() {
			return OTHERS;
		}

		public BigDecimal getZOMATO_PAY() {
			return ZOMATO_PAY;
		}

		public BigDecimal getFOOD_PANDA() {
			return FOOD_PANDA;
		}

		public BigDecimal getUBER_EATS() {
			return UBER_EATS;
		}

		public BigDecimal getFOODILOO() {
			return FOODILOO;
		}

		public BigDecimal getNEARBY() {
			return NEARBY;
		}
	}

	public static class MonthReport implements Database.OrderOnEntity {

		public int getTotalOrders() {
			return totalOrders;
		}

		public String getBestWaiter() {
			return bestWaiter;
		}

		public int getTotalOrderByWaiter() {
			return totalOrderByWaiter;
		}

		public String getBestItem() {
			return bestItem;
		}

		public int getItemOrderCount() {
			return itemOrderCount;
		}

		public String getItemImage() {
			return itemImage;
		}

		public String getItemId() {
			return itemId;
		}

		public int getTotalSales() {
			return totalSales;
		}

		private int totalOrders;
		private String bestWaiter;
		private int totalOrderByWaiter;
		private String bestItem;
		private int itemOrderCount;
		private String itemImage;
		private String itemId;
		private int totalSales;

		@Override
		public void readFromDB(ResultSet rs) {
			this.totalOrders = Database.readRsInt(rs, "count");
			this.bestWaiter = Database.readRsString(rs, "employeeId");
			this.totalOrderByWaiter = Database.readRsInt(rs, "waitersOrders");
			this.bestItem = Database.readRsString(rs, "title");
			this.itemOrderCount = Database.readRsInt(rs, "orderCount");
			this.itemImage = Database.readRsString(rs, "img");
			this.itemId = Database.readRsString(rs, "itemId");
			this.totalSales = Database.readRsInt(rs, "totalSales");
		}
	}

	public static class YearlyReport implements Database.OrderOnEntity {

		public int getTotalOrders() {
			return totalOrders;
		}

		public String getMonthName() {
			return new DateFormatSymbols().getMonths()[month - 1].substring(0, 3).toUpperCase();
		}

		public int getMonth() {
			return month;
		}

		public int totalOrders;
		public String monthName;
		public int month;

		@Override
		public void readFromDB(ResultSet rs) {

			this.totalOrders = Database.readRsInt(rs, "totalOrders");
		}
	}

	public static class IncentiveReport implements Database.OrderOnEntity {

		public BigDecimal getIncentive() {
			return incentive;
		}

		public BigDecimal getSale() {
			return sale;
		}

		public String getUserId() {
			return userId;
		}

		public void setIncentive(BigDecimal incentive) {
			this.incentive = incentive;
		}

		public void setSale(BigDecimal sale) {
			this.sale = sale;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		private BigDecimal incentive;
		private BigDecimal sale;
		private String userId;
		private String title;
		private int quantity;

		@Override
		public void readFromDB(ResultSet rs) {
			this.incentive = Database.readRsBigDecimal(rs, "incentive");
			this.sale = Database.readRsBigDecimal(rs, "sale");
			this.userId = Database.readRsString(rs, "userId");
			this.title = Database.readRsString(rs, "title");
			this.quantity = Database.readRsInt(rs, "qty");
		}
	}

	public static class DeliveryReport implements Database.OrderOnEntity {
		
		public String getBillNo() {
			return billNo;
		}

		public String getDeliveryBoy() {
			return deliveryBoy;
		}

		public String getDispatchTime() {
			return dispatchTime;
		}

		public BigDecimal getTotal() {
			return total;
		}

		private String billNo;
		private String deliveryBoy;
		private String dispatchTime;
		private BigDecimal total;

		@Override
		public void readFromDB(ResultSet rs) {
			this.billNo = Database.readRsString(rs, "billNo");
			this.deliveryBoy = Database.readRsString(rs, "deliveryBoy");
			this.dispatchTime = Database.readRsString(rs, "dispatchTime");
			this.total = Database.readRsBigDecimal(rs, "total");
		}
	}

	public static class Employee implements Database.OrderOnEntity {

		private String employeeId;
		private String firstName;
		private String surName;
		private String address;
		private String contactNumber;
		private String dob;
		private String sex;
		private String hiringDate;
		private String designation;
		private String department;
		private int salary;
		private int bonus;
		private String image;
		private String middleName;
		private String email;
		private BigDecimal accountBalance;

		public String getEmployeeId() {
			return employeeId;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getSurName() {
			return surName;
		}

		public String getFullName() {
			return firstName + " " + surName;
		}

		public String getAddress() {
			return address;
		}

		public String getContactNumber() {
			return contactNumber;
		}

		public String getDob() {
			return dob;
		}

		public String getSex() {
			return sex;
		}

		public String getHiringDate() {
			return hiringDate;
		}

		public String getDesignation() {
			return designation;
		}

		public String getDepartment() {
			return department;
		}

		public int getSalary() {
			return salary;
		}

		public int getBonus() {
			return bonus;
		}

		public String getImage() {
			return image;
		}

		public String getMiddleName() {
			return middleName;
		}

		public String getEmail() {
			return email;
		}

		public BigDecimal getAccountBalance() {
			return accountBalance;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.employeeId = Database.readRsString(rs, "employeeId");
			this.firstName = Database.readRsString(rs, "firstName");
			this.surName = Database.readRsString(rs, "surName");
			this.address = Database.readRsString(rs, "address");
			this.contactNumber = Database.readRsString(rs, "contactNumber");
			this.dob = Database.readRsString(rs, "dob");
			this.sex = Database.readRsString(rs, "sex");
			this.hiringDate = Database.readRsString(rs, "hiringDate");
			this.designation = Database.readRsString(rs, "designation");
			this.department = Database.readRsString(rs, "department");
			this.salary = Database.readRsInt(rs, "salary");
			this.bonus = Database.readRsInt(rs, "bonus");
			this.image = Database.readRsString(rs, "image");
			this.middleName = Database.readRsString(rs, "middleName");
			this.email = Database.readRsString(rs, "email");
			this.accountBalance = Database.readRsBigDecimal(rs, "accountBalance");
		}
	}

	public static class Attendance implements Database.OrderOnEntity {

		private int id;
		private String employeeId;
		private String firstName;
		private String surName;
		private String checkInTime;
		private String checkOutTime;
		private String checkInDate;
		private String checkOutDate;
		private String reason;
		private int authorisation;
		private int isPresent;
		private int shift;
		private String attendanceStr;
		private int presentCount;
		private int absentCount;
		private int excusedCount;
		private int salary;

		public int getId() {
			return id;
		}

		public String getEmployeeId() {
			return employeeId;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getSurName() {
			return surName;
		}

		public String getCheckInTime() {
			return checkInTime;
		}

		public String getCheckInTimeHHMM() {
			if (checkInTime.equals(""))
				return null;
			else if (checkInTime.length() == 5)
				return checkInTime;
			LocalDateTime now = LocalDateTime.parse(checkInTime);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			return now.format(formatter);
		}

		public String getCheckOutTime() {
			return checkOutTime;
		}

		public String getCheckOutTimeHHMM() {
			if (checkOutTime.equals(""))
				return null;
			else if (checkOutTime.length() == 5)
				return checkOutTime;
			LocalDateTime now = LocalDateTime.parse(checkOutTime);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			return now.format(formatter);
		}

		public String getCheckInDate() {
			return checkInDate;
		}

		public String getCheckOutDate() {
			return checkOutDate;
		}

		public String getReason() {
			return reason;
		}

		public int getAuthorisation() {
			return authorisation;
		}

		public int getIsPresent() {
			return isPresent;
		}

		public String getAttendanceStr() {
			return attendanceStr;
		}

		public int getShift() {
			return shift;
		}

		public int getPresentCount() {
			return presentCount;
		}

		public int getAbsentCount() {
			return absentCount;
		}

		public int getExcusedCount() {
			return excusedCount;
		}

		public int getSalary() {
			return salary;
		}

		@Override
		public void readFromDB(ResultSet rs) {

			this.employeeId = Database.readRsString(rs, "employeeId");
			this.id = Database.readRsInt(rs, "Id");
			this.firstName = Database.readRsString(rs, "firstName");
			this.surName = Database.readRsString(rs, "surName");
			this.employeeId = Database.readRsString(rs, "employeeId");
			this.checkInTime = Database.readRsString(rs, "checkInTime");
			this.checkOutTime = Database.readRsString(rs, "checkOutTime");
			this.checkInDate = Database.readRsString(rs, "checkInDate");
			this.checkOutDate = Database.readRsString(rs, "checkOutDate");
			this.reason = Database.readRsString(rs, "reason");
			this.authorisation = Database.readRsInt(rs, "authorisation");
			this.isPresent = Database.readRsInt(rs, "isPresent");
			this.attendanceStr = Database.readRsString(rs, "attendanceStr");
			this.shift = Database.readRsInt(rs, "shift");
			this.presentCount = Database.readRsInt(rs, "presentCount");
			this.absentCount = Database.readRsInt(rs, "absentCount");
			this.excusedCount = Database.readRsInt(rs, "excusedCount");
			this.salary = Database.readRsInt(rs, "salary");
		}
	}

	public static class Report implements Database.OrderOnEntity {

		private String hotelId;
		private String billNo;
		private String orderId;
		private String orderDate;
		private int state;
		private BigDecimal foodBill;
		private BigDecimal barBill;
		private BigDecimal foodDiscount;
		private BigDecimal barDiscount;
		private BigDecimal total;
		private BigDecimal serviceCharge;
		private BigDecimal serviceTax;
		private BigDecimal gst;
		private BigDecimal VATFood;
		private BigDecimal VATBar;
		private BigDecimal sbCess;
		private BigDecimal kkCess;
		private BigDecimal tip;
		private BigDecimal cashPayment;
		private BigDecimal cardPayment;
		private BigDecimal appPayment;
		private BigDecimal inhouseSales;
		private BigDecimal homeDeliverySales;
		private BigDecimal takeAwaySales;
		private String cardType;
		private int inhouse;
		private int cover;
		private int tableId;
		private int checks;
		private String discountName;
		private int orderCount;
		private int printCount;
		private int reprints;
		private int loyaltyAmount;
		private int complimentary;
		private String section;
		private BigDecimal grossSale;
		private BigDecimal nc;
		private int takeAwayType;

		@Override
		public void readFromDB(ResultSet rs) {
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.billNo = Database.readRsString(rs, "billNo");
			this.orderId = Database.readRsString(rs, "orderId");
			this.orderDate = Database.readRsString(rs, "orderDate");
			this.state = Database.readRsInt(rs, "state");
			this.foodBill = Database.readRsBigDecimal(rs, "foodBill");
			this.barBill = Database.readRsBigDecimal(rs, "barBill");
			this.foodDiscount = Database.readRsBigDecimal(rs, "foodDiscount");
			this.barDiscount = Database.readRsBigDecimal(rs, "barDiscount");
			this.total = Database.readRsBigDecimal(rs, "total");
			this.serviceCharge = Database.readRsBigDecimal(rs, "serviceCharge");
			this.serviceTax = Database.readRsBigDecimal(rs, "serviceTax");
			this.gst = Database.readRsBigDecimal(rs, "gst");
			this.VATFood = Database.readRsBigDecimal(rs, "VATFOOD");
			this.VATBar = Database.readRsBigDecimal(rs, "VATBAR");
			this.sbCess = Database.readRsBigDecimal(rs, "sbCess");
			this.kkCess = Database.readRsBigDecimal(rs, "kkCess");
			this.tip = Database.readRsBigDecimal(rs, "tip");
			this.cashPayment = Database.readRsBigDecimal(rs, "cashPayment");
			this.cardPayment = Database.readRsBigDecimal(rs, "cardPayment");
			this.appPayment = Database.readRsBigDecimal(rs, "appPayment");
			this.inhouseSales = Database.readRsBigDecimal(rs, "inhouse");
			this.homeDeliverySales = Database.readRsBigDecimal(rs, "homeDelivery");
			this.takeAwaySales = Database.readRsBigDecimal(rs, "takeAway");
			this.orderCount = Database.readRsInt(rs, "orderCount");
			this.printCount = Database.readRsInt(rs, "printCount");
			this.reprints = Database.readRsInt(rs, "reprints");
			this.inhouse = Database.readRsInt(rs, "inhouse");
			this.cover = Database.readRsInt(rs, "cover");
			this.tableId = Database.readRsInt(rs, "tableId");
			this.checks = Database.readRsInt(rs, "checks");
			this.discountName = Database.readRsString(rs, "discountName");
			this.cardType = Database.readRsString(rs, "cardType");
			this.loyaltyAmount = Database.readRsInt(rs, "loyalty");
			this.complimentary = Database.readRsInt(rs, "complimentary");
			this.section = Database.readRsString(rs, "section");
			this.grossSale = Database.readRsBigDecimal(rs, "grossTotal");
			this.nc = Database.readRsBigDecimal(rs, "nc");
			this.takeAwayType = Database.readRsInt(rs, "takeAwayType");
		}

		public String getHotelId() {
			return hotelId;
		}

		public String getBillNo() {
			return billNo;
		}

		public String getOrderId() {
			return orderId;
		}

		public String getOrderDate() {
			return orderDate;
		}

		public int getState() {
			return state;
		}

		public BigDecimal getNC() {
			return nc;
		}

		public BigDecimal getGrossSale() {
			return grossSale;
		}

		public BigDecimal getFoodBill() {
			return foodBill;
		}

		public BigDecimal getBarBill() {
			return barBill;
		}

		public BigDecimal getTotalBill() {
			return barBill.add(foodBill);
		}

		public BigDecimal getFoodDiscount() {
			return foodDiscount;
		}

		public BigDecimal getBarDiscount() {
			return barDiscount;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public BigDecimal getServiceCharge() {
			return serviceCharge;
		}

		public BigDecimal getServiceTax() {
			return serviceTax;
		}

		public BigDecimal getGST() {
			return gst;
		}

		public BigDecimal getVATFood() {
			return VATFood;
		}

		public BigDecimal getVATBar() {
			return VATBar;
		}

		public BigDecimal getSbCess() {
			return sbCess;
		}

		public BigDecimal getKkCess() {
			return kkCess;
		}

		public BigDecimal getTotalTax() {
			return gst.add(serviceCharge).add(serviceTax).add(VATFood).add(VATBar).add(sbCess).add(kkCess);
		}

		public BigDecimal getTip() {
			return tip;
		}

		public BigDecimal getCashPayment() {
			return cashPayment;
		}

		public BigDecimal getCardPayment() {
			return cardPayment;
		}

		public BigDecimal getTotalPayment() {
			return cardPayment.add(cashPayment).add(appPayment);
		}

		public BigDecimal getAppPayment() {
			return appPayment;
		}

		public BigDecimal getInhouseSales() {
			return inhouseSales;
		}

		public BigDecimal getHomeDeliverySales() {
			return homeDeliverySales;
		}

		public BigDecimal getTakeAwaySales() {
			return takeAwaySales;
		}

		public int getOrderCount() {
			return orderCount;
		}

		public int getPrintCount() {
			return printCount;
		}

		public int getReprints() {
			return reprints;
		}

		public int getInhouse() {
			return inhouse;
		}

		public int getCover() {
			return cover;
		}

		public int getTableId() {
			return tableId;
		}

		public int getChecks() {
			return checks;
		}

		public String getDiscountName() {
			return discountName;
		}

		public String getCardType() {
			return cardType;
		}

		public int getLoyaltyAmount() {
			return loyaltyAmount;
		}

		public int getComplimentary() {
			return complimentary;
		}

		public String getSection() {
			return section;
		}

		public int getTakeAwayType() {
			return takeAwayType;
		}
	}

	public static class Bank implements Database.OrderOnEntity {

		private String hotelId;
		private String accountNumber;
		private String bankName;
		private String accountName;
		private BigDecimal balance;

		@Override
		public void readFromDB(ResultSet rs) {

			this.hotelId = Database.readRsString(rs, "hotelId");
			this.accountNumber = Database.readRsString(rs, "accountNumber");
			this.bankName = Database.readRsString(rs, "bankName");
			this.accountName = Database.readRsString(rs, "accountName");
			this.balance = Database.readRsBigDecimal(rs, "balance");
		}

		public String getHotelId() {
			return hotelId;
		}

		public String getAccountNumber() {
			return accountNumber;
		}

		public String getBankName() {
			return bankName;
		}

		public String getAccountName() {
			return accountName;
		}

		public BigDecimal getBalance() {
			return balance;
		}
	}

	public static class Expense implements Database.OrderOnEntity {

		private int id;
		private String type;
		private String date;
		private BigDecimal amount;
		private String userId;
		private String payee;
		private String memo;
		private int chequeNumber;
		private String accountName;
		private String paymentType;
		private String hotelId;
		private String employeeId;
		private int salaryMonth;
		private int salary;
		private int bonus;
		private String sku;
		private int quantity;
		private String serviceType;
		/*
		 * c = create new stock r = read/used while cooking u = update quanity d =
		 * deleted/remove stock
		 */
		private String crud;

		@Override
		public void readFromDB(ResultSet rs) {

			this.id = Database.readRsInt(rs, "id");
			this.accountName = Database.readRsString(rs, "accountName");
			this.amount = Database.readRsBigDecimal(rs, "amount");
			this.chequeNumber = Database.readRsInt(rs, "chequeNo");
			this.date = Database.readRsString(rs, "serviceDate");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.memo = Database.readRsString(rs, "memo");
			this.payee = Database.readRsString(rs, "payee");
			this.type = Database.readRsString(rs, "type");
			this.userId = Database.readRsString(rs, "userId");
			this.employeeId = Database.readRsString(rs, "employeeId");
			this.salary = Database.readRsInt(rs, "salary");
			this.salaryMonth = Database.readRsInt(rs, "salaryMonth");
			this.bonus = Database.readRsInt(rs, "bonus");
			this.crud = Database.readRsString(rs, "crud");
			this.sku = Database.readRsString(rs, "sku");
			this.quantity = Database.readRsInt(rs, "quantity");
			this.serviceType = Database.readRsString(rs, "serviceType");
			this.paymentType = Database.readRsString(rs, "paymentType");
		}

		public int getId() {
			return id;
		}
		
		public String getType() {
			return type;
		}

		public String getDate() {
			return date;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public String getUserId() {
			return userId;
		}

		public String getPayee() {
			return payee;
		}

		public String getMemo() {
			return memo;
		}

		public int getChequeNumber() {
			return chequeNumber;
		}

		public String getAccountName() {
			return accountName;
		}

		public String getPaymentType() {
			return paymentType;
		}

		public String getHotelId() {
			return hotelId;
		}

		public String getEmployeeId() {
			return employeeId;
		}

		public int getSalaryMonth() {
			return salaryMonth;
		}

		public int getSalary() {
			return salary;
		}

		public int getBonus() {
			return bonus;
		}

		public String getCrud() {
			return crud;
		}

		public String getSku() {
			return sku;
		}

		public int getQuantity() {
			return quantity;
		}

		public String getServiceType() {
			return serviceType;
		}
	}

	public static class ServiceLog implements Database.OrderOnEntity {

		private String id;
		private String hotelId;
		private String serviceDate;
		private String serviceType;
		private String startTimeStamp;
		private String endTimeStamp;
		private int isCurrent;
		private int cashInHand;

		@Override
		public void readFromDB(ResultSet rs) {

			this.id = Database.readRsString(rs, "id");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.serviceDate = Database.readRsString(rs, "serviceDate");
			this.serviceType = Database.readRsString(rs, "serviceType");
			this.startTimeStamp = Database.readRsString(rs, "startTimeStamp");
			this.endTimeStamp = Database.readRsString(rs, "endTimeStamp");
			this.isCurrent = Database.readRsInt(rs, "isCurrent");
			this.cashInHand = Database.readRsInt(rs, "cashInHand");
		}

		public String getId() {
			return id;
		}
		
		public String getHotelId() {
			return hotelId;
		}

		public String getServiceDate() {
			return serviceDate;
		}

		public String getServiceType() {
			return serviceType;
		}

		public String getStartTimeStamp() {
			return startTimeStamp;
		}

		public String getEndTimeStamp() {
			return endTimeStamp;
		}

		public int getIsCurrent() {
			return isCurrent;
		}

		public int getCashInHand() {
			return cashInHand;
		}
	}

	public static class TotalRevenue implements Database.OrderOnEntity {

		private String hotelId;
		private String serviceType;
		private String serviceDate;
		private BigDecimal cash;
		private BigDecimal card;
		private BigDecimal total;
		private BigDecimal visa;
		private BigDecimal mastercard;
		private BigDecimal maestro;
		private BigDecimal amex;
		private BigDecimal others;
		private BigDecimal mswipe;
		private BigDecimal rupay;
		private BigDecimal difference;
		private String reason;
		private String clearance;

		@Override
		public void readFromDB(ResultSet rs) {

			this.hotelId = Database.readRsString(rs, "hotelId");
			this.serviceDate = Database.readRsString(rs, "serviceDate");
			this.serviceType = Database.readRsString(rs, "serviceType");
			this.cash = Database.readRsBigDecimal(rs, "cash");
			this.card = Database.readRsBigDecimal(rs, "card");
			this.total = Database.readRsBigDecimal(rs, "total");
			this.visa = Database.readRsBigDecimal(rs, "visa");
			this.mastercard = Database.readRsBigDecimal(rs, "mastercard");
			this.maestro = Database.readRsBigDecimal(rs, "maestro");
			this.amex = Database.readRsBigDecimal(rs, "amex");
			this.rupay = Database.readRsBigDecimal(rs, "rupay");
			this.others = Database.readRsBigDecimal(rs, "others");
			this.mswipe = Database.readRsBigDecimal(rs, "mswipe");
			this.difference = Database.readRsBigDecimal(rs, "difference");
			this.reason = Database.readRsString(rs, "reason");
			this.clearance = Database.readRsString(rs, "clearance");
		}

		public String getHotelId() {
			return hotelId;
		}

		public String getServiceType() {
			return serviceType;
		}

		public String getServiceDate() {
			return serviceDate;
		}

		public BigDecimal getCash() {
			return cash;
		}

		public BigDecimal getCard() {
			return card;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public BigDecimal getVisa() {
			return visa;
		}

		public BigDecimal getMastercard() {
			return mastercard;
		}

		public BigDecimal getMaestro() {
			return maestro;
		}

		public BigDecimal getAmex() {
			return amex;
		}

		public BigDecimal getOthers() {
			return others;
		}

		public BigDecimal getMSwipe() {
			return mswipe;
		}

		public BigDecimal getRupay() {
			return rupay;
		}

		public BigDecimal getDifference() {
			return difference;
		}

		public String getReason() {
			return reason;
		}

		public String getClearance() {
			return clearance;
		}
	}

	public static class MPNotification implements Database.OrderOnEntity {

		private String hotelId;
		private int checkoutOrders;
		private int outOfStock;

		@Override
		public void readFromDB(ResultSet rs) {

			this.hotelId = Database.readRsString(rs, "hotelId");
			this.checkoutOrders = Database.readRsInt(rs, "checkoutOrders");
			this.outOfStock = Database.readRsInt(rs, "outOfStock");
		}

		public String getHotelId() {
			return hotelId;
		}

		public int getCheckoutOrders() {
			return checkoutOrders;
		}

		public int getOutOfStock() {
			return outOfStock;
		}
	}

	public static class ServerLog implements Database.OrderOnEntity {

		private String hotelId;
		private String updateTime;
		private int status;

		public String getHotelId() {
			return hotelId;
		}

		public String getUpdateTime() {
			return updateTime;
		}

		public int getStatus() {
			return status;
		}

		@Override
		public void readFromDB(ResultSet rs) {

			hotelId = Database.readRsString(rs, "hotelId");
			updateTime = Database.readRsString(rs, "lastUpdateTime");
			this.status = Database.readRsInt(rs, "status");
		}

	}

	public static class Specifications implements Database.OrderOnEntity {

		private String specification;
		private String category;
		private int type;

		public String getSpecification() {
			return specification;
		}
		public String getCategory() {
			return category;
		}
		public int getType() {
			return type;
		}

		@Override
		public void readFromDB(ResultSet rs) {

			specification = Database.readRsString(rs, "specification");
			category = Database.readRsString(rs, "category");
			type = Database.readRsInt(rs, "type");
		}
	}

	public static class AddOn implements Database.OrderOnEntity {

		private String name;
		private String menuId;
		private BigDecimal inHouseRate;
		private BigDecimal deliveryRate;
		private BigDecimal onlineRate;
		private int id;

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getMenuId() {
			return menuId;
		}

		public BigDecimal getInHouseRate() {
			return inHouseRate;
		}

		public BigDecimal getDeliveryRate() {
			return deliveryRate;
		}

		public BigDecimal getOnlineRate() {
			return onlineRate;
		}

		@Override
		public void readFromDB(ResultSet rs) {

			this.id = Database.readRsInt(rs, "Id");
			this.name = Database.readRsString(rs, "name");
			this.menuId = Database.readRsString(rs, "menuId");
			this.inHouseRate = Database.readRsBigDecimal(rs, "inHouseRate");
			this.deliveryRate = Database.readRsBigDecimal(rs, "deliveryRate");
			this.onlineRate = Database.readRsBigDecimal(rs, "onlineRate");
		}
	}

	public static class LoyaltyOffer implements Database.OrderOnEntity {

		private int id;
		private String name;
		private int offerType;
		private int points;
		private String offerValue;
		private int offerQuantity;
		private int usageLimit;
		private String hasUsageLimit;
		private int minBill;
		private String userType;
		private String validCollections;
		private String status;
		private String startDate;
		private String expiryDate;
		private String hotelId;
		private String chainId;

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getOfferType() {
			return offerType;
		}

		public String getOfferTypeView() {
			if (offerType == CASH_LOYALTY_OFFER)
				return "CASH";
			else if (offerType == PERCENTAGE_LOYALTY_OFFER)
				return "PERCENTAGE";
			else
				return "PRODUCT";
		}

		public int getPoints() {
			return points;
		}

		public String getOfferValue() {
			return offerValue;
		}

		public int getOfferQuantity() {
			return offerQuantity;
		}

		public int getUsageLimit() {
			return usageLimit;
		}

		public Boolean gethasUsageLimit() {
			return Boolean.valueOf(hasUsageLimit);
		}

		public int getMinBill() {
			return minBill;
		}

		public String getUserType() {
			return userType;
		}

		public String[] getValidCollections() {
			return validCollections.split(",");
		}

		public Boolean getHasCollections() {
			return validCollections.length() > 0;
		}

		public Boolean getStatus() {
			return Boolean.valueOf(status);
		}

		public String getStartDate() {
			return startDate;
		}

		public String getExpiryDate() {
			return expiryDate;
		}

		public String getHotelId() {
			return hotelId;
		}

		public String getChainId() {
			return chainId;
		}

		@Override
		public void readFromDB(ResultSet rs) {

			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.offerType = Database.readRsInt(rs, "offerType");
			this.points = Database.readRsInt(rs, "points");
			this.offerValue = Database.readRsString(rs, "offerValue");
			this.offerQuantity = Database.readRsInt(rs, "offerQuantity");
			this.usageLimit = Database.readRsInt(rs, "usageLimit");
			this.hasUsageLimit = Database.readRsString(rs, "hasUsageLimit");
			this.minBill = Database.readRsInt(rs, "minBill");
			this.userType = Database.readRsString(rs, "userType");
			this.validCollections = Database.readRsString(rs, "validCollections");
			this.status = Database.readRsString(rs, "status");
			this.startDate = Database.readRsString(rs, "startDate");
			this.expiryDate = Database.readRsString(rs, "expiryDate");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.chainId = Database.readRsString(rs, "chainId");
		}
	}

	public static class LoyaltySetting implements Database.OrderOnEntity {

		private int id;
		private String userType;
		private int requiredPoints;
		private BigDecimal pointToRupee;

		public int getId() {
			return id;
		}

		public String getUserType() {
			return userType;
		}

		public int getRequiredPoints() {
			return requiredPoints;
		}

		public BigDecimal getPointToRupee() {
			return pointToRupee;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.id = Database.readRsInt(rs, "id");
			this.userType = Database.readRsString(rs, "userType");
			this.requiredPoints = Database.readRsInt(rs, "requiredPoints");
			this.pointToRupee = Database.readRsBigDecimal(rs, "pointToRupee");
		}
	}

	// ItemWiseReport-ap (edited)
	public static class TransactionHistory implements Database.OrderOnEntity {
		private String trType;
		private String trDetail;
		private String trAccountName;
		private String paymentType;
		private BigDecimal amount;
		private BigDecimal balance;
		private String trDate;
		private String serviceDate;
		private String userId;
		private String authoriser;


		public String getTrType() {
			return trType;
		}

		public String getTrDetail() {
			return trDetail;
		}

		public String getTrAccountName() {
			return trAccountName;
		}

		public String getPaymentType() {
			return paymentType;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public BigDecimal getBalance() {
			return balance;
		}

		public String getTrDate() {
			return trDate;
		}

		public String getServiceDate() {
			return serviceDate;
		}

		public String getUserId() {
			return userId;
		}

		public String getAuthoriser() {
			return authoriser;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.trType = Database.readRsString(rs, "trType");
			this.trDetail = Database.readRsString(rs, "trDetail");
			this.amount = Database.readRsBigDecimal(rs, "amount");
			this.balance = Database.readRsBigDecimal(rs, "balance");
			this.trDate = Database.readRsString(rs, "trDate");
			this.serviceDate = Database.readRsString(rs, "serviceDate");
			this.userId = Database.readRsString(rs, "userId");
			this.authoriser = Database.readRsString(rs, "authoriser");
			this.trAccountName = Database.readRsString(rs, "trAccountName");
			this.paymentType = Database.readRsString(rs, "paymentType");
		}
	}
	
	public static class Reservation implements OrderOnEntity {

		private int reservationId;
		private int customerId;
		private String customerName;
		private String mobileNumber;
		private String customerAddress;
		private String allergyInfo;
		private String isPriorityCust;
		private int maleCount;
		private int femaleCount;
		private int childrenCount;
		private int type;
		private int state;
		private String bookingTime;
		private String bookingDate;
		private String timeStamp;
		
		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.reservationId = Database.readRsInt(rs, "id");
			this.customerId = Database.readRsInt(rs, "customerId");
			this.customerAddress = Database.readRsString(rs, "customerAddress");
			this.customerName = Database.readRsString(rs, "customerName");
			this.mobileNumber = Database.readRsString(rs, "mobileNumber");
			this.allergyInfo = Database.readRsString(rs, "allergyInfo");
			this.isPriorityCust = Database.readRsString(rs, "isPriorityCust");
			this.maleCount = Database.readRsInt(rs, "maleCount");
			this.femaleCount = Database.readRsInt(rs, "femaleCount");
			this.childrenCount = Database.readRsInt(rs, "childrenCount");
			this.type = Database.readRsInt(rs, "type");
			this.state = Database.readRsInt(rs, "state");
			this.bookingTime = Database.readRsString(rs, "bookingTime");
			this.bookingDate = Database.readRsString(rs, "bookingDate");
			this.timeStamp = Database.readRsString(rs, "timeStamp");
		}

		public int getReservationId() {
			return reservationId;
		}

		public int getCustomerId() {
			return customerId;
		}

		public String getCustomerName() {
			return customerName;
		}

		public String getMobileNumber() {
			return mobileNumber;
		}

		public String getCustomerAddress() {
			return customerAddress;
		}

		public String getAllergyInfo() {
			return allergyInfo;
		}

		public Boolean getIsPriorityCust() {
			return Boolean.valueOf(isPriorityCust);
		}

		public String getBookingTime() {
			return bookingTime;
		}

		public String getBookingDate() {
			return bookingDate;
		}

		public int getMaleCount() {
			return maleCount;
		}

		public int getFemaleCount() {
			return femaleCount;
		}

		public int getChildrenCount() {
			return childrenCount;
		}

		public int getCovers() {
			return maleCount+femaleCount+childrenCount;
		}

		public int getType() {
			return type;
		}

		public int getState() {
			return state;
		}

		public String getTimeStamp() {
			return timeStamp;
		}
	}
	
	public static class Charge implements OrderOnEntity {
		
		private int id;
		private String name;
		private String type;
		private BigDecimal value;
		private String isActive;
		private String applicableOn;
		private String isAlwaysApplicable;
		private BigDecimal minBillAmount;
		private String hasTierWiseValue;
		private String taxesOnCharge;
		
		
		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public BigDecimal getValue() {
			return value;
		}

		public Boolean isActive() {
			return Boolean.valueOf(isActive);
		}

		public String getApplicableOn() {
			return applicableOn;
		}

		public Boolean isAlwaysApplicable() {
			return Boolean.valueOf(isAlwaysApplicable);
		}

		public BigDecimal getMinBillAmount() {
			return minBillAmount;
		}

		public Boolean hasTierWiseValue() {
			return Boolean.valueOf(hasTierWiseValue);
		}

		public JSONObject getTaxesOnCharge() throws JSONException {
			return new JSONObject(taxesOnCharge);
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.value = Database.readRsBigDecimal(rs, "value");
			this.type = Database.readRsString(rs, "type");
			this.isActive = Database.readRsString(rs, "isActive");
			this.applicableOn = Database.readRsString(rs, "applicatbleOn");
			this.isAlwaysApplicable = Database.readRsString(rs, "isAlwaysApplicable");
			this.minBillAmount = Database.readRsBigDecimal(rs, "minBillAmount");
			this.hasTierWiseValue = Database.readRsString(rs, "hasTierWiseValue");
			this.taxesOnCharge = Database.readRsString(rs, "taxesOnCharge");
		}
	}
	
	public static class Tax implements OrderOnEntity {

		private int id;
		private String name;
		private String type;
		private BigDecimal value;
		private String isActive;
		
		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public BigDecimal getValue() {
			return value;
		}

		public String getIsActive() {
			return isActive;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.value = Database.readRsBigDecimal(rs, "value");
			this.type = Database.readRsString(rs, "type");
			this.isActive = Database.readRsString(rs, "isActive");
		}
	}
	
	public static class Tier implements OrderOnEntity {
		
		private int id;
		private BigDecimal value;
		private BigDecimal minBillAmount;
		private String isChargeAlwaysApplicable;
		
		public int getId() {
			return id;
		}

		public BigDecimal getValue() {
			return value;
		}

		public BigDecimal getMinBillAmount() {
			return minBillAmount;
		}

		public String getIsChargeAlwaysApplicable() {
			return isChargeAlwaysApplicable;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.value = Database.readRsBigDecimal(rs, "value");
			this.minBillAmount = Database.readRsBigDecimal(rs, "minBillAmount");
			this.isChargeAlwaysApplicable = Database.readRsString(rs, "chargeAlwaysApplicable");
		}	
	}
	
	public static class Flag implements OrderOnEntity {
		
		private int id;
		private String name;
		private String groupId;
		
		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getGroupId() {
			return groupId;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.groupId = Database.readRsString(rs, "groupId");
		}	
	}

	public static class Collection implements Database.OrderOnEntity {
		
		private int id;
		private String name;
		private int collectionOrder;
		private String hasSubCollection;
		private String isActive;
		private int scheduleId;
		
		public String getName() {
			return name;
		}
		
		public int getId() {
			return id;
		}

		public int getCollectionOrder() {
			return collectionOrder;
		}

		public String getHasSubCollection() {
			return hasSubCollection;
		}

		public String getIsActive() {
			return isActive;
		}

		public int getScheduleId() {
			return scheduleId;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.name = Database.readRsString(rs, "name");
			this.id = Database.readRsInt(rs, "id");
			this.collectionOrder = Database.readRsInt(rs, "collectionOrder");
			this.hasSubCollection = Database.readRsString(rs, "hasSubCollection");
			this.isActive = Database.readRsString(rs, "isActive");
			this.scheduleId = Database.readRsInt(rs, "scheduleId");
		}
	}
	
	public static class Schedule implements OrderOnEntity {

		private int id;
		private String name;
		private String days;
		private String timeSlots;
		
		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getDays() {
			return days;
		}

		public String getTimeSlots() {
			return timeSlots;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.days = Database.readRsString(rs, "days");
			this.timeSlots = Database.readRsString(rs, "timeSlots"); 
		}
	}
	
	public static class SubCollection implements OrderOnEntity {
		
		private int id;
		private String name;
		private int subCollectionOrder;
		private String collection;
		
		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getSubCollectionOrder() {
			return subCollectionOrder;
		}

		public String getCollection() {
			return collection;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.subCollectionOrder = Database.readRsInt(rs, "subCollectionOrder");
			this.collection = Database.readRsString(rs, "collection");
		}
	}
	
	public static class Group implements OrderOnEntity {

		private int id;
		private String itemIds;
		private String name;
		private String description;
		private int max;
		private int min;
		private String isActive;

		public int getId() {
			return id;
		}

		public JSONObject getItemIds() throws JSONException {
			return new JSONObject(itemIds);
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public int getMax() {
			return max;
		}

		public int getMin() {
			return min;
		}

		public Boolean getIsActive() {
			return Boolean.valueOf(isActive);
		}
		
		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.itemIds = Database.readRsString(rs, "itemIds");
			this.description = Database.readRsString(rs, "description");
			this.max = Database.readRsInt(rs, "max");
			this.min = Database.readRsInt(rs, "min");
			this.isActive = Database.readRsString(rs, "isActive");
		}
	}
	
	public static class Flags implements OrderOnEntity {

		private int id;
		private String name;
		private int groupId;
		
		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getGroupId() {
			return groupId;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.groupId = Database.readRsInt(rs, "groupId");
		}
	}

	// User Authentication


	public User validUser(String hotelId, String userId, String password) {

		User user = this.getUserById(hotelId, userId);
		if(user == null) {
			return null;
		}
		EncryptDecryptString eds = new EncryptDecryptString();
		
		if(eds.isExpectedPassword(password.toCharArray(), user.getSalt(), user.getPasswd())) {
			return user;
		}
		return null;
	}


	private String setAuthToken(String userId, String hotelId) {

		String sql = "UPDATE Users SET authToken = ABS(RANDOM() % 10000000000), timeStamp = '"
				+ LocalDateTime.now().toString() + "' WHERE userId = '" + userId + "'AND hotelId = '" + hotelId + "';";
		db.executeUpdate(sql, hotelId, false);

		sql = "SELECT authToken FROM Users WHERE userId = '" + userId + "'AND hotelId = '" + hotelId + "';";

		User user = db.getOneRecord(sql, User.class, hotelId);
		if (user != null) {
			return user.getAuthToken();
		}
		return null;
	}


	public String validMPUser(String hotelId, String userId, String passwd) {

		if (this.validUser(hotelId, userId, passwd)!=null) {

			return setAuthToken(userId, hotelId);
		}
		String sql = "UPDATE Users SET authToken = 0, timeStamp = NULL WHERE userId = '" + userId + "'AND hotelId = '"
				+ hotelId + "';";
		db.executeUpdate(sql, hotelId, false);
		return null;
	}


	public Boolean validOnlinePlatform(String hotelId, String userId, String passwd) {
		if (this.validUser(hotelId, userId, passwd)!=null) {
			return true;
		}
		return false;
	}


	public boolean validateAccess1(String hotelId, String userId, String passwd) {
		User user = this.validUser(hotelId, userId, passwd);

		if(user == null)
			return false;
		if ((user.getUserType().equals(UserType.ADMINISTRATOR.getValue())
						|| user.getUserType().equals(UserType.OWNER.getValue())
						|| user.getUserType().equals(UserType.MANAGER.getValue()))) {

			return true;
		}
		return false;
	}

	public boolean validateOwner(String hotelId, String userId, String passwd) {
		User user = this.validUser(hotelId, userId, passwd);

		if(user == null)
			return false;
		if ((user.getUserType().equals(UserType.OWNER.getValue()))) {

			return true;
		}
		return false;
	}

	public boolean validateSecretUser(String hotelId, String userId, String passwd) {
		User user = this.validUser(hotelId, userId, passwd);

		if(user == null)
			return false;
		if ((user.getUserType().equals(UserType.SECRET.getValue()))) {

			return true;
		}
		return false;
	}

	public String validKDSUser(String hotelId, String userId, String passwd) {
		User user = this.validUser(hotelId, userId, passwd);

		if(user == null)
			return null;

		if (user.mUserType == UserType.CHEF.getValue() || user.mUserType == UserType.ADMINISTRATOR.getValue()) 
				return setAuthToken(userId, hotelId);
		
		String sql = "UPDATE Users SET authToken = 0, timeStamp = NULL WHERE userId = '" + userId + "'AND hotelId = '"
				+ hotelId + "';";
		db.executeUpdate(sql, false);
		return null;
	}


	public boolean removeToken(String hotelId, String userId) {

		String sql = "UPDATE Users SET authToken = 0 WHERE userId = '" + userId + "'AND hotelId = '" + hotelId + "';";

		return db.executeUpdate(sql, false);
	}


	public UserType validateToken(String hotelId, String userId, String authToken) {

		String sql = "SELECT authtoken, timeStamp, userType FROM Users WHERE userId = '" + userId + "'AND hotelId = '"
				+ hotelId + "';";
		User user = db.getOneRecord(sql, User.class, hotelId);

		if(hotelId.equals("dn0001"))
			return UserType.getType(user.getUserType());
		
		if (user != null) {

			int hourDiff = LocalDateTime.now().getHour() - LocalDateTime.parse(user.getTimeStamp()).getHour();

			// Check if it been 30 minutes since any activity.
			if (hourDiff == 0 || hourDiff == 1) {
				int offset = LocalDateTime.now().getMinute() - LocalDateTime.parse(user.getTimeStamp()).getMinute();
				offset = offset < 0? offset+60 : offset;
				
				if (offset <= 60 && offset >= 0) {
					if (user.getAuthToken().equals(authToken)) {
						sql = "UPDATE Users SET timeStamp = '" + LocalDateTime.now().toString() + "' WHERE userId = '" + userId
								+ "' AND hotelId = '" + hotelId + "';";
						db.executeUpdate(sql, false);
						return UserType.getType(user.getUserType());
					}
				}
			}
		}
		return UserType.UNAUTHORIZED;
	}

	// -------------------------------End User Validation

	// --------------------------------Hotel
	public Hotel getHotelById(String hotelId) {
		String sql = "SELECT * FROM Hotel WHERE hotelId='" + hotelId + "'";
		return db.getOneRecord(sql, Hotel.class, hotelId);
	}

	public boolean updateHotelFlags(String hotelId, String flags) {
		String sql = "UPDATE Hotel SET taxFlags='" + flags + "' WHERE hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	// -------------------------------Collections
	
	public ArrayList<Collection> getCollections(String hotelId) {
		String sql = "SELECT * FROM Collections  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, Collection.class, hotelId);
	}
	
	public ArrayList<Collection> getActiveCollections(String hotelId) {
		String sql = "SELECT * FROM Collections  WHERE hotelId='" + hotelId + "' AND isActive = 'true'";
		return db.getRecords(sql, Collection.class, hotelId);
	}

	public boolean addCollection(String hotelId, String name, String image) {

		String sql = "INSERT INTO Collections (hotelId, name, image) VALUES('" + escapeString(hotelId) + "', '"
				+ escapeString(name) + "', '" + (image.equals("No image") ? "" : "1") + "');";
		return db.executeUpdate(sql, true);
	}


	public Boolean collectionExists(String hotelId, String collectionName) {
		Collection collection = getCollectionByName(hotelId, collectionName);
		if (collection != null) {
			return true;
		}
		return false;
	}

	public Collection getCollectionByName(String hotelId, String collectionName) {
		String sql = "SELECT * FROM Collections WHERE name='" + escapeString(collectionName) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Collection.class, hotelId);
	}

	public boolean deleteCollection(String hotelId, String collectionName) {
		String sql = "DELETE FROM Collections WHERE name = '" + collectionName + "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}
	
	// --------------------------------Schedule
	
	public ArrayList<Schedule> getSchedules(String hotelId) {
		String sql = "SELECT * FROM Schedules  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, Schedule.class, hotelId);
	}

	public boolean addSchedule(String hotelId, String name, String days, String timeSlots) {

		String sql = "INSERT INTO Schedules (hotelId, name, days, timeSlots) VALUES('" + escapeString(hotelId) + "', '"
				+ escapeString(name) + "', '" + days + "', '" + timeSlots + "');";
		return db.executeUpdate(sql, true);
	}

	public Schedule getScheduleById(String hotelId, int scheduleId) {
		String sql = "SELECT * FROM Schedules WHERE id=" + scheduleId + " AND hotelId='" + escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Schedule.class, hotelId);
	}

	public boolean deleteSchedule(String hotelId, int scheduleId) {
		String sql = "DELETE FROM Schedules WHERE id = " + scheduleId + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}
	
	public JSONObject getSchedulesForCollection(String hotelId, int scheduleId) {
		Schedule schedule = this.getScheduleById(hotelId, scheduleId);
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("scheduleName", schedule.getName());
			outObj.put("scheduleDays", schedule.getDays());
			outObj.put("scheduleTimeSlots", schedule.getTimeSlots());
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return outObj;
	}

	// -------------------------------SubCollections
	
	public ArrayList<SubCollection> getSubCollections(String hotelId) {
		String sql = "SELECT * FROM SubCollections  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, SubCollection.class, hotelId);
	}
	
	public ArrayList<SubCollection> getActiveSubCollections(String hotelId) {
		String sql = "SELECT * FROM SubCollections  WHERE hotelId='" + hotelId + "' AND isActive = 'true'";
		return db.getRecords(sql, SubCollection.class, hotelId);
	}

	public boolean addSubCollection(String hotelId, String name, int order, String collection) {

		String sql = "INSERT INTO SubCollections (hotelId, name, subCollectionOrder, collection) VALUES('" + escapeString(hotelId) + "', '"
				+ escapeString(name) + "', " + order + ", '" + collection + "');";
		return db.executeUpdate(sql, true);
	}

	public SubCollection getSubCollectionByName(String hotelId, String subCollectionName) {
		String sql = "SELECT * FROM SubCollections WHERE name='" + escapeString(subCollectionName) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, SubCollection.class, hotelId);
	}

	public boolean deleteSubCollection(String hotelId, int id) {
		String sql = "DELETE FROM SubCollections WHERE id = " + id + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	// -------------------------------Taxes
	
	public ArrayList<Tax> getTaxes(String hotelId) {
		String sql = "SELECT * FROM Taxes  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, Tax.class, hotelId);
	}
	
	public ArrayList<Tax> getActiveTaxes(String hotelId) {
		String sql = "SELECT * FROM Taxes  WHERE hotelId='" + hotelId + "' AND isActive = 'true'";
		return db.getRecords(sql, Tax.class, hotelId);
	}

	public boolean addTax(String hotelId, String name, BigDecimal value, String type, Boolean isActive) {

		String sql = "INSERT INTO Taxes (hotelId, name, value, type, isActive) VALUES('" + escapeString(hotelId) + "', '"
				+ escapeString(name) + "', " + value + ", '" + type + "', '" + isActive + "');";
		return db.executeUpdate(sql, true);
	}

	public Tax getTaxByName(String hotelId, String taxName) {
		String sql = "SELECT * FROM Taxes WHERE name='" + escapeString(taxName) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Tax.class, hotelId);
	}

	public boolean deleteTax(String hotelId, int id) {
		String sql = "DELETE FROM Taxes WHERE id = " + id + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	// -------------------------------Charges
	
	public ArrayList<Charge> getCharges(String hotelId) {
		String sql = "SELECT * FROM Charges  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, Charge.class, hotelId);
	}
	
	public ArrayList<Charge> getActiveCharges(String hotelId) {
		String sql = "SELECT * FROM Charges  WHERE hotelId='" + hotelId + "' AND isActive = 'true'";
		return db.getRecords(sql, Charge.class, hotelId);
	}

	public boolean addCharge(String hotelId, String name, BigDecimal value, String type, Boolean isActive,
			String applicableOn, boolean isAlwaysApplicable, BigDecimal minBillAmount, boolean hasTierWiseValues) {

		String sql = "INSERT INTO Charges (hotelId, name, value, type, isActive, applicableOn, isAlwaysApplicable, minBillAmount, hasTierWiseValues"
				+ ", taxesOnCharge) VALUES('" + escapeString(hotelId) + "', '" + escapeString(name) + "', " + value + ", '" + type + "', '" 
				+ isActive + "', '" + applicableOn + "', '" + isAlwaysApplicable + "', " + minBillAmount + ", '" + hasTierWiseValues + "');";
		return db.executeUpdate(sql, true);
	}

	public Charge getChargeByName(String hotelId, String taxName) {
		String sql = "SELECT * FROM Charges WHERE name='" + escapeString(taxName) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Charge.class, hotelId);
	}

	public boolean deleteCharge(String hotelId, int id) {
		String sql = "DELETE FROM Charges WHERE id = " + id + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	// -------------------------------Tiers
	
	public ArrayList<Tier> getTiers(String hotelId) {
		String sql = "SELECT * FROM Tiers  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, Tier.class, hotelId);
	}

	public boolean addTier(String hotelId, BigDecimal value, boolean chargeAlwaysApplicable, BigDecimal minBillAMount) {

		String sql = "INSERT INTO Tiers (hotelId, value, chargeAlwaysApplicable, minBillAmount) VALUES('" + escapeString(hotelId) 
				+ "', " + value + ", '" + chargeAlwaysApplicable + "', " + minBillAMount + ");";
		return db.executeUpdate(sql, true);
	}

	public Tier getTierById(String hotelId, int id) {
		String sql = "SELECT * FROM Tiers WHERE id='" + id + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Tier.class, hotelId);
	}

	public boolean deleteTier(String hotelId, int id) {
		String sql = "DELETE FROM Tiers WHERE id = " + id + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	// -------------------------------Flags
	
	public ArrayList<Flag> getFlags(String hotelId) {
		String sql = "SELECT * FROM Charges  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, Flag.class, hotelId);
	}

	public boolean addFlag(String hotelId, BigDecimal value, boolean chargeAlwaysApplicable, BigDecimal minBillAMount) {

		String sql = "INSERT INTO Flags (hotelId, value, chargeAlwaysApplicable, minBillAmount) VALUES('" + escapeString(hotelId) 
				+ "', " + value + ", '" + chargeAlwaysApplicable + "', " + minBillAMount + ");";
		return db.executeUpdate(sql, true);
	}

	public Flag getFlagById(String hotelId, int id) {
		String sql = "SELECT * FROM Flags WHERE id='" + id + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Flag.class, hotelId);
	}

	public boolean deleteFlag(String hotelId, int id) {
		String sql = "DELETE FROM Flags WHERE id = " + id + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	// --------------------------------MenuItem


	public MenuItem itemExists(String hotelId, String title) {
		String sql = "SELECT * FROM MenuItems WHERE title='" + escapeString(title) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";

		return db.getOneRecord(sql, MenuItem.class, hotelId);
	}

	public boolean isTaxableMenuItem(String hotelId, String menuId) {

		String sql = "SELECT * FROM MenuItems WHERE menuId = '" + escapeString(menuId) + "' AND hotelId='"
				+ escapeString(hotelId) + "' AND isTaxable = 0;";

		return db.hasRecords(sql, hotelId);
	}


	public String addMenuItem(String hotelId, String title, String description, String category, String station,
			String flags, int preparationTime, BigDecimal rate, BigDecimal inhouseRate, BigDecimal onlineRate, BigDecimal costPrice, 
			int vegType, String image, int isTaxable, int incentiveType, int incentive, String shortForm) {

		String menuId = getNextMenuId(hotelId, category);

		String sql = "INSERT INTO MenuItems "
				+ "(hotelId, menuId, title, description, category, station, flags, preparationTime, rate, inhouseRate, onlineRate, costPrice, "
				+ "vegType, img, method, shortForm, state, isTaxable, hasIncentive, incentive) VALUES('"
				+ escapeString(hotelId) + "', '" + escapeString(menuId) + "', '" + escapeString(title) + "', '"
				+ escapeString(description) + "', '" + escapeString(category) + "', '" + escapeString(station) + "', '"
				+ escapeString(flags) + "', '" + Integer.toString(preparationTime) + "', " + rate
				+ ", " + inhouseRate + ", " + onlineRate + ", " + costPrice + ", "
				+ Integer.toString(vegType) + ", '" + (image.equals("No image") ? "" : "1") + "', '', '"
				+ shortForm + "', " + MENUITEM_STATE_AVAILABLE + ", " + isTaxable + ", " + incentiveType + ", " + incentive
				+ ");";

		if (db.executeUpdate(sql, true)) {
			return menuId;
		} else
			return "";
	}

	public MenuItem getMenuById(String hotelId, String menuId) {
		String sql = "SELECT * FROM MenuItems WHERE menuId='" + escapeString(menuId) + "' AND hotelId='" + hotelId
				+ "';";
		return db.getOneRecord(sql, MenuItem.class, hotelId);
	}


	public MenuItem getMenuItemByTitle(String hotelId, String title) {

		String sql = "SELECT * FROM MenuItems WHERE title = '" + escapeString(title) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";

		return db.getOneRecord(sql, MenuItem.class, hotelId);
	}

	public ArrayList<MenuItem> getMenuItemBySearch(String hotelId, String query) {

		query = escapeString(query);
		String sql = "SELECT * FROM MenuItems WHERE title LIKE '%" + query + "%' OR menuId LIKE '%" + query
				+ "%' OR shortForm LIKE '%" + query + "%' OR category LIKE '%" + query + "%' AND hotelId='" + escapeString(hotelId) + "';";

		return db.getRecords(sql, MenuItem.class, hotelId);
	}

	public ArrayList<MenuItem> getMenu(String hotelId) {
		String sql = "SELECT * FROM MenuItems  WHERE hotelId='" + hotelId + "' AND state = " + MENUITEM_STATE_AVAILABLE
				+ ";";
		return db.getRecords(sql, MenuItem.class, hotelId);
	}

	public ArrayList<EntityString> getMenuItems(String hotelId) {
		String sql = "SELECT title AS entityId FROM MenuItems  WHERE hotelId='" + hotelId + "';";
		return db.getRecords(sql, EntityString.class, hotelId);
	}

	public ArrayList<MenuItem> getMenuMP(String hotelId) {
		String sql = "SELECT * FROM MenuItems  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, MenuItem.class, hotelId);
	}

	public String getNextMenuId(String hotelId, String category) {

		Hotel hotel = this.getHotelById(hotelId);
		String sql = "";
		if(!hotel.getIsMenuIcCategorySpecific())
			sql = "SELECT MAX(CAST(menuId AS integer)) AS entityId FROM MenuItems WHERE hotelId='" + hotelId + "'";
		else
			sql = "SELECT MAX(CAST(menuId AS integer)) AS entityId FROM MenuItems WHERE category = '"
					+ category + "' AND hotelId='" + hotelId + "'";

		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		if (entity != null) {
			return Integer.toString((entity.getId() + 1));
		}
		return null;
	}

	public Boolean updateMenuItem(String hotelId, String menuId, String title, String description, String category,
			String station, String flags, int preparationTime, BigDecimal rate, BigDecimal inhouseRate, BigDecimal onlineRate, BigDecimal costPrice, int vegType,
			String image, int isTaxable, int incentiveType, int incentive, String shortForm) {

		String sql = "UPDATE MenuItems SET title = '" + escapeString(title) + "', description = '"
				+ escapeString(description) + "', category = '" + category + "', station = '"
				+ station + "', flags = '" + flags + "', preparationTime = '" + preparationTime
				+ "', rate = " + rate + ", inhouseRate = " + inhouseRate  + ", onlineRate = " + onlineRate 
				+ ", costPrice = " + costPrice 
				+ ", vegType = " + vegType + ", shortForm = '" + shortForm
				+ "', incentive = " + incentive + ", hasIncentive = " + incentiveType
				+ ", img  ='" + (image.equals("No image") ? "" : "1") + "', isTaxable = " + isTaxable
				+ " WHERE hotelId = '" + hotelId + "' AND menuId = '" + menuId + "';";

		return db.executeUpdate(sql, true);
	}


	public Boolean updateMenuItemState(String hotelId, String menuId, int state) {

		String sql = "UPDATE MenuItems SET state = " + Integer.toString(state) + " WHERE hotelId = '"
				+ escapeString(hotelId) + "' AND menuId = '" + escapeString(menuId) + "';";

		return db.executeUpdate(sql, true);
	}

	private String generateShortForm(String title) {
		String[] sf = title.split(" ");
		StringBuilder out = new StringBuilder();

		for (int i = 0; i < sf.length; i++) {
			if (sf[i].length() >= 2)
				out.append(sf[i].substring(0, 2).toUpperCase());
		}
		return out.toString();
	}

	public boolean deleteItem(String hotelId, String menuId) {
		String sql = "DELETE FROM MenuItems WHERE menuId = '" + menuId + "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	public boolean changeMenuItemState(String hotelId, String menuId, int state) {

		String sql = "UPDATE MenuItems SET state = '" + state + "' WHERE hotelId = '" + hotelId + "' AND menuId = '"
				+ menuId + "';";
		return db.executeUpdate(sql, true);
	}

	// -----------------------------User


	public Boolean userExists(String hotelId, String userId) {
		User user = getUserById(hotelId, userId);
		if (user != null) {
			return true;
		}
		return false;
	}


	public Boolean addUser(String hotelId, String userId, String employeeId, int userType, String userPasswd) {
		
		EncryptDecryptString eds = new EncryptDecryptString();
		byte[] salt = eds.getNextSalt();
		byte[] hash = eds.hash(userPasswd.toCharArray(), salt);
		
		String sql = "INSERT INTO Users ('hotelId', 'userId', 'userPasswd', 'employeeId', 'userType', 'authToken', 'timeStamp', 'salt') VALUES ('"
				+ escapeString(hotelId) + "','" + escapeString(userId) + "',?,'"
				+ escapeString(employeeId) + "'," + Integer.toString(userType) + ",NULL,NULL, ? );";

		Connection conn;
		try {
			conn = db.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setBytes(1, hash);
			pstmt.setBytes(2, salt);
			if(pstmt.executeUpdate() > 0) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}


	public JSONObject updateUser(String hotelId, String userId, String oldPassword, String password, int userType) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			EncryptDecryptString eds = new EncryptDecryptString();
			
			if(this.validUser(hotelId, userId, oldPassword)==null) {
				outObj.put("message", "Old Passwords don't match. Please try again.");
				return outObj;
			}
			
			byte[] salt = eds.getNextSalt();
			byte[] hash = eds.hash(password.toCharArray(), salt);
			
			String sql = "UPDATE Users SET userType= " + Integer.toString(userType) + ", userPasswd = ?, salt = ? "
					+ " WHERE userId='" + escapeString(userId) + "' AND hotelId='" + hotelId + "';";
	
			Connection conn;
			conn = db.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setBytes(1, hash);
			pstmt.setBytes(2, salt);
			if(pstmt.executeUpdate() > 0) {
				outObj.put("status", true);
			}else {
				outObj.put("message", "Could not update User. Please contact support");
				return outObj;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}


	public User getUserById(String hotelId, String userId) {
		String sql = "SELECT Users.*, (Employee.firstName || ' ' || Employee.surName) AS name "
				+ "FROM Users, Employee WHERE userId='" + escapeString(userId) + "' AND Users.hotelId='"
				+ escapeString(hotelId) + "' AND Users.employeeId = Employee.employeeId;";
		return db.getOneRecord(sql, User.class, hotelId);
	}

	public User getUserByEmpId(String hotelId, String employeeId) {
		String sql = "SELECT * FROM Users WHERE employeeId='" + escapeString(employeeId) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, User.class, hotelId);
	}

	public ArrayList<User> getAllUsers(String hotelId) {
		String sql = "SELECT Users.*, (Employee.firstName || ' ' || Employee.surName) AS name " + 
				"FROM Users, Employee WHERE Users.hotelId='" + escapeString(hotelId) + "' AND Users.employeeId == Employee.employeeId;";
		return db.getRecords(sql, User.class, hotelId);
	}

	public boolean deleteUser(String hotelId, String userId) {
		String sql = "DELETE FROM Users WHERE userId = '" + userId + "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	// ------------------------------Attendance

	public Boolean hasCheckedOut(String hotelId, String serviceDate) {
		String sql = "SELECT * FROM Attendance WHERE checkOutTime is NULL AND isPresent = 1 AND checkInDate = '"+serviceDate+"';";
		return db.hasRecords(sql, hotelId);
	}


	public Boolean isPresent(String hotelId, String employeeId) {
		String sql = "SELECT * FROM Attendance WHERE checkOutTime is NULL AND isPresent = 1 AND employeeId = '"
				+ employeeId + "';";
		return db.hasRecords(sql, hotelId);
	}


	public Boolean hasCheckedIn(String hotelId, String employeeId) {
		String sql = "SELECT * FROM Attendance WHERE checkInDate = '" + this.getServiceDate(hotelId)
				+ "' AND employeeId = '" + employeeId + "' AND hotelId = '" + hotelId + "' AND shift = 1;";
		return db.hasRecords(sql, hotelId);
	}


	public Boolean hasSecondShift(String hotelId, String startDate, String endDate) {
		String sql = "SELECT * FROM Attendance WHERE shift = 2 AND authorisation = 1 AND checkInDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "';";
		return db.hasRecords(sql, hotelId);
	}

	public ArrayList<Attendance> getAllAttendance(String hotelId, int shift) {

		String fromClause = " FROM Attendance where Employee.employeeId == Attendance.employeeId AND Attendance.shift == "
				+ shift + "  AND Attendance.checkInDate == '" + this.getServiceDate(hotelId) + "')";

		String sql = "SELECT employeeId, firstName, surName, (SELECT Attendance.Id " + fromClause + " AS Id, "
				+ "(SELECT Attendance.checkInTime " + fromClause + " AS checkInTime, "
				+ "(SELECT Attendance.checkOutTime " + fromClause + " AS checkOutTime, "
				+ "(SELECT Attendance.authorisation " + fromClause + " AS authorisation, "
				+ "(SELECT Attendance.reason  " + fromClause + " AS reason, (SELECT Attendance.checkOutDate "
				+ fromClause + " AS checkOutDate, (SELECT Attendance.isPresent " + fromClause + " AS isPresent, "
				+ "(SELECT Attendance.shift " + fromClause + " AS shift FROM Employee WHERE hotelId = '" + hotelId
				+ "' AND designation != 'ADMINISTRATOR';";
		return db.getRecords(sql, Attendance.class, hotelId);
	}

	public boolean markExcused(String hotelId, String employeeId, String reason, int shift) {

		String sql = "INSERT INTO ATTENDANCE (hotelId, employeeId, checkInTime, checkInDate, authorisation, isPresent, shift, reason)"
				+ " VALUES ('" + escapeString(hotelId) + "', '" + escapeString(employeeId) + "', '"
				+ LocalDateTime.now() + "', '" + escapeString(this.getServiceDate(hotelId)) + "', " + AUTHORIZE + ", "
				+ EXCUSED + ", " + shift + ", '" + reason + "');";

		return db.executeUpdate(sql, true);
	}

	public boolean markAbsent(String hotelId, String employeeId, int shift) {

		String sql = "INSERT INTO ATTENDANCE (hotelId, employeeId, checkInTime, checkInDate, authorisation, isPresent, shift)"
				+ " VALUES ('" + escapeString(hotelId) + "', '" + escapeString(employeeId) + "', '"
				+ LocalDateTime.now() + "', '" + escapeString(this.getServiceDate(hotelId)) + "', " + AUTHORIZE + ", "
				+ ABSENT + ", " + shift + ");";

		return db.executeUpdate(sql, true);
	}

	public String checkOut(String hotelId, int attendanceId) {

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String checkOutDate = now.format(formatter);
		formatter = DateTimeFormatter.ofPattern("HH:mm");

		String sql = "UPDATE ATTENDANCE SET checkOutTime = '" + now + "', checkOutDate = '" + checkOutDate
				+ "' WHERE hotelId = '" + hotelId + "' AND Id = '" + attendanceId + "';";

		db.executeUpdate(sql, true);

		return checkOutDate;
	}

	public String checkInEmployee(String hotelId, String employeeId, int shift) {

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		String checkInTime = now.format(formatter);

		String sql = "INSERT INTO ATTENDANCE (hotelId, employeeId, checkInTime, checkInDate, authorisation, isPresent, shift)"
				+ " VALUES ('" + escapeString(hotelId) + "', '" + escapeString(employeeId) + "', '" + now + "', '"
				+ escapeString(this.getServiceDate(hotelId)) + "', " + UNAUTHORIZE + ", " + PRESENT + ", " + shift
				+ ");";

		db.executeUpdate(sql, true);

		return checkInTime;
	}

	public boolean authorizeEmployee(String hotelId, int attendanceId) {

		String sql = "UPDATE ATTENDANCE SET authorisation = " + AUTHORIZE + " WHERE hotelId = '" + hotelId
				+ "' AND Id = '" + attendanceId + "';";

		return db.executeUpdate(sql, true);
	}

	public int getLastAttendanceId(String hotelId) {
		String sql = "SELECT MAX (Id) AS entityId FROM Attendance;";

		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		return entity.getId();
	}

	// -------------------------------Employee


	public String addEmployee(String hotelId, String firstName, String middleName, String surName, String address,
			String contactNumber, String dob, String sex, String hiringDate, String designation, String department,
			int salary, int bonus, String image, String email) {

		String employeeId = getNextEmployeeId(hotelId);

		String sql = "INSERT INTO Employee "
				+ "(hotelId, employeeId, firstName, surName, address, contactNumber, dob, sex, hiringDate, designation, department"
				+ ", salary, bonus, image, middleName, email) VALUES('" + escapeString(hotelId) + "', '"
				+ escapeString(employeeId) + "', '" + escapeString(firstName) + "', '" + escapeString(surName) + "', '"
				+ escapeString(address) + "', '" + escapeString(contactNumber) + "', '" + escapeString(dob) + "', '"
				+ escapeString(sex) + "', '" + escapeString(hiringDate) + "', '" + escapeString(designation) + "', '"
				+ escapeString(department) + "', " + Integer.toString(salary) + ", " + Integer.toString(bonus) + ", '"
				+ (image.equals("No image") ? "" : "1") + "', '" + escapeString(middleName) + "', '"
				+ escapeString(email) + "');";

		if (db.executeUpdate(sql, true)) {
			return employeeId;
		} else
			return "";
	}


	public Boolean updateEmployee(String hotelId, String employeeId, String firstName, String middleName,
			String surName, String address, String contactNumber, String dob, String sex, String hiringDate,
			String designation, String department, int salary, int bonus, String image, String email) {

		String sql = "UPDATE Employee SET firstName = '" + escapeString(firstName) + "', middleName = '"
				+ escapeString(middleName) + "', surName = '" + escapeString(surName) + "', address = '"
				+ escapeString(address) + "', contactNumber = '" + escapeString(contactNumber) + "', email = '"
				+ escapeString(email) + "', dob = '" + escapeString(dob) + "', sex = '" + escapeString(sex)
				+ "', hiringDate = '" + escapeString(hiringDate) + "', designation = '" + escapeString(designation)
				+ "', department = '" + escapeString(department) + "', salary = " + Integer.toString(salary)
				+ ", bonus = " + Integer.toString(bonus) + ", image  ='" + (image.equals("No image") ? "" : "1")
				+ "' WHERE hotelId = '" + escapeString(hotelId) + "' AND employeeId = '" + escapeString(employeeId)
				+ "';";

		return db.executeUpdate(sql, true);
	}


	public Employee getEmployeeById(String hotelId, String employeeId) {
		String sql = "SELECT * FROM Employee WHERE employeeId = '" + escapeString(employeeId) + "' AND hotelId = '"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Employee.class, hotelId);
	}
	
	public Employee getEmployeeByName(String hotelId, String name) {
		String sql = "SELECT * FROM Employee WHERE firstName = '" + escapeString(name) + "' COLLATE NOCASE AND hotelId = '"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Employee.class, hotelId);
	}


	public ArrayList<Employee> getEmployeesByDesignation(String hotelId, Designation designation) {
		String sql = "SELECT * FROM Employee WHERE designation = '" + escapeString(designation.toString())
				+ "' AND hotelId = '" + escapeString(hotelId) + "';";
		return db.getRecords(sql, Employee.class, hotelId);
	}
	
	public ArrayList<Employee> getEmployeesForNC(String hotelId) {
		String sql = "SELECT * FROM Employee WHERE designation = '" + Designation.OWNER.toString() + "' OR designation = '"
				+ Designation.MANAGER.toString() + "' AND hotelId = '" + escapeString(hotelId) + "';";
		return db.getRecords(sql, Employee.class, hotelId);
	}

	public ArrayList<Employee> getAllEmployee(String hotelId) {
		String sql = "SELECT * FROM Employee WHERE hotelId='" + escapeString(hotelId) + "';";
		return db.getRecords(sql, Employee.class, hotelId);
	}

	public ArrayList<Employee> getAllDeliveryEmployee(String hotelId) {
		String sql = "SELECT * FROM Employee WHERE hotelId='" + escapeString(hotelId)
				+ "' AND designation = 'DELIVERYBOY';";
		return db.getRecords(sql, Employee.class, hotelId);
	}

	public String getNextEmployeeId(String hotelId) {

		String sql = "SELECT MAX(CAST(SUBSTR(employeeId,3) AS integer)) AS entityId FROM Employee WHERE hotelId='"
				+ hotelId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		String employeeId = this.getHotelById(hotelId).mHotelCode;

		if (entity != null) {
			return employeeId + (String.format("%03d", entity.getId() + 1));
		}
		return employeeId + "000";
	}

	public boolean deleteEmployee(String hotelId, String employeeId) {
		String sql = "DELETE FROM Employee WHERE employeeId = '" + employeeId + "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	// -------------------------------Orders

	public ArrayList<Order> getAllOrders(String hotelId, String date, String query, boolean visible) {
		
		String sql = "SELECT Orders.*, (SELECT foodDiscount+barDiscount FROM Payment WHERE Orders.orderId == Payment.orderId) AS discount, "
				+ "(SELECT cashPayment+cardPayment+appPayment FROM Payment WHERE Orders.orderId == Payment.orderId) AS totalPayment, "
				+ "(SELECT cardType FROM Payment WHERE Orders.orderId == Payment.orderId) AS paymentType, "
				+ "(SELECT firstName FROM Employee WHERE Orders.deliveryBoy == Employee.employeeId) AS firstName "
				+ "FROM Orders WHERE Orders.hotelId='" + escapeString(hotelId) + "' ";
		if(!visible) 
			sql += " AND Orders.state!=" + ORDER_STATE_HIDDEN + " ";
		ArrayList<Order> orders = new ArrayList<Order>();
		if (date.length() > 0) {
			sql += "AND Orders.orderDate='" + date + "' ORDER BY id DESC;";
			return db.getRecords(sql, Order.class, hotelId);
		}
		String sql2;
		String serviceDate = this.getServiceDate(hotelId);
		Employee employee = this.getEmployeeByName(hotelId, query);
		if(employee != null) {
			sql2 = sql + "AND deliveryBoy = '" + employee.getEmployeeId() + "' AND Orders.orderDate='" + serviceDate + "' ORDER BY id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			return orders;
		} else if (query.trim().length() > 0) {
			sql2 = sql + "AND Orders.billNo = '" + query + "' AND Orders.orderDate='" + serviceDate + "' ORDER BY id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			sql2 = sql + "AND tableId LIKE '%" + query + "%' AND Orders.orderDate='" + serviceDate + "' ORDER BY id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			sql2 = sql + "AND customerName LIKE '" + query + "%' AND Orders.orderDate='" + serviceDate + "' ORDER BY id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			return orders;
		}
		if (date.length() == 0 && query.trim().length() == 0)
			sql += " AND Orders.orderDate='" + serviceDate + "' ";
		
		sql += " ORDER BY id DESC;";
		return db.getRecords(sql, Order.class, hotelId);
	}
	
	private ArrayList<Order> getCashOrders(String hotelId, String serviceDate) {
		
		String sql = "SELECT Orders.orderId, Orders.billNo, (SELECT cashPayment FROM Payment WHERE Orders.orderId == Payment.orderId) AS cashPayment " + 
					"FROM Orders, Payment " + 
					"WHERE Orders.hotelId='" + escapeString(hotelId) + "' AND Orders.orderDate='" + serviceDate + "' " + 
					"AND Orders.orderId == Payment.orderId " + 
					"AND Payment.cardType LIKE '%CASH%' " +
					"AND Orders.state != " + ORDER_STATE_HIDDEN + " " +
					"ORDER BY cashPayment DESC;";

		return db.getRecords(sql, Order.class, hotelId);
	}
	
	private ArrayList<Order> getAllVisibleOrders(String hotelId, String serviceDate) {
		
		String sql = "SELECT Orders.orderId, Orders.billNo " + 
					"FROM Orders " + 
					"WHERE Orders.hotelId='" + escapeString(hotelId) + "' AND Orders.orderDate BETWEEN '" + serviceDate +
					"' AND '" + this.getServiceDate(hotelId) + "' " + 
					"AND Orders.state != " + ORDER_STATE_HIDDEN + " " +
					"ORDER BY id;";

		return db.getRecords(sql, Order.class, hotelId);
	}
	
	private ArrayList<Order> getInVisibleOrders(String hotelId, String serviceDate) {
		
		String sql = "SELECT Orders.orderId, Orders.billNo " + 
					"FROM Orders " + 
					"WHERE Orders.hotelId='" + escapeString(hotelId) + "' AND Orders.orderDate = '" + serviceDate +
					"' AND Orders.state == " + ORDER_STATE_HIDDEN + " " +
					"ORDER BY id;";

		return db.getRecords(sql, Order.class, hotelId);
	}
	
	public ArrayList<Order> getCompletedOrders(String hotelId) {
		String sql = "SELECT * FROM Orders WHERE hotelId='" + escapeString(hotelId) +
					"' AND orderDate='" + this.getServiceDate(hotelId) +
					"' AND Orders.state = "+ORDER_STATE_BILLING+" ORDER BY id DESC;";

		return db.getRecords(sql, Order.class, hotelId);
	}

	public BigDecimal getTaxableFoodBill(String hotelId, String orderId) {

		String sql = "SELECT SUM(OrderItems.rate*OrderItems.qty) AS entityId FROM OrderItems, MenuItems WHERE orderId = '"
				+ orderId + "' AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId"
				+ " AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '" + hotelId
				+ "' AND (MenuItems.vegType == 1 OR MenuItems.vegType == 2);";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		BigDecimal totalBill = entity.getId();

		sql = "SELECT SUM(OrderAddOns.rate*OrderAddOns.qty) AS entityId FROM OrderAddOns, OrderItems, MenuItems"
				+ " WHERE OrderAddOns.orderId = '" + orderId + "'"
				+ " AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId"
				+ " AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '" + hotelId + "'"
				+ " AND (MenuItems.vegType == 1 OR MenuItems.vegType == 2)"
				+ " AND OrderAddOns.orderId == OrderItems.orderId"
				+ " AND OrderAddOns.subOrderId == OrderItems.subOrderId"
				+ " AND OrderAddOns.menuId == OrderItems.menuId;";

		entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		totalBill.add(entity.getId());

		return totalBill;
	}

	public BigDecimal getTaxableBarBill(String hotelId, String orderId) {

		String sql = "SELECT SUM(OrderItems.rate*OrderItems.qty) AS entityId FROM OrderItems, MenuItems WHERE orderId = '"
				+ orderId + "' AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId"
				+ " AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '" + hotelId
				+ "' AND (MenuItems.vegType == 3 OR MenuItems.vegType == 4);";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		BigDecimal totalBill = entity.getId();

		sql = "SELECT SUM(OrderAddOns.rate*OrderAddOns.qty) AS entityId FROM OrderAddOns, OrderItems, MenuItems"
				+ " WHERE OrderAddOns.orderId = '" + orderId + "'"
				+ " AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId"
				+ " AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '" + hotelId + "'"
				+ " AND (MenuItems.vegType == 3 OR MenuItems.vegType == 4)"
				+ " AND OrderAddOns.orderId == OrderItems.orderId"
				+ " AND OrderAddOns.subOrderId == OrderItems.subOrderId"
				+ " AND OrderAddOns.menuId == OrderItems.menuId;";

		entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		totalBill.add(entity.getId());

		return totalBill;
	}

	public OrderItem getOrderedItem(String hotelId, String orderId, String subOrderId, String menuId) {

		String sql = "SELECT OrderItems.subOrderDate AS subOrderDate, OrderItems.qty AS qty, "
				+ "MenuItems.title AS title, MenuItems.vegType AS vegType, MenuItems.category AS category, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.rate AS rate, "
				+ "MenuItems.isTaxable AS isTaxable, "
				+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE OrderItems.orderId='" + orderId
				+ "' AND OrderItems.subOrderId='" + subOrderId + "' AND OrderItems.menuId=='" + menuId
				+ "' AND OrderItems.hotelId='" + hotelId + "' AND OrderItems.menuId==MenuItems.menuId;";
		return db.getOneRecord(sql, OrderItem.class, hotelId);
	}

	public ArrayList<OrderItem> getOrderedItems(String hotelId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.Id AS Id, OrderItems.menuId AS menuId, OrderItems.qty AS qty, "
				+ "MenuItems.title AS title, MenuItems.vegType AS vegType, MenuItems.category AS category, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.specs AS reason, OrderItems.rate AS rate, "
				+ "MenuItems.isTaxable AS isTaxable, OrderItems.state AS state, "
				+ "substr(OrderItems.subOrderDate, 12, 5) AS time  FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId AND OrderItems.hotelId='" + hotelId + "' UNION ALL "
				+ "SELECT OrderItemLog.subOrderId AS subOrderId, OrderItemLog.subOrderDate AS subOrderDate, "
				+ "OrderItemLog.Id AS Id, OrderItemLog.menuId AS menuId, OrderItemLog.quantity AS qty, "
				+ "MenuItems.title AS title, MenuItems.vegType AS vegType, MenuItems.category AS category, "
				+ "MenuItems.station AS station, (SELECT specs FROM OrderItems WHERE OrderItems.orderId = '" + orderId
				+ "') AS specs, OrderItemLog.reason AS reason, OrderItemLog.rate AS rate, MenuItems.isTaxable AS isTaxable, "
				+ "OrderItemLog.state AS state, substr(OrderItemLog.datetime, 12, 5) AS time "
				+ "FROM MenuItems, OrderItemLog WHERE OrderItemLog.orderId='" + orderId
				+ "' AND OrderItemLog.menuId==MenuItems.menuId "
				+ "AND OrderItemLog.hotelId='" + hotelId + "' ORDER BY menuId;";

		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	public ArrayList<OrderItem> getOrderedItemForVoid(String hotelId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.Id AS Id, OrderItems.menuId AS menuId, OrderItems.qty AS qty, "
				+ "MenuItems.title AS title, MenuItems.vegType AS vegType, MenuItems.category AS category, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.rate AS rate, "
				+ "MenuItems.isTaxable AS isTaxable, OrderItems.state AS state, "
				+ "substr(OrderItems.subOrderDate, 12, 5) AS time  FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId AND OrderItems.hotelId='" + hotelId + "' ORDER BY menuId;";

		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	public ArrayList<EntityString> getUniqueMenuIdForOrder(String hotelId, String orderId) {
		String sql = "SELECT distinct OrderItems.menuId AS entityId FROM OrderItems, MenuItems WHERE OrderItems.orderId='"
				+ orderId + "' AND OrderItems.hotelId='" + hotelId
				+ "' AND MenuItems.menuId=OrderItems.menuId AND MenuItems.flags NOT LIKE '%ci%';";

		return db.getRecords(sql, EntityString.class, hotelId);
	}

	public ArrayList<EntityString> getUniqueMenuIdForComplimentaryOrder(String hotelId, String orderId) {
		String sql = "SELECT distinct menuId AS entityId FROM OrderItemLog WHERE OrderItemLog.orderId='" + orderId
				+ "'; AND hotelId='" + hotelId + "' AND state=" + ORDER_STATE_COMPLIMENTARY + ";";

		return db.getRecords(sql, EntityString.class, hotelId);
	}

	public ArrayList<OrderItem> getOrderedItemForBill(String hotelId, String orderId) {

		ArrayList<EntityString> menuIds = this.getUniqueMenuIdForOrder(hotelId, orderId);
		ArrayList<OrderItem> orderItems = new ArrayList<OrderItem>();
		for (EntityString menuId : menuIds) {

			String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
					+ "OrderItems.Id AS Id, OrderItems.menuId AS menuId, SUM(OrderItems.qty) AS qty, "
					+ "MenuItems.title AS title, MenuItems.vegType AS vegType, "
					+ "MenuItems.category AS category, MenuItems.station AS station, "
					+ "OrderItems.rate AS rate, OrderItems.specs AS specs, MenuItems.isTaxable AS isTaxable, "
					+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE OrderItems.orderId='" + orderId
					+ "' AND OrderItems.menuId='" + menuId.getEntity() + "' AND MenuItems.menuId='" + menuId.getEntity()
					+ "' AND OrderItems.hotelId='" + hotelId + "';";

			orderItems.add(db.getOneRecord(sql, OrderItem.class, hotelId));
		}
		return orderItems;
	}

	public ArrayList<OrderItem> getOrderedItemForBillCI(String hotelId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.menuId AS menuId, OrderItems.qty AS qty, MenuItems.title AS title, "
				+ "MenuItems.vegType AS vegType, MenuItems.category AS category, "
				+ "MenuItems.station AS station, OrderItems.specs AS specs, OrderItems.rate AS rate, "
				+ "OrderItems.specs AS specs, MenuItems.isTaxable AS isTaxable, "
				+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId AND OrderItems.hotelId='" + hotelId + "' "
				+ "AND MenuItems.flags LIKE '%ci;%'";

		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	public ArrayList<OrderItem> getComplimentaryOrderedItemForBill(String hotelId, String orderId) {

		ArrayList<EntityString> menuIds = this.getUniqueMenuIdForComplimentaryOrder(hotelId, orderId);
		ArrayList<OrderItem> orderItems = new ArrayList<OrderItem>();
		for (EntityString menuId : menuIds) {

			String sql = "SELECT OrderItemLog.subOrderId AS subOrderId, "
					+ "OrderItemLog.subOrderDate AS subOrderDate, OrderItemLog.Id AS Id, "
					+ "OrderItemLog.menuId AS menuId, SUM(OrderItemLog.quantity) AS qty, "
					+ "MenuItems.title AS title, MenuItems.vegType AS vegType, "
					+ "MenuItems.category AS category, MenuItems.station AS station, "
					+ "OrderItemLog.rate AS rate, MenuItems.isTaxable AS isTaxable, "
					+ "OrderItemLog.state AS state FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId='" + orderId
					+ "' AND OrderItemLog.menuId='" + menuId.getEntity() + "' AND MenuItems.menuId='"
					+ menuId.getEntity() + "' AND OrderItemLog.hotelId='" + hotelId + "';";

			orderItems.add(db.getOneRecord(sql, OrderItem.class, hotelId));
		}
		return orderItems;
	}
	
	public JSONObject newOnlineOrder(JSONObject orderObj, int portalId) {
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", "failed");
			String sql = "SELECT * FROM OnlineOrders WHERE orderId = '" + orderObj.getInt("order_id") +
					"' AND restaurantId = '" + orderObj.getInt("restaurant_id") + "';";
			
			if(db.hasRecords(sql, orderObj.getString("outlet_id"))) {
				outObj.put("message", "Order already exists.");
				outObj.put("code", "501");
				return outObj;
			}
			
			sql = "INSERT INTO OnlineOrders (hotelId, restaurantId, externalOrderId, portalId, data, status, dateTime) " +
					"VALUES ('" + orderObj.getString("outlet_id") + "', " + orderObj.getInt("restaurant_id") + 
					", " + orderObj.getInt("order_id") + ", " + portalId + ", '" + orderObj + "', "+ONLINE_ORDER_NEW+", '" + LocalDateTime.now() + "');";
			
			if(!db.executeUpdate(sql, false)) {
				outObj.put("message", "Order could not be placed.");
				outObj.put("code", "500");
				return outObj;
			}
			
			outObj.put("status", "success");
			outObj.put("code", "200");
			outObj.put("message", "Order registered.");
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}
	
	public ArrayList<OnlineOrder> getOnlineOrders(String hotelId){
		
		String sql = "SELECT * FROM OnlineOrders WHERE hotelId = '" + hotelId + "' AND status = "+ONLINE_ORDER_NEW+";";
		
		return db.getRecords(sql, OnlineOrder.class, hotelId);
	}

	public JSONObject newOrder(String hotelId, String userId, String[] tableIds, int peopleCount, String customer,
			String mobileNumber, String address, String allergyInfo, String section) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		String hotelType = this.getHotelById(hotelId).getHotelType();
		try {
			String serviceDate = this.getServiceDate(hotelId);
			if (serviceDate == null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started");
				return outObj;
			}
			for (int i = 0; i < tableIds.length; i++) {
				sql = "SELECT * FROM OrderTables WHERE tableId='" + tableIds[i] + "' AND hotelId='"
						+ escapeString(hotelId) + "';";
				TableUsage table = db.getOneRecord(sql, TableUsage.class, hotelId);
				if (table != null) {
					outObj.put("status", -1);
					outObj.put("message", "Table " + tableIds[i] + " not free");
					return outObj;
				}
			}
			StringBuilder tableId = new StringBuilder();
			for (int i = 0; i < tableIds.length; i++) {
				tableId.append(tableIds[i]);
				if (i != tableIds.length)
					tableId.append(",");
			}
			orderId = getNextOrderId(hotelId, userId);
			sql = "INSERT INTO Orders (hotelId, orderId, orderDate, customerName, "
					+ "customerNumber, customerAddress, isSmsSent, waiterId, numberOfGuests, "
					+ "state, inhouse, takeAwayType, tableId, serviceType, foodBill, barBill, section) values ('" + hotelId + "', '" + orderId
					+ "', '" + serviceDate + "','" + customer + "', '" + mobileNumber + "', '"+address+"', 0,'" + userId + "', "
					+ Integer.toString(peopleCount) + ", ";

			if (hotelType.equals("PREPAID")) {
				sql += Integer.toString(ORDER_STATE_BILLING) + "," + INHOUSE + "," + OnlineOrderingPortals.NONE.getValue() + ",'" + tableId.toString() + "','"
						+ getCurrentService(hotelId).getServiceType() + "',0,0, '"+section+"');";
			} else {
				sql += Integer.toString(ORDER_STATE_SERVICE) + "," + INHOUSE + "," + OnlineOrderingPortals.NONE.getValue() + ",'" + tableId.toString() + "','"
						+ getCurrentService(hotelId).getServiceType() + "',0,0,'"+section+"');";
			}
			for (int i = 0; i < tableIds.length; i++) {
				sql = sql + "INSERT INTO OrderTables (hotelId, tableId, orderId) values('" + hotelId + "','"
						+ tableIds[i] + "','" + orderId + "');";
			}
			if (!mobileNumber.equals("")) {
				if (!hasCustomer(hotelId, mobileNumber)) {
					addCustomer(hotelId, customer, mobileNumber, address, "", "", allergyInfo, Boolean.FALSE, Boolean.FALSE);
				} else {
					modifyCustomer(hotelId, customer, mobileNumber, "", "", "", allergyInfo, address, Boolean.FALSE);
				}
			}
			if (!db.executeUpdate(sql, true)) {
				db.rollbackTransaction();
				outObj.put("status", 1);
				outObj.put("message", "Could Not place Order");
				return outObj;
			}
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
			return outObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject hideOrder(String hotelId, String serviceDate, String serviceType, BigDecimal cashAmount) {
		JSONObject outObj = new JSONObject();
		ArrayList<Order> allOrders = this.getCashOrders(hotelId, serviceDate);
		BigDecimal cashSale = this.getCashCardSales(hotelId, serviceDate, serviceType).getCashPayment();
		BigDecimal cashDeducted = new BigDecimal("0.0");
		ServiceLog service = this.getCurrentService(hotelId);
		try {
			outObj.put("status", false);
			if(service.getServiceDate().equals(serviceDate) && service.getServiceType().equals(serviceType)) {
				outObj.put("message", "This process cannot be performed for the Current Service. Please continue after Service is ended.");
				return outObj;
			}
			if(allOrders.isEmpty()) {
				outObj.put("message", "The Selected Service Date and Type have no cash orders.");
				return outObj;
			}
	 	
			for(int i=0; i<allOrders.size(); i++) {
				if(allOrders.get(i).getCashPayment().compareTo((cashSale.subtract(cashAmount))) == 1)
					continue;
				this.changeOrderStateToHidden(hotelId, allOrders.get(i).getOrderId());
				cashDeducted.add(allOrders.get(i).getCashPayment());
				cashSale.subtract(allOrders.get(i).getCashPayment());
			}
			
			String sql = "SELECT cash FROM TotalRevenue WHERE serviceDate = '"+serviceDate+"' AND serviceType = '"+serviceType+"';";
			
			EntityBigDecimal cashReported = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
			if(cashReported!=null) {
				sql = "UPDATE TotalRevenue SET cash2 = " + cashReported.getId() + ", "
					+ "cash = " + (cashReported.getId().subtract(cashDeducted))
					+ ", deductedCash = " + cashAmount 
					+ " WHERE serviceDate = '"+serviceDate+"' AND serviceType = '"+serviceType+"';";
				
				db.executeUpdate(sql, true);
			}
			
			sql = "SELECT Orders.billNo AS entityId FROM Orders WHERE orderDate = '"+serviceDate+"' AND billNo != ''  order by billNo asc LIMIT 1";
			EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
			int billNo = entity.getId();
			this.updateBillNo2(hotelId, serviceDate);
			
			allOrders = this.getAllVisibleOrders(hotelId, serviceDate);
			for(int i=0; i<allOrders.size(); i++) {
				this.updateBillNo(hotelId, allOrders.get(i).getOrderId(), Integer.toString(billNo));
				billNo++;
			}
			
			allOrders = this.getInVisibleOrders(hotelId, serviceDate);
			for(int i=0; i<allOrders.size(); i++) {
				sql = "UPDATE OrderItems SET billNo2 = billNo, billNo = '' WHERE orderId = '"+allOrders.get(i).getOrderId()+"' AND hotelId = '"+hotelId+"'; ";
				
				db.executeUpdate(sql, true);
			}
			
			outObj.put("status", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}
	
	private boolean updateBillNo2(String hotelId, String serviceDate) {
		
		String sql = "UPDATE Orders SET billNo = '' WHERE orderDate = '"+serviceDate+"' AND hotelId = '"+hotelId+"'; "
				+ "UPDATE Payment SET billNo = '' WHERE orderDate = '"+serviceDate+"' AND hotelId = '"+hotelId+"'; ";
		
		return db.executeUpdate(sql, true);
	}
	
	private boolean updateBillNo(String hotelId, String orderId, String billNo) {
		
		String sql = "UPDATE Orders SET billNo = '"+billNo+"' WHERE orderId = '"+orderId+"' AND hotelId = '"+hotelId+"'; "
				+ "UPDATE OrderItems SET billNo2 = billNo, billNo = '"+billNo+"' WHERE orderId = '"+orderId+"' AND hotelId = '"+hotelId+"'; "
				+ "UPDATE Payment SET billNo = '"+billNo+"' WHERE orderId = '"+orderId+"' AND hotelId = '"+hotelId+"'; ";
		
		return db.executeUpdate(sql, true);
	}

	public JSONObject newNCOrder(String hotelId, String userId, String reference, String section, String remarks) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			String serviceDate = this.getServiceDate(hotelId);
			if (serviceDate == null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started");
				return outObj;
			}
			orderId = getNextOrderId(hotelId, userId);
			sql = "INSERT INTO Orders (hotelId, orderId, orderDate, waiterId, numberOfGuests, "
					+ "state, inhouse, serviceType, foodBill, barBill, section, reference, remarks) values ('" + hotelId + "', '" + orderId
					+ "', '" + serviceDate + "', '" + userId + "', 1, "
					+ Integer.toString(ORDER_STATE_COMPLETE) + "," + NON_CHARGEABLE + ",'" 
					+ getCurrentService(hotelId).getServiceType() + "',0,0,'"+section+"', '"+reference+"', '"+remarks+"');";
			
			if (!db.executeUpdate(sql, true)) {
				db.rollbackTransaction();
				outObj.put("status", 1);
				outObj.put("message", "Could Not place Order");
				return outObj;
			}
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
			return outObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject newQsrOrder(String hotelId, String userId, String customer, String mobileNumber,
			String allergyInfo, int orderType) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			String serviceDate = this.getServiceDate(hotelId);
			if (serviceDate == null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started");
				return outObj;
			}
			orderId = getNextOrderId(hotelId, userId);
			sql = "INSERT INTO Orders (hotelId, orderId, orderDate, customerName, "
					+ "customerNumber, customerAddress, waiterId, numberOfGuests, state, inhouse, serviceType, foodBill, barBill) values ('"
					+ hotelId + "', '" + orderId + "', '" + serviceDate + "','" + customer + "', '" + mobileNumber
					+ "', '', '" + userId + "', " + 1 + ", " + Integer.toString(ORDER_STATE_COMPLETE) + "," + orderType
					+ ",'" + getCurrentService(hotelId).getServiceType() + "',0,0);";

			if (!hasCustomer(hotelId, mobileNumber)) {
				addCustomer(hotelId, customer, mobileNumber, "", "", "", allergyInfo, Boolean.FALSE, Boolean.FALSE);
			} else {
				modifyCustomer(hotelId, customer, mobileNumber, "", "", "", allergyInfo, "", Boolean.FALSE);
			}
			db.executeUpdate(sql, true);
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
			outObj.put("orderNumber", this.getOrderNumber(hotelId, orderId));
			return outObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject placeOrder(String hotelId, String userId, String customer, String phone, String address,
			int orderType, int takeAwayType, String allergyInfo, String reference, String remarks) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			String serviceDate = this.getServiceDate(hotelId);
			Boolean status = false;
			if (serviceDate == null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started for the day");
				return outObj;
			}
			if (!phone.equals("")) {
				if (!hasCustomer(hotelId, phone)) {
					status = addCustomer(hotelId, customer, phone, address, "", "", allergyInfo, Boolean.FALSE, Boolean.FALSE);
				} else {
					status = modifyCustomer(hotelId, customer, phone, "", "", "", allergyInfo, address, Boolean.FALSE);
				}
				if (!status) {
					outObj.put("status", -1);
					outObj.put("message", "Failed to update customer information");
					db.rollbackTransaction();
					return outObj;
				}
			}

			String orderState = Integer.toString(ORDER_STATE_SERVICE);
			String hotelType = getHotelById(hotelId).getHotelType();
			if (hotelType.equals("PREPAID"))
				orderState = Integer.toString(ORDER_STATE_BILLING);

			orderId = getNextOrderId(hotelId, userId);
			sql = "INSERT INTO Orders (hotelId, orderId, orderDate, customerName, "
					+ "customerNumber, customerAddress, isSmsSent,  waiterId, numberOfGuests, state, inhouse, takeAwayType, serviceType, reference, remarks)"
					+ " values ('" + hotelId + "', '" + orderId + "', '" + serviceDate + "','" + customer + "', '"
					+ escapeString(phone) + "', '" + escapeString(address) + "',0, '" + userId + "', 1, "
					+ orderState + "," + orderType + "," + takeAwayType + ",'" + getCurrentService(hotelId).getServiceType() + "', '"+reference
					+ "', '"+remarks+"');";
			if (!db.executeUpdate(sql, true)) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to add order");
				db.rollbackTransaction();
				return outObj;
			}
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
			return outObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject newHomeDeliveryOrder(String hotelId, String userId, String customer, String phone, String address,
			String allergyInfo, String remarks) {
		return this.placeOrder(hotelId, userId, customer, phone, address, HOME_DELIVERY, OnlineOrderingPortals.NONE.getValue(), allergyInfo, "", remarks);
	}

	public JSONObject newTakeAwayOrder(String hotelId, String userId, String customer, String phone,
			String externalId, String allergyInfo, String remarks) {
		int takeAwaytype = OnlineOrderingPortals.COUNTER.getValue();
		if(!externalId.equals("")) {
			if(customer.equals("ZOMATO"))
				takeAwaytype = OnlineOrderingPortals.ZOMATO.getValue();
			else if(customer.equals("SWIGGY"))
				takeAwaytype = OnlineOrderingPortals.SWIGGY.getValue();
			else if(customer.equals("FOODPANDA"))
				takeAwaytype = OnlineOrderingPortals.FOODPANDA.getValue();
			else if(customer.equals("UBEREATS"))
				takeAwaytype = OnlineOrderingPortals.UBEREATS.getValue();
			else if(customer.equals("FOODILOO"))
				takeAwaytype = OnlineOrderingPortals.FOODILOO.getValue();
			customer = "";
		}
		return this.placeOrder(hotelId, userId, customer, phone, "", TAKE_AWAY, takeAwaytype, allergyInfo, externalId, remarks);
	}

	public JSONObject newBarOrder(String hotelId, String userId, String reference,
			String remarks) {
		return this.placeOrder(hotelId, userId, "", "", "", BAR, OnlineOrderingPortals.NONE.getValue(), "", reference, remarks);
	}

	public Boolean unCheckOutOrder(String hotelId, String orderId) {
		String hotelType = this.getHotelById(hotelId).getHotelType();
		String sql = "";
		
		if (hotelType.equals("PREPAID"))
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_OFFKDS) + " WHERE orderId='" + orderId
					+ "' AND hotelId='" + hotelId + "';";
		else
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_SERVICE) + " WHERE orderId='" + orderId
					+ "' AND hotelId='" + hotelId + "';";

		return db.executeUpdate(sql, true);
	}

	public Boolean checkOutOrder(String hotelId, String orderId) {
		String hotelType = this.getHotelById(hotelId).getHotelType();
		String sql = "";
		
		if (hotelType.equals("PREPAID"))
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_COMPLETE) + " WHERE orderId='" + orderId
					+ "' AND hotelId='" + hotelId + "'; UPDATE OrderItems SET state="
					+ Integer.toString(SUBORDER_STATE_COMPLETE) + " WHERE orderId='" + orderId + "' AND hotelId='"
					+ hotelId + "'";
		else
			sql += " UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_BILLING) + " WHERE orderId='" + orderId
					+ "' AND hotelId='" + hotelId + "';";

		return db.executeUpdate(sql, true);
	}
	
	public Boolean updateDeliveryTime(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET deliveryTimeStamp = '"+parseTime("HH:mm")+ "' WHERE "
				+ "hotelId = '" + hotelId + "' AND orderId = '"+ orderId + "';";
		
		return db.executeUpdate(sql, true);
	}
	
	public Boolean updateCompleteTime(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET completeTimeStamp = '"+parseTime("HH:mm")+ "' WHERE "
				+ "hotelId = '" + hotelId + "' AND orderId = '"+ orderId + "';";
		
		return db.executeUpdate(sql, true);
	}
	
	public Boolean updateDeliveryBoy(String hotelId, String orderId, String employeeName) {
		String sql = "UPDATE Orders SET deliveryBoy = '"+employeeName+ "' WHERE "
				+ "hotelId = '" + hotelId + "' AND orderId = '"+ orderId + "';";
		
		return db.executeUpdate(sql, true);
	}

	public String updateBillNoInOrders(String hotelId, String orderId) {

		String sql = "SELECT DISTINCT billNo AS entityId FROM OrderItems WHERE hotelId = '" + hotelId + "' AND orderId='" + orderId
				+ "';";

		ArrayList<EntityString> billNos = db.getRecords(sql, EntityString.class, hotelId);

		StringBuilder billNo = new StringBuilder();

		int offset = 1;
		for (EntityString billNoFeild : billNos) {
			billNo.append(billNoFeild.getEntity());
			if (billNos.size() != offset)
				billNo.append(";");
			offset++;
		}

		sql = "UPDATE Orders SET billNo = '" + billNo.toString() + "', billNo2 = '" + billNo.toString() + "' WHERE hotelId = '" + hotelId + "' AND orderId='"
				+ orderId + "';";

		if (db.executeUpdate(sql, true)) {
			return billNo.toString();
		}
		return "";
	}

	public Boolean changeOrderStatus(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_OFFKDS) + " WHERE orderId='" + orderId
				+ "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	public Boolean changeOrderStateToHidden(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_HIDDEN) + " WHERE orderId='" + orderId
				+ "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	public Boolean changeOrderStateToCancelled(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET state = "+ORDER_STATE_CANCELED+" WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
				+ "DELETE FROM OrderTables WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
				+ "DELETE FROM OrderSpecifications WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; ";
		return db.executeUpdate(sql, true);
	}

	public Boolean markPaymentComplete(String hotelId, String orderId) {
		String sql = "DELETE FROM OrderTables WHERE orderId='" + orderId + "' AND hotelId='" + hotelId + "';"
				+ "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_COMPLETE) + " WHERE orderId='" + orderId
				+ "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	public Boolean changeOrderStatusToService(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET state=" + Integer.toString(ORDER_STATE_SERVICE) + " WHERE orderId='" + orderId
				+ "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	public Boolean editSubOrder(String hotelId, String orderId, String subOrderId, String menuId, int qty) {
		String sql = null;
		if (qty == 0) {
			sql = "DELETE FROM OrderItems WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
					+ "' AND menuId='" + menuId + "' AND hotelId='" + hotelId + "' AND state="
					+ Integer.toString(SUBORDER_STATE_PENDING) + ";";
		} else {
			sql = "UPDATE OrderItems SET qty=" + Integer.toString(qty) + " WHERE orderId='" + orderId
					+ "' AND subOrderId=='" + subOrderId + "' AND menuId='" + menuId + "' AND hotelId='" + hotelId
					+ "' AND state=" + Integer.toString(SUBORDER_STATE_PENDING) + ";";
		}
		int itemId = qty + 1;
		this.removeAddOns(hotelId, orderId, subOrderId, menuId, itemId);
		this.removeOrderedSpecification(hotelId, orderId, subOrderId, menuId, itemId);
		return db.executeUpdate(sql, true);
	}

	public Boolean removeOrderedSpecification(String hotelId, String orderId, String subOrderId, String menuId,
			int itemId) {
		String sql = "DELETE FROM OrderSpecifications WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
				+ "' AND menuId='" + menuId + "' AND hotelId='" + hotelId + "' AND itemId=" + itemId + ";";

		return db.executeUpdate(sql, true);
	}

	public Boolean updateFoodBill(String hotelId, String orderId, String menuId, Integer qty, boolean isCancelled, BigDecimal rate) {
		Order order = getOrderById(hotelId, orderId);
		MenuItem menu = getMenuById(hotelId, menuId);

		int veg = menu.getVegType();
		BigDecimal total = new BigDecimal("0.0");
		String sql = null;
		if (isCancelled) {
			if (veg == 3) {
				total = order.getBarBill().subtract(rate);
				sql = "UPDATE Orders SET foodBill = " + order.getFoodBill() + ", barBill = " + total
						+ " WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId + "';";
			} else {
				total = order.getFoodBill().subtract(rate);
				sql = "UPDATE Orders SET foodBill = " + total + ", barBill = " + order.getBarBill()
						+ " WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId + "';";
			}
		} else {
			if (veg == 3) {
				total = rate.multiply(new BigDecimal(qty)).add(order.getBarBill());
				sql = "UPDATE Orders SET foodBill = " + order.getFoodBill() + ", barBill = " + total
						+ " WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId + "';";
			} else {
				total = rate.multiply(new BigDecimal(qty)).add(order.getFoodBill());
				sql = "UPDATE Orders SET foodBill = " + total + ", barBill = " + order.getBarBill()
						+ " WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId + "';";
			}
		}
		return db.executeUpdate(sql, true);
	}

	public Boolean updateFoodBillAddOn(String hotelId, String orderId, String subOrderId, String menuId, int itemId,
			BigDecimal rate) {
		Order order = getOrderById(hotelId, orderId);
		int veg = getMenuById(hotelId, menuId).getVegType();
		ArrayList<OrderAddOn> addOns = this.getOrderedAddOns(hotelId, orderId, subOrderId, menuId, itemId, false);
		for (OrderAddOn addOn : addOns) {
			BigDecimal total = new BigDecimal("0.0");
			String sql = null;
			if (veg == 3) {
				total = rate.multiply(new BigDecimal(addOn.getQty())).add(order.getBarBill());
				sql = "UPDATE Orders SET foodBill = " + order.getFoodBill() + ", barBill = " + total
						+ " WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId + "';";
			} else {
				total = rate.multiply(new BigDecimal(addOn.getQty())).add(order.getFoodBill());
				sql = "UPDATE Orders SET foodBill = " + total + ", barBill = " + order.getBarBill()
						+ " WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId + "';";
			}
			if (!db.executeUpdate(sql, true))
				return false;
		}
		return true;
	}

	public Boolean updateFoodBillAddOnReturn(String hotelId, String orderId, String subOrderId, String menuId,
			int itemId, int addOnId) {
		OrderAddOn addOn = this.getOrderedAddOnById(hotelId, orderId, subOrderId, menuId, itemId, addOnId);
		Order order = getOrderById(hotelId, orderId);
		int veg = getMenuById(hotelId, menuId).getVegType();
		BigDecimal rate = addOn.getRate();
		BigDecimal total = new BigDecimal("0.0");
		String sql = null;
		if (veg == 3) {
			total = order.getBarBill().subtract(rate).multiply(new BigDecimal(addOn.getQty()));
			sql = "UPDATE Orders SET foodBill = " + order.getFoodBill() + ", barBill = " + total + " WHERE hotelId = '"
					+ hotelId + "' AND orderId = '" + orderId + "';";
		} else {
			total = order.getFoodBill().subtract(rate).multiply(new BigDecimal(addOn.getQty()));
			sql = "UPDATE Orders SET foodBill = " + total + ", barBill = " + order.getBarBill() + " WHERE hotelId = '"
					+ hotelId + "' AND orderId = '" + orderId + "';";
		}
		if (!db.executeUpdate(sql, true))
			return false;
		return true;
	}

	public Boolean removeSubOrder(String hotelId, String orderId, String subOrderId, String menuId, int qty) {
		String sql = null;
		if (qty == 0) {
			sql = "DELETE FROM OrderItems WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
					+ "' AND menuId='" + menuId + "' AND hotelId='" + hotelId + "';";
		} else {
			sql = "UPDATE OrderItems SET qty=" + qty + " WHERE orderId='" + orderId + "' AND subOrderId=='" + subOrderId
					+ "' AND menuId='" + menuId + "' AND hotelId='" + hotelId + "';";
		}
		this.removeOrderedSpecification(hotelId, orderId, subOrderId, menuId, qty + 1);
		return db.executeUpdate(sql, true);
	}

	public JSONObject voidOrder(String hotelId, String orderId, String reason, String authId, String section) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);

			Order order = getOrderById(hotelId, orderId);

			String sql = "UPDATE Orders SET state=" + ORDER_STATE_VOIDED + ", foodBill = 0, barBill = 0, reason = '"
					+ reason + "', authId = '" + authId + "' WHERE orderId='" + orderId + "' AND hotelId='" + hotelId
					+ "';";

			if (!db.executeUpdate(sql, true)) {
				outObj.put("message", "Failed to void the order. Please try again.");
				db.rollbackTransaction();
				return outObj;
			}

			if (order.getState() == ORDER_STATE_BILLING || order.getState() == ORDER_STATE_OFFKDS
					|| order.getState() == ORDER_STATE_SERVICE) {

				sql = "DELETE FROM OrderTables WHERE orderId='" + orderId + "' AND hotelId='" + hotelId + "';";
				if (!db.executeUpdate(sql, true)) {
					outObj.put("message", "Failed to delete Order table. Please try again");
					db.rollbackTransaction();
					return outObj;
				}

				String billNo = this.updateBillNoInOrders(hotelId, orderId);
				sql = "INSERT INTO Payment (hotelId, billNo, billNo2, orderId, orderDate, foodBill, barBill, total, cardType, foodDiscount, barDiscount, gst, cashPayment, cardPayment, appPayment"
						+ ", VATBAR, complimentary, loyaltyAmount) "
						+ "VALUES('" + hotelId + "', '" + billNo + "', '" + billNo + "', '"
						+ orderId + "', '" + this.getServiceDate(hotelId) + "', "
						+ order.getFoodBill() + ", " + order.getBarBill() + ", "
						+ "0, 'VOID',0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);";
			} else {
				this.updateCashBalance(hotelId, this.getCashBalance(hotelId, section).subtract((this.getPayment(hotelId, orderId).getCashPayment())));
				sql = "UPDATE Payment SET cashPayment = 0, foodDiscount = 0,  barDiscount = 0, gst = 0, VATBAR = 0, complimentary = 0, loyaltyAmount = 0, cardPayment = 0, appPayment = 0, cardType = 'VOID', total = 0 WHERE orderId = '" + orderId
						+ "' AND hotelID = '" + hotelId + "';";
				
			}
			if (!db.executeUpdate(sql, true)) {
				outObj.put("message", "Failed to insert/update Payment. Please try again.");
				db.rollbackTransaction();
				return outObj;
			}

			ArrayList<OrderItem> orderitems = this.getOrderedItemForVoid(hotelId, orderId);

			for (OrderItem orderItem : orderitems) {
				if (!this.updateOrderItemLog(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), "Void",
						"void", orderItem.getQty(), orderItem.getRate(), 0)) {
					outObj.put("message", "Failed to update OrderItem Log. Please try again.");
					db.rollbackTransaction();
					return outObj;
				}
				if (!this.removeSubOrder(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0)) {
					outObj.put("message", "Failed to remove Ordered Item. Please try again.");
					db.rollbackTransaction();
					return outObj;
				}
				ArrayList<OrderAddOn> addOns = this.getAllOrderedAddOns(hotelId, orderId);
				for (OrderAddOn orderAddOn : addOns) {
					if (!this.updateOrderAddOnLog(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getSubOrderDate(), orderItem.getMenuId(),
							orderAddOn.getItemId(), "void", orderAddOn.getQty(), orderAddOn.getRate(),
							orderAddOn.getAddOnId())) {
						outObj.put("message", "Failed to update Addon Log. Please try again.");
						db.rollbackTransaction();
						return outObj;
					}
					if (!this.removeAddOns(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0)) {
						outObj.put("message", "Failed to remove Ordered Addon. Please try again.");
						db.rollbackTransaction();
						return outObj;
					}
				}
			}
			
			sql = "UPDATE OrderItemLog SET state=" + ORDER_STATE_VOIDED + ", reason = 'Void' WHERE orderId='" + orderId + "' AND hotelId='" + hotelId
					+ "';";

			if (!db.executeUpdate(sql, true)) {
				outObj.put("message", "Failed to void the order. Please try again.");
				db.rollbackTransaction();
				return outObj;
			}
			outObj.put("status", true);
			db.commitTransaction();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			db.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj;
	}

	public Boolean complimentaryOrder(String hotelId, String orderId, String authId) {

		String sql = "UPDATE Orders SET state=" + ORDER_STATE_COMPLIMENTARY + ", foodBill = 0, barBill = 0, "
				+ "authId = '" + authId + "' WHERE orderId='" + orderId + "' AND hotelId='" + hotelId + "';";

		ArrayList<OrderItem> orderitems = this.getOrderedItems(hotelId, orderId);

		for (OrderItem orderItem : orderitems) {
			this.updateOrderItemLog(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), "Complimentary",
					"comp", orderItem.getQty(), new BigDecimal("0.0"), 0);
			this.removeSubOrder(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0);
			ArrayList<OrderAddOn> addOns = this.getAllOrderedAddOns(hotelId, orderId);
			for (OrderAddOn orderAddOn : addOns) {
				this.updateOrderAddOnLog(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getSubOrderDate(), orderItem.getMenuId(),
						orderAddOn.getItemId(), "comp", orderAddOn.getQty(), orderAddOn.getRate(),
						orderAddOn.getAddOnId());
				this.removeAddOns(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0);
			}
		}
		return db.executeUpdate(sql, true);
	}

	public boolean complimentaryItem(String hotelId, String orderId, String menuId, String authId, String subOrderId,
			BigDecimal rate, int qty, String reason) {

		if (this.updateOrderItemLog(hotelId, orderId, subOrderId, menuId, reason, "comp", 1, rate, 0)) {
			this.removeSubOrder(hotelId, orderId, subOrderId, menuId, qty - 1);
			return true;
		}
		return false;
	}

	public boolean complimentaryAddOn(String hotelId, String orderId, int addOnId, String authId, String menuId,
			String subOrderId, String subOrderDate, int itemId, BigDecimal rate, int qty) {

		if (this.updateOrderAddOnLog(hotelId, orderId, subOrderId, subOrderDate, menuId, itemId, "comp", 1, rate, addOnId)) {
			this.removeAddOn(hotelId, orderId, subOrderId, menuId, qty - 1, addOnId, itemId);
			return true;
		}
		return false;
	}

	public JSONObject newSubOrder(String hotelId, String orderId, String menuId, Integer qty, String specs,
			String subOrderId, String waiterId, BigDecimal rate) {
		JSONObject outObj = new JSONObject();
		String sql = null;
		try {
			outObj.put("status", -1);
			outObj.put("message", "Unknown error!");
			MenuItem menu = getMenuById(hotelId, menuId);
			String billNo = getCurrentBill(hotelId, orderId,
					menu.getStation().equals("Bar") || menu.getStation().equals("BAR") ? "B" : "F",
					this.getHotelById(hotelId).getBillType());
			if (billNo.equals("")) {
				if (this.getHotelById(hotelId).getBillType() == BILLTYPE_NUMBER)
					billNo = this.getNextBillNoNumberFormat(hotelId);
				else if(this.getHotelById(hotelId).getBillType() == BILLTYPE_NUMBER_REFRESH)
					billNo = this.getNextBillNoNumberFormatDaywise(hotelId);
				else if(this.getHotelById(hotelId).getBillType() == BILLTYPE_BF)
					billNo = this.getNextBillNo(hotelId, menu.getStation());
			}
			Order order = this.getOrderById(hotelId, orderId);
			if(!menu.getFlags().contains("ci")) {
				if(order.getInHouse() == INHOUSE || order.getInHouse() == BAR || order.getInHouse() == NON_CHARGEABLE)
					rate = menu.getInhouseRate();
				else if(order.getInHouse() == HOME_DELIVERY || (order.getInHouse() == TAKE_AWAY && order.getTakeAwayType() == OnlineOrderingPortals.COUNTER.getValue()))
					rate = menu.getRate();
				else
					rate = menu.getOnlineRate();
			}
			
			String orderState = Integer.toString(SUBORDER_STATE_PENDING);
			boolean hasKds = getHotelById(hotelId).getHasKds();
			int kotPrinting = 1;
			if (!hasKds) {
				orderState = Integer.toString(SUBORDER_STATE_COMPLETE);
				kotPrinting = 0;
			}
			
			sql = "INSERT INTO OrderItems (hotelId, subOrderId, subOrderDate, orderId, menuId, qty, rate, specs, state, billNo, billNo2, isKotPrinted, waiterId) values ('"
					+ hotelId + "', '" + subOrderId + "', '"
					+ (new SimpleDateFormat("yyyy/MM/dd HH:mm")).format(new Date()) + "','" + orderId + "', '" + menuId
					+ "', " + Integer.toString(qty) + ", " + (new DecimalFormat("0.00")).format(rate) + ", '" + specs
					+ "', " + orderState + ", '" + billNo + "', '" + billNo + "', " + kotPrinting + ", '" + waiterId + "');";
			if (!db.executeUpdate(sql, true)) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to add suborder");
				return outObj;
			}
			updateFoodBill(hotelId, orderId, menuId, qty, false, rate);

			if (!hasKds) {
				this.manageStock(hotelId, menuId, subOrderId, orderId);
			}
			outObj.put("status", 0);
			outObj.put("subOrderId", subOrderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj;
	}

	public String getCurrentBill(String hotelId, String orderId, String type, int billType) {
		String sql = "SELECT billNo AS entityId FROM orderitems WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId
				+ "' AND billNo LIKE '" + type + "%';";
		if (billType == BILLTYPE_NUMBER || billType == BILLTYPE_NUMBER_REFRESH)
			sql = "SELECT billNo AS entityId FROM orderitems WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId + "';";

		EntityString billNoFeild = db.getOneRecord(sql, EntityString.class, hotelId);

		if (billNoFeild == null)
			return "";
		return billNoFeild.getEntity();
	}

	public Integer getOrderCount(String hotelId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT count(orderId) AS entityId FROM Orders WHERE waiterId=='" + userId + "' AND orderDate=='"
				+ (new SimpleDateFormat("yyyy/MM/dd")).format(dt) + "' AND hotelId='" + hotelId + "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	public Boolean updateSpecifications(String hotelId, String orderId, String subOrderId, String menuId,
			String specs) {

		String sql = "UPDATE OrderItems SET specs='" + specs + "' WHERE subOrderId=='" + subOrderId + "' AND menuId='"
				+ menuId + "' AND orderId='" + orderId + "' AND hotelId='" + hotelId + "';";

		return db.executeUpdate(sql, true);
	}

	public Boolean changeOrderStatus(String hotelId, String orderId, String subOrderId, String menuId) {
		String sql = null;
		OrderItem item = getOrderStatus(hotelId, orderId, subOrderId, menuId);
		int curState = item.state;
		ArrayList<Table> tables = getJoinedTables(hotelId, orderId);
		String tableId = "";
		if (tables.size() > 0) {
			tableId = tables.get(0).getTableId();
		}
		String userId = orderId.split(":")[0];
		Hotel hotel = this.getHotelById(hotelId);

		if (curState == SUBORDER_STATE_PENDING) {
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_PROCESSING) + " WHERE subOrderId=='"
					+ subOrderId + "' AND menuId='" + menuId + "' AND orderId='" + orderId + "' AND hotelId='" + hotelId
					+ "';";
		} else if (curState == SUBORDER_STATE_PROCESSING) {
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_RECIEVED) + " WHERE subOrderId=='"
					+ subOrderId + "' AND menuId='" + menuId + "' AND orderId='" + orderId + "' AND hotelId='" + hotelId
					+ "';";
			Boolean retval = db.executeUpdate(sql, true);
			if (!retval)
				return retval;
			if (allItemsProcessedOrReceived(hotelId, orderId)) {
				int notId = getNextNotificationId(userId, hotelId);
				String target = tableId.equals("") ? "Home Delivery" : "Table " + tableId;
				if (hotel.getHotelType().equals("KDS")) {
					String msg = "Order of " + target + " is ready.";
					sql += "INSERT INTO Notification (notId, hotelId, orderId, msg) VALUES (" + Integer.toString(notId)
							+ ", '" + hotelId + "','" + orderId + "', '" + msg + "');";
				}
				db.executeUpdate(sql, true);
			} else if (!tableId.equals("")) {
				int notId = getNextNotificationId(userId, hotelId);
				if (hotel.getHotelType().equals("KDS")) {
					String msg = item.title + " of Table " + tableId + " is ready.";
					sql += "INSERT INTO Notification (notId, hotelId, orderId, msg) VALUES (" + Integer.toString(notId)
							+ ", '" + hotelId + "','" + orderId + "', '" + msg + "');";
				}
				db.executeUpdate(sql, true);
			}
			this.manageStock(hotelId, menuId, subOrderId, orderId);

			return retval;
		} else if (curState == SUBORDER_STATE_RECIEVED) {
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_COMPLETE) + " WHERE subOrderId=='"
					+ subOrderId + "' AND menuId='" + menuId + "' AND orderId='" + orderId + "' AND hotelId='" + hotelId
					+ "';";
		} else {
			return false;
		}
		return db.executeUpdate(sql, true);
	}

	private int getQuantityOfOrderedItem(String hotelId, String menuId, String subOrderId, String orderId) {

		String sql = "SELECT qty AS entityId FROM OrderItems WHERE menuId = '" + menuId + "' AND subOrderId = '"
				+ subOrderId + "' AND orderId = '" + orderId + "' AND hotelId = '" + hotelId + "';";

		return db.getOneRecord(sql, EntityId.class, hotelId).getId();
	}

	private Boolean allItemsProcessedOrReceived(String hotelId, String orderId) {
		String sql = "SELECT * FROM OrderItems WHERE hotelId=='" + hotelId + "' AND orderId=='" + orderId
				+ "' AND state<>" + Integer.toString(SUBORDER_STATE_RECIEVED) + " AND state <> "
				+ Integer.toString(SUBORDER_STATE_COMPLETE);
		return !db.hasRecords(sql, hotelId);
	}

	public OrderItem getOrderStatus(String hotelId, String orderId, String subOrderId, String menuId) {
		String sql = "SELECT MenuItems.title as title,OrderItems.state FROM OrderItems,MenuItems WHERE MenuItems.menuId==OrderItems.menuId AND OrderItems.menuId='"
				+ menuId + "' AND OrderItems.subOrderId='" + subOrderId + "' AND OrderItems.orderId='" + orderId
				+ "' AND OrderItems.hotelId='" + hotelId + "';";
		return db.getOneRecord(sql, OrderItem.class, hotelId);
	}

	public ArrayList<OrderItem> getReturnedItems(String hotelId, String orderId) {

		String sql = "SELECT OrderItemLog.subOrderId AS subOrderId, OrderItemLog.subOrderDate AS subOrderDate, "
				+ "OrderItemLog.menuId AS menuId, OrderItemLog.quantity AS qty, "
				+ "OrderItemLog.itemId AS itemId, MenuItems.title AS title, OrderItemLog.rate AS rate, "
				+ "OrderItemLog.reason AS reason, "
				+ "OrderItemLog.state AS state FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId='" + orderId
				+ "' AND OrderItemLog.menuId==MenuItems.menuId AND OrderItemLog.hotelId='" + hotelId + "';";
		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	public Order getOrderById(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND hotelId='" + hotelId + "';";

		return db.getOneRecord(sql, Order.class, hotelId);
	}

	public Table getTableById(String hotelId, String tableId) {
		String sql = "SELECT * FROM TABLES WHERE tableId = '" + tableId + "' AND hotelID = '" + hotelId + "';";

		return db.getOneRecord(sql, Table.class, hotelId);
	}

	public Boolean hasCheckedOutOrders(String hotelId, String serviceDate) {
		String sql = "SELECT * FROM Orders WHERE (state == 0 OR state == 1 OR state == 2) AND hotelId = '" + hotelId
				+ "' AND orderDate == '" + serviceDate + "';";
		return db.hasRecords(sql, hotelId);
	}

	public boolean checkServiceCharge(String hotelId, String orderId) {
		String sql = "SELECT serviceCharge AS entityId FROM Payment WHERE orderId='" + orderId + "' AND hotelId='"
				+ hotelId + "';";
		boolean hasSC = db.hasRecords(sql, hotelId);
		BigDecimal sc = new BigDecimal("0.0");
		if (hasSC)
			sc = db.getOneRecord(sql, EntityBigDecimal.class, hotelId).getId();
		if (sc.compareTo(new BigDecimal("0.0")) == 1)
			return true;
		else
			return false;
	}

	private String getNextOrderId(String hotelId, String userId) {
		String sql = "SELECT MAX(CAST(SUBSTR(orderId," + Integer.toString(userId.length() + 2)
				+ ") AS integer)) AS entityId FROM Orders WHERE orderId LIKE '" + userId + ":%'  AND hotelId='"
				+ hotelId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return userId + ":" + (entity.getId() + 1);
		}
		return userId + ":0";
	}

	private int getOrderNumber(String hotelId, String orderId) {
		String sql = "SELECT Id AS entityId FROM Orders WHERE orderId = '" + orderId + "' AND hotelId='" + hotelId
				+ "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		return entity.getId();
	}

	public String getNextBillNo(String hotelId, String station) {

		StringBuilder billNo = new StringBuilder();
		if (station.equals("BAR") || station.equals("Bar"))
			billNo.append("B");
		else
			billNo.append("F");

		String sql = "SELECT MAX(CAST(REPLACE(billNo, '" + billNo.toString() + "', '') AS integer)) AS entityId "
				+ "FROM OrderItems WHERE hotelId='" + hotelId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		int billNum1 = entity.getId();

		sql = "SELECT MAX(CAST(REPLACE(billNo, '" + billNo.toString() + "', '') AS integer)) AS entityId "
				+ "FROM Orders WHERE hotelId='" + hotelId + "'";
		entity = db.getOneRecord(sql, EntityId.class, hotelId);

		int billNum2 = entity.getId();

		if (billNum1 > billNum2)
			billNo.append(billNum1 + 1);
		else
			billNo.append(billNum2 + 1);

		return billNo.toString();
	}

	public String getNextBillNoNumberFormat(String hotelId) {

		StringBuilder billNo = new StringBuilder();

		String sql = "SELECT MAX(CAST(billNo AS integer)) AS entityId FROM OrderItems WHERE hotelId='" + hotelId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		int billNum1 = entity.getId();

		sql = "SELECT MAX(CAST(billNo AS integer)) AS entityId FROM Orders WHERE hotelId='" + hotelId + "'";
		entity = db.getOneRecord(sql, EntityId.class, hotelId);

		int billNum2 = entity.getId();

		if (billNum1 > billNum2)
			billNo.append(billNum1 + 1);
		else
			billNo.append(billNum2 + 1);

		return billNo.toString();
	}

	public String getNextBillNoNumberFormatDaywise(String hotelId) {

		StringBuilder billNo = new StringBuilder();
		String serviceDate = this.getServiceDate(hotelId);
		String sql = "SELECT MAX(CAST(OrderItems.billNo AS integer)) AS entityId FROM OrderItems, Orders WHERE OrderItems.hotelId='"
				+ hotelId + "' AND Orders.orderId == OrderItems.orderId AND Orders.orderDate=='" + serviceDate + "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		int billNum1 = entity==null?1:entity.getId();

		sql = "SELECT MAX(CAST(billNo AS integer)) AS entityId FROM Orders WHERE hotelId='" + hotelId 
				+ "' AND Orders.orderDate=='" + serviceDate + "';";
		entity = db.getOneRecord(sql, EntityId.class, hotelId);

		int billNum2 = entity==null?1:entity.getId();

		if (billNum1 > billNum2)
			billNo.append(billNum1 + 1);
		else
			billNo.append(billNum2 + 1);

		return billNo.toString();
	}

	public String getNextSubOrderId(String hotelId, String orderId) {
		String sql = "SELECT MAX(CAST(subOrderId AS integer)) AS entityId FROM OrderItems WHERE orderId == '" + orderId
				+ "' AND hotelId='" + hotelId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return Integer.toString(entity.getId() + 1);
		}
		return "0";
	}

	public int getTotalBillAmount(String hotelId, String orderId) {
		String sql = "SELECT SUM(rate*qty) AS entityId FROM OrderItems WHERE hotelId='" + hotelId + "' AND orderId='"
				+ orderId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	public ArrayList<HomeDelivery> getActiveHomeDeliveries(String hotelId, String userId) {

		Hotel hotel = this.getHotelById(hotelId);
		String sql = "SELECT Orders.state, Orders.billNo, Orders.remarks, Orders.customerAddress as address, "
				+ "Orders.customerName as customer, Orders.customerNumber as mobileNo, Orders.orderId, Orders.takeAwayType "
				+ "FROM Orders WHERE inhouse="
				+ HOME_DELIVERY + " AND Orders.state=";

		if (hotel.getHotelType().equals("PREPAID") && !hotel.getHasKds())
			sql += Integer.toString(ORDER_STATE_BILLING);
		else
			sql += Integer.toString(ORDER_STATE_SERVICE);

		sql += " AND hotelId='" + hotelId + "' GROUP BY Orders.orderId";
		return db.getRecords(sql, HomeDelivery.class, hotelId);
	}

	public ArrayList<HomeDelivery> getActiveTakeAway(String hotelId, String userId) {

		Hotel hotel = this.getHotelById(hotelId);
		String sql = "SELECT Orders.state, Orders.billNo, Orders.reference, Orders.remarks, Orders.customerName as customer, "
				+ "Orders.customerNumber as mobileNo, Orders.orderId, Orders.takeAwayType FROM Orders WHERE inhouse="
				+ TAKE_AWAY + " AND Orders.state=";

		if (hotel.getHotelType().equals("PREPAID") && !hotel.getHasKds())
			sql += Integer.toString(ORDER_STATE_BILLING);
		else
			sql += Integer.toString(ORDER_STATE_SERVICE);

		sql += " AND hotelId='" + hotelId + "' GROUP BY Orders.orderId";
		return db.getRecords(sql, HomeDelivery.class, hotelId);
	}

	public ArrayList<HomeDelivery> getActiveBarOrders(String hotelId, String userId) {

		Hotel hotel = this.getHotelById(hotelId);
		String sql = "SELECT Orders.state AS state, Orders.customerName as customer, Orders.customerNumber as mobileNo, "
				+ "Orders.customerAddress as address, Orders.orderId, Orders.reference, Orders.remarks FROM Orders WHERE inhouse="
				+ BAR + " AND Orders.state=";

		if (hotel.getHotelType().equals("PREPAID") && !hotel.getHasKds())
			sql += Integer.toString(ORDER_STATE_BILLING);
		else
			sql += Integer.toString(ORDER_STATE_SERVICE);
		
		sql += " AND hotelId='" + hotelId + "' GROUP BY Orders.orderId";

		return db.getRecords(sql, HomeDelivery.class, hotelId);
	}

	public BigDecimal getOrderTotal(String hotelId, String orderId) {
		String sql = "SELECT TOTAL(OrderItems.qty*OrderItems.rate) AS entityId FROM OrderItems WHERE OrderItems.orderId='" + orderId
				+ "' AND OrderItems.hotelId='" + hotelId + "'";
		EntityBigDecimal amount = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		if (amount == null) {
			return new BigDecimal("0.0");
		}
		return amount.getId();
	}

	public Boolean isHomeDeliveryOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND inhouse==0 AND hotelId='" + hotelId
				+ "'";
		return db.hasRecords(sql, hotelId);
	}

	public Boolean isTakeAwayOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND inhouse==2 AND hotelId='" + hotelId
				+ "'";
		return db.hasRecords(sql, hotelId);
	}

	public Boolean isBarOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND inhouse==3 AND hotelId='" + hotelId
				+ "'";
		return db.hasRecords(sql, hotelId);
	}
	
	public ArrayList<OrderItem> getReturnedOrders(String hotelId, String orderId){
		String sql = null;
		
		sql = "SELECT * FROM OrderItemLog WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'";
		
		return db.getRecords(sql, OrderItem.class, hotelId);
	}
	
	public ArrayList<OrderItem> getCancellableOrderedItems(String hotelId, String orderId){
		String sql = null;

		sql = "SELECT * FROM OrderItems WHERE orderId=='" + orderId + "' AND state!="
				+ Integer.toString(SUBORDER_STATE_PENDING) + " AND hotelId='" + hotelId + "'";
		
		return db.getRecords(sql, OrderItem.class, hotelId);
	}
	
	public Boolean deleteOrder(String hotelId, String orderId) {
		String sql = null;
		sql = "DELETE FROM OrderItems WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
					+ "DELETE FROM Orders WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
					+ "DELETE FROM OrderTables WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
					+ "DELETE FROM OrderSpecifications WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; "
					+ "DELETE FROM OrderAddons WHERE orderId=='" + orderId + "' AND hotelId='" + hotelId + "'; ";
		return db.executeUpdate(sql, true);
	}

	public boolean updateOrderItemLog(String hotelId, String orderId, String subOrderId, String menuId, String reason,
			String type, int quantity, BigDecimal rate, int itemId) {

		int state = SUBORDER_STATE_RETURNED;
		if (type == "void")
			state = SUBORDER_STATE_VOIDED;
		else if (type == "comp")
			state = SUBORDER_STATE_COMPLIMENTARY;
		else if (type == "cancel")
			state = SUBORDER_STATE_CANCELED;

		OrderItem orderedItem = this.getOrderedItem(hotelId, orderId, subOrderId, menuId);

		String sql = "INSERT INTO OrderItemLog "
				+ "(hotelId, orderId, subOrderId, subOrderDate, menuId, state, reason, dateTime, quantity, rate, itemId) "
				+ "VALUES('" + escapeString(hotelId) + "', '" + escapeString(orderId) + "', '"
				+ escapeString(subOrderId) + "', '" + escapeString(orderedItem.getSubOrderDate()) + "', '"
				+ escapeString(menuId) + "', " + state + ", '" + reason + "', '"
				+ new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + "', " + quantity + ", " + rate + ", "
				+ itemId + ");";
		return db.executeUpdate(sql, true);
	}

	public boolean updateOrderAddOnLog(String hotelId, String orderId, String subOrderId, String subOrderDate, String menuId, 
			int itemId, String type, int quantity, BigDecimal rate, int addOnId) {

		int state = SUBORDER_STATE_RETURNED;
		if (type == "void")
			state = SUBORDER_STATE_VOIDED;
		else if (type == "comp")
			state = SUBORDER_STATE_COMPLIMENTARY;
		else if (type == "comp")
			state = SUBORDER_STATE_CANCELED;

		String sql = "INSERT INTO OrderAddOnLog "
				+ "(hotelId, orderId, subOrderId, subOrderDate, menuId, state, itemId, quantity, rate, addOnId) VALUES('"
				+ escapeString(hotelId) + "', '" + escapeString(orderId) + "', " + escapeString(subOrderId) + ", '"
				+ escapeString(subOrderDate) + "', '" + escapeString(menuId) + "', '" + state + "', " + itemId + ", " 
				+ quantity + ", " + rate + ", " + addOnId + ");";
		return db.executeUpdate(sql, true);
	}

	public boolean updateOrderPrintCount(String hotelId, String orderId) {

		String sql = "SELECT printCount AS entityId FROM Orders WHERE hotelId = '" + hotelId + "' AND orderId = '"
				+ orderId + "';";

		int printCount = getOrderPrintCount(hotelId, orderId) + 1;

		sql = "UPDATE Orders SET printCount = " + printCount + " WHERE hotelId = '" + hotelId + "' AND orderId = '"
				+ orderId + "';";
		return db.executeUpdate(sql, true);
	}

	public boolean updateOrderSMSStatusDone(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET isSmsSent = 1 WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId
				+ "';";
		return db.executeUpdate(sql, true);
	}

	public int getOrderPrintCount(String hotelId, String orderId) {

		String sql = "SELECT printCount AS entityId FROM Orders WHERE hotelId = '" + hotelId + "' AND orderId = '"
				+ orderId + "';";

		return db.getOneRecord(sql, EntityId.class, hotelId).getId();
	}

	public boolean updateKOTStatus(String hotelId, String orderId) {

		String sql = "UPDATE OrderItems SET isKotPrinted = 1 WHERE hotelId = '" + hotelId + "' AND orderId = '"
				+ orderId + "';";
		return db.executeUpdate(sql, true);
	}
	
	// ---------------------------------------Table

	public ArrayList<TableUsage> getTableUsage(String hotelId, String userId) {
		String sql = "SELECT Tables.tableId, (SELECT orderId FROM OrderTables WHERE OrderTables.tableId == Tables.tableId) AS orderId, "
				+ "(SELECT Orders.waiterId FROM Orders, OrderTables WHERE OrderTables.tableId == Tables.tableId AND OrderTables.orderId == Orders.orderId) AS userId, "
				+ "(SELECT Orders.state FROM Orders, OrderTables WHERE OrderTables.tableId == Tables.tableId AND OrderTables.orderId == Orders.orderId) AS state "
				+ "FROM Tables WHERE hotelId='" + escapeString(hotelId) + "';";
		return db.getRecords(sql, TableUsage.class, hotelId);
	}

	public ArrayList<TableUsage> getTables(String hotelId) {
		String sql = "SELECT * FROM Tables WHERE hotelId='" + escapeString(hotelId) + "';";
		return db.getRecords(sql, TableUsage.class, hotelId);
	}

	public boolean isTableOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM OrderTables WHERE orderId=='" + orderId + "' AND hotelId=='" + hotelId + "';";
		return db.hasRecords(sql, hotelId);
	}

	public String getOrderIdFromTables(String hotelId, String tableId) {
		String sql = "SELECT orderId FROM OrderTables WHERE tableId=='" + tableId + "' AND  hotelId='" + hotelId + "';";
		String orderId = db.getOneRecord(sql, TableUsage.class, hotelId).getOrderId();

		if (orderId == null)
			return null;
		else
			return orderId;
	}

	public ArrayList<Table> getJoinedTables(String hotelId, String orderId) {
		String sql = "SELECT * FROM OrderTables WHERE orderId == '" + orderId + "' AND hotelId='" + hotelId + "'";
		return db.getRecords(sql, Table.class, hotelId);
	}

	public boolean transferTable(String hotelId, String oldTableId, String newTableId, String orderId) {
		String sql = "UPDATE OrderTables SET tableId='" + newTableId + "' WHERE hotelId='" + hotelId + "' AND tableId='"
				+ oldTableId + "';";
		return db.executeUpdate(sql, true);
	}

	public JSONObject moveItem(String hotelId, int oldTableNumber, int newTableNumber, JSONArray orderItemIds) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);

			String sql = "SELECT * FROM OrderTables WHERE tableId = '" + oldTableNumber + "' AND hotelId='" + hotelId
					+ "';";

			Table oldTable = db.getOneRecord(sql, Table.class, hotelId);

			sql = "SELECT COUNT(id) AS entityId FROM OrderItems WHERE orderId = '" + oldTable.getOrderId()
					+ "' AND hotelId='" + hotelId + "';";
			int itemCount = db.getOneRecord(sql, EntityId.class, hotelId).getId();
			if (itemCount == orderItemIds.length()) {
				outObj.put("message", "Cannot move all item. Please cancel or void order.");
				db.rollbackTransaction();
				return outObj;
			}

			sql = "SELECT * FROM OrderTables WHERE tableId = '" + newTableNumber + "' AND hotelId='" + hotelId + "';";

			Table newTable = db.getOneRecord(sql, Table.class, hotelId);
			Order newOrder = this.getOrderById(hotelId, newTable.getOrderId());
			String billNo = newOrder.getBillNo();

			for (int i = 0; i < orderItemIds.length(); i++) {
				JSONObject orderItemId = orderItemIds.getJSONObject(i);
				sql = "SELECT * FROM OrderItems WHERE Id = " + orderItemId.getInt("id");
				OrderItem item = db.getOneRecord(sql, OrderItem.class, hotelId);
				try {
					if (billNo.equals("")) {
						if (this.getHotelById(hotelId).getBillType() == BILLTYPE_NUMBER)
							billNo = this.getNextBillNoNumberFormat(hotelId);
						else if(this.getHotelById(hotelId).getBillType() == BILLTYPE_NUMBER_REFRESH)
							billNo = this.getNextBillNoNumberFormatDaywise(hotelId);
						else {
							sql = "SELECT station AS entityId FROM MenuItems WHERE menuId = '" + item.getMenuId()
									+ "';";
							String station = db.getOneRecord(sql, EntityString.class, hotelId).getEntity();
							billNo = this.getNextBillNo(hotelId, station);
						}
					}
					String subOrderId = this.getNextSubOrderId(hotelId, newTable.getOrderId());
					sql = "UPDATE OrderItems SET orderId = '" + newTable.getOrderId() + "', billNo = '" + billNo
							+ "', subOrderId = '" + subOrderId + "' WHERE Id = " + orderItemId.getInt("id") + ";";
					if (!db.executeUpdate(sql, true)) {
						outObj.put("message", "Failed to move order. Please try again.");
						db.rollbackTransaction();
						return outObj;
					}
					sql = "UPDATE OrderAddOns SET orderId = '" + newTable.getOrderId() + "', subOrderId = '"
							+ subOrderId + "' WHERE orderId = '" + item.getOrderId() + "' AND subOrderId = '"
							+ item.getSubOrderId() + "' AND menuId = '" + item.getMenuId() + "';";
					if (!db.executeUpdate(sql, true)) {
						outObj.put("message", "Failed to move AddOn. Please try again.");
						db.rollbackTransaction();
						return outObj;
					}
					sql = "UPDATE OrderSpecifications SET orderId = '" + newTable.getOrderId() + "', subOrderId = '"
							+ subOrderId + "' WHERE orderId = '" + item.getOrderId() + "' AND subOrderId = '"
							+ item.getSubOrderId() + "' AND menuId = '" + item.getMenuId() + "';";
					if (!db.executeUpdate(sql, true)) {
						outObj.put("message", "Failed to move Specifications. Please try again.");
						db.rollbackTransaction();
						return outObj;
					}

					if (!this.updateFoodBill(hotelId, newTable.getOrderId(), item.getMenuId(), item.getQty(), false, item.getRate())) {
						outObj.put("message", "Failed to update Bill amount. Please try again.");
						db.rollbackTransaction();
						return outObj;
					}
					if (!this.updateFoodBill(hotelId, oldTable.getOrderId(), item.getMenuId(), item.getQty(), true, item.getRate())) {
						outObj.put("message", "Bill amount could not be updated. Please try again.");
						db.rollbackTransaction();
						return outObj;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			outObj.put("status", true);
			db.commitTransaction();
			if (newOrder.getBillNo().equals("")) {
				db.beginTransaction();
				this.updateBillNoInOrders(hotelId, newTable.getOrderId()).equals("");
				db.commitTransaction();
			}
		} catch (JSONException e1) {
			db.rollbackTransaction();
			e1.printStackTrace();
		}
		return outObj;
	}

	public boolean switchTable(String hotelId, String oldTableNumber, JSONArray newTableNumbers) {

		String sql = "SELECT orderId FROM OrderTables WHERE tableId = '" + oldTableNumber + "' AND hotelId='" + hotelId
				+ "';";

		Table table = db.getOneRecord(sql, Table.class, hotelId);

		sql = "DELETE FROM OrderTables WHERE orderId = '" + table.getOrderId() + "' AND hotelId = '" + hotelId + "';";
		db.executeUpdate(sql, true);
		String tableId = "";
		int currentTableId = 0;
		ArrayList<Integer> tables = new ArrayList<Integer>();

		for (int i = 0; i < newTableNumbers.length(); i++) {
			try {
				currentTableId = newTableNumbers.getJSONObject(i).getInt("tableId");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (!tables.contains(currentTableId))
				tables.add(currentTableId);
		}

		for (int i = 0; i < tables.size(); i++) {
			currentTableId = tables.get(i);
			sql = "INSERT INTO OrderTables (hotelId, tableId, orderId) values('" + hotelId + "','" + currentTableId
					+ "','" + table.getOrderId() + "');";
			db.executeUpdate(sql, true);
			tableId += currentTableId;
			if (i < tables.size() - 1)
				tableId += ", ";
		}
		sql = "UPDATE Orders SET tableId = '" + tableId + "' WHERE orderId = '" + table.getOrderId() + "' AND hotelId='"
				+ hotelId + "';";

		return db.executeUpdate(sql, true);
	}

	public boolean switchFromBarToTable(String hotelId, String orderId, JSONArray newTableNumbers) {

		String tableId = "";
		int currentTableId = 0;
		ArrayList<Integer> tables = new ArrayList<Integer>();
		for (int i = 0; i < newTableNumbers.length(); i++) {
			try {
				currentTableId = newTableNumbers.getJSONObject(i).getInt("tableId");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (!tables.contains(currentTableId))
				tables.add(currentTableId);
		}

		String sql = "";

		for (int i = 0; i < tables.size(); i++) {
			currentTableId = tables.get(i);
			sql = "INSERT INTO OrderTables (hotelId, tableId, orderId) values('" + hotelId + "','" + currentTableId
					+ "','" + orderId + "');";
			db.executeUpdate(sql, true);
			tableId += currentTableId;
			if (i < tables.size() - 1)
				tableId += ", ";
		}
		sql = "UPDATE Orders SET tableId = '" + tableId + "', inHouse = " + INHOUSE + " WHERE orderId = '" + orderId
				+ "' AND hotelId='" + hotelId + "';";

		return db.executeUpdate(sql, true);
	}

	// ----------------------------------------Service

	public String getServiceDate(String hotelId) {
		String sql = "SELECT * FROM ServiceLog WHERE isCurrent=0 AND hotelId='" + escapeString(hotelId) + "';";
		ServiceLog service = db.getOneRecord(sql, ServiceLog.class, hotelId);
		if (service == null) {
			return null;
		}
		return service.getServiceDate();
	}

	public String getServiceType(String hotelId) {
		String sql = "SELECT * FROM ServiceLog WHERE isCurrent=0 AND hotelId='" + escapeString(hotelId) + "';";
		ServiceLog service = db.getOneRecord(sql, ServiceLog.class, hotelId);
		if (service == null) {
			return null;
		}
		return service.getServiceType();
	}

	public boolean addService(String hotelId, String serviceType, String serviceDate, int cashInHand) {

		String sql = "INSERT INTO ServiceLog "
				+ "(hotelId, serviceDate, startTimeStamp, endTimeStamp, serviceType, isCurrent, cashInHand, smsEmailSent) "
				+ "VALUES('" + escapeString(hotelId) + "', '" + serviceDate + "', '"
				+ new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + "', '', '"
				+ escapeString(serviceType) + "', " + 0 + ", " + cashInHand + ", 'false');";
		return db.executeUpdate(sql, true);
	}

	public boolean checkSevicesEnded(String hotelId, String serviceDate, String serviceType) {

		Hotel hotel = this.getHotelById(hotelId);
		if(hotel.getSection().equals(""))
			return true;
		String sql = "SELECT COUNT(section) AS entityId FROM TotalRevenue WHERE hotelId = '"+hotelId+"' AND serviceDate = '"+serviceDate
				+ "' AND serviceType = '"+serviceType+"';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if((hotel.getSections().length-1) == entity.getId())
			return true;
		else
			return false;
	}

	public boolean endService(String hotelId, String serviceDate, String serviceType) {

		String sql = "UPDATE ServiceLog SET endTimeStamp ='"
				+ new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + "', isCurrent = 1  WHERE hotelId = '"
				+ hotelId + "' AND serviceDate = '" + serviceDate + "';";
		return db.executeUpdate(sql, true);
	}

	public boolean updateMessageStatus(String hotelId, String serviceDate, String serviceType, Boolean status) {

		String sql = "UPDATE ServiceLog SET smsEmailSent ='"
				+ status.toString() + "'  WHERE hotelId = '"
				+ hotelId + "' AND serviceDate = '" + serviceDate + "' AND serviceType = '" + serviceType + "';";
		return db.executeUpdate(sql, true);
	}

	public ArrayList<ServiceLog> getServiceLogsForMessageNotSent(String hotelId) {

		String sql = "SELECT * FROM ServiceLog WHERE smsEmailSent = 'false' AND hotelId = '" + hotelId + "' AND isCurrent = 1;";
		return db.getRecords(sql, ServiceLog.class, hotelId);
	}

	public ServiceLog getServiceLog(String hotelId, String serviceDate) {

		String sql = "SELECT * FROM ServiceLog WHERE hotelId = '" + hotelId + "' AND serviceDate = '" + serviceDate
				+ "';";

		return db.getOneRecord(sql, ServiceLog.class, hotelId);
	}
	
	public BigDecimal getSaleForService(String hotelId, String serviceDate, String serviceType) {
		
		String sql = "SELECT ROUND(SUM(Payment.total)*100)/100 AS entityId FROM Payment, Orders WHERE Payment.hotelId = '" 
				+ hotelId + "' AND Orders.orderDate = '" + serviceDate + "' AND Orders.serviceType = '" + serviceType
				+ "' AND Orders.orderID == Payment.orderId;";

		return db.getOneRecord(sql, EntityBigDecimal.class, hotelId).getId();
	}

	public int getCashInHand(String hotelId) {

		String sql = "SELECT cashInHand as entityId FROM ServiceLog WHERE hotelId = '" + hotelId
				+ "' AND isCurrent = 0;";

		return db.getOneRecord(sql, EntityId.class, hotelId).getId();
	}

	public ServiceLog getCurrentService(String hotelId) {

		String sql = "SELECT * FROM ServiceLog WHERE hotelId = '" + hotelId + "' AND isCurrent = 0;";

		return db.getOneRecord(sql, ServiceLog.class, hotelId);
	}

	// ---------------------------------------Customer
	public Boolean addCustomer(String hotelId, String customer, String phone, String address, String birthdate,
			String anniversary, String allergyInfo, Boolean wantsPromotion, Boolean isPriorityCust) {
		String sql = "INSERT INTO Customers (hotelId, customer,address,mobileNo, birthdate, anniversary, allergyInfo, points, wantsPromotion, isPriority, userType) VALUES ('"
				+ escapeString(hotelId) + "', '" + escapeString(customer) + "', '" + escapeString(address) + "', '"
				+ escapeString(phone) + "', '" + escapeString(birthdate) + "', '" + escapeString(anniversary) + "', '"
				+ escapeString(allergyInfo) + "', 0, '" + wantsPromotion + "', '"+ isPriorityCust + "', '"+this.getBaseLoyaltySetting(hotelId).getUserType()+"');";
		return db.executeUpdate(sql, true);
	}

	public Boolean modifyCustomer(String hotelId, String customerName, String phone, String birthdate,
			String anniversary, String remarks, String allergyInfo, String address, Boolean wantsPromotion) {

		allergyInfo = allergyInfo.equals("") ? "" : "', allergyInfo='" + escapeString(allergyInfo);
		remarks = remarks.equals("") ? "" : "', remarks='" + escapeString(remarks);
		birthdate = birthdate.equals("") ? "" : "', birthdate='" + escapeString(birthdate);
		anniversary = anniversary.equals("") ? "" : "', anniversary='" + escapeString(anniversary);
		address = address.equals("") ? "" : "', address='" + escapeString(address);

		String sql = "UPDATE Customers SET customer='" + escapeString(customerName) + birthdate + anniversary
				+ allergyInfo + remarks + address + "', wantsPromotion = '" + wantsPromotion + "' WHERE mobileNo='"
				+ escapeString(phone) + "' AND hotelId='" + escapeString(hotelId) + "';";
		return db.executeUpdate(sql, true);
	}

	public Boolean incrementVisitCount(String hotelId, Customer customer) {

		String sql = "UPDATE Customers SET visitCount="+ customer.getVisitCount()+" WHERE mobileNo='"
				+ customer.getMobileNo() + "' AND hotelId='" + escapeString(hotelId) + "';";
		return db.executeUpdate(sql, true);
	}

	public Boolean hasCustomer(String hotelId, String phone) {
		String sql = "SELECT * FROM customers WHERE mobileNo='" + phone + "' AND hotelId='" + escapeString(hotelId)
				+ "';";
		return db.hasRecords(sql, hotelId);
	}

	public Boolean editCustomerDetails(String hotelId, String orderId, String name, String number, String address,
			int noOfGuests, String allergyInfo) {
		Order order = this.getOrderById(hotelId, orderId);
		Customer customer = this.getCustomerDetails(hotelId, number);
		if (name.equals(""))
			name = order.getCustomerName();
		if (address.equals(""))
			address = order.getCustomerAddress();
		if (noOfGuests == 0)
			noOfGuests = order.getNumberOfGuests();
		String sql = "UPDATE Orders SET customerName ='" + escapeString(name) + "', numberOfGuests = "
				+ Integer.toString(noOfGuests) + ", customerNumber = '" + escapeString(number)
				+ "', customerAddress = '" + escapeString(address) + "' WHERE hotelId = '" + escapeString(hotelId)
				+ "' AND orderId = '" + escapeString(orderId) + "';";

		boolean hasUpdated = db.executeUpdate(sql, true);
		if (hasUpdated) {
			if (customer == null) {
				this.addCustomer(hotelId, name, number, address, "", "", allergyInfo, Boolean.FALSE, Boolean.FALSE);
			} else {
				this.modifyCustomer(hotelId, name, number, "", "", "", allergyInfo, address,
						customer.getWantsPromotion() == null ? false : customer.getWantsPromotion());
			}
			return true;
		}
		return false;
	}

	public ArrayList<Customer> getCustomersForSMS(String hotelId) {
		String sql = "SELECT distinct Customers.mobileNo, Customers.customer, Orders.completeTimestamp, Orders.orderId"
				+ " FROM Customers, Orders WHERE Orders.hotelId='" + hotelId
				+ "' AND Orders.isSmsSent = 0 AND Orders.customerNumber == Customers.mobileNo AND Orders.state == "
				+ORDER_STATE_COMPLETE+" AND Orders.orderDate == '"+this.getServiceDate(hotelId)+"';";
		return db.getRecords(sql, Customer.class, hotelId);
	}

	public Customer getCustomerDetails(String hotelId, String mobileNo) {
		String sql = "SELECT * FROM Customers WHERE mobileNo='" + mobileNo + "' AND hotelId='" + hotelId + "'";
		return db.getOneRecord(sql, Customer.class, hotelId);
	}

	public int getCustomerPoints(String hotelId, String mobileNo) {
		String sql = "SELECT points AS entityId FROM Customers WHERE mobileNo='" + mobileNo + "' AND hotelId='"
				+ hotelId + "'";
		return db.getOneRecord(sql, EntityId.class, hotelId).getId();
	}

	public EntityString getMobileNoFromOrderId(String hotelId, String orderId) {
		String sql = "SELECT customerNumber AS entityId FROM Orders WHERE orderId='" + orderId + "' AND hotelId='"
				+ hotelId + "'";
		return db.getOneRecord(sql, EntityString.class, hotelId);
	}

	public ArrayList<Customer> getAllCustomerDetails(String hotelId, int page) {
		int start = ((page -1)*100)+1;
		int end = start+99;
		String sql = "SELECT * FROM Customers WHERE hotelId='" + hotelId + "' AND id BETWEEN " + start + " AND " + end + ";";
		return db.getRecords(sql, Customer.class, hotelId);
	}

	public ArrayList<Customer> getAllCustomerDetailsBySearch(String hotelId, String query) {
		String sql = "SELECT * FROM Customers WHERE hotelId='" + hotelId + "' AND (mobileNo LIKE '%" + query 
				+ "%' OR customer LIKE '%" + query + "%');";
		return db.getRecords(sql, Customer.class, hotelId);
	}

	public ArrayList<Customer> getAllCustomerDetailsForOrdering(String hotelId) {
		String sql = "SELECT mobileNo, customer, address FROM Customers WHERE hotelId='" + hotelId + "';";
		return db.getRecords(sql, Customer.class, hotelId);
	}

	public Customer getCustomerBySearch(String hotelId, String query) {

		String sql = "SELECT * FROM Customers WHERE mobileNo LIKE '%" + query + "%' AND hotelId='" + escapeString(hotelId) + "';";

		return db.getOneRecord(sql, Customer.class, hotelId);
	}

	public ArrayList<Order> getOrdersOfOneCustomer(String hotelId, String mobileNo) {
		String sql = "SELECT Orders.*, Payment.foodDiscount, Payment.barDiscount, Payment.cardType, " + 
				"(Payment.cashPayment+Payment.cardPayment+Payment.appPayment) AS totalPayment " + 
				"FROM Orders, Payment WHERE customerNumber = '" + mobileNo + "' AND Orders.hotelId = '" + hotelId + "'" +
				" AND Orders.orderId = Payment.orderId;";
		return db.getRecords(sql, Order.class, hotelId);
	}

	// ------------------------------------------Payment

	public ArrayList<Report> getPaymentDetails(String hotelId, String sDate, String eDate) {
		String sql = "SELECT * FROM Payment WHERE hotelId= '" + hotelId + "' AND orderDate BETWEEN '" + sDate
				+ "' AND '" + eDate + "'";
		return db.getRecords(sql, Report.class, hotelId);
	}

	public BigDecimal getTotalCashIn(String hotelId, String serviceDate) {
		String sql = "SELECT SUM(cashPayment) FROM Payment WHERE hotelId= '" + hotelId + "' AND orderDate = '"
				+ serviceDate.replace("/", "-") + "';";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	public boolean addPayment(String hotelId, String orderId, BigDecimal foodBill, BigDecimal barBill, BigDecimal foodDiscount,
			BigDecimal barDiscount, BigDecimal loyalty, BigDecimal total, BigDecimal sc, BigDecimal gst, BigDecimal vatBar, BigDecimal tip, BigDecimal cashPayment,
			BigDecimal cardPayment, BigDecimal appPayment, String discountName, String cardType, BigDecimal complimentary, String section) {

		cardType = cardType.toUpperCase().replace(' ', '_');
		String orderDate = this.getServiceDate(hotelId);

		String sql = "SELECT * FROM Payment WHERE hotelId='" + hotelId + "' AND orderId='" + orderId
				+ "' AND orderDate='" + orderDate + "';";
//add app payment type.
		Report payment = db.getOneRecord(sql, Report.class, hotelId);

		if (payment != null)
			return false;

		Order order = getOrderById(hotelId, orderId);

		sql = "INSERT INTO Payment (hotelId, billNo, billNo2, orderId, orderDate, foodBill, barBill, foodDiscount, barDiscount, loyaltyAmount, total, "
				+ "serviceCharge, gst, VATBAR, tip, cashPayment, cardPayment, appPayment, discountName, cardType, complimentary, section) "
				+ "VALUES('" + hotelId + "', '" + order.getBillNo() + "', '" + order.getBillNo() + "', '" + orderId + "', '" + orderDate + "', "
				+ foodBill + ", " + barBill + ", " + foodDiscount + ", "+ barDiscount + ", " + loyalty + ", "+ total + ", " + sc + ", " + gst + ", " + vatBar + ", "
				+ tip + ", " + cashPayment + ", " + cardPayment + ", " + appPayment+ ", '" + discountName + "', '" + cardType + "', "
				+ complimentary + ", '"+section+"');";

		return db.executeUpdate(sql, true);
	}
	
	public boolean deletePayment(String hotelId, String orderId) {
		String sql = "DELETE FROM Payment WHERE hotelId == '"+hotelId+"' AND orderId == '"+orderId+"';";
		return db.executeUpdate(sql, true);
	}

	public boolean editPayment(String hotelId, String orderId, BigDecimal cashPayment, BigDecimal cardPayment, BigDecimal appPayment,
			String cardType) {

		String sql = "UPDATE Payment SET cashPayment = " + cashPayment + ", cardPayment = "
				+ cardPayment + ", appPayment = " + appPayment + ", cardType = '" + escapeString(cardType) + "' " + "WHERE orderId = '"
				+ orderId + "' AND hotelID = '" + hotelId + "';";

		return db.executeUpdate(sql, true);
	}

	private String appendEndDate(String endDate) {
		return endDate + " 23:59";
	}

	public Boolean updatePaymentForReturn(String hotelId, String orderId, BigDecimal foodBill, BigDecimal barBill,
			BigDecimal foodDiscount, BigDecimal barDiscount, BigDecimal total, BigDecimal serviceCharge, BigDecimal gst, BigDecimal VATBar, BigDecimal cashPayment,
			BigDecimal cardPayment, BigDecimal appPayment) {

		String sql = "UPDATE Payment SET foodBill = " + foodBill + ", barBill = " + barBill + ", " + "foodDiscount = "
				+ foodDiscount + "barDiscount = " + barDiscount + ", total = " + total + ", serviceCharge = " + serviceCharge 
				+ ", gst = " + gst + ", VATBar = " + VATBar + ", cashPayment = " + cashPayment + ", " + "cardPayment = " 
				+ cardPayment + ", appPayment = " + appPayment + " WHERE orderId = '" + orderId + "' AND hotelID = '" + hotelId + "';";

		return db.executeUpdate(sql, true);
	}

	public Report getPayment(String hotelId, String orderId) {
		String sql = "SELECT * FROM PAYMENT WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId + "';";

		return db.getOneRecord(sql, Report.class, hotelId);
	}

	// -----------------------------------------AddOn

	public Boolean addOrderAddon(String hotelId, String orderId, String menuId, int qty, int addOnId, String subOrderId,
			int itemId, BigDecimal rate) {

		String sql = "INSERT INTO OrderAddOns (hotelId, subOrderId, subOrderDate, addOnId, orderId, menuId, itemId, qty, rate, state) values ('"
				+ hotelId + "', '" + subOrderId + "', '" + (new SimpleDateFormat("yyyy/MM/dd HH:mm")).format(new Date()) + "', "
				+ addOnId + ",'" + orderId + "', '" + menuId + "', " + itemId
				+ ", " + Integer.toString(qty) + ", " + (new DecimalFormat("0.00")).format(rate) + ", " +SUBORDER_STATE_COMPLETE+ ");";
		if (!db.executeUpdate(sql, true)) {
			return false;
		}
		updateFoodBillAddOn(hotelId, orderId, subOrderId, menuId, itemId, rate);

		return true;
	}

	public ArrayList<AddOn> getAddOns(String hotelId) {
		String sql = "SELECT * FROM AddOns  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, AddOn.class, hotelId);
	}

	public AddOn getAddOnById(int addOnId, String hotelId) {

		String sql = "SELECT * FROM AddOns WHERE Id = " + addOnId;

		return db.getOneRecord(sql, AddOn.class, hotelId);
	}

	public boolean addAddOn(String hotelId, String name, BigDecimal inHouseRate, BigDecimal deliveryRate, BigDecimal onlineRate) {

		String sql = "INSERT INTO AddOns (name, inHouseRate, deliveryRate, onlineRate) VALUES('" + escapeString(name) + "', "
				+ inHouseRate + ", " + deliveryRate + ", " + onlineRate + ");";
		return db.executeUpdate(sql, true);
	}

	public OrderAddOn getOrderedAddOnById(String hotelId, String orderId, String subOrderId, String menuId, int itemId,
			int addOnId) {

		String sql = "SELECT OrderAddOns.addOnId AS addOnId, OrderAddOns.menuId AS menuId, "
				+ "OrderAddOns.qty AS qty, OrderAddOns.itemId AS itemId, OrderAddOns.rate AS rate, "
				+ "OrderAddOns.subOrderId AS subOrderId, AddOns.name AS name FROM OrderAddOns, AddOns "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.menuId='" + menuId + "' AND AddOns.id == OrderAddOns.addOnId "
				+ "AND OrderAddOns.subOrderId='" + subOrderId + "' AND OrderAddOns.itemId == " + itemId + " "
				+ "AND OrderAddOns.addOnId == " + addOnId + " AND OrderAddOns.hotelId='" + hotelId + "';";
		return db.getOneRecord(sql, OrderAddOn.class, hotelId);
	}
	
	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String menuId, boolean getReturnedItems) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId, OrderAddOns.state, "
				+ "OrderAddOns.qty, OrderAddOns.itemId, OrderAddOns.rate, "
				+ "OrderAddOns.subOrderId, AddOns.name FROM OrderAddOns, AddOns "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.menuId='" + menuId + "' "
				+ "AND OrderAddOns.addOnId == AddOns.id AND OrderAddOns.hotelId='" + hotelId + "'";
		if(getReturnedItems) {
			sql += " UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId, OrderAddOnLog.state, "
				+ "OrderAddOnLog.quantity AS qty, OrderAddOnLog.itemId, OrderAddOnLog.rate, "
				+ "OrderAddOnLog.subOrderId, AddOns.name FROM OrderAddOnLog, AddOns "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' AND OrderAddOnLog.menuId='" + menuId + "' "
				+ "AND OrderAddOnLog.addOnId == AddOns.id AND OrderAddOnLog.hotelId='" + hotelId + "';";
		}
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}
	
	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String subOrderId, String menuId, boolean getReturnedItems) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId, OrderAddOns.state, "
				+ "OrderAddOns.qty, OrderAddOns.itemId, OrderAddOns.rate, "
				+ "OrderAddOns.subOrderId, AddOns.name FROM OrderAddOns, AddOns "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND  OrderAddOns.subOrderId='" + subOrderId + "' AND OrderAddOns.menuId='" + menuId + "' "
				+ "AND OrderAddOns.addOnId == AddOns.id AND OrderAddOns.hotelId='" + hotelId + "'";
		if(getReturnedItems) {
			sql += " UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId, OrderAddOnLog.state, "
				+ "OrderAddOnLog.quantity AS qty, OrderAddOnLog.itemId, OrderAddOnLog.rate, "
				+ "OrderAddOnLog.subOrderId, AddOns.name FROM OrderAddOnLog, AddOns "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' AND OrderAddOnLog.menuId='" + menuId + "' "
				+ "AND OrderAddOnLog.subOrderId='" + subOrderId + "' "
				+ "AND OrderAddOnLog.addOnId == AddOns.id AND OrderAddOnLog.hotelId='" + hotelId + "';";
		}
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String subOrderId, String menuId,
			int itemId, boolean getReturnedItems) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.menuId, OrderAddOns.state, "
				+ "OrderAddOns.qty, OrderAddOns.itemId, OrderAddOns.rate, "
				+ "OrderAddOns.subOrderId, AddOns.name FROM OrderAddOns, AddOns "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.menuId='" + menuId + "' "
				+ "AND OrderAddOns.subOrderId='" + subOrderId + "' AND OrderAddOns.itemId == " + itemId + " "
				+ "AND OrderAddOns.addOnId == AddOns.id AND OrderAddOns.hotelId='" + hotelId + "'";
		if(getReturnedItems) {
			sql += " UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId, OrderAddOnLog.menuId, OrderAddOnLog.state, "
				+ "OrderAddOnLog.quantity AS qty, OrderAddOnLog.itemId, OrderAddOnLog.rate, "
				+ "OrderAddOnLog.subOrderId, AddOns.name FROM OrderAddOnLog, AddOns "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' AND OrderAddOnLog.menuId='" + menuId + "' "
				+ "AND OrderAddOnLog.subOrderId='" + subOrderId + "' AND OrderAddOnLog.itemId == " + itemId + " "
				+ "AND OrderAddOnLog.addOnId == AddOns.id AND OrderAddOnLog.hotelId='" + hotelId + "';";
		}
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	public ArrayList<OrderAddOn> getCanceledOrderedAddOns(String hotelId, String orderId, String subOrderId,
			String menuId, int itemId) {

		String sql = "SELECT OrderAddOnLog.addOnId AS addOnId, OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.quantity AS qty, OrderAddOnLog.itemId AS itemId, "
				+ "OrderAddOnLog.rate AS rate, OrderAddOnLog.subOrderId AS subOrderId, AddOns.name AS name "
				+ "FROM OrderAddOnLog, AddOns WHERE OrderAddOnLog.orderId='" + orderId + "' "
				+ "AND OrderAddOnLog.menuId='" + menuId + "' AND OrderAddOnLog.subOrderId='" + subOrderId + "' "
				+ "AND OrderAddOnLog.itemId == " + itemId + " AND OrderAddOnLog.addOnId == AddOns.id "
				+ "AND OrderAddOnLog.hotelId='" + hotelId + "';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	public ArrayList<OrderAddOn> getReturnedAddOns(String hotelId, String orderId, String subOrderId, String menuId,
			int itemId) {

		String sql = "SELECT OrderAddOnLog.addOnId AS addOnId, OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.quantity AS qty, OrderAddOnLog.rate AS rate, "
				+ "OrderAddOnLog.itemId AS itemId, OrderAddOnLog.subOrderId AS subOrderId, "
				+ "AddOns.name AS name FROM OrderAddOnLog, AddOns WHERE OrderAddOnLog.orderId='" + orderId
				+ "' AND OrderAddOnLog.menuId='" + menuId + "' AND OrderAddOnLog.subOrderId='" + subOrderId
				+ "' AND OrderAddOnLog.itemId == " + itemId + " AND OrderAddOnLog.addOnId == AddOns.id "
				+ "AND OrderAddOnLog.hotelId='" + hotelId + "';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	public ArrayList<OrderAddOn> getAllOrderedAddOns(String hotelId, String orderId) {

		String sql = "SELECT OrderAddOns.addOnId AS addOnId, OrderAddOns.menuId AS menuId, "
				+ "OrderAddOns.subOrderDate AS subOrderDate, OrderAddOns.state AS state, "
				+ "OrderAddOns.qty AS qty, OrderAddOns.rate AS rate, OrderAddOns.itemId AS itemId, "
				+ "OrderAddOns.subOrderId AS subOrderId, AddOns.name AS name FROM OrderAddOns, AddOns "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.addOnId == AddOns.id "
				+ "AND OrderAddOns.hotelId='" + hotelId + "' UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId AS addOnId, OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.subOrderDate AS subOrderDate, OrderAddOnLog.state AS state, "
				+ "OrderAddOnLog.quantity AS qty, OrderAddOnLog.rate AS rate, "
				+ "OrderAddOnLog.itemId AS itemId, OrderAddOnLog.subOrderId AS subOrderId, "
				+ "AddOns.name AS name FROM OrderAddOnLog, AddOns WHERE OrderAddOnLog.orderId='" + orderId
				+ "' AND OrderAddOnLog.addOnId == AddOns.id AND OrderAddOnLog.state == " + SUBORDER_STATE_COMPLIMENTARY
				+ " AND OrderAddOnLog.hotelId='" + hotelId + "';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String subOrderId, String menuId, String itemId) {

		String sql = "SELECT OrderAddOns.addOnId, OrderAddOns.qty, OrderAddOns.rate "
				+ ", AddOns.name, OrderAddOnLog.state FROM OrderAddOns, AddOns "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' AND OrderAddOns.addOnId == AddOns.id "
				+ "AND OrderAddOns.hotelId='" + hotelId + "' AND OrderAddOnLog.subOrderId='" + subOrderId
				+ "' AND OrderAddOnLog.menuId == '"+menuId+"' AND OrderAddOnLog.itemId == '" + itemId + "';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	public Boolean removeAddOns(String hotelId, String orderId, String subOrderId, String menuId, int qty) {
		String sql = "DELETE FROM OrderAddOns WHERE orderId='" + orderId + "' AND subOrderId =" + subOrderId
				+ " AND menuId='" + menuId + "' AND hotelId='" + hotelId + "' ";

		if (qty > 0)
			sql += "AND itemId =" + (qty + 1) + ";";

		return db.executeUpdate(sql, true);
	}

	public Boolean removeAddOnsFromItem(String hotelId, String orderId, String subOrderId, String menuId, int itemId) {
		String sql = "DELETE FROM OrderAddOns WHERE orderId='" + orderId + "' AND subOrderId =" + subOrderId
				+ " AND menuId='" + menuId + "' AND hotelId='" + hotelId + "' AND itemId = " + itemId + ";";

		return db.executeUpdate(sql, true);
	}

	public Boolean removeAddOn(String hotelId, String orderId, String subOrderId, String menuId, int qty, int addOnId,
			int itemId) {
		String sql = null;
		if (qty == 0) {
			sql = "DELETE FROM OrderAddOns WHERE orderId='" + orderId + "' AND subOrderId =" + subOrderId
					+ " AND menuId='" + menuId + "' AND hotelId='" + hotelId + "' AND addOnId=" + addOnId
					+ " AND itemId =" + itemId + ";";
		} else {
			sql = "UPDATE OrderAddOns SET qty=" + Integer.toString(qty) + " WHERE orderId='" + orderId
					+ "' AND subOrderId =" + subOrderId + " AND menuId='" + menuId + "' AND hotelId='" + hotelId
					+ "' AND addOnId=" + addOnId + " AND itemId =" + itemId + ";";
		}
		return db.executeUpdate(sql, true);
	}

	// ------------------------------------------Specification

	public Boolean addOrderSpecification(String hotelId, String orderId, String subOrderId, String menuId, int itemId,
			String specification) {

		String sql = "INSERT INTO OrderSpecifications (hotelId, orderId, subOrderId, menuId, itemId, specification) values ('"
				+ hotelId + "', '" + orderId + "', '" + subOrderId + "', '" + menuId + "', " + itemId + ", '"
				+ escapeString(specification) + "');";
		return db.executeUpdate(sql, true);
	}

	public ArrayList<Specifications> getSpecifications(String hotelId) {
		String sql = "SELECT * FROM Specifications ORDER BY specification;";
		return db.getRecords(sql, Specifications.class, hotelId);
	}

	public boolean addSpecification(String name) {

		String sql = "INSERT INTO Specifications (specification) VALUES('" + escapeString(name) + "');";
		return db.executeUpdate(sql, true);
	}

	public ArrayList<OrderSpecification> getOrderedSpecification(String hotelId, String orderId, String menuId,
			String subOrderId, int itemId) {

		String sql = "SELECT * FROM OrderSpecifications WHERE orderId='" + orderId + "' AND itemId == " + itemId
				+ " AND menuId='" + menuId + "' AND subOrderId='" + subOrderId + "';";
		return db.getRecords(sql, OrderSpecification.class, hotelId);
	}

	public ArrayList<OrderSpecification> getOrderedSpecification(String hotelId, String orderId, String menuId) {

		String sql = "SELECT * FROM OrderSpecifications WHERE orderId='" + orderId + "' AND menuId='" + menuId + "';";
		return db.getRecords(sql, OrderSpecification.class, hotelId);
	}

	// ----------------------------------------Stock

	private void manageStock(String hotelId, String menuId, String subOrderId, String orderId) {

		BigDecimal quantity = new BigDecimal(this.getQuantityOfOrderedItem(hotelId, menuId, subOrderId, orderId));
		BigDecimal newQuantity = new BigDecimal("0.0");

		ArrayList<Stock> recipeItems = this.getRecipe(hotelId, menuId);

		if (recipeItems == null) {
			return;
		}

		for (int i = 0; i < recipeItems.size(); i++) {
			Stock stockItem = this.getStockItemBySku(hotelId, recipeItems.get(i).getSku());
			if (stockItem != null) {
				newQuantity = stockItem.getQuantity().subtract((quantity));
				if (quantity.compareTo(new BigDecimal("0")) == 1)
					this.updateStock(hotelId, recipeItems.get(i).getSku(), newQuantity, quantity);
			}
		}
		return;
	}

	private BigDecimal getQuantity(String sku, String hotelId) {

		String sql = "SELECT quantity AS entityId FROM Stock WHERE sku = '" + sku + "' AND hotelId = '" + hotelId
				+ "';";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	public ArrayList<Stock> getStock(String hotelId) {
		String sql = "SELECT Material.sku AS sku, Material.name AS name, Material.unit AS unit, "
				+ "Material.displayableUnit AS displayableUnit, Material.ratePerUnit AS ratePerUnit, "
				+ "ROUND(Stock.quantity*100)/100 AS quantity FROM Material, Stock WHERE Material.hotelId= '" + hotelId + "' "
				+ "AND Material.sku == Stock.sku ORDER BY name;";
		return db.getRecords(sql, Stock.class, hotelId);
	}

	public ArrayList<Stock> getExpiringStock(String hotelId) {

		ArrayList<Stock> stock = new ArrayList<Stock>();
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String exDate = dateFormat.format(cal.getTime());

		for (int i = 0; i <= 1; i++) {
			String sql = "SELECT Stock.sku AS sku, Material.name AS name, Material.unit AS unit, "
					+ "Material.displayableUnit AS displayableUnit, Stock.quantity AS quantity, "
					+ "Stock.doc AS doc, Stock.doe AS doe FROM Material, Stock WHERE Material.hotelId= '" + hotelId
					+ "' AND Material.sku == Stock.sku AND Stock.doe == '" + exDate + "';";
			stock.addAll(db.getRecords(sql, Stock.class, hotelId));

			cal.add(Calendar.DATE, 1);
			exDate = dateFormat.format(cal.getTime());
		}
		return stock;
	}

	public ArrayList<Stock> getStockRunningOut(String hotelId) {

		String sql = "SELECT Stock.sku AS sku, Material.name AS name, "
				+ "Material.displayableUnit AS displayableUnit, Stock.quantity AS quantity, "
				+ "Material.minQuantity AS minQuantity FROM Material, Stock WHERE Material.hotelId= '" + hotelId
				+ "' AND Material.sku == Stock.sku AND quantity <= minQuantity ";
		return db.getRecords(sql, Stock.class, hotelId);
	}

	public boolean addStock(String hotelId, String sku, String doc, String doe, BigDecimal quantity, BigDecimal ratePerUnit) {

		String sql = "INSERT INTO Stock (hotelId, sku, doc, doe, quantity) VALUES('" + escapeString(hotelId) + "', '"
				+ escapeString(sku) + "', '" + escapeString(doc) + "', '" + escapeString(doe) + "', "
				+ quantity + ");";

		if (db.executeUpdate(sql, true)) {
			this.updateStockLog(hotelId, sku, quantity, ratePerUnit.multiply(quantity), "CREATE");
			return true;
		} else
			return false;
	}

	public Boolean updateStock(String hotelId, String sku, BigDecimal newQuantity, BigDecimal addedQuantity, BigDecimal ratePerUnit,
			String doe) {

		String sql = "UPDATE Stock SET quantity = '" + newQuantity + "', doe = '" + escapeString(doe)
				+ "' WHERE hotelId = '" + escapeString(hotelId) + "' AND sku = '" + escapeString(sku) + "';";

		if (db.executeUpdate(sql, true)) {
			this.updateStockLog(hotelId, sku, addedQuantity, addedQuantity.multiply(ratePerUnit), "UPDATE");
			return true;
		}
		return false;
	}

	public Boolean updateStock(String hotelId, String sku, BigDecimal newQuantity, BigDecimal addedQuantity) {

		String sql = "UPDATE Stock SET quantity = " + newQuantity + " WHERE hotelId = '"
				+ escapeString(hotelId) + "' AND sku = '" + escapeString(sku) + "';";

		if (db.executeUpdate(sql, true)) {
			this.updateStockLog(hotelId, sku, addedQuantity, addedQuantity.multiply(this.getRatePerUnit(sku, hotelId)),
					"USEDUP");
			return true;
		}
		return false;
	}

	public Stock getStockItemBySku(String hotelId, String sku) {
		String sql = "SELECT * FROM Stock WHERE sku='" + escapeString(sku) + "' AND hotelId='" + escapeString(hotelId)
				+ "';";
		return db.getOneRecord(sql, Stock.class, hotelId);
	}

	public boolean deleteStockItem(String hotelId, String sku) {
		String sql = "DELETE FROM Stock WHERE sku = '" + sku + "' AND hotelId='" + hotelId + "';";

		BigDecimal quantity = this.getQuantity(sku, hotelId);

		this.updateStockLog(hotelId, sku, quantity, quantity.multiply(this.getRatePerUnit(sku, hotelId)), "DELETED");

		if (db.executeUpdate(sql, true)) {
			sql = "DELETE FROM Material WHERE sku = '" + sku + "' AND hotelId='" + hotelId + "';";
			return db.executeUpdate(sql, true);
		} else
			return false;
	}

	public boolean updateStockLog(String hotelId, String sku, BigDecimal quantity, BigDecimal amount, String crud) {

		String sql = "INSERT INTO StockLog (hotelId, sku, crud, quantity, amount) VALUES('" + escapeString(hotelId)
				+ "', '" + sku + "', '" + crud + "', " + quantity + ", " + amount
				+ ");";
		return db.executeUpdate(sql, true);
	}

	// ----------------------------------------Materials

	private BigDecimal getRatePerUnit(String sku, String hotelId) {

		String sql = "SELECT ratePerUnit AS entityId FROM Material WHERE sku = '" + sku + "' AND hotelId = '" + hotelId
				+ "';";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	public ArrayList<Stock> getMaterial(String hotelId, int type) {
		String sql = "SELECT Material.sku AS sku, Material.name AS name, Material.unit AS unit, "
				+ "Material.displayableUnit AS displayableUnit, Material.ratePerUnit AS ratePerUnit, "
				+ "Material.minQuantity AS minQuantity, Stock.quantity AS quantity, Stock.doe AS doe "
				+ "FROM Material, Stock WHERE Material.hotelId= '" + hotelId + "' " + "AND Material.sku == Stock.sku ";
		if (type == 0)
			sql += "ORDER BY quantity - minQuantity;";
		else
			sql += "ORDER BY Material.name;";
		return db.getRecords(sql, Stock.class, hotelId);
	}

	public ArrayList<Stock> getMaterialByName(String hotelId) {
		String sql = "SELECT Material.sku AS sku, Material.name AS name, Material.unit AS unit, "
				+ "Material.displayableUnit AS displayableUnit, Material.ratePerUnit AS ratePerUnit, "
				+ "Material.minQuantity AS minQuantity, Stock.quantity AS quantity, Stock.doe AS doe "
				+ "FROM Material, Stock WHERE Material.hotelId= '" + hotelId + "' "
				+ "AND Material.sku == Stock.sku ORDER BY name;";
		return db.getRecords(sql, Stock.class, hotelId);
	}

	public boolean addMaterial(String hotelId, String materialName, BigDecimal ratePerUnit, BigDecimal minQuantity,
			BigDecimal quantity, String doe, int wastage, String unit, String displayableUnit) {

		String sku = getNextSKU(hotelId);

		String sql = "INSERT INTO Material "
				+ "(hotelId, sku, name, ratePerUnit, minQuantity, wastage, unit, displayableUnit) VALUES('"
				+ escapeString(hotelId) + "', '" + escapeString(sku) + "', '" + escapeString(materialName) + "', "
				+ ratePerUnit + ", " + minQuantity + ", " + Integer.toString(wastage)
				+ ", '" + escapeString(unit) + "', '" + escapeString(displayableUnit) + "');";

		if (db.executeUpdate(sql, true)) {
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			String doc = dateFormat.format(cal.getTime());
			this.addStock(hotelId, sku, doc, doe, quantity, ratePerUnit);
			return true;
		} else
			return false;
	}

	public ArrayList<Stock> getMaterialBySearch(String hotelId, String query, int type) {

		query = escapeString(query);
		String sql = "SELECT Material.sku AS sku, Material.name AS name, Material.unit AS unit, "
				+ "Material.displayableUnit AS displayableUnit, Material.ratePerUnit AS ratePerUnit, "
				+ "Material.minQuantity AS minQuantity, Stock.quantity AS quantity FROM Material, Stock "
				+ "WHERE Material.hotelId= '" + hotelId + "' AND Material.sku == Stock.sku ";

		if (query.equals("")) {
			sql += "";
		} else if (query.matches("\\D*")) {
			query = "%" + query + "%";
			sql += "AND Material.name LIKE'" + query + "';";
		} else {
			sql += "AND Material.sku =='" + query + "';";
		}
		if (type == 0)
			sql += "ORDER BY quantity - minQuantity;";
		else if (type == 1)
			sql += "ORDER BY Materials.name;";
		return db.getRecords(sql, Stock.class, hotelId);
	}

	public Stock getOneMaterial(String hotelId, String sku) {

		String sql = "SELECT Material.name AS name, Stock.sku AS sku, Material.unit AS unit, "
				+ "Material.displayableUnit AS displayableUnit, Material.ratePerUnit AS ratePerUnit, "
				+ "Material.minQuantity AS minQuantity, Material.wastage AS wastage, "
				+ "Stock.quantity AS quantity, Stock.doe AS doe FROM Material, Stock " + "WHERE Material.hotelId= '"
				+ hotelId + "' AND Material.sku == Stock.sku " + "AND Material.sku =='" + escapeString(sku) + "';";

		return db.getOneRecord(sql, Stock.class, hotelId);
	}

	public Boolean updateMaterial(String hotelId, String materialName, BigDecimal ratePerUnit, BigDecimal minQuantity,
			BigDecimal quantity, String doe, int wastage, String displayableUnit, String sku) {

		BigDecimal oldQuantity = this.getQuantity(sku, hotelId);

		String sql = "UPDATE Material SET name = '" + escapeString(materialName) + "', ratePerUnit = "
				+ ratePerUnit + ", minQuantity = " + minQuantity + ", wastage = "
				+ Integer.toString(wastage) + ", displayableUnit = '" + escapeString(displayableUnit)
				+ "' WHERE hotelId = '" + escapeString(hotelId) + "' AND sku = '" + escapeString(sku) + "';";

		if (quantity.compareTo(new BigDecimal("0.0")) == 1)
			this.updateStock(hotelId, sku, quantity, quantity.subtract(oldQuantity), ratePerUnit, doe);

		return db.executeUpdate(sql, true);
	}

	public Boolean materialExists(String hotelId, String name) {
		Stock item = getMaterialItemByTitle(hotelId, name);
		if (item != null) {
			return true;
		}
		return false;
	}

	public Stock getMaterialItemByTitle(String hotelId, String name) {
		String sql = "SELECT * FROM Material WHERE name='" + escapeString(name) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Stock.class, hotelId);
	}

	public String getNextSKU(String hotelId) {

		String sql = "SELECT MAX(CAST(sku AS integer)) AS entityId FROM Material WHERE hotelId='" + hotelId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		return String.format("%14d", entity.getId() + 1).replace(" ", "0");
	}

	// --------------------------------------------Recipe

	public boolean deleteRecipeItem(String hotelId, String sku, String menuId) {
		String sql = "DELETE FROM Recipe WHERE sku = '" + sku + "' AND menuId='" + menuId + "' AND hotelId='" + hotelId
				+ "';";
		return db.executeUpdate(sql, true);
	}

	public ArrayList<Stock> getRecipe(String hotelId, String menuId) {
		String sql = "SELECT Recipe.sku AS sku, Recipe.quantity AS quantity, Material.name AS name, "
				+ "Material.unit AS unit, Recipe.unit AS displayableUnit FROM Material, Recipe "
				+ "WHERE Recipe.hotelId= '" + hotelId + "' AND Recipe.menuId= '" + menuId + "' "
				+ "AND Material.sku == Recipe.sku;";
		return db.getRecords(sql, Stock.class, hotelId);
	}

	public Stock getMethod(String hotelId, String menuId) {
		String sql = "SELECT MenuItems.method AS method FROM MenuItems WHERE MenuItems.hotelId= '" + hotelId
				+ "' AND MenuItems.menuId= '" + menuId + "';";
		return db.getOneRecord(sql, Stock.class, hotelId);
	}

	public boolean recipeItemExists(String hotelId, String sku, String menuId) {
		Stock item = getRecipeItemByTitle(hotelId, sku, menuId);
		if (item != null) {
			return true;
		}
		return false;
	}

	public Stock getRecipeItemByTitle(String hotelId, String sku, String menuId) {
		String sql = "SELECT * FROM Recipe WHERE sku='" + escapeString(sku) + "' AND menuId='" + escapeString(menuId)
				+ "' AND hotelId='" + escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Stock.class, hotelId);
	}

	public boolean addRecipe(String hotelId, BigDecimal quantity, String menuId, String sku, String unit) {
		String sql = "INSERT INTO Recipe (hotelId, sku, unit, menuId, quantity) VALUES('" + escapeString(hotelId)
				+ "', '" + escapeString(sku) + "', '" + escapeString(unit) + "', '" + escapeString(menuId) + "', "
				+ quantity + ");";
		return db.executeUpdate(sql, true);
	}

	public boolean updateRecipe(String hotelId, BigDecimal quantity, String menuId, String sku, String unit) {

		String sql = "UPDATE Recipe SET quantity = " + quantity + ", unit = '" + escapeString(unit)
				+ "' WHERE hotelId = '" + escapeString(hotelId) + "' AND sku = '" + escapeString(sku)
				+ "' AND menuId = '" + escapeString(menuId) + "';";
		return db.executeUpdate(sql, true);
	}

	public boolean updateMethod(String hotelId, String menuId, String method) {

		String sql = "UPDATE MenuItems SET method = '" + escapeString(method) + "' WHERE hotelId = '"
				+ escapeString(hotelId) + "' AND menuId = '" + escapeString(menuId) + "';";
		return db.executeUpdate(sql, true);
	}

	public boolean reduceQuantity(String hotelId, String sku, BigDecimal newQuantity, BigDecimal quantity) {

		String sql = "UPDATE Stock SET quantity = " + newQuantity + " WHERE hotelId = '"
				+ escapeString(hotelId) + "' AND sku = '" + escapeString(sku) + "';";

		if (!db.executeUpdate(sql, true))
			return false;
		this.updateStockLog(hotelId, sku, quantity, quantity.multiply(this.getRatePerUnit(sku, hotelId)), "REDUCE");
		return true;
	}

	// ----------------------------------------------KOT
	public ArrayList<OrderItem> getOrderedItemsForKOT(String hotelId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.orderId AS orderId, "
				+ "OrderItems.subOrderDate AS subOrderDate, OrderItems.qty AS qty, MenuItems.title AS title, "
				+ "MenuItems.menuId AS menuId, MenuItems.vegType AS vegType, MenuItems.station AS station, "
				+ "OrderItems.specs AS specs FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId AND OrderItems.isKotPrinted = 0 "
				+ "AND OrderItems.hotelId='" + hotelId + "' ORDER BY MenuItems.category;";
		return db.getRecords(sql, OrderItem.class, hotelId);
	}
	
	public ArrayList<OrderItem> getOrderedItemsForReprintKOT(String hotelId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, OrderItems.orderId AS orderId, "
				+ "OrderItems.subOrderDate AS subOrderDate, OrderItems.qty AS qty, MenuItems.title AS title, "
				+ "MenuItems.menuId AS menuId, MenuItems.vegType AS vegType, MenuItems.station AS station, "
				+ "OrderItems.specs AS specs FROM OrderItems, MenuItems WHERE orderId='" + orderId
				+ "' AND OrderItems.menuId==MenuItems.menuId "
				+ "AND OrderItems.hotelId='" + hotelId + "' ORDER BY MenuItems.category;";
		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	public ArrayList<OrderItem> checkKOTPrinting(String hotelId) {

		String sql = "SELECT distinct orderId FROM OrderItems WHERE OrderItems.hotelId = '" + hotelId + "' "
				+ "AND isKotPrinted == 0;";

		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	// ---------------------------------------------KDS

	public ArrayList<KitchenDisplayOrders> getKDSOrdersListView(String hotelId) {

		String hotelType = this.getHotelById(hotelId).getHotelType();

		String sql = "SELECT OrderItems.orderId as orderId, OrderItems.subOrderDate as subOrderDate,"
				+ " OrderItems.subOrderId as subOrderId, MenuItems.title as title,"
				+ " MenuItems.menuId as menuId, MenuItems.vegType as vegType, OrderItems.qty as qty, "
				+ " OrderItems.specs as specs, OrderItems.state as orderState,  Orders.inhouse as inhouse, "
				+ " Orders.billNo as billNo,  Orders.customerAddress as customerAddress, "
				+ " Orders.customerName as customerName FROM OrderItems, MenuItems, Orders"
				+ " WHERE OrderItems.menuId == MenuItems.menuID AND OrderItems.orderId == Orders.orderId";

		if (hotelType.equals("PREPAID"))
			sql += " AND (Orders.state == " + ORDER_STATE_BILLING + " OR Orders.state == " + ORDER_STATE_SERVICE
					+ " OR Orders.state == " + ORDER_STATE_OFFKDS + ")";
		else
			sql += " AND Orders.state == " + ORDER_STATE_SERVICE;

		sql += " AND OrderItems.hotelId == '" + hotelId + "' AND Orders.hotelId == '" + hotelId + "'"
				+ " AND Orders.orderDate LIKE '%" + this.getServiceDate(hotelId) + "%'"
				+ " ORDER BY OrderItems.orderId ASC;";

		return db.getRecords(sql, KitchenDisplayOrders.class, hotelId);
	}
	// ----------------------------------------------Ratings

	public boolean submitRatings(String hotelId, String orderId, String customerName, String customerNumber,
			String customerBirthdate, String customerAnniversary, String reviewSuggestions, JSONObject ratings,
			Boolean wantsPromotion) {
		try {
			String sql = "";
			if (!customerNumber.equals("")) {
				if (!hasCustomer(hotelId, customerNumber)) {
					addCustomer(hotelId, customerName, customerNumber, "", customerBirthdate, customerAnniversary, "",
							wantsPromotion, Boolean.FALSE);
				} else {
					modifyCustomer(hotelId, customerName, customerNumber, customerBirthdate, customerAnniversary, "",
							"", "", wantsPromotion);
				}
			}

			sql = "UPDATE Orders SET customerName='" + customerName + "', customerNumber='" + customerNumber
					+ "', rating_ambiance=" + ratings.getInt("ambianceRating") + ", rating_qof="
					+ ratings.getInt("qualityOfFoodRating") + ", rating_service=" + ratings.getInt("serviceRating")
					+ ", rating_hygiene=" + ratings.getInt("hygieneRating") + ", reviewSuggestions='"
					+ reviewSuggestions + "' WHERE orderId='" + orderId + "' AND hotelId='" + hotelId + "';";
			return db.executeUpdate(sql, true);
		} catch (Exception e) {
			return false;
		}
	}

	public Integer getAmbiancePoints(String hotelId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT TOTAL(rating_ambiance) AS entityId FROM Orders WHERE waiterId=='" + userId
				+ "' AND orderDate=='" + (new SimpleDateFormat("yyyy/MM/dd")).format(dt) + "' AND hotelId='" + hotelId
				+ "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	public Integer getQoFPoints(String hotelId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT TOTAL(rating_qof) AS entityId FROM Orders WHERE waiterId=='" + userId
				+ "' AND orderDate=='" + (new SimpleDateFormat("yyyy/MM/dd")).format(dt) + "' AND hotelId='" + hotelId
				+ "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	public Integer getServicePoints(String hotelId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT TOTAL(rating_service) AS entityId FROM Orders WHERE waiterId=='" + userId
				+ "' AND orderDate=='" + (new SimpleDateFormat("yyyy/MM/dd")).format(dt) + "' AND hotelId='" + hotelId
				+ "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	public Integer getHygienePoints(String hotelId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT TOTAL(rating_hygiene) AS entityId FROM Orders WHERE waiterId=='" + userId
				+ "' AND orderDate=='" + (new SimpleDateFormat("yyyy/MM/dd")).format(dt) + "' AND hotelId='" + hotelId
				+ "';";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	public BigDecimal getAverageFood(String hotelId, String custNumber) {

		String sql = "SELECT ROUND(AVG(rating_qof)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"
				+ custNumber + "'";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	public BigDecimal getAverageAmbiance(String hotelId, String custNumber) {

		String sql = "SELECT ROUND(AVG(rating_ambiance)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"
				+ custNumber + "'";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	public BigDecimal getAverageService(String hotelId, String custNumber) {

		String sql = "SELECT ROUND(AVG(rating_service)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"
				+ custNumber + "'";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	public BigDecimal getAverageHygiene(String hotelId, String custNumber) {

		String sql = "SELECT ROUND(AVG(rating_hygiene)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"
				+ custNumber + "'";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	public BigDecimal getOverallAvgFood(String hotelId) {

		String sql = "SELECT ROUND(AVG(rating_qof)*100)/100 AS entityId FROM Orders;";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	public BigDecimal getOverallAvgAmbiance(String hotelId) {

		String sql = "SELECT ROUND(AVG(rating_ambiance)*100)/100 AS entityId FROM Orders;";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	public BigDecimal getOverallAvgService(String hotelId) {

		String sql = "SELECT ROUND(AVG(rating_service)*100)/100 AS entityId FROM Orders;";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	public BigDecimal getOverallAvgHygiene(String hotelId) {

		String sql = "SELECT ROUND(AVG(rating_hygiene)*100)/100 AS entityId FROM Orders;";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	// ----------------------------------------------------Station

	public ArrayList<KitchenStation> getKitchenStations(String hotelId) {
		String sql = "SELECT * FROM Stations WHERE hotelId='" + hotelId + "';";
		return db.getRecords(sql, KitchenStation.class, hotelId);
	}
	// -----------------------------------------------------Discount
	public Boolean addDiscount(String hotelId, String name, String description, int type, int foodValue, int barValue, String startDate,
			String expiryDate, String usageLimit, JSONArray validColletions, boolean hasExpiry) throws ParseException {

		String collections = "";
		for (int i = 0; i < validColletions.length(); i++) {
			try {
				collections += validColletions.getString(i);
				if (i < validColletions.length() - 1) {
					collections += ",";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(usageLimit.equals(""))
			usageLimit = "Unlimited";
		if(!hasExpiry) {
			startDate = "01/01/2018";
			expiryDate = "31/12/3000";
		}
		String sql = "INSERT INTO Discount "
				+ "(hotelId, name, description, type, foodValue, barValue, startDate, expiryDate, usageLimit, validCollections) "
				+ "VALUES('" + escapeString(hotelId) + "', '" + escapeString(name) + "', '" + escapeString(description)
				+ "', '" + Integer.toString(type) + "', " + Integer.toString(foodValue) + ", " + Integer.toString(barValue) + ", '" + startDate
				+ "', '" + expiryDate + "', '" + escapeString(usageLimit) + "', '"
				+ escapeString(collections) + "');";
		return db.executeUpdate(sql, true);
	}

	public Boolean editDiscount(String hotelId, String name, String description, int type, int foodValue, int barValue, String startDate,
			String expiryDate, String usageLimit, JSONArray validColletions, boolean hasExpiry) throws ParseException {

		String collections = "";
		for (int i = 0; i < validColletions.length(); i++) {
			try {
				collections += validColletions.getString(i);
				if (i < validColletions.length() - 1) {
					collections += ",";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(usageLimit.equals(""))
			usageLimit = "Unlimited";
		if(!hasExpiry) {
			startDate = "01/01/2018";
			expiryDate = "31/12/3000";
		}
		String sql = "UPDATE Discount SET description = '" + escapeString(description) + "', type = '"
				+ type + "', foodValue = " + foodValue + ", barValue = " + barValue
				+ ", startDate = '" + startDate + "', expiryDate = '" + expiryDate + "', usageLimit = '"
				+ escapeString(usageLimit) + "', validCollections = '" + escapeString(collections)
				+ "' WHERE  hotelId='" + hotelId + "' AND name = '" + name + "';";
		return db.executeUpdate(sql, true);
	}

	public Boolean updateUsageLimit(String hotelId, String name, int usageLimit) {

		String sql = "UPDATE Discount SET usageLimit = '" + Integer.toString(usageLimit) + "' WHERE  hotelId='"
				+ escapeString(hotelId) + "' AND name = '" + name + "';";
		return db.executeUpdate(sql, true);
	}

	public ArrayList<Discount> getAllDiscounts(String hotelId) {
		String sql = "SELECT * FROM Discount WHERE hotelId='" + escapeString(hotelId) + "';";
		return db.getRecords(sql, Discount.class, hotelId);
	}

	public Boolean deleteDiscount(String hotelId, String name) {
		String sql = "DELETE FROM Discount WHERE name='" + name + "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	public Boolean discountExists(String hotelId, String name) {
		Discount discount = getDiscountByName(hotelId, name);
		if (discount != null) {
			return true;
		}
		return false;
	}

	public Discount getDiscountByName(String hotelId, String name) {
		String sql = "SELECT * FROM Discount WHERE name='" + escapeString(name) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Discount.class, hotelId);
	}

	public String getDiscountUsageLimit(String hotelId, String name) {
		String sql = "SELECT usageLimit FROM Discount WHERE name='" + escapeString(name) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		Discount dis = db.getOneRecord(sql, Discount.class, hotelId);

		return dis.getUsageLimit();
	}

	public BigDecimal getAppliedDiscount(String hotelId, String orderId) {
		String sql = "SELECT (foodDiscount+barDiscount) AS entityId FROM PAYMENT WHERE hotelId = '" + hotelId + "' AND orderId = '"
				+ orderId + "';";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return new BigDecimal("0.0");
	}

	public JSONObject applyDiscount(String hotelId, String orderId, String discountCode) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			
			if(!this.discountExists(hotelId, discountCode)) {
				outObj.put("message", "This code does not exist. Please enter a valid discount code.");
			}
			Discount discount = this.getDiscountByName(hotelId, discountCode);
			
			if (discount.getHasUsageLimit()) {
				if (discount.getUsageLimit().equals("0")) {
					outObj.put("message", "This discount cannot be used right now as it has been exhausted.");
					return outObj;
				}
			}

			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date date = df.parse(discount.getExpiryDate());

			if (date.before(new Date())) {
				outObj.put("message", "This discount has expired. Please use another Offer.");
				return outObj;
			}
			date = df.parse(discount.getStartDate());

			if (date.after(new Date())) {
				outObj.put("message", "This discount is not active yet. Please use another Offer.");
				return outObj;
			}
			
			String sql = "UPDATE Orders SET discountCode = '" + escapeString(discountCode) + "' WHERE hotelId = '" + hotelId
					+ "' AND orderId = '" + orderId + "';";
			
			if(!db.executeUpdate(sql, true)) {
				outObj.put("message", "This discount code could not be applied. Please contact support.");
				db.rollbackTransaction();
				return outObj;
			}
			outObj.put("status", true);
			
			db.commitTransaction();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}
	
	public JSONObject calculateDiscount(String hotelId, String orderId, Discount discount, LoyaltyOffer loyalty) {
		
		BigDecimal foodbill = new BigDecimal("0.0"), barBill = new BigDecimal("0.0"), taxableFoodBill = new BigDecimal("0.0");
		BigDecimal taxableBarBill = new BigDecimal("0.0"), nonTaxableFoodBill = new BigDecimal("0.0"), nonTaxableBarBill = new BigDecimal("0.0"); 
		JSONObject outObj = new JSONObject();
		BigDecimal discountAmount = new BigDecimal("0.0"), totalPerItem = new BigDecimal("0.0");
		
		ArrayList<OrderItem> orderItems = this.getOrderedItems(hotelId, orderId);
		
		for (OrderItem orderItem : orderItems) {
			totalPerItem = orderItem.getRate().multiply(new BigDecimal(orderItem.getQty()));
			if(discount != null && discount.getType() == AccessManager.DISCOUNT_TYPE_PERCENTAGE) {
				if(discount.getHasCollections()) {
					
				}else {
					if(orderItem.getStation() == "BAR") {
						
					}else {
						
					}
				}
			} else if(discount != null && discount.getType() == AccessManager.DISCOUNT_TYPE_FIXED) {
				
			}
		}
		
		return outObj;
	}
	
	public boolean removeDiscount(String hotelId, String orderId) {

		String sql = "UPDATE Orders SET discountCode = '' WHERE hotelId = '" + hotelId
					+ "' AND orderId = '" + orderId + "';";
			
		return db.executeUpdate(sql, true);
	}

	public boolean applyCustomerGST(String hotelId, String orderId, String gst) {

		String sql = "UPDATE Orders SET customerGST = '" + escapeString(gst) + "' WHERE hotelId = '" + hotelId
				+ "' AND orderId = '" + orderId + "';";
		return db.executeUpdate(sql, true);
	}

	// -----------------------------------------------Report

	public MonthReport getTotalOrdersForCurMonth(String hotelId, String duration, boolean visible) {
		String sql = "SELECT COUNT(orderId) as count FROM Orders WHERE orderDate LIKE'" + escapeString(duration)
				+ "%' AND hotelId='" + hotelId + "' ";
		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN + ";";
		return db.getOneRecord(sql, MonthReport.class, hotelId);
	}

	public MonthReport getBestWaiter(String hotelId, String duration, boolean visible) {

		String sql = "SELECT RTRIM(orderId, '0123456789:') AS user, SUBSTR(subOrderDate, 1, " + duration.length()
				+ ") AS duration, count(*) AS waitersOrders, employeeID FROM OrderItems, Users WHERE duration = '"
				+ escapeString(duration) + "' AND Users.userId = user ";
		if(!visible)
			sql += "AND OrderItems.billNo != '' ";
		sql += "GROUP BY user ORDER BY count(*) desc  LIMIT 1;";
		return db.getOneRecord(sql, MonthReport.class, hotelId);
	}

	public ArrayList<MonthReport> getWeeklyRevenue(String hotelId, boolean visible) {

		ArrayList<MonthReport> weeklyRevenue = new ArrayList<MonthReport>();

		String duration = "";

		for (int i = 0; i < 7; i++) {

			duration = getPreviousDateString(i);
			String sql = "SELECT SUM(total) AS totalSales FROM Payment WHERE orderDate = '" + duration + "' ";
			if(!visible)
				sql += "AND billNo != '';";
			MonthReport report = db.getOneRecord(sql, MonthReport.class, hotelId);
			weeklyRevenue.add(report);
		}
		return weeklyRevenue;
	}

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

	public MonthReport getMostOrderedItem(String hotelId, String duration, boolean visible) {

		String sql = "SELECT OrderItems.menuId as itemId, SUBSTR(subOrderDate, 1, " + duration.length()
				+ ") AS duration, MenuItems.title AS title, SUM(qty) AS orderCount, img FROM OrderItems, MenuItems "
				+ "WHERE duration = '" + escapeString(duration) + "' AND MenuItems.menuId = OrderItems.menuId "
				+ " AND MenuItems.category != 'Roti' ";
		if(!visible)
			sql += "AND OrderItems.billNo != '' ";
		sql += " GROUP BY itemId ORDER BY orderCount desc LIMIT 1;";
		
		return db.getOneRecord(sql, MonthReport.class, hotelId);
	}

	// Reports
	// expense-ap
	public ArrayList<Expense> getExpenseReport(String hotelid, String startDate, String endDate) {

		String sql = "SELECT serviceDate, amount, type, payee, "
				+ "REPLACE(REPLACE(REPLACE(Expenses.memo,CHAR(13),''),CHAR(10),''),',','|') AS memo " // replaced ','
																										// with '|' AND
																										// '/n' with ' '
				+ "FROM Expenses WHERE hotelId = '" + hotelid + "' AND type != 'PAYIN'" 
				+ "AND serviceDate BETWEEN '" + startDate + "' AND '" + appendEndDate(endDate) + "';";
		return db.getRecords(sql, Expense.class, hotelid);
	}

	// new-ap(in progress)
	public ArrayList<DailyDiscountReport> getDailyDiscountReport(String hotelId, String startDate, String endDate, boolean visible) {
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
				+ "(SELECT COUNT(orderId) FROM Payment WHERE hotelId='" + hotelId + "' AND orderDate BETWEEN '" 
				+ startDate + "' AND '" + appendEndDate(endDate) +"' AND cardType != 'VOID') AS totalOrders, " 
				+ "(SELECT ROUND(SUM(foodBill+barBill)*100)/100 FROM Payment WHERE hotelId='" + hotelId + "' AND orderDate BETWEEN '" 
				+ startDate + "' AND '" + appendEndDate(endDate) +"' AND cardType != 'VOID') AS grossSale, " 
				+ "(SELECT ROUND(SUM(foodDiscount+barDiscount)*100)/100 FROM Payment WHERE hotelId='" + hotelId + "' AND orderDate BETWEEN '" 
				+ startDate + "' AND '" + appendEndDate(endDate) +"' AND cardType != 'VOID') AS grossDiscount " 
				+ "FROM payment, discount WHERE payment.hotelId='" + hotelId
				+ "' AND payment.orderDate BETWEEN '" + startDate + "' AND '" + appendEndDate(endDate)
				+ "' AND payment.discountName=discount.name ";

		if(!visible)
			sql += " AND payment.billNo != '" + "'";
		
				sql += " GROUP BY discount.name)";
		System.out.println(sql);
		return db.getRecords(sql, DailyDiscountReport.class, hotelId);
	}

	// new-ap
	// refere googlesheets for logic...
	public ArrayList<GrossSaleReport> getGrossSalesReport(String hotelId, String startDate, String endDate, boolean visible) {
		endDate = appendEndDate(endDate);
		String sql = "SELECT ROUND(SUM(payment.foodBill+barBill)*100)/100 as grossTotal, "
				+ "ROUND(SUM(payment.barDiscount+payment.foodDiscount)*100)/100 as grossDiscount, "
				+ "ROUND(SUM(payment.loyaltyAmount)*100)/100 as grossLoyalty, "
				+ "ROUND(SUM(payment.complimentary)*100)/100 as grossComplimentary, "
				+ "ROUND(SUM(payment.gst)*100)/100 as grossGst, "
				+ "ROUND(SUM(payment.appPayment)*100)/100 as appPayment, "
				+ "ROUND(SUM(payment.cardPayment)*100)/100 as cardPayment, "
				+ "ROUND(SUM(payment.appPayment)*100)/100 as appPayment, "
				+ "ROUND(SUM(payment.cashPayment)*100)/100 as cashPayment, "
				+ "ROUND(SUM(payment.serviceCharge)*100)/100 as grossServiceCharge, "
				+ "ROUND(SUM(payment.total)*100)/100 as totalSale, "
				+ "ROUND((SUM(payment.total)-(Select SUM(Expenses.amount) From Expenses Where (expenses.type!='CASH_LIFT' OR expenses.type!='PAYIN') AND expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND expenses.hotelId='" + hotelId + "'))*100)/100 as NetSales, "
				+ "ROUND((Select SUM(Expenses.amount) From Expenses Where (expenses.type!='CASH_LIFT' OR expenses.type!='PAYIN') AND expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND expenses.hotelId='" + hotelId
				+ "')*100)/100 as grossExpenses, "
				+ "ROUND((Select SUM(Expenses.amount) From Expenses Where expenses.type=='PAYIN' AND expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND expenses.hotelId='" + hotelId
				+ "')*100)/100 as totalPayIns, "
				+ "ROUND((Select SUM(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='99' AND OrderItemLog.hotelId='"
				+ hotelId + "' AND OrderItemLog.dateTime BETWEEN '" + startDate + "' AND '" + endDate
				+ "')*100)/100  as sumVoids,  "
				+ "(Select COUNT(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='99' AND OrderItemLog.hotelId='"
				+ hotelId + "' AND OrderItemLog.dateTime BETWEEN '" + startDate + "' AND '" + endDate
				+ "')  as countVoids,  "
				+ "ROUND((Select SUM(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='100' AND OrderItemLog.hotelId='"
				+ hotelId + "' AND OrderItemLog.dateTime BETWEEN '" + startDate + "' AND '" + endDate
				+ "')*100)/100  as sumReturns, "
				+ "(Select COUNT(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='100' AND OrderItemLog.hotelId='"
				+ hotelId + "' AND OrderItemLog.dateTime BETWEEN '" + startDate + "' AND '" + endDate
				+ "')  as countReturns FROM Payment  Where payment.hotelId='" + hotelId + "' "
				+ "AND payment.orderDate BETWEEN '" + startDate + "' AND '" + endDate + "' AND payment.cardType != 'VOID'";
		if(!visible) 
			sql += " AND payment.billNo != '" + "'";
		
		System.out.println(sql);
		return db.getRecords(sql, GrossSaleReport.class, hotelId);
	}

	// newCollectionWiseReportA-ap (gross and totals...)
	public ArrayList<CollectionWiseReportA> getCollectionWiseReportA(String hotelId, String startDate, String endDate) {
		endDate = appendEndDate(endDate);
		String sql = "Select DISTINCT(MenuItems.category) AS collection, "
				+ "ROUND(SUM(OrderItems.qty*OrderItems.rate*100))/100 AS grossTotal, "
				+ "ROUND(AVG(OrderItems.qty*OrderItems.rate)*100)/100 AS averagePrice, "
				+ "COUNT(OrderItems.menuId) AS noOrdersAffected, "
				+ "(ROUND(CAST(COUNT(OrderItems.menuId) AS FLOAT)/(SELECT CAST(SUM(OrderItems.qty) AS FLOAT) FROM OrderItems WHERE OrderItems.hotelId='"
				+ hotelId + "' AND OrderItems.subOrderDate BETWEEN '" + startDate + "' AND '" + endDate
				+ "')*100*100)/100)||' %' AS noOrdersAffectedPer, SUM(OrderItems.qty) AS totalQuantityOrdered, "
				+ "(ROUND(CAST(SUM(OrderItems.qty) AS FLOAT)/(SELECT CAST(SUM(OrderItems.qty) AS FLOAT) FROM OrderItems WHERE OrderItems.hotelId='"
				+ hotelId + "' AND OrderItems.subOrderDate BETWEEN '" + startDate + "' AND '" + endDate
				+ "')*100*100)/100)||' %' AS totalQuantityOrderedPer FROM OrderItems,MenuItems "
				+ "WHERE OrderItems.menuId=MenuItems.menuId AND OrderItems.hotelId='" + hotelId + "' "
				+ "AND OrderItems.subOrderDate BETWEEN '" + startDate + "' AND '" + endDate + "' "
				+ "GROUP BY MenuItems.category;";
		System.out.println(sql);
		return db.getRecords(sql, CollectionWiseReportA.class, hotelId);
	}

	// newCollectionWiseReportB-ap (top/hot selling item!)
	public ArrayList<CollectionWiseReportB> getCollectionWiseReportB(String hotelId, String startDate, String endDate) {
		endDate = appendEndDate(endDate);
		String sql = "SELECT title AS topItemTitle, max(SUM) "
				+ "FROM (SELECT MenuItems.category, MenuItems.title, OrderItems.menuId, SUM(OrderItems.qty) AS SUM "
				+ "        FROM OrderItems, MenuItems "
				+ "        WHERE OrderItems.menuId = MenuItems.menuId AND OrderItems.subOrderDate BETWEEN '" + startDate
				+ "' AND '" + endDate + "'         GROUP BY MenuItems.menuId "
				+ "        ORDER BY MenuItems.title) GROUP BY category;";
		System.out.println(sql);
		return db.getRecords(sql, CollectionWiseReportB.class, hotelId);
	}

	// total operating cost-ap
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
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// total revenue-ap
	public ArrayList<DailyOperationReport> getDailyOperationReport2(String hotelId, String startDate, String endDate) {
		String sql = "SELECT (SELECT ROUND(SUM(TotalRevenue.total)*100)/100 FROM TotalRevenue WHERE TotalRevenue.hotelId='"
				+ hotelId + "' AND TotalRevenue.serviceDate BETWEEN '" + startDate + "' AND '" + endDate
				+ "' ) AS totalRevenue, ROUND(SUM(payment.total)*100)/100 as grossTotal, "
				+ "ROUND(SUM(payment.foodDiscount+ payment.barDiscount)*100)/100 as grossDiscount, "
				+ "ROUND(SUM(payment.gst)*100)/100 as grossTaxes, "
				+ "ROUND(SUM(payment.serviceCharge)*100)/100 as grossServiceCharge, "
				+ "ROUND(ROUND((Select SUM(payment.total) FROM payment WHERE payment.hotelId='" + hotelId
				+ "' AND Payment.orderDate  BETWEEN '" + startDate + "' AND '" + endDate
				+ "')*100)/100 - ROUND((Select SUM(Expenses.amount) From Expenses WHERE expenses.type!='CASH_LIFT' AND Expenses.serviceDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "' AND expenses.hotelId='" + hotelId
				+ "')*100)/100)*100/100 as NetSales FROM Payment WHERE payment.hotelId='" + hotelId + "' "
				+ "AND payment.orderDate BETWEEN '" + startDate + "' AND '" + endDate + "';";
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// Total Operating Margin-ap //implementvisible
	public ArrayList<DailyOperationReport> getDailyOperationReport3(String hotelId, String startDate, String endDate) {
		String sql = "SELECT ROUND((SUM(TotalRevenue.total) - (SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.hotelId='"
				+ hotelId + "' AND Expenses.serviceDate BETWEEN '" + startDate + "' AND '" + appendEndDate(endDate)
				+ "'))*100)/100 AS totalOperatingMargin, "
				+ "ROUND((SELECT SUM(Payment.Total) FROM Payment WHERE Payment.hotelId='" + hotelId
				+ "' AND Payment.orderDate BETWEEN '" + startDate + "' AND '" + endDate + "')*100)/100 AS paidIn, "
				+ "ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type!='CASH_LIFT' AND expenses.hotelId='"
				+ hotelId + "' AND Expenses.serviceDate BETWEEN '" + startDate + "' AND '" + appendEndDate(endDate)
				+ "')*100)/100 AS paidOut FROM TotalRevenue Where hotelId='" + hotelId + "' "
				+ "AND TotalRevenue.serviceDate BETWEEN '" + startDate + "' AND '" + endDate + "';";
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// Operating Metrics-ap
	// main 3
	public ArrayList<DailyOperationReport> getDailyOperationReport4(String hotelId, String startDate, String endDate, boolean visible) {

		String billNo = visible?"Payment.billNo2":"Payment.billNo";
		
		String sql = "SELECT Distinct Orders.serviceType AS serviceType, "
				+ "SUM(Payment.total)/SUM(Orders.numberOfGuests) AS AvgAmountPerGuest, "
				+ "(SUM(Payment.total)/COUNT("+billNo+")) AS AvgAmountPerCheck, SUM(Payment.total) AS Total, "
				+ "SUM(Orders.numberOfGuests) AS noOfGuests, COUNT("+billNo+") AS noOfBills "
				+ "FROM Orders, Payment WHERE Orders.hotelId='" + hotelId + "' "
				+ "AND Orders.orderId=Payment.orderId AND Orders.orderDate BETWEEN '" + startDate + "' AND '" + endDate + "' ";

		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		sql += "GROUP BY Orders.serviceType ";
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// tables turned-ap
	public ArrayList<DailyOperationReport> getDailyOperationReport5(String hotelId, String startDate, String endDate, boolean visible) {
		
		String billNo = visible?"Payment.billNo2":"Payment.billNo";
		
		String sql = "SELECT SUM(Payment.total)/COUNT(distinct "+billNo+") as AvgAmountPerTableTurned "
				+ "FROM Payment,Orders WHERE payment.orderId=orders.orderId AND orders.inhouse='1' "
				+ "AND orders.serviceType=serviceType AND orders.hotelId='" + hotelId + "' "
				+ "AND orders.orderDate BETWEEN '" + startDate + "' AND '" + endDate + "' ";

		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		sql += "GROUP BY Orders.serviceType ";
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// voids-ap
	public ArrayList<DailyOperationReport> getDailyOperationReport6(String hotelId, String startDate, String endDate) {
		String sql = "SELECT COUNT(orders.orderId) AS voids FROM orders WHERE orders.state='99' "
				+ "AND orders.hotelId='" + hotelId + "' AND orders.orderDate BETWEEN '" + startDate + "' and '"
				+ endDate + "' GROUP BY Orders.serviceType;";
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// returns-ap
	public ArrayList<DailyOperationReport> getDailyOperationReport7(String hotelId, String startDate, String endDate) {
		String sql = "SELECT COUNT(Distinct OrderItemLog.orderId) AS returns FROM orderitemlog, orders "
				+ "WHERE orderitemlog.state='100' AND orderitemlog.orderId=orders.orderId "
				+ "AND orderitemlog.hotelId='" + hotelId + "' AND orderitemlog.dateTime BETWEEN '" + startDate
				+ "' AND '" + appendEndDate(endDate) + "' GROUP BY Orders.serviceType;";
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	// Jason
	// Discount Report-ap(edited)
	public ArrayList<DiscountReport> getDiscountReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT payment.discountName, payment.orderDate, "
				+ "ROUND(payment.foodDiscount*100)/100 AS foodDiscount, "
				+ "ROUND(payment.barDiscount*100)/100 AS barDiscount, "
				+ "ROUND((payment.foodDiscount+payment.barDiscount)*100)/100 AS totalDiscount, "
				+ "ROUND((payment.total+payment.foodDiscount+payment.barDiscount)*100)/100 AS total, "
				+ "customerName ,"
				+ "payment.total AS discountedTotal "
				+ "FROM payment, orders WHERE payment.orderid = orders.orderid AND discountName!='' "
				+ "AND payment.orderDate BETWEEN '" + startDate + "' AND '" + endDate + "' AND payment.hotelId='"
				+ hotelId + "';";
		System.out.println(sql);
		return db.getRecords(sql, DiscountReport.class, hotelId);
	}

	// Jason
	// itemwise-ap(hot selling items/menu category)
	public ArrayList<ItemWiseReport> getItemwiseReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT Menuitems.menuid AS menuId, category AS category, title AS title, "
				+ "SUM(qty) AS qty FROM MenuItems, Orderitems, Orders WHERE Menuitems.menuid = Orderitems.menuid "
				+ "AND Orders.orderDate BETWEEN '" + startDate + "' AND '" + appendEndDate(endDate) + "' "
				+ "AND Orderitems.hotelId ='" + hotelId + "' AND MenuItems.station!='Bar' "
				+ "AND Orderitems.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid UNION ALL "
				+ "SELECT menuitems.menuid AS menuId, category AS category, title AS title, "
				+ "SUM(OrderItemLog.quantity) AS qty FROM MenuItems, OrderItemLog, Orders WHERE menuitems.menuid = OrderItemLog.menuid "
				+ "AND Orders.orderDate BETWEEN '" + startDate + "' AND '" + appendEndDate(endDate) + "' "
				+ "AND OrderItemLog.hotelId='" + hotelId + "' AND menuItems.station!='Bar' AND OrderItemLog.state == 50 "
				+ "AND OrderItemLog.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid "
				+ "ORDER BY category, MenuItems.title;";
		System.out.println(sql);
		return db.getRecords(sql, ItemWiseReport.class, hotelId);
	}

	// liquor-ap(hot selling items/menu category)
	public ArrayList<ItemWiseReport> getLiquorReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT menuitems.menuid AS menuId, category AS category, title AS title, "
				+ "SUM(qty) AS qty FROM MenuItems, Orderitems, Orders WHERE menuitems.menuid = orderitems.menuid "
				+ "AND Orders.orderDate BETWEEN '" + startDate + "' AND '" + appendEndDate(endDate) + "' "
				+ "AND Orderitems.hotelId='" + hotelId + "' AND menuItems.station='Bar' "
				+ "AND Orderitems.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid UNION ALL "
				+ "SELECT menuitems.menuid AS menuId, category AS category, title AS title, "
				+ "SUM(OrderItemLog.quantity) AS qty FROM MenuItems, OrderItemLog, Orders WHERE menuitems.menuid = OrderItemLog.menuid "
				+ "AND Orders.orderDate BETWEEN '" + startDate + "' AND '" + appendEndDate(endDate) + "' "
				+ "AND OrderItemLog.hotelId='" + hotelId + "' AND menuItems.station='Bar' AND OrderItemLog.state == 50 "
				+ "AND OrderItemLog.orderId == Orders.orderId "
				+ "GROUP BY menuitems.menuid "
				+ "ORDER BY category, MenuItems.title;";
		System.out.println(sql);
		return db.getRecords(sql, ItemWiseReport.class, hotelId);
	}

	//Void Report
	public ArrayList<Order> getVoidOrderReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT Orders.id, Orders.billNo, Orders.orderDate, Orders.waiterId, Orders.inhouse, "
				+ "Orders.reason, Payment.foodBill, Payment.barBill, Payment.foodBill + Payment.barBill AS total FROM Orders, Payment WHERE Orders.state = 99 "
				+ "AND Orders.orderDate BETWEEN '" + startDate + "' AND '" + endDate + "' "
				+ "AND Orders.hotelId='" + hotelId + "' AND Orders.orderId == Payment.orderId ORDER BY Orders.id;";
		System.out.println(sql);
		return db.getRecords(sql, Order.class, hotelId);
	}

	//Void Report
	public ArrayList<Order> getNCOrderReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT Orders.id, Orders.billNo, Orders.orderDate, Orders.waiterId, Orders.inhouse, "
				+ "Orders.reference, Payment.foodBill, Payment.barBill, Payment.foodBill + Payment.barBill AS total FROM Orders, Payment WHERE Orders.inhouse = 4 "
				+ "AND Orders.orderId == Payment.orderId "
				+ "AND Orders.orderDate BETWEEN '" + startDate + "' AND '" + endDate + "' "
				+ "AND Orders.hotelId='" + hotelId + "' ORDER BY Orders.id;";
		System.out.println(sql);
		return db.getRecords(sql, Order.class, hotelId);
	}
	
	//Returned Item  Report
	public ArrayList<ReturnedItemsReport> getReturnedItemsReport(String hotelId, String startDate, String endDate, boolean visible) {
		
		String billNo = visible?"Orders.billNo2 AS billNo":"Orders.billNo";
		
		String sql = "SELECT Orders.id, "+billNo+", Orders.orderDate, Orders.waiterId, Orders.inhouse, "
				+ "MenuItems.title, OrderItemLog.dateTime, OrderItemLog.quantity, OrderItemLog.reason, OrderItemLog.rate "
				+ ", (OrderItemLog.rate*OrderItemLog.quantity) AS total FROM Orders, OrderItemlog, MenuItems WHERE OrderItemlog.state = 100 "
				+ "AND OrderItemlog.dateTime BETWEEN '" + startDate + "%' AND '" + endDate + "%' "
				+ "AND Orders.hotelId =='" + hotelId + "' AND OrderItemlog.orderId == Orders.orderId "
				+ "AND OrderItemLog.menuId == MenuItems.menuId ";
		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		sql += " ORDER BY OrderItemlog.id;";
		System.out.println(sql);
		return db.getRecords(sql, ReturnedItemsReport.class, hotelId);
	}
	
	//Returned Item  Report
	public ArrayList<ReturnedItemsReport> getComplimentaryItemsReport(String hotelId, String startDate, String endDate, boolean visible) {

		String billNo = visible?"Orders.billNo2 AS billNo":"Orders.billNo";
		
		String sql = "SELECT Orders.id, "+billNo+", Orders.orderDate, Orders.waiterId, Orders.inhouse, "
				+ "MenuItems.title, OrderItemLog.dateTime, OrderItemLog.quantity, OrderItemLog.rate "
				+ ", (OrderItemLog.rate*OrderItemLog.quantity) AS total FROM Orders, OrderItemlog, MenuItems WHERE OrderItemlog.state = 50 "
				+ "AND OrderItemlog.dateTime BETWEEN '" + startDate + "%' AND '" + endDate + "%' "
				+ "AND Orders.hotelId =='" + hotelId + "' AND OrderItemlog.orderId == Orders.orderId "
				+ "AND OrderItemLog.menuId == MenuItems.menuId ";
		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		sql += " ORDER BY OrderItemlog.id;";
		System.out.println(sql);
		return db.getRecords(sql, ReturnedItemsReport.class, hotelId);
	}

	public PaymentWiseSalesReport getPaymentWiseSalesReport(String hotelId, String startDate, String endDate,
			Integer i, boolean visible) {

		String[] cardTypes = this.getEnums(CardType.class);
		String[] onlinePaymentTypes = this.getEnums(OnlinePaymentType.class);
		
		String sql = "SELECT ROUND(SUM(Payment.foodBill)*100)/100 AS foodBill, ROUND(SUM(Payment.barBill)*100)/100 AS barBill, "
				+ "ROUND(SUM(Payment.total)*100)/100 AS total, SUM(Orders.numberOfGuests) AS cover, ROUND(SUM(Payment.cashPayment)*100)/100 AS cash, "
				+ "ROUND(SUM(Payment.cardPayment)*100)/100 AS card, ROUND(SUM(Payment.appPayment)*100)/100 AS app";
		
		for (String paymentType : cardTypes) {
			sql += ", (SELECT ifnull(ROUND(SUM(Payment.cardPayment)*100)/100, 0) FROM Payment,Orders WHERE Payment.cardType LIKE '%"+paymentType+"%' AND Payment.hotelId='"
				+ hotelId + "' AND Payment.orderDate BETWEEN '" + startDate + "' AND '" + endDate
				+ "' AND Orders.inhouse='" + i + "' AND Payment.orderId = Orders.orderId ) AS "+paymentType;
		}
		for (String paymentType : onlinePaymentTypes) {
			sql += ", (SELECT ifnull(ROUND(SUM(Payment.appPayment)*100)/100, 0) FROM Payment,Orders WHERE Payment.cardType LIKE '%"+paymentType+"%' AND Payment.hotelId='"
				+ hotelId + "' AND Payment.orderDate BETWEEN '" + startDate + "' AND '" + endDate
				+ "' AND Orders.inhouse='" + i + "' AND Payment.orderId = Orders.orderId ) AS "+paymentType;
		}
		sql += " FROM Payment, Orders WHERE Payment.orderId = Orders.orderId AND Payment.hotelId='" + hotelId
				+ "' AND Payment.orderdate BETWEEN '" + startDate + "' AND '" + endDate + "' " + "AND Orders.inhouse='"
				+ i + "' ";
		
		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		
		System.out.println(sql);
		return db.getOneRecord(sql, PaymentWiseSalesReport.class, hotelId);
	}
	
	public String[] getEnums(Class<? extends Enum<?>> e) {
	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}

	public ArrayList<Attendance> getAttendanceReport(String hotelId, String startDate, String endDate) {

		String sql = "SELECT Employee.employeeId, Employee.salary, Employee.firstName, Employee.surName, Attendance.shift, Attendance.checkInDate, Attendance.reason, Attendance.authorisation, Attendance.checkInTime, Attendance.checkOutTIme, "
				+ "REPLACE(REPLACE(REPLACE(Attendance.isPresent, " + PRESENT + ", \"PRESENT\") , " + ABSENT
				+ ", \"ABSENT\"), " + EXCUSED + ", \"EXCUSED\") AS attendanceStr, "
				+ "	(SELECT COUNT(isPresent) FROM Attendance WHERE 	Attendance.checkInDate between '" + startDate
				+ "' AND '" + endDate + "' 	AND Attendance.employeeId = Employee.employeeId"
				+ "	AND Attendance.authorisation = 1 	AND Attendance.isPresent=2 " + "	AND Attendance.hotelId = '"
				+ hotelId + "' "
				+ "	GROUP BY Attendance.employeeId ) AS excusedCount,	(SELECT COUNT(isPresent) FROM Attendance  "
				+ "	WHERE Attendance.checkInDate between '" + startDate + "' AND '" + endDate + "' "
				+ "	AND Attendance.employeeId = Employee.employeeId	AND Attendance.authorisation = 1 "
				+ "	AND Attendance.isPresent=3 	AND Attendance.hotelId = '" + hotelId + "' "
				+ "	GROUP BY Attendance.employeeId ) AS absentCount,	(SELECT COUNT(isPresent) FROM Attendance "
				+ "	WHERE Attendance.checkInDate between '" + startDate + "' AND '" + endDate + "' "
				+ "	AND Attendance.employeeId = Employee.employeeId	AND Attendance.authorisation = 1 "
				+ "	AND Attendance.isPresent=1 	AND Employee.hotelId = '" + hotelId + "' "
				+ "	GROUP BY Attendance.employeeId ) AS presentCount FROM Employee, Attendance "
				+ "WHERE Employee.employeeId = Attendance.employeeId AND Attendance.checkInDate between '" + startDate
				+ "' AND '" + endDate + "' AND Attendance.authorisation = 1 " + "AND Employee.hotelId = '" + hotelId
				+ "' " + "ORDER BY Attendance.employeeId, Attendance.checkInDate, Attendance.shift";
		System.out.println(sql);
		return db.getRecords(sql, Attendance.class, hotelId);
	}

	public ArrayList<Attendance> getAttendanceReportB(String hotelId, String startDate, String endDate) {

		String sql = "SELECT distinct Attendance.checkInDate FROM Attendance "
				+ "WHERE Attendance.checkInDate between '" + startDate + "' AND '" + endDate + "' "
				+ "AND Attendance.authorisation = 1 AND Attendance.hotelId = '" + hotelId + "' "
				+ "ORDER BY Attendance.checkInDate";
		System.out.println(sql);
		return db.getRecords(sql, Attendance.class, hotelId);
	}

	public Report getTotalSalesForService(String hotelId, String serviceDate, String serviceType, String section, boolean visible) {
		
		String sql2 = "";
		if(!visible)
			sql2 = " AND Orders.state != "+ ORDER_STATE_HIDDEN;
		
		String sql = "SELECT ROUND(SUM(Payment.complimentary+Payment.loyaltyAmount+payment.foodDiscount+payment.barDiscount+Payment.foodBill+Payment.barBill+gst+VATBAR+serviceCharge)*100)/100 AS grossTotal, "
				+ "ROUND(SUM(Payment.foodBill)*100)/100 AS foodBill, "
				+ "ROUND(SUM(Payment.barBill)*100)/100 AS barBill, "
				+ "ROUND(SUM(Payment.total)*100)/100 AS total, "
				+ "ROUND(SUM(Payment.foodDiscount)*100)/100 AS foodDiscount, "
				+ "ROUND(SUM(Payment.barDiscount)*100)/100 AS barDiscount, "
				+ "ROUND(SUM(Payment.gst)*100)/100 AS gst, "
				+ "ROUND(SUM(Payment.serviceCharge)*100)/100 AS serviceCharge, "
				+ "ROUND(SUM(Payment.VATBAR)*100)/100 AS VATBAR, "
				+ "ROUND(SUM(Payment.complimentary)*100)/100 AS complimentary, "
				+ "ROUND(SUM(Payment.loyaltyAmount)*100)/100 AS loyalty, "
				+ "SUM(Orders.printCount) AS printCount, "
				+ "(SELECT SUM(Orders.printCount-1) FROM Orders WHERE printCount >1 AND orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ") AS reprints, COUNT(*) AS orderCount, "
				+ "ROUND(SUM(Payment.cashPayment)*100)/100 AS cashPayment, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 1 AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS inhouse, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 0 AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS homeDelivery, "
				+ "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' " + sql2
				+ ")*100)/100 AS takeAway, "
				+ "ROUND((SELECT SUM(Payment.foodBill+Payment.barBill) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND Orders.orderDate = '"
				+ serviceDate + "' AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + sql2 + "' AND cardType = 'NON_CHARGEABLE' "
				+ ")*100)/100 AS nc FROM Payment, Orders WHERE Payment.orderId = Orders.orderId "
				+ "AND Orders.orderDate = '" + serviceDate + "' AND Orders.hotelId = '" + hotelId + "' "
				+ "AND Orders.serviceType = '" + serviceType + "'  AND Payment.cardType != 'VOID'"
				+ sql2;

		Hotel hotel = this.getHotelById(hotelId);
		if(hotel.hasSection())
			sql += " AND Payment.section = '" + section + "';";
		else
			sql += ";";
		return db.getOneRecord(sql, Report.class, hotelId);
	}

	public Report getCashCardSales(String hotelId, String serviceDate, String serviceType) {

		String sql = "SELECT ROUND(SUM(Payment.cashPayment)*100)/100 AS cashPayment, "
				+ "ROUND(SUM(Payment.cardPayment)*100)/100 AS cardPayment FROM Payment, Orders WHERE Payment.orderId = Orders.orderId "
				+ "AND Orders.orderDate = '" + serviceDate + "' AND Orders.hotelId = '" + hotelId + "' "
				+ "AND Orders.serviceType = '" + serviceType + "'  AND Payment.cardType != 'VOID' AND Orders.state != "+ORDER_STATE_HIDDEN+" ;";

		return db.getOneRecord(sql, Report.class, hotelId);
	}

	public ArrayList<Expense> getCashExpenses(String hotelId, String serviceDate, String serviceType, String section) {
		Hotel hotel = this.getHotelById(hotelId);
		String sql = "SELECT * FROM Expenses WHERE accountName = 'CASH_DRAWER' AND hotelId = '" + hotelId + "' "
				+ "AND serviceDate = '" + serviceDate + "' AND serviceType = '" + serviceType;
		if(hotel.hasSection())
			sql += "' AND section = '" + section + "';";
		else
			sql += "';";

		return db.getRecords(sql, Expense.class, hotelId);
	}

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

	public int getAppPaymentByType(String hotelId, String serviceDate, String serviceType, String cardType) {

		String sql = "SELECT SUM(Payment.appPayment) AS entityId FROM Payment, Orders "
				+ "WHERE Payment.orderId = Orders.orderId AND Orders.orderDate = '" + serviceDate + "' "
				+ "AND Orders.hotelId = '" + hotelId + "' AND Orders.serviceType = '" + serviceType + "' "
				+ "AND Payment.cardType LIKE '%" + cardType + "%';";

		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

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

	public BigDecimal getTotalCardPayment(String hotelId, String serviceDate, String serviceType) {

		String sql = "SELECT SUM(Payment.cardPayment) as entityId FROM Payment,Orders "
				+ "WHERE Orders.orderDate = '" + serviceDate + "' AND Orders.serviceType = '" + serviceType + "' "
				+ "AND Orders.hotelId = '" + hotelId + "' AND Payment.orderId = Orders.orderId;";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return new BigDecimal("0.0");
	}

	public BigDecimal getTotalAppPayment(String hotelId, String serviceDate, String serviceType) {

		String sql = "SELECT SUM(Payment.appPayment) as entityId FROM Payment,Orders "
				+ "WHERE Orders.orderDate = '" + serviceDate + "' "
				+ "AND Orders.serviceType = '" + serviceType + "' AND Orders.hotelId = '" + hotelId + "' "
				+ "AND Payment.orderId = Orders.orderId;";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return new BigDecimal("0.0");
	}

	public ArrayList<Report> getSaleSummaryReport(String hotelId, String startDate, String endDate, boolean visible) {

		String billNo = visible?"Payment.billNo2":"Payment.billNo";
		
		String sql = "SELECT ROUND(Payment.foodBill*100)/100 AS foodBill, "
				+ "ROUND(Payment.barBill*100)/100 AS barBill, "
				+ billNo +" AS billNo, "
				+ "ROUND(Payment.total*100)/100 AS total, "
				+ "ROUND(Payment.foodDiscount*100)/100 AS foodDiscount, "
				+ "ROUND(Payment.barDiscount*100)/100 AS barDiscount, "
				+ "ROUND(Payment.serviceTax*100)/100 AS serviceTax, "
				+ "ROUND(Payment.serviceCharge*100)/100 AS serviceCharge, "
				+ "ROUND(Payment.tip*100)/100 AS tip, "
				+ "ROUND(Payment.gst*100)/100 AS gst, "
				+ "Orders.numberOfGuests AS cover, "
				+ "Orders.inhouse AS inhouse, "
				+ "Orders.takeAwayType AS takeAwayType, "
				+ "Orders.state AS state, "
				+ "Orders.tableId AS tableId, "
				+ "Orders.orderDate AS orderDate, "
				+ "ROUND(Payment.cashPayment*100)/100 AS cashPayment, "
				+ "ROUND(Payment.cardPayment*100)/100 AS cardPayment, "
				+ "ROUND(Payment.appPayment*100)/100 AS appPayment, "
				+ "Payment.cardType AS cardType, "
				+ "Orders.section AS section "
				+ "FROM Payment, Orders WHERE Payment.orderId = Orders.orderId "
				+ "AND Payment.hotelId = '" + hotelId;

		if (endDate.equals("")) {
			sql += "' AND Orders.orderDate ='" + startDate + "' ";
		} else {
			sql += "' AND Orders.orderDate BETWEEN '" + startDate + "' AND '" + endDate + "' ";
		}
		if(!visible)
			sql += "AND Orders.state != " + ORDER_STATE_HIDDEN;
		
		return db.getRecords(sql, Report.class, hotelId);
	}

	public ArrayList<CustomerReport> getCustomerReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT ROUND(SUM(OrderItems.rate*qty)*100)/100 AS totalSpent, "
				+ "ROUND(SUM(OrderItems.rate*qty)/SUM(Orders.numberOfGuests)) AS spentPerPax, "
				+ "ROUND(SUM(OrderItems.rate*qty)/COUNT(Orders.orderId)) AS spentPerWalkin, "
				+ "SUM(Orders.numberOfGuests) AS totalGuests, Orders.customerName AS customerName, "
				+ "Orders.customerNumber AS mobileNo, COUNT(Orders.orderId) AS totalWalkins "
				+ "FROM OrderItems, Orders WHERE OrderItems.orderId == Orders.orderId AND Orders.hotelId = '" + hotelId
				+ "' AND OrderItems.hotelId = '" + hotelId + "' AND orderDate BETWEEN '" + startDate + "' AND '"
				+ endDate + "' GROUP BY Orders.customerNumber;";

		System.out.println(sql);
		return db.getRecords(sql, CustomerReport.class, hotelId);
	}

	public ArrayList<CustomerReport> getCustomerReviewReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT Customers.customer, Customers.mobileNo, Orders.rating_ambiance, Orders.rating_hygiene, Orders.rating_qof, "
				+ "Orders.rating_service, REPLACE(Orders.reviewSuggestions, ',', ';') AS reviewSuggestions, payment.total, Orders.billNo, Orders.numberOfGuests "
				+ "FROM Customers, Orders, Payment "
				+ "WHERE Customers.mobileNo == Orders.customerNumber AND Orders.hotelId == '"+hotelId+"' "
				+ "AND Orders.orderId == Payment.orderId AND Orders.orderdate between '"+startDate+"' and '"+endDate+"' "
				+ "AND mobileNo != '' AND rating_ambiance is not null AND (rating_ambiance+rating_hygiene+rating_qof+rating_service) > 0";

		System.out.println(sql);
		return db.getRecords(sql, CustomerReport.class, hotelId);
	}

	public Report getDailyIncome(String hotelId, String startDate, String endDate, int inhouse) {

		String sql = "SUM(Payment.total) AS total, SUM(Orders.numberOfGuests) AS pax, "
				+ "COUNT(Orders.Id) AS checks, SUM(Payment.foodDiscount) AS foodDiscount, SUM(Payment.barDiscount) AS foodDiscount, "
				+ "SUM(Payment.serviceCharge) AS serviceCharge SUM(Payment.serviceTax) AS serviceTax "
				+ "SUM(Payment.gst) AS gst SUM(Payment.VATFOOD) AS VATFOOD SUM(Payment.VATBAR) AS VATBAR "
				+ "SUM(Payment.sbCess) AS sbCess SUM(Payment.kkCess) AS kkCess FROM Payment, Orders "
				+ "WHERE Payment.orderId = Orders.orderId AND Orders.inhouse = " + inhouse + "AND Orders.hotelId = '"
				+ hotelId;

		if (endDate.equals("")) {
			sql += "' AND Orders.orderDate ='" + startDate + "';";
		} else {
			sql += "' AND Orders.orderDate BETWEEN '" + startDate + "' AND '" + endDate + "';";
		}
		return db.getOneRecord(sql, Report.class, hotelId);
	}

	public ArrayList<Expense> getDailyExpense(String hotelId, String startDate, String endDate) {

		String sql = "SELECT SUM(Expense.amount) AS amount, SUM(Expense.type) AS type FROM Expense "
				+ "WHERE hotelId = '" + hotelId;

		if (endDate.equals("")) {
			sql += "' AND Expense.date LIKE'" + startDate + "%';";
		} else {
			sql += "' AND Expense.date BETWEEN '" + startDate + "' AND '" + endDate + "';";
		}
		return db.getRecords(sql, Expense.class, hotelId);
	}

	// -------------------------------------------Bank

	public ArrayList<Bank> getBankAccounts(String hotelId) {
		String sql = "SELECT * FROM Bank WHERE hotelId='" + hotelId + "';";
		return db.getRecords(sql, Bank.class, hotelId);
	}

	public BigDecimal getCashBalance(String hotelId, String section) {
		String sql = "";
		
		if(section.equals("")) {
			sql = "SELECT SUM(balance) as entityId FROM Bank WHERE hotelId='" + hotelId + "' AND accountNumber = "
					+ CASH_ACCOUNT + ";";
		}else {
			sql = "SELECT balance as entityId FROM Bank WHERE hotelId='" + hotelId + "' AND accountNumber = "
					+ CASH_ACCOUNT + " AND section = '"+section+"';";
			
		}
		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	public boolean updateCashBalance(String hotelId, BigDecimal balance) {
		String sql = "UPDATE Bank SET balance = " + balance + " WHERE hotelId='" + hotelId + "' AND accountNumber = "
				+ CASH_ACCOUNT + ";";
		return db.executeUpdate(sql, true);
	}

	// -------------------------------------------Notifications

	private int getNextNotificationId(String userId, String hotelId) {
		String sql = "SELECT * FROM Notification WHERE orderId LIKE '" + userId + ":%' AND hotelId = '" + hotelId
				+ "' ORDER BY notId";
		ArrayList<Notification> notifs = db.getRecords(sql, Notification.class, hotelId);
		if (notifs.size() == 0) {
			return 0;
		}
		return notifs.get(notifs.size() - 1).mNotId + 1;
	}

	public Notification getNextNotification(String hotelId, String userId) {
		String sql = "SELECT * FROM Notification WHERE hotelId='" + hotelId + "' AND orderId LIKE '" + userId
				+ ":%' ORDER BY notId";
		ArrayList<Notification> notifs = db.getRecords(sql, Notification.class, hotelId);
		if (notifs.size() == 0) {
			return null;
		} else {
			sql = "DELETE FROM Notification WHERE notId=" + notifs.get(0).mNotId + " AND hotelId='" + hotelId
					+ "' AND orderId LIKE '" + userId + ":%';";
			db.executeUpdate(sql, true);
			return notifs.get(0);
		}
	}

	public MPNotification getMPNotification(String hotelId) {

		String sql = "SELECT COUNT(Stock.sku) AS outOfStock FROM Material, Stock WHERE Material.hotelId= '" + hotelId
				+ "' AND Material.sku == Stock.sku AND Stock.quantity <= Material.minQuantity ";

		MPNotification notification = new MPNotification();

		notification.outOfStock = db.getOneRecord(sql, MPNotification.class, hotelId).getOutOfStock();
		notification.hotelId = hotelId;

		sql = "SELECT COUNT(Id) AS checkOutOrders FROM Orders WHERE hotelId = '" + hotelId + "' AND state = 1";

		notification.checkoutOrders = db.getOneRecord(sql, MPNotification.class, hotelId).getCheckoutOrders();

		return notification;
	}

	// -------------------------------------------Expenses
	
	public boolean addExpense(String hotelId, BigDecimal expenseAmount, String details, String payeeName, int cheque,
			String paymentType, String expenseType, String bankAccount, String userId, String employeeId) {

		String sql = "INSERT INTO Expenses "
				+ "(hotelId, type, serviceDate, serviceType, amount, userId, payee, memo, chequeNo, accountName, "
				+ "paymentType, employeeId) "
				+ "VALUES('" + escapeString(hotelId) + "', '" + escapeString(expenseType) + "', '"
				+ getServiceDate(hotelId) + "', '" + getServiceType(hotelId) + "', " + expenseAmount
				+ ", '" + escapeString(userId) + "', '" + escapeString(payeeName) + "', '" + escapeString(details)
				+ "', " + Integer.toString(cheque) + ", '" + bankAccount + "', '" + paymentType + "', '"
				+ escapeString(employeeId) + "');";
		return db.executeUpdate(sql, true);
	}
	
	public ArrayList<Expense> getExpenses(String hotelId) {

		String sql = "SELECT * FROM Expenses WHERE hotelId = '" + hotelId + "' AND type != 'PAYIN' AND serviceDate = '"
				+ this.getServiceDate(hotelId) + "';";
		return db.getRecords(sql, Expense.class, hotelId);
	}
	
	public Expense getExpense(String hotelId, int expenseId) {

		String sql = "SELECT * FROM Expenses WHERE hotelId = '" + hotelId + "' AND id = " + expenseId;
		
		return db.getOneRecord(sql, Expense.class, hotelId);
	}
	
	public ArrayList<Expense> getPayIns(String hotelId) {

		String sql = "SELECT * FROM Expenses WHERE hotelId = '" + hotelId + "' AND type == 'PAYIN' AND serviceDate = '"
				+ this.getServiceDate(hotelId) + "';";
		return db.getRecords(sql, Expense.class, hotelId);
	}
	
	public boolean deleteExpense(String hotelId , int expenseId, String section, String paymentType, BigDecimal amount) {

		String sql = "DELETE FROM Expenses WHERE hotelId = '"+hotelId+"' AND id = "+ expenseId;
		return db.executeUpdate(sql, true);
	}
	
	public boolean clearBigDecimal(String hotelId , int expenseId, String userId, String authoriser, String section
			, String accountName, String paymentType, BigDecimal amount, String employeeId) {

		this.deleteExpense(hotelId, expenseId, section, paymentType, amount);
		
		return this.updateTransactionHistory(hotelId, "DEBIT", ExpenseType.FLOAT.toString(), accountName, paymentType, 
				amount, employeeId, userId, authoriser);
	}

	// -------------------------------------------Transaction History
	
	public ArrayList<TransactionHistory> getTransactionHistory(String hotelId) {

		String sql = "SELECT * FROM TransactionHistory WHERE hotelId = '" + hotelId + "' AND serviceDate = '"
				+ this.getServiceDate(hotelId) + "';";
		return db.getRecords(sql, TransactionHistory.class, hotelId);
	}
	
	public BigDecimal getBigDecimalForDeliveryBoy(String hotelId, String employeeId) {
		String sql = "SELECT amount AS entityId FROM TransactionHistory WHERE hotelId = '" + hotelId + "' AND serviceDate = '"
				+ this.getServiceDate(hotelId) + "' AND employeeId = '"+employeeId+"';";
		
		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		return entity==null?new BigDecimal("0.0"):entity.getId();
	}
	
	public Boolean updateTransactionHistory(String hotelId, String trType, String trDetail, String trAccountName, 
			String paymentType, BigDecimal amount, String employeeId, String userId, String authoriser) {

		BigDecimal accountBalance = this.getAccountBalance(hotelId, employeeId);
		if (trType.equals("CREDIT"))
			accountBalance.add(amount);
		else
			accountBalance.add(amount);

		String sql = "INSERT INTO TransactionHistory ('trType', 'trDetail', 'trAccountName', 'paymentType', 'amount', 'balance',"
				+ "'trDate', 'userId', 'authoriser', 'employeeId', 'hotelId', 'serviceDate') VALUES ('" + trType + "', '" + escapeString(trDetail) + "', '" 
				+ trAccountName + "', '" + paymentType + "', " + amount + ", " + accountBalance + ", '" + LocalDateTime.now() + "', '" 
				+ userId + "', '" + authoriser + "', '" + employeeId + "', '" + escapeString(hotelId) + "', '" + this.getServiceDate(hotelId) + "');";

		db.executeUpdate(sql, true);
		
		sql = "UPDATE Employee SET accountBalance = "+accountBalance+ " WHERE hotelId = '"+hotelId+"' AND employeeId = '"+employeeId+"';";
		return db.executeUpdate(sql, true);
	}

	public BigDecimal getAccountBalance(String hotelId, String employeeId) {
		String sql = "SELECT accountBalance as entityId FROM Employee WHERE hotelId = '" + hotelId
				+ "' AND employeeId = '" + employeeId + "'";

		return db.getOneRecord(sql, EntityBigDecimal.class, hotelId).getId();
	}

	// -------------------------------------------Labour

	public boolean updateLabourLog(String hotelId, BigDecimal salary, String employeeId, BigDecimal bonus) {

		String sql = "SELECT MAX(salaryMonth) AS entityId FROM LabourLog WHERE hotelId = '" + hotelId
				+ "' AND employeeId = '" + employeeId + "';";

		int month = db.getOneRecord(sql, EntityId.class, hotelId).getId();
		if (month == 0) {
			month = Integer.parseInt(new SimpleDateFormat("MM").format(new Date())) - 1;
		}

		sql = "INSERT INTO LabourLog (hotelId, salary, employeeId, date, salaryMonth, bonus) VALUES('"
				+ escapeString(hotelId) + "', " + salary + ", '" + employeeId + "', '"
				+ new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + "', " + Integer.toString(month + 1)
				+ ", " + bonus + ");";
		return db.executeUpdate(sql, true);
	}

	// -------------------------------------------TotalRevenue
	public boolean addRevenue(String hotelId, String serviceType, String serviceDate, BigDecimal cash, BigDecimal card,
			BigDecimal app, BigDecimal total, BigDecimal visa, BigDecimal mastercard, BigDecimal maestro, BigDecimal amex, BigDecimal others,
			BigDecimal mswipe, BigDecimal rupay, BigDecimal zomato, BigDecimal zomatopay, BigDecimal swiggy, BigDecimal dineOut, BigDecimal paytm,
			BigDecimal foodPanda, BigDecimal uberEats, BigDecimal foodiloo, BigDecimal nearby,
			BigDecimal complimentary, BigDecimal difference, String reason, String clearance, String section) {

		String sql = "INSERT INTO TotalRevenue "
				+ "(hotelId, serviceType, serviceDate, cash, card, app, total, visa, mastercard, maestro, amex, "
				+ "others, mswipe, rupay, zomato, zomatoPay, swiggy, foodPanda, uberEats, foodiloo, dineOut, paytm, nearBy, complimentary, difference, reason, clearance, section) "
				+ "VALUES('" + escapeString(hotelId) + "', '" + escapeString(serviceType) + "', '"
				+ escapeString(serviceDate) + "', " + cash + ", " + card + ", "
				+ app + ", " + total + ", " + visa + ", "
				+ mastercard + ", " + maestro + ", " + amex + ", "
				+ others + ", " + mswipe + ", " + rupay + ", "
				+ zomato + ", "+  zomatopay + ", " + swiggy + ", " + foodPanda + ", " 
				+ uberEats + ", " + foodiloo + ", "+ dineOut + ", "
				+ paytm + ", " + nearby + ", " + complimentary + ", " + difference
				+ ", '" + escapeString(reason) + "', '" + escapeString(clearance) + "', '" + escapeString(section) + "');";
		return db.executeUpdate(sql, true);
	}

	// -------------------------------------------Server
	public boolean syncOnServer(String hotelId, String sqlQueries) {

		db.beginTransaction(hotelId);
		if (!db.executeUpdate(sqlQueries, hotelId, false)) {
			db.rollbackTransaction();
			System.out.println("Rolling back");
			return false;
		}
		System.out.println("All Transaction logged Successfully at " + hotelId + ".");
		db.commitTransaction();
		return true;
	}

	public ServerLog getLastServerLog(String hotelId) {

		String sql = "SELECT * FROM ServerLog WHERE hotelId = '" + escapeString(hotelId)
				+ "' Order by id desc Limit 1;";

		return db.getOneRecord(sql, ServerLog.class, hotelId);
	}

	public boolean updateServerLog(String hotelId) {

		String sql = "UPDATE ServerLog SET lastUpdateTime = '" + LocalDateTime.now() + "', status = 1 WHERE hotelId = '"
				+ hotelId + "';";

		return db.executeUpdate(sql, false);
	}

	public boolean updateServerStatus(String hotelId, Boolean updateServer) {

		String sql = "UPDATE ServerLog SET status = 0 WHERE hotelId = '" + hotelId + "';";

		return db.executeUpdate(sql, updateServer);
	}

	public boolean createServerLog(String hotelId) {

		String sql = "INSERT into ServerLog ('hotelId', 'lastUpdateTime', 'status') VALUES ('" + escapeString(hotelId)
				+ "','" + LocalDateTime.now() + "', 1);";

		return db.executeUpdate(sql, true);
	}

	// -------------------------------------------Table

	public Boolean assignWaiterToTable(String hotelId, String waiterId, int tableId) {

		String sql = "UPDATE Tables SET waiterId = '" + waiterId + "' WHERE hotelId = '" + hotelId + "' AND tableId = '"
				+ tableId + "';";

		return db.executeUpdate(sql, true);
	}

	// -------------------------------------------Loyalty

	public JSONObject addLoyaltyOffer(String name, int offerType, int points, String offerValue, String hasUsageLimit,
			int usageLimit, int minBill, String userType, String validCollections, String status, String startDate,
			String expiryDate, String hotelId, String chainId, int offerQuantity) {

		JSONObject outObj = new JSONObject();
		try {
			String sql = "SELECT * FROM LoyaltyOffers WHERE hotelId = '" + hotelId + "' AND name = '" + name + "';";
			if (db.hasRecords(sql, hotelId)) {
				outObj.put("message", "This offer already exists. Please enter a new name.");
				return outObj;
			}
			if (offerType == PRODUCT_LOYALTY_OFFER) {
				sql = "SELECT * FROM MenuItems WHERE menuId='" + escapeString(offerValue) + "' AND hotelId='"
						+ escapeString(hotelId) + "';";
				if (!db.hasRecords(sql, hotelId)) {
					outObj.put("message", "This item does not exists in the database. Please enter a valid Menu Item.");
					return outObj;
				}
			}

			sql = "INSERT INTO LoyaltyOffers ('name', 'offerType', 'points', 'offerValue', 'offerQuantity', 'hasUsageLimit', 'usageLimit', 'minBill', "
					+ "'userType', 'validCollections' , 'status', 'startDate', 'expiryDate', 'hotelId', 'chainId') VALUES ('"
					+ escapeString(name) + "'," + offerType + "," + points + ",'" + offerValue + "'," + offerQuantity
					+ ",'" + hasUsageLimit + "'," + usageLimit + "," + minBill + ",'" + escapeString(userType) + "','"
					+ escapeString(validCollections) + "','" + status + "','" + this.formatDate(startDate, "yyyy-MM-dd", "yyyy/MM/dd") + "','"
					+ this.formatDate(expiryDate, "yyyy-MM-dd", "yyyy/MM/dd") + "','" + escapeString(hotelId) + "','" + escapeString(chainId) + "');";

			if (!db.executeUpdate(sql, true)) {
				outObj.put("message", "This offer could not be added. Internal Error");
				return outObj;
			}
			outObj.put("status", true);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}

	public LoyaltyOffer getLoyaltyOfferById(String hotelId, int id) {
		String sql = "SELECT * FROM LoyaltyOffers WHERE hotelId = '" + hotelId + "' AND id = " + id + ";";

		return db.getOneRecord(sql, LoyaltyOffer.class, hotelId);
	}

	public ArrayList<LoyaltyOffer> getAllLoyaltyOffers(String hotelId) {
		String sql = "SELECT * FROM LoyaltyOffers WHERE hotelId = '" + hotelId + "';";

		return db.getRecords(sql, LoyaltyOffer.class, hotelId);
	}

	public ArrayList<LoyaltyOffer> getAllLoyaltyOffersByChain(String chainId) {
		String sql = "SELECT * FROM LoyaltyOffers WHERE chainId = '" + chainId + "';";

		return db.getRecords(sql, LoyaltyOffer.class, chainId);
	}

	public ArrayList<LoyaltyOffer> getAllLoyaltyOffersForCustomer(String hotelId, Customer customer) {
		String sql = "SELECT * FROM LoyaltyOffers WHERE hotelId = '" + hotelId + "' AND status = 'true' AND points <= "
				+ customer.getPoints() + " AND "
				+ "((SELECT requiredPoints FROM LoyaltySettings WHERE LoyaltySettings.userType == LoyaltyOffers.userType)  <= "
				+ customer.getPoints() + ");";

		return db.getRecords(sql, LoyaltyOffer.class, hotelId);
	}

	public JSONObject editLoyaltyOffer(int id, int offerType, int points, String offerValue, String hasUsageLimit,
			int usageLimit, int minBill, String userType, String validCollections, String status, String startDate,
			String expiryDate, String hotelId, String chainId, int offerQuantity) throws ParseException {

		JSONObject outObj = new JSONObject();

		try {
			outObj.put("status", false);

			if (offerType == PRODUCT_LOYALTY_OFFER) {
				MenuItem item = itemExists(hotelId, offerValue);
				if (item == null) {
					outObj.put("message", "This item does not exists in the database. Please enter a valid Menu Item.");
					return outObj;
				} else
					offerValue = item.getMenuId();
			}
			String sql = "UPDATE LoyaltyOffers SET points = " + points + ", offerValue = '" + offerValue
					+ "', offerQuantity = " + offerQuantity + ", hasUsageLimit = '" + hasUsageLimit + "', usageLimit = "
					+ usageLimit + ", minBill = " + minBill + ", userType = '" + userType + "', validCollections = '"
					+ validCollections + "', status = '" + status + "', startDate = '" + this.formatDate(startDate, "yyyy-MM-dd", "yyyy/MM/dd") + "', expiryDate = '"
					+ this.formatDate(expiryDate, "yyyy-MM-dd", "yyyy/MM/dd") + "' WHERE hotelId = '" + hotelId + "' AND Id = " + id + ";";

			if (!db.executeUpdate(sql, true)) {
				outObj.put("message", "Failed to edit this offer. Please try again or contact OrderOn support.");
				return outObj;
			}

			outObj.put("status", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}

	private Boolean updateOfferUsageLimit(String hotelId, int updatedCount, int loyaltyId) {

		String sql = "UPDATE LoyaltyOffers SET usageLimit = " + updatedCount + " WHERE id = " + loyaltyId
				+ " AND hotelId = '" + hotelId + "';";

		return db.executeUpdate(sql, true);
	}

	public JSONObject redeemLoyaltyOffer(String hotelId, String orderId, int loyaltyId, int redeemablePoints) {

		JSONObject outObj = new JSONObject();
		Order order = this.getOrderById(hotelId, orderId);
		Customer customer = this.getCustomerDetails(hotelId, order.getCustomerNumber());
		int requiredPoints = this.getRequiredLoyaltyPoints(hotelId, loyaltyId);
		LoyaltySetting setting = this.getLoyaltySettingByUserType(hotelId, customer.getUserType());
		LoyaltyOffer loyalty = this.getLoyaltyOfferById(hotelId, loyaltyId);
		int balancePoints = 0;
		JSONObject discountObj = new JSONObject();
		JSONArray collectionArr = new JSONArray();

		try {
			outObj.put("status", false);

			if (loyalty.getMinBill() > this.getTotalBillAmount(hotelId, orderId)) {
				outObj.put("message", "This offer requires minimum billing of " + loyalty.getMinBill()
						+ ". (Bill Amount without Tax and Before Discount.)");
				return outObj;
			}

			if (loyalty.offerType == CASH_LOYALTY_OFFER) {

				if (customer.getPoints() < redeemablePoints) {
					outObj.put("message", "Redeemable Points should be less than or equal to the points in Wallet.");
					return outObj;
				}

			} else if (loyalty.offerType == PERCENTAGE_LOYALTY_OFFER) {

				if (customer.getPoints() < requiredPoints) {
					outObj.put("message", "Customer does not have enough points to redeem this offer.");
					return outObj;
				}
				if (setting.getRequiredPoints() > customer.getPoints()) {
					outObj.put("message",
							"Customer must be a " + setting.getUserType() + " Customer to redeem this offer.");
					return outObj;
				}
				if (loyalty.gethasUsageLimit()) {
					if (loyalty.getUsageLimit() == 0)
						outObj.put("message", "This offer cannot be used right now as it has been exhausted.");
					return outObj;
				}

				DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
				Date date = null;
				date = df.parse(loyalty.getExpiryDate());

				if (date.before(new Date())) {
					outObj.put("message", "This offer has expired. Please use another Offer.");
					return outObj;
				}
				date = df.parse(loyalty.getStartDate());

				if (date.after(new Date())) {
					outObj.put("message", "This offer is not active yet. Please use another Offer.");
					return outObj;
				}

				discountObj.put("name", loyalty.getName());
				discountObj.put("type", loyalty.getOfferType());
				discountObj.put("value", loyalty.getOfferValue());
				String[] collections = loyalty.getValidCollections();
				JSONObject collObj = null;
				for (int j = 0; j < collections.length; j++) {
					if (collections[j].equals(""))
						continue;
					collObj = new JSONObject();
					collObj.put("collection", collections[j]);
					collectionArr.put(collObj);
				}
				discountObj.put("validCollections", collectionArr);

				outObj.put("discount", discountObj);

			} else {
				if (customer.getPoints() < requiredPoints) {
					outObj.put("message", "Customer does not have enough points to redeem this offer.");
					return outObj;
				}
				if (setting.getRequiredPoints() > customer.getPoints()) {
					outObj.put("message",
							"Customer must be a " + setting.getUserType() + " Customer to redeem this offer.");
					return outObj;
				}
				ArrayList<OrderItem> orderItems = this.getOrderedItems(hotelId, orderId);
				boolean complimetaryAdded = false;
				int excessQty = loyalty.getOfferQuantity();
				MenuItem item = null;
				for (OrderItem orderItem : orderItems) {
					item = this.getMenuById(hotelId, orderItem.getMenuId());
					if (item.getState() == SUBORDER_STATE_CANCELED || item.getState() == SUBORDER_STATE_COMPLIMENTARY
							|| item.getState() == SUBORDER_STATE_RETURNED || item.getState() == SUBORDER_STATE_VOIDED)
						continue;
					if (loyalty.getOfferValue().equals(orderItem.getMenuId())) {
						excessQty -= orderItem.getQty();
						int qty = excessQty <= 0 ? loyalty.getOfferQuantity() : orderItem.getQty();
						complimetaryAdded = this.complimentaryItem(hotelId, orderId, orderItem.getMenuId(), "",
								orderItem.getSubOrderId(), orderItem.getRate(), qty, loyalty.getName());
						if (!complimetaryAdded) {
							outObj.put("message", "Offer could not be applied. Please contact support. Code 50");
							return outObj;
						}
						if (excessQty <= 0)
							break;
					}
				}
				if (excessQty > 0) {
					item = this.getMenuById(hotelId, loyalty.getOfferValue());
					String sql = "INSERT INTO OrderItemLog "
							+ "(hotelId, orderId, subOrderId, subOrderDate, menuId, state, reason, dateTime, quantity, rate, itemId) "
							+ "VALUES('" + hotelId + "', '" + orderId + "', '"
							+ this.getNextSubOrderId(hotelId, orderId) + "', '"
							+ new SimpleDateFormat("yyyy/MM/dd").format(new Date()) + "', '" + item.getMenuId() + "', "
							+ ORDER_STATE_COMPLIMENTARY + ", 'Loyalty:" + loyalty.getName() + "', '"
							+ new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + "', " + excessQty + ", "
							+ item.getRate() + ", " + 1 + ");";
					if (!db.executeUpdate(sql, true)) {
						outObj.put("message", "Offer could not be applied. Please contact support. Code 50");
						db.rollbackTransaction();
						return outObj;
					}
				}
			}

			balancePoints = customer.getPoints() - requiredPoints;
			String sql = "UPDATE Customers SET points = " + balancePoints + " WHERE mobileNo = '"
					+ customer.getMobileNo() + "' AND hotelId = '" + hotelId + "';";

			if (!db.executeUpdate(sql, true)) {
				outObj.put("message", "Customer wallet could not be updated. Please contact support.");
				db.rollbackTransaction();
				return outObj;
			}

			sql = "UPDATE Orders SET loyaltyId = " + loyaltyId + ", loyaltyPaid = " + redeemablePoints
					+ " WHERE orderId = '" + orderId + "' AND hotelId = '" + hotelId + "';";

			if (!db.executeUpdate(sql, true)) {
				outObj.put("message", "Loyalty could not be added to order. Please contact support.");
				db.rollbackTransaction();
				return outObj;
			}

			if (!this.updateOfferUsageLimit(hotelId, loyalty.getUsageLimit() - 1, loyaltyId)) {
				outObj.put("message", "Usage Limit could not be updated. Please contact support.");
				db.rollbackTransaction();
				return outObj;
			}
			/*
			 * SendSMS sms = new SendSMS();
			 * 
			 * String message = "Dear " + customer.getCustomer() + "! Greetings from " +
			 * hotel.getHotelName() +
			 * ". Thank you for dining with us. It was a pleasure to have you over. Have a great day!"
			 * ;
			 * 
			 * sms.sendSms(message, customer.getMobileNo());
			 */
			outObj.put("status", true);

		} catch (ParseException e) {
			db.rollbackTransaction();
			e.printStackTrace();
		} catch (JSONException e1) {
			db.rollbackTransaction();
			e1.printStackTrace();
		}
		return outObj;
	}

	private int getRequiredLoyaltyPoints(String hotelId, int loyaltyId) {
		String sql = "SELECT points AS entityId FROM LoyaltyOffers WHERE id=" + loyaltyId + " AND hotelId='" + hotelId
				+ "'";

		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		return entity.getId();
	}

	public Boolean addLoyaltyPoints(String hotelId, int points, String mobileNo) {

		int loyaltyPoints = this.getCustomerPoints(hotelId, mobileNo) + points;
		String sql = "SELECT * FROM LoyaltySettings WHERE hotelId = '" + hotelId
				+ "' AND userType != 'All' ORDER BY requiredPoints DESC;";
		ArrayList<LoyaltySetting> loyaltySettings = db.getRecords(sql, LoyaltySetting.class, hotelId);
		String userType = "";
		for (LoyaltySetting setting : loyaltySettings) {
			if (loyaltyPoints < setting.getRequiredPoints()) {
				continue;
			} else {
				userType = setting.getUserType();
				break;
			}
		}

		sql = "UPDATE Customers SET userType = '" + userType + "', points=" + loyaltyPoints + " WHERE mobileNo='"
				+ escapeString(mobileNo) + "' AND hotelId='" + escapeString(hotelId) + "';";
		return db.executeUpdate(sql, true);
	}

	// -------------------------------------------Loyalty Settings

	public ArrayList<LoyaltySetting> getLoyaltySettings(String hotelId) {
		String sql = "SELECT * FROM LoyaltySettings WHERE hotelId = '" + hotelId + "' ORDER BY requiredPoints DESC;";

		return db.getRecords(sql, LoyaltySetting.class, hotelId);
	}

	public LoyaltySetting getBaseLoyaltySetting(String hotelId) {
		String sql = "SELECT * FROM LoyaltySettings WHERE hotelId = '" + hotelId + "' AND requiredPoints = 0 AND userType != 'All';";

		return db.getOneRecord(sql, LoyaltySetting.class, hotelId);
	}

	public LoyaltySetting getLoyaltySettingByUserType(String hotelId, String userType) {
		String sql = "SELECT * FROM LoyaltySettings WHERE hotelId = '" + hotelId + "' AND userType = '"
				+ (userType == "" ? "Prime" : userType) + "';";

		return db.getOneRecord(sql, LoyaltySetting.class, hotelId);
	}

	public Boolean editLoyaltySettings(String hotelId, String userType, int requiredPoints, BigDecimal pointToRupee) {

		String sql = "UPDATE LoyaltySettings SET requiredPoints = " + requiredPoints + ", pointToRupee = "
				+ pointToRupee + " WHERE hotelId = '" + hotelId + "' AND userType = '" + userType + "';";

		return db.executeUpdate(sql, true);
	}

	public ArrayList<IncentiveReport> getIncentivisedItemReport(String hotelId) {
		String sql = "SELECT title, incentive, hasIncentive AS qty FROM MenuItems WHERE hasIncentive != 0 AND hotelId = '" + hotelId + "' "
				+ "ORDER BY title";
		
		return db.getRecords(sql, IncentiveReport.class, hotelId);
	}
	public IncentiveReport getIncentiveForEmployee(String hotelId, String userId, boolean isBar, String startDate,
			String endDate) {

		String sql = "SELECT SUM(sale) AS sale, SUM(incentive) AS incentive "
				+ "FROM (SELECT (SUM(OrderItems.qty)/MenuItems.hasIncentive)*MenuItems.incentive AS incentive, "
				+ "SUM(OrderItems.rate*OrderItems.qty) AS sale FROM OrderItems, MenuItems, Orders "
				+ "WHERE OrderItems.menuId == MenuItems.menuId AND MenuItems.hasIncentive !=0 "
				+ "AND OrderItems.waiterId = '" + userId + "' AND OrderItems.hotelId = '" + hotelId + "' "
				+ "AND Orders.hotelId == OrderItems.hotelId AND Orders.orderId == OrderItems.orderId "
				+ "AND OrderItems.hotelId == MenuItems.hotelId AND Orders.orderDate ";
		if (startDate.equals(endDate))
			sql += " LIKE '" + startDate + "%' ";
		else
			sql += " BETWEEN '" + startDate + "' AND '" + endDate + "' ";

		if (isBar)
			sql += "AND MenuItems.vegType = 3 ";
		else
			sql += "AND MenuItems.vegType != 3 ";

		sql += "GROUP BY Orders.orderId, MenuItems.menuId " + "ORDER BY Orders.orderId) WHERE incentive>0;";
		System.out.println(sql);

		return db.getOneRecord(sql, IncentiveReport.class, hotelId);
	}

	public ArrayList<IncentiveReport> getItemwiseIncentiveReport(String hotelId, String userId, String startDate,
			String endDate, boolean isBar) {

		String sql = "SELECT title, orderId, SUM(qty) AS qty, SUM(incentive) AS incentive FROM (SELECT MenuItems.title AS title, Orders.orderId, SUM(OrderItems.qty) AS qty,"
				+ "(SUM(OrderItems.qty)/MenuItems.hasIncentive)*MenuItems.incentive AS incentive, "
				+ "OrderItems.waiterId AS userId FROM OrderItems, MenuItems, Orders WHERE OrderItems.menuId == MenuItems.menuId AND MenuItems.hasIncentive !=0 "
				+ "AND OrderItems.waiterId = '" + userId + "' AND OrderItems.hotelId = '" + hotelId + "' "
				+ "AND Orders.hotelId == OrderItems.hotelId AND Orders.orderId == OrderItems.orderId "
				+ "AND OrderItems.hotelId == MenuItems.hotelId AND Orders.orderDate ";

		if (startDate.equals(endDate))
			sql += " LIKE '" + startDate + "%' ";
		else
			sql += " BETWEEN '" + startDate + "' AND '" + endDate + "' ";

		if (isBar)
			sql += "AND MenuItems.vegType = 3 ";
		else
			sql += "AND MenuItems.vegType != 3 ";

		sql += "GROUP BY Orders.orderId, MenuItems.menuId "
				+ "ORDER BY Orders.orderId) WHERE incentive>0 GROUP BY title ORDER BY title;";

		System.out.println(sql);

		return db.getRecords(sql, IncentiveReport.class, hotelId);
	}

	public ArrayList<DeliveryReport> getDeliveryReport(String hotelId, String userId, String startDate,
			String endDate, boolean visible) {

		String billNo = visible?"Orders.billNo2 AS billNo":"Orders.billNo";
		
		String sql = "SELECT "+billNo+", Orders.deliveryBoy, deliveryTimeStamp AS dispatchtime, Payment.total FROM Orders, Payment " + 
				"WHERE Orders.orderId == Payment.orderId AND Orders.inhouse == 0 "
				+ "AND Orders.deliveryBoy = '" + userId + "' AND Orders.hotelId = '" + hotelId + "' "
				+ "AND Orders.orderDate ";

		if (startDate.equals(endDate))
			sql += " LIKE '" + startDate + "%' ";
		else
			sql += " BETWEEN '" + startDate + "' AND '" + endDate + "' ";

		sql += "ORDER BY Orders.id;";

		System.out.println(sql);

		return db.getRecords(sql, DeliveryReport.class, hotelId);
	}

	public ArrayList<EntityString> getCaptainOrderService(String hotelId, String startDate, String endDate) {
		String sql = "SELECT DISTINCT waiterId AS entityId FROM OrderItems WHERE OrderItems.subOrderDate";
		if (startDate.equals(endDate))
			sql += " LIKE '" + startDate + "%';";
		else
			sql += " BETWEEN '" + startDate + "' AND '" + endDate + "';";

		System.out.println(sql);
		return db.getRecords(sql, EntityString.class, hotelId);
	}
	
	public ArrayList<ConsumptionReport> getConsumptionReport(String hotelId, String startDate, String endDate, int department) {
		
		String depSql = "";
		if(department == DEPARTMENT_FOOD)
			depSql = " (MenuItems.vegType == 1 OR MenuItems.vegType == 2) ";
		else if(department == DEPARTMENT_NON_ALCOHOLIC_BEVERAGE)
			depSql = " (MenuItems.vegType == 4) ";
		else
			depSql = " (MenuItems.vegType == 3) ";
		
		String date = "";
		if (startDate.equals(endDate))
			date = " = '" + startDate + "' ";
		else
			date = " BETWEEN '" + startDate + "' AND '" + endDate + "' ";
		
		String sql ="SELECT *, (totalSaleQty+totalCompQty) AS totalQty, "
				+ "ROUND(totalAfterDiscount*100*1000/departmentSale)/1000 AS percentOfDepartmentSale, "
				+ "ROUND(totalAfterDiscount*100*1000/totalSale)/1000 AS percentOfTotalSale,"
				+ "ROUND((qty+compQty)*100*100/(totalSaleQty+totalCompQty))/100 AS percentOfTotalQty "
				+ "FROM "
				+ "(SELECT category, title, vegType, IFNULL(SUM(qty), 0) AS qty, IFNULL(SUM(compQty), 0) AS compQty, rate, ROUND(SUM(total)*100)/100 AS total, ROUND(SUM(totalAfterDiscount)*100)/100 AS totalAfterDiscount,"
				+ "(SELECT ROUND(SUM(foodBill-foodDiscount)*100)/100 FROM Payment WHERE orderDate "+date+") AS departmentSale,"
				+ "(SELECT ROUND(SUM(foodBill+barBill-foodDiscount-barDiscount)*100)/100 FROM Payment WHERE orderDate BETWEEN '" + startDate + "' AND '" + endDate + "') AS totalSale,"
				+ "(SELECT IFNULL(SUM(qty), 0) FROM OrderItems, Orders, MenuItems WHERE orderDate " + date + " AND Orders.orderId == OrderItems.orderId AND MenuItems.menuId == OrderItems.menuId AND "+depSql+") AS totalSaleQty,"
				+ "(SELECT IFNULL(SUM(quantity), 0) FROM OrderItemLog, Orders, MenuItems WHERE orderDate " + date + " AND "
				+ "Orders.orderId == OrderItemLog.orderId AND OrderItemLog.state == 50 AND MenuItems.menuId == OrderItemLog.menuId AND "+depSql+") AS totalCompQty "
				+ "FROM "
				+ "(SELECT category, title, vegType, MenuItems.menuId, OrderItems.qty, 0 AS compQty, OrderItems.rate, OrderItems.qty*OrderItems.rate AS total, "
				+ "(CASE "
				+ "WHEN Orders.discountCode != '' "
				+ "THEN "
				+ "(CASE "
				+ "WHEN (SELECT type FROM Discount WHERE Orders.discountCode == name) == 0 "
				+ "THEN (OrderItems.qty*OrderItems.rate) - ((SELECT foodValue FROM Discount WHERE Orders.discountCode == name)*OrderItems.rate/100)*OrderItems.qty "
				+ "ELSE (OrderItems.qty*OrderItems.rate) - (SELECT foodValue FROM Discount WHERE Orders.discountCode == name)*OrderItems.qty "
				+ "END) "
				+ "ELSE "
				+ "OrderItems.qty*OrderItems.rate "
				+ "END) AS totalAfterDiscount "
				+ "FROM Orders, OrderItems, MenuItems "
				+ "WHERE Orders.orderId == OrderItems.orderId "
				+ "AND MenuItems.menuId == OrderItems.menuId "
				+ "AND Orders.orderDate " + date + " "
				+ "AND " + depSql
				+ "UNION ALL "
				+ "SELECT category, title, vegType, MenuItems.menuId, 0 AS qty, OrderItemLog.quantity AS compQty, OrderItemLog.rate, OrderItemLog.quantity*OrderItemLog.rate AS total, "
				+ "0 AS totalAfterDiscount "
				+ "FROM Orders, OrderItemLog, MenuItems "
				+ "WHERE Orders.orderId == OrderItemLog.orderId "
				+ "AND MenuItems.menuId == OrderItemLog.menuId "
				+ "AND Orders.orderDate " + date + " "
				+ "AND " + depSql
				+ "AND OrderItemLog.state == 50) "
				+ "GROUP BY menuId "
				+ "ORDER BY category, vegType, title)";

		System.out.println(sql);
		return db.getRecords(sql, ConsumptionReport.class, hotelId);
	}

	// -------------------------------------------Reservation
	
	public Boolean createNewReservation(String hotelId, int maleCount, int femaleCount,
			int childrenCount, String bookingTime, String customerName, String mobileNumber, Boolean isPriorityCust) {
		
		Customer customer = this.getCustomerDetails(hotelId, mobileNumber);
		if(customer == null) {
			this.addCustomer(hotelId, customerName, mobileNumber, "", "", "", "", false, isPriorityCust);
			customer = this.getCustomerDetails(hotelId, mobileNumber);
		}else if(isPriorityCust) {
			String sql = "UPDATE Customer SET isPriority = '" +true+ "' WHERE id = "+customer.getId()+";";
			db.executeUpdate(sql, true);
		}
		
		String sql = "INSERT INTO Reservations(hotelId, customerId, maleCount, femaleCount, childrenCount, bookingTime, timeStamp, state, type)"
				+ "VALUES('" + hotelId + "', " + customer.getId() + ", " + maleCount
				+ ", " + femaleCount + ", " + childrenCount + ", '" + bookingTime + "', '" + LocalDateTime.now()
				+ "', " + RESERVATION_STATE_BOOKED + ", " + TYPE_RESERVATION + "');";
		return db.executeUpdate(sql, true);
	}

	public Boolean createNewWaitList(String hotelId, int maleCount, int femaleCount,
			int childrenCount, String customerName, String mobileNumber, Boolean isPriorityCust) {

		Customer customer = this.getCustomerDetails(hotelId, mobileNumber);
		if(customer == null) {
			this.addCustomer(hotelId, customerName, mobileNumber, "", "", "", "", false, isPriorityCust);
			customer = this.getCustomerDetails(hotelId, mobileNumber);
		}else if(isPriorityCust) {
			String sql = "UPDATE Customer SET isPriority = '" +true+ "' WHERE id = "+customer.getId()+";";
			db.executeUpdate(sql, true);
		}
		
		String sql = "INSERT INTO Reservations(hotelId, customerId, maleCount, femaleCount, childrenCount, bookingTime, bookingDate, timeStamp, state, type)"
				+ "VALUES('" + hotelId + "', " + customer.getId() + ", " + maleCount
				+ ", " + femaleCount + ", " + childrenCount + ", '" + parseTime("HH:mm") + "', '" + LocalDateTime.now()
				+ "', " + RESERVATION_STATE_WAITING + ", " + TYPE_WAITLIST + "');";
		return db.executeUpdate(sql, true);
	}

	public Boolean editReservation(String hotelId, int reservationId, int maleCount, int femaleCount,
			int childrenCount, String bookingTime) {

		String sql = "UPDATE Reservations SET maleCount = " + maleCount + ", femaleCount = "
				+ femaleCount + ", childrenCount = " + childrenCount + ", bookingTime = '" + bookingTime
				+ "' WHERE  hotelId='" + hotelId + "' AND reservationId = " + reservationId + ";";
		return db.executeUpdate(sql, true);
	}

	public Boolean updateReservationState(String hotelId, int reservationId, int state) {

		String sql = "UPDATE Reservations SET state = " + state + " WHERE  hotelId='" + hotelId + "' AND reservationId = " + reservationId + ";";
		return db.executeUpdate(sql, true);
	}

	public Boolean assignOrderToReservation(String hotelId, int reservationId, int state, String orderId) {

		String sql = "UPDATE Reservations SET state = " + state 
				+ ", orderId = '"+orderId+"' WHERE  hotelId='" + hotelId + "' AND reservationId = " + reservationId + ";";
		return db.executeUpdate(sql, true);
	}
	
	public ArrayList<Reservation> getReservations(String hotelId, String bookingDate){
		
		String sql = "SELECT Reservations.*, Customers.mobileNo, Customers.customer, Customers.isPriority FROM Reservations, "
				+ "Customers WHERE Customers.Id == Reservations.customerId AND hotelId='" + hotelId + "' AND bookingDate = " + bookingDate 
				+ " AND type = "+TYPE_RESERVATION+";";
		return db.getRecords(sql, Reservation.class, hotelId);
	}
	
	public ArrayList<Reservation> getWaitList(String hotelId, String bookingDate){
		
		String sql = "SELECT Reservations.*, Customers.mobileNo, Customers.customer, Customers.isPriority FROM Reservations, "
				+ "Customers WHERE Customers.Id == Reservations.customerId AND hotelId='" + hotelId + "' AND bookingDate = " + bookingDate 
				+ " AND type = "+TYPE_WAITLIST+";";
		return db.getRecords(sql, Reservation.class, hotelId);
	}
	
	public Reservation getReservation(String hotelId, int reservationId){
		
		String sql = "SELECT Reservations.*, Customers.mobileNo, Customers.customer, Customers.isPriority FROM Reservations, "
				+ "Customers WHERE Reservations.id == "+reservationId+" AND hotelId='" + hotelId +"';";
		return db.getOneRecord(sql, Reservation.class, hotelId);
	}

	// -------------------------------------------Common
	private String getPreviousDateString(int day) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, day * (-1));
		return dateFormat.format(cal.getTime());
	}

	public static void main(String args[]) {
		// dynamic testcases
		//AccessManager dao = new AccessManager(false);
		//dao.hideOrder("h0002", "f", "", "", 25000.0);

	}
	
	public void loadShortForms(String hotelId) {

		ArrayList<MenuItem> menuItems = this.getMenu(hotelId);
		for (MenuItem menuItem : menuItems) {
			String sql = "UPDATE MenuItems SET shortform = '" + this.generateShortForm(menuItem.getTitle())
					+ "' WHERE menuId = '" + menuItem.getMenuId() + "';";
			db.executeUpdate(sql, false);
		}
	}

	// Convert DateTime string to the requested format.
	public String parseTime(String time, String format) {
		LocalDateTime now = LocalDateTime.parse(time);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return now.format(formatter);
	}

	// Returns current time in the requested format.
	public String parseTime(String format) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return now.format(formatter);
	}
	
	public String formatDate(String dateStr, String oldFormat, String newFormat) throws ParseException {

		DateFormat df = new SimpleDateFormat(oldFormat);
		DateFormat df2 = new SimpleDateFormat(newFormat);
		Date date = df.parse(dateStr);
		return df2.format(date);
	}

	public String getOrderType(int orderTypeCode){
		if(orderTypeCode == INHOUSE)
			return "Inhouse";
		else if(orderTypeCode == HOME_DELIVERY)
			return "Home Delivery";
		else if(orderTypeCode == BAR)
			return "Bar";
		else
			return "Take Away";
	}
}