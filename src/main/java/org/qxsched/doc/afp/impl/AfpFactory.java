package org.qxsched.doc.afp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpException;
import org.qxsched.doc.afp.AfpReadWriteProperties;
import org.qxsched.doc.afp.AfpRecord;
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
 * Class {@link AfpFactory} creates {@link AfpRecord} objects.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpFactory extends org.qxsched.doc.afp.AfpFactory {

	// Logger
	private static Logger LOG = Logger.getLogger(AfpFactory.class);

	private AfpClasses classes;
	private InputStream in;
	private AfpReadWriteProperties props = AfpReadWriteProperties.instance();

	@Override
	public AfpRecord createAfpRecord() throws AfpException {

		// Return if no more bytes
		try {
			if (getInputStream().available() == 0) {
				return null;
			}
		} catch (IOException e) {
			throw new AfpException("Unexpected exception ", e);
		}

		// Initialize
		init();

		// Read record
		AfpRecord rec = new GenericAfpRecord(in);

		// Return if the record must not be read into a specific class
		if (!props.isConvertSpecific(rec)) {
			return rec;
		}

		// Instantiate specific class
		rec = classes.instantiateSpecific(rec, this);

		// Return
		return rec;
	}

	public AfpTriplet[] createAfpTriplets(byte[] data, int offset, int max)
			throws AfpException {

		// Make list
		List<AfpTriplet> list = new ArrayList<AfpTriplet>();

		boolean go = true;
		int cnt = 0;
		while (go) {

			// Break if count reached
			if (max > -1 && cnt == max) {
				break;
			}

			// Break if offset at end of array
			if (offset == data.length) {
				break;
			}

			// Create AfpTriplet
			AfpTriplet t = new GenericAfpTriplet(data, offset);

			// Instantiate specific class
			AfpTriplet tSpec = classes.instantiateSpecific(t, this);

			// Add to list
			list.add(tSpec);

			// Increment offset
			offset += t.getLength();
		}

		// Make return array and return
		AfpTriplet[] ret = new AfpTriplet[list.size()];
		ret = list.toArray(ret);
		return ret;
	}

	@Override
	public InputStream getInputStream() throws AfpException {
		return in;
	}

	private void init() throws AfpException {
		if (classes != null) {
			return;
		}
		classes = AfpClasses.instance();
	}

	@Override
	public void setAfpReadWriteProperties(AfpReadWriteProperties props)
			throws AfpException {
		this.props = props;
	}

	@Override
	public void setInputStream(InputStream in) throws AfpException {
		this.in = in;
	}

}
