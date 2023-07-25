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
package eu.dzhw.zofar.management.utils.string;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class ReplaceClient.
 */
public class ReplaceClient {
	/** The Constant INSTANCE. */
	private static final ReplaceClient INSTANCE = new ReplaceClient();
	/** The Constant LOGGER. */
	private final static Logger LOGGER = LoggerFactory.getLogger(ReplaceClient.class);
	/**
	 * Instantiates a new replace client.
	 */
	private ReplaceClient() {
		super();
	}
	/**
	 * Gets the single instance of ReplaceClient.
	 * 
	 * @return single instance of ReplaceClient
	 */
	public static synchronized ReplaceClient getInstance() {
		return INSTANCE;
	}
	/**
	 * Replace String between tags.
	 * 
	 * @param content
	 *            the content
	 * @param startTag
	 *            the start tag
	 * @param stopTag
	 *            the stop tag
	 * @param replacement
	 *            the replacement
	 * @return the string
	 */
	public String replaceBetweenTags(final String content, final String startTag, final String stopTag, final String replacement) {
		final String back = content.replaceAll("(" + Pattern.quote(startTag) + ")((?!" + Pattern.quote(stopTag) + ").)*(" + Pattern.quote(stopTag) + ")", replacement);
		return back;
	}
	public String replaceTag(final String content,final String tag, final String replacement) {
		final String back = content.replaceAll(Pattern.quote(tag), replacement);
		return back;
	}
	/**
	 * Strip leading breaks.
	 * 
	 * @param content
	 *            the content
	 * @return the string
	 */
	public String stripLeadingBreaks(final String content) {
		final String back = content.replaceAll("^\\n*", "");
		return back;
	}
	/**
	 * Find in string.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param content
	 *            the content
	 * @return the list
	 */
	public List<String> findInString(final String pattern, final String content) {
		return this.findInString(pattern, content, false);
	}
	public List<String> findInString(final String pattern, final String content,final boolean caseSensitive) {
		Pattern modulePattern = null;
		if(!caseSensitive) modulePattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		else modulePattern = Pattern.compile(pattern);
		final Matcher matcher = modulePattern.matcher(content);
		final List<String> back = new ArrayList<String>();
		if (matcher.find()) {
			final String found = matcher.group();
			back.add(found);
		}
		return back;
	}
	public List<String> findInString(final String pattern, final String content,final boolean caseSensitive,final int patternSubGroup) {
		Pattern modulePattern = null;
		if(!caseSensitive) modulePattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		else modulePattern = Pattern.compile(pattern);
		final Matcher matcher = modulePattern.matcher(content);
		final List<String> back = new ArrayList<String>();
		if (matcher.find()) {
			final String found = matcher.group(patternSubGroup);
			back.add(found);
		}
		return back;
	}
	public List<String> findAllInString(final String pattern, final String content,final boolean caseSensitive) {
		Pattern modulePattern = null;
		if(!caseSensitive) modulePattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		else modulePattern = Pattern.compile(pattern);
		final Matcher matcher = modulePattern.matcher(content);
		final List<String> back = new ArrayList<String>();
		while (matcher.find()) {
			final String found = matcher.group();
			if(!back.contains(found))back.add(found);
		}
		return back;
	}
	/**
	 * Check if List of haystack contains needle.
	 * 
	 * @param haystack
	 *            the haystack
	 * @param needle
	 *            the needle
	 * @return true, if successful
	 */
	public boolean contains(final List<String> haystack, final String needle) {
		if (haystack == null)
			return false;
		if (haystack.isEmpty())
			return false;
		if (needle == null)
			return false;
		for (final String item : haystack) {
			if (item.startsWith(needle))
				return true;
		}
		return false;
	}
	/**
	 * Cleaned string from Tabs,Linebreaks, multiple blanks and #{layout.BREAK} Zofar-Tags.
	 * 
	 * @param input
	 *            the input
	 * @return the string
	 */
	public String cleanedString(final String input) {
		String output = input;
		output = output.replaceAll("\\Q#{layout.\\E[^\\Q}\\E]+}", "");
		output = output.replaceAll("\n", "");
		output = output.replaceAll("\t", " ");
		output = output.replaceAll(" {2,}", " ");
		output = output.trim();
		return output;
	}
	/**
	 * Strip HTML-Tags from input.
	 * 
	 * @param str
	 *            the input
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String stripHTML(final String input) throws IOException {
		if (input == null)
			return null;
		return Jsoup.parse(input).text();
	}
}
