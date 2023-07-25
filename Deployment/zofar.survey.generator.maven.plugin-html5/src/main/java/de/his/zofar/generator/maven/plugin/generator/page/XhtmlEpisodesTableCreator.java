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
package de.his.zofar.generator.maven.plugin.generator.page;
import org.apache.xmlbeans.XmlException;
import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.container.SectionType;
import de.his.zofar.xml.questionnaire.EpisodesTableType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
public class XhtmlEpisodesTableCreator extends AbstractXhtmlElementCreator implements IXhtmlCreator {
	public XhtmlEpisodesTableCreator() {
		super();
	}
	@Override
	public void addToSection(IdentificationalType source, SectionType target, boolean root) throws XmlException {
		createElement(source, target.addNewEpisodesTable());
	}
	@Override
	public void addToSort(IdentificationalType source, SortType target) throws XmlException {
		createElement(source, target.addNewEpisodesTable());
	}
	private void createElement(IdentificationalType source, com.sun.java.jsf.composite.episodes.EpisodesTableType target)
			throws XmlException {
		if (!(EpisodesTableType.class.isAssignableFrom(source.getClass())))
			return;
		EpisodesTableType episodes = (EpisodesTableType) source;
		this.setIdentifier(source, target);
		if (episodes.isSetVisible()) {
			target.setRendered(createElExpression(episodes.getVisible()));
		}
		target.setConfiguration(episodes.getConfiguration());
		if(episodes.isSetDisabled())target.setDisabled(episodes.getDisabled());
		if(episodes.isSetFocus())target.setFocus(episodes.getFocus());
		if(episodes.isSetMaxYear())target.setMaxYear(episodes.getMaxYear());
		if(episodes.isSetMinYear())target.setMinYear(episodes.getMinYear());
		if(episodes.isSetMaxMonth())target.setMaxMonth(episodes.getMaxMonth());
		if(episodes.isSetMinMonth())target.setMinMonth(episodes.getMinMonth());
		target.setConfiguration(episodes.getConfiguration());
		target.setVar(createVariableReference(episodes.getVariable()));
		if(episodes.isSetLanguage())target.setLang(episodes.getLanguage());
		else target.setLang(createElExpression("sessionController.currentLocale"));
	}
}
