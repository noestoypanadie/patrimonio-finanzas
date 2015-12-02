package es.vdiaz.patrimonio.finanzas.medias;

import java.util.HashMap;
import java.util.Map;

public class Dividendos {

	private Map<String, Float> importeDividendos = new HashMap<String, Float>();

	private static Dividendos dividendos;

	private Dividendos() {
		importeDividendos.put("ABE.MC-ABERTIS", 0.5F);
	}

	public static Dividendos getInstance() {
		if (dividendos == null) {
			dividendos = new Dividendos();
		}
		return dividendos;
	}

	public Float getDividendo(String nombreAccion) {
		Float divs = importeDividendos.get(nombreAccion);
		if (divs == null) {
			divs = 0F;
		}

		return divs;
	}
}
