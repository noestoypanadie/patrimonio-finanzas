package es.vdiaz.patrimionio.finanzas.exec;

/*
 * Aris David
 * 04-July-2013
 * Test Program to Test the Yahoo Finance Historical Data Downloader.
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import es.vdiaz.patrimionio.finanzas.excel.ExcelDocument;
import es.vdiaz.patrimionio.finanzas.yahoo.YahooFinanceHttp;
import es.vdiaz.patrimionio.finanzas.yahoo.YahooFinanceURLConstruct;

public class Ibex35 {

	public static void main(String[] args) {

		String rutaBase = "C:\\Users\\vdiaz\\Dropbox\\Patrimonio\\Finanzas\\cotizaciones\\indexacion-progresiva\\";
		if (args.length > 0) {
			rutaBase = args[0] + File.separator;
			System.out.println("Ejecutando YahooFinance en la ruta: " + rutaBase);
		} else {
			// System.err.println("Necesito un parametro con la ruta base");
			// return;
		}

		// Start Date
		Calendar startDate = Calendar.getInstance();
		// Set startDate
		startDate.set(Calendar.YEAR, 1900);
		startDate.set(Calendar.MONTH, 0); // Month Jan = 0, Feb = 1...Dec = 11
		startDate.set(Calendar.DATE, 1);

		// End Date
		Calendar endDate = Calendar.getInstance();

		Map<String, String> tickers = new HashMap<String, String>();

		Properties valores = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(rutaBase + "valores.properties");

			valores.load(input);

			for (Object valor : valores.keySet()) {
				tickers.put(valor.toString(), valores.get(valor).toString());
			}

			ExcelDocument excelDocument = new ExcelDocument();
			excelDocument.open(rutaBase + "cotizaciones.xlsx");

			for (String ticker : tickers.keySet()) {
				System.out.println("Bajando " + ticker);
				YahooFinanceURLConstruct AAPL = new YahooFinanceURLConstruct(startDate, endDate, ticker);

				YahooFinanceHttp testobj = new YahooFinanceHttp(AAPL.constructURL());

				try {
					FileOutputStream fos = new FileOutputStream(new File(rutaBase + ticker + "-" + tickers.get(ticker) + ".txt"));
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					for (String linea : testobj.getLineasProcesadas()) {
						// System.out.println(linea);
						linea += "\n";
						bos.write(linea.getBytes());
					}
					bos.close();
					fos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				rellenaExcel(tickers.get(ticker), testobj.getLineasProcesadas(), excelDocument);
			}
			excelDocument.close();
			excelDocument.getWb().write(new FileOutputStream(new File(rutaBase + "cotizaciones.xlsx")));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void rellenaExcel(String valor, List<String> data, ExcelDocument excelDocument) throws Exception {
		Sheet sheet = localizaHoja(excelDocument, valor.toLowerCase().trim());
		if (sheet == null) {
			sheet = excelDocument.getSh();
		}
		short rowNumber = 0;
		short columnNumber = 0;

		CellStyle styleDouble = excelDocument.getWb().createCellStyle();
		styleDouble.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0,000"));
		CellStyle styleDate = excelDocument.getWb().createCellStyle();
		styleDate.setDataFormat((short) BuiltinFormats.getBuiltinFormat("d-mmm-yy"));
		CellStyle styleInteger = excelDocument.getWb().createCellStyle();
		styleInteger.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0"));
		for (String registro : data) {
			columnNumber = 0;
			Row row1 = sheet.getRow(rowNumber);
			if (row1 == null) {
				row1 = sheet.createRow(rowNumber);
			}
			rowNumber += 1;
			for (String columna : registro.split(YahooFinanceHttp.SEPARADOR)) {
				Cell cell = row1.getCell(columnNumber);
				if (cell == null) {
					cell = row1.createCell(columnNumber);
				}
				columnNumber += 1;
				if (columna != null) {
					if (rowNumber > 1 && columnNumber > 1) {
						cell.setCellStyle(styleDouble);
						cell.setCellValue(Double.valueOf(columna.replaceAll(",", ".")));
					} else {
						cell.setCellValue(columna.toString());
					}
				}
			}
		}
	}

	private static Sheet localizaHoja(ExcelDocument excelDocument, String nombreHoja) {
		Sheet sheetAgrupado = null;
		for (int i = 0; i < excelDocument.getWb().getNumberOfSheets(); i++) {
			if (excelDocument.getWb().getSheetName(i).toLowerCase().equals(nombreHoja)) {
				sheetAgrupado = excelDocument.getWb().getSheetAt(i);
				break;
			}
		}
		return sheetAgrupado;
	}
}