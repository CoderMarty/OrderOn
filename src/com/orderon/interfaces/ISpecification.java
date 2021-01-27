package com.orderon.interfaces;

import java.util.ArrayList;

import com.orderon.dao.AccessManager.OrderSpecification;
import com.orderon.dao.AccessManager.Specifications;

public interface ISpecification extends IAccess{
	
	public Boolean addOrderSpecification(String systemId, String outletId, String orderId, String subOrderId, String menuId, int itemId,
			String specification);

	public ArrayList<Specifications> getSpecifications(String systemId, String outletId);

	public boolean addSpecification(String sysmteId, String name);

	public ArrayList<OrderSpecification> getOrderedSpecification(String systemId, String orderId, String menuId,
			String subOrderId, int itemId);

	public ArrayList<OrderSpecification> getOrderedSpecification(String systemId, String orderId, String menuId);

	public ArrayList<Specifications> getSpecifications(String hotelId);

	public Specifications getSpecification(String hotelId, String spec);
}
