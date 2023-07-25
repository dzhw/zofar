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
package de.his.zofar.service.common.mapper;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.dozer.DozerEventListener;
import org.dozer.event.DozerEvent;
import de.his.zofar.persistence.common.entities.BaseEntity;
/**
 * Dozer Event Listener which flushes the EntityManager before
 * an entity is mapped to a DTO.
 * 
 * @author Reitmann
 */
public class FlushingMappingListener implements DozerEventListener {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public void mappingFinished(final DozerEvent event) {
    }
    /**
     * Flush EntityManager before entity is mapped to DTO.
     */
    @Override
    public void mappingStarted(final DozerEvent event) {
        if (BaseEntity.class.isAssignableFrom(event.getSourceObject()
                .getClass())) {
            this.entityManager.flush();
        }
    }
    @Override
    public void postWritingDestinationValue(final DozerEvent event) {
    }
    @Override
    public void preWritingDestinationValue(final DozerEvent event) {
    }
}
