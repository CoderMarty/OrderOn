package com.orderon.commons;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import com.orderon.dao.AccessManager;

public class ShortForm{
    public static void main(String args[]) throws ParseException {

		String alltitle = "";
		
		String[] titles = alltitle.split(",");
		
		for (String t : titles) {
			//generateShortForm(t);
			//System.out.println(AccessManager.toTitleCase(t));
		}
		String x = "123456789";
		
		System.out.print(x.substring(0, x.length()-1));
	}
    
    private static void generateShortForm(String title) {

		String[] sf = title.split(" ");
		StringBuilder out = new StringBuilder();
	
		for (int i = 0; i < sf.length; i++) {
			if (sf[i].length() > 0)
				out.append(sf[i].substring(0, 1).toUpperCase());
		}
		System.out.println(out.toString());
		
    }
    
	private static void camelise(String title) {

		String[] sf = title.split(" ");
		StringBuilder out = new StringBuilder();
	
		for (int i = 0; i < sf.length; i++) {
			if(sf[i].length()<2)
				continue;
			if (sf[i].length() > 0)
				out.append(sf[i].substring(0, 1).toUpperCase());
			out.append(sf[i].substring(1, sf[i].length()).toLowerCase());
			out.append(" ");
		}
		System.out.println(out.toString());
	}
	
}