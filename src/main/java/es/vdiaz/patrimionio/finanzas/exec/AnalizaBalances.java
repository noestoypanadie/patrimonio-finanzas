package es.vdiaz.patrimionio.finanzas.exec;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfContentReaderTool;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

import es.vdiaz.patrimionio.finanzas.pdf.FPSTextExtractionStrategy;

/**
 * 
 * @author vdiaz
 * 
 */
public class AnalizaBalances {

	private static final Log LOG = LogFactory.getLog(AnalizaBalances.class);

	private static final String RUTA_BASE = "C://Users//vdiaz//Dropbox//Patrimonio//Finanzas//analisis-fundamental//Información financiera intermedia//";

	private String PDF_SELECCIONADO = null;

	// private String PDF_SELECCIONADO ="07866323_9_0_1007102248_CV.pdf";

	/**
	 * 
	 */
	public static void main(String[] args) {

		AnalizaBalances pdfTextParser = new AnalizaBalances();
		try {
			pdfTextParser.exec();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String fechaActual;
	private String fechaAnterior;

	public void exec() throws Exception {
		File tmp = new File(RUTA_BASE);
		String[] fileNames = tmp.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith("pdf")) {
					return true;
				} else {
					return false;
				}
			}
		});

		for (String filename : fileNames) {
			List<String> lineasProcesadas = new ArrayList<String>();
			if (PDF_SELECCIONADO != null && !filename.equals(PDF_SELECCIONADO)) {
				continue;
			}
			String procesando = "";
			String proxima = "";
			try {
				List<String> lineas = parseaCV(RUTA_BASE + filename);
				for (String linea : lineas) {
					if (linea.startsWith("6. BALANCE CONSOLIDADO")) {
						procesando = "BALANCE";
					} else if (linea.startsWith("7. CUENTA DE PÉRDIDAS Y GANANCIAS CONSOLIDADA")) {
						procesando = "CUENTA_P_G";
					} else if (linea.startsWith("8. ESTADO DE INGRESOS Y GASTOS")) {
						procesando = "";
					}

					if (proxima != null && proxima.equals("P. ACTUAL")) {
						fechaActual = linea;
						proxima = null;
					}
					if (proxima != null && proxima.equals("P. ANTERIOR")) {
						fechaAnterior = linea;
						proxima = null;
					}
					if ("BALANCE".equals(procesando) && "P. ACTUAL".equals(linea)) {
						proxima = "P. ACTUAL";
					} else if ("BALANCE".equals(procesando) && "P. ANTERIOR".equals(linea)) {
						proxima = "P. ANTERIOR";
					}
					String separador = "";
					if (procesando.equals("BALANCE") || procesando.equals("CUENTA_P_G")) {
						for (int i = 1; i < 10; i++) {
							linea = linea.replaceAll("  ", "");
						}

						linea = linea.replaceAll("1\\. ", "1.").replaceAll("2\\. ", "2.").replaceAll("3\\. ", "3.").replaceAll("4\\. ", "4.").replaceAll("5\\. ", "5.").replaceAll("6\\. ", "6.")
								.replaceAll("7\\. ", "7.").replaceAll("8\\. ", "8.").replaceAll("9\\. ", "9.").replaceAll("0\\. ", "0.");

						String[] textos = linea.split(" ");
						String lineaTabulada = "";
						for (String texto : textos) {
							texto = texto.trim();

							try {
								String textoFormateadoParaDoble = texto.replaceAll("\\.", "").replaceAll(",", "").replaceAll("\\(", "-").replaceAll("\\)", "");
								Double.valueOf(textoFormateadoParaDoble);
								separador = "\t";
							} catch (Exception e) {
								separador = " ";
							}
							lineaTabulada += separador + texto;
						}
						lineasProcesadas.add(lineaTabulada);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<String> lineasUnificadas = new ArrayList<String>();
			String lineaConcatenada = "";
			for (String linea : lineasProcesadas) {
				if (linea.indexOf("\t") != -1) {
					if (!lineaConcatenada.isEmpty()) {
						lineasUnificadas.add(lineaConcatenada + linea);
						lineaConcatenada = "";
					} else {
						lineasUnificadas.add(linea);
					}
				} else {
					lineaConcatenada = lineaConcatenada + linea;
				}
			}
			FileOutputStream fos = new FileOutputStream(new File(RUTA_BASE + filename + ".txt"));
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			for (String linea : lineasUnificadas) {
				System.out.println(linea);
				linea += "\n";
				bos.write(linea.getBytes());
			}
			bos.close();
			fos.close();
		}

		LOG.info("Fecha Actual: " + fechaActual);
		LOG.info("Fecha Anterior: " + fechaAnterior);

	}

	public List<String> parseaCV(String pdf) throws IOException {
		PdfReader reader = new PdfReader(pdf);
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		FPSTextExtractionStrategy fpsTextExtractionStrategy = new FPSTextExtractionStrategy();
		for (int i = 1; i <= reader.getNumberOfPages(); i++) {
			parser.processContent(i, fpsTextExtractionStrategy);
		}
		reader.close();

		return fpsTextExtractionStrategy.getLineas();
	}

	// public List<String> parsePdf(String pdf, String txt) throws IOException {
	// PdfReader reader = new PdfReader(pdf);
	// // if (reader.isEncrypted()) {
	// // LOG.info("fichero encriptado: " + pdf);
	// // return new ArrayList<String>();
	// // }
	// PdfReaderContentParser parser = new PdfReaderContentParser(reader);
	// PrintWriter out = new PrintWriter(new FileOutputStream(txt));
	// TextExtractionStrategy strategy;
	// FPSTextExtractionStrategy fpsTextExtractionStrategy = new
	// FPSTextExtractionStrategy();
	// for (int i = 1; i <= reader.getNumberOfPages(); i++) {
	// strategy = parser.processContent(i, fpsTextExtractionStrategy);
	// out.println(strategy.getResultantText());
	// }
	// out.flush();
	// out.close();
	// reader.close();
	//
	// return fpsTextExtractionStrategy.getLineas();
	// }

	public void inspectPdf(String pdf, String txt) throws IOException {
		PrintWriter out = new PrintWriter(new FileOutputStream(txt));
		PdfContentReaderTool.listContentStream(new File(pdf), out);
		out.flush();
		out.close();
	}

	public void inspect(PdfReader reader) throws IOException {

		LOG.info("Number of pages: " + reader.getNumberOfPages());
		LOG.info("File length: " + reader.getFileLength());
		LOG.info("Is rebuilt? " + reader.isRebuilt());
		LOG.info("Is encrypted? " + reader.isEncrypted());
	}
}
