package com.orderon.dao;

import java.util.ArrayList;

import com.orderon.interfaces.IDesignation;

public class DesignationManager extends AccessManager implements IDesignation {

	public DesignationManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<Designation> getDesignations(String hotelId) {
		String sql = "SELECT * FROM Designations  WHERE hotelId='" + hotelId + "';";
		return db.getRecords(sql, Designation.class, hotelId);
	}

	@Override
	public Designation getDesignationById(String hotelId, int id) {
		String sql = "SELECT * FROM Designations WHERE id='" + id + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Designation.class, hotelId);
	}

	@Override
	public ArrayList<Designation> getDesignationHavingIncentive(String hotelId) {
		String sql = "SELECT * FROM Designations WHERE hotelId='" + hotelId + "' AND hasIncentive = 'true';";
		return db.getRecords(sql, Designation.class, hotelId);
	}
}
