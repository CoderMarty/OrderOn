package com.orderon.dao;

import java.util.ArrayList;

import com.orderon.interfaces.ISection;

public class SectionManager extends AccessManager implements ISection{

	public SectionManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ArrayList<Section> getSections(String hotelId){
		String sql = "SELECT * FROM Sections WHERE hotelId = '"+hotelId+"';";
		
		return db.getRecords(sql, Section.class, hotelId);
	}

	@Override
	public Section getSectionById(String hotelId, int id){
		String sql = "SELECT * FROM Sections WHERE hotelId = '"+hotelId+"' AND id = "+id+";";
		
		return db.getOneRecord(sql, Section.class, hotelId);
	}

	@Override
	public Section getSectionByName(String hotelId, String name){
		String sql = "SELECT * FROM Sections WHERE hotelId = '"+hotelId+"' AND name = '"+name+"';";
		
		return db.getOneRecord(sql, Section.class, hotelId);
	}
}
