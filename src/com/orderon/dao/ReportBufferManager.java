package com.orderon.dao;

import java.util.ArrayList;

import org.json.JSONArray;

import com.orderon.interfaces.IReportBuffer;

public class ReportBufferManager extends AccessManager implements IReportBuffer {

	public ReportBufferManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean addToBuffer(String outletId, String subject, String emailText, String smsText, JSONArray emailIds,
			JSONArray mobileNumbers) {
		
		String sql = "INSERT INTO ReportBuffer (outletId, subject, emailText, smsText, mobileNumbers, emailIds) "
			+ "VALUES('" + escapeString(outletId) + "', '" + escapeString(subject) + "', '" + escapeString(emailText) + "', '" 
			+ escapeString(smsText) + "', '" + (mobileNumbers.toString()) + "', '" +escapeString(emailIds.toString())+"');";
		
		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public ArrayList<ReportBuffer> getBuffer(String outletId) {
		
		String sql = "SELECT * FROM ReportBuffer;";
		
		return db.getRecords(sql, ReportBuffer.class, outletId);
	}

	@Override
	public boolean deleteBuffer(String outletId, int id) {
		
		String sql = "DELETE FROM ReportBuffer WHERE id = '"+id+"';";
		
		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public boolean deleteBuffer(String outletId) {
		
		String sql = "DELETE FROM ReportBuffer;";
		
		return db.executeUpdate(sql, outletId, false);
	}
}
