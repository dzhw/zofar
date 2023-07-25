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
package eu.dzhw.zofar.management.utils.odf.components;
@Deprecated
public abstract class OdfTextElement {
	private final String content;
	public OdfTextElement(final String content) {
		super();
		this.content = content;
	}
	public String getContent() {
		return this.content;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.content == null) ? 0 : this.content.hashCode());
		return result;
	}
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final OdfTextElement other = (OdfTextElement) obj;
		if (this.content == null) {
			if (other.content != null)
				return false;
		} else if (!this.content.equals(other.content))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "OdfTextElement [content=" + this.content + "]";
	}
}
