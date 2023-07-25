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
package de.his.zofar.generator.maven.plugin.test.reader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.xmlbeans.XmlException;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceAnswerOptionType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import de.his.zofar.xml.questionnaire.TextResponseOptionType;
import de.his.zofar.xml.questionnaire.VariableType;
/**
 * @author le
 *
 */
public final class QuestionnaireReaderTest {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(QuestionnaireReaderTest.class);
    @Test
    @Ignore
    public void test() {
        QuestionnaireDocument document = null;
        final URL url = QuestionnaireReaderTest.class
                .getResource("/questionnaire.xml");
        try {
            document = QuestionnaireDocument.Factory.parse(new File(url
                    .getFile()));
        } catch (final XmlException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        if (document == null) {
            throw new RuntimeException();
        }
        final String namespace = "declare namespace z='http://www####/zofar/xml/questionnaire'";
        for (final VariableType variable : document.getQuestionnaire()
                .getVariables().getVariableArray()) {
            if (variable.getType().equals(
                    VariableType.Type.SINGLE_CHOICE_ANSWER_OPTION)) {
                LOGGER.info("single choice variable: {}", variable.getName());
                final String expression = namespace + " "
                        + "
                        + "']
                final Object[] result = document.selectPath(expression);
                for (final Object child : result) {
                    final QuestionSingleChoiceAnswerOptionType option = (QuestionSingleChoiceAnswerOptionType) child;
                    String label = "";
                    if (option.getLabelArray().length > 0) {
                        for (final TextResponseOptionType l : option
                                .getLabelArray()) {
                            label += l.toString() + " ";
                        }
                    } else {
                        label = option.getLabel2();
                    }
                    LOGGER.info("uid: {}, label: {}, value: {}", new Object[] {
                            option.getUid(), label, option.getValue() });
                }
            }
        }
    }
}
