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
	public boolean addEmailToBuffer(String outletId, String subject, String emailText, JSONArray emailIds) {
		
		String sql = "INSERT INTO ReportBuffer (outletId, subject, emailText, emailIds) "
			+ "VALUES('" + escapeString(outletId) + "', '" + escapeString(subject) + "', '" + escapeString(emailText) + "', '" 
			+ escapeString(emailIds.toString())+"');";
		
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public boolean addSmsToBuffer(String outletId, String smsText, JSONArray mobileNumbers) {
		
		String sql = "INSERT INTO ReportBuffer (outletId, smsText, mobileNumbers) "
			+ "VALUES('" + escapeString(outletId) + "', '" + escapeString(smsText) + "', '" + (mobileNumbers.toString()) +"');";
		
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public boolean addEWardsToBuffer(String outletId, String eWardsSettleBill) {
		
		String sql = "INSERT INTO ReportBuffer (outletId, eWardsSettleBill) "
			+ "VALUES('" + escapeString(outletId) + "', '" + eWardsSettleBill+"');";
		
		return db.executeUpdate(sql, outletId, true);
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
