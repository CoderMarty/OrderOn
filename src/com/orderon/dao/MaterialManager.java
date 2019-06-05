package com.orderon.dao;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.commons.CountableUnit;
import com.orderon.commons.MaterialCategory;
import com.orderon.commons.MeasurableUnit;
import com.orderon.interfaces.IMaterial;

public class MaterialManager extends AccessManager implements IMaterial {

	public MaterialManager(Boolean transactionBased) {
		super(transactionBased);
	}

	@Override
	public Material getMaterialByName(String outletId, String name) {
		String sql = "SELECT * FROM Materials WHERE name='" + name + "' AND outletId='"
				+ outletId + "';";
		return db.getOneRecord(sql, Material.class, outletId);
	}

	@Override
	public ArrayList<Material> getMaterials(String outletId, String category) {
		String sql = "SELECT * FROM Materials WHERE category='" + category + "' AND outletId='"
				+ outletId + "' ORDER BY name;";
		return db.getRecords(sql, Material.class, outletId);
	}

	@Override
	public ArrayList<Material> getMaterials(String outletId) {
		String sql = "SELECT * FROM Materials WHERE outletId='" + outletId + "' ORDER BY name;;";
		return db.getRecords(sql, Material.class, outletId);
	}

	@Override
	public ArrayList<EntityString> getMaterialSubTypes(String outletId) {
		String sql = "SELECT DISTINCT subType AS entityId FROM Materials WHERE outletId='" + outletId + "';";
		return db.getRecords(sql, EntityString.class, outletId);
	}

	@Override
	public ArrayList<Material> getMaterialsRunningOutOfStock(String outletId, String category) {
		String sql = "SELECT * FROM Materials WHERE category='" + category + "' AND outletId='"
				+ outletId + "' AND quantity <= minQuantity;";
		return db.getRecords(sql, Material.class, outletId);
	}

	@Override
	public ArrayList<Material> getMaterialByFilter(String outletId, String filterVariable, String filter, String query,
			String category) {
		
		String sql = "SELECT * FROM Materials WHERE category='" + category
				+ "' AND outletId='" + outletId + "' ";
		//if(!filterVariable.isEmpty()) {
		//	sql += "AND "+filterVariable+"='" + filter;
		//}
		if(!query.isEmpty()) {
			sql += "AND name LIKE '%" + query + "%' ";
		}
		sql += ";";
		return db.getRecords(sql, Material.class, outletId);
	}

	@Override
	public Material getMaterialBySku(String outletId, int sku) {
		String sql = "SELECT * FROM Materials WHERE sku='" + sku + "' AND outletId='"
				+ outletId + "';";
		return db.getOneRecord(sql, Material.class, outletId);
	}

	@Override
	public JSONObject addMaterial(String name, MeasurableUnit measurableUnit, MeasurableUnit displayableUnit, BigDecimal countableConversion, 
			boolean isCountable, CountableUnit countableUnit, BigDecimal ratePerUnit, String materialType, String subType, 
			MaterialCategory category, BigDecimal minQuantity, BigDecimal quantity, String outletId, String state, JSONArray taxes) {
		
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			int sku = this.getNextSKU(outletId);
		
			String sql = "INSERT INTO Materials "
					+ "(outletId, sku, name, measurableUnit, displayableUnit, countableConversion, isCountable, countableUnit, "
					+ "ratePerUnit, materialType, subType, category, minQuantity, quantity, state, isActive, tax) VALUES('"
					+ escapeString(outletId) + "', '" 
					+ sku + "', '" 
					+ escapeString(name) + "', '"
					+ measurableUnit.toString() + "', '"
					+ displayableUnit.toString() + "', "
					+ countableConversion + ", '"
					+ isCountable + "', '"
					+ countableUnit.toString() + "', "
					+ ratePerUnit + ", '"
					+ escapeString(materialType) + "', '"
					+ escapeString(subType.toUpperCase()) + "', '"
					+ category.getName() + "', "
					+ minQuantity + ", " 
					+ quantity + ", '" 
					+ escapeString(state)  + "', '" 
					+ "true', '"
					+ taxes.toString() + "'" 
					+ ");";
	
			if(db.executeUpdate(sql, outletId, true)) {
				outObj.put("status", true);
				outObj.put("sku", sku);
			}else {
				outObj.put("message", "Could not add Material.");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public JSONObject updateMaterial(int sku, String name, MeasurableUnit measurableUnit, MeasurableUnit displayableUnit, 
			BigDecimal countableConversion, boolean isCountable, CountableUnit countableUnit, String materialType, String subType, 
			MaterialCategory category, BigDecimal minQuantity, String outletId, String state, boolean isActive, JSONArray taxes) {
		
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			String sql = "UPDATE Materials SET "
					+ "name = '" + escapeString(name) + "', "
					+ "measurableUnit = '"+ measurableUnit.toString()+"', "
					+ "displayableUnit = '"+ displayableUnit.toString()+"', "
					+ "countableConversion = "+ countableConversion+", "
					+ "isCountable = '"+ isCountable+"', "
					+ "countableUnit = '"+ countableUnit.toString()+"', "
					+ "materialType = '"+ escapeString(materialType)+"', "
					+ "subType = '"+ escapeString(subType)+"', "
					+ "category = '"+ escapeString(category.getName())+"', "
					+ "minQuantity = "+ minQuantity+", "
					+ "state = '"+ escapeString(state)+"', "
					+ "isActive = '"+ isActive+"', "
					+ "tax = '"+ taxes.toString()+"' "
					+ "WHERE outletId = '" + escapeString(outletId) + "' AND sku = " + sku + ";";

			if(db.executeUpdate(sql, outletId, true)) {
				outObj.put("status", true);
			}else{
				outObj.put("message", "Could not update Material.");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public boolean updateQuantity(String outletId, int sku, BigDecimal quantity) {
		
		String sql = "UPDATE Materials SET "
				+ "quantity = " + quantity 
				+ " WHERE outletId = '" + escapeString(outletId) 
				+ "' AND sku = " + sku + ";";
		
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public boolean updateMaterial(String outletId, int sku, BigDecimal quantity, BigDecimal ratePerUnit, JSONArray taxes) {
		
		String sql = "UPDATE Materials SET "
				+ "quantity = " + quantity + ", "
				+ "ratePerUnit = " + ratePerUnit + ", "
				+ "tax = '"+ taxes.toString()+"' "
				+ " WHERE outletId = '" + escapeString(outletId) 
				+ "' AND sku = " + sku + ";";
		
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public Boolean materialExists(String outletId, String name) {
		Material item = getMaterialByName(outletId, name);
		if (item != null) {
			return true;
		}
		return false;
	}

	private int getNextSKU(String outletId) {

		String sql = "SELECT MAX(sku) AS entityId FROM Materials WHERE outletId='" + outletId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, outletId);

		return (entity.getId() + 1);
	}

	@Override
	public boolean deleteMaterial(String outletId, int sku) {
		String sql = "DELETE FROM Materials WHERE sku=" + sku + " AND outletId='" + outletId + "';";
		return db.executeUpdate(sql, outletId, true);
	}
}
