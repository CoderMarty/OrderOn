package com.orderon.dao;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.interfaces.IRecipe;

public class RecipeManager extends AccessManager implements IRecipe{

	public RecipeManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean deleteRecipeItem(String outletId, int sku, String menuId) {
		String sql = "DELETE FROM Recipe WHERE sku = '" + sku + "' AND menuId='" + menuId + "' AND hotelId='" + outletId
				+ "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean deleteRecipeItemForProcessedMaterial(String outletId, int processedMaterialSku) {
		String sql = "DELETE FROM Recipe WHERE processedMaterialSku = '" + processedMaterialSku + "' AND hotelId='" + outletId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean deleteRecipeItem(String outletId, int sku, int processedMaterialSku) {
		String sql = "DELETE FROM Recipe WHERE sku = '" + sku + "' AND processedMaterialSku=" + processedMaterialSku + " AND hotelId='" + outletId
				+ "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public ArrayList<Recipe> getRecipe(String outletId, String menuId) {
		String sql = "SELECT Recipe.sku AS sku, Recipe.quantity AS quantity, Materials.name AS name, "
				+ "Materials.measurableUnit AS measurableUnit, Recipe.unit AS displayableUnit FROM Materials, Recipe "
				+ "WHERE Recipe.hotelId= '" + outletId + "' AND Recipe.menuId= '" + menuId + "' "
				+ "AND Materials.sku == Recipe.sku;";
		return db.getRecords(sql, Recipe.class, outletId);
	}

	@Override
	public Recipe getRecipeForDirectItem(String outletId, int sku) {
		String sql = "SELECT * FROM Recipe WHERE Recipe.hotelId= '" + outletId + "' " + "AND Recipe.sku == "+ sku+";";
		return db.getOneRecord(sql, Recipe.class, outletId);
	}

	@Override
	public ArrayList<Recipe> getRecipe(String outletId, int processedMaterialSku) {
		String sql = "SELECT Recipe.sku AS sku, Recipe.quantity AS quantity, Materials.name AS name, "
				+ "Materials.measurableUnit AS measurableUnit, Recipe.unit AS displayableUnit FROM Materials, Recipe "
				+ "WHERE Recipe.hotelId= '" + outletId + "' AND Recipe.processedMaterialSku= " + processedMaterialSku + " "
				+ "AND Materials.sku == Recipe.sku;";
		return db.getRecords(sql, Recipe.class, outletId);
	}

	@Override
	public String getMethod(String outletId, String menuId) {
		String sql = "SELECT method FROM MenuItems WHERE hotelId= '" + outletId + "' AND menuId= '" + menuId + "';";
		return db.getOneRecord(sql, EntityString.class, outletId).getEntity();
	}

	@Override
	public boolean recipeItemExists(String outletId, int sku, String menuId) {
		Inventory item = getRecipeItemByTitle(outletId, sku, menuId);
		if (item != null) {
			return true;
		}
		return false;
	}

	@Override
	public Inventory getRecipeItemByTitle(String outletId, int sku, String menuId) {
		String sql = "SELECT * FROM Recipe WHERE sku='" + sku + "' AND menuId='" + escapeString(menuId)
				+ "' AND hotelId='" + escapeString(outletId) + "';";
		return db.getOneRecord(sql, Inventory.class, outletId);
	}

	@Override
	public boolean addRecipe(String outletId, BigDecimal quantity, String menuId, int sku, String unit) {
		String sql = "INSERT INTO Recipe (hotelId, sku, unit, menuId, quantity) VALUES('" + escapeString(outletId)
				+ "', '" + sku + "', '" + escapeString(unit) + "', '" + escapeString(menuId) + "', "
				+ quantity + ");";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean addDirectRecipe(String outletId, String menuId, int sku) {
		return this.addRecipe(outletId, new BigDecimal("1"), menuId, sku, "PIECE");
	}

	@Override
	public boolean updateRecipe(String outletId, BigDecimal quantity, String menuId, int sku, String unit) {

		String sql = "UPDATE Recipe SET quantity = " + quantity + ", unit = '" + escapeString(unit)
				+ "' WHERE hotelId = '" + escapeString(outletId) + "' AND sku = '" + sku
				+ "' AND menuId = '" + escapeString(menuId) + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean updateMethod(String outletId, String menuId, String method) {

		String sql = "UPDATE MenuItems SET method = '" + escapeString(method) + "' WHERE hotelId = '"
				+ escapeString(outletId) + "' AND menuId = '" + escapeString(menuId) + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean addRecipeForProcessedItem(String outletId, BigDecimal quantity, int processedMaterialSku, int sku,
			String unit) {
		String sql = "INSERT INTO Recipe (hotelId, sku, unit, processedMaterialSku, menuId, quantity) VALUES('" + escapeString(outletId)
				+ "', '" + sku + "', '" + escapeString(unit) + "', " + processedMaterialSku + ",'', " + quantity + ");";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean updateDirectRecipe(String outletId, String menuId, int sku) {

		String sql = "UPDATE Recipe SET menuId = '" + menuId + "' WHERE hotelId = '" + escapeString(outletId) + "' AND sku = '" + sku
				+ "';";
		return db.executeUpdate(sql, true);
	}
}
