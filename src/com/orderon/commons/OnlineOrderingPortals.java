package com.orderon;

public enum OnlineOrderingPortals {

	NONE(0, ""), COUNTER(100, "Counter Parcel"), ZOMATO(1, "Zomato"), SWIGGY(2, "Swiggy"), 
	FOODPANDA(3, "Food Panda"), UBEREATS(4, "Uber Eats"), FOODILOO(5, "Foodiloo");
	
	private int value;
	private String name;
	
	private OnlineOrderingPortals(int value, String name){
		this.value = value;
		this.name = name;
	}
	
	public int getValue(){
		return this.value;
	}
	public String getName() {
		return this.name;
	}
	
	public static OnlineOrderingPortals getType(int value){
		
		if(value == 100)
			return OnlineOrderingPortals.COUNTER;
		else if(value == 1)
			return OnlineOrderingPortals.ZOMATO;
		else if(value == 2)
			return OnlineOrderingPortals.SWIGGY;
		else if(value == 3)
			return OnlineOrderingPortals.FOODPANDA;
		else if(value == 4)
			return OnlineOrderingPortals.UBEREATS;
		else if(value == 5)
			return OnlineOrderingPortals.FOODILOO;
		else
			return OnlineOrderingPortals.NONE;
	}
			
}


