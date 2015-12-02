package es.vdiaz.patrimionio.finanzas.yahoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class YahooFinanceHttp {

	public static final String SEPARADOR = "\t";

	List<Cotizacion> listaCotizaciones = new ArrayList<YahooFinanceHttp.Cotizacion>();

	private URL url = null;
	private URLConnection urlConn = null;
	private InputStreamReader inStream = null;

	List<String> lineasProcesadas = new ArrayList<String>();

	public YahooFinanceHttp(String urlStr) {
		try {
			// Open Connection the Yahoo Finance URL
			this.url = new URL(urlStr);
			this.urlConn = url.openConnection();

			// Start Reading
			this.urlConn = this.url.openConnection();
			this.inStream = new InputStreamReader(this.urlConn.getInputStream());
			BufferedReader buff = new BufferedReader(this.inStream);
			String stringLine;

			stringLine = buff.readLine(); // This is the header.
			String lineaProcesada = stringLine.replaceAll("\\,", SEPARADOR);
			lineasProcesadas.add(lineaProcesada);

			Cotizacion cotizacion;
			while ((stringLine = buff.readLine()) != null) {

				String[] dohlcav = stringLine.split("\\,");

				cotizacion = new Cotizacion();
				cotizacion.dateS = dohlcav[0];
				cotizacion.open = Double.parseDouble(dohlcav[1]);
				cotizacion.high = Double.parseDouble(dohlcav[2]);
				cotizacion.low = Double.parseDouble(dohlcav[3]);
				cotizacion.close = Double.parseDouble(dohlcav[4]);
				cotizacion.volume = Long.parseLong(dohlcav[5]);
				cotizacion.adjClose = Double.parseDouble(dohlcav[6]);

				lineaProcesada = stringLine.replaceAll("\\,", SEPARADOR).replaceAll("\\.", ",");

				lineaProcesada += SEPARADOR + cotizacion.dateS.substring(0, 4);
				// lineaProcesada += SEPARADOR + cotizacion.dateS.substring(5, 7);
				// lineaProcesada += SEPARADOR + cotizacion.dateS.substring(8, 10);

				cotizacion.año = new Integer(cotizacion.dateS.substring(0, 4));
				cotizacion.mes = new Integer(cotizacion.dateS.substring(5, 7));
				cotizacion.dia = new Integer(cotizacion.dateS.substring(8, 10));

				cotizacion.lineaProcesada = lineaProcesada;

				listaCotizaciones.add(cotizacion);

				lineasProcesadas.add(lineaProcesada);
			}

			Collections.reverse(listaCotizaciones);

		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public class Cotizacion {
		String dateS;
		Date date;
		double open;
		double high;
		double low;
		double close;
		long volume;
		double adjClose;

		Integer dia;
		Integer mes;
		Integer año;

		String lineaProcesada;
	}

	public List<String> getLineasProcesadas() {
		return lineasProcesadas;
	}

	public List<Cotizacion> getListaCotizaciones() {
		return listaCotizaciones;
	}

	public void setListaCotizaciones(List<Cotizacion> listaCotizaciones) {
		this.listaCotizaciones = listaCotizaciones;
	}

}