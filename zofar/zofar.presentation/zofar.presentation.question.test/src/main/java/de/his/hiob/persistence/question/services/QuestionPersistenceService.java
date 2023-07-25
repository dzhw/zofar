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
package de.his.hiob.persistence.question.services;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import de.his.hiob.model.question.models.Question;
import de.his.hiob.model.question.models.QuestionVariable;
import de.his.hiob.model.question.models.concrete.BooleanQuestion;
import de.his.hiob.model.question.models.concrete.MultipleChoiceQuestion;
import de.his.hiob.model.question.models.concrete.OpenQuestion;
import de.his.hiob.model.question.models.concrete.SingleChoiceQuestion;
import de.his.hiob.model.question.models.structure.InstructionText;
import de.his.hiob.model.question.models.structure.QuestionText;
import de.his.hiob.service.valuetype.services.ValueTypeService;
/**
 * this class is a mock up of the QuestionInternalService and must be replaced
 * by the actual implementation of the service.
 *
 * @author le
 *
 */
@Service
public class QuestionPersistenceService {
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(QuestionPersistenceService.class);
    /**
     *
     */
    @Inject
    private ValueTypeService valueTypeService;
    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.hiob.service.question.internal.QuestionInternalService#loadQuestion
     * (java.lang.Long)
     */
    public Question loadQuestion(final Long id) {
        if (!questions.containsKey(id)) {
            throw new IllegalArgumentException("no question exist with id "
                    + id);
        }
        return questions.get(id);
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.hiob.service.question.internal.QuestionInternalService#saveQuestion
     * (de.his.hiob.model.question.entities.Question)
     */
    public Question saveQuestion(final Question question) {
        if (question.getId() == null) {
            question.setId(getNextId());
        }
        questions.put(question.getId(), question);
        return question;
    }
    /**
     * the fake db.
     */
    private final Map<Long, Question> questions =
            new HashMap<Long, Question>();
    /**
     * init the fake DB.
     */
    @PostConstruct
    private void init() {
        LOGGER.warn("You are using a MOCK!");
        createHowManyKidsQuestion();
        createWhyDontLikePieQuestion();
        createLikePieQuestion();
        createAskGenderQuestion();
        createWhichDessertQuestion();
        createGenderUnknownOpenQuestion();
    }
    /**
     * create a question about how many kids does on have.
     */
    private void createHowManyKidsQuestion() {
        final SingleChoiceQuestion question = new SingleChoiceQuestion();
        question.setId(getNextId());
        final QuestionVariable variable = new QuestionVariable();
        variable.setUuid(UUID.randomUUID().toString());
        variable.setValueType(valueTypeService
                .loadByIdentifier("integerscale1to5"));
        question.addHeaderElement(new QuestionText(
                "How many children do you have?"));
        question.setVariable(variable);
        questions.put(question.getId(), question);
    }
    /**
     * create a why don't you like pie question.
     */
    private void createWhyDontLikePieQuestion() {
        final OpenQuestion question = new OpenQuestion();
        question.setId(getNextId());
        final QuestionVariable variable = new QuestionVariable();
        variable.setUuid(UUID.randomUUID().toString());
        variable.setValueType(valueTypeService
                .loadByIdentifier("defaultemptystring"));
        question.addHeaderElement(new QuestionText("Why don't your __kids kids like pie?"));
        question.addHeaderElement(new InstructionText(
                "Please give us the reason why you don't like pie."));
        question.setVariable(variable);
        questions.put(question.getId(), question);
    }
    /**
     * creates a like pie question.
     */
    private void createLikePieQuestion() {
        final SingleChoiceQuestion question = new SingleChoiceQuestion();
        question.setId(getNextId());
        final QuestionVariable variable = new QuestionVariable();
        variable.setUuid(UUID.randomUUID().toString());
        variable.setValueType(valueTypeService
                .loadByIdentifier("yesnomaybe"));
        question.addHeaderElement(new QuestionText("Do you like pie?"));
        question.setVariable(variable);
        questions.put(question.getId(), question);
    }
    /**
     * creates a question about the gender.
     */
    private void createAskGenderQuestion() {
        final SingleChoiceQuestion question = new SingleChoiceQuestion();
        question.setId(getNextId());
        final QuestionVariable variable = new QuestionVariable();
        variable.setUuid(UUID.randomUUID().toString());
        variable.setValueType(valueTypeService
                .loadByIdentifier("gender"));
        question.addHeaderElement(new QuestionText(
                "What gender do you have?"));
        question.setVariable(variable);
        questions.put(question.getId(), question);
    }
    /**
     * creates a question about which dessert one likes.
     */
    private void createWhichDessertQuestion() {
        final MultipleChoiceQuestion question =
                new MultipleChoiceQuestion();
        question.setId(getNextId());
        final String[] desserts =
                new String[] { "cupcake", "donut", "éclair", "frozen yoghurt",
                        "gingerbread", "honeycomb", "ice cream sandwich",
                        "jelly bean", "key lime pie" };
        for (final String dessert : desserts) {
            final QuestionVariable variable = new QuestionVariable();
            variable.setUuid(UUID.randomUUID().toString());
            variable.setValueType(valueTypeService
                    .loadByIdentifier("defaultbooleantype"));
            final BooleanQuestion childQuestion =
                    new BooleanQuestion(variable);
            childQuestion.addHeaderElement(new QuestionText(dessert));
            question.addQuestion(childQuestion);
        }
        question.addHeaderElement(new QuestionText(
                "Which dessert do you like then?"));
        question.addHeaderElement(new InstructionText(
                "Please select at least one of these delicious desserts."));
        questions.put(question.getId(), question);
    }
    /**
     * creates a gender open question (answer option 'unknown').
     */
    private void createGenderUnknownOpenQuestion() {
        final OpenQuestion question = new OpenQuestion();
        question.setId(getNextId());
        final QuestionVariable variable = new QuestionVariable();
        variable.setUuid(UUID.randomUUID().toString());
        variable.setValueType(valueTypeService
                .loadByIdentifier("defaultemptystring"));
        question.setVariable(variable);
        questions.put(question.getId(), question);
    }
    /**
     * @return the next id
     */
    private Long getNextId() {
        return new Long(questions.size() + 1);
    }
}
