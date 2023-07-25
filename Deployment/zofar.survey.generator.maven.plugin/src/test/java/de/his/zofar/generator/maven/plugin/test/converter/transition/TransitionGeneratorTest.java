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
/**
 *
 */
package de.his.zofar.generator.maven.plugin.test.converter.transition;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.java.xml.ns.javaee.FacesConfigNavigationCaseType;
import de.his.zofar.generator.maven.plugin.generator.transition.TransitionGenerator;
/**
 * usage demonstrator of the TransitionGenerator.
 *
 * @author le
 *
 */
public class TransitionGeneratorTest {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TransitionGeneratorTest.class);
    /**
     * demonstrates how to generate the faces-config.xml in which zofar survey
     * describes and defines its transitions.
     */
    @Test
    @Ignore
    public void testDemonstrator() {
        final TransitionGenerator generator = new TransitionGenerator();
        generator.createDocument();
        final FacesConfigNavigationCaseType case1 = generator
                .createNavigationCase(null, "page1");
        generator.addNavigationRule("index", case1);
        final FacesConfigNavigationCaseType case2 = generator
                .createNavigationCase("v1.value==1", "page3");
        final FacesConfigNavigationCaseType case3 = generator
                .createNavigationCase("v1.value==2", "page4");
        generator.addNavigationRule("page2", case2, case3);
        LOGGER.info("\n{}", generator.getDocument());
        Assert.assertTrue(generator.validate());
    }
}
