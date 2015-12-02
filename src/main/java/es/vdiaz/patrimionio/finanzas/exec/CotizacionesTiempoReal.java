package es.vdiaz.patrimionio.finanzas.exec;

/*
 * Aris David
 * 04-July-2013
 * Test Program to Test the Yahoo Finance Historical Data Downloader.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import es.vdiaz.patrimionio.finanzas.excel.ExcelDocument;
import es.vdiaz.patrimionio.finanzas.yahoo.YahooFinanceURLConstruct;

public class CotizacionesTiempoReal {

	public static void main(String[] args) {
		String rutaBase = "C:\\Users\\vdiaz\\Dropbox\\Patrimonio\\Finanzas\\_cartera\\";
		if (args.length > 0) {
			rutaBase = args[0] + File.separator;
			// System.out.println("Ejecutando YahooFinance en la ruta: " + rutaBase);
		} else {
			// System.err.println("Necesito un parametro con la ruta base, uso por defecto:" + rutaBase);
			// return;
		}

		Map<String, String> tickers = new HashMap<String, String>();

		Properties valores = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(rutaBase + "..\\cotizaciones\\valores.properties");

			valores.load(input);

			for (Object valor : valores.keySet()) {
				tickers.put(valor.toString(), valores.get(valor).toString());
			}

			ExcelDocument excelDocument = new ExcelDocument();
			excelDocument.open(rutaBase + "Cartera.xlsx");
			Sheet sheet = YahooFinance.localizaHoja(excelDocument, "TIEMPO_REAL".toLowerCase());

			if (sheet == null) {
				sheet = excelDocument.getSh();
			}
			short rowNumber = 0;
			CellStyle styleDouble = excelDocument.getWb().createCellStyle();
			styleDouble.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0,000"));

			for (String ticker : tickers.keySet()) {

				URL url = new URL(YahooFinanceURLConstruct.constructTiempoReal(ticker));
				URLConnection urlConn = url.openConnection();

				InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
				BufferedReader buff = new BufferedReader(inStream);

				String stringLine;
				StringBuffer pagina = new StringBuffer();
				while ((stringLine = buff.readLine()) != null) {
					pagina.append(stringLine);
				}
				String paginaS = pagina.toString().toLowerCase();

				String sTag = "<span id=\"yfs_l84_" + ticker.toLowerCase() + "\">";
				int iStart = paginaS.indexOf(sTag);
				int iEnd = paginaS.indexOf("<", iStart + sTag.length());
				String cotizacion = paginaS.substring(iStart + sTag.length(), iEnd);

				if (ticker != null) {
					Row row1 = sheet.getRow(rowNumber);
					if (row1 == null) {
						row1 = sheet.createRow(rowNumber);
					}
					rowNumber += 1;
					Cell cell = row1.getCell(0);
					if (cell == null) {
						cell = row1.createCell(0);
					}
					cell.setCellValue(ticker);
					Cell cell2 = row1.getCell(1);
					if (cell2 == null) {
						cell2 = row1.createCell(1);
					}
					cell2.setCellStyle(styleDouble);
					cell2.setCellValue(Double.valueOf(cotizacion.replaceAll(",", ".")));
				}
				String lineaProcesada = ticker + "\t" + cotizacion;
				System.out.println(lineaProcesada);
			}

			excelDocument.close();
			excelDocument.getWb().write(new FileOutputStream(new File(rutaBase + "Cartera.xlsx")));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}