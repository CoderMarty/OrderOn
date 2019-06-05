package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.EntityString;
import com.orderon.dao.AccessManager.MenuItem;

public interface IMenuItem {
	
	public MenuItem itemExists(String hotelId, String title);

	public boolean isTaxableMenuItem(String hotelId, String menuId);

	public String addMenuItem(String hotelId, String title, String description, String collection, String subCollection, String station,
			String flags, String groups, int preparationTime, BigDecimal deliveryRate, BigDecimal dineInRate, BigDecimal dineInNonAcRate, 
			BigDecimal onlineRate, BigDecimal costPrice, String image, int incentiveType, int incentive, String code, String taxes, String charges,
			boolean isRecomended, boolean isTreats, boolean isDefault, boolean isBogo, BigDecimal comboReducedPrice, boolean isAddon
			, boolean syncOnZomato, boolean gstInclusive, String discountType, BigDecimal discountValue);

	public MenuItem getMenuById(String hotelId, String menuId);

	public MenuItem getMenuItemByTitle(String hotelId, String title);

	public ArrayList<MenuItem> getMenuItemBySearch(String hotelId, String query);

	public ArrayList<MenuItem> getMenu(String hotelId);

	public ArrayList<MenuItem> getMenuForIntegration(String hotelId);

	public ArrayList<EntityString> getMenuItems(String hotelId);
	
	public ArrayList<MenuItem> getMenuMP(String hotelId);

	public String getNextMenuId(String hotelId, String collection);

	public Boolean updateMenuItem(String hotelId, String menuId, String title, String description, String collection, String subCollection, String station,
				String flags, String groups, int preparationTime, BigDecimal deliveryRate, BigDecimal dineInRate, BigDecimal dineInNonAcRate, 
				BigDecimal onlineRate, BigDecimal costPrice, String image, int incentiveType, int incentive, String code, String taxes, String charges,
				boolean isRecomended, boolean isTreats, boolean isDefault, boolean isBogo, BigDecimal comboReducedPrice, boolean isAddon,
				boolean syncOnZomato, boolean gstInclusive, String discountType, BigDecimal discountValue);
	
	public Boolean updateMenuItemStockState(String hotelId, String menuId, int state);
	
	public Boolean updateMenuItemStateOnZomato(String hotelId, String menuId, boolean state);

	public boolean deleteItem(String hotelId, String menuId);

	public boolean changeMenuItemState(String hotelId, String menuId, int state);
}
