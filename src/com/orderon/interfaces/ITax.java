package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Tax;

public interface ITax {

	public ArrayList<Tax> getTaxes(String hotelId);
	
	public ArrayList<Tax> getActiveTaxes(String hotelId);
	
	public ArrayList<Tax> getTaxesForMaterials(String hotelId);

	public boolean addTax(String hotelId, String name, BigDecimal value, String type, Boolean isActive);

	public Tax getTaxByName(String hotelId, String taxName);

	public boolean deleteTax(String hotelId, int id);
}
