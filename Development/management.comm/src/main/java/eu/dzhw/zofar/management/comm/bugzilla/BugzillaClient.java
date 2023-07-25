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
 * Class to communicate to BugZilla Instance
 */
package eu.dzhw.zofar.management.comm.bugzilla;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.j2bugzilla.base.Attachment;
import com.j2bugzilla.base.AttachmentFactory;
import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugFactory;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.BugzillaException;
import com.j2bugzilla.base.ConnectionException;
import com.j2bugzilla.rpc.AddAttachment;
import com.j2bugzilla.rpc.GetBug;
import com.j2bugzilla.rpc.LogIn;
import com.j2bugzilla.rpc.ReportBug;
/**
 * The Class BugzillaClient.
 */
public class BugzillaClient {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BugzillaClient.class);
	/** The Constant INSTANCE. */
	private static final BugzillaClient INSTANCE = new BugzillaClient();
	/**
	 * Instantiates a new bugzilla client.
	 */
	private BugzillaClient() {
		super();
	}
	/**
	 * Gets the single instance of BugzillaClient.
	 *
	 * @return single instance of BugzillaClient
	 */
	public static BugzillaClient getInstance() {
		return INSTANCE;
	}
	/**
	 * Gets connection to Bugzilla instance.
	 *
	 * @param url the url
	 * @return the connection
	 */
	public BugzillaConnector getConnection(final String url) {
		try {
			final BugzillaConnector conn = new BugzillaConnector();
			conn.connectTo(url);
			return conn;
		} catch (final ConnectionException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Login.
	 *
	 * @param conn the conn
	 * @param username the username
	 * @param password the password
	 * @return true, if successful
	 */
	public boolean login(final BugzillaConnector conn, final String username, final String password) {
		if (conn == null)
			return false;
		try {
			final LogIn logIn = new LogIn(username, password);
			conn.executeMethod(logIn);
			return true;
		} catch (final BugzillaException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * Creates the bug.
	 *
	 * @param product the product
	 * @param component the component
	 * @param version the version
	 * @param plattform the plattform
	 * @param operationSystem the operation system
	 * @param description the description
	 * @param summary the summary
	 * @return the bug
	 */
	public Bug createBug(final String product, final String component, final String version, final String plattform, final String operationSystem, final String description, final String summary) {
		final Bug bug = new BugFactory().newBug().setProduct(product).setComponent(component).setVersion(version).setPlatform(plattform).setOperatingSystem(operationSystem)
				.setDescription(description).setSummary(summary).setAlias(UUID.randomUUID().toString().substring(0, 20)).createBug();
		return bug;
	}
	/**
	 * Update bug.
	 *
	 * @param conn the conn
	 * @param bug the bug
	 * @return the bug
	 */
	public Bug updateBug(final BugzillaConnector conn, final Bug bug) {
		final Bug updatedbug = this.getBug(conn, bug.getAlias());
		return updatedbug;
	}
	/**
	 * Creates a pdf attachment.
	 *
	 * @param data the data
	 * @param name the name
	 * @param bug the bug
	 * @return the attachment
	 */
	public Attachment createPdfAttachment(final byte[] data, final String name, final Bug bug) {
		return this.createAttachment(data, name,"application/pdf", bug);
	}
	/**
	 * Creates a text attachment.
	 *
	 * @param data the data
	 * @param name the name
	 * @param bug the bug
	 * @return the attachment
	 */
	public Attachment createTextAttachment(final byte[] data, final String name, final Bug bug) {
		return this.createAttachment(data, name,"text/plain", bug);
	}
	/**
	 * Creates a xml attachment.
	 *
	 * @param data the data
	 * @param name the name
	 * @param bug the bug
	 * @return the attachment
	 */
	public Attachment createXmlAttachment(final byte[] data, final String name, final Bug bug) {
		return this.createAttachment(data, name,"text/xml", bug);
	}
	/**
	 * Creates a word attachment.
	 *
	 * @param data the data
	 * @param name the name
	 * @param bug the bug
	 * @return the attachment
	 */
	public Attachment createWordAttachment(final byte[] data, final String name, final Bug bug) {
		return this.createAttachment(data, name,"application/msword", bug);
	}
	/**
	 * Creates a attachment.
	 *
	 * @param data the data
	 * @param name the name
	 * @param mime the mime
	 * @param bug the bug
	 * @return the attachment
	 */
	private Attachment createAttachment(final byte[] data, final String name, final String mime, final Bug bug) {
		LOGGER.info("create Attachment bug {}", this.getBugInfo(bug));
		final AttachmentFactory factory = new AttachmentFactory();
		factory.newAttachment().setData(data).setMime(mime).setName(name).setBugID(Integer.parseInt(bug.getParameterMap().get("id") + "")).setCreationDate(new Date()).setSummary("Summary")
				.setCreator("Creator");
		final Attachment attachment = factory.createAttachment();
		return attachment;
	}
	/**
	 * Adds the attachement to a bug.
	 *
	 * @param conn the conn
	 * @param attachment the attachment
	 * @param bug the bug
	 * @return true, if successful
	 */
	public boolean addAttachement(final BugzillaConnector conn, final Attachment attachment, final Bug bug) {
		final AddAttachment report = new AddAttachment(attachment, bug);
		LOGGER.info("updated bug {}", this.getBugInfo(bug));
		LOGGER.info("attachment {}", this.getAttachmentInfo(attachment));
		try {
			conn.executeMethod(report);
			return true;
		} catch (final BugzillaException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * Gets a bug from Bugzilla Instance by alias.
	 *
	 * @param conn the conn
	 * @param alias the alias
	 * @return the bug
	 */
	public Bug getBug(final BugzillaConnector conn, final String alias) {
		final GetBug action = new GetBug(alias);
		try {
			conn.executeMethod(action);
			return action.getBug();
		} catch (final BugzillaException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Report a bug to Bugzilla Instance.
	 *
	 * @param conn the conn
	 * @param bug the bug
	 * @return true, if successful
	 */
	public boolean report(final BugzillaConnector conn, final Bug bug) {
		final ReportBug report = new ReportBug(bug);
		try {
			conn.executeMethod(report);
			return true;
		} catch (final BugzillaException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * Gets the bug info.
	 *
	 * @param bug the bug
	 * @return the bug info
	 */
	private String getBugInfo(final Bug bug) {
		if (bug == null)
			return null;
		final StringBuffer buffer = new StringBuffer();
		final Map<Object, Object> parameters = bug.getParameterMap();
		for (final Map.Entry<Object, Object> parameter : parameters.entrySet()) {
			buffer.append(parameter.getKey() + " : " + parameter.getValue() + "\n");
		}
		return buffer.toString();
	}
	/**
	 * Gets the attachment info.
	 *
	 * @param attachment the attachment
	 * @return the attachment info
	 */
	private String getAttachmentInfo(final Attachment attachment) {
		if (attachment == null)
			return null;
		final StringBuffer buffer = new StringBuffer();
		buffer.append("Bug ID : " + attachment.getBugID() + "\n");
		buffer.append("Attachment ID : " + attachment.getAttachmentID() + "\n");
		buffer.append("File Name : " + attachment.getFileName() + "\n");
		buffer.append("Creator : " + attachment.getCreator() + "\n");
		buffer.append("Summary : " + attachment.getSummary() + "\n");
		buffer.append("Raw Data : " + Arrays.toString(attachment.getRawData()) + "\n");
		return buffer.toString();
	}
}
