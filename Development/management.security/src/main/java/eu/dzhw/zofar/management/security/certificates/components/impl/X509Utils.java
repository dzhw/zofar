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
 * X509 Implementation of eu.dzhw.zofar.management.security.certificates.components.CertificateUtils
 */
package eu.dzhw.zofar.management.security.certificates.components.impl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import eu.dzhw.zofar.management.security.certificates.components.CertificateUtils;
import eu.dzhw.zofar.management.security.certificates.exceptions.CustomCertificateException;
/**
 * The Class X509Utils.
 */
public class X509Utils extends CertificateUtils {
	/** The instance. */
	private static X509Utils INSTANCE;
	/**
	 * Instantiates a new x509 utils.
	 */
	private X509Utils() {
		super();
	}
	/**
	 * Gets the single instance of X509Utils.
	 * 
	 * @return single instance of X509Utils
	 */
	public static X509Utils getInstance() {
		if (INSTANCE == null)
			INSTANCE = new X509Utils();
		return INSTANCE;
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#createRootCertificate(java.lang.String, java.security.KeyPair, int)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Certificate createRootCertificate(String subjectDN, KeyPair pair,
		long validity) throws Exception {
		X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
		certGen.setSerialNumber(BigInteger.ONE);
		certGen.setIssuerDN(new X500Principal("CN=" + subjectDN));
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + validity));
		certGen.setSubjectDN(new X500Principal("CN=" + subjectDN));
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
		try {
			java.security.cert.X509Certificate certificate = certGen
					.generate(pair.getPrivate());
			return toCertificate(certificate);
		} catch (Exception e) {
			throw new CustomCertificateException(e);
		} 
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#createDerivedCert(java.lang.String, java.security.PublicKey, java.security.PrivateKey, java.security.cert.Certificate, int, eu.dzhw.zofar.management.security.certificates.components.CertificateUtils.TYPE)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Certificate createDerivedCert(String subjectDN, PublicKey pubKey,
			PrivateKey caKey, Certificate caCert, long validity, TYPE type) throws Exception {
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		X509Certificate x509Cert = fromCertificate(caCert);
		certGen.setSerialNumber(BigInteger.ONE);
		certGen.setIssuerDN(x509Cert.getSubjectX500Principal());
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + validity));
		certGen.setSubjectDN(new X500Principal("CN=" + subjectDN));
		certGen.setPublicKey(pubKey);
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
		try {
			switch (type) {
			case INTERMEDIATED:
				certGen.addExtension(X509Extensions.AuthorityKeyIdentifier,
						false, new AuthorityKeyIdentifierStructure(x509Cert));
				certGen.addExtension(X509Extensions.SubjectKeyIdentifier,
						false, new SubjectKeyIdentifierStructure(pubKey));
				certGen.addExtension(X509Extensions.BasicConstraints, true,
						new BasicConstraints(0));
				certGen.addExtension(X509Extensions.KeyUsage, true,
						new KeyUsage(KeyUsage.digitalSignature
								| KeyUsage.keyCertSign | KeyUsage.cRLSign));
				return toCertificate(certGen.generate(caKey));
			case END:
				certGen.addExtension(X509Extensions.AuthorityKeyIdentifier,
						false, new AuthorityKeyIdentifierStructure(x509Cert));
				certGen.addExtension(X509Extensions.SubjectKeyIdentifier,
						false, new SubjectKeyIdentifierStructure(pubKey));
				certGen.addExtension(X509Extensions.BasicConstraints, true,
						new BasicConstraints(false));
				certGen.addExtension(X509Extensions.KeyUsage, true,
						new KeyUsage(KeyUsage.digitalSignature
								| KeyUsage.keyEncipherment));
				return toCertificate(certGen.generate(caKey));
			}
			throw new CustomCertificateException("UNKOWN DERIVED TYPE : "+type);
		} catch (Exception e) {
		    throw new CustomCertificateException(e);
		}
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#toCertificate(java.lang.Object)
	 */
	@Override
	protected Certificate toCertificate(Object certificate) throws Exception {
		if((X509CertificateHolder.class).isAssignableFrom(certificate.getClass())){
			try {
				return (new JcaX509CertificateConverter()).setProvider("BC").getCertificate((X509CertificateHolder) certificate);
			} catch (CertificateException e) {
				throw new CustomCertificateException(e);
			}
		}
		else if((X509CertificateHolder.class).isAssignableFrom(certificate.getClass())){
			return ((X509Certificate)certificate);
		}
		else if((X509CertificateObject.class).isAssignableFrom(certificate.getClass())){
			return ((X509Certificate)certificate);
		}
		else{
			throw new CustomCertificateException("no certificate");
		}
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#toPublicKey(java.lang.Object)
	 */
	@Override
	protected PublicKey toPublicKey(Object publicKey) {
		return (PublicKey)publicKey;
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#toPrivateKey(java.lang.Object, java.lang.String)
	 */
	@Override
	protected PrivateKey toPrivateKey(Object privateKey,String passphrase) {
		return (PrivateKey)privateKey;
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#fromCertificate(java.security.cert.Certificate)
	 */
	@Override
	protected X509Certificate fromCertificate(Certificate certificate) throws CustomCertificateException {
		try{
	        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
	        InputStream in = new ByteArrayInputStream(certificate.getEncoded());
	        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
	        return cert;
		}
		catch(Exception e){
		    throw new CustomCertificateException(e);
		}
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#fromPublicKey(java.security.PublicKey)
	 */
	@Override
	protected PublicKey fromPublicKey(PublicKey publicKey) {
		return publicKey;
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#fromPrivateKey(java.security.PrivateKey)
	 */
	@Override
	protected PrivateKey fromPrivateKey(PrivateKey privateKey) {
		return privateKey;
	}
}
