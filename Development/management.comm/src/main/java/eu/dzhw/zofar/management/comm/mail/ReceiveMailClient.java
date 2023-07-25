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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.comm.mail.components.EmailAuthenticator;
public class ReceiveMailClient extends AbstractMailClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveMailClient.class);
	private static final ReceiveMailClient INSTANCE = new ReceiveMailClient();
	private Store store;
	private Set<Folder> folders;
	private ReceiveMailClient() {
		super();
	}
	public static ReceiveMailClient getInstance() {
		return INSTANCE;
	}
	public void openStore(final String smtp, final int port, final EmailAuthenticator auth) throws MessagingException {
		final Properties props = System.getProperties();
		props.put("mail.smtp.host", smtp);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.socketFactory.port", port+"");
		final Session session = Session.getDefaultInstance(props, auth);
		store = session.getStore("imaps");
		store.connect(smtp, auth.getPasswordAuthentication().getUserName(), auth.getPasswordAuthentication().getPassword());
	}
	public Folder openFolder(final String folderPath) throws MessagingException {
		if (store == null)
			throw new MessagingException("Store is closed");
		final Folder folder = store.getFolder(folderPath);
		return openFolder(folder);
	}
	public Folder openFolder(final Folder folder) throws MessagingException {
		if (store == null)
			throw new MessagingException("Store is closed");
		if (folders == null)
			folders = new HashSet<Folder>();
		if (!folders.contains(folder)) {
			if ((folder != null) && (!folder.isOpen()))
				folder.open(Folder.READ_WRITE);
			folders.add(folder);
		}
		return folder;
	}
	public Map<String, List<Message>> loadMessages(final Folder folder, final SearchTerm searchTerm) throws MessagingException {
		return loadMessages(folder, searchTerm, folder);
	}
	private Map<String, List<Message>> loadMessages(final Folder folder, final SearchTerm searchTerm, final Folder baseFolder) throws MessagingException {
		openFolder(folder);
		Message[] messages = null;
		if (searchTerm == null)
			messages = folder.getMessages();
		else {
			LOGGER.info("Searching...");
			messages = folder.search(searchTerm);
		}
		Map<String, List<Message>> back = new HashMap<String, List<Message>>();
		if (messages != null) {
			for (Message message : messages) {
				String folderName = "UNKOWN";
				final Folder parentFolder = message.getFolder();
				if (parentFolder != null)
					folderName = parentFolder.getFullName();
				folderName = folderName.replaceAll(baseFolder.getFullName(), "");
				if(folderName.equals(""))folderName="EMPTY";
				List<Message> messageBag = null;
				if (back.containsKey(folderName))
					messageBag = back.get(folderName);
				if (messageBag == null)
					messageBag = new ArrayList<Message>();
				messageBag.add(message);
				back.put(folderName, messageBag);
			}
		}
		final Folder[] subFolders = folder.list("*");
		if (subFolders != null) {
			for (Folder subFolder : subFolders) {
				back.putAll(loadMessages(subFolder, searchTerm, baseFolder));
			}
		}
		return back;
	}
	@Override
	public void markFolder(Object folder,Message[] messages, Flags flags, boolean value) throws MessagingException {
		if(folder == null)return;
		if((Folder.class).isAssignableFrom(folder.getClass()))((Folder)folder).setFlags(messages,flags, value);
	}
}
