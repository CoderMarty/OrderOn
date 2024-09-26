package com.orderon.dao;

import java.util.ArrayList;

import com.orderon.commons.OnlineOrderingPortals;
import com.orderon.interfaces.IOnlineOrderingPortal;

public class OnlineOrderingPortalManager extends AccessManager implements IOnlineOrderingPortal{

	public OnlineOrderingPortalManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<OnlineOrderingPortal> getOnlineOrderingPortals(String hotelId){
		String sql = "SELECT * FROM OnlineOrderingPortals WHERE hotelId = '"+hotelId+"';";
		
		return db.getRecords(sql, OnlineOrderingPortal.class, hotelId);
	}

	@Override
	public ArrayList<OnlineOrderingPortal> getOnlineOrderingPortalsForIntegration(String hotelId){
		String sql = "SELECT * FROM OnlineOrderingPortals WHERE hotelId = '"+hotelId+"' AND hasIntegration='true';";
		
		return db.getRecords(sql, OnlineOrderingPortal.class, hotelId);
	}

	@Override
	public OnlineOrderingPortal getOnlineOrderingPortalByPortal(String hotelId, OnlineOrderingPortals portal){
		String sql = "SELECT * FROM OnlineOrderingPortals WHERE hotelId = '"+hotelId+"' AND portal = '"+portal.toString()+"';";
		
		return db.getOneRecord(sql, OnlineOrderingPortal.class, hotelId);
	}

	@Override
	public OnlineOrderingPortal getOnlineOrderingPortalById(String hotelId, int id){
		String sql = "SELECT * FROM OnlineOrderingPortals WHERE hotelId = '"+hotelId+"' AND id = "+id+";";
		
		return db.getOneRecord(sql, OnlineOrderingPortal.class, hotelId);
	}
}
