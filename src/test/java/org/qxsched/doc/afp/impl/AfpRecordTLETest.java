package org.qxsched.doc.afp.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpFactory;
import org.qxsched.doc.afp.AfpReadWriteProperties;
import org.qxsched.doc.afp.AfpRecord;
import org.qxsched.doc.afp.AfpTriplet;

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
 * Class {@link AfpRecordTLETest} tests class {@link AfpRecordTLE}.
 * 
 * @author Vincenzo Zocca
 */
public class AfpRecordTLETest extends TestCase {

	private static final File AA_DIR_TMP = new File("target/test-classes");
	private static final File TLE_SAMPLE = new File(AA_DIR_TMP,
			"tle-sample.afp");
	private static final File TLE_TEST_CMP = new File(AA_DIR_TMP,
			"tle-test-cmp.afp");

	private static Logger LOG = Logger.getLogger(AfpRecordTLETest.class);

	public void test_1() {

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
			LOG.info("AttName: " + afpRecordTLE.getAttName());
			LOG.info("AttVal: " + afpRecordTLE.getAttVal());

			// Close input stream
			LOG.info("Close: " + TLE_SAMPLE.getPath());
			in.close();

			// Open output stream
			LOG.info("Open for writing: " + TLE_TEST_CMP.getPath());
			OutputStream out = new BufferedOutputStream(new FileOutputStream(
					TLE_TEST_CMP));

			LOG.info("Write: " + afpRecordTLE.getSFIdentifierAbbrev());
			afpRecordTLE.write(out, arwProps);
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
					System.out));
			afpRecordTLE.write(w, arwProps);
			w.flush();

			// Close output stream
			LOG.info("Close: " + TLE_TEST_CMP.getPath());
			out.close();

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_2() {

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
			LOG.info("AttName: " + afpRecordTLE.getAttName());
			LOG.info("AttVal: " + afpRecordTLE.getAttVal());

			// Close input stream
			LOG.info("Close: " + TLE_SAMPLE.getPath());
			in.close();

			// Copy triples
			AfpTriplet[] trips = afpRecordTLE.getAfpTriplets();
			AfpTriplet02 t02New = null;
			// AfpTriplet36 t36New = null;
			for (int i = 0; i < trips.length; i++) {

				// Triplet
				AfpTriplet trip = trips[i];

				// AfpTriplet02
				if (trip instanceof AfpTriplet02) {

					// Cast
					AfpTriplet02 t02Orig = (AfpTriplet02) trip;

					// Get fQNType and fQName
					int fQNType = t02Orig.getFQNType();
					Object fQName = t02Orig.getFQName();

					if (fQName instanceof String) {
						t02New = new AfpTriplet02(fQNType, (String) fQName);
					}
				}

				// AfpTriplet36
				if (trip instanceof AfpTriplet36) {

					// Cast
					// AfpTriplet36 t36Orig = (AfpTriplet36) trip;

					// t36Orig.get
				}
			}

			if (t02New == null) {
				throw new Exception("Failed to instantiate new AfpTriplet02");
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
}