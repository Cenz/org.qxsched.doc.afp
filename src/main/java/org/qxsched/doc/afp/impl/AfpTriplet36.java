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
 * Class {@link AfpTriplet36} implements {@link GenericAfpTriplet} for the 36
 * triplet.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpTriplet36 extends GenericAfpTriplet {

	private static Logger LOG = Logger.getLogger(AfpTriplet36.class);
	private static final int TID = 0x36;

	private Charset cs = AfpFactory.AFP_DEFAULT_CS;;
	private String attVal;
	protected int reserved;

	public AfpTriplet36(AfpTriplet triplet) throws AfpException {
		this(triplet, null);
	}

	public AfpTriplet36(AfpTriplet triplet, Charset cs) throws AfpException {

		// Store character set
		if (cs != null) {
			this.cs = cs;
		}

		// Check tid
		if (triplet.getTid() != TID) {
			throw new AfpException("Expected TID "
					+ AfpStructuredFieldDefinitions.hexString(TID, 2)
					+ " but got TID "
					+ AfpStructuredFieldDefinitions.hexString(triplet.getTid(),
							2));
		}
		setTid(triplet.getTid());

		// Set data
		setData(triplet.getData());

	}

	public String getAttVal() {
		return attVal;
	}

	private void readAttVal(byte[] data) throws AfpException {

		attVal = new String(data, 4, data.length - 4, cs);
		if (LOG.isTraceEnabled()) {
			LOG.trace("AttVal: " + attVal);
		}
	}

	private void setData(byte[] data) throws AfpException {

		// Check data length
		if (data.length < 4) {
			throw new AfpException(
					"Expected data length of at least 4 but got " + data.length);
		}

		// Get FQNType
		reserved = data[2] & 0xff;
		reserved <<= 8;
		reserved = data[3] & 0xff;

		// Read GID
		readAttVal(data);

		super.setData(data, 0);
	}

	public void write(BufferedWriter out, AfpReadWriteProperties props,
			String prefix) throws IOException, AfpException {

		// Let superclass print if no GID
		if (attVal == null) {
			super.write(out, props, prefix);
			return;
		}

		// Print line
		out.write(prefix);
		out.write("  ");
		out.write("TRP TID=");
		out.write(AfpStructuredFieldDefinitions.hexString(getTid(), 2));
		out.write(" AttVal=");
		out.write(attVal);
		out.newLine();
	}
}
