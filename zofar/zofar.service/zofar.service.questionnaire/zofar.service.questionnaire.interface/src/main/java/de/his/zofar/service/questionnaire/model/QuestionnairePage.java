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
package de.his.zofar.service.questionnaire.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.his.zofar.service.common.model.BaseDTO;
import de.his.zofar.service.question.model.Question;
import de.his.zofar.service.question.model.structure.StructuredElement;
import de.his.zofar.service.questionnaire.model.trigger.Trigger;

/**
 * @author le
 * 
 */
public class QuestionnairePage extends BaseDTO implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -4754695139884274002L;
    private UUID uuid;
    private boolean isFirstPage = false;
    private List<StructuredElement> header;
    private Questionnaire questionnaire;
    private List<Question> questions;
    private List<Transition> transitions;
    private List<Trigger> triggers;

    /**
     * 
     */
    public QuestionnairePage() {
        super();
        this.uuid = UUID.randomUUID();
    }

    /**
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @param uuid
     *            the uuid to set
     */
    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the isFirstPage
     */
    public boolean isIsFirstPage() {
        return isFirstPage;
    }

    /**
     * @param isFirstPage
     *            the isFirstPage to set
     */
    public void setIsFirstPage(final boolean firstPage) {
        this.isFirstPage = firstPage;
    }

    /**
     * @return the header
     */
    public final List<StructuredElement> getHeader() {
        if (header == null) {
            header = new ArrayList<StructuredElement>();
        }
        return header;
    }

    /**
     * @param element
     *            the element to add
     */
    public final void addHeaderElement(final StructuredElement element) {
        getHeader().add(element);
    }

    /**
     * @return the questionnaire
     */
    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    /**
     * @param questionnaire
     *            the questionnaire to set
     */
    public void setQuestionnaire(final Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    /**
     * @return the questions
     */
    public List<Question> getQuestions() {
        if (questions == null) {
            questions = new ArrayList<Question>();
        }
        return questions;
    }

    /**
     * @param questions
     *            the questions to set
     */
    public void setQuestions(final List<Question> questions) {
        this.questions = questions;
    }

    /**
     * @return the transitions
     */
    public List<Transition> getTransitions() {
        if (transitions == null) {
            transitions = new ArrayList<Transition>();
        }
        return transitions;
    }

    /**
     * @param transitions
     *            the transitions to set
     */
    public void setTransitions(final List<Transition> transitions) {
        this.transitions = transitions;
    }

    /**
	 * @return the triggers
	 */
	public List<Trigger> getTriggers() {
        if (triggers == null) {
        	triggers = new ArrayList<Trigger>();
        }
		return triggers;
	}

	/**
	 * @param triggers the triggers to set
	 */
	public void setTriggers(final List<Trigger> triggers) {
		this.triggers = triggers;
	}

	/**
     * @param question
     */
    public void addQuestion(final Question question) {
        if (questions == null) {
            questions = new ArrayList<Question>();
        }
        questions.add(question);
    }

    /**
     * @param transition
     */
    public void addTransition(final Transition transition) {
        if (transitions == null) {
            transitions = new ArrayList<Transition>();
        }
        transitions.add(transition);
    }
    
    /**
     * @param trigger
     */
    public void addTrigger(final Trigger trigger) {
        getTriggers().add(trigger);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final QuestionnairePage other = (QuestionnairePage) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QuestionnairePageDTO [uuid=" + uuid + "]";
    }

}
