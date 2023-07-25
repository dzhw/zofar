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
package de.his.zofar.presentation.surveyengine.ui.renderer.comparison;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.UIAttachedOpenQuestion;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.MultipleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.RadioButtonSingleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.question.UIQuestion;
import de.his.zofar.presentation.surveyengine.ui.components.question.composite.comparison.UIComparisonItem;
import de.his.zofar.presentation.surveyengine.ui.components.question.composite.comparison.UIComparisonResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.question.composite.comparison.UIComparisonUnit;
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.UIMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
import de.his.zofar.presentation.surveyengine.ui.interfaces.ISequence;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UIComparisonResponseDomain.COMPONENT_FAMILY, rendererType = ZofarComparisonResponseDomainRenderer.RENDERER_TYPE)
public class ZofarComparisonResponseDomainRenderer extends ZofarMatrixResponseDomainRenderer {
	public static final String RENDERER_TYPE = "org.zofar.ComparisonResponseDomain";
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarComparisonResponseDomainRenderer.class);
	public ZofarComparisonResponseDomainRenderer() {
		super();
	}
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(context), null);
		writer.writeAttribute("class", "form-orientation form-horizontal", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-scroller", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-inner form-2-col pt-2 pt-sm-4", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "container", null);
		final String carouselId = component.getClientId(context).replace(':', '_') + "_carousel";
		writer.startElement("div", component);
		writer.writeAttribute("id", carouselId, null);
		writer.writeAttribute("data-interval", "true", null);
		writer.writeAttribute("data-ride", "carousel", null);
		writer.startElement("div", component);
		writer.writeAttribute("role", "listbox", null);
	}
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		final UIComparisonResponseDomain rdc = (UIComparisonResponseDomain) component;
		final UIComponent header = component.getFacet("header");
		final UIComponent missings = component.getFacet("missingHeader");
		final List<UIComponent> allTitles = new ArrayList<UIComponent>();
		if (header != null)
			allTitles.addAll(this.retrieveTitles(header));
		if (missings != null)
			allTitles.addAll(this.retrieveTitles(missings));
		final Map<UIComponent, Map<UIQuestion, UIComponent>> mapping = this.retrieveMapping(rdc, allTitles);
		final int rowIndex = 1;
		this.addItems(context, rdc, header, writer, mapping, rowIndex);
		this.addItems(context, rdc, missings, writer, mapping, rowIndex);
	}
	private int addItems(final FacesContext context, final UIComparisonResponseDomain rdc,
			final UIComponent titleContainer, final ResponseWriter writer,
			final Map<UIComponent, Map<UIQuestion, UIComponent>> mapping, final int rowIndex) throws IOException {
		if (context == null)
			return rowIndex;
		if (rdc == null)
			return rowIndex;
		if (titleContainer == null)
			return rowIndex;
		final int count = this.getItemCount(rdc);
		int back = rowIndex;
		for (final UIComponent child : titleContainer.getChildren()) {
			if ((UIComparisonUnit.class).isAssignableFrom(child.getClass())) {
				final UIComparisonUnit tmp = (UIComparisonUnit) child;
				back = this.addItemsHelperUnit(context, writer, rdc, count, tmp, mapping, 0, back);
			} else if ((UIText.class).isAssignableFrom(child.getClass())) {
				final UIText tmp = (UIText) child;
				back = this.addItemsHelperItem(context, writer, rdc, mapping, tmp, 0, back);
			}
		}
		return back;
	}
	private int addItemsHelperUnit(final FacesContext context, final ResponseWriter writer,
			final UIComparisonResponseDomain rdc, final int count, final UIComparisonUnit unit,
			final Map<UIComponent, Map<UIQuestion, UIComponent>> mapping, final int indent, final int rowIndex)
			throws IOException {
		if (context == null)
			return rowIndex;
		if (writer == null)
			return rowIndex;
		if (rdc == null)
			return rowIndex;
		if (unit == null)
			return rowIndex;
		int back = rowIndex;
		if (unit.isRendered()) {
		}
		for (final UIComponent child : unit.getChildren()) {
			if ((UIComparisonUnit.class).isAssignableFrom(child.getClass())) {
				final UIComparisonUnit tmp = (UIComparisonUnit) child;
				if (!unit.isRendered())
					tmp.setRendered(false);
				back = this.addItemsHelperUnit(context, writer, rdc, count, tmp, mapping, indent + 1, back);
			} else if ((UIText.class).isAssignableFrom(child.getClass())) {
				final UIText tmp = (UIText) child;
				if (!unit.isRendered())
					tmp.setRendered(false);
				back = this.addItemsHelperItem(context, writer, rdc, mapping, tmp, indent + 1, back);
			} else if ((UISort.class).isAssignableFrom(child.getClass())) {
				final UISort tmp = (UISort) child;
				for (final UIComponent sortChild : tmp.sortChildren()) {
					if ((UIComparisonUnit.class).isAssignableFrom(sortChild.getClass())) {
						final UIComparisonUnit sortTmp = (UIComparisonUnit) sortChild;
						if (!tmp.isRendered())
							sortTmp.setRendered(false);
						back = this.addItemsHelperUnit(context, writer, rdc, count, sortTmp, mapping, indent + 1, back);
					} else if ((UIText.class).isAssignableFrom(sortChild.getClass())) {
						final UIText sortTmp = (UIText) sortChild;
						if (!tmp.isRendered())
							sortTmp.setRendered(false);
						back = this.addItemsHelperItem(context, writer, rdc, mapping, sortTmp, indent + 1, back);
					}
				}
			}
		}
		return back;
	}
	private int addItemsHelperItem(final FacesContext context, final ResponseWriter writer,
			final UIComparisonResponseDomain rdc, final Map<UIComponent, Map<UIQuestion, UIComponent>> mapping,
			final UIComponent title, final int indent, final int rowIndex) throws IOException {
		if (context == null)
			return rowIndex;
		if (writer == null)
			return rowIndex;
		if (rdc == null)
			return rowIndex;
		if (title == null)
			return rowIndex;
		if (title.isRendered()) {
			writer.startElement("div", rdc);
			String classes = "row highlight ";
			if(rowIndex == 0)classes += " active";
			writer.writeAttribute("class", classes, null);
			writer.writeAttribute("data-matrix", "item", null);
			writer.startElement("div", rdc);
			writer.writeAttribute("class", "col-md-4", null);
			final UIComponent header = title;
			if (header != null) {
				writer.startElement("p", rdc);
				writer.writeAttribute("id", header.getClientId(context) + "_header", null);
				writer.writeAttribute("class", "text-sub", null);
				final String headerText =JsfUtility.getInstance().getTextComponentAsString(context, header);
				 writer.write(headerText);
				for (final UIComponent child : rdc.getChildren()) {
					if ((UIAttachedOpenQuestion.class).isAssignableFrom(child.getClass())) {
						child.encodeAll(context);
					}
					for (final UIComponent subchild : child.getChildren()) {
						if ((UIAttachedOpenQuestion.class).isAssignableFrom(subchild.getClass())) {
							subchild.encodeAll(context);
						}
					}
				}
				writer.endElement("p");
			}
			writer.endElement("div");
			writer.startElement("div", rdc);
			writer.writeAttribute("class", "col-12 col-sm-8", null);
			writer.startElement("div", rdc);
			writer.writeAttribute("id", rdc.getClientId(), null);
			final Map<UIQuestion, UIComponent> map = mapping.get(title);
			String tmpClasses0 = "custom-form";
			if((map != null)&&(!map.isEmpty())){
				final Map.Entry<UIQuestion, UIComponent> item = map.entrySet().iterator().next();
				if ((de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption.class).isAssignableFrom(item.getValue().getClass())) {
					tmpClasses0 += " custom-form-radio";
				} else if ((de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption.class).isAssignableFrom(item.getValue().getClass())) {
					tmpClasses0 += " custom-form-checkbox";
				}
			}
			writer.writeAttribute("class", tmpClasses0, null);
			writer.startElement("div", rdc);
			writer.writeAttribute("class", "flex-wrapper", null);
			for (final Map.Entry<UIQuestion, UIComponent> item : map.entrySet()) {
				writer.startElement("div", rdc);
				String tmpClasses = "pipe-zofar-item-parent custom-control";
				if ((de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption.class).isAssignableFrom(item.getValue().getClass())) {
					tmpClasses += " custom-radio";
					((de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption)item.getValue()).setShowLabelFlag(true);
				} else if ((de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption.class).isAssignableFrom(item.getValue().getClass())) {
					tmpClasses += " custom-checkbox";
					((de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption)item.getValue()).setShowLabelFlag(true);
				}
				writer.writeAttribute("class", tmpClasses, null);
				item.getValue().encodeAll(context);
				writer.endElement("div");
			}
			writer.endElement("div");
			writer.endElement("div");
			writer.endElement("div");
			writer.endElement("div");
			return rowIndex + 1;
		}
		return rowIndex;
	}
	private int getItemCount(final UIComponent component) {
		int count = 0;
		if ((UIComparisonResponseDomain.class).isAssignableFrom(component.getClass())) {
			final UIComparisonResponseDomain rdc = (UIComparisonResponseDomain) component;
			for (final UIComponent item : rdc.getChildren()) {
				if ((UIComparisonItem.class).isAssignableFrom(item.getClass())) {
					final UIComparisonItem tmp = (UIComparisonItem) item;
					for (final UIComponent question : tmp.getChildren()) {
						if ((UIQuestion.class).isAssignableFrom(question.getClass())) {
							if (!question.isRendered())
								continue;
							count = count + 1;
						}
					}
				}
			}
		}
		return count;
	}
	private List<UIComponent> retrieveTitles(final UIComponent parent) {
		if (parent == null)
			return null;
		final List<UIComponent> back = new ArrayList<UIComponent>();
		final List<UIComponent> childs = parent.getChildren();
		for (final UIComponent child : childs) {
			if ((UIComparisonUnit.class).isAssignableFrom(child.getClass())) {
				back.addAll(this.retrieveTitles(child));
			} else if ((UISort.class).isAssignableFrom(child.getClass())) {
				back.addAll(this.retrieveTitles(child));
			} else if ((UIText.class).isAssignableFrom(child.getClass())) {
				back.add(child);
			}
		}
		return back;
	}
	private Map<UIComponent, Map<UIQuestion, UIComponent>> retrieveMapping(final UIComparisonResponseDomain rdc,
			final List<UIComponent> allTitles) {
		final Map<UIComponent, Map<UIQuestion, UIComponent>> back = new LinkedHashMap<UIComponent, Map<UIQuestion, UIComponent>>();
		for (final UIComponent title : allTitles) {
			final Map<UIQuestion, UIComponent> itemMap = new LinkedHashMap<UIQuestion, UIComponent>();
			final int index = allTitles.indexOf(title);
			final int count = rdc.getChildCount();
			for (int a = 0; a < count; a++) {
				final UIQuestion question = this.getQuestion(rdc, a);
				if (question == null)
					continue;
				if (!question.isRendered())
					continue;
				final UIComponent ao = this.getAnswerOption(question, index);
				itemMap.put(question, ao);
			}
			back.put(title, itemMap);
		}
		return back;
	}
	private UIQuestion getQuestion(final UIComparisonResponseDomain component, final int index) {
		if (component == null)
			return null;
		if (component.getChildCount() > index) {
			final UIComponent item = component.getChildren().get(index);
			if ((UIComparisonItem.class).isAssignableFrom(item.getClass())) {
				for (final UIComponent child : item.getChildren()) {
					if (!child.isRendered())
						continue;
					if ((UIQuestion.class).isAssignableFrom(child.getClass())) {
						return (UIQuestion) child;
					}
				}
			}
		}
		return null;
	}
	private UIComponent getAnswerOption(final UIQuestion question, final int index) {
		if (question == null)
			return null;
		if (question.getChildCount() > 0) {
			for (final UIComponent child : question.getChildren()) {
				if ((MultipleChoiceResponseDomain.class).isAssignableFrom(child.getClass())) {
					final MultipleOption ao = (MultipleOption) child.getChildren().get(index);
					return ao;
				}
				if ((RadioButtonSingleChoiceResponseDomain.class).isAssignableFrom(child.getClass())) {
					final SingleOption ao = (SingleOption) child.getChildren().get(index);
					return ao;
				}
			}
		}
		return null;
	}
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.render.Renderer#getRendersChildren()
	 */
	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
