package com.orderon.commons;

import java.math.BigDecimal;

import com.orderon.dao.AccessManager;
import com.orderon.dao.AccessManager.LoyaltyOffer;
import com.orderon.dao.AccessManager.OrderItem;
import com.orderon.dao.LoyaltyManager;
import com.orderon.interfaces.ILoyalty;

public class LoyaltyCalculator {

	private ILoyalty dao;
	private BigDecimal loyaltyRupee;
	private BigDecimal loyaltyAmount;
	
	public LoyaltyCalculator() {
		dao = new LoyaltyManager(false);
		loyaltyAmount = new BigDecimal(0.0);
		loyaltyRupee = new BigDecimal(0.0);
	}
	
	public BigDecimal getLoyaltyAmount() {
		return loyaltyAmount;
	}

	public void calculateLoyalty(String systemId, int loyaltyOfferId, OrderItem item, BigDecimal subTotal) {
		
		LoyaltyOffer loyalty = dao.getLoyaltyOfferById(systemId, loyaltyOfferId);

		if(loyalty == null){
			return;
		}
		
		if(loyalty.getOfferType() == AccessManager.CASH_LOYALTY_OFFER) {
			loyaltyRupee = new BigDecimal(loyalty.getOfferValue());
		}
		
		if(loyalty.getHasCollections()) {
			for (int i = 0; i < loyalty.getValidCollections().length; i++) {
				if(loyalty.getValidCollections()[i].equals(item.getCollection())) {
					if(loyalty.getOfferType() == AccessManager.PERCENTAGE_LOYALTY_OFFER) {
						loyaltyAmount = subTotal.multiply(new BigDecimal(loyalty.getOfferValue()));
					}else {
						if(subTotal.compareTo(loyaltyRupee) == 1 || subTotal.compareTo(loyaltyRupee) == 0) {
							loyaltyAmount = loyaltyRupee;
							loyaltyRupee = new BigDecimal(0.0);
						}else {
							loyaltyRupee = loyaltyRupee.subtract(subTotal);
							loyaltyAmount = subTotal;
						}
					}
				}
			}
		}else {
			if(loyalty.getOfferType() == AccessManager.PERCENTAGE_LOYALTY_OFFER) {
				loyaltyAmount = subTotal.multiply(new BigDecimal(loyalty.getOfferValue()));
			}else {
				if(subTotal.compareTo(loyaltyRupee) == 1 || subTotal.compareTo(loyaltyRupee) == 0) {
					loyaltyAmount = loyaltyRupee;
					loyaltyRupee = new BigDecimal(0.0);
				}else {
					loyaltyRupee = loyaltyRupee.subtract(subTotal);
					loyaltyAmount = subTotal;
				}
			}
		}
	}
}
