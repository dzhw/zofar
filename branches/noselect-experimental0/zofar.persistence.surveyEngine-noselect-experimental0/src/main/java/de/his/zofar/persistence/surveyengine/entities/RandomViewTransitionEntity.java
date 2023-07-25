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
package de.his.zofar.persistence.surveyengine.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.his.zofar.persistence.common.entities.BaseEntity;

/**
 * Entity which stores a page transition which was randomly chosen.
 * 
 * @author Reitmann
 */
@Entity
@Table(name = "random_view_transition",
        uniqueConstraints = { @UniqueConstraint(columnNames = {
                "participant_id", "fromviewid" }) })
@SequenceGenerator(initialValue = 1, allocationSize = 1,
        name = "primary_key_generator",
        sequenceName = "SEQ_RANDOM_VIEW_TRANSITION_ID")
public class RandomViewTransitionEntity extends BaseEntity {

    /**
     * The participant who executed this transition.
     */
    @ManyToOne(optional = false)
    private ParticipantEntity participant;

    @Column(nullable = false)
    private String fromViewId;

    @Column(nullable = false)
    private String toViewId;

    public RandomViewTransitionEntity() {
        super();
    }

    public String getFromViewId() {
        return this.fromViewId;
    }

    public ParticipantEntity getParticipant() {
        return this.participant;
    }

    public String getToViewId() {
        return this.toViewId;
    }

    public void setFromViewId(final String fromViewId) {
        this.fromViewId = fromViewId;
    }

    public void setParticipant(final ParticipantEntity participant) {
        this.participant = participant;
    }

    public void setToViewId(final String toViewId) {
        this.toViewId = toViewId;
    }
}
