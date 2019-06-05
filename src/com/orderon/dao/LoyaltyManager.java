package com.orderon.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.ILoyalty;
import com.orderon.interfaces.ILoyaltySettings;
import com.orderon.interfaces.IMenuItem;
import com.orderon.interfaces.IOrderItem;
import com.orderon.interfaces.ITable;

public class LoyaltyManager extends AccessManager implements ILoyalty, ILoyaltySettings{

	public LoyaltyManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
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

	@Override
	public LoyaltyOffer getLoyaltyOfferById(String hotelId, int id) {
		String sql = "SELECT * FROM LoyaltyOffers WHERE hotelId = '" + hotelId + "' AND id = " + id + ";";

		return db.getOneRecord(sql, LoyaltyOffer.class, hotelId);
	}

	@Override
	public ArrayList<LoyaltyOffer> getAllLoyaltyOffers(String hotelId) {
		String sql = "SELECT * FROM LoyaltyOffers WHERE hotelId = '" + hotelId + "';";

		return db.getRecords(sql, LoyaltyOffer.class, hotelId);
	}

	@Override
	public ArrayList<LoyaltyOffer> getAllLoyaltyOffersByChain(String chainId) {
		String sql = "SELECT * FROM LoyaltyOffers WHERE chainId = '" + chainId + "';";

		return db.getRecords(sql, LoyaltyOffer.class, chainId);
	}

	@Override
	public ArrayList<LoyaltyOffer> getAllLoyaltyOffersForCustomer(String hotelId, Customer customer) {
		String sql = "SELECT * FROM LoyaltyOffers WHERE hotelId = '" + hotelId + "' AND status = 'true' AND points <= "
				+ customer.getPoints() + " AND "
				+ "((SELECT requiredPoints FROM LoyaltySettings WHERE LoyaltySettings.userType == LoyaltyOffers.userType)  <= "
				+ customer.getPoints() + ");";

		return db.getRecords(sql, LoyaltyOffer.class, hotelId);
	}

