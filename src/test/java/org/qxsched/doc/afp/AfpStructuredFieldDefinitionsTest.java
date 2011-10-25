package org.qxsched.doc.afp;

import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpException;
import org.qxsched.doc.afp.AfpStructuredFieldDefinitions;

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
 * Class {@link AfpStructuredFieldDefinitionsTest} tests class
 * {@link AfpStructuredFieldDefinitions}.
 * 
 * @author Vincenzo Zocca
 */
public class AfpStructuredFieldDefinitionsTest extends TestCase {

	private static Logger LOG = Logger
			.getLogger(AfpStructuredFieldDefinitionsTest.class);

	public void test1() {

		try {

			// Get instance
			AfpStructuredFieldDefinitions instance = AfpStructuredFieldDefinitions
					.instance();

			// Get all codes
			Set<Integer> codes = instance.getCodes();

			// Loop through codes
			for (Integer code : codes) {

				// Print code
				String abbrev = instance.getAbbreviation(code);
				String desc = instance.getDescription(code);

				if (LOG.isDebugEnabled()) {
					LOG.debug(Integer.toString(code, 16) + ":" + abbrev + ":"
							+ desc);
				}

			}

		} catch (AfpException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}