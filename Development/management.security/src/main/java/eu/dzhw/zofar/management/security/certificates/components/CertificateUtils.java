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
 * Abstract Class to generate Certificates
 */
package eu.dzhw.zofar.management.security.certificates.components;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.bc.BcRSAKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.bc.BcRSAKeyTransRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.util.encoders.Base64;

import eu.dzhw.zofar.management.security.certificates.exceptions.CustomKeyException;

/**
 * The Class CertificateUtils.
 */
public abstract class CertificateUtils {
    
    /**
     * The Enum TYPE.
     */
    public enum TYPE {
	
	/** The root. */
	ROOT,
	/** The intermediated. */
	INTERMEDIATED,
	/** The end. */
	END
    }
    
    /** The instance. */
    protected static CertificateUtils INSTANCE;
    
    /**
     * Instantiates a new certificate utils.
     */
    protected CertificateUtils() {
	super();
    }
    
    /**
     * Encrypt file.
     * 
     * @param input
     *            the input
     * @param output
     *            the output
     * @param cert
     *            the cert
     * @return the file
     * @throws Exception
     *             the exception
     */
    public File encrypt(final File input, final File output, final Certificate cert) throws Exception {
	List<X509Certificate> certs = new ArrayList<X509Certificate>();
	certs.add(asX509(cert));
	encryptHelper(certs, input, output);
	return output;
    }
    
    /**
     * Encrypt String.
     * 
     * @param input
     *            the input
     * @param output
     *            the output
     * @param cert
     *            the cert
     * @return the file
     * @throws Exception
     *             the exception
     */
    public File encrypt(final String input, final File output, final Certificate cert) throws Exception {
	List<X509Certificate> certs = new ArrayList<X509Certificate>();
	certs.add(asX509(cert));
	encryptHelper(certs, input.getBytes(), output);
	return output;
    }
    
    /**
     * Decrypt file.
     * 
     * @param input
     *            the input
     * @param output
     *            the output
     * @param privateKey
     *            the private key
     * @param cert
     *            the cert
     * @return the file
     * @throws CertificateException
     *             the certificate exception
     * @throws CMSException
     *             the CMS exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public File decrypt(final File input, final File output, PrivateKey privateKey, Certificate cert) throws Exception {
	decryptHelper(input, output, privateKey, asX509(cert));
	return output;
    }
    
    /**
     * convert Certificate to x509.
     * 
     * @param cert
     *            the cert
     * @return the x509 certificate
     * @throws CertificateException
     *             the certificate exception
     */
    private X509Certificate asX509(Certificate cert) throws CertificateException {
	CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
	InputStream in = new ByteArrayInputStream(cert.getEncoded());
	return (X509Certificate) certFactory.generateCertificate(in);
    }
    
    /**
     * Read file as byte array.
     * 
     * @param file
     *            the file
     * @return the byte[]
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private byte[] readFile(File file) throws java.io.IOException {
	return FileUtils.readFileToByteArray(file);
    }
    
    /**
     * Encrypt helper.
     * 
     * @param certs
     *            the certs
     * @param message
     *            the message
     * @param output
     *            the output
     * @throws CertificateEncodingException
     *             the certificate encoding exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws CMSException
     *             the CMS exception
     */
    private void encryptHelper(List<X509Certificate> certs, File message, File output) throws Exception {
	encryptHelper(certs, readFile(message), output);
    }
    
    /**
     * Encrypt helper.
     * 
     * @param certs
     *            the certs
     * @param message
     *            the message
     * @param output
     *            the output
     * @throws CertificateEncodingException
     *             the certificate encoding exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws CMSException
     *             the CMS exception
     */
    private void encryptHelper(List<X509Certificate> certs, byte[] message, File output) throws Exception {
	CMSTypedData msg = new CMSProcessableByteArray(message);
	CMSEnvelopedDataGenerator edGen = new CMSEnvelopedDataGenerator();
	Set<X509Certificate> certSet = new HashSet<X509Certificate>(certs);
	for (X509Certificate cert : certSet) {
	    if (cert != null) {
		JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder(cert);
		edGen.addRecipientInfoGenerator(new BcRSAKeyTransRecipientInfoGenerator(certHolder));
	    }
	}
	CMSEnvelopedData ed = edGen.generate(msg, new JceCMSContentEncryptorBuilder(CMSAlgorithm.DES_EDE3_CBC).setProvider("BC").build());
	
	FileOutputStream fos = new FileOutputStream(output);
	fos.write(ed.getEncoded());
	fos.close();
    }
    
