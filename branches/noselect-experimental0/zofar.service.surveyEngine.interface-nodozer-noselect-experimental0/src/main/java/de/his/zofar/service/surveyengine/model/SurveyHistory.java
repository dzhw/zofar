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

import java.util.Date;

import de.his.zofar.service.common.model.BaseDTO;

/**
 * @author le
 *
 */
public class SurveyHistory extends BaseDTO {

    /**
     *
     */
    private static final long serialVersionUID = 7925815229347439667L;

    /**
     *
     */
    private Participant participant;

    /**
     *
     */
    private String page;

    /**
     *
     */
    private Date timestamp;

    /**
     *
     */
    public SurveyHistory() {
        super();
    }

    /**
     * @param participant
     * @param page
     */
    public SurveyHistory(final Participant participant, final String page) {
        super();
        this.participant = participant;
        this.page = page;
    }

    /**
     * DO NOT USE in code other than in SurveyEngineServiceImpl to increase
     * performance on mapping a large collection of SurveyHistoryEntity.
     *
     * @param participant
     * @param page
     * @param timestamp
     * @param id
     * @param version
     */
    public SurveyHistory(final Participant participant, final String page,
            final Date timestamp, final Long id, final int version) {
        this.participant = participant;
        this.page = page;
        this.timestamp = timestamp;
        this.setId(id);
        this.setVersion(version);
    }

    /**
     * @return the participant
     */
    public Participant getParticipant() {
        return this.participant;
    }

    /**
     * @param participant
     *            the participant to set
     */
    public void setParticipant(final Participant participant) {
        this.participant = participant;
    }

    /**
     * @return the page
     */
    public String getPage() {
        return this.page;
    }

    /**
     * @param page
     *            the page to set
     */
    public void setPage(final String page) {
        this.page = page;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return this.timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
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
        result = prime * result + ((this.page == null) ? 0 : this.page.hashCode());
        result = prime * result
                + ((this.participant == null) ? 0 : this.participant.hashCode());
        result = prime * result
                + ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
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
        if (this.getClass() != obj.getClass())
            return false;
        final SurveyHistory other = (SurveyHistory) obj;
        if (this.page == null) {
            if (other.page != null)
                return false;
        } else if (!this.page.equals(other.page))
            return false;
        if (this.participant == null) {
            if (other.participant != null)
                return false;
        } else if (!this.participant.equals(other.participant))
            return false;
        if (this.timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!this.timestamp.equals(other.timestamp))
            return false;
        return true;
    }

}
