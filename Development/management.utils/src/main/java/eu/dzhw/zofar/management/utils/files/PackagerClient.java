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
 * Class to handle Packages
 */
package eu.dzhw.zofar.management.utils.files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;
/**
 * The Class PackagerClient.
 */
public class PackagerClient {
	final static Logger LOGGER = LoggerFactory.getLogger(PackagerClient.class);
	/** The instance. */
	private static PackagerClient INSTANCE;
	/**
	 * Instantiates a new packager client.
	 */
	private PackagerClient() {
		super();
	}
	/**
	 * Gets the single instance of PackagerClient.
	 * 
	 * @return single instance of PackagerClient
	 */
	public static PackagerClient getInstance() {
		if (INSTANCE == null)
			INSTANCE = new PackagerClient();
		return INSTANCE;
	}
	/**
	 * Package zip.
	 * 
	 * @param file
	 *            the file
	 * @param packageObj
	 *            the package obj
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void packageZip(final File file, final Map<String, Object> packageObj) throws IOException {
		this.packageZip(file, packageObj, DirectoryClient.getInstance().getTemp());
	}
	public void packageZip(final File file, final Map<String, Object> packageObj, final File destination ) throws IOException {
		if (file == null)
			return;
		if (destination == null)
			return;
		if (packageObj == null)
			return;
		if (!file.exists())
			file.createNewFile();
		ZipOutputStream zos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			zos = new ZipOutputStream(fos);
			final File wrapdir = DirectoryClient.getInstance().createDir(destination,
					"tmp_"+System.currentTimeMillis());			
			final File basedir = DirectoryClient.getInstance().createDir(wrapdir,
							file.getName());
			this.packageZipHelper(basedir, "", file.getName(), packageObj, zos);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (zos != null)
				zos.close();
			if (fos != null)
				fos.close();
		}
	}
	private void packageZipHelper(final File basedir, final String parent, final String name, final Object content,
			final ZipOutputStream zos) throws IOException {
		if (parent == null)
			return;
		if (name == null)
			return;
		if (content == null)
			return;
		if ((String.class).isAssignableFrom(content.getClass())) {
			final File tmp = FileClient.getInstance().createTextFile(name, content,basedir.getParentFile());
			this.packageZipHelper(basedir, parent, name, tmp, zos);
		} else if ((File.class).isAssignableFrom(content.getClass())) {
			try {
				final File tmp = (File) content;
				if (tmp.isFile()) {
					final File file = FileClient.getInstance().createOrGetFile(name,
							"", basedir);
					FileClient.getInstance().copyFile(tmp, file);
					this.addToZipFile(parent, file, zos);
				} else if (tmp.isDirectory()) {			
					List<File> files = DirectoryClient.getInstance().readDir(tmp,false);
					if (files != null) {
						for (final File item : files) {
							this.packageZipHelper(basedir, parent + "/" + name , item.getName(), item, zos);
						}
					}
				} else {
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else if ((Map.class).isAssignableFrom(content.getClass())) {
			try {
				this.addToZipFile(parent + "/" + name, new File(basedir, name), zos);
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
			@SuppressWarnings("unchecked")
			final Map<String, Object> subDir = (Map<String, Object>) content;
			for (final Map.Entry<String, Object> entry : subDir.entrySet()) {
				this.packageZipHelper(basedir, parent + "/" + name, entry.getKey(), entry.getValue(), zos);
			}
		} else {
			LOGGER.error("Ignored Package class : {}", content.getClass());
		}
	}
	/**
	 * Package zip helper.
	 * 
	 * @param parent
	 *            the parent
	 * @param name
	 *            the name
	 * @param content
	 *            the content
	 * @param zos
	 *            the zos
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public File packZIP(final List<File> uncompressed,final String password) {
		try {
			ZipParameters zipParameters = new ZipParameters();
			zipParameters.setEncryptFiles(true);
			zipParameters.setEncryptionMethod(EncryptionMethod.AES);
			zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256); 
			String name = "compressed";
			for(final File x : uncompressed) {
				System.out.println("x : "+x.getAbsolutePath());
			}
			ZipFile back = new ZipFile(name+".zip", password.toCharArray());
			back.addFiles(uncompressed, zipParameters);
			return back.getFile();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public File packZIP(final File uncompressed,final String password) {
		try {
			ZipParameters zipParameters = new ZipParameters();
			zipParameters.setEncryptFiles(true);
			zipParameters.setEncryptionMethod(EncryptionMethod.AES);
			zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256); 
			ZipFile back = new ZipFile(uncompressed.getName()+".zip", password.toCharArray());
			if(uncompressed.isDirectory())back.addFolder(uncompressed, zipParameters);
			else back.addFiles(Arrays.asList(uncompressed), zipParameters);
			return back.getFile();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public File packZIP(final File uncompressed) {
		final byte[] buffer = new byte[1024];
		try {
			final File back = FileClient.getInstance().createOrGetFile(uncompressed.getName(), ".zip",
					uncompressed.getParentFile());
			final FileOutputStream fileOutputStream = new FileOutputStream(back);
			final ZipOutputStream zipOuputStream = new ZipOutputStream(fileOutputStream);
			final ZipEntry ze = new ZipEntry(uncompressed.getName());
			zipOuputStream.putNextEntry(ze);
			final FileInputStream fileInput = new FileInputStream(uncompressed);
			int bytes_read;
			while ((bytes_read = fileInput.read(buffer)) > 0) {
				zipOuputStream.write(buffer, 0, bytes_read);
			}
			fileInput.close();
			zipOuputStream.closeEntry();
			zipOuputStream.finish();
			zipOuputStream.close();
			return back;
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public File extractZipAsDir(final File compressed) {
		if (compressed == null)
			return null;
		if (!compressed.exists())
			return null;
		try {
			byte[] buffer = new byte[1024];
			ZipInputStream zis = new ZipInputStream(new FileInputStream(compressed));
			ZipEntry ze = zis.getNextEntry();
			final File cacheDir = DirectoryClient.getInstance().createDir(compressed.getParentFile(), compressed.getName()+System.currentTimeMillis());
			while (ze != null) {
				if(ze.isDirectory()){
				}
				else{
					String path = ze.getName();
					String dirPath = path;
					String filePath = path;
					if(path.lastIndexOf(File.separator) != -1){
						dirPath = path.substring(0, path.lastIndexOf(File.separator));
						filePath = path.substring(path.lastIndexOf(File.separator)+1);
					}
					final File currentDir = DirectoryClient.getInstance().createDir(cacheDir, dirPath);
					File newFile = FileClient.getInstance().createOrGetFile(filePath, "",currentDir);
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
			return cacheDir;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<File> extractZip(final File compressed) {
		if (compressed == null)
			return null;
		if (!compressed.exists())
			return null;
		try {
			byte[] buffer = new byte[1024];
			ZipInputStream zis = new ZipInputStream(new FileInputStream(compressed));
			ZipEntry ze = zis.getNextEntry();
			List<File> back = new ArrayList<File>();
			final File cacheDir = DirectoryClient.getInstance().createDir(compressed.getParentFile(), compressed.getName()+System.currentTimeMillis());
			while (ze != null) {
				if(ze.isDirectory()){
				}
				else{
					String path = ze.getName();
					String dirPath = path;
					String filePath = path;
					if(path.lastIndexOf(File.separator) != -1){
						dirPath = path.substring(0, path.lastIndexOf(File.separator));
						filePath = path.substring(path.lastIndexOf(File.separator)+1);
					}
					final File currentDir = DirectoryClient.getInstance().createDir(cacheDir, dirPath);
					File newFile = FileClient.getInstance().createOrGetFile(filePath, "",currentDir);
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
					back.add(newFile);
				}
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
			return back;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Adds the to zip file.
	 * 
	 * @param parent
	 *            the parent
	 * @param file
	 *            the file
	 * @param zos
	 *            the zos
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void addToZipFile(final String parent, final File file, final ZipOutputStream zos)
			throws FileNotFoundException, IOException {
		if (file == null)
			return;
		if (!file.exists())
			return;
		System.out.println("Writing '" + parent + "/" + file.getName() + "' to zip file");
		if (file.isFile()) {
			final FileInputStream fis = new FileInputStream(file);
			final ZipEntry zipEntry = new ZipEntry(parent + "/" + file.getName());
			zos.putNextEntry(zipEntry);
			final byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
			zos.closeEntry();
			fis.close();
		} else if (file.isDirectory()) {
			final ZipEntry zipEntry = new ZipEntry(parent + "/" + file.getName());
			zos.putNextEntry(zipEntry);
			zos.closeEntry();
			final File[] files = file.listFiles();
			if (files != null) {
				final int count = files.length;
				for (int i = 0; i < count; i++) {
					this.addToZipFile(parent + "/" + file.getName(), files[i], zos);
				}
			}
		}
	}
	/**
	 * Extract gzip.
	 * 
	 * @param compressed
	 *            the compressed
	 * @return the file
	 */
	public File extractGZIP(final File compressed,final boolean cleanFileIfExist) {
		final byte[] buffer = new byte[1024];
		try {
			final File back = FileClient.getInstance().createOrGetFile(compressed.getName(), ".uncompressed",
					compressed.getParentFile());
			if(cleanFileIfExist&&back.exists()&&back.canWrite()){
				FileUtils.write(back, "",false);
			}
			final FileInputStream fileIn = new FileInputStream(compressed);
			final GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
			final FileOutputStream fileOutputStream = new FileOutputStream(back);
			int bytes_read;
			while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, bytes_read);
			}
			gZIPInputStream.close();
			fileOutputStream.close();
			return back;
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	/**
	 * Pack gzip.
	 * 
	 * @param uncompressed
	 *            the uncompressed
	 * @return the file
	 */
	public File packGZIP(final File uncompressed) {
		final byte[] buffer = new byte[1024];
		try {
			final File back = FileClient.getInstance().createOrGetFile(uncompressed.getName(), ".gzip",
					uncompressed.getParentFile());
			final FileOutputStream fileOutputStream = new FileOutputStream(back);
			final GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fileOutputStream);
			final FileInputStream fileInput = new FileInputStream(uncompressed);
			int bytes_read;
			while ((bytes_read = fileInput.read(buffer)) > 0) {
				gzipOuputStream.write(buffer, 0, bytes_read);
			}
			fileInput.close();
			gzipOuputStream.finish();
			gzipOuputStream.close();
			return back;
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	/**
	 * Extract TarGZ.
	 * 
	 * @param compressed
	 *            the compressed
	 * @return List of files
	 */
	public List<File> extractTarGZ(final File compressed) {
		final List<File> untaredFiles = new LinkedList<File>();
		try {
			InputStream input = new FileInputStream(compressed);
			input = new GzipCompressorInputStream(input);
			final TarArchiveInputStream tar = new TarArchiveInputStream(input);
			TarArchiveEntry entry = null;
			while ((entry = (TarArchiveEntry) tar.getNextEntry()) != null) {
				String fileName = entry.getName();
				final File outputFile = new File(DirectoryClient.getInstance().getTemp(), fileName);
				if (!entry.isDirectory()) {
					final File parent = outputFile.getParentFile();
					if (!parent.exists()) {
						if (!parent.mkdirs())
							LOGGER.error("failed to create directory {}", parent.getAbsolutePath());
					}
					if (!outputFile.exists()) {
						if (!outputFile.createNewFile())
							LOGGER.error("failed to create file {}", outputFile.getAbsolutePath());
					}
					if (outputFile.exists()) {
						final OutputStream outputFileStream = new FileOutputStream(outputFile);
						IOUtils.copy(tar, outputFileStream);
						outputFileStream.close();
					}
				}
				untaredFiles.add(outputFile);
			}
			tar.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return untaredFiles;
	}
}
