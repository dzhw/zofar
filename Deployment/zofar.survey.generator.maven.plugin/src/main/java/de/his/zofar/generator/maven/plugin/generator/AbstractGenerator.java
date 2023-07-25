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
/**
 *
 */
package de.his.zofar.generator.maven.plugin.generator;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

/**
 * abstract generator class to provide function which used in all generators,
 * e.g. saveDocument()
 *
 * @author le
 *
 * @param <T>
 *            the output document type of the implementing generator
 */
public abstract class AbstractGenerator<T extends XmlObject> {

    private final T document;

    /**
     *
     */
    public AbstractGenerator(final T document) {
        super();
        this.document = document;
    }

    /**
     * saves the document in the path.
     *
     * @param path
     *            the path in which the document will be saved
     * @param fileName
     *            the file name
     * @param options
     *            XmlOptions to configure layout of the file
     * @throws IOException
     */
    protected final void saveDocument(final String path, final String fileName,
            final XmlOptions options) throws IOException {
        final File file = new File(path + File.separator + fileName);
        this.document.save(file, options);
    }

    /**
     * @return the document
     */
    public final T getDocument() {
        return this.document;
    }

    /**
     * @return
     */
    public final Boolean validate() {
        return this.document.validate();
    }

}
