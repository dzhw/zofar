/*START HEADER*/
/* Zofar Survey System
* Copyright (C) 2014 Deutsches Zentrum für Hochschul- und Wissenschaftsforschung
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
/*STOP HEADER*/
/*
 * Class representing a ODF-Document
 */
package eu.dzhw.zofar.management.utils.odf.components;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.attribute.fo.FoTextAlignAttribute;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.text.TextIndexBodyElement;
import org.odftoolkit.odfdom.dom.element.text.TextIndexTitleElement;
import org.odftoolkit.odfdom.dom.element.text.TextIndexTitleTemplateElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextTableOfContentElement;
import org.odftoolkit.odfdom.dom.element.text.TextTableOfContentSourceElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfPageLayoutProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTableCellProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTableProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTextProperties;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.doc.text.OdfWhitespaceProcessor;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.type.Length;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import eu.dzhw.zofar.management.utils.images.ImageConverter;
/**
 * The Class WriterDocument.
 */
public class WriterDocument {
	/** The doc. */
	private OdfTextDocument doc;
	public String coreCellStyleName = "";
	public String coreParagraphStyleName = "";
	private String coreTableStyleName;
	/**
	 * Instantiates a new writer document.
	 *
	 * @param documentPath
	 *            the document path
	 * @throws Exception
	 *             the exception
	 */
	public WriterDocument(final String documentPath) throws Exception {
		this(documentPath, true);
	}
	public WriterDocument(final String documentPath, final boolean isResource) throws Exception {
		super();
		if (documentPath != null) {
			final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = null;
			if (isResource) {
				is = classloader.getResourceAsStream(documentPath);
			} else {
				is = new FileInputStream(new File(documentPath));
			}
			this.doc = OdfTextDocument.loadDocument(is);
			this.doc.getContentRoot().setTextUseSoftPageBreaksAttribute(true);
		}
	}
	/**
	 * Instantiates a new writer document.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public WriterDocument() throws Exception {
		super();
		this.doc = OdfTextDocument.newTextDocument();
		this.doc.getContentRoot().setTextUseSoftPageBreaksAttribute(true);
		final OdfOfficeAutomaticStyles styles = doc.getContentDom().getOrCreateAutomaticStyles();
		final OdfStyle coreTableStyle = styles.newStyle(OdfStyleFamily.Table);
		coreTableStyle.setProperty(OdfTableProperties.RelWidth, "80%");
		coreTableStyleName = coreTableStyle.getStyleNameAttribute();
		final OdfStyle coreCellStyle = styles.newStyle(OdfStyleFamily.TableCell);
		coreCellStyle.setProperty(OdfTableCellProperties.WrapOption, "wrap");
		coreCellStyle.setProperty(OdfTableCellProperties.VerticalAlign, "middle");
		coreCellStyle.setProperty(OdfParagraphProperties.TextAlign, "center");
		coreCellStyle.setProperty(OdfTextProperties.FontName, "Arial");
		coreCellStyle.setProperty(OdfTextProperties.FontWeight, "Regular");
		coreCellStyle.setProperty(OdfTextProperties.FontSize, "10pt");
		coreCellStyle.setProperty(OdfTextProperties.Color, "#001234");
		coreCellStyleName = coreCellStyle.getStyleNameAttribute();
		final OdfStyle coreParagraphStyle = styles.newStyle(OdfStyleFamily.Paragraph);
		coreParagraphStyle.setProperty(OdfTextProperties.FontName, "Arial");
		coreParagraphStyle.setProperty(OdfTextProperties.FontWeight, "Regular");
		coreParagraphStyle.setProperty(OdfTextProperties.FontSize, "10pt");
		coreParagraphStyle.setProperty(OdfTextProperties.Color, "#000000");
		coreParagraphStyleName = coreParagraphStyle.getStyleNameAttribute();
	}
	public void setLandscape(final boolean landscape) {
		if (this.doc == null) {
			return;
		}
		if (landscape) {
			final StyleMasterPageElement defaultPage = this.doc.getOfficeMasterStyles().getMasterPage("Standard");
			final String pageLayoutName = defaultPage.getStylePageLayoutNameAttribute();
			final OdfStylePageLayout pageLayoutStyle = defaultPage.getAutomaticStyles().getPageLayout(pageLayoutName);
			pageLayoutStyle.setProperty(OdfPageLayoutProperties.PrintOrientation, "landscape");
			pageLayoutStyle.setProperty(OdfPageLayoutProperties.PageHeight, "210.01mm");
			pageLayoutStyle.setProperty(OdfPageLayoutProperties.PageWidth, "297mm");
			pageLayoutStyle.setProperty(OdfPageLayoutProperties.NumFormat, "1");
		} else {
			final StyleMasterPageElement defaultPage = this.doc.getOfficeMasterStyles().getMasterPage("Standard");
			final String pageLayoutName = defaultPage.getStylePageLayoutNameAttribute();
			final OdfStylePageLayout pageLayoutStyle = defaultPage.getAutomaticStyles().getPageLayout(pageLayoutName);
			pageLayoutStyle.setProperty(OdfPageLayoutProperties.PrintOrientation, "portrait");
			pageLayoutStyle.setProperty(OdfPageLayoutProperties.PageWidth, "210.01mm");
			pageLayoutStyle.setProperty(OdfPageLayoutProperties.PageHeight, "297mm");
			pageLayoutStyle.setProperty(OdfPageLayoutProperties.NumFormat, "1");
		}
	}
	/** The Constant DUMMY. */
	private final static String[] DUMMY = new String[0];
	/**
	 * Add Table to Document.
	 *
	 * @param text
	 *            the text
	 * @param row
	 *            the row
	 * @param column
	 *            the column
	 * @param data
	 *            the data
	 * @param showRowLabels
	 *            the show row labels
	 * @throws Exception
	 *             the exception
	 */
	public void addTable(final String text, final ArrayList<String> row, final ArrayList<String> column,
			final Map<String, Map<String, String>> data, final boolean showRowLabels) throws Exception {
		if (this.doc == null) {
			return;
		}
		final String[] rowlabels = row.toArray(DUMMY);
		final String[] columnlabels = column.toArray(DUMMY);
		final int rowCount = rowlabels.length;
		final int columnCount = columnlabels.length;
		final String[][] dataArray = new String[rowCount][columnCount];
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final String rowLabel = rowlabels[rowIndex];
			if (data.containsKey(rowLabel)) {
				for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
					final String columnLabel = columnlabels[columnIndex];
					if (data.get(rowLabel).containsKey(columnLabel)) {
						dataArray[rowIndex][columnIndex] = data.get(rowLabel).get(columnLabel);
					}
				}
			}
		}
		this.addText(text);
		if (showRowLabels) {
			this.createTable(UUID.randomUUID().toString(), rowlabels, columnlabels, dataArray);
		} else {
			this.createTable(UUID.randomUUID().toString(), columnlabels, dataArray);
		}
	}
	/** The Constant DUMMY1. */
	private final static String[][] DUMMY1 = new String[0][0];
	/**
	 * Add Table to Document.
	 *
	 * @param text
	 *            the text
	 * @param columnLabels
	 *            the column labels
	 * @param data
	 *            the data
	 * @return the odf table
	 * @throws Exception
	 *             the exception
	 */
	public OdfTable addTable(final String text, final String[] columnLabels, final List<String[]> data)
			throws Exception {
		return this.addTable(text, columnLabels, data.toArray(DUMMY1));
	}
	/**
	 * Add Table to Document.
	 *
	 * @param text
	 *            the text
	 * @param columnLabels
	 *            the column labels
	 * @param data
	 *            the data
	 * @return the odf table
	 * @throws Exception
	 *             the exception
	 */
	public OdfTable addTable(final String text, final String[] columnLabels, final String[][] data) throws Exception {
		if (this.doc == null) {
			return null;
		}
		this.addText(text);
		return this.createTable(UUID.randomUUID().toString(), columnLabels, data);
	}
	/**
	 * Creates table Element.
	 *
	 * @param tablename
	 *            the tablename
	 * @param rowlabels
	 *            the rowlabels
	 * @param columnlabels
	 *            the columnlabels
	 * @param data
	 *            the data
	 * @return the odf table
	 */
	private OdfTable createTable(final String tablename, final String[] rowlabels, final String[] columnlabels,
			final String[][] data) {
		if (this.doc == null) {
			return null;
		}
		final OdfTable table = OdfTable.newTable(this.doc, rowlabels, columnlabels, data);
		table.setTableName(tablename);
		final int rowCount = table.getRowCount();
		final int colCount = table.getColumnCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			for (int colIndex = 0; colIndex < colCount; colIndex++) {
				final OdfTableCell cell = table.getCellByPosition(colIndex, rowIndex);
				cell.getOdfElement().setStyleName(this.coreCellStyleName);
				cell.setTextWrapped(false);
			}
		}
		return table;
	}
	/**
	 * Creates table Element.
	 *
	 * @param tablename
	 *            the tablename
	 * @param colNames
	 *            the col names
	 * @param data
	 *            the data
	 * @return the odf table
	 */
	private OdfTable createTable(final String tablename, final String[] colNames, final String[][] data) {
		if (data == null) {
			return null;
		}
		if (data.length == 0) {
			return null;
		}
		final OdfTextElement[][] dataElements = new OdfTextElement[data.length][];
		int rowIndex = 0;
		for (final String[] entry : data) {
			int colIndex = 0;
			dataElements[rowIndex] = new OdfTextElement[entry.length];
			for (final String col : entry) {
				dataElements[rowIndex][colIndex] = new OdfText(col);
				colIndex++;
			}
			rowIndex++;
		}
		return this.createTable(tablename, colNames, dataElements);
	}
	/**
	 * Creates table Element.
	 *
	 * @param tablename
	 *            the tablename
	 * @param colNames
	 *            the col names
	 * @param data
	 *            the data
	 * @return the odf table
	 */
	private OdfTable createTable(final String tablename, final String[] colNames, final OdfTextElement[][] data) {
		if (data == null) {
			return null;
		}
		if (data.length == 0) {
			return null;
		}
		if (this.doc == null) {
			return null;
		}
		int maxColumnCount = 0;
		for (final OdfTextElement[] entry : data) {
			maxColumnCount = Math.max(maxColumnCount, entry.length);
		}
		final OdfTable table = OdfTable.newTable(this.doc, data.length, colNames.length, 1, colNames.length);
		table.setTableName(tablename);
		int rowIndex = 0;
		int headerColIndex = 0;
		for (final String headerCol : colNames) {
			if (headerColIndex >= colNames.length) {
				break;
			}
			table.getCellByPosition(headerColIndex, rowIndex).setStringValue(headerCol);
			table.getCellByPosition(headerColIndex, rowIndex).getOdfElement().setStyleName(this.coreCellStyleName);
			headerColIndex++;
		}
		rowIndex++;
		for (final OdfTextElement[] entry : data) {
			int colIndex = 0;
			for (final OdfTextElement col : entry) {
				if (colIndex >= colNames.length) {
					break;
				}
				table.getCellByPosition(colIndex, rowIndex).getOdfElement().setStyleName(this.coreCellStyleName);
				table.getCellByPosition(colIndex, rowIndex).setStringValue(col.getContent());
				table.getCellByPosition(colIndex, rowIndex)
						.setHorizontalAlignment(FoTextAlignAttribute.Value.CENTER.toString());
				table.getCellByPosition(colIndex, rowIndex)
						.setVerticalAlignment(FoTextAlignAttribute.Value.CENTER.toString());
				if ((OdfColoredText.class).isAssignableFrom(col.getClass())) {
					table.getCellByPosition(colIndex, rowIndex).setDisplayText(col.getContent(), "Text");
					table.getCellByPosition(colIndex, rowIndex)
							.setCellBackgroundColor(((OdfColoredText) col).getColor());
				} else if ((OdfText.class).isAssignableFrom(col.getClass())) {
					table.getCellByPosition(colIndex, rowIndex).setDisplayText(col.getContent(), "Text");
				}
				if ((OdfTitle.class).isAssignableFrom(col.getClass())) {
					table.getCellByPosition(colIndex, rowIndex).setDisplayText(col.getContent(), "Heading_20_1");
				}
				colIndex++;
			}
			rowIndex++;
		}
		return table;
	}
	/**
	 * Add Table of Content Element.
	 *
	 * @param title
	 *            the title
	 */
	public TextTableOfContentElement addTOC(final String title) {
		if (this.doc == null) {
			return null;
		}
		return this.createTOC(title, this.getTop(), true);
	}
	/**
	 * Add Table of Content Element.
	 *
	 * @param title
	 *            the title
	 * @param ref
	 *            the ref
	 * @return the text table of content element
	 */
	public TextTableOfContentElement addTOC(final String title, final Node ref) {
		if (this.doc == null) {
			return null;
		}
		return this.createTOC(title, ref, false);
	}
	public void updateTOC(final TextTableOfContentElement toc) {
		if (toc == null) {
			return;
		}
	}
	/**
	 * Retrieve top element of Document.
	 *
	 * @return the top
	 */
	private Node getTop() {
		try {
			final Node top = this.doc.getContentRoot().getFirstChild();
			return top;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Retrieve root element of Document.
	 *
	 * @return the root
	 */
	private Node getRoot() {
		try {
			final Node top = this.doc.getContentRoot();
			return top;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Creates Table of Content Element.
	 *
	 * @param title
	 *            the title
	 * @param ref
	 *            the ref
	 * @param before
	 *            the before
	 * @return the text table of content element
	 */
	private TextTableOfContentElement createTOC(final String title, final Node ref, final boolean before) {
		try {
			final OdfContentDom content = this.doc.getContentDom();
			final TextTableOfContentElement toc = content.newOdfElement(TextTableOfContentElement.class);
			toc.setTextNameAttribute(title);
			toc.setTextProtectedAttribute(true);
			final TextTableOfContentSourceElement tocSource = toc.newTextTableOfContentSourceElement();
			tocSource.setTextOutlineLevelAttribute(10);
			tocSource.setTextUseIndexMarksAttribute(true);
			final TextIndexTitleTemplateElement textIndexTitleTemplate = tocSource.newTextIndexTitleTemplateElement();
			textIndexTitleTemplate.setTextStyleNameAttribute("Contents_20_Heading");
			textIndexTitleTemplate.setTextContent(title);
			final TextIndexBodyElement textIndexBody = toc.newTextIndexBodyElement();
			final TextIndexTitleElement TextIndexTitle = textIndexBody.newTextIndexTitleElement(title + "_Head");
			final TextPElement texp = TextIndexTitle.newTextPElement();
			texp.setTextStyleNameAttribute("Contents_20_Heading");
			texp.setTextContent(title);
			if (before) {
				this.getRoot().insertBefore(toc, ref);
			} else {
				final Node refNextNode = ref.getNextSibling();
				if (refNextNode == null) {
					this.getRoot().appendChild(toc);
				} else {
					this.getRoot().insertBefore(toc, refNextNode);
				}
			}
			return toc;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Add Title Element.
	 *
	 * @param text
	 *            the text
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addTitle(final String text) throws Exception {
		return this.addTextBlock(text, "Title");
	}
	/**
	 * Add SubHeading Element (Style Heading_20_2).
	 *
	 * @param text
	 *            the text
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addSubHeading1(final String text) throws Exception {
		return this.addTextBlock(text, "Heading_20_2");
	}
	/**
	 * Add SubSubHeading Element (Style Heading_20_3).
	 *
	 * @param text
	 *            the text
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addSubHeading2(final String text) throws Exception {
		return this.addTextBlock(text, "Heading_20_3");
	}
	/**
	 * Add Heading Element (Style Heading_20_1).
	 *
	 * @param text
	 *            the text
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addHeading(final String text) throws Exception {
		return this.addTextBlock(text, "Heading_20_1");
	}
	/**
	 * Add text Element.
	 *
	 * @param text
	 *            the text
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addText(final String text) throws Exception {
		return this.addTextBlock(text, this.coreParagraphStyleName);
	}
	/**
	 * Add text-Pair Element connected by glue.
	 *
	 * @param text1
	 *            the text1
	 * @param text2
	 *            the text2
	 * @param glue
	 *            the glue
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addTextPair(final String text1, final String text2, final String glue) throws Exception {
		return this.addPair(text1, text2, glue, this.coreParagraphStyleName);
	}
	/**
	 * Add text-Pair Element connected by glue using custom Style.
	 *
	 * @param text1
	 *            the text1
	 * @param text2
	 *            the text2
	 * @param glue
	 *            the glue
	 * @param style
	 *            the style
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	private OdfTextParagraph addPair(final String text1, final String text2, final String glue, final String style)
			throws Exception {
		if (this.doc == null) {
			return null;
		}
		final OdfTextParagraph paragraph = this.doc.newParagraph();
		paragraph.addStyledContent(style, text1);
		OdfWhitespaceProcessor.appendText(paragraph, glue + text2);
		paragraph.setStyleName(style);
		return paragraph;
	}
	/**
	 * Add text block using custom Style.
	 *
	 * @param text
	 *            the text
	 * @param style
	 *            the style
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addTextBlock(final String text, final String style) throws Exception {
		if (this.doc == null) {
			return null;
		}
		final OdfTextParagraph paragraph = this.doc.newParagraph();
		if (style != null) {
			paragraph.addStyledContent(style, text);
			paragraph.setStyleName(style);
		}
		return paragraph;
	}
	/**
	 * Add page break.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void addPageBreak() throws Exception {
		if (this.doc == null) {
			return;
		}
		final OdfContentDom contentDocument = this.doc.getContentDom();
		final OdfOfficeAutomaticStyles styles = contentDocument.getAutomaticStyles();
		final OdfStyle style = styles.newStyle(OdfStyleFamily.Paragraph);
		style.newStyleParagraphPropertiesElement().setFoBreakBeforeAttribute("page");
		final TextPElement page = this.doc.getContentRoot().newTextPElement();
		page.setStyleName(style.getStyleNameAttribute());
	}
	/**
	 * Add line break using style 'text'.
	 *
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addTextLineBreak() throws Exception {
		return this.addLineBreak("Text");
	}
	/**
	 * Add line break using custom style.
	 *
	 * @param style
	 *            the style
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	private OdfTextParagraph addLineBreak(final String style) throws Exception {
		if (this.doc == null) {
			return null;
		}
		final OdfTextParagraph paragraph = this.doc.newParagraph();
		paragraph.addStyledContent(style, "");
		OdfWhitespaceProcessor.appendText(paragraph, "\n");
		return paragraph;
	}
	public OdfDrawImage addImage(final File imageFile, int maxImageHeight, int maxImageWidth) throws Exception {
		if (this.doc == null) {
			return null;
		}
		String ext = FilenameUtils.getExtension(imageFile.getAbsolutePath());
		final OdfTextParagraph paragraph = this.doc.newParagraph();
		paragraph.setProperty(OdfParagraphProperties.TextAlign, "center");
		OdfPackage pkg = this.doc.getPackage();
		final DrawFrameElement frame = paragraph.newDrawFrameElement();
		frame.setSvgWidthAttribute(Length.mapToUnit(maxImageWidth + "px", Length.Unit.CENTIMETER));
		frame.setSvgHeightAttribute(Length.mapToUnit(maxImageHeight + "px", Length.Unit.CENTIMETER));
		final OdfDrawImage image = (OdfDrawImage) frame.newDrawImageElement();
		final BufferedImage orig = ImageConverter.getInstance().fromImage(ImageIO.read(imageFile));
		final BufferedImage scaled = ImageConverter.getInstance().scaleMax(orig, maxImageHeight, maxImageWidth);
		final File scaledFile = new File(imageFile.getParentFile(), "scaled_" + imageFile.getName());
		try {
			ImageIO.write(scaled, ext, scaledFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String packagePath = image.newImage(scaledFile.toURI());
		return image;
	}
	/**
	 * Clear Document.
	 *
	 * @throws DOMException
	 *             the DOM exception
	 * @throws Exception
	 *             the exception
	 */
	public void clear() throws DOMException, Exception {
		if (this.doc == null) {
			return;
		}
		Node childNode = this.doc.getContentRoot().getFirstChild();
		while (childNode != null) {
			this.doc.getContentRoot().removeChild(childNode);
			childNode = this.doc.getContentRoot().getFirstChild();
		}
	}
	/**
	 * Save Document.
	 *
	 * @param documentPath
	 *            the document path
	 * @throws Exception
	 *             the exception
	 */
	public void save(final String documentPath) throws Exception {
		if (this.doc == null) {
			return;
		}
		this.doc.save(documentPath);
	}
	/**
	 * Export Document as pdf.
	 *
	 * @param documentPath
	 *            the document path
	 * @throws Exception
	 *             the exception
	 */
	public void exportAsPdf(final String documentPath) throws Exception {
		throw new Exception("pretty buggy.");
	}
	/**
	 * Enumeration of Styles embedded in Document
	 *
	 * @return the styles
	 * @throws Exception
	 *             the exception
	 */
	public Map<String, OdfStyle> getStyles() throws Exception {
		if (this.doc == null) {
			return null;
		}
		final OdfOfficeStyles styles = this.doc.getDocumentStyles();
		if (styles != null) {
			final Map<String, OdfStyle> back = new HashMap<String, OdfStyle>();
			final NodeList styleList = styles.getElementsByTagName("style:style");
			for (int i = 0; i < styleList.getLength(); i++) {
				final OdfStyle style = (OdfStyle) styleList.item(i);
				final String name = style.getStyleNameAttribute();
				back.put(name, style);
			}
			return back;
		}
		return null;
	}
}
