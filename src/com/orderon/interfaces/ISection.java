package com.orderon.interfaces;

import java.util.ArrayList;

import com.orderon.dao.AccessManager.Section;

public interface ISection {
	
	public ArrayList<Section> getSections(String hotelId);
	
	public Section getSectionById(String hotelId, int id);
	
	public Section getSectionByName(String hotelId, String name);
}
