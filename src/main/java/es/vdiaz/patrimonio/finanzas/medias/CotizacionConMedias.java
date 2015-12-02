package es.vdiaz.patrimonio.finanzas.medias;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CotizacionConMedias {

	public static final Integer NUMERO_MAXIMO_DIAS_PARA_MEDIA = 1301;

	public static final String CABECERA = "Fecha;Primera cotizacion Mes;Apertura;Maximo;Minimo;Cierre;Volumen;Cierre Ajustado;Media 260;% a Media 260;Media 520;% a Media 520;Media 780;% a Media 780;Media 1040;% a Media 1040;Media 1300;% a Media 1300\n";

	private Cotizacion cotizacion;

	// Guarda la media del periodo que contenga la clave. Calculada para este día
	private Map<Integer, Float> medias = new HashMap<Integer, Float>();
	// Guarda la distancia (en tanto por 1) a la media del periodo que contenga la clave. Calculada para este día
	private Map<Integer, Float> distanciaALaMedia = new HashMap<Integer, Float>();

	private Integer[] diasAMostrar = new Integer[] {260, 520, 780, 1040, 1300 };

	public CotizacionConMedias(Cotizacion cotizacion) {
		this.cotizacion = cotizacion;
	}

	public Float getDiferenciaDeCotizacionConMedia(int periodoMedia) {
		return distanciaALaMedia.get(periodoMedia);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(cotizacion);

		for (int i = 0; i < diasAMostrar.length; i++) {
			sb.append(";");
			if (medias.get(diasAMostrar[i]) != null) {
				sb.append(String.format(Locale.getDefault(), "%.3f", medias.get(diasAMostrar[i])));
				sb.append(";");
				sb.append(String.format(Locale.getDefault(), "%.4f", distanciaALaMedia.get(diasAMostrar[i])));
			}
		}
		return sb.toString();
	}

	public String printAllMedias() {
		StringBuffer sb = new StringBuffer();

		for (int i = 1; i < NUMERO_MAXIMO_DIAS_PARA_MEDIA; i++) {
			sb.append(i);
			sb.append(";");
			sb.append(String.format(Locale.getDefault(), "%.3f", medias.get(i)));
			sb.append(";");
			sb.append(String.format(Locale.getDefault(), "%.4f", distanciaALaMedia.get(i)));
			sb.append("\n");
		}

		return sb.toString();
	}

	public void addMedia(Integer periodo, Float valorMedia) {
		medias.put(periodo, valorMedia);
		if (valorMedia != null) {
			distanciaALaMedia.put(periodo, (cotizacion.getCierreAjustado() / valorMedia) - 1);
		}
	}

	public Cotizacion getCotizacion() {
		return cotizacion;
	}

	public void setCotizacion(Cotizacion cotizacion) {
		this.cotizacion = cotizacion;
	}

	public Map<Integer, Float> getMedias() {
		return medias;
	}
}
