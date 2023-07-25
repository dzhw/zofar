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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.singlechoice.responsedomain;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption;
import de.his.zofar.presentation.surveyengine.ui.components.matrix.singlechoice.UISingleChoiceMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.components.matrix.singlechoice.UISingleChoiceMatrixItemResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.matrix.singlechoice.UISingleChoiceMatrixResponseDomainUnit;
import de.his.zofar.presentation.surveyengine.ui.components.tablebase.AbstractTableResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixResponseDomainRenderer;
/**
 * @author meisner
 * 
 */
@FacesRenderer(componentFamily = AbstractTableResponseDomain.COMPONENT_FAMILY, rendererType = ZofarDifferentialMatrixResponseDomainRenderer.RENDERER_TYPE)
public class ZofarDifferentialMatrixResponseDomainRenderer extends
		ZofarMatrixResponseDomainRenderer {
	public static final String RENDERER_TYPE = "org.zofar.DifferentialMatrixResponseDomainRenderer";
	public ZofarDifferentialMatrixResponseDomainRenderer() {
		super();
	}
	protected Integer getNumberOfResponseOptions(final FacesContext context,
			final UIComponent component) {
		Integer back = 0;
		UISingleChoiceMatrixItem firstItem = null;
		for (final UIComponent child : component.getChildren()) {
			if ((UISingleChoiceMatrixItem.class).isAssignableFrom(child
					.getClass())) {
				firstItem = (UISingleChoiceMatrixItem) child;
				break;
			}
		}
		if (firstItem != null) {
			UISingleChoiceMatrixItemResponseDomain firstRDC = null;
			for (final UIComponent child : firstItem.getChildren()) {
				if ((UISingleChoiceMatrixItemResponseDomain.class)
						.isAssignableFrom(child.getClass())) {
					firstRDC = (UISingleChoiceMatrixItemResponseDomain) child;
					break;
				}
			}
			if (firstRDC != null) {
				for (final UIComponent child : firstRDC.getChildren()) {
					if ((SingleOption.class).isAssignableFrom(child
							.getClass())) {
						final SingleOption tmp = (SingleOption) child;
						if (!tmp.isMissing()) {
							back++;
						} 
					}
				}
			}
		}
		return back;
	}
	protected Integer getNumberOfMissingOptions(final FacesContext context,
			final UIComponent component) {
		Integer back = 0;
		UISingleChoiceMatrixItem firstItem = null;
		for (final UIComponent child : component.getChildren()) {
			if ((UISingleChoiceMatrixItem.class).isAssignableFrom(child
					.getClass())) {
				firstItem = (UISingleChoiceMatrixItem) child;
				break;
			}
		}
		if (firstItem != null) {
			UISingleChoiceMatrixItemResponseDomain firstRDC = null;
			for (final UIComponent child : firstItem.getChildren()) {
				if ((UISingleChoiceMatrixItemResponseDomain.class)
						.isAssignableFrom(child.getClass())) {
					firstRDC = (UISingleChoiceMatrixItemResponseDomain) child;
					break;
				}
			}
			if (firstRDC != null) {
				for (final UIComponent child : firstRDC.getChildren()) {
					if ((SingleOption.class).isAssignableFrom(child
							.getClass())) {
						final SingleOption tmp = (SingleOption) child;
						if (tmp.isMissing()) {
							back++;
						} 
					}
				}
			}
		}
		return back;
	}
	@Override
	protected void renderHeader(final FacesContext context,
			final UIComponent component) throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		final UIComponent header = component.getFacet(HEADER);
		if (header == null) {
			return;
		}
		writer.startElement(TABLE_ROW, component);
			writer.startElement(TABLE_HEADER, component);
			writer.endElement(TABLE_HEADER);
		if (UIText.class.isAssignableFrom(header.getClass())) {
			writer.startElement(TABLE_HEADER, component);
			writer.writeAttribute("class", "zo-matrix-table-header", null);
			header.encodeAll(context);
			writer.endElement(TABLE_HEADER);
		} else {
			for (final UIComponent columnHeader : header.getChildren()) {
				writer.startElement(TABLE_HEADER, component);
				writer.writeAttribute("class", "zofar-matrix-table-header",
						null);
				columnHeader.encodeAll(context);
				writer.endElement(TABLE_HEADER);
			}
		}
			writer.startElement(TABLE_HEADER, component);
			writer.endElement(TABLE_HEADER);
		this.renderMissingHeader(context, component);
		writer.endElement(TABLE_ROW);
		if (isShowValues(context, component)) {
			final List<String> values = new ArrayList<String>();
			final List<String> missingValues = new ArrayList<String>();
			UISingleChoiceMatrixItem firstItem = null;
			for (final UIComponent child : component.getChildren()) {
				if ((UISingleChoiceMatrixItem.class).isAssignableFrom(child.getClass())) {
					firstItem = (UISingleChoiceMatrixItem) child;
					break;
				} else if ((UISingleChoiceMatrixResponseDomainUnit.class).isAssignableFrom(child.getClass())) {
					for (final UIComponent unitChild : child.getChildren()) {
						if ((UISingleChoiceMatrixItem.class).isAssignableFrom(unitChild.getClass())) {
							firstItem = (UISingleChoiceMatrixItem) unitChild;
							break;
						}
					}
				}
			}
			if (firstItem != null) {
				UISingleChoiceMatrixItemResponseDomain firstRDC = null;
				for (final UIComponent child : firstItem.getChildren()) {
					if ((UISingleChoiceMatrixItemResponseDomain.class)
							.isAssignableFrom(child.getClass())) {
						firstRDC = (UISingleChoiceMatrixItemResponseDomain) child;
						break;
					}
				}
				if (firstRDC != null) {
					for (final UIComponent child : firstRDC.getChildren()) {
						if ((SingleOption.class).isAssignableFrom(child
								.getClass())) {
							final SingleOption tmp = (SingleOption) child;
							if (tmp.isMissing()) {
								missingValues.add(tmp.getValue());
							} else {
								values.add(tmp.getValue());
							}
						}
					}
				}
			}
			if (!values.isEmpty()) {
				final Iterator<String> valueIt = values.iterator();
				writer.startElement(TABLE_ROW, component);
				writer.startElement("td", component);
				writer.endElement("td");
				while (valueIt.hasNext()) {
					writer.startElement("td", component);
					writer.writeAttribute("class",
							"zo-matrix-values zo-sc-matrix-values", null);
					writer.write(valueIt.next());
					writer.endElement("td");
				}
				writer.startElement("td", component);
				writer.endElement("td");
				if (!missingValues.isEmpty()) {
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
				}
				writer.endElement(TABLE_ROW);
			}
		}
	}
	@Override
	protected void renderScaleHeader(final FacesContext context,
			final UIComponent component) throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		final UIComponent scaleHeader = component.getFacet(SCALE_HEADER);
		if (scaleHeader != null && scaleHeader.isRendered()) {
			writer.startElement(TABLE_ROW, component);
			writer.startElement(TABLE_HEADER, component);
			writer.endElement(TABLE_HEADER);
			final int colSpan1 = this.getNumberOfResponseOptions(context,
					component);
			writer.startElement(TABLE_HEADER, component);
			writer.writeAttribute(COLSPAN, colSpan1, null);
			writer.writeAttribute("class", "zo-dif-matrix-scale-header", null);
			scaleHeader.encodeAll(context);
			writer.endElement(TABLE_HEADER);
			writer.startElement(TABLE_HEADER, component);
			writer.endElement(TABLE_HEADER);
			final int colSpan2 = this.getNumberOfMissingOptions(context,
					component);
			writer.startElement(TABLE_HEADER, component);
			writer.writeAttribute(COLSPAN, colSpan2, null);
			writer.writeAttribute("class", "zo-dif-matrix-scale-header-missing", null);
			writer.endElement(TABLE_HEADER);
			writer.endElement(TABLE_ROW);
		}
	}
	@Override
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}
		super.encodeBegin(context, component,"zo-dif-matrix-response-domain");
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
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}
		super.encodeEnd(context, component);
	}
}
