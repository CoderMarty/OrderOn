package com.orderon.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.orderon.interfaces.ICustomer;
import com.orderon.interfaces.IRating;

public class RatingManager extends AccessManager implements IRating {

	public RatingManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean submitRatings(String hotelId, String orderId, String customerName, String customerNumber,
			String customerBirthdate, String customerAnniversary, String reviewSuggestions, JSONObject ratings,
			Boolean wantsPromotion, String customerEmailId, String referenceForReview) {
		try {
			String sql = "";
			if (!customerNumber.equals("")) {
				String[] name = customerName.split(" ");
				String surName = "";
				if(name.length>1) {
					surName = name[1];
				}
				ICustomer customerDao = new CustomerManager(false);
				if (!customerDao.hasCustomer(hotelId, customerNumber)) {
					customerDao.addCustomer(hotelId, name[0], surName, customerNumber, "", customerBirthdate, customerAnniversary, "",
							wantsPromotion, Boolean.FALSE, customerEmailId, "NONE", referenceForReview);
				} else {
					customerDao.updateCustomer(hotelId, null, name[0], surName, customerNumber, customerBirthdate, customerAnniversary, "",
							"", "", wantsPromotion, "", "");
				}
			}

			sql = "UPDATE Orders SET customerName='" + customerName + "', customerNumber='" + customerNumber
					+ "', rating_ambiance=" + ratings.getInt("ambianceRating") + ", rating_qof="
					+ ratings.getInt("qualityOfFoodRating") + ", rating_service=" + ratings.getInt("serviceRating")
					+ ", rating_hygiene=" + ratings.getInt("hygieneRating") + ", reviewSuggestions='"
					+ reviewSuggestions + "' WHERE orderId='" + orderId + "' AND hotelId='" + hotelId + "';";
			return db.executeUpdate(sql, hotelId, true);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
	public BigDecimal getAverageFood(String hotelId, String custNumber) {

		String sql = "SELECT ROUND(AVG(rating_qof)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"
				+ custNumber + "'";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	@Override
	public BigDecimal getAverageAmbiance(String hotelId, String custNumber) {

		String sql = "SELECT ROUND(AVG(rating_ambiance)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"
				+ custNumber + "'";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	@Override
	public BigDecimal getAverageService(String hotelId, String custNumber) {

		String sql = "SELECT ROUND(AVG(rating_service)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"
				+ custNumber + "'";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	@Override
	public BigDecimal getAverageHygiene(String hotelId, String custNumber) {

		String sql = "SELECT ROUND(AVG(rating_hygiene)*100)/100 AS entityId FROM Orders WHERE customerNumber = '"
				+ custNumber + "'";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	@Override
	public BigDecimal getOverallAvgFood(String hotelId) {

		String sql = "SELECT ROUND(AVG(rating_qof)*100)/100 AS entityId FROM Orders;";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	@Override
	public BigDecimal getOverallAvgAmbiance(String hotelId) {

		String sql = "SELECT ROUND(AVG(rating_ambiance)*100)/100 AS entityId FROM Orders;";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	@Override
	public BigDecimal getOverallAvgService(String hotelId) {

		String sql = "SELECT ROUND(AVG(rating_service)*100)/100 AS entityId FROM Orders;";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}

	@Override
	public BigDecimal getOverallAvgHygiene(String hotelId) {

		String sql = "SELECT ROUND(AVG(rating_hygiene)*100)/100 AS entityId FROM Orders;";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);

		return entity.getId();
	}
}
