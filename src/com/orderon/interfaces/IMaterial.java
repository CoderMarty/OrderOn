package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.commons.CountableUnit;
import com.orderon.commons.MaterialCategory;
import com.orderon.commons.MeasurableUnit;
import com.orderon.dao.AccessManager.EntityString;
import com.orderon.dao.AccessManager.Material;

public interface IMaterial {

	public ArrayList<Material> getMaterials(String outletId, String category);

	public ArrayList<Material> getMaterials(String outletId);
	
	public ArrayList<Material> getMaterialsRunningOutOfStock(String outletId, String category);

	public ArrayList<Material> getMaterialByFilter(String outletId, String filterVariable, String filter, String query, String category);

	public Material getMaterialBySku(String outletId, int sku);
	
	public Material getMaterialByName(String outletId, String name);

	public JSONObject addMaterial(String name, MeasurableUnit measurableUnit, MeasurableUnit displayableUnit, BigDecimal countableConversion, 
			boolean isCountable, CountableUnit countableUnit, BigDecimal ratePerUnit, String materialType, String subType, 
			MaterialCategory category, BigDecimal minQuantity, BigDecimal quantity, String outletId, String state, JSONArray taxes);

	public JSONObject updateMaterial(int sku, String name, MeasurableUnit measurableUnit, MeasurableUnit displayableUnit, BigDecimal countableConversion, 
			boolean isCountable, CountableUnit countableUnit, String materialType, String subType, MaterialCategory category, 
			BigDecimal minQuantity, String outletId, String state, boolean isActive, JSONArray taxes);

	public boolean updateMaterial(String outletId, int sku, BigDecimal quantity, BigDecimal ratePerUnit, JSONArray taxes);

	public Boolean materialExists(String outletId, String name);

	public boolean updateQuantity(String outletId, int sku, BigDecimal quantity);
	
	public boolean deleteMaterial(String outletId, int sku);
	
	public ArrayList<EntityString> getMaterialSubTypes(String outletId);
}
