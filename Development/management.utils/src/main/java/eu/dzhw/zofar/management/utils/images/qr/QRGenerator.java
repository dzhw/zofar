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
package eu.dzhw.zofar.management.utils.images.qr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


public class QRGenerator {
	
	private static final QRGenerator INSTANCE = new QRGenerator();
	
	/** The Constant LOGGER. */
	final static Logger LOGGER = LoggerFactory.getLogger(QRGenerator.class);

	private QRGenerator() {
		super();
	}
	
	public static synchronized QRGenerator getInstance() {
		return INSTANCE;
	}
	
	private BitMatrix generateBitMatrix(final String content,final int size){
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            return qrCodeWriter.encode(content,BarcodeFormat.QR_CODE, size, size, hintMap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	public BufferedImage generate(final String content){
        return this.generate(content,25);
	}
	
	public BufferedImage generate(final String content,final int size){
		final BitMatrix byteMatrix = generateBitMatrix(content,size);
        final int width = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(width, width,
                BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, width);
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }      
        return image;
	}
}
