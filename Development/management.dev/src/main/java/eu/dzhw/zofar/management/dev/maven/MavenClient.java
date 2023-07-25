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
package eu.dzhw.zofar.management.dev.maven;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.comm.network.download.Loader;
import eu.dzhw.zofar.management.comm.network.download.impl.MavenLoader;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
import eu.dzhw.zofar.management.utils.files.PackagerClient;
public class MavenClient {
	final static Logger LOGGER = LoggerFactory.getLogger(MavenClient.class);
	private static final MavenClient INSTANCE = new MavenClient();
	private final String MAVEN_VERSION = "apache-maven-3.0.4";
	private MavenClient() {
		super();
	}
	/**
	 * @return the singleton of MavenService
	 */
	public static MavenClient getInstance() {
		return MavenClient.INSTANCE;
	}
	// loader.getContent("http://archive.apache.org/dist/maven/binaries/apache-maven-"+version+"-bin.tar.gz");
	private File getMavenHome() {
		final DirectoryClient dirClient = DirectoryClient.getInstance();
		final File mavenDir = new File(dirClient.getTemp(), this.MAVEN_VERSION);
		if (!mavenDir.exists()) {
			final Loader loader = new MavenLoader();
			File maven = null;
			try {
				final String version = this.MAVEN_VERSION.replaceAll("apache-maven-", "");
				maven = loader.getContent("http://archive.apache.org/dist/maven/binaries/apache-maven-" + version + "-bin.tar.gz");
			} catch (final IOException e) {
				e.printStackTrace();
			}
			if (maven != null) {
				MavenClient.LOGGER.info("maven: {}", maven);
				final List<File> content = PackagerClient.getInstance().extractTarGZ(maven);
				if (content != null) {
					final FileClient fileClient = FileClient.getInstance();
					for (final File item : content) {
						fileClient.copyToDir(item, mavenDir);
					}
				}
				maven.delete();
			}
		}
		if (mavenDir.exists())
			return mavenDir;
		return null;
	}
	private File getMavenExecutable() {
		final File mavenHome = this.getMavenHome();
		if (mavenHome == null)
			return null;
		if (!mavenHome.exists())
			return null;
		final File mavenExec = new File(mavenHome.getAbsoluteFile() + File.separator + "bin" + File.separator + "mvn");
		if (!mavenExec.exists())
			return null;
		if (!mavenExec.canExecute())
			mavenExec.setExecutable(true, true);
		return mavenExec;
	}
	private String getTargetName(final File pom) {
		if ((pom != null) && (pom.exists())) {
			Reader reader = null;
			try {
				reader = new FileReader(pom);
				final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
				final Model model = xpp3Reader.read(reader);
				final String version = model.getVersion();
				final String artifactId = model.getArtifactId();
				final String packagingFormat = model.getPackaging();
				final String finalName = model.getBuild().getFinalName();
				if (reader != null)
					reader.close();
				if((finalName != null)&&(!finalName.equals(""))){
					return finalName + "." + packagingFormat;
				}
				else return artifactId + "-" + version + "." + packagingFormat;
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			} catch (final XmlPullParserException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public File getPom(final File projectDir){
		final File pom = new File(projectDir.getAbsolutePath() + File.separator + "pom.xml");
		return pom;
	}
	public File doCleanInstall(final File projectDir) throws MavenInvocationException {
		return this.doCleanInstall(projectDir, new File(DirectoryClient.getInstance().getHome().getAbsolutePath() + File.separator + ".m2" + File.separator +"settings.xml"));
	}
	public File doCleanInstall(final File projectDir,final File mavenSettings) throws MavenInvocationException {
		final InvocationRequest request = new DefaultInvocationRequest();
		final File pom = getPom(projectDir);
		MavenClient.LOGGER.info("POM ({}) : {}", pom.exists(), pom);
		request.setPomFile(pom);
		final List<String> goals = new ArrayList<String>();
		goals.add("clean");
		goals.add("install");
		request.setGoals(goals);
		request.setBaseDirectory(projectDir);
		request.setGlobalSettingsFile(mavenSettings);
		request.setMavenOpts("-Dmaven.multiModuleProjectDirectory="+this.getMavenHome().getAbsolutePath());
		final Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(this.getMavenHome());
		invoker.setMavenExecutable(this.getMavenExecutable());
		invoker.setWorkingDirectory(projectDir);
		invoker.execute(request);
		System.out.println("request base : "+request.getBaseDirectory());
		System.out.println("Target Name : "+this.getTargetName(pom));
		final File target = new File(projectDir.getAbsolutePath() + File.separator + "target" + File.separator + this.getTargetName(pom));
		return target;
	}
	public File doCleanPackage(final File projectDir,final File mavenSettings) throws MavenInvocationException {
		final InvocationRequest request = new DefaultInvocationRequest();
		final File pom = getPom(projectDir);
		MavenClient.LOGGER.info("POM ({}) : {}", pom.exists(), pom);
		request.setPomFile(pom);
		final List<String> goals = new ArrayList<String>();
		goals.add("clean");
		goals.add("package");
		request.setGoals(goals);
		request.setGlobalSettingsFile(mavenSettings);
		request.setMavenOpts("-Dmaven.multiModuleProjectDirectory="+this.getMavenHome().getAbsolutePath());
		request.setBaseDirectory(projectDir);
		final Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(this.getMavenHome());
		invoker.setMavenExecutable(this.getMavenExecutable());
		invoker.setWorkingDirectory(projectDir);
		invoker.execute(request);
		System.out.println("Target Name : "+this.getTargetName(pom));
		final File target = new File(projectDir.getAbsolutePath() + File.separator + "target" + File.separator + this.getTargetName(pom));
		return target;
	}
	public File doClean(final File projectDir,final File mavenSettings) throws MavenInvocationException {
		final InvocationRequest request = new DefaultInvocationRequest();
		final File pom = new File(projectDir.getAbsolutePath() + File.separator + "pom.xml");
		request.setPomFile(pom);
		final List<String> goals = new ArrayList<String>();
		goals.add("clean");
		request.setGoals(goals);
		request.setGlobalSettingsFile(mavenSettings);
		request.setMavenOpts("-Dmaven.multiModuleProjectDirectory="+this.getMavenHome().getAbsolutePath());
		final Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(this.getMavenHome());
		invoker.setMavenExecutable(this.getMavenExecutable());
		invoker.setWorkingDirectory(projectDir);
		invoker.execute(request);
		return null;
	}
	public File createProject(final String groupId, final String artifactId, final String archetypeGroupId, final String archetypeArtifactId, final String archetypeVersion,final File baseDir) {
		return this.createProjectFromArchetype(groupId, artifactId,null,null,null, baseDir,new File(DirectoryClient.getInstance().getHome().getAbsolutePath() + File.separator + ".m2" + File.separator +"settings.xml"));
	}
	public File createProject(final String groupId, final String artifactId,final File baseDir,final File mavenSettings) {
		return this.createProjectFromArchetype(groupId, artifactId,null,null,null, baseDir,mavenSettings);
	}
	public File createProjectFromArchetype(final String groupId, final String artifactId, final String archetypeGroupId, final String archetypeArtifactId, final String archetypeVersion,final File baseDir) {
		return this.createProjectFromArchetype(groupId, artifactId,archetypeGroupId,archetypeArtifactId,archetypeVersion, baseDir,new File(DirectoryClient.getInstance().getHome().getAbsolutePath() + File.separator + ".m2" + File.separator +"settings.xml"));
	}
	public File createProjectFromArchetype(final String groupId, final String artifactId, final String archetypeGroupId, final String archetypeArtifactId, final String archetypeVersion,final File baseDir,final File mavenSettings) {
		final InvocationRequest request = new DefaultInvocationRequest();
		final List<String> goals = new ArrayList<String>();
		goals.add("archetype:generate");
		request.setGoals(goals);
		request.setGlobalSettingsFile(mavenSettings);
		request.setMavenOpts("-Dmaven.multiModuleProjectDirectory="+this.getMavenHome().getAbsolutePath());
		request.setInteractive(false);
		final Properties properties = new Properties();
		properties.setProperty("groupId", groupId);
		properties.setProperty("artifactId", artifactId);
		if(archetypeGroupId != null)properties.setProperty("archetypeGroupId", archetypeGroupId);
		if(archetypeArtifactId != null)properties.setProperty("archetypeArtifactId", archetypeArtifactId);
		if(archetypeVersion != null)properties.setProperty("archetypeVersion", archetypeVersion);
		request.setProperties(properties);
		request.setBaseDirectory(baseDir);
		final Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(this.getMavenHome());
		invoker.setMavenExecutable(this.getMavenExecutable());
		try {
			final InvocationResult result = invoker.execute(request);
			return new File(baseDir,artifactId);
		} catch (final MavenInvocationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
