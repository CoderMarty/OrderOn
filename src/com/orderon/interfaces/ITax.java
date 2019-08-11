package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Tax;

public interface ITax {

	public ArrayList<Tax> getTaxes(String systemId);
	
	public ArrayList<Tax> getActiveTaxes(String systemId);
	
	public ArrayList<Tax> getTaxesForMaterials(String systemId);

	public boolean addTax(String corporateId, String systemId, String outletId, String name, BigDecimal value, String type, Boolean isActive);

	public Tax getTaxByName(String systemId, String outletId, String taxName);

	public boolean deleteTax(String systemId, int id);
}
