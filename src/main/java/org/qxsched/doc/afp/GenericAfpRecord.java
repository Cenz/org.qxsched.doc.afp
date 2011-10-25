package org.qxsched.doc.afp;

import java.io.BufferedWriter;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.util.AfpDump;

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
 * Class {@link GenericAfpRecord} implements a minimal AFP record.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class GenericAfpRecord implements AfpRecord {

	private static AfpStructuredFieldDefinitions afpDefs;
	protected static int ccc = 0x5a;

	private static Logger LOG = Logger.getLogger(GenericAfpRecord.class);
	private static int maxDataLength = 0xffff - 8;
	private static int maxFlags = 0xff;
	private static int maxIdentifier = 0xffffff;
	private static int maxReserved = 0xffff;

	private byte[] data;
	private int flags;
	private int identifier;
	private String identifierAbbrev;
	private int length;
	private int reserved;

	protected GenericAfpRecord() throws AfpException {
	}

	public GenericAfpRecord(InputStream in) throws AfpException {

		// Initialize
		init();

		// Expect the CCC
		int cccRead;
		try {
			cccRead = in.read();
		} catch (IOException e) {
			throw new AfpException("Failed to read CCC from input stream", e);
		}
		if (cccRead != ccc) {
			throw new AfpException(
					"Expected Carriage Control Character but got "
							+ AfpStructuredFieldDefinitions.hexString(cccRead,
									2));
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Got CCC byte.");
		}

		// Expect 8 bytes structured field introducer
		byte[] buff = new byte[8];
		int read;
		try {
			read = in.read(buff);
		} catch (IOException e) {
			throw new AfpException(
					"Failed to read record length from input stream", e);
		}
		if (read != buff.length) {
			throw new AfpException("Expected " + buff.length
					+ " bytes as structured field introducer but got " + read
					+ " bytes from input stream.");
		}
		// for (int i = 0; i < buff.length; i++) {
		// log
		// .debug("Byte " + i + ": "
		// + AfpStructuredFieldDefinitions.hexString(buff[i] & 0xff, 2));
		// }

		// Make length
		length = buff[0] & 0xff;
		length <<= 8;
		length += buff[1] & 0xff;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Length: "
					+ AfpStructuredFieldDefinitions.hexString(length, 4));
		}

		// Make identifier
		identifier = buff[2] & 0xff;
		identifier <<= 8;
		identifier += buff[3] & 0xff;
		identifier <<= 8;
		identifier += buff[4] & 0xff;
		if (LOG.isDebugEnabled()) {
			LOG.debug("SF-Identifier: " + getSFIdentifierString());
		}
		setSFIdentifier(identifier);

		// Make flags
		flags = buff[5] & 0xff;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Flags: "
					+ AfpStructuredFieldDefinitions.hexString(flags, 2));
		}

		// Make reserved
		reserved = buff[6] & 0xff;
		reserved <<= 8;
		reserved += buff[7] & 0xff;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Reserved: "
					+ AfpStructuredFieldDefinitions.hexString(reserved, 4));
		}

		// Expect data
		data = new byte[length - 8];
		try {
			read = in.read(data);
		} catch (IOException e) {
			throw new AfpException(
					"Failed to read record data from input stream", e);
		}
		if (read != data.length) {
			throw new AfpException("Expected " + data.length
					+ " bytes as data but got " + read
					+ " bytes from input stream.");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Data length: "
					+ AfpStructuredFieldDefinitions.hexString(data.length, 4));
		}

	}

	public GenericAfpRecord(InputStream in, AfpReadWriteProperties props)
			throws AfpException, IOException {
	}

	public GenericAfpRecord(int identifier, int flags, int reserved, byte[] data)
			throws AfpException {

		// Initialize
		init();

		setSFIdentifier(identifier);
		setFlags(flags);
		setReserved(reserved);
		setData(data);
	}

	public byte[] getData() {
		return data;
	}

	public int getFlags() {
		return flags;
	}

	public int getLength() {
		return length;
	}

	public int getReserved() {
		return reserved;
	}

	public int getSFIdentifier() {
		return identifier;
	}

	public String getSFIdentifierAbbrev() {
		return identifierAbbrev;
	}

	public String getSFIdentifierString() {
		return AfpStructuredFieldDefinitions.hexString(identifier, 6);
	}

	private synchronized void init() throws AfpException {

		// Ready if afpDefs is set
		if (afpDefs != null) {
			return;
		}

		// Get AfpStructuredFieldDefinitions
		afpDefs = AfpStructuredFieldDefinitions.instance();

	}

	protected void setData(byte[] data) throws AfpException {

		// Check length
		if (data.length > maxDataLength) {
			throw new AfpException("Byte array too long. Max allowed is "
					+ maxDataLength);
		}
		this.data = data;
		length = this.data.length + 8;
	}

	protected void setFlags(int flags) throws AfpException {

		// Check size
		if (flags > maxFlags) {
			throw new AfpException("Value for flags too large. Max allowed is "
					+ maxFlags);
		}
		this.flags = flags;
	}

	protected void setReserved(int reserved) throws AfpException {

		// Check size
		if (reserved > maxReserved) {
			throw new AfpException(
					"Value for reserved too large. Max allowed is "
							+ maxReserved);
		}
		this.reserved = reserved;
	}

	protected void setSFIdentifier(int identifier) throws AfpException {

		// Check size
		if (identifier > maxIdentifier) {
			throw new AfpException("Value for identifier too large ("
					+ getSFIdentifierString() + "). Max allowed is "
					+ AfpStructuredFieldDefinitions.hexString(maxIdentifier, 6));
		}
		this.identifier = identifier;
		identifierAbbrev = afpDefs.getAbbreviation(identifier);
	}

	public void write(BufferedWriter out, AfpReadWriteProperties props)
			throws IOException, AfpException {
		write(out, props, 0);
	}

	public void write(BufferedWriter out, AfpReadWriteProperties props,
			int level) throws IOException, AfpException {

		// Make prefix
		StringBuffer prefixSB = new StringBuffer();
		for (int i = 0; i < level; i++) {
			prefixSB.append(" ");
		}
		String prefix = prefixSB.toString();

		// Write identifier
		String abbrev = afpDefs.getAbbreviation(identifier);
		String desc = afpDefs.getDescription(identifier);

		out.write(prefix);
		if (abbrev == null) {
			out.write(StringUtils.leftPad(
					getSFIdentifierString().toUpperCase(), 6, '0'));
			out.write(": ");
		} else {
			out.write(abbrev);
			out.write(": ");
			out.write(desc);
			out.newLine();
			out.write(prefix);
			out.write("  ");
		}

		// Write length
		out.write("length:");
		if (mustWriteMD5(props)) {
			out.write("????");
		} else {
			out.write(StringUtils.leftPad(AfpStructuredFieldDefinitions
					.hexString(length, 4), 4, '0'));
		}

		// Write flags
		out.write(" flags:");
		out.write(StringUtils.leftPad(AfpStructuredFieldDefinitions.hexString(
				flags, 2), 2, '0'));

		// Write reserved
		out.write(" reserved:");
		out.write(StringUtils.leftPad(AfpStructuredFieldDefinitions.hexString(
				reserved, 4), 4, '0'));

		// Write data
		out.newLine();
		writeData(out, props, prefix);
	}

	public void write(OutputStream out, AfpReadWriteProperties props)
			throws IOException {

		// Write CCC
		out.write(ccc);

		// Write length
		byte b = (byte) (length >> 8);
		out.write(b);
		b = (byte) (length & 0xff);
		out.write(b);

		// Write identifier
		b = (byte) (identifier >> 16 & 0xff);
		out.write(b);
		b = (byte) (identifier >> 8 & 0xff);
		out.write(b);
		b = (byte) (identifier & 0xff);
		out.write(b);

		// Write flags
		out.write(flags);

		// Write reserved
		b = (byte) (reserved >> 8 & 0xff);
		out.write(b);
		b = (byte) (reserved & 0xff);
		out.write(b);

		// Write data
		out.write(getData());
	}

	private boolean mustWriteMD5(AfpReadWriteProperties props) {
		return props.getMessageDigestThreshold() > -1
				&& data.length > props.getMessageDigestThreshold();
	}

	public void writeData(BufferedWriter out, AfpReadWriteProperties props,
			String prefix) throws IOException, AfpException {

		// Get data
		byte[] data = getData();

		// Write MD5 sum if data too big
		if (mustWriteMD5(props)) {
			if (writeMd(out, props, prefix, data)) {
				return;
			}
		}

		// Write data
		AfpDump.dumpData(out, props, "", prefix, data);
	}

	public boolean writeMd(BufferedWriter out, AfpReadWriteProperties props,
			String prefix, byte[] data) throws IOException, AfpException {

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(data);
			byte[] digest = messageDigest.digest();

			// Write digext
			AfpDump.dumpData(out, props, "MD5: ", prefix, digest);

		} catch (NoSuchAlgorithmException e) {
			LOG.error("No MD5 MessageDigest", e);
			return false;
		}

		return true;
	}
}
