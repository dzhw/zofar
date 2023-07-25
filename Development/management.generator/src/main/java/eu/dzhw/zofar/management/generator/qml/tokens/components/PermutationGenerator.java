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
package eu.dzhw.zofar.management.generator.qml.tokens.components;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.utils.string.ReplaceClient;
import eu.dzhw.zofar.management.utils.string.StringUtils;
public class PermutationGenerator {
	private static final PermutationGenerator INSTANCE = new PermutationGenerator();
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PermutationGenerator.class);
	private PermutationGenerator() {
		super();
	}
	public static PermutationGenerator getInstance() {
		return INSTANCE;
	}
	private List<String> doComputations(String inputString, final int length) {
		List<String> totalList = new ArrayList<String>();
		totalList.addAll(getCombinationsPerLength(inputString, length));
		return totalList;
	}
	private ArrayList<String> getCombinationsPerLength(String inputString, int i) {
		ArrayList<String> combinations = new ArrayList<String>();
		if (i == 1) {
			char[] charArray = inputString.toCharArray();
			for (int j = 0; j < charArray.length; j++) {
				combinations.add(((Character) charArray[j]).toString());
			}
			return combinations;
		}
		for (int j = 0; j < inputString.length(); j++) {
			ArrayList<String> combs = getCombinationsPerLength(inputString, i - 1);
			for (String string : combs) {
				combinations.add(inputString.charAt(j) + string);
			}
		}
		return combinations;
	}
	public List<String> calculatePermutations(final String chars,final int minLength,final int maxLength,final int count,final boolean shuffel,final Set<String> ignorePattern) throws Exception{
		PermutationGenerator permutationGenerator = new PermutationGenerator();
		int neededSlots = 1;
		StringUtils stringUtils = StringUtils.getInstance();
		while(neededSlots<=maxLength){
			List<String> possiblePermutations =  permutationGenerator.doComputations(chars,neededSlots);
			if((ignorePattern != null)&&(!ignorePattern.isEmpty())) {
				final ReplaceClient replacer = ReplaceClient.getInstance();
				List<String> filteredPossiblePermutations = new ArrayList<String>();
				for(final String token : possiblePermutations){
					boolean ignore = false;
					for(final String pattern : ignorePattern){
						final List<String> tmp = replacer.findInString(pattern, token);
						if(!tmp.isEmpty()) {
							ignore = true;
						}
					}
					if(ignore)continue;
					filteredPossiblePermutations.add(token);
				}
				possiblePermutations = filteredPossiblePermutations;
			}
			if(possiblePermutations.size() >= count){
				if(shuffel)Collections.shuffle(possiblePermutations);
				if(neededSlots < minLength){
					final List<String> filledUpPossiblePermutations = new ArrayList<String>();
					for(final String token : possiblePermutations.subList(0, count)){
						String salt = stringUtils.randomString(chars, minLength-neededSlots);
						final String filledUpToken = token+salt;
						filledUpPossiblePermutations.add(filledUpToken);
					}
					possiblePermutations = filledUpPossiblePermutations;
				}
				return possiblePermutations.subList(0, count);
			}
			neededSlots = neededSlots+1;
		}
		throw new Exception("too less variations of Characters for "+maxLength+" digits");
	}
	public List<String> calculatePermutations(final String chars,final int minLength,final int maxLength,final int count,final boolean shuffel) throws Exception{
		PermutationGenerator permutationGenerator = new PermutationGenerator();
		int neededSlots = 1;
		StringUtils stringUtils = StringUtils.getInstance();
		while(neededSlots<=maxLength){
			List<String> possiblePermutations =  permutationGenerator.doComputations(chars,neededSlots);
			if(possiblePermutations.size() >= count){
				if(shuffel)Collections.shuffle(possiblePermutations);
				if(neededSlots < minLength){
					final List<String> filledUpPossiblePermutations = new ArrayList<String>();
					for(final String token : possiblePermutations.subList(0, count)){
						String salt = stringUtils.randomString(chars, minLength-neededSlots);
						final String filledUpToken = token+salt;
						filledUpPossiblePermutations.add(filledUpToken);
					}
					possiblePermutations = filledUpPossiblePermutations;
				}
				return possiblePermutations.subList(0, count);
			}
			neededSlots = neededSlots+1;
		}
		throw new Exception("too less variations of Characters for "+maxLength+" digits");
	}
}
