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
package de.his.zofar.service.questionnaire.service;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import de.his.zofar.service.common.model.PageDTO;
import de.his.zofar.service.questionnaire.model.Questionnaire;
import de.his.zofar.service.questionnaire.model.QuestionnairePage;
import de.his.zofar.service.questionnaire.model.QuestionnaireQuery;

/**
 * @author le
 *
 */
public interface QuestionnaireService {

    /**
     * loading a questionnaire by the name of the questionnaire
     *
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public abstract Questionnaire loadQuestionnaire(UUID uuid);

    /**
     * @param query
     * @return
     */
    @Transactional(readOnly = true)
    public abstract PageDTO<Questionnaire> searchAll(QuestionnaireQuery query);

    /**
     * @param pageUUID
     * @return
     */
    @Transactional(readOnly = true)
    public abstract QuestionnairePage loadPage(UUID pageUUID);

    /**
     * @param questionnaire
     * @return
     */
    @Transactional(readOnly = true)
    public abstract QuestionnairePage loadFirstPage(Questionnaire questionnaire);

}