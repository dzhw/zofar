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
import de.his.zofar.service.question.model.concrete.OpenQuestion;
/**
 * A Factory that creates a UIComponent for an open question with all its
 * properties.
 * 
 * Note: This class must not be used other than in the QuestionComponentFactory.
 * 
 * @author le
 * 
 */
class OpenQuestionComponentFactory extends BaseQuestionComponentFactory {
    /**
     * 
     */
    private static final long serialVersionUID = -807296991282014465L;
    private static OpenQuestionComponentFactory INSTANCE = null;
    static OpenQuestionComponentFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OpenQuestionComponentFactory();
        }
        return INSTANCE;
    }
    private OpenQuestionComponentFactory() {
        super();
    }
    /**
     * Factory method to create the UIComponent
     * 
     * @param question
     * @return
     */
    @Override
    BaseQuestionComponent createQuestionComponent(final Question question) {
        final OpenQuestionComponent component = new OpenQuestionComponent();
        initBaseQuestionComponent(component, question);
        component.addInputText(((OpenQuestion) question).getVariable());
        return component;
    }
}
