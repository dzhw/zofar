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
package eu.dzhw.zofar.management.calc.geo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
public class ZeroResult implements XmlObject,Serializable {
	private static final long serialVersionUID = -8728607495536391900L;
	public ZeroResult() {
		super();
	}
	@Override
	public XmlDocumentProperties documentProperties() {
		return null;
	}
	@Override
	public void dump() {
	}
	@Override
	public Node getDomNode() {
		return null;
	}
	@Override
	public Object monitor() {
		return null;
	}
	@Override
	public XmlCursor newCursor() {
		return null;
	}
	@Override
	public Node newDomNode() {
		return null;
	}
	@Override
	public Node newDomNode(XmlOptions arg0) {
		return null;
	}
	@Override
	public InputStream newInputStream() {
		return null;
	}
	@Override
	public InputStream newInputStream(XmlOptions arg0) {
		return null;
	}
	@Override
	public Reader newReader() {
		return null;
	}
	@Override
	public Reader newReader(XmlOptions arg0) {
		return null;
	}
	@Override
	public XMLInputStream newXMLInputStream() {
		return null;
	}
	@Override
	public XMLInputStream newXMLInputStream(XmlOptions arg0) {
		return null;
	}
	@Override
	public XMLStreamReader newXMLStreamReader() {
		return null;
	}
	@Override
	public XMLStreamReader newXMLStreamReader(XmlOptions arg0) {
		return null;
	}
	@Override
	public void save(File arg0) throws IOException {
	}
	@Override
	public void save(OutputStream arg0) throws IOException {
	}
	@Override
	public void save(Writer arg0) throws IOException {
	}
	@Override
	public void save(ContentHandler arg0, LexicalHandler arg1) throws SAXException {
	}
	@Override
	public void save(File arg0, XmlOptions arg1) throws IOException {
	}
	@Override
	public void save(OutputStream arg0, XmlOptions arg1) throws IOException {
	}
	@Override
	public void save(Writer arg0, XmlOptions arg1) throws IOException {
	}
	@Override
	public void save(ContentHandler arg0, LexicalHandler arg1, XmlOptions arg2) throws SAXException {
	}
	@Override
	public String xmlText() {
		return null;
	}
	@Override
	public String xmlText(XmlOptions arg0) {
		return null;
	}
	@Override
	public XmlObject changeType(SchemaType arg0) {
		return null;
	}
	@Override
	public int compareTo(Object arg0) {
		return 0;
	}
	@Override
	public int compareValue(XmlObject arg0) {
		return 0;
	}
	@Override
	public XmlObject copy() {
		return null;
	}
	@Override
	public XmlObject[] execQuery(String arg0) {
		return null;
	}
	@Override
	public XmlObject[] execQuery(String arg0, XmlOptions arg1) {
		return null;
	}
	@Override
	public boolean isImmutable() {
		return false;
	}
	@Override
	public boolean isNil() {
		return false;
	}
	@Override
	public SchemaType schemaType() {
		return null;
	}
	@Override
	public XmlObject selectAttribute(QName arg0) {
		return null;
	}
	@Override
	public XmlObject selectAttribute(String arg0, String arg1) {
		return null;
	}
	@Override
	public XmlObject[] selectAttributes(QNameSet arg0) {
		return null;
	}
	@Override
	public XmlObject[] selectChildren(QName arg0) {
		return null;
	}
	@Override
	public XmlObject[] selectChildren(QNameSet arg0) {
		return null;
	}
	@Override
	public XmlObject[] selectChildren(String arg0, String arg1) {
		return null;
	}
	@Override
	public XmlObject[] selectPath(String arg0) {
		return null;
	}
	@Override
	public XmlObject[] selectPath(String arg0, XmlOptions arg1) {
		return null;
	}
	@Override
	public XmlObject set(XmlObject arg0) {
		return null;
	}
	@Override
	public void setNil() {
	}
	@Override
	public XmlObject substitute(QName arg0, SchemaType arg1) {
		return null;
	}
	@Override
	public boolean validate() {
		return false;
	}
	@Override
	public boolean validate(XmlOptions arg0) {
		return false;
	}
	@Override
	public boolean valueEquals(XmlObject arg0) {
		return false;
	}
	@Override
	public int valueHashCode() {
		return 0;
	}
	@Override
	public XmlObject copy(XmlOptions arg0) {
		return null;
	}
}
