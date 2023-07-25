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
package de.his.zofar.persistence.question.entities.structured.impl;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import de.his.zofar.persistence.question.entities.structured.StructuredElementEntity;

@Entity
public abstract class StructuredTextElementEntity extends StructuredElementEntity {
	
	private String content;
	
	@ManyToOne
	private StructuredTextElementEntity parent;
	
	@OneToMany(mappedBy = "parent")
	private List<StructuredTextElementEntity> children;

	public StructuredTextElementEntity() {
		super();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public StructuredTextElementEntity getParent() {
		return parent;
	}

	public void setParent(StructuredTextElementEntity parent) {
		this.parent = parent;
	}

	public List<StructuredTextElementEntity> getChildren() {
		return children;
	}

	public void setChildren(List<StructuredTextElementEntity> children) {
		this.children = children;
	}
	
	

}
