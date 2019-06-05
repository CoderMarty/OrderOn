package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONObject;

import com.orderon.dao.AccessManager.Outlet;
import com.orderon.dao.AccessManager.Settings;

public interface IOutlet {
	
	public JSONObject addOutlet(String corporateId, String outletId, String name, String address,
			String contact, String gstNumber, String vatNumber, String outletCode, String fileName);

	public ArrayList<Outlet> getOutlets(String corporateId);
	
	public Outlet getOutlet(String corporateId, String outletId);
	
	public Outlet getOutlet(String outletId);
	
	public boolean outletExists(String corporateId, String outletId);
	
	public Settings getSettings(String outletId);

	ArrayList<Outlet> getOutletsForCorporate(String corporateId);
}
