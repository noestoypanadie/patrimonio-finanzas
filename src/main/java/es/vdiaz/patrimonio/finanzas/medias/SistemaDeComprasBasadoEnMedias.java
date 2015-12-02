package es.vdiaz.patrimonio.finanzas.medias;

import java.util.List;

public class SistemaDeComprasBasadoEnMedias {

	private static List<CotizacionConMedias> cotizacionesMensuales;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ValorConCotizacionesConMedias valorConCotizacionesConMedias = new ValorConCotizacionesConMedias("ABE.MC-ABERTIS");
		cotizacionesMensuales = valorConCotizacionesConMedias.getCotizacionesPrimerasDeMesConMediasDeAntiguasANuevas();

		estrategiaNoComprarNada();

		estrategiaComprarCantidadFijaCadaMes();

		estrategiaComprarPorcentajeFijoCadaMes();

		estrategiaComprarPorcentajeCadaMesSegunDiferenciaConMedia();

		estrategiaComprarPorcentajeCadaMesSegunDiferenciaConMediaYVenta();

		estrategiaCompraPeriodicaPeroMasCantidadSiEstamosDebajoDeLaMedia();
	}

	/*-
	 * Aportacion: mensual fija 
	 * Compra: Nunca 
	 * Venta: Nunca
	 * 
	 * @return
	 */
	public static Cartera estrategiaNoComprarNada() {
		System.out.println("Estrategia no comprar nada. ");

		Cartera.getInstance().reset();

		for (CotizacionConMedias cotizacion : cotizacionesMensuales) {
			Cartera.getInstance().aporteMensual(cotizacion.getCotizacion().getFechaS(), null);
		}

		System.out.println(Cartera.getInstance());
		return Cartera.getInstance();
	}

	/*-
	 * Aportacion: mensual fija 
	 * Compra: mensual por la misma cantidad que se ha aportado
	 * Venta: Nunca
	 * 
	 * @return
	 */
	public static Cartera estrategiaComprarCantidadFijaCadaMes() {
		System.out.println("Estrategia comprar cantidad fija cada mes. ");

		Cartera.getInstance().reset();

		for (CotizacionConMedias cotizacion : cotizacionesMensuales) {
			Cartera.getInstance().aporteMensual(cotizacion.getCotizacion().getFechaS(), null);
			Cartera.getInstance().comprarAcciones("ABE.MC-ABERTIS", Constantes.APORTE_MENSUAL, cotizacion.getCotizacion().getCierreAjustado());
		}

		System.out.println(Cartera.getInstance());
		return Cartera.getInstance();
	}

	/*-
	 * Aportacion: mensual fija 
	 * Compra: mensual por un porcentaje fijo del cash disponible
	 * Venta: Nunca
	 * 
	 * @return
	 */
	public static Cartera estrategiaComprarPorcentajeFijoCadaMes() {
		System.out.println("Estrategia comprar porcentaje fijo cada mes. ");

		Cartera.getInstance().reset();

		for (CotizacionConMedias cotizacion : cotizacionesMensuales) {
			Cartera.getInstance().aporteMensual(cotizacion.getCotizacion().getFechaS(), null);
			Cartera.getInstance().comprarAcciones("ABE.MC-ABERTIS", Cartera.getInstance().getCash() * 0.10F, cotizacion.getCotizacion().getCierreAjustado());
		}

		System.out.println(Cartera.getInstance());
		return Cartera.getInstance();
	}

	/*-
	 * Aportacion: mensual fija 
	 * Compra: mensual por un porcentaje del cash disponible igual a la diferencia porcentual del precio con la media 260 si es negativa (0 si es positiva)
	 * Venta: Nunca
	 * 
	 * @return
	 */
	public static Cartera estrategiaComprarPorcentajeCadaMesSegunDiferenciaConMedia() {
		System.out.println("Estrategia comprar cada mes segun diferencia con media. ");

		Cartera.getInstance().reset();

		for (CotizacionConMedias cotizacion : cotizacionesMensuales) {
			Cartera.getInstance().aporteMensual(cotizacion.getCotizacion().getFechaS(), null);
			Float diferencia = cotizacion.getDiferenciaDeCotizacionConMedia(260);
			if (diferencia != null && diferencia < 0) {
				float importe = -Cartera.getInstance().getCash() * diferencia;
				Cartera.getInstance().comprarAcciones("ABE.MC-ABERTIS", importe, cotizacion.getCotizacion().getCierreAjustado());
				Cartera.getInstance().getAccion("ABE.MC-ABERTIS").setPrecioActual(cotizacion.getCotizacion().getCierreAjustado());
			}
		}

		System.out.println(Cartera.getInstance());
		return Cartera.getInstance();
	}

	/*-
	 * Aportacion: mensual fija 
	 * Compra: mensual por un porcentaje del cash disponible igual a la diferencia porcentual del precio con la media 260 si es negativa (0 si es positiva)
	 * Venta: Nunca
	 * 
	 * @return
	 */
	public static Cartera estrategiaCompraPeriodicaPeroMasCantidadSiEstamosDebajoDeLaMedia() {
		System.out.println("Estrategia comprar cada mes pero más cantidad cuando estamos por debajo de la media. ");

		Cartera.getInstance().reset();

		for (CotizacionConMedias cotizacion : cotizacionesMensuales) {
			Cartera.getInstance().aporteMensual(cotizacion.getCotizacion().getFechaS(), null);
			Float diferencia = cotizacion.getDiferenciaDeCotizacionConMedia(260);
			if (diferencia != null && diferencia < 0) {
				float importe = -Cartera.getInstance().getCash() * diferencia;
				Cartera.getInstance().comprarAcciones("ABE.MC-ABERTIS", importe, cotizacion.getCotizacion().getCierreAjustado());
			} else {
				Cartera.getInstance().comprarAcciones("ABE.MC-ABERTIS", Constantes.APORTE_MENSUAL, cotizacion.getCotizacion().getCierreAjustado());
			}
		}

		System.out.println(Cartera.getInstance());
		return Cartera.getInstance();
	}

	/*-
	 * Aportacion: mensual fija 
	 * Compra: mensual por un porcentaje del cash disponible igual a la diferencia porcentual del precio con la media 260 si es negativa (0 si es positiva)
	 * Venta:  y venta de un porcentaje = a la diferencia del precio medio con la media de 260
	 *  
	 * @return
	 */
	public static Cartera estrategiaComprarPorcentajeCadaMesSegunDiferenciaConMediaYVenta() {
		System.out.println("Estrategia comprar cada mes segun diferencia con media y venta de un porcentaje = a la diferencia del precio medio con la media de 260. ");

		Cartera.getInstance().reset();

		for (CotizacionConMedias cotizacion : cotizacionesMensuales) {
			Cartera.getInstance().aporteMensual(cotizacion.getCotizacion().getFechaS(), null);
			Float diferenciaPrecioYMedia = cotizacion.getDiferenciaDeCotizacionConMedia(260);
			if (diferenciaPrecioYMedia != null && diferenciaPrecioYMedia < 0) {
				float importe = -Cartera.getInstance().getCash() * diferenciaPrecioYMedia;
				Cartera.getInstance().comprarAcciones("ABE.MC-ABERTIS", importe, cotizacion.getCotizacion().getCierreAjustado());
				Cartera.getInstance().getAccion("ABE.MC-ABERTIS").setPrecioActual(cotizacion.getCotizacion().getCierreAjustado());
			} else {
				Accion accion = Cartera.getInstance().getAccion("ABE.MC-ABERTIS");
				if (accion != null) {
					accion.setPrecioActual(cotizacion.getCotizacion().getCierreAjustado());
					Float porcentajeGanancias = accion.getPorcentajeGananciasPorAccion();
					if (porcentajeGanancias > 1.5) {
						int numeroAcciones = (int) Math.floor((porcentajeGanancias - 1) * accion.getTotalAcciones());
						Cartera.getInstance().venderAcciones("ABE.MC-ABERTIS", numeroAcciones, accion.getPrecioActual());
					}
				}
			}
		}

		System.out.println(Cartera.getInstance());
		return Cartera.getInstance();
	}
}
