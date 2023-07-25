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
 * Class to handle Directories
 */
package eu.dzhw.zofar.management.utils.files;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class DirectoryClient.
 */
public class DirectoryClient {
	/** The Constant INSTANCE. */
	private static final DirectoryClient INSTANCE = new DirectoryClient();
	/** The Constant LOGGER. */
	final static Logger LOGGER = LoggerFactory.getLogger(DirectoryClient.class);
	/**
	 * The Enum SortMode.
	 */
	public enum SortMode {
		/** The date. */
		DATE,
		/** The date desc. */
		DATE_DESC,
		/** The unsorted. */
		UNSORTED
	}
	/**
	 * The Class DirFilter.
	 */
	private class DirFilter implements IOFileFilter {
		/** The current dir. */
		private final File currentDir;
		/**
		 * Instantiates a new dir filter.
		 * 
		 * @param currentDir
		 *            the current dir
		 */
		public DirFilter(final File currentDir) {
			super();
			this.currentDir = currentDir;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File)
		 */
		public boolean accept(final File file) {
			if (file.getParentFile().equals(this.currentDir))
				return true;
			return false;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File,
		 * java.lang.String)
		 */
		public boolean accept(final File dir, final String name) {
			return false;
		}
	}
	/**
	 * The Class FileFilter.
	 */
	private class FileFilter implements IOFileFilter {
		/** The current dir. */
		private final File currentDir;
		/**
		 * Instantiates a new file filter.
		 * 
		 * @param currentDir
		 *            the current dir
		 */
		public FileFilter(final File currentDir) {
			super();
			this.currentDir = currentDir;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File)
		 */
		public boolean accept(final File file) {
			if (file.getParentFile().equals(this.currentDir))
				return true;
			return false;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File,
		 * java.lang.String)
		 */
		public boolean accept(final File dir, final String name) {
			return false;
		}
	}
	/**
	 * The Class FileComparator.
	 */
	private class FileComparator implements Comparator<File> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(final File o1, final File o2) {
			if ((o1 == null) && (o2 == null))
				return 0;
			if ((o1 != null) && (o2 == null))
				return 1;
			if ((o1 == null) && (o2 != null))
				return -1;
			if ((o1 != null) && (o2 != null)) {
				final long mod1 = o1.getAbsoluteFile().lastModified();
				final long mod2 = o2.getAbsoluteFile().lastModified();
				if (mod1 > mod2)
					return 1;
				if (mod1 < mod2)
					return -1;
			}
			return 0;
		}
	};
	/**
	 * Instantiates a new directory client.
	 */
	private DirectoryClient() {
		super();
	}
	/**
	 * Gets the single instance of DirectoryClient.
	 * 
	 * @return single instance of DirectoryClient
	 */
	public static synchronized DirectoryClient getInstance() {
		return INSTANCE;
	}
	/**
	 * Search files in Directory by extensions and pattern match in filename.
	 * 
	 * @param dir
	 *            the dir
	 * @param extensions
	 *            the extensions
	 * @param pattern
	 *            the pattern
	 * @return the list
	 */
	public List<File> searchFiles(final File dir, final String pattern) {
		if (dir == null)
			return null;
		if (!dir.isDirectory())
			return null;
		if (!dir.canRead())
			return null;
		final List<File> back = new ArrayList<File>();
		final List<File> founds = this.readDir(dir);
		for (final File found : founds) {
			if (found.getName().matches(pattern)) {
				back.add(found);
			}
		}
		return back;
	}
	public List<File> recursiveSearchFiles(final File dir, final String pattern) {
		if (dir == null)
			return null;
		if (!dir.isDirectory())
			return null;
		if (!dir.canRead())
			return null;
		final List<File> back = new ArrayList<File>();
		final List<File> founds = this.readDir(dir);
		for (final File found : founds) {
			if (found.getName().matches(pattern)) {
				if (found.isDirectory())
					back.addAll(recursiveSearchFiles(found, pattern));
				back.add(found);
			}
		}
		return back;
	}
	/**
	 * Read Directory by extensions.
	 * 
	 * @param dir
	 *            the dir
	 * @param extensions
	 *            the extensions
	 * @return the list
	 */
	public List<File> readDir(final File dir) {
		return this.readDir(dir, SortMode.DATE);
	}
	public List<File> readDir(final File dir,boolean recursive) {
		return this.readDir(dir, SortMode.DATE,recursive);
	}
	/**
	 * Read Directory by extensions and sort (SortMode.DATE,SortMode.DATE_DESC) .
	 * 
	 * @param dir
	 *            the dir
	 * @param extensions
	 *            the extensions
	 * @param mode
	 *            the mode
	 * @return the list
	 */
	public List<File> readDir(final File dir, final SortMode mode) {
		return this.readDir(dir,mode,true);
	}
	public List<File> readDir(final File dir, final SortMode mode,boolean recursive) {
		if (dir == null)
			return null;
		if (!dir.isDirectory())
			return null;
		if (!dir.canRead())
			return null;
		final List<File> back = new ArrayList<File>();
		back.addAll(FileUtils.listFiles(dir, null, recursive));
		if(!recursive) {
			final File[] tmp = dir.listFiles();
			for (final File file : tmp) {
				if (file.isDirectory())
					back.add(file);
			}
		}
		back.remove(dir);
		if (mode != null) {
			if (mode.equals(SortMode.DATE))
				return this.sortByDate(back);
			if (mode.equals(SortMode.DATE_DESC))
				return this.reverseSortByDate(back);
		}
		return back;
	}
	public List<File> readDirRecursive(final File current) {
		final List<File> back = new ArrayList<File>();
		final List<File> files = readDir(current, null);
		for (final File file : files) {
			if (file.isDirectory())
				back.addAll(readDirRecursive(file));
			else if (file.isFile())
				back.add(file);
		}
		return back;
	}
	public Map<String,Object> readDirRecursiveAsMap(final File current) {
		final Map<String,Object> back = new LinkedHashMap<String,Object>();
		final List<File> files = readDir(current, null);
		for (final File file : files) {
			if (file.isDirectory())
				back.put(file.getName(),readDirRecursiveAsMap(file));
			else if (file.isFile())
				back.put(file.getName(),file);
		}
		return back;
	}
	/**
	 * Clean directory.
	 * 
	 * @param dir
	 *            the dir
	 * @return true, if successful
	 */
	public boolean cleanDirectory(final File dir) {
		if (dir == null)
			return false;
		if (!dir.isDirectory())
			return false;
		if (!dir.canRead())
			return false;
		if (!dir.canWrite())
			return false;
		try {
			FileUtils.cleanDirectory(dir);
			return true;
		} catch (final IOException e) {
			LOGGER.error("clean Directory Error ", e);
		}
		return false;
	}
	public void copyDirectory(final File srcDir, final File destDir) throws IOException {
		FileUtils.copyDirectory(srcDir, destDir, true);
	}
	/**
	 * Creates Directory.
	 * 
	 * @param rootDir
	 *            the root dir
	 * @param name
	 *            the name
	 * @return the file
	 */
	public File createDir(final File rootDir, final String name) {
		final File dir = new File(rootDir, name);
		if (dir.exists()) {
			return dir;
		} else if (dir.mkdirs())
			return dir;
		return null;
	}
	/**
	 * Delete Directory.
	 * 
	 * @param rootDir
	 *            the root dir
	 * @param name
	 *            the name
	 * @return true, if successful
	 */
	public boolean deleteDir(final File rootDir, final String name) {
		final File dir = new File(rootDir, name);
		if (dir.exists() && dir.canWrite()) {
			return dir.delete();
		}
		return false;
	}
	public boolean deleteDir(final File dir) {
		try {
			FileUtils.deleteDirectory(dir);
			return true;
		}
		catch(Exception e) {
			LOGGER.error("Deletion of "+dir.getAbsolutePath()+" failed", e);
		}
		return false;
	}
	/**
	 * Gets user home.
	 * 
	 * @return the home
	 */
	public File getHome() {
		return new File(System.getProperty("user.home"));
	}
	/**
	 * Gets Temp Directory.
	 * 
	 * @return the temp
	 */
	public File getTemp() {
		return new File(System.getProperty("java.io.tmpdir"));
	}
	public File getCurrent() {
		return new File(System.getProperty("user.dir"));
	}
	/**
	 * Sort by date.
	 * 
	 * @param list
	 *            the list
	 * @return the list
	 */
	private List<File> sortByDate(final List<File> list) {
		if (list == null)
			return null;
		Collections.sort(list, new FileComparator());
		return list;
	}
	/**
	 * Reverse sort by date.
	 * 
	 * @param list
	 *            the list
	 * @return the list
	 */
	private List<File> reverseSortByDate(final List<File> list) {
		if (list == null)
			return null;
		final List<File> tmpList = this.sortByDate(list);
		Collections.reverse(tmpList);
		return tmpList;
	}
}
