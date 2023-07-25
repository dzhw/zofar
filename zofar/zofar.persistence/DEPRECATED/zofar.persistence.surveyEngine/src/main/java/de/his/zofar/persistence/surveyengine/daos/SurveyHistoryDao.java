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
package de.his.zofar.persistence.surveyengine.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import de.his.zofar.persistence.common.daos.GenericCRUDDao;
import de.his.zofar.persistence.surveyengine.entities.ParticipantEntity;
import de.his.zofar.persistence.surveyengine.entities.QSurveyHistoryEntity;
import de.his.zofar.persistence.surveyengine.entities.SurveyHistoryEntity;

/**
 * @author le
 *
 */
public interface SurveyHistoryDao extends GenericCRUDDao<SurveyHistoryEntity> {
    public static QSurveyHistoryEntity surveyHistory = QSurveyHistoryEntity.surveyHistoryEntity;

    /*
     * Overwritten to be able to monitor run times with Jamon.
     *
     * (non-Javadoc)
     *
     * @see org.springframework.data.repository.CrudRepository#save(S)
     */
    @Override
    @SuppressWarnings("unchecked")
    public SurveyHistoryEntity save(SurveyHistoryEntity surveyHistory);

//    public List<SurveyHistoryEntity> findByParticipant(ParticipantEntity participant);
    public List<SurveyHistoryEntity> findByParticipantOrderByTimestampAsc(ParticipantEntity participant);

    @Query ("select history from SurveyHistoryEntity history WHERE history.participant=?1 AND history.timestamp >= ALL(select MAX(temp.timestamp) from SurveyHistoryEntity temp WHERE temp.participant=?1 AND temp.page LIKE '%index%')")
    public List<SurveyHistoryEntity> findCustomByParticipantOrderByTimestampAsc(ParticipantEntity participant);
}
