package com.orderon.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.commons.Database;
import com.orderon.commons.Database.OrderOnEntity;
import com.orderon.commons.MeasurableUnit;
import com.orderon.interfaces.IAccess;
import com.orderon.interfaces.IMenuItem;
import com.orderon.interfaces.IOutlet;
import com.orderon.interfaces.IService;
import com.orderon.interfaces.IUser;

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
/**
 * @author Marty
 *
 */
/**
 * @author Marty
 *
 */
public class AccessManager implements IAccess{
	
	public static int ORDER_STATE_SERVICE = 0;
	public static int ORDER_STATE_BILLING = 1;
	public static int ORDER_STATE_OFFKDS = 2;
	public static int ORDER_STATE_COMPLETE = 3;
	public static int ORDER_STATE_VOIDED = 99;
	public static int ORDER_STATE_CANCELED = 101;
	public static int ORDER_STATE_HIDDEN = 102;
	public static int ORDER_STATE_COMPLIMENTARY = 50;
	
	public static int SUBORDER_STATE_PENDING = 0;
	public static int SUBORDER_STATE_PROCESSING = 2;
	public static int SUBORDER_STATE_COMPLETE = 1;
	public static int SUBORDER_STATE_RECIEVED = 3;
	public static int SUBORDER_STATE_RETURNED = 100;
	public static int SUBORDER_STATE_CANCELED = 101;
	public static int SUBORDER_STATE_VOIDED = 99;
	public static int SUBORDER_STATE_COMPLIMENTARY = 50;
	
	public static int VEG = 1;
	public static int NONVEG = 2;
	public static int ALCOHOLIC = 3;
	public static int NONALCOHOLIC = 4;
	
	public static int CASH_ACCOUNT = 1;
	
	public static int MENUITEM_STATE_AVAILABLE = 1;
	public static int MENUITEM_STATE_UNAVAILABLE = 0;
	
	public static int AUTH_TOKEN = 0;
	public static int PRESENT = 1;
	public static int ABSENT = 3;
	public static int EXCUSED = 2;
	public static int AUTHORIZE = 1;
	public static int UNAUTHORIZE = 0;
	
	public static int DINE_IN = 1;
	public static int HOME_DELIVERY = 0;
	public static int TAKE_AWAY = 2;
	public static int BAR = 3;
	public static int NON_CHARGEABLE = 4;
	
	public static int PERCENTAGE_LOYALTY_OFFER = 0;
	public static int CASH_LOYALTY_OFFER = 1;
	public static int PRODUCT_LOYALTY_OFFER = 2;
	
	public static int ONLINE_ORDER_NEW = 0;
	public static int ONLINE_ORDER_ACCEPTED = 1;
	public static int ONLINE_ORDER_DELIVERED = 3;
	public static int ONLINE_ORDER_DECLINED = 2;
	public static int ONLINE_ORDER_REJECTED = 99;
	public static int ONLINE_ORDER_TIMEDOUT = 100;
	
	public static int BILLTYPE_MONTHLY_REFRESH = 4;
	public static int BILLTYPE_NUMBER_REFRESH = 3;
	public static int BILLTYPE_NUMBER = 2;
	public static int BILLTYPE_BF = 1;
	public static String GENERATE_BILL_PRE_ORDERING = "PRE_ORDERING";
	public static String GENERATE_BILL_POST_ORDERING = "POST_ORDERING";
	
	public static int DEPARTMENT_FOOD = 1;
	public static int DEPARTMENT_NON_ALCOHOLIC_BEVERAGE = 2;
	public static int DEPARTMENT_ALCOHOLIC_BEVRAGE = 3;
	
	public static int RESERVATION_STATE_CANCELLED= 4;
	public static int RESERVATION_STATE_BOOKED= 1;
	public static int RESERVATION_STATE_WAITING= 2;
	public static int RESERVATION_STATE_DELAYED= 3;
	public static int RESERVATION_STATE_SEATED= 4;
	public static int TYPE_RESERVATION= 0;
	public static int TYPE_WAITLIST= 1;
	
	public static int DISCOUNT_TYPE_FIXED = 1;
	public static int DISCOUNT_TYPE_PERCENTAGE = 0;
	public static String OFFER_TYPE_FIXED = "FIXED";
	public static String OFFER_TYPE_PERCENTAGE = "PERCENTAGE";
	public static String DISCOUNT_TYPE_DISCOUNT_CODE = "DISCOUNT_CODE";
	public static String DISCOUNT_TYPE_ZOMATO_VOUCHER = "ZOMATO_VOUCHER";
	public static String DISCOUNT_TYPE_FIXED_RUPEE_DISCOUNT = "FIXED_RUPEE_DISCOUNT";
	public static String DISCOUNT_TYPE_PIGGYBANK = "PIGGYBANK";
	public static String DISCOUNT_TYPE_DISH = "DISH";
	public static String DISCOUNT_TYPE_EWARDS = "EWARDS";
	
	public static String TABLE_TYPE_AC = "AC";	
	public static String TABLE_TYPE_NON_AC = "NON-AC";	
	
	public static String DESIGNATION_OWNER = "OWNER";	
	public static String DESIGNATION_MANAGER = "MANAGER";
	
	public static int DEDUCTION_NONE= 0;
	public static int DEDUCTION_HIDE= 1;
	public static int DEDUCTION_DELETE_MONTHLY= 2;
	public static int DEDUCTION_DELETE_DAILY= 3;
	
	public static int ONLINE_ORDERING_PORTAL_NONE= 0;
	
	public static int COUNTER_PARCEL_ORDER = 100;
	
	public static String RECIPE_CATEGORY_MANAGED = "RECIPE MANAGED";	
	public static String RECIPE_CATEGORY_NOT_MANAGED = "RECIPE NOT MANAGED";	
	public static String RECIPE_CATEGORY_OTHERS = "OTHERS";
	
	public static String MATERIAL_RAW = "RAW";	
	public static String MATERIAL_PROCESSED = "PROCESSED";	
	public static String MATERIAL_DIRECT_ITEM = "DIRECT_ITEM";
	
	public static String INVENTORY_ADDED = "ADDED";	
	public static String INVENTORY_PURCHASED = "PURCHASED";	
	public static String INVENTORY_USEDUP = "USEDUP";		
	public static String INVENTORY_SPOILAGE = "SPOILAGE";	
	public static String INVENTORY_TRANSFERED = "TRANSFERED";		
	public static String INVENTORY_DELETED = "DELETED";			
	public static String INVENTORY_RETURNED = "RETURNED";		
	
	public static String OFFER_TYPE_DISCOUNT = "DISCOUNT";	
	public static String OFFER_TYPE_BOGO = "BOGO";	
	
	public static String CUSTOMER_CREDIT_UNSETTLED = "UNSETTLED";	
	public static String CUSTOMER_CREDIT_SETTLED = "SETTLED";	
	
	public static String TRANSASCTION_CREDIT = "CREDIT";		
	public static String TRANSASCTION_DEBIT = "DEBIT";	
	
	public static int USER_AUTHENTICATION_ANY = 0;	
	public static int USER_AUTHENTICATION_TOP= 1;	
	public static int USER_AUTHENTICATION_ADMIN= 2;	
	public static int USER_AUTHENTICATION_OWNER= 3;	
	public static int USER_AUTHENTICATION_SECRET= 101;	
	public static int USER_AUTHENTICATION_KITCHEN = 10;	

	public static Pattern VALID_EMAIL_ADDRESS_REGEX = 
		Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	
	public static Pattern VALID_MOBILE_NUMBER_REGEX = 
			Pattern.compile("^[0-9+]+$", Pattern.CASE_INSENSITIVE);


	public static Pattern VALID_DATE_REGEX = 
		Pattern.compile("^(?:(?:31(-)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(-)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(-)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(-)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$", Pattern.CASE_INSENSITIVE);

	protected Database db = null;

	public AccessManager(Boolean transactionBased) {
		db = new Database(transactionBased);
	}

	protected String escapeString(String val) {
		return val.replaceAll("'", "''");
	}

	public void beginTransaction() {
		db.beginTransaction();
	}

	public void beginTransaction(String outletId) {
		db.beginTransaction(outletId);
	}

	public void commitTransaction(String outletId) {
		db.commitTransaction(outletId, false);
	}

	public void commitTransaction(String outletId, boolean isServerUpdate) {
		db.commitTransaction(outletId, isServerUpdate);
	}

	public void rollbackTransaction() {
		db.rollbackTransaction();
	}

