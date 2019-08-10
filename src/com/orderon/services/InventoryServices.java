package com.orderon.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.commons.CountableUnit;
import com.orderon.commons.MaterialCategory;
import com.orderon.commons.MaterialType;
import com.orderon.commons.MeasurableUnit;
import com.orderon.commons.PaymentType;
import com.orderon.dao.AccessManager;
import com.orderon.dao.AccessManager.Material;
import com.orderon.dao.AccessManager.Outlet;
import com.orderon.dao.AccessManager.Settings;
import com.orderon.dao.AccessManager.Tax;
import com.orderon.dao.AccessManager.Vendor;
import com.orderon.dao.ExpenseManager;
import com.orderon.dao.InventoryManager;
import com.orderon.dao.MaterialManager;
import com.orderon.dao.MenuItemManager;
import com.orderon.dao.OutletManager;
import com.orderon.dao.RecipeManager;
import com.orderon.dao.ServiceManager;
import com.orderon.dao.TaxManager;
import com.orderon.dao.VendorManager;
import com.orderon.interfaces.IExpense;
import com.orderon.interfaces.IInventory;
import com.orderon.interfaces.IMaterial;
import com.orderon.interfaces.IMenuItem;
import com.orderon.interfaces.IOutlet;
import com.orderon.interfaces.IPurchase;
import com.orderon.interfaces.IRecipe;
import com.orderon.interfaces.IService;
import com.orderon.interfaces.ITax;
import com.orderon.interfaces.IVendor;
import com.orderon.interfaces.IVendorTransaction;

