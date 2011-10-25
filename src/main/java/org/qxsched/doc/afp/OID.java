package org.qxsched.doc.afp;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.impl.Untested;

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
 * Class {@link OID} represents OIDs as used in AFP triplets.
 * 
 * At this moment the class isn't developed and tested completely.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class OID implements Untested {

	private static Logger LOG = Logger.getLogger(OID.class);

	public static final String OID_RX = "\\{\\d+(\\.\\d+)*\\}";

	private byte[] compIdBytes;
	private String compIdStr;

	public OID(byte[] data) throws AfpException {
		this(data, 0, data.length);
	}

	public OID(byte[] data, int offset, int length) throws AfpException {

		// Make compIdBytes
		compIdBytes = new byte[length];
		for (int i = 0; i < length; i++) {
			compIdBytes[i] = data[i + offset];
		}

		// Make list for component ID
		List<Integer> compIdList = new ArrayList<Integer>();

		// Loop through data
		int buff = 0;
		int shift = 0;
		boolean end = false;
		for (int i = 0; i < compIdBytes.length; i++) {

			// Read byte
			int b = compIdBytes[i] & 0xff;
			end = (b & 0x80) != 0;
			int clean = b & 0x7f;
			clean <<= 7 * shift;
			shift++;

			// Add to buffer
			buff |= clean;

			if (LOG.isTraceEnabled()) {
				LOG.trace("Read byte binary: " + Integer.toString((int) b, 2));
				LOG.trace("Read byte hex: " + Integer.toString((int) b, 16));
				LOG.trace("End of compnent ID?: " + end);
				LOG.trace("Cleaned byte binary: " + Integer.toString(clean, 2));
				LOG.trace("Cleaned byte hex: " + Integer.toString(clean, 16));
				LOG.trace("Buffer so far binary: " + Integer.toString(buff, 2));
				LOG.trace("Buffer so far hex: " + Integer.toString(buff, 16));
			}

			// Handle end
			if (end || i == length - 1) {

				// Add to component ID list
				if (compIdList.size() == 0) {
					int x = buff / 40;
					int y = buff % 40;
					compIdList.add(new Integer(x));
					compIdList.add(new Integer(y));
					if (LOG.isTraceEnabled()) {
						LOG.trace("First element 'x' binary: "
								+ Integer.toString(x, 2));
						LOG.trace("First element 'x' hex: "
								+ Integer.toString(x, 16));
						LOG.trace("First element 'y' binary: "
								+ Integer.toString(y, 2));
						LOG.trace("First element 'y' hex: "
								+ Integer.toString(y, 16));
					}
				} else {
					compIdList.add(new Integer(buff));
				}

				// Reset buffer and shift counter
				buff = 0;
				shift = 0;
			}
		}

		// Make compIdStr
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (int i = 0; i < compIdList.size(); i++) {
			if (i != 0) {
				sb.append(".");
			}
			sb.append(compIdList.get(i));
		}
		sb.append("}");
		compIdStr = sb.toString();
	}

	public OID(String oid) throws AfpException {

		// Check syntax
		if (!oid.matches(OID_RX)) {
			throw new AfpException("Syntax error in supplied OID string: "
					+ oid);
		}

		// Remember string
		compIdStr = oid;

		// Clean and split
		oid = oid.replaceFirst("^\\{", "");
		oid = oid.replaceFirst("\\}$", "");
		String[] oidStrArr = oid.split("\\.");
		int[] compId = new int[oidStrArr.length];
		for (int i = 0; i < oidStrArr.length; i++) {
			compId[i] = new Integer(oidStrArr[i]).intValue();
		}

		// Make bytes out of integers
		compIdBytes = makeBytes(compId);
	}

	private void addBytes(List<Byte> dataList, int num) {

		// Loop
		while (true) {

			// Get byte
			byte b = (byte) (num & 0x7f);

			// Shift the number
			num >>= 7;

			// Handle the last significant part
			if (num == 0) {
				b |= 0x80;
			}

			// Add to list
			dataList.add(new Byte(b));

			// Break at last significant part
			if (num == 0) {
				break;
			}
		}
	}

	public boolean equals(Object obj) {

		// False if null
		if (obj == null) {
			if (obj == null) {
				return false;
			}
		}

		// False if class differs
		if (!(obj instanceof OID)) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Diff on class type");
			}
			return false;
		}

		// Cast
		OID objOid = (OID) obj;

		// False if bytes differ
		byte[] thisData = getData();
		byte[] objData = objOid.getData();
		if (thisData.length != objData.length) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Diff on data length");
			}
			return false;
		}
		for (int i = 0; i < thisData.length; i++) {
			if (thisData[i] != objData[i]) {
				if (LOG.isTraceEnabled()) {
					LOG
							.trace("Diff on data index "
									+ i
									+ ": "
									+ Integer
											.toString((thisData[i] & 0xff), 16)
									+ " / "
									+ Integer.toString((objData[i] & 0xff), 16));
				}
				return false;
			}
		}

		// String
		String thisStr = toString();
		String objStr = objOid.toString();
		if (thisStr == null) {
			if (objStr != null) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Diff on string representation");
				}
				return false;
			}
			return true;
		}
		return thisStr.equals(objStr);
	}

	public byte[] getData() {
		return compIdBytes;
	}

	private byte[] makeBytes(int[] compId) {

		// Byte array
		List<Byte> dataList = new ArrayList<Byte>();

		// Handle first two numbers
		int num01 = compId[0] * 40;
		if (compId.length > 1) {
			num01 += compId[1];
		}
		addBytes(dataList, num01);

		// Handle the remaining numbers
		for (int i = 2; i < compId.length; i++) {
			addBytes(dataList, compId[i]);
		}

		// Make byte array
		byte[] ret = new byte[dataList.size()];
		for (int i = 0; i < dataList.size(); i++) {
			ret[i] = dataList.get(i);
		}
		return ret;
	}

	public String toString() {
		return compIdStr;
	}
}
