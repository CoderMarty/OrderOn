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
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.commons.Configurator;
import com.orderon.commons.FileManager;
import com.orderon.dao.ChargeManager;
import com.orderon.dao.GroupManager;
import com.orderon.dao.MenuItemManager;
import com.orderon.dao.OnlineOrderingPortalManager;
import com.orderon.dao.OrderManager;
import com.orderon.dao.OutletManager;
import com.orderon.dao.TaxManager;
import com.orderon.dao.AccessManager.Charge;
import com.orderon.dao.AccessManager.EntityString;
import com.orderon.dao.AccessManager.Group;
import com.orderon.dao.AccessManager.MenuItem;
import com.orderon.dao.AccessManager.OnlineOrderingPortal;
import com.orderon.dao.AccessManager.Outlet;
import com.orderon.dao.AccessManager.Specifications;
import com.orderon.dao.AccessManager.Tax;
import com.orderon.interfaces.ICharge;
import com.orderon.interfaces.IGroup;
import com.orderon.interfaces.IMenuItem;
import com.orderon.interfaces.IOnlineOrderingPortal;
import com.orderon.interfaces.IOutlet;
import com.orderon.interfaces.ISpecification;
import com.orderon.interfaces.ITax;

@Path("/Services/Menu")
public class MenuServices {
	
