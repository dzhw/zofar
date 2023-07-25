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
 * Class to handle Files
 */
package eu.dzhw.zofar.management.utils.files;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class FileClient.
 */
public class FileClient {
	/** The Constant INSTANCE. */
	private static final FileClient INSTANCE = new FileClient();
	/** The Constant LOGGER. */
	final static Logger LOGGER = LoggerFactory.getLogger(FileClient.class);
	/**
	 * Instantiates a new file client.
	 */
	private FileClient() {
		super();
	}
	/**
	 * Gets the single instance of FileClient.
	 * 
	 * @return single instance of FileClient
	 */
	public static synchronized FileClient getInstance() {
		return INSTANCE;
	}
	/**
	 * Creates a temporary file.
	 * 
	 * @param name
	 *            the name
	 * @param suffix
	 *            the suffix
	 * @return the file
	 */
	public File createTempFile(final String name, final String suffix) {
		try {
			final File file = File.createTempFile(name, suffix);
			if (!file.exists())
				file.createNewFile();
			return file;
		} catch (final IOException e) {
			LOGGER.error("temp file creation failed", e);
		}
		return null;
	}
	/**
	 * Creates a file.
	 * 
	 * @param name
	 *            the name
	 * @param suffix
	 *            the suffix
	 * @param dir
	 *            the dir
	 * @return the file
	 */
	public File createOrGetFile(final String name, final String suffix, final File dir) {
		if (dir == null)
			return null;
		if (!dir.exists())
			return null;
		try {
			final File file = new File(dir,name+suffix);
			if (!file.exists())
				file.createNewFile();
			return file;
		} catch (final IOException e) {
			LOGGER.error("createorgetFile failed", e);
			e.printStackTrace();
		}
		return null;
	}
	public boolean exists(final String path){
		if(path == null)return false;
		final File file = new File(path);
		return file.exists();
	}
	/**
	 * Gets URL from Resources.
	 * 
	 * @return the url
	 */
	public URL getUrl(final String name) {
		final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		return classloader.getResource(name);
	}
	/**
	 * Gets File from Resources.
	 * 
	 * @return the file
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public File getResource(final String name) {
        InputStream input = getClass().getResourceAsStream("/resources/" + name);
        if (input == null) {
            input = getClass().getClassLoader().getResourceAsStream(name);
        }
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(input));
        File back = FileClient.getInstance().createTempFile(name, ".tmp");
        try {
			FileUtils.touch(back);
	        byte[] buffer = IOUtils.toByteArray(inputReader);
	        FileUtils.writeByteArrayToFile(back, buffer);
	        input.close();
	        inputReader.close();
			return back;
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
	}
	/**
	 * Gets File from Resources.
	 * 
	 * @return the file
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public File getResource1(final String name) throws URISyntaxException, IOException {
		final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		URL url = classloader.getResource(name);
		LOGGER.info("URL for {} is {}",name,url);
		URL[] urls = new URL[1];
		urls[0] = url;
		final File[] files = FileUtils.toFiles(urls);
		if((files != null)&&(files.length == 1))return files[0];
		return null;
	}
	/**
	 * Creates a text file.
	 * 
	 * @param name
	 *            the name
	 * @param content
	 *            the content
	 * @return the file
	 */
	public File createTextFile(final String name, final Object content) {
		try {
			final File file = this.createOrGetFile(name, "", DirectoryClient.getInstance().getTemp());
			FileUtils.writeStringToFile(file, content + "", "UTF-8");
			return file;
		} catch (final IOException e) {
			LOGGER.error("Text File creation failed", e);
		}
		return null;
	}
	public File createTextFile(final String name, final Object content, final File dirTemp,final String encoding) {
		try {
			final File file = this.createOrGetFile(name, "", dirTemp);
			FileUtils.writeStringToFile(file, content + "", encoding);
			return file;
		} catch (final IOException e) {
			LOGGER.error("Text File creation failed", e);
		}
		return null;
	}
	public File createTextFile(final String name, final Object content, final File dirTemp) {
		return createTextFile(name,content,dirTemp,"UTF-8");
	}
	public String getNameWithoutSuffix(final File file){
		if(file == null)return null;
		return FilenameUtils.getBaseName(file.getAbsolutePath());
	}
	public String getSuffix(final File file){
		if(file == null)return null;
		return FilenameUtils.getExtension(file.getAbsolutePath());
	}
	/**
	 * Read File as bytes array.
	 * 
	 * @param file
	 *            the file
	 * @return the byte[]
	 */
	public byte[] readAsBytes(final File file) {
		if (file == null)
			return null;
		if (!file.isFile())
			return null;
		if (!file.canRead())
			return null;
		try {
			return FileUtils.readFileToByteArray(file);
		} catch (final IOException e) {
			LOGGER.error("readAsBytes failed", e);
		}
		return null;
	}
	/**
	 * Show content.
	 * 
	 * @param file
	 *            the file
	 * @return the string
	 */
	public String readAsString(final File file) {
		if (file == null)
			return null;
		if (!file.isFile())
			return null;
		if (!file.canRead())
			return null;
		try {
			return FileUtils.readFileToString(file);
		} catch (final IOException e) {
			LOGGER.error("readAsString failed", e);
		}
		return null;
	}
	/**
	 * Iterate content.
	 * 
	 * @param file
	 *            the file
	 * @return the line iterator
	 */
	public LineIterator iterateContent(final File file) {
		if (file == null)
			return null;
		if (!file.isFile())
			return null;
		if (!file.canRead())
			return null;
		try {
			return FileUtils.lineIterator(file);
		} catch (final IOException e) {
			LOGGER.error("iterateContent failed", e);
		}
		return null;
	}
	/**
	 * Write to file.
	 * 
	 * @param filename
	 *            the filename
	 * @param content
	 *            the content
	 * @param append
	 *            the append
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void writeToFile(final String filename, final String content, final boolean append) throws IOException {
		if (filename == null)
			return;
		final File file = new File(filename);
		if (!file.exists())
			file.createNewFile();
		if (!file.isFile()) {
			LOGGER.error("[FILE] {} is not a file", filename);
			return;
		}
		if (!file.canWrite()) {
			LOGGER.error("[FILE] {} is write protected", filename);
			return;
		}
		FileUtils.writeStringToFile(file, content, append);
	}
	public void writeToFile(final File file, final String content, final boolean append) throws IOException {
		if (file == null)
			return;
		if (!file.isFile()) {
			LOGGER.error("[FILE] {} is not a file", file.getAbsolutePath());
			return;
		}
		if (!file.canWrite()) {
			LOGGER.error("[FILE] {} is write protected", file.getAbsolutePath());
			return;
		}
		FileUtils.writeStringToFile(file, content, append);
	}
	/**
	 * Delete file.
	 * 
	 * @param file
	 *            the file
	 * @return true, if successful
	 */
	public boolean deleteFile(final File file) {
		if (file == null)
			return false;
		if (!file.isFile())
			return false;
		if (!file.canWrite())
			return false;
		return FileUtils.deleteQuietly(file);
	}
	/**
	 * Copy file using stream.
	 * 
	 * @param source
	 *            the source
	 * @param dest
	 *            the dest
	 * @throws Exception 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	  public void copyFileUsingStream(final File source, final File dest) throws Exception {
		  if(source == null)throw new Exception("Source File is null");
		  if(!source.canRead())throw new Exception("Cannot read Source File "+source.getAbsolutePath());
		  if(dest == null)throw new Exception("Destination File is null");
		  if(!dest.canWrite())throw new Exception("Cannot write to Destination File "+dest.getAbsolutePath());
		  if((source.getAbsolutePath()).equals(dest.getAbsolutePath()))return;
		    InputStream inStream = null;
		    OutputStream outStream = null;
		      try {
		        byte[] bucket = new byte[32*1024];
		        inStream = new BufferedInputStream(new FileInputStream(source));
		        outStream = new BufferedOutputStream(new FileOutputStream(dest, false));
		        int bytesRead = 0;
		        while(bytesRead != -1){
		          bytesRead = inStream.read(bucket); 
		          if(bytesRead > 0){
		            outStream.write(bucket, 0, bytesRead);
		          }
		        }
		      }
		      finally {
		        if (inStream != null) inStream.close();
		        if (outStream != null) outStream.close();
		      }
		  }
	/**
	 * Copy file to destination direction.
	 * 
	 * @param source
	 *            the source
	 * @param destDir
	 *            the dest dir
	 * @return the file
	 */
	public File copyToDir(final File source, final File destDir) {
		try {
			if (source.isFile()) {
				FileUtils.copyFileToDirectory(source, destDir);
				return new File(destDir, source.getName());
			} else if (source.isDirectory()) {
				FileUtils.copyDirectory(source, destDir);
				return destDir;
			}
		} catch (final IOException e) {
			LOGGER.error("copytoDir failed", e);
		}
		return null;
	}
	/**
	 * Copy file to destination file.
	 * 
	 * @param source
	 *            the source
	 * @param dest
	 *            the dest
	 * @return the file
	 */
	public File copyFile(final File source, final File dest) {
		try {
			if (source.isFile()) {
				FileUtils.copyFile(source, dest);
				return dest;
			} else if (source.isDirectory()) {
				FileUtils.copyDirectory(source, dest);
				return dest;
			}
		} catch (final IOException e) {
			LOGGER.error("copyFile failed", e);
		}
		return null;
	}
	/**
	 * Move file to destination direction.
	 * 
	 * @param source
	 *            the source
	 * @param destDir
	 *            the dest dir
	 * @return the file
	 */
	public File move(final File source, final File destDir) {
		final File destFile = this.copyToDir(source, destDir);
		if ((destFile != null) && (source.delete()))
			return destFile;
		return null;
	}
	public File renameTo(final File source, final String newName) throws Exception {
		if(source.renameTo(new File(newName)))return source;
		else throw new Exception("Renaming failed for "+source.getAbsolutePath()+ " to "+newName);
	}
}
