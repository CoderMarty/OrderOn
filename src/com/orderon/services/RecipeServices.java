package com.orderon.services;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.commons.MeasurableUnit;
import com.orderon.dao.AccessManager.Recipe;
import com.orderon.dao.RecipeManager;
import com.orderon.interfaces.IRecipe;

@Path("/RecipeServices")
public class RecipeServices {

	private String filterUnitToStore(String unit) {

		if (unit.equals("TABLESPOON (GM)"))
			unit = "TABLESPOONGM";
		else if (unit.equals("TABLESPOON (ML)"))
			unit = "TABLESPOONML";
		else if (unit.equals("TEASPOON (GM)"))
			unit = "TEASPOONGM";
		else if (unit.equals("TEASPOON (ML)"))
			unit = "TEASPOONML";

		return unit;
	}

	@GET
	@Path("/v1/getRecipe")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRecipe(@QueryParam("outletId") String outletId, @QueryParam("menuId") String menuId
			, @QueryParam("processedMaterialSku") int processedMaterialSku) {
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<Recipe> recipeItems = null;
		
		IRecipe dao = new RecipeManager(false);
		if(!menuId.isEmpty()) {
			recipeItems = dao.getRecipe(outletId, menuId);
		}else {
			recipeItems = dao.getRecipe(outletId, processedMaterialSku);
		}
		MeasurableUnit measurableUnit = MeasurableUnit.GRAM;

		try {
			for (int i = 0; i < recipeItems.size(); i++) {
				measurableUnit = MeasurableUnit.valueOf(filterUnitToStore(recipeItems.get(i).getMeasurableUnit()));
				itemDetails = new JSONObject();
				itemDetails.put("sku", recipeItems.get(i).getSku());
				itemDetails.put("name", recipeItems.get(i).getName());
				itemDetails.put("unit", measurableUnit.toString());
				itemDetails.put("displayableUnit", recipeItems.get(i).getDisplayableUnit());
				itemDetails.put("quantity", recipeItems.get(i).getDisplayableQuantity());
				itemDetails.put("processedMaterialSku", recipeItems.get(i).getProcessedMaterialSku());

				itemsArr.put(itemDetails);
			}
			outObj.put("items", itemsArr);
			if(!menuId.isEmpty())
				outObj.put("method", dao.getMethod(outletId, menuId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/addRecipe")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addRecipe(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IRecipe dao = new RecipeManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			MeasurableUnit measurableUnit = MeasurableUnit.valueOf(filterUnitToStore(inObj.getString("unit")));
			String outletId = inObj.getString("outletId");

			if (dao.recipeItemExists(outletId, inObj.getInt("sku"), inObj.getString("menuId"))) {
				outObj.put("status",
						dao.updateRecipe(inObj.getString("outletId"),
								measurableUnit.convertToBaseUnit(new BigDecimal(inObj.getDouble("quantity"))), inObj.getString("menuId"),
								inObj.getInt("sku"), inObj.getString("unit")));
			} else {
				outObj.put("status",
						dao.addRecipe(inObj.getString("outletId"), measurableUnit.convertToBaseUnit(new BigDecimal(inObj.getDouble("quantity"))),
								inObj.getString("menuId"), inObj.getInt("sku"), inObj.getString("unit")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateMethod")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateMethod(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IRecipe dao = new RecipeManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			;

			outObj.put("status",
					dao.updateMethod(inObj.getString("outletId"), inObj.getString("menuId"), inObj.getString("method")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/deleteRecipeItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteRecipeItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IRecipe dao = new RecipeManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if(!inObj.has("menuId") && !inObj.has("processedMaterialSku")) {
				outObj.put("message", "MenuId/processedMaterialSku not found");
			}else if(!inObj.has("menuId")) {
				outObj.put("status", dao.deleteRecipeItem(inObj.getString("outletId"), inObj.getInt("sku"),
						inObj.getInt("processedMaterialSku")));
			}else {
				outObj.put("status", dao.deleteRecipeItem(inObj.getString("outletId"), inObj.getInt("sku"),
						inObj.getString("menuId")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
}
