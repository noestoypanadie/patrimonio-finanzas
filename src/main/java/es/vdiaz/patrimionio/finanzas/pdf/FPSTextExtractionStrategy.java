package es.vdiaz.patrimionio.finanzas.pdf;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.pdf.parser.LineSegment;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;

public class FPSTextExtractionStrategy extends SimpleTextExtractionStrategy {
	
	private static final Log LOG = LogFactory.getLog(FPSTextExtractionStrategy.class);
	
	private StringBuffer result = new StringBuffer();
	private Vector lastStart;
	private Vector lastEnd;
	
	private List<String> lineas = new ArrayList<String>();

	@Override
	public void beginTextBlock() {
	}

	@Override
	public void endTextBlock() {
//		System.out.println(result.toString());
//		result = new StringBuffer();
	}

	@Override
	public void renderText(TextRenderInfo renderInfo) {
		boolean firstRender = result.length() == 0;
		boolean hardReturn = false;

		LineSegment segment = renderInfo.getBaseline();
		Vector start = segment.getStartPoint();
		Vector end = segment.getEndPoint();

		if (!firstRender) {
			Vector x0 = start;
			Vector x1 = lastStart;
			Vector x2 = lastEnd;

			// see
			// http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
			float dist = (x2.subtract(x1)).cross((x1.subtract(x0))).lengthSquared() / x2.subtract(x1).lengthSquared();

			float sameLineThreshold = 1f; // we should probably base this on the
											// current font metrics, but 1 pt
											// seems to be sufficient for the
											// time being
			if (dist > sameLineThreshold)
				hardReturn = true;

			// Note: Technically, we should check both the start and end
			// positions, in case the angle of the text changed without any
			// displacement
			// but this sort of thing probably doesn't happen much in reality,
			// so we'll leave it alone for now
		}

		if (hardReturn) {
			// System.out.println("<< Hard Return >>");
//			result.append('\n');
//			System.out.println();
			String linea = result.toString().trim();
			if(!linea.isEmpty()) {
				lineas.add(linea);
				LOG.debug("nueva linea: " + result.toString());
			}
			result = new StringBuffer();
		} else if (!firstRender) {
			if (result.charAt(result.length() - 1) != ' ' && renderInfo.getText().charAt(0) != ' ') { // we
																										// only
																										// insert
																										// a
																										// blank
																										// space
																										// if
																										// the
																										// trailing
																										// character
																										// of
																										// the
																										// previous
																										// string
																										// wasn't
																										// a
																										// space,
																										// and
																										// the
																										// leading
																										// character
																										// of
																										// the
																										// current
																										// string
																										// isn't
																										// a
																										// space
				float spacing = lastEnd.subtract(start).length();
				if (spacing > renderInfo.getSingleSpaceWidth() / 2f) {
					result.append(' ');
					// System.out.println("Inserting implied space before '" +
					// renderInfo.getText() + "'");
				}
			}
		} else {
			// System.out.println("Displaying first string of content '" + text
			// + "' :: x1 = " + x1);
		}

		// System.out.println("[" + renderInfo.getStartPoint() + "]->[" +
		// renderInfo.getEndPoint() + "] " + renderInfo.getText());
		result.append(renderInfo.getText());

		lastStart = start;
		lastEnd = end;

	}

	public List<String> getLineas(){
		return lineas; 
	}
}
