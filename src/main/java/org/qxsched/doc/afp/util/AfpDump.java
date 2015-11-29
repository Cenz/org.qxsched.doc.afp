package org.qxsched.doc.afp.util;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpException;
import org.qxsched.doc.afp.AfpFactory;
import org.qxsched.doc.afp.AfpReadWriteProperties;
import org.qxsched.doc.afp.AfpRecord;
import org.qxsched.doc.afp.AfpRecordGrouper;
import org.qxsched.doc.afp.AfpStructuredFieldDefinitions;
import org.qxsched.doc.afp.util.AfpDump.Options.OptionsException;

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
 * Class {@link AfpDump} is a simple utility that prints the contents of an AFP
 * file in human readable format. It is implemented using classes in package
 * {@link org.qxsched.doc.afp}.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpDump {

	// Private class to parse arguments
	protected class Options {

		// Private class for illegal options.
		protected class OptionsException extends Exception {

			private static final long serialVersionUID = 5011257380515029864L;

			public OptionsException() {
				super();
			}

			public OptionsException(String message) {
				super(message);
			}

			public OptionsException(String message, Throwable cause) {
				super(message, cause);
			}

			public OptionsException(Throwable cause) {
				super(cause);
			}

		}

		// The command line parser
		private CmdLineParser cmdLineParser = null;

		// Option -c
		private Map<Integer, Object> convert_specific;
		private CmdLineParser.Option convert_specific_opt;

		// Option --group-records
		private boolean group_records = false;
		private CmdLineParser.Option group_records_opt;

		// Option -h
		private boolean help = false;
		private CmdLineParser.Option help_opt;

		// Option --md-thold
		private Integer md_thold;
		private CmdLineParser.Option md_thold_opt;

		// Option -o
		private String out_file;
		private CmdLineParser.Option out_file_opt;

		// Option --out-cs
		private String out_file_cs;
		private CmdLineParser.Option out_file_cs_opt;

		// Value remaining argument
		private String remainingArg;

		protected Options(String[] args) throws IllegalOptionValueException,
				UnknownOptionException, OptionsException, AfpException {

			// Initialize
			initOptions();

			// Parse the options
			parseOptions(args);
		}

		/**
		 * Returns a map of records that must be converted to specific
		 * implementations. Keys are record codes .
		 * 
		 * @return a map of records that must be converted to specific
		 *         implementations.
		 */
		public Map<Integer, Object> getConvert_specific() {
			return convert_specific;
		}

		/**
		 * Returns the message digest threshold.
		 * 
		 * @return the message digest threshold.
		 */
		protected Integer getMd_thold() {
			return md_thold;
		}

		/**
		 * Returns the output file.
		 * 
		 * @return the output file.
		 */
		protected String getOutFile() {
			return out_file;
		}

		/**
		 * Returns the character set for the output file.
		 * 
		 * @return the character set for the output file.
		 */
		protected String getOutFileCs() {
			return out_file_cs;
		}

		/**
		 * Returns the remaining argument.
		 * 
		 * @return the remaining argument.
		 */
		protected String getRemainingArg() {
			return remainingArg;
		}

		private void initOptions() {

			// Return if cmdLineParser is defined
			if (cmdLineParser != null)
				return;

			// Create the command line parser
			cmdLineParser = new CmdLineParser();

			// Convert specific
			if (OPT_CONVERT_SPECIFIC_CHR == null) {
				convert_specific_opt = new CmdLineParser.Option.StringOption(
						OPT_CONVERT_SPECIFIC_STR);
			} else {
				convert_specific_opt = new CmdLineParser.Option.StringOption(
						OPT_CONVERT_SPECIFIC_CHR.charValue(),
						OPT_CONVERT_SPECIFIC_STR);
			}
			cmdLineParser.addOption(convert_specific_opt);

			// Group records
			if (OPT_GROUP_RECORDS_CHR == null) {
				group_records_opt = new CmdLineParser.Option.BooleanOption(
						OPT_GROUP_RECORDS_STR);
			} else {
				group_records_opt = new CmdLineParser.Option.BooleanOption(
						OPT_GROUP_RECORDS_CHR.charValue(),
						OPT_GROUP_RECORDS_STR);
			}
			cmdLineParser.addOption(group_records_opt);

			// Help
			if (OPT_HELP_CHR == null) {
				help_opt = new CmdLineParser.Option.BooleanOption(OPT_HELP_STR);
			} else {
				help_opt = new CmdLineParser.Option.BooleanOption(OPT_HELP_CHR
						.charValue(), OPT_HELP_STR);
			}
			cmdLineParser.addOption(help_opt);

			// Message digest threshold
			if (OPT_MD_THOLD_CHR == null) {
				md_thold_opt = new CmdLineParser.Option.IntegerOption(
						OPT_MD_THOLD_STR);
			} else {
				md_thold_opt = new CmdLineParser.Option.IntegerOption(
						OPT_MD_THOLD_CHR.charValue(), OPT_MD_THOLD_STR);
			}
			cmdLineParser.addOption(md_thold_opt);

			// Out file
			if (OPT_OUT_FILE_CHR == null) {
				out_file_opt = new CmdLineParser.Option.StringOption(
						OPT_OUT_FILE_STR);
			} else {
				out_file_opt = new CmdLineParser.Option.StringOption(
						OPT_OUT_FILE_CHR.charValue(), OPT_OUT_FILE_STR);
			}
			cmdLineParser.addOption(out_file_opt);

			// Out file charset
			if (OPT_OUT_FILE_CS_CHR == null) {
				out_file_cs_opt = new CmdLineParser.Option.StringOption(
						OPT_OUT_FILE_CS_STR);
			} else {
				out_file_cs_opt = new CmdLineParser.Option.StringOption(
						OPT_OUT_FILE_CS_CHR.charValue(), OPT_OUT_FILE_CS_STR);
			}
			cmdLineParser.addOption(out_file_cs_opt);

		}

		/**
		 * Returns <code>true</code> if the group records option was supplied.
		 * 
		 * @return <code>true</code> if the group records option was supplied.
		 */
		public boolean isGroup_records() {
			return group_records;
		}

		/**
		 * Returns <code>true</code> if the help option was supplied.
		 * 
		 * @return <code>true</code> if the help record options was supplied.
		 */
		public boolean isHelp() {
			return help;
		}

		// Parse options
		private void parseOptions(String args[])
				throws IllegalOptionValueException, UnknownOptionException,
				OptionsException, AfpException {

			// Parse the command line
			cmdLineParser.parse(args);

			// Option -c
			String valS = (String) cmdLineParser
					.getOptionValue(convert_specific_opt);
			if (valS == null) {
				valS = OPT_CONVERT_SPECIFIC_VAL_DEF;
			} else {
				if (valS.matches("^\\s*$")) {
					valS = null;
				}
			}

			if (valS != null) {
				// Instantiate convert specific
				convert_specific = new HashMap<Integer, Object>();

				// Split value
				String[] valArr = valS.split("\\W+");

				// Get AFP structured field definitions
				AfpStructuredFieldDefinitions defs = AfpStructuredFieldDefinitions
						.instance();

				// Loop through elements
				StringBuffer error = new StringBuffer();
				for (int i = 0; i < valArr.length; i++) {

					// Ignore empty
					if (valArr[i].equals("")) {
						continue;
					}

					// Get code for abbreviated type
					Integer code = defs.getCode(valArr[i]);
					if (code == null) {
						error.append("Cannot obtain record code for '");
						error.append(valArr[i]);
						error.append("'. ");
					}

					// Set convert specific
					convert_specific.put(code, valArr[i]);

				}
				if (error.length() > 0) {
					throw new OptionsException(error.toString());
				}
			}

			// Option --group-records
			Boolean boolVal = (Boolean) cmdLineParser
					.getOptionValue(group_records_opt);
			if (boolVal != null) {
				group_records = boolVal.booleanValue();
			}

			// Option -h
			boolVal = (Boolean) cmdLineParser.getOptionValue(help_opt);
			if (boolVal != null) {
				help = boolVal.booleanValue();
			}

			// Option --md-thold
			md_thold = (Integer) cmdLineParser.getOptionValue(md_thold_opt);
			if (md_thold == null) {
				md_thold = OPT_MD_THOLD_VAL_DEF;
			} else if (md_thold.intValue() < 0) {
				md_thold = null;
			}

			// Option -o
			out_file = (String) cmdLineParser.getOptionValue(out_file_opt);

			// Option --out-cs
			out_file_cs = (String) cmdLineParser
					.getOptionValue(out_file_cs_opt);

			// Remaining arguments
			String[] remainingArgs = cmdLineParser.getRemainingArgs();
			switch (remainingArgs.length) {
			case 0:
				break;

			case 1:
				remainingArg = remainingArgs[0];
				break;

			default:
				throw new OptionsException(
						"Zero or one file name can be specified after the options.");
			}
		}
	}

	// Logger
	private static Logger LOG = Logger.getLogger(AfpDump.class);

	private static final Character OPT_CONVERT_SPECIFIC_CHR = new Character('c');
	private static final String OPT_CONVERT_SPECIFIC_STR = "convert-specific";
	private static final String OPT_CONVERT_SPECIFIC_VAL_DEF = "nop";

	private static final Character OPT_GROUP_RECORDS_CHR = new Character('g');
	private static final String OPT_GROUP_RECORDS_STR = "group-records";

	private static final Character OPT_HELP_CHR = new Character('h');
	private static final String OPT_HELP_STR = "help";

	private static final Character OPT_MD_THOLD_CHR = null;
	private static final String OPT_MD_THOLD_STR = "md-thold";
	private static final int OPT_MD_THOLD_VAL_DEF = 0;

	private static final Character OPT_OUT_FILE_CHR = new Character('o');
	private static final String OPT_OUT_FILE_STR = "out";

	private static final Character OPT_OUT_FILE_CS_CHR = null;
	private static final String OPT_OUT_FILE_CS_STR = "out-cs";

	public static void main(String[] arg) {

		try {

			// Instantiate
			AfpDump afpDump = new AfpDump(arg);

			// Run
			afpDump.run();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Exit OK
		System.exit(0);
	}

	protected static String usage() {

		// String buffer
		StringBuffer sb = new StringBuffer();

		// Class
		sb.append(AfpDump.class.getName());
		sb.append(" \\");
		sb.append(System.getProperty("line.separator"));

		// OPT_CONVERT_SPECIFIC
		sb.append("\t");
		sb.append("[");
		if (OPT_CONVERT_SPECIFIC_CHR != null) {
			sb.append("-");
			sb.append(OPT_CONVERT_SPECIFIC_CHR);
			sb.append("|");
		}
		sb.append("--");
		sb.append(OPT_CONVERT_SPECIFIC_STR);
		sb.append(" [tle...]]");
		sb.append(" \\ # Defaults to '");
		sb.append(OPT_CONVERT_SPECIFIC_VAL_DEF);
		sb.append("'. Supply empty string for none.");
		sb.append(System.getProperty("line.separator"));

		// OPT_GROUP_RECORDS
		sb.append("\t");
		sb.append("[");
		if (OPT_GROUP_RECORDS_CHR != null) {
			sb.append("-");
			sb.append(OPT_GROUP_RECORDS_CHR);
			sb.append("|");
		}
		sb.append("--");
		sb.append(OPT_GROUP_RECORDS_STR);
		sb.append("]");
		sb.append(" \\");
		sb.append(System.getProperty("line.separator"));

		// OPT_HELP
		sb.append("\t");
		sb.append("[");
		if (OPT_HELP_CHR != null) {
			sb.append("-");
			sb.append(OPT_HELP_CHR);
			sb.append("|");
		}
		sb.append("--");
		sb.append(OPT_HELP_STR);
		sb.append("]");
		sb.append(" \\");
		sb.append(System.getProperty("line.separator"));

		// OPT_MD_THOLD
		sb.append("\t");
		sb.append("[");
		if (OPT_MD_THOLD_CHR != null) {
			sb.append("-");
			sb.append(OPT_MD_THOLD_CHR);
			sb.append("|");
		}
		sb.append("--");
		sb.append(OPT_MD_THOLD_STR);
		sb.append("]");
		sb.append(" \\ # Defaults to '");
		sb.append(OPT_MD_THOLD_VAL_DEF);
		sb.append("'. Supply <0 to disable.");
		sb.append(System.getProperty("line.separator"));

		// OPT_OUT_FILE
		sb.append("\t");
		sb.append("[");
		if (OPT_OUT_FILE_CHR != null) {
			sb.append("-");
			sb.append(OPT_OUT_FILE_CHR);
			sb.append("|");
		}
		sb.append("--");
		sb.append(OPT_OUT_FILE_STR);
		sb.append(" <out-file>]");
		sb.append(" \\");
		sb.append(System.getProperty("line.separator"));

		// OPT_OUT_FILE_CS
		sb.append("\t");
		sb.append("[");
		if (OPT_OUT_FILE_CS_CHR != null) {
			sb.append("-");
			sb.append(OPT_OUT_FILE_CS_CHR);
			sb.append("|");
		}
		sb.append("--");
		sb.append(OPT_OUT_FILE_CS_STR);
		sb.append(" <out-file-charset>]");
		sb.append(" \\");
		sb.append(System.getProperty("line.separator"));

		// In-File
		sb.append("\t[in-file]");
		sb.append(System.getProperty("line.separator"));

		// Return
		return sb.toString();
	}

	// AFP read/write properties
	private AfpReadWriteProperties arwProps;

	// Input file
	private InputStream in;

	// Options
	private Options opts;

	// Output file
	private BufferedWriter out;

	public AfpDump(String[] args) throws IllegalOptionValueException,
			UnknownOptionException, OptionsException, AfpException {

		// Make options
		opts = this.new Options(args);

		// Make AFP read/write props
		makeAfpReadWriteProperties();
	}

	private void filesClose() throws IOException {
		in.close();
		out.close();
	}

	private void filesOpen() throws IOException {

		// Open input file for reading
		if (opts.getRemainingArg() == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Using STDIN as input");
			}
			in = new BufferedInputStream(System.in);
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Using " + opts.getRemainingArg() + " as input");
			}
			in = new BufferedInputStream(new FileInputStream(opts.getRemainingArg()));
		}

		// Open output file for writing
		OutputStream os = null;
		try {
			if (opts.getOutFile() == null) {
				os = new BufferedOutputStream(System.out);
			} else {
				os = new BufferedOutputStream(new FileOutputStream(opts
						.getOutFile()));
			}
		} catch (IOException e) {
			in.close();
			throw e;
		}
		String cs = opts.getOutFileCs();
		if (cs == null) {
			out = new BufferedWriter(new OutputStreamWriter(
					new BufferedOutputStream(os)));
		} else {
			out = new BufferedWriter(new OutputStreamWriter(
					new BufferedOutputStream(os), cs));
		}
	}

	private void makeAfpReadWriteProperties() {

		// Get default AFP read/write properties
		arwProps = new AfpReadWriteProperties();

		// Convert specific
		if (opts.getConvert_specific() != null) {

			// Clear current settings
			arwProps.setConvertSpecificNone();

			// Add codes
			Map<Integer, Object> convert_specific = opts.getConvert_specific();
			for (Integer code : convert_specific.keySet()) {
				arwProps.setConvertSpecific(code);
			}
		}

		// Message digest threshold
		if (opts.getMd_thold() != null) {
			arwProps.setMessageDigestThreshold(opts.getMd_thold().intValue());
		}
	}

	public void run() throws AfpException, IOException {

		// Usage
		if (opts.isHelp()) {
			System.err.print(usage());
			return;
		}

		// Open files
		filesOpen();

		try {

			// Create AfpFactory
			AfpFactory fact = AfpFactory.createAfpFactory();
			fact.setAfpReadWriteProperties(arwProps);

			// Set input stream
			fact.setInputStream(in);

			if (opts.isGroup_records()) {

				AfpRecordGrouper grouper = new AfpRecordGrouper(fact);

				// Read input until finished
				for (AfpRecord afpRecord = grouper.getAfpRecord(); afpRecord != null; afpRecord = grouper
						.getAfpRecord()) {

					// Write record
					LOG.debug("afpRecord: " + afpRecord);
					afpRecord.write(out, arwProps, 0);

				}

			} else {

				// Read input until finished and write record
				for (AfpRecord rec = fact.createAfpRecord(); rec != null; rec = fact.createAfpRecord()) {
					rec.write(out, arwProps, 0);
				}
			}
		} finally {
			// Close files
			filesClose();
		}
	}

	public static void dumpData(BufferedWriter out,
			AfpReadWriteProperties props, String label, String prefix,
			byte[] data) throws IOException {

		// Loop
		for (int i = 0; i < data.length;) {

			int iStart = i;
			out.write(prefix);
			out.write("    ");
			out.write(label);

			for (int j = i; j < data.length && j < iStart + 16; j++) {
				i++;

				int val = data[j] & 0xff;
				out.write(StringUtils.leftPad(Integer.toString(val, 16)
						.toLowerCase(), 2, '0'));
				out.write(" ");

			}
			out.newLine();
		}
	}

	public static void dumpData(StringBuffer sb, AfpReadWriteProperties props,
			String label, String prefix, byte[] data, boolean doLastNl) {

		// Loop
		for (int i = 0; i < data.length;) {

			int iStart = i;
			sb.append(prefix);
			sb.append("    ");
			sb.append(label);

			for (int j = i; j < data.length && j < iStart + 16; j++) {
				i++;

				int val = data[j] & 0xff;
				sb.append(StringUtils.leftPad(Integer.toString(val, 16)
						.toLowerCase(), 2, '0'));
				sb.append(" ");

			}
			if (doLastNl) {
				sb.append(System.getProperty("line.separator"));
			}
		}
	}

	public static String dumpData(AfpReadWriteProperties props, String label,
			String prefix, byte[] data, boolean doLastNl) {
		StringBuffer sb = new StringBuffer();
		dumpData(sb, props, label, prefix, data, doLastNl);
		return sb.toString();
	}
}
