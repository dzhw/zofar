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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.xmlbeans.XmlException;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.generator.maven.plugin.generator.page.ZofarWebPage;
import de.his.zofar.xml.questionnaire.PageType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
/**
 * @author le
 *
 */
public class PageGeneratorTest {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PageGeneratorTest.class);
    @Test
    @Ignore
    public void testReadFromXml() {
        final URL url = PageGeneratorTest.class
                .getResource("/questionnaire.xml");
        try {
            final QuestionnaireDocument document = QuestionnaireDocument.Factory
                    .parse(new File(url.getFile()));
            for (final PageType xmlpage : document.getQuestionnaire()
                    .getPageArray()) {
                final ZofarWebPage xhtmlPage = new ZofarWebPage(
                        xmlpage.getUid());
                xhtmlPage.addPageContentRecursively(xmlpage);
                LOGGER.info("page {}: {}", xmlpage.getUid(), xhtmlPage);
            }
        } catch (XmlException | IOException e) {
            e.printStackTrace();
        }
    }
}
