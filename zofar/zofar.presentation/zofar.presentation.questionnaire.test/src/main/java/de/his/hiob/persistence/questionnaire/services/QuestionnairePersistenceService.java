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
package de.his.hiob.persistence.questionnaire.services;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import de.his.hiob.model.common.models.PageDTO;
import de.his.hiob.model.question.models.concrete.BooleanQuestion;
import de.his.hiob.model.question.models.concrete.MultipleChoiceQuestion;
import de.his.hiob.model.question.models.concrete.OpenQuestion;
import de.his.hiob.model.question.models.concrete.SingleChoiceQuestion;
import de.his.hiob.model.question.models.structure.StructuredElement;
import de.his.hiob.model.question.models.structure.Text;
import de.his.hiob.model.questionnaire.models.Questionnaire;
import de.his.hiob.model.questionnaire.models.QuestionnairePage;
import de.his.hiob.model.questionnaire.models.QuestionnaireQuery;
import de.his.hiob.model.questionnaire.models.Transition;
import de.his.hiob.service.question.services.QuestionService;
/**
 * @author le
 *
 */
@Service
public class QuestionnairePersistenceService {
    /**
     * the logger.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(QuestionnairePersistenceService.class);
    /**
     *
     */
    @Inject
    private QuestionService questionService;
    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.hiob.service.questionnaire.internal.QuestionnaireInternalService
     * #loadQuestionnaire(java.util.UUID)
     */
    public Questionnaire loadQuestionnaire(final UUID uuid) {
        if (!questionnaires.containsKey(uuid)) {
            throw new IllegalArgumentException(
                    "there is no questionnaire with uuid " + uuid);
        }
        return questionnaires.get(uuid);
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.hiob.service.questionnaire.internal.QuestionnaireInternalService
     * #searchAll
     * (de.his.hiob.service.questionnaire.external.dtos.QuestionnaireQueryDTO)
     */
    public PageDTO<Questionnaire> searchAll(final QuestionnaireQuery query) {
        final PageDTO<Questionnaire> result = new PageDTO<Questionnaire>();
        final List<Questionnaire> s = new ArrayList<Questionnaire>();
        s.addAll(questionnaires.values());
        result.setContent(s);
        return result;
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.hiob.service.questionnaire.internal.QuestionnaireInternalService
     * #loadPage(java.util.UUID)
     */
    @Transactional(readOnly = true)
    public QuestionnairePage loadPage(final UUID pageUUID) {
        if (!pages.containsKey(pageUUID)) {
            throw new IllegalArgumentException("there is no page with uuid "
                    + pageUUID);
        }
        return pages.get(pageUUID);
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.hiob.service.questionnaire.internal.QuestionnaireInternalService
     * #loadFirstPage
     * (de.his.hiob.service.questionnaire.external.dtos.QuestionnaireDTO)
     */
    public QuestionnairePage loadFirstPage(final Questionnaire questionnaire) {
        return pages.get(questionnaire.getFirstPageUuid());
    }
    private Map<UUID, Questionnaire> questionnaires;
    private Map<UUID, QuestionnairePage> pages;
    @PostConstruct
    private void init() {
        LOGGER.warn("You are using a MOCK!");
        questionnaires = new HashMap<UUID, Questionnaire>();
        pages = new HashMap<UUID, QuestionnairePage>();
        final Questionnaire questionnaire = createQuestionnaireOne();
        questionnaires.put(questionnaire.getUuid(), questionnaire);
    }
    /**
     * @return
     */
    private Questionnaire createQuestionnaireOne() {
        final Questionnaire questionnaire = new Questionnaire();
        final QuestionnairePage pageOne = createPageOne();
        final QuestionnairePage pageTwo = createPageTwo();
        final QuestionnairePage pageThree = createPageThree();
        final QuestionnairePage pageFour = createPageFour();
        final QuestionnairePage pageFive = createPageFive();
        final QuestionnairePage pageEnd = createEndPage();
        final Transition transitionOne = new Transition();
        transitionOne.setNextPageUuid(pageTwo.getUuid());
        transitionOne.setCondition("__gender != 1");
        pageOne.addTransition(transitionOne);
        final Transition transitionTwo = new Transition();
        transitionTwo.setNextPageUuid(pageThree.getUuid());
        transitionTwo.setCondition("__gender == 1");
        pageOne.addTransition(transitionTwo);
        final Transition transitionThree = new Transition();
        transitionThree.setNextPageUuid(pageFour.getUuid());
        transitionThree.setCondition("__likepie != 1");
        pageThree.addTransition(transitionThree);
        final Transition transitionFour = new Transition();
        transitionFour.setNextPageUuid(pageFive.getUuid());
        transitionFour.setCondition("__likepie == 1");
        pageThree.addTransition(transitionFour);
        final Transition transitionFive = new Transition();
        transitionFive.setNextPageUuid(pageFive.getUuid());
        pageFour.addTransition(transitionFive);
        final Transition transitionEnd = new Transition();
        transitionEnd.setNextPageUuid(pageEnd.getUuid());
        pageFive.addTransition(transitionEnd);
        pageTwo.addTransition(transitionEnd);
        questionnaire.addPage(pageOne);
        questionnaire.addPage(pageTwo);
        questionnaire.addPage(pageThree);
        questionnaire.addPage(pageFour);
        questionnaire.addPage(pageFive);
        pages.put(pageOne.getUuid(), pageOne);
        pages.put(pageTwo.getUuid(), pageTwo);
        pages.put(pageThree.getUuid(), pageThree);
        pages.put(pageFour.getUuid(), pageFour);
        pages.put(pageFive.getUuid(), pageFive);
        pages.put(pageEnd.getUuid(), pageEnd);
        return questionnaire;
    }
    /**
     * the gender question.
     *
     * @return
     */
    private QuestionnairePage createPageOne() {
        final QuestionnairePage page = new QuestionnairePage();
        final SingleChoiceQuestion question = (SingleChoiceQuestion) questionService
                .loadQuestion(Long.valueOf(4));
        question.getVariable().setName("gender");
        final OpenQuestion openQuestion = (OpenQuestion) questionService
                .loadQuestion(Long.valueOf(6));
        openQuestion.getVariable().setName("gender-open");
        question.getAnswerOptions().get(2).setOpenQuestion(openQuestion);
        page.addQuestion(question);
        page.setIsFirstPage(true);
        return page;
    }
    /**
     * the how many kids question.
     *
     * @return
     */
    private QuestionnairePage createPageTwo() {
        final QuestionnairePage page = new QuestionnairePage();
        final SingleChoiceQuestion question = (SingleChoiceQuestion) questionService
                .loadQuestion(Long.valueOf(1));
        question.getVariable().setName("kids");
        page.addQuestion(question);
        return page;
    }
    /**
     * the pie question.
     *
     * @return
     */
    private QuestionnairePage createPageThree() {
        final QuestionnairePage page = new QuestionnairePage();
        final SingleChoiceQuestion question = (SingleChoiceQuestion) questionService
                .loadQuestion(Long.valueOf(3));
        question.getVariable().setName("likepie");
        page.addQuestion(question);
        return page;
    }
    /**
     * the why don't like pie question.
     *
     * @return
     */
    private QuestionnairePage createPageFour() {
        final QuestionnairePage page = new QuestionnairePage();
        final OpenQuestion question = (OpenQuestion) questionService
                .loadQuestion(Long.valueOf(2));
        question.getVariable().setName("whynopie");
        page.addQuestion(question);
        return page;
    }
    /**
     * the dessert question.
     *
     * @return
     */
    private QuestionnairePage createPageFive() {
        final QuestionnairePage page = new QuestionnairePage();
        final MultipleChoiceQuestion question = (MultipleChoiceQuestion) questionService
                .loadQuestion(Long.valueOf(5));
        int index = 0;
        for (final BooleanQuestion child : question.getQuestions()) {
            child.getVariable().setName("dessert_" + index++);
            for (final StructuredElement element : child.getHeader()) {
                if (element.getContent().equals("key lime pie")) {
                    child.setVisibilityCondition("__likepie != 1");
                }
            }
        }
        page.addQuestion(question);
        return page;
    }
    /**
     * @return
     */
    private QuestionnairePage createEndPage() {
        final QuestionnairePage page = new QuestionnairePage();
        page.addHeaderElement(new Text("Ende"));
        final Text forFemale = new Text("For female only.");
        forFemale.setVisibilityCondition("__gender == 1");
        page.addHeaderElement(forFemale);
        final Text forMale = new Text("For male only.");
        forMale.setVisibilityCondition("__gender == 2");
        page.addHeaderElement(forMale);
        return page;
    }
}
