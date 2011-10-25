package org.qxsched.doc.afp.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpException;
import org.qxsched.doc.afp.AfpReadWriteProperties;
import org.qxsched.doc.afp.AfpStructuredFieldDefinitions;
import org.qxsched.doc.afp.AfpTriplet;
import org.qxsched.doc.afp.GenericAfpTriplet;

/*
 * 
 * Copyright 2009, 2010, 2011 Vincenzo Zocca
 * 
 * This file is part of Java library org.qxsched.doc.afp.
 *
 * Java library org.qxsched.doc.afp is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Java library org.qxsched.doc.afp is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Java library org.qxsched.doc.afp.
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 */

/**
 * Class {@link AfpTriplet01} implements {@link GenericAfpTriplet} for the 01
 * triplet.
 * 
 * At this moment the class isn't developed and tested completely.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpTriplet01 extends GenericAfpTriplet {

	private static Logger LOG = Logger.getLogger(AfpTriplet01.class);
	private static final int TID = 0x01;

	private int ccsId;
	private int cpgId;
	private int gcsgId;
	private boolean isCcsid;
	private int tid;

	public AfpTriplet01(AfpTriplet triplet) throws AfpException {
		this(triplet, null);
	}

	public AfpTriplet01(AfpTriplet triplet, Charset cs) throws AfpException {

		// Check tid
		if (triplet.getTid() != TID) {
			throw new AfpException("Expected TID "
					+ AfpStructuredFieldDefinitions.hexString(TID, 2)
					+ " but got TID "
					+ AfpStructuredFieldDefinitions.hexString(triplet.getTid(),
							2));
		}
		tid = TID;

		// Set data
		setData(triplet.getData());

	}

	private void setData(byte[] data) throws AfpException {

		// Check data length
		if (data.length != 6) {
			throw new AfpException("Expected data length of 6 but got "
					+ data.length);
		}

		// Get GCSGID or switch code
		gcsgId = data[2] & 0xff;
		gcsgId = gcsgId << 8;
		gcsgId += data[3] & 0xff;
		isCcsid = gcsgId == 0;

		// Get CCSID/CPGID
		int tmp = data[4] & 0xff;
		tmp = tmp << 8;
		tmp += data[5] & 0xff;
		if (isCcsid) {
			ccsId = tmp;
		} else {
			cpgId = tmp;
		}

		super.setData(data, 0);
	}

	public void write(BufferedWriter out, AfpReadWriteProperties props,
			String prefix) throws IOException, AfpException {

		// Print line
		out.write(prefix);
		out.write("  ");
		out.write("TRP TID=0X");
		out.write(AfpStructuredFieldDefinitions.hexString(tid, 2));
		if (isCcsid) {
			out.write(" GCSGID=");
			out.write(AfpStructuredFieldDefinitions.hexString(gcsgId, 2));
			out.write(" CPGID=");
			out.write(AfpStructuredFieldDefinitions.hexString(cpgId, 2));
		} else {
			out.write(" CCSID=");
			out.write(AfpStructuredFieldDefinitions.hexString(ccsId, 2));
		}
		out.newLine();
	}
}
