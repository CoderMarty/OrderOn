package com.orderon.interfaces;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.orderon.dao.AccessManager.Customer;
import com.orderon.dao.AccessManager.LoyaltyOffer;
import com.orderon.dao.AccessManager.Order;

public interface ILoyalty {

	public JSONObject addLoyaltyOffer(String name, int offerType, int points, String offerValue, String hasUsageLimit,
			int usageLimit, int minBill, String userType, String validCollections, String status, String startDate,
			String expiryDate, String hotelId, String chainId, int offerQuantity);

	public LoyaltyOffer getLoyaltyOfferById(String hotelId, int id);

	public ArrayList<LoyaltyOffer> getAllLoyaltyOffers(String hotelId);

	public ArrayList<LoyaltyOffer> getAllLoyaltyOffersByChain(String chainId);

	public ArrayList<LoyaltyOffer> getAllLoyaltyOffersForCustomer(String hotelId, Customer customer);

	public JSONObject editLoyaltyOffer(int id, int offerType, int points, String offerValue, String hasUsageLimit,
			int usageLimit, int minBill, String userType, String validCollections, String status, String startDate,
			String expiryDate, String hotelId, String chainId, int offerQuantity) throws ParseException;

	public JSONObject redeemLoyaltyOffer(String hotelId, Order order, int loyaltyId, int redeemablePoints, Customer customer);
	
	public Boolean addLoyaltyPoints(String hotelId, String orderId, int points, Customer customer);
}
