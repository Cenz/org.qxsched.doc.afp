package org.qxsched.doc.afp.impl;

import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpException;
import org.qxsched.doc.afp.AfpFactory;
import org.qxsched.doc.afp.AfpReadWriteProperties;
import org.qxsched.doc.afp.AfpRecord;
import org.qxsched.doc.afp.AfpStructuredFieldDefinitions;
import org.qxsched.doc.afp.GenericAfpRecord;

/*
 * 
 * Copyright 2009, 2010, 2011, 2016 Vincenzo Zocca
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
 * Class {@link AfpRecordNOP} implements {@link GenericAfpRecord} for the NOP
 * record.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpRecordNOP extends GenericAfpRecord {

	private static final String[] CHARSET_TRY = { "cp500", "ISO-8859-1" };
	private static AfpStructuredFieldDefinitions defs;

	private static Logger LOG = Logger.getLogger(AfpRecordNOP.class);
	private static String myAbbrev = "NOP";
	private static Integer myIdentifier;

	private static synchronized void init() throws AfpException {

		// Return if instantiated
		if (defs != null) {
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Initializing "
					+ AfpStructuredFieldDefinitions.class.getName());
		}
		defs = AfpStructuredFieldDefinitions.instance();
		myIdentifier = defs.getCode(myAbbrev);
	}

	public AfpRecordNOP(AfpRecord record, AfpFactory fact) throws AfpException {

		// Init
		init();

		// Check type
		if (record.getSFIdentifier() != myIdentifier) {
			throw new AfpException("Expected '" + myAbbrev + "'/"
					+ AfpStructuredFieldDefinitions.hexString(myIdentifier, 6)
					+ ") but got object for '" + record.getSFIdentifierAbbrev()
					+ "'/ (" + record.getSFIdentifierString() + ")");
		}

		// Set identifier
		setSFIdentifier(myIdentifier);

		// Set flags
		setFlags(record.getFlags());

		// Set reserved
		setReserved(record.getReserved());

		// Set data
		setData(record.getData());
		
		// Set EndsInCrLf
		setEndsInCrLf(record.isEndsInCrLf());
	}

	public void writeData(BufferedWriter out, AfpReadWriteProperties props,
			String prefix) throws IOException, AfpException {

		// Get data
		byte[] data = getData();

		// If no data, let superclass write
		if (data.length == 0) {
			super.writeData(out, props, prefix);
			return;
		}

		// Make string and clean it
		String nopStr = null;
		for (int i = 0; i < CHARSET_TRY.length; i++) {

			String str = new String(data, CHARSET_TRY[i]);
			String strClean = new String(str.getBytes("ASCII"), "ASCII");

			// Count '?' characters
			char[] strChr = strClean.toCharArray();
			int qCnt = 0;
			for (int j = 0; j < strChr.length; j++) {
				if (strChr[j] == '?') {
					qCnt++;
				}
			}

			// If more than 15% of the characters are '?' then try next charset
			float ratio = (float) qCnt / (float) strChr.length;
			if (ratio > .15) {
				continue;
			}

			// We found a plausible string
			nopStr = str;
			break;
		}

		// Write
		if (nopStr == null) {
			super.writeData(out, props, prefix);
			return;
		}

		out.write(prefix);
		out.write("    STR:");
		out.write(nopStr);
		out.newLine();

	}
}
