package com.orderon.dao;

import java.util.ArrayList;

import com.orderon.interfaces.IFlag;

public class FlagManager extends AccessManager implements IFlag {

	public FlagManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<Flag> getFlags(String hotelId) {
		String sql = "SELECT * FROM Flags  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, Flag.class, hotelId);
	}

	@Override
	public boolean addFlag(String hotelId, String name, String groupId) {

		String sql = "INSERT INTO Flags (hotelId, name, groupId) VALUES('" + escapeString(hotelId) 
				+ "', '" + name + "', '" + groupId + "');";
		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public Flag getFlagById(String hotelId, int id) {
		String sql = "SELECT * FROM Flags WHERE id='" + id + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Flag.class, hotelId);
	}

	@Override
	public boolean deleteFlag(String hotelId, int id) {
		String sql = "DELETE FROM Flags WHERE id = " + id + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, hotelId, true);
	}
}
