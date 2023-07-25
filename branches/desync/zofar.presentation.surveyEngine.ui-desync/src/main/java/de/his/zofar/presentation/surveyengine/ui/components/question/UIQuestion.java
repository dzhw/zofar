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
package de.his.zofar.presentation.surveyengine.ui.components.question;

import javax.faces.component.UINamingContainer;

import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.Visible;
import de.his.zofar.presentation.surveyengine.ui.renderer.ZofarQuestionRenderer;

/**
 * base component for all questions in zofar. this also includes matrices and
 * other composites.
 *
 * @author le
 *
 */
public abstract class UIQuestion extends UINamingContainer implements Identificational,Visible {

	private static final String STYLE_CLASS = "zo-question";

    public static final String HEADER_STYLE_CLASS = "zo-question-header";

    public static final String COMPONENT_FAMILY = "org.zofar.Question";

    private enum PropertyKeys {
        styleClass
    }

    public UIQuestion() {
        super();
    }
    
	@Override
	@Deprecated
	public String getUID() {
		return this.getId();
	}

	@Override
	@Deprecated
	public Boolean visibleCondition() {
		return this.isRendered();
	}

    /**
     * returns the specific CSS style class of the question.
     *
     * @return
     */
    protected abstract String getSpecificStyleClass();

    /**
     * appends the style class set by the user of the component to the one set
     * in the constructor.
     *
     * @param styleClass
     */
    public void setStyleClass(final String styleClass) {
        this.getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    /**
     * returns the style class of this component.
     *
     * @return
     */
    public String getStyleClass() {
        String styleClass = STYLE_CLASS + " " + this.getSpecificStyleClass();

        final String additionalStyleClass = (String) this.getStateHelper().get(
                PropertyKeys.styleClass);
        if (additionalStyleClass != null && !additionalStyleClass.isEmpty()) {
            styleClass += " " + additionalStyleClass;
        }

        return styleClass;
    }

    /**
     * the layout of all questions is always "block".
     *
     * @return
     */
    public String getLayout() {
        return "block";
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.component.UIComponentBase#getRendererType()
     */
    @Override
    public String getRendererType() {
        return ZofarQuestionRenderer.RENDERER_TYPE;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.component.UINamingContainer#getFamily()
     */
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.component.UIComponentBase#isTransient()
     */
    @Override
    public boolean isTransient() {
        return true;
    }

}
