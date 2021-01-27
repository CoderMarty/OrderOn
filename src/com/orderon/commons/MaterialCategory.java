package com.orderon.commons;

public enum MaterialCategory {

	RECIPE_MANAGED("RECIPE MANAGED"), RECIPE_NOT_MANAGED("RECIPE NOT MANAGED"), OTHERS("OTHERS");
	
	private String name;
	
	public String getName() {
		return name;
	}

	private MaterialCategory(String name){
		this.name = name;
	}
	
	public static MaterialCategory getType(String name){
		
		if(name.equals("RECIPE MANAGED"))
			return MaterialCategory.RECIPE_MANAGED;
		else if(name.equals("RECIPE NOT MANAGED"))
			return MaterialCategory.RECIPE_NOT_MANAGED;
		else 
			return MaterialCategory.OTHERS;
	}	
}
