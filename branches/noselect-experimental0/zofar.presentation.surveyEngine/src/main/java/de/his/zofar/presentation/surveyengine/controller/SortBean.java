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
package de.his.zofar.presentation.surveyengine.controller;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import de.his.zofar.presentation.surveyengine.facades.SurveyEngineServiceFacade;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.interfaces.ISortBean;
/**
 * @author meisner
 *
 */
@Controller("sortBean")
@Scope("request")
public class SortBean implements Serializable, ISortBean {
    private class PlaceHolder extends UIComponentBase {
        public PlaceHolder() {
            super();
        }
        /*
         * (non-Javadoc)
         *
         * @see javax.faces.component.UIComponent#getFamily()
         */
        @Override
        public String getFamily() {
            return null;
        }
    }
    private static final long serialVersionUID = -3796070924905942938L;
    private Map<String, List<String>> sortMap = new HashMap<String, List<String>>();
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SortBean.class);
    private final SurveyEngineServiceFacade surveyEngineServiceFacade;
    @Inject
    private SessionController session;
    public SortBean() {
        surveyEngineServiceFacade = new SurveyEngineServiceFacade();
    }
    /**
     * get existing Sorting for given uid
     *
     * @param uid
     *            of parent element
     * @return sorted list of children uids
     */
    private List<String> getSort(final String uid) {  	
        if (sortMap.containsKey(uid)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getSort for {} ==> {}", uid,
                        sortMap.get(uid));
            }
            return sortMap.get(uid);
        }
        return new ArrayList<String>();
    }
    @PostConstruct
    private void init() {
        sortMap = surveyEngineServiceFacade.loadSortings(session
                .getParticipant());
        if (sortMap == null) {
            sortMap = new HashMap<String, List<String>>();
        }
    }
    protected List<UIComponent> resort(final List<String> sorting,
            final List<UIComponent> childs) {
        if (childs == null) {
            return null;
        }
        if (childs.isEmpty()) {
            return null;
        }
        if (sorting == null) {
            return childs;
        }
        if (sorting.isEmpty()) {
            return childs;
        }
        final Map<String, Identificational> map = new HashMap<String, Identificational>();
        final List<UIComponent> transferList = new ArrayList<UIComponent>();
        Iterator<UIComponent> it = childs.iterator();
        while (it.hasNext()) {
            final UIComponent child = it.next();
            if ((Identificational.class).isAssignableFrom(child.getClass())) {
                final Identificational tmp = (Identificational) child;
                final String clientId = tmp.getClientId();
                map.put(clientId, tmp);
                if (sorting.contains(clientId)) {
                    transferList.add(new PlaceHolder());
                } else {
                    transferList.add(child);
                }
            } else {
                transferList.add(child);
            }
        }
        final Stack<String> sortingStack = new Stack<String>();
        sortingStack.addAll(sorting);
        final List<UIComponent> back = new ArrayList<UIComponent>();
        it = transferList.iterator();
        while (it.hasNext()) {
            final UIComponent child = it.next();
            if ((PlaceHolder.class).isAssignableFrom(child.getClass())) {
                final String nextUID = sortingStack.firstElement();
                sortingStack.remove(nextUID);
                back.add((UIComponent) map.get(nextUID));
            } else {
                back.add(child);
            }
        }
        return back;
    }
    protected List<String> retrieveSorting(final List<?> childs) {
        if (childs == null) {
            return null;
        }
        if (childs.isEmpty()) {
            return null;
        }
        final List<String> back = new ArrayList<String>();
        final Iterator<?> it = childs.iterator();
        while (it.hasNext()) {
            final Object child = it.next();
            if ((Identificational.class).isAssignableFrom(child.getClass())) {
                back.add(((Identificational) child).getClientId());
            }
        }
        return back;
    }
    /**
     * set Sorting for given uid
     *
     * @param uid
     *            of parent element
     * @param childUIDs
     *            sorted list of children uids
     */
    private void setSort(final String uid, final List<String> childUIDs) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setSort for {} ==> {}", uid, childUIDs);
        }
        sortMap.put(uid, childUIDs);
        surveyEngineServiceFacade.saveSorting(uid, childUIDs,
                session.getParticipant());
    }
    @Override
    public List<UIComponent> sort(List<UIComponent> childrenToSort,
            final String componentUid, final String sortMode) {
        final List<UIComponent> sortedChildren = new ArrayList<UIComponent>();
        if (sortMode.equals("random")) {
            Collections.shuffle(childrenToSort);
            setSort(componentUid, retrieveSorting(childrenToSort));
        } else if (sortMode.equals("randomonce")) {
            final List<String> sorting = getSort(componentUid);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("existing sorting for {} ==> {}", componentUid,
                        sorting);
            }
            if (sorting.isEmpty()) {
                Collections.shuffle(childrenToSort);
                setSort(componentUid, retrieveSorting(childrenToSort));
            } else {
                childrenToSort = resort(sorting, childrenToSort);
            }
        } else {
            throw new RuntimeException("sortmode " + sortMode
                    + " not implemented yet");
        }
        sortedChildren.addAll(childrenToSort);
        return sortedChildren;
    }
}
