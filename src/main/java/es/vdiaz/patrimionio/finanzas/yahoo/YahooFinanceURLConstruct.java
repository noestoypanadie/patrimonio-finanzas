package es.vdiaz.patrimionio.finanzas.yahoo;

import java.util.Calendar;

public class YahooFinanceURLConstruct {
	// Data
	private String startDateDay, startDateMonth, startDateYear;
	private String endDateDay, endDateMonth, endDateYear;
	private String tickerSymbol;

	// YahooFinanceHttp yahoo = new YahooFinanceHttp(constructURL());

	// http://ichart.finance.yahoo.com/table.csv?s=AAPL&d=6&e=3&f=2013&g=d&a=8&b=7&c=1984&ignore=.csv

	// Create a constructor to set the data
	public YahooFinanceURLConstruct(Calendar startDate, Calendar endDate, String tickerSymbol) {
		// startingDate
		this.startDateDay = Integer.toString(startDate.get(Calendar.DATE));
		this.startDateMonth = Integer.toString(startDate.get(Calendar.MONTH));
		this.startDateYear = Integer.toString(startDate.get(Calendar.YEAR));

		// Set the end date values
		this.endDateDay = Integer.toString(endDate.get(Calendar.DATE));
		this.endDateMonth = Integer.toString(endDate.get(Calendar.MONTH));
		this.endDateYear = Integer.toString(endDate.get(Calendar.YEAR));

		// tickerSymbol
		this.tickerSymbol = tickerSymbol;
	}

	// Create a method to construct the URL given the startDate, endDate,
	// tickerSymbol as input
	public String constructURL() {

		String baseURL = "http://ichart.finance.yahoo.com/table.csv?s=";
		// YahooFinance URL Reconstructed
		String urlStr = (baseURL + tickerSymbol + "&d=" + endDateMonth + "&e=" + endDateDay + "&f=" + endDateYear + "&g=d" + "&a=" + startDateMonth + "&b=" + startDateDay + "&c=" + startDateYear);
		return urlStr;
	}

	public static String constructTiempoReal(String ticker) {
		String baseURL = "https://es.finance.yahoo.com/q?s=";

		return baseURL + ticker;
	}

}