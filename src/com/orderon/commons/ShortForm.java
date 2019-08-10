package com.orderon.commons;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import com.orderon.dao.AccessManager;

public class ShortForm{
    public static void main(String args[]) throws ParseException {

		String alltitle = "Healthy Broccoli Salad," +
				"ROASTED TOMATO SOUP WITH FRESH MINT," +
				"MUSHROOM CAPPUCCINO WITH TRUFFLE OIL," +
				"VEG MANCHOW SOUP," +
				"NONVEG MANCHOW SOUP," +
				"HEALTHY BROCCOLI SOUP," +
				"EGG KETO SALAD," +
				"VEG KETO SALAD," +
				"TRADITIONAL VEG CEASAR SALAD," +
				"TRADITIONAL CHICKEN CEASAR SALAD," +
				"TERIYAKI CHICKEN WITH SESAME DRESSING," +
				"FALAFELS IN BLANKET," +
				"VEG FAJITA WRAP," +
				"CHICKEN FAJITA WRAP," +
				"THAI SATAY WRAP," +
				"WRAPPED VEG CEASARÊ," +
				"WRAPPED CHICKENÊ CEASAR," +
				"HOT N SWEET CHICKEN WRAP," +
				"BOMBAY," +
				"COLE SLAW SANDWICH," +
				"COTTAGE CHEESE PESTO," +
				"CHICKEN PESTO," +
				"PHILLY CHICKEN," +
				"PULLED CHICKEN BBQ," +
				"THE PROTEINA BURGER," +
				"SHROOM SALSA BURGER," +
				"CHEESE MASH UP BURGER," +
				"REGULAR VEG BURGER," +
				"REGULAR NON VEG BURGER," +
				"BBQ CHICKEN BURGER," +
				"LOUISIANA BURGER," +
				"AMOR MEXICANO BURGER," +
				"CHICKEN PARM BURGER," +
				"JALAPENO POPPERS," +
				"COTTAGE CHEESE SKEWERS," +
				"VEG BRUSCHETTAS," +
				"NONVEG BRUSCHETTAS," +
				"VEG NACHOS," +
				"NONVEG NACHOS," +
				"VEG CAJUN CHILLY," +
				"NONVEG CAJUN CHIILLY," +
				"PANEER KUNG PAO," +
				"CHICKEN KUNGPAO," +
				"PRIKKAI," +
				"CHICKEN PERI PERI WINGSÊ," +
				"CHICKEN SRIRACHA WINGSÊ," +
				"CHICKEN TERIYAKI WINGSÊ," +
				"CHICKEN STICKY WINGSÊ," +
				"CHICKEN BUFFALO WINGSÊ," +
				"JERK CHICKEN LEG," +
				"CHICKEN N CHIPS," +
				"CHICKEN POPPERS," +
				"FALAFEL HUMMUS CONE," +
				"VEG KOFTE CURRY CONE," +
				"NONVEG KOFTE CURRY CONE," +
				"SCHEZWAN PANEER CONE," +
				"SCHEZWAN CHICKEN CONE," +
				"VEG MAGGELICIOUS CONE," +
				"NONVEG MAGGELICIOUS CONE," +
				"ÊMAKHANI PANEER CONE," +
				"ÊMAKHANI CHICKEN CONE," +
				"MARGHERITA NAPOLETAN PIZZA," +
				"CAPRISSIOSO ITALIANO PIZZA," +
				"AL FUNGHI PIZZA," +
				"ALL VEGGIE PIZZA," +
				"ÊTANDOORI PANEER PIZZA," +
				"ÊTANDOORI CHICKEN PIZZA," +
				"CAJUN CHICKEN PIZZA," +
				"ÊALFREDO PASTA," +
				"AGLIO OLIO," +
				"POMODORO NAPOLITANA PASTA," +
				"AL FUNGHI PASTA," +
				"PESTO CREAM PASTA," +
				"PESTO PASTA," +
				"PINK SAUCE PASTA," +
				"HERB RICE WITH CAJUN CHILLI VEG," +
				"HERB RICE WITH CAJUN CHILLI CHICKEN," +
				"VEG MAKHANI AND JEERA RICEÊ," +
				"CHICKEN MAKHANI AND JEERA RICEÊ," +
				"JAMBALAYA WITH GRILLED CHICKENÊ," +
				"THAI BASIL RICE WITH ORIENTAL PANEER," +
				"THAI BASIL RICE WITH ORIENTAL CHICKEN," +
				"PANEER GARLIC PEPPER FRY," +
				"CHICKEN GARLIC PEPPER FRY," +
				"BIRYANI VEG," +
				"BIRYANI CHICKEN," +
				"PERI PERI CHICKEN WITH BURNT GARLIC RICEÊ," +
				"RICE AND BEANS WITH JERK CHICKEN," +
				"MUSHROOM RISSOTTO," +
				"VEGGIE RISSOTTO," +
				"HERB CHICKEN RISSOTTO," +
				"CHOLE CHAWAL," +
				"RAJMA CHAWAL," +
				"VEG ORIENTAL SIZZLER," +
				"NONVEG ORIENTAL SIZZLER," +
				"VEG EURO SIZZLER," +
				"NONVEG EURO SIZZLER," +
				"VEG INDIE SIZZLER," +
				"NONVEG INDIE SIZZLER," +
				"TRUFFLED FRIES," +
				"FRENCH FRIES," +
				"PERI PERI FRIES," +
				"CHEESE FRIES," +
				"GARLIC BREAD," +
				"CHEESE GARLIC BREAD," +
				"COLE SLAW," +
				"POTATO WEDGESÊ," +
				"PERI PERI POTATO WEDGES," +
				"CHEESY POTATO WEDGES," +
				"POTATO AGLIO E OLIO," +
				"COLD COFFEE," +
				"CHOCOLATE SHAKE," +
				"CHOCO MINT," +
				"OREO CHOCO," +
				"FERRERO ROCHER," +
				"KITKAT," +
				"KITKAT OREO," +
				"PEANUT BUTTER BANANA," +
				"CHOCO BANANA," +
				"PEPPERY GUAVAÊ," +
				"MINT CUCUMBER," +
				"WATERMELON THYME COOLERÊ," +
				"WATERMELON SANGRIA," +
				"PEACH AND ORANGE," +
				"REVIVAL," +
				"LITCHIANO," +
				"MOJITO," +
				"LIT," +
				"BLUE LEMONÊ," +
				"BOTTLED WATER," +
				"SOFT DRINKS (250 ML)," +
				"CAPPUCCINO," +
				"ESPRESSO," +
				"LATTEÊ," +
				"MASALA TEA," +
				"BANANA PEANUT BUTTER JELLY WRAP," +
				"CHOCOLATE CREPESÊ," +
				"MIXED FRUITS CREPESÊ," +
				"STRAWBERRY WAFFLES," +
				"CHOCOLATE WAFFLES," +
				"CHOCOLATE BANANA WAFFLES," +
				"GULAB JAMUN," +
				"CHOCOLATE BROWNIEÊ," +
				"BROWNIE WITH ICE CREAM," +
				"ICE ON FIREÊ," +
				"ICE CREAMS," +
				"PAN PIZZA (6Ó THIN CRUST)+PEPSI," +
				"REGULAR BURGER+PEPSI," +
				"PASTA TOMATO/ALFREDO+PEPSIÊ," +
				"CHICKEN WINGS+PEPSI," +
				"WRAPS+PEPSI," +
				"VEG MEAL 1," +
				"ÊVEG MEAL 2," +
				"NONVEG MEAL 1," +
				"NONVEG MEAL 2," +
				"VEG MEAL 3," +
				"VEG MEAL 4," +
				"NONVEG MEAL 3," +
				"ADD CHICKEN SAUSAGES," +
				"ADD GRILLED CHICKENÊ," +
				"ADD VEGGIES," +
				"ADD CHEESE";
		
		String[] titles = alltitle.split(",");
		
		for (String t : titles) {
			//generateShortForm(t);
			//System.out.println(AccessManager.toTitleCase(t));
		}
		String x = "123456789";
		
		System.out.print(x.substring(x.length()-4, x.length()));
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