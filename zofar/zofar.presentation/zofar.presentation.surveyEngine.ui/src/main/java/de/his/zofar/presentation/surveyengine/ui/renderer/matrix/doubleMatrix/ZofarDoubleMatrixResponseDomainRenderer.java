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
package de.his.zofar.presentation.surveyengine.ui.renderer.matrix.doubleMatrix;
import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.composite.doublematrix.UIDoubleMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.components.composite.doublematrix.UIDoubleMatrixResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.composite.doublematrix.UIDoubleMatrixUnit;
import de.his.zofar.presentation.surveyengine.ui.components.matrix.singlechoice.UISingleChoiceMatrixItemResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
/**
* @author meisner
*
*/
@FacesRenderer(componentFamily = UIDoubleMatrixResponseDomain.COMPONENT_FAMILY, rendererType = ZofarDoubleMatrixResponseDomainRenderer.RENDERER_TYPE)
public class ZofarDoubleMatrixResponseDomainRenderer extends
		ZofarAbstractDoubleMatrixRenderer {
	private int row;
	public static final String RENDERER_TYPE = "org.zofar.DoubleMatrixResponseDomain";
    private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarDoubleMatrixResponseDomainRenderer.class);
    private static final String SCALE_HEADER_LEFT = "leftScaleHeader";
    private static final String SCALE_HEADER_RIGHT = "rightScaleHeader";
    private static final String HEADER_LEFT = "leftHeader";
    private static final String HEADER_RIGHT = "rightHeader";
    private static final String MISSING_HEADER_LEFT = "leftMissingHeader";
    private static final String MISSING_HEADER_RIGHT = "rightMissingHeader";
    private static final String CSS_CLASS = "zofar-table-responsedomain zofar-matrix-double";
	public ZofarDoubleMatrixResponseDomainRenderer() {
		super();
	}
    @Override
    public boolean getRendersChildren() {
        return true;
    }
    @Override
    public synchronized void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        row = 0;
        writer.startElement("table", component);
        writer.writeAttribute("class", CSS_CLASS, null);
        writer.startElement("tr", component);
        this.renderScaleHeader(context,component, Side.LEFT);
        this.renderEmptyHeader(context,component);
        this.renderScaleHeader(context,component, Side.RIGHT);
        writer.endElement("tr");
        writer.startElement("tr", component);
        this.renderHeader(context,component, Side.LEFT);
        this.renderEmptyHeader(context,component);
        this.renderHeader(context,component, Side.RIGHT);
        writer.endElement("tr");
        if (this.isShowValues(context,component)) {
            final UIDoubleMatrixItem item = this.findFirstItem(component
                    .getChildren());
            if (item != null) {
                writer.startElement("tr", component);
                this.renderValuesHeader(context,component, item, Side.LEFT);
                this.renderEmptyHeader(context,component);
                this.renderValuesHeader(context,component, item, Side.RIGHT);
                writer.endElement("tr");
            }
        }
    }
    /**
     * @param context
     * @param item
     * @param side
     * @throws IOException
     */
    private synchronized void renderValuesHeader(final FacesContext context, final UIComponent component,
            final UIDoubleMatrixItem item, final Side side) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        String facetName = null;
        switch (side) {
        case LEFT:
            facetName = this.LEFT_QUESTION;
            break;
        case RIGHT:
            facetName = this.RIGHT_QUESTION;
            break;
        default:
            throw new IllegalStateException();
        }
        final UIComponent question = item.getFacet(facetName);
        if (question != null
                && UISingleChoiceMatrixItemResponseDomain.class
                        .isAssignableFrom(question.getClass())) {
            for (final UIComponent child : question.getChildren()) {
                if (SingleOption.class.isAssignableFrom(child.getClass())) {                	                
                    writer.startElement("th", component);
                    if (((SingleOption) child).isMissing()){
                    	writer.writeAttribute("class", "zo-missing-value", null);
                    }
                    writer.write(((SingleOption) child).getValue());
                    writer.endElement("th");
                }
            }
        }
    }
    /**
     * @param children
     * @return
     */
    private synchronized UIDoubleMatrixItem findFirstItem(final List<UIComponent> children) {
        for (final UIComponent child : children) {
            if (UIDoubleMatrixItem.class.isAssignableFrom(child.getClass())) {
                return (UIDoubleMatrixItem) child;
            } else {
                return this.findFirstItem(child.getChildren());
            }
        }
        return null;
    }
    /**
     * @param context
     * @param side
     * @throws IOException
     */
    private synchronized void renderHeader(final FacesContext context, final UIComponent component,final Side side)
            throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        String facetName = null;
        switch (side) {
        case LEFT:
            facetName = HEADER_LEFT;
            break;
        case RIGHT:
            facetName = HEADER_RIGHT;
            break;
        default:
            throw new IllegalStateException();
        }
        final UIComponent headerFacet = component.getFacet(facetName);
        if (headerFacet != null) {
            final boolean isDifferential = this.isDifferential(context,component);
            if (isDifferential) {
                this.renderEmptyHeader(context, component);
            }
            if (UIText.class.isAssignableFrom(headerFacet.getClass())) {
                writer.startElement("th", component);
                headerFacet.encodeAll(context);
                writer.endElement("th");
            } else {
                for (final UIComponent header : headerFacet.getChildren()) {
                    writer.startElement("th", component);
                    writer.writeAttribute("style", this.getHeaderMinWidth(context,component),
                            null);
                    header.encodeAll(context);
                    writer.endElement("th");
                }
            }
            if (isDifferential) {
                this.renderEmptyHeader(context,component);
            }
        }
        this.renderMissingHeader(context,component, side);
    }
    private synchronized void renderMissingHeader(final FacesContext context, final UIComponent component, final Side side)
            throws IOException {
        this.renderMissingHeader(context,component, side, false);
    }
    /**
     * @param context
     * @param side
     */
    private synchronized void renderMissingHeader(final FacesContext context, final UIComponent component,
            final Side side, final Boolean isEmpty) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        String facetName = null;
        switch (side) {
        case LEFT:
            facetName = MISSING_HEADER_LEFT;
            break;
        case RIGHT:
            facetName = MISSING_HEADER_RIGHT;
            break;
        default:
            throw new IllegalStateException();
        }
        final UIComponent missingHeaderFacet = component.getFacet(facetName);
        if (missingHeaderFacet != null) {
            if (UIText.class.isAssignableFrom(missingHeaderFacet.getClass())) {
                writer.startElement("th", component);
                writer.writeAttribute("class", "zofar-missing", null);
                if (!isEmpty) {
                    missingHeaderFacet.encodeAll(context);
                }
                writer.endElement("th");
            } else {
                for (final UIComponent header : missingHeaderFacet
                        .getChildren()) {
                    writer.startElement("th", component);
                    writer.writeAttribute("class", "zofar-missing", null);
                    writer.writeAttribute("style", this.getHeaderMinWidth(context,component),
                            null);
                    if (!isEmpty) {
                        header.encodeAll(context);
                    }
                    writer.endElement("th");
                }
            }
        }
    }
    /**
     * @param context
     */
    private synchronized void renderScaleHeader(final FacesContext context, final UIComponent component, final Side side)
            throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        String facetName = null;
        switch (side) {
        case LEFT:
            facetName = SCALE_HEADER_LEFT;
            break;
        case RIGHT:
            facetName = SCALE_HEADER_RIGHT;
            break;
        default:
            throw new IllegalStateException();
        }
        final UIComponent scaleHeaderFacet = component.getFacet(facetName);
        if (scaleHeaderFacet != null) {
            writer.startElement("th", component);
            final int colspan = this.isDifferential(context,component) ? this
                    .getNumberOfResponseOptions(context,component) + 2 : this
                    .getNumberOfResponseOptions(context,component);
            writer.writeAttribute("colspan", colspan, null);
            scaleHeaderFacet.encodeAll(context);
            writer.endElement("th");
        }
        this.renderMissingHeader(context,component, side, true);
    }
    /**
     * @param context
     */
    private synchronized void renderEmptyHeader(final FacesContext context, final UIComponent component)
            throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        writer.startElement("th", component);
        writer.endElement("th");
    }
    @Override
    public synchronized void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
        this.renderItems(context,component, component.getChildren());
    }
    private synchronized void renderItems(final FacesContext context, final UIComponent component,
            final List<UIComponent> items) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
		final String[] rowClasses = itemClassesToArray((String) component.getAttributes().get(
				"itemClasses"));
		final boolean hasRowClasses = rowClasses.length > 0;
        for (final UIComponent child : items) {
            if (UIDoubleMatrixItem.class.isAssignableFrom(child.getClass())) {
                writer.startElement("tr", component);
    			String classes = "";
    			if (hasRowClasses) {
    				classes += rowClasses[row++ % rowClasses.length]+" ";
    			}
    			if(!classes.equals(""))writer.writeAttribute("class", classes.trim(), null);
                child.encodeAll(context);
                writer.endElement("tr");
            } else if (UIDoubleMatrixUnit.class.isAssignableFrom(child.getClass())) {
                this.renderUnit(context,component, (UIDoubleMatrixUnit) child);
            } else if (UISort.class.isAssignableFrom(child.getClass())) {
                this.renderItems(context,component, ((UISort) child).sortChildren());
            }
        }
    }
    private synchronized void renderUnit(final FacesContext context, final UIComponent component,
            final UIDoubleMatrixUnit unit) throws IOException {
    	LOGGER.info("renderUnit {}",unit.getClientId());
        final ResponseWriter writer = context.getResponseWriter();
        final UIComponent unitHeader = unit.getFacet("header");
        if (unitHeader != null) {
            writer.startElement("tr", component);
            writer.startElement("td", component);
            writer.writeAttribute("class", "zo-doublematrix-unit-header", null);
            final int colspan = this.isDifferential(context,component) ? ((this
                    .getNumberOfResponseOptions(context,component) + 2) * 2) + 1 : (this
                    .getNumberOfResponseOptions(context,component) * 2) + 1;
            writer.writeAttribute("colspan", colspan, null);
            unitHeader.encodeAll(context);
            writer.endElement("td");
            writer.endElement("tr");
        }
        this.renderItems(context,component, unit.getChildren());
    }
    @Override
    public synchronized void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        writer.endElement("table");
    }
    public synchronized Integer getNumberOfResponseOptions(final FacesContext context, final UIComponent component) {
        Integer value = (Integer) component.getAttributes().get("noResponseOptions");
        if (value == null) {
            value = 0;
        }
        return value;
    }
    /**
     * Does this Matrix question represent a Differential question.
     *
     * @return
     */
    public synchronized Boolean isDifferential(final FacesContext context, final UIComponent component) {
        Boolean isDifferential = false;
        if (component.getAttributes().get("isDifferential") != null) {
            isDifferential = (Boolean) component.getAttributes().get(
                    "isDifferential");
        }
        return isDifferential;
    }
    public synchronized Boolean isShowValues(final FacesContext context, final UIComponent component) {
        Boolean isShowValues = false;
        if (component.getAttributes().get("isShowValues") != null) {
            isShowValues = (Boolean) component.getAttributes().get("isShowValues");
        }
        return isShowValues;
    }
    /**
     * @return
     */
    private synchronized String getHeaderMinWidth(final FacesContext context, final UIComponent component) {
        return "width: "
                + 700
                / (((this.getNumberOfResponseOptions(context,component) * 2) + this
                        .getNumberOfMissings(context,component))) + "px;";
    }
    private synchronized Integer getNumberOfMissings(final FacesContext context, final UIComponent component) {
        Integer noMissings = 0;
        final UIComponent left = component.getFacet(MISSING_HEADER_LEFT);
        final UIComponent right = component.getFacet(MISSING_HEADER_RIGHT);
        if (left != null) {
            if (left.getChildren().isEmpty()) {
                noMissings += 1;
            } else {
                noMissings += left.getChildren().size();
            }
        }
        if (right != null) {
            if (right.getChildren().isEmpty()) {
                noMissings += 1;
            } else {
                noMissings += right.getChildren().size();
            }
        }
        return noMissings;
    }
}
