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

import de.his.zofar.service.question.model.Question;
import de.his.zofar.service.question.model.concrete.MultipleChoiceQuestion;

/**
 * @author le
 * 
 */
class MultipleChoiceQuestionComponentFactory extends
        BaseQuestionComponentFactory {

    /**
     * 
     */
    private static final long serialVersionUID = -2520122436888203959L;
    private static MultipleChoiceQuestionComponentFactory INSTANCE = null;

    static MultipleChoiceQuestionComponentFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MultipleChoiceQuestionComponentFactory();
        }
        return INSTANCE;
    }

    private MultipleChoiceQuestionComponentFactory() {
        super();
    }

    @Override
    BaseQuestionComponent createQuestionComponent(final Question question) {
        final MultipleChoiceQuestionComponent component = new MultipleChoiceQuestionComponent();
        initBaseQuestionComponent(component, question);

        component
                .addSingleChoiceQuestions(((MultipleChoiceQuestion) question)
                        .getQuestions());

        return component;
    }
}
