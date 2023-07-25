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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import de.his.zofar.persistence.common.entities.BaseEntity;

/**
 * @author le
 *
 */
@Entity
@Table(name = "surveyhistory", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "participant_id", "page", "timestamp" }) })
@SequenceGenerator(initialValue = 1, allocationSize = 50, name = "primary_key_generator", sequenceName = "SEQ_SURVEYHISTORY_ID")
public class SurveyHistoryEntity extends BaseEntity {

    /**
     *
     */
    @ManyToOne(optional = false)
    private ParticipantEntity participant;

    /**
     *
     */
    @Column(nullable = false)
    private String page;

    /**
     *
     */
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    /**
     *
     */
    public SurveyHistoryEntity() {
        super();
    }

    /**
     * @param participant
     * @param page
     */
    public SurveyHistoryEntity(final ParticipantEntity participant,
            final String page) {
        super();
        this.participant = participant;
        this.page = page;
    }

    /**
     * setting time stamp before persisting to db
     */
    @PrePersist
    private void setTimestamp() {
        this.timestamp = new Date();
    }

    /**
     * @return the participant
     */
    public ParticipantEntity getParticipant() {
        return this.participant;
    }

    /**
     * @param participant
     *            the participant to set
     */
    public void setParticipant(final ParticipantEntity participant) {
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
        final SurveyHistoryEntity other = (SurveyHistoryEntity) obj;
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
