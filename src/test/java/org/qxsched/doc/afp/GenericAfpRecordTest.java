package org.qxsched.doc.afp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpReadWriteProperties;
import org.qxsched.doc.afp.AfpRecord;
import org.qxsched.doc.afp.GenericAfpRecord;

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
 * Class {@link GenericAfpRecordTestTest} tests class
 * {@link GenericAfpRecordTest}.
 * 
 * @author Vincenzo Zocca
 */
public class GenericAfpRecordTest extends TestCase {

	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(GenericAfpRecordTest.class);

	private static File sample1In = new File("target/test-classes/sample1.afp");
	private static File sample1Out = new File(sample1In.getPath() + ".out");
	private static File sample1Dump = new File(sample1In.getPath() + ".dump");

	public void test1() {

		try {

			// Props
			AfpReadWriteProperties props = AfpReadWriteProperties.instance();

			// Open sample input file for reading
			PushbackInputStream in = new PushbackInputStream(new BufferedInputStream(new FileInputStream(sample1In)));

			// Open sample output file for writing AFP
			OutputStream out = new BufferedOutputStream(new FileOutputStream(
					sample1Out));

			// Open sample output file for dumping AFP
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new BufferedOutputStream(
							new FileOutputStream(sample1Dump))));

			// Read input until finished
			while (in.available() > 0) {

				// Read record
				AfpRecord rec = new GenericAfpRecord(in);

				// Write record
				rec.write(out, props);

				// Write record
				rec.write(bw, props, 0);
			}

			// Close files
			in.close();
			out.close();
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}