package com.orderon;

public enum TaxTypes {
	
	//Never change the values.
	GST(0, 5.0, "GST"), GST2(1, 12.0, "GST"), GST3(2, 18.0, "GST"), GST4(3, 28.0, "GST"),  SC(4, 2.0, "SC"),  SC5(6, 5.0, "SC"),
	VATBAR(5, 12.0, "VATBAR"), INVALID(100, 0, "Invalid");
	
	private int value;
	private double taxPercent;
	private String name;
	
	public int getValue(){
		return this.value;
	}
	
	public double getTaxPercent(){
		return this.taxPercent;
	}
	
	public String getName(){
		return this.name;
	}
	
	private TaxTypes(int value, double taxPercent, String name){
		this.value = value;
		this.taxPercent = taxPercent;
		this.name = name;
	}
	
	public static TaxTypes getType(int value){
		
		if(value == 0)
			return TaxTypes.GST;
		else if(value == 1)
			return TaxTypes.GST2;
		else if(value == 2)
			return TaxTypes.GST2;
		else if(value == 3)
			return TaxTypes.GST3;
		else if(value == 4)
			return TaxTypes.SC;
		else if(value == 5)
			return TaxTypes.VATBAR;
		else if(value == 6)
			return TaxTypes.SC5;
		else
			return TaxTypes.INVALID;
	}
}
