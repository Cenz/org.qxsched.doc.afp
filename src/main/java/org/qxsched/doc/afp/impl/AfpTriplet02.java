package org.qxsched.doc.afp.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpException;
import org.qxsched.doc.afp.AfpReadWriteProperties;
import org.qxsched.doc.afp.AfpStructuredFieldDefinitions;
import org.qxsched.doc.afp.AfpTriplet;
import org.qxsched.doc.afp.GenericAfpTriplet;
import org.qxsched.doc.afp.OID;

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
 * Class {@link AfpTriplet02} implements {@link GenericAfpTriplet} for the 02
 * triplet.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpTriplet02 extends GenericAfpTriplet {

	private static final int[] FQN_FMT_RANGE_INT = { 0x00, 0x10, 0x20 };
	private static final Map<Integer, String> FQN_FMT_RANGE_MAP = new HashMap<Integer, String>();
	private static final String[] FQN_FMT_RANGE_STR = { "Character string",
			"OID", "URL" };

	private static final int[] FQN_TYPE_RANGE_INT = { 0x01, 0x07, 0x08, 0x09,
			0x0A, 0x0B, 0x0C, 0x0D, 0x11, 0x41, 0x6E, 0x7E, 0x83, 0x84, 0x85,
			0x86, 0x87, 0x8D, 0x8E, 0x98, 0xB0, 0xBE, 0xCA, 0xCE, 0xDE };
	private static final Map<Integer, String> FQN_TYPE_RANGE_MAP = new HashMap<Integer, String>();
	private static final String[] FQN_TYPE_RANGE_STR = {
			"Replace First GID name", "Font Family Name", "Font Typeface Name",
			"MO:DCA Resource Hierarchy Reference",
			"Begin Resource Group Reference", "Attribute GID",
			"Process Element GID", "Begin Page Group Reference",
			"Media Type Reference",
			"Color Management Resource (CMR) Reference",
			"Data-object Font Base Font Identifier",
			"Data-object Font Linked Font Identifier",
			"Begin Document Reference", "Resource Object Reference",
			"Code Page Name Reference", "Font Character Set Name Reference",
			"Begin Page Reference", "Begin Medium Map Reference",
			"Coded Font Name Reference", "Begin Document Index Reference",
			"Begin Overlay Reference",
			"Data Object Internal Resource Reference", "Index Element GID",
			"Other Object Data Reference",
			"Data Object External Resource Reference" };

	private static Logger LOG = Logger.getLogger(AfpTriplet02.class);
	private static final int TID = 0x02;

	static {
		for (int i = 0; i < FQN_FMT_RANGE_INT.length; i++) {
			Integer intg = new Integer(FQN_FMT_RANGE_INT[i]);
			FQN_FMT_RANGE_MAP.put(intg, FQN_FMT_RANGE_STR[i]);
		}
	}

	static {
		for (int i = 0; i < FQN_TYPE_RANGE_INT.length; i++) {
			Integer intg = new Integer(FQN_TYPE_RANGE_INT[i]);
			FQN_TYPE_RANGE_MAP.put(intg, FQN_TYPE_RANGE_STR[i]);
		}
	}

	private Charset cs = AfpFactory.AFP_DEFAULT_CS;;
	private Object fQName;
	protected int fQNFmt;
	protected int fQNType;

	public AfpTriplet02(AfpTriplet triplet) throws AfpException {
		this(triplet, null);
	}

	public AfpTriplet02(AfpTriplet triplet, Charset cs) throws AfpException {

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

	private AfpTriplet02(int fQNType, int fQNFmt, String fQName, Charset cs)
			throws AfpException {

		// FQNFmt 0x00
		this.fQNFmt = fQNFmt;

		// Store character set
		if (cs != null) {
			this.cs = cs;
		}

		// Check fQNType
		StringBuffer err = new StringBuffer();
		if (!FQN_TYPE_RANGE_MAP.containsKey(fQNType)) {
			err.append("Illegal FQNType: ");
			err.append(AfpStructuredFieldDefinitions.hexString(fQNType, 2));
			err.append(". ");
		}

		// Check fQName
		if (fQName == null || fQName.equals("")) {
			err.append("Illegal value for FQName: ");
			err.append(fQName);
			err.append(". ");
		}

		// Throw exception
		if (err.length() > 0) {
			throw new AfpException(err.toString());
		}

		// Convert fQName to bytes
		byte[] fQNameB = fQName.getBytes(this.cs);

		// Make data
		int fQNameBLen = (fQNameB.length) > 250 ? 250 : fQNameB.length;
		byte[] data = new byte[fQNameBLen + 4];
		data[0] = (byte) (fQNameBLen + 4 & 0xff);
		data[1] = 0x02;
		data[2] = (byte) (fQNType & 0xff);
		data[3] = (byte) (fQNFmt & 0xff);
		System.arraycopy(fQNameB, 0, data, 4, fQNameBLen);

		// Set data
		setData(data);
	}

	public AfpTriplet02(int fQNType, OID fQName) throws AfpException {

		// Check fQName
		byte[] fQNameB = fQName.getData();
		if (fQNameB.length > 250) {
			throw new AfpException(
					"Supplied FQName yields to byte array with more than 250 bytes: "
							+ fQNameB.length);
		}

		// Make data
		byte[] data = new byte[fQNameB.length + 4];
		data[0] = (byte) (fQNameB.length + 4 & 0xff);
		data[1] = 0x02;
		data[2] = (byte) (fQNType & 0xff);
		data[3] = 0x10;
		System.arraycopy(fQNameB, 0, data, 4, fQNameB.length);

		// Set data
		setData(data);
	}

	public AfpTriplet02(int fQNType, String fQName) throws AfpException {
		this(fQNType, fQName, null);
	}

	public AfpTriplet02(int fQNType, String fQName, Charset cs)
			throws AfpException {
		this(fQNType, 0x00, fQName, cs);
		this.fQName = fQName;
	}

	public AfpTriplet02(int fQNType, URL fQName) throws AfpException {
		this(fQNType, fQName, null);
	}

	public AfpTriplet02(int fQNType, URL fQName, Charset cs)
			throws AfpException {
		this(fQNType, 0x20, fQName.toString(), cs);
		this.fQName = fQName;
	}

	public boolean equals(Object obj) {

		// False if null
		if (obj == null) {
			if (obj == null) {
				return false;
			}
		}

		// False if class differs
		if (!(obj instanceof AfpTriplet02)) {
			return false;
		}

		// Cast
		AfpTriplet02 objTrip = (AfpTriplet02) obj;

		// False if bytes differ
		byte[] thisData = getData();
		byte[] objData = objTrip.getData();
		if (thisData.length != objData.length) {
			return false;
		}
		for (int i = 0; i < thisData.length; i++) {
			if (thisData[i] != objData[i]) {
				return false;
			}
		}

		// FQNType
		int thisFQNType = getFQNType();
		int objFQNType = objTrip.getFQNType();
		if (thisFQNType != objFQNType) {
			return false;
		}

		// FQNFmt
		int thisFQNFmt = getFQNFmt();
		int objFQNFmt = objTrip.getFQNFmt();
		if (thisFQNFmt != objFQNFmt) {
			return false;
		}

		// TFQName
		Object thisTFQName = getFQName();
		Object objTFQName = objTrip.getFQName();
		if (thisTFQName == null) {
			if (objTFQName != null) {
				return false;
			}
		} else {
			if (!thisTFQName.equals(objTFQName)) {
				return false;
			}
		}

		// Equal
		return true;
	}

	public Object getFQName() {
		return fQName;
	}

	public int getFQNFmt() {
		return fQNFmt;
	}

	public int getFQNType() {
		return fQNType;
	}

	private void readFQName(byte[] data) throws AfpException {

		switch (fQNFmt) {

		// fQNFmt: 0x00 -> character string or binary
		case 0x00: {

			switch (fQNType) {

			// fQNType: Binary types
			case 0x84:
			case 0xBE:
				byte[] ba = new byte[data.length - 4];
				fQName = ba;
				for (int i = 0; i < ba.length; i++) {
					ba[i] = data[i + 4];
				}
				break;

			// String types
			default:
				fQName = new String(data, 4, data.length - 4, cs);
				if (LOG.isTraceEnabled()) {
					LOG.trace("fQName: " + fQName);
				}
				break;
			}
			break;
		}

			// fQNFmt: 0x10 -> OID, ASN.1
		case 0x10: {
			fQName = new OID(data, 4, data.length - 4);
			break;
		}

			// fQNFmt: 0x20 -> URL
		case 0x20: {
			String url = new String(data, 4, data.length - 4, cs);
			try {
				fQName = new URL(url);
			} catch (MalformedURLException e) {
				throw new AfpException("Malformed URL: " + url);
			}
			if (LOG.isTraceEnabled()) {
				LOG.trace("fQName: " + fQName);
			}
			break;
		}

		default: {
			break;
		}
		}
	}

	private void setData(byte[] data) throws AfpException {

		// Check data length
		if (data.length < 5) {
			throw new AfpException(
					"Expected data length of at least 5 but got " + data.length);
		}

		// Get FQNType
		Integer tmp = new Integer(data[2] & 0xff);
		if (!FQN_TYPE_RANGE_MAP.containsKey(tmp)) {
			throw new AfpException("Unexpected FQNType 0x"
					+ AfpStructuredFieldDefinitions.hexString(tmp, 2));
		}
		fQNType = tmp;

		// Get FQNFmt
		tmp = new Integer(data[3] & 0xff);
		if (!FQN_FMT_RANGE_MAP.containsKey(tmp)) {
			throw new AfpException("Unexpected FQNFmt 0x"
					+ AfpStructuredFieldDefinitions.hexString(tmp, 2));
		}
		fQNFmt = tmp;

		// Read fQName
		readFQName(data);

		super.setData(data, 0);
	}

	public void write(BufferedWriter out, AfpReadWriteProperties props,
			String prefix) throws IOException, AfpException {

		// Let superclass print if no fQName
		if (fQName == null) {
			super.write(out, props, prefix);
			return;
		}

		// Print line
		out.write(prefix);
		out.write("  ");
		out.write("TRP TID=");
		out.write(AfpStructuredFieldDefinitions.hexString(getTid(), 2));
		out.write(" FQNType=");
		out.write(AfpStructuredFieldDefinitions.hexString(fQNType, 2));
		out.write("(");
		out.write(FQN_TYPE_RANGE_MAP.get(fQNType));
		out.write(")");
		out.write(" FQNFmt=");
		out.write(AfpStructuredFieldDefinitions.hexString(fQNFmt, 2));
		out.write("(");
		out.write(FQN_FMT_RANGE_MAP.get(fQNFmt));
		out.write(")");
		out.write(" FQName=");
		out.write(fQName.toString());
		out.newLine();
	}
}
