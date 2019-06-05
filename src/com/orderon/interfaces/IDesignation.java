package com.orderon.interfaces;

import java.util.ArrayList;

import com.orderon.dao.AccessManager.Designation;

public interface IDesignation {

	public ArrayList<Designation> getDesignations(String hotelId);
	
	public Designation getDesignationById(String hotelId, int id);
	
	public ArrayList<Designation> getDesignationHavingIncentive(String hotelId);
}
