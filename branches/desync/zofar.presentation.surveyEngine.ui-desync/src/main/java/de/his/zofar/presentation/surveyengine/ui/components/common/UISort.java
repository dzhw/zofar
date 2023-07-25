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
package de.his.zofar.presentation.surveyengine.ui.components.common;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.interfaces.ISortBean;
/**
 * self rendered class with sort its children first before rendering them.
 * actual sorting algorithm must be implemented by a class which implements the
 * ISortBean interface.
 * 
 * @author meisner
 * 
 */
@FacesComponent(value = "org.zofar.Sort")
public class UISort extends UINamingContainer {
	private static final Logger LOGGER = LoggerFactory.getLogger(UISort.class);
	private enum PropertyKeys {
		bean, mode, sorted
	}
	public UISort() {
		super();
		this.setRendererType(null);
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context
	 * .FacesContext)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.component.UIComponentBase#encodeChildren(javax.faces.context
	 * .FacesContext)
	 */
	@Override
	public void encodeChildren(final FacesContext context) throws IOException {
		for (final UIComponent child : sortChildren()) {
			child.encodeAll(context);
		}
	}
	/**
	 * delegates the actual sorting to a implementation of the ISortBean
	 * interface.
	 * 
	 * @return
	 */
	public List<UIComponent> sortChildren() {
		final List<UIComponent> childrenToSort = new ArrayList<UIComponent>(
				this.getChildren());
		final List<UIComponent> fromBean = this.getBean().sort(childrenToSort,
				this.getClientId(this.getFacesContext()), this.getMode());
		return fromBean;
	}
	public void setBean(final String bean) {
		this.getStateHelper().put(PropertyKeys.bean, bean);
	}
	public ISortBean getBean() {
		return (ISortBean) this.getStateHelper().eval(PropertyKeys.bean,
				new SortBean());
	}
	public void setMode(final String mode) {
		this.getStateHelper().put(PropertyKeys.mode, mode);
	}
	public String getMode() {
		return (String) this.getStateHelper().eval(PropertyKeys.mode, "random");
	}
	public void setSorted(final boolean sorted) {
		this.getStateHelper().put(PropertyKeys.sorted, sorted);
	}
	public boolean isSorted() {
		return Boolean.valueOf(this.getStateHelper()
				.eval(PropertyKeys.sorted, Boolean.TRUE).toString());
	}
	/**
	 * default / fallback implementation of the ISortBean interface. this is a
	 * very dump implementation and always produces a new sort order on every
	 * request.
	 * 
	 * TODO this is unnecessary if we make the bean attribute required. BUT this
	 * is not working right now. which means even if we declare the bean
	 * attribute as required it is possible to use the component without passing
	 * a sort bean instance to the component.
	 * 
	 * @author le
	 * 
	 */
	private class SortBean implements ISortBean {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * de.his.zofar.presentation.surveyengine.ui.interfaces.ISortBean#sort
		 * (java.util.List, java.lang.String, java.lang.String)
		 */
		@Override
		public List<UIComponent> sort(final List<UIComponent> toSort,
				final String parentId, final String mode) {
			Collections.shuffle(toSort);
			return toSort;
		}
	}
}
