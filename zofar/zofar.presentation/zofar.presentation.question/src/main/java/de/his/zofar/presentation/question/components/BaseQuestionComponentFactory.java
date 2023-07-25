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
package de.his.zofar.presentation.question.components;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.service.question.model.Question;
import de.his.zofar.service.question.model.structure.InstructionText;
import de.his.zofar.service.question.model.structure.IntroductionText;
import de.his.zofar.service.question.model.structure.QuestionText;
import de.his.zofar.service.question.model.structure.StructuredElement;
/**
 * @author Reitmann
 * 
 */
abstract class BaseQuestionComponentFactory implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = -4596593972040021544L;
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    /**
     * Factory method to create the UIComponent
     * 
     * @param question
     * @return
     */
    abstract BaseQuestionComponent createQuestionComponent(Question question);
    /**
     * @param baseQuestionComponent
     * @param question
     * @return
     */
    protected BaseQuestionComponent initBaseQuestionComponent(
            final BaseQuestionComponent baseQuestionComponent,
            final Question question) {
        for (final StructuredElement element : question.getHeader()) {
            final Class<?> clazz = element.getClass();
            if (IntroductionText.class.isAssignableFrom(clazz)) {
                baseQuestionComponent
                        .addIntroduction((IntroductionText) element);
            } else if (QuestionText.class.isAssignableFrom(clazz)) {
                baseQuestionComponent
                        .addQuestionText((QuestionText) element);
            } else if (InstructionText.class.isAssignableFrom(clazz)) {
                baseQuestionComponent
                        .addInstruction((InstructionText) element);
            } else {
                throw new RuntimeException("Not yet implemented");
            }
        }
        if (!question.getVisibilityCondition().isEmpty()) {
            baseQuestionComponent.addVisibility(question
                    .getVisibilityCondition());
        }
        return baseQuestionComponent;
    }
}
