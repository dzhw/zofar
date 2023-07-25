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
package mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.AllTests;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import eu.dzhw.zofar.management.comm.svn.SVNClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
import eu.dzhw.zofar.testing.condition.ConditionAnalyzerClient;


/**
 * @author dick
 * 
 */
@Mojo(name = "conditioncheck-qml")
public class ConditionCheckQML extends AbstractMojo {
	
	final static Logger LOGGER = LoggerFactory.getLogger(ConditionCheckQML.class);
	
    @Parameter(defaultValue = "${contentPath}", required = true)
    private String contentPath;
    @Parameter(defaultValue = "${basedir}", required = true)
    private String basedir;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		this.getLog().info("start condition check");
		System.setProperty("suite", "true");
		System.setProperty("runMode", "maven");
		
		final ConditionAnalyzerClient analyzer = ConditionAnalyzerClient.getInstance();
		
		try {
			analyzer.vectorMap(new File(contentPath));
		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
