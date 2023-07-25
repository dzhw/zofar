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

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.his.zofar.service.question.model.Question;
import de.his.zofar.service.question.model.concrete.MultipleChoiceQuestion;
import de.his.zofar.service.question.model.concrete.OpenQuestion;
import de.his.zofar.service.question.model.concrete.SingleChoiceQuestion;

/**
 * The Factory for all question components.
 *
 * @author le
 *
 */
@ManagedBean
@ApplicationScoped
public class QuestionComponentFactory implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4742292935264676803L;

    public BaseQuestionComponent createQuestionComponent(
            final Question question) {
        if (question == null) {
            throw new IllegalArgumentException(
                    "Cannot create question component for null question");
        }
        final Class<?> clazz = question.getClass();
        BaseQuestionComponentFactory factory;
        if (OpenQuestion.class.isAssignableFrom(clazz)) {
            factory = OpenQuestionComponentFactory.getInstance();
        } else if (SingleChoiceQuestion.class.isAssignableFrom(clazz)) {
            factory = SingleChoiceQuestionComponentFactory.getInstance();
        } else if (MultipleChoiceQuestion.class.isAssignableFrom(clazz)) {
            factory = MultipleChoiceQuestionComponentFactory.getInstance();
        } else {
            throw new RuntimeException("Not yet implemented :-(");
        }
        return factory.createQuestionComponent(question);
    }
}
