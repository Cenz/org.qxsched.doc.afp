package org.qxsched.doc.afp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;

/*
 * 
 * Copyright 2009, 2010, 2011, 2015 Vincenzo Zocca
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
 * Interface {@link AfpRecord} represents the minimum Structured Fields in an
 * AFP record.
 * 
 * @author Vincenzo Zocca
 */
public interface AfpRecord {

	/**
	 * Returns the record data as byte array.
	 * 
	 * @return the record data as byte array.
	 */
	public byte[] getData();

	/**
	 * Returns the 1 byte flags field from the structured field introducer as
	 * integer.
	 * 
	 * @return the 1 byte flags field from the structured field introducer as
	 *         integer.
	 */
	public int getFlags();

	/**
	 * Returns the 2 bytes length field from the structured field introducer as
	 * integer.
	 * 
	 * @return the 2 bytes length field from the structured field introducer as
	 *         integer.
	 */
	public int getLength();

	/**
	 * Returns the 2 bytes reserved field from the structured field introducer
	 * as integer.
	 * 
	 * @return the 2 bytes reserved field from the structured field introducer
	 *         as integer.
	 */
	public int getReserved();

	/**
	 * Returns the 3 bytes structured field identifier field from the structured
	 * field introducer as integer.
	 * 
	 * @return the 3 bytes structured field identifier field from the structured
	 *         field introducer as integer.
	 */
	public int getSFIdentifier();

	/**
	 * Returns the abbreviated name for the structured field identifier.
	 * 
	 * @return the abbreviated name for the structured field identifier.
	 */
	public String getSFIdentifierAbbrev();

	/**
	 * Returns the 3 bytes structured field identifier field from the structured
	 * field introducer as hexadecimal string.
	 * 
	 * @return the 3 bytes structured field identifier field from the structured
	 *         field introducer as hexadecimal string.
	 */
	public String getSFIdentifierString();

	/**
	 * Returns the flag to denote that the record is ended with CR LF.
	 * 
	 * @return the flag to denote that the record is ended with CR LF.
	 */
	public boolean isEndsInCrLf();
	
	/**
	 * Writes the {@link AfpRecord} in a human readable form to the supplied
	 * buffered writer. Intended for analysis.
	 * 
	 * @param out
	 *            the buffered writer to write to.
	 * @param props
	 *            the properties for writing the record
	 * @param level
	 *            the level at which the record is written
	 * @throws IOException
	 *             if an IO exception occurs.
	 * @throws AfpException
	 *             if an AFP error occurs.
	 */
	public void write(BufferedWriter out, AfpReadWriteProperties props,
			int level) throws IOException, AfpException;

	/**
	 * Writes the {@link AfpRecord} to the supplied output stream.
	 * 
	 * @param out
	 *            the output stream to write to.
	 * @param props
	 *            the properties for writing the record
	 * @throws IOException
	 *             if an IO exception occurs.
	 * @throws AfpException
	 *             if an AFP error occurs.
	 */
	public void write(OutputStream out, AfpReadWriteProperties props)
			throws IOException, AfpException;

	/**
	 * Writes the data from {@link AfpRecord} in a human readable form to the
	 * supplied buffered writer. Intended for analysis.
	 * 
	 * @param out
	 *            the buffered writer to write to.
	 * @param props
	 *            the properties for writing the record
	 * @param prefix
	 *            the prefix to write before starting the triplet
	 * @throws IOException
	 *             if an IO exception occurs.
	 * @throws AfpException
	 *             if an AFP error occurs.
	 */
	public void writeData(BufferedWriter out, AfpReadWriteProperties props,
			String prefix) throws IOException, AfpException;
}
