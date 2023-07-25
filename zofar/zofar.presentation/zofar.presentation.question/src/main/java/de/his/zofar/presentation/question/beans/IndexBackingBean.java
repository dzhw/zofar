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
package de.his.zofar.presentation.question.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlPanelGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.question.components.QuestionComponentFactory;
import de.his.zofar.service.question.model.AnswerableQuestion;
import de.his.zofar.service.question.model.Question;
import de.his.zofar.service.question.model.QuestionVariable;
import de.his.zofar.service.question.model.components.AnswerOption;
import de.his.zofar.service.question.model.concrete.MatrixQuestion;
import de.his.zofar.service.question.model.concrete.MultipleChoiceQuestion;
import de.his.zofar.service.question.model.concrete.SingleChoiceQuestion;
import de.his.zofar.service.question.model.questionvalues.QuestionNumberValue;
import de.his.zofar.service.question.model.questionvalues.QuestionStringValue;
import de.his.zofar.service.question.model.questionvalues.QuestionValue;
import de.his.zofar.service.question.service.QuestionService;
import de.his.zofar.service.valuetype.model.NumberValueType;
import de.his.zofar.service.valuetype.model.StringValueType;

/**
 * @author le
 *
 */
@ManagedBean
@ViewScoped
public class IndexBackingBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -6610898277162229980L;

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(IndexBackingBean.class);

    /**
     *
     */
    private transient HtmlPanelGroup questionPanel;

    /**
     * this is the map in which the participant stores all its data.
     */
    private transient Map<String, QuestionValue> valueMap = new HashMap<String, QuestionValue>();

    @ManagedProperty(value = "#{questionComponentFactory}")
    private QuestionComponentFactory qComponentFactory;

    @ManagedProperty(value = "#{questionService}")
    private QuestionService questionService;

    /**
     * @return the questionPanel
     */
    public HtmlPanelGroup getQuestionPanel() {
        return this.questionPanel;
    }

    /**
     * creating questionPanel with questions inside.
     *
     * @return
     */
    @PostConstruct
    private void createForm() {
        this.questionPanel = new HtmlPanelGroup();

        final Question question1 = questionService.loadQuestion(new Long(1));
        findVariables(question1);
        questionPanel.getChildren().add(
                qComponentFactory.createQuestionComponent(question1));

        final Question question2 = questionService
                .loadQuestion(Long.valueOf(2));
        findVariables(question2);
        questionPanel.getChildren().add(
                qComponentFactory.createQuestionComponent(question2));

        final Question question3 = questionService
                .loadQuestion(Long.valueOf(3));
        findVariables(question3);
        questionPanel.getChildren().add(
                qComponentFactory.createQuestionComponent(question3));

        final Question question4 = questionService
                .loadQuestion(Long.valueOf(4));
        findVariables(question4);
        questionPanel.getChildren().add(
                qComponentFactory.createQuestionComponent(question4));
    }

    /**
     * @param question
     */
    private void findVariables(final Question question) {
        final Class<?> clazz = question.getClass();
        if (AnswerableQuestion.class.isAssignableFrom(clazz)) {
            fillupValueMap((QuestionVariable) ((AnswerableQuestion) question)
                    .getVariable());
            if (SingleChoiceQuestion.class.isAssignableFrom(clazz)) {
                for (final AnswerOption answerOption : ((SingleChoiceQuestion) question)
                        .getAnswerOptions()) {
                    if (answerOption.getOpenQuestion() != null) {
                        fillupValueMap((QuestionVariable) answerOption
                                .getOpenQuestion().getVariable());
                    }
                }
            }
        } else if (MultipleChoiceQuestion.class.isAssignableFrom(clazz)) {
            for (final Question childQuestion : ((MultipleChoiceQuestion) question)
                    .getQuestions()) {
                findVariables(childQuestion);
            }
        } else if (MatrixQuestion.class.isAssignableFrom(clazz)) {
            for (final Question childQuestion : ((MatrixQuestion) question)
                    .getMatrixChildren()) {
                findVariables(childQuestion);
            }
        } else {
            throw new RuntimeException("Not yet implemented! " + clazz);
        }
    }

    /**
     * @param variable
     */
    private void fillupValueMap(final QuestionVariable variable) {
        if (NumberValueType.class.isAssignableFrom(variable.getValueType()
                .getClass())) {
            valueMap.put(variable.getName(), new QuestionNumberValue());
        } else if (StringValueType.class.isAssignableFrom(variable
                .getValueType().getClass())) {
            valueMap.put(variable.getName(), new QuestionStringValue());
        } else {
            throw new RuntimeException("Not yet implemented! "
                    + variable.getValueType().getClass());
        }
    }

    /**
     * @return the valueMap
     */
    public Map<String, QuestionValue> getValueMap() {
        return this.valueMap;
    }

    public String nextPage() {
        LOGGER.debug("retrieving variables and their values from view:");
        if (this.valueMap != null) {
            for (final Entry<String, QuestionValue> entry : this.valueMap
                    .entrySet()) {
                LOGGER.debug("Variable: {}, Value: {}", entry.getKey(), entry
                        .getValue().getValue());
            }
        }
        return null;
    }

    /**
     * @param questionPanel
     *            the questionPanel to set
     */
    public void setQuestionPanel(final HtmlPanelGroup form) {
        this.questionPanel = form;
    }

    /**
     * setter for JSF to be able to inject the QuestionComponentFactory
     *
     * @param qComponentFactory
     *            the qComponentFactory to set
     */
    public void setqComponentFactory(
            final QuestionComponentFactory qComponentFactory) {
        this.qComponentFactory = qComponentFactory;
    }

    /**
     * JSF dependency injection needs this to resolve ManagedProperties.
     *
     * @param questionExternalService
     *            the service to be set
     */
    public void setQuestionService(final QuestionService questionExternalService) {
        this.questionService = questionExternalService;
    }

    /**
     * @param valueMap
     *            the valueMap to set
     */
    public void setValueMap(final Map<String, QuestionValue> variableMap) {
        this.valueMap = variableMap;
    }

}
