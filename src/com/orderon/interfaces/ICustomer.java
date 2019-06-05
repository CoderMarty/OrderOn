package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Customer;
import com.orderon.dao.AccessManager.CustomerForOrdering;

public interface ICustomer extends IAccess{

	public Boolean customerExists(String outletId, String mobileNumber);
	
	public Boolean addCustomer(String hotelId, String firstName, String surName, String phone, String address, String birthdate,
			String anniversary, String allergyInfo, Boolean wantsPromotion, Boolean isPriorityCust, String emailId, String referenceForReview);
	
	public Boolean updateCustomer(String hotelId, Integer id, String firstName, String surName, String phone, String birthdate,
			String anniversary, String remarks, String allergyInfo, String address, Boolean wantsPromotion, String emailId, String sex);

	public Boolean incrementVisitCount(String hotelId, Customer customer);

	public Boolean hasCustomer(String hotelId, String phone);

	public Boolean editCustomerDetails(String hotelId, String orderId, String customerName, String number, String address,
			int noOfGuests, String allergyInfo);

	public Boolean editCustomerDetails(String hotelId, String orderId, String firstName, String surName, String number, String address,
			int noOfGuests, String allergyInfo);

	public Customer getCustomerById(String hotelId, int id);
	
	public ArrayList<Customer> getCustomersForSMS(String hotelId);

	public ArrayList<Customer> getAllCustomers(String hotelId);
	
	public ArrayList<CustomerForOrdering> getCustomersForOrdering(String hotelId);

	public Customer getCustomerDetails(String hotelId, String mobileNumber);

	public int getCustomerPoints(String hotelId, String mobileNumber);

	public ArrayList<Customer> getAllCustomerDetails(String hotelId, int page);

	public ArrayList<Customer> getAllCustomerDetailsBySearch(String hotelId, String query);

	public ArrayList<Customer> getAllCustomerDetailsForOrdering(String hotelId);

	public Customer getCustomerBySearch(String hotelId, String query);
	
	public boolean addAllCustomers(String outletId, JSONArray customers);
	
	public boolean deleteAllCustomers(String outletId);
	
	public JSONObject toggleSMS(String outletId, String mobileNumber);
}
