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
package de.his.zofar.persistence.question.entities.components;

import static javax.persistence.CascadeType.ALL;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import de.his.zofar.persistence.common.entities.BaseEntity;
import de.his.zofar.persistence.question.entities.concrete.OpenQuestionEntity;
import de.his.zofar.persistence.question.entities.concrete.SingleChoiceQuestionEntity;
import de.his.zofar.persistence.question.entities.questionvalues.QuestionValueEntity;

@Entity
@SequenceGenerator(initialValue = 1, name = "primary_key_generator",
        sequenceName = "SEQ_ANSWEROPTION_ID")
public class AnswerOptionEntity extends BaseEntity {

    private String displayText;

    @ManyToOne(cascade = ALL)
    private SingleChoiceQuestionEntity question;

    @OneToOne(cascade = ALL)
    private OpenQuestionEntity openQuestion;

    @OneToOne(cascade = CascadeType.PERSIST)
    private QuestionValueEntity value;

    public AnswerOptionEntity() {
        super();
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public SingleChoiceQuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(SingleChoiceQuestionEntity question) {
        this.question = question;
    }

    public OpenQuestionEntity getOpenQuestion() {
        return openQuestion;
    }

    public void setOpenQuestion(OpenQuestionEntity open) {
        this.openQuestion = open;
    }

    /**
     * @return the value
     */
    public QuestionValueEntity getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(QuestionValueEntity value) {
        this.value = value;
    }
}
