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
package eu.dzhw.zofar.management.generator.qml.slc;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.utils.files.FileClient;
/**
 * Generator for QML Code based on Templates specialized to SLC
 * @author meisner
 *
 */
public class QMLGenerator {
	private static final QMLGenerator INSTANCE = new QMLGenerator();
	private static final Logger LOGGER = LoggerFactory.getLogger(QMLGenerator.class);
	private QMLGenerator() {
		super();
	}
	public static QMLGenerator getInstance() {
		return INSTANCE;
	}
	/**
	 * 
	 * main Generator method
	 * @param templates must contain Files named Template_ModulePrefix_cleaned.xml,Template_LoopPrefix_cleaned.xml,Template_SplitPrefix_cleaned.xml,Template_Split_cleaned.xml,Template_SplitPostfix_cleaned.xml,Template_LoopPostfix_cleaned.xml,Template_ModulePostfix_cleaned.xml
	 * @param splitCount Times to generate Code based on Template_Split_cleaned.xml, will decrease each splitLoop (for example first loop 5, second loop 4, third loop 3, ...)
	 * @param loopCount Times to generate Code based on Template_SplitPrefix_cleaned.xml,Template_Split_cleaned.xml and Template_SplitPostfix_cleaned.xml
	 * @param globalReplacements Replacements which are neither loop nor split dependent
	 * @return generated QML Code
	 * @throws Exception
	 * 
	 * 1 * Template_ModulePrefix_cleaned.xml + loopCount * (1 * Template_LoopPrefix_cleaned.xml + splitCount * (1 * Template_SplitPrefix_cleaned.xml + 1 * Template_Split_cleaned.xml + 1 * Template_SplitPostfix_cleaned.xml) + 1 * Template_LoopPostfix_cleaned.xml) + 1 * Template_ModulePostfix_cleaned.xml
	 * 
	 * 	loop and split independent placeholder are:
	 * 
	* [[modul]] id of the current module
	* [[loop00]] 0
	* [[split00]] 0
	* [[loop0]] a
	* [[loop1]] b
	* [[loop2]] c
	* [[loop3]] d
	* [[loop4]] e
	* [[loop5]] f
	* [[loop6]] g
	* [[loop7]] h
	* [[loop8]] i
	* [[loop9]] j
	* [[loop10]] k
	* [[loop11]] l
	* [[loop12]] m
	* [[loop13]] n
	* [[loop14]] o
	* [[loop15]] p
	* [[loop16]] q
	* [[loop17]] r
	* [[loop18]] s
	* [[loop19]] t
	* [[loop20]] u
	* [[loop21]] v
	* [[loop22]] w
	* [[loop23]] x
	* [[loop24]] y
	* [[loop25]] z
	* [[split0]] a
	* [[split1]] b
	* [[split2]] c
	* [[split3]] d
	* [[split4]] e
	* [[split5]] f
	* [[split6]] g
	* [[split7]] h
	* [[split8]] i
	* [[split9]] j
	* [[split10]] k
	* [[split11]] l
	* [[split12]] m
	* [[split13]] n
	* [[split14]] o
	* [[split15]] p
	* [[split16]] q
	* [[split17]] r
	* [[split18]] s
	* [[split19]] t
	* [[split20]] u
	* [[split21]] v
	* [[split22]] w
	* [[split23]] x
	* [[split24]] y
	* [[split25]] z
	 * 
	 * 
	 * loop or split dependent placeholder are:
	 * 
	 * [[loop]] starting with a (a,b,c,d,e,...,z)
	 * [[preLoop]] starting with 0 (0,a,b,c,d,e,...,y)
	 * [[nextLoop]] starting with b (b,c,d,e,...,z)
	 * [[split]] starting with b (b,c,d,e,...,z)
	 * [[preSplit]] starting with a (a,b,c,d,e,...,y)
	 * [[nextSplit]] starting with c (c,d,e,...,z)
	 * 
	 * 
	 */
	public String generate(Map<String, File> templates, int splitCount, final int loopCount,final Map<String,String> globalReplacements)
			throws Exception {
		if (!templates.containsKey("Template_ModulePrefix_cleaned.xml")) 
			throw new Exception("keine Template_ModulePrefix_cleaned.xml gefunden");
		if (!templates.containsKey("Template_ModulePostfix_cleaned.xml"))
			throw new Exception("keine Template_ModulePostfix_cleaned.xml gefunden");
		if (!templates.containsKey("Template_LoopPrefix_cleaned.xml"))
			throw new Exception("keine Template_LoopPrefix_cleaned.xml gefunden");
		if (!templates.containsKey("Template_LoopPostfix_cleaned.xml"))
			throw new Exception("keine Template_LoopPostfix_cleaned.xml gefunden");
		if (!templates.containsKey("Template_SplitPostfix_cleaned.xml"))
			throw new Exception("keine Template_SplitPostfix_cleaned.xml gefunden");
		if (!templates.containsKey("Template_SplitPrefix_cleaned.xml"))
			throw new Exception("keine Template_SplitPrefix_cleaned.xml gefunden");
		if (!templates.containsKey("Template_Split_cleaned.xml"))
			throw new Exception("keine Template_Split_cleaned.xml gefunden");
		if(splitCount <= 0)throw new Exception("Anzahl Splits kleiner/gleich 0");
		if(loopCount <= 0)throw new Exception("Anzahl Loops kleiner/gleich 0");
		final char[] loops = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		final char[] splits = "bcdefghijklmnopqrstuvwxyz".toCharArray();
		final FileClient fileClient = FileClient.getInstance();
		final GenericGenerator module = ModuleGenerator.getInstance();
		final GenericGenerator loop = LoopGenerator.getInstance();
		final GenericGenerator split = SplitGenerator.getInstance();
		final String modulePrefixTemplate = fileClient.readAsString(templates.get("Template_ModulePrefix_cleaned.xml"));
		final String modulePostfixTemplate = fileClient.readAsString(templates.get("Template_ModulePostfix_cleaned.xml"));
		final String loopPrefixTemplate = fileClient.readAsString(templates.get("Template_LoopPrefix_cleaned.xml"));
		final String loopPostfixTemplate = fileClient.readAsString(templates.get("Template_LoopPostfix_cleaned.xml"));
		final String splitPrefixTemplate = fileClient.readAsString(templates.get("Template_SplitPrefix_cleaned.xml"));
		final String splitPostfixTemplate = fileClient.readAsString(templates.get("Template_SplitPostfix_cleaned.xml"));
		final String splitTemplate = fileClient.readAsString(templates.get("Template_Split_cleaned.xml"));
		final StringBuilder moduleContent = new StringBuilder();
		final List<Map<String, String>> modulePrefixReplacements = new ArrayList<Map<String, String>>();
		modulePrefixReplacements.add(globalReplacements);
		final String modulePrefixResult = module.generatePrefix(modulePrefixTemplate, modulePrefixReplacements);
		moduleContent.append(modulePrefixResult + "\n");
		for (int loopIndex = 0; loopIndex < loopCount; loopIndex++) {
			String preLoopID = "0";
			if(loopIndex >= 1)preLoopID = loops[loopIndex-1] + "";
			final String loopID = loops[loopIndex] + "";
			final String nextLoopID = loops[loopIndex+1] + "";
			final StringBuffer loopContent = new StringBuffer();
			final List<Map<String, String>> loopPrefixReplacements = new ArrayList<Map<String, String>>();
			for (int lft = 0; lft < 1; lft++) {
				final Map<String, String> replacement = new HashMap<String, String>();
				replacement.putAll(globalReplacements);
				replacement.put("loop", loopID);
				replacement.put("preLoop", preLoopID);
				replacement.put("nextLoop", nextLoopID);
				loopPrefixReplacements.add(replacement);
			}
			final String loopPrefixResult = loop.generatePrefix(loopPrefixTemplate, loopPrefixReplacements);
			loopContent.append(loopPrefixResult + "\n");
			for (int splitIndex = 0; splitIndex < splitCount; splitIndex++) {
				String preSplitID = "a";
				if(splitIndex >= 1)preSplitID = splits[splitIndex-1] + "";
				final String splitID = splits[splitIndex] + "";
				final String nextSplitID = splits[splitIndex+1] + "";
				final List<Map<String, String>> splitPrefixReplacements = new ArrayList<Map<String, String>>();
				for (int lft = 0; lft < 1; lft++) {
					final Map<String, String> replacement = new HashMap<String, String>();
					replacement.putAll(globalReplacements);
					replacement.put("loop", loopID);
					replacement.put("preLoop", preLoopID);
					replacement.put("nextLoop", nextLoopID);
					replacement.put("split", splitID);
					replacement.put("preSplit", preSplitID);
					replacement.put("nextSplit", nextSplitID);
					splitPrefixReplacements.add(replacement);
				}
				final String splitPrefixResult = split.generatePrefix(splitPrefixTemplate, splitPrefixReplacements);
				loopContent.append(splitPrefixResult + "\n");
				final List<Map<String, String>> splitReplacements = new ArrayList<Map<String, String>>();
				for (int lft = 0; lft < 1; lft++) {
					final Map<String, String> replacement = new HashMap<String, String>();
					replacement.putAll(globalReplacements);
					replacement.put("loop", loopID);
					replacement.put("preLoop", preLoopID);
					replacement.put("nextLoop", nextLoopID);
					replacement.put("split", splitID);
					replacement.put("preSplit", preSplitID);
					replacement.put("nextSplit", nextSplitID);
					splitReplacements.add(replacement);
				}
				final String splitsResult = split.process(splitTemplate, splitReplacements);
				loopContent.append(splitsResult + "\n");
				final List<Map<String, String>> splitPostfixReplacements = new ArrayList<Map<String, String>>();
				for (int lft = 0; lft < 1; lft++) {
					final Map<String, String> replacement = new HashMap<String, String>();
					replacement.putAll(globalReplacements);
					replacement.put("loop", loopID);
					replacement.put("preLoop", preLoopID);
					replacement.put("nextLoop", nextLoopID);
					replacement.put("split", splitID);
					replacement.put("preSplit", preSplitID);
					replacement.put("nextSplit", nextSplitID);
					splitPostfixReplacements.add(replacement);
				}
				final String splitPostfixResult = split.generatePrefix(splitPostfixTemplate, splitPostfixReplacements);
				loopContent.append(splitPostfixResult + "\n");
			}
			splitCount = splitCount - 1;
			final List<Map<String, String>> loopPostfixReplacements = new ArrayList<Map<String, String>>();
			for (int lft = 0; lft < 1; lft++) {
				final Map<String, String> replacement = new HashMap<String, String>();
				replacement.putAll(globalReplacements);
				replacement.put("loop", loopID);
				replacement.put("preLoop", preLoopID);
				replacement.put("nextLoop", nextLoopID);
				loopPostfixReplacements.add(replacement);
			}
			final String loopPostfixResult = loop.generatePostfix(loopPostfixTemplate, loopPostfixReplacements);
			loopContent.append(loopPostfixResult + "\n");
			final List<Map<String, String>> loopReplacements = new ArrayList<Map<String, String>>();
			final Map<String, String> replacements = new HashMap<String, String>();
			replacements.putAll(globalReplacements);
			replacements.put("loop", loopID);
			replacements.put("preLoop", preLoopID);
			replacements.put("nextLoop", nextLoopID);
			loopReplacements.add(replacements);
			final String loopResult = loop.process(loopContent.toString(), loopReplacements);
			moduleContent.append(loopResult + "\n");
		}
		final List<Map<String, String>> modulePostfixReplacements = new ArrayList<Map<String, String>>();
		for (int lft = 0; lft < 1; lft++) {
			final Map<String, String> replacements = new HashMap<String, String>();
			replacements.putAll(globalReplacements);
			modulePostfixReplacements.add(replacements);
		}
		final String postfixResult = module.generatePostfix(modulePostfixTemplate, modulePostfixReplacements);
		moduleContent.append(postfixResult + "\n");
		final List<Map<String, String>> moduleReplacements = new ArrayList<Map<String, String>>();
		moduleReplacements.add(globalReplacements);
		final String moduleResult = module.process(moduleContent.toString(), moduleReplacements);
		return moduleResult;
	}
}
