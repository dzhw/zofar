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
package de.his.zofar.service.valuetype.model.possiblevalues;
import java.util.ArrayList;
import java.util.List;
import de.his.zofar.service.common.model.BaseDTO;
import de.his.zofar.service.valuetype.model.ValueType;
public abstract class PossibleValue extends BaseDTO {
    /**
     *
     */
    private static final long serialVersionUID = -1567411117371321610L;
    private Boolean isMissing;
    private List<String> labels;
    public void addLabel(final String label) {
        if (this.labels == null) {
            this.labels = new ArrayList<String>();
        }
        this.labels.add(label);
    }
    public Boolean getIsMissing() {
        return this.isMissing;
    }
    public List<String> getLabels() {
        return this.labels;
    }
    public abstract ValueType getValueType();
    public void setIsMissing(final Boolean isMissing) {
        this.isMissing = isMissing;
    }
    public void setLabels(final List<String> labels) {
        this.labels = labels;
    }
}
