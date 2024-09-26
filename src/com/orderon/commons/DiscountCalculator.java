package com.orderon.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.dao.AccessManager;
import com.orderon.dao.AccessManager.Discount;
import com.orderon.dao.AccessManager.Order;
import com.orderon.dao.AccessManager.OrderItem;
import com.orderon.dao.DiscountManager;
import com.orderon.interfaces.IDiscount;

public class DiscountCalculator {

	private IDiscount dao;
	private BigDecimal discountRupeeFood;
	private BigDecimal discountRupeeBar;
	private BigDecimal foodDiscount;
	private BigDecimal barDiscount;
	private boolean isRupeeDiscountApplied;
	private JSONArray discountArr;
	
	public DiscountCalculator() {
		dao = new DiscountManager(false);
		discountRupeeFood = new BigDecimal(0.0);
		discountRupeeBar = new BigDecimal(0.0);
		foodDiscount = new BigDecimal(0.0);
		barDiscount = new BigDecimal(0.0);
		isRupeeDiscountApplied = false;
		discountArr = new JSONArray();
	}

	public BigDecimal getFoodDiscount() {
		return foodDiscount;
	}
	public BigDecimal getBarDiscount() {
		return barDiscount;
	}
	public JSONArray getDiscounts() {
		return discountArr;
	}
	
	public void calculateDiscount(String systemId, String outletId, JSONArray discountCodes, OrderItem item, BigDecimal subTotal, Order order) {
		
		//Get all discount objects.
		ArrayList<Discount> discounts = new ArrayList<Discount>();
		Discount discount = null;
		foodDiscount = new BigDecimal(0.0);
		barDiscount = new BigDecimal(0.0);
		
		JSONObject discountObj = null;
		try {
			for(int i=0; i<discountCodes.length(); i++) {
				discount = dao.getDiscountByName(systemId, outletId, discountCodes.getString(i));
				
				if(discount.getName().equals(AccessManager.DISCOUNT_TYPE_FIXED_RUPEE_DISCOUNT)) {
					discount.setFoodValue(order.getFixedRupeeDiscount().intValue());
				}else if(discount.getName().equals(AccessManager.DISCOUNT_TYPE_ZOMATO_VOUCHER)) {
					discount.setFoodValue(order.getZomatoVoucherAmount().intValue());
				}else if(discount.getName().equals(AccessManager.DISCOUNT_TYPE_PIGGYBANK)) {
					discount.setFoodValue(order.getPiggyBankAmount().intValue());
				}

				discounts.add(discount);
				
				if(!isRupeeDiscountApplied) {
					if(discount.getType() == AccessManager.DISCOUNT_TYPE_FIXED) {
						discountRupeeFood = discountRupeeFood.add(new BigDecimal(discount.getFoodValue()));
						discountRupeeBar = discountRupeeBar.add(new BigDecimal(discount.getBarValue()));
						isRupeeDiscountApplied = true;
					}
				}
				
				discountObj = new JSONObject();
				discountObj.put("name", discount.getName());
				discountObj.put("foodValue", discount.getFoodValue());
				discountObj.put("barValue", discount.getBarValue());
				discountObj.put("type", discount.getType());
				discountArr.put(discountObj);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.calculateDiscountOnItem(systemId, discounts, item, subTotal);
	}

	private void calculateDiscountOnItem(String systemId, ArrayList<Discount> discounts, OrderItem item, BigDecimal subTotal) {
		
		if(discounts.size() == 0)
			return;

		BigDecimal discountAmountOnItem = new BigDecimal(0.0);
		BigDecimal temp = new BigDecimal(0.0);
		
		for (Discount discount : discounts) {
			if(discount != null) {
				if(discount.getHasCollections()) {
					for (int i = 0; i < discount.getValidCollections().length; i++) {
						if(discount.getValidCollections()[i].equals(item.getCollection())) {
							discountAmountOnItem = this.calculateDiscountAmountOnItem(item, subTotal, discount);
						}
					}
				}else {
					discountAmountOnItem = this.calculateDiscountAmountOnItem(item, subTotal, discount);
				}
			}
			if(discount.getMaxFoodDiscountAmount() > 0) {
				temp = foodDiscount.add(discountAmountOnItem);
				BigDecimal maxFoodDiscount = new BigDecimal(discount.getMaxFoodDiscountAmount());
				if(temp.compareTo(maxFoodDiscount) > 1) {
					foodDiscount = maxFoodDiscount;
				}
			}else if(discount.getMaxBarDiscountAmount() > 0) {
				temp = barDiscount.add(discountAmountOnItem);
				BigDecimal maxBarDiscount = new BigDecimal(discount.getMaxBarDiscountAmount());
				if(temp.compareTo(maxBarDiscount) > 1) {
					barDiscount = maxBarDiscount;
				}
			}
		}
	}
	
	private BigDecimal calculateDiscountAmountOnItem(OrderItem item, BigDecimal totalItemAmount, Discount discount) {
		
		BigDecimal discountAmountOnItem = new BigDecimal(0.0);
		BigDecimal hundred = new BigDecimal(100.0);
		if(discount.getOfferType().equals(AccessManager.OFFER_TYPE_DISCOUNT)) {
			if(discount.getType() == AccessManager.DISCOUNT_TYPE_PERCENTAGE) {
				if(item.getStation().equals("Bar"))
					barDiscount = barDiscount.add(totalItemAmount.multiply(new BigDecimal(discount.getBarValue())).divide(hundred).setScale(2, RoundingMode.HALF_UP));
				else
					foodDiscount = foodDiscount.add(totalItemAmount.multiply(new BigDecimal(discount.getFoodValue())).divide(hundred).setScale(2, RoundingMode.HALF_UP));
			}else {
				if(item.getStation().equals("Bar")) {
					if(totalItemAmount.compareTo(discountRupeeBar) == 1) {
						barDiscount = barDiscount.add(discountRupeeBar);
						discountRupeeBar = new BigDecimal(0.0);
					}else {
						discountRupeeBar = discountRupeeBar.subtract(totalItemAmount);
						barDiscount = barDiscount.add(totalItemAmount);
					}
				}else {
					if(totalItemAmount.compareTo(discountRupeeFood) == 1) {
						foodDiscount = foodDiscount.add(discountRupeeFood);
						discountRupeeFood = new BigDecimal(0.0);
					}else {
						discountRupeeFood = discountRupeeFood.subtract(totalItemAmount);
						foodDiscount = foodDiscount.add(totalItemAmount);
					}
				}
			}
		}else if(discount.getOfferType().equals(AccessManager.DISCOUNT_TYPE_DISH)) {
			if(item.getDiscountType().equals(AccessManager.OFFER_TYPE_PERCENTAGE)) {
				if(item.getStation().equals("Bar"))
					barDiscount = barDiscount.add(totalItemAmount.multiply(item.getDiscountValue()).divide(hundred).setScale(2, RoundingMode.HALF_UP));
				else
					foodDiscount = foodDiscount.add(totalItemAmount.multiply(item.getDiscountValue()).divide(hundred).setScale(2, RoundingMode.HALF_UP));
			}else {
				if(item.getStation().equals("Bar"))
					barDiscount = barDiscount.add(item.getDiscountValue());
				else
					foodDiscount = foodDiscount.add(item.getDiscountValue());
			}
		}
		if(item.getStation().equals("Bar"))
			discountAmountOnItem = barDiscount;
		else
			discountAmountOnItem = foodDiscount;
		
		return discountAmountOnItem;
	}
}
