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
package eu.dzhw.zofar.management.comm.mail;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
public class ReceiveFileMailClient extends AbstractMailClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveFileMailClient.class);
	private Map<Message,File> map;
	private File seen;
	private File unseen;
	private static final ReceiveFileMailClient INSTANCE = new ReceiveFileMailClient();
	private ReceiveFileMailClient() {
		super();
		map = new HashMap<Message,File>();
	}
	public static ReceiveFileMailClient getInstance() {
		return INSTANCE;
	}
	public Map<String, List<Message>> loadMessages(final File folder) throws MessagingException, IOException, ChunkNotFoundException {
		if (folder == null)
			return null;
		final List<File> files = DirectoryClient.getInstance().readDir(folder);
		if (files == null)
			return null;
		if (files.isEmpty())
			return null;
		final String folderName = folder.getPath();
		seen = DirectoryClient.getInstance().createDir(folder, "seen");
		DirectoryClient.getInstance().cleanDirectory(seen);
		unseen = DirectoryClient.getInstance().createDir(folder, "unseen");
		DirectoryClient.getInstance().cleanDirectory(unseen);
		Map<String, List<Message>> back = new HashMap<String, List<Message>>();
		for (final File file : files) {
			if (file.isDirectory()) {
				if(file.equals(seen))continue;
				if(file.equals(unseen))continue;
				back.putAll(loadMessages(file));
			} else {
				String suffix = FilenameUtils.getExtension(file.getAbsolutePath());
				if(!suffix.equals("msg"))continue;
				MAPIMessage message = new MAPIMessage(file);
				List<Message> messageBag = null;
				if (back.containsKey(folderName))
					messageBag = back.get(folderName);
				if (messageBag == null)
					messageBag = new ArrayList<Message>();
				final Message converted = convert(message);
				map.put(converted, file);
				messageBag.add(converted);
				back.put(folderName, messageBag);
			}
		}
		return back;
	}
	@Override
	public void markFolder(Object folder,Message[] messages, Flags flags, boolean value) throws MessagingException {
		if(folder == null)return;
		if((File.class).isAssignableFrom(folder.getClass())){
			if(messages != null){
				for(Message message:messages){
					final File file = map.get(message);
					if(file != null){
						if(value){
							if(flags.contains(javax.mail.Flags.Flag.SEEN))FileClient.getInstance().copyToDir(file, seen);
						}
						else{
							if(flags.contains(javax.mail.Flags.Flag.SEEN))FileClient.getInstance().copyToDir(file, unseen);
						}
					}
				}
			}
		}
	}
	private Message convert(MAPIMessage msg) throws MessagingException, ChunkNotFoundException {
		if (msg == null)
			return null;
		Properties props = new Properties();
		javax.mail.Session session = javax.mail.Session.getDefaultInstance(props);
		Message mimeMsg = new MimeMessage(session);
		try {
			String[] headers = msg.getHeaders();
			if(headers != null){
				for(String header : headers ){
					String[] splitted = header.split(":");
					if((splitted != null)&&(splitted.length == 2)){
						mimeMsg.addHeader(splitted[0].trim(), splitted[1].trim());
					}
				}
			}
		} catch (ChunkNotFoundException e) {
		}
		try {
			String body = msg.getTextBody();
			mimeMsg.setText(body);
		} catch (ChunkNotFoundException e) {
			System.err.println("No message body");
		}
		return mimeMsg;
	}
}
