package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.EntityString;
import com.orderon.dao.AccessManager.MenuItem;

public interface IMenuItem {
	
	public boolean itemExists(String systemId, String outletId, String title);

	public String addMenuItem(String corporateId, String restaurantId, String systemId, String outletId, String title, String description, 
			String collection, String subCollection, String station, String flags, String groups, int preparationTime, 
			BigDecimal deliveryRate, BigDecimal dineInRate, BigDecimal dineInNonAcRate, BigDecimal onlineRate, BigDecimal zomatoRate, 
			BigDecimal swiggyRate, BigDecimal uberEatsRate, BigDecimal comboPrice, BigDecimal costPrice, String imgUrl, String coverImgUrl, 
			int incentiveQuantity, int incentiveAmount, String code, String taxes, String charges, boolean isRecomended, boolean isTreats, 
			boolean isDefault, boolean isBogo, boolean isCombo, BigDecimal comboReducedPrice, boolean syncOnZomato, 
			boolean gstInclusive, String discountType, BigDecimal discountValue);

	public MenuItem getMenuById(String systemId, String menuId);

	public MenuItem getMenuItemByTitle(String systemId, String outletId, String title);

	public ArrayList<MenuItem> getMenuItemBySearch(String systemId, String outletId, String query);

	public ArrayList<MenuItem> getMenu(String systemId);

	public ArrayList<MenuItem> getMenu(String systemId, String outletId);

	public ArrayList<MenuItem> getMenuForIntegration(String systemId, String outletId);

	public ArrayList<EntityString> getMenuItems(String systemId, String outletId);
	
	public ArrayList<MenuItem> getMenuItems(String systemId, String outletId, String collection, String subCollection);
	
	public ArrayList<MenuItem> getMenuMP(String systemId, String outletId);

	public String getNextMenuId(String systemId, String outletId);

	public Boolean updateMenuItem(String systemId, String outletId, String menuId, String title, String description, String collection, String subCollection, String station,
				String flags, String groups, int preparationTime, BigDecimal deliveryRate, BigDecimal dineInRate, BigDecimal dineInNonAcRate, 
				BigDecimal onlineRate, BigDecimal zomatoRate, BigDecimal swiggyRate, BigDecimal uberEatsRate, BigDecimal comboPrice, 
				BigDecimal costPrice, String imgUrl, String coverImgUrl, int incentiveQuantity, int incentiveAmount, String code, String taxes, String charges,
				boolean isRecomended, boolean isTreats, boolean isDefault, boolean isBogo, boolean isCombo, BigDecimal comboReducedPrice,
				boolean syncOnZomato, boolean gstInclusive, String discountType, BigDecimal discountValue);
	
	public Boolean updateMenuItemStockState(String systemId, String menuId, boolean inStock);

	public Boolean updateMenuImageUrl(String systemId, String menuId, String imageUrl);
	
	public Boolean updateMenuItemStateOnZomato(String systemId, String menuId, boolean state);

	public boolean deleteItem(String systemId, String menuId);
}
