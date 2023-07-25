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
package de.his.zofar.persistence.question.entities.concrete;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import de.his.zofar.persistence.question.entities.MatrixQuestionComponentEntity;
/**
 * @author le
 * 
 */
@Entity
public class MultipleChoiceQuestionEntity extends MatrixQuestionComponentEntity {
    @OrderColumn
    @OneToMany(mappedBy = "parentMultipleChoiceQuestion",
            cascade = CascadeType.ALL)
    private List<SingleChoiceQuestionEntity> questions;
    /**
     * 
     */
    public MultipleChoiceQuestionEntity() {
        super();
    }
    /**
     * @return the questions
     */
    public List<SingleChoiceQuestionEntity> getQuestions() {
        return questions;
    }
    /**
     * @param questions
     *            the questions to set
     */
    public void setQuestions(List<SingleChoiceQuestionEntity> questions) {
        this.questions = questions;
    }
    public void addQuestion(SingleChoiceQuestionEntity question) {
        if (this.questions == null) {
            this.questions = new ArrayList<SingleChoiceQuestionEntity>();
        }
        question.setParentMultipleChoiceQuestion(this);
        this.questions.add(question);
    }
}