@Path("/InventoryServices")
public class InventoryServices {

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
	@Path("/v1/getMaterials")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMaterials(@QueryParam("outletId") String outletId, @QueryParam("categoryWise") boolean categoryWise
			, @QueryParam("isStockCheck") boolean isStockCheck) {
		JSONObject outObj = new JSONObject();
		JSONArray categoryArr = new JSONArray();
		JSONObject categoryObj = null;
		JSONArray materialArr = new JSONArray();
		JSONObject materialObj = null;
		
		IMaterial dao = new MaterialManager(false);
		ArrayList<Material> materials = null;

		try {
			if(categoryWise) {
				for (MaterialCategory category : MaterialCategory.values()) {
					categoryObj = new JSONObject();
					categoryObj.put("category", category.toString());
					categoryObj.put("name", category.getName());
					categoryArr.put(categoryObj);
					
					materials = dao.getMaterials(outletId, category.getName());
					if(materials!=null) {
						materialObj = new JSONObject();
						materialObj.put("category", category.getName());
						materialObj.put("materials", new JSONArray(materials));
						materialArr.put(materialObj);
					}
				}
				outObj.put("categories", categoryArr);
				outObj.put("materials", materialArr);
			}else if(isStockCheck){
				materials = dao.getMaterials(outletId, MaterialCategory.RECIPE_MANAGED.getName());
				if(materials!=null) {
					for(int i=0; i<materials.size(); i++) {
						materialObj = new JSONObject();
						materialObj.put("name", materials.get(i).getName());
						materialObj.put("subType", materials.get(i).getSubType());
						materialObj.put("displayableUnit", materials.get(i).getDisplayableUnit());
						materialObj.put("sku", materials.get(i).getSku());
						materialArr.put(materialObj);
					}
					outObj.put("materials", materialArr);
				}
			}else {
				materials = dao.getMaterials(outletId);
				outObj.put("materials", materials);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getRecipeManagedMaterials")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRecipeManagedMaterials(@QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();
		IMaterial dao = new MaterialManager(false);
		ArrayList<Material> materials = null;

		try {
			materials = dao.getMaterials(outletId, MaterialCategory.RECIPE_MANAGED.getName());
			if(materials==null) {
				outObj.put("status", false);
			}else {
				outObj.put("materials", materials);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getMaterialFilters")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMaterialFilters(@QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();
		JSONArray categoryArr = new JSONArray();
		JSONObject categoryObj = null;
		JSONArray typeArr = new JSONArray();
		JSONObject typeObj = null;
		
		IMaterial dao = new MaterialManager(false);

		try {
			for (MaterialCategory category : MaterialCategory.values()) {
				categoryObj = new JSONObject();
				categoryObj.put("category", category.toString());
				categoryObj.put("name", category.getName());
				categoryArr.put(categoryObj);
			}
			for (MaterialType type : MaterialType.values()) {
				typeObj = new JSONObject();
				typeObj.put("type", type.toString());
				typeArr.put(typeObj);
			}
			outObj.put("categories", categoryArr);
			outObj.put("types", typeArr);
			outObj.put("subTypes", dao.getMaterialSubTypes(outletId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/addMaterial")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addMaterial(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IMaterial dao = new MaterialManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			String name = "";
			MeasurableUnit displayableUnit = null;
			BigDecimal countableConversion = null;
			Boolean isCountable = false;
			CountableUnit countableUnit = CountableUnit.NONE;
			BigDecimal ratePerUnit = null;
			String materialType = "";
			String subType = "";
			MaterialCategory category = null;
			BigDecimal minQuantity = null;
			BigDecimal quantity = null;
			String outletId = "";
			
			if(!inObj.has("name")) {
				outObj.put("message", "Material Name not found.");
				return outObj.toString();
			}else if(!inObj.has("displayableUnit")) {
				outObj.put("message", "MeasurableUnit not found.");
				return outObj.toString();
			}else if(!inObj.has("ratePerUnit")) {
				outObj.put("message", "RatePerUnit not found.");
				return outObj.toString();
			}else if(inObj.has("isCountable")) {
				isCountable = inObj.getBoolean("isCountable");
				if(isCountable) {
					if(!inObj.has("countableUnit")) {
						outObj.put("message", "Countable MeasurableUnit not found.");
						return outObj.toString();
					}else if(!inObj.has("countableConversion")) {
						outObj.put("message", "Countable MeasurableUnit Conversion not found.");
						return outObj.toString();
					}
				}
			}else if(!inObj.has("type")) {
				outObj.put("message", "Material Type not found.");
				return outObj.toString();
			}else if(!inObj.has("category")) {
				outObj.put("message", "Category not found.");
				return outObj.toString();
			}else if(!inObj.has("outletId")) {
				outObj.put("message", "OutletId not found.");
				return outObj.toString();
			}
			
			name = inObj.getString("name");
			ratePerUnit = new BigDecimal(inObj.getDouble("ratePerUnit"));
			displayableUnit = MeasurableUnit.valueOf(filterUnitToStore(inObj.getString("displayableUnit")));
			isCountable = inObj.getBoolean("isCountable");
			if(isCountable) {
				countableUnit = CountableUnit.valueOf(inObj.getString("countableUnit"));
				countableConversion = new BigDecimal(inObj.getDouble("countableConversion"));
				ratePerUnit = ratePerUnit.divide(countableConversion);
			}
			materialType = inObj.getString("type");
			subType = inObj.getString("subType");
			category = MaterialCategory.valueOf(inObj.getString("category"));
			minQuantity = new BigDecimal(inObj.getDouble("minQuantity"));
			quantity = new BigDecimal(inObj.getDouble("quantity"));
			outletId = inObj.getString("outletId");
			JSONArray taxes = inObj.getJSONArray("taxes");
			
			//Convert rate, quantity and minQuantity to base value equivalent to the base units.
			ratePerUnit = displayableUnit.convertToBaseRate(ratePerUnit);
			minQuantity = displayableUnit.convertToBaseUnit(minQuantity);
			quantity = displayableUnit.convertToBaseUnit(quantity);
			name = name.toUpperCase();
					
			if (dao.materialExists(outletId, name)) {
				outObj.put("status", false);
				outObj.put("message", "Item Exists.");
				return outObj.toString();
			}
			
			outObj = dao.addMaterial(name, MeasurableUnit.valueOf(displayableUnit.getAssociation()), displayableUnit, countableConversion, isCountable, 
					countableUnit, ratePerUnit, materialType, subType, category, minQuantity, quantity, outletId, "1", taxes);
			
			if(outObj.getBoolean("status")) {
				
				IInventory inventDao = new InventoryManager(false);
				outObj.put("status", inventDao.addNewMaterialToInventory(outObj.getInt("sku"), quantity, ratePerUnit, outletId));
				if(materialType.equals(AccessManager.MATERIAL_DIRECT_ITEM)) {
					IRecipe recipeDao = new RecipeManager(false);
					recipeDao.addDirectRecipe(outletId, inObj.getString("menuId"), outObj.getInt("sku"));
				}else if(materialType.equals(AccessManager.MATERIAL_PROCESSED)) {
					IRecipe recipeDao = new RecipeManager(false);
					JSONArray recipeItems = inObj.getJSONArray("recipeItems");
					JSONObject material = null;
					for(int i=0; i<recipeItems.length(); i++) {
						material = recipeItems.getJSONObject(i);
						recipeDao.addRecipeForProcessedItem(outletId, new BigDecimal(material.getDouble("quantity")), outObj.getInt("sku"), material.getInt("sku"), 
								material.getString("unit"));
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getMaterialByName")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMaterialByName(@QueryParam("outletId") String outletId, @QueryParam("name") String name) {
		JSONObject outObj = new JSONObject();
		IMaterial dao = new MaterialManager(false);
		Material material = dao.getMaterialByName(outletId, name);

		try {
			outObj.put("status", false);
			if(material!= null) {
				outObj.put("status", true);
				outObj.put("material", material);
				return outObj.toString();
			}
			outObj.put("message", "Material Not found");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getMaterial")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMaterialByName(@QueryParam("outletId") String outletId, @QueryParam("sku") int sku) {
		JSONObject outObj = new JSONObject();
		IMaterial dao = new MaterialManager(false);
		Material material = dao.getMaterialBySku(outletId, sku);

		try {
			outObj.put("status", false);
			if(material!= null) {
				outObj.put("status", true);
				outObj.put("material", new JSONObject(material));
				if(material.getMaterialType().equals(AccessManager.MATERIAL_DIRECT_ITEM)) {
					IRecipe recipeDao = new RecipeManager(false);
					IMenuItem menuDao = new MenuItemManager(false);
					String menuId = recipeDao.getRecipeForDirectItem(outletId, sku).getMenuId();
					outObj.put("menuId", menuId);
					outObj.put("menutitle", menuDao.getMenuById(outletId, menuId).getTitle());
				}
				return outObj.toString();
			}
			outObj.put("message", "Material Not found");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getMaterialsRunningOut")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStockRunningOut(@QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();
		JSONArray categoryArr = new JSONArray();
		JSONObject categoryObj = null;
		JSONArray materialArr = new JSONArray();
		JSONObject materialObj = null;
		
		IMaterial dao = new MaterialManager(false);
		ArrayList<Material> materials = null;

		try {
			for (MaterialCategory category : MaterialCategory.values()) {
				categoryObj = new JSONObject();
				categoryObj.put("category", category.getName());
				categoryArr.put(categoryObj);
				
				materials = dao.getMaterialsRunningOutOfStock(outletId, category.getName());
				if(materials!=null) {
					materialObj = new JSONObject();
					materialObj.put("category", category.getName());
					materialObj.put("materials", new JSONArray(materials));
					materialArr.put(materialObj);
				}
			}
			outObj.put("categories", categoryArr);
			outObj.put("materials", materialArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@GET
	@Path("/v1/getMaterialsBySearch")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMaterialsBySearch(@QueryParam("outletId") String outletId, @QueryParam("query") String query,
			@QueryParam("filterVariable") String filterVariable, @QueryParam("filter") String filter,
			@QueryParam("isStockCheck") boolean isStockCheck) {
		JSONObject outObj = new JSONObject();
		JSONArray categoryArr = new JSONArray();
		JSONObject categoryObj = null;
		JSONArray materialArr = new JSONArray();
		JSONObject materialObj = null;
		
		IMaterial dao = new MaterialManager(false);
		ArrayList<Material> materials = null;

		try {
			if(isStockCheck){
				materials = dao.getMaterialByFilter(outletId, filterVariable, filterVariable, query, MaterialCategory.RECIPE_MANAGED.getName());
				if(materials!=null) {
					for(int i=0; i<materials.size(); i++) {
						materialObj = new JSONObject();
						materialObj.put("name", materials.get(i).getName());
						materialObj.put("subType", materials.get(i).getSubType());
						materialObj.put("displayableUnit", materials.get(i).getDisplayableUnit());
						materialObj.put("sku", materials.get(i).getSku());
						materialArr.put(materialObj);
					}
					outObj.put("materials", materialArr);
				}
			}else {
				for (MaterialCategory category : MaterialCategory.values()) {
					categoryObj = new JSONObject();
					categoryObj.put("category", category.getName());
					categoryArr.put(categoryObj);
					
					materials = dao.getMaterialByFilter(outletId, filterVariable, filter, query, category.getName());
					if(materials!=null) {
						materialObj = new JSONObject();
						materialObj.put("category", category.getName());
						materialObj.put("materials", new JSONArray(materials));
						materialArr.put(materialObj);
					}
				}
				outObj.put("categories", categoryArr);
				outObj.put("materials", materialArr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/updateMaterial")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateMaterial(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IMaterial dao = new MaterialManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("outletId")) {
				outObj.put("message", "OutletId not found.");
				return outObj.toString();
			}else if(!inObj.has("sku")) {
				outObj.put("message", "SKU not found.");
				return outObj.toString();
			}
			
			String outletId = inObj.getString("outletId");
			int sku = inObj.getInt("sku");
			MeasurableUnit displayableUnit = MeasurableUnit.valueOf(filterUnitToStore(inObj.getString("displayableUnit")));
			BigDecimal minQuantity = new BigDecimal(inObj.getDouble("minQuantity"));
			
			minQuantity = displayableUnit.convertToBaseUnit(minQuantity);
			boolean isCountable = inObj.getBoolean("isCountable");
			CountableUnit countableUnit = CountableUnit.NONE;
			BigDecimal countableConversion = null;
			if(isCountable) {
				countableUnit = CountableUnit.valueOf(inObj.getString("countableUnit"));
				countableConversion = new BigDecimal(inObj.getDouble("countableConversion"));
			}
			outObj = dao.updateMaterial(sku, inObj.getString("name"), MeasurableUnit.valueOf(displayableUnit.getAssociation()), displayableUnit, 
							countableConversion, inObj.getBoolean("isCountable"), countableUnit, inObj.getString("type"), 
							inObj.getString("subType"), MaterialCategory.valueOf(inObj.getString("category")), minQuantity, 
							outletId, "AVAILABLE", inObj.getBoolean("isActive"), inObj.getJSONArray("taxes"));
			
			if(outObj.getBoolean("status")) {
				Material material = dao.getMaterialBySku(outletId, sku);
				
				if(material.getMaterialType().equals(AccessManager.MATERIAL_DIRECT_ITEM)) {
					IRecipe recipeDao = new RecipeManager(false);
					recipeDao.updateDirectRecipe(outletId, inObj.getString("menuId"), sku);
				}else if(material.getMaterialType().equals(AccessManager.MATERIAL_PROCESSED)) {
					IRecipe recipeDao = new RecipeManager(false);
					recipeDao.deleteRecipeItemForProcessedMaterial(outletId, sku);
					JSONArray recipeItems = inObj.getJSONArray("recipeItems");
					JSONObject recipeMaterial = null;
					for(int i=0; i<recipeItems.length(); i++) {
						recipeMaterial = recipeItems.getJSONObject(i);
						recipeDao.addRecipeForProcessedItem(outletId, new BigDecimal(recipeMaterial.getDouble("quantity")), sku, 
								recipeMaterial.getInt("sku"), recipeMaterial.getString("unit"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();

	}

	@POST
	@Path("/v1/deleteMaterial")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteStockItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IMaterial dao = new MaterialManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteMaterial(inObj.getString("outletId"), inObj.getInt("sku")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getUnits")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUnit() {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		try {
			for (MeasurableUnit measurableUnit : MeasurableUnit.values()) {
				JSONObject obj = new JSONObject();

				obj.put("unit", AccessManager.filterUnitToDisplay(measurableUnit.toString()));
				obj.put("association", measurableUnit.getAssociation());
				obj.put("value", measurableUnit.getConversion());
				obj.put("conversion", measurableUnit.getConversion());
				outArr.put(obj);
			}
			outObj.put("measurableUnits", outArr);
			outArr = new JSONArray();
			
			for (CountableUnit unit : CountableUnit.values()) {
				JSONObject obj = new JSONObject();

				obj.put("unit", unit.toString());
				outArr.put(obj);
			}
			outObj.put("countableUnits", outArr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getTaxesForMaterials")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTaxesForMaterials(@QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();

		ITax dao = new TaxManager(false);
		ArrayList<Tax> taxes = null;
		taxes = dao.getTaxesForMaterials(outletId);
		try {
			outObj.put("taxes", taxes);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getNextPurchaseId")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNextPurchaseId(@QueryParam("outletId") String outletId, @QueryParam("type") String type) {
		JSONObject outObj = new JSONObject();
		
		IPurchase dao = new InventoryManager(false);

		try {
			outObj.put("purchaseId", dao.getNextPurchaseId(outletId, type));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/addPurchase")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addPurchase(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IPurchase dao = new InventoryManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("billNo")) {
				outObj.put("message", "Bill No not found.");
				return outObj.toString();
			}else if(!inObj.has("vendorId")) {
				outObj.put("message", "Vendor Details not found.");
				return outObj.toString();
			}else if(!inObj.has("outletId")) {
				outObj.put("message", "Outlet Details not found.");
				return outObj.toString();
			}else if(!inObj.has("grandTotal")) {
				outObj.put("message", "GrandTotal not found.");
				return outObj.toString();
			}else if(!inObj.has("purchaseDate")) {
				outObj.put("message", "Purchase Date not found.");
				return outObj.toString();
			}else if(!inObj.has("materials")) {
				outObj.put("message", "Materials not found.");
				return outObj.toString();
			}else if(!inObj.has("paymentType")) {
				outObj.put("message", "Payment Type not found.");
				return outObj.toString();
			}else if(!inObj.has("account")) {
				outObj.put("message", "Payment account not found.");
				return outObj.toString();
			}
			
			String billNo = inObj.getString("billNo");
			int vendorId = inObj.getInt("vendorId");
			String outletId = inObj.getString("outletId");
			String corporateId = inObj.getString("corporateId");
			String userId = inObj.getString("userId");
			BigDecimal grandTotal = new BigDecimal(inObj.getDouble("grandTotal")).setScale(2, BigDecimal.ROUND_HALF_UP);
			String purchaseDate = inObj.getString("purchaseDate");
			String paymentType = inObj.getString("paymentType");
			String account = inObj.getString("account");
			String remark = inObj.getString("remark");

			String challanNo = inObj.getString("challanNo");
			BigDecimal additionalDiscount = new BigDecimal(inObj.getDouble("additionalDiscount")).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal totalDiscount = new BigDecimal(inObj.getDouble("totalDiscount")).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal charge = new BigDecimal(inObj.getDouble("charge")).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal roundOff = new BigDecimal(inObj.getDouble("roundOff")).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal totalGst = new BigDecimal(inObj.getDouble("totalGst")).setScale(2, BigDecimal.ROUND_HALF_UP);
			JSONArray materials = inObj.getJSONArray("materials");
			
			
			if(materials.length() == 0) {
				outObj.put("message", "Please add materials to purchase. Materials cannot be empty.");
				return outObj.toString();
			}else if(paymentType.isEmpty()) {
				outObj.put("message", "Please enter Payment Type.");
				return outObj.toString();
			}else if(!paymentType.equals(PaymentType.CREDIT.toString()) && account.isEmpty()) {
				outObj.put("message", "Please enter Acount of Payment.");
				return outObj.toString();
			}
			
			outObj = dao.addPurchase(billNo, challanNo, vendorId, outletId, additionalDiscount, totalDiscount, charge, 
					roundOff, totalGst, grandTotal, purchaseDate, paymentType, account, remark);
			
			if(outObj.getBoolean("status")) {
				
				if(!paymentType.equals(PaymentType.CREDIT.toString())) {
					IExpense expenseDao = new ExpenseManager(false);
					IVendor vendorDao = new VendorManager(false);
					IOutlet outletDao = new OutletManager(false);
					IService serviceDao = new ServiceManager(false);
					Settings settings = outletDao.getSettings(outletId);
					
					Vendor vendor = vendorDao.getVendorById(outletId, vendorId);
					expenseDao.addExpense(outletId, grandTotal, remark, vendor.getName(), billNo, 0, paymentType, "INVENTORY_PURCHASE", 
							account, inObj.getString("userId"), "", purchaseDate, serviceDao.getServiceType(outletId));
					if (settings.getHasCashDrawer()) {
						Services.cashdrawerOpen(outletId);
					}
				}else {
					IVendor vendorDao = new VendorManager(false);
					vendorDao.updateVendorBalance(vendorId, outletId, grandTotal, AccessManager.TRANSASCTION_CREDIT);
					IVendorTransaction vendorTransDao = new VendorManager(false);
					vendorTransDao.addVendorTransaction(corporateId, outletId, vendorId, AccessManager.TRANSASCTION_CREDIT, grandTotal, 
							"", purchaseDate, "", userId);
				}
				JSONObject materialObj = new JSONObject();
				
				IInventory inventDao = new InventoryManager(false);
				IMaterial materialDao = new MaterialManager(false);
				BigDecimal quantity = null;
				BigDecimal ratePerUnit = null;
				Material material = null;
				JSONArray taxes = null;
				MeasurableUnit measurableUnit = MeasurableUnit.NONE;
				for (int i = 0; i < materials.length(); i++) {
					materialObj = materials.getJSONObject(i);
					//Get material.
					material = materialDao.getMaterialBySku(outletId, materialObj.getInt("sku"));
					
					quantity =  new BigDecimal(materialObj.getDouble("quantity"));
					
					ratePerUnit = new BigDecimal(materialObj.getDouble("ratePerUnit"));
					measurableUnit = MeasurableUnit.valueOf(material.getDisplayableUnit());
					
					if(material.getIsCountable()) {
						ratePerUnit = ratePerUnit.divide(material.getCountableConversion());
						quantity = quantity.multiply(material.getCountableConversion());
					}
					taxes = materialObj.getJSONArray("tax");
					ratePerUnit = measurableUnit.convertToBaseRate(ratePerUnit);
					quantity = measurableUnit.convertToBaseUnit(quantity);
					//Adding purchase to purchaseLog.
					outObj.put("status", inventDao.addMaterialAfterPurchase(outObj.getString("purchaseId"), materialObj.getInt("sku"), quantity, 
							ratePerUnit, outletId, 
							new BigDecimal(materialObj.getDouble("taxAmount")).setScale(2, BigDecimal.ROUND_HALF_UP), 
							materialObj.getDouble("taxValue"), 
							new BigDecimal(materialObj.getDouble("discount")).setScale(2, BigDecimal.ROUND_HALF_UP), 
							new BigDecimal(materialObj.getDouble("subTotal")).setScale(2, BigDecimal.ROUND_HALF_UP),
							new BigDecimal(materialObj.getDouble("total")).setScale(2, BigDecimal.ROUND_HALF_UP)));
					
					
					//calculate updated quantity
					quantity = quantity.add(material.getQuantity());
					
					//Update quantity, ratePerUnit and taxes.
					materialDao.updateMaterial(outletId, materialObj.getInt("sku"), quantity, ratePerUnit, taxes);
				}
			}
			
		} catch (DateTimeParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return outObj.toString();
	}

	@GET
	@Path("/v1/getPurchases")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPurchases(@QueryParam("outletId") String outletId, @QueryParam("startDate") String startDate, 
			@QueryParam("endDate") String endDate) {
		
		JSONObject outObj = new JSONObject();
		IPurchase dao = new InventoryManager(false);
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		
		if(startDate.isEmpty() && endDate.isEmpty()) {
			endDate = now.format(dateFormat);
			now = LocalDateTime.now().plusDays(-7);
			startDate = now.format(dateFormat);
		}

		try {
			outObj.put("purchases", dao.getPurchaseHistory(outletId, startDate, endDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCreditPurchaseByVendor")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCreditPurchaseByVendor(@QueryParam("outletId") String outletId, @QueryParam("vendorId") int vendorId) {
		
		JSONObject outObj = new JSONObject();
		IPurchase dao = new InventoryManager(false);

		try {
			outObj.put("purchases", dao.getCreditPurchasesByVendor(outletId, vendorId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getInventoryLogForPurchase")
	@Produces(MediaType.APPLICATION_JSON)
	public String getInventoryLogForPurchase(@QueryParam("outletId") String outletId, @QueryParam("purchaseId") String purchaseId) {
		JSONObject outObj = new JSONObject();
		IInventory dao = new InventoryManager(false);

		try {
			outObj.put("logs", dao.getInventoryLogForPurchase(outletId, purchaseId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getInventoryCheckLog")
	@Produces(MediaType.APPLICATION_JSON)
	public String getInventoryCheckLog(@QueryParam("outletId") String outletId, @QueryParam("startDate") String startDate, 
			@QueryParam("endDate") String endDate) {
		
		JSONObject outObj = new JSONObject();
		IInventory dao = new InventoryManager(false);
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		
		if(startDate.isEmpty() && endDate.isEmpty()) {
			endDate = now.format(dateFormat);
			now = LocalDateTime.now().plusDays(-7);
			startDate = now.format(dateFormat);
		}

		try {
			outObj.put("checks", dao.getInventoryCheckLog(outletId, startDate, endDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/inventoryCheck")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String inventoryCheck(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IInventory dao = new InventoryManager(false);
		IMaterial materialDao = new MaterialManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("serviceDate")) {
				outObj.put("message", "ServiceDate No not found.");
				return outObj.toString();
			}else if(!inObj.has("userId")) {
				outObj.put("message", "UserId not found.");
				return outObj.toString();
			}else if(!inObj.has("outletId")) {
				outObj.put("message", "Outlet Details not found.");
				return outObj.toString();
			}else if(!inObj.has("materialData")) {
				outObj.put("message", "Material Data not found.");
				return outObj.toString();
			}
			
			String outletId = inObj.getString("outletId");
			String userId = inObj.getString("userId");
			JSONArray materials = inObj.getJSONArray("materialData");
			JSONArray updatedMaterialData = new JSONArray();
			
			if(materials.length() == 0) {
				outObj.put("message", "Please select materials to check. Materials cannot be empty.");
				return outObj.toString();
			}
			
			JSONObject materialObj = new JSONObject();
			Material material = null;
			BigDecimal quantityDifference = new BigDecimal(0.0);
			BigDecimal currentQuantityInBaseUnit = new BigDecimal(0.0);
			MeasurableUnit displayableUnit = null;
			for(int i=0; i<materials.length(); i++) {
				materialObj = materials.getJSONObject(i);
				//get current material
				material = materialDao.getMaterialBySku(outletId, materialObj.getInt("sku"));
				//convert entered quantity to  base unit
				displayableUnit = MeasurableUnit.valueOf(material.getDisplayableUnit());
				currentQuantityInBaseUnit = displayableUnit.convertToBaseUnit(new BigDecimal(materialObj.getDouble("quantity")));
				//calculate quantity difference
				quantityDifference = currentQuantityInBaseUnit.subtract(material.getQuantity());
				
				//Adding old quantity to the material data obj.
				materialObj.put("oldQuantity", material.getDisplayableQuantity());
				materialObj.put("displayableUnit", material.getDisplayableUnit());
				//Adding the quantity difference to the material data obj.
				materialObj.put("quantityDiff", displayableUnit.convertToDisplayableUnit(quantityDifference));
				materialObj.put("subType", material.getSubType());
				materialObj.put("name", material.getName());
				
				materialDao.updateQuantity(outletId, materialObj.getInt("sku"), currentQuantityInBaseUnit);
				
				updatedMaterialData.put(materialObj);
			}
			
			outObj.put("status", dao.inventoryCheck(outletId, updatedMaterialData, userId));
			outObj.put("materials", updatedMaterialData);
	
		} catch (DateTimeParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return outObj.toString();
	}

	@POST
	@Path("/v3/sendInventoryCheckEmail")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String sendInventoryCheckEmail(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IOutlet outletDao = new OutletManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			if(!inObj.has("userId")) {
				outObj.put("message", "UserId not found.");
				return outObj.toString();
			}else if(!inObj.has("outletId")) {
				outObj.put("message", "Outlet Details not found.");
				return outObj.toString();
			}else if(!inObj.has("printableData")) {
				outObj.put("message", "Email Data not found.");
				return outObj.toString();
			}
			
			String outletId = inObj.getString("outletId");
			String userId = inObj.getString("userId");
			String emailText = inObj.getString("printableData");
			
			Outlet outlet = outletDao.getOutlet(outletId);
			
			String emailSubject = "Inventory Check performed at Outlet: " + outlet.getName() + ", " 
					+ outlet.getLocation().getString("place")
					+ " by " + userId;
			
			Services.SendEmailWithAttachment(outletId, emailSubject, emailText, "", null, false, false);

			outObj.put("status", true);
		} catch (DateTimeParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return outObj.toString();
	}
}
