package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Tax;

public interface ITax {

	public ArrayList<Tax> getTaxes(String systemId, String outletId);
	
	public ArrayList<Tax> getActiveTaxes(String systemId, String outletId);
	
	public ArrayList<Tax> getTaxesForMaterials(String systemId, String outletId);

	public boolean addTax(String corporateId, String restaurantId, String systemId, String outletId, String name, BigDecimal value, String type, Boolean isActive);

	public Tax getTaxByName(String systemId, String outletId, String taxName);

	public boolean deleteTax(String systemId, int id);
}
