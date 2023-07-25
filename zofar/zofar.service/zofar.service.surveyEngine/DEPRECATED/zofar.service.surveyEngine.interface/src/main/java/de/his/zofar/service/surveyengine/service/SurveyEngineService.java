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
package de.his.zofar.service.surveyengine.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import de.his.zofar.service.surveyengine.model.Participant;
import de.his.zofar.persistence.surveyengine.entities.ParticipantEntity;
import de.his.zofar.service.surveyengine.model.SurveyHistory;

/**
 * @author le
 *
 */
public interface SurveyEngineService {
    @Transactional(readOnly = true)
    public List<SurveyHistory> loadHistory(Participant participant);

    @Transactional(readOnly = true)
    public String loadRandomViewTransition(Participant participant,
            String fromViewId);

    @Transactional(readOnly = true)
    public Participant loadParticipant(String token);

    @Transactional(readOnly = true)
    public Map<String, List<String>> loadSortings(Participant participant);

    @Transactional
    public void saveHistory(SurveyHistory history);

    @Transactional
    public Participant saveParticipant(Participant participant);
    
    @Transactional
    public Participant saveSurveyParticipant(Participant participant);
    
    @Transactional
    public Participant createAnonymousParticipant();
    
    @Transactional
    public Participant createParticipant(final String token,final String password);
    
    @Transactional
    public Participant createParticipant(final String token,final String password,final Map<String,String> preloads);

    @Transactional
    public void saveRandomViewTransition(Participant participant,
            String fromViewId, String toViewId);

    @Transactional
    public void saveSorting(String parentUID, List<String> childrenUIDs,
            Participant participant);

    @Transactional(readOnly = true)
    public Map<String, String> loadLabelsAndConditions(String variable,
            String answerOptionUid);
    
    @Transactional(readOnly = true)
    public List<ParticipantEntity> exportParticipants();
}
