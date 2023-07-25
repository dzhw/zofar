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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.his.zofar.persistence.common.entities.BaseEntity;

/**
 * @author le
 *
 */
@Entity
@Table(name = "surveydata", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "participant_id", "variablename" }) })
@SequenceGenerator(initialValue = 1, allocationSize = 50, name = "primary_key_generator", sequenceName = "SEQ_SURVEYDATA_ID")
public class SurveyDataEntity extends BaseEntity {

    /**
     *
     */
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    private ParticipantEntity participant;

    /**
     *
     */
    @Column(nullable = false)
    private String variableName;

    /**
     *
     */
    @Column(nullable = false, length=2000)
    private String value;

    /**
     *
     */
    public SurveyDataEntity() {
        super();
    }

    /**
     * @param participant
     * @param variableName
     * @param value
     */
    public SurveyDataEntity(final ParticipantEntity participant,
            final String variableName, final String value) {
        super();
        this.participant = participant;
        this.variableName = variableName;
        this.value = value;
    }

    /**
     * @return the participant
     */
    public ParticipantEntity getParticipant() {
        return participant;
    }

    /**
     * @param participant
     *            the participant to set
     */
    public void setParticipant(final ParticipantEntity participant) {
        this.participant = participant;
    }

    /**
     * @return the variableName
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * @param variableName
     *            the variableName to set
     */
    public void setVariableName(final String variableName) {
        this.variableName = variableName;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(final String value) {
        this.value = value;
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
                + ((participant == null) ? 0 : participant.hashCode());
        result = prime * result
                + ((variableName == null) ? 0 : variableName.hashCode());
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
        final SurveyDataEntity other = (SurveyDataEntity) obj;
        if (participant == null) {
            if (other.participant != null)
                return false;
        } else if (!participant.equals(other.participant))
            return false;
        if (variableName == null) {
            if (other.variableName != null)
                return false;
        } else if (!variableName.equals(other.variableName))
            return false;
        return true;
    }

}
