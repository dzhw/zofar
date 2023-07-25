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
/*
 * 
 */
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.singlechoice.responsedomain;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.UIDropDownMissingResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.matrix.singlechoice.UISingleChoiceMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.components.tablebase.AbstractTableResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixResponseDomainRenderer;
/**
 * @author meisner
 * 
 */
@FacesRenderer(componentFamily = AbstractTableResponseDomain.COMPONENT_FAMILY, rendererType = ZofarSingleChoiceMatrixResponseDomainRenderer.RENDERER_TYPE)
public class ZofarSingleChoiceMatrixResponseDomainRenderer extends
		ZofarMatrixResponseDomainRenderer {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarSingleChoiceMatrixResponseDomainRenderer.class);
	public static final String RENDERER_TYPE = "org.zofar.SingleChoiceMatrixResponseDomain";
	public ZofarSingleChoiceMatrixResponseDomainRenderer() {
		super();
	}
	@Override
	public void encodeBegin(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}
		String additionalClasses = "zo-sc-matrix-response-domain";
		for (final UIComponent comp : component.getChildren()) {
			if ((UISingleChoiceMatrixItem.class).isAssignableFrom(comp
					.getClass())) {
				for (final UIComponent newComp : comp.getChildren()) {
					if ((UIDropDownMissingResponseDomain.class)
							.isAssignableFrom(newComp.getClass())) {
						additionalClasses = additionalClasses
								+ " zo-dropdown-matrix-responsedomain";
						break;
					}
				}
				break;
			}
		}
		super.encodeBegin(context, component, additionalClasses);
	}
	@Override
	protected void renderHeader(final FacesContext context,
			final UIComponent component) throws IOException {
		if(!component.isRendered()){
			return;
		}
		boolean flag = false;
		for (final UIComponent comp : component.getChildren()) {
			if ((UISingleChoiceMatrixItem.class).isAssignableFrom(comp
					.getClass())) {
				for (final UIComponent newComp : comp.getChildren()) {
					if ((UIDropDownMissingResponseDomain.class)
							.isAssignableFrom(newComp.getClass())) {
						flag = true;
						break;
					}
				}
				break;
			}
		}
		if(!flag){
			super.renderHeader(context, component);
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		final UIComponent header = component.getFacet(ZofarMatrixResponseDomainRenderer.HEADER);
		if (header == null) {
			return;
		}
		writer.startElement(ZofarMatrixResponseDomainRenderer.TABLE_ROW, component);
		if (this.isHasQuestionColumn(context, component)) {
			writer.startElement(ZofarMatrixResponseDomainRenderer.TABLE_HEADER, component);
			writer.endElement(ZofarMatrixResponseDomainRenderer.TABLE_HEADER);
		}
		writer.startElement(ZofarMatrixResponseDomainRenderer.TABLE_HEADER, component);
		writer.endElement(ZofarMatrixResponseDomainRenderer.TABLE_HEADER);
		this.renderMissingHeader(context, component);
		writer.endElement(ZofarMatrixResponseDomainRenderer.TABLE_ROW);
		if (this.isShowValues(context, component)) {
			final List<String> values = new ArrayList<String>();
			final List<String> missingValues = new ArrayList<String>();
			for (final UIComponent comp : component.getChildren()) {
				if ((UISingleChoiceMatrixItem.class).isAssignableFrom(comp
						.getClass())) {
					for (final UIComponent newComp : comp.getChildren()) {
						if ((UIDropDownMissingResponseDomain.class)
								.isAssignableFrom(newComp.getClass())) {
							final UIDropDownMissingResponseDomain tmp = (UIDropDownMissingResponseDomain) newComp;
							final UIComponent missings = tmp.getFacet("missing");
							if (missings != null) {
								for (final UIComponent child : missings
										.getChildren()) {
									if ((SingleOption.class)
											.isAssignableFrom(child.getClass())) {
										final SingleOption tmpChild = (SingleOption) child;
										if (tmpChild.isMissing()) {
											missingValues.add(tmpChild
													.getValue());
										} else {
											values.add(tmpChild.getValue());
										}
									}
								}
							}
						}
					}
					if (!missingValues.isEmpty())
						break;
				}
			}
			if (!missingValues.isEmpty()) {
				writer.startElement(ZofarMatrixResponseDomainRenderer.TABLE_ROW, component);
				writer.startElement("td", component);
				writer.endElement("td");
				writer.startElement("td", component);
				writer.endElement("td");
				int counter = 0;
				final Iterator<String> valueMissingIt = missingValues
						.iterator();
				while (valueMissingIt.hasNext()) {
					writer.startElement("td", component);
					writer.writeAttribute("class",
							"zo-matrix-values zo-sc-matrix-values-missing-"
									+ (counter++), null);
					writer.write(valueMissingIt.next());
					writer.endElement("td");
				}
				writer.endElement(ZofarMatrixResponseDomainRenderer.TABLE_ROW);
			}
		}
	}
	@Override
	public void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		super.encodeChildren(context, component);
	}
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}
		super.encodeEnd(context, component);
	}
}
