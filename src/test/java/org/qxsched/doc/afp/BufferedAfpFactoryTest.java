package org.qxsched.doc.afp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

public class BufferedAfpFactoryTest extends TestCase {

	private static File sample1In = new File("target/test-classes/sample1.afp");

	private static Logger LOG = Logger.getLogger(BufferedAfpFactoryTest.class);

	public void test1() {

		PushbackInputStream in = null;
		try {

			AfpReadWriteProperties arwprops = new AfpReadWriteProperties();
			arwprops.setConvertSpecificNone();

			AfpFactory fact = AfpFactory.createAfpFactory();
			in = new PushbackInputStream(new BufferedInputStream(new FileInputStream(sample1In)));
			fact.setAfpReadWriteProperties(arwprops);
			fact.setInputStream(in);
			BufferedAfpFactory buffact = new BufferedAfpFactory(fact, 2);
			LOG.info("buffact: PASS0 hist-size/unget-buff: "
					+ buffact.getHistoryStackSize() + "/"
					+ buffact.ungetStackSize());

			AfpRecord rec1 = buffact.createAfpRecord();
			LOG.info("1st record: " + rec1.getSFIdentifierAbbrev());

			AfpRecord rec2 = buffact.createAfpRecord();
			LOG.info("2nd record: " + rec2.getSFIdentifierAbbrev());
			AfpRecord rec3 = buffact.createAfpRecord();
			LOG.info("3rd record: " + rec3.getSFIdentifierAbbrev());
			LOG.info("buffact: PASS1 hist-size/unget-buff: "
					+ buffact.getHistoryStackSize() + "/"
					+ buffact.ungetStackSize());

			buffact.unget();
			buffact.unget();
			LOG.info("buffact: PASS2 hist-size/unget-buff: "
					+ buffact.getHistoryStackSize() + "/"
					+ buffact.ungetStackSize());

			AfpRecord rec2Reget = buffact.createAfpRecord();
			LOG.info("2nd re-gotten record: "
					+ rec2Reget.getSFIdentifierAbbrev());
			AfpRecord rec3Reget = buffact.createAfpRecord();
			LOG.info("3rd re-gotten record: "
					+ rec3Reget.getSFIdentifierAbbrev());
			LOG.info("buffact: PASS3 hist-size/unget-buff: "
					+ buffact.getHistoryStackSize() + "/"
					+ buffact.ungetStackSize());

			buffact.unget();
			buffact.unget();
			LOG.info("buffact: PASS4 hist-size/unget-buff: "
					+ buffact.getHistoryStackSize() + "/"
					+ buffact.ungetStackSize());

			AfpRecord rec2Reget2 = buffact.createAfpRecord();
			LOG.info("2nd re-re-gotten record: "
					+ rec2Reget2.getSFIdentifierAbbrev());
			AfpRecord rec3Reget2 = buffact.createAfpRecord();
			LOG.info("3rd re-re-gotten record: "
					+ rec3Reget2.getSFIdentifierAbbrev());
			LOG.info("buffact: PASS5 hist-size/unget-buff: "
					+ buffact.getHistoryStackSize() + "/"
					+ buffact.ungetStackSize());

			assertEquals(
					"Expected 2nd gotten, and re-re-gotten record to be identical",
					rec2, rec2Reget2);
			assertEquals(
					"Expected 3rd gotten, and re-re-gotten record to be identical",
					rec3, rec3Reget2);

			// Expect exception
			LOG.info("buffact: PASS6 hist-size/unget-buff: "
					+ buffact.getHistoryStackSize() + "/"
					+ buffact.ungetStackSize());
			buffact.unget();
			buffact.unget();
			buffact.unget();
			fail("Expected IllegalStateException after ungetting tooo many records");

			in.close();
			in = null;
		} catch (IllegalStateException e) {
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}
}
