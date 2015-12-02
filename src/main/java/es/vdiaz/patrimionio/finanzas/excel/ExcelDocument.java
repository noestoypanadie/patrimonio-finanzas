package es.vdiaz.patrimionio.finanzas.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Hoja Excel
 * 
 * @author Alejandro
 * 
 */
public class ExcelDocument { // NOPMD
	/**
	 * Log
	 */
	private static final Log LOG = LogFactory.getLog(Object.class);

	/**
	 * Woorkbook
	 */
	private Workbook wb;

	/**
	 * Nombre del fichero Excel
	 */
	private String filename;

	/**
	 * Hojas de cálculo
	 */
	private List<Sheet> sheets;

	/**
	 * Hoja actual
	 */
	private Sheet sh;

	/**
	 * Nombre de la hoja por defecto
	 */
	private String defaultSheetName;

	/**
	 * Primera colunna a mirar para establecer si una fila tiene valores
	 */
	private int colRangeMin;

	/**
	 * Última colunna a mirar para establecer si una fila tiene valores
	 */
	private int colRangeMax;

	/**
	 * Evaluator
	 */
	private FormulaEvaluator evaluator;

	/**
	 * Fila actual
	 */
	private int row;

	/**
	 * Columna actual
	 */
	private int col;

	/**
	 * Indica si se debe abrir la excel al inicio
	 */
	private boolean autoOpen;

	/**
	 * Propiedades extras
	 */
	private Properties props;
	/**
	 * 
	 */
	private FileInputStream fis;

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @see com.novayre.commons.excel.IExcelDocument#open()
	 */
	public void open(String fileName) throws Exception {

		fis = new FileInputStream(fileName);

		wb = new XSSFWorkbook(fis);

		evaluator = wb.getCreationHelper().createFormulaEvaluator();

		if (defaultSheetName == null) {
			sh = getWb().getSheetAt(0);

			if (sh == null) {
				throw new Exception("Excel no valida. No se ha podido leer la hoja por defecto");
			}
		} else {
			sh = getWb().getSheet(defaultSheetName);
		}
	}

	/**
	 * @throws IOException
	 * 
	 */
	public void close() throws Exception {
		fis.close();
	}

	/**
	 * @see com.novayre.commons.excel.IExcelDocument#isOpen()
	 */
	public boolean isOpen() {
		return wb != null;
	}

	/**
	 * @see com.novayre.commons.excel.IExcelDocument#getCurrentSheetName()
	 */
	public String getCurrentSheetName() {

		if (sh == null) {
			return null;
		}

		return sh.getSheetName();
	}

	/**
	 * @see com.novayre.commons.excel.IExcelDocument#getString(com.novayre.commons.excel.IColumnsEnumerator)
	 */
	public String getString(int col) {
		Cell cell = sh.getRow(row).getCell(col);

		if (cell == null) {
			return null;
		}

		if (Cell.CELL_TYPE_FORMULA == cell.getCellType()) {

			CellValue cellValue = evaluator.evaluate(cell);

			if (cellValue == null) {
				return null;
			}

			return cellValue.formatAsString();
		}

		if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
			double numericCellValue = cell.getNumericCellValue();
			return String.valueOf(numericCellValue);
		} else {
			return cell.getStringCellValue();
		}
	}

	/**
	 * @see com.novayre.commons.excel.IExcelDocument#getString(com.novayre.commons.excel.IColumnsEnumerator)
	 */
	public String getString(int row, int col) {

		Row r = sh.getRow(row);

		if (r == null) {
			return null;
		}

		Cell cell = r.getCell(col);

		if (cell == null) {
			return null;
		}

		return cell.getStringCellValue();
	}

	/**
	 * @see com.novayre.commons.excel.IExcelDocument#getDate(int)
	 */
	public Date getDate(int col) {
		Cell cell = sh.getRow(row).getCell(col);

		if (Cell.CELL_TYPE_NUMERIC != cell.getCellType()) {
			LOG.warn("No ha sido posible parsear el valor [" + cell.getStringCellValue() + "] a formato Fecha");
			return null;
		}

		try {
			return cell.getDateCellValue();
		} catch (NumberFormatException e) {
			LOG.warn("No ha sido posible parsear el valor [" + cell.getStringCellValue() + "] a formato Fecha");
			return null;
		} catch (IllegalStateException ie) {
			LOG.warn("No ha sido posible parsear el valor [" + cell.getNumericCellValue() + "] a formato Fecha");
			return null;
		}
	}

	/**
	 * @see com.novayre.commons.excel.IExcelDocument#getDate(int, int)
	 */
	public Date getDate(int row, int col) {

		this.row = row;

		return getDate(col);
	}

	/**
	 * @see com.novayre.commons.excel.IExcelDocument#reset()
	 */
	public void reset() {
		row = -1;
	}

	/**
	 * @see com.novayre.commons.excel.IExcelDocument#next()
	 */
	public boolean next() {

		if (!isOpen()) {
			throw new RuntimeException("La hoja excel no está abierta");
		}

		boolean existsData = isExistsData(row + 1, colRangeMin, colRangeMax);

		if (!existsData) {
			return false;
		}

		row++;

		return true;
	}

	/**
	 * Devuelve si una fila contiene algún dato entre dos columnas dadas
	 * 
	 * @param row
	 * @param minCol
	 * @param maxCol
	 * @return
	 */
	private boolean isExistsData(int row, int minCol, int maxCol) {

		for (int i = minCol; i <= maxCol; i++) {

			String cellData = getString(row, i);

			if (cellData != null && !"".equals(cellData)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @see com.novayre.commons.excel.IExcelDocument#getCell(int)
	 */
	public Cell getCell(int col) {

		return sh.getRow(row).getCell(col);

	}

	/**
	 * @see com.novayre.commons.excel.IExcelDocument#getCell(int, int)
	 */
	public Cell getCell(int row, int col) {

		this.row = row;

		return getCell(col);
	}

	public Workbook getWb() {

		return wb;
	}

	public void setWb(Workbook wb) {
		this.wb = wb;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<Sheet> getSheets() {
		return sheets;
	}

	public void setSheets(List<Sheet> sheets) {
		this.sheets = sheets;
	}

	public Sheet getSh() {
		return sh;
	}

	public void setSh(Sheet sh) {
		this.sh = sh;
	}

	public String getDefaultSheetName() {
		return defaultSheetName;
	}

	public void setDefaultSheetName(String defaultSheetName) {
		this.defaultSheetName = defaultSheetName;
	}

	public boolean isAutoOpen() {
		return autoOpen;
	}

	public void setAutoOpen(boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

	/**
	 * @return the colRangeMin
	 */
	public int getColRangeMin() {
		return colRangeMin;
	}

	/**
	 * @param colRangeMin
	 *            the colRangeMin to set
	 */
	public void setColRangeMin(int colRangeMin) {
		this.colRangeMin = colRangeMin;
	}

	/**
	 * @return the colRangeMax
	 */
	public int getColRangeMax() {
		return colRangeMax;
	}

	/**
	 * @param colRangeMax
	 *            the colRangeMax to set
	 */
	public void setColRangeMax(int colRangeMax) {
		this.colRangeMax = colRangeMax;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @param row
	 *            the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return the col
	 */
	public int getCol() {
		return col;
	}

	/**
	 * @param col
	 *            the col to set
	 */
	public void setCol(int col) {
		this.col = col;
	}

	/**
	 * @return the props
	 */
	public Properties getProps() {
		return props;
	}

	/**
	 * @param props
	 *            the props to set
	 */
	public void setProps(Properties props) {
		this.props = props;
	}

}
