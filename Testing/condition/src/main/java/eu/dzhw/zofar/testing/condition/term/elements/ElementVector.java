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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
public class ElementVector {
	private Map<String,Set<Element>> tupels;
	public ElementVector() {
		super();
		this.tupels = new LinkedHashMap<String,Set<Element>>();
	}
	public Map<String,Set<Element>> getTupels() {
		return tupels;
	}
	public void setTupels(Map<String,Set<Element>> tupels) {
		this.tupels = tupels;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tupels == null) ? 0 : tupels.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ElementVector))
			return false;
		ElementVector other = (ElementVector) obj;
		if (tupels == null) {
			if (other.tupels != null)
				return false;
		} 
		else{
			for(Entry<String, Set<Element>> item : tupels.entrySet()){
				final String key = item.getKey();
				final Set<Element> thisSet = item.getValue();
				final Set<Element> otherSet = other.tupels.get(key);
				if (thisSet == null) {
					if (otherSet != null)
						return false;
				} 
				else{
					if (otherSet == null) return false;
					else if(!thisSet.equals(otherSet))return false;
				}
			}
		}
		return true;
	}
	@Override
	public String toString() {
		return "" + (tupels != null ? "" + tupels : "") + "";
	}
}
