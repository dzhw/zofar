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
 * SSH Client to communicate secured with remote server
 */
package eu.dzhw.zofar.management.comm.ssh;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import eu.dzhw.zofar.management.utils.objects.CollectionClient;
/**
 * The Class SSHClient.
 */
public class SSHClient {
	/**
	 * The Class MyUserInfo.
	 */
	private class MyUserInfo implements UserInfo {
		/*
		 * (non-Javadoc)
		 *
		 * @see com.jcraft.jsch.UserInfo#getPassphrase()
		 */
		@Override
		public String getPassphrase() {
			LOGGER.error("MyUserInfo.getPassphrase not implemented yet");
			return null;
		}
		/*
		 * (non-Javadoc)
		 *
		 * @see com.jcraft.jsch.UserInfo#getPassword()
		 */
		@Override
		public String getPassword() {
			LOGGER.error("MyUserInfo.getPassword not implemented yet");
			return null;
		}
		/*
		 * (non-Javadoc)
		 *
		 * @see com.jcraft.jsch.UserInfo#promptPassphrase(java.lang.String)
		 */
		@Override
		public boolean promptPassphrase(final String arg0) {
			return false;
		}
		/*
		 * (non-Javadoc)
		 *
		 * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
		 */
		@Override
		public boolean promptPassword(final String arg0) {
			return false;
		}
		/*
		 * (non-Javadoc)
		 *
		 * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
		 */
		@Override
		public boolean promptYesNo(final String arg0) {
			return true;
		}
		/*
		 * (non-Javadoc)
		 *
		 * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
		 */
		@Override
		public void showMessage(final String arg0) {
			LOGGER.info(arg0);
		}
	};
	public static class MyLogger implements com.jcraft.jsch.Logger {
		static java.util.Hashtable name = new java.util.Hashtable();
		static {
			name.put(new Integer(DEBUG), "DEBUG: ");
			name.put(new Integer(INFO), "INFO: ");
			name.put(new Integer(WARN), "WARN: ");
			name.put(new Integer(ERROR), "ERROR: ");
			name.put(new Integer(FATAL), "FATAL: ");
		}
		public boolean isEnabled(int level) {
			return true;
		}
		public void log(int level, String message) {
			System.err.print(name.get(new Integer(level)));
			System.err.println(message);
		}
	}
	/** The Constant INSTANCE. */
	private static final SSHClient INSTANCE = new SSHClient();
	/** The Constant LOGGER. */
	final static Logger LOGGER = LoggerFactory.getLogger(SSHClient.class);
	/** The known hosts file. */
	private File knownHostsFile;
	/**
	 * Instantiates a new SSH client.
	 */
	private SSHClient() {
		super();
		JSch.setLogger(new MyLogger());
	}
	/**
	 * Gets the single instance of SSHClient.
	 *
	 * @return single instance of SSHClient
	 */
	public static synchronized SSHClient getInstance() {
		return INSTANCE;
	}
	/**
	 * Gets the known hosts file.
	 *
	 * @return the known hosts file
	 */
	private File getKnownHostsFile() {
		if (this.knownHostsFile == null) {
			final String dirPath = System.getProperty("user.home") + File.separator + ".zofar";
			final File dir = new File(dirPath);
			if (!dir.exists()) {
				try {
					FileUtils.forceMkdir(dir);
				} catch (final IOException e) {
					LOGGER.error("", e);
				}
			}
			this.knownHostsFile = new File(dir, "ssh_known_hosts");
			if (!this.knownHostsFile.exists()) {
				try {
					this.knownHostsFile.createNewFile();
				} catch (final IOException e) {
					LOGGER.error("", e);
				}
			}
		}
		return this.knownHostsFile;
	}
	/**
	 * Gets a list of kown hosts.
	 *
	 * @return the kown hosts
	 */
	public HostKey[] getKownHosts() {
		try {
			final JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFile().getAbsolutePath());
			final HostKeyRepository hkr = jsch.getHostKeyRepository();
			final HostKey[] hks = hkr.getHostKey();
			return hks;
		} catch (final Exception e) {
			LOGGER.error("", e);
		}
		return null;
	}
	/**
	 * Gets an secured console.
	 *
	 * @param server the server
	 * @param user   the user
	 * @param pass   the pass
	 * @return the console
	 */
	public void getConsole(final String server, final String user, final String pass) {
		try {
			final JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFile().getAbsolutePath());
			final Session session = jsch.getSession(user, server, 22);
			session.setPassword(pass);
			final UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.connect(30000); 
			final Channel channel = session.openChannel("shell");
			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);
			channel.connect(3 * 1000);
		} catch (final Exception e) {
			LOGGER.error("", e);
		}
	}
	private class DummyUserInfo implements UserInfo {
		final List<String> passwords;
		final Iterator<String> it;
		public DummyUserInfo(final List<String> passwords) {
			super();
			this.passwords = passwords;
			this.it = this.passwords.iterator();
		}
		@Override
		public String getPassphrase() {
			String back = null;
			if (it.hasNext()) {
				back = this.it.next();
			}
			return back;
		}
		@Override
		public String getPassword() {
			String back = null;
			if (it.hasNext()) {
				back = this.it.next();
			}
			return back;
		}
		@Override
		public boolean promptPassphrase(final String arg0) {
			return true;
		}
		@Override
		public boolean promptPassword(final String arg0) {
			return true;
		}
		@Override
		public boolean promptYesNo(final String arg0) {
			return true;
		}
		@Override
		public void showMessage(final String arg0) {
		}
	};
	public Session connect(final String server, final String user, final String password) throws JSchException {
		final ArrayList<String> passwords = new ArrayList<String>();
		passwords.add(password);
		return this.connectByHop(null, 0, 22, server, user, passwords, null);
	}
	public Session connect(final String server, final String user, final ArrayList<String> passwords,
			final Map<String, String> identities) throws JSchException {
		return this.connectByHop(null, 0, 22, server, user, passwords, identities);
	}
	public Session connectByHop(final Session parent, final int localPort, final int remotePort, final String server,
			final String user, final ArrayList<String> passwords, final Map<String, String> identities)
			throws JSchException {
		final JSch jsch = new JSch();
		jsch.setKnownHosts(getKnownHostsFile().getAbsolutePath());
		if (identities != null) {
			for (final Map.Entry<String, String> identity : identities.entrySet()) {
				jsch.addIdentity(identity.getKey(), identity.getValue());
			}
		}
		Session session = null;
		String host = server;
		int port = 22;
		if (parent != null) {
			port = parent.setPortForwardingL(localPort, server, remotePort);
			host = "127.0.0.1";
		}
		session = jsch.getSession(user, host, port);
		session.setUserInfo(new DummyUserInfo(passwords));
		session.setHostKeyAlias(server);
		final Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		return session;
	}
	public Session connectByProxy(final String proxyServer, final String proxyUser,
			final ArrayList<String> proxyPasswords, final Map<String, String> proxyIdentities,
			final String targetServer, final String targetUser, final ArrayList<String> targetPasswords,
			final Map<String, String> targetIdentities) throws JSchException, IOException {
		final JSch jsch = new JSch();
		jsch.setKnownHosts(getKnownHostsFile().getAbsolutePath());
		if (proxyIdentities != null) {
			for (final Map.Entry<String, String> identity : proxyIdentities.entrySet()) {
				jsch.addIdentity(identity.getKey(), identity.getValue());
			}
		}
		final Session proxySession = jsch.getSession(proxyUser, proxyServer, 22);
		proxySession.setUserInfo(new DummyUserInfo(proxyPasswords));
		proxySession.setHostKeyAlias(proxyServer);
		final Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		proxySession.setConfig(config);
		final int proxyPort = proxySession.setPortForwardingL(0, targetServer, 22);
		proxySession.connect();
		proxySession.openChannel("direct-tcpip");
		final Session secondSession = jsch.getSession(targetUser, "####", proxyPort);
		secondSession.setUserInfo(new DummyUserInfo(targetPasswords));
		secondSession.setConfig("StrictHostKeyChecking", "no");
		secondSession.connect();
		return secondSession;
	}
	public Session sshTunnel(final String proxyServer, final String proxyUser, final String proxyPass,
			final String targetServer, final String targetUser, final String targetPass, final int localPort) throws JSchException {
		return this.tunnel(proxyServer, proxyUser, proxyPass, targetServer, targetUser, targetPass, 22, localPort, null);
	}
	public Session tunnel(final String proxyServer, final String proxyUser, final String proxyPass,
			final String targetServer, final String targetUser, final String targetPass, final int targetPort,
			final int localPort) throws JSchException {
		return this.tunnel(proxyServer, proxyUser, proxyPass, targetServer, targetUser, targetPass, targetPort, localPort, null);
	}
	public Session tunnel(final String proxyServer, final String proxyUser, final String proxyPass,
			final String targetServer, final String targetUser, final String targetPass, final int targetPort,
			final int localPort, final com.jcraft.jsch.Logger logger) throws JSchException {
		JSch.setLogger(logger);
		final JSch jsch = new JSch();
		jsch.setKnownHosts(getKnownHostsFile().getAbsolutePath());
		final Session session = jsch.getSession(proxyUser, proxyServer, 22);
		session.setPassword(proxyPass);
		final Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		session.setPortForwardingL(localPort, targetServer, targetPort);
		return session;
	}
	public int retrieveFreePort() throws IOException {
		Integer back = -1;
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			back = socket.getLocalPort();
		}
		finally {
			if(socket != null)socket.close();
		}
		return back;
	}
	public List<String> exec(final Session session, final String cmd) throws Exception {
		if(session == null)throw new Exception("Session is null");
		if(!session.isConnected())throw new Exception("Session is not connected");
		if(session.getPort() != 22) {
			System.out.println("Session : "+session.getHost()+" "+session.getPort());
		}
		final Channel c = session.openChannel("exec");
		final ChannelExec ce = (ChannelExec) c;
		ce.setCommand(cmd);
		final InputStream in = ce.getInputStream();
		final OutputStream out = ce.getOutputStream();
		ce.setErrStream(System.err);
		ce.connect(session.getTimeout());
		final List<String> back = new ArrayList<String>();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = reader.readLine()) != null) {
			back.add(line);
		}
		ce.disconnect();
		if (ce.getExitStatus() != 0) {
			throw new IOException(
					"Command failed Exit Status : " + ce.getExitStatus() + " Response : " + back.toString());
		}
		return back;
	}
	@SuppressWarnings("finally")
	public List<Object> remotSudoCmd(final Session session, final String sudoPass,
			final String cmd) throws Exception {
		if(session == null)throw new Exception("Session is null");
		if(!session.isConnected())throw new Exception("Session is not connected");
		final List<Object> back = new ArrayList<Object>();
		ChannelExec ce = null;
		try {
			final Channel c = session.openChannel("exec");
			ce = (ChannelExec) c;
			ce.setPty(true);
			ce.setCommand("sudo -S -p '' " + cmd);
			System.out.println("send Command : sudo -S -p '' " + cmd + " to " + session.getHost());
			final InputStream in = ce.getInputStream();
			final OutputStream out = ce.getOutputStream();
			ce.setErrStream(System.err);
			ce.connect();
			out.write((sudoPass + "\n").getBytes());
			out.flush();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				back.add(line);
			}
			if (ce.getExitStatus() == 1)throw new IOException(CollectionClient.getInstance().implode(back));
		} catch (Exception e) {
			throw new IOException("Failed to excecute Sudo Command", e);
		} finally {
			if (ce != null)
				ce.disconnect();
			if (session != null)
				session.disconnect();
			return back;
		}
	}
	public void remotSudoCmd(final String server, final String user, final String pass, final String sudoPass,
			final String cmd) throws JSchException, IOException {
		Session session = null;
		ChannelExec ce = null;
		try {
			final JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFile().getAbsolutePath());
			session = jsch.getSession(user, server, 22);
			session.setPassword(pass);
			session.setOutputStream(System.out);
			final Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			final Channel c = session.openChannel("exec");
			ce = (ChannelExec) c;
			ce.setPty(true);
			ce.setCommand("sudo -S -p '' " + cmd);
			final InputStream in = ce.getInputStream();
			final OutputStream out = ce.getOutputStream();
			ce.setErrStream(System.err);
			ce.connect();
			out.write((sudoPass + "\n").getBytes());
			out.flush();
			final StringBuffer result = new StringBuffer();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
					result.append(new String(tmp, 0, i));
				}
				if (ce.isClosed()) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			System.out.println("exit-status: " + ce.getExitStatus() + " Result : " + result.toString());
			if (ce.getExitStatus() == 1)
				throw new IOException(result.toString());
		} catch (Exception e) {
			throw new IOException("Failed to excecute Sudo Command", e);
		} finally {
			if (ce != null)
				ce.disconnect();
			if (session != null)
				session.disconnect();
		}
	}
	/**
	 * Secured copy to Server.
	 *
	 * @param server the server
	 * @param user   the user
	 * @param pass   the pass
	 * @param from   the local source File
	 * @param toPath the path to target dir at remote server
	 */
	public void scpTo(final String server, final String user, final String pass, final File from, final String toPath) {
		try {
			final JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFile().getAbsolutePath());
			final Session session = jsch.getSession(user, server, 22);
			session.setPassword(pass);
			final UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();
			final String lfile = from.getAbsolutePath();
			final String rfile = toPath + "/" + from.getName();
			FileInputStream fis = null;
			try {
				String command = "scp -t " + rfile;
				final Channel channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(command);
				final OutputStream out = channel.getOutputStream();
				final InputStream in = channel.getInputStream();
				channel.connect();
				if (checkAck(in) != 0) {
					return;
				}
				final File _lfile = new File(lfile);
				final long filesize = _lfile.length();
				command = "C0644 " + filesize + " ";
				if (lfile.lastIndexOf('/') > 0) {
					command += lfile.substring(lfile.lastIndexOf('/') + 1);
				} else {
					command += lfile;
				}
				command += "\n";
				out.write(command.getBytes());
				out.flush();
				if (checkAck(in) != 0) {
					return;
				}
				fis = new FileInputStream(lfile);
				final byte[] buf = new byte[1024];
				while (true) {
					final int len = fis.read(buf, 0, buf.length);
					if (len <= 0) {
						break;
					}
					out.write(buf, 0, len); 
				}
				fis.close();
				fis = null;
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
				if (checkAck(in) != 0) {
					return;
				}
				out.close();
				channel.disconnect();
				session.disconnect();
				return;
			} catch (final Exception e) {
				LOGGER.error("", e);
				try {
					if (fis != null) {
						fis.close();
					}
				} catch (final Exception ee) {
				}
			}
		} catch (final Exception e) {
			LOGGER.error("", e);
		}
	}
	public void scpTo(final Session session, final File from, final String toPath) {
		try {
			final String lfile = from.getAbsolutePath();
			final String rfile = toPath + File.separator + from.getName();
			FileInputStream fis = null;
			try {
				String command = "scp -t " + rfile;
				final Channel channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(command);
				final OutputStream out = channel.getOutputStream();
				final InputStream in = channel.getInputStream();
				channel.connect();
				if (checkAck(in) != 0) {
					return;
				}
				final File _lfile = new File(lfile);
				final long filesize = _lfile.length();
				command = "C0644 " + filesize + " ";
				if (lfile.lastIndexOf('/') > 0) {
					command += lfile.substring(lfile.lastIndexOf('/') + 1);
				} else {
					command += lfile;
				}
				command += "\n";
				out.write(command.getBytes());
				out.flush();
				if (checkAck(in) != 0) {
					return;
				}
				fis = new FileInputStream(lfile);
				final byte[] buf = new byte[1024];
				while (true) {
					final int len = fis.read(buf, 0, buf.length);
					if (len <= 0) {
						break;
					}
					out.write(buf, 0, len); 
				}
				fis.close();
				fis = null;
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
				if (checkAck(in) != 0) {
					return;
				}
				out.close();
				channel.disconnect();
				return;
			} catch (final Exception e) {
				LOGGER.error("", e);
				try {
					if (fis != null) {
						fis.close();
					}
				} catch (final Exception ee) {
				}
			}
		} catch (final Exception e) {
			LOGGER.error("", e);
		}
	}
	public File scpFrom(final Session session, final String from) {
		FileOutputStream fos = null;
		try {
			final File back = File.createTempFile(from.replaceAll(Pattern.quote(File.separator), "_") + "_", "_loaded");
			final String command = "scp -f " + from;
			final Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			final OutputStream out = channel.getOutputStream();
			final InputStream in = channel.getInputStream();
			channel.connect();
			final byte[] buf = new byte[1024];
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			while (true) {
				final int c = checkAck(in);
				if (c != 'C') {
					break;
				}
				in.read(buf, 0, 5);
				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						break;
					}
					if (buf[0] == ' ') {
						break;
					}
					filesize = ((filesize * 10L) + buf[0]) - '0';
				}
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						break;
					}
				}
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
				fos = new FileOutputStream(back.getAbsolutePath());
				int foo;
				while (true) {
					if (buf.length < filesize) {
						foo = buf.length;
					} else {
						foo = (int) filesize;
					}
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L) {
						break;
					}
				}
				fos.close();
				fos = null;
				if (checkAck(in) != 0) {
					break;
				}
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}
			channel.disconnect();
			session.disconnect();
			return back;
		} catch (final Exception e) {
			LOGGER.error("", e);
		}
		return null;
	}
	public File scpFrom(final String server, final String privkeyFilePath, final String passphrase, final String user, final int port, final String from) {
		FileOutputStream fos = null;
		try {
			final JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFile().getAbsolutePath());
			 jsch.addIdentity(privkeyFilePath,passphrase);
			final Session session = jsch.getSession(user, server, port);
			final UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();
			final File back = File.createTempFile(from.replaceAll(Pattern.quote(File.separator), "_") + "_", "_loaded");
			final String command = "scp -f " + from;
			final Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			final OutputStream out = channel.getOutputStream();
			final InputStream in = channel.getInputStream();
			channel.connect();
			final byte[] buf = new byte[1024];
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			while (true) {
				final int c = checkAck(in);
				if (c != 'C') {
					break;
				}
				in.read(buf, 0, 5);
				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						break;
					}
					if (buf[0] == ' ') {
						break;
					}
					filesize = ((filesize * 10L) + buf[0]) - '0';
				}
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						break;
					}
				}
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
				fos = new FileOutputStream(back.getAbsolutePath());
				int foo;
				while (true) {
					if (buf.length < filesize) {
						foo = buf.length;
					} else {
						foo = (int) filesize;
					}
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L) {
						break;
					}
				}
				fos.close();
				fos = null;
				if (checkAck(in) != 0) {
					System.exit(0);
				}
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}
			channel.disconnect();
			session.disconnect();
			return back;
		} catch (final Exception e) {
			LOGGER.error("", e);
		}
		return null;
	}
	/**
	 * Secured copy from Server.
	 *
	 * @param server the server
	 * @param user   the user
	 * @param pass   the pass
	 * @param from   the remote source path
	 * @return the file
	 */
	public File scpFrom(final String server, final String user, final String pass, final String from) {
		FileOutputStream fos = null;
		try {
			final JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFile().getAbsolutePath());
			final Session session = jsch.getSession(user, server, 22);
			session.setPassword(pass);
			final UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();
			final File back = File.createTempFile(from.replaceAll(Pattern.quote(File.separator), "_") + "_", "_loaded");
			final String command = "scp -f " + from;
			final Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			final OutputStream out = channel.getOutputStream();
			final InputStream in = channel.getInputStream();
			channel.connect();
			final byte[] buf = new byte[1024];
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			while (true) {
				final int c = checkAck(in);
				if (c != 'C') {
					break;
				}
				in.read(buf, 0, 5);
				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						break;
					}
					if (buf[0] == ' ') {
						break;
					}
					filesize = ((filesize * 10L) + buf[0]) - '0';
				}
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						break;
					}
				}
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
				fos = new FileOutputStream(back.getAbsolutePath());
				int foo;
				while (true) {
					if (buf.length < filesize) {
						foo = buf.length;
					} else {
						foo = (int) filesize;
					}
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L) {
						break;
					}
				}
				fos.close();
				fos = null;
				if (checkAck(in) != 0) {
					System.exit(0);
				}
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}
			channel.disconnect();
			session.disconnect();
			return back;
		} catch (final Exception e) {
			LOGGER.error("", e);
		}
		return null;
	}
	/**
	 * Check ack.
	 *
	 * @param in the in
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private int checkAck(final InputStream in) throws IOException {
		final int b = in.read();
		if (b == 0) {
			return b;
		}
		if (b == -1) {
			return b;
		}
		if ((b == 1) || (b == 2)) {
			final StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { 
				LOGGER.error(sb.toString());
			}
			if (b == 2) { 
				LOGGER.error(sb.toString());
			}
		}
		return b;
	}
}
