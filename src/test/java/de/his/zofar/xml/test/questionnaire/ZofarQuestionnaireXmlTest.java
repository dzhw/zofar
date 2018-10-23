/**
 *
 */
package de.his.zofar.xml.test.questionnaire;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.xml.questionnaire.PageType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import de.his.zofar.xml.questionnaire.QuestionnaireType;
import de.his.zofar.xml.questionnaire.VariableType;

/**
 * usage demonstrator test of the xml beans of the zofar questionnaire xsd.
 *
 * @author le
 *
 */
public class ZofarQuestionnaireXmlTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ZofarQuestionnaireXmlTest.class);

    private static final String RESOURCES_PATH = "src" + File.separator
            + "test" + File.separator + "resources";

    @Test
    public void test() {
        final File file = new File(RESOURCES_PATH + File.separator
                + "test-survey.xml");
        try {
            final QuestionnaireDocument document = QuestionnaireDocument.Factory
                    .parse(file);

            final QuestionnaireType questionnaire = document.getQuestionnaire();

            final VariableType[] variables = questionnaire.getVariables()
                    .getVariableArray();

            LOGGER.info("variables: {}", variables);

            for (final PageType page : questionnaire.getPageArray()) {
                LOGGER.info("page: {}", page.getUid());
            }
        } catch (XmlException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
