package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;

import com.orderon.dao.AccessManager.ReportBuffer;

public interface IReportBuffer extends IAccess{

	public boolean addEmailToBuffer(String outletId, String subject, String emailText, JSONArray emailIds);

	public boolean addSmsToBuffer(String outletId, String smsText, JSONArray mobileNumbers);

	public boolean addEWardsToBuffer(String outletId, String eWardsSettleBill);
	
	public ArrayList<ReportBuffer> getBuffer(String outletId);
	
	public boolean deleteBuffer(String outletId, int id);

	public boolean deleteBuffer(String outletId);
}
