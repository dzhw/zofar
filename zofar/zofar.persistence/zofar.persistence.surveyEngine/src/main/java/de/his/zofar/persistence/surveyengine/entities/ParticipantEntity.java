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
package de.his.zofar.persistence.surveyengine.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import de.his.zofar.persistence.common.entities.BaseEntity;

/**
 * @author le
 * 
 */
@Entity
@Table(name = "participant")
@SequenceGenerator(initialValue = 1, allocationSize = 1,
        name = "primary_key_generator", sequenceName = "SEQ_PARTICIPANT_ID")
public class ParticipantEntity extends BaseEntity {

    /**
     *
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     *
     */
    @Column(nullable = false)
    private String password;

    /**
     *
     */
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    @MapKey(name = "variableName")
    private Map<String, SurveyDataEntity> surveyData = new HashMap<String, SurveyDataEntity>();

    /**
     *
     */
    @OneToMany(mappedBy = "participant")
    private List<SurveyHistoryEntity> surveyHistory;

    /**
     *
     */
    public ParticipantEntity() {
        super();
    }

    /**
     * @param token
     * @param password
     */
    public ParticipantEntity(final String token, final String password) {
        super();
        this.token = token;
        this.password = password;
    }

    /**
     * @param data
     */
    public void addSurveyData(final SurveyDataEntity data) {
        if (this.surveyData == null) {
            this.surveyData = new HashMap<String, SurveyDataEntity>();
        }
        this.surveyData.put(data.getVariableName(), data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ParticipantEntity other = (ParticipantEntity) obj;
        if (this.token == null) {
            if (other.token != null) {
                return false;
            }
        } else if (!this.token.equals(other.token)) {
            return false;
        }
        return true;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @return the surveyData
     */
    public Map<String, SurveyDataEntity> getSurveyData() {
        return this.surveyData;
    }

    /**
     * @return the surveyHistory
     */
    public List<SurveyHistoryEntity> getSurveyHistory() {
        return this.surveyHistory;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return this.token;
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
        result = prime * result
                + ((this.token == null) ? 0 : this.token.hashCode());
        return result;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * @param surveyData
     *            the surveyData to set
     */
    public void setSurveyData(final Map<String, SurveyDataEntity> surveyData) {
        this.surveyData = surveyData;
    }

    /**
     * @param surveyHistory
     *            the surveyHistory to set
     */
    public void setSurveyHistory(final List<SurveyHistoryEntity> surveyHistory) {
        this.surveyHistory = surveyHistory;
    }

    /**
     * @param token
     *            the token to set
     */
    public void setToken(final String token) {
        this.token = token;
    }

}
