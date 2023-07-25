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
package eu.dzhw.zofar.management.utils.system;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
/**
 * The Class ConfigurationUtils.
 */
public class ConfigurationUtils {
	/** The instance. */
	private static ConfigurationUtils INSTANCE;
	/**
	 * Instantiates a new configuration utils.
	 */
	protected ConfigurationUtils() {
		super();
	}
	/**
	 * Gets the single instance of ConfigurationUtils.
	 * 
	 * @return single instance of ConfigurationUtils
	 */
	public static ConfigurationUtils getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ConfigurationUtils();
		return INSTANCE;
	}
	/**
	 * Gets the system configuration (system.properties from Resource Directory)
	 * 
	 * @return the system configuration
	 */
	public Properties getSystemConfiguration() {
		return this.getConfiguration("system.properties");
	}
	/**
	 * Gets the configuration located at path (XML or classical Properties
	 * format)
	 * 
	 * @param path
	 *            the path
	 * @return the configuration
	 */
	public Properties getConfiguration(final String path) {
		final Properties prop = new Properties();
		if (path != null) {
			try {
				final InputStream in = ConfigurationUtils.class.getResourceAsStream(("/" + path).replaceAll("/{2,}", "/"));
				if (in != null) {
					if (path.endsWith(".properties"))
						prop.load(in);
					else if (path.endsWith(".xml"))
						prop.loadFromXML(in);
					in.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return prop;
	}
	/**
	 * Gets the configuration located at path (XML or classical Properties
	 * format)
	 * 
	 * @param path
	 *            the path
	 * @return the configuration
	 */
	public Properties getConfigurationFromFileSystem(final String path) {
		final Properties prop = new Properties();
		if (path != null) {
			try {
				final InputStream in = FileUtils.openInputStream(new File(path.replaceAll("/{2,}", "/")));
				prop.load(new InputStreamReader(in, "UTF-8"));
				if (in != null) {
					if (path.endsWith(".properties"))
						prop.load(in);
					else if (path.endsWith(".xml"))
						prop.loadFromXML(in);
					in.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return prop;
	}
	public boolean saveConfiguration(final Properties props, final String path) {
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(path);
			if (path.endsWith(".properties"))props.store(stream, null);
			else if (path.endsWith(".xml"))
				props.storeToXML(stream, null);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return false;
	}
	public void show(Properties props){
		for(final Entry<Object, Object> pair : props.entrySet())System.out.println("Prop : "+pair.getKey()+" = "+pair.getValue());
	}
}
