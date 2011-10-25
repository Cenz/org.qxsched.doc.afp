package org.qxsched.doc.afp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
 * Class {@link GenericAfpTriplet} implements a minimal AFP triplet.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class GenericAfpTriplet implements AfpTriplet {

	private static Logger LOG = Logger.getLogger(GenericAfpTriplet.class);

	public static int countTids(AfpTriplet[] triplets, int tid) {

		int cnt = 0;
		for (int i = 0; i < triplets.length; i++) {
			AfpTriplet afpTriplet = triplets[i];
			if (afpTriplet.getTid() == tid) {
				cnt++;
			}
		}
		return cnt;
	}

	private byte[] content;
	private int length;
	private int tid;

	protected GenericAfpTriplet() {
	}

	/**
	 * Constructor accepting a data array.
	 * 
	 * @param data
	 *            the data byte array
	 * @throws AfpException
	 *             if an AFP exception occurs.
	 */
	public GenericAfpTriplet(byte[] data) throws AfpException {
		this(data, 0);
	}

	/**
	 * Constructor accepting a data array and a start offset.
	 * 
	 * @param data
	 *            the data byte array
	 * @param offset
	 *            the start offset
	 * @throws AfpException
	 *             if an AFP exception occurs.
	 */
	public GenericAfpTriplet(byte[] data, int offset) throws AfpException {
		setData(data, offset);
	}

	/**
	 * Returns the content byte array without length and TID byte.
	 * 
	 * @return the content byte array without length and TID byte.
	 */
	public byte[] getContent() {
		return content;
	}

	public byte[] getData() {
		byte[] ret = new byte[content.length + 2];
		ret[0] = (byte) ret.length;
		ret[1] = (byte) tid;
		System.arraycopy(content, 0, ret, 2, content.length);
		return ret;
	}

	/**
	 * Returns the value of the length byte.
	 * 
	 * @return the value of the length byte.
	 */
	public int getLength() {
		return length;
	}

	public int getTid() {
		return tid;
	}

	/**
	 * Sets the content bytes without length and TID byte.
	 * 
	 * @param content
	 *            the content bytes without length and TID byte.
	 * @throws AfpException
	 *             if an AFP exception occurs.
	 */
	protected void setContent(byte[] content) throws AfpException {
		if (content.length == 0 || content.length > 0xff - 2) {
			throw new AfpException("Illegal byte array size " + content.length);
		}
		this.content = content;
		length = content.length - 2;
	}

	/**
	 * Sets the attributes using supplied byte array, starting at supplied
	 * offset.
	 * 
	 * @param data
	 *            byte array inclusing length and tid bytes.
	 * @param offset
	 *            start offset.
	 * @throws AfpException
	 *             if an AFP exception occurs.
	 */
	protected void setData(byte[] data, int offset) throws AfpException {

		// Check data length
		int lenEff = data.length - offset;
		if (lenEff < 3) {
			throw new AfpException(
					"Too little data in supplied array: length/offset/eff-len: "
							+ data.length + "/" + offset + "/" + lenEff);
		}

		// Get and check length
		int length = data[offset] & 0xff;

		if (length < 3) {
			throw new AfpException("Illegal length byte at offset " + offset);
		}
		if (lenEff < length) {
			throw new AfpException("Effective length too small (" + lenEff
					+ ") for triplet length (" + length + ") differ.");
		}
		this.length = length;

		// Get tid
		tid = data[offset + 1] & 0xff;

		// Remember content
		content = new byte[length - 2];
		System.arraycopy(data, offset + 2, content, 0, length - 2);
	}

	/**
	 * Sets the TID.
	 * 
	 * @param tid
	 *            the TID
	 * @throws AfpException
	 *             if an AFP exception occurs.
	 */
	public void setTid(int tid) throws AfpException {
		if (tid > 0xff) {
			throw new AfpException("Illegal TID value " + tid);
		}
		this.tid = tid;
	}

	public void write(BufferedWriter out, AfpReadWriteProperties props,
			String prefix) throws IOException, AfpException {

		// Get data
		byte[] data = getData();

		// Loop
		for (int i = 0; i < data.length;) {

			int iStart = i;
			out.write(prefix);
			out.write("  ");
			if (i == 0) {
				out.write("TRP ");
			} else {
				out.write("    ");
			}

			for (int j = i; j < data.length && j < iStart + 16; j++) {
				i++;

				int val = data[j] & 0xff;
				out.write(StringUtils.leftPad(Integer.toString(val, 16)
						.toLowerCase(), 2, '0'));
				out.write(" ");

			}
			out.newLine();
		}
	}

	public void write(OutputStream out, AfpReadWriteProperties props)
			throws IOException, AfpException {
		LOG.info("ORIG FOOOO");
		out.write(getData());
	}
}
