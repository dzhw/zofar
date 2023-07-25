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
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKeyColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.his.zofar.persistence.common.entities.BaseEntity;

/**
 * @author le
 *
 */
@Entity
@Table(name = "answeroption", uniqueConstraints = @UniqueConstraint(columnNames = {
        "answeroptionuid", "variablename" }))
@SequenceGenerator(initialValue = 1, allocationSize = 1, name = "primary_key_generator", sequenceName = "SEQ_ANSWEROPTION_ID")
public class AnswerOptionEntity extends BaseEntity {

    @Column(nullable = false)
    private String answerOptionUid;

    @Column(nullable = false)
    private String variableName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "answeroption_conditionkey")
    @MapKeyColumn(name = "visiblecondition")
    @Column(name = "resourcekey")
    private Map<String, String> conditionedResourceKeys;

    /**
     *
     */
    public AnswerOptionEntity() {
        super();
    }

    /**
     * @param answerOptionUid
     * @param variableName
     */
    public AnswerOptionEntity(final String answerOptionUid,
            final String variableName) {
        super();
        this.answerOptionUid = answerOptionUid;
        this.variableName = variableName;
    }

    /**
     * @return the answerOptionUid
     */
    public String getAnswerOptionUid() {
        return this.answerOptionUid;
    }

    /**
     * @param answerOptionUid
     *            the answerOptionUid to set
     */
    public void setAnswerOptionUid(final String answerOptionUid) {
        this.answerOptionUid = answerOptionUid;
    }

    /**
     * @return the variableName
     */
    public String getVariableName() {
        return this.variableName;
    }

    /**
     * @param variableName
     *            the variableName to set
     */
    public void setVariableName(final String variableName) {
        this.variableName = variableName;
    }

    /**
     * @return the conditionedResourceKeys
     */
    public Map<String, String> getConditionedResourceKeys() {
        return this.conditionedResourceKeys;
    }

    /**
     * @param conditionedResourceKeys
     *            the conditionedResourceKeys to set
     */
    public void setConditionedResourceKeys(
            final Map<String, String> conditionedResourceKeys) {
        this.conditionedResourceKeys = conditionedResourceKeys;
    }

    /**
     * @param condition
     * @param key
     */
    public void addConditionAndKey(final String condition, final String key) {
        if (this.conditionedResourceKeys == null) {
            this.conditionedResourceKeys = new HashMap<String, String>();
        }
        this.conditionedResourceKeys.put(condition, key);
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
        result = prime
                * result
                + ((this.answerOptionUid == null) ? 0 : this.answerOptionUid
                        .hashCode());
        result = prime
                * result
                + ((this.variableName == null) ? 0 : this.variableName
                        .hashCode());
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
        final AnswerOptionEntity other = (AnswerOptionEntity) obj;
        if (this.answerOptionUid == null) {
            if (other.answerOptionUid != null)
                return false;
        } else if (!this.answerOptionUid.equals(other.answerOptionUid))
            return false;
        if (this.variableName == null) {
            if (other.variableName != null)
                return false;
        } else if (!this.variableName.equals(other.variableName))
            return false;
        return true;
    }

}
