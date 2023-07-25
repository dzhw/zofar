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
package de.his.zofar.service.questionnaire.internal;

import java.util.UUID;

import org.springframework.stereotype.Service;

import de.his.zofar.service.common.exceptions.NotYetImplementedException;
import de.his.zofar.service.common.internal.InternalServiceInterface;
import de.his.zofar.service.common.model.PageDTO;
import de.his.zofar.service.questionnaire.model.Questionnaire;
import de.his.zofar.service.questionnaire.model.QuestionnairePage;
import de.his.zofar.service.questionnaire.model.QuestionnaireQuery;

/**
 * @author le
 *
 */
@Service
public class QuestionnaireInternalService implements InternalServiceInterface {

    /**
     * loading a questionnaire by the name of the questionnaire.
     *
     * @param uuid
     *            the questionnaires uuid
     * @return the questionnaire identified by the uuid
     */
    public Questionnaire loadQuestionnaire(final UUID uuid) {
        throw new NotYetImplementedException();
    }

    /**
     * search for questionnaires
     *
     * @param query
     * @return
     */
    public PageDTO<Questionnaire> searchAll(final QuestionnaireQuery query) {
        throw new NotYetImplementedException();
    }

    /**
     * @param pageUUID
     * @return
     */
    public QuestionnairePage loadPage(final UUID pageUUID) {
        throw new NotYetImplementedException();
    }

    /**
     * @param questionnaire
     * @return
     */
    public QuestionnairePage loadFirstPage(final Questionnaire questionnaire) {
        throw new NotYetImplementedException();
    }

}
