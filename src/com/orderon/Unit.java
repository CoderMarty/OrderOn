package com.orderon;

import java.math.BigDecimal;

public enum Unit {
	
	GRAM(new BigDecimal("1"), "GRAM"), KG(new BigDecimal("0.001"), "GRAM"), MG(new BigDecimal("100"), "GRAM"), LITRE(new BigDecimal("0.001"), "ML"), 
	ML(new BigDecimal("1"), "ML"), PIECE(new BigDecimal("1"), "PIECE"), CUP(new BigDecimal("250"), "ML"), TEASPOONGM(new BigDecimal("5"), "GRAM"), 
	TABLESPOONGM(new BigDecimal("15"), "GRAM"), TEASPOONML(new BigDecimal("5"), "ML"), TABLESPOONML(new BigDecimal("15"), "ML"), PINCH(new BigDecimal("1"), "GRAM");
	
	private BigDecimal conversion;
	private BigDecimal value;
	private String association;

	public String getAssociation() {
		return association;
	}

	public void setAssociation(String association) {
		this.association = association;
	}

	public BigDecimal getValue() {
		return this.value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getConversion(){
		return this.conversion;
	}
	
	public BigDecimal convertToStore(){
		return this.value.multiply(this.conversion);
	}
	public BigDecimal convertToDisplay(){
		return this.value.divide(this.conversion);
	}
	
	private Unit(BigDecimal conversion, String association){
		this.conversion = conversion;
		this.association = association;
	}
}
