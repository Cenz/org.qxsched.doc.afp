package org.qxsched.doc.afp.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpFactory;
import org.qxsched.doc.afp.AfpReadWriteProperties;
import org.qxsched.doc.afp.AfpRecord;
import org.qxsched.doc.afp.AfpTriplet;
import org.qxsched.doc.afp.OID;
import org.qxsched.doc.afp.util.AfpDump;

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
 * Class {@link AfpTriplet02Test} tests class {@link AfpTriplet02}.
 * 
 * @author Vincenzo Zocca
 */
public class AfpTriplet02Test extends TestCase {

	private static final File AA_DIR_TMP = new File("target/test-classes");
	private static final File TLE_SAMPLE = new File(AA_DIR_TMP,
			"tle-sample.afp");
	private static final String URL_TEST = "http://localhost/foo/bar";
	private static final String OID_TEST = "{1.2.3}";

	private static Logger LOG = Logger.getLogger(AfpTriplet02Test.class);

	public void test_1_string() {

		try {

			// Create AfpFactory
			LOG.info("Instantiating properties and factory");
			AfpReadWriteProperties arwProps = new AfpReadWriteProperties();
			arwProps.setConvertSpecificNone();
			AfpFactory fact = AfpFactory.createAfpFactory();
			fact.setAfpReadWriteProperties(arwProps);

			// Open input stream
			LOG.info("Open for readiong: " + TLE_SAMPLE.getPath());
			InputStream in = new BufferedInputStream(new FileInputStream(TLE_SAMPLE));
			fact.setInputStream(in);

			// Read record
			LOG.info("Reading record");
			AfpRecord rec = fact.createAfpRecord();
			LOG.info("Got record: " + rec.getSFIdentifierAbbrev());

			// Make specific TLE record
			AfpRecordTLE afpRecordTLE = new AfpRecordTLE(rec, fact);

			// Close input stream
			LOG.info("Close: " + TLE_SAMPLE.getPath());
			in.close();

			// Get triplet
			AfpTriplet[] trips = afpRecordTLE.getAfpTriplets();
			AfpTriplet02 t02Orig = null;
			AfpTriplet02 t02New = null;
			for (int i = 0; i < trips.length; i++) {

				// Triplet
				AfpTriplet trip = trips[i];

				// AfpTriplet02
				if (trip instanceof AfpTriplet02) {

					// Cast
					t02Orig = (AfpTriplet02) trip;

					// Get fQNType and fQName
					int fQNType = t02Orig.getFQNType();
					Object fQName = t02Orig.getFQName();

					if (fQName instanceof String) {
						t02New = new AfpTriplet02(fQNType, (String) fQName);
					}
				}
			}

			// Throw exception
			if (t02New == null) {
				throw new Exception("Failed to instantiate new AfpTriplet02.");
			}

			// Get original data
			byte[] dataOrig = t02Orig.getData();
			byte[] dataNew = t02New.getData();

			// Check length
			StringBuffer err = new StringBuffer();
			if (dataOrig.length != dataNew.length) {
				err.append("Original and new data array differ in length, ");
				err.append(dataOrig.length);
				err.append(" / ");
				err.append(dataNew.length);
				err.append(". ");
			}

			// Check bytes
			for (int i = 0; i < dataOrig.length && i < dataNew.length; i++) {

				if (dataOrig[i] != dataNew[i]) {
					err.append("Original and new data array differ ");
					err.append("starting at index ");
					err.append(i);
					err.append(". ");
					break;
				}
			}

			// Make information message
			StringBuffer info = new StringBuffer("Triplets: ");
			info.append(System.getProperty("line.separator"));
			AfpDump.dumpData(info, arwProps, "T02 ORIG: ", "", dataOrig, true);
			AfpDump.dumpData(info, arwProps, "T02 NEW:  ", "", dataNew, false);
			LOG.info(info);

			// Throw exception
			if (err.length() > 0) {
				err.append(info);
				throw new Exception(err.toString());
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_2_url() {

		try {
			URL urlOrig = new URL(URL_TEST);
			AfpTriplet02 t02url = new AfpTriplet02(0x84, urlOrig);
			Object fQName = t02url.getFQName();
			if (fQName instanceof URL) {
				LOG.info("urlOrig: " + urlOrig.toString());
				LOG.info("fQName: " + fQName.toString());
				assertEquals(urlOrig, fQName);
			} else {
				fail("After initialization through URL, returned object NOT instance of "
						+ URL.class.getName()
						+ ", but of "
						+ fQName.getClass().getName());
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_3_oid() {

		try {
			OID oidOrig = new OID(OID_TEST);
			AfpTriplet02 t02oid = new AfpTriplet02(0x84, oidOrig);
			Object fQName = t02oid.getFQName();
			if (fQName instanceof OID) {
				LOG.info("oidOrig: " + oidOrig.toString());
				LOG.info("FQName: " + fQName.toString());
				assertEquals(oidOrig, fQName);
			} else {
				fail("After initialization through OID, returned object NOT instance of "
						+ OID.class.getName()
						+ ", but of "
						+ fQName.getClass().getName());
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
}