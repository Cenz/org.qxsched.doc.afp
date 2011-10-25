package org.qxsched.doc.afp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;

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
 * Interface {@link AfpTriplet} represents the minimum attributes in an AFP
 * triplet.
 * 
 * @author Vincenzo Zocca
 */
public interface AfpTriplet {

	/**
	 * Returns the content byte array without length and TID byte.
	 * 
	 * @return the content byte array without length and TID byte.
	 */
	public byte[] getContent();

	/**
	 * Returns the complete data byte array with length and TID byte.
	 * 
	 * @return the complete data byte array with length and TID byte.
	 */
	public byte[] getData();

	/**
	 * Returns the value of the length byte.
	 * 
	 * @return the value of the length byte.
	 */
	public int getLength();

	public int getTid();

	// /**
	// * Sets the content bytes without length and TID byte.
	// *
	// * @param content
	// * the content bytes without length and TID byte.
	// * @throws AfpException
	// * if an AFP exception occurs.
	// */
	// public void setContent(byte[] content) throws AfpException;

	// /**
	// * Sets the attributes using supplied byte array, starting at supplied
	// * offset.
	// *
	// * @param data
	// * byte array inclusing length and tid bytes.
	// * @param offset
	// * start offset.
	// * @throws AfpException
	// * if an AFP exception occurs.
	// */
	// public void setData(byte[] data, int offset) throws AfpException;

	/**
	 * Writes the {@link AfpTriplet} in a human readable form to the supplied
	 * buffered writer. Intended for analysis.
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
	public void write(BufferedWriter out, AfpReadWriteProperties props,
			String prefix) throws IOException, AfpException;

	/**
	 * Writes the {@link AfpTriplet} to the supplied output stream.
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
}
