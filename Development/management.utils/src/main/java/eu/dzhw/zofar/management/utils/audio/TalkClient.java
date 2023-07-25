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
package eu.dzhw.zofar.management.utils.audio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
public class TalkClient {
	/** The Constant INSTANCE. */
	private static final TalkClient INSTANCE = new TalkClient();
	/** The Constant LOGGER. */
	final static Logger LOGGER = LoggerFactory.getLogger(TalkClient.class);
	private static final String VOICENAME_kevin = "kevin16";
	private  TalkClient() {
		super();
	}
	/**
	 * Gets the single instance of TalkClient.
	 * 
	 * @return single instance of TalkClient
	 */
	public static synchronized TalkClient getInstance() {
		return INSTANCE;
	}
	public void talk(final String text){
	    VoiceManager voiceManager = VoiceManager.getInstance();
	    Voice[] voices = voiceManager.getVoices();
	    for (int i = 0; i < voices.length; i++) {
	     System.out.println("    " + voices[i].getName() + " ("
	       + voices[i].getDomain() + " domain)");
	    }
	    Voice voice = voiceManager.getVoice("kevin16"); 
		voice.allocate();
		voice.speak(text);
	}
}
