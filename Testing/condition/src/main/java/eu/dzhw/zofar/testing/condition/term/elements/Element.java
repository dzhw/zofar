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
package eu.dzhw.zofar.testing.condition.term.elements;

public class Element {
	
	private Object value;
	private Object valueId;
	
	public Element(Object value) {
		super();
		this.value = value;
	}
	
	public Element(Object value,Object valueId) {
		super();
		this.value = value;
		this.valueId = valueId;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	
	public Object getValueId() {
		return valueId;
	}

	public void setValueId(Object valueId) {
		this.valueId = valueId;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((valueId == null) ? 0 : valueId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Element other = (Element) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (valueId == null) {
			if (other.valueId != null)
				return false;
		} else if (!valueId.equals(other.valueId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Element [value=" + value + ", valueId=" + valueId + "]";
	}
}
