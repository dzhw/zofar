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
package eu.dzhw.zofar.management.security.files;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.properties.EncryptableProperties;

public class EncryptedFileClient {

	/** The instance. */
	private static EncryptedFileClient INSTANCE;

	/**
	 * Instantiates a new certificate client.
	 */
	private EncryptedFileClient() {
		super();
	}

	/**
	 * Gets the single instance of CertificateClient.
	 * 
	 * @return single instance of CertificateClient
	 */
	public static EncryptedFileClient getInstance() {
		if (INSTANCE == null)
			INSTANCE = new EncryptedFileClient();
		return INSTANCE;
	}
	
	public Properties encryptProps(final String propsPath,final String encpath,final String password) throws IOException {
		
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		 encryptor.setPassword(password);
		 encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
		 encryptor.setIvGenerator(new RandomIvGenerator());
		
		final InputStream in = new FileInputStream(propsPath);
		final Properties props = new Properties();
		props.load(in);
		
		EncryptableProperties encprops = new EncryptableProperties(encryptor);
		for(Map.Entry<Object,Object> property:props.entrySet()) {
			encprops.setProperty(property.getKey()+"", "ENC("+encryptor.encrypt(property.getValue()+"")+")");
		}
		
		encprops.store(new FileOutputStream(encpath), "");
		
		return encprops;
	}
	
	public Properties decodeProps(final String encodedPropsPath,final String password) throws IOException {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(password);
		encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
		encryptor.setIvGenerator(new RandomIvGenerator());

		final InputStream in = new FileInputStream(encodedPropsPath);
		Properties encprops = new EncryptableProperties(encryptor);
		encprops.load(in);
		
		Properties props = new Properties();
		
		for(Object encpropertyKey :encprops.keySet()) {
			final String key = (String)encpropertyKey;
			props.setProperty(key, encprops.getProperty(key));
		}
		return props;
	}

}
