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
import java.net.URL;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.generator.maven.plugin.generator.variable.VariableGenerator;
import de.his.zofar.generator.maven.plugin.reader.QuestionnaireReader;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import de.his.zofar.xml.questionnaire.VariableType;
/**
 * usage demonstrator of the VariableGenerator.
 *
 * @author le
 *
 */
public class VariableGeneratorTest {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(VariableGeneratorTest.class);
    @Test
    @Ignore
    public void testSingleChoiceAnswerOptionCreation() {
        final VariableGenerator generator = new VariableGenerator();
        QuestionnaireDocument questionnaire = null;
        try {
            final URL url = VariableGeneratorTest.class
                    .getResource("/questionnaire.xml");
            final File xml = new File(url.getFile());
            questionnaire = QuestionnaireReader.getInstance().readDocument(xml,
                    true);
        } catch (final IllegalArgumentException iae) {
            LOGGER.warn("No questionnaire XML file! Create base variables file only.");
        }
        VariableType[] variables = null;
        if (questionnaire != null) {
            variables = questionnaire.getQuestionnaire().getVariables()
                    .getVariableArray();
        }
        generator.createDocument();
        if (variables != null) {
            for (final VariableType variable : variables) {
                if (variable.getType().equals(
                        VariableType.Type.SINGLE_CHOICE_ANSWER_OPTION)) {
                    generator.addSingleChoiceAnswerOption(variable,
                            questionnaire);
                }
            }
        }
        LOGGER.info("document: {}", generator.getDocument());
    }
}
