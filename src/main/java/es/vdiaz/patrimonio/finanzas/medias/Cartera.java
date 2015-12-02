package es.vdiaz.patrimonio.finanzas.medias;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Cartera {

	private Float cash = 0F;
	private Float aportado = 0F;

	private Map<String, Accion> acciones;

	private static Cartera cartera;

	private Cartera() {
		reset();
	}

	public void reset() {
		cash = Constantes.CASH_INICIAL;
		aportado = Constantes.CASH_INICIAL;

		acciones = new HashMap<String, Accion>();
	}

	public static Cartera getInstance() {
		if (cartera == null) {
			cartera = new Cartera();
		}
		return cartera;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("Aportado " + String.format(Locale.getDefault(), "%.2f", aportado)).append("\n");
		sb.append("Cash: " + cash).append("\n");
		float total = cash;
		for (Accion accion : acciones.values()) {
			sb.append(accion);
			total += accion.getValorActual();
		}
		sb.append("Total: " + total + "( " + String.format(Locale.getDefault(), "%.2f", (total / aportado) * 100F) + "% )").append("\n");

		return sb.toString();
	}

	public void cobraDividendos() {
		if (Constantes.SE_COBRAN_DIVIDENDOS) {
			for (Accion accion : acciones.values()) {
				Float dividendos = accion.getDividendoMedioAnual();
				dividendos = dividendos / 2; // Se cobran en dos veces
				dividendos = dividendos * (1 - Constantes.IMPUESTOS_DIVIDENDOS); // Le quitamos los impuestos
				dividendos = dividendos * accion.getTotalAcciones(); // Los dividendos son por accion
				accion.addDividendosCobrados(dividendos);
				cash += dividendos;
			}
		}
	}

	public void venderAcciones(String nombreAccion, Integer numeroAcciones, Float precioActual) {
		if (numeroAcciones == null || numeroAcciones == 0) {
			return;
		}
		Accion accion = acciones.get(nombreAccion);
		if (accion == null) {
			accion = new Accion(nombreAccion);
			acciones.put(nombreAccion, accion);
		}
		if (numeroAcciones > accion.getTotalAcciones()) {
			numeroAcciones = accion.getTotalAcciones();
		}

		accion.vendeAcciones(numeroAcciones, precioActual);
		cash += precioActual * numeroAcciones * (1 - Constantes.IMPUESTOS_PLUSVALIAS);
		cash -= Constantes.COMISION_VENTA_ACCIONES_NACIONALES;
	}

	public void comprarAcciones(String nombreAccion, Float importeAGastar, Float precioActual) {
		Accion accion = acciones.get(nombreAccion);
		if (accion == null) {
			accion = new Accion(nombreAccion);
			acciones.put(nombreAccion, accion);
		}
		if (importeAGastar > cash) {
			System.out.println("WARN: No tenemos liquidez suficiente, reducimos la posicion");
			importeAGastar = cash;
		}
		int numeroAcciones = (int) Math.floor(importeAGastar / precioActual);
		accion.addAcciones(numeroAcciones, precioActual);
		accion.setPrecioActual(precioActual);
		cash -= precioActual * numeroAcciones;
		cash -= Constantes.COMISION_COMPRA_ACCIONES_NACIONALES;
	}

	public Accion getAccion(String nombreAcccion) {
		return acciones.get(nombreAcccion);
	}

	int contadorMesesParaDividendos = 0;

	public void aporteMensual(String fecha, Float cantidad) {
		if (cantidad == null) {
			cantidad = Constantes.APORTE_MENSUAL;
		}
		addCash(fecha, calculaInteresesDeposito());
		addCash(fecha, cantidad);
		aportado += cantidad;
		contadorMesesParaDividendos++;
		if (contadorMesesParaDividendos == Constantes.MESES_PARA_COBRAR_DIVIDENDOS) {
			contadorMesesParaDividendos = 0;
			cobraDividendos();
		}
	}

	private Float calculaInteresesDeposito() {
		return cash * Constantes.INTERES_TAE_DEPOSITO / 1200;
	}

	public Float getCash() {
		return cash;
	}

	public void addCash(String fecha, Float cash) {
		this.cash += cash;
	}

}