	@Override
	public JSONObject editLoyaltyOffer(int id, int offerType, int points, String offerValue, String hasUsageLimit,
			int usageLimit, int minBill, String userType, String validCollections, String status, String startDate,
			String expiryDate, String hotelId, String chainId, int offerQuantity) throws ParseException {

		JSONObject outObj = new JSONObject();

		try {
			outObj.put("status", false);

			if (offerType == PRODUCT_LOYALTY_OFFER) {
				IMenuItem menuDao = new MenuItemManager(false);
				MenuItem item = menuDao.itemExists(hotelId, offerValue);
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

	@Override
	public JSONObject redeemLoyaltyOffer(String hotelId, Order order, int loyaltyId, int redeemablePoints, Customer customer) {

		JSONObject outObj = new JSONObject();
		int requiredPoints = this.getRequiredLoyaltyPoints(hotelId, loyaltyId);
		LoyaltySetting setting = this.getLoyaltySettingByUserType(hotelId, customer.getUserType());
		LoyaltyOffer loyalty = this.getLoyaltyOfferById(hotelId, loyaltyId);
		int balancePoints = 0;
		JSONObject discountObj = new JSONObject();
		JSONArray collectionArr = new JSONArray();
		IOrderItem orderDao = new OrderManager(false);

		try {
			outObj.put("status", false);

			if (loyalty.getMinBill() > orderDao.getTotalBillAmount(hotelId, order.getOrderId())) {
				outObj.put("message", "This offer requires minimum billing of " + loyalty.getMinBill()
						+ ". (Bill Amount without Tax and Before Discount.)");
				return outObj;
			}

			if (loyalty.getOfferType() == CASH_LOYALTY_OFFER) {

				if (customer.getPoints() < redeemablePoints) {
					outObj.put("message", "Redeemable Points should be less than or equal to the points in Wallet.");
					return outObj;
				}
				requiredPoints = redeemablePoints;

			} else if (loyalty.getOfferType() == PERCENTAGE_LOYALTY_OFFER) {

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
				ArrayList<OrderItem> orderItems = orderDao.getOrderedItems(hotelId, order.getOrderId(), false);
				boolean complimetaryAdded = false;
				int excessQty = loyalty.getOfferQuantity();
				for (OrderItem orderItem : orderItems) {
					if (orderItem.getState() == SUBORDER_STATE_CANCELED || orderItem.getState() == SUBORDER_STATE_COMPLIMENTARY
							|| orderItem.getState() == SUBORDER_STATE_RETURNED || orderItem.getState() == SUBORDER_STATE_VOIDED)
						continue;
					if (loyalty.getOfferValue().equals(orderItem.getMenuId())) {
						excessQty -= orderItem.getQty();
						int qty = excessQty <= 0 ? loyalty.getOfferQuantity() : orderItem.getQty();
						complimetaryAdded = orderDao.complimentaryItem(hotelId, order.getOrderId(), orderItem.getMenuId(), "",
								orderItem.getSubOrderId(), orderItem.getRate(), qty, loyalty.getName());
						if (!complimetaryAdded) {
							outObj.put("message", "Offer could not be applied. Please contact support. Code 50");
							return outObj;
						}
						if (excessQty <= 0)
							break;
					}
				}
				MenuItem item = null;
				if (excessQty > 0) {
					ITable tableDao = new TableManager(false);
					String tableType = tableDao.getTableType(hotelId, order.getTableId().split(",")[0]);
					IMenuItem menuDao = new MenuItemManager(false);
					item = menuDao.getMenuById(hotelId, loyalty.getOfferValue());
					Double rate = item.getDineInRate().doubleValue();
					if(order.getInHouse() == HOME_DELIVERY || (order.getInHouse() == TAKE_AWAY && order.getTakeAwayType() == COUNTER_PARCEL_ORDER)) 
						rate = item.getDeliveryRate().doubleValue();
					else if(order.getInHouse() == TAKE_AWAY && order.getTakeAwayType() != COUNTER_PARCEL_ORDER)
						rate = item.getOnlineRate().doubleValue();
					else if(order.getInHouse() == DINE_IN && tableType.equals(TABLE_TYPE_AC))
						rate = item.getDineInNonAcRate().doubleValue();
					
					String sql = "INSERT INTO OrderItemLog "
							+ "(hotelId, orderId, subOrderId, subOrderDate, menuId, state, reason, dateTime, quantity, rate, itemId) "
							+ "VALUES('" + hotelId + "', '" + order.getOrderId() + "', '"
							+ orderDao.getNextSubOrderId(hotelId, order.getOrderId()) + "', '"
							+ new SimpleDateFormat("yyyy/MM/dd").format(new Date()) + "', '" + item.getMenuId() + "', "
							+ ORDER_STATE_COMPLIMENTARY + ", 'Loyalty:" + loyalty.getName() + "', '"
							+ new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + "', " + excessQty + ", "
							+ rate + ", " + 1 + ");";
					if (!db.executeUpdate(sql, true)) {
						outObj.put("message", "Offer could not be applied. Please contact support. Code 50");
						db.rollbackTransaction();
						return outObj;
					}
				}
			}

			balancePoints = customer.getPoints() - requiredPoints;
			String sql = "UPDATE Customers SET points = " + balancePoints + " WHERE mobileNumber = '"
					+ customer.getMobileNumber() + "' AND hotelId = '" + hotelId + "';";

			if (!db.executeUpdate(sql, true)) {
				outObj.put("message", "Customer wallet could not be updated. Please contact support.");
				db.rollbackTransaction();
				return outObj;
			}

			sql = "UPDATE Orders SET loyaltyId = " + loyaltyId + ", loyaltyPaid = " + redeemablePoints
					+ " WHERE orderId = '" + order.getOrderId() + "' AND hotelId = '" + hotelId + "';";

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
			 * sms.sendSms(message, customer.getmobileNumber());
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
	
	@Override
	public Boolean addLoyaltyPoints(String hotelId, String orderId, int points, Customer customer) {

		int loyaltyPoints = customer.getPoints() + points;
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

		sql = "UPDATE Customers SET userType = '" + userType + "', points=" + loyaltyPoints + " WHERE mobileNumber='"
				+ escapeString(customer.getMobileNumber()) + "' AND hotelId='" + escapeString(hotelId) + "';";
		
		sql += "UPDATE Orders SET loyaltyEarned = " +points+" WHERE orderId = '"+orderId+"' AND hotelId = '"+hotelId+"';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public ArrayList<LoyaltySetting> getLoyaltySettings(String hotelId) {
		String sql = "SELECT * FROM LoyaltySettings WHERE hotelId = '" + hotelId + "' ORDER BY requiredPoints DESC;";

		return db.getRecords(sql, LoyaltySetting.class, hotelId);
	}

	@Override
	public LoyaltySetting getBaseLoyaltySetting(String hotelId) {
		String sql = "SELECT * FROM LoyaltySettings WHERE hotelId = '" + hotelId + "' AND requiredPoints = 0 AND userType != 'All';";

		return db.getOneRecord(sql, LoyaltySetting.class, hotelId);
	}

	@Override
	public LoyaltySetting getLoyaltySettingByUserType(String hotelId, String userType) {
		String sql = "SELECT * FROM LoyaltySettings WHERE hotelId = '" + hotelId + "' AND userType = '"
				+ (userType == "" ? "Prime" : userType) + "';";

		return db.getOneRecord(sql, LoyaltySetting.class, hotelId);
	}

	@Override
	public Boolean editLoyaltySettings(String hotelId, String userType, int requiredPoints, BigDecimal pointToRupee) {

		String sql = "UPDATE LoyaltySettings SET requiredPoints = " + requiredPoints + ", pointToRupee = "
				+ pointToRupee + " WHERE hotelId = '" + hotelId + "' AND userType = '" + userType + "';";

		return db.executeUpdate(sql, true);
	}
}
