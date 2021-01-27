package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;

import com.orderon.dao.AccessManager.PromotionalCampaign;

public interface IPromotionalCampaign {

	public boolean addCampaign(String outletId, String name, String messageContent, JSONArray outletIds, JSONArray userTypes, 
			String sex, JSONArray ageGroup);

	public boolean editCampaign(String outletId, int campaignId, String name, String messageContent, JSONArray outletIds, 
			JSONArray userTypes, String sex, JSONArray ageGroup);
	
	public boolean updateCampaign(String outletId, int campaignId, int totalSmsSent, int totalCustomerCount,
			int failedSmsCount, int newBalance);
	
	public ArrayList<PromotionalCampaign> getPromotionalCampaigns(String outletId);
	
	public PromotionalCampaign getPromotionalCampaignById(String outletId, int id);
	
	public boolean deleteCampaign(String outletId, int id);
}
