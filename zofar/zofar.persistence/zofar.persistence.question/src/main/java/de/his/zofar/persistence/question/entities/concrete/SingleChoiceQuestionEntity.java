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
package de.his.zofar.persistence.question.entities.concrete;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import de.his.zofar.persistence.question.entities.AnswerableQuestionEntity;
import de.his.zofar.persistence.question.entities.components.AnswerOptionEntity;

@Entity
public class SingleChoiceQuestionEntity extends AnswerableQuestionEntity {
    @OrderColumn
    @OneToMany(mappedBy = "question", cascade = ALL)
    private List<AnswerOptionEntity> answerOptions;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private MultipleChoiceQuestionEntity parentMultipleChoiceQuestion;

    public SingleChoiceQuestionEntity() {
        super();
    }

    public List<AnswerOptionEntity> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<AnswerOptionEntity> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public void addAnswerOption(AnswerOptionEntity answerOption) {
        if (this.answerOptions == null) {
            this.answerOptions = new ArrayList<AnswerOptionEntity>();
        }
        answerOption.setQuestion(this);
        this.answerOptions.add(answerOption);
    }

    /**
     * @return the parentMultipleChoiceQuestion
     */
    public MultipleChoiceQuestionEntity getParentMultipleChoiceQuestion() {
        return parentMultipleChoiceQuestion;
    }

    /**
     * @param parentMultipleChoiceQuestion
     *            the parentMultipleChoiceQuestion to set
     */
    public void setParentMultipleChoiceQuestion(
            MultipleChoiceQuestionEntity parentMultipleChoiceQuestion) {
        this.parentMultipleChoiceQuestion = parentMultipleChoiceQuestion;
    }
}
