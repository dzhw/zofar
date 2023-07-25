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
package de.his.zofar.presentation.surveyengine.ui.interfaces;

import java.util.Stack;

/**
 * @author le
 *
 */
public interface INavigator {

    enum DirectionType {
        UNKOWN, SAME, FORWARD, BACKWARD
    }

    /**
     * Method to retrieve next-to-last visited page
     * 
     * @return next-to-last Page-Name
     */
    public abstract String getBackwardViewID();

    /**
     * Method to retrieve current visited page (needed by onexit-validation
     * Exception handling)
     * 
     * @return current Page-Name
     */
    public abstract String getSameViewID();

    /**
     * @return true if current Movement Direction is backward
     */
    public abstract boolean isBackward();

    /**
     * @return true if current Movement Direction is forward
     */
    public abstract boolean isForward();

    /**
     * Method used for render-decision of backward-Button
     * 
     * @return true, if current history is empty
     */
    public abstract boolean isHistoryEmpty();

    /**
     * @return true if current Movement Direction is same
     */
    public abstract boolean isSame();

    /**
     * Randomly pick a view and register the chosen viewId.
     * 
     * @param randomOnce
     *            true if we only need to shuffle once
     * @param viewIds
     *            List of viewIds, seperated by comma.
     * @return The randomly chosen viewId.
     */
    public abstract String pickAndSendViewID(boolean randomOnce, String viewIds);

    /**
     * Register viewId of current requested page and redirect to last valid page
     * if applicable
     * 
     * @param viewId
     */
    public abstract void registerViewID(String viewId);

    /**
     * Register viewId of page, which was sended by effective Navigation-case
     * 
     * @param viewId
     * @return viewId without File-Suffix and trailing /
     */
    public abstract String sendViewID(String viewId);

    public abstract Stack<String> getMovements();
}
