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
package tests.schema;

import java.util.Iterator;
import java.util.List;

import org.custommonkey.xmlunit.jaxp13.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXParseException;

import tests.common.AbstractVerificationTest;
import tests.common.MessageProvider;


public class SchemaValidation extends AbstractVerificationTest {

	public SchemaValidation() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void test() {
		MessageProvider.info(this,"validate document against schema...");
		final Validator validator = this.getValidator();
		if (!validator.isInstanceValid(this.getStreamSource())) {
			@SuppressWarnings("unchecked")
			List<SAXParseException> errors = (List<SAXParseException>)validator.getInstanceErrors(getStreamSource());
			MessageProvider.error(this,"validation failed : ");
			if(errors != null){
				Iterator<SAXParseException> it = errors.iterator();
				while(it.hasNext()){
					SAXParseException saxException = it.next();
					final int line = saxException.getLineNumber();
					final int column = saxException.getColumnNumber();
					MessageProvider.error(this,"[{}] {}",line+","+column,saxException.getMessage());
				}
			}
		}

	}
}
