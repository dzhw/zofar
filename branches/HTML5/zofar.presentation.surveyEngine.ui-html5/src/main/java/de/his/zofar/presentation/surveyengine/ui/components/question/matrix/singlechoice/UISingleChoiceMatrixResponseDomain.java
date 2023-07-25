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
package de.his.zofar.presentation.surveyengine.ui.components.question.matrix.singlechoice;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.FacesComponent;
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.UIMatrixResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.singlechoice.responsedomain.ZofarSingleChoiceMatrixResponseDomainRenderer;
/**
 * represents the HTML table in which the single choice matrix items will be
 * rendered.
 *
 * this component is self rendered (doesn't have a dedicated renderer. for
 * now!).
 *
 * @author le
 *
 */
@FacesComponent("org.zofar.SingleChoiceMatrixResponseDomain")
public class UISingleChoiceMatrixResponseDomain extends UIMatrixResponseDomain {
	public UISingleChoiceMatrixResponseDomain() {
		super();
	}
	@Override
	public String getRendererType() {
		return ZofarSingleChoiceMatrixResponseDomainRenderer.RENDERER_TYPE;
	}
	/**
	 * Does this Matrix question represent a Differential question.
	 *
	 * @return
	 */
	public Boolean isDifferential() {
		Boolean isDifferential = false;
		if (this.getAttributes().get("isDifferential") != null) {
			isDifferential = (Boolean) this.getAttributes().get("isDifferential");
		}
		return isDifferential;
	}
	public Boolean isShowValues() {
		Boolean isShowValues = false;
		if (this.getAttributes().get("isShowValues") != null) {
			isShowValues = (Boolean) this.getAttributes().get("isShowValues");
		}
		return isShowValues;
	}
    public List<String> getAdditonalClasses(){
		final String itemClassesStr = (String) this.getAttributes().get("itemClasses");
		if(itemClassesStr != null) {
			final String cleaned = itemClassesStr.strip();
			if(!cleaned.contentEquals("")) {
				final String[] splitted = cleaned.split(" ");
				if((splitted != null)&&(splitted.length > 0)) {
					final List<String> back = new ArrayList<String>();
					for(final String classStr : splitted) {
						final String cleanedClass = classStr.strip();
						if(!cleanedClass.contentEquals(""))back.add(cleanedClass);
					}
					return back;
				}
			}
		}
		return null;
	}
}