    /**
     * Decrypt helper.
     * 
     * @param message
     *            the message
     * @param output
     *            the output
     * @param privateKey
     *            the private key
     * @param cert
     *            the cert
     * @throws CMSException
     *             the CMS exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void decryptHelper(File message, File output, PrivateKey privateKey, X509Certificate cert) throws Exception {
	CMSEnvelopedDataParser ed = new CMSEnvelopedDataParser(readFile(message));
	RecipientInformationStore ripStore = ed.getRecipientInfos();
	BcRSAKeyTransEnvelopedRecipient privKeyRecipient = new BcRSAKeyTransEnvelopedRecipient(PrivateKeyFactory.createKey(privateKey.getEncoded()));
	KeyTransRecipientId myID = new JceKeyTransRecipientId(cert);
	if(ripStore.getRecipients(myID) == null)throw new CertificateException("wrong Certificate");
	if(ripStore.getRecipients(myID).size() == 0)throw new CertificateException("wrong Certificate");
	Iterator<RecipientInformation> it = ripStore.getRecipients(myID).iterator();
	while (it.hasNext()) {
	    KeyTransRecipientInformation recipient = (KeyTransRecipientInformation) it.next();
	    byte[] decrypted = recipient.getContent(privKeyRecipient);
	    FileOutputStream fos = new FileOutputStream(output);
	    fos.write(decrypted);
	    fos.close();
	    return;
	}
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
    public abstract Certificate createRootCertificate(final String subjectDN, KeyPair pair, long validity) throws Exception;
    
    /**
     * Creates a derived cert.
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
     * @param type
     *            the type
     * @return the certificate
     * @throws Exception
     *             the exception
     */
    public abstract Certificate createDerivedCert(final String subjectDN, PublicKey pubKey, PrivateKey caKey, Certificate caCert, long validity, TYPE type) throws Exception;
    
    /**
     * Save certificate.
     * 
     * @param certificate
     *            the certificate
     * @param certFile
     *            the cert file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void saveCertificate(Certificate certificate, File certFile) throws Exception {
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	PEMWriter pemWriter = new PEMWriter(new FileWriter(certFile));
	pemWriter.writeObject(fromCertificate(certificate));
	pemWriter.flush();
	pemWriter.close();
    }
    
    public void saveKey(PublicKey key, File file) throws Exception {
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	PEMWriter pemWriter = new PEMWriter(new FileWriter(file));
	pemWriter.writeObject(fromPublicKey(key));
	pemWriter.flush();
	pemWriter.close();
    }
    
    public void saveKey(PrivateKey key, File file) throws Exception {
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	PEMWriter pemWriter = new PEMWriter(new FileWriter(file));
	pemWriter.writeObject(fromPrivateKey(key));
	pemWriter.flush();
	pemWriter.close();
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
    public Certificate loadCertificate(File certFile) throws Exception {
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	PEMParser pemParser = new PEMParser(new FileReader(certFile));
	final Object back = pemParser.readObject();
	pemParser.close();
	return toCertificate(back);
    }
    
    /**
     * Gets a unprotected PEM private key from file.
     * 
     * @param keyFile
     *            the key file
     * @return the PEM private key
     * @throws Exception
     *             the exception
     */
    public PrivateKey getPEMPrivateKey(File keyFile) throws Exception {
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	
	BufferedReader br = new BufferedReader(new FileReader(keyFile));
	String line;
	StringBuffer keyBuf = new StringBuffer();
	while ((line = br.readLine()) != null) {
	    if (!(line.startsWith("-----BEGIN") || line.startsWith("-----END"))) {
		keyBuf.append(line);
		keyBuf.append("\n");
	    }
	}
	br.close();
	
	PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.decode(keyBuf.toString()));
	
	KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
	return kf.generatePrivate(spec);
    }
    
    /**
     * Gets a password protected PEM private key from file.
     * 
     * @param keyFile
     *            the key file
     * @param password
     *            the password
     * @return the PEM private key
     * @throws Exception
     *             the exception
     */
    public PrivateKey getPEMPrivateKey(File keyFile, final String password) throws Exception {
	throw new CustomKeyException("Not implemented yet");
    }
    
    /**
     * To certificate.
     * 
     * @param certificate
     *            the certificate
     * @return the certificate
     * @throws Exception
     *             the exception
     */
    protected abstract Certificate toCertificate(Object certificate) throws Exception;
    
    /**
     * To public key.
     * 
     * @param publicKey
     *            the public key
     * @return the public key
     */
    protected abstract PublicKey toPublicKey(Object publicKey);
    
    /**
     * To private key.
     * 
     * @param privateKey
     *            the private key
     * @param passphrase
     *            the passphrase
     * @return the private key
     */
    protected abstract PrivateKey toPrivateKey(Object privateKey, String passphrase);
    
    /**
     * From certificate.
     * 
     * @param certificate
     *            the certificate
     * @return the object
     */
    protected abstract Object fromCertificate(Certificate certificate) throws Exception;
    
    /**
     * From public key.
     * 
     * @param publicKey
     *            the public key
     * @return the object
     */
    protected abstract Object fromPublicKey(PublicKey publicKey);
    
    /**
     * From private key.
     * 
     * @param privateKey
     *            the private key
     * @return the object
     */
    protected abstract Object fromPrivateKey(PrivateKey privateKey);
    
    /**
     * Gets the key info.
     * 
     * @param key
     *            the key
     * @return the key info
     */
    public String getKeyInfo(Key key) {
	if (key == null)
	    return null;
	final StringBuffer back = new StringBuffer();
	back.append("Key Format = " + key.getFormat() + " ");
	back.append("Algorithm = " + key.getAlgorithm() + "");
	return back.toString().trim();
    }
    
    public String getCertInfo(Certificate cert) {
	if (cert == null)
	    return null;
	final StringBuffer back = new StringBuffer();
	back.append("Type = " + cert.getType() + " ");
	if ((X509Certificate.class).isAssignableFrom(cert.getClass())) {
	    X509Certificate tmp = (X509Certificate) cert;
	    back.append("Lifetime = " + tmp.getNotBefore() + " - " + tmp.getNotAfter());
	    back.append("Version = " + tmp.getVersion() + " ");
	    back.append("Serial = " + tmp.getSerialNumber() + " ");
	    back.append("Subject.Name = " + tmp.getSubjectX500Principal().getName() + " ");
	}
	back.append("PublicKey.Algorithm = " + cert.getPublicKey().getAlgorithm() + " ");
	back.append("PublicKey.Format = " + cert.getPublicKey().getFormat() + " ");
	
	return back.toString().trim();
    }
    
    /**
     * Generate a rsa key pair.
     * 
     * @return the key pair
     * @throws Exception
     *             the exception
     */
    public KeyPair generateRSAKeyPair() throws Exception {
	Security.addProvider(new BouncyCastleProvider());
	KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
	kpGen.initialize(2048, new SecureRandom());
	return kpGen.generateKeyPair();
    }
    
    /**
     * Generate a des key.
     * 
     * @return the secret key
     * @throws CustomKeyException 
     */
    public SecretKey generateDESKey() throws CustomKeyException {
	Security.addProvider(new BouncyCastleProvider());
	try {
	    final KeyGenerator kg = KeyGenerator.getInstance("DESede");
	    kg.init(112, new SecureRandom());
	    final SecretKey key = kg.generateKey();
	    return key;
	} catch (final Exception e) {
	    throw new CustomKeyException(e);
	}	
    }
}
