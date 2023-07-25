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
package eu.dzhw.zofar.management.comm.network.webservices;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.dzhw.zofar.management.comm.network.http.HTTPClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
public class W3CMarkupValidatorService {
	/** The Constant INSTANCE. */
	private static final W3CMarkupValidatorService INSTANCE = new W3CMarkupValidatorService();
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(W3CMarkupValidatorService.class);
	/**
	 * The Enum TYPE.
	 */
	public enum TYPE {
		data, messages
	};
	public class W3CMessage {
		final int firstColumn;
		final int firstRow;
		final int lastColumn;
		final int lastRow;
		final String type;
		final String message;
		final Object source;
		public W3CMessage(int firstColumn, int firstRow, int lastColumn, int lastRow, String type, String message, Object source) {
			super();
			this.firstColumn = firstColumn;
			this.firstRow = firstRow;
			this.lastColumn = lastColumn;
			this.lastRow = lastRow;
			this.type = type;
			this.message = message;
			this.source = source;
		}
		public int getFirstColumn() {
			return firstColumn;
		}
		public int getFirstRow() {
			return firstRow;
		}
		public int getLastColumn() {
			return lastColumn;
		}
		public int getLastRow() {
			return lastRow;
		}
		public String getType() {
			return type;
		}
		public String getMessage() {
			return message;
		}
		public Object getSource() {
			return source;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + firstColumn;
			result = prime * result + firstRow;
			result = prime * result + lastColumn;
			result = prime * result + lastRow;
			result = prime * result + ((message == null) ? 0 : message.hashCode());
			result = prime * result + ((source == null) ? 0 : source.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			W3CMessage other = (W3CMessage) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (firstColumn != other.firstColumn) {
				return false;
			}
			if (firstRow != other.firstRow) {
				return false;
			}
			if (lastColumn != other.lastColumn) {
				return false;
			}
			if (lastRow != other.lastRow) {
				return false;
			}
			if (message == null) {
				if (other.message != null) {
					return false;
				}
			} else if (!message.equals(other.message)) {
				return false;
			}
			if (source == null) {
				if (other.source != null) {
					return false;
				}
			} else if (!source.equals(other.source)) {
				return false;
			}
			if (type == null) {
				if (other.type != null) {
					return false;
				}
			} else if (!type.equals(other.type)) {
				return false;
			}
			return true;
		}
		private W3CMarkupValidatorService getOuterType() {
			return W3CMarkupValidatorService.this;
		}
		@Override
		public String toString() {
			return "W3CMessage [firstColumn=" + firstColumn + ", firstRow=" + firstRow + ", lastColumn=" + lastColumn + ", lastRow=" + lastRow + ", " + (type != null ? "type=" + type + ", " : "") + (message != null ? "message=" + message + ", " : "") + (source != null ? "source=" + source : "") + "]";
		}
	}
	/**
	 * Instantiates a new HTTP client.
	 */
	private W3CMarkupValidatorService() {
		super();
	}
	public static W3CMarkupValidatorService getInstance() {
		return INSTANCE;
	}
	private class CustomContentHandler implements org.xml.sax.ContentHandler {
		private XMLReader xmlReader;
		public CustomContentHandler(XMLReader xmlReader) {
			this.xmlReader = xmlReader;
		}
		public void setDocumentLocator(Locator locator) {
		}
		public void startDocument() throws SAXException {
		}
		public void endDocument() throws SAXException {
		}
		public void startPrefixMapping(String prefix, String uri) throws SAXException {
		}
		public void endPrefixMapping(String prefix) throws SAXException {
		}
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		}
		public void endElement(String uri, String localName, String qName) throws SAXException {
		}
		public void characters(char[] ch, int start, int length) throws SAXException {
			System.out.println(new String(ch, start, length));
		}
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		}
		public void processingInstruction(String target, String data) throws SAXException {
		}
		public void skippedEntity(String name) throws SAXException {
		}
	};
	public Map<TYPE, Object> validate(final Page page) {
		try {
			WebResponse response = page.getWebResponse();
			String body = response.getContentAsString();
			if (body.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))
				body = body.substring("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length());
			final Map<String, String> parameter = new HashMap<String, String>();
			parameter.put("Content-Type", "text/html; charset=UTF-8");
			Page resultPage = HTTPClient.getInstance().post("http://vmtest.####:8080/vnu/?out=json&parser=htm5", parameter, body);
			if (resultPage != null) {
				final Map<TYPE, Object> back = new LinkedHashMap<TYPE, Object>();
				back.put(TYPE.data, body);
				final String result = resultPage.getWebResponse().getContentAsString();
				Gson gson = new Gson();
				JsonObject json = gson.fromJson(result, JsonObject.class);
				JsonElement messages = json.get("messages");
				if (messages != null) {
					final ArrayList<W3CMessage> messageList = new ArrayList<W3CMessage>();
					final JsonArray msgArray = messages.getAsJsonArray();
					if (msgArray != null) {
						Iterator<JsonElement> it = msgArray.iterator();
						while (it.hasNext()) {
							final JsonObject msg = (JsonObject) it.next();
							final JsonElement type = msg.get("type");
							final JsonElement message = msg.get("message");
							final JsonElement found = msg.get("extract");
							if (message.getAsString().startsWith("Malformed byte sequence:"))
								continue;
							JsonElement locationFirstLine = msg.get("firstLine");
							JsonElement locationFirstColumn = msg.get("firstColumn");
							final JsonElement locationLastLine = msg.get("lastLine");
							final JsonElement locationLastColumn = msg.get("lastColumn");
							if (locationFirstLine == null)
								locationFirstLine = locationLastLine;
							if (locationFirstColumn == null)
								locationFirstColumn = locationLastColumn;
							final W3CMessage tmpMsg = new W3CMessage(locationFirstColumn.getAsInt(), locationFirstLine.getAsInt(), locationLastColumn.getAsInt(), locationLastLine.getAsInt(), type.getAsString(), message.getAsString(), found);
							messageList.add(tmpMsg);
						}
					}
					back.put(TYPE.messages, messageList);
				}
				return back;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
	// HTTPClient.getInstance().post("https://validator.w3.org/nu/?out=json&parser=htm5",
}
