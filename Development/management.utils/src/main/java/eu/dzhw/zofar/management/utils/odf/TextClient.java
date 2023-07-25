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
 * Class to create and export ODF-Documents
 */
package eu.dzhw.zofar.management.utils.odf;
import java.io.File;
import java.util.Map;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.dom.element.text.TextTableOfContentElement;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import eu.dzhw.zofar.management.utils.odf.components.OdfTextElement;
import eu.dzhw.zofar.management.utils.odf.components.WriterDocument;
/**
 * The Class TextClient.
 */
public class TextClient {
	/** The Constant INSTANCE. */
	private static final TextClient INSTANCE = new TextClient();
	/** The Constant LOGGER. */
	private final static Logger LOGGER = LoggerFactory.getLogger(TextClient.class);
	/**
	 * Instantiates a new text client.
	 */
	private TextClient() {
		super();
	}
	/**
	 * Gets the single instance of TextClient.
	 * 
	 * @return single instance of TextClient
	 */
	public static synchronized TextClient getInstance() {
		return INSTANCE;
	}
	/**
	 * Creates the document.
	 * 
	 * @return the writer document
	 * @throws Exception
	 *             the exception
	 */
	public WriterDocument createDocument() throws Exception {
		return new WriterDocument();
	}
	/**
	 * Creates the document.
	 * 
	 * @return the writer document
	 * @throws Exception
	 *             the exception
	 */
	public WriterDocument createDocument(final String template) throws Exception {
		return new WriterDocument(template);
	}
	/**
	 * Load the document.
	 * 
	 * @return the writer document
	 * @throws Exception
	 *             the exception
	 */
	public WriterDocument loadDocument(final String path) throws Exception {
		return new WriterDocument(path);
	}
	/**
	 * Save document.
	 * 
	 * @param doc
	 *            the doc
	 * @param documentPath
	 *            the document path
	 * @throws Exception
	 *             the exception
	 */
	public void saveDocument(final WriterDocument doc, final String documentPath) throws Exception {
		if (doc == null)
			return;
		doc.save(documentPath);
	}
	/**
	 * Export document as pdf.
	 * 
	 * @param doc
	 *            the doc
	 * @param documentPath
	 *            the document path
	 * @throws Exception
	 *             the exception
	 */
	public void exportDocumentAsPDF(final WriterDocument doc, final String documentPath) throws Exception {
		if (doc == null)
			return;
		doc.exportAsPdf(documentPath);
	}
	/**
	 * Add Text to Document.
	 * 
	 * @param doc
	 *            the doc
	 * @param text
	 *            the text
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addText(final WriterDocument doc, final String text) throws Exception {
		if (doc == null)
			return null;
		return doc.addText(text);
	}
	/**
	 * Add Text-Pair connected by glue to Document.
	 * 
	 * @param doc
	 *            the doc
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
	public OdfTextParagraph addTextPair(final WriterDocument doc, final String text1, final String text2, final String glue) throws Exception {
		if (doc == null)
			return null;
		return doc.addTextPair(text1, text2, glue);
	}
	/**
	 * Add Title* Add Text to Document.
	 * 
	 * @param doc
	 *            the doc
	 * @param text
	 *            the text
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addTitle(final WriterDocument doc, final String text) throws Exception {
		if (doc == null)
			return null;
		return doc.addTitle(text);
	}
	/**
	 * Add Heading to Document.
	 * 
	 * @param doc
	 *            the doc
	 * @param text
	 *            the text
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addHeading(final WriterDocument doc, final String text) throws Exception {
		if (doc == null)
			return null;
		return doc.addHeading(text);
	}
	/**
	 * Add Sub-Heading to Document.
	 * 
	 * @param doc
	 *            the doc
	 * @param text
	 *            the text
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addSubHeading(final WriterDocument doc, final String text) throws Exception {
		if (doc == null)
			return null;
		return doc.addSubHeading1(text);
	}
	/**
	 * Add Sub-Sub-Heading to Document.
	 * 
	 * @param doc
	 *            the doc
	 * @param text
	 *            the text
	 * @return the odf text paragraph
	 * @throws Exception
	 *             the exception
	 */
	public OdfTextParagraph addSubSubHeading(final WriterDocument doc, final String text) throws Exception {
		if (doc == null)
			return null;
		return doc.addSubHeading2(text);
	}
	/**
	 * Add Page-Break to Document.
	 * 
	 * @param doc
	 *            the doc
	 * @throws Exception
	 *             the exception
	 */
	public void addBreak(final WriterDocument doc) throws Exception {
		if (doc == null)
			return;
		doc.addPageBreak();
	}
	/**
	 * Add Line-Break to Document.
	 * 
	 * @param doc
	 *            the doc
	 * @throws Exception
	 *             the exception
	 */
	public void addLineBreak(final WriterDocument doc) throws Exception {
		if (doc == null)
			return;
		doc.addTextLineBreak();
	}
	public void addImage(final WriterDocument doc,final File image, int maxImageHeight,  int maxImageWidth) throws Exception {
		if (doc == null)
			return;
		doc.addImage(image,maxImageHeight,maxImageWidth);
	}
	/**
	 * Add Table to Document.
	 * 
	 * @param doc
	 *            the doc
	 * @param title
	 *            the title
	 * @param columnLabels
	 *            the column labels
	 * @param data
	 *            the data
	 * @return the odf table
	 * @throws Exception
	 *             the exception
	 */
	public OdfTable addTable(final WriterDocument doc, final String title, final String[] columnLabels, final String[][] data) throws Exception {
		if (doc == null)
			return null;
		return doc.addTable(title, columnLabels, data);
	}
	/**
	 * Add Table to Document.
	 * 
	 * @param doc
	 *            the doc
	 * @param title
	 *            the title
	 * @param columnLabels
	 *            the column labels
	 * @param data
	 *            the data
	 * @return the odf table
	 * @throws Exception
	 *             the exception
	 */
	/**
	 * Add Table of Content to Document.
	 * 
	 * @param doc
	 *            the doc
	 * @param title
	 *            the title
	 * @throws Exception
	 *             the exception
	 */
	public TextTableOfContentElement addTOC(final WriterDocument doc, final String title) throws Exception {
		if (doc == null)
			return null;
		return doc.addTOC(title);
	}
	/**
	 * Add Table of Content to Document.
	 * 
	 * @param doc
	 *            the doc
	 * @param ref
	 *            the ref
	 * @param title
	 *            the title
	 * @return the text table of content element
	 * @throws Exception
	 *             the exception
	 */
	public TextTableOfContentElement addTOC(final WriterDocument doc, final Node ref, final String title) throws Exception {
		if (doc == null)
			return null;
		return doc.addTOC(title, ref);
	}
	public void updateTOC(final WriterDocument doc,final TextTableOfContentElement toc) throws Exception {
		if (toc == null)
			return;
		doc.updateTOC(toc);
	}
	/**
	 * Enumeration of Styles embedded in Document
	 * 
	 * @param doc
	 *            the doc
	 * @return the styles
	 * @throws Exception
	 *             the exception
	 */
	public Map<String, OdfStyle> getStyles(final WriterDocument doc) throws Exception {
		if (doc == null)
			return null;
		return doc.getStyles();
	}
}
