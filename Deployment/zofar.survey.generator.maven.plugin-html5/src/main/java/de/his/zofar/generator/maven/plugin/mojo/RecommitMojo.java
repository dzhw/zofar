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
package de.his.zofar.generator.maven.plugin.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import eu.dzhw.zofar.management.comm.svn.SVNClient;


@Mojo(name = "recommit-qml")
public class RecommitMojo  extends AbstractMojo{
	
	@Parameter(defaultValue = "${project}", required = true)
	private MavenProject project;
	
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire_generated.xml", required = true)
	private File generatedQML;
	
	@Parameter(defaultValue = "${recommit_qml}", required = false)
	private boolean recommit_qml;
	    
    @Parameter(defaultValue = "qmlrecommit", required = false)
    private String svnUser;
    
    @Parameter(defaultValue = "tsdR(&2uH=S_a=-!", required = false)
    private String svnPass;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		if(!recommit_qml)return;
		
		
		
		
		if((generatedQML != null)&&(svnUser != null)&&(svnPass != null)){
			try{
				SVNClient svn = SVNClient.getInstance();
				svn.doCommit(svnUser, svnPass, generatedQML, "automated recommit");
			}
			catch (Exception e) {
				throw new MojoFailureException(e.getMessage());
			}
		}
		
	}

}
