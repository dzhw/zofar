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
package de.his.zofar.service.surveyengine.model;

import java.util.HashMap;
import java.util.Map;

import de.his.zofar.persistence.surveyengine.entities.ParticipantEntity;
import de.his.zofar.service.common.mapper.annotations.DTOEntityMapping;
import de.his.zofar.service.common.model.BaseDTO;

/**
 * @author le
 *
 */
@DTOEntityMapping(entity = ParticipantEntity.class)
public class Participant extends BaseDTO {

    /**
     *
     */
    private static final long serialVersionUID = -5824214133884993118L;

    /**
     *
     */
    private String token;

    /**
     *
     */
    private String password;

    /**
     *
     */
    private Map<String, SurveyData> surveyData = new HashMap<String, SurveyData>();

    /**
     *
     */
    public Participant() {
        super();
    }

    /**
     * @param token
     * @param password
     */
    public Participant(final String token, final String password) {
        super();
        this.token = token;
        this.password = password;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token
     *            the token to set
     */
    public void setToken(final String token) {
        this.token = token;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * @return the surveyData
     */
    public Map<String, SurveyData> getSurveyData() {
        return surveyData;
    }

    /**
     * @param surveyData
     *            the surveyData to set
     */
    public void setSurveyData(final Map<String, SurveyData> surveyData) {
        this.surveyData = surveyData;
    }

    public void addSurveyData(final SurveyData data) {
        if (surveyData == null) {
            surveyData = new HashMap<String, SurveyData>();
        }
        surveyData.put(data.getVariableName(), data);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    public String getPassword() {
        return password;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.security.core.userdetails.UserDetails#getUsername()
     */
    public String getUsername() {
        return token;
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
        result = prime * result + ((token == null) ? 0 : token.hashCode());
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
        final Participant other = (Participant) obj;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
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
        return "Participant [token=" + token + "]";
    }

}
