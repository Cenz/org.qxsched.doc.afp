package org.qxsched.doc.afp.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpFactory;
import org.qxsched.doc.afp.AfpReadWriteProperties;
import org.qxsched.doc.afp.AfpRecord;

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
 * Class {@link AfpSplit} is a simple utility that reads an AFP file and
 * generates a ZIP file containing one entry per AFP record. It is implemented
 * using classes in package {@link org.qxsched.doc.afp}.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpSplit {

	private static Logger LOG = Logger.getLogger(AfpSplit.class);

	private static final NumberFormat MY_DEC_FORMAT_8 = new DecimalFormat();

	static {

		// Set MY_DEC_FORMAT_8
		MY_DEC_FORMAT_8.setMinimumFractionDigits(0);
		MY_DEC_FORMAT_8.setMinimumIntegerDigits(8);
		MY_DEC_FORMAT_8.setGroupingUsed(false);
	}

	public static void main(String[] arg) {

		try {

			// Run
			run(arg);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Exit OK
		System.exit(0);
	}

	public static void run(String[] arg) throws Exception {

		// Check arguments
		if (arg.length < 2 || arg.length > 3) {
			throw new Exception("Usage error: " + usage());
		}

		// Props
		AfpReadWriteProperties props = AfpReadWriteProperties.instance();
		props.setConvertSpecificNone();

		// Get file names
		String fnIn = arg[0];
		String fnOut = arg[1];

		// Make entry prefix
		String entryPre = "";
		if (arg.length > 2) {
			entryPre = arg[2];
		}

		// Open AFP file for reading
		InputStream in = new BufferedInputStream(new FileInputStream(fnIn));

		// Open output file for writing records
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(fnOut)));

		// Create AfpFactory
		AfpFactory fact = AfpFactory.createAfpFactory();
		fact.setAfpReadWriteProperties(props);

		// Set input stream
		fact.setInputStream(in);
		long i = 0;
		for (AfpRecord rec = fact.createAfpRecord(); rec != null; rec = fact
				.createAfpRecord()) {

			// Make entry name
			String entryNr = MY_DEC_FORMAT_8.format(i);
			StringBuffer entryNameSb = new StringBuffer(entryPre);
			entryNameSb.append(entryNr.substring(0, entryNr.length() - 6));
			// entryNameSb.append("/");
			entryNameSb.append(entryNr.substring(entryNr.length() - 6, entryNr
					.length() - 4));
			entryNameSb.append("/");
			entryNameSb.append(entryNr.substring(entryNr.length() - 4, entryNr
					.length() - 2));
			// entryNameSb.append("/");
			entryNameSb.append(entryNr.substring(entryNr.length() - 2, entryNr
					.length()));
			entryNameSb.append(".");
			entryNameSb.append(rec.getSFIdentifierAbbrev());

			// Make ZIP Entry
			ZipEntry e = new ZipEntry(entryNameSb.toString());

			// Put entry
			out.putNextEntry(e);

			// Write record
			rec.write(out, props);

			// Close entry
			out.closeEntry();

			// Increment counter
			i++;
		}

		// Close files
		in.close();
		out.close();
	}

	private static String usage() {
		return AfpSplit.class.getName() + " <afp-in> <zip-out> [entry-prefix]";
	}
}
