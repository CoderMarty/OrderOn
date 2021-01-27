package com.orderon.dao;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.interfaces.IMenuItem;

public class MenuItemManager extends AccessManager implements IMenuItem {

	public MenuItemManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean itemExists(String systemId, String outletId, String title) {
		String sql = "SELECT * FROM MenuItems WHERE title='" + escapeString(title) + "' AND outletId='" + outletId + "';";

		return db.hasRecords(sql, systemId);
	}

	@Override
	public String addMenuItem(String corporateId, String restaurantId, String systemId, String outletId, String title, String description, 
			String collection, String subCollection, String station, String flags, String groups, int preparationTime, 
			BigDecimal deliveryRate, BigDecimal dineInRate, BigDecimal dineInNonAcRate, BigDecimal onlineRate, BigDecimal zomatoRate, 
			BigDecimal swiggyRate, BigDecimal uberEatsRate, BigDecimal comboPrice, BigDecimal costPrice, String imgUrl, String coverImgUrl, 
			int incentiveQuantity, int incentiveAmount, String code, String taxes, String charges, boolean isRecomended, boolean isTreats, 
			boolean isDefault, boolean isBogo, boolean isCombo, BigDecimal comboReducedPrice, boolean syncOnZomato, 
			boolean gstInclusive, String discountType, BigDecimal discountValue) {

		String menuId = getNextMenuId(systemId, outletId);
		
		taxes = taxes.replaceAll("\"", "");
		charges = charges.replaceAll("\"", "");
		flags = flags.replace("\"", "");
		
		title = AccessManager.toTitleCase(title);
		
		//If gst inclusive of price calculate GST amount, subtract amount from the rate.
		if(gstInclusive) {
			BigDecimal gstCal = new BigDecimal("0.95238095");
			dineInRate = dineInRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
			dineInNonAcRate = dineInNonAcRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
			deliveryRate = deliveryRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
			onlineRate = gstCal.multiply(onlineRate).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		
		String sql = "INSERT INTO MenuItems "
				+ "(corporateId, restaurantId, systemId, outletId, menuId, title, description, collection, subCollection, station, flags, groups, preparationTime, deliveryRate, "
				+ "dineInRate, dineInNonAcRate, onlineRate, onlineRate1, onlineRate2, onlineRate3, onlineRate4, onlineRate5, comboPrice, costPrice, "
				+ "imgUrl, coverImgUrl, code, incentiveQuantity, incentiveAmount, taxes, charges, isRecomended, isTreats, isDefault, isBogo, isCombo, "
				+ "comboReducedPrice, syncOnZomato, discountType, discountValue) VALUES('"
				+ escapeString(corporateId) + "', '" + escapeString(restaurantId) + "', '" + escapeString(systemId) + "', '" + escapeString(outletId) + "', '" 
				+ escapeString(menuId) + "', '" + escapeString(title) + "', '" + escapeString(description) + "', '" + escapeString(collection) + "', '" 
				+ escapeString(subCollection) + "', '" + escapeString(station) + "', '" + escapeString(flags) +"', '" + escapeString(groups) +  "', " + preparationTime + ", " 
				+ deliveryRate + ", " + dineInRate + ", " + dineInNonAcRate + ", " + onlineRate + ", " + zomatoRate + ", " + swiggyRate + ", " + uberEatsRate + ", " 
				+ onlineRate + ", " + onlineRate + ", " + comboPrice + ", " + costPrice + ", '" + imgUrl + "', '" + coverImgUrl + "', '"+ code + "', " 
				+ incentiveQuantity + ", " + incentiveAmount + ", '"+taxes+"'" + ", '" + charges + "', '" + Boolean.toString(isRecomended) + "', '" 
				+ Boolean.toString(isTreats) + "', '" + Boolean.toString(isDefault) + "', '" + Boolean.toString(isBogo) + "', '" + Boolean.toString(isCombo) + "', " 
				+ comboReducedPrice + ", '" +syncOnZomato+"', '"+discountType+"', "+discountValue+");";

		System.out.println(sql);
		if(db.executeUpdate(sql, systemId, true)) {
			return menuId;
		}
		return "";
	}

	@Override
	public MenuItem getMenuById(String systemId, String menuId) {
		String sql = "SELECT * FROM MenuItems WHERE menuId='" + escapeString(menuId) + "';";
		return db.getOneRecord(sql, MenuItem.class, systemId);
	}

	@Override
	public MenuItem getMenuItemByTitle(String systemId, String outletId, String title) {

		String sql = "SELECT * FROM MenuItems WHERE title = '" + escapeString(title) + "' AND outletId='"
				+ escapeString(outletId) + "';";

		return db.getOneRecord(sql, MenuItem.class, systemId);
	}

	@Override
	public ArrayList<MenuItem> getMenuItemBySearch(String systemId, String outletId, String query) {

		query = escapeString(query);
		String sql = "SELECT * FROM MenuItems WHERE title LIKE '%" + query + "%' OR menuId LIKE '%" + query
				+ "%' OR code LIKE '%" + query + "%' OR collection LIKE '%" + query + "%' ";
		
		if(!outletId.isEmpty()) {
			sql += "AND outletId = '"+ outletId + "'";
		}
		sql += ";";
		return db.getRecords(sql, MenuItem.class, systemId);
	}

	@Override
	public ArrayList<MenuItem> getMenu(String systemId) {
		String sql = "SELECT * FROM MenuItems ORDER BY title;";
		return db.getRecords(sql, MenuItem.class, systemId);
	}

	@Override
	public ArrayList<MenuItem> getMenu(String systemId, String outletId) {
		String sql = "SELECT * FROM MenuItems  WHERE outletId='" + outletId + "' AND inStock = 'true' ORDER BY title;";
		return db.getRecords(sql, MenuItem.class, systemId);
	}

	@Override
	public ArrayList<MenuItem> getMenuForIntegration(String systemId, String outletId) {
		String sql = "SELECT * FROM MenuItems  WHERE outletId='" + outletId + "' AND state = 'true' AND syncOnZomato = 'true';";
		return db.getRecords(sql, MenuItem.class, systemId);
	}

	@Override
	public ArrayList<EntityString> getMenuItems(String systemId, String outletId) {
		String sql = "SELECT title AS entityId FROM MenuItems WHERE outletId = '"+outletId+"';";
		return db.getRecords(sql, EntityString.class, systemId);
	}

	@Override
	public ArrayList<MenuItem> getMenuItems(String systemId, String outletId, String collection, String subCollection) {
		String sql = "SELECT * FROM MenuItems WHERE outletId='" + outletId + "' AND collection = '"+collection+"' AND subCollection = '"+subCollection+"';";
		return db.getRecords(sql, MenuItem.class, systemId);
	}

	@Override
	public ArrayList<MenuItem> getMenuMP(String systemId, String outletId) {
		String sql = "SELECT * FROM MenuItems ";
		if(!outletId.isEmpty()) {
			sql += "WHERE outletId = '"+ outletId + "' ";
		}
		sql += "ORDER BY outletId, collection, subcollection;";
		return db.getRecords(sql, MenuItem.class, systemId);
	}

	@Override
	public String getNextMenuId(String systemId, String outletId) {

		String sql = "SELECT MAX(CAST(menuId AS integer)) AS entityId FROM MenuItems WHERE outletId='" + outletId + "'";

		EntityId entity = db.getOneRecord(sql, EntityId.class, systemId);

		if (entity != null) {
			return Integer.toString((entity.getId() + 1));
		}
		return null;
	}

	@Override
	public Boolean updateMenuItem(String systemId, String outletId, String menuId, String title, String description, String collection, String subCollection, String station,
			String flags, String groups, int preparationTime, BigDecimal deliveryRate, BigDecimal dineInRate, BigDecimal dineInNonAcRate, 
			BigDecimal onlineRate, BigDecimal zomatoRate, BigDecimal swiggyRate, BigDecimal uberEatsRate, BigDecimal comboPrice, 
			BigDecimal costPrice, String imgUrl, String coverImgUrl, int incentiveQuantity, int incentiveAmount, String code, String taxes, String charges,
			boolean isRecomended, boolean isTreats, boolean isDefault, boolean isBogo, boolean isCombo, BigDecimal comboReducedPrice,
			boolean syncOnZomato, boolean gstInclusive, String discountType, BigDecimal discountValue) {
		
		title = AccessManager.toTitleCase(title);

		MenuItem menuItem = this.getMenuById(systemId, menuId);

		if(gstInclusive != menuItem.getGstInclusive()) {
			if(gstInclusive) {
				BigDecimal gstCal = new BigDecimal("100");
				gstCal = gstCal.divide(new BigDecimal("105"));
				dineInRate = dineInRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
				dineInNonAcRate = dineInNonAcRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
				deliveryRate = deliveryRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
				onlineRate = onlineRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}
		flags = flags.replace("\"", "");
		
		if(menuItem.getImgUrl().length()>10 && imgUrl.isEmpty()) {
			imgUrl = menuItem.getImgUrl();
		}
		
		String sql = "UPDATE MenuItems SET title = '" + escapeString(title) 
				+ "', description = '" + escapeString(description) 
				+ "', collection = '" + collection 
				+ "', subCollection = '" + subCollection 
				+ "', station = '" + station 
				+ "', flags = '" + flags 
				+ "', groups = '" + groups 
				+ "', preparationTime = " + preparationTime
				+ ", deliveryRate = " + deliveryRate 
				+ ", dineInRate = " + dineInRate  
				+ ", dineInNonAcRate = " + dineInNonAcRate  
				+ ", onlineRate = " + onlineRate  
				+ ", onlineRate1 = " + zomatoRate  
				+ ", onlineRate2 = " + swiggyRate  
				+ ", onlineRate3 = " + uberEatsRate  
				+ ", onlineRate4 = " + onlineRate  
				+ ", onlineRate5 = " + onlineRate  
				+ ", comboPrice = " + comboPrice 
				+ ", costPrice = " + costPrice 
				+ ", incentiveQuantity = " + incentiveQuantity
				+ ", incentiveAmount = " + incentiveAmount 
				+ ", code = '" + code
				+ "', taxes = '" + taxes 
				+ "', charges = '" + charges 
				+ "', imgUrl  ='" + imgUrl
				+ "', coverImgUrl  ='" + coverImgUrl 
				+ "', isRecomended = '" + isRecomended
				+ "', isTreats = '" + isTreats
				+ "', syncOnZomato = '" + syncOnZomato
				+ "', isBogo = '" + isBogo
				+ "', isCombo = '" + isCombo
				+ "', isDefault = '" + isDefault
				+ "', comboReducedPrice = " + comboReducedPrice
				+ ", discountType = '" + discountType
				+ "', discountValue = " + discountValue
				+ " WHERE menuId = '" + menuId + "';";

		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean updateMenuItemStockState(String systemId, String menuId, boolean inStock) {

		String sql = "UPDATE MenuItems SET inStock = " + inStock + " WHERE menuId = '" + escapeString(menuId) + "';";

		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Boolean updateMenuItemStateOnZomato(String systemId, String menuId, boolean state) {

		String sql = "UPDATE MenuItems SET syncOnZomato = '" + state + "' WHERE menuId = '" + escapeString(menuId) + "';";

		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean deleteItem(String systemId, String menuId) {
		String sql = "DELETE FROM MenuItems WHERE menuId = '" + menuId + "';";
		return db.executeUpdate(sql, systemId, true);
	}
	
	public void loadShortForms(String systemId, String outletId) {

		ArrayList<MenuItem> menuItems = this.getMenu(systemId, outletId);
		for (MenuItem menuItem : menuItems) {
			String sql = "UPDATE MenuItems SET shortform = '" + this.generateShortForm(menuItem.getTitle())
					+ "' WHERE menuId = '" + menuItem.getMenuId() + "';";
			db.executeUpdate(sql, systemId, false);
		}
	}

	@Override
	public Boolean updateMenuImageUrl(String systemId, String menuId, String imageUrl) {

		String sql = "UPDATE MenuItems SET img = '"+ imageUrl +"' WHERE menuId = '" + escapeString(menuId) + "';";

		return db.executeUpdate(sql, systemId, true);
	}
}
