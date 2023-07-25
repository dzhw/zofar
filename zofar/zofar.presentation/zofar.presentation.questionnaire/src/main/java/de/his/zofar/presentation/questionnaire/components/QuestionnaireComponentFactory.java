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
package de.his.zofar.presentation.questionnaire.components;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import de.his.zofar.presentation.question.components.QuestionComponentFactory;
import de.his.zofar.service.question.model.Question;
import de.his.zofar.service.question.model.structure.StructuredElement;
import de.his.zofar.service.question.model.structure.Text;
import de.his.zofar.service.questionnaire.model.QuestionnairePage;
/**
 * Factory for QuestionnairePage.
 *
 * @author le
 *
 */
@ManagedBean
@ApplicationScoped
public class QuestionnaireComponentFactory {
    /**
     *
     */
    @ManagedProperty(value = "#{questionComponentFactory}")
    private QuestionComponentFactory questionComponentFactory;
    /**
     * @param questionComponentFactory
     *            the questionComponentFactory to set
     */
    public void setQuestionComponentFactory(
            final QuestionComponentFactory questionComponentFactory) {
        this.questionComponentFactory = questionComponentFactory;
    }
    /**
     * creates the UIComponent for a questionnaire page with all its attributes
     * and questions.
     *
     * @param page
     *            the page to create the UIComponent from
     * @return the UIComponent
     */
    public QuestionnairePageComponent createQuestionnaireComponent(
            final QuestionnairePage page) {
        final QuestionnairePageComponent component =
                new QuestionnairePageComponent();
        for (final StructuredElement element : page.getHeader()) {
            if (Text.class.isAssignableFrom(element.getClass())) {
                component.addText((Text) element);
            } else {
                throw new RuntimeException("Not yet implemented");
            }
        }
        for (final Question question : page.getQuestions()) {
            component.getChildren().add(
                    questionComponentFactory.createQuestionComponent(question));
        }
        return component;
    }
}
