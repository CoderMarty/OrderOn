package com.orderon.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.ICustomer;
import com.orderon.interfaces.ICustomerCredit;
import com.orderon.interfaces.ILoyaltySettings;
import com.orderon.interfaces.IOrder;
import com.orderon.interfaces.IPromotionalCampaign;
import com.orderon.interfaces.IService;

public class CustomerManager extends AccessManager implements ICustomer, ICustomerCredit, IPromotionalCampaign{

	public CustomerManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Boolean customerExists(String outletId, String mobileNumber) {
		Customer customer = getCustomerDetails(outletId, mobileNumber);
		if (customer != null) {
			return true;
		}
		return false;
	}

	@Override
	public Boolean addCustomer(String hotelId, String firstName, String surName, String phone, String address, String birthdate,
			String anniversary, String allergyInfo, Boolean wantsPromotion, Boolean isPriorityCust, String emailId, String sex, 
			String referenceForReview) {
		if(phone.isEmpty())
			return false;
		ILoyaltySettings loyalty = new LoyaltyManager(false);
		String sql = "INSERT INTO Customers (hotelId, firstName, surName, address, mobileNumber, birthdate, anniversary, allergyInfo, "
				+ "points, wantsPromotion, isPriority, userType, emailId, sex, reference, communicationMode, sendSMS, joiningDate) VALUES ('"
				+ escapeString(hotelId) + "', '" + escapeString(firstName) + "', '" + escapeString(surName) + "', '" + escapeString(address) + "', '"
				+ escapeString(phone) + "', '" + escapeString(birthdate) + "', '" + escapeString(anniversary) + "', '"
				+ escapeString(allergyInfo) + "', 0, '" + wantsPromotion + "', '"+ isPriorityCust + "', '"+loyalty.getBaseLoyaltySetting(hotelId).getUserType()
				+ "', '"+escapeString(emailId)+ "', '"+escapeString(sex)+"', '"+escapeString(referenceForReview)+"', '[]', 'true', '"+(new SimpleDateFormat("yyyy/MM/dd")).format(new Date())+"');";
		return db.executeUpdate(sql, hotelId, true);
	}
	
