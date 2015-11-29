package org.qxsched.doc.afp.util;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

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
 * Class {@link AfpDumpTest} tests class {@link AfpDump}.
 * 
 * @author Vincenzo Zocca
 */
public class AfpDumpTest extends TestCase {

	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(AfpDumpTest.class);

	private static File sample1In = new File("target/test-classes/sample1.afp");
	// private static File sample1Out = new File(sample1In.getPath() + ".out");
	// private static File sample1Dump = new File(sample1In.getPath() +
	// ".dump");

	public void test1() {

		try {

			// Run
			// String[] arg = { "-c", "tle", sample1In.getPath() };
			String[] arg = { "--md-thold", "20", "-c", "tle,nop",
					sample1In.getPath() };
			// String[] arg = { "-h" };
			AfpDump afpDump = new AfpDump(arg);
			afpDump.run();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}