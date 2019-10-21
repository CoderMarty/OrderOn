package com.orderon.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum MeasurableUnit {
	
	GRAM(new BigDecimal("1.0"), "GRAM"), KG(new BigDecimal("0.001"), "GRAM"), MG(new BigDecimal("1000.0"), "GRAM"), 
	LITRE(new BigDecimal("0.001"), "ML"), ML(new BigDecimal("1.0"), "ML"), PIECE(new BigDecimal("1.0"), "PIECE"), 
	CUP(new BigDecimal("250.0"), "ML"), TEASPOONGM(new BigDecimal("5.0"), "GRAM"), TABLESPOONGM(new BigDecimal("15.0"), "GRAM"), 
	TEASPOONML(new BigDecimal("5.0"), "ML"), TABLESPOONML(new BigDecimal("15.0"), "ML"), 
	PINCH(new BigDecimal("1.0"), "GRAM"), NONE(new BigDecimal("1.0"), "NONE");
	
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
	
	public BigDecimal convertToDisplayableUnit(BigDecimal value){
		return value.multiply(this.conversion).setScale(6, RoundingMode.HALF_UP);
	}
	
	public BigDecimal convertToBaseUnit(BigDecimal value){
		return value.divide(this.conversion).setScale(6, RoundingMode.HALF_UP);
	}
	
	public BigDecimal convertToBaseRate(BigDecimal rate){
		return rate.multiply(this.conversion).setScale(6, RoundingMode.HALF_UP);
	}
	
	public BigDecimal convertToDisplayableRate(BigDecimal rate){
		return rate.divide(this.conversion).setScale(6, RoundingMode.HALF_UP);
	}
	
	private MeasurableUnit(BigDecimal conversion, String association){
		this.conversion = conversion;
		this.association = association;
	}
}
