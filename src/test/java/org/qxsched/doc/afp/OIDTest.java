package org.qxsched.doc.afp;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpException;
import org.qxsched.doc.afp.AfpReadWriteProperties;
import org.qxsched.doc.afp.OID;
import org.qxsched.doc.afp.util.AfpDump;

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
 * Class {@link OIDTest} tests class {@link OID}.
 * 
 * @author Vincenzo Zocca
 */
public class OIDTest extends TestCase {

	private static Logger LOG = Logger.getLogger(OIDTest.class);

	private static final String OID_STR_1 = "{1.1.1.200}";
	private static final String OID_STR_2 = "{1.3.18.0.4.1.1.14}";
	private static final byte[] OID_UNENC = { 0x06, 0x07, 0x2B, 0x12, 0x00,
			0x04, 0x01, 0x01, 0x0E };

	static {
		for (int i = 0; i < OID_UNENC.length; i++) {
		}
	}

	public void test1() {

		try {

			AfpReadWriteProperties arwProps = new AfpReadWriteProperties();

			OID oid1 = new OID(OID_STR_2);
			byte[] oid1Data = oid1.getData();
			LOG.debug("oid1Data: "
					+ AfpDump.dumpData(arwProps, "", "", oid1Data, false));
			String oid1String = oid1.toString();
			LOG.debug("oid1String: " + oid1String);

			OID oid2 = new OID(oid1Data);
			byte[] oid2Data = oid2.getData();
			LOG.debug("oid2Data: "
					+ AfpDump.dumpData(arwProps, "", "", oid2Data, false));
			String oid2String = oid2.toString();
			LOG.debug("oid2String: " + oid2String);

			assertEquals(oid1, oid2);

		} catch (AfpException e) {
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void off_test2() {

		try {

			// byte[] data = { 0x48, (byte) 0x81 };
			// byte[] data = { 0x06, 0x03, (byte) 0x81, 0x34, 0x03 };
			byte[] data = { 0x03 };
			OID oid = new OID(data);
			String oidStr = oid.toString();
			LOG.debug("oidStr: " + oidStr);

		} catch (AfpException e) {
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
}