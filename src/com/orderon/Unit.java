package com.orderon;

public enum Unit {
	
	GRAM(1.0, "GRAM"), KG(0.001, "GRAM"), MG(100.0, "GRAM"), LITRE(0.001, "ML"), ML(1.0, "ML"), 
	PIECE(1.0, "PIECE"), CUP(250.0, "ML"), TEASPOONGM(5.0, "GRAM"), TABLESPOONGM(15.0, "GRAM")
	, TEASPOONML(5.0, "ML"), TABLESPOONML(15.0, "ML"), PINCH(1.0, "GRAM");
	
	private double conversion;
	private double value;
	private String association;

	public String getAssociation() {
		return association;
	}

	public void setAssociation(String association) {
		this.association = association;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getConversion(){
		return this.conversion;
	}
	
	public double convertToStore(){
		return this.value*this.conversion;
	}
	public double convertToDisplay(){
		double x = this.value/this.conversion;
		return x;
	}
	
	private Unit(double conversion, String association){
		this.conversion = conversion;
		this.association = association;
	}
}
