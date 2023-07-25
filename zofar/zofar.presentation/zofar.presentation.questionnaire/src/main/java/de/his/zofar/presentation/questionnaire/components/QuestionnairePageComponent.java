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
import java.io.Serializable;
import de.his.zofar.presentation.question.components.BaseQuestionComponent;
import de.his.zofar.service.question.model.structure.Text;
/**
 * this class represents the JSF view of a questionnaire page.
 *
 * @author le
 *
 */
public class QuestionnairePageComponent extends BaseQuestionComponent implements
Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -620842057096719788L;
    /**
     *
     */
    private static final String QUESTIONNAIRE_PAGE_CLASS = "questionnaire-page";
    /**
     *
     */
    public QuestionnairePageComponent() {
        super();
        setLayout("block");
        setStyleClass(QUESTIONNAIRE_PAGE_CLASS);
    }
    /**
     * @param text
     */
    void addText(final Text text) {
        addOutputText(text, "page-text");
    }
}
