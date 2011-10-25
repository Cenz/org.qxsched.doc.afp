package org.qxsched.doc.afp.impl;

import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpException;
import org.qxsched.doc.afp.AfpFactory;
import org.qxsched.doc.afp.AfpReadWriteProperties;
import org.qxsched.doc.afp.AfpRecord;
import org.qxsched.doc.afp.AfpStructuredFieldDefinitions;
import org.qxsched.doc.afp.AfpTriplet;
import org.qxsched.doc.afp.GenericAfpRecord;
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
 * Class {@link AfpRecordTLE} implements {@link GenericAfpRecord} for the TLE
 * record.
 * 
 * The class is practically very useful but...
 * 
 * At this moment the class isn't fully implemented. Triplet 01 isn't handled
 * well. Triplet 02 and 36 are handled but improvement is needs. Further
 * Development is required with emphasis on code pages / character sets.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpRecordTLE extends GenericAfpRecord {

	private static AfpStructuredFieldDefinitions defs;

	private static Logger LOG = Logger.getLogger(AfpRecordTLE.class);
	private static String myAbbrev = "TLE";
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

	private AfpTriplet[] trips;

	public AfpRecordTLE(AfpRecord record, AfpFactory fact) throws AfpException {

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
		setData(record.getData(), fact);
	}

	public AfpRecordTLE(int flags, int reserved, AfpTriplet[] trips)
			throws AfpException {

		// Init
		init();

		// Set identifier
		setSFIdentifier(myIdentifier);

		// Set flags
		setFlags(flags);

		// Set reserved
		setReserved(reserved);

		// Set data
		setData(trips);
	}

	public AfpTriplet[] getAfpTriplets() {
		return trips;
	}

	public String getAttName() {

		// Get triplets
		AfpTriplet[] trips = getAfpTriplets();

		// Get first triplet 02 and return it's TID
		for (int i = 0; i < trips.length; i++) {

			// Get triplet
			AfpTriplet trip = trips[i];

			// Check for 02
			if (trip instanceof AfpTriplet02) {

				// Cast
				AfpTriplet02 trip02 = (AfpTriplet02) trip;

				// Return
				return trip02.getFQName().toString();
			}
		}

		// Return null
		return null;
	}

	public String getAttVal() {

		// Get triplets
		AfpTriplet[] trips = getAfpTriplets();

		// Get first triplet 36 and return it's attribute value
		for (int i = 0; i < trips.length; i++) {

			// Get triplet
			AfpTriplet trip = trips[i];

			// Check for 36
			if (trip instanceof AfpTriplet36) {

				// Cast
				AfpTriplet36 trip36 = (AfpTriplet36) trip;

				// Get attribute value
				Object attVal = trip36.getAttVal();

				// Return
				return attVal == null ? null : attVal.toString();
			}
		}

		// Return null
		return null;
	}

	// public byte[] getData() {
	//
	// // Make buffer
	// byte[] buff = new byte[0xffff];
	// int pos = 0;
	//
	// for (int i = 0; i < trips.length; i++) {
	// AfpTriplet trip = trips[i];
	// byte[] tdata = trip.getData();
	// for (int j = 0; j < tdata.length; j++) {
	// buff[j + pos] = tdata[j];
	// }
	// pos += tdata.length;
	// }
	//
	// byte[] ret = new byte[pos];
	// System.arraycopy(buff, 0, ret, 0, pos);
	//
	// return ret;
	// }

	protected void check(AfpTriplet[] trips) throws AfpException {

		// Must have one triplet of ID 0x02
		if (GenericAfpTriplet.countTids(trips, 0x02) < 1) {
			throw new AfpException(
					"TLE data must contain at least one triple of ID 0x02");
		}

		// Must have one triplet of ID 0x36
		if (GenericAfpTriplet.countTids(trips, 0x36) < 1) {
			throw new AfpException(
					"TLE data must contain at least one triple of ID 0x36");
		}

		// Loop through triplets
		StringBuffer err = new StringBuffer();
		int count02 = 0;
		int count36 = 0;
		int count80 = 0;
		for (int i = 0; i < trips.length; i++) {
			AfpTriplet trip = trips[i];
			int tid = trip.getTid();
			switch (tid) {
			case 0x01:
				break;

			case 0x02:

				// Increment count
				count02++;

				// Cast
				AfpTriplet02 t02 = (AfpTriplet02) trip;

				// First 02 triplet must be of type 0x0b and of format 0x00
				if (count02 == 1) {
					if (t02.getFQNType() != 0x0b) {
						throw new AfpException(
								"First 0x02 triplet in TLE data must be of type 0x0b, not 0x"
										+ Integer
												.toString(t02.getFQNType(), 16));
					}
					if (t02.getFQNFmt() != 0x00) {
						throw new AfpException(
								"First 0x02 triplet in TLE data must be of format 0x00, not 0x"
										+ Integer.toString(t02.getFQNFmt(), 16));
					}
				}

				break;

			case 0x36:

				// Increment count
				count36++;
				break;

			case 0x80:

				// Increment count
				count80++;
				break;

			default:
				err.append("Illegal triplet for TLE record: ");
				err.append(Integer.toString(tid));
				err.append(". ");
				break;
			}
		}

		// Expect at least one 0x02
		if (count02 < 1) {
			err.append("Expected at least one 0x02 triplit but only got ");
			err.append(count02);
			err.append(". ");
		}

		// Expect at least one 0x36
		if (count36 < 1) {
			err.append("Expected at least one 0x36 triplit but only got ");
			err.append(count36);
			err.append(". ");
		}

		// Throw exception on error
		if (err.length() > 0) {
			throw new AfpException(err.toString());
		}
	}

	protected void setData(AfpTriplet[] trips) throws AfpException {

		// Check
		check(trips);

		// Make data
		byte[] data = makeData(trips);

		// Set data
		super.setData(data);
	}

	private byte[] makeData(AfpTriplet[] trips) {

		// Calculate size
		int len = 0;
		for (int i = 0; i < trips.length; i++) {
			AfpTriplet trip = trips[i];
			len += trip.getData().length;
		}

		// Allocate
		byte[] ret = new byte[len];

		// Copy
		int offset = 0;
		for (int i = 0; i < trips.length; i++) {
			AfpTriplet trip = trips[i];
			byte[] tData = trip.getData();
			System.arraycopy(tData, 0, ret, offset, tData.length);
			offset++;
		}

		// Return
		return ret;
	}

	protected void setData(byte[] data, AfpFactory fact) throws AfpException {

		// Get all triplets from data
		AfpTriplet[] trips = fact.createAfpTriplets(data, 0, -1);

		// Check triplets
		check(trips);

		// Remember triplets
		this.trips = trips;

		// Set data
		super.setData(data);
	}

	public void writeData(BufferedWriter out, AfpReadWriteProperties props,
			String prefix) throws IOException, AfpException {
		for (int i = 0; i < trips.length; i++) {
			AfpTriplet trip = trips[i];
			trip.write(out, props, prefix);
		}
	}
}
