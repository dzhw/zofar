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
 * Class to generate Certificates
 */
package eu.dzhw.zofar.management.security.certificates;

import java.io.File;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import eu.dzhw.zofar.management.security.certificates.components.CertificateUtils;
import eu.dzhw.zofar.management.security.certificates.components.impl.X509Utils;

/**
 * The Class CertificateClient.
 */
public class CertificateClient {

	/** The instance. */
	private static CertificateClient INSTANCE;

	/**
	 * Instantiates a new certificate client.
	 */
	private CertificateClient() {
		super();
	}

	/**
	 * Gets the single instance of CertificateClient.
	 * 
	 * @return single instance of CertificateClient
	 */
	public static CertificateClient getInstance() {
		if (INSTANCE == null)
			INSTANCE = new CertificateClient();
		return INSTANCE;
	}

	/**
	 * Creates a rsa key pair.
	 * 
	 * @return the key pair
	 * @throws Exception
	 *             the exception
	 */
	public KeyPair createRSAKeyPair() throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		return certTools.generateRSAKeyPair();
	}

	/**
	 * Creates a root certificate.
	 * 
	 * @param subjectDN
	 *            the subject dn
	 * @param pair
	 *            the pair
	 * @param validity
	 *            the validity
	 * @return the certificate
	 * @throws Exception
	 *             the exception
	 */
	public Certificate createRootCertificate(final String subjectDN, final KeyPair pair, final long validity) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		return certTools.createRootCertificate(subjectDN, pair, validity);
	}

	/**
	 * Creates a intermediated certificate.
	 * 
	 * @param subjectDN
	 *            the subject dn
	 * @param pubKey
	 *            the pub key
	 * @param caKey
	 *            the ca key
	 * @param caCert
	 *            the ca cert
	 * @param validity
	 *            the validity
	 * @return the certificate
	 * @throws Exception
	 *             the exception
	 */
	public Certificate createIntermediatedCertificate(String subjectDN, PublicKey pubKey, PrivateKey caKey, Certificate caCert, long validity) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		return certTools.createDerivedCert(subjectDN, pubKey, caKey, caCert, validity, CertificateUtils.TYPE.INTERMEDIATED);
	}
	
	/**
	 * Creates a end user certificate.
	 * 
	 * @param subjectDN
	 *            the subject dn
	 * @param pubKey
	 *            the pub key
	 * @param caKey
	 *            the ca key
	 * @param caCert
	 *            the ca cert
	 * @param validity
	 *            the validity
	 * @return the certificate
	 * @throws Exception
	 *             the exception
	 */
	public Certificate createEndUserCertificate(String subjectDN, PublicKey pubKey, PrivateKey caKey, Certificate caCert, long validity) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		return certTools.createDerivedCert(subjectDN, pubKey, caKey, caCert, validity, CertificateUtils.TYPE.END);
	}
	
	/**
	 * Save certificate.
	 * 
	 * @param cert
	 *            the cert
	 * @param certFile
	 *            the cert file
	 * @throws Exception
	 *             the exception
	 */
	public void saveCertificate(final Certificate cert, final File certFile) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		certTools.saveCertificate(cert, certFile);
	}
	
	public void verifyCertificate(final Certificate cert, final PublicKey pubKey) throws Exception {
	    	if((cert != null)&&(pubKey != null))cert.verify(pubKey);
	}
	
	public String getInfo(final Certificate cert) {
	    	if(cert == null)return null;
	    	CertificateUtils certTools = X509Utils.getInstance();
	    	return certTools.getCertInfo(cert);
	}
	
	public String getInfo(final Key key) {
	    	if(key == null)return null;
	    	CertificateUtils certTools = X509Utils.getInstance();
	    	return certTools.getKeyInfo(key);
	}
	
	public void savePrivateKey(final PrivateKey key, final File file) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		certTools.saveKey(key, file);
	}
	
	public void savePublicKey(final PublicKey key, final File file) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		certTools.saveKey(key, file);
	}
	
	/**
	 * Load certificate.
	 * 
	 * @param certFile
	 *            the cert file
	 * @return the certificate
	 * @throws Exception
	 *             the exception
	 */
	public Certificate loadCertificate(final File certFile) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		return certTools.loadCertificate(certFile);
	}
	
	/**
	 * Load unprotected private pem key.
	 * 
	 * @param keyFile
	 *            the key file
	 * @return the private key
	 * @throws Exception
	 *             the exception
	 */
	public PrivateKey loadPrivatePEMKey(final File keyFile) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		return certTools.getPEMPrivateKey(keyFile);
	}
	
	/**
	 * Load password protected private pem key.
	 * 
	 * @param keyFile
	 *            the key file
	 * @param password
	 *            the password
	 * @return the private key
	 * @throws Exception
	 *             the exception
	 */
	public PrivateKey loadPrivatePEMKey(final File keyFile,final String password) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		return certTools.getPEMPrivateKey(keyFile,password);
	}
	
	/**
	 * Encrypt file.
	 * 
	 * @param inputFile
	 *            the input file
	 * @param outputFile
	 *            the output file
	 * @param cert
	 *            the cert
	 * @return the file
	 * @throws Exception
	 *             the exception
	 */
	public File encryptFile(final File inputFile,final File outputFile,final Certificate cert) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		return certTools.encrypt(inputFile, outputFile, cert);
	}
	
	/**
	 * Encrypt string.
	 * 
	 * @param inputContent
	 *            the input content
	 * @param outputFile
	 *            the output file
	 * @param cert
	 *            the cert
	 * @return the file
	 * @throws Exception
	 *             the exception
	 */
	public File encryptString(final String inputContent,final File outputFile,final Certificate cert) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		return certTools.encrypt(inputContent, outputFile, cert);
	}
	
	/**
	 * Decrypt file.
	 * 
	 * @param inputFile
	 *            the input file
	 * @param outputFile
	 *            the output file
	 * @param privateKey
	 *            the private key
	 * @param cert
	 *            the cert
	 * @return the file
	 * @throws Exception
	 *             the exception
	 */
	public File decryptFile(final File inputFile,final File outputFile,final PrivateKey privateKey, final Certificate cert) throws Exception {
		CertificateUtils certTools = X509Utils.getInstance();
		return certTools.decrypt(inputFile, outputFile, privateKey, cert);
	}

}
