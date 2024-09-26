package com.orderon.interfaces;

import java.util.ArrayList;

import com.orderon.commons.OnlineOrderingPortals;
import com.orderon.dao.AccessManager.OnlineOrderingPortal;

public interface IOnlineOrderingPortal {

	public ArrayList<OnlineOrderingPortal> getOnlineOrderingPortals(String hotelId);
	
	public ArrayList<OnlineOrderingPortal> getOnlineOrderingPortalsForIntegration(String hotelId);
	
	public OnlineOrderingPortal getOnlineOrderingPortalByPortal(String hotelId, OnlineOrderingPortals portal);
	
	public OnlineOrderingPortal getOnlineOrderingPortalById(String hotelId, int id);
}
