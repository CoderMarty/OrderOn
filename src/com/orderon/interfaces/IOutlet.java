package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONObject;

import com.orderon.dao.AccessManager.EntityString;
import com.orderon.dao.AccessManager.Outlet;
import com.orderon.dao.AccessManager.Settings;

public interface IOutlet {
	
	public JSONObject addOutlet(String corporateId, String restaurantId, String outletId, String name, String companyName, String address,
			String contact, String gstNumber, String vatNumber, String outletCode, String imgLocation, JSONObject location);

	public ArrayList<Outlet> getOutlets(String corporateId);
	
	public ArrayList<Outlet> getOutletsForSystem(String systemId);
	
	public ArrayList<EntityString> getOutletsIds(String systemId);
	
	public Outlet getOutlet(String corporateId, String outletId);
	
	public Outlet getOutletForSystem(String systemId, String outletId);
	
	public boolean outletExists(String corporateId, String outletId);
	
	public Settings getSettings(String systemId);

	ArrayList<Outlet> getOutletsForCorporate(String corporateId);
	
	public Settings getHotelSettings(String systemId);

	public boolean isOldVersion(String systemId);
	
	public boolean updateInternetFlag(String systemId, boolean isInternetAvailable);
}
