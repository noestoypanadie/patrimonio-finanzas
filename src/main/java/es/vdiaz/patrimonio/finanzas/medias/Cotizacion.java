package es.vdiaz.patrimonio.finanzas.medias;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Cotizacion {

	private SimpleDateFormat formatoDeFecha = new SimpleDateFormat("yyyy-MM-dd");

	private String fechaS;
	private Date fecha;
	private float apertura;
	private float maximo;
	private float minimo;
	private float cierre;
	private int volumen;
	private float cierreAjustado;

	private boolean primeraCotizacionDelMes = false;

	public Cotizacion(String cotizacionYahoo) throws Exception {
		String[] cotizacion = cotizacionYahoo.split("\t");
		fechaS = cotizacion[0];
		apertura = Float.valueOf(cotizacion[1].replaceAll(",", "."));
		maximo = Float.valueOf(cotizacion[2].replaceAll(",", "."));
		minimo = Float.valueOf(cotizacion[3].replaceAll(",", "."));
		cierre = Float.valueOf(cotizacion[4].replaceAll(",", "."));
		volumen = Integer.valueOf(cotizacion[5]);
		cierreAjustado = Float.valueOf(cotizacion[6].replaceAll(",", "."));

		fecha = formatoDeFecha.parse(fechaS);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(fechaS);
		sb.append(";");
		sb.append(primeraCotizacionDelMes);
		sb.append(";");
		sb.append(String.format(Locale.getDefault(), "%.3f", apertura));
		sb.append(";");
		sb.append(String.format(Locale.getDefault(), "%.3f", maximo));
		sb.append(";");
		sb.append(String.format(Locale.getDefault(), "%.3f", minimo));
		sb.append(";");
		sb.append(String.format(Locale.getDefault(), "%.3f", cierre));
		sb.append(";");
		sb.append(volumen);
		sb.append(";");
		sb.append(String.format(Locale.getDefault(), "%.3f", cierreAjustado));

		return sb.toString();
	}

	public SimpleDateFormat getFormatoDeFecha() {
		return formatoDeFecha;
	}

	public String getFechaS() {
		return fechaS;
	}

	public Date getFecha() {
		return fecha;
	}

	public float getApertura() {
		return apertura;
	}

	public float getMaximo() {
		return maximo;
	}

	public float getMinimo() {
		return minimo;
	}

	public float getCierre() {
		return cierre;
	}

	public int getVolumen() {
		return volumen;
	}

	public float getCierreAjustado() {
		return cierreAjustado;
	}

	public boolean isPrimeraCotizacionDelMes() {
		return primeraCotizacionDelMes;
	}

	public void setPrimeraCotizacionDelMes(boolean primeraCotizacionDelMes) {
		this.primeraCotizacionDelMes = primeraCotizacionDelMes;
	}
}
