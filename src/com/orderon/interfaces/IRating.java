package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.Date;

import org.json.JSONObject;

public interface IRating {

	public boolean submitRatings(String hotelId, String orderId, String customerName, String customerNumber,
			String customerBirthdate, String customerAnniversary, String reviewSuggestions, JSONObject ratings,
			Boolean wantsPromotion, String customerEmailId, String referenceForReview);

	public Integer getAmbiancePoints(String hotelId, String userId, Date dt);

	public Integer getQoFPoints(String hotelId, String userId, Date dt);

	public Integer getServicePoints(String hotelId, String userId, Date dt);

	public Integer getHygienePoints(String hotelId, String userId, Date dt);

	public BigDecimal getAverageFood(String hotelId, String custNumber);

	public BigDecimal getAverageAmbiance(String hotelId, String custNumber);

	public BigDecimal getAverageService(String hotelId, String custNumber);

	public BigDecimal getAverageHygiene(String hotelId, String custNumber);

	public BigDecimal getOverallAvgFood(String hotelId);

	public BigDecimal getOverallAvgAmbiance(String hotelId);

	public BigDecimal getOverallAvgService(String hotelId);

	public BigDecimal getOverallAvgHygiene(String hotelId);
}
