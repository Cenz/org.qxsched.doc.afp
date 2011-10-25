package org.qxsched.doc.afp;

import java.util.HashMap;
import java.util.Map;

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
 * Class {@link AfpReadWriteProperties} contains properties for reading and
 * writing AFP records. A singleton instance is available which is used by
 * default. Changes in the singleton instance apply to all operations that use
 * {@link AfpReadWriteProperties} but which are not specified.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpReadWriteProperties {

	private static AfpReadWriteProperties instance;

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the singleton instance.
	 */
	public static synchronized AfpReadWriteProperties instance() {

		// Return instance not null
		if (instance != null) {
			return instance;
		}

		// Instantiate and return
		instance = new AfpReadWriteProperties();
		return instance;
	}

	private Map<Integer, String> convertSpecific;
	private Map<Integer, String> levelPrefix = new HashMap<Integer, String>();
	private int levelSpaces = 2;
	private int messageDigestThreshold = -1;

	/**
	 * Simple constructor.
	 */
	public AfpReadWriteProperties() {
	}

	public String getLevelPrefix(int level) {

		if (levelPrefix.containsKey(level)) {
			return levelPrefix.get(level);
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < level; i++) {
			for (int j = 0; j < levelSpaces; j++) {
				sb.append(" ");
			}
		}
		String prefix = sb.toString();

		synchronized (levelPrefix) {
			levelPrefix.put(level, prefix);
		}

		return prefix;
	}

	/**
	 * Returns the threshold for the data length at which a message digest is
	 * shown instead of the actual contents. A negative value means no threshold
	 * exists.
	 * 
	 * @return the threshold for the data length at which a message digest is
	 *         shown instead of the actual contents.
	 */
	public int getMessageDigestThreshold() {
		return messageDigestThreshold;
	}

	/**
	 * Returns true if the supplied generic AFP record must be converted to a
	 * specific object during reading.
	 * 
	 * @param rec
	 *            the generic record
	 */
	public boolean isConvertSpecific(AfpRecord rec) {
		return isConvertSpecific(rec.getSFIdentifier());
	}

	/**
	 * Returns true if objects of the supplied record identifier type must be
	 * converted to a specific object during reading.
	 * 
	 * The default behavior is that all generic AFP records should be converted
	 * to specific object types.
	 * 
	 * @param identifier
	 *            the AFP record identifier
	 */
	public boolean isConvertSpecific(Integer identifier) {

		// Convert all if map is null
		if (convertSpecific == null) {
			return true;
		}

		// Convert if map contains key
		return convertSpecific.containsKey(identifier);
	}

	/**
	 * Sets that objects of supplied identifier type must be converted to
	 * specific object types during read.
	 * 
	 * @param identifier
	 *            the AFP record identifier
	 */
	public void setConvertSpecific(Integer identifier) {

		// Create map if null
		if (convertSpecific == null) {
			convertSpecific = new HashMap<Integer, String>();
		}

		// Set identifier
		convertSpecific.put(identifier, "");
	}

	/**
	 * Sets that objects of all identifier types must be converted to specific
	 * object types during read.
	 */
	public void setConvertSpecificAll() {
		convertSpecific = null;
	}

	/**
	 * Sets that no objects must be converted to specific object types during
	 * read.
	 */
	public void setConvertSpecificNone() {
		convertSpecific = new HashMap<Integer, String>();
	}

	/**
	 * Sets the threshold for the data length at which a message digest is shown
	 * instead of the actual contents.
	 * 
	 * @param messageDigestThreshold
	 *            the threshold for the data length at which a message digest is
	 *            shown instead of the actual contents.
	 */
	public void setMessageDigestThreshold(int messageDigestThreshold) {
		this.messageDigestThreshold = messageDigestThreshold;
	}

	/**
	 * Sets that objects of supplied identifier types must NOT be converted to
	 * specific object types during read.
	 * 
	 * @param identifier
	 *            the AFP record identifier
	 */
	public void unsetConvertSpecific(Integer identifier) {

		// Do nothing if null
		if (convertSpecific == null) {
			return;
		}

		// Remove identifier
		convertSpecific.remove(identifier);
	}
}
