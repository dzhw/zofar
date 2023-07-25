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
package main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestSuite;
import tests.integrity.EpisodeIntegrity;
import tests.integrity.IdIntegrity;
import tests.integrity.MixedMatrixIntegrity;
import tests.integrity.OpenFieldIntegrity;
import tests.integrity.PageIntegrity;
import tests.integrity.QuestionIntegrity;
import tests.integrity.VariableIntegrity;
import tests.schema.SchemaValidation;

@RunWith(Suite.class)
@SuiteClasses({SchemaValidation.class,VariableIntegrity.class,IdIntegrity.class,QuestionIntegrity.class,PageIntegrity.class,OpenFieldIntegrity.class,MixedMatrixIntegrity.class,EpisodeIntegrity.class})
public class AllTests extends TestSuite{
	final static Logger LOGGER = LoggerFactory.getLogger(AllTests.class);

	@BeforeClass 
    public static void setUpClass() {     
		if(System.getProperty("suite") == null)System.setProperty("suite", "true");
		if(System.getProperty("runMode") == null)System.setProperty("runMode", "junit");
    }

    @AfterClass 
    public static void tearDownClass() { 
    	System.clearProperty("suite");
		System.clearProperty("runMode"); 
    }
}
