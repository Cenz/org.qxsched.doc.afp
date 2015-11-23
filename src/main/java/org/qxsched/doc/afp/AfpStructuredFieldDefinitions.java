package org.qxsched.doc.afp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

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
 * Class {@link AfpStructuredFieldDefinitions} contains AFP structured
 * definition codes.
 * 
 * The codes, abbreviations and descriptions are from IBM document
 * "S544-3884-04/Programming Guide and Line Data Reference" and are stored in a
 * property file named identical to this java file -except for the file
 * extension of course.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpStructuredFieldDefinitions {

	private static AfpStructuredFieldDefinitions instance = null;
	private static Logger LOG = Logger
			.getLogger(AfpStructuredFieldDefinitions.class);

	/**
	 * Returns the supplied integer as hexadecimal string. The string starts
	 * with <code>"0x"</code>. The hexadecimal string is padded with
	 * <code>'0'</code> characters to get to the required width.
	 * 
	 * @param val
	 *            the integer value.
	 * @param width
	 *            the qequired width, without considering the leading
	 *            <code>"0x"</code>.
	 * @return the supplied integer as hexadecimal string.
	 */
	public static String hexString(Integer val, int width) {
		return "0x"
				+ StringUtils.leftPad(Integer.toString(val, 16).toUpperCase(),
						width, '0');
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the singleton instance.
	 * @throws AfpException
	 *             if an exception occurs when reading the codes table;
	 */
	public synchronized static AfpStructuredFieldDefinitions instance()
			throws AfpException {

		// Instantiate
		if (instance == null) {
			instance = new AfpStructuredFieldDefinitions();
		}

		// Return if already loaded
		return instance;
	}

	private Map<String, Integer> abbrev2code = new HashMap<String, Integer>();
	private Map<Integer, Integer> begin2end = new HashMap<Integer, Integer>();
	private Map<Integer, String> code2abbrev = new TreeMap<Integer, String>();
	private Map<Integer, String> code2desc = new HashMap<Integer, String>();
	private Map<Integer, Integer> end2begin = new HashMap<Integer, Integer>();

	private AfpStructuredFieldDefinitions() throws AfpException {

		// Get resource
		String resourceName = AfpStructuredFieldDefinitions.class.getName()
				.replace('.', '/')
				+ ".properties";
		if (LOG.isTraceEnabled()) {
			LOG.trace("Loading resource " + resourceName);
		}

		// Map properties object
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(resourceName);
		Properties props = new Properties();
		try {
			props.load(is);
		} catch (IOException e) {
			throw new AfpException(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					throw new AfpException(e.getMessage(), e);
				}
			}
		}

		// Fill maps
		Set<Object> keys = props.keySet();
		for (Object keyObj : keys) {

			// Cast key
			String key = (String) keyObj;
			String keyLc = key.toLowerCase();

			// Check key
			if (!keyLc.matches("^0x[0-9a-f]+$")) {
				LOG.error("Illegal key '" + key + "' in resource "
						+ resourceName);
			}

			// Make integer key
			Integer keyInt = Integer.decode(key);

			// Get value and split
			String val = props.getProperty(key);
			String[] valSplit = val.split("\\W+", 2);

			// Make abbreviation mapping
			code2abbrev.put(keyInt, valSplit[0].toUpperCase());
			abbrev2code.put(valSplit[0].toUpperCase(), keyInt);

			// Make description string
			if (valSplit.length > 1) {
				code2desc.put(keyInt, valSplit[1]);
			}
		}

		// Make begin/end maps
		// Loop through codes
		Set<Integer> codeSet = code2abbrev.keySet();
		for (Integer codeBegin : codeSet) {

			// Get abbreviation and description
			String abbrev = code2abbrev.get(codeBegin);
			String desc = code2desc.get(codeBegin);

			// Do nothing if not begin
			if (!desc.toLowerCase().startsWith("begin")) {
				continue;
			}

			// Make "end" abbreviation
			String abbrevEnd = abbrev.replaceFirst("^[A-Z]", "E");
			abbrevEnd = abbrevEnd.replaceFirst("^[a-z]", "e");

			// Throw exception if "end" abbreviation does not exist
			Integer codeEnd = abbrev2code.get(abbrevEnd);
			if (codeEnd == null) {
				throw new AfpException(
						"Did not find \"end\" abbreviation for \"begin\" abbreviation "
								+ abbrev + " (" + desc + ")");
			}
			if (LOG.isTraceEnabled()) {
				LOG.trace("code-bigin/code-end:" + hexString(codeBegin, 6)
						+ "/" + hexString(codeEnd, 6));
			}

			// Assert begin2end
			if (begin2end.containsKey(codeBegin)) {
				throw new AfpException("Unexpected state. Code "
						+ hexString(codeBegin, 6)
						+ " defined miltiple times in begin-to-end map.");
			}
			begin2end.put(codeBegin, codeEnd);

			// Assert end2begin
			Integer codeBeginError = end2begin.get(codeEnd);
			if (codeBeginError != null) {

				// Get abbreviations
				String abbrevBegin = code2abbrev.get(codeBegin);
				String abbrevBeginError = code2abbrev.get(codeBeginError);

				throw new AfpException("End-code '" + abbrevEnd + "'/"
						+ hexString(codeEnd, 1)
						+ " matches multiple begin-codes: '" + abbrevBeginError
						+ "'/" + hexString(codeBeginError, 6) + " and '"
						+ abbrevBegin + "'/" + hexString(codeBegin, 6));
			}
			end2begin.put(codeEnd, codeBegin);
		}
	}

	/**
	 * Return all known structured definition abbreviation strings in the
	 * object.
	 * 
	 * @return all known structured definition abbreviation strings in the
	 *         object.
	 */
	public Set<String> getAbbrev() {
		return abbrev2code.keySet();
	}

	/**
	 * Returns the abbreviation string for the supplied structured definition
	 * code. If code is unknown, <code>null</code> is returned.
	 * 
	 * @param code
	 *            the structured definition code.
	 * @return the abbreviation string for the supplied structured definition
	 *         code.
	 */
	public String getAbbreviation(Integer code) {
		return code2abbrev.get(code);
	}

	/**
	 * Returns the structured definition code for the supplied abbreviation
	 * string. If the abbreviation is unknown, <code>null</code> is returned.
	 * 
	 * @param abbrev
	 *            the structured definition code.
	 * @return the description string for the supplied structured definition
	 *         code.
	 */
	public Integer getCode(String abbrev) {
		return abbrev2code.get(abbrev.toUpperCase());
	}

	/**
	 * Return all known structured definition codes in the object.
	 * 
	 * @return all known structured definition codes in the object.
	 */
	public Set<Integer> getCodes() {
		return code2abbrev.keySet();
	}

	/**
	 * Returns the description string for the supplied structured definition
	 * code. If code is unknown, <code>null</code> is returned.
	 * 
	 * @param code
	 *            the structured definition code.
	 * @return the description string for the supplied structured definition
	 *         code.
	 */
	public String getDescription(Integer code) {
		return code2desc.get(code);
	}

	/**
	 * Returns the matching begin-code for the supplied end-group code. If no
	 * begin-code is found -e.g. when the supplied code is not an end- group
	 * code- then <code>null</code> is returned.
	 * 
	 * @param code
	 *            the end-group code.
	 * @return the matching begin-code for the supplied end-group code.
	 */
	public Integer getGroupBegin(Integer code) {
		return end2begin.get(code);
	}

	/**
	 * Returns the matching end-code for the supplied begin-group code. If no
	 * end-code is found -e.g. when the supplied code is not a begin-group code-
	 * then <code>null</code> is returned.
	 * 
	 * @param code
	 *            the begin-group code.
	 * @return the matching end-code for the supplied begingroup -code.
	 */
	public Integer getGroupEnd(Integer code) {
		return begin2end.get(code);
	}

	/**
	 * Returns <code>true</code> if the code belongs to a 'begin-group' AFP
	 * record and <code>false</code> otherwise.
	 * 
	 * @param code
	 *            the code to test.
	 * @return <code>true</code> if the code belongs to a 'begin-group' AFP
	 *         record and <code>false</code> otherwise.
	 */
	public Boolean isGroupBegin(Integer code) {
		return begin2end.containsKey(code);
	}

	/**
	 * Returns <code>true</code> if the code belongs to a 'end-group' AFP record
	 * and <code>false</code> otherwise.
	 * 
	 * @param code
	 *            the code to test.
	 * @return <code>true</code> if the code belongs to a 'end-group' AFP record
	 *         and <code>false</code> otherwise.
	 */
	public Boolean isGroupEnd(Integer code) {
		return end2begin.containsKey(code);
	}

}
