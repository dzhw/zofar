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
package eu.dzhw.zofar.management.dev.file.archive.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dzhw.zofar.management.dev.file.archive.Archiver;
@Deprecated
public class TarGz implements Archiver {
	
	final static Logger LOGGER = LoggerFactory.getLogger(TarGz.class);

	public synchronized List<File> unpack(final File archiveFile,
			final File directory) {
		final List<File> untaredFiles = new LinkedList<File>();
		try {
			InputStream input = new FileInputStream(archiveFile);
			input = new GzipCompressorInputStream(input);
			final TarArchiveInputStream tar = new TarArchiveInputStream(input);
			TarArchiveEntry entry = null;
			while ((entry = (TarArchiveEntry) tar.getNextEntry()) != null) {
				final File outputFile = new File(directory, entry.getName());
				if (!entry.isDirectory()) {
					final File parent = outputFile.getParentFile();
					if(!parent.exists()){
						if(!parent.mkdirs())LOGGER.error("failed to create directory {}",parent.getAbsolutePath());
					}
					if (!outputFile.exists()) {
						if(!outputFile.createNewFile())LOGGER.error("failed to create file {}",outputFile.getAbsolutePath());
					}
					if(outputFile.exists()){
						final OutputStream outputFileStream = new FileOutputStream(
								outputFile);
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



	public void pack(File file, Map<String, Object> packageObj) throws Exception {
		throw new Exception("not implemented yet");
	}

}
