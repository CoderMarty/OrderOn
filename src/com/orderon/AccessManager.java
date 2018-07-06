package com.orderon;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	public static final int ORDER_STATE_CANCELED= 101;
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
	public static final int MENUITEM_STATE_AVAILABLE = 0;
	public static final int MENUITEM_STATE_UNAVAILABLE = 1;
	public static int AUTH_TOKEN = 0;
	public static int PRESENT = 1;
	public static int ABSENT = 3;
	public static int EXCUSED = 2;
	public static int AUTHORIZE = 1;
	public static int UNAUTHORIZE = 0;
	public static int INHOUSE =1;
	public static int HOME_DELIVERY = 0;
	public static int TAKE_AWAY = 2;
	public static int BAR = 3;
	public static int DISCOUNT_LOYALTY_OFFER = 0;
	public static int PRODUCT_LOYALTY_OFFER = 1;
	
	/*
	 * KDS and POSTPAID
	 * 
	 * ORDER STATES 0,1,2,3;
	 * ORDERITEM STATES 0,2,3,1
	 * 
	 * 
	 * NON-KDS and POSTPAID
	 * 
	 * ORDER STATES 0,1,3;
	 * ORDERITEM STATES 1
	 * 
	 * KDS and PREPAID
	 * 
	 * ORDER STATES 1,2,3;
	 * ORDERITEM STATES 0,2,3,1
	 * 
	 * 
	 * NON-KDS and PREPAID
	 * 
	 * ORDER STATES 1,2,3
	 * ORDERITEM STATES 1
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
	
	public void initDatabase(String hotelId) {
		String sql = null;
		
		sql = "CREATE TABLE IF NOT EXISTS AddOns ( "
				+ "Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ "name TEXT NOT NULL, "
				+ "inhouseRate INTEGER, "
				+ "deliveryRate INTEGER )";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Attendance ( Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "employeeId text NOT NULL, "
				+ "checkInTime text NOT NULL, "
				+ "checkOutTime text, "
				+ "checkInDate text NOT NULL, "
				+ "checkOutDate text, "
				+ "reason text, "
				+ "authorisation INTEGER, "
				+ "isPresent INTEGER, "
				+ "shift INTEGER, "
				+ "FOREIGN KEY(employeeId) REFERENCES Employee(employeeId) )";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Bank ("
				+ "accountNumber INTEGER NOT NULL  UNIQUE , "
				+ "bankName TEXT, "
				+ "accountName TEXT NOT NULL  UNIQUE, "
				+ "balance INTEGER NOT NULL  DEFAULT 0, "
				+ "hotelId TEXT, "
				+ "PRIMARY KEY (accountNumber, accountName));";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Collections("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "collection text NOT NULL, "
				+ "image TEXT, "
				+ "UNIQUE (hotelId, collection))";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Customers("
				+ "Id integer NOT NULL PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "mobileNo text UNIQUE NOT NULL, "
				+ "customer text NOT NULL, "
				+ "address text NOT NULL, "
				+ "birthdate text,"
				+ "anniversary text,"
				+ "isPriviledged integer, "
				+ "remarks text, "
				+ "allergyInfo text, "
				+ "points int, "
				+ "wantsPromotion text, "
				+ "UNIQUE( hotelId, mobileNo))";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Discount("
				+ "hotelId text NOT NULL, "
				+ "name text UNIQUE NOT NULL, "
				+ "description text NOT NULL, "
				+ "type text NOT NULL, "
				+ "value integer NOT NULL, "
				+ "startDate text NOT NULL,"
				+ "expiryDate text, "
				+ "validCollections text, "
				+ "usageLimit text NOT NULL)";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Employee("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "employeeId text NOT NULL, "
				+ "firstName text NOT NULL, "
				+ "surName text NOT NULL, "
				+ "address integer NOT NULL, " 
				+ "contactNumber int NULL, " 
				+ "dob text NULL, " 
				+ "sex text NULL, "
				+ "hiringDate text NULL, " 
				+ "designation text NULL, " 
				+ "department text NULL, " 
				+ "salary int NULL, " 
				+ "bonus int NULL, "
				+ "image TEXT DEFAULT (null), "
				+ "middleName TEXT, "
				+ "email TEXT, " 
				+ "accountBalance INTEGER, "
				+ "UNIQUE (employeeId))";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Expenses ("
				+ "id INTEGER PRIMARY KEY  NOT NULL  DEFAULT (0), "
				+ "type TEXT NOT NULL, "
				+ "serviceDate TEXT NOT NULL  DEFAULT (CURRENT_TIMESTAMP), "
				+ "amount INTEGER NOT NULL  DEFAULT (0), "
				+ "userId TEXT NOT NULL, "
				+ "payee TEXT, "
				+ "memo TEXT, "
				+ "chequeNo INTEGER, "
				+ "accountName TEXT DEFAULT (null), "
				+ "bankName TEXT,payment_type TEXT DEFAULT (null), "
				+ "hotelId TEXT, "
				+ "serviceType TEXT, "
				+ "FOREIGN KEY(accountName) REFERENCES Bank(accountName), "
				+ "FOREIGN KEY(userId) REFERENCES Users(userId));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Hotel ( Id integer, "
				+ "hotelId text NOT NULL, "
				+ "hotelCode text NOT NULL, "
				+ "hotelName text NOT NULL, "
				+ "isEnabled int NOT NULL, "
				+ "hotelAddress TEXT, "
				+ "hotelContact TEXT, "
				+ "isChargingTax INTEGER DEFAULT (0), "
				+ "flags TEXT, "
				+ "VATNumber TEXT, "
				+ "GSTNumber TEXT DEFAULT (null), "
				+ "hotelType TEXT, "
				+ "description TEXT, "
				+ "website TEXT, "
				+ "smsEnabled INTEGER, "
				+ "serverEnabled INTEGER, "
				+ "hasCashDrawer INTEGER, "
				+ "hasLoyalty INTEGER, "
				+ "hasIncentiveScheme INTEGER, "
				+ "billType INTEGER, "
				+ "PRIMARY KEY(Id) )";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS LabourLog (Id INTEGER PRIMARY KEY NOT NULL, "
				+ "salary INTEGER NOT NULL DEFAULT (null), "
				+ "employeeId TEXT NOT NULL, "
				+ "date TEXT NOT NULL, "
				+ "salaryMonth TEXT NOT NULL, "
				+ "bonus INTEGER, "
				+ "hotelId TEXT)";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS LoyaltyOffers ( "
				+ "Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ "name TEXT NOT NULL UNIQUE, "
				+ "description TEXT NOT NULL, "
				+ "hotelId TEXT NOT NULL, "
				+ "points INTEGER NOT NULL, "
				+ "count INTEGER, "
				+ "offerType INTEGER NOT NULL, "
				+ "offer TEXT NOT NULL,"
				+ "status INTEGER NOT NULL )";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Material ("
				+ "sku TEXT PRIMARY KEY NOT NULL,  "
				+ "name TEXT NOT NULL, "
				+ "unit TEXT NOT NULL, "
				+ "ratePerUnit DOUBLE NOT NULL, "
				+ "wastage INTEGER, "
				+ "minQuantity INTEGER NOT NULL, "
				+ "hotelId text NOT NULL, "
				+ "displayableUnit TEXT NOT NULL DEFAULT GRAM);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS MenuItems("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "station text NOT NULL,"
				+ "menuId text NOT NULL, "
				+ "title text NOT NULL, " 
				+ "description text NOT NULL, " 
				+ "category text NOT NULL, "
				+ "flags text,"
				+ "preparationTime integer,"
				+ "rate real NOT NULL,"
				+ "costPrice INTEGER,"
				+ "inhouseRate INTEGER,"
				+ "vegType int,"
				+ "method text,"
				+ "state int,"
				+ "shortForm text,"
				+ "addOns text,"
				+ "img text, "
				+ "hasIncentive INTEGER,"
				+ "incentive DOUBLE, "
				+ "isTaxable INTEGER, UNIQUE(hotelId, menuId))";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Notification("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "orderId text NOT NULL, "
				+ "notId int NOT NULL, "
				+ "msg text NOT NULL)";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderAddOnLog  ("
				+ "Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , "
				+ "hotelId TEXT NOT NULL , "
				+ "orderId TEXT NOT NULL , "
				+ "addOnId TEXT NOT NULL , "
				+ "subOrderId TEXT NOT NULL , "
				+ "subOrderDate TEXT NOT NULL, "
				+ "menuId TEXT NOT NULL , "
				+ "state TEXT NOT NULL , "
				+ "itemId INTEGER NOT NULL , "
				+ "quantity INTEGER, "
				+ "rate INTEGER)";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS OrderAddOns("
				+ "Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ "hotelId text NOT NULL, "
				+ "subOrderId text NOT NULL, "
				+ "subOrderDate text NOT NULL, "
				+ "state text NOT NULL, "
				+ "orderId text NOT NULL, "
				+ "addOnId INTEGER NOT NULL, "
				+ "menuId text NOT NULL,"
				+ "itemId INTEGER NOT NULL,"
				+ "qty INTEGER NOT NULL, "
				+ "rate INTEGER NOT NULL)";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS OrderDelivery("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "employeeId text NOT NULL,"
				+ "orderId text NOT NULL, "
				+ "UNIQUE(hotelId, orderId), "
				+ "FOREIGN KEY(employeeId) REFERENCES Employee(employeeId))";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS OrderItemLog  ("
				+ "Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , "
				+ "hotelId TEXT NOT NULL , "
				+ "orderId TEXT NOT NULL , "
				+ "subOrderId TEXT NOT NULL , "
				+ "menuId TEXT NOT NULL , "
				+ "itemId TEXT NOT NULL , "
				+ "state INTEGER NOT NULL , "
				+ "reason TEXT, "
				+ "dateTime TEXT, "
				+ "quantity INTEGER, "
				+ "rate INTEGER, "
				+ "subOrderDate TEXT)";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderItems("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "subOrderId text NOT NULL, "
				+ "subOrderDate text NOT NULL, "
				+ "orderId text NOT NULL, "
				+ "menuId text NOT NULL,"
				+ "qty int NOT NULL, "
				+ "rate real NOT NULL, "
				+ "specs text, "
				+ "state integer, "
				+ "billNo varchar,"
				+ "waiterId text,"
				+ "isKotPrinted integer)";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS OrderSpecifications ( "
				+ "Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ "hotelId TEXT NOT NULL, "
				+ "menuId TEXT NOT NULL, "
				+ "orderId TEXT NOT NULL, "
				+ "subOrderId TEXT NOT NULL, "
				+ "specification TEXT NOT NULL, "
				+ "itemId INTEGER NOT NULL )";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderTables("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "orderId text NOT NULL, "
				+ "tableId text NOT NULL)";
		
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Orders("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "orderId text NOT NULL, "
				+ "orderDate text NOT NULL,"
				+ "customerName text NOT NULL,"
				+ "customerAddress text NOT NULL,"
				+ "customerNumber text NOT NULL,"
				+ "rating_ambiance integer NOT NULL,"
				+ "rating_qof integer NOT NULL,"
				+ "rating_service integer NOT NULL,"
				+ "rating_hygiene integer NOT NULL,"
				+ "reviewSuggestions text,"
				+ "waiterId text NOT NULL,"
				+ "numberOfGuests integer NOT NULL,"
				+ "state integer NOT NULL,"
				+ "tableId integer,"
				+ "serviceType text NOT NULL,"
				+ "inhouse integer NOT NULL,"
				+ "foodBill double,"
				+ "barBill double,"
				+ "billNo varchar,"
				+ "reason text,"
				+ "authId text,"
				+ "printCount integer,"
				+ "discountCode text,"
				+ "isSmsSent integer,"
				+ "loyaltyId integer,"
				+ "loyaltyPaid integer,"
				+ "completeTimestamp text)";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Payment("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "billNo varchar NOT NULL, "
				+ "orderId text NOT NULL, "
				+ "orderDate DATETIME NOT NULL, "
				+ "foodBill integer NOT NULL, "
				+ "barBill integer NOT NULL, "
				+ "discount integer NOT NULL, "
				+ "total integer NOT NULL, "
				+ "serviceCharge integer NOT NULL, "
				+ "serviceTax integer NOT NULL, "
				+ "VATFOOD integer NOT NULL, "
				+ "VATBAR integer NOT NULL, "
				+ "sbCess integer NOT NULL, "
				+ "kkCess integer NOT NULL, "
				+ "tip integer NOT NULL, "
				+ "cashPayment integer NOT NULL, "
				+ "cardPayment integer NOT NULL, "
				+ "discountName text, "
				+ "cardType text, "
				+ "gst INTEGER, "
				+ "loyaltyAmount INTEGER, "
				+ "complimentary INTEGER, "
				+ "UNIQUE(hotelId, orderId, billNo), "
				+ "FOREIGN KEY(discountName) REFERENCES Discount(name))";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Recipe ("
				+ "Id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ " sku TEXT NOT NULL,"
				+ " menuID TEXT NOT NULL,"
				+ " unit TEXT NOT NULL,"
				+ " quantity INTEGER NOT NULL,"
				+ " hotelId text NOT NULL, "
				+ " FOREIGN KEY(sku) REFERENCES Stock(sku),"
				+ " FOREIGN KEY(menuId) REFERENCES MenuItems(menuId));";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE  TABLE  IF NOT EXISTS ServerLog ("
				+	"Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
				+	"lastUpdateTime TEXT, status INTEGER, "
				+ 	"hotelId TEXT )";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS ServiceLog ("
				+	"Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , "
				+	"hotelId TEXT NOT NULL , "
				+ 	"serviceDate TEXT NOT NULL , "
				+	"startTimeStamp TEXT NOT NULL , "
				+	"endTimeStamp TEXT NOT NULL , "
				+	"serviceType TEXT NOT NULL , "
				+	"isCurrent INTEGER NOT NULL , "
				+	"cashInHand INTEGER);";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE  TABLE IF NOT EXISTS Specifications ("
				+	"Id INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , "
				+	"specification TEXT)";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Stations("
				+	"Id integer PRIMARY KEY AUTOINCREMENT, "
				+	"hotelId text NOT NULL, "
				+	"station text NOT NULL, "
				+	"UNIQUE (hotelId, station));";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS StockLog ("
				+ "Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , "
				+ "sku INTEGER NOT NULL , "
				+ "crud TEXT NOT NULL , "
				+ "quantity DOUBLE NOT NULL , "
				+ "amount DOUBLE NOT NULL , "
				+ "hotelId TEXT , "
				+ "FOREIGN KEY(sku) REFERENCES Material(sku))";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Stock ("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "sku TEXT NOT NULL,  "
				+ "doc TEXT NOT NULL, "
				+ "doe TEXT, "
				+ "quantity INTEGER NOT NULL, "
				+ "hotelId text NOT NULL, "
				+ "FOREIGN KEY(sku) REFERENCES Material(sku));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Tables("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "tableId text NOT NULL, "
				+ "state integer, UNIQUE (hotelId, tableId))";
		db.executeUpdate(sql, hotelId, false);	
		
		sql = "CREATE TABLE IF NOT EXISTS TotalRevenue ("
				+  "id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , " 
				+  "hotelId TEXT NOT NULL , "
				+  "serviceDate TEXT NOT NULL , " 
				+  "serviceType TEXT NOT NULL , "
				+  "cash DOUBLE NOT NULL , "
				+  "card DOUBLE NOT NULL , "
				+  "visa DOUBLE, "
				+  "mastercard DOUBLE, "
				+  "maestro DOUBLE, "
				+  "amex DOUBLE, "
				+  "rupay DOUBLE, "
				+  "others DOUBLE, "
				+  "difference DOUBLE, "
				+  "reason TEXT, "
				+  "total DOUBLE NOT NULL, "
				+  "clearance TEXT, "
				+  "zomato DOUBLE, "
				+  "swiggy DOUBLE, "
				+  "magicPin DOUBLE,"
				+  "mswipe DOUBLE, "
				+  "zomatoCash DOUBLE, "
				+  "swiggyCash DOUBLE );";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS TransactionHistory ( "
				+ "Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ "trType TEXT NOT NULL, "
				+ "trDetail TEXT NOT NULL, "
				+ "amount DOUBLE NOT NULL, "
				+ "balance DOUBLE NOT NULL, "
				+ "trDate TEXT NOT NULL, userId TEXT, "
				+ "authoriser TEXT )";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Users("
				+ "Id integer PRIMARY KEY AUTOINCREMENT, "
				+ "hotelId text NOT NULL, "
				+ "userId text NOT NULL, "
				+ "userPasswd text NOT NULL, "
				+ "employeeId text NOT NULL, "
				+ "userType integer NOT NULL, " 
				+ "authToken text NULL, " 
				+ "timeStamp text NULL, "
				+ "UNIQUE(hotelId, userId, employeeId), "
				+ "FOREIGN KEY(employeeId) REFERENCES Employee(employeeId));";
		db.executeUpdate(sql, hotelId, false);
		// Create all other tables here...
		//this.updatePayTM(hotelId);
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
		public double ratePerUnit;
		public int wastage;
		public double minQuantity;
		public String hotelId;
		public double quantity;
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
		public double getRatePerUnit() {
			return ratePerUnit;
		}
		public int getWastage() {
			return wastage;
		}
		public double getMinQuantity() {
			return minQuantity;
		}
		public String getHotelId() {
			return hotelId;
		}
		public double getQuantity() {
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
			this.ratePerUnit = Database.readRsDouble(rs, "ratePerUnit");
			this.wastage = Database.readRsInt(rs, "wastage");
			this.minQuantity = Database.readRsDouble(rs, "minQuantity");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.quantity = Database.readRsDouble(rs, "quantity");
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
		
		public int getNotificationId () {
			return mNotId;
		}
		
		public String getHotelId () {
			return mHotelId;
		}
		
		public String getOrderId () {
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
		public String getPasswd() {
			return mPasswd;
		}

		public String getHotelId() {
			return mHotelId;
		}

		public String getUserId() {
			return mUserId;
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

		private String mHotelId;
		private String mPasswd;
		private String mUserId;
		private String mEmployeeId;
		private Integer mUserType;
		private String mAuthToken;
		private String timeStamp;
		
		@Override
		public void readFromDB(ResultSet rs) {
			this.mHotelId = Database.readRsString(rs, "hotelId");
			this.mUserId = Database.readRsString(rs, "userId");
			this.mPasswd = Database.readRsString(rs, "userPasswd");
			this.mEmployeeId = Database.readRsString(rs, "employeeId");
			this.mUserType = Database.readRsInt(rs, "userType");
			this.mAuthToken = Database.readRsString(rs, "authToken");
			this.timeStamp = Database.readRsString(rs, "timeStamp");
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
	

	public static class EntityDouble implements Database.OrderOnEntity {
		public Double getId() {
			return mId;
		}
		private Double mId;
		@Override
		public void readFromDB(ResultSet rs) {
			this.mId = Database.readRsDouble(rs, "entityId");
		}
	}

	public static class MenuCollection implements Database.OrderOnEntity {
		public String getCollection() {
			return mCollection;
		}
		private String mCollection;
		@Override
		public void readFromDB(ResultSet rs) {
			this.mCollection = Database.readRsString(rs, "collection");
		}
	}
	
	public static class DeliveryPerson implements Database.OrderOnEntity {
		public String getName() {
			return mName;
		}
		public String getId() {
			return mEmployeeId;
		}
		private String mName;
		private String mEmployeeId;
		@Override
		public void readFromDB(ResultSet rs) {
			this.mName = Database.readRsString(rs, "name");
			this.mEmployeeId = Database.readRsString(rs, "employeeId");
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
		public int getIsChargingTax() {
			return isChargingTax;
		}
		public String getFlags() {
			return flags;
		}
		public String getVatNumber() {
			return vatNumber;
		}
		public String getGstNumber() {
			return gstNumber;
		}
		public String getHotelType() {
			return hotelType.split(":")[0];
		}
		public String getKDSType() {
			return hotelType.split(":")[1];
		}
		public String getKOTCount() {
			return hotelType.split(":")[2];
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
		public int getIsServerEnabled() {
			return isServerEnabled;
		}
		public String getServerUpdateTime() {
			return serverUpdateTime;
		}
		public int getHasCashDrawer() {
			return hasCashDrawer;
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

		private String mHotelName;
		private String mHotelId;
		private Integer mIsEnabled;
		private String mHotelCode;
		private String hotelAddress;
		private String hotelContact;
		private int isChargingTax;
		private String flags;
		private String vatNumber;
		private String gstNumber;
		private String hotelType;
		private String description;
		private String website;
		private int isSmsEnabled;
		private int isServerEnabled;
		private int hasCashDrawer;
		private int hasLoyalty;
		private int hasIncentiveScheme;
		private String serverUpdateTime;
		private int billType;
		@Override
		public void readFromDB(ResultSet rs) {
			this.mHotelName = Database.readRsString(rs, "hotelName");
			this.mHotelId = Database.readRsString(rs, "hotelId");
			this.mIsEnabled = Database.readRsInt(rs, "isEnabled");
			this.mHotelCode = Database.readRsString(rs, "hotelCode");
			this.hotelAddress = Database.readRsString(rs, "hotelAddress");
			this.hotelContact = Database.readRsString(rs, "hotelContact");
			this.isChargingTax = Database.readRsInt(rs, "isChargingTax");
			this.flags = Database.readRsString(rs, "flags");
			this.vatNumber = Database.readRsString(rs, "VATNumber");
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

	public static class BillNoFeild implements Database.OrderOnEntity {
		public String getBillNo() {
			return billNo;
		}
		private String billNo;
		@Override
		public void readFromDB(ResultSet rs) {
			this.billNo = Database.readRsString(rs, "billNo");
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

	public static class AmountField implements Database.OrderOnEntity {
		public Double getAmount() {
			return mAmount;
		}
		
		private Double mAmount;
		@Override
		public void readFromDB(ResultSet rs) {
			try {
				this.mAmount = rs.getDouble(1);
			}
			catch (Exception e) {
				this.mAmount = 0.0;
			}
		}
	}

	public static class Customer implements Database.OrderOnEntity {
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
		public int getIsPriviledged() {
			return isPriviledged;
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
		public Boolean getWantsPromotion() {
			return Boolean.valueOf(wantsPromotion);
		}

		private String mCustomer;
		private String mMobileNo;
		private String mAddress;
		private String mBirthdate;
		private String mAnniversary;
		private int isPriviledged;
		private int isSmsSent;
		private String remarks;
		private String completeTimestamp;
		private String orderId;
		private String allergyInfo;
		private int points;
		private String wantsPromotion;
		@Override
		public void readFromDB(ResultSet rs) {
			this.mCustomer = Database.readRsString(rs, "customer");
			this.mMobileNo = Database.readRsString(rs, "mobileNo");
			this.mAddress = Database.readRsString(rs, "address");
			this.mBirthdate = Database.readRsString(rs, "birthdate");
			this.mAnniversary = Database.readRsString(rs, "anniversary");
			this.isPriviledged = Database.readRsInt(rs, "isPriviledged");
			this.remarks = Database.readRsString(rs, "remarks");
			this.isSmsSent = Database.readRsInt(rs, "isSmsSent");
			this.completeTimestamp = Database.readRsString(rs, "completeTimestamp");
			this.orderId = Database.readRsString(rs, "orderId");
			this.allergyInfo = Database.readRsString(rs, "allergyInfo");
			this.points = Database.readRsInt(rs, "points");
			this.wantsPromotion = Database.readRsString(rs, "wantsPromotion");
		}
	}
	
	public static class CustomerReport implements Database.OrderOnEntity {
		
		public String getCustomerName() {
			return customerName;
		}
		public String getMobileNumber() {
			return mobileNumber;
		}
		public Double getSpentPerPax() {
			return spentPerPax;
		}
		public Double getSpentPerWalkin() {
			return spentPerWalkin;
		}
		public Double getTotalSpent() {
			return totalSpent;
		}
		public int getTotalGuests() {
			return totalGuests;
		}
		public int getTotalWalkins() {
			return totalWalkins;
		}

		private String customerName;
		private String mobileNumber;
		private Double spentPerPax;
		private Double spentPerWalkin;
		private Double totalSpent;
		private int totalGuests;
		private int totalWalkins;
		
		@Override
		public void readFromDB(ResultSet rs) {
			this.customerName = Database.readRsString(rs, "customerName");
			this.mobileNumber = Database.readRsString(rs, "mobileNo");
			this.spentPerPax = Database.readRsDouble(rs, "spentPerPax");
			this.spentPerWalkin = Database.readRsDouble(rs, "spentPerWalkin");
			this.totalSpent = Database.readRsDouble(rs, "totalSpent");
			this.totalGuests = Database.readRsInt(rs, "totalGuests");
			this.totalWalkins = Database.readRsInt(rs, "totalWalkins");
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
		public Double getTotal() {
			return mTotal;
		}
		public String getBillNo() {
			return mBillNo;
		}
		private String mCustomer;
		private String mMobileNo;
		private String mAddress;
		private String mOrderId;
		private Integer mState;
		private Double mTotal;
		private String mBillNo;
		@Override
		public void readFromDB(ResultSet rs) {
			this.mCustomer = Database.readRsString(rs, "customer");
			this.mMobileNo = Database.readRsString(rs, "mobileNo");
			this.mAddress = Database.readRsString(rs, "address");
			this.mState = Database.readRsInt(rs, "state");
			this.mOrderId = Database.readRsString(rs, "orderId");
			this.mTotal = Database.readRsDouble(rs, "total");
			this.mBillNo = Database.readRsString(rs, "billNo");
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
		public String getTableId() {
			return tableId;
		}
		public String getServiceType() {
			return serviceType;
		}
		public Double getFoodBill() {
			return foodBill;
		}
		public Double getBarBill() {
			return barBill;
		}
		public String getBillNo() {
			return billNo;
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

		private String orderId;
		private int orderNumber;
		private Date orderDate;
		private String customerName;
		private String customerAddress;
		private String customerNumber;
		private Integer rating_ambiance;
		private Integer rating_qof;
		private Integer rating_service;
		private Integer rating_hygiene;
		private String waiterId;
		private Integer numberOfGuests;
		private Integer state;
		private Integer inHouse;
		private String tableId;
		private String serviceType;
		private Double foodBill;
		private Double barBill;
		private String billNo;
		private String reason;
		private String authId;
		private Integer printCount;
		private String discountCode;
		private Integer loyaltyId;
		private Integer loyaltyPaid;
		private String reviewSuggestions;
		@Override
		public void readFromDB(ResultSet rs) {
			this.orderId = Database.readRsString(rs,"orderId");
			this.orderNumber = Database.readRsInt(rs,"Id");
			this.orderDate = Database.readRsDate(rs, "orderDate");
			this.customerName = Database.readRsString(rs,"customerName");
			this.customerAddress = Database.readRsString(rs,"customerAddress");
			this.customerNumber = Database.readRsString(rs,"customerNumber");
			this.rating_ambiance = Database.readRsInt(rs,"rating_ambiance");
			this.rating_qof = Database.readRsInt(rs,"rating_qof");
			this.rating_service = Database.readRsInt(rs,"rating_service");
			this.rating_hygiene = Database.readRsInt(rs,"rating_hygiene");
			this.waiterId = Database.readRsString(rs,"waiterId");
			this.numberOfGuests = Database.readRsInt(rs,"numberOfGuests");
			this.state = Database.readRsInt(rs,"state");
			this.inHouse = Database.readRsInt(rs,"inhouse");
			this.tableId = Database.readRsString(rs,"tableId");
			this.serviceType = Database.readRsString(rs,"serviceType");
			this.foodBill = Database.readRsDouble(rs, "foodBill");
			this.barBill = Database.readRsDouble(rs, "barBill");
			this.billNo = Database.readRsString(rs, "billNo");
			this.reason = Database.readRsString(rs, "reason");
			this.authId = Database.readRsString(rs, "authId");
			this.printCount = Database.readRsInt(rs, "printCount");
			this.discountCode = Database.readRsString(rs, "discountCode");
			this.loyaltyId = Database.readRsInt(rs, "loyaltyId");
			this.loyaltyPaid = Database.readRsInt(rs, "loyaltyPaid");
			this.reviewSuggestions = Database.readRsString(rs, "reviewSuggestions");
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

		public String getState() {
			return state;
		}

		private int mTableId;
		private String mUserId;
		private String mOrderId;
		private String waiterId;
		private String state;

		@Override
		public void readFromDB(ResultSet rs) {
			this.mTableId = Database.readRsInt(rs, "tableId");
			this.mUserId = Database.readRsString(rs, "userId");
			this.mOrderId = Database.readRsString(rs, "orderId");
			this.waiterId = Database.readRsString(rs, "waiterId");
			this.state = Database.readRsString(rs, "state");
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
		public double getRate() {
			return rate;
		}
		public double getInhouseRate() {
			return inhouseRate;
		}
		public double getCostPrice() {
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
		public String getShortForm(){
			return shortForm;
		}
		public int getState(){
			return state;
		}
		public double getIsTaxable() {
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
		public ArrayList<Integer> getAddOnIds(){
			ArrayList<Integer> addOn = new ArrayList<Integer>();
			if(addOns.equals(""))
				return null;
			String[] temp = addOns.split(",");
			for(String t : temp) {
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
		private double rate;
		private double inhouseRate;
		private double costPrice;
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
			this.rate = Database.readRsDouble(rs, "rate");
			this.inhouseRate = Database.readRsDouble(rs, "inhouseRate");
			this.costPrice = Database.readRsDouble(rs, "costPrice");
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
		public int getRate() {
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
		private String menuId;
		private String vegType;
		private String title;
		private String category;
		private String waiterId;
		private String specs;
		private int rate;
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
			this.rate = Database.readRsInt(rs, "rate");
			this.qty = Database.readRsInt(rs, "qty");
			this.specs = Database.readRsString(rs, "specs");
			this.billNo = Database.readRsString(rs, "billNo");
			this.reason = Database.readRsString(rs, "reason");
			this.station = Database.readRsString(rs, "station");
			this.isKOTPrinted = Database.readRsInt(rs, "isKotPrinted");
			this.isTaxable = Database.readRsInt(rs, "isTaxable");
			this.itemId = Database.readRsInt(rs, "itemId");
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
		public int getRate() {
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
		private int rate;
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
			this.rate = Database.readRsInt(rs, "rate");
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

		public int getValue() {
			return value;
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
		
		public String[] getValidCollections() {
			return validCollections.split(",");
		}

		private String name;
		private String description;
		private int type;
		private int value;
		private String startDate;
		private String expiryDate;
		private String usageLimit;
		private String validCollections;
		
		@Override
		public void readFromDB(ResultSet rs) {
			this.name = Database.readRsString(rs, "name");
			this.description = Database.readRsString(rs, "description");
			this.type = Database.readRsInt(rs, "type");
			this.value = Database.readRsInt(rs, "value");
			this.startDate = Database.readRsString(rs, "startDate");
			this.expiryDate = Database.readRsString(rs, "expiryDate");
			this.usageLimit = Database.readRsString(rs, "usageLimit");
			this.validCollections = Database.readRsString(rs, "validCollections");
		}	
	}
	
	//DailyDiscountReport-ap
	public static class DailyDiscountReport implements Database.OrderOnEntity {
		private String name;
		private Integer type;
		private String value;
		private String description;
		//payment
		private Double sumDiscount;
		private Double sumTotal;
		private Double avgDiscount;
		private Double avgTotal;
		private Integer paymentId;
		private String orderDate;
		private Integer ordersAffected;
		private Integer sumDiscountedTotal;
		private String discountPer;
		
		@Override
		public void readFromDB(ResultSet rs) {
			this.name = Database.readRsString(rs, "name");
			this.type = Database.readRsInt(rs, "type");
			this.value = Database.readRsString(rs, "value");
			this.description = Database.readRsString(rs, "description");
			//payment table
			this.sumDiscount = Database.readRsDouble(rs, "sumDiscount");
			this.sumTotal = Database.readRsDouble(rs, "sumTotal");
			this.avgDiscount = Database.readRsDouble(rs, "avgDiscount");
			this.avgTotal = Database.readRsDouble(rs, "avgTotal");
			this.paymentId = Database.readRsInt(rs, "paymentId");
			this.orderDate=Database.readRsString(rs, "orderDate");
			this.discountPer=Database.readRsString(rs, "discountPer");
			this.ordersAffected = Database.readRsInt(rs, "ordersAffected");
			this.sumDiscountedTotal = Database.readRsInt(rs, "sumDiscountedTotal");
		}
		public String getName() {
			return name;
		}
		public Integer getType() {
			return type;
		}
		public String getValue() {
			return value;
		}
		public String getDescription() {
			return description;
		}
		public Double getDiscount() {
			return sumDiscount;
		}
		public Double getTotal() {
			return sumTotal;
		}
		public String getTotalPer() {
			Double per=((sumDiscount/sumTotal)*100);
			return per.toString()+" %";
		}
		public Double getAvgTotal() {
			return avgTotal;
		}
		public Double getAvgDiscount() {
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
		public Integer getSumDiscountedTotal() {
			return sumDiscountedTotal;
		}
		public String getDiscountPer() {
			return discountPer;
		}
	}

	//DiscountReport-ap
	public static class DiscountReport implements Database.OrderOnEntity {
		private String discountName;
		private String orderDate;
		private double total;
		private double discount;
		private String customerName;
		private double discountedTotal;
		@Override
		public void readFromDB(ResultSet rs) {
			this.discountName = Database.readRsString(rs, "discountName");
			this.orderDate = Database.readRsString(rs, "orderDate");
			this.total = Database.readRsInt(rs, "total");
			this.discount = Database.readRsDouble(rs, "discount");
			this.customerName = Database.readRsString(rs, "customerName");
			this.discountedTotal = Database.readRsDouble(rs, "discountedTotal");
			
		}
		public String getDiscountName() {
			return discountName;
		}
		public String getOrderDate() {
			return orderDate;
		}
		public double getTotal() {
			return total;
		}
		public double getDiscount() {
			return discount;
		}
		public String getCustomerName() {
			return customerName;
		}
		public double getDiscountedTotal() {
			return discountedTotal;
		}
	}
	
	//GrossSaleReport-ap
	public static class GrossSaleReport implements Database.OrderOnEntity {
		private double grossTotal;
		private double grossDiscount;
		private double grossTaxes;
		private double grossServiceCharge;
		private double NetSales;
		private double grossExpenses;
		private double Total;
		private double sumVoids;
		private double sumReturns;
		private Integer countVoids;
		private Integer countReturns;
		@Override
		public void readFromDB(ResultSet rs) {
			this.grossTotal = Database.readRsDouble(rs, "grossTotal");
			this.grossDiscount = Database.readRsDouble(rs, "grossDiscount");
			this.grossTaxes = Database.readRsDouble(rs, "grossTaxes");
			this.grossServiceCharge = Database.readRsDouble(rs, "grossServiceCharge");
			this.NetSales = Database.readRsDouble(rs, "NetSales");
			this.grossExpenses = Database.readRsDouble(rs, "grossExpenses");
			this.Total = Database.readRsDouble(rs, "Total");
			this.sumVoids = Database.readRsDouble(rs, "sumVoids");
			this.sumReturns = Database.readRsDouble(rs, "sumReturns");
			this.countVoids = Database.readRsInt(rs, "countVoids");
			this.countReturns = Database.readRsInt(rs, "countReturns");
			
		}
		public double getGrossTotal() {
			return grossTotal;
		}
		public double getGrossDiscount() {
			return grossDiscount;
		}
		public double getGrossTaxes() {
			return grossTaxes;
		}
		public double getGrossServiceCharge() {
			return grossServiceCharge;
		}
		public double getNetSales() {
			return NetSales;
		}
		public double getGrossExpenses() {
			return grossExpenses;
		}
		public double getGrandTotal() {
			return Total;
		}
		public double getSumVoids() {
			return sumVoids;
		}
		public double getSumReturns() {
			return sumReturns;
		}
		public Integer getCountVoids() {
			return countVoids;
		}
		public Integer getCountReturns() {
			return countReturns;
		}
	}
	
	//CollectionWiseReportA-ap
	public static class CollectionWiseReportA implements Database.OrderOnEntity {
		private String collection;
		private double grossTotal;
		private double averagePrice;
		private Integer noOrdersAffected;
		private String noOrdersAffectedPer;
		private Integer totalQuantityOrdered;
		private String totalQuantityOrderedPer ;

		@Override
		public void readFromDB(ResultSet rs) {
			this.collection = Database.readRsString(rs, "collection");
			this.grossTotal = Database.readRsDouble(rs, "grossTotal");
			this.averagePrice = Database.readRsDouble(rs, "averagePrice");
			this.noOrdersAffected = Database.readRsInt(rs, "noOrdersAffected");
			this.noOrdersAffectedPer = Database.readRsString(rs, "noOrdersAffectedPer");
			this.totalQuantityOrdered = Database.readRsInt(rs, "totalQuantityOrdered");
			this.totalQuantityOrderedPer = Database.readRsString(rs, "totalQuantityOrderedPer");
		}
		
		public String getCollection() {
			return collection;
		}
		public double getGrossTotal() {
			return grossTotal;
		}
		public double getAveragePrice() {
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
	
	//CollectionWiseReportB-ap
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
	
	//DailyOperationReport-ap
	public static class DailyOperationReport implements Database.OrderOnEntity {
		//total Revenue
		private double totalRevenue;
		private double grossTotal;
		private double grossDiscount;
		private double grossTaxes;
		private double grossServiceCharge;
		private double NetSales;
		//total operating cost
		private double totalOperatingCost;
		private double INVENTORY;
		private double LABOUR;
		private double RENT;
		private double ELECTRICITY_BILL;
		private double GAS_BILL;
		private double PETROL;
		private double TELEPHONE_BILL;
		private double MOBILE_RECHARGE;
		private double INTERNET;
		private double SOFTWARE;
		private double COMPUTER_HARDWARE;
		private double REPAIRS;
		private double OTHERS;
		private double CASH_LIFT;
		//Total Operating Margin
		private double totalOperatingMargin;
		private double paidIn;
		private double paidOut;
		//operating metrics
		//main
		private String serviceType;
		private double AvgAmountPerGuest;
		private double AvgAmountPerCheck;
		private double Total;
		private double noOfGuests;
		private double noOfBills;
		
		//extras
		private double AvgAmountPerTableTurned;
		private double voids;
		private double returns;
		
		@Override
		public void readFromDB(ResultSet rs) {
			//total Revenue
			this.totalRevenue = Database.readRsDouble(rs, "totalRevenue");
			this.grossTotal = Database.readRsDouble(rs, "grossTotal");
			this.grossDiscount = Database.readRsDouble(rs, "grossDiscount");
			this.grossTaxes = Database.readRsDouble(rs, "grossTaxes");
			this.grossServiceCharge = Database.readRsDouble(rs, "grossServiceCharge");
			this.NetSales = Database.readRsDouble(rs, "NetSales");
			//total operating cost
			this.totalOperatingCost = Database.readRsDouble(rs, "totalOperatingCost");
			this.INVENTORY = Database.readRsDouble(rs, "INVENTORY");
			this.LABOUR = Database.readRsDouble(rs, "LABOUR");
			this.RENT = Database.readRsDouble(rs, "RENT");
			this.ELECTRICITY_BILL = Database.readRsDouble(rs, "ELECTRICITY_BILL");
			this.GAS_BILL = Database.readRsDouble(rs, "GAS_BILL");
			this.PETROL = Database.readRsDouble(rs, "PETROL");
			this.TELEPHONE_BILL = Database.readRsDouble(rs, "TELEPHONE_BILL");
			this.MOBILE_RECHARGE = Database.readRsDouble(rs, "MOBILE_RECHARGE");
			this.INTERNET = Database.readRsDouble(rs, "INTERNET");
			this.SOFTWARE = Database.readRsDouble(rs, "SOFTWARE");
			this.COMPUTER_HARDWARE = Database.readRsDouble(rs, "COMPUTER_HARDWARE");
			this.REPAIRS = Database.readRsDouble(rs, "REPAIRS");
			this.OTHERS = Database.readRsDouble(rs, "OTHERS");
			this.CASH_LIFT = Database.readRsDouble(rs, "CASH_LIFT");
			//Total Operating Margin
			this.totalOperatingMargin = Database.readRsDouble(rs, "totalOperatingMargin");
			this.paidIn = Database.readRsDouble(rs, "paidIn");
			this.paidOut = Database.readRsDouble(rs, "paidOut");
			//operating metrics
			//main
			this.serviceType = Database.readRsString(rs, "serviceType");
			this.AvgAmountPerGuest = Database.readRsDouble(rs, "AvgAmountPerGuest");
			this.AvgAmountPerCheck = Database.readRsDouble(rs, "AvgAmountPerCheck");
			this.totalOperatingCost = Database.readRsDouble(rs, "topItemTitle");
			this.Total = Database.readRsDouble(rs, "Total");
			this.noOfGuests = Database.readRsDouble(rs, "noOfGuests");
			this.noOfBills = Database.readRsDouble(rs, "noOfBills");
			//extras
			this.AvgAmountPerTableTurned = Database.readRsDouble(rs, "AvgAmountPerTableTurned");
			this.voids = Database.readRsDouble(rs, "voids");
			this.returns = Database.readRsDouble(rs, "returns");
		}
		//total Revenue
		public double getTotalRevenue() {
			return totalRevenue;
		}
		public double getGrossTotal() {
			return grossTotal;
		}
		public double getGrossDiscount() {
			return grossDiscount;
		}
		public double getGrossTaxes() {
			return grossTaxes;
		}
		public double getGrossServiceCharge() {
			return grossServiceCharge;
		}
		public double getNetSales() {
			return NetSales;
		}
		//total operating cost
		public double gettotalOperatingCost() {
			return totalOperatingCost;
		}
		public double getINVENTORY() {
			return INVENTORY;
		}
		public double getLABOUR() {
			return LABOUR;
		}
		public double getRENT() {
			return RENT;
		}
		public double getELECTRICITY_BILL() {
			return ELECTRICITY_BILL;
		}
		public double getGAS_BILL() {
			return GAS_BILL;
		}
		public double getPETROL() {
			return PETROL;
		}
		public double getTELEPHONE_BILL() {
			return TELEPHONE_BILL;
		}
		public double getMOBILE_RECHARGE() {
			return MOBILE_RECHARGE;
		}
		public double getINTERNET() {
			return INTERNET;
		}
		public double getSOFTWARE() {
			return SOFTWARE;
		}
		public double getCOMPUTER_HARDWARE() {
			return COMPUTER_HARDWARE;
		}
		public double getREPAIRS() {
			return REPAIRS;
		}
		public double getOTHERS() {
			return OTHERS;
		}
		public double getCASH_LIFT() {
			return CASH_LIFT;
		}
		//Total Operating Margin
		public double getTotalOperatingMargin() {
			return totalOperatingMargin;
		}
		public double getPaidIn() {
			return paidIn;
		}
		public double getPaidOut() {
			return paidOut;
		}
		//operating metrics
		//main
		public String getServiceType() {
			return serviceType;
		}
		public double getAvgAmountPerGuest() {
			return AvgAmountPerGuest;
		}
		public double getAvgAmountPerCheck() {
			return AvgAmountPerCheck;
		}
		public double getTotal() {
			return Total;
		}
		public double getNoOfGuests() {
			return noOfGuests;
		}
		public double getNoOfBills() {
			return noOfBills;
		}
		//extras
		public double getAvgAmountPerTableTurned() {
			return AvgAmountPerTableTurned;
		}
		public double getVoids() {
			return voids;
		}
		public double getReturns() {
			return returns;
		}
	}

	//itemWiseReport-ap (edited)
	public static class itemWiseReport implements Database.OrderOnEntity {
		private String category; //Jason
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
	
	//LunchDinnerSalesReport-ap (edited)
	public static class LunchDinnerSalesReport implements Database.OrderOnEntity {
		private double foodBill;
		private double barBill;
		private double cash; 
		private double card;
		private double VISA;
		private double MASTERCARD;
		private double MAESTRO;
		private double AMEX;
		private double RUPAY;
		private double MSWIPE;
		private double ZOMATO;
		private double PAYTM;
		private double SWIGGY;
		private double MAGIC_PIN;
		private double OTHERS;
		private Integer inhouse;
		private double pax;
		@Override
		public void readFromDB(ResultSet rs) {
			this.foodBill = Database.readRsDouble(rs, "foodBill");
			this.barBill = Database.readRsDouble(rs, "barBill");
			this.inhouse = Database.readRsInt(rs, "inhouse");
			this.pax = Database.readRsInt(rs, "pax");
			this.cash = Database.readRsDouble(rs, "cash");
			this.card = Database.readRsDouble(rs, "card");
			this.VISA = Database.readRsDouble(rs, "VISA");
			this.MASTERCARD = Database.readRsDouble(rs, "MASTERCARD");
			this.MAESTRO = Database.readRsDouble(rs, "MAESTRO");
			this.AMEX = Database.readRsDouble(rs, "AMEX");
			this.RUPAY = Database.readRsDouble(rs, "RUPAY");
			this.MSWIPE = Database.readRsDouble(rs, "MSWIPE");
			this.ZOMATO = Database.readRsDouble(rs, "ZOMATO");
			this.PAYTM = Database.readRsDouble(rs, "PAYTM");
			this.SWIGGY = Database.readRsDouble(rs, "SWIGGY");
			this.MAGIC_PIN = Database.readRsDouble(rs, "MAGICPIN");
			this.OTHERS = Database.readRsDouble(rs, "OTHERS");
		}
		public double getFoodBill() {
			return foodBill;
		}
		public double getBarBill() {
			return barBill;
		}
		public double getPax() {
			return pax;
		}
		public double getInhouse() {
			return inhouse;
		}
		public double getCash() {
			return cash;
		}
		public double getCard() {
			return card;
		}
		public double getVISA() {
			return VISA;
		}
		public double getMASTERCARD() {
			return MASTERCARD;
		}
		public double getMAESTRO() {
			return MAESTRO;
		}
		public double getAMEX() {
			return AMEX;
		}
		public double getRUPAY() {
			return RUPAY;
		}
		public double getMSWIPE() {
			return MSWIPE;
		}
		public double getZOMATO() {
			return ZOMATO;
		}
		public double getPAYTM() {
			return PAYTM;
		}
		public double getSWIGGY() {
			return SWIGGY;
		}
		public double getMAGIC_PIN() {
			return MAGIC_PIN;
		}
		public double getOTHERS() {
			return OTHERS;
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
	
	public static class YearlyReport implements Database.OrderOnEntity{

		public int getTotalOrders() {
			return totalOrders;
		}
		public String getMonthName() {
			return new DateFormatSymbols().getMonths()[month-1].substring(0, 3).toUpperCase();
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

	public static class IncentiveReport implements Database.OrderOnEntity{

		public double getIncentive() {
			return incentive;
		}
		public double getSale() {
			return sale;
		}
		public String getUserId() {
			return userId;
		}
		public void setIncentive(double incentive) {
			this.incentive = incentive;
		}
		public void setSale(double sale) {
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

		private double incentive;
		private double sale;
		private String userId;
		private String title;
		private int quantity;
		
		@Override
		public void readFromDB(ResultSet rs) {
			this.incentive = Database.readRsDouble(rs, "incentive");
			this.sale = Database.readRsDouble(rs, "sale");
			this.userId = Database.readRsString(rs, "userId");
			this.title = Database.readRsString(rs, "title");
			this.quantity = Database.readRsInt(rs, "qty");
		}
	}
	
	public static class Employee implements Database.OrderOnEntity{

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
		}
	}
	
	public static class Attendance implements Database.OrderOnEntity{
		
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
			if(checkInTime.equals(""))
				return null;
			else if(checkInTime.length() == 5)
				return checkInTime;
			LocalDateTime now = LocalDateTime.parse(checkInTime);
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
	        return now.format(formatter);
		}
		public String getCheckOutTime() {
			return checkOutTime;
		}
		public String getCheckOutTimeHHMM() {
			if(checkOutTime.equals(""))
				return null;
			else if(checkOutTime.length() == 5)
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
	
	public static class Report implements Database.OrderOnEntity{

		private String hotelId;
		private String billNo;
		private String orderId;
		private String orderDate;
		private double foodBill;
		private double barBill;
		private double discount;
		private double total;
		private double serviceCharge; 
		private double serviceTax;
		private double gst;
		private double VATFood;
		private double VATBar;
		private double sbCess;
		private double kkCess;
		private double tip;
		private double cashPayment; 
		private double cardPayment;
		private double inhouseSales;
		private double homeDeliverySales;
		private double takeAwaySales;
		private String cardType;
		private int inhouse;
		private int pax;
		private int tableId;
		private int checks;
		private String discountName;
		private int orderCount;
		private int printCount;
		private int loyaltyAmount;
		private int complimentary;
		private double grossTotal;
		
		@Override
		public void readFromDB(ResultSet rs) {
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.billNo = Database.readRsString(rs, "billNo");
			this.orderId = Database.readRsString(rs, "orderId");
			this.orderDate = Database.readRsString(rs, "orderDate");
			this.foodBill = Database.readRsDouble(rs, "foodBill");
			this.barBill = Database.readRsDouble(rs, "barBill");
			this.discount = Database.readRsDouble(rs, "discount");
			this.total = Database.readRsDouble(rs, "total");
			this.grossTotal = Database.readRsDouble(rs, "grossTotal");
			this.serviceCharge = Database.readRsDouble(rs, "serviceCharge");
			this.serviceTax = Database.readRsDouble(rs, "serviceTax");
			this.gst = Database.readRsDouble(rs, "gst");
			this.VATFood = Database.readRsDouble(rs, "VATFOOD");
			this.VATBar = Database.readRsDouble(rs, "VATBAR");
			this.sbCess = Database.readRsDouble(rs, "sbCess");
			this.kkCess = Database.readRsDouble(rs, "kkCess");
			this.tip = Database.readRsDouble(rs, "tip");
			this.cashPayment = Database.readRsDouble(rs, "cashPayment");
			this.cardPayment = Database.readRsDouble(rs, "cardPayment");
			this.inhouseSales = Database.readRsDouble(rs, "inhouse");
			this.homeDeliverySales = Database.readRsDouble(rs, "homeDelivery");
			this.takeAwaySales = Database.readRsDouble(rs, "takeAway");
			this.orderCount = Database.readRsInt(rs, "orderCount");
			this.printCount = Database.readRsInt(rs, "printCount");
			this.inhouse = Database.readRsInt(rs, "inhouse");
			this.pax = Database.readRsInt(rs, "pax");
			this.tableId = Database.readRsInt(rs, "tableId");
			this.checks = Database.readRsInt(rs, "checks");
			this.discountName = Database.readRsString(rs, "discountName");
			this.cardType = Database.readRsString(rs, "cardType");
			this.loyaltyAmount = Database.readRsInt(rs, "loyaltyAmount");
			this.complimentary = Database.readRsInt(rs, "complimentary");
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
		public double getFoodBill() {
			return foodBill;
		}
		public double getBarBill() {
			return barBill;
		}
		public double getTotalBill() {
			return barBill+foodBill;
		}
		public double getDiscount() {
			return discount;
		}
		public double getTotal() {
			return total;
		}
		public double getServiceCharge() {
			return serviceCharge;
		}
		public double getServiceTax() {
			return serviceTax;
		}
		public double getGST() {
			return gst;
		}
		public double getVATFood() {
			return VATFood;
		}
		public double getVATBar() {
			return VATBar;
		}
		public double getSbCess() {
			return sbCess;
		}
		public double getKkCess() {
			return kkCess;
		}
		public double getTotalTax() {
			return gst + serviceCharge + serviceTax + VATFood + VATBar + sbCess +kkCess;
		}
		public double getTip() {
			return tip;
		}
		public double getCashPayment() {
			return cashPayment;
		}
		public double getCardPayment() {
			return cardPayment;
		}
		public double getInhouseSales() {
			return inhouseSales;
		}
		public double getHomeDeliverySales() {
			return homeDeliverySales;
		}
		public double getTakeAwaySales() {
			return takeAwaySales;
		}
		public double getOrderCount() {
			return orderCount;
		}
		public double getPrintCount() {
			return printCount;
		}
		public int getInhouse() {
			return inhouse;
		}
		public int getPax() {
			return pax;
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
		public Double getGrossTotal() {
			return grossTotal;
		}
	}
	
	public static class Bank implements Database.OrderOnEntity{

		private String hotelId;
		private String accountNumber;
		private String bankName;
		private String accountName;
		private int balance;
		
		@Override
		public void readFromDB(ResultSet rs) {
			
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.accountNumber = Database.readRsString(rs, "accountNumber");
			this.bankName = Database.readRsString(rs, "bankName");
			this.accountName = Database.readRsString(rs, "accountName");
			this.balance = Database.readRsInt(rs, "balance");
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
		public int getBalance() {
			return balance;
		}
	}
	
	public static class Expense implements Database.OrderOnEntity{
		
		private String type;
		private String date;
		private int amount;
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
		private double quantity;
		private String serviceType;
		/*
		 * c = create new stock
		 * r = read/used while cooking
		 * u = update quanity
		 * d = deleted/remove stock
		 */
		private String crud;
		
		@Override
		public void readFromDB(ResultSet rs) {
			
			this.accountName = Database.readRsString(rs, "accountName");
			this.amount = Database.readRsInt(rs, "amount");
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
		}

		public String getType() {
			return type;
		}
		public String getDate() {
			return date;
		}
		public int getAmount() {
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
		public double getQuantity() {
			return quantity;
		}
		public String getServiceType(){
			return serviceType;
		}
	}
	
	public static class ServiceLog implements Database.OrderOnEntity{
		
		private String hotelId;
		private String serviceDate;
		private String serviceType;
		private String startTimeStamp;
		private String endTimeStamp;
		private int isCurrent;
		private int cashInHand;
		
		@Override
		public void readFromDB(ResultSet rs) {
			
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.serviceDate = Database.readRsString(rs, "serviceDate");
			this.serviceType = Database.readRsString(rs, "serviceType");
			this.startTimeStamp = Database.readRsString(rs, "startTimeStamp");
			this.endTimeStamp = Database.readRsString(rs, "endTimeStamp");
			this.isCurrent = Database.readRsInt(rs, "isCurrent");
			this.cashInHand = Database.readRsInt(rs, "cashInHand");
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

	public static class TotalRevenue implements Database.OrderOnEntity{
		
		private String hotelId;
		private String serviceType;
		private String serviceDate;
		private double cash;
		private double card;
		private double total;
		private double visa;
		private double mastercard;
		private double maestro;
		private double amex;
		private double others;
		private double mswipe;
		private double rupay;
		private double difference;
		private String reason; 
		private String clearance;
		
		@Override
		public void readFromDB(ResultSet rs) {
			
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.serviceDate = Database.readRsString(rs, "serviceDate");
			this.serviceType = Database.readRsString(rs, "serviceType");
			this.cash = Database.readRsDouble(rs, "cash");
			this.card = Database.readRsDouble(rs, "card");
			this.total = Database.readRsDouble(rs, "total");
			this.visa = Database.readRsDouble(rs, "visa");
			this.mastercard = Database.readRsDouble(rs, "mastercard");
			this.maestro = Database.readRsDouble(rs, "maestro");
			this.amex = Database.readRsDouble(rs, "amex");
			this.rupay = Database.readRsDouble(rs, "rupay");
			this.others = Database.readRsDouble(rs, "others");
			this.mswipe = Database.readRsDouble(rs, "mswipe");
			this.difference = Database.readRsDouble(rs, "difference");
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
		public double getCash() {
			return cash;
		}
		public double getCard() {
			return card;
		}
		public double getTotal() {
			return total;
		}
		public double getVisa() {
			return visa;
		}
		public double getMastercard() {
			return mastercard;
		}
		public double getMaestro() {
			return maestro;
		}
		public double getAmex() {
			return amex;
		}
		public double getOthers() {
			return others;
		}
		public double getMSwipe() {
			return mswipe;
		}
		public double getRupay() {
			return rupay;
		}
		public double getDifference() {
			return difference;
		}
		public String getReason() {
			return reason;
		}
		public String getClearance() {
			return clearance;
		}
	}
	
	public static class MPNotification implements Database.OrderOnEntity{
		
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
	
	public static class ServerLog implements Database.OrderOnEntity{
		
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
	
	public static class Specifications implements Database.OrderOnEntity{
		
		private String specification;
		
		public String getSpecification() {
			return specification;
		}
		@Override
		public void readFromDB(ResultSet rs) {
			
			specification = Database.readRsString(rs, "specification");
		}
	}

	public static class AddOn implements Database.OrderOnEntity{
		
		private String name;
		private String menuId;
		private Double inHouseRate;
		private Double deliveryRate;
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
		public Double getInHouseRate() {
			return inHouseRate;
		}
		public Double getDeliveryRate() {
			return deliveryRate;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			
			this.id  = Database.readRsInt(rs, "Id");
			this.name = Database.readRsString(rs, "name");
			this.menuId = Database.readRsString(rs, "menuId");
			this.inHouseRate = Database.readRsDouble(rs, "inHouseRate");
			this.deliveryRate = Database.readRsDouble(rs, "deliveryRate");
		}
	}

	public static class LoyaltyOffer implements Database.OrderOnEntity{
		
		private String name;
		private String description;
		private String offerType;
		private String offer;
		private int points;
		private int count;
		private int id;
		private int status;

		public String getName() {
			return name;
		}
		public String getDescription() {
			return description;
		}
		public String getOfferType() {
			return offerType;
		}
		public String getOffer() {
			return offer;
		}
		public int getPoints() {
			return points;
		}
		public int getCount() {
			return count;
		}
		public int getId() {
			return id;
		}
		public int getStatus() {
			return status;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			
			this.id  = Database.readRsInt(rs, "Id");
			this.points  = Database.readRsInt(rs, "points");
			this.count  = Database.readRsInt(rs, "count");
			this.name = Database.readRsString(rs, "name");
			this.description = Database.readRsString(rs, "description");
			this.offerType = Database.readRsString(rs, "offerType");
			this.offer = Database.readRsString(rs, "offer");
			this.status = Database.readRsInt(rs, "status");
		}
	}

	//itemWiseReport-ap (edited)
	public static class TransacctionHistory implements Database.OrderOnEntity {
		private String trType;
		private String trDetail;
		private double amount;
		private double balance;
		private String trDate;
		private String userId;
		private String authoriser;
		
		public String getTrType() {
			return trType;
		}

		public String getTrDetail() {
			return trDetail;
		}

		public Double getAmount() {
			return amount;
		}

		public Double getBalance() {
			return balance;
		}

		public String getTrDate() {
			return trDate;
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
			this.amount = Database.readRsDouble(rs, "amount");
			this.balance = Database.readRsDouble(rs, "balance");
			this.trDetail = Database.readRsString(rs, "trDate");
			this.userId = Database.readRsString(rs, "userId");
			this.authoriser = Database.readRsString(rs, "authoriser");
		}
	}
	
	//User Authentication
	
	//Tested
	public Boolean validUser(String hotelId, String userId, String passwd) {
		
		User user = getUserById(hotelId, userId);
		if (user != null) {
			return user.getPasswd().equals(passwd);
		}
		return false;
	}

	//Tested
	private String setAuthToken(String userId, String hotelId){
		
		String sql = "UPDATE Users SET authToken = ABS(RANDOM() % 10000000000), timeStamp = '" + LocalDateTime.now().toString() + "' WHERE userId = '" + userId + "'AND hotelId = '"+ hotelId+"';";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "SELECT authToken FROM Users WHERE userId = '" + userId + "'AND hotelId = '"+ hotelId+"';";
		
		User user = db.getOneRecord(sql, User.class, hotelId);
		if (user!=null) {
			return user.getAuthToken();
		}
		return null;
	}

	//Tested
	public String validMPUser(String hotelId, String userId, String passwd) {
		User user = getUserById(hotelId, userId);
		
		if (user != null && user.getPasswd().equals(passwd)) {
			
			return setAuthToken(userId, hotelId);
		}
		String sql = "UPDATE Users SET authToken = 0, timeStamp = NULL WHERE userId = '" + userId + "'AND hotelId = '"+ hotelId+"';";
		db.executeUpdate(sql, hotelId, false);
		return null;
	}

	//Tested
	public boolean validateAccess1(String hotelId, String userId, String passwd) {
		User user = getUserById(hotelId, userId);
		
		if (user != null && user.getPasswd().equals(passwd) && (user.getUserType().equals(UserType.ADMINISTRATOR.getValue()) ||user.getUserType().equals(UserType.OWNER.getValue()) || user.getUserType().equals(UserType.MANAGER.getValue()))) {
			
			return true;
		}
		return false;
	}

	//Tested
	public String validKDSUser(String hotelId, String userId, String passwd) {
		User user = getUserById(hotelId, userId);
		
		if (user != null && user.getPasswd().equals(passwd)) {
			
			if(user.mUserType == UserType.CHEF.getValue() || user.mUserType == UserType.ADMINISTRATOR.getValue()){
			
				return setAuthToken(userId, hotelId);
			}
		}
		String sql = "UPDATE Users SET authToken = 0, timeStamp = NULL WHERE userId = '" + userId + "'AND hotelId = '"+ hotelId+"';";
		db.executeUpdate(sql, false);
		return null;
	}

	//Tested
	public boolean removeToken(String hotelId, String userId){
		
		String sql = "UPDATE Users SET authToken = 0 WHERE userId = '" + userId + "'AND hotelId = '"+ hotelId+"';";
		
		return db.executeUpdate(sql, false);
	}

	//Tested
	public UserType validateToken(String hotelId, String userId, String authToken){
		
		String sql = "SELECT authtoken, timeStamp, userType FROM Users WHERE userId = '" + userId + "'AND hotelId = '"+ hotelId+"';";
		User user = db.getOneRecord(sql, User.class, hotelId);
		String timeStamp = LocalDateTime.now().toString();
		
		if (user!=null) {
			
			int minute = LocalDateTime.parse(user.getTimeStamp()).getMinute();
			int currentMinute = LocalDateTime.now().getMinute();
			int hour = LocalDateTime.parse(user.getTimeStamp()).getHour();
			int currentHour = LocalDateTime.now().getHour();
			
			//Check if it been 30 minutes since any activity.
			if(currentHour-hour == 0 || currentHour-hour == 1 ){
				
				int offset = currentMinute-minute;
				
				if(offset < 0){
					offset += 60;
					if(offset <= 30){
						if(user.getAuthToken().equals(authToken)){
							sql = "UPDATE Users SET timeStamp = '" + timeStamp + "' WHERE userId = '" + userId + "'AND hotelId = '"+ hotelId+"';";
							db.executeUpdate(sql, false);
							return UserType.getType(user.getUserType());
						}
					}
				}else if (offset <= 30 && offset >= 0){
					if(user.getAuthToken().equals(authToken)){
						sql = "UPDATE Users SET timeStamp = '" + timeStamp + "' WHERE userId = '" + userId + "' AND hotelId = '"+ hotelId+"';";
						db.executeUpdate(sql, false);
						return UserType.getType(user.getUserType());
					}
				}
			}
		}
		return UserType.UNAUTHORIZED;
	}
	
	//-------------------------------End User Validation

	//--------------------------------Hotel
	public Hotel getHotelById(String hotelId) {
		String sql = "SELECT * FROM Hotel WHERE hotelId='"+hotelId+"'";
		return db.getOneRecord(sql, Hotel.class, hotelId);
	}
	
	public Boolean hasCashDrawer(String hotelId) {
		String sql = "SELECT * FROM Hotel WHERE hotelId='"+hotelId+"' AND hasCashDrawer == 1";
		return db.hasRecords(sql, hotelId);
	}
	
	public boolean updateHotelFlags(String hotelId, String flags) {
		String sql = "UPDATE Hotel SET flags='"+flags+"'" + " WHERE hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}


	//-------------------------------Collections
	public ArrayList<MenuCollection> getCollections(String hotelId) {
		String sql = "SELECT * FROM Collections  WHERE hotelId='"+hotelId+"'";
		return db.getRecords(sql, MenuCollection.class, hotelId);
	}
	
	public boolean addCollection(String hotelId, String name, String image) {
		
		String sql = "INSERT INTO Collections " +
				"(hotelId, collection, image) " +
				"VALUES('"+ escapeString(hotelId) + 
				"', '"+ escapeString(name) +
				"', '"+ (image.equals("No image")?"":"1") + 
				"');";
		return db.executeUpdate(sql, true);
	}

	//Tested
	public Boolean collectionExists(String hotelId, String collectionName) {
		MenuCollection collection = getCollectionByName(hotelId, collectionName);
		if (collection != null) {
			return true;
		}
		return false;
	}

	public MenuCollection getCollectionByName(String hotelId, String collection) {
		String sql = "SELECT * FROM Collections WHERE collection='" + escapeString(collection) + "' AND hotelId='"+escapeString(hotelId)+"';";
		return db.getOneRecord(sql, MenuCollection.class, hotelId);
	}
	
	public boolean deleteCollection(String hotelId, String collection) {
		String sql = "DELETE FROM Collections WHERE collection = '" + collection + "' AND hotelId='"+hotelId+"';";
		return db.executeUpdate(sql, true);
	}


	//--------------------------------MenuItem

	//Tested
	public Boolean itemExists(String hotelId, String title) {
		String sql = "SELECT * FROM MenuItems WHERE title='" + escapeString(title) + "' AND hotelId='"+escapeString(hotelId)+"';";
		return db.hasRecords(sql, hotelId);
	}

	public boolean isTaxableMenuItem(String hotelId, String menuId) {
		
		String sql = "SELECT * FROM MenuItems WHERE menuId = '" + escapeString(menuId)  + "' AND hotelId='"+escapeString(hotelId)+"' AND isTaxable = 0;";
		
		return db.hasRecords(sql, hotelId);
	}
	
	//Tested
	public String addMenuItem(String hotelId, String title, String description, String category, String station, 
			String flags, int preparationTime, int rate, int inhouseRate, int costPrice, int vegType, 
			String image, int isTaxable) {
		
		String menuId = getNextMenuId(hotelId, category);
		
		String sql = "INSERT INTO MenuItems " +
				"(hotelId, menuId, title, description, category, station, flags, preparationTime, rate, inhouseRate, costPrice, vegType, img, method, shortForm, state, isTaxable) " +
				"VALUES('"+ escapeString(hotelId) + 
				"', '"+ escapeString(menuId) + 
				"', '"+ escapeString(title) + 
				"', '"+ escapeString(description) + 
				"', '"+ escapeString(category) +  
				"', '"+ escapeString(station) + 
				"', '"+ escapeString(flags) + 
				"', '"+ Integer.toString(preparationTime) + 
				"', "+ Integer.toString(rate) + 
				", "+ Integer.toString(inhouseRate) + 
				", "+ Integer.toString(costPrice) + 
				", "+ Integer.toString(vegType) + 
				", '"+ (image.equals("No image")?"":"1") + 
				"', '', '" + generateShortForm(title) +
				"', " + 0 + ", " + isTaxable +
				");";

		if(db.executeUpdate(sql, true)){
			return menuId;
		}
		else
			return "";
	}
	
	public MenuItem getMenuById(String hotelId, String menuId) {
		String sql = "SELECT * FROM MenuItems WHERE menuId='"+escapeString(menuId)+"' AND hotelId='"+hotelId+"';";
		return db.getOneRecord(sql, MenuItem.class, hotelId);
	}

	//Tested
	public MenuItem getMenuItemByTitle(String hotelId, String title) {
		
		String sql = "SELECT * FROM MenuItems WHERE title = '" + escapeString(title)  + "' AND hotelId='"+escapeString(hotelId)+"';";
		
		return db.getOneRecord(sql, MenuItem.class, hotelId);
	}

	public ArrayList<MenuItem> getMenuItemBySearch(String hotelId, String query) {
		
		query = escapeString(query);
		String sql = "SELECT * FROM MenuItems WHERE title LIKE '%" + query 
				+ "%' OR menuId LIKE '%" + query 
				+ "%' OR shortForm LIKE '%" + query 
				+ "%' AND hotelId='"+escapeString(hotelId)+"';";
		
		return db.getRecords(sql, MenuItem.class, hotelId);
	}

	public ArrayList<MenuItem> getMenu(String hotelId) {
		String sql = "SELECT * FROM MenuItems  WHERE hotelId='"+hotelId+"' AND state = "+ MENUITEM_STATE_AVAILABLE+ ";";
		return db.getRecords(sql, MenuItem.class, hotelId);
	}

	public ArrayList<MenuItem> getMenuMP(String hotelId) {
		String sql = "SELECT * FROM MenuItems  WHERE hotelId='"+hotelId+"'";
		return db.getRecords(sql, MenuItem.class, hotelId);
	}
	
	public String getNextMenuId(String hotelId, String category){

		String sql = "SELECT MAX(CAST(menuId AS integer)) AS entityId "+
						"FROM MenuItems WHERE hotelId='"+hotelId+"'";
	
		if(hotelId.equals("h0001")){
			sql = "SELECT MAX(CAST(SUBSTR(menuId,3) AS integer)) AS entityId "+
					"FROM MenuItems WHERE category = '"+ category +"' AND hotelId='"+hotelId+"'";
		}
		
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		String menuId = category.substring(0, 2);
		
		if (entity != null) {
			if(hotelId.equals("h0001")){
				return menuId +  (String.format("%04d", entity.getId() + 1));
			}
			int x = entity.getId() + 1;
			return Integer.toString(x);
		}
		return menuId + "0000";
	}

	//Tested
	public Boolean updateMenuItem(String hotelId, String menuId, String title, String description, String category, String station, 
			String flags, int preparationTime, int rate, int inhouseRate, int costPrice, int vegType, String image, int isTaxable) {
		
		String sql = "UPDATE MenuItems SET" +
				" title = '"+ escapeString(title) + 
				"', description = '"+ escapeString(description) + 
				"', category = '"+ escapeString(category) +  
				"', station = '"+ escapeString(station) + 
				"', flags = '"+ escapeString(flags) + 
				"', preparationTime = '"+ Integer.toString(preparationTime) + 
				"', rate = "+ Integer.toString(rate) +  
				", inhouseRate = "+ Integer.toString(inhouseRate) + 
				", costPrice = "+ Integer.toString(costPrice) + 
				", vegType = "+ Integer.toString(vegType) + 
				", img  ='"+ (image.equals("No image")?"":"1") +
				"', isTaxable = "+ Integer.toString(isTaxable) + 
				" WHERE hotelId = '" + escapeString(hotelId) +
				"' AND menuId = '" + escapeString(menuId) +
				"';";

		return db.executeUpdate(sql, true);
	}
	
	//Tested
	public Boolean updateMenuItemState(String hotelId, String menuId, int state){
		
		String sql = "UPDATE MenuItems SET" +
				" state = "+ Integer.toString(state) + 
				" WHERE hotelId = '" + escapeString(hotelId) +
				"' AND menuId = '" + escapeString(menuId) +
				"';";

		return db.executeUpdate(sql, true);
	}
	
	private String generateShortForm(String title){
		String[] sf  = title.split(" ");
		StringBuilder out = new StringBuilder();
		
		for(int i=0; i<sf.length; i++){
			if(sf[i].length() >= 2)
				out.append(sf[i].substring(0,  2).toUpperCase());
		}
		return out.toString();
	}

	public boolean deleteItem(String hotelId, String menuId) {
		String sql = "DELETE FROM MenuItems WHERE menuId = '" + menuId + "' AND hotelId='"+hotelId+"';";
		return db.executeUpdate(sql, true);
	}
	
	public boolean changeMenuItemState(String hotelId, String menuId, int state){
		
		String sql = "UPDATE MenuItems SET state = '"+state+"' WHERE hotelId = '"+hotelId+"' AND menuId = '"+menuId+"';";
		return db.executeUpdate(sql, true);
	}

	//-----------------------------User

	//Tested
	public Boolean userExists(String hotelId, String userId) {
		User user = getUserById(hotelId, userId);
		if (user != null) {
			return true;
		}
		return false;
	}
	
	//Tested
	public Boolean addUser(String hotelId, String userId, String employeeId, int userType, String userPasswd) {
		String sql = "INSERT INTO Users ('hotelId', 'userId', 'userPasswd', 'employeeId', 'userType', 'authToken', 'timeStamp') VALUES ('"
				+ escapeString(hotelId)
				+ "','"
				+ escapeString(userId)
				+ "','"
				+ escapeString(userPasswd)
				+ "','"
				+ escapeString(employeeId)
				+ "',"
				+ Integer.toString(userType)
				+ ","
				+ "NULL"
				+ ","
				+ "NULL )";

		return db.executeUpdate(sql, true);
	}
	
	//Tested
	public boolean checkPassword(String userId, String password, String hotelId) {	//Jason
		String sql = "select * from Users where userId = '" +escapeString(userId)+ "' and " + 
					"userPasswd = '" + escapeString(password) +"' and hotelId = '" + hotelId + "';";
		return db.hasRecords(sql, hotelId);
	}
	
	//Tested
	public Boolean updateUser(String hotelId, String userId, String password, int userType) {
		
		String sql = "UPDATE Users SET userType= " + Integer.toString(userType) + 
				", userPasswd = '" + escapeString(password) + 
		 		"' WHERE userId='" + escapeString(userId) + "' AND hotelId='"+hotelId+"';";

		return db.executeUpdate(sql, true);
	}

	//Tested
	public User getUserById(String hotelId, String userId) {
		String sql = "SELECT * FROM Users WHERE userId='" + escapeString(userId) + "' AND hotelId='"+escapeString(hotelId)+"';";
		return db.getOneRecord(sql, User.class, hotelId);
	}
	
	public User getUserByEmpId(String hotelId, String employeeId) {
		String sql = "SELECT * FROM Users WHERE employeeId='" + escapeString(employeeId) + "' AND hotelId='"+escapeString(hotelId)+"';";
		return db.getOneRecord(sql, User.class, hotelId);
	}
	
	public ArrayList<User> getAllUsers(String hotelId) {
		String sql = "SELECT * FROM Users WHERE hotelId='"+escapeString(hotelId)+"';";
		return db.getRecords(sql, User.class, hotelId);
	}
	
	//Tested
	public Boolean modifyUserPasswd(String hotelId, String userId, String passwd) {
		String sql = "UPDATE Users SET userPasswd='" + escapeString(passwd)
				+ "' WHERE userId='" + escapeString(userId) + "' AND hotelId='"+escapeString(hotelId)+"'";

		return db.executeUpdate(sql, true);
	}
	
	//Tested
	public boolean deleteUser(String hotelId, String userId) {
		String sql = "DELETE FROM Users WHERE userId = '" + userId + "' AND hotelId='"+hotelId+"';";
		return db.executeUpdate(sql, true);
	}

	//------------------------------Attendance
	//Tested
	public Boolean hasCheckedOut(String hotelId) {
		String sql = "SELECT * FROM Attendance WHERE checkOutTime is NULL AND isPresent = 1;";
		return db.hasRecords(sql, hotelId);
	}

	//Tested
	public Boolean isPresent(String hotelId, String employeeId) {
		String sql = "SELECT * FROM Attendance WHERE checkOutTime is NULL AND isPresent = 1 AND employeeId = '"+employeeId+"';";
		return db.hasRecords(sql, hotelId);
	}

	//Tested
	public Boolean hasCheckedIn(String hotelId, String employeeId) {
		String sql = "SELECT * FROM Attendance WHERE checkInDate = '"+this.getServiceDate(hotelId)+
				"' AND employeeId = '"+employeeId+
				"' AND hotelId = '"+hotelId+
				"' AND shift = 1;";
		return db.hasRecords(sql, hotelId);
	}

	//Tested
	public Boolean hasSecondShift(String hotelId, String startDate, String endDate) {
		String sql = "SELECT * FROM Attendance WHERE shift = 2 AND authorisation = 1 AND checkInDate BETWEEN '"+startDate+"' AND '"+endDate+"';";
		return db.hasRecords(sql, hotelId);
	}

	public ArrayList<Attendance> getAllAttendance(String hotelId, int shift) {
		
		String fromClause = " FROM Attendance where Employee.employeeId == Attendance.employeeId AND Attendance.shift == "+shift+"  AND Attendance.checkInDate == '"+this.getServiceDate(hotelId)+"')";
		
		String sql = "SELECT employeeId, firstName, surName, " + 
						"(SELECT Attendance.Id "+fromClause+" AS Id, " + 
						"(SELECT Attendance.checkInTime "+fromClause+" AS checkInTime, " + 
						"(SELECT Attendance.checkOutTime "+fromClause+" AS checkOutTime, " + 
						"(SELECT Attendance.authorisation "+fromClause+" AS authorisation, " + 
						"(SELECT Attendance.reason  "+fromClause+" AS reason, " + 
						"(SELECT Attendance.checkOutDate "+fromClause+" AS checkOutDate, " + 
						"(SELECT Attendance.isPresent "+fromClause+" AS isPresent, " + 
						"(SELECT Attendance.shift "+fromClause+" AS shift " + 
						"FROM Employee " + 
						"WHERE hotelId = '"+hotelId+"' " +
						"AND designation != 'ADMINISTRATOR';";
		return db.getRecords(sql, Attendance.class, hotelId);
	}
	
	public boolean markExcused(String hotelId, String employeeId, String reason, int shift) {
        
        String sql = "INSERT INTO ATTENDANCE (hotelId, employeeId, checkInTime, checkInDate, authorisation, isPresent, shift, reason)"
        		+ " VALUES ('" + escapeString(hotelId)
        		+ "', '" + escapeString(employeeId)
        		+ "', '" + LocalDateTime.now()
        		+ "', '" + escapeString(this.getServiceDate(hotelId))
        		+ "', "
        		+AUTHORIZE
        		+", "
        		+EXCUSED
        		+", "
        		+shift
        		+ ", '"
        		+reason+"');";
        
        return db.executeUpdate(sql, true);
	}
	
	public boolean markAbsent(String hotelId, String employeeId, int shift) {
        
        String sql = "INSERT INTO ATTENDANCE (hotelId, employeeId, checkInTime, checkInDate, authorisation, isPresent, shift)"
        		+ " VALUES ('" + escapeString(hotelId)
        		+ "', '" + escapeString(employeeId)
        		+ "', '" + LocalDateTime.now()
        		+ "', '" + escapeString(this.getServiceDate(hotelId))
        		+ "', "
        		+AUTHORIZE
        		+", "
        		+ABSENT
        		+", "
        		+shift
        		+");";
        
        return db.executeUpdate(sql, true);
	}
	
	public String checkOut(String hotelId, int attendanceId) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String checkOutDate = now.format(formatter);
        formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        String sql = "UPDATE ATTENDANCE SET checkOutTime = '"+now+"', checkOutDate = '"+checkOutDate+"' WHERE hotelId = '"+hotelId+"' AND Id = '" +attendanceId+ "';";
        
        db.executeUpdate(sql, true);
        
        return now.format(formatter);
	}

	public String checkInEmployee(String hotelId, String employeeId, int shift) {
		
		LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String checkInTime = now.format(formatter);
        
        String sql = "INSERT INTO ATTENDANCE (hotelId, employeeId, checkInTime, checkInDate, authorisation, isPresent, shift)"
        		+ " VALUES ('" + escapeString(hotelId)
        		+ "', '" + escapeString(employeeId)
        		+ "', '" + now
        		+ "', '" + escapeString(this.getServiceDate(hotelId))
        		+ "', "+UNAUTHORIZE+", "+PRESENT+", "+shift+");";
        
        db.executeUpdate(sql, true);
        
        return checkInTime;
	}

	public boolean authorizeEmployee(String hotelId, int attendanceId) {
        
        String sql = "UPDATE ATTENDANCE SET authorisation = "+AUTHORIZE+" WHERE hotelId = '"+hotelId+"' AND Id = '" +attendanceId+ "';";
        
        return db.executeUpdate(sql, true);
	}
	
	public int getLastAttendanceId(String hotelId) {
		String sql = "SELECT MAX (Id) AS entityId FROM Attendance;";
		
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		return entity.getId();
	}
	
	//-------------------------------Employee

	//Tested
	public String addEmployee(String hotelId, String firstName, String middleName, String surName, String address, 
			String contactNumber, String dob, String sex, String hiringDate, String designation, String department, 
			int salary, int bonus, String image, String email) {
		
		String employeeId = getNextEmployeeId(hotelId);
		
		String sql = "INSERT INTO Employee " +
				"(hotelId, employeeId, firstName, surName, address, contactNumber, dob, sex, hiringDate, designation, department"
				+ ", salary, bonus, image, middleName, email) " +
				"VALUES('"+ escapeString(hotelId) + 
				"', '"+ escapeString(employeeId) + 
				"', '"+ escapeString(firstName) + 
				"', '"+ escapeString(surName) + 
				"', '"+ escapeString(address) + 
				"', '"+ escapeString(contactNumber) + 
				"', '"+ escapeString(dob) + 
				"', '"+ escapeString(sex) + 
				"', '"+ escapeString(hiringDate) + 
				"', '"+ escapeString(designation) + 
				"', '"+ escapeString(department) + 
				"', "+ Integer.toString(salary) + 
				", "+ Integer.toString(bonus) + 
				", '"+ (image.equals("No image")?"":"1") +  
				"', '"+ escapeString(middleName) + 
				"', '"+ escapeString(email) +
				"');";

		if(db.executeUpdate(sql, true)){
			return employeeId;
		}
		else
			return "";
	}
	
	//Tested
	public Boolean updateEmployee(String hotelId, String employeeId, String firstName, String middleName, String surName, String address, 
			String contactNumber, String dob, String sex, String hiringDate, String designation, String department, 
			int salary, int bonus, String image, String email) {
		
		String sql = "UPDATE Employee SET" +
				" firstName = '"+ escapeString(firstName) +
				"', middleName = '"+ escapeString(middleName) + 
				"', surName = '"+ escapeString(surName) +  
				"', address = '"+ escapeString(address) + 
				"', contactNumber = '"+ escapeString(contactNumber) +
				"', email = '"+ escapeString(email) +  
				"', dob = '"+ escapeString(dob) +  
				"', sex = '"+ escapeString(sex) + 
				"', hiringDate = '"+ escapeString(hiringDate) + 
				"', designation = '"+ escapeString(designation) + 
				"', department = '"+ escapeString(department) + 
				"', salary = "+ Integer.toString(salary) + 
				", bonus = "+ Integer.toString(bonus) +  
				", image  ='"+ (image.equals("No image")?"":"1") + 
				"' WHERE hotelId = '" + escapeString(hotelId) +
				"' AND employeeId = '" + escapeString(employeeId) +
				"';";

		return db.executeUpdate(sql, true);
	}
	
	//Tested
	public Employee getEmployeeById(String hotelId, String employeeId){
		String sql = "SELECT * FROM Employee WHERE employeeId = '" + escapeString(employeeId) + "' AND hotelId = '"+escapeString(hotelId)+"';";
		return db.getOneRecord(sql, Employee.class, hotelId);
	}
	
	//Tested
	public ArrayList<Employee> getEmployeesByDesignation(String hotelId, Designation designation){
		String sql = "SELECT * FROM Employee WHERE designation = '" + escapeString(designation.toString()) + "' AND hotelId = '"+escapeString(hotelId)+"';";
		return db.getRecords(sql, Employee.class, hotelId);
	}

	public ArrayList<Employee> getAllEmployee(String hotelId) {
		String sql = "SELECT * FROM Employee WHERE hotelId='"+escapeString(hotelId)+"';";
		return db.getRecords(sql, Employee.class, hotelId);
	}

	public ArrayList<Employee> getAllDeliveryEmployee(String hotelId) {
		String sql = "SELECT * FROM Employee WHERE hotelId='"+escapeString(hotelId)+"' AND designation = 'DELIVERYBOY';";
		return db.getRecords(sql, Employee.class, hotelId);
	}
	
	public String getNextEmployeeId(String hotelId){

		String sql = "SELECT MAX(CAST(SUBSTR(employeeId,3) AS integer)) AS entityId "+
					"FROM Employee WHERE hotelId='"+hotelId+"'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		String employeeId = this.getHotelById(hotelId).mHotelCode;	
		
		if (entity != null) {
			return employeeId +  (String.format("%03d", entity.getId() + 1));
		}
		return employeeId + "000";
	}

	//Tested
	public boolean deleteEmployee(String hotelId, String employeeId) {
		String sql = "DELETE FROM Employee WHERE employeeId = '" + employeeId + "' AND hotelId='"+hotelId+"';";
		return db.executeUpdate(sql, true);
	}

	//-------------------------------Orders
	
	public ArrayList<Order> getAllOrders(String hotelId, String date, String query) {
		String sql = "SELECT * FROM Orders WHERE hotelId='"+escapeString(hotelId)+"' ";
		ArrayList<Order> orders = new ArrayList<Order>();
		if(date.length() > 0) {
			sql +=  "AND orderDate='"+date+"' ORDER BY id DESC;";
			return db.getRecords(sql, Order.class, hotelId);
		}
		String sql2;
		String serviceDate = this.getServiceDate(hotelId);
		if(query.trim().length() > 0){
			sql2 = sql + "AND billNo = '"+query+"' AND orderDate='"+serviceDate+"' ORDER BY id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			sql2 = sql + "AND tableId LIKE '%"+query+"%' AND orderDate='"+serviceDate+"' ORDER BY id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			sql2 = sql + "AND customerName LIKE '"+query+"%' AND orderDate='"+serviceDate+"' ORDER BY id DESC;";
			orders.addAll(db.getRecords(sql2, Order.class, hotelId));
			return orders;
		}
		if(date.length() == 0 && query.trim().length() ==0)
			sql +=  " AND orderDate='"+serviceDate+"' ";
		
		sql += " ORDER BY id DESC;";
		
		return db.getRecords(sql, Order.class, hotelId);
	}
	
	public Double getTaxableFoodBill(String hotelId, String orderId){
		
		String sql = "SELECT SUM(OrderItems.rate*OrderItems.qty) AS entityId FROM OrderItems, MenuItems WHERE orderId = '"+ orderId
				+ "' AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId"
				+ " AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '"+hotelId
				+ "' AND (MenuItems.vegType == 1 OR MenuItems.vegType == 2);";
		
		EntityDouble entity = db.getOneRecord(sql, EntityDouble.class, hotelId);
		Double totalBill = entity.getId();
		
		sql = "SELECT SUM(OrderAddOns.rate*OrderAddOns.qty) AS entityId FROM OrderAddOns, OrderItems, MenuItems" + 
				" WHERE OrderAddOns.orderId = '"+orderId+"'" + 
				" AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId" + 
				" AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '"+hotelId+"'" + 
				" AND (MenuItems.vegType == 1 OR MenuItems.vegType == 2)" + 
				" AND OrderAddOns.orderId == OrderItems.orderId" + 
				" AND OrderAddOns.subOrderId == OrderItems.subOrderId" + 
				" AND OrderAddOns.menuId == OrderItems.menuId;";
		
		entity = db.getOneRecord(sql, EntityDouble.class, hotelId);
		totalBill += entity.getId();
		
		return totalBill;
	}
	
	public Double getTaxableBarBill(String hotelId, String orderId){
		
		String sql = "SELECT SUM(OrderItems.rate*OrderItems.qty) AS entityId FROM OrderItems, MenuItems WHERE orderId = '"+ orderId
				+ "' AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId"
				+ " AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '"+hotelId
				+ "' AND (MenuItems.vegType == 3 OR MenuItems.vegType == 4);";
		
		EntityDouble entity = db.getOneRecord(sql, EntityDouble.class, hotelId);
		Double totalBill = entity.getId();
		
		sql = "SELECT SUM(OrderAddOns.rate*OrderAddOns.qty) AS entityId FROM OrderAddOns, OrderItems, MenuItems" + 
				" WHERE OrderAddOns.orderId = '"+orderId+"'" + 
				" AND MenuItems.isTaxable = 0 AND OrderItems.menuId == MenuItems.menuId" + 
				" AND OrderItems.hotelId == MenuItems.hotelId AND OrderItems.hotelId == '"+hotelId+"'" + 
				" AND (MenuItems.vegType == 3 OR MenuItems.vegType == 4)" + 
				" AND OrderAddOns.orderId == OrderItems.orderId" + 
				" AND OrderAddOns.subOrderId == OrderItems.subOrderId" + 
				" AND OrderAddOns.menuId == OrderItems.menuId;";
		
		entity = db.getOneRecord(sql, EntityDouble.class, hotelId);
		totalBill += entity.getId();
		
		return totalBill;
	}

	public OrderItem getOrderedItem(String hotelId, String orderId, String subOrderId, String menuId) {
		
		String sql = "SELECT OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.qty AS qty, "
				+ "MenuItems.title AS title, "
				+ "MenuItems.vegType AS vegType, "
				+ "MenuItems.category AS category, "
				+ "MenuItems.station AS station, "
				+ "OrderItems.specs AS specs, "
				+ "OrderItems.rate AS rate, "
				+ "MenuItems.isTaxable AS isTaxable, "
				+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE OrderItems.orderId='" + orderId 
				+ "' AND OrderItems.subOrderId='" + subOrderId
				+ "' AND OrderItems.menuId=='"+menuId
				+ "' AND OrderItems.hotelId='"+hotelId
				+ "' AND OrderItems.menuId==MenuItems.menuId;";
		return db.getOneRecord(sql, OrderItem.class, hotelId);
	}

	public ArrayList<OrderItem> getOrderedItems(String hotelId, String orderId) {
		
		String sql = "SELECT OrderItems.subOrderId AS subOrderId, "
				+ "OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.Id AS Id, "
				+ "OrderItems.menuId AS menuId, "
				+ "OrderItems.qty AS qty, "
				+ "MenuItems.title AS title, "
				+ "MenuItems.vegType AS vegType, "
				+ "MenuItems.category AS category, "
				+ "MenuItems.station AS station, "
				+ "OrderItems.specs AS specs, "
				+ "OrderItems.rate AS rate, "
				+ "MenuItems.isTaxable AS isTaxable, "
				+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE orderId='" + orderId 
				+ "' AND OrderItems.menuId==MenuItems.menuId AND OrderItems.hotelId='"+hotelId+"' "
				+ "UNION ALL "
				+ "SELECT OrderItemLog.subOrderId AS subOrderId, "
				+ "OrderItemLog.subOrderDate AS subOrderDate, "
				+ "OrderItemLog.Id AS Id, "
				+ "OrderItemLog.menuId AS menuId, "
				+ "OrderItemLog.quantity AS qty, "
				+ "MenuItems.title AS title, "
				+ "MenuItems.vegType AS vegType, "
				+ "MenuItems.category AS category, "
				+ "MenuItems.station AS station, "
				+ "(SELECT specs FROM OrderItems WHERE OrderItems.orderId = '"+orderId+"') AS specs, "
				+ "OrderItemLog.rate AS rate, "
				+ "MenuItems.isTaxable AS isTaxable, "
				+ "OrderItemLog.state AS state "
				+ "FROM MenuItems, OrderItemLog "
				+ "WHERE OrderItemLog.orderId='"+orderId+"' "
				+ "AND OrderItemLog.state = 50 "
				+ "AND OrderItemLog.menuId==MenuItems.menuId "
				+ "AND OrderItemLog.hotelId='"+hotelId+"' "
				+ "ORDER BY menuId;";

		return db.getRecords(sql, OrderItem.class, hotelId);
	}
	
	public ArrayList<EntityString> getUniqueMenuIdForOrder(String hotelId, String orderId){
		String sql = "SELECT distinct OrderItems.menuId AS entityId FROM OrderItems, MenuItems WHERE OrderItems.orderId='"+orderId+"' AND OrderItems.hotelId='"+hotelId
				+"' AND MenuItems.menuId=OrderItems.menuId AND MenuItems.flags NOT LIKE '%ci%';";
		
		return db.getRecords(sql, EntityString.class, hotelId);
	}
	
	public ArrayList<EntityString> getUniqueMenuIdForComplimentaryOrder(String hotelId, String orderId){
		String sql = "SELECT distinct menuId AS entityId FROM OrderItemLog WHERE OrderItemLog.orderId='"+orderId+"'; AND hotelId='"+hotelId+"' AND state="+ORDER_STATE_COMPLIMENTARY+";";
		
		return db.getRecords(sql, EntityString.class, hotelId);
	}
	
	public ArrayList<OrderItem> getOrderedItemForBill(String hotelId, String orderId) {

		ArrayList<EntityString> menuIds = this.getUniqueMenuIdForOrder(hotelId, orderId);
		ArrayList<OrderItem> orderItems = new ArrayList<OrderItem>();
		for (EntityString menuId : menuIds) {
			
			String sql = "SELECT OrderItems.subOrderId AS subOrderId, "
					+ "OrderItems.subOrderDate AS subOrderDate, "
					+ "OrderItems.Id AS Id, "
					+ "OrderItems.menuId AS menuId, "
					+ "SUM(OrderItems.qty) AS qty, "
					+ "MenuItems.title AS title, "
					+ "MenuItems.vegType AS vegType, "
					+ "MenuItems.category AS category, "
					+ "MenuItems.station AS station, "
					+ "OrderItems.rate AS rate, "
					+ "OrderItems.specs AS specs, "
					+ "MenuItems.isTaxable AS isTaxable, "
					+ "OrderItems.state AS state FROM OrderItems, MenuItems "
					+ "WHERE OrderItems.orderId='" + orderId 
					+ "' AND OrderItems.menuId='"+menuId.getEntity()+"' "
					+ "AND MenuItems.menuId='"+menuId.getEntity()+"' "
					+ "AND OrderItems.hotelId='"+hotelId+"';";
	
			orderItems.add(db.getOneRecord(sql, OrderItem.class, hotelId));
		}
		return orderItems;
	}
	
	public ArrayList<OrderItem> getOrderedItemForBillCI(String hotelId, String orderId) {

		String sql = "SELECT OrderItems.subOrderId AS subOrderId, "
				+ "OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.menuId AS menuId, "
				+ "OrderItems.qty AS qty, "
				+ "MenuItems.title AS title, "
				+ "MenuItems.vegType AS vegType, "
				+ "MenuItems.category AS category, "
				+ "MenuItems.station AS station, "
				+ "OrderItems.specs AS specs, "
				+ "OrderItems.rate AS rate, "
				+ "OrderItems.specs AS specs, "
				+ "MenuItems.isTaxable AS isTaxable, "
				+ "OrderItems.state AS state FROM OrderItems, MenuItems WHERE orderId='" + orderId 
				+ "' AND OrderItems.menuId==MenuItems.menuId AND OrderItems.hotelId='"+hotelId+"' "
				+ "AND MenuItems.flags LIKE '%ci;%'";
		
		return db.getRecords(sql, OrderItem.class, hotelId);
	}
	
	public ArrayList<OrderItem> getComplimentaryOrderedItemForBill(String hotelId, String orderId) {

		ArrayList<EntityString> menuIds = this.getUniqueMenuIdForComplimentaryOrder(hotelId, orderId);
		ArrayList<OrderItem> orderItems = new ArrayList<OrderItem>();
		for (EntityString menuId : menuIds) {
			
			String sql = "SELECT OrderItemLog.subOrderId AS subOrderId, "
					+ "OrderItemLog.subOrderDate AS subOrderDate, "
					+ "OrderItemLog.Id AS Id, "
					+ "OrderItemLog.menuId AS menuId, "
					+ "SUM(OrderItemLog.quantity) AS qty, "
					+ "MenuItems.title AS title, "
					+ "MenuItems.vegType AS vegType, "
					+ "MenuItems.category AS category, "
					+ "MenuItems.station AS station, "
					+ "OrderItemLog.rate AS rate, "
					+ "MenuItems.isTaxable AS isTaxable, "
					+ "OrderItemLog.state AS state FROM OrderItemLog, MenuItems "
					+ "WHERE OrderItemLog.orderId='" + orderId 
					+ "' AND OrderItemLog.menuId='"+menuId.getEntity()+"' "
					+ "AND MenuItems.menuId='"+menuId.getEntity()+"' "
					+ "AND OrderItemLog.hotelId='"+hotelId+"';";
	
			orderItems.add(db.getOneRecord(sql, OrderItem.class, hotelId));
		}
		return orderItems;
	}
 	
 	public JSONObject newOrder(String hotelId, String userId, String[] tableIds, int peopleCount, String customer, String mobileNumber, String allergyInfo) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		String hotelType = this.getHotelById(hotelId).getHotelType();
		try {
			String serviceDate = this.getServiceDate(hotelId);
			if (serviceDate==null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started");
				return outObj;
			}
			for (int i=0;i<tableIds.length;i++) {
				sql = "SELECT * FROM OrderTables WHERE tableId='" + tableIds[i]+ "' AND "
						+ "hotelId='"+escapeString(hotelId)+"';";
				TableUsage table = db.getOneRecord(sql, TableUsage.class, hotelId);
				if (table != null) {
					outObj.put("status", -1);
					outObj.put("message", "Table " + tableIds[i] + " not free");
					return outObj;
				}
			}
			StringBuilder tableId = new StringBuilder();
			for(int i=0; i< tableIds.length; i++) {
				tableId.append(tableIds[i]);
			    if(i!=tableIds.length)
			    	tableId.append(",");
			}
			orderId = getNextOrderId(hotelId,userId);
			sql = "INSERT INTO Orders (hotelId, orderId, orderDate, customerName, "
					+ "customerNumber, customerAddress, waiterId, numberOfGuests, "
					+ "state, inhouse, tableId, serviceType, foodBill, barBill) values ('" + 
					hotelId + "', '" + 
				orderId + "', '" + 
				serviceDate + "','" +
				customer + "', '"+mobileNumber+"', '', '" +
				userId + "', " +
				Integer.toString(peopleCount) + ", ";
			
			if(hotelType.equals("PREPAID")){
				sql += Integer.toString(ORDER_STATE_BILLING) + ","+INHOUSE+",'"+tableId.toString()+"','"+getCurrentService(hotelId).getServiceType()+"',0,0);";
			}else{
				sql += Integer.toString(ORDER_STATE_SERVICE) + ","+INHOUSE+",'"+tableId.toString()+"','"+getCurrentService(hotelId).getServiceType()+"',0,0);";
			}
			for (int i=0;i<tableIds.length;i++) {
				sql = sql+"INSERT INTO OrderTables (hotelId, tableId, orderId) values('" + hotelId+ "','" + tableIds[i]+ "','" + orderId + "');";
			}
			if(!mobileNumber.equals("")){
				if(!hasCustomer(hotelId, mobileNumber)) {
					addCustomer(hotelId, customer, mobileNumber,  "", "", "", allergyInfo, Boolean.FALSE);
				}
				else {
					modifyCustomer(hotelId, customer, mobileNumber,"","",allergyInfo, "", Boolean.FALSE);
				}
			}
			db.executeUpdate(sql, true);
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
			return outObj;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 	
 	public JSONObject newQsrOrder(String hotelId, String userId, String customer, String mobileNumber, String allergyInfo, int orderType) {
		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			String serviceDate = this.getServiceDate(hotelId);
			if (serviceDate==null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started");
				return outObj;
			}
			orderId = getNextOrderId(hotelId,userId);
			sql = "INSERT INTO Orders (hotelId, orderId, orderDate, customerName, "
					+ "customerNumber, customerAddress, waiterId, numberOfGuests, state, inhouse, serviceType, foodBill, barBill) values ('" + 
					hotelId + "', '" + 
					orderId + "', '" + 
					serviceDate + "','" +
					customer + "', '"+mobileNumber+"', '', '" +
					userId + "', " +
					1 + ", " +
					Integer.toString(ORDER_STATE_COMPLETE) + "," +
					orderType+",'" +
					getCurrentService(hotelId).getServiceType()+"',0,0);";
			
			if (!hasCustomer(hotelId, mobileNumber)) {
				addCustomer(hotelId, customer, mobileNumber,  "", "", "", allergyInfo, Boolean.FALSE);
			}
			else {
				modifyCustomer(hotelId, customer, mobileNumber,"","",allergyInfo, "", Boolean.FALSE);
			}
			db.executeUpdate(sql, true);
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
			outObj.put("orderNumber", this.getOrderNumber(hotelId, orderId));
			return outObj;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 	
 	public JSONObject placeOrder(String hotelId, String userId, String customer, String phone, String address, int orderType, String allergyInfo) {
 		JSONObject outObj = new JSONObject();
		String orderId = "";
		String sql = "";
		try {
			String serviceDate = this.getServiceDate(hotelId);
			Boolean status= false;
			if (serviceDate==null) {
				outObj.put("status", -1);
				outObj.put("message", "Service has not started for the day");
				return outObj;
			}
			if(!phone.equals("")){
				if (!hasCustomer(hotelId, phone)) {
					status = addCustomer(hotelId, customer, phone, address, "", "", allergyInfo, Boolean.FALSE);
				}else {
					status = modifyCustomer(hotelId, customer, phone,"","",allergyInfo,address, Boolean.FALSE);
				}
			}

			if (!status) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to update customer information");
				return outObj;
			}

			String orderState = Integer.toString(ORDER_STATE_SERVICE);
			String hotelType = getHotelById(hotelId).getHotelType();
			if(hotelType.equals("PREPAID"))
				orderState = Integer.toString(ORDER_STATE_BILLING);
				
			orderId = getNextOrderId(hotelId, userId);
			sql = "INSERT INTO Orders (hotelId, orderId, orderDate, customerName, "
					+ "customerNumber, customerAddress, rating_ambiance, rating_qof,"
					+ "rating_service, rating_hygiene, waiterId, numberOfGuests, state, inhouse, serviceType) values ('" + 
					hotelId + "', '" + 
				orderId + "', '" + 
				serviceDate + "','" +
				customer + "', '"+escapeString(phone)+"', '"+escapeString(address)+"', 5, 5, 5, 5, '" +
				userId + "', 1, " +
				orderState + ","+orderType+",'"+ getCurrentService(hotelId).getServiceType()+"');";
			if (!db.executeUpdate(sql, true)) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to create home delivery order");
				return outObj;
			}
			outObj.put("status", 0);
			outObj.put("orderId", orderId);
			return outObj;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
 	}
 	
 	public JSONObject newHomeDeliveryOrder(String hotelId, String userId, String customer, String phone, String address, String allergyInfo) {
		return this.placeOrder(hotelId, userId, customer, phone, address, HOME_DELIVERY, allergyInfo);
	}
 	
 	public JSONObject newTakeAwayOrder(String hotelId, String userId, String customer, String phone, String allergyInfo) {
 		return this.placeOrder(hotelId, userId, customer, phone, "", TAKE_AWAY, allergyInfo);
	}
 	
 	public JSONObject newBarOrder(String hotelId, String userId, String customer, String phone, String address, String allergyInfo) {
 		return this.placeOrder(hotelId, userId, customer, phone, address, BAR, allergyInfo);
	}
	
	public Boolean checkOutOrder(String hotelId, String orderId) {
		return checkOutOrder(hotelId, orderId, "");
	}
	
	public Boolean checkOutOrder(String hotelId, String orderId, String employeeId) {
		String hotelType = this.getHotelById(hotelId).getHotelType();
		String sql = "DELETE FROM OrderDelivery WHERE hotelId='"+hotelId+"' AND orderId='"+orderId+"';";
		if (!employeeId.equals("")) {
			sql += "INSERT INTO OrderDelivery (hotelId, orderId, employeeId) VALUES ('"+hotelId+"','"+orderId+"','"+employeeId+"');";
		}
		if(hotelType.equals("PREPAID"))
			sql += " UPDATE Orders SET state=" +Integer.toString(ORDER_STATE_COMPLETE)+ " WHERE orderId='" +orderId+ "' AND hotelId='"+hotelId+"';"
					+ " UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_COMPLETE) + " WHERE orderId='" + orderId + "' AND hotelId='"+hotelId+"'";
		else
			sql += " UPDATE Orders SET state=" +Integer.toString(ORDER_STATE_BILLING)+ " WHERE orderId='" +orderId+ "' AND hotelId='"+hotelId+"';"
					+ " UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_COMPLETE) + " WHERE orderId='" + orderId + "' AND hotelId='"+hotelId+"'";
			
		return db.executeUpdate(sql, true);
	}
	
	private String updateBillNoInOrders(String hotelId, String orderId) {
		
		String sql = "SELECT DISTINCT billNo FROM OrderItems WHERE hotelId = '"+hotelId+"' AND orderId='"+orderId+"';";
		
		ArrayList<BillNoFeild> billNos = db.getRecords(sql, BillNoFeild.class, hotelId);
		
		StringBuilder billNo = new StringBuilder();
		
		int offset = 1;
		for (BillNoFeild billNoFeild : billNos) {
			billNo.append(billNoFeild.getBillNo());
			if(billNos.size() !=offset)
				billNo.append(";");
			offset++;
		}
		
		sql = "UPDATE Orders SET billNo = '"+billNo.toString()+"' WHERE hotelId = '"+hotelId+"' AND orderId='"+orderId+"';";

		if(db.executeUpdate(sql, true)){
			return billNo.toString();
		}
		return "";
	}

	public Boolean changeOrderStatus(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET state=" +Integer.toString(ORDER_STATE_OFFKDS)+ " WHERE orderId='" +orderId+ "' AND hotelId='"+hotelId+"';";
		return db.executeUpdate(sql, true);
	}

	public Boolean markPaymentComplete(String hotelId, String orderId) {
		String sql = "DELETE FROM OrderTables WHERE orderId='" + orderId+ "' AND hotelId='"+hotelId+"';"
				+	"UPDATE Orders SET state=" +Integer.toString(ORDER_STATE_COMPLETE)+ " WHERE orderId='" +orderId+ "' AND hotelId='"+hotelId+"';";
		return db.executeUpdate(sql, true);
	}

	public Boolean changeOrderStatusToService(String hotelId, String orderId) {
		String sql = "UPDATE Orders SET state=" +Integer.toString(ORDER_STATE_SERVICE)+ " WHERE orderId='" +orderId+ "' AND hotelId='"+hotelId+"';";
		return db.executeUpdate(sql, true);
	}
	
	public Boolean editSubOrder(String hotelId, String orderId, String subOrderId, String menuId, int qty) {
		String sql = null;
		if (qty==0) {
			sql = "DELETE FROM OrderItems WHERE orderId='"+orderId+"' AND subOrderId=='" + subOrderId + "' AND menuId='" + menuId + "' AND hotelId='"+hotelId+"' AND state=" + Integer.toString(SUBORDER_STATE_PENDING) + ";";
		}
		else {
			sql = "UPDATE OrderItems SET qty=" + Integer.toString(qty) + " WHERE orderId='"+orderId+"' AND subOrderId=='" + subOrderId + "' AND menuId='" + menuId + "' AND hotelId='"+hotelId+"' AND state=" + Integer.toString(SUBORDER_STATE_PENDING) + ";";
		}
		int itemId = qty+1;
		this.removeOrderedAddon(hotelId, orderId, subOrderId, menuId, itemId);
		this.removeOrderedSpecification(hotelId, orderId, subOrderId, menuId, itemId);
		return db.executeUpdate(sql, true);
	}

	public Boolean removeOrderedSpecification(String hotelId, String orderId, String subOrderId, String menuId, int itemId){
		String sql = "DELETE FROM OrderSpecifications WHERE orderId='"+orderId+"' AND subOrderId=='" + subOrderId + "' AND menuId='" + menuId + "' AND hotelId='"+hotelId+"' AND itemId=" + itemId + ";";

		return db.executeUpdate(sql, true);
	}

	public Boolean removeOrderedAddon(String hotelId, String orderId, String subOrderId, String menuId, int itemId){
		String sql = "DELETE FROM OrderAddons WHERE orderId='"+orderId+"' AND subOrderId=='" + subOrderId + "' AND menuId='" + menuId + "' AND hotelId='"+hotelId+"' AND itemId=" + itemId + ";";

		return db.executeUpdate(sql, true);
	}

	public Boolean updateFoodBill(String hotelId, String orderId, String menuId, Integer qty, boolean isCancelled){
		Order order = getOrderById(hotelId, orderId);
		MenuItem menu = getMenuById(hotelId, menuId);
		double rate = 0;
		if(order.getInHouse()==1 || order.getInHouse()==3)
			rate = menu.getInhouseRate();
		else
			rate = menu.getRate();
		
		int veg = menu.getVegType();
		Double total = 0.0;
		String sql = null;
		if(isCancelled){
			if(veg == 3){
				total = order.getBarBill()-rate;
				sql = "UPDATE Orders SET foodBill = "+order.getFoodBill()+", barBill = "+total+" WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
			}else{
				total = order.getFoodBill()-rate;
				sql = "UPDATE Orders SET foodBill = "+total+", barBill = "+order.getBarBill()+" WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
			}
		}
		else{
			if(veg == 3){
				total = rate*qty + order.getBarBill();
				sql = "UPDATE Orders SET foodBill = "+order.getFoodBill()+", barBill = "+total+" WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
			}else{
				total = rate*qty + order.getFoodBill();
				sql = "UPDATE Orders SET foodBill = "+total+", barBill = "+order.getBarBill()+" WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
			}
		}
		return db.executeUpdate(sql, true);
	}

	public Boolean updateFoodBillAddOn(String hotelId, String orderId, String subOrderId, String menuId, int itemId){
		Order order = getOrderById(hotelId, orderId);
		int veg = getMenuById(hotelId, menuId).getVegType();
		ArrayList<OrderAddOn> addOns = this.getOrderedAddOns(hotelId, orderId, subOrderId, menuId, itemId);
		for (OrderAddOn addOn : addOns) {
			double rate = addOn.getRate();
			Double total = 0.0;
			String sql = null;
			if(veg == 3){
				total = rate*addOn.getQty() + order.getBarBill();
				sql = "UPDATE Orders SET foodBill = "+order.getFoodBill()+", barBill = "+total+" WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
			}else{
				total = rate*addOn.getQty() + order.getFoodBill();
				sql = "UPDATE Orders SET foodBill = "+total+", barBill = "+order.getBarBill()+" WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
			}
			if(!db.executeUpdate(sql, true))
				return false;
		}
		return true;	
	}
	public Boolean updateFoodBillAddOnReturn(String hotelId, String orderId, String subOrderId, String menuId, OrderAddOn addOn){
		Order order = getOrderById(hotelId, orderId);
		int veg = getMenuById(hotelId, menuId).getVegType();
			double rate = addOn.getRate();
			Double total = 0.0;
			String sql = null;
			if(veg == 3){
				total = order.getBarBill()-rate*addOn.getQty();
				sql = "UPDATE Orders SET foodBill = "+order.getFoodBill()+", barBill = "+total+" WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
			}else{
				total = order.getFoodBill()-rate*addOn.getQty();
				sql = "UPDATE Orders SET foodBill = "+total+", barBill = "+order.getBarBill()+" WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
			}
			if(!db.executeUpdate(sql, true))
				return false;
		return true;	
	}

	public Boolean removeSubOrder(String hotelId, String orderId, String subOrderId, String menuId, int qty) {
		String sql = null;
		if (qty==0) {
			sql = "DELETE FROM OrderItems WHERE orderId='"+orderId+"' AND subOrderId=='" + subOrderId + "' AND menuId='" + menuId + "' AND hotelId='"+hotelId+ "';";
		}
		else {
			sql = "UPDATE OrderItems SET qty=" + Integer.toString(qty) + " WHERE orderId='"+orderId+"' AND subOrderId=='" + subOrderId + "' AND menuId='" + menuId + "' AND hotelId='"+hotelId+ "';";
		}
		this.removeOrderedSpecification(hotelId, orderId, subOrderId, menuId, qty+1);
		return db.executeUpdate(sql, true);
	}
	
	public Boolean voidOrder(String hotelId, String orderId, String reason, String authId) {
		
		Order order = getOrderById(hotelId, orderId);
		
		String sql = "UPDATE Orders SET state="+ ORDER_STATE_VOIDED +", foodBill = 0, barBill = 0, " +
					"reason = '"+reason+"', authId = '"+authId+"' WHERE orderId='"+orderId+"' AND hotelId='"+hotelId+ "';";
		
		db.executeUpdate(sql, true);
		
		if(order.getState() == ORDER_STATE_BILLING || order.getState() == ORDER_STATE_OFFKDS || order.getState() == ORDER_STATE_SERVICE){
			
			sql = sql += "DELETE FROM OrderTables WHERE orderId='" + orderId+ "' AND hotelId='"+hotelId+"';";
			db.executeUpdate(sql, true);
			
			String billNo = this.updateBillNoInOrders(hotelId, orderId);
			sql = "INSERT INTO Payment " +
					"(hotelId, billNo, orderId, orderDate, foodBill, barBill, total, cardType) " +
					"VALUES('"+ escapeString(hotelId) + 
					"', '"+ escapeString(billNo) +
					"', '"+ escapeString(orderId) +  
					"', '"+ this.getServiceDate(hotelId) + 
					"', "+ Double.toString(order.getFoodBill()) + 
					", " + Double.toString(order.getBarBill()) + 
					", " + Double.toString(order.getFoodBill() + order.getBarBill()) + 
					", 'VOID');";
		}else{
			sql = "UPDATE Payment SET cashPayment = 0, cardPayment = 0 WHERE orderId = '"+orderId+"' AND hotelID = '"+hotelId+"';";
		}
		
		ArrayList<OrderItem> orderitems = this.getOrderedItems(hotelId, orderId);
		
		for (OrderItem orderItem : orderitems) {
			this.updateOrderItemLog(hotelId, orderId, orderItem.getSubOrderId(),  
					orderItem.getMenuId(), "Void", "void", orderItem.getQty(), orderItem.getRate(), 0);
			this.removeSubOrder(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0);
			ArrayList<OrderAddOn> addOns = this.getAllOrderedAddOns(hotelId, orderId);
			for (OrderAddOn orderAddOn : addOns) {
				this.updateOrderAddOnLog(hotelId, orderId, orderItem.getSubOrderId(), 
						orderItem.getMenuId(), orderAddOn.getItemId(), "void", orderAddOn.getQty(), orderAddOn.getRate(), orderAddOn.getAddOnId());
				this.removeAddOns(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0);
			}
		}
		return db.executeUpdate(sql, true);
	}
	
	public Boolean complimentaryOrder(String hotelId, String orderId, String authId) {
		
		String sql = "UPDATE Orders SET state="+ ORDER_STATE_COMPLIMENTARY +", foodBill = 0, barBill = 0, " +
					"authId = '"+authId+"' WHERE orderId='"+orderId+"' AND hotelId='"+hotelId+ "';";
		
		db.executeUpdate(sql, true);
		
		ArrayList<OrderItem> orderitems = this.getOrderedItems(hotelId, orderId);
		
		for (OrderItem orderItem : orderitems) {
			this.updateOrderItemLog(hotelId, orderId, orderItem.getSubOrderId(), 
					orderItem.getMenuId(), "Complimentary", "comp", orderItem.getQty(), 0, 0);
			this.removeSubOrder(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0);
			ArrayList<OrderAddOn> addOns = this.getAllOrderedAddOns(hotelId, orderId);
			for (OrderAddOn orderAddOn : addOns) {
				this.updateOrderAddOnLog(hotelId, orderId, orderItem.getSubOrderId(), 
						orderItem.getMenuId(), orderAddOn.getItemId(), "comp", orderAddOn.getQty(), orderAddOn.getRate(), orderAddOn.getAddOnId());
				this.removeAddOns(hotelId, orderId, orderItem.getSubOrderId(), orderItem.getMenuId(), 0);
			}
		}
		return db.executeUpdate(sql, true);
	}

	public void complimentaryItem(String hotelId, String orderId, String menuId, String authId, String subOrderId, int rate, int qty) {
	
		this.updateOrderItemLog(hotelId, orderId, subOrderId, menuId,
				"Complimentary", "comp", 1, rate, 0);
		this.removeSubOrder(hotelId, orderId, subOrderId, menuId, qty-1);
	}

	public void complimentaryAddOn(String hotelId, String orderId, int addOnId, String authId, String menuId, String subOrderId, 
			int itemId, int rate, int qty) {
		
		this.updateOrderAddOnLog(hotelId, orderId, subOrderId, 
				menuId, itemId, "comp", 1, rate, addOnId);
		this.removeAddOn(hotelId, orderId, subOrderId, menuId, qty-1, addOnId, itemId);
	}
	
	public JSONObject newSubOrder(String hotelId, String orderId, String menuId, Integer qty, String specs, String subOrderId, String waiterId, int choiceRate) {
		JSONObject outObj = new JSONObject();
		String sql = null;
		try {
			outObj.put("status", -1);
			outObj.put("message", "Unknown error!");
			Order order = getOrderById(hotelId, orderId);
			MenuItem menu = getMenuById(hotelId, menuId);
			String billNo = getCurrentBill(hotelId, orderId, menu.getStation().equals("Bar")|| menu.getStation().equals("BAR") ? "B" : "F", this.getHotelById(hotelId).getBillType());
			if(billNo.equals("")) {
				if(this.getHotelById(hotelId).getBillType()==2)
					billNo = this.getNextBillNoNumberFormat(hotelId, menu.getStation());
				else
					billNo = this.getNextBillNo(hotelId, menu.getStation());
			}
			String orderState = Integer.toString(SUBORDER_STATE_PENDING);
			String kdsType = getHotelById(hotelId).getKDSType();
			int kotPrinting = 1;
			if(kdsType.equals("NONKDS")){
				orderState = Integer.toString(SUBORDER_STATE_COMPLETE);
				kotPrinting = 0;
			}
					
			double rate = 0;
			if(order.getInHouse()==1 || order.getInHouse()==3)
				rate = menu.getInhouseRate();
			else
				rate = menu.getRate();
			String[] flags = menu.flags.split(";");
			for (String flag : flags) {
				if(flag.equals("ci"))
					rate = choiceRate==0?rate:choiceRate;
			}
			
			sql = "INSERT INTO OrderItems (hotelId, subOrderId, subOrderDate, orderId, menuId, qty, rate, specs, state, billNo, isKotPrinted, waiterId) values ('" + 
					hotelId + "', '" + 
					subOrderId + "', '" + 
					(new SimpleDateFormat("yyyy/MM/dd HH:mm")).format(new Date()) + "','" +
					orderId+ "', '" +
					menuId + "', " +
					Integer.toString(qty) + ", " +
					(new DecimalFormat("0.00")).format(rate) + ", '" +
					specs + "', " +
					orderState+ ", '" + 
					billNo +"',"+kotPrinting+", '"+waiterId+"');";
			if (!db.executeUpdate(sql, true)) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to add a suborder");
				return outObj;
			}
			updateFoodBill(hotelId, orderId, menuId, qty, false);
			if(subOrderId.equals("1"))
				this.updateBillNoInOrders(hotelId, orderId);

			if(kdsType.equals("NONKDS")){
				this.manageStock(hotelId, menuId, subOrderId, orderId);
			}
			outObj.put("status", 0);
			outObj.put("subOrderId", subOrderId);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return outObj;
	}
	
	public String getCurrentBill(String hotelId, String orderId, String type, int billType){
		String sql = "SELECT billNo FROM orderitems WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"' AND billNo LIKE '"+type+"%';";
		if(billType == 2)
			sql =  "SELECT billNo FROM orderitems WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
		
		BillNoFeild billNoFeild = db.getOneRecord(sql, BillNoFeild.class, hotelId);
		
		if(billNoFeild == null)
			return "";
		return billNoFeild.getBillNo();
	}
	
	public Integer getOrderCount(String hotelId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT count(orderId) AS entityId FROM Orders WHERE waiterId=='"+userId+"' AND orderDate=='"+(new SimpleDateFormat("yyyy/MM/dd")).format(dt)+"' AND "
				+ "hotelId='"+hotelId+"';";
		EntityId entity=db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}
	
	public Boolean updateSpecifications(String hotelId, String orderId, String subOrderId, String menuId, String specs) {
		
		String sql = "UPDATE OrderItems SET specs='" + specs + "' WHERE subOrderId=='" + subOrderId + "' AND menuId='"+menuId+ "' AND orderId='" +orderId+"' AND hotelId='"+hotelId+"';";
		
		return db.executeUpdate(sql, true);
	}
	
	public Boolean changeOrderStatus(String hotelId, String orderId, String subOrderId, String menuId) {
		String sql = null;
		OrderItem item = getOrderStatus(hotelId, orderId, subOrderId, menuId);
		int curState = item.state;
		ArrayList<Table> tables = getJoinedTables(hotelId, orderId);
		String tableId = "";
		if (tables.size()>0) {
			tableId = tables.get(0).getTableId();
		}
		String userId = orderId.split(":")[0];
		Hotel hotel = this.getHotelById(hotelId);
		
		if(curState == SUBORDER_STATE_PENDING){
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_PROCESSING) + " WHERE subOrderId=='" + subOrderId + "' AND menuId='"+menuId+ "' AND orderId='" +orderId+"' AND hotelId='"+hotelId+"';";
		}else if(curState == SUBORDER_STATE_PROCESSING){
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_RECIEVED) + " WHERE subOrderId=='" + subOrderId + "' AND menuId='"+menuId+ "' AND orderId='" +orderId+"' AND hotelId='"+hotelId+"';";
			Boolean retval = db.executeUpdate(sql, true);
			if (!retval)
				return retval;
			if (allItemsProcessedOrReceived(hotelId, orderId)) {
				int notId = getNextNotificationId(userId, hotelId);
				String target = tableId.equals("")? "Home Delivery" : "Table " + tableId;
				if(hotel.getHotelType().equals("KDS")) {
					String msg = "Order of " + target + " is ready.";
					sql += "INSERT INTO Notification (notId, hotelId, orderId, msg) VALUES ("+ Integer.toString(notId)+", '"+hotelId+"','"+orderId+"', '"+msg+"')";
				}
				db.executeUpdate(sql, true);
			}
			else if (!tableId.equals("")){
				int notId = getNextNotificationId(userId, hotelId);
				if(hotel.getHotelType().equals("KDS")) {
					String msg = item.title + " of Table " + tableId + " is ready.";
					sql += "INSERT INTO Notification (notId, hotelId, orderId, msg) VALUES ("+ Integer.toString(notId)+", '"+hotelId+"','"+orderId+"', '"+msg+"')";
				}
				db.executeUpdate(sql, true);
			}
			this.manageStock(hotelId, menuId, subOrderId, orderId);
				
			return retval;
		}else if(curState == SUBORDER_STATE_RECIEVED){
			sql = "UPDATE OrderItems SET state=" + Integer.toString(SUBORDER_STATE_COMPLETE) + " WHERE subOrderId=='" + subOrderId + "' AND menuId='"+menuId+ "' AND orderId='"+orderId+"' AND hotelId='"+hotelId+"';";
		}
		else{
			return false;
		}
		return db.executeUpdate(sql, true);
	}

	private double getQuantityOfOrderedItem(String hotelId, String menuId, String subOrderId, String orderId){
		
		String sql = "SELECT qty AS entityId FROM OrderItems WHERE menuId = '"+menuId+ "' AND subOrderId = '"+subOrderId+"' AND orderId = '"+orderId+ "' AND hotelId = '"+hotelId+"';";
		
		EntityDouble entity = db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}

	private Boolean allItemsProcessedOrReceived(String hotelId, String orderId) {
		String sql = "SELECT * FROM OrderItems WHERE hotelId=='"+hotelId+"' AND orderId=='"+orderId+"' AND state<>" + Integer.toString(SUBORDER_STATE_RECIEVED) + " AND state <> " + Integer.toString(SUBORDER_STATE_COMPLETE);
		return !db.hasRecords(sql, hotelId);
	}
	
	public OrderItem getOrderStatus(String hotelId, String orderId, String subOrderId, String menuId) {
		String sql = "SELECT MenuItems.title as title,OrderItems.state FROM OrderItems,MenuItems WHERE MenuItems.menuId==OrderItems.menuId AND OrderItems.menuId='" + menuId + "' AND OrderItems.subOrderId='" + subOrderId + "' AND OrderItems.orderId='"+orderId+"' AND OrderItems.hotelId='"+hotelId+"';";
		return db.getOneRecord(sql, OrderItem.class, hotelId);
	}

	public ArrayList<OrderItem> getReturnedItems(String hotelId, String orderId) {
		
		String sql = "SELECT OrderItemLog.subOrderId AS subOrderId, "
				+ "OrderItemLog.subOrderDate AS subOrderDate, "
				+ "OrderItemLog.menuId AS menuId, "
				+ "OrderItemLog.quantity AS qty, "
				+ "OrderItemLog.itemId AS itemId, "
				+ "MenuItems.title AS title, "
				+ "OrderItemLog.rate AS rate, "
				+ "OrderItemLog.reason AS reason, "
				+ "OrderItemLog.state AS state FROM OrderItemLog, MenuItems WHERE OrderItemLog.orderId='" + orderId 
				+ "' AND OrderItemLog.menuId==MenuItems.menuId AND OrderItemLog.hotelId='"+hotelId+"';";
		return db.getRecords(sql, OrderItem.class, hotelId);
	}
	
	public ArrayList<OrderItem> getAllOrderedItems(String hotelId) {
		String sql = "SELECT OrderItems.subOrderId AS subOrderId, "
				+ "OrderItems.menuId AS menuId, "
				+ "OrderItems.qty AS qty, "
				+ "MenuItems.title AS title, "
				+ "MenuItems.rate AS rate, "
				+ "MenuItems.inhouseRate AS inhouseRate, "
				+ "OrderItems.state AS state " 
				+ "FROM OrderItems, MenuItems "
				+ "WHERE OrderItems.menuId==MenuItems.menuId AND hotelId='"+hotelId+"';";
		return db.getRecords(sql, OrderItem.class, hotelId);
	}
	
	public Order getOrderById(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='" + orderId + "' AND hotelId='"+hotelId+"';";
		
		return db.getOneRecord(sql, Order.class, hotelId);
	}
	
	public Table getTableById(String hotelId, String tableId) {
		String sql = "SELECT * FROM TABLES WHERE tableId = '"+tableId+"' AND hotelID = '"+hotelId+"';";
		
		return db.getOneRecord(sql, Table.class, hotelId);
	}
	
	public boolean checkServiceCharge(String hotelId, String orderId) {
		String sql = "SELECT serviceCharge AS entityId FROM Payment WHERE orderId='" + orderId + "' AND hotelId='"+hotelId+"';";
		boolean hasSC = db.hasRecords(sql, hotelId);
		double sc = 0.0;
		if(hasSC)
			sc = db.getOneRecord(sql, EntityDouble.class, hotelId).getId();
		if(sc > 0)
			return true;
		else
			return false;
	}
	
	private String getNextOrderId(String hotelId, String userId) {
		String sql = "SELECT MAX(CAST(SUBSTR(orderId,"
				+ Integer.toString(userId.length()+2)
				+ ") AS integer)) AS entityId FROM Orders WHERE orderId LIKE '" + userId+ ":%'  AND hotelId='"+hotelId+"'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return userId + ":" + (entity.getId() + 1);
		}
		return userId + ":0";
	}
	
	private int getOrderNumber(String hotelId, String orderId) {
		String sql = "SELECT Id AS entityId FROM Orders WHERE orderId = '" + orderId+ "' AND hotelId='"+hotelId+"'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		return entity.getId();
	}
	
	public String getNextBillNo(String hotelId, String station){

		StringBuilder billNo = new StringBuilder();
		if(station.equals("BAR") || station.equals("Bar"))
			billNo.append("B");
		else
			billNo.append("F");
		
		String sql ="SELECT MAX(CAST(REPLACE(billNo, '"+billNo.toString()+"', '') AS integer)) AS entityId "+
					"FROM OrderItems WHERE hotelId='"+hotelId+"'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		
		int billNum1 = entity.getId();

		sql ="SELECT MAX(CAST(REPLACE(billNo, '"+billNo.toString()+"', '') AS integer)) AS entityId "+
					"FROM Orders WHERE hotelId='"+hotelId+"'";
		entity = db.getOneRecord(sql, EntityId.class, hotelId);

		int billNum2 = entity.getId();
		
		if(billNum1 > billNum2)
			billNo.append(billNum1 + 1);
		else
			billNo.append(billNum2 + 1);
		
		return billNo.toString();
	}
	
	public String getNextBillNoNumberFormat(String hotelId, String station){

		StringBuilder billNo = new StringBuilder();
		
		String sql ="SELECT MAX(CAST(billNo AS integer)) AS entityId "+
					"FROM OrderItems WHERE hotelId='"+hotelId+"'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		
		int billNum1 = entity.getId();

		sql ="SELECT MAX(CAST(billNo AS integer)) AS entityId "+
					"FROM Orders WHERE hotelId='"+hotelId+"'";
		entity = db.getOneRecord(sql, EntityId.class, hotelId);

		int billNum2 = entity.getId();
		
		if(billNum1 > billNum2)
			billNo.append(billNum1 + 1);
		else
			billNo.append(billNum2 + 1);
		
		return billNo.toString();
	}
	
	public String getNextSubOrderId(String hotelId, String orderId) {
		String sql = "SELECT MAX(CAST(subOrderId AS integer)) AS entityId FROM OrderItems WHERE orderId == '" + orderId+ "' AND hotelId='"+hotelId+"'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return Integer.toString(entity.getId() + 1);
		}
		return "0";
	}
	public int getTotalBillAmount(String hotelId, String orderId){
		String sql = "SELECT SUM(rate*qty) AS entityId FROM OrderItems WHERE hotelId='" + hotelId +"' AND orderId='" + orderId +"'";
		EntityId entity=db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}
	public ArrayList<HomeDelivery> getActiveHomeDeliveries(String hotelId, String userId) {

		Hotel hotel = this.getHotelById(hotelId);
		String sql = "SELECT Orders.state, Orders.billNo, Orders.customerName as customer, Orders.customerNumber as mobileNo, Orders.orderId FROM Orders WHERE inhouse="+HOME_DELIVERY+" AND Orders.state=";
		
		if(hotel.getHotelType().equals("PREPAID") && hotel.getKDSType().equals("NONKDS"))
			sql += Integer.toString(ORDER_STATE_BILLING);
		else
			sql += Integer.toString(ORDER_STATE_SERVICE);
			
		sql += " AND hotelId='"+hotelId+"' GROUP BY Orders.orderId";
		return db.getRecords(sql, HomeDelivery.class, hotelId);
	}
	
	public ArrayList<HomeDelivery> getActiveTakeAway(String hotelId, String userId) {

		Hotel hotel = this.getHotelById(hotelId);
		String sql = "SELECT Orders.state, Orders.billNo, Orders.customerName as customer, Orders.customerNumber as mobileNo, Orders.orderId FROM Orders WHERE inhouse="+TAKE_AWAY+" AND Orders.state=";
		
		if(hotel.getHotelType().equals("PREPAID") && hotel.getKDSType().equals("NONKDS"))
			sql += Integer.toString(ORDER_STATE_BILLING);
		else
			sql += Integer.toString(ORDER_STATE_SERVICE);
			
		sql += " AND hotelId='"+hotelId+"' GROUP BY Orders.orderId";
		return db.getRecords(sql, HomeDelivery.class, hotelId);
	}
	
	public ArrayList<HomeDelivery> getActiveBarOrders(String hotelId, String userId) {

		Hotel hotel = this.getHotelById(hotelId);
		String sql = "SELECT Orders.state AS state, Orders.customerName as customer, Orders.customerNumber as mobileNo, Orders.customerAddress as address, Orders.orderId FROM Orders WHERE inhouse="+BAR+" AND Orders.state="+Integer.toString(ORDER_STATE_SERVICE) + " AND hotelId='"+hotelId+"' GROUP BY Orders.orderId";
		
		if(hotel.getHotelType().equals("PREPAID") && hotel.getKDSType().equals("NONKDS"))
			 sql = "SELECT Orders.state AS state, Orders.customerName as customer, Orders.customerNumber as mobileNo, Orders.customerAddress as address, Orders.orderId FROM Orders WHERE inhouse="+BAR+" AND Orders.state="+Integer.toString(ORDER_STATE_BILLING) + " AND hotelId='"+hotelId+"' GROUP BY Orders.orderId";
		
		return db.getRecords(sql, HomeDelivery.class, hotelId);
	}
	
	public Integer getHomeDeliveryOrderState(String hotelId, String orderId) {
		Boolean hasQueued=false;
		Boolean hasProcessing=false;
		Boolean hasReceived=false;
		Boolean hasCompleted=false;
		
		String sql = "SELECT * FROM Orders, OrderItems WHERE Orders.orderId==OrderItems.orderId AND Orders.orderId='"+orderId+"' AND Orders.inhouse!=1 AND OrderItems.state=0  AND OrderItems.hotelId='"+hotelId+"'";
		if (db.hasRecords(sql, hotelId)) {
			hasQueued=true;
		}
		sql = "SELECT * FROM Orders, OrderItems WHERE Orders.orderId==OrderItems.orderId AND Orders.orderId='"+orderId+"' AND Orders.inhouse!=1 AND OrderItems.state=2 AND OrderItems.hotelId='"+hotelId+"'";
		if (db.hasRecords(sql, hotelId)) {
			hasProcessing=true;
		}
		sql = "SELECT * FROM Orders, OrderItems WHERE Orders.orderId==OrderItems.orderId AND Orders.orderId='"+orderId+"' AND Orders.inhouse!=1 AND OrderItems.state=3 AND OrderItems.hotelId='"+hotelId+"'";
		if (db.hasRecords(sql, hotelId)) {
			hasReceived=true;
		}
		sql = "SELECT * FROM Orders, OrderItems WHERE Orders.orderId==OrderItems.orderId AND Orders.orderId='"+orderId+"' AND Orders.inhouse!=1 AND OrderItems.state=1 AND OrderItems.hotelId='"+hotelId+"'";
		if (db.hasRecords(sql, hotelId)) {
			hasCompleted=true;
		}
		if (!hasQueued && !hasProcessing && !hasReceived && hasCompleted) {
			return 1; //Queued
		}
		if (!hasCompleted && !hasProcessing && !hasReceived) {
			return 0; //Sent
		}
		if (!hasQueued && !hasProcessing && hasReceived) {
			return 3;
		}
		return 2;
	}
	
	public Double getOrderTotal(String hotelId, String orderId) {
		String sql = "SELECT TOTAL(OrderItems.qty*OrderItems.rate) FROM OrderItems WHERE OrderItems.orderId='"+orderId+"' AND OrderItems.hotelId='"+hotelId+"'";
		AmountField amount = db.getOneRecord(sql, AmountField.class, hotelId);
		if (amount==null) {
			return 0.0;
		}
		return amount.getAmount();
	}
	
	public Boolean isHomeDeliveryOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='"+orderId+"' AND inhouse==0 AND hotelId='"+hotelId+"'";
		return db.hasRecords(sql, hotelId);
	}
	
	public Boolean isTakeAwayOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='"+orderId+"' AND inhouse==2 AND hotelId='"+hotelId+"'";
		return db.hasRecords(sql, hotelId);
	}
	public Boolean isBarOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM Orders WHERE orderId='"+orderId+"' AND inhouse==3 AND hotelId='"+hotelId+"'";
		return db.hasRecords(sql, hotelId);
	}
	public Boolean cancelOrder(String hotelId, String orderId) {
		String sql = null;
		sql = "SELECT * FROM OrderItems WHERE orderId=='" + orderId + "' AND state!="+Integer.toString(SUBORDER_STATE_PENDING) + " AND hotelId='"+hotelId+"'";
		
		if (db.hasRecords(sql, hotelId)) {
			return false;
		}
		sql = "DELETE FROM OrderItems WHERE orderId=='" + orderId + "' AND hotelId='"+hotelId+"'; "
				+ "DELETE FROM Orders WHERE orderId=='" + orderId + "' AND hotelId='"+hotelId+"'; "
				+ "DELETE FROM OrderTables WHERE orderId=='" + orderId + "' AND hotelId='"+hotelId+"'; "
				+ "DELETE FROM OrderSpecifications WHERE orderId=='" + orderId + "' AND hotelId='"+hotelId+"'; "
				+ "DELETE FROM OrderAddons WHERE orderId=='" + orderId + "' AND hotelId='"+hotelId+"'; ";
		return db.executeUpdate(sql, true);
	}
	public MonthReport getMaxOrderedItem(String hotelId, String duration) {
		
		String sql = "SELECT OrderItems.menuId as itemId, SUBSTR(subOrderDate, 1, "+duration.length()+") AS duration, MenuItems.title AS title, SUM(qty) AS orderCount, img FROM OrderItems, MenuItems "
					+	"WHERE duration = '"+ escapeString(duration)
					+	"' AND MenuItems.menuId = OrderItems.menuId "
					+	" AND MenuItems.category != 'Roti' "
					+	"GROUP BY itemId "
					+	"ORDER BY orderCount desc "
					+	"LIMIT 1;";
		return db.getOneRecord(sql, MonthReport.class, hotelId);
	}

	public boolean updateOrderItemLog(String hotelId, String orderId, String subOrderId, String menuId, 
			String reason, String type, int quantity, int rate, int itemId){
		
		int state = SUBORDER_STATE_RETURNED;
		if(type == "void")
			state = SUBORDER_STATE_VOIDED;
		else if(type =="comp")
			state = SUBORDER_STATE_COMPLIMENTARY;
		else if(type =="cancel")
			state = SUBORDER_STATE_CANCELED;
		
		OrderItem orderedItem = this.getOrderedItem(hotelId, orderId, subOrderId, menuId);
		
		String sql = "INSERT INTO OrderItemLog " +
				"(hotelId, orderId, subOrderId, subOrderDate, menuId, state, reason, dateTime, quantity, rate, itemId) " + 
				"VALUES('" + escapeString(hotelId) +
				"', '" + escapeString(orderId) +
				"', '" + escapeString(subOrderId) +
				"', '" + escapeString(orderedItem.getSubOrderDate()) +
				"', '" + escapeString(menuId) +
				"', " + state + 
				", '" + reason + 
				"', '" + new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + 
				"', " + quantity +
				", " + rate +
				", " + itemId +
				");";
		return db.executeUpdate(sql, true);
	}

	public boolean updateOrderAddOnLog(String hotelId, String orderId, String subOrderId, String menuId, 
			int itemId, String type, int quantity, int rate, int addOnId){
		
		int state = SUBORDER_STATE_RETURNED;
		if(type == "void")
			state = SUBORDER_STATE_VOIDED;
		else if(type == "comp")
			state = SUBORDER_STATE_COMPLIMENTARY;
		else if(type =="comp")
			state = SUBORDER_STATE_CANCELED;
		
		String sql = "INSERT INTO OrderAddOnLog " +
				"(hotelId, orderId, subOrderId, menuId, state, itemId, quantity, rate, addOnId) " + 
				"VALUES('" + escapeString(hotelId) +
				"', '" + escapeString(orderId) +
				"', " + escapeString(subOrderId) +
				", '" + escapeString(menuId) +
				"', '" + state + 
				"', " + itemId + 
				", " + quantity +
				", " + rate +
				", " + addOnId +
				");";
		return db.executeUpdate(sql, true);
	}
	
	public boolean updateOrderPrintCount(String hotelId, String orderId){
		
		String sql = "SELECT printCount AS entityId FROM Orders WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";

		int printCount = getOrderPrintCount(hotelId, orderId) + 1;
		
		sql = "UPDATE Orders SET " +
				"printCount = " + printCount + " WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
		return db.executeUpdate(sql, true);
	}
	
	public boolean updateOrderSMSStatus(String hotelId, String orderId){
		String sql = "UPDATE Orders SET " +
				"isSmsSent = 0, completeTimestamp = '"+ this.parseTime("HH:mm") +"' WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
		return db.executeUpdate(sql, true);
	}
	
	public boolean updateOrderSMSStatusDone(String hotelId, String orderId){
		String sql = "UPDATE Orders SET " +
				"isSmsSent = 1 WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
		return db.executeUpdate(sql, true);
	}
	
	public int getOrderPrintCount(String hotelId, String orderId){
		
		String sql = "SELECT printCount AS entityId FROM Orders WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";

		return db.getOneRecord(sql, EntityId.class, hotelId).getId();
	}
	public boolean updateKOTStatus(String hotelId, String orderId) {
		
		String sql = "UPDATE OrderItems SET isKotPrinted = 1"
				+ " WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId + "';";
		return db.executeUpdate(sql, true);
	}
	//---------------------------------------Table

	public ArrayList<TableUsage> getTableUsage(String hotelId, String userId) {
		String sql = "SELECT * FROM Tables WHERE hotelId='"+escapeString(hotelId)+"';";
		ArrayList<TableUsage> tables = db.getRecords(sql, TableUsage.class, hotelId);
		for (int i = 0;i<tables.size(); i++) {
			sql = "SELECT OrderTables.tableId AS tableId, OrderTables.orderId AS orderId, Orders.waiterId AS userId, Orders.state AS state FROM OrderTables, Orders WHERE "
					+ "OrderTables.orderId==Orders.orderId AND OrderTables.tableId=='"+tables.get(i).getTableId()+"' AND "
					+ "OrderTables.hotelId='"+escapeString(hotelId)+"';";
			TableUsage table = db.getOneRecord(sql, TableUsage.class, hotelId);
			if (table!=null) {
				tables.get(i).mOrderId = table.getOrderId();
				tables.get(i).mUserId = table.getUserId();
				tables.get(i).state = table.getState();
			}
		}
		return tables;
	}

	public ArrayList<TableUsage> getTables(String hotelId) {
		String sql = "SELECT * FROM Tables WHERE hotelId='"+escapeString(hotelId)+"';";
		return db.getRecords(sql, TableUsage.class, hotelId);
	}
	
	public boolean isTableOrder(String hotelId, String orderId) {
		String sql = "SELECT * FROM OrderTables WHERE orderId=='" + orderId + "' AND hotelId=='"+hotelId+"';";
		return db.hasRecords(sql, hotelId);
	}
	
	public String getOrderIdFromTables(String hotelId, String tableId) {
		String sql = "SELECT orderId FROM OrderTables WHERE tableId=='"+tableId+"' AND "
				+ " hotelId='"+hotelId+"';";
		String orderId = db.getOneRecord(sql, TableUsage.class, hotelId).getOrderId();
		
		if(orderId == null)
			return null;
		else
			return orderId;
	}
	public ArrayList<Table> getJoinedTables(String hotelId, String orderId) {
		String sql = "SELECT * FROM OrderTables WHERE orderId == '" + orderId+ "' AND hotelId='"+hotelId+"'";
		return db.getRecords(sql, Table.class, hotelId);
	}
	
	public boolean transferTable(String hotelId, String oldTableId, String newTableId, String orderId) {
		String sql = "UPDATE OrderTables " +
				"SET tableId='"+newTableId+"' WHERE hotelId='"+hotelId+"' AND tableId='"+oldTableId+"';";
		return db.executeUpdate(sql, true);
	}
	
	public boolean moveItem(String hotelId, int oldTableNumber, int newTableNumber, JSONArray orderItemIds) {
		
		String sql = "SELECT orderId FROM OrderTables WHERE tableId = '"+oldTableNumber+ "' AND hotelId='"+hotelId+"';";
		
		Table oldTable = db.getOneRecord(sql, Table.class, hotelId);
		
		sql = "SELECT orderId FROM OrderTables WHERE tableId = '"+newTableNumber+ "' AND hotelId='"+hotelId+"';";
		
		Table newTable = db.getOneRecord(sql, Table.class, hotelId);
		
		for (int i=0; i<orderItemIds.length(); i++) {
			try {
				JSONObject orderItemId = orderItemIds.getJSONObject(i);
				sql = "UPDATE OrderItems SET orderId = '"+newTable.getOrderId()+"' WHERE Id = " + orderItemId.getInt("id");
				if(!db.executeUpdate(sql, true))
					return false;
				sql = "SELECT * FROM OrderItems WHERE Id = " +orderItemId.getInt("id");
				OrderItem item = db.getOneRecord(sql, OrderItem.class, hotelId);
				this.updateFoodBill(hotelId, newTable.getOrderId(), item.getMenuId(), item.getQty(), false);
				this.updateFoodBill(hotelId, oldTable.getOrderId(), item.getMenuId(), item.getQty(), true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean switchTable(String hotelId, String oldTableNumber, JSONArray newTableNumbers){
		
		String sql = "SELECT orderId FROM OrderTables WHERE tableId = '"+oldTableNumber+ "' AND hotelId='"+hotelId+"';";
		
		Table table = db.getOneRecord(sql, Table.class, hotelId);
		
		sql = "DELETE FROM OrderTables WHERE orderId = '"+table.getOrderId()+"' AND hotelId = '"+hotelId+"';";
		db.executeUpdate(sql, true);
		String tableId = "";
		int currentTableId = 0;
		ArrayList<Integer> tables = new ArrayList<Integer>();
		
		for(int i=0; i<newTableNumbers.length(); i++) {
			try {
				currentTableId = newTableNumbers.getJSONObject(i).getInt("tableId");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(!tables.contains(currentTableId))
				tables.add(currentTableId);
		}
		
			
		for(int i=0; i<tables.size(); i++) {
			currentTableId = tables.get(i);
			sql = "INSERT INTO OrderTables (hotelId, tableId, orderId) values('" + hotelId+ "','" +currentTableId+ "','" + table.getOrderId() + "');";
			db.executeUpdate(sql, true);
			tableId += currentTableId;
			if(i<tables.size()-1)
				tableId += ", ";
		}
		sql = "UPDATE Orders SET tableId = '"+tableId+"' WHERE orderId = '"+table.getOrderId()+"' AND hotelId='"+hotelId+"';";
		
		return db.executeUpdate(sql, true);
	}
	
	public boolean switchFromBarToTable(String hotelId, String orderId, JSONArray newTableNumbers){

		String tableId = "";
		int currentTableId = 0;
		ArrayList<Integer> tables = new ArrayList<Integer>();
		for(int i=0; i<newTableNumbers.length(); i++) {
			try {
				currentTableId = newTableNumbers.getJSONObject(i).getInt("tableId");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(!tables.contains(currentTableId))
				tables.add(currentTableId);
		}
		
		String sql = "";
			
		for(int i=0; i<tables.size(); i++) {
			currentTableId = tables.get(i);
			sql = "INSERT INTO OrderTables (hotelId, tableId, orderId) values('" + hotelId+ "','" +currentTableId+ "','" + orderId + "');";
			db.executeUpdate(sql, true);
			tableId += currentTableId;
			if(i<tables.size()-1)
				tableId += ", ";
		}
		sql = "UPDATE Orders SET tableId = '"+tableId+"', inHouse = "+INHOUSE+" WHERE orderId = '"+orderId+"' AND hotelId='"+hotelId+"';";
		
		return db.executeUpdate(sql, true);
	}

	//----------------------------------------Service

	public String getServiceDate(String hotelId) {
		String sql = "SELECT * FROM ServiceLog WHERE isCurrent=0 AND "
				+ "hotelId='"+escapeString(hotelId)+"';";
		ServiceLog service = db.getOneRecord(sql, ServiceLog.class, hotelId);
		if (service==null) {
			return null;
		}
		return service.getServiceDate();
	}
	
	public String getServiceType(String hotelId) {
		String sql = "SELECT * FROM ServiceLog WHERE isCurrent=0 AND "
				+ "hotelId='"+escapeString(hotelId)+"';";
		ServiceLog service = db.getOneRecord(sql, ServiceLog.class, hotelId);
		if (service==null) {
			return null;
		}
		return service.getServiceType();
	}
	
	public boolean addService(String hotelId, String serviceType, String serviceDate, int cashInHand){
		
		String sql = "INSERT INTO ServiceLog " +
				"(hotelId, serviceDate, startTimeStamp, endTimeStamp, serviceType, isCurrent, cashInHand) " + 
				"VALUES('" + escapeString(hotelId) +
				"', '" + serviceDate +
				"', '" + new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) +
				"', '" +  
				"', '" + escapeString(serviceType) +
				"', " + 0 +
				", " + cashInHand +
				");";
		return db.executeUpdate(sql, true);
	}
	
	public boolean endService(String hotelId, String serviceDate){
		
		String sql = "UPDATE ServiceLog SET " +
				"endTimeStamp ='" + new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) +
				"', isCurrent = 1  WHERE hotelId = '" + hotelId +
				"' AND serviceDate = '" + serviceDate + "';";
		return db.executeUpdate(sql, true);
	}
	
	public ServiceLog getServiceLog(String hotelId, String serviceDate){
		
		String sql = "SELECT * FROM ServiceLog WHERE hotelId = '"+hotelId+"' AND serviceDate = '"+serviceDate+"';";
		
		return db.getOneRecord(sql, ServiceLog.class, hotelId);
	}
	public int getCashInHand(String hotelId){
		
		String sql = "SELECT cashInHand as entityId FROM ServiceLog WHERE hotelId = '"+hotelId+"' AND isCurrent = 0;";
		
		return db.getOneRecord(sql, EntityId.class, hotelId).getId();
	}
	
	public ServiceLog getCurrentService(String hotelId){
		
		String sql = "SELECT * FROM ServiceLog WHERE hotelId = '"+hotelId+"' AND isCurrent = 0;";
		
		return db.getOneRecord(sql, ServiceLog.class, hotelId);
	}

	//---------------------------------------Customer
	public Boolean addCustomer(String hotelId, String customer, String phone, String address, String birthdate, String anniversary,
			String allergyInfo, Boolean wantsPromotion) {
		String sql = "INSERT INTO Customers (hotelId, customer,address,mobileNo, birthdate, anniversary, allergyInfo, points, wantsPromotion) VALUES ('"
				+escapeString(hotelId)+"', '"+escapeString(customer)+"', '"+escapeString(address)+"', '"+escapeString(phone)+"', '"
				+escapeString(birthdate)+"', '"+escapeString(anniversary)+"', '"+escapeString(allergyInfo)+"', 0, '"+wantsPromotion+"')";
		return db.executeUpdate(sql, true);
 	}
 	
 	public Boolean modifyCustomer(String hotelId, String customerName, String phone,String birthdate, String anniversary, String allergyInfo,
 			String address, Boolean wantsPromotion) {
 		
 		allergyInfo = allergyInfo.equals("")?"":"', allergyInfo='"+escapeString(allergyInfo);
 		birthdate = birthdate.equals("")?"":"', birthdate='"+escapeString(birthdate);
 		anniversary = anniversary.equals("")?"":"', anniversary='"+escapeString(anniversary);
 		address = address.equals("")?"":"', address='"+escapeString(address);
		
 		String sql = "UPDATE Customers SET customer='"+escapeString(customerName)+ birthdate
				+ anniversary + allergyInfo + address+"', wantsPromotion = '"+wantsPromotion+"' WHERE mobileNo='"+escapeString(phone)+"' AND "
				+ "hotelId='"+escapeString(hotelId)+"'";
		return db.executeUpdate(sql, true);
 	}
 	
 	public Boolean hasCustomer(String hotelId, String phone) {
		String sql = "SELECT * FROM customers WHERE mobileNo='" + phone + "' AND "
				+ "hotelId='"+escapeString(hotelId)+"';";
		return db.hasRecords(sql, hotelId);
 	}

	public Boolean editCustomerDetails(String hotelId, String orderId, String name, String number, String address, int noOfGuests,
			String allergyInfo) {
		String sql = "UPDATE Orders SET customerName ='" +escapeString(name)+ "', numberOfGuests = "+ Integer.toString(noOfGuests)+
				", customerNumber = '" + escapeString(number) + "', customerAddress = '" + escapeString(address) + "' WHERE hotelId = '" +escapeString(hotelId)+ "' AND orderId = '" 
				+escapeString(orderId)+ "';";
		
		boolean hasUpdated = db.executeUpdate(sql, true);
		if(hasUpdated){
			Customer customer  = this.getCustomerDetails(hotelId, number);
			if(customer == null) {
				this.addCustomer(hotelId, name, number, address, "", "", allergyInfo, false);
			}else {
				this.modifyCustomer(hotelId, name, number, "", "", allergyInfo, address, customer.getWantsPromotion()==null?false:customer.getWantsPromotion());
			}
			return true;
		}
		return false;
	}

	public ArrayList<Customer> getCustomersForSMS(String hotelId) {
		String sql = "SELECT distinct Customers.mobileNo, Customers.customer, Orders.completeTimestamp, Orders.orderId"
				+ " FROM Customers, Orders"
				+ " WHERE Orders.hotelId='"+hotelId
				+ "' AND Orders.isSmsSent = 0 AND Orders.customerNumber == Customers.mobileNo;";
		return db.getRecords(sql, Customer.class, hotelId);
	} 
	
	public Customer getCustomerDetails(String hotelId, String mobileNo) {
		String sql = "SELECT * FROM Customers WHERE mobileNo='"+mobileNo+"' AND hotelId='"+hotelId+"'";
		return db.getOneRecord(sql, Customer.class, hotelId);
	}

	public EntityString getMobileNoFromOrderId(String hotelId, String orderId) {
		String sql = "SELECT customerNumber AS entityId FROM Orders WHERE orderId='"+orderId+"' AND hotelId='"+hotelId+"'";
		return db.getOneRecord(sql, EntityString.class, hotelId);
	}

	public ArrayList<Customer> getAllCustomerDetails(String hotelId) {
		String sql = "SELECT * FROM Customers WHERE hotelId='"+hotelId+"'";
		return db.getRecords(sql, Customer.class, hotelId);
	}

	public ArrayList<Order> getOrdersOfOneCustomer(String hotelId, String mobileNo) {
		System.out.println("M " + mobileNo + " hid: " + hotelId);
		String sql = "SELECT * FROM Orders WHERE customerNumber = '"+mobileNo+"' AND hotelId = '"+ hotelId + "';";
		return db.getRecords(sql, Order.class, hotelId);
	}

	public boolean updateCustomer (String name, String address, String mobileNo, String birthday, String anni, 
			String remarks, String allergy, String hotelId) {
		String sql = "UPDATE CUSTOMERS SET customer = '" + escapeString(name) + "'," + 
				"address = '" + escapeString(address) + "' ," + 
				"birthdate = '" + escapeString(birthday) + "', "+
				"anniversary = '" + escapeString(anni) + "' ,"+
				"remarks = '" + escapeString(remarks) + "' "+
				"alleryInfo = '" + escapeString(allergy) + "' "+
				"where mobileNo = '" + escapeString(mobileNo) + "' " + 
				"AND hotelId = '"+escapeString(hotelId)+"';";
		System.out.println(sql);
		return db.executeUpdate(sql, true);
	}
	
	//------------------------------------------Payment

	public ArrayList<Report> getPaymentDetails(String hotelId, String sDate, String eDate){
		String sql = "SELECT * FROM Payment WHERE hotelId= '"+hotelId+"' AND orderDate BETWEEN '"+sDate+"' AND '"+eDate+"'";
		return db.getRecords(sql, Report.class, hotelId);
	}
	
	public Double getTotalCashIn(String hotelId, String serviceDate){
		String sql = "SELECT SUM(cashPayment) FROM Payment WHERE hotelId= '"+hotelId+"' AND orderDate = '"+serviceDate.replace("/", "-")+"';";
		
		EntityDouble entity =  db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}
	
	public boolean addPayment(String hotelId, String orderId, String orderDate, double foodBill, double barBill,
			double discount, double total, double sc, double gst, double tip, double cashPayment, double cardPayment, 
			String discountName, String cardType, double complimentary) {
		
		cardType = cardType.toUpperCase().replace(' ', '_');
		
		String sql = "SELECT * FROM Payment WHERE hotelId='" + hotelId + "' AND orderId='" + orderId + 
					"' AND orderDate='" + orderDate + "';";
		
		Report payment = db.getOneRecord(sql, Report.class, hotelId);
		
		if(payment != null)
			return false;
		
		Order order = getOrderById(hotelId, orderId);
		
		sql = "INSERT INTO Payment " +
				"(hotelId, billNo, orderId, orderDate, foodBill, barBill, discount, total, " +
				"serviceCharge, gst, tip, cashPayment, cardPayment, discountName, cardType, complimentary) " +
				"VALUES('"+ escapeString(hotelId) + 
				"', '"+ escapeString(order.getBillNo()) +
				"', '"+ escapeString(orderId) +  
				"', '"+ escapeString(orderDate) + 
				"', "+ Double.toString(foodBill) + 
				", " + Double.toString(barBill) + 
				", " + Double.toString(discount) + 
				", " + Double.toString(total) + 
				", " + Double.toString(sc) + 
				", " + Double.toString(gst) + 
				", " + Double.toString(tip) + 
				", " + Double.toString(cashPayment) + 
				", " + Double.toString(cardPayment) +
				", '" + escapeString(discountName) +
				"', '" + escapeString(cardType) +
				"', " + Double.toString(complimentary) + 
				");";

		return db.executeUpdate(sql, true);
	}
	
	public boolean editPayment(String hotelId, String orderId, double cashPayment, double cardPayment, String cardType) {
		
		String sql = "UPDATE Payment SET " +
				"cashPayment = " + Double.toString(cashPayment) + ", " +
				"cardPayment = " + Double.toString(cardPayment) + ", " +
				"cardType = '" + escapeString(cardType) + "' " +
				"WHERE orderId = '"+orderId+"' AND hotelID = '"+hotelId+"';";

		return db.executeUpdate(sql, true);
	}
	
	private String appendEndDate(String endDate) {
		return endDate + " 23:59";
	}
	public Boolean updatePaymentForReturn(String hotelId, String orderId, double foodBill, double barBill,
			double discount, double total, double serviceCharge, double gst,
			double VATBar, double cashPayment, double cardPayment) {
			
		String sql = "UPDATE Payment SET "
				+ "foodBill = "+ foodBill +", "
				+ "barBill = "+ barBill +", "
				+ "discount = "+discount +", "
				+ "total = "+total +", "
				+ "serviceCharge = "+serviceCharge +", "
				+ "gst = "+gst +", "
				+ "VATBar = "+VATBar +", "
				+ "cashPayment = "+cashPayment +", "
				+ "cardPayment = "+cardPayment
				+ " WHERE orderId = '"+orderId+"' AND hotelID = '"+hotelId+"';";
		
		return db.executeUpdate(sql, true);
	}
	public Report getPayment(String hotelId, String orderId){
		String sql = "SELECT * FROM PAYMENT WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
		
		return db.getOneRecord(sql, Report.class, hotelId);
	}

	//-----------------------------------------AddOn
	
	public Boolean addOrderAddon(String hotelId, String orderId, String menuId, int qty, int addOnId, String subOrderId, int itemId) {
		
		Order order = getOrderById(hotelId, orderId);
		AddOn addOn = getAddOnById(addOnId, hotelId);
		
		double rate = 0;
		if(order.getInHouse()==1 || order.getInHouse()==3)
			rate = addOn.getInHouseRate();
		else
			rate = addOn.getDeliveryRate();
		
		String sql = "INSERT INTO OrderAddOns (hotelId, subOrderId, addOnId, orderId, menuId, itemId, qty, rate) values ('" + 
				hotelId + "', '" + 
				subOrderId + "', " + 
				addOnId + ",'" +
				orderId+ "', '" +
				menuId + "', " +
				itemId + ", " +
				Integer.toString(qty) + ", " +
				(new DecimalFormat("0.00")).format(rate) + ");";
		if (!db.executeUpdate(sql, true)) {
			return false;
		}
		updateFoodBillAddOn(hotelId, orderId, subOrderId, menuId, itemId);
		
		return true;
	}
	public ArrayList<AddOn> getAddOns(String hotelId) {
		String sql = "SELECT * FROM AddOns  WHERE hotelId='"+hotelId+"'";
		return db.getRecords(sql, AddOn.class, hotelId);
	}
	
	public AddOn getAddOnById(int addOnId, String hotelId) {
		
		String sql = "SELECT * FROM AddOns WHERE Id = "+addOnId;
		
		return db.getOneRecord(sql, AddOn.class, hotelId);
	}
	
	public boolean addAddOn(String hotelId, String name, Double inHouseRate, Double deliveryRate) {
		
		String sql = "INSERT INTO AddOns " +
				"(name, inHouseRate, deliveryRate) " +
				"VALUES('"+ escapeString(name) + 
				"', "+ inHouseRate +
				", "+ deliveryRate+ 
				");";
		return db.executeUpdate(sql, true);
	}

	public OrderAddOn getOrderedAddOn(String hotelId, String orderId, String subOrderId, String menuId, int itemId,int addOnId) {
		
		String sql = "SELECT OrderAddOns.addOnId AS addOnId, "
				+ "OrderAddOns.menuId AS menuId, "
				+ "OrderAddOns.qty AS qty, "
				+ "OrderAddOns.itemId AS itemId, "
				+ "OrderAddOns.rate AS rate, "
				+ "OrderAddOns.subOrderId AS subOrderId, "
				+ "AddOns.name AS name "
				+ "FROM OrderAddOns, AddOns "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' "
				+ "AND OrderAddOns.menuId='" + menuId + "' "
				+ "AND OrderAddOns.subOrderId='" + subOrderId + "' "
				+ "AND OrderAddOns.itemId == "+ itemId + " "
				+ "AND OrderAddOns.addOnId == "+addOnId+ " "
				+ "AND OrderAddOns.hotelId='"+hotelId+"';";
		return db.getOneRecord(sql, OrderAddOn.class, hotelId);
	}
	public ArrayList<OrderAddOn> getOrderedAddOns(String hotelId, String orderId, String subOrderId, String menuId, int itemId) {
		
		String sql = "SELECT OrderAddOns.addOnId AS addOnId, "
				+ "OrderAddOns.menuId AS menuId, "
				+ "OrderAddOns.qty AS qty, "
				+ "OrderAddOns.itemId AS itemId, "
				+ "OrderAddOns.rate AS rate, "
				+ "OrderAddOns.subOrderId AS subOrderId, "
				+ "AddOns.name AS name "
				+ "FROM OrderAddOns, AddOns "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' "
				+ "AND OrderAddOns.menuId='" + menuId + "' "
				+ "AND OrderAddOns.subOrderId='" + subOrderId + "' "
				+ "AND OrderAddOns.itemId == "+ itemId + " "
				+ "AND OrderAddOns.addOnId == AddOns.id "
				+ "AND OrderAddOns.hotelId='"+hotelId+"';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}
	public ArrayList<OrderAddOn> getCanceledOrderedAddOns(String hotelId, String orderId, String subOrderId, String menuId, int itemId) {
		
		String sql = "SELECT OrderAddOnLog.addOnId AS addOnId, "
				+ "OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.quantity AS qty, "
				+ "OrderAddOnLog.itemId AS itemId, "
				+ "OrderAddOnLog.rate AS rate, "
				+ "OrderAddOnLog.subOrderId AS subOrderId, "
				+ "AddOns.name AS name "
				+ "FROM OrderAddOnLog, AddOns "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' "
				+ "AND OrderAddOnLog.menuId='" + menuId + "' "
				+ "AND OrderAddOnLog.subOrderId='" + subOrderId + "' "
				+ "AND OrderAddOnLog.itemId == "+ itemId + " "
				+ "AND OrderAddOnLog.addOnId == AddOns.id "
				+ "AND OrderAddOnLog.hotelId='"+hotelId+"';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}
	public ArrayList<OrderAddOn> getReturnedAddOns(String hotelId, String orderId, String subOrderId, String menuId, int itemId) {
		
		String sql = "SELECT OrderAddOnLog.addOnId AS addOnId, "
				+ "OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.quantity AS qty, "
				+ "OrderAddOnLog.rate AS rate, "
				+ "OrderAddOnLog.itemId AS itemId, "
				+ "OrderAddOnLog.subOrderId AS subOrderId, "
				+ "AddOns.name AS name "
				+ "FROM OrderAddOnLog, AddOns "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' "
				+ "AND OrderAddOnLog.menuId='" + menuId + "' "
				+ "AND OrderAddOnLog.subOrderId='" + subOrderId + "' "
				+ "AND OrderAddOnLog.itemId == "+ itemId + " "
				+ "AND OrderAddOnLog.addOnId == AddOns.id "
				+ "AND OrderAddOnLog.hotelId='"+hotelId+"';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}
	
	public ArrayList<OrderAddOn> getAllOrderedAddOns(String hotelId, String orderId) {
		
		String sql = "SELECT OrderAddOns.addOnId AS addOnId, "
				+ "OrderAddOns.menuId AS menuId, "
				+ "OrderAddOns.subOrderDate AS subOrderDate, "
				+ "OrderAddOns.state AS state, "
				+ "OrderAddOns.qty AS qty, "
				+ "OrderAddOns.rate AS rate, "
				+ "OrderAddOns.itemId AS itemId, "
				+ "OrderAddOns.subOrderId AS subOrderId, "
				+ "AddOns.name AS name "
				+ "FROM OrderAddOns, AddOns "
				+ "WHERE OrderAddOns.orderId='" + orderId + "' "
				+ "AND OrderAddOns.addOnId == AddOns.id "
				+ "AND OrderAddOns.hotelId='"+hotelId+"' "
				+ "UNION ALL "
				+ "SELECT OrderAddOnLog.addOnId AS addOnId, "
				+ "OrderAddOnLog.menuId AS menuId, "
				+ "OrderAddOnLog.subOrderDate AS subOrderDate, "
				+ "OrderAddOnLog.state AS state, "
				+ "OrderAddOnLog.quantity AS qty, "
				+ "OrderAddOnLog.rate AS rate, "
				+ "OrderAddOnLog.itemId AS itemId, "
				+ "OrderAddOnLog.subOrderId AS subOrderId, "
				+ "AddOns.name AS name "
				+ "FROM OrderAddOnLog, AddOns "
				+ "WHERE OrderAddOnLog.orderId='" + orderId + "' "
				+ "AND OrderAddOnLog.addOnId == AddOns.id "
				+ "AND OrderAddOnLog.state == " + SUBORDER_STATE_COMPLIMENTARY + " "
				+ "AND OrderAddOnLog.hotelId='"+hotelId+"';";
		return db.getRecords(sql, OrderAddOn.class, hotelId);
	}

	public Boolean removeAddOns(String hotelId, String orderId, String subOrderId, String menuId, int qty) {
		String sql = "DELETE FROM OrderAddOns WHERE orderId='"+orderId+"' AND subOrderId =" 
				+ subOrderId + " AND menuId='" + menuId + "' AND hotelId='"+hotelId + "' ";
		
		if(qty>0)
			sql += "AND itemId =" + (qty+1) + ";";
		
		return db.executeUpdate(sql, true);
	}

	public Boolean removeAddOn(String hotelId, String orderId, String subOrderId, String menuId, int qty, int addOnId, int itemId) {
		String sql = null;
		if (qty==0) {
			sql = "DELETE FROM OrderAddOns WHERE orderId='"+orderId+"' AND subOrderId =" 
					+ subOrderId + " AND menuId='" + menuId + "' AND hotelId='"+hotelId + "' AND addOnId="+addOnId
					+ " AND itemId =" +itemId + ";";
		}
		else {
			sql = "UPDATE OrderAddOns SET qty=" + Integer.toString(qty) + " WHERE orderId='"+orderId+"' AND subOrderId =" + 
					subOrderId + " AND menuId='" + menuId + "' AND hotelId='"+hotelId + "' AND addOnId="+addOnId + 
					" AND itemId =" +itemId + ";";
		}
		return db.executeUpdate(sql, true);
	}

	//------------------------------------------Specification

	public Boolean addOrderSpecification(String hotelId, String orderId, String subOrderId, String menuId, int itemId, String specification) {
		
		String sql = "INSERT INTO OrderSpecifications (hotelId, orderId, subOrderId, menuId, itemId, specification) values ('" + 
				hotelId + 		"', '" + 
				orderId + 		"', '" + 
				subOrderId + 	"', '" + 
				menuId + 		"', " +
				itemId+ 			", '" +
				escapeString(specification) + "');";
		return db.executeUpdate(sql, true);
	}
	public ArrayList<Specifications> getSpecifications(String hotelId) {
		String sql = "SELECT * FROM Specifications";
		return db.getRecords(sql, Specifications.class, hotelId);
	}
	
	public boolean addSpecification(String name) {
		
		String sql = "INSERT INTO Specifications " +
				"(specification) " +
				"VALUES('"+ escapeString(name) + 
				"');";
		return db.executeUpdate(sql, true);
	}

	public ArrayList<OrderSpecification> getOrderedSpecification(String hotelId, String orderId, String menuId, String subOrderId, int itemId) {
		
		String sql = "SELECT * FROM OrderSpecifications "
				+ "WHERE orderId='" + orderId + "' "
				+ "AND itemId == "+ itemId + " "
				+ "AND menuId='" + menuId + "' "
				+ "AND subOrderId='" + subOrderId + "';";
		return db.getRecords(sql, OrderSpecification.class, hotelId);
	}
	
	
	//----------------------------------------Stock

	private void manageStock(String hotelId, String menuId, String subOrderId, String orderId){
		
		double quantity = this.getQuantityOfOrderedItem(hotelId, menuId, subOrderId, orderId);
		double newQuantity = 0;
		
		ArrayList<Stock> recipeItems = this.getRecipe(hotelId, menuId);
		
		if(recipeItems == null){
			return;
		}
		
		for(int i=0; i<recipeItems.size(); i++){
			Stock stockItem = this.getStockItemBySku(hotelId, recipeItems.get(i).getSku());
			if(stockItem != null){
				newQuantity = stockItem.getQuantity() - (quantity*(recipeItems.get(i).getQuantity()+ (recipeItems.get(i).getQuantity()*recipeItems.get(i).getWastage()/100)));
				if(quantity > 0)
					this.updateStock(hotelId, recipeItems.get(i).getSku(), newQuantity, quantity);
			}
		}
		return;
	}

	private double getQuantity(String sku, String hotelId){
		
		String sql = "SELECT quantity AS entityId FROM Stock WHERE sku = '"+sku+ "' AND hotelId = '"+hotelId+ "';";
		
		EntityDouble entity = db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}

	public ArrayList<Stock> getStock(String hotelId) {
		String sql = "SELECT Material.sku AS sku, "
				+ "Material.name AS name, "
				+ "Material.unit AS unit, "
				+ "Material.displayableUnit AS displayableUnit, "
				+ "Material.ratePerUnit AS ratePerUnit, "
				+ "Stock.quantity AS quantity "
				+ "FROM Material, Stock "
				+ "WHERE Material.hotelId= '"+hotelId+"' "
				+ "AND Material.sku == Stock.sku "
				+ "ORDER BY name;";
		return db.getRecords(sql, Stock.class, hotelId);
	}

	public ArrayList<Stock> getExpiringStock(String hotelId) {
		
		ArrayList<Stock> stock = new ArrayList<Stock>();
		Date date  = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String exDate = dateFormat.format(cal.getTime());
		
		for(int i=0; i<=1; i++){
			String sql = "SELECT Stock.sku AS sku, "
					+ "Material.name AS name, "
					+ "Material.unit AS unit, "
					+ "Material.displayableUnit AS displayableUnit, "
					+ "Stock.quantity AS quantity, "
					+ "Stock.doc AS doc, "
					+ "Stock.doe AS doe "
					+ "FROM Material, Stock "
					+ "WHERE Material.hotelId= '"+hotelId+"' "
					+ "AND Material.sku == Stock.sku "
					+ "AND Stock.doe == '" + exDate +"';";
			stock.addAll(db.getRecords(sql, Stock.class, hotelId));
			
			cal.add(Calendar.DATE, 1);
			exDate = dateFormat.format(cal.getTime());
		}
		return stock;
	}
	
	public ArrayList<Stock> getStockRunningOut(String hotelId) {
		
		String sql = "SELECT Stock.sku AS sku, "
				+ "Material.name AS name, "
				+ "Material.displayableUnit AS displayableUnit, "
				+ "Stock.quantity AS quantity, "
				+ "Material.minQuantity AS minQuantity "
				+ "FROM Material, Stock "
				+ "WHERE Material.hotelId= '"+hotelId+"' "
				+ "AND Material.sku == Stock.sku "
				+ "AND quantity <= minQuantity ";
		return db.getRecords(sql, Stock.class, hotelId);
	}
	
	public boolean addStock(String hotelId, String sku, String doc, String doe, double quantity, double ratePerUnit ) {

		String sql = "INSERT INTO Stock " +
				"(hotelId, sku, doc, doe, quantity) " +
				"VALUES('"+ escapeString(hotelId) + 
				"', '"+ escapeString(sku) + 
				"', '"+ escapeString(doc) + 
				"', '"+ escapeString(doe) + 
				"', "+ Double.toString(quantity) + 
				");";

		if(db.executeUpdate(sql, true)){
			this.updateStockLog(hotelId, sku, quantity, ratePerUnit*quantity, "CREATE");
			return true;
		}
		else
			return false;
	}
	
	public Boolean updateStock(String hotelId, String sku, double newQuantity, double addedQuantity, double ratePerUnit, String doe) {
		
		String sql = "UPDATE Stock SET" +
				" quantity = '"+ Double.toString(newQuantity) + 
				"', doe = '"+ escapeString(doe) + 
				"' WHERE hotelId = '" + escapeString(hotelId) +
				"' AND sku = '" + escapeString(sku) +
				"';";

		if(db.executeUpdate(sql, true)){
			this.updateStockLog(hotelId, sku, addedQuantity, addedQuantity*ratePerUnit, "UPDATE");
			return true;
		}
		return false;
	}
	
	public Boolean updateStock(String hotelId, String sku, double newQuantity, double addedQuantity) {
		
		String sql = "UPDATE Stock SET" +
				" quantity = "+ Double.toString(newQuantity) + 
				" WHERE hotelId = '" + escapeString(hotelId) +
				"' AND sku = '" + escapeString(sku) +
				"';";

		if(db.executeUpdate(sql, true)){
			this.updateStockLog(hotelId, sku, addedQuantity, addedQuantity*this.getRatePerUnit(sku, hotelId), "USEDUP");
			return true;
		}
		return false;
	}

	public Stock getStockItemBySku(String hotelId, String sku) {
		String sql = "SELECT * FROM Stock WHERE sku='" + escapeString(sku) + "' AND hotelId='"+escapeString(hotelId)+"';";
		return db.getOneRecord(sql, Stock.class, hotelId);
	}
	
	public boolean deleteStockItem(String hotelId, String sku) {
		String sql = "DELETE FROM Stock WHERE sku = '" + sku + "' AND hotelId='"+hotelId+"';";
		
		double quantity = this.getQuantity(sku, hotelId);
		
		this.updateStockLog(hotelId, sku, quantity, quantity*this.getRatePerUnit(sku, hotelId), "DELETED");
		
		if(db.executeUpdate(sql, true)){
			sql = "DELETE FROM Material WHERE sku = '" + sku + "' AND hotelId='"+hotelId+"';";
			return db.executeUpdate(sql, true);
		}
		else
			return false;
	}

	public boolean updateStockLog(String hotelId, String sku, double quantity, double amount, String crud){
		
		String sql = "INSERT INTO StockLog " +
				"(hotelId, sku, crud, quantity, amount) " + 
				"VALUES('" + escapeString(hotelId) +
				"', '" + sku +
				"', '" + crud +
				"', " + Double.toString(quantity) +
				", " + Double.toString(amount) +
				");";
		return db.executeUpdate(sql, true);
	}

	//----------------------------------------Materials

	
	private double getRatePerUnit(String sku, String hotelId){
		
		String sql = "SELECT ratePerUnit AS entityId FROM Material WHERE sku = '"+sku+ "' AND hotelId = '"+hotelId+ "';";
		
		EntityDouble entity = db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}
	
	public ArrayList<Stock> getMaterial(String hotelId, int type) {
		String sql = "SELECT Material.sku AS sku, "
				+ "Material.name AS name, "
				+ "Material.unit AS unit, "
				+ "Material.displayableUnit AS displayableUnit, "
				+ "Material.ratePerUnit AS ratePerUnit, "
				+ "Material.minQuantity AS minQuantity, "
				+ "Stock.quantity AS quantity, "
				+ "Stock.doe AS doe "
				+ "FROM Material, Stock "
				+ "WHERE Material.hotelId= '"+hotelId+"' "
				+ "AND Material.sku == Stock.sku ";
		if(type == 0)
			sql += "ORDER BY quantity - minQuantity;";
		else
			sql += "ORDER BY Material.name;";
		return db.getRecords(sql, Stock.class, hotelId);
	}
	
	public ArrayList<Stock> getMaterialByName(String hotelId) {
		String sql = "SELECT Material.sku AS sku, "
				+ "Material.name AS name, "
				+ "Material.unit AS unit, "
				+ "Material.displayableUnit AS displayableUnit, "
				+ "Material.ratePerUnit AS ratePerUnit, "
				+ "Material.minQuantity AS minQuantity, "
				+ "Stock.quantity AS quantity, "
				+ "Stock.doe AS doe "
				+ "FROM Material, Stock "
				+ "WHERE Material.hotelId= '"+hotelId+"' "
				+ "AND Material.sku == Stock.sku "
				+ "ORDER BY name;";
		return db.getRecords(sql, Stock.class, hotelId);
	}
	public boolean addMaterial(String hotelId, String materialName, Double ratePerUnit, double minQuantity, double quantity, String doe, int wastage, 
			String unit, String displayableUnit) {

		String sku = getNextSKU(hotelId);
		
		String sql = "INSERT INTO Material " +
				"(hotelId, sku, name, ratePerUnit, minQuantity, wastage, unit, displayableUnit) " +
				"VALUES('"+ escapeString(hotelId) + 
				"', '"+ escapeString(sku) + 
				"', '"+ escapeString(materialName) + 
				"', "+ Double.toString(ratePerUnit) + 
				", "+ Double.toString(minQuantity) + 
				", "+ Integer.toString(wastage) + 
				", '"+ escapeString(unit) + 
				"', '"+ escapeString(displayableUnit) + 
				"');";

		if(db.executeUpdate(sql, true)){
			Date date  = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			String doc = dateFormat.format(cal.getTime()); 
			this.addStock(hotelId, sku, doc, doe, quantity, ratePerUnit);
			return true;
		}
		else
			return false;
	}
	public ArrayList<Stock> getMaterialBySearch(String hotelId, String query, int type) {
		
		query = escapeString(query);
		String sql = "SELECT Material.sku AS sku, "
				+ "Material.name AS name, "
				+ "Material.unit AS unit, "
				+ "Material.displayableUnit AS displayableUnit, "
				+ "Material.ratePerUnit AS ratePerUnit, "
				+ "Material.minQuantity AS minQuantity, "
				+ "Stock.quantity AS quantity "
				+ "FROM Material, Stock "
				+ "WHERE Material.hotelId= '"+hotelId+"' "
				+ "AND Material.sku == Stock.sku ";
		
		if(query.equals("")){
			sql += "";
		}else if(query.matches("\\D*")){
			query = "%" + query + "%";
			sql += "AND Material.name LIKE'" + query + "';";
		}else{
			sql += "AND Material.sku =='" + query + "';";
		}
		if(type == 0)
			sql += "ORDER BY quantity - minQuantity;";
		else if(type ==1)
			sql += "ORDER BY Materials.name;";
		return db.getRecords(sql, Stock.class, hotelId);
	}
	
	public Stock getOneMaterial(String hotelId, String sku) {
		
		String sql = "SELECT Material.name AS name, "
			+ "Stock.sku AS sku, "
			+ "Material.unit AS unit, "
			+ "Material.displayableUnit AS displayableUnit, "
			+ "Material.ratePerUnit AS ratePerUnit, "
			+ "Material.minQuantity AS minQuantity, "
			+ "Material.wastage AS wastage, "
			+ "Stock.quantity AS quantity, "
			+ "Stock.doe AS doe "
			+ "FROM Material, Stock "
			+ "WHERE Material.hotelId= '"+hotelId+"' "
			+ "AND Material.sku == Stock.sku "
			+ "AND Material.sku =='" + escapeString(sku) + "';";
		
		return db.getOneRecord(sql, Stock.class, hotelId);
	}
	public Boolean updateMaterial(String hotelId, String materialName, double ratePerUnit, double minQuantity, double quantity, 
			String doe, int wastage, String displayableUnit, String sku) {
		
		double oldQuantity = this.getQuantity(sku, hotelId);
		
		String sql = "UPDATE Material SET" +
				" name = '"+ escapeString(materialName) + 
				"', ratePerUnit = "+ Double.toString(ratePerUnit) + 
				", minQuantity = "+ Double.toString(minQuantity) + 
				", wastage = "+ Integer.toString(wastage) + 
				", displayableUnit = '"+ escapeString(displayableUnit) + 
				"' WHERE hotelId = '" + escapeString(hotelId) +
				"' AND sku = '" + escapeString(sku) +
				"';";
		
		if (quantity>0)
			this.updateStock(hotelId, sku, quantity, quantity-oldQuantity, ratePerUnit, doe);

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
		String sql = "SELECT * FROM Material WHERE name='" + escapeString(name) + "' AND hotelId='"+escapeString(hotelId)+"';";
		return db.getOneRecord(sql, Stock.class, hotelId);
	}

	public String getNextSKU(String hotelId){

		String sql = "SELECT MAX(CAST(sku AS integer)) AS entityId "+
					"FROM Material WHERE hotelId='"+hotelId+"'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		return String.format("%14d", entity.getId() + 1).replace(" ", "0");
	}


	//--------------------------------------------Recipe

	public boolean deleteRecipeItem(String hotelId, String sku, String menuId) {
		String sql = "DELETE FROM Recipe WHERE sku = '" +sku+ "' AND menuId='"+menuId+ "' AND hotelId='"+hotelId+"';";
		return db.executeUpdate(sql, true);
	}
	
	public ArrayList<Stock> getRecipe(String hotelId, String menuId) {
		String sql = "SELECT Recipe.sku AS sku, "
				+ "Recipe.quantity AS quantity, "
				+ "Material.name AS name, "
				+ "Material.unit AS unit, "
				+ "Recipe.unit AS displayableUnit "
				+ "FROM Material, Recipe "
				+ "WHERE Recipe.hotelId= '"+hotelId+"' "
				+ "AND Recipe.menuId= '"+menuId+"' "
				+ "AND Material.sku == Recipe.sku;";
		return db.getRecords(sql, Stock.class, hotelId);
	}

	public Stock getMethod(String hotelId, String menuId) {
		String sql = "SELECT MenuItems.method AS method "
				+ "FROM MenuItems "
				+ "WHERE MenuItems.hotelId= '"+hotelId+"' "
				+ "AND MenuItems.menuId= '"+menuId+"';";
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
		String sql = "SELECT * FROM Recipe WHERE sku='" + escapeString(sku) + "' AND menuId='"+escapeString(menuId) + "' AND hotelId='"+escapeString(hotelId)+"';";
		return db.getOneRecord(sql, Stock.class, hotelId);
	}

	public boolean addRecipe(String hotelId, double quantity, String menuId, String sku, String unit) {
		String sql = "INSERT INTO Recipe " +
				"(hotelId, sku, unit, menuId, quantity) " +
				"VALUES('"+ escapeString(hotelId) + 
				"', '"+ escapeString(sku) + 
				"', '"+ escapeString(unit) + 
				"', '"+ escapeString(menuId) + 
				"', "+ Double.toString(quantity) + 
				");";
		return db.executeUpdate(sql, true);
	}

	public boolean updateRecipe(String hotelId, double quantity, String menuId, String sku, String unit) {

		String sql = "UPDATE Recipe SET" +
				" quantity = "+ Double.toString(quantity) + 
				", unit = '"+ escapeString(unit) +  
				"' WHERE hotelId = '" + escapeString(hotelId) +
				"' AND sku = '" + escapeString(sku) +
				"' AND menuId = '" + escapeString(menuId) +
				"';";
		return db.executeUpdate(sql, true);
	}
	
	public boolean updateMethod(String hotelId, String menuId, String method) {

		String sql = "UPDATE MenuItems SET" +
				" method = '"+ escapeString(method) +
				"' WHERE hotelId = '" + escapeString(hotelId) +
				"' AND menuId = '" + escapeString(menuId) +
				"';";
		return db.executeUpdate(sql, true);
	}

	public boolean reduceQuantity(String hotelId, String sku, double newQuantity, double quantity) {
		
		String sql = "UPDATE Stock SET" +
				" quantity = "+ Double.toString(newQuantity) + 
				" WHERE hotelId = '" + escapeString(hotelId) +
				"' AND sku = '" + escapeString(sku) +
				"';";

		 if(!db.executeUpdate(sql, true))
			 return false;
		 this.updateStockLog(hotelId, sku, quantity, quantity*this.getRatePerUnit(sku, hotelId), "REDUCE");
		 return true;
	}

	//----------------------------------------------KOT
	public ArrayList<OrderItem> getOrderedItemsForKOT(String hotelId, String orderId) {
		
		String sql = "SELECT OrderItems.subOrderId AS subOrderId, "
				+ "OrderItems.orderId AS orderId, "
				+ "OrderItems.subOrderDate AS subOrderDate, "
				+ "OrderItems.qty AS qty, "
				+ "MenuItems.title AS title, "
				+ "MenuItems.menuId AS menuId, "
				+ "MenuItems.vegType AS vegType, "
				+ "MenuItems.station AS station, "
				+ "OrderItems.specs AS specs "
				+ "FROM OrderItems, MenuItems WHERE orderId='" + orderId 
				+ "' AND OrderItems.menuId==MenuItems.menuId "
				+ "AND OrderItems.isKotPrinted = 0 "
				+ "AND OrderItems.hotelId='"+hotelId+"' "
				+ "ORDER BY MenuItems.category, OrderItems.Id desc;";
		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	public ArrayList<OrderItem> checkKOTPrinting(String hotelId){
		
		String sql = "SELECT distinct orderId FROM OrderItems "
				+ "WHERE OrderItems.hotelId = '"+hotelId+"' "
				+ "AND isKotPrinted == 0;";
				
		return db.getRecords(sql, OrderItem.class, hotelId);
	}

	//---------------------------------------------KDS
	
	public ArrayList<KitchenDisplayOrders> getKDSOrdersListView(String hotelId){
		
		String hotelType = this.getHotelById(hotelId).getHotelType();
		
		String sql =  "SELECT OrderItems.orderId as orderId,"
				+" OrderItems.subOrderDate as subOrderDate,"
				+" OrderItems.subOrderId as subOrderId,"
				+" MenuItems.title as title,"
				+" MenuItems.menuId as menuId,"
				+" MenuItems.vegType as vegType,"
				+" OrderItems.qty as qty, "
				+" OrderItems.specs as specs,"
				+" OrderItems.state as orderState, "
				+" Orders.inhouse as inhouse, "
				+" Orders.billNo as billNo, "
				+" Orders.customerAddress as customerAddress, "
				+" Orders.customerName as customerName"
				+" FROM OrderItems, MenuItems, Orders"
				+" WHERE OrderItems.menuId == MenuItems.menuID"
				+" AND OrderItems.orderId == Orders.orderId";
		
		if(hotelType.equals("PREPAID"))
			sql +=  " AND (Orders.state == " + ORDER_STATE_BILLING
				+" OR Orders.state == "+ ORDER_STATE_SERVICE
				+" OR Orders.state == "+ ORDER_STATE_OFFKDS+")";
		else
			sql += " AND Orders.state == " + ORDER_STATE_SERVICE;
		
		sql += " AND OrderItems.hotelId == '"+ hotelId +"'"
			+" AND Orders.hotelId == '"+ hotelId +"'"
			+" AND Orders.orderDate LIKE '%"+ this.getServiceDate(hotelId) +"%'"
			+" ORDER BY OrderItems.orderId ASC;";
		
		return db.getRecords(sql, KitchenDisplayOrders.class, hotelId);
	}
	//----------------------------------------------Ratings

	public boolean submitRatings(String hotelId, String orderId, String customerName, String customerNumber, 
			String customerBirthdate, String customerAnniversary, String reviewSuggestions, JSONObject ratings, Boolean wantsPromotion) {
		try {
			String sql="";
			if(!customerNumber.equals("")){
				if (!hasCustomer(hotelId, customerNumber)) {
					addCustomer(hotelId, customerName, customerNumber, "", customerBirthdate, customerAnniversary, "", wantsPromotion);
				}
				else {
					modifyCustomer(hotelId, customerName, customerNumber, customerBirthdate, customerAnniversary, "", "", wantsPromotion);
				}
			}

			sql = "UPDATE Orders SET customerName='" + 
					customerName + "', customerNumber='" + 
					customerNumber + "', rating_ambiance=" +
					ratings.getInt("ambianceRating")+ ", rating_qof=" +
					ratings.getInt("qualityOfFoodRating") + ", rating_service=" +
					ratings.getInt("serviceRating") + ", rating_hygiene=" +
					ratings.getInt("hygieneRating") +", reviewSuggestions='" + 
					reviewSuggestions + "' WHERE orderId='"+orderId+"' AND hotelId='"+hotelId+"';";
			return db.executeUpdate(sql, true);
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public Integer getAmbiancePoints(String hotelId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT TOTAL(rating_ambiance) AS entityId FROM Orders WHERE waiterId=='"+userId+"' AND orderDate=='"+(new SimpleDateFormat("yyyy/MM/dd")).format(dt)+"' AND hotelId='"+hotelId+"';";
		EntityId entity=db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}
	
	public Integer getQoFPoints(String hotelId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT TOTAL(rating_qof) AS entityId FROM Orders WHERE waiterId=='"+userId+"' AND orderDate=='"+(new SimpleDateFormat("yyyy/MM/dd")).format(dt)+"' AND hotelId='"+hotelId+"';";
		EntityId entity=db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}
	
	public Integer getServicePoints(String hotelId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT TOTAL(rating_service) AS entityId FROM Orders WHERE waiterId=='"+userId+"' AND orderDate=='"+(new SimpleDateFormat("yyyy/MM/dd")).format(dt)+"' AND hotelId='"+hotelId+"';";
		EntityId entity=db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}
	
	public Integer getHygienePoints(String hotelId, String userId, Date dt) {
		/* A small Hack */
		String sql = "SELECT TOTAL(rating_hygiene) AS entityId FROM Orders WHERE waiterId=='"+userId+"' AND orderDate=='"+(new SimpleDateFormat("yyyy/MM/dd")).format(dt)+"' AND hotelId='"+hotelId+"';";
		EntityId entity=db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}

	public double getAverageFood(String hotelId, String custNumber) {
		
		String sql = "SELECT ROUND(AVG(rating_qof)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"+custNumber+"'";
		
		EntityDouble entity =  db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}
	
	public double getAverageAmbiance(String hotelId, String custNumber) {
		
		String sql = "SELECT ROUND(AVG(rating_ambiance)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"+custNumber+"'";
		
		EntityDouble entity =  db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}
	
	public double getAverageService(String hotelId, String custNumber) {
		
		String sql = "SELECT ROUND(AVG(rating_service)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"+custNumber+"'";
		
		EntityDouble entity =  db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}
	
	public double getAverageHygiene(String hotelId, String custNumber) {
		
		String sql = "SELECT ROUND(AVG(rating_hygiene)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"+custNumber+"'";
		
		EntityDouble entity =  db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}
	
	public double getOverallAvgFood(String hotelId) {
		
		String sql = "SELECT ROUND(AVG(rating_qof)*100)/100 AS entityId FROM Orders;";
		
		EntityDouble entity =  db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}
	
	public double getOverallAvgAmbiance(String hotelId) {
		
		String sql = "SELECT ROUND(AVG(rating_ambiance)*100)/100 AS entityId FROM Orders;";
		
		EntityDouble entity =  db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}
	
	public double getOverallAvgService(String hotelId) {
		
		String sql = "SELECT ROUND(AVG(rating_service)*100)/100 AS entityId FROM Orders;";
		
		EntityDouble entity =  db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}
	
	public double getOverallAvgHygiene(String hotelId) {
		
		String sql = "SELECT ROUND(AVG(rating_hygiene)*100)/100 AS entityId FROM Orders;";
		
		EntityDouble entity =  db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}

	//----------------------------------------------------Station

	public ArrayList<KitchenStation> getKitchenStations(String hotelId) {
		String sql = "SELECT * FROM Stations WHERE hotelId='"+hotelId+"';";
		return db.getRecords(sql,KitchenStation.class, hotelId);
	}

	//----------------------------------------------------Payment

	public Double getTotalPaidAmount(String hotelId, String orderId){
		String sql = "SELECT SUM(cashPayment) AS entityId FROM Payment WHERE hotelId='" + hotelId +"' AND orderId='" + orderId +"'";
		EntityDouble entity=db.getOneRecord(sql, EntityDouble.class, hotelId);
		if (entity != null) {
			Double total = entity.getId();
			sql = "SELECT SUM(cardPayment) AS entityId FROM Payment WHERE hotelId='" + hotelId +"' AND orderId='" + orderId +"'";
			entity=db.getOneRecord(sql, EntityDouble.class, hotelId);
			if (entity != null)
				total += entity.getId();
			return total;
		}
		return 0.0;
	}

	//-----------------------------------------------------Discount
	public Boolean addDiscount(String hotelId, String name, String description, int type, int value,
			String startDate, String expiryDate, String usageLimit, String validColletions) {
		
		String sql = "INSERT INTO Discount " +
				"(hotelId, name, description, type, value, startDate, expiryDate, usageLimit, validCollections) " +
				"VALUES('"+ escapeString(hotelId) + 
				"', '"+ escapeString(name) + 
				"', '"+ escapeString(description) + 
				"', '"+ Integer.toString(type) +
				"', "+ Integer.toString(value) + 
				", '"+ escapeString(startDate) +  
				"', '"+ escapeString(expiryDate) + 
				"', '"+ escapeString(usageLimit) +
				"', '"+ escapeString(validColletions) +
				"');";
		return db.executeUpdate(sql, true);
	}
	
	public Boolean editDiscount(String hotelId, String name, String description, int type, int value,
			String startDate, String expiryDate, String usageLimit, String validColletions) {
		
		String sql = "UPDATE Discount SET " + 
				"description = '"+ escapeString(description) + 
				"', type = '"+ Integer.toString(type) + 
				"', value = "+ Integer.toString(value) + 
				", startDate = '"+ escapeString(startDate) +  
				"', expiryDate = '"+ escapeString(expiryDate) + 
				"', usageLimit = '"+ escapeString(usageLimit) +
				"', validCollections = '"+ escapeString(validColletions) +
				"' WHERE  hotelId='"+escapeString(hotelId) +  
				"' AND name = '"+name+"';";
		return db.executeUpdate(sql, true);
	}
	
	public Boolean updateUsageLimit(String hotelId, String name,  int usageLimit) {
		
		String sql = "UPDATE Discount SET " + 
				"usageLimit = '"+ Integer.toString(usageLimit) +
				"' WHERE  hotelId='"+escapeString(hotelId) +  
				"' AND name = '"+name+"';";
		return db.executeUpdate(sql, true);
	}
	
	public ArrayList<Discount> getAllDiscounts(String hotelId) {
		String sql = "SELECT * FROM Discount WHERE hotelId='"+escapeString(hotelId)+"';";
		return db.getRecords(sql, Discount.class, hotelId);
	}
	
	public Boolean deleteDiscount(String hotelId, String name) {
		String sql = "DELETE FROM Discount WHERE name='" + name+ "' AND hotelId='"+hotelId+"';";
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
		String sql = "SELECT * FROM Discount WHERE name='" + escapeString(name) + "' AND hotelId='"+escapeString(hotelId)+"';";
		return db.getOneRecord(sql, Discount.class, hotelId);
	}
	
	public String getDiscountUsageLimit(String hotelId, String name) {
		String sql = "SELECT usageLimit FROM Discount WHERE name='" + escapeString(name) + "' AND hotelId='"+escapeString(hotelId)+"';";
		Discount dis = db.getOneRecord(sql, Discount.class, hotelId);
		
		return dis.getUsageLimit();
	}

	public Double getAppliedDiscount(String hotelId, String orderId){
		String sql = "SELECT discount AS entityId FROM PAYMENT WHERE hotelId = '"+hotelId+"' AND orderId = '"+orderId+"';";
		
		EntityDouble entity = db.getOneRecord(sql, EntityDouble.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0.0;
	}
	public boolean applyDiscount(String hotelId, String orderId, String discountCode) {
		
		String sql = "UPDATE Orders SET discountCode = '" + escapeString(discountCode) +
				"' WHERE hotelId = '" + hotelId + "' AND orderId = '" + orderId + "';";
		return db.executeUpdate(sql, true);
	}

	//-----------------------------------------------Report

	public MonthReport getTotalOrdersForCurMonth(String hotelId, String duration) {
		String sql = "SELECT SUBSTR(subOrderDate, 1, "+duration.length()+") as entity, COUNT(*) as count FROM OrderItems WHERE entity ='"+ escapeString(duration) +"' AND hotelId='"+escapeString(hotelId)+"';";
		return db.getOneRecord(sql, MonthReport.class, hotelId);
	}
	
	public MonthReport getBestWaiter(String hotelId, String duration) {
		
		String sql = "SELECT RTRIM(orderId, '0123456789:') AS user, SUBSTR(subOrderDate, 1, "+duration.length()+") AS duration, count(*) AS waitersOrders, employeeID FROM OrderItems, Users "
					+	"WHERE duration = '"+ escapeString(duration)
					+	"' AND Users.userId = user "
					+	"GROUP BY user "
					+	"ORDER BY count(*) desc "
					+	"LIMIT 1;";
		return db.getOneRecord(sql, MonthReport.class, hotelId);
	}

	public ArrayList<MonthReport> getWeeklyRevenue(String hotelId) {
		
		ArrayList<MonthReport> weeklyRevenue = new ArrayList<MonthReport>();
		
		String duration = "";
		
		for(int i= 0; i < 7; i++){
		
			duration = getPreviousDateString(i);
			String sql = "SELECT SUM(rate) AS totalSales, SUBSTR(subOrderDate, 1, "+duration.length()+") AS duration "
					+ "FROM OrderItems "
					+ "WHERE duration = '"+duration+"';";
			MonthReport report = db.getOneRecord(sql, MonthReport.class, hotelId);
			weeklyRevenue.add(report);
		}
		return weeklyRevenue;
	}
	
	public ArrayList<YearlyReport> getYearlyOrders(String hotelId) {
		
		ArrayList<YearlyReport> out = new ArrayList<YearlyReport>();
		
		Date date  = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		String duration = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM");
		
		for(int i=0; i<9; i++){
			duration = dateFormat.format(cal.getTime());
			String sql = "SELECT count(Id) AS totalOrders FROM Orders WHERE orderDate LIKE '"+duration+"%';";
			YearlyReport report = db.getOneRecord(sql, YearlyReport.class, hotelId);
			report.month = month + 1;
			out.add(report);
			cal.add(Calendar.MONTH, -1);
			month = cal.get(Calendar.MONTH);
			
		}
		return out;
	}

	//Reports
	//expense-ap
	public ArrayList<Expense> getExpenseReport(String hotelid,String startDate, String endDate){
		
		String sql ="SELECT Expenses.serviceDate AS date, "	
				+  "Expenses.amount AS amount, "
				+  "Expenses.type AS type, "
				+  "Expenses.payee AS payee, "
				+  "REPLACE(REPLACE(REPLACE(Expenses.memo,CHAR(13),''),CHAR(10),''),',','|') AS memo "         //replaced ',' with '|' AND '/n' with ' '
				+  "FROM Expenses "
				+  "WHERE Expenses.hotelId = '"+hotelid+"' "
				+ "AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+appendEndDate(endDate)+"';";
		return db.getRecords(sql, Expense.class, hotelid);
	}

	//new-ap(in progress)
	public ArrayList<DailyDiscountReport> getDailyDiscountReport(String hotelId,String startDate, String endDate) {
		String sql = "SELECT discount.name AS name, "
				+ "CASE discount.type WHEN 1 THEN 'Rs '||discount.value else discount.value||' %' END AS value, "
				+ "discount.description AS description, "
				+ "(ROUND(ROUND(SUM(payment.discount))/ROUND(SUM(payment.total+payment.discount))*100*100)/100)||' %' AS discountPer, "
				+ "(ROUND(SUM(payment.total+payment.discount)*100)/100) AS sumTotal, "
				+ "(ROUND(AVG(payment.total+payment.discount)*100)/100) AS avgTotal, "
				+ "(ROUND(AVG(payment.discount)*100)/100) AS avgDiscount, "
				+ "(ROUND(SUM(payment.discount)*100)/100) AS sumDiscount, "
				+ "(ROUND(SUM((total+discount)- discount)*100)/100) AS sumDiscountedTotal, "
				+ "COUNT(payment.Id) AS ordersAffected "
				+ "FROM payment, discount "
				+ "WHERE payment.hotelId='"+hotelId+"' "
				+ "AND payment.orderDate BETWEEN '"+ startDate + "' AND '" +appendEndDate(endDate) + "' "
				+ "AND payment.discountName=discount.name "
				+ "GROUP BY discount.name;";
		System.out.println(sql);
		return db.getRecords(sql, DailyDiscountReport.class, hotelId);
	}
	
	//new-ap
	//refere googlesheets for logic...
	public ArrayList<GrossSaleReport> getGrossSalesReport(String hotelId, String startDate, String endDate) {
		endDate = appendEndDate(endDate);
		String sql = "SELECT ROUND(SUM(payment.total)*100)/100 as grossTotal, "
				+ "ROUND(SUM(payment.discount)*100)/100 as grossDiscount, "
				+ "ROUND(SUM(payment.gst)*100)/100 as grossTaxes, "
				+ "ROUND(SUM(payment.serviceCharge)*100)/100 as grossServiceCharge, "
				+ "ROUND(SUM(payment.total)*100)/100-ROUND((Select SUM(Expenses.amount) From Expenses Where expenses.type!='CASH_LIFT' AND expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND expenses.hotelId='"+hotelId+"')*100)/100 as NetSales, "
				+ "ROUND((Select SUM(Expenses.amount) From Expenses Where expenses.type!='CASH_LIFT' AND expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND expenses.hotelId='"+hotelId+"')*100)/100 as grossExpenses, "
				+ "ROUND((SUM(payment.total)+ SUM(payment.discount)+ SUM(payment.gst)+ SUM(payment.serviceCharge)+ ((Select SUM(Expenses.amount) From Expenses Where expenses.type!='CASH_LIFT' AND expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND expenses.hotelId='"+hotelId+"') -SUM(payment.total) ) )*100)/100 as Total, "
				+ "ROUND((Select SUM(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='99' AND OrderItemLog.hotelId='"+hotelId+"' AND OrderItemLog.dateTime BETWEEN '"+startDate+"' AND '"+endDate+"')*100)/100  as sumVoids,  "
				+ "(Select COUNT(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='99' AND OrderItemLog.hotelId='"+hotelId+"' AND OrderItemLog.dateTime BETWEEN '"+startDate+"' AND '"+endDate+"')  as countVoids,  "
				+ "ROUND((Select SUM(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='100' AND OrderItemLog.hotelId='"+hotelId+"' AND OrderItemLog.dateTime BETWEEN '"+startDate+"' AND '"+endDate+"')*100)/100  as sumReturns, "
				+ "(Select COUNT(OrderItemLog.rate*OrderItemLog.quantity) FROM OrderItemLog Where OrderItemLog.state='100' AND OrderItemLog.hotelId='"+hotelId+"' AND OrderItemLog.dateTime BETWEEN '"+startDate+"' AND '"+endDate+"')  as countReturns "
				+ "FROM Payment "
				+" Where payment.hotelId='"+hotelId+"' "
				+ "AND payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"';";
		System.out.println(sql);
		return db.getRecords(sql, GrossSaleReport.class, hotelId);
	}
	
	//newCollectionWiseReportA-ap (gross and totals...)
		public ArrayList<CollectionWiseReportA> getCollectionWiseReportA(String hotelId,String startDate,String endDate) {
			endDate = appendEndDate(endDate);
			String sql = "Select DISTINCT(MenuItems.category) AS collection, "
					+ "SUM(OrderItems.qty*OrderItems.rate) AS grossTotal, "
					+ "ROUND(AVG(OrderItems.qty*OrderItems.rate)*100)/100 AS averagePrice, "
					+ "COUNT(OrderItems.menuId) AS noOrdersAffected, "
					+ "(ROUND(CAST(COUNT(OrderItems.menuId) AS FLOAT)/(SELECT CAST(SUM(OrderItems.qty) AS FLOAT) FROM OrderItems WHERE OrderItems.hotelId='"+hotelId+"' AND OrderItems.subOrderDate BETWEEN '"+startDate+"' AND '"+endDate+"')*100*100)/100)||' %' AS noOrdersAffectedPer, "
					+ "SUM(OrderItems.qty) AS totalQuantityOrdered, "
					+ "(ROUND(CAST(SUM(OrderItems.qty) AS FLOAT)/(SELECT CAST(SUM(OrderItems.qty) AS FLOAT) FROM OrderItems WHERE OrderItems.hotelId='"+hotelId+"' AND OrderItems.subOrderDate BETWEEN '"+startDate+"' AND '"+endDate+"')*100*100)/100)||' %' AS totalQuantityOrderedPer "
					+ "FROM OrderItems,MenuItems "
					+ "WHERE OrderItems.menuId=MenuItems.menuId "
					+ "AND OrderItems.hotelId='"+hotelId+"' "
					+ "AND OrderItems.subOrderDate BETWEEN '"+startDate+"' AND '"+endDate+"' "
					+ "GROUP BY MenuItems.category;";
			System.out.println(sql);
			return db.getRecords(sql, CollectionWiseReportA.class, hotelId);
	}
	
	//newCollectionWiseReportB-ap (top/hot selling item!)
	public ArrayList<CollectionWiseReportB> getCollectionWiseReportB(String hotelId,String startDate,String endDate) {
		endDate = appendEndDate(endDate);
		String sql = "SELECT title AS topItemTitle, max(SUM) "
				+ "FROM (SELECT MenuItems.category, MenuItems.title, OrderItems.menuId, SUM(OrderItems.qty) AS SUM "
				+ "        FROM OrderItems, MenuItems "
				+ "        WHERE OrderItems.menuId = MenuItems.menuId AND OrderItems.subOrderDate BETWEEN '"+startDate+"' AND '"+endDate+"' "
				+ "        GROUP BY MenuItems.menuId "
				+ "        ORDER BY MenuItems.title) "
				+ "GROUP BY category;";
		System.out.println(sql);
		return db.getRecords(sql, CollectionWiseReportB.class, hotelId);
	}

	//total operating cost-ap
	public ArrayList<DailyOperationReport> getDailyOperationReport1(String hotelId,String startDate,String endDate) {
		endDate = appendEndDate(endDate);
		String sql = "SELECT ROUND(SUM(Expenses.amount)*100)/100 AS  totalOperatingCost, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='INVENTORY' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS INVENTORY, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='LABOUR' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS LABOUR, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='RENT' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS RENT, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='ELECTRICITY_BILL' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS ELECTRICITY_BILL, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='GAS_BILL' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS GAS_BILL, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='PETROL' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS PETROL, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='TELEPHONE_BILL' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS TELEPHONE_BILL, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='MOBILE_RECHARGE' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS MOBILE_RECHARGE, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='INTERNET' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS INTERNET, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='SOFTWARE' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS SOFTWARE, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='COMPUTER_HARDWARE' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS COMPUTER_HARDWARE, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='REPAIRS' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS REPAIRS, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='OTHERS' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS OTHERS, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type='CASH_LIFT' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Expenses.hotelId='"+hotelId+"')*100)/100 AS CASH_LIFT " + 
				"FROM Expenses " + 
				"WHERE Expenses.hotelId='"+hotelId+"' " +  
				"AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"';";
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}
			
	//total revenue-ap
	public ArrayList<DailyOperationReport> getDailyOperationReport2(String hotelId,String startDate,String endDate) {
		String sql = "SELECT (SELECT ROUND(SUM(TotalRevenue.total)*100)/100 FROM TotalRevenue WHERE TotalRevenue.hotelId='"+hotelId+"' AND TotalRevenue.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' ) AS totalRevenue, " + 
				"ROUND(SUM(payment.total)*100)/100 as grossTotal, " + 
				"ROUND(SUM(payment.discount)*100)/100 as grossDiscount, " + 
				"ROUND(SUM(payment.gst)*100)/100 as grossTaxes, " + 
				"ROUND(SUM(payment.serviceCharge)*100)/100 as grossServiceCharge, " + 
				"ROUND(ROUND((Select SUM(payment.total) FROM payment WHERE payment.hotelId='"+hotelId+"' AND Payment.orderDate  BETWEEN '"+startDate+"' AND '"+endDate+"')*100)/100 - ROUND((Select SUM(Expenses.amount) From Expenses WHERE expenses.type!='CASH_LIFT' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND expenses.hotelId='"+hotelId+"')*100)/100)*100/100 as NetSales " + 
				"FROM Payment " + 
				"WHERE payment.hotelId='"+hotelId+"' " + 
				"AND payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"';";
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	//Total Operating Margin-ap
	public ArrayList<DailyOperationReport> getDailyOperationReport3(String hotelId,String startDate,String endDate) {
		String sql = "SELECT ROUND((SUM(TotalRevenue.total) - (SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.hotelId='"+hotelId+"' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+appendEndDate(endDate)+"'))*100)/100 AS totalOperatingMargin, " + 
				"ROUND((SELECT SUM(Payment.Total) FROM Payment WHERE Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"')*100)/100 AS paidIn, " + 
				"ROUND((SELECT SUM(Expenses.amount) FROM Expenses WHERE Expenses.type!='CASH_LIFT' AND expenses.hotelId='"+hotelId+"' AND Expenses.serviceDate BETWEEN '"+startDate+"' AND '"+appendEndDate(endDate)+"')*100)/100 AS paidOut " + 
				"FROM TotalRevenue " + 
				"Where hotelId='"+hotelId+"' " + 
				"AND TotalRevenue.serviceDate BETWEEN '"+startDate+"' AND '"+endDate+"';";
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}
			
	//Operating Metrics-ap
	//main 3
	public ArrayList<DailyOperationReport> getDailyOperationReport4(String hotelId,String startDate,String endDate) {
		String sql = "SELECT Distinct Orders.serviceType AS serviceType, " + 
				"SUM(Payment.total)/SUM(Orders.numberOfGuests) AS AvgAmountPerGuest, " + 
				"(SUM(Payment.total)/COUNT(Payment.billNo)) AS AvgAmountPerCheck, " +
				"SUM(Payment.total) AS Total, " +
				"SUM(Orders.numberOfGuests) AS noOfGuests, " +
				"COUNT(Payment.billNo) AS noOfBills " + 
				"FROM Orders, Payment " + 
				"WHERE Orders.hotelId='"+hotelId+"' " + 
				"AND Orders.orderId=Payment.orderId " + 
				"AND Orders.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' " + 
				"GROUP BY Orders.serviceType;";
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}
	
	//tables turned-ap
	public ArrayList<DailyOperationReport> getDailyOperationReport5(String hotelId,String startDate,String endDate) {
		String sql = "SELECT SUM(Payment.total)/COUNT(distinct Payment.billNo) as AvgAmountPerTableTurned "
				+ "FROM Payment,Orders "
				+ "WHERE payment.orderId=orders.orderId "
				+ "AND orders.inhouse='1' "
				+ "AND orders.serviceType=serviceType "
				+ "AND orders.hotelId='"+hotelId+"' "
				+ "AND orders.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' "
				+ "GROUP BY Orders.serviceType;";

		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}
	
	//voids-ap
	public ArrayList<DailyOperationReport> getDailyOperationReport6(String hotelId,String startDate,String endDate) {
		String sql = "SELECT COUNT(orders.orderId) AS voids "
				+ "FROM orders "
				+ "WHERE orders.state='99' "
				+ "AND orders.hotelId='"+hotelId+"' "
				+ "AND orders.orderDate BETWEEN '"+startDate+"' and '"+endDate+"' "
				+ "GROUP BY Orders.serviceType;";		
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}
	
	//returns-ap
	public ArrayList<DailyOperationReport> getDailyOperationReport7(String hotelId,String startDate,String endDate) {
		String sql = "SELECT COUNT(Distinct OrderItemLog.orderId) AS returns "
				+ "FROM orderitemlog, orders "
				+ "WHERE orderitemlog.state='100' "
				+ "AND orderitemlog.orderId=orders.orderId "
				+ "AND orderitemlog.hotelId='"+hotelId+"' "
				+ "AND orderitemlog.dateTime BETWEEN '"+startDate+"' AND '"+appendEndDate(endDate)+"' "
				+ "GROUP BY Orders.serviceType;";
		System.out.println(sql);
		return db.getRecords(sql, DailyOperationReport.class, hotelId);
	}

	//Jason
	//Discount Report-ap(edited)
	public ArrayList<DiscountReport> getDiscountReport(String hotelId,String startDate, String endDate) {
		String sql = 
				"SELECT payment.discountName, "
				+ "payment.orderDate, "
				+ "(payment.foodBill+payment.barBill+payment.serviceCharge+payment.VATBAR+payment.gst) AS total, "
				+ "discount, "
				+ "customerName ,"
				+ "((payment.foodBill+payment.barBill+payment.serviceCharge+payment.VATBAR+payment.gst) - payment.discount) AS discountedTotal "
				+ "FROM payment, orders "
				+ "WHERE payment.orderid = orders.orderid "
				+ "AND discountName!='' "
				+ "AND payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' "
				+ "AND payment.hotelId='"+hotelId+"';";
		System.out.println(sql);
		return db.getRecords(sql, DiscountReport.class, hotelId);
	}

	//Jason
	//itemwise-ap(hot selling items/menu category)
	public ArrayList<itemWiseReport> getItemwiseReport(String hotelId,String startDate,String endDate) {
		String sql = "SELECT menuitems.menuid AS menuId, "
			+ "category AS category, "
			+ "title AS title, "
			+ "SUM(qty) AS qty "
			+ "FROM menuItems, orderitems " 
			+ "WHERE menuitems.menuid = orderitems.menuid "
			+ "AND orderitems.subOrderDate BETWEEN '"+ startDate + "' AND '" + appendEndDate(endDate) + "' "
			+ "AND orderitems.hotelId='"+ hotelId +"' " 
			+ "GROUP BY menuitems.menuid "
			+ "ORDER BY category;";
		System.out.println(sql);
		return db.getRecords(sql, itemWiseReport.class, hotelId);
	}


	//liquor-ap(hot selling items/menu category)
	public ArrayList<itemWiseReport> getLiquorReport(String hotelId,String startDate,String endDate) {
		String sql = "SELECT menuitems.menuid AS menuId, "
			+ "category AS category, "
			+ "title AS title, "
			+ "SUM(qty) AS qty "
			+ "FROM menuItems, orderitems " 
			+ "WHERE menuitems.menuid = orderitems.menuid "
			+ "AND orderitems.subOrderDate BETWEEN '"+ startDate + "' AND '" + appendEndDate(endDate) + "' "
			+ "AND orderitems.hotelId='"+ hotelId +"' " 
			+ "AND menuItems.station='Bar' " 
			+ "GROUP BY menuitems.menuid "
			+ "ORDER BY category, MenuItems.title;";
		System.out.println(sql);
		return db.getRecords(sql, itemWiseReport.class, hotelId);
	}
	
	//lunchanddinner-ap in progress
	public ArrayList<LunchDinnerSalesReport> getLunchDinnerSalesReport(String hotelId,String startDate,String endDate,Integer i) {
		String sql = "SELECT SUM(Payment.foodBill) AS foodBill, " + 
				"SUM(Payment.barBill) AS barBill, " + 
				"SUM(Orders.numberOfGuests) AS pax, " + 
				"SUM(Payment.cashPayment) AS cash, " + 
				"SUM(Payment.cardPayment) AS card, " + 
				"(SELECT SUM(Payment.cardPayment) FROM Payment,Orders WHERE Payment.cardType LIKE '%VISA%' AND Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Orders.inhouse='"+i+"' AND Payment.orderId = Orders.orderId ) AS VISA, " + 
				"(SELECT SUM(Payment.cardPayment) FROM Payment,Orders WHERE Payment.cardType LIKE '%MASTERCARD%' AND Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Orders.inhouse='"+i+"' AND Payment.orderId = Orders.orderId ) AS MASTERCARD, " + 
				"(SELECT SUM(Payment.cardPayment) FROM Payment,Orders WHERE Payment.cardType LIKE '%MAESTRO%' AND Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Orders.inhouse='"+i+"' AND Payment.orderId = Orders.orderId ) AS MAESTRO, " + 
				"(SELECT SUM(Payment.cardPayment) FROM Payment,Orders WHERE Payment.cardType LIKE '%AMEX%' AND Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Orders.inhouse='"+i+"' AND Payment.orderId = Orders.orderId ) AS AMEX, " + 
				"(SELECT SUM(Payment.cardPayment) FROM Payment,Orders WHERE Payment.cardType LIKE '%RUPAY%' AND Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Orders.inhouse='"+i+"' AND Payment.orderId = Orders.orderId ) AS RUPAY, " + 
				"(SELECT SUM(Payment.cardPayment) FROM Payment,Orders WHERE Payment.cardType LIKE '%MSWIPE%' AND Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Orders.inhouse='"+i+"' AND Payment.orderId = Orders.orderId ) AS MSWIPE, " + 
				"(SELECT SUM(Payment.cardPayment) FROM Payment,Orders WHERE Payment.cardType LIKE '%ZOMATO%' AND Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Orders.inhouse='"+i+"' AND Payment.orderId = Orders.orderId ) AS ZOMATO, " + 
				"(SELECT SUM(Payment.cardPayment) FROM Payment,Orders WHERE Payment.cardType LIKE '%PAYTM%' AND Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Orders.inhouse='"+i+"' AND Payment.orderId = Orders.orderId ) AS PAYTM, " + 
				"(SELECT SUM(Payment.cardPayment) FROM Payment,Orders WHERE Payment.cardType LIKE '%SWIGGY%' AND Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Orders.inhouse='"+i+"' AND Payment.orderId = Orders.orderId ) AS SWIGGY, " + 
				"(SELECT SUM(Payment.cardPayment) FROM Payment,Orders WHERE Payment.cardType LIKE '%MAGIC_PIN%' AND Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Orders.inhouse='"+i+"' AND Payment.orderId = Orders.orderId ) AS MAGIC_PIN, " + 
				"(SELECT SUM(Payment.cardPayment) FROM Payment,Orders WHERE Payment.cardType LIKE '%OTHERS%' AND Payment.hotelId='"+hotelId+"' AND Payment.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND Orders.inhouse='"+i+"' AND Payment.orderId = Orders.orderId ) AS OTHERS " + 
				"FROM Payment, Orders " + 
				"WHERE Payment.orderId = Orders.orderId "+ 
				"AND Payment.hotelId='"+hotelId+"' "+ 
				"AND Payment.orderdate BETWEEN '"+startDate+"' AND '"+endDate+"' " + 
				"AND Orders.inhouse='"+i+"';";
		System.out.println(sql);
		return db.getRecords(sql, LunchDinnerSalesReport.class, hotelId);
	}
	
	public ArrayList<Attendance> getAttendanceReport(String hotelId, String startDate, String endDate){
		
		String sql = "SELECT Employee.employeeId, Employee.salary, Employee.firstName, Employee.surName, Attendance.shift, Attendance.checkInDate, Attendance.reason, Attendance.authorisation, Attendance.checkInTime, Attendance.checkOutTIme, " + 
				"REPLACE(REPLACE(REPLACE(Attendance.isPresent, "+PRESENT+", \"PRESENT\") , "+ABSENT+", \"ABSENT\"), "+EXCUSED+", \"EXCUSED\") AS attendanceStr, " + 
				"	(SELECT COUNT(isPresent) FROM Attendance WHERE " + 
				"	Attendance.checkInDate between '"+startDate+"' AND '"+endDate+"' " + 
				"	AND Attendance.employeeId = Employee.employeeId" + 
				"	AND Attendance.authorisation = 1 " + 
				"	AND Attendance.isPresent=2 " +
				"	AND Attendance.hotelId = '"+hotelId+"' " + 
				"	GROUP BY Attendance.employeeId ) AS excusedCount," + 
				"	(SELECT COUNT(isPresent) FROM Attendance  " + 
				"	WHERE Attendance.checkInDate between '"+startDate+"' AND '"+endDate+"' " + 
				"	AND Attendance.employeeId = Employee.employeeId" + 
				"	AND Attendance.authorisation = 1 " + 
				"	AND Attendance.isPresent=3 " + 
				"	AND Attendance.hotelId = '"+hotelId+"' " + 
				"	GROUP BY Attendance.employeeId ) AS absentCount," + 
				"	(SELECT COUNT(isPresent) FROM Attendance " + 
				"	WHERE Attendance.checkInDate between '"+startDate+"' AND '"+endDate+"' " + 
				"	AND Attendance.employeeId = Employee.employeeId" + 
				"	AND Attendance.authorisation = 1 " + 
				"	AND Attendance.isPresent=1 " + 
				"	AND Employee.hotelId = '"+hotelId+"' " + 
				"	GROUP BY Attendance.employeeId ) AS presentCount " +
				"FROM Employee, Attendance " + 
				"WHERE Employee.employeeId = Attendance.employeeId " + 
				"AND Attendance.checkInDate between '"+startDate+"' AND '"+endDate + "' " +
				"AND Attendance.authorisation = 1 " +
				"AND Employee.hotelId = '" + hotelId + "' " +
				"ORDER BY Attendance.employeeId, Attendance.checkInDate, Attendance.shift";
		System.out.println(sql);
		return db.getRecords(sql, Attendance.class, hotelId);
	}
	
	public ArrayList<Attendance> getAttendanceReportB(String hotelId, String startDate, String endDate){
		
		String sql = "SELECT distinct Attendance.checkInDate " + 
				"FROM Attendance " + 
				"WHERE Attendance.checkInDate between '"+startDate+"' AND '"+endDate + "' " +
				"AND Attendance.authorisation = 1 " + 
				"AND Attendance.hotelId = '" + hotelId + "' " +
				"ORDER BY Attendance.checkInDate";
		System.out.println(sql);
		return db.getRecords(sql, Attendance.class, hotelId);
	}

	public Report getTotalSalesForService(String hotelId, String serviceDate, String serviceType){
		
		String sql = "SELECT SUM(Payment.foodBill) AS foodBill, "
				+  "SUM(Payment.barBill) AS barBill, "
				+  "SUM(Payment.foodBill + Payment.barBill + Payment.serviceCharge + Payment.gst) AS grossTotal, "
				+  "SUM(Payment.total) AS total, "
				+  "SUM(Payment.discount) AS discount, "
				+  "SUM(Payment.gst) AS gst, "
				+  "SUM(Payment.serviceCharge) AS serviceCharge, "
				+  "SUM(Payment.VATBAR) AS VATBAR, "
				+  "SUM(Payment.complimentary) AS complimentary, "
				+  "SUM(Orders.printCount) AS printCount, "
				+  "COUNT(*) AS orderCount, "
				+  "SUM(Payment.cashPayment) AS cashPayment, "
				+  "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 1 AND Orders.orderDate = '"+serviceDate+"' AND Orders.hotelId = '"+hotelId+"' AND Orders.serviceType = '"+serviceType+"')*100)/100 AS inhouse, "
				+  "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 0 AND Orders.orderDate = '"+serviceDate+"' AND Orders.hotelId = '"+hotelId+"' AND Orders.serviceType = '"+serviceType+"')*100)/100 AS homeDelivery, "
				+  "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 2 AND Orders.orderDate = '"+serviceDate+"' AND Orders.hotelId = '"+hotelId+"' AND Orders.serviceType = '"+serviceType+"')*100)/100 AS takeAway, "
				+  "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 3 AND Orders.orderDate = '"+serviceDate+"' AND Orders.hotelId = '"+hotelId+"' AND Orders.serviceType = '"+serviceType+"')*100)/100 AS zomato, "
				+  "ROUND((SELECT SUM(total) from Payment, Orders WHERE Orders.orderid == Payment.orderid AND inhouse = 4 AND Orders.orderDate = '"+serviceDate+"' AND Orders.hotelId = '"+hotelId+"' AND Orders.serviceType = '"+serviceType+"')*100)/100 AS swiggy "
				+  "FROM Payment, Orders "
				+  "WHERE Payment.orderId = Orders.orderId "
				+  "AND Orders.orderDate = '"+serviceDate+"' "
				+  "AND Orders.hotelId = '"+hotelId+"' "
				+  "AND Orders.serviceType = '"+serviceType+"';";
		
		return db.getOneRecord(sql, Report.class, hotelId);
	}
	
	public ArrayList<Expense> getCashExpenses(String hotelId, String serviceDate, String serviceType){
		String sql = "SELECT * FROM Expenses WHERE accountName = 'CASH_DRAWER' AND hotelId = '"+hotelId+"' "
				+ "AND serviceDate = '"+serviceDate+"' AND serviceType = '"+serviceType+"';";
		
		return db.getRecords(sql, Expense.class, hotelId);
	}
	
	public int getCardPaymentByType(String hotelId, String serviceDate, String serviceType, String cardType){
		
		String sql = "SELECT SUM(Payment.cardPayment) AS entityId "
				+  "FROM Payment, Orders "
				+  "WHERE Payment.orderId = Orders.orderId "
				+  "AND Orders.orderDate = '"+serviceDate+"' "
				+  "AND Orders.hotelId = '"+hotelId+"' "
				+  "AND Orders.serviceType = '"+serviceType+"' " 
				+  "AND Payment.cardType LIKE '%"+cardType+"%';";
		
		EntityId entity=db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}
	
	public int getVoidTransactions(String hotelId, String serviceDate, String serviceType){
		
		String sql = "SELECT SUM(Payment.total) AS entityId "
				+  "FROM Payment, Orders "
				+  "WHERE Payment.orderId = Orders.orderId "
				+  "AND Orders.orderDate = '"+serviceDate+"' "
				+  "AND Orders.hotelId = '"+hotelId+"' "
				+  "AND Orders.serviceType = '"+serviceType+"' " 
				+  "AND Orders.state = 99;";
		
		EntityId entity=db.getOneRecord(sql, EntityId.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0;
	}
	
	public Double getTotalCardPayment(String hotelId, String serviceDate, String serviceType){
		
		String sql = "SELECT SUM(Payment.cardPayment) as entityId "
				+ "FROM Payment,Orders " 
				+ "WHERE Payment.cardType NOT LIKE '%ZOMATO%' "
				+ "AND Payment.cardType NOT LIKE '%PAYTM%' "
				+ "AND Payment.cardType NOT LIKE '%SWIGGY%' "
				+ "AND Payment.cardType NOT LIKE '%MAGIC_PIN%' "
				+ "AND Orders.orderDate = '"+serviceDate+"' "
				+ "AND Orders.serviceType = '"+serviceType+"' "
				+ "AND Orders.hotelId = '"+hotelId+"' "
				+ "AND Payment.orderId = Orders.orderId;";
		
		EntityDouble entity=db.getOneRecord(sql, EntityDouble.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0.0;
	}

	public Double getTotalAppPayment(String hotelId, String serviceDate, String serviceType){
		
		String sql = "SELECT SUM(Payment.cardPayment) as entityId "
				+ "FROM Payment,Orders " 
				+ "WHERE Payment.cardType NOT LIKE '%VISA%' "
				+ "AND Payment.cardType NOT LIKE '%MASTERCARD%' "
				+ "AND Payment.cardType NOT LIKE '%MAESTRO%' "
				+ "AND Payment.cardType NOT LIKE '%AMEX%' "
				+ "AND Payment.cardType NOT LIKE '%RUPAY%' "
				+ "AND Payment.cardType NOT LIKE '%OTHERS%' "
				+ "AND Payment.cardType NOT LIKE '%MSWIPE%' "
				+ "AND Orders.orderDate = '"+serviceDate+"' "
				+ "AND Orders.serviceType = '"+serviceType+"' "
				+ "AND Orders.hotelId = '"+hotelId+"' "
				+ "AND Payment.orderId = Orders.orderId;";
		
		EntityDouble entity=db.getOneRecord(sql, EntityDouble.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return 0.0;
	}
	
	public ArrayList<Report> getSaleSummaryReport(String hotelId, String startDate, String endDate){
		
		String sql = "SELECT Payment.foodBill AS foodBill, "
				+  "Payment.barBill AS barBill, "
				+  "Payment.billNo AS billNo, "
				+  "Payment.total AS total, "
				+  "Payment.discount AS discount, "
				+  "Payment.serviceTax AS serviceTax, "
				+  "Payment.serviceCharge AS serviceCharge, "
				+  "Payment.tip AS tip, "
				+  "Payment.gst AS gst, "
				+  "Orders.numberOfGuests AS pax, "
				+  "Orders.inhouse AS inhouse, "
				+  "Orders.tableId AS tableId, "
				+  "Orders.orderDate AS orderDate, "
				+  "Payment.cashPayment AS cashPayment, "
				+  "Payment.cardPayment AS cardPayment, " 
				+  "Payment.cardType AS cardType " 
				+  "FROM Payment, Orders "
				+  "WHERE Payment.orderId = Orders.orderId "
				+  "AND Payment.hotelId = '"+hotelId;
		
		if(endDate.equals("")){
			sql += "' AND Orders.orderDate ='"+startDate+"';";
		}
		else{
			sql += "' AND Orders.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"';";
		}
		return db.getRecords(sql, Report.class, hotelId);
	}
	
	public ArrayList<CustomerReport> getCustomerReport(String hotelId, String startDate, String endDate) {
		String sql = "SELECT SUM(OrderItems.rate*qty) AS totalSpent, "
				+	"ROUND(SUM((OrderItems.rate*qty))/SUM(Orders.numberOfGuests)) AS spentPerPax, "
				+	"ROUND(SUM((OrderItems.rate*qty))/COUNT(Orders.orderId)) AS spentPerWalkin, "
				+	"SUM(Orders.numberOfGuests) AS totalGuests, Orders.customerName AS customerName, "
				+	"Orders.customerNumber AS mobileNo, "
				+	"COUNT(Orders.orderId) AS totalWalkins "
				+	"FROM OrderItems, Orders " 
				+	"WHERE OrderItems.orderId == Orders.orderId "
				+ 	"AND Orders.hotelId = '" +hotelId+ "' "
				+ 	"AND OrderItems.hotelId = '" +hotelId+ "' "
				+ 	"AND orderDate BETWEEN '"+ startDate + "' AND '" + endDate + "' "
				+	"GROUP BY Orders.customerNumber;";
		
		return db.getRecords(sql, CustomerReport.class, hotelId);
	}
	
	public Report getDailyIncome(String hotelId, String startDate, String endDate, int inhouse){
		
		String sql = "SUM(Payment.total) AS total, "
				+ 	"SUM(Orders.numberOfGuests) AS pax, "
				+ 	"COUNT(Orders.Id) AS checks, "
				+ 	"SUM(Payment.discount) AS discount, "
				+ 	"SUM(Payment.serviceCharge) AS serviceCharge "
				+ 	"SUM(Payment.serviceTax) AS serviceTax "
				+ 	"SUM(Payment.gst) AS gst "
				+ 	"SUM(Payment.VATFOOD) AS VATFOOD "
				+ 	"SUM(Payment.VATBAR) AS VATBAR "
				+ 	"SUM(Payment.sbCess) AS sbCess "
				+ 	"SUM(Payment.kkCess) AS kkCess "
				+ 	"FROM Payment, Orders "
				+ 	"WHERE Payment.orderId = Orders.orderId "
				+ 	"AND Orders.inhouse = "+inhouse 
				+ 	"AND Orders.hotelId = '"+hotelId;
		
		if(endDate.equals("")){
			sql += "' AND Orders.orderDate ='"+startDate+"';";
		}
		else{
			sql += "' AND Orders.orderDate BETWEEN '"+startDate+"' AND '"+endDate+"';";
		}
		return db.getOneRecord(sql, Report.class, hotelId);
	}
	public ArrayList<Expense> getDailyExpense(String hotelId, String startDate, String endDate){
		
		String sql = "SELECT SUM(Expense.amount) AS amount, "
				+ 	"SUM(Expense.type) AS type "
				+ 	"FROM Expense "
				+	"WHERE hotelId = '" + hotelId;
		
		if(endDate.equals("")){
			sql += "' AND Expense.date LIKE'"+startDate+"%';";
		}
		else{
			sql += "' AND Expense.date BETWEEN '"+startDate+"' AND '"+endDate+"';";
		}
		return db.getRecords(sql, Expense.class, hotelId);
	}

	//-------------------------------------------Bank

	public ArrayList<Bank> getBankAccounts(String hotelId) {
		String sql = "SELECT * FROM Bank WHERE hotelId='"+hotelId+"';";
		return db.getRecords(sql,Bank.class, hotelId);
	}

	public int getCashBalance(String hotelId) {
		String sql = "SELECT balance as entityId FROM Bank WHERE hotelId='"+hotelId+"' AND accountNumber = "+CASH_ACCOUNT+";";
		EntityId entity =  db.getOneRecord(sql,EntityId.class, hotelId);
		
		return entity.getId();
	}

	public boolean updateCashBalance(String hotelId, int balance) {
		String sql = "UPDATE Bank SET balance = "+balance+" WHERE hotelId='"+hotelId+"' AND accountNumber = "+CASH_ACCOUNT+";";
		return db.executeUpdate(sql, true);
	}

	//-------------------------------------------Notifications

	private int getNextNotificationId(String userId, String hotelId) {
		String sql = "SELECT * FROM Notification WHERE orderId LIKE '"+userId+":%' AND hotelId = '"+hotelId+"' ORDER BY notId";
		ArrayList<Notification> notifs = db.getRecords(sql, Notification.class, hotelId);
		if (notifs.size()==0) {
			return 0;
		}
		return notifs.get(notifs.size()-1).mNotId+1;
	}
	
	public Notification getNextNotification(String hotelId, String userId) {
		String sql = "SELECT * FROM Notification WHERE hotelId='"+hotelId+"' AND orderId LIKE '"+userId+":%' ORDER BY notId";
		ArrayList<Notification> notifs = db.getRecords(sql, Notification.class, hotelId);
		if (notifs.size()==0) {
			return null;
		}
		else {
			sql = "DELETE FROM Notification WHERE notId=" +notifs.get(0).mNotId+ " AND hotelId='"+hotelId+"' AND orderId LIKE '" + userId+ ":%'";
			db.executeUpdate(sql, true);
			return notifs.get(0);
		}
	}
	public MPNotification getMPNotification(String hotelId){
		
		String sql = "SELECT COUNT(Stock.sku) AS outOfStock "
				+ "FROM Material, Stock "
				+ "WHERE Material.hotelId= '"+hotelId+"' "
				+ "AND Material.sku == Stock.sku "
				+ "AND Stock.quantity <= Material.minQuantity ";
		
		MPNotification notification = new MPNotification();
		
		notification.outOfStock = db.getOneRecord(sql, MPNotification.class, hotelId).getOutOfStock();
		notification.hotelId = hotelId;
		
		sql = "SELECT COUNT(Id) AS checkOutOrders FROM Orders WHERE hotelId = '"+hotelId+"' AND state = 1";
		
		notification.checkoutOrders = db.getOneRecord(sql, MPNotification.class, hotelId).getCheckoutOrders();
				
		return notification;
	}

	//-------------------------------------------Expenses
	public boolean addExpense(String hotelId, int expenseAmount, String details, String payeeName, int cheque, 
			String paymentType, String expenseType, String bankAccount, String userId) {
		
		String sql = "INSERT INTO Expenses " +
				"(hotelId, type, serviceDate, serviceType, amount, userId, payee, memo, chequeNo, accountName, paymentType) " +
				"VALUES('"+ escapeString(hotelId) + 
				"', '"+ escapeString(expenseType) + 
				"', '"+ getServiceDate(hotelId) + 
				"', '"+ getServiceType(hotelId) + 
				"', "+ Integer.toString(expenseAmount) + 
				", '"+ escapeString(userId) + 
				"', '"+ escapeString(payeeName) + 
				"', '"+ escapeString(details) + 
				"', "+ Integer.toString(cheque) +  
				", '"+ escapeString(bankAccount) + 
				"', '"+ escapeString(paymentType) + 
				"');";
		return db.executeUpdate(sql, true);
	}

	//-------------------------------------------Labour

	public boolean updateLabourLog(String hotelId, double salary, String employeeId, double bonus){
		
		String sql = "SELECT MAX(salaryMonth) AS entityId FROM LabourLog WHERE hotelId = '"+hotelId+"' AND employeeId = '"+employeeId+"';";
		
		int month=db.getOneRecord(sql, EntityId.class, hotelId).getId();
		if(month == 0){
			month = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()))-1;
		}
		
		sql = "INSERT INTO LabourLog " +
				"(hotelId, salary, employeeId, date, salaryMonth, bonus) " + 
				"VALUES('" + escapeString(hotelId) +
				"', " + Double.toString(salary) +
				", '" + employeeId +
				"', '" + new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) +
				"', " + Integer.toString(month+1) +
				", " + Double.toString(bonus) +
				");";
		return db.executeUpdate(sql, true);
	}

	//-------------------------------------------TotalRevenue
	public boolean addRevenue(String hotelId, String serviceType, String serviceDate, double cash, 
			double card, double app, double total, double visa, double mastercard, double maestro, double amex, double others, double mswipe, 
			double rupay, double zomato, double swiggy, double magicPin, double paytm, double difference, String reason, String clearance){
		
		String sql = "INSERT INTO TotalRevenue " +
				"(hotelId, serviceType, serviceDate, cash, card, app, total, visa, mastercard, maestro, amex, " +
				"others, mswipe, rupay, zomato, swiggy, magicPin, paytm, difference, reason, clearance) " + 
				"VALUES('" + escapeString(hotelId) +
				"', '" + escapeString(serviceType) +
				"', '" + escapeString(serviceDate) +
				"', " + Double.toString(cash) +
				", " + Double.toString(card) +
				", " + Double.toString(app) +
				", " + Double.toString(total) +
				", " + Double.toString(visa) +
				", " + Double.toString(mastercard) +
				", " + Double.toString(maestro) +
				", " + Double.toString(amex) +
				", " + Double.toString(others) +
				", " + Double.toString(mswipe) +
				", " + Double.toString(zomato) +
				", " + Double.toString(swiggy) +
				", " + Double.toString(magicPin) +
				", " + Double.toString(paytm) +
				", " + Double.toString(rupay) +
				", " + Double.toString(difference) +
				", '" + escapeString(reason) +
				"', '" + escapeString(clearance) +
				"');";
		return db.executeUpdate(sql, true);
	}
	
	//-------------------------------------------Server
	public boolean syncOnServer(String hotelId, ArrayList<String> sqlQueries){
		
		db.beginTransaction(hotelId);
		for(String sql : sqlQueries){
			if(!db.executeUpdate(sql, hotelId, false)){
				db.rollbackTransaction();
				System.out.println("Rolling back");
				return false;
			}
		}
		System.out.println("All Transaction logged Successfully");
		db.commitTransaction();
		return true;
	}
	public ServerLog getLastServerLog(String hotelId){
		
		String sql = "SELECT * FROM ServerLog WHERE hotelId = '" + escapeString(hotelId)+ "' Order by id desc Limit 1;";
		
		return db.getOneRecord(sql, ServerLog.class, hotelId);
	}
	
	public boolean updateServerLog(String hotelId){
		
		String sql = "UPDATE ServerLog SET lastUpdateTime = '" +LocalDateTime.now()+"', status = 1 WHERE hotelId = '"+hotelId+"';";
		
		return db.executeUpdate(sql, false);
	}
	
	public boolean updateServerStatus(String hotelId, Boolean updateServer){
		
		String sql = "UPDATE ServerLog SET status = 0 WHERE hotelId = '"+hotelId+"';";
		
		return db.executeUpdate(sql, updateServer);
	}
	
	public boolean createServerLog(String hotelId){
		
		String sql = "INSERT into ServerLog ('hotelId', 'lastUpdateTime', 'status') VALUES ('"
				+	escapeString(hotelId) + "','"
				+	LocalDateTime.now() + "', 1);";
				
		return db.executeUpdate(sql, true);
	}

	//-------------------------------------------Table
	
	public Boolean assignWaiterToTable(String hotelId, String waiterId, int tableId) {
		
		String sql = "UPDATE Tables SET waiterId = '"+waiterId+"' WHERE hotelId = '"+hotelId+"' AND tableId = '"+tableId+"';";
		
		return db.executeUpdate(sql, true);
	}
	
	//-------------------------------------------Loyalty
	
	public Boolean addLoyaltyOffer(String hotelId, String name, String description, int points, int offerType, String offer) {
		
		String sql = "INSERT INTO LoyaltyOffers ('hotelId', 'name', 'description', 'points', 'offerType', 'offer', 'status') VALUES ('"
				+	escapeString(hotelId) + "','"
				+	escapeString(name) + "','"
				+	escapeString(description) + "',"
				+	points + ",'"
				+	offerType + "','"
				+	escapeString(offer) + "',"
				+   0 + ");";
		
		return db.executeUpdate(sql, true);
	}
	
	public ArrayList<LoyaltyOffer> getAllLoyaltyOffers(String hotelId) {
		String sql = "SELECT * FROM LoyaltyOffers WHERE hotelID = '"+hotelId+"';";
		
		return db.getRecords(sql, LoyaltyOffer.class, hotelId);
	}
	
	public LoyaltyOffer getLoyaltyOfferById(String hotelId, int id) {
		String sql = "SELECT * FROM LoyaltyOffers WHERE Id = "+id+ " AND hotelID = '"+hotelId+"';";
		
		return db.getOneRecord(sql, LoyaltyOffer.class, hotelId);
	}
	
	public Boolean getLoyaltyOfferStatus(String hotelId, int id) {
		String sql = "SELECT status AS entityId FROM LoyaltyOffers WHERE Id = "+id+ " AND hotelID = '"+hotelId+"';";
		
		EntityId entityId = db.getOneRecord(sql, EntityId.class, hotelId);
		
		if(entityId.getId() == 0)
			return true;
		else
			return false;
	}
	
	public Boolean editLoyaltyOfferStatus(String hotelId, String id, String status) {

		String sql = "UPDATE LoyaltyOffers SET status " + status 
				+	"WHERE hotelId = '"+hotelId+ "' "
				+ 	"AND Id = "+id+");";
		
		return db.executeUpdate(sql, true);
	}
	
	public Boolean deleteLoyaltyOffer(String hotelId, int id) {
		String sql = "DELETE FROM LoyaltyOffer WHERE hotelId = '"+hotelId+"' AND id = "+id;
		
		return db.executeUpdate(sql, true);
	}
	
	public Boolean incrementOfferCount(String hotelId, int id) {
		
		int usedCount = this.getLoyaltyOfferCount(hotelId, id) + 1;
		
		String sql = "UPDATE LoyaltyOffers SET count = "+usedCount+ " WHERE Id = "+ id + " AND hotelId = '"+hotelId+"';";
		
		return db.executeUpdate(sql, true);
	}
	
	public Boolean redeemLoyaltyOffer(String hotelId, String orderId, String loyaltyId, String mobileNo, int balancePoints) {
		
		String sql = "UPDATE Customers SET points = " +balancePoints+ " WHERE mobileNo = '"+mobileNo+"' AND hotelId = '"+hotelId+"'; "
				+ "UPDATE Orders SET loyaltyId = "+loyaltyId+ " WHERE orderId = '"+orderId+"' AND hotelId = '"+hotelId+"'; ";
		
		return db.executeUpdate(sql, true);
	}
	
	public int getLoyaltyOfferCount(String hotelId, int id) {
		String sql = "SELECT count AS entityId FROM LoyaltyOffers WHERE Id = "+id+ " AND hotelID = '"+hotelId+"';";
		
		EntityId entityId = db.getOneRecord(sql, EntityId.class, hotelId);
		
		return entityId.getId();
	}
	 
	public Double getLoyaltyPoints(String hotelId, String mobileNo) {
		String sql = "SELECT points AS entityId FROM Customers WHERE mobileNo='"+mobileNo+"' AND hotelId='"+hotelId+"'";
		
		EntityDouble entity =  db.getOneRecord(sql, EntityDouble.class, hotelId);
		
		return entity.getId();
	}
	
	public Boolean addLoyaltyPoints(String hotelId, String orderId, Double points, String mobileNo) {
		
		Double loyaltyPoints = this.getLoyaltyPoints(hotelId, mobileNo) + points;
		
		String sql = "UPDATE Customers SET points="+loyaltyPoints+" WHERE mobileNo='"+escapeString(mobileNo)+"' AND "
				+ "hotelId='"+escapeString(hotelId)+"'; UPDATE Orders SET loyaltyPaid = "+points+" WHERE orderId = '"+orderId+"';";
		return db.executeUpdate(sql, true);
	}
	

	//-------------------------------------------Transaction History
	
	public Boolean updateTransactionHistory(String hotelId, String trType, String trDetail, Double amount, 
			String employeeId, String userId, String authoriser) {
		
		double accountBalance = this.getAccountBalance(hotelId, employeeId);
		if(trType.equals("CREDIT"))
			accountBalance += amount;
		else
			accountBalance -= amount;
		
		String sql = "INSERT INTO TransactionHistory ('trType', 'trDetail', 'amount', 'balance', 'trDate', 'userId', "
				+ "'authoriser', 'employeeId', 'hotelId') VALUES ('"
				+ trType + "', '"
				+ trDetail + "', "
				+ amount + ", "
				+ accountBalance + ", '"
				+ LocalDateTime.now() + "', '"
				+ userId + "', '"
				+ authoriser + "', '"
				+ employeeId + "', '"
				+ hotelId + "';";
				
		return db.executeUpdate(sql, true);
	}
	
	public Double getAccountBalance(String hotelId, String employeeId) {
		String sql = "SELECT accountBalance as entityId FROM Employees WHERE hotelId = '"+hotelId+"' AND employeeId = '"+employeeId+"'";
		
		return db.getOneRecord(sql, EntityDouble.class, hotelId).getId();
	}
	
	public IncentiveReport getIncentiveForEmployee(String hotelId, String userId, boolean isBar, String startDate, String endDate) {
		
		String sql = "SELECT SUM(OrderItems.qty*MenuItems.incentive)"
				+" AS incentive, SUM(OrderItems.rate*OrderItems.qty) AS sale,"
				+ "waiterId FROM OrderItems, MenuItems "
				+ "WHERE OrderItems.menuId == MenuItems.menuId "
				+ "AND MenuItems.hasIncentive =" + 1
				+ " AND OrderItems.waiterId = '"+userId+"'"
				+ " AND OrderItems.hotelId = '"+hotelId+"' "
				+ "AND OrderItems.hotelId == MenuItems.hotelId "
				+ "AND OrderItems.subOrderDate ";
		if(startDate.equals(endDate))
			sql += " LIKE '"+startDate+"%' ";
		else
			sql += " BETWEEN '"+startDate+"' AND '"+endDate+"' ";

		if(isBar)
			sql += "AND MenuItems.vegType = 3;";
		else
			sql += "AND MenuItems.vegType != 3";
		
		System.out.println(sql);
		IncentiveReport incentive = db.getOneRecord(sql, IncentiveReport.class, hotelId);
		
		sql = "SELECT SUM((OrderItems.qty/2)*MenuItems.incentive)"
				+" AS incentive, SUM(OrderItems.rate*OrderItems.qty) AS sale,"
				+ "waiterId FROM OrderItems, MenuItems "
				+ "WHERE OrderItems.menuId == MenuItems.menuId "
				+ "AND MenuItems.hasIncentive =" + 2
				+ " AND OrderItems.waiterId = '"+userId+"'"
				+ " AND OrderItems.hotelId ='"+hotelId+"' "
				+ "AND OrderItems.hotelId == MenuItems.hotelId "
				+ "AND OrderItems.subOrderDate ";
		if(startDate.equals(endDate))
			sql += " LIKE '"+startDate+"%' ";
		else
			sql += " BETWEEN '"+startDate+"' AND '"+endDate+"' ";

		if(isBar)
			sql += "AND MenuItems.vegType = 3;";
		else
			sql += "AND MenuItems.vegType != 3;";

		System.out.println(sql);
		IncentiveReport incentive2 = db.getOneRecord(sql, IncentiveReport.class, hotelId);
		incentive.setIncentive(incentive2.getIncentive() + incentive.getIncentive());
		incentive.setSale(incentive2.getSale() + incentive.getSale());
		
		return incentive;
	}

	public ArrayList<IncentiveReport> getItemwiseIncentiveReport(String hotelId, String userId, String startDate, String endDate) {
		
		String sql = "SELECT MenuItems.title AS title, SUM(OrderItems.qty) AS qty, " +
				"MenuItems.incentive*SUM(OrderItems.qty)  AS incentive, OrderItems.waiterId AS userId " + 
				"FROM MenuItems, OrderItems " + 
				"WHERE OrderItems.menuId == MenuItems.menuId AND MenuItems.hasIncentive == 1 AND OrderItems.waiterId = '"+userId+"' " + 
				"AND OrderItems.subOrderDate ";
		
		if(startDate.equals(endDate))
			sql += " LIKE '"+startDate+"%' ";
		else
			sql += " BETWEEN '"+startDate+"' AND '"+endDate+"' ";
		
		sql += "GROUP BY MenuItems.station, MenuItems.category, MenuItems.title;";
		
		System.out.println(sql);
		ArrayList<IncentiveReport> incentive = db.getRecords(sql, IncentiveReport.class, hotelId);
		
		sql = "SELECT MenuItems.title AS title, SUM(OrderItems.qty) AS qty, " +
				"MenuItems.incentive*(SUM(OrderItems.qty/2)) AS incentive, OrderItems.waiterId AS userId " + 
				"FROM MenuItems, OrderItems " + 
				"WHERE OrderItems.menuId == MenuItems.menuId AND MenuItems.hasIncentive == 2  AND OrderItems.waiterId = '"+userId+"' " + 
				"AND OrderItems.subOrderDate ";
		
		if(startDate.equals(endDate))
			sql += " LIKE '"+startDate+"%' ";
		else
			sql += " BETWEEN '"+startDate+"' AND '"+endDate+"' ";

		sql += "GROUP BY MenuItems.station, MenuItems.category, MenuItems.title;";
		
		System.out.println(sql);
		incentive.addAll(db.getRecords(sql, IncentiveReport.class, hotelId));
		
		return incentive;
	} 
	
	public ArrayList<EntityString> getCaptainOrderService(String hotelId, String startDate, String endDate){
		String sql = "SELECT DISTINCT waiterId AS entityId FROM OrderItems WHERE OrderItems.subOrderDate";
		if(startDate.equals(endDate))
			sql += " LIKE '"+startDate+"%';";
		else
			sql += " BETWEEN '"+startDate+"' AND '"+endDate+"';";

		System.out.println(sql);
		return db.getRecords(sql, EntityString.class, hotelId);
	}
	
	//-------------------------------------------Common
	private String getPreviousDateString(int day) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, day*(-1));    
        return dateFormat.format(cal.getTime());
	}
	public static void main(String args[] ) {
		//dynamic testcases
		AccessManager dao = new AccessManager(false);
		dao.editCustomerDetails("h0002", "Admin:83", "Martin", "9867", "", 3, "No peanuts");
		
	}
	
	public void updatePayTM(String hotelId) {
		String sql = "SELECT serviceDate AS entityId FROM ServiceLog";
		ArrayList<EntityString> serviceDates =  db.getRecords(sql, EntityString.class, hotelId);
		for (EntityString serviceDate : serviceDates) {
			sql = "UPDATE TotalRevenue SET paytm = (SELECT SUM(cardPayment) FROM Payment WHERE cardType LIKE '%PAYTM%' AND Payment.orderDate == '"+serviceDate.getEntity()+"') "
				+ "WHERE TotalRevenue.serviceDate == '"+serviceDate.getEntity()+"';";
			db.executeUpdate(sql, false);
			sql = "UPDATE TotalRevenue SET app = (SELECT SUM(cardPayment) FROM Payment WHERE (cardType LIKE '%PAYTM%' OR cardType LIKE '%ZOMATO%' " + 
				"OR cardType LIKE '%SWIGGY%' OR cardType LIKE '%MAGICPIN%' ) AND Payment.orderDate == '"+serviceDate.getEntity()+"') " + 
				"WHERE TotalRevenue.serviceDate == '"+serviceDate.getEntity()+"';";
			db.executeUpdate(sql, false);
		}
	}
	
	public void loadShortForms(String hotelId) {
		
		ArrayList<MenuItem> menuItems = this.getMenu(hotelId);
		for (MenuItem menuItem : menuItems) {
			String sql = "UPDATE MenuItems SET shortform = '"+this.generateShortForm(menuItem.getTitle())+"' WHERE menuId = '"+menuItem.getMenuId()+"';";
			db.executeUpdate(sql, false);
		}
	}
	
	//Convert DateTime string to the requested format.
	public String parseTime(String time, String format) {
		LocalDateTime now = LocalDateTime.parse(time);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
	}

	//Returns current time in the requested format.
	public String parseTime(String format) {
		LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
	}
}