	@GET
	@Path("/v3/Integration/getMenu")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuv3(@QueryParam("systemId") String systemId, @QueryParam("outletId") String outletId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		IGroup groupDao = new GroupManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		ArrayList<OnlineOrderingPortal> portals = portalDao.getOnlineOrderingPortals(systemId);
		ArrayList<MenuItem> items = dao.getMenuForIntegration(systemId, outletId);
		ArrayList<Group> groups = groupDao.getGroups(systemId, outletId);
		try {
			for (int i = 0; i < items.size(); i++) {
				itemDetails = addItemsToObject(items.get(i), portals);
				itemsArr.put(itemDetails);
			}
			outObj.put("menuItems", itemsArr);
			outObj.put("groups", groups);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getMenuItems")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuItems(@QueryParam("systemId") String systemId, @QueryParam("outletId") String outletId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;

		IMenuItem dao = new MenuItemManager(false);
		ArrayList<EntityString> menuItems = dao.getMenuItems(systemId, outletId);
		try {
			for (int i = 0; i < menuItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("title", menuItems.get(i).getEntity());
				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v1/getMenuMP")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuMP(@QueryParam("systemId") String systemId, @QueryParam("outletId") String outletId, @QueryParam("query") String query) {
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;

		IMenuItem dao = new MenuItemManager(false);
		ArrayList<MenuItem> orderItems = null;
		if(query.equals(""))
			orderItems = dao.getMenuMP(systemId, outletId);
		else
			orderItems = dao.getMenuItemBySearch(systemId, outletId, query);
		try {
			for (int i = 0; i < orderItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("menuId", orderItems.get(i).getMenuId());
				itemDetails.put("collection", orderItems.get(i).getCollection());
				itemDetails.put("subCollection", orderItems.get(i).getSubCollection());
				itemDetails.put("title", orderItems.get(i).getTitle());
				itemDetails.put("description", orderItems.get(i).getDescription());
				itemDetails.put("flags", orderItems.get(i).getFlags());
				itemDetails.put("deliveryRate", orderItems.get(i).getDeliveryRate());
				itemDetails.put("dineInRate", orderItems.get(i).getDineInRate());
				itemDetails.put("onlineRate", orderItems.get(i).getOnlineRate());
				itemDetails.put("inStock", orderItems.get(i).getInStock());
				itemDetails.put("syncOnZomato", orderItems.get(i).getSyncOnZomato());
				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v3/getMenuItem")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuItem(@QueryParam("systemId") String systemId, @QueryParam("menuId") String menuId) {
		IMenuItem dao = new MenuItemManager(false);
		return new JSONObject(dao.getMenuById(systemId, menuId)).toString();
	}

	@GET
	@Path("/Zomato/v3/getMenuItem")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuItemForZ(@QueryParam("systemId") String systemId, @QueryParam("menuId") String menuId) {
		IMenuItem dao = new MenuItemManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		ArrayList<OnlineOrderingPortal> portals = portalDao.getOnlineOrderingPortals(systemId);
		
		return addItemsToObject(dao.getMenuById(systemId, menuId), portals).toString();
	}

	@POST
	@Path("/v1/deleteItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deteleItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IMenuItem dao = new MenuItemManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteItem(inObj.getString("hotelId"), inObj.getString("menuId")));
			FileManager.deleteFile("http://"+Configurator.getIp()+"/Images" + "/hotels/" + inObj.getString("hotelId") + "MenuItems" + inObj.getString("menuId") + ".jpg");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getNextMenuId")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNextMenuId(@QueryParam("systemId") String systemId, @QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		try {
			outObj.put("menuId", dao.getNextMenuId(systemId, outletId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getMenuOnly")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuOnly(@QueryParam("systemId") String systemId, @QueryParam("outletId") String outletId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		ArrayList<MenuItem> items = dao.getMenu(systemId, outletId);
		ArrayList<OnlineOrderingPortal> portals = portalDao.getOnlineOrderingPortals(systemId);
		try {
			for (int i = 0; i < items.size(); i++) {
				itemDetails = addItemsToObject(items.get(i), portals);
				itemsArr.put(itemDetails);
			}
			outObj.put("menuItems", itemsArr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getMenu")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuv3ForMP(@QueryParam("systemId") String systemId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		JSONObject outObj = new JSONObject();

		IMenuItem dao = new MenuItemManager(false);
		IGroup groupDao = new GroupManager(false);
		ISpecification specsDao = new OrderManager(false);
		ICharge chargeDao = new ChargeManager(false);
		ITax taxDao = new TaxManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		IOutlet outletDao = new OutletManager(false);

		ArrayList<EntityString> outletIds = outletDao.getOutletsIds(systemId);
		
		ArrayList<MenuItem> items = null;
		ArrayList<Specifications> specs = null;
		ArrayList<Group> groups = null;
		ArrayList<Tax> taxes = null;
		ArrayList<Charge> charges = null;
		ArrayList<OnlineOrderingPortal> portals = null;

		for (EntityString outletId : outletIds) {
		
			items = dao.getMenu(systemId, outletId.getEntity());
			specs = specsDao.getSpecifications(systemId, outletId.getEntity());
			groups = groupDao.getGroups(systemId, outletId.getEntity());
			taxes = taxDao.getTaxes(systemId, outletId.getEntity());
			charges = chargeDao.getCharges(systemId, outletId.getEntity());
			portals = portalDao.getOnlineOrderingPortals(systemId);
			try {
				for (int i = 0; i < items.size(); i++) {
					itemDetails = addItemsToObject(items.get(i), portals);
					itemsArr.put(itemDetails);
				}
				outObj.put("menuItems", itemsArr);
				outObj.put("addOns", itemsArr);
				outObj.put("groups", groups);
				outObj.put("specifications", specs);
				outObj.put("taxes", new JSONArray(taxes));
				outObj.put("charges", new JSONArray(charges));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v3/addMenuItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addMenuItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		IOutlet outletDao = new OutletManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String groups = inObj.getJSONArray("groups").toString();
			String flags = inObj.getJSONArray("flags").toString();
			String taxes = inObj.getJSONArray("taxes").toString();
			String charges = inObj.getJSONArray("charges").toString();

			String systemId = inObj.getString("systemId");
			String outletId = inObj.getString("outletId");
			
			Outlet outlet = outletDao.getOutletForSystem(systemId, outletId);
			
			if (dao.itemExists(systemId, outletId, inObj.getString("title"))) {
				outObj.put("error", "Item Exists");
			} else {
				String menuId = dao.addMenuItem(outlet.getCorporateId(), outlet.getRestaurantId(), systemId, outletId, inObj.getString("title"),
						inObj.getString("description"), inObj.getString("collection"), inObj.getString("subCollection"), inObj.getString("station"),
						flags, groups, inObj.getInt("preparationTime"), new BigDecimal(Double.toString(inObj.getDouble("deliveryRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("dineInRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("dineInNonAcRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("onlineRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("zomatoRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("swiggyRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("uberEatsRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("comboPrice"))),
						new BigDecimal(Double.toString(inObj.getDouble("costPrice"))), 
						inObj.getString("imgUrl"), inObj.getString("coverImgUrl"), 
						inObj.getInt("incentiveQuantity"), inObj.getInt("incentiveAmount"), inObj.getString("code"),
						taxes, charges, inObj.getBoolean("isRecomended"),
						inObj.getBoolean("isTreats"), inObj.getBoolean("isDefault"), inObj.getBoolean("isBogo"), inObj.getBoolean("isCombo"), 
						new BigDecimal(Double.toString(inObj.getDouble("comboReducedPrice"))), 
						inObj.getBoolean("syncOnZomato"), inObj.getBoolean("gstInclusive"),
						inObj.getString("discountType"), new BigDecimal(inObj.getDouble("discountValue")));

				if (!menuId.equals("")) {
					outObj.put("status", true);
					outObj.put("menuId", menuId);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/updateMenuItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateMenuItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String groups = inObj.getJSONArray("groups").toString();
			String flags = inObj.getJSONArray("flags").toString();
			String taxes = inObj.getJSONArray("taxes").toString().replaceAll("\"", "");
			String charges = inObj.getJSONArray("charges").toString().replaceAll("\"", "");

			String systemId = inObj.getString("systemId");
			String outletId = inObj.getString("outletId");
			
			if (inObj.getString("image") != "No image") {
				FileManager.deleteFile("http://"+Configurator.getIp()+"/Images" + "/hotels/" + inObj.getString("hotelId") + "MenuItems" + inObj.getString("menuId") + ".jpg");
			}

			outObj.put("status",
					dao.updateMenuItem(systemId, outletId, inObj.getString("menuId"), inObj.getString("title"),
							inObj.getString("description"), inObj.getString("collection"), 
							inObj.getString("subCollection"), inObj.getString("station"),
							flags, groups, inObj.getInt("preparationTime"), 
							new BigDecimal(Double.toString(inObj.getDouble("deliveryRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("dineInRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("dineInNonAcRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("onlineRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("zomatoRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("swiggyRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("uberEatsRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("comboPrice"))),
							new BigDecimal(Double.toString(inObj.getDouble("costPrice"))), 
							inObj.getString("imgUrl"), 
							inObj.getString("coverImgUrl"), 
							inObj.getInt("incentiveQuantity"), 
							inObj.getInt("incentiveAmount"), inObj.getString("code"),
							taxes, charges, 
							inObj.getBoolean("isRecomended"),
							inObj.getBoolean("isTreats"), 
							inObj.getBoolean("isDefault"), 
							inObj.getBoolean("isBogo"), 
							inObj.getBoolean("isCombo"), 
							new BigDecimal(Double.toString(inObj.getDouble("comboReducedPrice"))),
							inObj.getBoolean("syncOnZomato"), 
							inObj.getBoolean("gstInclusive"),
							inObj.getString("discountType"), 
							new BigDecimal(inObj.getDouble("discountValue"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateMenuItems")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateMenuItems(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", false);
			JSONArray menuItems = inObj.getJSONArray("menuItems");
			String systemId = inObj.getString("systemId");
			JSONObject menuItem = null;
			for (int i = 0; i < menuItems.length(); i++) {
				menuItem = menuItems.getJSONObject(i);
				if(menuItem.has("item_in_stock")) {
					dao.updateMenuItemStockState(systemId, Integer.toString(menuItem.getInt("item_id")), menuItem.getInt("item_in_stock")==1?true:false);
				}else {
					dao.updateMenuItemStateOnZomato(systemId, Integer.toString(menuItem.getInt("item_id")), menuItem.getInt("item_is_active")==1?true:false);
				}
			}
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	private JSONObject addItemsToObject(MenuItem item, ArrayList<OnlineOrderingPortal> portals) {
		JSONObject itemDetails = new JSONObject();
		try {
			itemDetails.put("station", item.getStation());
			itemDetails.put("menuId", item.getMenuId());
			itemDetails.put("title", item.getTitle());
			itemDetails.put("description", item.getDescription());
			itemDetails.put("collection", item.getCollection());
			itemDetails.put("subCollection", item.getSubCollection());
			itemDetails.put("flags", item.getFlags());
			itemDetails.put("preparationTime", item.getPreparationTime());
			itemDetails.put("deliveryRate", item.getDeliveryRate());
			itemDetails.put("dineInRate", item.getDineInRate());
			itemDetails.put("dineInNonAcRate", item.getDineInNonAcRate());
			itemDetails.put("onlineRate", item.getOnlineRate());
			itemDetails.put("image", item.getImgUrl());
			itemDetails.put("coverImgUrl", item.getCoverImgUrl());
			itemDetails.put("insStock", item.getInStock());
			itemDetails.put("code", item.getCode());
			itemDetails.put("taxes", item.getTaxes());
			itemDetails.put("groups", item.getGroups());
			itemDetails.put("charges", item.getCharges());
			itemDetails.put("isRecomended", item.getIsRecomended());
			itemDetails.put("isTreats", item.getIsTreats());
			itemDetails.put("isDefault", item.getIsDefault());
			itemDetails.put("isBogo", item.getIsBogo());
			itemDetails.put("isCombo", item.getIsCombo());
			itemDetails.put("comboPrice", item.getComboPrice());
			itemDetails.put("comboReducedPrice", item.getComboReducedPrice());
			itemDetails.put("discountType", item.getDiscountType());
			itemDetails.put("discountValue", item.getDiscountValue());

			for (OnlineOrderingPortal onlineOrderingPortal : portals) {
				if(onlineOrderingPortal.getMenuAssociation() == 0) {
					continue;
				}
				if(onlineOrderingPortal.getMenuAssociation() == 1) {
					itemDetails.put(onlineOrderingPortal.getPortal().toLowerCase() + "Rate", item.getOnlineRate1());
				}else if(onlineOrderingPortal.getMenuAssociation() == 2) {
					itemDetails.put(onlineOrderingPortal.getPortal().toLowerCase() + "Rate", item.getOnlineRate2());
				}else if(onlineOrderingPortal.getMenuAssociation() == 3) {
					itemDetails.put(onlineOrderingPortal.getPortal().toLowerCase() + "Rate", item.getOnlineRate3());
				}else if(onlineOrderingPortal.getMenuAssociation() == 4) {
					itemDetails.put(onlineOrderingPortal.getPortal().toLowerCase() + "Rate", item.getOnlineRate4());
				}else if(onlineOrderingPortal.getMenuAssociation() == 5) {
					itemDetails.put(onlineOrderingPortal.getPortal().toLowerCase() + "Rate", item.getOnlineRate5());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return itemDetails;
	}

}
