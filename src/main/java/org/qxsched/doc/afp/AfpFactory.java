package org.qxsched.doc.afp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ibm.icu.charset.CharsetICU;

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
 * Class {@link AfpFactory} abstract class {@link AfpRecord} objects.
 * 
 * @author Vincenzo Zocca
 * 
 */
public abstract class AfpFactory {

	/**
	 * Contains the default character set for AFP.
	 */
	public static Charset AFP_DEFAULT_CS;

	/**
	 * Contains the default code page name for AFP.
	 */
	public static final String AFP_DEFAULT_CP_NAME = "IBM500";

	private static final Logger LOG = Logger.getLogger(AfpFactory.class);

	static {
		if (AFP_DEFAULT_CP_NAME == null
				|| !CharsetICU.isSupported(AFP_DEFAULT_CP_NAME)) {
			LOG.error("Unsupported character set " + AFP_DEFAULT_CP_NAME);
		}
		AFP_DEFAULT_CS = CharsetICU.forNameICU(AFP_DEFAULT_CP_NAME);
	}

	/**
	 * Returns the {@link AfpFactory} service provider.
	 * 
	 * @return the {@link AfpFactory} service provider.
	 * @throws AfpException
	 *             if an AFP exception occurred.
	 */
	public static AfpFactory createAfpFactory() throws AfpException {

		// Make properties file name
		String resName = AfpFactory.class.getName().replace('.', '/')
				+ ".properties";

		// Get the first occurring resource
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(resName);

		// Create properties
		Properties props = new Properties();
		try {
			props.load(is);
		} catch (IOException e) {
			throw new AfpException("Failed to load properties resource "
					+ resName, e);
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					throw new AfpException("Failed to load properties resource "
							+ resName, e);
				}
			}
		}

		// Get property
		String className = props.getProperty(AfpFactory.class.getName());
		if (className == null) {
			throw new AfpException("No property '" + AfpFactory.class.getName()
					+ "' in resource " + resName);
		}

		// Load the class and return it
		Class<?> cls;
		try {
			cls = Thread.currentThread().getContextClassLoader().loadClass(
					className);
		} catch (ClassNotFoundException e) {
			throw new AfpException("Failed to find class named " + className, e);
		}

		// Instantiate class
		Object clsInstance;
		try {
			clsInstance = (AfpFactory) cls.newInstance();
		} catch (Exception e) {
			throw new AfpException("Failed to instantiate class "
					+ cls.getName(), e);
		}

		// Check class type
		if (!AfpFactory.class.isInstance(clsInstance)) {
			throw new AfpException("Class not " + cls.getName()
					+ " is not an instance of " + AfpFactory.class.getName());
		}

		// Cast
		AfpFactory afpFactoryInstance = (AfpFactory) clsInstance;

		// Return
		return afpFactoryInstance;
	}

	/**
	 * Empty constructor.
	 */
	protected AfpFactory() {
	}

	/**
	 * Creates AFP records from the object's input stream.
	 * 
	 * @return an instantiated {@link AfpRecord} object or <code>null</code>
	 *         when end of inpot stream reached.
	 * @throws AfpException
	 *             if an AFP exception occurred.
	 */
	public abstract AfpRecord createAfpRecord() throws AfpException;

	public abstract AfpTriplet[] createAfpTriplets(byte[] data, int i, int j)
			throws AfpException;

	/**
	 * Returns the object's input stream.
	 * 
	 * NOTE: This may be an input stream derived from the one provided through
	 * {@link #setInputStream(InputStream)}.
	 * 
	 * @return the object's input stream.
	 */
	public abstract InputStream getInputStream() throws AfpException;

	/**
	 * Sets the object's read/write properties.
	 * 
	 * @param props
	 *            the object's read/write properties.
	 * @throws AfpException
	 *             if an AFP exception occurred.
	 */
	public abstract void setAfpReadWriteProperties(AfpReadWriteProperties props)
			throws AfpException;

	/**
	 * Sets the object's input stream.
	 * 
	 * @param in
	 *            the object's input stream.
	 * @throws AfpException
	 *             if an AFP exception occurred.
	 */
	public abstract void setInputStream(InputStream in) throws AfpException;
}
