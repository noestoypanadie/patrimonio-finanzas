package es.vdiaz.patrimonio.finanzas.medias;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.vdiaz.patrimonio.finanzas.util.FileUtils;

public class ValorConCotizacionesConMedias {

	public static String RUTA_BASE = "C:\\Users\\vdiaz\\Dropbox\\Patrimonio\\Finanzas\\cotizaciones";
	public static String NOMBRE_VALOR = "^IBEX-IBEX_35";

	private List<Cotizacion> cotizacionDeRecienteAAntiguo = new ArrayList<Cotizacion>();
	private List<CotizacionConMedias> diasConMedias = new ArrayList<CotizacionConMedias>();
	private ArrayList<CotizacionConMedias> primerosDeMesConMedias = new ArrayList<CotizacionConMedias>();

	private String nombreValor;

	public static void main(String[] args) throws Exception {

		if (args.length > 1) {
			// FIXME Cojer la ruta base de una propiedad del sistema
			RUTA_BASE = args[0] + File.separator;
			NOMBRE_VALOR = args[1] + File.separator;
		} else {
			// System.err.println("Necesito un parametro con la ruta base");
			// return;
		}

		System.out.println("Ejecutando SistemaMedias en la ruta: " + RUTA_BASE + " para el valor " + NOMBRE_VALOR);

		ValorConCotizacionesConMedias mediasParaUnValor = new ValorConCotizacionesConMedias(NOMBRE_VALOR);
		mediasParaUnValor.writeToFile();

		// System.out.println(mediasParaUnValor.getDiasConMedias().get(0).printAllMedias());
	}

	@SuppressWarnings("deprecation")
	public ValorConCotizacionesConMedias(String nombreValor) throws Exception {
		this.nombreValor = nombreValor;
		String fileName = nombreValor + ".txt";

		List<String> datos = FileUtils.readFile(RUTA_BASE + File.separator + fileName, false);
		Cotizacion ultimaCotizacionLeida = null;
		// Leemos las cotizaciones del fichero
		for (String dato : datos) {
			Cotizacion cotizacion = new Cotizacion(dato);
			cotizacionDeRecienteAAntiguo.add(cotizacion);
			if (ultimaCotizacionLeida != null && ultimaCotizacionLeida.getFecha().getMonth() != cotizacion.getFecha().getMonth()) {
				ultimaCotizacionLeida.setPrimeraCotizacionDelMes(true);
			}
			ultimaCotizacionLeida = cotizacion;

		}
		// Calculamos las medias para cada cotizacion
		for (int i = 0; i < cotizacionDeRecienteAAntiguo.size(); i++) {
			CotizacionConMedias mediasDeCotizacion = new CotizacionConMedias(cotizacionDeRecienteAAntiguo.get(i));
			diasConMedias.add(mediasDeCotizacion);
			if (mediasDeCotizacion.getCotizacion().isPrimeraCotizacionDelMes()) {
				primerosDeMesConMedias.add(mediasDeCotizacion);
			}
			for (int j = 1; j < CotizacionConMedias.NUMERO_MAXIMO_DIAS_PARA_MEDIA; j++) {
				if (j != 260) {
					continue;
				}
				mediasDeCotizacion.addMedia(j, calculaMedia(cotizacionDeRecienteAAntiguo, i, j));
			}
		}
	}

	public List<CotizacionConMedias> getCotizacionesPrimerasDeMesConMediasDeAntiguasANuevas() {
		List<CotizacionConMedias> clone = (List<CotizacionConMedias>) primerosDeMesConMedias.clone();
		Collections.reverse(clone);
		return clone;
	}

	public void writeToFile() throws Exception {
		File fout = new File(RUTA_BASE + File.separator + nombreValor + ".csv");
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(CotizacionConMedias.CABECERA);
		for (CotizacionConMedias diaConMedias : diasConMedias) {
			bw.write(diaConMedias.toString());
			bw.newLine();
		}
		bw.close();
	}

	private static Float calculaMedia(List<Cotizacion> cotizacionDeRecienteAAntiguo, Integer inicio, Integer numeroDias) {
		Float res = 0F;
		if ((inicio + numeroDias) > cotizacionDeRecienteAAntiguo.size()) {
			return null;
		}
		for (int i = inicio; i < inicio + numeroDias; i++) {
			res = res + cotizacionDeRecienteAAntiguo.get(i).getCierreAjustado();
		}
		res = res / numeroDias;
		return res;
	}

	public List<CotizacionConMedias> getDiasConMedias() {
		return diasConMedias;
	}
}
