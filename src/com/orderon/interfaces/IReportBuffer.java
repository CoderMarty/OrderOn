package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;

import com.orderon.dao.AccessManager.ReportBuffer;

public interface IReportBuffer extends IAccess{

	public boolean addToBuffer(String outletId, String subject, String emailText, String smsText, JSONArray emailIds, JSONArray mobileNumbers);
	
	public ArrayList<ReportBuffer> getBuffer(String outletId);
	
	public boolean deleteBuffer(String outletId, int id);

	public boolean deleteBuffer(String outletId);
}
