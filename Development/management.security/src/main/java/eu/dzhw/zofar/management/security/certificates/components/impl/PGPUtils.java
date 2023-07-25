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
 * PGP Implementation of eu.dzhw.zofar.management.security.certificates.components.CertificateUtils
 */
package eu.dzhw.zofar.management.security.certificates.components.impl;
import java.security.KeyPair;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import eu.dzhw.zofar.management.security.certificates.components.CertificateUtils;
/**
 * The Class PGPUtils.
 */
public class PGPUtils extends CertificateUtils {
	/** The instance. */
	private static PGPUtils INSTANCE;
	/**
	 * Instantiates a new PGP utils.
	 */
	private PGPUtils() {
		super();
	}
	/**
	 * Gets the single instance of PGPUtils.
	 * 
	 * @return single instance of PGPUtils
	 */
	public static PGPUtils getInstance() {
		if (INSTANCE == null)
			INSTANCE = new PGPUtils();
		return INSTANCE;
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#createRootCertificate(java.lang.String, java.security.KeyPair, int)
	 */
	@Override
	public Certificate createRootCertificate(String subjectDN, KeyPair pair,
		long validity) {
		return null;
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#createDerivedCert(java.lang.String, java.security.PublicKey, java.security.PrivateKey, java.security.cert.Certificate, int, eu.dzhw.zofar.management.security.certificates.components.CertificateUtils.TYPE)
	 */
	@Override
	public Certificate createDerivedCert(String subjectDN, PublicKey pubKey,
			PrivateKey caKey, Certificate caCert, long validity, TYPE type) throws Exception {
		throw new Exception("Not implemented yet");
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#toCertificate(java.lang.Object)
	 */
	@Override
	protected Certificate toCertificate(Object certificate) throws Exception {
		throw new Exception("Not implemented yet");
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#toPublicKey(java.lang.Object)
	 */
	@Override
	protected PublicKey toPublicKey(Object publicKey) {
		Security.addProvider(new BouncyCastleProvider());
		try {
			return ((PGPPublicKey)publicKey).getKey("BC");
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (PGPException e) {
			e.printStackTrace();
		}		
		return null;
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#toPrivateKey(java.lang.Object, java.lang.String)
	 */
	@Override
	protected PrivateKey toPrivateKey(Object privateKey,String passphrase) {
		Security.addProvider(new BouncyCastleProvider());
		try {
			PGPPrivateKey pKey = ((PGPSecretKey)privateKey).extractPrivateKey(passphrase.toCharArray(), "BC");
			return pKey.getKey();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (PGPException e) {
			e.printStackTrace();
		}		
		return null;
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#fromCertificate(java.security.cert.Certificate)
	 */
	@Override
	protected Object fromCertificate(Certificate certificate) {
		return null;
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#fromPublicKey(java.security.PublicKey)
	 */
	@Override
	protected Object fromPublicKey(PublicKey publicKey) {
		Security.addProvider(new BouncyCastleProvider());
		return null;
	}
	/* (non-Javadoc)
	 * @see eu.dzhw.zofar.management.security.certificates.components.CertificateUtils#fromPrivateKey(java.security.PrivateKey)
	 */
	@Override
	protected Object fromPrivateKey(PrivateKey privateKey) {
		Security.addProvider(new BouncyCastleProvider());
		return null;
	}
}
