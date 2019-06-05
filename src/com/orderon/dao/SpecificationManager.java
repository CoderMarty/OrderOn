package com.orderon.dao;

import java.util.ArrayList;

import com.orderon.interfaces.ISpecification;

public class SpecificationManager extends AccessManager implements ISpecification{

	public SpecificationManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Boolean addOrderSpecification(String hotelId, String orderId, String subOrderId, String menuId, int itemId,
			String specification) {

		String sql = "INSERT INTO OrderSpecifications (hotelId, orderId, subOrderId, menuId, itemId, specification) values ('"
				+ hotelId + "', '" + orderId + "', '" + subOrderId + "', '" + menuId + "', " + itemId + ", '"
				+ escapeString(specification) + "');";
		return db.executeUpdate(sql, true);
	}

	@Override
	public ArrayList<Specifications> getSpecifications(String hotelId) {
		String sql = "SELECT * FROM Specifications ORDER BY specification;";
		return db.getRecords(sql, Specifications.class, hotelId);
	}

	@Override
	public boolean addSpecification(String name) {

		String sql = "INSERT INTO Specifications (specification) VALUES('" + escapeString(name) + "');";
		return db.executeUpdate(sql, true);
	}

	@Override
	public ArrayList<OrderSpecification> getOrderedSpecification(String hotelId, String orderId, String menuId,
			String subOrderId, int itemId) {

		String sql = "SELECT * FROM OrderSpecifications WHERE orderId='" + orderId + "' AND itemId == " + itemId
				+ " AND menuId='" + menuId + "' AND subOrderId='" + subOrderId + "';";
		return db.getRecords(sql, OrderSpecification.class, hotelId);
	}

	@Override
	public ArrayList<OrderSpecification> getOrderedSpecification(String hotelId, String orderId, String menuId) {

		String sql = "SELECT * FROM OrderSpecifications WHERE orderId='" + orderId + "' AND menuId='" + menuId + "';";
		return db.getRecords(sql, OrderSpecification.class, hotelId);
	}

}