	@Override
	public boolean addAllCustomers(String outletId, JSONArray customers) {
		
		JSONObject customer = new JSONObject();
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO Customers (corporateId, hotelId, id, mobileNumber, firstName, surName , address, birthdate, anniversary, userType, "
				+ "allergyInfo, wantsPromotion, referalCode, communicationMode, sex, visitCount, emailId, reference, " 
				+ "isBlocked, isVerified, remarks, points, isPriority, joiningDate, lastRechargeDate, lastVisitDate) VALUES ");
		
		try {
			for(int i=0; i<customers.length(); i++) {
				customer = customers.getJSONObject(i);
				if(i>0) {
					sql.append(", ");
				}
				sql.append("('"+ escapeString(customer.getString("corporateId")) + "', '" 
						+ escapeString(customer.getString("hotelId")) + "', "
								+ customer.getInt("id")+" ,'" 
								+ escapeString(customer.getString("mobileNumber")) + "', '" 
								+ escapeString(customer.getString("firstName")) + "', '" 
								+ escapeString(customer.getString("surName")) + "', '" 
								+ escapeString(customer.getString("address")) + "', '" 
								+ escapeString(customer.getString("birthdate")) + "', '" 
								+ escapeString(customer.getString("anniversary")) + "', '"
								+ escapeString(customer.getString("userType")) + "', '" 
								+ escapeString(customer.getString("allergyInfo")) + "', '" 
								+ customer.getBoolean("wantsPromotion") + "', '" 
								+ escapeString(customer.getString("referalCode")) + "', '" 
								+ customer.getJSONArray("communicationModes") + "', '" 
								+ escapeString(customer.getString("sex")) + "', "
								+ customer.getInt("visitCount")+", '" 
								+ escapeString(customer.getString("emailId"))+ "', '" 
								+ escapeString(customer.getString("reference"))+ "', '" 
								+ customer.getBoolean("isBlocked")+ "', '" 
								+ customer.getBoolean("isVerified")+ "', '" 
								+ escapeString(customer.getString("remarks"))+ "', " 
								+ customer.getInt("points")+ ", '" 
								+ customer.getBoolean("isPriority")+ "', '" 
								+ escapeString(customer.getString("joiningDate"))+ "', '" 
								+ escapeString(customer.getString("lastRechargeDate"))+ "', '" 
								+ escapeString(customer.getString("lastVisitDate"))+ "')");
			}
			if(!db.executeUpdate(sql.toString(), outletId, false)) {
				return false;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public Boolean updateCustomer(String hotelId, Integer id, String firstName, String surName, String phone, String birthdate,
			String anniversary, String remarks, String allergyInfo, String address, Boolean wantsPromotion, String emailId, String sex) {

		surName = surName.equals("") ? "" : "', surName='" + escapeString(surName);
		allergyInfo = allergyInfo.equals("") ? "" : "', allergyInfo='" + escapeString(allergyInfo);
		remarks = remarks.equals("") ? "" : "', remarks='" + escapeString(remarks);
		birthdate = birthdate.equals("") ? "" : "', birthdate='" + escapeString(birthdate);
		anniversary = anniversary.equals("") ? "" : "', anniversary='" + escapeString(anniversary);
		address = address.equals("") ? "" : "', address='" + escapeString(address);
		emailId = emailId.equals("") ? "" : "', emailId='" + escapeString(emailId);
		sex = sex.equals("") ? "" : "', sex='" + escapeString(sex);
		
		Customer customer = null;
		if(id == null) {
			customer = this.getCustomerDetails(hotelId, phone);
		}else {
			customer = this.getCustomerById(hotelId, id);
		}
		boolean updateCustomer = false;
		
		if(!customer.getFirstName().equals(firstName))
			updateCustomer = true;
		else if(!customer.getSurName().equals(surName))
			updateCustomer = true;
		else if(!customer.getAddress().equals(address))
			updateCustomer = true;
		else if(!customer.getBirthdate().equals(birthdate))
			updateCustomer = true;
		else if(!customer.getAnniversary().equals(anniversary))
			updateCustomer = true;
		else if(!customer.getAllergyInfo().equals(address))
			updateCustomer = true;
		else if(!customer.getRemarks().equals(remarks))
			updateCustomer = true;

		if(updateCustomer) {
			String sql = "";
			if(id == null) {
				sql = "UPDATE Customers SET firstName='" + escapeString(firstName) + surName + birthdate + anniversary
				+ allergyInfo + remarks + address + emailId + sex + "', wantsPromotion = '" + wantsPromotion + "' WHERE mobileNumber='"
				+ escapeString(phone) + "';";
			}else {
				sql = "UPDATE Customers SET firstName='" + escapeString(firstName) + surName + birthdate + anniversary
				+ allergyInfo + remarks + address + emailId + sex + "', wantsPromotion = '" + wantsPromotion + "', mobileNumber='"
				+ escapeString(phone) + "' WHERE id = "+id+";";
			}
			return db.executeUpdate(sql, hotelId, true);
		}
		return true;
	}

	@Override
	public Boolean incrementVisitCount(String hotelId, Customer customer) {

		int visitCount = customer.getVisitCount()+1;
		String sql = "UPDATE Customers SET visitCount="+ visitCount +", lastVisitDate = '"+(new SimpleDateFormat("yyyy/MM/dd")).format(new Date())+"' WHERE mobileNumber='"
				+ customer.getMobileNumber() +"';";
		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public Boolean hasCustomer(String hotelId, String phone) {
		String sql = "SELECT * FROM customers WHERE mobileNumber='" + phone + "';";
		return db.hasRecords(sql, hotelId);
	}
	
	@Override
	public Boolean editCustomerDetails(String hotelId, String orderId, String customerName, String number, String address,
			int noOfGuests, String allergyInfo) {
		String firstName = customerName;
		String surName = "";
		if(customerName.contains(" ")) {
			firstName = customerName.split(" ")[0];
			surName = customerName.split(" ")[1];
		}
		return this.editCustomerDetails(hotelId, orderId, firstName, surName, number, address, noOfGuests, allergyInfo, "", "NONE");
	}

	@Override
	public Boolean editCustomerDetails(String hotelId, String orderId, String firstName, String surName, String number, String address,
			int noOfGuests, String allergyInfo, String emailId, String sex) {
		
		IOrder orderDao = new OrderManager(false);
		Order order = orderDao.getOrderById(hotelId, orderId);
		
		Customer customer = this.getCustomerDetails(hotelId, number);
		if (firstName.equals("")) {
			String customerName = order.getCustomerName();
			if(customerName.contains(" ")) {
				firstName = customerName.split(" ")[0];
				surName = customerName.split(" ")[1];
			}else {
				firstName = customerName;
			}
		}
		if (address.equals(""))
			address = order.getCustomerAddress();
		if (noOfGuests == 0)
			noOfGuests = order.getNumberOfGuests();
		String sql = "UPDATE Orders SET customerName ='" + escapeString(firstName + " " + surName) + "', numberOfGuests = "
				+ Integer.toString(noOfGuests) + ", customerNumber = '" + escapeString(number)
				+ "', customerAddress = '" + escapeString(address)
				+ "' WHERE orderId = '" + escapeString(orderId) + "';";

		boolean hasUpdated = db.executeUpdate(sql, hotelId, true);
		if (hasUpdated) {
			if (customer == null) {
				this.addCustomer(hotelId, firstName, surName, number, address, "", "", allergyInfo, Boolean.FALSE, Boolean.FALSE, emailId, sex, "");
			} else {
				this.updateCustomer(hotelId, null, firstName, surName, number, "", "", "", allergyInfo, address,
						customer.getWantsPromotion() == null ? false : customer.getWantsPromotion(), emailId, "");
			}
			return true;
		}
		return false;
	}

	@Override
	public ArrayList<Customer> getCustomersForSMS(String hotelId) {
		
		IService serviceDao = new ServiceManager(false);
		
		String sql = "SELECT distinct Customers.mobileNumber, Customers.firstName, Orders.completeTimestamp, Orders.orderId"
				+ " FROM Customers, Orders WHERE Orders.hotelId='" + hotelId
				+ "' AND Orders.isSmsSent = 0 AND Orders.customerNumber == Customers.mobileNumber AND Orders.state == "
				+ORDER_STATE_COMPLETE+" AND Orders.orderDate == '"+serviceDao.getServiceDate(hotelId)+"';";
		return db.getRecords(sql, Customer.class, hotelId);
	}

	@Override
	public Customer getCustomerDetails(String hotelId, String mobileNumber) {
		String sql = "SELECT * FROM Customers WHERE mobileNumber='" + mobileNumber + "';";
		return db.getOneRecord(sql, Customer.class, hotelId);
	}

	@Override
	public int getCustomerPoints(String hotelId, String mobileNumber) {
		String sql = "SELECT points AS entityId FROM Customers WHERE mobileNumber='" + mobileNumber + "';";
		return db.getOneRecord(sql, EntityId.class, hotelId).getId();
	}

	@Override
	public ArrayList<Customer> getAllCustomerDetails(String hotelId, int page) {
		int offset = (page-1)*100;
		String sql = "SELECT * FROM Customers LIMIT 100 OFFSET "+offset+";";
		return db.getRecords(sql, Customer.class, hotelId);
	}

	@Override
	public ArrayList<Customer> getAllCustomers(String hotelId) {
		String sql = "SELECT * FROM Customers;";
		return db.getRecords(sql, Customer.class, hotelId);
	}

	@Override
	public ArrayList<CustomerForOrdering> getCustomersForOrdering(String hotelId) {
		String sql = "SELECT mobileNumber, firstName, surName, emailId, address FROM Customers;";
		return db.getRecords(sql, CustomerForOrdering.class, hotelId);
	}

	@Override
	public ArrayList<Customer> getAllCustomerDetailsBySearch(String hotelId, String query) {
		String sql = "SELECT * FROM Customers WHERE (mobileNumber LIKE '%" + query 
				+ "%' OR firstName LIKE '%" + query + "%' OR surName LIKE '%" + query + "%');";
		return db.getRecords(sql, Customer.class, hotelId);
	}

	@Override
	public ArrayList<Customer> getAllCustomersByFilters(String hotelId, JSONArray outletIds, JSONArray userTypes, 
			String sex, JSONArray ageGroups) {
		String sql = "SELECT * FROM Customers WHERE (";
		
		int offset = outletIds.length();
		for (int i=0; i<outletIds.length(); i++) {
			try {
				sql += "hotelId == '" + outletIds.getString(i) + "'";
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			offset--;
			if(offset == 0){
				sql += ") ";
			}else {
				sql += " OR ";
			}
		}
		boolean checkForUserTypes = true;
		for (int i=0; i<userTypes.length(); i++) {
			try {
				if(userTypes.getString(i).equals("All")) {
					checkForUserTypes = false;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(checkForUserTypes) {
			if(userTypes.length()>0) {
				sql += "AND (";
			}
			offset = userTypes.length();
			for (int i=0; i<userTypes.length(); i++) {
				try {
					sql += "userType == '" + userTypes.getString(i) + "'";
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				offset--;
				if(offset == 0){
					sql += ") ";
				}else {
					sql += " OR ";
				}
			}
		}
		
		//Gender/Sex criteria
		if(!sex.equals("ALL")) {
			sql += "AND sex = '"+sex+"' ";
		}
		/*
		//Age group criteria
		try {
			if(ageGroups.length()>0) {
				JSONObject ageGroup = ageGroups.getJSONObject(0);
				int minAge = ageGroup.getInt("minAge");
				int maxAge = ageGroup.getInt("maxAge");
				
				if(minAge !=0 && maxAge !=0) {

					LocalDateTime date = LocalDateTime.now();
					int year = date.getYear();
					String minDate = (year-minAge) + "/" + date.getMonth().getValue() + "/" + date.getDayOfMonth();
					String maxDate = (year-maxAge) + "/" + date.getMonth().getValue() + "/" + date.getDayOfMonth();
							
					sql += "AND birthDate BETWEEN '" + maxDate + "' AND '"+minDate+"';";
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		return db.getRecords(sql, Customer.class, hotelId);
	}

	@Override
	public ArrayList<Customer> getAllCustomerDetailsForOrdering(String hotelId) {
		String sql = "SELECT id, mobileNumber, firstName, surName, address, sex, emailId FROM Customers;";
		return db.getRecords(sql, Customer.class, hotelId);
	}

	@Override
	public Customer getCustomerBySearch(String hotelId, String query) {

		String sql = "SELECT * FROM Customers WHERE mobileNumber LIKE '%" + query + "%';";

		return db.getOneRecord(sql, Customer.class, hotelId);
	}

	@Override
	public JSONObject addCustomerCredit(String outletId, String mobileNumber, BigDecimal amount, String orderId) {
		JSONObject outObj = new JSONObject();
		
		try {
			IService serviceDao = new ServiceManager(false);
			String sql = "INSERT INTO CustomerCreditLog (outletId, mobileNumber, amount, orderId, state, transDate) "
					+ "VALUES ('"+outletId+"', '"+mobileNumber+"', "+amount+", '"+orderId+"', '"+
					CUSTOMER_CREDIT_UNSETTLED+"', '"+serviceDao.getServiceDate(outletId)+"')";
			
			outObj.put("status", db.executeUpdate(sql, outletId, true));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public JSONObject settleCustomerCredit(String outletId, String mobileNumber, String orderId, String paymentType) {
		JSONObject outObj = new JSONObject();
		
		try {
			String sql = "UPDATE CustomerCreditLog SET "
					+ "'state' = '" + CUSTOMER_CREDIT_SETTLED+"',"
					+ "'paymentType' = '" + paymentType+"' "
					+ "WHERE outletId = '"+outletId+"' AND mobileNumber = '"+mobileNumber+"' AND "
					+ "orderId = '"+orderId+"';";
			
			outObj.put("status", db.executeUpdate(sql, outletId, true));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public ArrayList<CustomerCreditLog> getCreditLog(String outletId, String startDate){
		return getCreditLog(outletId, startDate, "");
	}
	
	@Override
	public ArrayList<CustomerCreditLog> getCreditLog(String outletId, String startDate, String endDate){
		
		String dateQuery = "";
		if(!endDate.equals("")) {
			dateQuery = "BETWEEN '"+ startDate + "' AND '"+ endDate + "';";
		}else {
			dateQuery = "= '"+ startDate + "';";
		}
		
		String sql = "SELECT * FROM CustomerCreditLog WHERE outletId ='"+ outletId + "' AND transDate " + dateQuery;
		
		return db.getRecords(sql, CustomerCreditLog.class, outletId);
	}

	@Override
	public boolean deleteAllCustomers(String outletId) {
		
		String sql = "DELETE FROM Customers;";
		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public JSONObject toggleSMS(String outletId, String mobileNumber) {
		
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			Customer customer = this.getCustomerDetails(outletId, mobileNumber);
			if(customer == null) {
				outObj.put("message", "Customer does not exist");
				return outObj;
			}
			boolean status = customer.getSendSMS()?false:true;
			String sql = "UPDATE Customers SET sendSMS = '"+status+"' WHERE mobileNumber = '"+mobileNumber+"';";
			outObj.put("status", db.executeUpdate(sql, outletId, true));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public Customer getCustomerById(String hotelId, int id) {
		
		String sql = "SELECT * FROM Customers WHERE id = "+id+";";
		
		return db.getOneRecord(sql, Customer.class, hotelId);
	}

	@Override
	public boolean addCampaign(String outletId, String name, String messageContent, JSONArray outletIds, JSONArray userTypes,
			String sex, JSONArray ageGroup) {
		String sql = "INSERT INTO PromotionalCampaign (outletId, name, messageContent, outletIds, userTypes, sex, ageGroup, status) VALUES ('"
				+ escapeString(outletId) + "', '" + escapeString(name) + "', '" + escapeString(messageContent) + "', '" + outletIds.toString() + "', '"
				+ userTypes.toString() + "', '" + sex + "', '"+ageGroup+"', 'DRAFTED');";
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public ArrayList<PromotionalCampaign> getPromotionalCampaigns(String outletId) {
		
		String sql = "SELECT * FROM PromotionalCampaign WHERE outletId = '"+outletId+"';";
		return db.getRecords(sql, PromotionalCampaign.class, outletId);
	}

	@Override
	public PromotionalCampaign getPromotionalCampaignById(String outletId, int id) {
		
		String sql = "SELECT * FROM PromotionalCampaign WHERE outletId = '"+outletId+"' AND id = "+id+";";
		return db.getOneRecord(sql, PromotionalCampaign.class, outletId);
	}

	@Override
	public boolean deleteCampaign(String outletId, int id) {
		
		String sql = "DELETE FROM PromotionalCampaign WHERE outletId = '"+outletId+"' AND id = "+id+";";
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public boolean updateCampaign(String outletId, int campaignId, int totalSmsSent, int totalCustomerCount, 
			int failedSmsCount, int newBalance) {

		JSONObject usageDetail = new JSONObject();
		try {
			usageDetail.put("dateTime", LocalDateTime.now());
			usageDetail.put("totalSmsSent", totalSmsSent);
			usageDetail.put("totalCustomers", totalCustomerCount);
			usageDetail.put("failedCount", failedSmsCount);
			usageDetail.put("newBalance", newBalance);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String sql = "UPDATE PromotionalCampaign SET usageDetails = '"+usageDetail.toString()+"', status='SENT' WHERE id = "+campaignId+"";
		
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public boolean editCampaign(String outletId, int campaignId, String name, String messageContent,
			JSONArray outletIds, JSONArray userTypes, String sex, JSONArray ageGroup) {
		
		String sql = "UPDATE PromotionalCampaign SET name = '"+name
				+"', messageContent='"+messageContent
				+"', outletIds='"+outletIds.toString()
				+"', userTypes='"+userTypes.toString()
				+"', sex='"+sex
				+"', ageGroup='"+ageGroup.toString()
				+"' WHERE id = "+campaignId+"";
		
		return db.executeUpdate(sql, outletId, true);
	}
}
