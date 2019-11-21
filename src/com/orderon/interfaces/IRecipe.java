package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Inventory;
import com.orderon.dao.AccessManager.Recipe;

public interface IRecipe {

	public boolean deleteRecipeItem(String outletId, int sku, String menuId);

	public boolean deleteRecipeItem(String outletId, int sku, int processedMaterialSku);
	
	public boolean deleteRecipeItemForProcessedMaterial(String outletId, int processedMaterialSku);

	public ArrayList<Recipe> getRecipe(String outletId, String menuId);
	
	public Recipe getRecipeForDirectItem(String outletId, int sku);

	public ArrayList<Recipe> getRecipe(String outletId, int processedMaterialSku);

	public String getMethod(String outletId, String menuId);

	public boolean recipeItemExists(String outletId, int sku, String menuId);

	public Inventory getRecipeItemByTitle(String outletId, int sku, String menuId);

	public boolean addRecipe(String outletId, BigDecimal quantity, String menuId, int sku, String unit);
	
	public boolean addRecipeForProcessedItem(String outletId, BigDecimal quantity, int processedMaterialSku, int sku, String unit);

	public boolean addDirectRecipe(String outletId, String menuId, int sku);
	
	public boolean updateDirectRecipe(String outletId, String menuId, int sku);

	public boolean updateRecipe(String outletId, BigDecimal quantity, String menuId, int sku, String unit);

	public boolean updateMethod(String outletId, String menuId, String method);

	public boolean checkIfMaterialHasRecipe(String outletId, int sku);
}