	public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
	}
	
	public static boolean validateMobileNumber(String mobileNumber) {
		if(mobileNumber.length()<10)
			return false;
		if(mobileNumber.length()>13)
			return false;
        Matcher matcher = VALID_MOBILE_NUMBER_REGEX .matcher(mobileNumber);
        return matcher.find();
	}
	
	public static boolean validateDate(String dateStr) {
		if(dateStr.equals(""))
			return true;
        Matcher matcher = VALID_DATE_REGEX .matcher(dateStr);
        return matcher.find();
	}

	protected String generateShortForm(String title) {
		String[] sf = title.split(" ");
		StringBuilder out = new StringBuilder();

		for (int i = 0; i < sf.length; i++) {
			if (sf[i].length() >= 2)
				out.append(sf[i].substring(0, 2).toUpperCase());
		}
		return out.toString();
	}

	protected String appendEndDate(String endDate) {
		return endDate + " 23:59";
	}
	
	protected String getPreviousDateString(int day) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, day * (-1));
		return dateFormat.format(cal.getTime());
	}

	public static String filterUnitToDisplay(String unit) {

		if (unit.equals("TABLESPOONGM"))
			return "TABLESPOON (GM)";
		else if (unit.equals("TABLESPOONML"))
			return "TABLESPOON (ML)";
		else if (unit.equals("TEASPOONGM"))
			return "TEASPOON (GM)";
		else if (unit.equals("TEASPOONML"))
			return "TEASPOON (ML)";
		else
			return unit;
	}

	public static void main(String args[]) {
		// dynamic testcases
		//AccessManager dao = new AccessManager(false);
		//dao.hideOrder("h0002", "f", "", "", 25000.0);

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
	
	public static String getDiscountType(int type) {
		if(type == DISCOUNT_TYPE_PERCENTAGE) {
			return "PERCENTAGE";
		}else
			return "FIXED";
	}
	
	public String[] getEnums(Class<? extends Enum<?>> e) {
	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
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
			
		
		if(oldVersion.equals("3.3.3.27")) {

			sql = "";
			
			sql += "Update Hotel SET version = '3.4';";
		} 
		//Init Updated
		if(oldVersion.equals("3.3.3.26")) {

			sql = "ALTER TABLE Orders ADD COLUMN orderDateTime TEXT;"
					+ "ALTER TABLE Orders ADD COLUMN goldDiscount DOUBLE;";
			
			sql += "Update Hotel SET version = '3.3.3.27';";
		} else if(oldVersion.equals("3.3.3.25")) {

			sql = "ALTER TABLE Orders ADD COLUMN eWards TEXT;";
			
			sql += "Update Hotel SET version = '3.3.3.26';";
		} else if(oldVersion.equals("3.3.3.24")) {

			sql = "ALTER TABLE ReportBuffer ADD COLUMN subject TEXT;";
			
			sql += "Update Hotel SET version = '3.3.3.25';";
		} else if(oldVersion.equals("3.3.3.23")) {

			sql = "UPDATE MenuItems SET flags = '[5]' WHERE flags = '[Alcoholic Beverage]';"
				+ "UPDATE MenuItems SET flags = '[5]' WHERE flags = '[“Alcoholic Beverage”]';"
				+ "UPDATE MenuItems SET flags = '[5]' WHERE flags = '[\"Alcoholic Beverage\"]';";
			
			sql += "Update Hotel SET version = '3.3.3.24';";
		} else if(oldVersion.equals("3.3.3.22")) {

			sql =  "ALTER TABLE OrderItems ADD COLUMN botNumber INTEGER;";
			sql += "Update Hotel SET version = '3.3.3.23';";
		}else if(oldVersion.equals("3.3.3.21")) {
			
			if(hotelId.equals("vh0001")) {
				sql += "UPDATE Hotel SET senderId = 'VINTGE', smsId = 'vh0001', smsAPIKey = 'Ae67Ng_-';";
			} else if(hotelId.contains("sg000")) {
				sql += "UPDATE Hotel SET senderId = 'SPGOLD', smsId = 'springold350', smsAPIKey = 'springold@350';";
			} else if(hotelId.contains("ka0001")) {
				sql += "UPDATE Hotel SET senderId = 'KAFINE', smsId = 'ka0001', smsAPIKey = '1b@fR3Y!';";
			} 
			
			sql += "Update Hotel SET version = '3.3.3.22';";
			sql += "Update Customers SET sendSMS = 'true' WHERE sendSMS IS NULL;";
					
		}else if(oldVersion.equals("3.3.3.20")) {
			sql = "ALTER TABLE Hotel ADD COLUMN senderId;";
			
			if(hotelId.equals("h0002") || hotelId.equals("h0003")) {
				sql += "UPDATE Hotel SET senderId = 'DEMOOO';";
			} else if(hotelId.equals("wc0001")) {
				sql += "UPDATE Hotel SET senderId = 'WCOAST';";
			}
			
			sql += "Update Hotel SET version = '3.3.3.21';";
					
		}else if(oldVersion.equals("3.3.3.19")) {
			sql = "ALTER TABLE Hotel ADD COLUMN smsId;";
			
			if(hotelId.equals("wc0001")) {
				sql += "UPDATE Hotel SET smsId = 'wc00010', smsAPIKey = '3!6j_NIm';";
			}
					
			sql += "CREATE TABLE ReportBuffer ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, smsText TEXT, "
					+ "emailText TEXT, mobileNumbers TEXT DEFAULT '{}', emailIds TEXT DEFAULT '{}', outletId TEXT);";

			sql += "Update Hotel SET version = '3.3.3.20';";
					
		}else if(oldVersion.equals("3.3.3.18")) {
			sql = "DELETE FROM Flags;"
				+ " INSERT INTO Flags (Id, hotelId, name, groupId) VALUES "
				+ "(1, '"+hotelId+"', 'Vegetarian', 1), "
				+ "(2, '"+hotelId+"', 'Non-Vegetarian', 1), "
				+ "(3, '"+hotelId+"', 'Beverage', 5), "
				+ "(5, '"+hotelId+"', 'Alcoholic Beverage', 5), "
				+ "(4, '"+hotelId+"', 'Spicy', 2), "
				+ "(7, '"+hotelId+"', 'Seasonal', 3), "
				+ "(10, '"+hotelId+"', 'Chef Special', 4), "
				+ "(11, '"+hotelId+"', 'Grilled', 5), "
				+ "(12, '"+hotelId+"', 'Fried', 5), "
				+ "(13, '"+hotelId+"', ' Platter', 6), "
				+ "(14, '"+hotelId+"', 'Wheat Free', 7), "
				+ "(15, '"+hotelId+"', 'Gluten Free', 8), "
				+ "(16, '"+hotelId+"', 'Vegan', 9), "
				+ "(17, '"+hotelId+"', 'Age Restriction', 10), "
				+ "(19, '"+hotelId+"', 'Choice Item', 14), "
				+ "(20, '"+hotelId+"', 'Treat Available', 11), "
				+ "(21, '"+hotelId+"', 'Pepsi', 11), "
				+ "(22, '"+hotelId+"', 'Meal', 12), "
				+ "(23, '"+hotelId+"', 'Cake', 13), "
				+ "(24, '"+hotelId+"', 'Egg', 1), "
				+ "(25, '"+hotelId+"', 'Lipton', 11), "
				+ "(26, '"+hotelId+"', 'Turbo', 11), "
				+ "(27, '"+hotelId+"', 'Weekend Specials', 14), "
				+ "(28, '"+hotelId+"', 'Free Item', 11), "
				+ "(29, '"+hotelId+"', 'Value Week', 11), "
				+ "(30, '"+hotelId+"', 'Diet Pepsi', 11), "
				+ "(31, '"+hotelId+"', 'Exclusive Offer', 11), "
				+ "(32, '"+hotelId+"', 'Pepsi Combo', 11), "
				+ "(33, '"+hotelId+"', 'Thums Up', 11), "
				+ "(34, '"+hotelId+"', 'Sprite', 11), "
				+ "(35, '"+hotelId+"', 'Fanta', 11), "
				+ "(36, '"+hotelId+"', 'Kinley Soda', 11), "
				+ "(37, '"+hotelId+"', 'Maaza', 11), "
				+ "(38, '"+hotelId+"', 'Minute Maid', 11), "
				+ "(39, '"+hotelId+"', 'Limca', 11), "
				+ "(40, '"+hotelId+"', 'BREAKFAST', 15), "
				+ "(41, '"+hotelId+"', 'LUNCH', 16), "
				+ "(42, '"+hotelId+"', 'DINNER', 17), "
				+ "(43, '"+hotelId+"', 'PIZZA', 18), "
				+ "(44, '"+hotelId+"', 'SNACKS', 19), "
				+ "(45, '"+hotelId+"', 'NORTH_INDIAN', 20), "
				+ "(46, '"+hotelId+"', 'DESSERT', 21), "
				+ "(47, '"+hotelId+"', 'CHINESE', 22), "
				+ "(48, '"+hotelId+"', 'SOUTH_INDIAN', 23), "
				+ "(49, '"+hotelId+"', 'BURGER', 24), "
				+ "(50, '"+hotelId+"', 'BIRYANI', 25), "
				+ "(51, '"+hotelId+"', 'FAST_FOOD', 26), "
				+ "(53, '"+hotelId+"', 'PARTY COMBOS', 27), "
				+ "(54, '"+hotelId+"', 'BEVERAGES', 28), "
				+ "(55, '"+hotelId+"', 'SNACKS', 29), "
				+ "(56, '"+hotelId+"', 'BREADS', 30), "
				+ "(57, '"+hotelId+"', 'DESSERTS', 31), "
				+ "(58, '"+hotelId+"', 'RICE', 32), "
				+ "(59, '"+hotelId+"', 'SIDES', 33), "
				+ "(60, '"+hotelId+"', 'NEW', 34);";

			sql += "Update Hotel SET version = '3.3.3.19';";
		}else if(oldVersion.equals("3.3.3.17")) {
			sql = "ALTER TABLE Hotel ADD COLUMN deliverySmsEnabled TEXT; "
				+ "ALTER TABLE Hotel ADD COLUMN downloadReports TEXT; "
				+ "UPDATE Hotel SET deliverySmsEnabled = 'true', downloadReports = 'true';";
			
			sql += "Update Hotel SET version = '3.3.3.18';";
		}else if(oldVersion.equals("3.3.3.16")) {
			sql = "CREATE TABLE IF NOT EXISTS DBTransactions (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, transactions TEXT);";
			
			sql += "Update Hotel SET version = '3.3.3.17';";
		}else if(oldVersion.equals("3.3.3.15")) {
			sql += "UPDATE MenuItems SET flags = '[1]' WHERE flags = '[\"Vegetarian\"]';"
					+ "UPDATE MenuItems SET flags = '[2]' WHERE flags = '[\"Non-Vegetarian\"]';"
					+ "UPDATE MenuItems SET flags = '[3]' WHERE flags = '[\"Beverage\"]';"
					+ "UPDATE MenuItems SET flags = '[1]' WHERE flags = '[“Vegetarian”]';"
					+ "UPDATE MenuItems SET flags = '[2]' WHERE flags = '[“Non-Vegetarian”]';"
					+ "UPDATE MenuItems SET flags = '[3]' WHERE flags = '[“Beverage”]';"
					+ "UPDATE MenuItems SET flags = '[1]' WHERE flags = '[Vegetarian]';"
					+ "UPDATE MenuItems SET flags = '[2]' WHERE flags = '[Non-Vegetarian]';"
					+ "UPDATE MenuItems SET flags = '[3]' WHERE flags = '[Beverage]';"
					+ "UPDATE MenuItems SET flags = '[5]' WHERE flags = '[Alcoholic-Beverage]';"
					+ "UPDATE MenuItems SET flags = '[5]' WHERE flags = '[“Alcoholic-Beverage”]';"
					+ "UPDATE MenuItems SET flags = '[5]' WHERE flags = '[\"Alcoholic-Beverage\"]';"
					+ "UPDATE MenuItems SET title = REPLACE(title, \"'\", \"\");";

			sql += "Update Hotel SET version = '3.3.3.16';";
		}else if(oldVersion.equals("3.3.3.14")) {
			sql += "ALTER TABLE PromotionalCampaign ADD COLUMN status TEXT;";

			sql += "Update Hotel SET version = '3.3.3.15';";
		}else if(oldVersion.equals("3.3.3.13")) {
			sql += "ALTER TABLE Groups ADD COLUMN title TEXT;"
				+ "ALTER TABLE Groups ADD COLUMN subTitle TEXT;";

			sql += "Update Hotel SET version = '3.3.3.14';";
		}else if(oldVersion.equals("3.3.3.12")) {
			sql = "DELETE FROM Designations;"
				+ "INSERT INTO Designations (Id, hotelId, designation, hasIncentive) VALUES "
				+ "(0, '"+hotelId+"', 'WAITER', 'false'), "
				+ "(1, '"+hotelId+"', 'MANAGER', 'false'), "
				+ "(2, '"+hotelId+"', 'ADMINISTRATOR', 'false'), "
				+ "(3, '"+hotelId+"', 'CHEF', 'false'), "
				+ "(4, '"+hotelId+"', 'RECEPTIONIST', 'false'), "
				+ "(5, '"+hotelId+"', 'RETAILASSCOCIATE', 'false'), "
				+ "(6, '"+hotelId+"', 'BACKOFFICE', 'false'), "
				+ "(7, '"+hotelId+"', 'DELIVERYBOY', 'false'), "
				+ "(8, '"+hotelId+"', 'OWNER', 'false'), "
				+ "(9, '"+hotelId+"', 'CAPTAIN', 'false'), "
				+ "(10, '"+hotelId+"', 'CASHIER', 'false'),"
				+ "(11, '"+hotelId+"', 'HELPER', 'false'),"
				+ "(12, '"+hotelId+"', 'CLEANER', 'false'),"
				+ "(13, '"+hotelId+"', 'EXEC_CHEF', 'false'),"
				+ "(14, '"+hotelId+"', 'COMMI_1', 'false'),"
				+ "(15, '"+hotelId+"', 'COMMI_2', 'false'),"
				+ "(16, '"+hotelId+"', 'COMMI_3', 'false');";

			sql += "Update Hotel SET version = '3.3.3.13';";
			
		}else if(oldVersion.equals("3.3.3.11")) {
			
			sql = "UPDATE MenuItems set flags = replace (flags, '\"', '');";
			sql += "Update Hotel SET version = '3.3.3.12';";
			
		}else if(oldVersion.equals("3.3.3.10")) {
			sql += "ALTER TABLE MenuItems ADD COLUMN coverImgUrl TEXT;"
				+ "ALTER TABLE MenuItems ADD COLUMN isCombo TEXT; UPDATE MenuItems SET isCombo = 'false';"
				+ "ALTER TABLE MenuItems ADD COLUMN comboPrice DOUBLE DEFAULT 0.0; "
				+ "UPDATE MenuItems SET comboReducedPrice = 0.0 WHERE comboReducedPrice == NULL;"
				+ "UPDATE MenuItems SET comboPrice = 0.0 WHERE comboReducedPrice = 0.0;"
				+ "UPDATE MenuItems SET comboPrice = comboReducedPrice, isCombo = 'true' WHERE comboReducedPrice > 0;";
			
			sql += "DELETE FROM Flags;";
			sql += "INSERT INTO Flags (Id, hotelId, name, groupId) VALUES "
					+ "(1, '"+hotelId+"', 'Vegetarian', 1), "
					+ "(2, '"+hotelId+"', 'Non-Vegetarian', 1), "
					+ "(3, '"+hotelId+"', 'Beverage', 5), "
					+ "(24, '"+hotelId+"', 'Egg', 1), "
					+ "(5, '"+hotelId+"', 'Alcoholic Beverage', 5), "
					+ "(4, '"+hotelId+"', 'Spicy', 2), "
					+ "(7, '"+hotelId+"', 'Seasonal', 3), "
					+ "(10, '"+hotelId+"', 'Chef Special', 4), "
					+ "(11, '"+hotelId+"', 'Grilled', 5), "
					+ "(12, '"+hotelId+"', 'Fried', 5), "
					+ "(13, '"+hotelId+"', ' Platter', 6), "
					+ "(14, '"+hotelId+"', 'Wheat Free', 7), "
					+ "(15, '"+hotelId+"', 'Gluten Free', 8), "
					+ "(16, '"+hotelId+"', 'Vegan', 9), "
					+ "(17, '"+hotelId+"', 'Age Restriction', 10), "
					+ "(20, '"+hotelId+"', 'Treat Available', 11), "
					+ "(22, '"+hotelId+"', 'Meal', 12), "
					+ "(23, '"+hotelId+"', 'Cake', 13), "
					+ "(19, '"+hotelId+"', 'Choice Item', 14),"
					+ "(41, '"+hotelId+"', 'BREAKFAST', 15),"
					+ "(42, '"+hotelId+"', 'LUNCH', 16),"
					+ "(43, '"+hotelId+"', 'DINNER', 17),"
					+ "(48, '"+hotelId+"', 'PIZZA', 18),"
					+ "(46, '"+hotelId+"', 'SNACKS', 19),"
					+ "(44, '"+hotelId+"', 'NORTH_INDIAN', 20),"
					+ "(40, '"+hotelId+"', 'DESSERT', 21),"
					+ "(45, '"+hotelId+"', 'CHINESE', 22),"
					+ "(47, '"+hotelId+"', 'SOUTH_INDIAN', 23),"
					+ "(49, '"+hotelId+"', 'BURGER', 24),"
					+ "(50, '"+hotelId+"', 'BIRYANI', 25),"
					+ "(51, '"+hotelId+"', 'FAST_FOOD', 26),"
					+ "(53, '"+hotelId+"', 'PARTY COMBOS', 27);";

			sql += "Update Hotel SET version = '3.3.3.11';";
		}else if(oldVersion.equals("3.3.3.9")) {
			sql += "ALTER TABLE Collections ADD COLUMN tags TEXT; UPDATE Collections SET tags = '[]';";

			sql += "Update Hotel SET version = '3.3.3.10';";
		}else if(oldVersion.equals("3.3.3.8")) {
			sql += "ALTER TABLE PromotionalCampaign ADD COLUMN ageGroup TEXT;";

			sql += "Update Hotel SET version = '3.3.3.9';";
		}else if(oldVersion.equals("3.3.3.7")) {
			sql += "ALTER TABLE Hotel ADD COLUMN isWalletOffline TEXT DEFAULT 'false';";

			sql += "Update Hotel SET version = '3.3.3.8';";
		}else if(oldVersion.equals("3.3.3.6")) {
			sql += "ALTER TABLE Hotel ADD COLUMN smsAPIKey TEXT;"
				+ "ALTER TABLE Hotel ADD COLUMN promotionalSmsBalance INTEGER;"
				+ "ALTER TABLE Hotel ADD COLUMN transactionalSMSCount INTEGER;";

			if(hotelId.contains("sg")) {
				sql += "UPDATE Hotel SET smsAPIKey = '7fuq5R6qA1A-HnD9qq35P5G5MwfV7dQeE81qSvI4sZ';";
			}
			sql += "CREATE TABLE PromotionalCampaign ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, messageContent TEXT NOT NULL, "
					+ "usageDetails TEXT, totalSMSSent INTEGER, outletId TEXT, outletIds TEXT DEFAULT '[]', userTypes TEXT DEFAULT '[]', sex TEXT DEFAULT 'BOTH');";
			
			sql += "Update Hotel SET version = '3.3.3.7';";
		}else if(oldVersion.equals("3.3.3.5")) {
			sql += "UPDATE Payment set roundOff = ROUND(((cardPayment+cashPayment+appPayment+promotionalCash+creditAmount+walletPayment)-total)*100)/100;" 
				+	"UPDATE Payment set roundOff = 0 where cardType = 'VOID';"
				+	"UPDATE Payment set roundOff = 0 where cardType = 'NON_CHARGEABLE';"
				+	"ALTER TABLE PromoCode ADD COLUMN showOnApp TEXT DEFAULT 'false';";

			sql += "Update Hotel SET version = '3.3.3.6';";
		}else if(oldVersion.equals("3.3.3.4")) {
			sql += "CREATE TABLE InventoryCheckLog (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, dateTime TEXT NOT NULL, "
					+ "serviceDate TEXT NOT NULL, materialData TEXT NOT NULL, userId TEXT NOT NULL, outletId TEXT);";

			sql += "Update Hotel SET version = '3.3.3.5';";
		}else if(oldVersion.equals("3.3.3.3")) {
			
			sql += "UPDATE MenuItems SET onlineRate1 = onlineRate, onlineRate2 = onlineRate, onlineRate3 = onlineRate, "
					+ "onlineRate4 = onlineRate, onlineRate5 = onlineRate;";
			
			sql += "UPDATE OnlineOrderingPortals SET menuAssociation = 0;"
					+ "UPDATE OnlineOrderingPortals SET menuAssociation = 1 WHERE portal = 'ZOMATO';"
					+ "UPDATE OnlineOrderingPortals SET menuAssociation = 2 WHERE portal = 'SWIGGY';"
					+ "UPDATE OnlineOrderingPortals SET menuAssociation = 3 WHERE portal = 'FOODPANDA';"
					+ "UPDATE OnlineOrderingPortals SET menuAssociation = 4 WHERE portal = 'UBEREATS';"
					+ "UPDATE OnlineOrderingPortals SET menuAssociation = 5 WHERE portal = 'FOODILOO';";
			
			sql += "Update Hotel SET version = '3.3.3.4';";
		}else if(oldVersion.equals("3.3.3.2")) {
			sql += "CREATE TABLE IF NOT EXISTS Payment2 ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, billNo TEXT NOT NULL DEFAULT (null), "
					+ "orderId text NOT NULL UNIQUE, orderDate DATETIME NOT NULL DEFAULT (null), foodBill DOUBLE NOT NULL, barBill DOUBLE NOT NULL, foodDiscount DOUBLE DEFAULT (0), "
					+ "barDiscount DOUBLE, total DOUBLE NOT NULL, serviceCharge DOUBLE DEFAULT (0), serviceTax DOUBLE DEFAULT (0), VATFOOD DOUBLE DEFAULT (0), VATBAR DOUBLE DEFAULT (0), "
					+ "sbCess DOUBLE DEFAULT (0), kkCess DOUBLE DEFAULT (0), tip DOUBLE DEFAULT (0), cashPayment DOUBLE DEFAULT (0), cardPayment DOUBLE DEFAULT (0), "
					+ "appPayment DOUBLE DEFAULT (0), discountName text, cardType TEXT, gst DOUBLE, loyaltyAmount DOUBLE, complimentary DOUBLE, section TEXT, billNo2 TEXT , "
					+ "packagingCharge DOUBLE DEFAULT 0.0, deliveryCharge DOUBLE DEFAULT 0.0, walletPayment DOUBLE DEFAULT 0.0, creditAmount DOUBLE DEFAULT 0.0, "
					+ "roundOff DOUBLE DEFAULT 0.0, promotionalCash DOUBLE DEFAULT 0.0); ";
			
			sql += "INSERT INTO Payment2 (hotelId, billNo, billNo2, orderId, orderDate, foodBill, barBill, foodDiscount, barDiscount, loyaltyAmount, total, " + 
					"serviceCharge, packagingCharge, deliveryCharge, gst, VATBAR, tip, cashPayment, cardPayment, appPayment, walletPayment, " + 
					"creditAmount, promotionalCash, discountName, cardType, complimentary, section, roundOff) " + 
					"SELECT hotelId, billNo, billNo2, orderId, orderDate, foodBill, barBill, foodDiscount, barDiscount, loyaltyAmount, total, " + 
					"serviceCharge, packagingCharge, deliveryCharge, gst, VATBAR, tip, cashPayment, cardPayment, appPayment, walletPayment, " + 
					"creditAmount, promotionalCash, discountName, cardType, complimentary, section, roundOff FROM Payment; ";
			
			sql += "DROP TABLE Payment;" +
				"ALTER TABLE Payment2 RENAME TO Payment;";
			
			sql += "Update Hotel SET version = '3.3.3.3';";
		}else if(oldVersion.equals("3.3.3.1")) {
			//Delete duplicate entries in Payment table
			sql = "DELETE FROM Payment WHERE id NOT IN  (SELECT id FROM Payment GROUP BY orderId); ";

			sql += "Update Hotel SET version = '3.3.3.2';";
		}else if(oldVersion.equals("3.3.3")) {
			
			sql += "ALTER TABLE MenuItems ADD COLUMN onlineRate1 DOUBLE DEFAULT 0.0;"
				+	"ALTER TABLE MenuItems ADD COLUMN onlineRate2 DOUBLE DEFAULT 0.0;"
				+	"ALTER TABLE MenuItems ADD COLUMN onlineRate3 DOUBLE DEFAULT 0.0;"
				+	"ALTER TABLE MenuItems ADD COLUMN onlineRate4 DOUBLE DEFAULT 0.0;"
				+	"ALTER TABLE MenuItems ADD COLUMN onlineRate5 DOUBLE DEFAULT 0.0;"
				+	"ALTER TABLE OnlineOrderingPortals ADD COLUMN menuAssociation INTEGER;";
			
			sql += "Update Hotel SET version = '3.3.3.1';";
		}else if(oldVersion.equals("3.3.2.23")) {
			
			sql += "ALTER TABLE MenuItems ADD COLUMN discountType TEXT;"
				+	"ALTER TABLE MenuItems ADD COLUMN discountValue DOUBLE;";
			
			sql += "Update Hotel SET version = '3.3.3';";
		}else if(oldVersion.equals("3.3.2.22")) {
			
			sql += "ALTER TABLE Hotel ADD COLUMN capturePayments TEXT DEFAULT 'false';";
			
			if(hotelId.equals("sg0002")) {
				sql += "UPDATE Hotel SET capturePayments = 'true';";
			}else {
				sql += "UPDATE Hotel SET capturePayments = 'false';";
			}
			
			sql += "Update Hotel SET version = '3.3.2.23';";
		}else if(oldVersion.equals("3.3.2.21")) {
			
			sql = "UPDATE Customers SET communicationMode = '[]' WHERE communicationMode is null;"
				+ "Update Hotel SET version = '3.3.2.22';";
			
			System.out.println(sql);
			db.executeUpdate(sql, hotelId, true);
			System.out.println("Updating Menu Items.");

			IMenuItem dao = new MenuItemManager(false);
			ArrayList<MenuItem> menu = dao.getMenu(hotelId);
			String title = "";
			for (MenuItem menuItem : menu) {
				title = toTitleCase(menuItem.getTitle());
				sql = "UPDATE MenuItems SET title = '"+ title +"' WHERE menuId = '"+menuItem.getMenuId()+"';";
				db.executeUpdate(sql, hotelId, true);
			}
			System.out.println("Updated Menu Items.");
			return;
		}else if(oldVersion.equals("3.3.2.20")) {
			
			sql += "ALTER TABLE Collections ADD COLUMN isSpecialCombo TEXT; Update Collections SET isSpecialCombo = 'false';"
				+ "ALTER TABLE Collections ADD COLUMN isActiveOnZomato TEXT; Update Collections SET isActiveOnZomato = isActive;"
				+ "Update Collections SET isActive = 'true';"
				+ "UPDATE Hotel SET kotSettings = '600:455:2', kotFontSize = '15px', kotFontWeight = 'bold';";
			
			sql += "Update Hotel SET version = '3.3.2.21';";
		}else if(oldVersion.equals("3.3.2.19")) {
			
			sql += "ALTER TABLE Orders ADD COLUMN riderStatus TEXT;";
			
			sql += "Update Hotel SET version = '3.3.2.20';";
		}else if(oldVersion.equals("3.3.2.18")) {
			
			sql += "ALTER TABLE TransactionLog ADD COLUMN promoAmount DOUBLE DEFAULT 0.0; UPDATE TransactionLog SET promoAmount = 0.0;";
			
			sql += "Update Hotel SET version = '3.3.2.19';";
		}else if(oldVersion.equals("3.3.2.17")) {
			
			sql += "ALTER TABLE Orders ADD COLUMN promotionalCash DOUBLE DEFAULT 0.0; UPDATE Orders SET promotionalCash = 0.0;";
			
			sql += "Update Hotel SET version = '3.3.2.18';";
		}else if(oldVersion.equals("3.3.2.16")) {
			
			sql += "ALTER TABLE Customers ADD COLUMN promotionalCash DOUBLE DEFAULT 0.0; UPDATE Customers SET promotionalCash = 0;"
				+	"ALTER TABLE Payment ADD COLUMN roundOff DOUBLE DEFAULT 0.0; UPDATE Payment SET roundOff = ROUND((total-(Payment.foodBill+Payment.barBill+gst+VATBAR-foodDiscount-barDiscount))*100)/100;"
				+	"ALTER TABLE Payment ADD COLUMN promotionalCash DOUBLE DEFAULT 0.0; UPDATE Payment SET promotionalCash = 0.0;"
				+	"ALTER TABLE MenuItems ADD COLUMN gstInclusive TEXT DEFAULT 'false'; UPDATE MenuItems SET gstInclusive = 'false';";
			
			sql += "Update Hotel SET version = '3.3.2.17';";
		}else if(oldVersion.equals("3.3.2.15")) {
			
			sql += "UPDATE TransactionLog SET cashAmount = 0, cardAmount = 0, appAmount = 0, transferAmount = 0; "
				+ "UPDATE TransactionLog SET cashAmount = transAmount WHERE paymentType = 'CASH'; "
				+ "UPDATE TransactionLog SET cardAmount = transAmount WHERE paymentType = 'CARD';"
				+ "UPDATE TransactionLog SET appAmount = transAmount WHERE paymentType = 'PAYTM';"
				+ "UPDATE TransactionLog SET transferAmount = transAmount WHERE paymentType = 'TRANSFER';";
			
			sql += "Update Hotel SET version = '3.3.2.16';";
		}else if(oldVersion.equals("3.3.2.14")) {
			
			sql += "ALTER TABLE TransactionLog ADD COLUMN appAmount DOUBLE;"
				+ "ALTER TABLE TransactionLog ADD COLUMN transferAmount DOUBLE;";
			
			sql += "Update Hotel SET version = '3.3.2.15';";
			
			sql += "ALTER TABLE TransactionLog ADD COLUMN cashAmount DOUBLE;"
				+ "ALTER TABLE TransactionLog ADD COLUMN cardAmount DOUBLE;";
		}else if(oldVersion.equals("3.3.2.13")) {
			sql = "ALTER TABLE Hotel ADD COLUMN hasFullRounding TEXT; " + 
				"ALTER TABLE Customers ADD COLUMN sendSMS TEXT; UPDATE Customers SET sendSMS = 'true'; ";
			
			if(hotelId.contains("sg000")) {
				sql += "UPDATE Hotel SET hasFullRounding = 'true';";
			}else {
				sql += "UPDATE Hotel SET hasFullRounding = 'false';";
			}

			sql += "Update Hotel SET version = '3.3.2.14';";
		}else if(oldVersion.equals("3.3.2.12")) {
			
			sql = "DROP TABLE Designations;";
			
			sql += "CREATE TABLE IF NOT EXISTS Designations ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, designation TEXT UNIQUE, hasIncentive TEXT "
					+ ", hotelId TEXT);";
			
			sql += "INSERT INTO Designations (Id, hotelId, designation, hasIncentive) VALUES "
					+ "(0, '"+hotelId+"', 'WAITER', 'false'), "
					+ "(1, '"+hotelId+"', 'MANAGER', 'false'), "
					+ "(2, '"+hotelId+"', 'ADMINISTRATOR', 'false'), "
					+ "(3, '"+hotelId+"', 'CHEF', 'false'), "
					+ "(4, '"+hotelId+"', 'RECEPTIONIST', 'false'), "
					+ "(5, '"+hotelId+"', 'RETAILASSCOCIATE', 'false'), "
					+ "(6, '"+hotelId+"', 'BACKOFFICE', 'false'), "
					+ "(7, '"+hotelId+"', 'DELIVERYBOY', 'false'), "
					+ "(8, '"+hotelId+"', 'OWNER', 'false'), "
					+ "(9, '"+hotelId+"', 'CAPTAIN', 'false'), "
					+ "(10, '"+hotelId+"', 'CASHIER', 'false');";
			
			sql += "ALTER TABLE Orders ADD COLUMN walletTransactionId INTEGER;";
			
			sql += "Update Hotel SET version = '3.3.2.13';";
		}else if(oldVersion.equals("3.3.2.11")) {
			sql = "ALTER TABLE TransactionLog ADD COLUMN authorizer TEXT;"
				+ "ALTER TABLE TransactionLog ADD COLUMN reason TEXT; "
				+ "ALTER TABLE TransactionLog ADD COLUMN serviceDate TEXT; ";

			sql += "Update Hotel SET version = '3.3.2.12';";
		}else if(oldVersion.equals("3.3.2.10")) {
			sql = "ALTER TABLE Orders ADD COLUMN isFoodReady TEXT; UPDATE Orders SET isFoodReady='true'; "
				+ "ALTER TABLE OnlineOrders ADD COLUMN orderNumber INTEGER; ";

			sql += "Update Hotel SET version = '3.3.2.11';";
		}else if(oldVersion.equals("3.3.2.9")) {
			sql = "ALTER TABLE Recipe ADD COLUMN processedMaterialSku INTEGER; ";

			sql += "Update Hotel SET version = '3.3.2.10';";
		}else if(oldVersion.equals("3.3.2.8")) {
			sql = "ALTER TABLE PurchaseLog ADD COLUMN creditBalance DOUBLE; "
				+ "UPDATE PurchaseLog SET creditBalance = grandTotal WHERE paymentType = 'CREDIT'; ";

			sql += "CREATE TABLE IF NOT EXISTS VendorTransactions ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
					+ "vendorId INTEGER NOT NULL, transType TEXT NOT NULL, transAmount DOUBLE NOT NULL, paymentType TEXT NOT NULL, account TEXT NOT NULL, "
					+ "dateTime TEXT NOT NULL, paymentDate TEXT NOT NULL, userId TEXT NOT NULL, corporateId TEXT, outletId TEXT); ";
			
			sql += "Update Hotel SET version = '3.3.2.9';";
		}else if(oldVersion.equals("3.3.2.7")) {
			sql = "UPDATE Users SET userType = 12 WHERE userType = 10;" 
				+ "UPDATE Users SET userType = 10 WHERE userType = 5;"
				+ "UPDATE Users SET userType = 11 WHERE userType = 6;"
				+ "UPDATE Users SET userType = 19 WHERE userType = 7;"
				+ "UPDATE Users SET userType = 17 WHERE userType = 8;"
				+ "UPDATE Users SET userType = 18 WHERE userType = 9;"
				+ "UPDATE Users SET userType = 9 WHERE userType = 19;"
				+ "UPDATE Users SET userType = 7 WHERE userType = 17;"
				+ "UPDATE Users SET userType = 8 WHERE userType = 18;";
			
			sql += "Update Hotel SET version = '3.3.2.8';";
		}else if(oldVersion.equals("3.3.2.6")) {
			sql = "CREATE TABLE Recipe2 ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, sku INTEGER NOT NULL, menuId TEXT NOT NULL, "
					+ "quantity INTEGER NOT NULL, hotelId TEXT NOT NULL, unit TEXT NOT NULL DEFAULT GRAM, "
					+ "FOREIGN KEY(menuId) REFERENCES MenuItems(menuId), FOREIGN KEY(sku) REFERENCES Stock(sku));";
			if(!hotelId.equals("gg0001")) 
				sql += "INSERT INTO Recipe2 (id , sku, menuId, quantity , hotelId, unit) " + 
					"SELECT Id , CAST(sku AS INTEGER), menuId, quantity , hotelId, unit FROM Recipe;";
				
			sql += "DROP TABLE Recipe;" +
				"ALTER TABLE Recipe2 RENAME TO Recipe;";
			
			sql += "Update Hotel SET version = '3.3.2.7';";
		}else if(oldVersion.equals("3.3.2.5")) {
			sql = "ALTER TABLE PurchaseLog ADD COLUMN paymentType TEXT NOT NULL DEFAULT 'CASH';"
				+ "ALTER TABLE PurchaseLog ADD COLUMN account TEXT NOL NULL DEFAULT 'CASH_DRAWER';"
				+ "ALTER TABLE PurchaseLog ADD COLUMN remark TEXT;";

			sql += "Update Hotel SET version = '3.3.2.6';";
		}else if(oldVersion.equals("3.3.2.4")) {
			sql = "ALTER TABLE Hotel ADD COLUMN theme TEXT DEFAULT '0';";
			if(hotelId.equals("gg0001") || hotelId.equals("am0001"))
				sql += "UPDATE Hotel SET theme = '1';";

			sql += "Update Hotel SET version = '3.3.2.5';";
		}else if(oldVersion.equals("3.3.2.3")) {
			sql = "ALTER TABLE InventoryLog ADD COLUMN ratePerUnit DOUBLE DEFAULT 0.0;";

			sql += "Update Hotel SET version = '3.3.2.4';";
		}else if(oldVersion.equals("3.3.2.2")) {
			sql = "ALTER TABLE Hotel ADD COLUMN hasConciseBill TEXT DEFAULT 'false';";
			
			if(hotelId.equals("am0001") || hotelId.equals("cb0001") ||hotelId.equals("gg0001") ||hotelId.equals("bb0001"))
				sql += "UPDATE Hotel SET hasConciseBill = 'true';";
			else
				sql += "UPDATE Hotel SET hasConciseBill = 'false';";
			
			sql += "Update Hotel SET version = '3.3.2.3';";
		}else if(oldVersion.equals("3.3.2.1")) {
			sql = "CREATE TABLE IF NOT EXISTS PurchaseLog (purchaseId TEXT NOT NULL PRIMARY KEY UNIQUE, "
				+ "billNo TEXT, challanNo TEXT, "
				+ "vendorId INTEGER, outletId INTEGER, additionalDiscount DOUBLE, totalDiscount DOUBLE, charge DOUBLE, roundOff DOUBLE, "
				+ "totalGst DOUBLE, grandTotal DOUBLE, purchaseDate INTEGER, dateTime TEXT);";
			
			sql += "Update Hotel SET version = '3.3.2.2';";
		}else if(oldVersion.equals("3.3.2")) {
			sql = "CREATE TABLE InventoryLog2 ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, sku INTEGER NOT NULL, type TEXT NOT NULL, "
				+ "quantity DOUBLE NOT NULL, amount DOUBLE NOT NULL, outletId TEXT, orderId TEXT, menuId TEXT, balanceQuantity INTEGER, "
				+ "gst DOUBLE, gstValue INTEGER, discount DOUBLE, totalAmount DOUBLE, purchaseId INTEGER, logId TEXT DEFAULT '[]', dateTime TEXT);";
			if(!hotelId.equals("gg0001")) 
				sql += "INSERT INTO InventoryLog2 (sku, type, quantity, amount, outletId, orderId, menuId)" + 
					"SELECT sku, function, quantity, amount, outletId, orderId, menuId FROM InventoryLog;";
				
			sql += "DROP TABLE InventoryLog;" +
					"ALTER TABLE InventoryLog2 RENAME TO InventoryLog;";
			
			sql += "Update Hotel SET version = '3.3.2.1';";
		}else if(oldVersion.equals("3.3.1.9")) {
			sql = "Update Hotel SET version = '3.3.2';";
			sql += "DROP TABLE InventoryManager;";
		}else if(oldVersion.equals("3.3.1.8")) {
			sql = "ALTER TABLE Outlet ADD COLUMN closedDates TEXT DEFAULT '[]';";
			
			sql += "Update Hotel SET version = '3.3.1.9';";
		}else if(oldVersion.equals("3.3.1.7")) {
			sql = "ALTER TABLE Outlet ADD COLUMN carouselImages TEXT DEFAULT '[]';"
				+ "ALTER TABLE Outlet ADD COLUMN menuBanner TEXT;"
				+ "ALTER TABLE Outlet ADD COLUMN featuredItemId TEXT;";
			
			sql += "Update Hotel SET version = '3.3.1.8';";
		}else if(oldVersion.equals("3.3.1.6")) {
			sql = "ALTER TABLE Materials ADD COLUMN tax TEXT DEFAULT '[]'; UPDATE Materials SET tax = '[]';";
			
			sql += "Update Hotel SET version = '3.3.1.7';";
		}else if(oldVersion.equals("3.3.1.5")) {
			sql = "ALTER TABLE OrderItems ADD COLUMN itemIsMoved TEXT DEFAULT 'false';";
			
			sql += "Update Hotel SET version = '3.3.1.6';";
		}else if(oldVersion.equals("3.3.1.4")) {
			sql = "ALTER TABLE Orders ADD COLUMN amountReceivable DOUBLE;";
			
			sql += "Update Hotel SET version = '3.3.1.5';";
		}else if(oldVersion.equals("3.3.1.3")) {
			sql = "ALTER TABLE Hotel ADD COLUMN apiKey TEXT;"
				+ "ALTER TABLE OnlineOrders ADD COLUMN riderName TEXT;"
				+ "ALTER TABLE OnlineOrders ADD COLUMN riderNumber TEXT;"
				+ "ALTER TABLE OnlineOrders ADD COLUMN riderStatus TEXT;";
			
			sql += "Update Hotel SET version = '3.3.1.4';";
			
			sql += "ALTER TABLE OnlineOrders ADD COLUMN portalId TEXT;";
		}else if(oldVersion.equals("3.3.1.2")) {
			sql = "CREATE TABLE InventoryLog (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, sku INTEGER NOT NULL, " +
				"function TEXT NOT NULL, quantity DOUBLE NOT NULL, amount DOUBLE NOT NULL, outletId TEXT, orderId TEXT, menuId TEXT);" + 
				"INSERT INTO InventoryLog (sku, function, quantity, amount, outletId, orderId, menuId)" + 
				"SELECT CAST(sku AS INTEGER), crud, quantity, amount, hotelId, orderId, menuId FROM StockLog;" + 
				"DROP TABLE StockLog;";
			
			sql += "Update Hotel SET version = '3.3.1.3';";
		}else if(oldVersion.equals("3.3.1.1")) {
			sql = "ALTER TABLE ServiceLog ADD COLUMN reportForEmail TEXT;";
			
			sql += "Update Hotel SET version = '3.3.1.2';";
		}else if(oldVersion.equals("3.3")) {
			sql = "PRAGMA foreign_keys = OFF;" + 
			"CREATE TABLE Material2 ( sku INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, displayableUnit TEXT NOT NULL DEFAULT 'GRAM'," + 
			"quantity DOUBLE, measurableUnit TEXT NOT NULL, isCountable TEXT DEFAULT 'false', countableUnit TEXT, countableConversion DOUBLE, " + 
			"ratePerUnit DOUBLE NOT NULL DEFAULT 0.0, minQuantity INTEGER NOT NULL DEFAULT 1, outletId TEXT NOT NULL, materialType TEXT DEFAULT 'RAW'," + 
			"subType TEXT, state TEXT DEFAULT 'AVAILABLE', isActive TEXT DEFAULT 'true', category TEXT DEFAULT 'RECIPE MANAGED');" + 
			"INSERT INTO Material2 ( sku, name , displayableUnit, quantity, measurableUnit, isCountable, ratePerUnit, minQuantity, outletId," + 
			"materialType, state , isActive, category) " + 
			"SELECT sku, name , displayableUnit, 0, unit, 'false', ratePerUnit, minQuantity, hotelId, 'RAW', state , 'true', 'RECIPE MANAGED' FROM Material;" + 
			"DROP Table Material;" + 
			"ALTER TABLE Material2 RENAME TO Materials;" + 
			"UPDATE Materials SET quantity = (SELECT quantity FROM Stock WHERE Materials.sku == CAST(Stock.sku AS INTEGER));" + 
			"DROP Table Stock;";
			
			sql += "Update Hotel SET version = '3.3.1.1';";
		}else if(oldVersion.equals("3.2.9.12")) {
			sql = "CREATE TABLE Vendors (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, corporateId TEXT, outletId TEXT, name TEXT,"
					+ " address TEXT, poc TEXT, emailId TEXT, GSTNumber TEXT, balance DOUBLE); ";
			
			sql += "Update Hotel SET version = '3.3';";
		}else if(oldVersion.equals("3.2.9.11")) {
			sql =  "ALTER TABLE Orders ADD COLUMN excludedTaxes TEXT DEFAULT '[]'; UPDATE Orders SET excludedTaxes = '[]';"
				+ "ALTER TABLE Orders ADD COLUMN fixedRupeeDiscount DOUBLE DEFAULT 0.0;";
			
			sql += "INSERT INTO Discount (hotelId, name, description, type, foodValue, barValue, startDate, expiryDate, "
					+ "usageLimit, validCollections, offerType, applicableOnZomato, offerQuantity, bogoItems, startTime, "
					+ "endTime, minOrderAmount, firstOrderOnly) VALUES('"+hotelId+"', 'FIXED_RUPEE_DISCOUNT', '', '1', 0, 0, '01/02/2019', "
					+ "'31/12/3000', 'Unlimited', '', 'DISCOUNT', 'false', 1, '[]', '00:00:00', '23:59:45', 0, 'false');";	
			
			sql += "Update Hotel SET version = '3.2.9.12';";
		}else if(oldVersion.equals("3.2.9.10")) {
			sql = "CREATE TABLE CustomerCreditLog ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, amount DOUBLE NOT NULL, "
			+ "mobileNumber TEXT NOT NULL, outletId TEXT NOT NULL, state TEXT NOT NULL, transDate TEXT NOT NULL, "
			+ "settlementDate TEXT NOT NULL, orderId TEXT NOT NULL, paymentType TEXT);";
				
			sql += "Update Hotel SET version = '3.2.9.11';";
		}else if(oldVersion.equals("3.2.9.9")) {
			sql = "ALTER TABLE TransactionLog ADD COLUMN cashAmount TEXT;"
				+ "ALTER TABLE TransactionLog ADD COLUMN cardAmount TEXT;"
				+ "ALTER TABLE TransactionLog ADD COLUMN lastVisitDate TEXT;";
				
			sql += "Update Hotel SET version = '3.2.9.10';";
		}else if(oldVersion.equals("3.2.9.8")) {
			sql = "ALTER TABLE Customers ADD COLUMN joiningDate TEXT;"
				+ "ALTER TABLE Customers ADD COLUMN lastRechargeDate TEXT;"
				+ "ALTER TABLE Customers ADD COLUMN lastVisitDate TEXT;";
				
			sql += "Update Hotel SET version = '3.2.9.9';";
		}else if(oldVersion.equals("3.2.9.7")) {
			sql = "ALTER TABLE Orders ADD COLUMN loyaltyEarned DOUBLE;"
				+ "ALTER TABLE Payment ADD COLUMN creditAmount DOUBLE;";
				
			sql += "Update Hotel SET version = '3.2.9.8';";
		}else if(oldVersion.equals("3.2.9.6")) {
			sql = "ALTER TABLE Hotel ADD COLUMN isWalletOnline TEXT DEFAULT 'false';"
				+ "ALTER TABLE Hotel ADD COLUMN isCreditActive TEXT DEFAULT 'false';"
				+ "ALTER TABLE Hotel ADD COLUMN printLogo TEXT DEFAULT 'false';"
				+ "ALTER TABLE Customers ADD COLUMN sex TEXT DEFAULT 'MALE';";
			
			sql += "UPDATE MenuItems SET flags = '[19]' WHERE flags LIKE '%Choice Item%';";
				
			sql += "Update Hotel SET version = '3.2.9.7';";
		}else if(oldVersion.equals("3.2.9.5")) {

			sql = "INSERT INTO Outlet (id, corporateId, outletId, name, companyName, address, contact, GSTNumber, VATNumber, code) "
			+ "SELECT Id, hotelId, hotelId, hotelName, description, hotelAddress, hotelContact, GSTNumber, VATNumber, hotelCode FROM Hotel;";
			sql += "Update Hotel SET version = '3.2.9.6';";
		}else if(oldVersion.equals("3.2.9.4.2")) {
			sql = "ALTER TABLE Payment ADD COLUMN walletPayment DOUBLE DEFAULT 0.0;"
				+ "ALTER TABLE TotalRevenue ADD COLUMN wallet DOUBLE DEFAULT 0.0;";
			
			sql += "Update Hotel SET version = '3.2.9.5';";
		}else if(oldVersion.equals("3.2.9.4.1")) {

			sql = "Update Hotel SET version = '3.2.9.4.2';";
			sql += "INSERT INTO OnlineOrderingPortals (id, portal, name, requiresLogistics, commisionValue, commisionType, hasIntegration,"
					+ " hotelId, paymentCycleDay, discountsApplied) VALUES "
					+ "(6 , 'ZOMATO_PICKUP', 'Zomato Pickup', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]');";
			
		}else if(oldVersion.equals("3.2.9.4")) {
			sql = "CREATE TABLE Outlet (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, corporateId TEXT NOT NULL, "
					+ "outletId TEXT NOT NULL, name TEXT NOT NULL, companyName TEXT, address TEXT, contact TEXT, GSTNumber TEXT, VATNumber TEXT, "
					+ "code TEXT NOT NULL, imageLocation TEXT, schedule TEXT DEFAULT [], location TEXT DEFAULT [],"
					+ "links TEXT DEFAULT []);";
			sql += "ALTER TABLE TransactionLog ADD COLUMN orderId TEXT;"
				+ "ALTER TABLE TotalRevenue ADD COLUMN zomatoPickup DOUBLE;";
			
			sql += "Update Hotel SET version = '3.2.9.4.1';";
		}else if(oldVersion.equals("3.2.9.3.1")) {
			
			sql = "SELECT * FROM Employee WHERE sendSMS = 'true';";
			ArrayList<Employee> employees = db.getRecords(sql, Employee.class, hotelId);
			sql = "";
			for(Employee e : employees) {
				sql += "UPDATE Employee SET sendEODSMS = 'true' WHERE employeeId = '"+ e.getEmployeeId() + "';";
			}
			if(hotelId.equals("jp0001")) {
				sql += "UPDATE payment SET total = foodbill-foodDiscount+gst, appPayment = ROUND(foodbill-foodDiscount+gst), cardType = 'SWIGGY' WHERE payment.orderId= (SELECT orderId  FROM Orders WHERE Orders.inhouse = 2 AND Orders.takeAwaytype = 2 AND orderId = Payment.orderId) AND appPayment = 0 AND cardType != 'VOID';";
			}
			sql += "Update Hotel SET version = '3.2.9.4';";
		}else if(oldVersion.equals("3.2.9.3")) {
			sql = "ALTER TABLE Employee ADD COLUMN sendEODSMS TEXT DEFAULT false; UPDATE Employee SET sendEODSMS = 'false';";
				
			sql += "Update Hotel SET version = '3.2.9.3.1';";
		}else if(oldVersion.equals("3.2.9.2")) {
			sql = "Update Hotel SET version = '3.2.9.3';"
				+ "ALTER TABLE Customers ADD COLUMN imageLocation TEXT;"
				+ "ALTER TABLE Customers ADD COLUMN address2 TEXT;"
				+ "ALTER TABLE Customers ADD COLUMN address3 TEXT;"
				+ "ALTER TABLE MenuItems ADD COLUMN corporateId TEXT;";
			
			sql += "DROP Table CorporateId;"
				+ "CREATE TABLE Corporation ( corporateId text NOT NULL UNIQUE, businessName text NOT NULL, referrerBonus INTEGER NOT NULL DEFAULT 0, refereeBonus INTEGER NOT NULL DEFAULT 0)";
		}else if(oldVersion.equals("3.2.9.1")) {
			sql = "Update Hotel SET version = '3.2.9.2';"
				+ "ALTER TABLE TransactionLog ADD COLUMN paymentId TEXT;"
				+ "ALTER TABLE TransactionLog ADD COLUMN paymentRequestId TEXT;"
				+ "ALTER TABLE TransactionLog ADD COLUMN paymentType TEXT;"
				+ "ALTER TABLE TransactionLog ADD COLUMN transactionId INTEGER NOT NULL DEFAULT 0;";
		}else if(oldVersion.equals("3.2.9")) {
			sql = "Update Hotel SET version = '3.2.9.1';"
				+ "ALTER TABLE TotalRevenue ADD COLUMN swiggyPop DOUBLE;"
				+ "ALTER TABLE TotalRevenue ADD COLUMN googlePay DOUBLE;"
				+ "ALTER TABLE TotalRevenue ADD COLUMN magicPin DOUBLE;";
		}else if(oldVersion.equals("3.2.8.9")) {
			sql = "Update Hotel SET version = '3.2.9';"
				+ "CREATE TABLE TransactionLog ( Id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, corporateId	TEXT NOT NULL, "
				+ "outletId	TEXT NOT NULL, transDate TEXT NOT NULL, transTime TEXT NOT NULL, "
				+ "mobileNumber	TEXT NOT NULL, state TEXT NOT NULL, userId TEXT, rechargeAmount INTEGER NOT NULL, "
				+ "offerAmount INTEGER, transAmount	INTEGER NOT NULL, referralCode TEXT, promoCode INTEGER, transType TEXT NOT NULL, "
				+ "gst DOUBLE, transCharge DOUBLE);"
				+ "ALTER TABLE Customers ADD COLUMN otp INTEGER;"
				+ "ALTER TABLE Customers ADD COLUMN otpCount INTEGER;"
				+ "ALTER TABLE Customers ADD COLUMN isVerified TEXT DEFAULT 'false';"
				+ "ALTER TABLE Customers ADD COLUMN isBlocked TEXT DEFAULT 'false';"
				+ "UPDATE Customers SET isVerified = 'false', isBlocked = 'false';"
				+ "ALTER TABLE Customers ADD COLUMN pinGenTime TEXT;";
		}else if(oldVersion.equals("3.2.8.8")) {
			sql = "Update Hotel SET version = '3.2.8.9';"
				+ "ALTER TABLE Customers ADD COLUMN communicationMode TEXT;";
		}else if(oldVersion.equals("3.2.8.7")) {
			sql = "Update Hotel SET version = '3.2.8.8';"
				+ "ALTER TABLE Customers ADD COLUMN referalCode TEXT;"
				+ "ALTER TABLE Customers ADD COLUMN wallet INTEGER;"
				+ "ALTER TABLE Customers ADD COLUMN amountEarned DOUBLE;"
				+ "ALTER TABLE Customers ADD COLUMN amountSpent DOUBLE;";

			sql += "CREATE TABLE IF NOT EXISTS PromoCode ( name text NOT NULL UNIQUE, corporateId text NOT NULL, "
			+ "description text NOT NULL, offerType text NOT NULL, rechargeAmount integer NOT NULL, offerAmount integer NOT NULL, startDate text NOT NULL, expiryDate text, "
			+ "isActive TEXT DEFAULT 'true', firstTimeOnly TEXT DEFAULT 'false', showOnApp TEXT DEFAULT 'false', PRIMARY KEY(name) );";

			sql += "CREATE TABLE IF NOT EXISTS CorporateId ( corporateId text NOT NULL UNIQUE, businessName text NOT NULL, referalbonusAmount INTEGER NOT NULL );";
		}else if(oldVersion.equals("3.2.8.6")) {
			sql = "Update Hotel SET version = '3.2.8.7';"
				+ "ALTER TABLE Customers ADD COLUMN corporateId TEXT;";
			if(hotelId.equals("sg0001")) {
				sql += "Update Customers SET corporateId = 'SPRINGOLD';";
			}
		}else if(oldVersion.equals("3.2.8.5")) {
			sql = "Update Hotel SET version = '3.2.8.6';";
			sql += "ALTER TABLE Hotel ADD COLUMN drawerCode TEXT;";
			if(hotelId.equals("ka0001")) {
				sql += "Update Hotel SET drawerCode = '{ 27, 112, 0, 100, (byte) 250 }';";
			}else if(hotelId.equals("nd0001")) {
				sql += "Update Hotel SET drawerCode = '{ 27, 112, 48, 55, (byte) 121 }';";
			}
		}else if(oldVersion.equals("3.2.8.4")) {
			sql = "Update Hotel SET version = '3.2.8.5';";
			sql +=  "ALTER TABLE Groups ADD COLUMN description TEXT;";
		}else if(oldVersion.equals("3.2.8.3")) {
			sql =  "UPDATE Bank SET section = 'DEFAULT';";
			sql += "Update Hotel SET version = '3.2.8.4';";
		}else if(oldVersion.equals("3.2.8.2")) {
			sql =  "ALTER TABLE Discount ADD COLUMN maxFoodDiscountAmount DOUBLE;"
					+ "ALTER TABLE Discount ADD COLUMN maxBarDiscountAmount DOUBLE;";
			sql += "Update Hotel SET version = '3.2.8.3';";
		}else if(oldVersion.equals("3.2.8.1")) {
			sql =  "ALTER TABLE Orders ADD COLUMN cashToBeCollected DOUBLE;"
					+ "ALTER TABLE Orders ADD COLUMN zomatoVoucherAmount DOUBLE;" 
					+ "ALTER TABLE Orders ADD COLUMN piggyBank DOUBLE;";
			sql += "Update Hotel SET version = '3.2.8.2';";
		}else if(oldVersion.equals("3.2.8")) {
			sql = "INSERT INTO Discount (hotelId, name, description, type, foodValue, barValue, startDate, expiryDate, "
					+ "usageLimit, validCollections, offerType, applicableOnZomato, offerQuantity, bogoItems, startTime, "
					+ "endTime, minOrderAmount, firstOrderOnly) VALUES('"+hotelId+"', 'ZOMATO_VOUCHER', '', '1', 0, 0, '01/02/2019', "
					+ "'31/12/3000', 'Unlimited', '', 'DISCOUNT', 'false', 1, '[]', '00:00:00', '23:59:45', 0, 'false');";

			sql += "INSERT INTO Discount (hotelId, name, description, type, foodValue, barValue, startDate, expiryDate, "
					+ "usageLimit, validCollections, offerType, applicableOnZomato, offerQuantity, bogoItems, startTime, "
					+ "endTime, minOrderAmount, firstOrderOnly) VALUES('"+hotelId+"', 'PIGGYBANK', '', '1', 0, 0, '01/02/2019', "
					+ "'31/12/3000', 'Unlimited', '', 'DISCOUNT', 'false', 1, '[]', '00:00:00', '23:59:45', 0, 'false');";
					
			sql += "DELETE FROM Flags;";
			sql += "INSERT INTO Flags (Id, hotelId, name, groupId) VALUES "
					+ "(1, '"+hotelId+"', 'Vegetarian', 1), "
					+ "(2, '"+hotelId+"', 'Non-Vegetarian', 1), "
					+ "(3, '"+hotelId+"', 'Beverage', 5), "
					+ "(24, '"+hotelId+"', 'Egg', 1), "
					+ "(5, '"+hotelId+"', 'Alcoholic Beverage', 5), "
					+ "(4, '"+hotelId+"', 'Spicy', 2), "
					+ "(7, '"+hotelId+"', 'Seasonal', 3), "
					+ "(10, '"+hotelId+"', 'Chef Special', 4), "
					+ "(11, '"+hotelId+"', 'Grilled', 5), "
					+ "(12, '"+hotelId+"', 'Fried', 5), "
					+ "(13, '"+hotelId+"', ' Platter', 6), "
					+ "(14, '"+hotelId+"', 'Wheat Free', 7), "
					+ "(15, '"+hotelId+"', 'Gluten Free', 8), "
					+ "(16, '"+hotelId+"', 'Vegan', 9), "
					+ "(17, '"+hotelId+"', 'Age Restriction', 10), "
					+ "(20, '"+hotelId+"', 'Treat Available', 11), "
					+ "(22, '"+hotelId+"', 'Meal', 12), "
					+ "(23, '"+hotelId+"', 'Cake', 13), "
					+ "(19, '"+hotelId+"', 'Choice Item', 14),"
					+ "(41, '"+hotelId+"', 'BREAKFAST', 15),"
					+ "(42, '"+hotelId+"', 'LUNCH', 16),"
					+ "(43, '"+hotelId+"', 'DINNER', 17),"
					+ "(48, '"+hotelId+"', 'PIZZA', 18),"
					+ "(46, '"+hotelId+"', 'SNACKS', 19),"
					+ "(44, '"+hotelId+"', 'NORTH_INDIAN', 20),"
					+ "(40, '"+hotelId+"', 'DESSERT', 21),"
					+ "(45, '"+hotelId+"', 'CHINESE', 22),"
					+ "(47, '"+hotelId+"', 'SOUTH_INDIAN', 23),"
					+ "(49, '"+hotelId+"', 'BURGER', 24),"
					+ "(50, '"+hotelId+"', 'BIRYANI', 25),"
					+ "(51, '"+hotelId+"', 'FAST_FOOD', 26),"
					+ "(53, '"+hotelId+"', 'PARTY COMBOS', 27);";
			sql += "Update Hotel SET version = '3.2.8.1';";
		}else if(oldVersion.equals("3.2.7.11")) {
			sql = "CREATE TABLE Customers2 ( Id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId	text NOT NULL," + 
					"	mobileNumber text NOT NULL, firstName text, surName TEXT, address text, birthdate TEXT," + 
					"	anniversary	TEXT, userType TEXT, remarks TEXT , allergyInfo	TEXT, points INTEGER, wantsPromotion	TEXT," + 
					"	visitCount	TEXT, dateOfLastVisit TEXT, isPriority TEXT, emailId TEXT, reference TEXT, password BLOB," + 
					"	salt BLOB, authToken TEXT, timeStamp TEXT, isLoggedIn TEXT );";
			sql += "INSERT INTO Customers2 (Id, hotelId, mobileNumber, firstName , address, birthdate, anniversary, userType, remarks, allergyInfo, points, wantsPromotion," + 
					"visitCount, dateOfLastVisit, isPriority, emailId, reference) " +
					"SELECT Id, hotelId, mobileNo, customer , address, birthdate, anniversary, userType, remarks, allergyInfo, points, wantsPromotion," + 
					"visitCount, dateOfLastVisit, isPriority, emailId, reference FROM Customers;" + 
					"DROP TABLE Customers; " + 
					"ALTER TABLE Customers2 RENAME TO Customers;";
			sql += "Update Hotel SET version = '3.2.8';";
		}else if(oldVersion.equals("3.2.7.10")) {
			sql = "CREATE TABLE IF NOT EXISTS Sections (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, kitchenPrinter TEXT, barPrinter TEXT, "
					+ "summaryPrinter TEXT, beveragePrinter TEXT, outdoorPrinter TEXT, cashierPrinter INTEGER, hotelId TEXT );";
			sql += "INSERT INTO Sections (id, name, kitchenPrinter, barPrinter, summaryPrinter, beveragePrinter, outdoorPrinter, cashierPrinter, hotelId) VALUES "
					+ "(1 , 'DEFAULT', 'Kitchen', 'Kitchen', 'Kitchen', 'Kitchen', 'Kitchen', 'Kitchen', '"+hotelId+"');";
			sql += "Update Hotel SET version = '3.2.7.11';";
		}else if(oldVersion.equals("3.2.7.9")) {
			sql = "update payment set complimentary = foodBill +barBill, gst = 0, total=0, foodDiscount = 0, barDiscount = 0, serviceCharge = 0, vatBar = 0 where cardType = 'NON_CHARGEABLE';";

			sql += "Update Hotel SET version = '3.2.7.10';";
		}else if(oldVersion.equals("3.2.7.8")) {
			sql =  "ALTER TABLE Discount ADD COLUMN offerType TEXT; UPDATE Discount SET offerType = '"+OFFER_TYPE_DISCOUNT+"';"
				+  "ALTER TABLE Discount ADD COLUMN applicableOnZomato TEXT DEFAULT 'false'; UPDATE Discount SET applicableOnZomato = 'false';"
				+  "ALTER TABLE Discount ADD COLUMN isActive TEXT DEFAULT 'true'; UPDATE Discount SET isActive = 'true';"
				+  "ALTER TABLE Discount ADD COLUMN offerQuantity INTEGER DEFAULT 0; UPDATE Discount SET offerQuantity = 0;"
				+  "ALTER TABLE Discount ADD COLUMN startTime TEXT;"
				+  "ALTER TABLE Discount ADD COLUMN endTime TEXT;"
				+  "ALTER TABLE Discount ADD COLUMN firstOrderOnly TEXT DEFAULT 'false'; UPDATE Discount SET firstOrderOnly = 'false';"
				+  "ALTER TABLE Discount ADD COLUMN minOrderAmount INTEGER DEFAULT 0; UPDATE Discount SET minOrderAmount = 0;"
				+  "ALTER TABLE Discount ADD COLUMN bogoItems TEXT DEFAULT '[]'; UPDATE Discount SET bogoItems = '[]';";

			sql += "Update Hotel SET version = '3.2.7.9';";
		}else if(oldVersion.equals("3.2.7.7")) {
			sql =  "ALTER TABLE Orders ADD COLUMN externalOrderId TEXT;"
				+	"ALTER TABLE Orders ADD COLUMN riderName TEXT;"
				+	"ALTER TABLE Orders ADD COLUMN riderNumber TEXT;"
				+	"ALTER TABLE Charges ADD COLUMN isApplicableOnline TEXT; UPDATE Charges SET isApplicableOnline = 'false';"
				+   "UPDATE Orders SET excludedCharges = '[]' WHERE excludedCharges is null;";
			
			sql += "CREATE TABLE IF NOT EXISTS OnlineOrderingPortals ( id INTEGER NOT NULL UNIQUE, portal TEXT NOT NULL, name TEXT NOT NULL, requiresLogistics TEXT NOT NULL, "
					+ "commisionValue DOUBLE, commisionType TEXT, hasIntegration TEXT, hotelId TEXT, paymentCycleDay TEXT, discountsApplied TEXT, PRIMARY KEY(id) );";

			sql += "INSERT INTO OnlineOrderingPortals (id, portal, name, requiresLogistics, commisionValue, commisionType, hasIntegration,"
					+ " hotelId, paymentCycleDay, discountsApplied, menuAssociation) VALUES "
					+ "(0 , 'NONE', 'none', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]', 0), "
					+ "(1 , 'ZOMATO', 'Zomato', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]', 1), "
					+ "(2 , 'SWIGGY', 'Swiggy', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]', 2), "
					+ "(3 , 'FOODPANDA', 'Food Panda', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]', 3), "
					+ "(4 , 'UBEREATS', 'Uber Eats', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]', 4), "
					+ "(5 , 'FOODILOO', 'Foodiloo', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]', 5), "
					+ "(6 , 'ZOMATO PICKUP', 'Zomato Pickup', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]', 1), "
					+ "(100 , 'COUNTER', 'Counter Parcel', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]', 0);";
			
			sql += "Update Hotel SET version = '3.2.7.8';";
		}else if(oldVersion.equals("3.2.7.6")) {
			sql =  "ALTER TABLE Customers ADD COLUMN emailId TEXT;"
				+	"ALTER TABLE Customers ADD COLUMN reference TEXT;"
				+	"ALTER TABLE Orders ADD COLUMN excludedCharges TEXT DEFAULT '[]';";

			sql += "Update Hotel SET version = '3.2.7.7';";
		}else if(oldVersion.equals("3.2.7.5")) {
			//SQLLITE CONCATE OPERATION ||
			sql =  "ALTER TABLE OrderItems ADD COLUMN kotNumber INTEGER;";

			sql += "Update Hotel SET version = '3.2.7.6';";
		}else if(oldVersion.equals("3.2.7.4")) {
			//SQLLITE CONCATE OPERATION ||
			sql = "UPDATE Orders set discountCode = '[]' WHERE discountCode is null;"
				+ "UPDATE Payment set discountName = '[]' WHERE discountName = '';"
				+ "UPDATE Orders set discountCode = '[\"' || discountCode || '\"]' WHERE discountCode not like '[%';"
				+ "UPDATE Payment set discountName = '[\"' || discountName || '\"]' WHERE discountName not like '[%';";

			sql += "Update Hotel SET version = '3.2.7.5';";
		}else if(oldVersion.equals("3.2.7.3")) {
			sql = "ALTER TABLE Employee ADD COLUMN sendOperationalEmail TEXT DEFAULT false; UPDATE Employee SET sendOperationalEmail = 'false';"
				+ "ALTER TABLE Employee ADD COLUMN sendEODEmail TEXT DEFAULT false; UPDATE Employee SET sendEODEmail = 'false';"
				+ "ALTER TABLE Employee ADD COLUMN sendSMS TEXT DEFAULT false; UPDATE Employee SET sendSMS = 'false';";

			sql += "Update Hotel SET version = '3.2.7.4';";
		}else if(oldVersion.equals("3.2.7.2")) {
			sql = "CREATE TABLE OrderAddOns2 ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, orderId TEXT NOT NULL, subOrderId INTEGER NOT NULL, " + 
					"qty INTEGER NOT NULL, menuId TEXT NOT NULL, addOnId TEXT NOT NULL, rate INTEGER NOT NULL, itemId INTEGER, state TEXT, subOrderDate TEXT );" + 
					"INSERT INTO OrderAddOns2 (Id, hotelId, orderId, subOrderId, qty, menuId, addOnId, rate, itemId, state, subOrderDate) SELECT Id, hotelId, orderId, subOrderId, " +
					"qty, menuId, addOnId, rate, itemId, state, subOrderDate FROM OrderAddOns;" + 
					"DROP TABLE OrderAddOns; " + 
					"ALTER TABLE OrderAddOns2 RENAME TO OrderAddOns;" + 
					"CREATE TABLE OrderAddOnLog2 ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, orderId TEXT NOT NULL, " +
					"subOrderId TEXT NOT NULL, menuId TEXT NOT NULL, itemId INTEGER NOT NULL, quantity INTEGER NOT NULL, rate INTEGER NOT NULL, state TEXT, addOnId TEXT, subOrderDate TEXT );" + 
					"INSERT INTO OrderAddOnLog2 (Id, hotelId, orderId, subOrderId, menuId, itemId, quantity, rate, state, addOnId, subOrderDate) " +
					"SELECT Id, hotelId, orderId, subOrderId, menuId, itemId, quantity, rate, state, addOnId, subOrderDate FROM OrderAddOnLog;" + 
					"DROP TABLE OrderAddOnLog;" + 
					"ALTER TABLE OrderAddOnLog2 RENAME TO OrderAddOnLog;";

			sql += "Update Hotel SET version = '3.2.7.3';";
		}else if(oldVersion.equals("3.2.7.1")) {
			sql = "ALTER TABLE Material ADD COLUMN materialType TEXT DEFAULT 'INVENTORY_MANAGED';"
					+ "ALTER TABLE Material ADD COLUMN state INTEGER DEFAULT 1;"
					+ " UPDATE Material SET materialType = 'INVENTORY_MANAGED', "
					+ "state = 1;";

			sql += "Update Hotel SET version = '3.2.7.2';";
		}else if(oldVersion.equals("3.2.7")) {
			sql = "ALTER TABLE ServiceLog ADD COLUMN deductionState INTEGER DEFAULT 0;";

			sql += "Update Hotel SET version = '3.2.7.1';";
		}else if(oldVersion.equals("3.2.6")) {
			sql = "ALTER TABLE Hotel ADD COLUMN deductionType INTEGER DEFAULT 0;";

			sql += "Update Hotel SET version = '3.2.7';";
		}else if(oldVersion.equals("3.2.5")) {
			sql = "ALTER TABLE Charges ADD COLUMN isApplicableOn TEXT DEFAULT '[]';"
				+ "ALTER TABLE Hotel ADD COLUMN VATNumber TEXT DEFAULT '';";

			sql += "Update Hotel SET version = '3.2.6';";
		}else if(oldVersion.equals("3.2.4")) {
			sql = "ALTER TABLE StockLog ADD COLUMN orderId TEXT;"
				+ "ALTER TABLE StockLog ADD COLUMN menuId TEXT;";

			sql += "Update Hotel SET version = '3.2.5';";
		}else if(oldVersion.equals("3.2.3")) {
			sql = "ALTER TABLE Payment ADD COLUMN packagingCharge DOUBLE DEFAULT 0.0; UPDATE Payment SET packagingCharge = 0.0;"
				+ "ALTER TABLE Payment ADD COLUMN deliveryCharge DOUBLE DEFAULT 0.0; UPDATE Payment SET deliveryCharge = 0.0;"
				+ "ALTER TABLE Taxes ADD COLUMN applicableOn TEXT DEFAULT 'FOOD'; UPDATE Taxes SET applicableOn = 'FOOD';";
			

			sql += "Update Hotel SET version = '3.2.4';";
		}else if(oldVersion.equals("3.2.2")) {
			this.initDatabase(hotelId);

			sql = "INSERT INTO Designations (Id, hotelId, designation, hasIncentive) VALUES "
					+ "(1, '"+hotelId+"', 'WAITER', 'false'), "
					+ "(2, '"+hotelId+"', 'MANAGER', 'false'), "
					+ "(3, '"+hotelId+"', 'ADMINISTRATOR', 'false'), "
					+ "(4, '"+hotelId+"', 'CHEF', 'false'), "
					+ "(5, '"+hotelId+"', 'RECEPTIONIST', 'false'), "
					+ "(6, '"+hotelId+"', 'RETAILASSCOCIATE', 'false'), "
					+ "(7, '"+hotelId+"', 'BACKOFFICE', 'false'), "
					+ "(8, '"+hotelId+"', 'DELIVERYBOY', 'false'), "
					+ "(9, '"+hotelId+"', 'OWNER', 'false'), "
					+ "(10, '"+hotelId+"', 'CAPTAIN', 'false'), "
					+ "(11, '"+hotelId+"', 'CASHIER', 'false');";
			
			sql += "DELETE FROM Flags;"
					+ " INSERT INTO Flags (Id, hotelId, name, groupId) VALUES "
					+ "(1, '"+hotelId+"', 'Vegetarian', 1), "
					+ "(2, '"+hotelId+"', 'Non-Vegetarian', 1), "
					+ "(3, '"+hotelId+"', 'Beverage', 5), "
					+ "(5, '"+hotelId+"', 'Alcoholic Beverage', 5), "
					+ "(4, '"+hotelId+"', 'Spicy', 2), "
					+ "(7, '"+hotelId+"', 'Seasonal', 3), "
					+ "(10, '"+hotelId+"', 'Chef Special', 4), "
					+ "(11, '"+hotelId+"', 'Grilled', 5), "
					+ "(12, '"+hotelId+"', 'Fried', 5), "
					+ "(13, '"+hotelId+"', ' Platter', 6), "
					+ "(14, '"+hotelId+"', 'Wheat Free', 7), "
					+ "(15, '"+hotelId+"', 'Gluten Free', 8), "
					+ "(16, '"+hotelId+"', 'Vegan', 9), "
					+ "(17, '"+hotelId+"', 'Age Restriction', 10), "
					+ "(19, '"+hotelId+"', 'Choice Item', 14), "
					+ "(20, '"+hotelId+"', 'Treat Available', 11), "
					+ "(21, '"+hotelId+"', 'Pepsi', 11), "
					+ "(22, '"+hotelId+"', 'Meal', 12), "
					+ "(23, '"+hotelId+"', 'Cake', 13), "
					+ "(24, '"+hotelId+"', 'Egg', 1), "
					+ "(25, '"+hotelId+"', 'Lipton', 11), "
					+ "(26, '"+hotelId+"', 'Turbo', 11), "
					+ "(27, '"+hotelId+"', 'Weekend Specials', 14), "
					+ "(28, '"+hotelId+"', 'Free Item', 11), "
					+ "(29, '"+hotelId+"', 'Value Week', 11), "
					+ "(30, '"+hotelId+"', 'Diet Pepsi', 11), "
					+ "(31, '"+hotelId+"', 'Exclusive Offer', 11), "
					+ "(32, '"+hotelId+"', 'Pepsi Combo', 11), "
					+ "(33, '"+hotelId+"', 'Thums Up', 11), "
					+ "(34, '"+hotelId+"', 'Sprite', 11), "
					+ "(35, '"+hotelId+"', 'Fanta', 11), "
					+ "(36, '"+hotelId+"', 'Kinley Soda', 11), "
					+ "(37, '"+hotelId+"', 'Maaza', 11), "
					+ "(38, '"+hotelId+"', 'Minute Maid', 11), "
					+ "(39, '"+hotelId+"', 'Limca', 11), "
					+ "(40, '"+hotelId+"', 'BREAKFAST', 15), "
					+ "(41, '"+hotelId+"', 'LUNCH', 16), "
					+ "(42, '"+hotelId+"', 'DINNER', 17), "
					+ "(43, '"+hotelId+"', 'PIZZA', 18), "
					+ "(44, '"+hotelId+"', 'SNACKS', 19), "
					+ "(45, '"+hotelId+"', 'NORTH_INDIAN', 20), "
					+ "(46, '"+hotelId+"', 'DESSERT', 21), "
					+ "(47, '"+hotelId+"', 'CHINESE', 22), "
					+ "(48, '"+hotelId+"', 'SOUTH_INDIAN', 23), "
					+ "(49, '"+hotelId+"', 'BURGER', 24), "
					+ "(50, '"+hotelId+"', 'BIRYANI', 25), "
					+ "(51, '"+hotelId+"', 'FAST_FOOD', 26), "
					+ "(53, '"+hotelId+"', 'PARTY COMBOS', 27),"
					+ "(54, '"+hotelId+"', 'BEVERAGES', 28), "
					+ "(55, '"+hotelId+"', 'SNACKS', 29), "
					+ "(56, '"+hotelId+"', 'BREADS', 30), "
					+ "(57, '"+hotelId+"', 'DESSERTS', 31), "
					+ "(58, '"+hotelId+"', 'RICE', 32), "
					+ "(59, '"+hotelId+"', 'SIDES', 33), "
					+ "(60, '"+hotelId+"', 'NEW', 34);";

			sql += "INSERT INTO Taxes (Id, hotelId, name, value, type, isActive) VALUES "
					+ "(1, '"+hotelId+"', 'CGST', 2.5, 'PERCENTAGE', 'true'), "
					+ "(2, '"+hotelId+"', 'SGST', 2.5, 'PERCENTAGE', 'true'); ";
			
			sql += "ALTER TABLE Hotel ADD COLUMN isCaptainBasedOrdering TEXT; UPDATE Hotel SET isCaptainBasedOrdering = 'true';"
					+  "ALTER TABLE Hotel ADD COLUMN showOccupiedTablesOnly TEXT; UPDATE Hotel SET showOccupiedTablesOnly = 'false';"
					+  "ALTER TABLE Tables ADD COLUMN subTables TEXT;";
			
			sql += "UPDATE MenuItems SET flags = '[2]' WHERE vegType ==2 AND flags == '';"
				+ "UPDATE MenuItems SET flags = '[1]' WHERE vegType ==1 AND flags == '';"
				+ "UPDATE MenuItems SET flags = '[5]' WHERE vegType ==3 AND flags == '';"
				+ "UPDATE MenuItems SET flags = '[3]' WHERE vegType ==4 AND flags == '';"
				+ "UPDATE MenuItems SET flags = '[2, 19]' WHERE vegType ==2 AND flags LIKE '%ci%';"
				+ "UPDATE MenuItems SET flags = '[1 ,19]' WHERE vegType ==1 AND flags LIKE '%ci%';"
				+ "UPDATE MenuItems SET flags = '[5, 19]' WHERE vegType ==3 AND flags LIKE '%ci%';"
				+ "UPDATE MenuItems SET flags = '[1 ,19]' WHERE vegType ==4 AND flags LIKE '%ci%';";
			
			sql += "UPDATE MenuItems SET taxes = '[1,2]' WHERE isTaxable ==0;"
				+	"UPDATE MenuItems SET taxes = '[]' WHERE isTaxable ==1;"
				+	"UPDATE MenuItems SET charges = '[]';"
				+	"UPDATE MenuItems SET groups = '[]';";
			
			sql += "Update Hotel SET version = '3.2.3';";
		}else if(oldVersion.equals("3.2.1")) {
			
			sql = "ALTER TABLE Tables ADD COLUMN type TEXT DEFAULT 'AC'; UPDATE Tables SET type = 'AC';"
				+ "ALTER TABLE Tables ADD COLUMN showTableView TEXT DEFAULT 'true'; UPDATE Tables SET showTableView = 'true';"
				+ "ALTER TABLE Hotel ADD COLUMN hasNewOrderScreen TEXT DEFAULT 'true'; UPDATE Hotel SET hasNewOrderScreen = 'true';";
					
			sql += "Update Hotel SET version = '3.2.2';";
		}else if(oldVersion.equals("3.2")) {
			
			sql = "ALTER TABLE Collections ADD COLUMN description TEXT;" +
					"ALTER TABLE Collections ADD COLUMN imgUrl TEXT;" +
					"ALTER TABLE MenuItems ADD COLUMN syncOnZomato TEXT;" +
					"ALTER TABLE Charges ADD COLUMN orderType TEXT;" +
					"ALTER TABLE SubCollections ADD COLUMN description TEXT;" +
					"ALTER TABLE Specifications ADD COLUMN isDisplayable TEXT; UPDATE Specifications SET isDisplayable = 'true' WHERE id < 21;"
					+ " UPDATE Specifications SET isDisplayable = 'false' WHERE id > 20; ";

			sql += "ALTER TABLE Collections ADD COLUMN collectionOrder INTEGER; update Collections set collectionOrder = id;" + 
					"ALTER TABLE Collections ADD COLUMN hasSubCollection TEXT NOT NULL DEFAULT 'false';" +
					"ALTER TABLE Collections ADD COLUMN isActive TEXT NOT NULL DEFAULT 'true';" +
					"ALTER TABLE Collections ADD COLUMN name TEXT; update Collections set name = collection;" +
					"ALTER TABLE Collections ADD COLUMN scheduleIds TEXT;";
			sql += "CREATE TABLE Collections1 ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, name text NOT NULL, "
					+ "image TEXT DEFAULT (null), collectionOrder INTEGER NOT NULL, hasSubCollection TEXT NOT NULL, isActive TEXT NOT NULL, scheduleIds TEXT ); ";
			sql += "INSERT INTO Collections1 (hotelId, name, image, collectionOrder, hasSubCollection, isActive, scheduleIds) " +
					"SELECT hotelId, collection, image, collectionOrder, hasSubCollection, isActive, scheduleIds FROM Collections; " +
					"DROP TABLE Collections; " +
					"ALTER TABLE Collections1 RENAME TO Collections; ";
			sql += "Update Hotel SET version = '3.2.1';";
		}else if(oldVersion.equals("3.1")) {
			
			sql = "ALTER TABLE MenuItems ADD COLUMN subCollection INTEGER;" +
					"ALTER TABLE MenuItems ADD COLUMN groups TEXT; ALTER TABLE MenuItems ADD COLUMN taxes TEXT; ALTER TABLE MenuItems ADD COLUMN charges TEXT;" +
					"ALTER TABLE MenuItems ADD COLUMN isRecomended TEXT DEFAULT 'false'; ALTER TABLE MenuItems ADD COLUMN isTreats TEXT DEFAULT 'false'; " +
					"ALTER TABLE MenuItems ADD COLUMN isBogo TEXT DEFAULT 'false'; ALTER TABLE MenuItems ADD COLUMN isDefault TEXT DEFAULT 'false'; " +
					"ALTER TABLE MenuItems ADD COLUMN isAddOn TEXT NOT NULL DEFAULT 'false';" +
					"ALTER TABLE MenuItems ADD COLUMN comboReducedPrice DOUBLE DEFAULT 0.0;";
			sql += "CREATE TABLE IF NOT EXISTS MenuItems1 ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, station TEXT NOT NULL, " +
					"menuId text NOT NULL UNIQUE, title text NOT NULL, description text, collection text NOT NULL, subCollection TEXT, " +
					"flags text, preparationTime integer, deliveryRate DOUBLE NOT NULL, dineInRate DOUBLE NOT NULL DEFAULT 0, dineInNonAcRate DOUBLE NOT NULL DEFAULT 0, onlineRate DOUBLE, " +
					"costPrice DOUBLE DEFAULT Null, vegType int NOT NULL, method TEXT, state INTEGER NOT NULL, code TEXT, addOns TEXT, " +
					"img text, hasIncentive INTEGER, incentive INTEGER, isTaxable INTEGER NOT NULL, groups TEXT, taxes TEXT, charges TEXT, " +
					"isRecomended TEXT DEFAULT 'false', isTreats TEXT DEFAULT 'false', isDefault TEXT DEFAULT 'false', isBogo TEXT DEFAULT 'false', comboReducedPrice DOUBLE, isAddOn TEXT NOT NULL DEFAULT 'false'); "; 
			sql += "INSERT INTO MenuItems1 (Id, hotelId, station, menuId, title, description, collection, subCollection, flags, preparationTime, deliveryRate, " +
					"dineInRate, onlineRate, costPrice, vegType, method, state, code, addOns, img, hasIncentive, incentive, isTaxable, groups, taxes, " +
					"charges, isRecomended, isTreats, isDefault, isBogo, comboReducedPrice, isAddOn) " +
					"SELECT Id, hotelId, station, menuId, title, description, category, subCollection, flags, preparationTime, rate, " +
					"inHouseRate, onlineRate, costPrice, vegType, method, state, shortForm, addOns, img, hasIncentive, incentive, isTaxable, groups, taxes, charges, " +
					"isRecomended, isTreats, isDefault, isBogo, comboReducedPrice, isAddOn FROM MenuItems; " +
					"DROP TABLE MenuItems; " +
					"ALTER TABLE MenuItems1 RENAME TO MenuItems;";
			sql += "Update Hotel SET version = '3.2';";
		}else if(oldVersion.equals("3")) {
			
			sql = "CREATE TABLE IF NOT EXISTS SubCollections ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL,"
					+ " subCollectionOrder INTEGER NOT NULL, collection TEXT NOT NULL, hotelId INTEGER NOT NULL, isActive TEXT); ";
			
			sql += "UPDATE MenuItems SET state = 1; Update Hotel SET version = '3.1';";
		}else if(oldVersion.equals("2.11")) {
			sql += "CREATE TABLE Charges ( sr INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, amount DOUBLE NOT NULL, amount2 INTEGER, amount3 INTEGER );"
					+ " Update Hotel SET version = '3';";
		}else if(oldVersion.equals("2.1")) {
			sql += "ALTER TABLE Customers ADD COLUMN isPriority TEXT; update Customers set isPriority = 'false';" +
				"Update Hotel SET version = '2.11';";
		}else if(oldVersion.equals("2") || oldVersion.equals("2.0")) {
			sql = "ALTER TABLE TotalRevenue ADD COLUMN zomatoPay DOUBLE; update TotalRevenue set zomatoPay = 0.0;" + 
				"ALTER TABLE TotalRevenue ADD COLUMN nearBy DOUBLE; update TotalRevenue set nearBy = 0.0;" +
				"Update Hotel SET version = '2.1';";
		}else if(oldVersion.equals("1.02 rev.10023")) {
			sql = "ALTER TABLE Payment ADD COLUMN appPayment BigDecimal; update Payment set appPayment = 0.0 where appPayment isNull;" + 
				"update Payment set appPayment= cardpayment, cardpayment=0.0 where (cardType LIKE '%ZOMATO%') OR (cardType LIKE '%SWIGGY%') " +
				"OR (cardType LIKE '%DINEOUT%') OR (cardType LIKE '%PAYTM%') OR (cardType LIKE '%FOODPANDA%') OR (cardType LIKE '%UBEREATS%') " + 
				" OR (cardType LIKE '%FOODILOO%');"
				+ "update Payment set complimentary = 0.0 where complimentary isnull;"
				+ "update Payment set loyaltyAmount = 0.0 where complimentary isnull;"
				+ "Update Hotel SET version = '2.0';";
		}else if(oldVersion.equals("1.01A rev.10023") || oldVersion.equals("")) {
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
		db.executeUpdate(sql, hotelId, true);
	}

	public void initDatabase(String hotelId) {
		
		String sql = "CREATE TABLE IF NOT EXISTS Users (  Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, userId text NOT NULL, "
		+ "userPasswd BLOB NOT NULL, employeeId text NOT NULL, userType integer NOT NULL, authToken text, timeStamp text, salt BLOB,"
	 	+ "UNIQUE(hotelId,userId,employeeId),"
	 	+ "FOREIGN KEY(employeeId) REFERENCES Employee(employeeId));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Designations ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, designation TEXT UNIQUE, hasIncentive TEXT "
		+ ", hotelId TEXT);";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS TransactionHistory ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, trType TEXT NOT NULL, "
		+ "trDetail TEXT NOT NULL, amount DOUBLE NOT NULL, balance DOUBLE NOT NULL, trDate TEXT NOT NULL, userId TEXT, authoriser TEXT, "
		+ "employeeId TEXT NOT NULL, hotelId TEXT, paymentType TEXT, trAccountName TEXT, serviceDate TEXT);";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS SubCollections ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, "
		+ "subCollectionOrder INTEGER NOT NULL, collection TEXT NOT NULL, hotelId INTEGER NOT NULL, isActive TEXT , "
		+ "description TEXT);";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Schedules ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, days TEXT NOT NULL, " +
		"timeSlots INTEGER NOT NULL, hotelId TEXT NOT NULL );";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Groups ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, itemIds TEXT NOT NULL, name TEXT NOT NULL, "
		+ "description TEXT, max INTEGER NOT NULL, min INTEGER NOT NULL, hotelId TEXT, isActive TEXT NOT NULL DEFAULT 'true', "
		+ "title TEXT, subTitle TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS TotalRevenue ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, "
		+ "serviceDate TEXT NOT NULL, serviceType TEXT NOT NULL, cash DOUBLE NOT NULL, card DOUBLE NOT NULL, app DOUBLE, visa DOUBLE, "
		+ "mastercard DOUBLE, maestro DOUBLE, amex DOUBLE, rupay DOUBLE, others DOUBLE, difference DOUBLE DEFAULT (null), reason TEXT, "
		+ "total DOUBLE NOT NULL, clearance TEXT, zomato DOUBLE DEFAULT (null), swiggy DOUBLE DEFAULT (null), dineOut DOUBLE DEFAULT (null), "
		+ "mswipe DOUBLE, paytm DOUBLE, complimentary DOUBLE, section TEXT, foodiloo DOUBLE, uberEats DOUBLE, foodPanda DOUBLE, "
		+ "deductedCash DOUBLE, cash2 DOUBLE, zomatoPay DOUBLE, nearBy DOUBLE, swiggyPop DOUBLE, googlePay DOUBLE, magicPin DOUBLE, "
		+ "zomatoPickup DOUBLE, wallet DOUBLE);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Tables ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, tableId text NOT NULL, state integer, "
		+ "hotelId TEXT NOT NULL, section TEXT, type TEXT, showTableView TEXT, waiterId TEXT, subTables TEXT );";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Taxes ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name INTEGER NOT NULL, value DOUBLE NOT NULL, "
		+ "type TEXT NOT NULL, isActive TEXT NOT NULL DEFAULT 'true', applicableOn TEXT NOT NULL DEFAULT 'FOOD', hotelId TEXT NOT NULL );";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Flags ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, "
		+ "groupId INTEGER NOT NULL, hotelId TEXT NOT NULL );";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Tiers ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, value DOUBLE NOT NULL, "
		+ "chargeAlwaysApplicable TEXT NOT NULL DEFAULT 'true', minBillAmount DOUBLE, hotelId TEXT NOT NULL );";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Charges ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, type TEXT NOT NULL, value DOUBLE NOT NULL, "
		+ "isActive TEXT NOT NULL DEFAULT 'false', applicableOn TEXT, isAlwaysApplicable INTEGER DEFAULT 'false', minBillAmount DOUBLE, "
		+ "hasTierWiseValues TEXT, taxesOnCharge TEXT, hotelId TEXT NOT NULL, orderType TEXT, isApplicableOn TEXT DEFAULT '[]', isApplicableOnline TEXT );";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS InventoryLog ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, sku INTEGER NOT NULL, type TEXT NOT NULL, "
		+ "quantity DOUBLE NOT NULL, ratePerUnit DOUBLE NOT NULL, amount DOUBLE NOT NULL, outletId TEXT, orderId TEXT, menuId TEXT, balanceQuantity INTEGER, "
		+ "gst DOUBLE, gstValue INTEGER, discount DOUBLE, totalAmount DOUBLE, purchaseId INTEGER, logId TEXT DEFAULT '[]', dateTime TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Stations ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, station text NOT NULL,"
	 	+ "UNIQUE(hotelId,station));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Specifications ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, specification TEXT NOT NULL, station TEXT, "
		+ "type INTEGER NOT NULL, isDisplayable TEXT DEFAULT 'true');";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS ServiceLog ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, serviceDate TEXT NOT NULL, "
		+ "startTimeStamp TEXT NOT NULL, endTimeStamp TEXT NOT NULL, serviceType TEXT NOT NULL, isCurrent INTEGER NOT NULL, cashInHand INTEGER, smsEmailSent TEXT,"
		+ " deductionState INTEGER, reportForEmail TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS ServerLog ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, lastUpdateTime TEXT, hotelId TEXT, status INTEGER);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Roles ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, role TEXT, clearanceLevel INTEGER, hotelId TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS RoleAccess ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, role INTEGER, page INTEGER, read INTEGER, "
		+ "write INTEGER, deleteAccess INTEGER, hotelId INTEGER);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE Recipe ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, sku INTEGER NOT NULL, menuId TEXT NOT NULL, "
		+ "quantity INTEGER NOT NULL, hotelId TEXT NOT NULL, unit TEXT NOT NULL DEFAULT GRAM, processedMaterialSku INTEGER, "
		+ "FOREIGN KEY(menuId) REFERENCES MenuItems(menuId), FOREIGN KEY(sku) REFERENCES Stock(sku))";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Payment ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, billNo TEXT NOT NULL DEFAULT (null), "
		+ "orderId text NOT NULL UNIQUE, orderDate DATETIME NOT NULL DEFAULT (null), foodBill DOUBLE NOT NULL, barBill DOUBLE NOT NULL, foodDiscount DOUBLE DEFAULT (0), "
		+ "barDiscount DOUBLE, total DOUBLE NOT NULL, serviceCharge DOUBLE DEFAULT (0), serviceTax DOUBLE DEFAULT (0), VATFOOD DOUBLE DEFAULT (0), VATBAR DOUBLE DEFAULT (0), "
		+ "sbCess DOUBLE DEFAULT (0), kkCess DOUBLE DEFAULT (0), tip DOUBLE DEFAULT (0), cashPayment DOUBLE DEFAULT (0), cardPayment DOUBLE DEFAULT (0), "
		+ "appPayment DOUBLE DEFAULT (0), discountName text, cardType TEXT, gst DOUBLE, loyaltyAmount DOUBLE, complimentary DOUBLE, section TEXT, billNo2 TEXT , "
		+ "packagingCharge DOUBLE DEFAULT 0.0, deliveryCharge DOUBLE DEFAULT 0.0, walletPayment DOUBLE DEFAULT 0.0, creditAmount DOUBLE DEFAULT 0.0, "
		+ "roundOff DOUBLE DEFAULT 0.0, promotionalCash DOUBLE DEFAULT 0.0);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Orders ( Id  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId  text NOT NULL, orderId  text NOT NULL UNIQUE, "
		+ "orderDate text NOT NULL, orderDateTime TEXT, customerName  text, customerAddress  text, customerNumber  text, rating_ambiance  integer, rating_qof  "
		+ "integer, rating_service  integer, rating_hygiene  integer, waiterId  text, numberOfGuests  integer, state  integer NOT NULL, "
		+ "inhouse  integer NOT NULL, tableId  text, reviewSuggestions  TEXT, serviceType  TEXT NOT NULL, foodBill  DOUBLE DEFAULT (null), "
		+ "barBill  DOUBLE DEFAULT (null), billNo  INTEGER, billNo2  TEXT, reason  TEXT, authId  TEXT, printCount  INTEGER DEFAULT (0), "
		+ "discountCode  TEXT, isSmsSent  INTEGER, completeTimestamp  TEXT, loyaltyId  INTEGER, loyaltyPaid  INTEGER, section  TEXT, "
		+ "reference  TEXT, remarks  TEXT, deliveryBoy  TEXT, deliveryTimeStamp  TEXT, customerGST  TEXT, takeAwaytype integer, "
		+ "excludedCharges TEXT DEFAULT '[]', excludedTaxes TEXT DEFAULT '[]', externalOrderId TEXT, riderName TEXT, riderNumber TEXT, cashToBeCollected TEXT, "
		+ "zomatoVoucherAmount DOUBLE, goldDiscount DOUBLE, piggyBank DOUBLE, loyaltyEarned DOUBLE, fixedRupeeDiscount DOUBLE, amountReceivable DOUBLE, isFoodReady TEXT, "
		+ "walletTransactionId INTEGER, eWards TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderTables ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, orderId text NOT NULL, "
		+ "tableId text NOT NULL);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderSpecifications ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, orderId TEXT NOT NULL, "
		+ "subOrderId TEXT NOT NULL, menuId TEXT NOT NULL, itemId INTEGER NOT NULL, specification TEXT NOT NULL, hotelId TEXT NOT NULL);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderItems ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, subOrderId text NOT NULL, "
		+ "subOrderDate text NOT NULL, orderId text NOT NULL, menuId text NOT NULL, qty int NOT NULL, rate real NOT NULL, specs text, state integer, "
		+ "billNo TEXT, isKotPrinted INTEGER, waiterId TEXT, billNo2 TEXT, kotNumber INTEGER, botNumber INTEGER, itemIsMoved TEXT DEFAULT 'false');";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderItemLog ( Id INTEGER NOT NULL, hotelId TEXT NOT NULL, orderId TEXT NOT NULL DEFAULT (null), "
		+ "subOrderId TEXT NOT NULL, menuId TEXT NOT NULL, state INTEGER NOT NULL, reason TEXT, dateTime TEXT, quantity INTEGER, rate INTEGER, "
		+ "itemId INTEGER, subOrderDate TEXT,"
	 	+ "PRIMARY KEY(Id));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderAddOns ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, orderId TEXT NOT NULL, "
		+ "subOrderId INTEGER NOT NULL, qty INTEGER NOT NULL, menuId TEXT NOT NULL, addOnId TEXT NOT NULL, rate INTEGER NOT NULL, itemId INTEGER, "
		+ "state TEXT, subOrderDate TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OrderAddOnLog ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, orderId TEXT NOT NULL, "
		+ "subOrderId TEXT NOT NULL, menuId TEXT NOT NULL, itemId INTEGER NOT NULL, quantity INTEGER NOT NULL, rate INTEGER NOT NULL, state TEXT, "
		+ "addOnId TEXT NOT NULL, subOrderDate TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS OnlineOrders ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT, restaurantId INTEGER, "
		+ "orderId TEXT, orderNumber INTEGER, externalOrderId INTEGER, data TEXT, status INTEGER, dateTime INTEGER, portalId INTEGER, riderName TEXT, riderNumber TEXT, riderStatus TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Notification ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, orderId text NOT NULL, "
		+ "notId int NOT NULL, msg text NOT NULL);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS MenuItems ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId TEXT NOT NULL, station TEXT NOT NULL, "
		+ "menuId text NOT NULL UNIQUE, title text NOT NULL, description text, collection text NOT NULL, subCollection TEXT, flags text, "
		+ "preparationTime integer, deliveryRate DOUBLE NOT NULL, dineInRate DOUBLE NOT NULL DEFAULT 0, dineInNonAcRate DOUBLE NOT NULL DEFAULT 0, onlineRate DOUBLE, "
		+ "costPrice DOUBLE DEFAULT Null, vegType int NOT NULL, method TEXT, state INTEGER NOT NULL, code TEXT, addOns TEXT, "
		+ "img text, hasIncentive INTEGER, incentive INTEGER, isTaxable INTEGER NOT NULL, groups TEXT, taxes TEXT, charges TEXT, "
		+ "isRecomended INTEGER, isTreats INTEGER, isDefault INTEGER, isBogo INTEGER, comboReducedPrice DOUBLE, isAddOn TEXT NOT NULL, "
		+ "syncOnZomato TEXT DEFAULT 'true', corporateId TEXT NOT NULL, gstInclusive TEXT DEFAULT 'false', discountType TEXT, discountValue DOUBLE,"
		+ "onlineRate1 DOUBLE DEFAULT 0.0, onlineRate2 DOUBLE DEFAULT 0.0, onlineRate3 DOUBLE DEFAULT 0.0, onlineRate4 DOUBLE DEFAULT 0.0,"
		+ "onlineRate5 DOUBLE DEFAULT 0.0, comboPrice DOUBLE DEFAULT 0.0, isCombo TEXT DEFAULT 'false', coverImgUrl TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Materials ( sku INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, displayableUnit TEXT NOT NULL DEFAULT 'GRAM'," + 
		"quantity DOUBLE, measurableUnit TEXT NOT NULL DEFAULT 'GRAM', isCountable TEXT DEFAULT 'false', countableUnit TEXT, countableConversion DOUBLE, " + 
		"ratePerUnit DOUBLE NOT NULL DEFAULT 0.0, minQuantity INTEGER NOT NULL DEFAULT 1, outletId TEXT NOT NULL, materialType TEXT DEFAULT 'RAW'," + 
		"subType TEXT, state TEXT DEFAULT '1', isActive TEXT DEFAULT 'true', category TEXT DEFAULT 'RECIPE MANAGED', tax TEXT DEFAULT '[]');";
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

		sql = "CREATE TABLE IF NOT EXISTS Hotel ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, hotelCode text NOT NULL, "
		+ "hotelName text NOT NULL, isEnabled int NOT NULL, hotelAddress TEXT, hotelContact TEXT, GSTNumber TEXT, "
		+ "hotelType TEXT DEFAULT 'POSTPAID', description TEXT, website TEXT, smsEnabled INTEGER DEFAULT 1, serverEnabled INTEGER DEFAULT 0, "
		+ "hasCashDrawer INTEGER DEFAULT 0, hasLoyalty INTEGER DEFAULT 0, hasIncentiveScheme INTEGER DEFAULT 0, billType TEXT DEFAULT '2:POST_ORDERING', "
		+ "sections TEXT, integrations TEXT, onlinePlatforms TEXT, kotIHTBNSZSUF TEXT DEFAULT '1:1:1:1:1:0:1:1:1:1', kotSettings TEXT DEFAULT '600:455:2', "
		+ "hasKds TEXT DEFAULT 'false', hasKot TEXT DEFAULT 'true', hasDirectCheckout TEXT DEFAULT 'false', hasNC TEXT DEFAULT 'true', hasBar TEXT DEFAULT 'true', "
		+ "loadCustomerDb TEXT DEFAULT 'true', isMenuIdCategorySpecific TEXT DEFAULT 'false', allowItemCancellationOnPhone TEXT DEFAULT 'false', kotFontFamily TEXT DEFAULT 'Arial', "
		+ "kotFontSize TEXT DEFAULT '15px', kotFontWeight TEXT DEFAULT 'bold', hasEod TEXT DEFAULT 'false', version TEXT, hasNewOrderScreen TEXT DEFAULT 'true', "
		+ "isCaptainbasedOrdering TEXT DEFAULT 'true', deductionType INTEGER default (0), VATNumber TEXT, isWalletOnline TEXT DEFAULT 'false', isWalletOffline TEXT DEFAULT 'false', isCreditActive TEXT DEFAULT 'false',"
		+ "printLogo TEXT DEFAULT 'false', showOccupiedTablesOnly TEXT DEFAULT 'false', apiKey TEXT, hasConciseBill TEXT DEFAULT 'true', theme TEXT DEFAULT '0', "
		+ "hasFullRounding TEXT DEFAULT 'false', capturePayments TEXT DEFAULT 'false', promotionalSMSBalance INTEGER DEFAULT 0, transactionalSMSCount INTEGER DEFAULT 0,"
		+ "smsAPIKey TEXT, downloadReports TEXT DEFAULT 'true', deliverySmsEnabled TEXT DEFAULT 'false', smsId TEXT, senderId TEXT);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Expenses ( id INTEGER NOT NULL DEFAULT (0) PRIMARY KEY AUTOINCREMENT UNIQUE, type TEXT NOT NULL, "
		+ "serviceDate TEXT NOT NULL DEFAULT (CURRENT_TIMESTAMP), amount INTEGER NOT NULL DEFAULT (0), userId TEXT NOT NULL, payee TEXT, "
		+ "memo TEXT, chequeNo INTEGER, accountName TEXT DEFAULT (null), paymentType TEXT DEFAULT (null), hotelId TEXT DEFAULT (null), "
		+ "serviceType TEXT, section TEXT, employeeId TEXT, invoiceNumber INTEGER);";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Employee ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, employeeId text NOT NULL, "
		+ "firstName text NOT NULL, surName text NOT NULL, address text, contactNumber text, dob text, sex text, hiringDate text, designation text DEFAULT (null), "
		+ "salary int, bonus int, image TEXT DEFAULT (Null), middleName TEXT, email TEXT, accountBalance INTEGER , "
		+ "sendOperationalEmail TEXT DEFAULT false, sendEODEmail TEXT DEFAULT false, sendSMS TEXT DEFAULT 'false', sendEODSMS TEXT DEFAULT 'false');";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Discount ( name text NOT NULL UNIQUE, hotelId text NOT NULL, description text NOT NULL, type text NOT NULL, "
		+ "foodValue integer NOT NULL, startDate text NOT NULL, expiryDate text, usageLimit text NOT NULL, validCollections TEXT, "
		+ "barValue INTEGER, offerType TEXT, applicableOnZomato TEXT DEFAULT 'false', isActive TEXT DEFAULT 'true', "
		+ "offerQuantity INTEGER DEFAULT 0, bogoItems TEXT DEFAULT '[]', startTime TEXT, endTime TEXT, firstorderOnly TEXT, "
		+ "minOrderAmount INTEGER, maxFoodDiscountAmount DOUBLE, maxBarDiscountAmount DOUBLE, PRIMARY KEY(name) );";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Customers ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, mobileNumber text NOT NULL UNIQUE, "
		+ "firstName text, surName TEXT, address text, birthdate TEXT, anniversary TEXT, userType TEXT, remarks TEXT, allergyInfo TEXT, points INTEGER, "
		+ "wantsPromotion TEXT, visitCount TEXT, dateOfLastVisit TEXT, isPriority TEXT, emailId TEXT, reference TEXT, password BLOB, salt BLOB, authToken TEXT, "
		+ "timeStamp TEXT, isLoggedIn TEXT, corporateId TEXT, referalCode TEXT, wallet INTEGER, promotionalCash INTEGER, amountEarned DOUBLE, amountSpent DOUBLE, communicationMode TEXT DEFAULT '[]', "
		+ "otp INTEGER, otpCount INTEGER, isVerified TEXT DEFAULT 'false', isBlocked TEXT DEFAULT 'false', pinGenTime TEXT, address2 TEXT, address3 TEXT, sex TEXT"
		+ ", joiningDate TEXT, lastVisitDate TEXT, lastRechargeDate TEXT, sendSMS TEXT)";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS PromoCode ( name text NOT NULL UNIQUE, corporateId text NOT NULL, "
		+ "description text NOT NULL, offerType text NOT NULL, rechargeAmount integer NOT NULL, offerAmount integer NOT NULL, startDate text NOT NULL, expiryDate text, "
		+ "isActive TEXT DEFAULT 'true', firstTimeOnly TEXT DEFAULT 'false', PRIMARY KEY(name) );";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Corporation ( corporateId text NOT NULL UNIQUE, businessName text NOT NULL, referrerBonus INTEGER NOT NULL DEFAULT 0, refereeBonus INTEGER NOT NULL DEFAULT 0)";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Collections ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, "
		+ "name text NOT NULL UNIQUE, image TEXT DEFAULT (null), collectionOrder INTEGER NOT NULL, hasSubCollection TEXT NOT NULL, "
		+ "isActive TEXT NOT NULL, scheduleIds TEXT , description TEXT, imgUrl TEXT, isSpecialCombo TEXT, isActiveOnZomato TEXT, tags TEXT DEFAULT '[]');";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Bank ( accountNumber INTEGER NOT NULL UNIQUE, bankName TEXT, accountName TEXT NOT NULL UNIQUE, "
		+ "balance INTEGER NOT NULL DEFAULT 0, hotelId TEXT, section TEXT,"
	 	+ "PRIMARY KEY(accountNumber));";
		db.executeUpdate(sql, hotelId, false);

		sql = "CREATE TABLE IF NOT EXISTS Attendance ( Id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, hotelId text NOT NULL, employeeId text NOT NULL, "
		+ "checkInTime text NOT NULL, checkOutTime text, checkInDate text NOT NULL, checkOutDate text, reason text, authorisation INTEGER, isPresent INTEGER, shift INTEGER,"
	 	+ "FOREIGN KEY(employeeId) REFERENCES Employee(employeeId));";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Reservations ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
		+ "customerId INTEGER NOT NULL, maleCount INTEGER NOT NULL, femaleCount INTEGER NOT NULL, "
		+ "childrenCount INTEGER NOT NULL, bookingTime TEXT NOT NULL, bookingDate TEXT NOT NULL, timeStamp TEXT NOT NULL, state INTEGER NOT NULL, "
		+ "type INTEGER NOT NULL, orderId TEXT UNIQUE, hotelId TEXT NOT NULL, foreign key(customerId) references Customers(id) );";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS OnlineOrderingPortals ( id INTEGER NOT NULL UNIQUE, portal TEXT NOT NULL, name TEXT NOT NULL, requiresLogistics TEXT NOT NULL, "
		+ "commisionValue DOUBLE, commisionType TEXT, hasIntegration TEXT, hotelId TEXT, paymentCycleDay TEXT, discountsApplied TEXT, "
		+ "menuAssociation INTEGER, PRIMARY KEY(id));";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Sections (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, kitchenPrinter TEXT, barPrinter TEXT, summaryPrinter TEXT, beveragePrinter TEXT, outdoorPrinter TEXT, cashierPrinter INTEGER, hotelId TEXT );";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS TransactionLog ( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, corporateId TEXT NOT NULL, outletId TEXT NOT NULL, "
		+ "transDate TEXT NOT NULL, transTime TEXT NOT NULL, mobileNumber TEXT NOT NULL, state TEXT NOT NULL, userId TEXT, rechargeAmount INTEGER NOT NULL, "
		+ "offerAmount INTEGER, transAmount INTEGER NOT NULL, referralCode TEXT, promoCode INTEGER, transType TEXT NOT NULL, gst DOUBLE, transCharge DOUBLE, "
		+ "paymentId TEXT, paymentStatus TEXT, paymentRequestId TEXT, transactionId INTEGER NOT NULL UNIQUE, paymentType TEXT, orderId TEXT, "
		+ "authorizer TEXT, reason TEXT, serviceDate TEXT , cashAmount DOUBLE DEFAULT 0.0, cardAmount DOUBLE DEFAULT 0.0, appAmount DOUBLE DEFAULT 0.0);";
		db.executeUpdate(sql, hotelId, false);
		// Create all other tables here...
		
		sql = "CREATE TABLE IF NOT EXISTS CustomerCreditLog ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, amount DOUBLE NOT NULL, "
		+ "mobileNumber TEXT NOT NULL, outletId TEXT NOT NULL, state TEXT NOT NULL, transDate TEXT NOT NULL, "
		+ "settlementDate TEXT NOT NULL, orderId TEXT NOT NULL, paymentType TEXT);";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Vendors (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, corporateId TEXT, outletId TEXT, name TEXT,"
		+ " address TEXT, poc TEXT, emailId TEXT, GSTNumber TEXT, balance DOUBLE)";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS Outlet ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, corporateId TEXT, outletId TEXT NOT NULL, "
		+ "name TEXT NOT NULL, address TEXT, contact TEXT, GSTNumber TEXT, VATNumber TEXT, code TEXT NOT NULL, imageLocation TEXT, schedule TEXT DEFAULT '[]', "
		+ "location TEXT DEFAULT '{\"place\":\"\"}', links TEXT, companyName TEXT, carouselImages TEXT DEFAULT '[]', menuBanner TEXT, featuredItemId TEXT, closeDates TEXT DEFAULT '[]');";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS PurchaseLog (purchaseId TEXT NOT NULL PRIMARY KEY UNIQUE, billNo TEXT, challanNo TEXT, "
		+ "vendorId INTEGER, outletId TEXT, additionalDiscount DOUBLE, totalDiscount DOUBLE, charge DOUBLE, roundOff DOUBLE, "
		+ "totalGst DOUBLE, grandTotal DOUBLE, purchaseDate TEXT, dateTime TEXT, paymentType TEXT NOT NULL DEFAULT 'CASH', "
		+ "account TEXT NOT NULL DEFAULT 'CASH_DRAWER', remark TEXT, creditBalance DOUBLE);";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS VendorTransactions ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
		+ "vendorId INTEGER NOT NULL, transType TEXT NOT NULL, transAmount DOUBLE NOT NULL, paymentType TEXT NOT NULL, account TEXT NOT NULL, "
		+ "dateTime TEXT NOT NULL, paymentDate TEXT NOT NULL, userId TEXT NOT NULL, corporateId TEXT, outletId TEXT)";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS InventoryCheckLog (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, dateTime TEXT NOT NULL, "
		+ "serviceDate TEXT NOT NULL, materialData TEXT NOT NULL, userId TEXT NOT NULL, outletId TEXT );";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS PromotionalCampaign ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT NOT NULL, "
		+ "messageContent TEXT NOT NULL, usageDetails TEXT, totalSMSSent INTEGER, outletId TEXT, outletIds TEXT DEFAULT '[]', "
		+ "userTypes TEXT DEFAULT '[]', sex TEXT DEFAULT 'BOTH', ageGroup TEXT DEFAULT '[]', status TEXT);";
		db.executeUpdate(sql, hotelId, false);
		
		sql = "CREATE TABLE IF NOT EXISTS DBTransactions (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, transactions TEXT);";
		db.executeUpdate(sql, hotelId, false);
		
		sql += "CREATE TABLE IF NOT EXISTS ReportBuffer ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, smsText TEXT, "
		+ "subject TEXT, emailText TEXT, mobileNumbers TEXT DEFAULT '{}', emailIds TEXT DEFAULT '{}', outletId TEXT)";
		db.executeUpdate(sql, hotelId, false);
	}
	
	public JSONObject restaurantSetup(String hotelName, String hotelCode, String hotelId, String version) {
		
		hotelCode = escapeString(hotelCode);
		hotelId = escapeString(hotelId);
		hotelName = escapeString(hotelName);
		
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			String sql = "INSERT INTO Hotel (hotelId, hotelCode, hotelName, isEnabled, version) "
					+ "VALUES ('" + hotelId + "', '" + hotelCode + "', '" + hotelName + "', 1, '"+ version + "');";
			db.executeUpdate(sql, false);
			
			sql = "INSERT INTO Outlet (outletId, code, name, corporateId) "
					+ "VALUES ('" + hotelId + "', '" + hotelCode + "', '" + hotelName + "', '');";
			db.executeUpdate(sql, false);
			
			sql = "INSERT INTO BANK (accountNumber, bankName, accountName, balance, hotelId, section) VALUES "
				+ "(1, 'CASH', 'CASH_DRAWER', 0, '"+hotelId+"', 'DEFAULT');";
			db.executeUpdate(sql, false);
			
			sql = "INSERT INTO Employee "
				+ "(hotelId, employeeId, firstName, surName, address, contactNumber, dob, sex, hiringDate, designation"
				+ ", salary, bonus, image, middleName, email) VALUES"
				+ "('" + hotelId + "', '"+hotelCode+"001', 'Order', 'On', ' ', '', '01/01/1992', 'Male', '01/01/2018', 'ADMINISTRATOR', 0,0,'','',''),"
				+ "('" + hotelId + "', '"+hotelCode+"002', 'Staff', ' ', ' ', '', '', '', '', 'CAPTAIN', 0,0,'','',''),"
				+ "('" + hotelId + "', '"+hotelCode+"003', 'Delivery', ' ', ' ', '', '', '', '', 'DELIVERYBOY', 0,0,'','','');";
			db.executeUpdate(sql, false);
			
			sql = "INSERT INTO LoyaltySettings (hotelId, userType, requiredPoints, pointToRupee) VALUES ('" 
				+ hotelId + "', 'Prime', 0, 10), ('" + hotelId + "', 'Premium', 20000, 10), ('" + hotelId + "', 'Elite', 40000, 10), ('"
				+ hotelId + "', 'All', 0, 0);";
			db.executeUpdate(sql, false);
			
			sql = "INSERT INTO SERVERLOG (hotelId, status) VALUES "
					+ "('"+hotelId+"', 0);";
			db.executeUpdate(sql, false);
	
			sql = "INSERT INTO STATIONS (hotelId, station) VALUES "
					+ "('"+hotelId+"', 'Kitchen'), "
					+ "('"+hotelId+"', 'Bar Non-Alcoholic'), "
					+ "('"+hotelId+"', 'Beverage');";
			db.executeUpdate(sql, false);
	
			sql = "INSERT INTO Designations (Id, hotelId, designation, hasIncentive) VALUES "
					+ "(0, '"+hotelId+"', 'WAITER', 'false'), "
					+ "(1, '"+hotelId+"', 'MANAGER', 'false'), "
					+ "(2, '"+hotelId+"', 'ADMINISTRATOR', 'false'), "
					+ "(3, '"+hotelId+"', 'CHEF', 'false'), "
					+ "(4, '"+hotelId+"', 'RECEPTIONIST', 'false'), "
					+ "(5, '"+hotelId+"', 'RETAILASSCOCIATE', 'false'), "
					+ "(6, '"+hotelId+"', 'BACKOFFICE', 'false'), "
					+ "(7, '"+hotelId+"', 'DELIVERYBOY', 'false'), "
					+ "(8, '"+hotelId+"', 'OWNER', 'false'), "
					+ "(9, '"+hotelId+"', 'CAPTAIN', 'false'), "
					+ "(10, '"+hotelId+"', 'CASHIER', 'false'),"
					+ "(11, '"+hotelId+"', 'HELPER', 'false'),"
					+ "(12, '"+hotelId+"', 'CLEANER', 'false'),"
					+ "(13, '"+hotelId+"', 'EXEC_CHEF', 'false'),"
					+ "(14, '"+hotelId+"', 'COMMI_1', 'false'),"
					+ "(15, '"+hotelId+"', 'COMMI_2', 'false'),"
					+ "(16, '"+hotelId+"', 'COMMI_3', 'false');";
			db.executeUpdate(sql, false);
			
			sql = "INSERT INTO Flags (Id, hotelId, name, groupId) VALUES "
					+ "(1, '"+hotelId+"', 'Vegetarian', 1), "
					+ "(2, '"+hotelId+"', 'Non-Vegetarian', 1), "
					+ "(3, '"+hotelId+"', 'Beverage', 5), "
					+ "(24, '"+hotelId+"', 'Egg', 1), "
					+ "(5, '"+hotelId+"', 'Alcoholic Beverage', 5), "
					+ "(4, '"+hotelId+"', 'Spicy', 2), "
					+ "(7, '"+hotelId+"', 'Seasonal', 3), "
					+ "(10, '"+hotelId+"', 'Chef Special', 4), "
					+ "(11, '"+hotelId+"', 'Grilled', 5), "
					+ "(12, '"+hotelId+"', 'Fried', 5), "
					+ "(13, '"+hotelId+"', ' Platter', 6), "
					+ "(14, '"+hotelId+"', 'Wheat Free', 7), "
					+ "(15, '"+hotelId+"', 'Gluten Free', 8), "
					+ "(16, '"+hotelId+"', 'Vegan', 9), "
					+ "(17, '"+hotelId+"', 'Age Restriction', 10), "
					+ "(20, '"+hotelId+"', 'Treat Available', 11), "
					+ "(22, '"+hotelId+"', 'Meal', 12), "
					+ "(23, '"+hotelId+"', 'Cake', 13), "
					+ "(19, '"+hotelId+"', 'Choice Item', 14), "
					+ "(40, '"+hotelId+"', 'BREAKFAST', 15), "
					+ "(41, '"+hotelId+"', 'LUNCH', 16), "
					+ "(42, '"+hotelId+"', 'DINNER', 17), "
					+ "(43, '"+hotelId+"', 'PIZZA', 18), "
					+ "(44, '"+hotelId+"', 'SNACKS', 19), "
					+ "(45, '"+hotelId+"', 'NORTH_INDIAN', 20), "
					+ "(46, '"+hotelId+"', 'DESSERT', 21), "
					+ "(47, '"+hotelId+"', 'CHINESE', 22), "
					+ "(48, '"+hotelId+"', 'SOUTH_INDIAN', 23), "
					+ "(49, '"+hotelId+"', 'BURGER', 24), "
					+ "(50, '"+hotelId+"', 'BIRYANI', 25), "
					+ "(51, '"+hotelId+"', 'FAST_FOOD', 26), "
					+ "(53, '"+hotelId+"', 'PARTY COMBOS', 27),"
					+ "(54, '"+hotelId+"', 'BEVERAGES', 28), "
					+ "(55, '"+hotelId+"', 'SNACKS', 29), "
					+ "(56, '"+hotelId+"', 'BREADS', 30), "
					+ "(57, '"+hotelId+"', 'DESSERTS', 31), "
					+ "(58, '"+hotelId+"', 'RICE', 32), "
					+ "(59, '"+hotelId+"', 'SIDES', 33), "
					+ "(60, '"+hotelId+"', 'NEW', 34);";
			db.executeUpdate(sql, false);
			
			sql = "INSERT INTO Taxes (Id, hotelId, name, value, type, isActive) VALUES "
					+ "(1, '"+hotelId+"', 'CGST', 2.5, 'PERCENTAGE', 'true'), "
					+ "(2, '"+hotelId+"', 'SGST', 2.5, 'PERCENTAGE', 'true'); ";
			db.executeUpdate(sql, false);
	
			sql = "INSERT INTO OnlineOrderingPortals (id, portal, name, requiresLogistics, commisionValue, commisionType, hasIntegration,"
					+ " hotelId, paymentCycleDay, discountsApplied) VALUES "
					+ "(0 , 'NONE', 'none', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]'), "
					+ "(1 , 'ZOMATO', 'Zomato', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]'), "
					+ "(2 , 'SWIGGY', 'Swiggy', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]'), "
					+ "(3 , 'FOODPANDA', 'Food Panda', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]'), "
					+ "(4 , 'UBEREATS', 'Uber Eats', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]'), "
					+ "(5 , 'FOODILOO', 'Foodiloo', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]'), "
					+ "(6 , 'ZOMATO PICKUP', 'Zomato Pickup', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]'), "
					+ "(100 , 'COUNTER', 'Counter Parcel', 'false', 0.0, 'PERCENTAGE', 'false', '"+hotelId+"', '', '[]');";
			db.executeUpdate(sql, false);
	
			sql = "INSERT INTO Discount (hotelId, name, description, type, foodValue, barValue, startDate, expiryDate, "
					+ "usageLimit, validCollections, offerType, applicableOnZomato, offerQuantity, bogoItems, startTime, "
					+ "endTime, minOrderAmount, firstOrderOnly) VALUES('"+hotelId+"', 'ZOMATO_VOUCHER', '', '1', 0, 0, '01/02/2019', "
					+ "'31/12/3000', 'Unlimited', '', 'DISCOUNT', 'false', 1, '[]', '00:00:00', '23:59:45', 0, 'false');";
			db.executeUpdate(sql, false);
			
			sql = "INSERT INTO Discount (hotelId, name, description, type, foodValue, barValue, startDate, expiryDate, "
					+ "usageLimit, validCollections, offerType, applicableOnZomato, offerQuantity, bogoItems, startTime, "
					+ "endTime, minOrderAmount, firstOrderOnly) VALUES('"+hotelId+"', 'FIXED_RUPEE_DISCOUNT', '', '1', 0, 0, '01/02/2019', "
					+ "'31/12/3000', 'Unlimited', '', 'DISCOUNT', 'false', 1, '[]', '00:00:00', '23:59:45', 0, 'false');";
			db.executeUpdate(sql, false);
	
			sql = "INSERT INTO Discount (hotelId, name, description, type, foodValue, barValue, startDate, expiryDate, "
					+ "usageLimit, validCollections, offerType, applicableOnZomato, offerQuantity, bogoItems, startTime, "
					+ "endTime, minOrderAmount, firstOrderOnly) VALUES('"+hotelId+"', 'PIGGYBANK', '', '1', 0, 0, '01/02/2019', "
					+ "'31/12/3000', 'Unlimited', '', 'DISCOUNT', 'false', 1, '[]', '00:00:00', '23:59:45', 0, 'false');";
			db.executeUpdate(sql, false);
			
			sql = "INSERT INTO Sections (id, name, kitchenPrinter, barPrinter, summaryPrinter, beveragePrinter, outdoorPrinter, cashierPrinter, hotelId) VALUES "
					+ "(1 , 'DEFAULT', 'Kitchen', 'Kitchen', 'Kitchen', 'Kitchen', 'Kitchen', 'Kitchen', '"+hotelId+"');";
			db.executeUpdate(sql, false);
			
			IUser userDao = new UserManager(false);
			userDao.addUser(hotelId, "OrderOn", hotelCode+"001", 2, "9867334");
			userDao.addUser(hotelId, "staff", hotelCode+"002", 10, "1111");
			userDao.addUser(hotelId, "Zomato", hotelCode+"001", 10, "zomato");
			outObj.put("status", false);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}

	public static class Recipe implements Database.OrderOnEntity {

		private int sku;
		private String menuId;
		private BigDecimal quantity;
		private String hotelId;
		private String measurableUnit;
		private String displayableUnit;
		private String name;
		private int processedMaterialSku;

		public int getSku() {
			return sku;
		}

		public String getMenuId() {
			return menuId;
		}

		public BigDecimal getQuantity() {
			return quantity;
		}

		public String getHotelId() {
			return hotelId;
		}

		public String getMeasurableUnit() {
			return measurableUnit;
		}

		public String getDisplayableUnit() {
			return displayableUnit;
		}

		public String getName() {
			return name;
		}

		public BigDecimal getDisplayableQuantity() {
			MeasurableUnit unit = MeasurableUnit.valueOf(displayableUnit);
			return unit.convertToDisplayableUnit(quantity);
		}
		
		public int getProcessedMaterialSku() {
			return processedMaterialSku;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.sku = Database.readRsInt(rs, "sku");
			this.menuId = Database.readRsString(rs, "menuId");
			this.quantity = Database.readRsBigDecimal(rs, "quantity");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.measurableUnit = Database.readRsString(rs, "measurableUnit");
			this.displayableUnit = Database.readRsString(rs, "displayableUnit");
			this.name = Database.readRsString(rs, "name");
			this.processedMaterialSku = Database.readRsInt(rs, "processedMaterialSku");
		}
	}

	public static class Material implements Database.OrderOnEntity {

		private String sku;
		private String name;
		private String measurableUnit;
		private String displayableUnit;
		private BigDecimal countableConversion;
		private String isCountable;
		private String countableUnit;
		private BigDecimal ratePerUnit;
		private String materialType;
		private String subType;
		private String category;
		private BigDecimal minQuantity;
		private BigDecimal quantity;
		private String outletId;
		private String state;
		private String isActive;
		private String tax;
		
		public String getSku() {
			return sku;
		}

		public String getName() {
			return name;
		}

		public String getMeasurableUnit() {
			return measurableUnit;
		}

		public String getDisplayableUnit() {
			return filterUnitToDisplay(displayableUnit);
		}

		public BigDecimal getCountableConversion() {
			return countableConversion;
		}

		public Boolean getIsCountable() {
			return Boolean.valueOf(isCountable);
		}

		public String getCountableUnit() {
			return Boolean.valueOf(isCountable)?countableUnit:"-";
		}

		public BigDecimal getRatePerUnitInternal() {
			return ratePerUnit;
		}

		public BigDecimal getRatePerUnit() {
			MeasurableUnit measurableUnit = MeasurableUnit.valueOf(displayableUnit);
			return measurableUnit.convertToDisplayableRate(ratePerUnit);
		}

		public BigDecimal getCountableRatePerUnit() {
			MeasurableUnit measurableUnit = MeasurableUnit.valueOf(displayableUnit);
			if(Boolean.valueOf(isCountable)) {
				ratePerUnit = ratePerUnit.multiply(countableConversion);
				return measurableUnit.convertToDisplayableRate(ratePerUnit);
			}else
				return new BigDecimal("0");
		}

		public String getMaterialType() {
			return materialType;
		}

		public String getSubType() {
			return subType;
		}

		public String getCategory() {
			return category;
		}

		public BigDecimal getMinQuantity() {
			MeasurableUnit unit = MeasurableUnit.valueOf(displayableUnit);
			return unit.convertToDisplayableUnit(minQuantity);
		}

		public BigDecimal getQuantity() {
			return quantity;
		}

		public BigDecimal getDisplayableQuantity() {
			MeasurableUnit unit = MeasurableUnit.valueOf(displayableUnit);
			return unit.convertToDisplayableUnit(quantity);
		}

		public String getOutletId() {
			return outletId;
		}

		public String getState() {
			return state;
		}

		public Boolean getIsActive() {
			return Boolean.valueOf(isActive);
		}

		public JSONArray getTaxes(){
			try {
				return new JSONArray(tax);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.name = Database.readRsString(rs, "name");
			this.sku = Database.readRsString(rs, "sku");
			this.measurableUnit = Database.readRsString(rs, "measurableUnit");
			this.displayableUnit = Database.readRsString(rs, "displayableUnit");
			this.countableUnit = Database.readRsString(rs, "countableUnit");
			this.countableConversion = Database.readRsBigDecimal(rs, "countableConversion");
			this.isCountable = Database.readRsString(rs, "isCountable");
			this.ratePerUnit = Database.readRsBigDecimal(rs, "ratePerUnit");
			this.materialType = Database.readRsString(rs, "materialType");
			this.subType = Database.readRsString(rs, "subType");
			this.category = Database.readRsString(rs, "category");
			this.minQuantity = Database.readRsBigDecimal(rs, "minQuantity");
			this.quantity = Database.readRsBigDecimal(rs, "quantity");
			this.outletId = Database.readRsString(rs, "outletId");
			this.state = Database.readRsString(rs, "state");
			this.isActive = Database.readRsString(rs, "isActive");
			this.tax = Database.readRsString(rs, "tax");
		}
	}

	public static class Inventory implements Database.OrderOnEntity {

		private int id;
		private int sku;
		private String materialName;
		private String type;
		private BigDecimal quantity;
		private BigDecimal amount;
		private BigDecimal ratePerUnit;
		private String outletId;
		private String orderId;
		private String menuId;
		private BigDecimal balanceQuantity;
		private BigDecimal gst;
		private BigDecimal gstValue;
		private BigDecimal discount;
		private BigDecimal totalAmount;
		private int purchaseId;
		private String logId;
		private String dateTime;
		private String method;
		private String countableUnit;
		private String isCountable;
		private BigDecimal countableConversion;
		private String displayableUnit;
		
		public String getMethod() {
			return method;
		}

		public int getId() {
			return id;
		}

		public int getSku() {
			return sku;
		}

		public String getMaterialName() {
			return materialName;
		}
		public String getType() {
			return type;
		}

		public BigDecimal getQuantity() {
			return quantity;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public BigDecimal getRatePerUnitInternal() {
			return ratePerUnit;
		}

		public String getOutletId() {
			return outletId;
		}

		public String getOrderId() {
			return orderId;
		}

		public String getMenuId() {
			return menuId;
		}

		public BigDecimal getBalanceQuantity() {
			return balanceQuantity;
		}

		public BigDecimal getGst() {
			return gst;
		}

		public BigDecimal getGstValue() {
			return gstValue;
		}

		public BigDecimal getDiscount() {
			return discount;
		}

		public BigDecimal getTotalAmount() {
			return totalAmount;
		}

		public int getPurchaseId() {
			return purchaseId;
		}

		public String getLogId() {
			return logId;
		}

		public String getDateTime() {
			return dateTime;
		}

		public BigDecimal getRatePerUnit() {
			MeasurableUnit measurableUnit = MeasurableUnit.valueOf(displayableUnit);
			return measurableUnit.convertToDisplayableRate(ratePerUnit);
		}

		public BigDecimal getCountableRatePerUnit() {
			MeasurableUnit measurableUnit = MeasurableUnit.valueOf(displayableUnit);
			if(Boolean.valueOf(isCountable)) {
				ratePerUnit = ratePerUnit.multiply(countableConversion);
				return measurableUnit.convertToDisplayableRate(ratePerUnit);
			}else
				return new BigDecimal("0");
		}

		public BigDecimal getCountableConversion() {
			return countableConversion;
		}

		public Boolean getIsCountable() {
			return Boolean.valueOf(isCountable);
		}

		public String getCountableUnit() {
			return Boolean.valueOf(isCountable)?countableUnit:"-";
		}
		
		public String getDisplayableUnit() {
			return displayableUnit;
		}

		public BigDecimal getDisplayableQuantity() {
			MeasurableUnit unit = MeasurableUnit.valueOf(displayableUnit);
			return unit.convertToDisplayableUnit(quantity);
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.id = Database.readRsInt(rs, "id");
			this.sku = Database.readRsInt(rs, "sku");
			this.materialName = Database.readRsString(rs, "materialName");
			this.type = Database.readRsString(rs, "type");
			this.quantity = Database.readRsBigDecimal(rs, "quantity");
			this.amount = Database.readRsBigDecimal(rs, "amount");
			this.ratePerUnit = Database.readRsBigDecimal(rs, "ratePerUnit");
			this.outletId = Database.readRsString(rs, "outletId");
			this.orderId = Database.readRsString(rs, "orderId");
			this.menuId = Database.readRsString(rs, "menuId");
			this.balanceQuantity = Database.readRsBigDecimal(rs, "balanceQuantity");
			this.gst = Database.readRsBigDecimal(rs, "gst");
			this.gstValue = Database.readRsBigDecimal(rs, "gstValue");
			this.discount = Database.readRsBigDecimal(rs, "discount");
			this.totalAmount = Database.readRsBigDecimal(rs, "totalAmount");
			this.purchaseId = Database.readRsInt(rs, "purchaseId");
			this.logId = Database.readRsString(rs, "logId");
			this.dateTime = Database.readRsString(rs, "dateTime");
			this.displayableUnit = Database.readRsString(rs, "displayableUnit");
			this.isCountable = Database.readRsString(rs, "isCountable");
			this.countableConversion = Database.readRsBigDecimal(rs, "countableConversion");
			this.countableUnit = Database.readRsString(rs, "countableUnit");
		}
	}
	
	public static class InventoryCheckLog implements Database.OrderOnEntity{
		private int id;
		private String dateTime;
		private String serviceDate;
		private String userId;
		private String materialData;
		private String outletId;
		
		public int getId() {
			return id;
		}
		public String getDateTime() {
			return dateTime;
		}
		public String getServiceDate() {
			return serviceDate;
		}
		public String getUserId() {
			return userId;
		}
		public JSONArray getMaterialData() {
			try {
				if(materialData.length()>0) {
					return new JSONArray(materialData);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}
		public String getOutletId() {
			return outletId;
		}
		@Override
		public void readFromDB(ResultSet rs) {
			this.id = Database.readRsInt(rs, "id");
			this.dateTime = Database.readRsString(rs, "dateTime");
			this.serviceDate = Database.readRsString(rs, "serviceDate");
			this.userId = Database.readRsString(rs, "userId");
			this.materialData = Database.readRsString(rs, "materialData");
			this.outletId = Database.readRsString(rs, "outletId");
		}
		
	}

	public static class Purchase implements Database.OrderOnEntity {
		private String purchaseId;
		private String billNo;
		private String challanNo;
		private int vendorId;
		private String vendorName;
		private String outletId;
		private BigDecimal additionalDiscount;
		private BigDecimal totalDiscount;
		private BigDecimal charge;
		private BigDecimal roundOff;
		private BigDecimal totalGst;
		private BigDecimal grandTotal;
		private String purchaseDate;
		private String dateTime;
		private String paymentType;
		private String account;
		private String remark;
		private String outletName;
		private BigDecimal creditBalance;
		
		public String getPurchaseId() {
			return purchaseId;
		}
		public String getBillNo() {
			return billNo;
		}
		public String getChallanNo() {
			return challanNo;
		}
		public int getVendorId() {
			return vendorId;
		}
		public String getVendorName() {
			return vendorName;
		}
		public String getOutletId() {
			return outletId;
		}
		public BigDecimal getAdditionalDiscount() {
			return additionalDiscount;
		}
		public BigDecimal getTotalDiscount() {
			return totalDiscount;
		}
		public BigDecimal getCharge() {
			return charge;
		}
		public BigDecimal getRoundOff() {
			return roundOff;
		}
		public BigDecimal getTotalGst() {
			return totalGst;
		}
		public BigDecimal getGrandTotal() {
			return grandTotal;
		}
		public String getPurchaseDate() {
			return purchaseDate;
		}
		public String getDateTime() {
			return dateTime;
		}
		public String getPaymentType() {
			return paymentType;
		}
		public String getAccount() {
			return account;
		}
		public String getRemark() {
			return remark;
		}
		public String getOutletName() {
			return outletName;
		}
		public BigDecimal getCreditBalance() {
			return creditBalance;
		}
		
		@Override
		public void readFromDB(ResultSet rs) {
			this.purchaseId = Database.readRsString(rs, "purchaseId");
			this.billNo = Database.readRsString(rs, "billNo");
			this.challanNo = Database.readRsString(rs, "challanNo");
			this.vendorId = Database.readRsInt(rs, "vendorId");
			this.vendorName = Database.readRsString(rs, "vendorName");
			this.outletId = Database.readRsString(rs, "outletId");
			this.additionalDiscount = Database.readRsBigDecimal(rs, "additionalDiscount");
			this.totalDiscount = Database.readRsBigDecimal(rs, "totalDiscount");
			this.charge = Database.readRsBigDecimal(rs, "charge");
			this.roundOff = Database.readRsBigDecimal(rs, "roundOff");
			this.totalGst = Database.readRsBigDecimal(rs, "totalGst");
			this.grandTotal = Database.readRsBigDecimal(rs, "grandTotal");
			this.purchaseDate = Database.readRsString(rs, "purchaseDate");
			this.dateTime = Database.readRsString(rs, "dateTime");
			this.paymentType = Database.readRsString(rs, "paymentType");
			this.account = Database.readRsString(rs, "account");
			this.remark = Database.readRsString(rs, "remark");
			this.outletName = Database.readRsString(rs, "outletName");
			this.creditBalance = Database.readRsBigDecimal(rs, "creditBalance");
		}
	}
	
	public static class Vendor implements Database.OrderOnEntity {

		public int id;
		public String corporateId;
		public String outletId;
		public String name;
		public String address;
		public String poc;
		public String GSTNumber;
		public String emailId;
		public BigDecimal balance;

		public int getId() {
			return id;
		}

		public String getCorporateId() {
			return corporateId;
		}

		public String getOutletId() {
			return outletId;
		}

		public String getName() {
			return name;
		}

		public String getAddress() {
			return address;
		}

		public JSONArray getPoc() {
			try {
				return new JSONArray(poc);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public String getGSTNumber() {
			return GSTNumber;
		}

		public String getEmailId() {
			return emailId;
		}

		public BigDecimal getBalance() {
			return balance;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.corporateId = Database.readRsString(rs, "corporateId");
			this.outletId = Database.readRsString(rs, "outletId");
			this.address = Database.readRsString(rs, "address");
			this.poc = Database.readRsString(rs, "poc");
			this.GSTNumber = Database.readRsString(rs, "GSTNumber");
			this.emailId = Database.readRsString(rs, "emailId");
			this.balance = Database.readRsBigDecimal(rs, "balance");
		}
	}
	
	public static class InventoryReport implements Database.OrderOnEntity {

		public String sku;
		public String materialName;
		public String unit;
		public String displayableUnit;
		public String hotelId;
		public Double quantity;
		public String orderId;
		public String menuId;
		public String itemName;

		@Override
		public void readFromDB(ResultSet rs) {
			this.materialName = Database.readRsString(rs, "materialName");
			this.itemName = Database.readRsString(rs, "itemName");
			this.sku = Database.readRsString(rs, "sku");
			this.unit = Database.readRsString(rs, "unit");
			this.displayableUnit = Database.readRsString(rs, "displayableUnit");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.quantity = Database.readRsDouble(rs, "quantity");
			this.orderId = Database.readRsString(rs, "orderId");
			this.menuId = Database.readRsString(rs, "menuId");
		}
		public String getSku() {
			return sku;
		}
		public String getMaterialName() {
			return materialName;
		}
		public String getUnit() {
			return unit;
		}
		public String getDisplayableUnit() {
			return displayableUnit;
		}
		public String getHotelId() {
			return hotelId;
		}
		public Double getQuantity() {
			return quantity;
		}
		public String getOrderId() {
			return orderId;
		}
		public String getMenuId() {
			return menuId;
		}
		public String getItemName() {
			return itemName;
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
		
		public byte[] getPassword() {
			return mPassword;
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
		private byte[] mPassword;
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
			this.mPassword = Database.readRsBytes(rs, "userPasswd");
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

	public static class Settings implements Database.OrderOnEntity {
		public String getOutletId() {
			return outletId;
		}
		public Integer getIsEnabled() {
			return mIsEnabled;
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
		public String getWebsite() {
			return website;
		}
		public int getIsSmsEnabled() {
			return isSmsEnabled;
		}
		public boolean getHasSms() {
			return isSmsEnabled==1?true:false;
		}
		public boolean getHasDeliverySms() {
			return Boolean.valueOf(deliverySmsEnabled);
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
		public byte[] getDrawerCode() {
			return drawerCode.getBytes();
		}
		public int getHasLoyalty() {
			return hasLoyalty;
		}
		public int getHasIncentiveScheme() {
			return hasIncentiveScheme;
		}
		public int getBillType() {
			return billType.contains(":")?Integer.parseInt(billType.split(":")[0]):Integer.parseInt(billType);
		}
		public String getBillGenerationType() {
			return billType.contains(":")?billType.split(":")[1]:GENERATE_BILL_PRE_ORDERING;
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
		public boolean getHasNewOrderScreen() {
			return Boolean.valueOf(hasNewOrderScreen);
		}
		public boolean getIsCaptainBasedOrdering() {
			return Boolean.valueOf(isCaptainbasedOrdering);
		}
		public boolean getShowOccupiedTablesOnly() {
			return Boolean.valueOf(showOccupiedTablesOnly);
		}
		public String getIsCaptainbasedOrdering() {
			return isCaptainbasedOrdering;
		}
		public int getDeductionType() {
			return deductionType;
		}
		public Boolean getIsWalletOnline() {
			return Boolean.valueOf(isWalletOnline);
		}
		public Boolean getIsWalletOffline() {
			return Boolean.valueOf(isWalletOffline);
		}
		public Boolean getIsCreditActive() {
			return Boolean.valueOf(isCreditActive);
		}
		public Boolean getPrintLogo() {
			return Boolean.valueOf(printLogo);
		}
		public String getApiKey() {
			return apiKey;
		}
		public Boolean getHasConciseBill() {
			return Boolean.valueOf(hasConciseBill);
		}
		public String getTheme() {
			return theme;
		}
		public boolean getHasFullRounding() {
			return Boolean.valueOf(hasFullRounding);
		}
		public boolean getCapturePayments() {
			return Boolean.valueOf(capturePayments);
		}
		public String getSmsAPIKey() {
			return smsAPIKey;
		}
		public String getSmsId() {
			return smsId;
		}
		public String getSenderId() {
			return senderId;
		}
		public int getPromotionalSMSBalance() {
			return promotionalSMSBalance;
		}
		public int getTransactionSMSCount() {
			return transactionSMSCount;
		}
		public boolean getDownloadReports() {
			return Boolean.valueOf(downloadReports);
		}

		private String outletId;
		private Integer mIsEnabled;
		private String website;
		private int isSmsEnabled;
		private String deliverySmsEnabled;
		private int isServerEnabled;
		private int hasCashDrawer;
		private int hasLoyalty;
		private int hasIncentiveScheme;
		private String serverUpdateTime;
		private String billType;
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
		private String hasNewOrderScreen;
		private String isCaptainbasedOrdering;
		private String showOccupiedTablesOnly;
		private int deductionType;
		private String drawerCode;
		private String isWalletOnline;
		private String isWalletOffline;
		private String isCreditActive;
		private String printLogo;
		private String apiKey;
		private String hasConciseBill;
		private String theme;
		private String hasFullRounding;
		private String capturePayments;
		private String smsAPIKey;
		private int promotionalSMSBalance;
		private int transactionSMSCount;
		private String downloadReports;
		private String smsId;
		private String senderId;
		
		@Override
		public void readFromDB(ResultSet rs) {
			this.outletId = Database.readRsString(rs, "hotelId");
			this.mIsEnabled = Database.readRsInt(rs, "isEnabled");
			this.hotelType = Database.readRsString(rs, "hotelType");
			this.website = Database.readRsString(rs, "website");
			this.isSmsEnabled = Database.readRsInt(rs, "smsEnabled");
			this.deliverySmsEnabled = Database.readRsString(rs, "deliverySmsEnabled");
			this.isServerEnabled = Database.readRsInt(rs, "serverEnabled");
			this.hasCashDrawer = Database.readRsInt(rs, "hasCashDrawer");
			this.hasLoyalty = Database.readRsInt(rs, "hasLoyalty");
			this.hasIncentiveScheme = Database.readRsInt(rs, "hasIncentiveScheme");
			this.billType = Database.readRsString(rs, "billType");
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
			this.hasNewOrderScreen = Database.readRsString(rs, "hasNewOrderScreen");
			this.isCaptainbasedOrdering = Database.readRsString(rs, "isCaptainbasedOrdering");
			this.showOccupiedTablesOnly = Database.readRsString(rs, "showOccupiedTablesOnly");
			this.deductionType = Database.readRsInt(rs, "deductionType");
			this.drawerCode = Database.readRsString(rs, "drawerCode");
			this.isWalletOnline = Database.readRsString(rs, "isWalletOnline");
			this.isWalletOffline = Database.readRsString(rs, "isWalletOffline");
			this.isCreditActive = Database.readRsString(rs, "isCreditActive");
			this.printLogo = Database.readRsString(rs, "printLogo");
			this.apiKey = Database.readRsString(rs, "apiKey");
			this.hasConciseBill = Database.readRsString(rs, "hasConciseBill");
			this.theme = Database.readRsString(rs, "theme");
			this.hasFullRounding = Database.readRsString(rs, "hasFullRounding");
			this.capturePayments = Database.readRsString(rs, "capturePayments");
			this.smsAPIKey = Database.readRsString(rs, "smsAPIKey");
			this.promotionalSMSBalance = Database.readRsInt(rs, "promotionalSMSBalance");
			this.transactionSMSCount = Database.readRsInt(rs, "transactionSMSCount");
			this.downloadReports = Database.readRsString(rs, "downloadReports");
			this.smsId = Database.readRsString(rs, "smsId");
			this.senderId = Database.readRsString(rs, "senderId");
		}
	}

	public static class Outlet implements Database.OrderOnEntity {

		private int id;
		private String corporateId;
		private String outletId;
		private String name;
		private String companyName;
		private String address;
		private String contact;
		private String gstNumber;
		private String vatNumber;
		private String outletCode;
		private String imageLocation;
		private String schedules;
		private String closedDates;
		private String links;
		private String location;
		private String carouselImages;
		private String menuBanner;
		private String featuredItemId;
		
		public int getId() {
			return id;
		}

		public String getCorporateId() {
			return corporateId;
		}

		public String getOutletId() {
			return outletId;
		}

		public String getName() {
			return name;
		}

		public String getCompanyName() {
			return companyName;
		}

		public String getAddress() {
			return address;
		}

		public String getContact() {
			return contact;
		}

		public String getGstNumber() {
			return gstNumber;
		}

		public String getVatNumber() {
			return vatNumber;
		}

		public String getOutletCode() {
			return outletCode;
		}

		public String getImageLocation() {
			if(imageLocation.equals(""))
				return imageLocation;
			return "Images/Wallet/" + corporateId + "/Outlets/" + imageLocation;
		}
		
		public JSONArray getSchedules() {
			try {
				return new JSONArray(schedules);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}
		
		public JSONArray getClosedDates() {
			try {
				return new JSONArray(closedDates);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}
		
		public JSONObject getLocation() {
			try {
				return new JSONObject(location);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONObject();
		}
		
		public JSONObject getLinks() {
			try {
				return new JSONObject(links);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONObject();
		}
		
		public JSONArray getCarouselImages() {
			try {
				return new JSONArray(carouselImages);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public String getMenuBanner() {
			return menuBanner;
		}

		public String getFeaturedItemId() {
			return featuredItemId;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.corporateId = Database.readRsString(rs, "corporateId");
			this.outletId = Database.readRsString(rs, "outletId");
			this.name = Database.readRsString(rs, "name");
			this.companyName = Database.readRsString(rs, "companyName");
			this.address = Database.readRsString(rs, "address");
			this.contact = Database.readRsString(rs, "contact");
			this.gstNumber = Database.readRsString(rs, "GSTNumber");
			this.vatNumber = Database.readRsString(rs, "VATNumber");
			this.outletCode = Database.readRsString(rs, "code");
			this.imageLocation = Database.readRsString(rs, "imageLocation");
			this.schedules = Database.readRsString(rs, "schedule");
			this.closedDates = Database.readRsString(rs, "closedDates");
			this.location = Database.readRsString(rs, "location");
			this.links = Database.readRsString(rs, "links");
			this.carouselImages = Database.readRsString(rs, "carouselImages");
			this.menuBanner = Database.readRsString(rs, "menuBanner");
			this.featuredItemId = Database.readRsString(rs, "featuredItemId");
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

		public String getType() {
			return type;
		}

		private String mTableId;
		private String orderId;
		private String waiterId;
		private String type;

		@Override
		public void readFromDB(ResultSet rs) {
			this.mTableId = Database.readRsString(rs, "tableId");
			this.orderId = Database.readRsString(rs, "orderId");
			this.waiterId = Database.readRsString(rs, "waiterId");
			this.type = Database.readRsString(rs, "type");
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

		private int id;
		private String firstName;
		private String surName;
		private String mobileNumber;
		private String address;
		private String address2;
		private String address3;
		private String birthdate;
		private String anniversary;
		private String userType;
		private int isSmsSent;
		private String remarks;
		private String allergyInfo;
		private int points;
		private String wantsPromotion;
		private int visitCount;
		private String emailId;
		private String reference;
		private String hotelId;
		private String corporateId;
		private byte[] password;
		private String authToken;
		private String timeStamp;
		private byte[] salt;
		private String referalCode;
		private int wallet;
		private int promotionalCash;
		private int amountEarned;
		private int amountSpent;
		private int otp;
		private String pinGenTime;
		private int otpCount;
		private String isVerified;
		private String isBlocked;
		private String communicationModes;
		private String imageLocation;
		private String sex;
		private String completeTimestamp;
		private String orderId;
		private String lastVisitDate;
		private String lastRechargeDate;
		private String joiningDate;
		private String sendSMS;
		
		public int getId() {
			return id;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getSurName() {
			return surName;
		}
		
		public String getFullName() {
			return (firstName + " " +surName).trim();
		}

		public String getMobileNumber() {
			return mobileNumber;
		}

		public String getAddress() {
			return address;
		}

		public String getAddress2() {
			return address2;
		}

		public String getAddress3() {
			return address3;
		}

		public String getBirthdate() {
			return birthdate;
		}

		public String getAnniversary() {
			return anniversary;
		}

		protected int getIsSmsSent() {
			return isSmsSent;
		}

		public String getRemarks() {
			return remarks;
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

		public int getVisitCount() {
			return visitCount;
		}

		public String getEmailId() {
			return emailId;
		}

		public String getReference() {
			return reference;
		}
		
		public String getHotelId() {
			return hotelId;
		}

		public String getCorporateId() {
			return corporateId;
		}

		protected byte[] getPassword() {
			return password;
		}

		protected String getAuthToken() {
			return authToken;
		}

		protected String getTimeStamp() {
			return timeStamp;
		}

		protected byte[] getSalt() {
			return salt;
		}
		
		public String getUserType() {
			return userType;
		}

		public String getReferalCode() {
			return referalCode;
		}

		public int getWallet() {
			return wallet;
		}

		public int getPromotionalCash() {
			return promotionalCash;
		}

		public int getAmountEarned() {
			return amountEarned;
		}

		public int getAmountSpent() {
			return amountSpent;
		}

		public int getOtp() {
			return otp;
		}

		protected String getPinGenTime() {
			return pinGenTime;
		}

		public int getOtpCount() {
			return otpCount;
		}

		public Boolean getIsVerified() {
			return Boolean.valueOf(isVerified);
		}

		public Boolean getIsBlocked() {
			return Boolean.valueOf(isBlocked);
		}
		
		public JSONArray getCommunicationModes() {
			try {
				return new JSONArray(communicationModes);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public String getImageLocation() {
			return imageLocation;
		}

		public String getSex() {
			return sex;
		}
		
		public String getCompleteTimestamp() {
			return completeTimestamp;
		}

		public String getOrderId() {
			return orderId;
		}

		public String getLastVisitDate() {
			return lastVisitDate;
		}

		public String getLastRechargeDate() {
			return lastRechargeDate;
		}

		public String getJoiningDate() {
			return joiningDate;
		}

		public boolean getSendSMS() {
			return Boolean.valueOf(sendSMS);
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.id = Database.readRsInt(rs, "id");
			this.firstName = Database.readRsString(rs, "firstName");
			this.surName = Database.readRsString(rs, "surName");
			this.mobileNumber = Database.readRsString(rs, "mobileNumber");
			this.address = Database.readRsString(rs, "address");
			this.address2 = Database.readRsString(rs, "address2");
			this.address3 = Database.readRsString(rs, "address3");
			this.birthdate = Database.readRsString(rs, "birthdate");
			this.anniversary = Database.readRsString(rs, "anniversary");
			this.userType = Database.readRsString(rs, "userType");
			this.remarks = Database.readRsString(rs, "remarks");
			this.isSmsSent = Database.readRsInt(rs, "isSmsSent");
			this.allergyInfo = Database.readRsString(rs, "allergyInfo");
			this.points = Database.readRsInt(rs, "points");
			this.wantsPromotion = Database.readRsString(rs, "wantsPromotion");
			this.visitCount = Database.readRsInt(rs, "visitCount");
			this.emailId = Database.readRsString(rs, "emailId");
			this.reference = Database.readRsString(rs, "reference");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.corporateId = Database.readRsString(rs, "corporateId");
			this.mobileNumber = Database.readRsString(rs, "mobileNumber");
			this.password = Database.readRsBytes(rs, "password");
			this.authToken = Database.readRsString(rs, "authToken");
			this.timeStamp = Database.readRsString(rs, "timeStamp");
			this.salt = Database.readRsBytes(rs, "salt");
			this.referalCode = Database.readRsString(rs, "referalCode");
			this.wallet = Database.readRsInt(rs, "wallet");
			this.promotionalCash = Database.readRsInt(rs, "promotionalCash");
			this.amountEarned = Database.readRsInt(rs, "amountEarned");
			this.amountSpent = Database.readRsInt(rs, "amountSpent");
			this.otp = Database.readRsInt(rs, "otp");
			this.pinGenTime = Database.readRsString(rs, "pinGenTime");
			this.otpCount = Database.readRsInt(rs, "otpCount");
			this.isBlocked = Database.readRsString(rs, "isBlocked");
			this.isVerified = Database.readRsString(rs, "isVerified");
			this.communicationModes = Database.readRsString(rs, "communicationMode");
			this.imageLocation = Database.readRsString(rs, "imageLocation");
			this.sex = Database.readRsString(rs, "sex");
			this.completeTimestamp = Database.readRsString(rs, "completeTimestamp");
			this.orderId = Database.readRsString(rs, "orderId");
			this.lastVisitDate = Database.readRsString(rs, "lastVisitDate");
			this.lastRechargeDate = Database.readRsString(rs, "lastRechargeDate");
			this.joiningDate = Database.readRsString(rs, "joiningDate");
			this.sendSMS = Database.readRsString(rs, "sendSMS");
		}
	}
	
	public static class CustomerForOrdering implements Database.OrderOnEntity {

		private int id;
		private String firstName;
		private String surName;
		private String mobileNumber;
		private String address;
		private String emailId;
		
		public int getId() {
			return id;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getSurName() {
			return surName;
		}
		
		public String getFullName() {
			return (firstName + " " +surName).trim();
		}

		public String getMobileNumber() {
			return mobileNumber;
		}

		public String getAddress() {
			return address;
		}

		public String getEmailId() {
			return emailId;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.id = Database.readRsInt(rs, "id");
			this.firstName = Database.readRsString(rs, "firstName");
			this.surName = Database.readRsString(rs, "surName");
			this.mobileNumber = Database.readRsString(rs, "mobileNumber");
			this.address = Database.readRsString(rs, "address");
			this.emailId = Database.readRsString(rs, "emailId");
		}
	}

	public static class CustomerReport implements Database.OrderOnEntity {

		public String getFullName() {
			return (firstName + " " +surName).trim();
		}
		
		public String getFirstName() {
			return firstName;
		}
		
		public String getSurName() {
			return surName;
		}
		
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

		private String firstName;
		private String surName;
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
			this.firstName = Database.readRsString(rs, "firstName");
			this.surName = Database.readRsString(rs, "surName");
			this.customerName = Database.readRsString(rs, "customerName");
			this.mobileNumber = Database.readRsString(rs, "mobileNumber");
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

	public static class CustomerCreditLog implements Database.OrderOnEntity{
		
		private int id;
		private Double amount;
		private String mobileNumber;
		private String outletId;
		private String state;
		private String transDate;
		private String settlementDate;
		private String orderId;
		private String paymentType;
		public int getId() {
			return id;
		}
		public Double getAmount() {
			return amount;
		}
		public String getMobileNumber() {
			return mobileNumber;
		}
		public String getOutletId() {
			return outletId;
		}
		public String getState() {
			return state;
		}
		public String getTransDate() {
			return transDate;
		}
		public String getSettlementDate() {
			return settlementDate;
		}
		public String getOrderId() {
			return orderId;
		}
		public String getPaymentType() {
			return paymentType;
		}
		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.amount = Database.readRsDouble(rs, "amount");
			this.mobileNumber  = Database.readRsString(rs, "mobileNumber");
			this.outletId  = Database.readRsString(rs, "outletId");
			this.state  = Database.readRsString(rs, "state");
			this.transDate  = Database.readRsString(rs, "transDate");
			this.settlementDate  = Database.readRsString(rs, "settlementDate");
			this.orderId  = Database.readRsString(rs, "orderId");
			this.paymentType = Database.readRsString(rs, "paymentType");
		}
		
	}
	
	public static class HomeDelivery implements Database.OrderOnEntity {
		public String getCustomer() {
			return mCustomer;
		}

		public String getAddress() {
			return mAddress;
		}

		public String getMobileNumber() {
			return mMobileNumber;
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
		
		public boolean getIsFoodReady() {
			return Boolean.valueOf(isFoodReady);
		}
		
		public int getOrderNumber() {
			return orderNumber;
		}
		
		public String getOrderDateTime() {
			return orderDateTime;
		}

		private String mCustomer;
		private String mMobileNumber;
		private String mAddress;
		private String mOrderId;
		private Integer mState;
		private BigDecimal mTotal;
		private String mBillNo;
		private String reference;
		private String remarks;
		private int takeAwayType;
		private String isFoodReady;
		private int orderNumber;
		private String orderDateTime;

		@Override
		public void readFromDB(ResultSet rs) {
			this.mCustomer = Database.readRsString(rs, "customer");
			this.mMobileNumber = Database.readRsString(rs, "mobileNumber");
			this.mAddress = Database.readRsString(rs, "address");
			this.mState = Database.readRsInt(rs, "state");
			this.mOrderId = Database.readRsString(rs, "orderId");
			this.mTotal = Database.readRsBigDecimal(rs, "total");
			this.mBillNo = Database.readRsString(rs, "billNo");
			this.reference = Database.readRsString(rs, "reference");
			this.remarks = Database.readRsString(rs, "remarks");
			this.takeAwayType = Database.readRsInt(rs, "takeAwayType");
			this.isFoodReady = Database.readRsString(rs, "isFoodReady");
			this.orderNumber = Database.readRsInt(rs, "orderNumber");
			this.orderDateTime = Database.readRsString(rs, "orderDateTime");
		}
	}
	
	public static class OnlineOrder implements Database.OrderOnEntity{

		private String hotelId;
		private int restaurantId;
		private int portalId;
		private String orderId;
		private int orderNumber;
		private int externalOrderId;
		private String data;
		private int status;
		private String dateTime;
		private String riderName;
		private String riderNumber;
		private String riderStatus;
		
		public String getHotelId() {
			return hotelId;
		}
		public int getRestaurantId() {
			return restaurantId;
		}
		public String getOrderId() {
			return orderId;
		}
		public int getOrderNumber() {
			return orderNumber;
		}
		public int getPortalId() {
			return portalId;
		}
		public int getExternalOrderId() {
			return externalOrderId;
		}
		public String getData() {
			return data;
		}
		public int getStatus() {
			return status;
		}
		public String getDateTime() {
			return dateTime;
		}
		public String getRiderName() {
			return riderName;
		}
		public String getRiderNumber() {
			return riderNumber;
		}
		public String getRiderStatus() {
			return riderStatus;
		}
		
		@Override
		public void readFromDB(ResultSet rs) {
			this.data = Database.readRsString(rs, "data");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.portalId = Database.readRsInt(rs, "portalId");
			this.restaurantId = Database.readRsInt(rs, "restaurantId");
			this.externalOrderId = Database.readRsInt(rs, "externalOrderId");
			this.orderId = Database.readRsString(rs, "orderId");
			this.orderNumber = Database.readRsInt(rs, "orderNumber");
			this.status = Database.readRsInt(rs, "status");
			this.dateTime = Database.readRsString(rs, "dateTime");
			this.riderName = Database.readRsString(rs, "riderName");
			this.riderNumber = Database.readRsString(rs, "riderNumber");
			this.riderStatus = Database.readRsString(rs, "riderStatus");
		}
	}
	
	public static class OnlineOrderingPortal implements Database.OrderOnEntity {

		private int id;
		private String hotelId;
		private String name;
		private String portal;
		private String requiresLogistics;
		private double commisionValue;
		private String commisionType;
		private String hasIntegration;
		private String paymentCycleDay;
		private int menuAssociation;
		
		public int getId() {
			return id;
		}
		public String getHotelId() {
			return hotelId;
		}
		public String getName() {
			return name;
		}
		public String getPortal() {
			return portal;
		}
		public Boolean getRequiresLogistics() {
			return Boolean.valueOf(requiresLogistics);
		}
		public double getCommisionValue() {
			return commisionValue;
		}
		public String getCommisionType() {
			return commisionType;
		}
		public Boolean getHasIntegration() {
			return Boolean.valueOf(hasIntegration);
		}
		public String getPaymentCycleDay() {
			return paymentCycleDay;
		}
		public int getMenuAssociation() {
			return menuAssociation;
		}
		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.portal = Database.readRsString(rs, "portal");
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.requiresLogistics = Database.readRsString(rs, "requiresLogistics");
			this.commisionValue = Database.readRsInt(rs, "commisionValue");
			this.commisionType = Database.readRsString(rs, "commisionType");
			this.paymentCycleDay = Database.readRsString(rs, "paymentCycleDay");
			this.menuAssociation = Database.readRsInt(rs, "menuAssociation");
			this.hasIntegration = Database.readRsString(rs, "hasIntegration");
		}
	}

	public static class Order implements Database.OrderOnEntity {
		
		public String getOrderId() {
			return orderId;
		}

		public int getOrderNumber() {
			return orderNumber;
		}

		public String getOrderDate() {
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

		public BigDecimal getLoyaltyEarned() {
			return loyaltyEarned;
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
		
		public Integer getOrderType() {
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

		public JSONArray getDiscountCode() {
			try {
				return new JSONArray(discountCode);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public JSONArray getExcludedCharges() {
			try {
				return new JSONArray(excludedCharges);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public JSONArray getExcludedTaxes() {
			try {
				return new JSONArray(excludedTaxes);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
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
		
		public BigDecimal getWalletPayment() {
			return walletPayment;
		}

		public void setWalletPayment(BigDecimal walletPayment) {
			this.walletPayment = walletPayment;
		}

		public BigDecimal getCreditAmount() {
			return creditAmount;
		}

		public void setCreditAmount(BigDecimal creditAmount) {
			this.creditAmount = creditAmount;
		}

		public BigDecimal getCashToBeCollected() {
			return cashToBeCollected;
		}
		
		public BigDecimal getZomatoVoucherAmount() {
			return zomatoVoucherAmount;
		}
		
		public BigDecimal getGoldDiscount() {
			return goldDiscount;
		}
		
		public BigDecimal getPiggyBankAmount() {
			return piggyBank;
		}
		
		public BigDecimal getFixedRupeeDiscount() {
			return fixedRupeeDiscount;
		}

		public String getExternalOrderId() {
			return externalOrderId;
		}

		public boolean getIsIntegrationOrder() {
			return externalOrderId.equals("")?false:true;
		}

		public String getRiderName() {
			return riderName;
		}
		
		public String getRiderNumber() {
			return riderNumber;
		}
		
		public String getRiderStatus() {
			return riderStatus;
		}

		public boolean getIsSmsSent() {
			return isSMSSent==0?false:true;
		}
		
		public BigDecimal getAmountReceivable() {
			return amountReceivable;
		}

		public boolean getIsFoodReady() {
			return Boolean.valueOf(isFoodReady);
		}
		public int getWalletTransactionId() {
			return walletTransactionId;
		}
		public BigDecimal getPromotionalCash() {
			return promotionalCash;
		}
		public JSONObject getEWards() {
			try {
				if(eWards.isEmpty()) {
					return new JSONObject();
				}
				return new JSONObject(eWards);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONObject();
		}
		
		public String getOrderDateTime() {
			return orderDateTime;
		}

		private String orderId;
		private int orderNumber;
		private String orderDate;
		private String customerName;
		private String customerAddress;
		private String customerNumber;
		private String customerGst;
		private BigDecimal loyaltyEarned;
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
		private BigDecimal walletPayment;
		private BigDecimal creditAmount;
		private String excludedCharges;
		private String excludedTaxes;
		private String externalOrderId;
		private String riderName;
		private String riderNumber;
		private String riderStatus;
		private BigDecimal cashToBeCollected;
		private BigDecimal zomatoVoucherAmount;
		private BigDecimal goldDiscount;
		private BigDecimal piggyBank;
		private int isSMSSent;
		private BigDecimal fixedRupeeDiscount;
		private BigDecimal amountReceivable;
		private String isFoodReady;
		private int walletTransactionId;
		private BigDecimal promotionalCash;
		private String eWards;
		private String orderDateTime;

		@Override
		public void readFromDB(ResultSet rs) {
			this.orderId = Database.readRsString(rs, "orderId");
			this.orderNumber = Database.readRsInt(rs, "Id");
			this.orderDate = Database.readRsString(rs, "orderDate");
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
			this.walletPayment = Database.readRsBigDecimal(rs, "walletPayment");
			this.creditAmount = Database.readRsBigDecimal(rs, "creditAmount");
			this.excludedCharges = Database.readRsString(rs, "excludedCharges");
			this.excludedTaxes = Database.readRsString(rs, "excludedTaxes");
			this.externalOrderId = Database.readRsString(rs, "externalOrderId");
			this.riderName = Database.readRsString(rs, "riderName");
			this.riderNumber = Database.readRsString(rs, "riderNumber");
			this.riderStatus = Database.readRsString(rs, "riderStatus");
			this.cashToBeCollected = Database.readRsBigDecimal(rs, "cashToBeCollected");
			this.zomatoVoucherAmount = Database.readRsBigDecimal(rs, "zomatoVoucherAmount");
			this.goldDiscount = Database.readRsBigDecimal(rs, "goldDiscount");
			this.piggyBank = Database.readRsBigDecimal(rs, "piggyBank");
			this.isSMSSent = Database.readRsInt(rs, "isSmsSent");
			this.loyaltyEarned = Database.readRsBigDecimal(rs, "loyaltyEarned");
			this.fixedRupeeDiscount = Database.readRsBigDecimal(rs, "fixedRupeeDiscount");
			this.amountReceivable = Database.readRsBigDecimal(rs, "amountReceivable");
			this.isFoodReady = Database.readRsString(rs, "isFoodReady");
			this.walletTransactionId = Database.readRsInt(rs, "walletTransactionId");
			this.promotionalCash = Database.readRsBigDecimal(rs, "promotionalCash");
			this.eWards = Database.readRsString(rs, "ewards");
			this.orderDateTime = Database.readRsString(rs, "orderDateTime");
		}
	}

	public static class TableUsage implements Database.OrderOnEntity {

		public String getTableId() {
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

		public String getType() {
			return type;
		}

		public Boolean getShowTableView() {
			return Boolean.valueOf(showTableView);
		}

		private String mTableId;
		private String mUserId;
		private String mOrderId;
		private String waiterId;
		private int state;
		private String section;
		private String type;
		private String showTableView;

		@Override
		public void readFromDB(ResultSet rs) {
			this.mTableId = Database.readRsString(rs, "tableId");
			this.mUserId = Database.readRsString(rs, "userId");
			this.mOrderId = Database.readRsString(rs, "orderId");
			this.waiterId = Database.readRsString(rs, "waiterId");
			this.state = Database.readRsInt(rs, "state");
			this.section = Database.readRsString(rs, "section");
			this.type = Database.readRsString(rs, "type");
			this.showTableView = Database.readRsString(rs, "showTableView");
		}
	}

	public static class MenuItem implements Database.OrderOnEntity {

		private int id;
		private String station;
		private String menuId;
		private String title;
		private String description;
		private String collection;
		private String subCollection;
		private String flags;
		private int preparationTime;
		private BigDecimal deliveryRate;
		private BigDecimal dineInRate;
		private BigDecimal dineInNonAcRate;
		private BigDecimal onlineRate;
		private BigDecimal onlineRate1;
		private BigDecimal onlineRate2;
		private BigDecimal onlineRate3;
		private BigDecimal onlineRate4;
		private BigDecimal onlineRate5;
		private BigDecimal costPrice;
		private String method;
		private int state;
		private int vegType;//to be removed
		private String img;
		private String coverImgUrl;
		private String code;
		private int isTaxable;//to be removed
		private String addOns;//to be removed
		private int hasIncentive;
		private int incentive;
		private String groups;
		private String taxes;
		private String charges;
		private String isRecomended;
		private String isTreats;
		private String isDefault;
		private String isBogo;
		private String isCombo;
		private BigDecimal comboReducedPrice;
		private BigDecimal comboPrice;
		private String isAddOn;
		private String syncOnZomato;
		private String gstInclusive;
		private String discountType;
		private BigDecimal discountValue;

		public int getId() {
			return id;
		}

		public String getStation() {
			return station;
		}

		public String getMenuId() {
			return menuId;
		}

		public String getTitle() {
			return title;
		}

		public String getDescription() {
			return description;
		}

		public String getCollection() {
			return collection;
		}

		public String getSubCollection() {
			return subCollection;
		}

		public JSONArray getFlags() {
			try {
				return new JSONArray(flags);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public int getPreparationTime() {
			return preparationTime;
		}

		public BigDecimal getDeliveryRate() {
			return deliveryRate;
		}

		public BigDecimal getDineInRate() {
			return dineInRate;
		}

		public BigDecimal getDineInNonAcRate() {
			return dineInNonAcRate;
		}

		public BigDecimal getOnlineRate() {
			return onlineRate;
		}

		public BigDecimal getOnlineRate1() {
			return onlineRate1;
		}

		public BigDecimal getOnlineRate2() {
			return onlineRate2;
		}

		public BigDecimal getOnlineRate3() {
			return onlineRate3;
		}

		public BigDecimal getOnlineRate4() {
			return onlineRate4;
		}

		public BigDecimal getOnlineRate5() {
			return onlineRate5;
		}

		public BigDecimal getCostPrice() {
			return costPrice;
		}

		public String getMethod() {
			return method;
		}

		public int getState() {
			return state;
		}

		public int getVegType() {
			return vegType;
		}

		public String getImg() {
			return img;
		}

		public String getCoverImgUrl() {
			return coverImgUrl;
		}

		public String getCode() {
			return code;
		}

		public int getIsTaxable() {
			return isTaxable;
		}

		public String getAddOnString() {
			return addOns;
		}

		public int getHasIncentive() {
			return hasIncentive;
		}

		public int getIncentive() {
			return incentive;
		}
		
		public JSONArray getGroups() {
			try {
				return new JSONArray(groups);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public JSONArray getTaxes() {
			try {
				return new JSONArray(taxes);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public JSONArray getCharges() {
			try {
				return new JSONArray(charges);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public Boolean getIsRecomended() {
			return Boolean.valueOf(isRecomended);
		}

		public Boolean getIsTreats() {
			return Boolean.valueOf(isTreats);
		}

		public Boolean getIsDefault() {
			return Boolean.valueOf(isDefault);
		}

		public Boolean getIsBogo() {
			return Boolean.valueOf(isBogo);
		}

		public Boolean getIsCombo() {
			return Boolean.valueOf(isCombo);
		}

		public BigDecimal getComboReducedPrice() {
			return comboReducedPrice;
		}

		public BigDecimal getComboPrice() {
			return comboPrice;
		}

		public Boolean getIsAddOn(){
			return Boolean.valueOf(isAddOn);
		}

		public Boolean getSyncOnZomato(){
			return Boolean.valueOf(syncOnZomato);
		}

		public Boolean getGstInclusive(){
			return Boolean.valueOf(gstInclusive);
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

		public String getDiscountType() {
			return discountType;
		}

		public BigDecimal getDiscountValue() {
			return discountValue;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.id = Database.readRsInt(rs, "id");
			this.station = Database.readRsString(rs, "station");
			this.menuId = Database.readRsString(rs, "menuId");
			this.title = Database.readRsString(rs, "title");
			this.description = Database.readRsString(rs, "description");
			this.collection = Database.readRsString(rs, "collection");
			this.subCollection = Database.readRsString(rs, "subCollection");
			this.flags = Database.readRsString(rs, "flags");
			this.preparationTime = Database.readRsInt(rs, "preparationTime");
			this.deliveryRate = Database.readRsBigDecimal(rs, "deliveryRate");
			this.dineInRate = Database.readRsBigDecimal(rs, "dineInRate");
			this.dineInNonAcRate = Database.readRsBigDecimal(rs, "dineInNonAcRate");
			this.onlineRate = Database.readRsBigDecimal(rs, "onlineRate");
			this.onlineRate1 = Database.readRsBigDecimal(rs, "onlineRate1");
			this.onlineRate2 = Database.readRsBigDecimal(rs, "onlineRate2");
			this.onlineRate3 = Database.readRsBigDecimal(rs, "onlineRate3");
			this.onlineRate4 = Database.readRsBigDecimal(rs, "onlineRate4");
			this.onlineRate5 = Database.readRsBigDecimal(rs, "onlineRate5");
			this.costPrice = Database.readRsBigDecimal(rs, "costPrice");
			this.vegType = Database.readRsInt(rs, "vegType");
			this.method = Database.readRsString(rs, "method");
			this.state = Database.readRsInt(rs, "state");
			this.code = Database.readRsString(rs, "code");
			this.img = Database.readRsString(rs, "img");
			this.coverImgUrl = Database.readRsString(rs, "coverImgUrl");
			this.isTaxable = Database.readRsInt(rs, "isTaxable");
			this.addOns = Database.readRsString(rs, "addOns");
			this.hasIncentive = Database.readRsInt(rs, "hasIncentive");
			this.incentive = Database.readRsInt(rs, "incentive");
			this.groups = Database.readRsString(rs, "groups");
			this.taxes = Database.readRsString(rs, "taxes");
			this.charges = Database.readRsString(rs, "charges");
			this.isRecomended = Database.readRsString(rs, "isRecomended");
			this.isTaxable = Database.readRsInt(rs, "isTaxable");
			this.isDefault = Database.readRsString(rs, "isDefault");
			this.isBogo = Database.readRsString(rs, "isBogo");
			this.isCombo = Database.readRsString(rs, "isCombo");
			this.isTreats = Database.readRsString(rs, "isTreats");
			this.comboReducedPrice = Database.readRsBigDecimal(rs, "comboReducedPrice");
			this.comboPrice = Database.readRsBigDecimal(rs, "comboPrice");
			this.isAddOn = Database.readRsString(rs, "isAddOn");
			this.syncOnZomato = Database.readRsString(rs, "syncOnZomato");
			this.gstInclusive = Database.readRsString(rs, "gstInclusive");
			this.discountType = Database.readRsString(rs, "discountType");
			this.discountValue = Database.readRsBigDecimal(rs, "discountValue");
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

		public String getCollection() {
			return collection;
		}

		public int getVegType() {
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

		public BigDecimal getSubTotal() {
			return subTotal;
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

		public JSONArray getTaxes() {
			try {
				return new JSONArray(taxes);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public JSONArray getCharges() {
			try {
				return new JSONArray(charges);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public int getKotNumber() {
			return kotNumber;
		}

		public int getBotNumber() {
			return botNumber;
		}
		
		public BigDecimal getFinalAmount() {
			return finalAmount;
		}
		
		public Boolean getItemIsMoved() {
			return Boolean.valueOf(itemIsMoved);
		}

		public String getDiscountType() {
			return discountType;
		}

		public BigDecimal getDiscountValue() {
			return discountValue;
		}

		private int id;
		private String orderId;
		private String subOrderId;
		private String subOrderDate;
		private String logTime;
		private String menuId;
		private int vegType;
		private String title;
		private String collection;
		private String waiterId;
		private String specs;
		private BigDecimal rate;
		private BigDecimal subTotal;
		private int state;
		private int qty;
		private String billNo;
		private String reason;
		private String station;
		private int isKOTPrinted;
		private int isTaxable;
		private int itemId;
		private String taxes;
		private String charges;
		private int kotNumber;
		private int botNumber;
		private BigDecimal finalAmount;
		private String itemIsMoved;
		private String discountType;
		private BigDecimal discountValue;

		@Override
		public void readFromDB(ResultSet rs) {
			this.orderId = Database.readRsString(rs, "orderId");
			this.id = Database.readRsInt(rs, "Id");
			this.subOrderId = Database.readRsString(rs, "subOrderId");
			this.subOrderDate = Database.readRsString(rs, "subOrderDate");
			this.menuId = Database.readRsString(rs, "menuId");
			this.vegType = Database.readRsInt(rs, "vegType");
			this.title = Database.readRsString(rs, "title");
			this.collection = Database.readRsString(rs, "collection");
			this.waiterId = Database.readRsString(rs, "waiterId");
			this.state = Database.readRsInt(rs, "state");
			this.rate = Database.readRsBigDecimal(rs, "rate");
			this.subTotal = Database.readRsBigDecimal(rs, "subTotal");
			this.qty = Database.readRsInt(rs, "qty");
			this.specs = Database.readRsString(rs, "specs");
			this.billNo = Database.readRsString(rs, "billNo");
			this.reason = Database.readRsString(rs, "reason");
			this.station = Database.readRsString(rs, "station");
			this.isKOTPrinted = Database.readRsInt(rs, "isKotPrinted");
			this.isTaxable = Database.readRsInt(rs, "isTaxable");
			this.itemId = Database.readRsInt(rs, "itemId");
			this.logTime = Database.readRsString(rs, "time");
			this.taxes = Database.readRsString(rs, "taxes");
			this.charges = Database.readRsString(rs, "charges");
			this.kotNumber = Database.readRsInt(rs, "kotNumber");
			this.botNumber = Database.readRsInt(rs, "botNumber");
			this.finalAmount = Database.readRsBigDecimal(rs, "finalAmount");
			this.itemIsMoved = Database.readRsString(rs, "itemIsMoved");
			this.discountType = Database.readRsString(rs, "discountType");
			this.discountValue = Database.readRsBigDecimal(rs, "discountValue");
		}
	}

	public static class OrderAddOn implements Database.OrderOnEntity {
		public String getOrderId() {
			return orderId;
		}

		public String getSubOrderId() {
			return subOrderId;
		}

		public String getAddOnId() {
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

		public String getStation() {
			return station;
		}

		public JSONArray getTaxes() {
			try {
				return new JSONArray(taxes);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public JSONArray getCharges() {
			try {
				return new JSONArray(charges);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		private String orderId;
		private String subOrderId;
		private String addOnId;
		private int itemId;
		private String menuId;
		private String name;
		private BigDecimal rate;
		private int qty;
		private int state;
		private String taxes;
		private String charges;
		private String station;

		@Override
		public void readFromDB(ResultSet rs) {
			this.orderId = Database.readRsString(rs, "orderId");
			this.subOrderId = Database.readRsString(rs, "subOrderId");
			this.addOnId = Database.readRsString(rs, "addOnId");
			this.itemId = Database.readRsInt(rs, "itemId");
			this.menuId = Database.readRsString(rs, "menuId");
			this.name = Database.readRsString(rs, "title");
			this.rate = Database.readRsBigDecimal(rs, "rate");
			this.qty = Database.readRsInt(rs, "qty");
			this.state = Database.readRsInt(rs, "state");
			this.taxes = Database.readRsString(rs, "taxes");
			this.charges = Database.readRsString(rs, "charges");
			this.station = Database.readRsString(rs, "station");
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

		public String getOfferType() {
			return offerType;
		}

		public Boolean getApplicableOnZomato() {
			return Boolean.valueOf(applicableOnZomato);
		}

		public int getOfferQuantity() {
			return offerQuantity;
		}

		public JSONArray getBogoItems() {
			try {
				return new JSONArray(bogoItems);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public String getStartTime() {
			return startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public int getMinOrderAmount() {
			return minOrderAmount;
		}

		public Boolean getFirstOrderOnly() {
			return Boolean.valueOf(firstOrderOnly);
		}
		
		public Boolean getIsActive() {
			return Boolean.valueOf(isActive);
		}

		public void setFoodValue(int foodValue) {
			this.foodValue = foodValue;
		}

		public int getMaxFoodDiscountAmount() {
			return maxFoodDiscountAmount;
		}

		public int getMaxBarDiscountAmount() {
			return maxBarDiscountAmount;
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
		private String offerType;
		private String applicableOnZomato;
		private int offerQuantity;
		private String bogoItems;
		private String startTime;
		private String endTime;
		private int minOrderAmount;
		private String firstOrderOnly;
		private String isActive;
		private int maxFoodDiscountAmount;
		private int maxBarDiscountAmount;
		
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
			this.offerType = Database.readRsString(rs, "offerType");
			this.applicableOnZomato = Database.readRsString(rs, "applicableOnZomato");
			this.offerQuantity = Database.readRsInt(rs, "offerQuantity");
			this.bogoItems = Database.readRsString(rs, "bogoItems");
			this.startTime = Database.readRsString(rs, "startTime");
			this.endTime = Database.readRsString(rs, "endTime");
			this.minOrderAmount = Database.readRsInt(rs, "minOrderAmount");
			this.firstOrderOnly = Database.readRsString(rs, "firstOrderOnly");
			this.isActive = Database.readRsString(rs, "isActive");
			this.maxFoodDiscountAmount = Database.readRsInt(rs, "maxFoodDiscountAmount");
			this.maxBarDiscountAmount = Database.readRsInt(rs, "maxBarDiscountAmount");
		}
	}
	
	public static class OrderDiscount implements Database.OrderOnEntity {
		
		private String name;
		private String type;
		private String collection;
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
		public String getCollection() {
			return collection;
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
			this.collection = Database.readRsString(rs, "collection");
			this.type = Database.readRsString(rs, "type");
			this.isTaxed = Database.readRsString(rs, "isTaxed");
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
		private BigDecimal foodBill;
		private BigDecimal barBill;
		private BigDecimal grossTotal;
		private BigDecimal foodDiscount;
		private BigDecimal barDiscount;
		private BigDecimal totalDiscount;
		private BigDecimal grossLoyalty;
		private BigDecimal grossComplimentary;
		private BigDecimal grossGst;
		private BigDecimal grossVatBar;
		private BigDecimal grossPackagingCharge;
		private BigDecimal grossDeliveryCharge;
		private BigDecimal appPayment;
		private BigDecimal cardPayment;
		private BigDecimal cashPayment;
		private BigDecimal walletPayment;
		private BigDecimal promotionalCash;
		private BigDecimal creditAmount;
		private BigDecimal grossServiceCharge;
		private BigDecimal totalSale;
		private BigDecimal NetSales;
		private BigDecimal grossExpenses;
		private BigDecimal grossPayIns;
		private BigDecimal sumVoids;
		private BigDecimal sumReturns;
		private BigDecimal roundOffDifference;
		private BigDecimal totalPayment;
		private BigDecimal roundOff;
		private Integer countVoids;
		private Integer countReturns;

		@Override
		public void readFromDB(ResultSet rs) {
			this.foodBill = Database.readRsBigDecimal(rs, "foodBill");
			this.barBill = Database.readRsBigDecimal(rs, "barBill");
			this.grossTotal = Database.readRsBigDecimal(rs, "grossTotal");
			this.foodDiscount = Database.readRsBigDecimal(rs, "foodDiscount");
			this.barDiscount = Database.readRsBigDecimal(rs, "barDiscount");
			this.totalDiscount = Database.readRsBigDecimal(rs, "totalDiscount");
			this.grossLoyalty = Database.readRsBigDecimal(rs, "grossLoyalty");
			this.grossComplimentary = Database.readRsBigDecimal(rs, "grossComplimentary");
			this.grossGst = Database.readRsBigDecimal(rs, "grossGst"); 
			this.grossVatBar = Database.readRsBigDecimal(rs, "grossVatBar"); 
			this.grossPackagingCharge = Database.readRsBigDecimal(rs, "grossPackagingCharge"); 
			this.grossDeliveryCharge = Database.readRsBigDecimal(rs, "grossDeliveryCharge"); 
			this.appPayment = Database.readRsBigDecimal(rs, "appPayment");
			this.cardPayment = Database.readRsBigDecimal(rs, "cardPayment");
			this.cashPayment = Database.readRsBigDecimal(rs, "cashPayment");
			this.walletPayment = Database.readRsBigDecimal(rs, "walletPayment");
			this.promotionalCash = Database.readRsBigDecimal(rs, "promotionalCash");
			this.creditAmount = Database.readRsBigDecimal(rs, "creditAmount");
			this.grossServiceCharge = Database.readRsBigDecimal(rs, "grossServiceCharge");
			this.NetSales = Database.readRsBigDecimal(rs, "netSales");
			this.grossExpenses = Database.readRsBigDecimal(rs, "grossExpenses");
			this.grossPayIns = Database.readRsBigDecimal(rs, "totalPayIns");
			this.totalSale = Database.readRsBigDecimal(rs, "totalSale");
			this.sumVoids = Database.readRsBigDecimal(rs, "sumVoids");
			this.sumReturns = Database.readRsBigDecimal(rs, "sumReturns");
			this.roundOffDifference = Database.readRsBigDecimal(rs, "roundOffDifference");
			this.totalPayment = Database.readRsBigDecimal(rs, "totalPayment");
			this.countVoids = Database.readRsInt(rs, "countVoids");
			this.countReturns = Database.readRsInt(rs, "countReturns");
			this.roundOff = Database.readRsBigDecimal(rs, "roundOff");

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
		public BigDecimal getFoodDiscount() {
			return foodDiscount;
		}
		public BigDecimal getBarDiscount() {
			return barDiscount;
		}
		public BigDecimal getGrossGst() {
			return grossGst;
		}
		public BigDecimal getGrossVatBar() {
			return grossVatBar;
		}
		public BigDecimal getGrossPackagingCharge() {
			return grossPackagingCharge;
		}
		public BigDecimal getGrossDeliveryCharge() {
			return grossDeliveryCharge;
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
		public BigDecimal getFoodBill() {
			return foodBill;
		}
		public BigDecimal getBarBill() {
			return barBill;
		}

		public BigDecimal getWalletPayment() {
			return walletPayment;
		}

		public BigDecimal getPromotionalCash() {
			return promotionalCash;
		}

		public BigDecimal getCreditAmount() {
			return creditAmount;
		}

		public BigDecimal getRoundOffDifference() {
			return roundOffDifference;
		}

		public BigDecimal getTotalPayment() {
			return totalPayment;
		}

		public BigDecimal getTotalDiscount() {
			return totalDiscount;
		}
		public BigDecimal getRoundOff() {
			return roundOff;
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
		private String collection;
		private String station;
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
		
		public String getCollection() {
			return collection;
		}
		public String getStation() {
			return station;
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
			this.collection = Database.readRsString(rs, "collection");
			this.station = Database.readRsString(rs, "station");
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
		private String collection; // Jason
		private int qty;
		private String menuId;
		private String title;
		private String station;

		public String getCollection() {
			return collection;
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

		public String getStation() {
			return station;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.title = Database.readRsString(rs, "title");
			this.menuId = Database.readRsString(rs, "menuId");
			this.qty = Database.readRsInt(rs, "qty");
			this.collection = Database.readRsString(rs, "collection");
			this.station = Database.readRsString(rs, "station");
		}
	}

	// LunchDinnerSalesReport-ap (edited)
	public static class PaymentWiseSalesReport implements Database.OrderOnEntity {
		private BigDecimal foodBill;
		private BigDecimal barBill;
		private BigDecimal total;
		private BigDecimal cash;
		private BigDecimal card;
		private BigDecimal wallet;
		private BigDecimal promotionalCash;
		private BigDecimal credit;
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
		private BigDecimal ZOMATO_PICKUP;
		private BigDecimal SWIGGY;
		private BigDecimal PAYTM;
		private BigDecimal DINE_OUT;
		private BigDecimal FOOD_PANDA;
		private BigDecimal UBER_EATS;
		private BigDecimal FOODILOO;
		private BigDecimal NEARBY;
		private BigDecimal SWIGGY_POP;
		private BigDecimal GOOGLE_PAY;
		private BigDecimal MAGIC_PIN;
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
			this.wallet = Database.readRsBigDecimal(rs, "wallet");
			this.promotionalCash = Database.readRsBigDecimal(rs, "promotionalCash");
			this.credit = Database.readRsBigDecimal(rs, "credit");
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
			this.ZOMATO_PICKUP = Database.readRsBigDecimal(rs, "ZOMATO_PICKUP");
			this.SWIGGY = Database.readRsBigDecimal(rs, "SWIGGY");
			this.PAYTM = Database.readRsBigDecimal(rs, "PAYTM");
			this.DINE_OUT = Database.readRsBigDecimal(rs, "DINEOUT");
			this.FOOD_PANDA = Database.readRsBigDecimal(rs, "FOODPANDA");
			this.UBER_EATS = Database.readRsBigDecimal(rs, "UBEREATS");
			this.FOODILOO = Database.readRsBigDecimal(rs, "FOODILOO");
			this.NEARBY = Database.readRsBigDecimal(rs, "NEARBY");
			this.SWIGGY_POP = Database.readRsBigDecimal(rs, "SWIGGYPOP");
			this.GOOGLE_PAY = Database.readRsBigDecimal(rs, "GOOGLEPAY");
			this.MAGIC_PIN = Database.readRsBigDecimal(rs, "MAGICPIN");
		}

		public BigDecimal getWallet() {
			return wallet;
		}

		public BigDecimal getCredit() {
			return credit;
		}

		public BigDecimal getSWIGGY_POP() {
			return SWIGGY_POP;
		}

		public BigDecimal getGOOGLE_PAY() {
			return GOOGLE_PAY;
		}

		public BigDecimal getMAGIC_PIN() {
			return MAGIC_PIN;
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

		public BigDecimal getZOMATO_PICKUP() {
			return ZOMATO_PICKUP;
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

		public BigDecimal getSWIGGYPOP() {
			return SWIGGY_POP;
		}

		public BigDecimal getGOOGLEPAY() {
			return GOOGLE_PAY;
		}

		public BigDecimal getMAGICPIN() {
			return MAGIC_PIN;
		}

		public BigDecimal getPromotionalCash() {
			return promotionalCash;
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
		private int salary;
		private int bonus;
		private String image;
		private String middleName;
		private String email;
		private BigDecimal accountBalance;
		private String sendOperationalEmail;
		private String sendEODEmail;
		private String sendSMS;
		private String sendEODSMS;

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

		public boolean getSendOperationalEmail() {
			return Boolean.valueOf(sendOperationalEmail);
		}

		public boolean getSendEODEmail() {
			return Boolean.valueOf(sendEODEmail);
		}

		public boolean getSendSMS() {
			return Boolean.valueOf(sendSMS);
		}

		public boolean getSendEODSMS() {
			return Boolean.valueOf(sendEODSMS);
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
			this.salary = Database.readRsInt(rs, "salary");
			this.bonus = Database.readRsInt(rs, "bonus");
			this.image = Database.readRsString(rs, "image");
			this.middleName = Database.readRsString(rs, "middleName");
			this.email = Database.readRsString(rs, "email");
			this.accountBalance = Database.readRsBigDecimal(rs, "accountBalance");
			this.sendEODEmail = Database.readRsString(rs, "sendEODEmail");
			this.sendOperationalEmail = Database.readRsString(rs, "sendOperationalEmail");
			this.sendSMS = Database.readRsString(rs, "sendSMS");
			this.sendEODSMS = Database.readRsString(rs, "sendEODSMS");
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
		private String completeTime;
		private String customerName;
		private String remarks;
		private int state;
		private BigDecimal foodBill;
		private BigDecimal barBill;
		private BigDecimal foodGross;
		private BigDecimal barGross;
		private BigDecimal totalBill;
		private BigDecimal foodSale;
		private BigDecimal barSale;
		private BigDecimal totalDiscount;
		private BigDecimal foodDiscount;
		private BigDecimal barDiscount;
		private BigDecimal total;
		private BigDecimal netSale;
		private BigDecimal serviceCharge;
		private BigDecimal packagingCharge;
		private BigDecimal deliveryCharge;
		private BigDecimal vatBar;
		private BigDecimal gst;
		private BigDecimal tip;
		private BigDecimal cashPayment;
		private BigDecimal cardPayment;
		private BigDecimal appPayment;
		private BigDecimal walletPayment;
		private BigDecimal inhouseSales;
		private BigDecimal homeDeliverySales;
		private BigDecimal takeAwaySales;
		private BigDecimal zomatoSale;
		private BigDecimal swiggySale;
		private BigDecimal foodPandaSale;
		private BigDecimal uberEatsSale;
		private String cardType;
		private int inhouse;
		private int covers;
		private int tableId;
		private int checks;
		private String discountName;
		private int orderCount;
		private int printCount;
		private int reprints;
		private BigDecimal loyaltyAmount;
		private BigDecimal complimentary;
		private String section;
		private BigDecimal grossSale;
		private BigDecimal nc;
		private int takeAwayType;
		private BigDecimal creditAmount;
		private BigDecimal roundOff;
		private BigDecimal promotionalCash;
		private String externalOrderId;
		private String reference;

		@Override
		public void readFromDB(ResultSet rs) {
			this.hotelId = Database.readRsString(rs, "hotelId");
			this.billNo = Database.readRsString(rs, "billNo");
			this.orderId = Database.readRsString(rs, "orderId");
			this.orderDate = Database.readRsString(rs, "orderDate");
			this.completeTime = Database.readRsString(rs, "completeTimestamp");
			this.customerName = Database.readRsString(rs, "customerName");
			this.remarks = Database.readRsString(rs, "remarks");
			this.state = Database.readRsInt(rs, "state");
			this.foodBill = Database.readRsBigDecimal(rs, "foodBill");
			this.barBill = Database.readRsBigDecimal(rs, "barBill");
			this.foodGross = Database.readRsBigDecimal(rs, "foodGross");
			this.barGross = Database.readRsBigDecimal(rs, "barGross");
			this.totalBill = Database.readRsBigDecimal(rs, "totalBill");
			this.foodSale = Database.readRsBigDecimal(rs, "foodSale");
			this.barSale = Database.readRsBigDecimal(rs, "barSale");
			this.totalDiscount = Database.readRsBigDecimal(rs, "totalDiscount");
			this.foodDiscount = Database.readRsBigDecimal(rs, "foodDiscount");
			this.barDiscount = Database.readRsBigDecimal(rs, "barDiscount");
			this.total = Database.readRsBigDecimal(rs, "total");
			this.netSale = Database.readRsBigDecimal(rs, "netSale");
			this.serviceCharge = Database.readRsBigDecimal(rs, "serviceCharge");
			this.packagingCharge = Database.readRsBigDecimal(rs, "packagingCharge");
			this.deliveryCharge = Database.readRsBigDecimal(rs, "deliveryCharge");
			this.vatBar = Database.readRsBigDecimal(rs, "vatBar");
			this.gst = Database.readRsBigDecimal(rs, "gst");
			this.tip = Database.readRsBigDecimal(rs, "tip");
			this.cashPayment = Database.readRsBigDecimal(rs, "cashPayment");
			this.cardPayment = Database.readRsBigDecimal(rs, "cardPayment");
			this.appPayment = Database.readRsBigDecimal(rs, "appPayment");
			this.walletPayment = Database.readRsBigDecimal(rs, "walletPayment");
			this.inhouseSales = Database.readRsBigDecimal(rs, "inhouse");
			this.homeDeliverySales = Database.readRsBigDecimal(rs, "homeDelivery");
			this.takeAwaySales = Database.readRsBigDecimal(rs, "takeAway");
			this.zomatoSale = Database.readRsBigDecimal(rs, "zomatoSale");
			this.swiggySale = Database.readRsBigDecimal(rs, "swiggySale");
			this.uberEatsSale = Database.readRsBigDecimal(rs, "uberEatsSale");
			this.foodPandaSale = Database.readRsBigDecimal(rs, "foodPandaSale");
			this.orderCount = Database.readRsInt(rs, "orderCount");
			this.printCount = Database.readRsInt(rs, "printCount");
			this.reprints = Database.readRsInt(rs, "reprints");
			this.inhouse = Database.readRsInt(rs, "inhouse");
			this.covers = Database.readRsInt(rs, "covers");
			this.tableId = Database.readRsInt(rs, "tableId");
			this.checks = Database.readRsInt(rs, "checks");
			this.discountName = Database.readRsString(rs, "discountName");
			this.cardType = Database.readRsString(rs, "cardType");
			this.loyaltyAmount = Database.readRsBigDecimal(rs, "loyaltyAmount");
			this.complimentary = Database.readRsBigDecimal(rs, "complimentary");
			this.section = Database.readRsString(rs, "section");
			this.grossSale = Database.readRsBigDecimal(rs, "grossTotal");
			this.nc = Database.readRsBigDecimal(rs, "nc");
			this.takeAwayType = Database.readRsInt(rs, "takeAwayType");
			this.creditAmount = Database.readRsBigDecimal(rs, "creditAmount");
			this.roundOff = Database.readRsBigDecimal(rs, "roundOff");
			this.promotionalCash = Database.readRsBigDecimal(rs, "promotionalCash");
			this.externalOrderId = Database.readRsString(rs, "externalOrderId");
			this.reference = Database.readRsString(rs, "reference");
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

		public String getCompleteTime() {
			return completeTime;
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

		public BigDecimal getFoodGross() {
			return foodGross;
		}

		public BigDecimal getBarGross() {
			return barGross;
		}

		public BigDecimal getFoodSale() {
			return foodSale;
		}

		public BigDecimal getBarSale() {
			return barSale;
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

		public BigDecimal getNetSale() {
			return netSale;
		}

		public BigDecimal getServiceCharge() {
			return serviceCharge;
		}

		public BigDecimal getPackagingCharge() {
			return packagingCharge;
		}

		public BigDecimal getDeliveryCharge() {
			return deliveryCharge;
		}

		public BigDecimal getVatBar() {
			return vatBar;
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

		public BigDecimal getWalletPayment() {
			return walletPayment;
		}

		public BigDecimal getTotalPayment() {
			return cardPayment.add(cashPayment).add(appPayment).add(walletPayment);
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

		public BigDecimal getZomatoSale() {
			return zomatoSale;
		}

		public BigDecimal getSwiggySale() {
			return swiggySale;
		}

		public BigDecimal getFoodPandaSale() {
			return foodPandaSale;
		}

		public BigDecimal getUberEatsSale() {
			return uberEatsSale;
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

		public int getCovers() {
			return covers;
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

		public BigDecimal getLoyaltyAmount() {
			return loyaltyAmount;
		}

		public BigDecimal getComplimentary() {
			return complimentary;
		}

		public String getSection() {
			return section;
		}

		public int getTakeAwayType() {
			return takeAwayType;
		}

		public String getCustomerName() {
			return customerName;
		}

		public String getRemarks() {
			return remarks;
		}

		public BigDecimal getGst() {
			return gst;
		}

		public BigDecimal getNc() {
			return nc;
		}

		public BigDecimal getCreditAmount() {
			return creditAmount;
		}

		public BigDecimal getRoundOff() {
			return roundOff;
		}

		public BigDecimal getPromotionalCash() {
			return promotionalCash;
		}

		public String getExternalOrderId() {
			return externalOrderId;
		}

		public String getReference() {
			return reference;
		}

		public BigDecimal getTotalDiscount() {
			return totalDiscount;
		}

		public BigDecimal getTotalBill() {
			return totalBill;
		}
		
	}

	public static class Account implements Database.OrderOnEntity {

		private String hotelId;
		private String accountNumber;
		private String bankName;
		private String accountName;
		private BigDecimal initialBalance;
		//BANK, CASH, ORDER
		private String accountType;

		@Override
		public void readFromDB(ResultSet rs) {

			this.hotelId = Database.readRsString(rs, "hotelId");
			this.accountNumber = Database.readRsString(rs, "accountNumber");
			this.bankName = Database.readRsString(rs, "bankName");
			this.accountName = Database.readRsString(rs, "accountName");
			this.initialBalance = Database.readRsBigDecimal(rs, "balance");
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

		public BigDecimal getInitialBalance() {
			return initialBalance;
		}

		public String getAccountType() {
			return accountType;
		}
	}
	
	public static class AccountLog implements Database.OrderOnEntity {

		private int transactionId;
		private int accountNumber;
		private BigDecimal transAmount;
		//CREDIT/DEBIT
		private String transType;
		private String transDate;
		private String transTime;
		private String outletId;
		private String userId;
		
		public int getTransactionId() {
			return transactionId;
		}

		public int getAccountNumber() {
			return accountNumber;
		}

		public BigDecimal getTransAmount() {
			return transAmount;
		}

		public String getTransType() {
			return transType;
		}

		public String getTransDate() {
			return transDate;
		}

		public String getTransTime() {
			return transTime;
		}

		public String getOutletId() {
			return outletId;
		}

		public String getUserId() {
			return userId;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.transactionId = Database.readRsInt(rs, "transactionId");
			this.accountNumber = Database.readRsInt(rs, "accountNumber");
			this.transAmount = Database.readRsBigDecimal(rs, "transAmount");
			this.transType = Database.readRsString(rs, "transType");
			this.transDate = Database.readRsString(rs, "transDate");
			this.transTime = Database.readRsString(rs, "transTime");
			this.outletId = Database.readRsString(rs, "outletId");
			this.userId = Database.readRsString(rs, "userId");
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
		private String description;
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
		private String invoiceNumber;
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
			this.description = Database.readRsString(rs, "description");
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
			this.invoiceNumber = Database.readRsString(rs, "invoiceNumber");
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

		public String getDescription() {
			return description;
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

		public String getInvoiceNumber() {
			return invoiceNumber;
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
		private int deductionState;
		private String reportForEmail;

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
			this.deductionState = Database.readRsInt(rs, "deductionState");
			this.reportForEmail = Database.readRsString(rs, "reportForEmail");
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

		public int getDeductionState() {
			return deductionState;
		}

		public String getReportForEmail() {
			return reportForEmail;
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

		public void setHotelId(String hotelId) {
			this.hotelId = hotelId;
		}

		public void setCheckoutOrders(int checkoutOrders) {
			this.checkoutOrders = checkoutOrders;
		}

		public void setOutOfStock(int outOfStock) {
			this.outOfStock = outOfStock;
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
		private String collection;
		private int type;
		private String isDisplayable;

		public String getSpecification() {
			return specification;
		}
		public String getCollection() {
			return collection;
		}
		public int getType() {
			return type;
		}
		public Boolean getIsDisplayable() {
			return Boolean.valueOf(isDisplayable);
		}

		@Override
		public void readFromDB(ResultSet rs) {

			specification = Database.readRsString(rs, "specification");
			collection = Database.readRsString(rs, "collection");
			type = Database.readRsInt(rs, "type");
			isDisplayable = Database.readRsString(rs, "isDisplayable");
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
	
	public static class VendorTransaction implements Database.OrderOnEntity {
		private int id;
		private int vendorId;
		private String transType;
		private String account;
		private String paymentType;
		private BigDecimal transAmount;
		private String dateTime;
		private String paymentDate;
		private String userId;
		private String corporateId;
		private String outletId;
		
		public int getId() {
			return id;
		}
		public int getVendorId() {
			return vendorId;
		}
		public String getTransType() {
			return transType;
		}
		public String getAccount() {
			return account;
		}
		public String getPaymentType() {
			return paymentType;
		}
		public BigDecimal getTransAmount() {
			return transAmount;
		}
		public String getDateTime() {
			return dateTime;
		}
		public String getPaymentDate() {
			return paymentDate;
		}
		public String getUserId() {
			return userId;
		}
		public String getCorporateId() {
			return corporateId;
		}
		public String getOutletId() {
			return outletId;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.id = Database.readRsInt(rs, "id");
			this.vendorId = Database.readRsInt(rs, "vendorId");
			this.transType = Database.readRsString(rs, "transType");
			this.transAmount = Database.readRsBigDecimal(rs, "transAmount");
			this.account = Database.readRsString(rs, "account");
			this.paymentType = Database.readRsString(rs, "paymentType");
			this.dateTime = Database.readRsString(rs, "dateTime");
			this.paymentDate = Database.readRsString(rs, "paymentDate");
			this.userId = Database.readRsString(rs, "userId");
			this.corporateId = Database.readRsString(rs, "corporateId");
			this.outletId = Database.readRsString(rs, "outletId");
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
		private String isApplicableOn;
		private String isAlwaysApplicable;
		private BigDecimal minBillAmount;
		private String hasTierWiseValue;
		private String taxesOnCharge;
		private String orderType;
		
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

		public Boolean getIsActive() {
			return Boolean.valueOf(isActive);
		}

		public String getApplicableOn() {
			return applicableOn;
		}

		public String getIsApplicableOn() {
			return isApplicableOn;
		}

		public Boolean getIsAlwaysApplicable() {
			return Boolean.valueOf(isAlwaysApplicable);
		}

		public BigDecimal getMinBillAmount() {
			return minBillAmount;
		}

		public Boolean getHasTierWiseValue() {
			return Boolean.valueOf(hasTierWiseValue);
		}

		public JSONArray getTaxesOnCharge(){
			try {
				return new JSONArray(taxesOnCharge);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public JSONArray getOrderType() {
			try {
				return new JSONArray(orderType);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.value = Database.readRsBigDecimal(rs, "value");
			this.type = Database.readRsString(rs, "type");
			this.isActive = Database.readRsString(rs, "isActive");
			this.applicableOn = Database.readRsString(rs, "applicableOn");
			this.isApplicableOn = Database.readRsString(rs, "isApplicableOn");
			this.isAlwaysApplicable = Database.readRsString(rs, "isAlwaysApplicable");
			this.minBillAmount = Database.readRsBigDecimal(rs, "minBillAmount");
			this.hasTierWiseValue = Database.readRsString(rs, "hasTierWiseValue");
			this.taxesOnCharge = Database.readRsString(rs, "taxesOnCharge");
			this.orderType = Database.readRsString(rs, "orderType");
		}
	}
	
	public static class Tax implements OrderOnEntity {

		private int id;
		private String name;
		private String type;
		private BigDecimal value;
		private String isActive;
		private String applicableOn;
		
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

		public String getApplicableOn() {
			return applicableOn;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.value = Database.readRsBigDecimal(rs, "value");
			this.type = Database.readRsString(rs, "type");
			this.isActive = Database.readRsString(rs, "isActive");
			this.applicableOn = Database.readRsString(rs, "applicableOn");
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
		private String scheduleIds;
		private String description;
		private String imgUrl;
		private String isSpecialCombo;
		private String isActiveOnZomato;
		private String tags;
		
		public String getName() {
			return name;
		}
		
		public int getId() {
			return id;
		}

		public int getCollectionOrder() {
			return collectionOrder;
		}

		public Boolean getHasSubCollection() {
			return Boolean.valueOf(hasSubCollection);
		}

		public String getIsActive() {
			return isActive;
		}

		public JSONArray getScheduleIds() {
			try {
				return new JSONArray(scheduleIds);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}
		
		public String getDescription() {
			return description;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public Boolean getIsSpecialCombo() {
			return Boolean.valueOf(isSpecialCombo);
		}

		public Boolean getIsActiveOnZomato() {
			return Boolean.valueOf(isActiveOnZomato);
		}

		public JSONArray getTags() {
			try {
				return new JSONArray(tags);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		@Override
		public void readFromDB(ResultSet rs) {
			this.name = Database.readRsString(rs, "name");
			this.id = Database.readRsInt(rs, "id");
			this.collectionOrder = Database.readRsInt(rs, "collectionOrder");
			this.hasSubCollection = Database.readRsString(rs, "hasSubCollection");
			this.isActive = Database.readRsString(rs, "isActive");
			this.scheduleIds = Database.readRsString(rs, "scheduleIds");
			this.description = Database.readRsString(rs, "description");
			this.imgUrl = Database.readRsString(rs, "imgUrl");
			this.isSpecialCombo = Database.readRsString(rs, "isSpecialCombo");
			this.isActiveOnZomato = Database.readRsString(rs, "isActiveOnZomato");
			this.tags = Database.readRsString(rs, "tags");
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

		public JSONArray getDays() {
			try {
				return new JSONArray(days);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public JSONArray getTimeSlots() {
			try {
				return new JSONArray(timeSlots);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
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
		private String description;
		private int subCollectionOrder;
		private String collection;
		private String isActive;
		
		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public int getSubCollectionOrder() {
			return subCollectionOrder;
		}

		public String getCollection() {
			return collection;
		}

		public Boolean getIsActive() {
			return Boolean.valueOf(isActive);
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.description = Database.readRsString(rs, "description");
			this.subCollectionOrder = Database.readRsInt(rs, "subCollectionOrder");
			this.collection = Database.readRsString(rs, "collection");
			this.isActive = Database.readRsString(rs, "isActive");
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
		private String title;
		private String subTitle;

		public int getId() {
			return id;
		}

		public JSONArray getItemIds(){
			try {
				return new JSONArray(itemIds);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
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
		
		public String getTitle() {
			return title;
		}
		public String getSubTitle() {
			return subTitle;
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
			this.title = Database.readRsString(rs, "title");
			this.subTitle = Database.readRsString(rs, "subTitle");
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
	
	public static class Designation implements OrderOnEntity {

		private int id;
		private String designation;
		private String hasIncentive;
		
		public int getId() {
			return id;
		}

		public String getDesignation() {
			return designation;
		}

		public Boolean getHasIncentive() {
			return Boolean.valueOf(hasIncentive);
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.designation = Database.readRsString(rs, "designation");
			this.hasIncentive = Database.readRsString(rs, "hasIncentive");
		}
	}
	
	public static class Section implements OrderOnEntity{
		private String barPrinter;
		private String beveragePrinter;
		private String cashierPrinter;
		private String kitchenPrinter;
		private String outDoorPrinter;
		private String summaryPrinter;
		private String name;
		private int id;
		
		public String getBarPrinter() {
			return barPrinter;
		}

		public String getBeveragePrinter() {
			return beveragePrinter;
		}

		public String getCashierPrinter() {
			return cashierPrinter;
		}

		public String getKitchenPrinter() {
			return kitchenPrinter;
		}

		public String getOutDoorPrinter() {
			return outDoorPrinter;
		}

		public String getSummaryPrinter() {
			return summaryPrinter;
		}

		public String getName() {
			return name;
		}

		public int getId() {
			return id;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.barPrinter = Database.readRsString(rs, "barPrinter");
			this.beveragePrinter = Database.readRsString(rs, "beveragePrinter");
			this.cashierPrinter = Database.readRsString(rs, "cashierPrinter");
			this.kitchenPrinter = Database.readRsString(rs, "kitchenPrinter");
			this.outDoorPrinter = Database.readRsString(rs, "outDoorPrinter");
			this.summaryPrinter = Database.readRsString(rs, "summaryPrinter");
		}
		
	}
	
	public static class OnlineOrderingSalesReport implements OrderOnEntity {
		
		private int portalId;
		private String portalName;
		private BigDecimal totalBillAmount;
		private BigDecimal discount;
		private BigDecimal gst;
		private BigDecimal packagingCharge;
		private BigDecimal serviceCharge;
		private BigDecimal deliveryCharge;
		private BigDecimal netAmount;
		private BigDecimal commisionType;
		private BigDecimal commisionValue;
		private BigDecimal commisionAmount;
		private BigDecimal tds;
		
		public int getPortalId() {
			return portalId;
		}
		public String getPortalName() {
			return portalName;
		}
		public BigDecimal getTotalBillAmount() {
			return totalBillAmount;
		}
		public BigDecimal getDiscount() {
			return discount;
		}
		public BigDecimal getGst() {
			return gst;
		}
		public BigDecimal getPackagingCharge() {
			return packagingCharge;
		}
		public BigDecimal getServiceCharge() {
			return serviceCharge;
		}
		public BigDecimal getDeliveryCharge() {
			return deliveryCharge;
		}
		public BigDecimal getNetAmount() {
			return netAmount;
		}
		public BigDecimal getCommisionType() {
			return commisionType;
		}
		public BigDecimal getCommisionValue() {
			return commisionValue;
		}
		public BigDecimal getCommisionAmount() {
			return commisionAmount;
		}
		public BigDecimal getTds() {
			return tds;
		}
		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.portalId = Database.readRsInt(rs, "portalId");
			this.portalName = Database.readRsString(rs, "portalName");
			this.totalBillAmount = Database.readRsBigDecimal(rs, "totalBillAmount");
			this.discount = Database.readRsBigDecimal(rs, "discount");
			this.gst = Database.readRsBigDecimal(rs, "gst");
			this.packagingCharge = Database.readRsBigDecimal(rs, "packagingCharge");
			this.serviceCharge = Database.readRsBigDecimal(rs, "serviceCharge");
			this.deliveryCharge = Database.readRsBigDecimal(rs, "deliveryCharge");
			this.netAmount = Database.readRsBigDecimal(rs, "netAmount");
			this.commisionType = Database.readRsBigDecimal(rs, "commisionType");
			this.commisionValue = Database.readRsBigDecimal(rs, "commisionValue");
			this.commisionAmount = Database.readRsBigDecimal(rs, "commisionAmount");
			this.tds = Database.readRsBigDecimal(rs, "tds");
		}
	}
	
	public static class PromotionalCampaign implements OrderOnEntity{

		private int id;
		private String name;
		private String messageContent;
		private String usageDetails;
		private int totalSMSSent;
		private String outletId;
		private String outletIds;
		private String userTypes;
		private String sex;
		private String ageGroup;
		private String status;
		
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		public String getMessageContent() {
			return messageContent;
		}
		public JSONObject getUsageDetails() {
			try {
				if(!usageDetails.isEmpty()) {
					return new JSONObject(usageDetails);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new JSONObject();
		}
		public int getTotalSMSSent() {
			return totalSMSSent;
		}
		public String getOutletId() {
			return outletId;
		}
		
		public JSONArray getOutletIds() {
			try {
				if(!outletIds.isEmpty()) {
					return new JSONArray(outletIds);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new JSONArray();
		}
		public JSONArray getUserTypes() {
			try {
				if(!userTypes.isEmpty()) {
					return new JSONArray(userTypes);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new JSONArray();
		}
		public String getSex() {
			return sex;
		}
		public JSONArray getAgeGroup() {
			try {
				if(!userTypes.isEmpty()) {
					return new JSONArray(ageGroup);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new JSONArray();
		}
		public String getStatus() {
			return status;
		}
		
		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.name = Database.readRsString(rs, "name");
			this.messageContent = Database.readRsString(rs, "messageContent");
			this.usageDetails = Database.readRsString(rs, "usageDetails");
			this.totalSMSSent = Database.readRsInt(rs, "totalSMSSent");
			this.outletId = Database.readRsString(rs, "outletId");
			this.outletIds = Database.readRsString(rs, "outletIds");
			this.userTypes = Database.readRsString(rs, "userTypes");
			this.sex = Database.readRsString(rs, "sex");
			this.ageGroup = Database.readRsString(rs, "ageGroup");
			this.status = Database.readRsString(rs, "status");
		}
	}
	
	public static class Combo {
	
		private String outletId; 
		private MenuItem comboItem;
		private ArrayList<Group> groups;
		private Collection collection;
		private ArrayList<SubCollection> subCollections;
		
		public String getOutletId() {
			return outletId;
		}

		public MenuItem getComboItem() {
			return comboItem;
		}

		public ArrayList<Group> getGroups() {
			return groups;
		}

		public Collection getCollection() {
			return collection;
		}

		public ArrayList<SubCollection> getSubCollections() {
			return subCollections;
		}

		public void setOutletId(String outletId) {
			this.outletId = outletId;
		}

		public void setComboItem(MenuItem comboItem) {
			this.comboItem = comboItem;
		}

		public void setGroups(ArrayList<Group> groups) {
			this.groups = groups;
		}

		public void setCollection(Collection collection) {
			this.collection = collection;
		}

		public void setSubCollection(ArrayList<SubCollection> subCollections) {
			this.subCollections = subCollections;
		}
	}
	
	public static class DBTransaction implements OrderOnEntity {
	
		private int id; 
		private String transaction;
		
		public int getId() {
			return id;
		}

		public String getTransaction() {
			return transaction;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.transaction = Database.readRsString(rs, "transactions");
		}
	}
	
	public static class ReportBuffer implements OrderOnEntity {
	
		private int id; 
		private String smsText;
		private String subject;
		private String emailText;
		private String mobileNumbers;
		private String emailIds;
		private String outletId;
		
		public int getId() {
			return id;
		}

		public String getSmsText() {
			return smsText;
		}

		public String getSubject() {
			return subject;
		}

		public String getEmailText() {
			return emailText;
		}

		public JSONArray getMobileNumbers() {
			try {
				if(!mobileNumbers.isEmpty()) {
					return new JSONArray(mobileNumbers);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public JSONArray getEmailIds() {
			try {
				if(!mobileNumbers.isEmpty()) {
					return new JSONArray(emailIds);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new JSONArray();
		}

		public String getOutletId() {
			return outletId;
		}

		@Override
		public void readFromDB(ResultSet rs) {
			// TODO Auto-generated method stub
			this.id = Database.readRsInt(rs, "id");
			this.smsText = Database.readRsString(rs, "smsText");
			this.subject = Database.readRsString(rs, "subject");
			this.emailText = Database.readRsString(rs, "emailText");
			this.mobileNumbers = Database.readRsString(rs, "mobileNumbers");
			this.emailIds = Database.readRsString(rs, "emailIds");
			this.outletId = Database.readRsString(rs, "outletId");
		}
	}

	public ArrayList<KitchenDisplayOrders> getKDSOrdersListView(String hotelId) {

		IOutlet outlet = new OutletManager(false);
		Settings setting = outlet.getSettings(hotelId);
		String hotelType = setting.getHotelType();
		IService serviceDao = new ServiceManager(false);

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
				+ " AND Orders.orderDate LIKE '%" + serviceDao.getServiceDate(hotelId) + "%'"
				+ " ORDER BY OrderItems.orderId ASC;";

		return db.getRecords(sql, KitchenDisplayOrders.class, hotelId);
	}

	// ----------------------------------------------------Station

	public ArrayList<KitchenStation> getKitchenStations(String hotelId) {
		String sql = "SELECT * FROM Stations WHERE hotelId='" + hotelId + "';";
		return db.getRecords(sql, KitchenStation.class, hotelId);
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
			BigDecimal mswipe, BigDecimal rupay, BigDecimal zomato, BigDecimal zomatoPickup, BigDecimal zomatopay, BigDecimal swiggy, BigDecimal dineOut, BigDecimal paytm,
			BigDecimal foodPanda, BigDecimal uberEats, BigDecimal foodiloo, BigDecimal nearby, BigDecimal swiggyPop, BigDecimal googlePay, BigDecimal magicPin,
			BigDecimal complimentary, BigDecimal difference, String reason, String clearance, String section) {

		String sql = "INSERT INTO TotalRevenue "
				+ "(hotelId, serviceType, serviceDate, cash, card, app, total, visa, mastercard, maestro, amex, "
				+ "others, mswipe, rupay, zomato, zomatoPickup, zomatoPay, swiggy, foodPanda, uberEats, foodiloo, dineOut, paytm, nearBy, "
				+ "swiggyPop, googlePay, magicPin, complimentary, difference, reason, clearance, section) "
				+ "VALUES('" + escapeString(hotelId) + "', '" + escapeString(serviceType) + "', '"
				+ escapeString(serviceDate) + "', " + cash + ", " + card + ", "
				+ app + ", " + total + ", " + visa + ", "
				+ mastercard + ", " + maestro + ", " + amex + ", "
				+ others + ", " + mswipe + ", " + rupay + ", "
				+ zomato + ", "+ zomatoPickup + ", "+  zomatopay + ", " + swiggy + ", " + foodPanda + ", " 
				+ uberEats + ", " + foodiloo + ", "+ dineOut + ", "
				+ paytm + ", " + nearby + ", " + complimentary + ", "
				+ swiggy + ", " + googlePay + ", " + magicPin + ", " + difference
				+ ", '" + escapeString(reason) + "', '" + escapeString(clearance) + "', '" + escapeString(section) + "');";
		return db.executeUpdate(sql, true);
	}
	
	public static String toTitleCase(String init) {
		if (init == null)
		    return null;
		
		StringBuilder ret = new StringBuilder(init.length());
		
		for (String word : init.split(" ")) {
			if (!word.isEmpty()) {
			    ret.append(Character.toUpperCase(word.charAt(0)));
			    ret.append(word.substring(1).toLowerCase());
			}
			if (!(ret.length() == init.length()))
			    ret.append(" ");
		}
		
		return ret.toString();
	}
}