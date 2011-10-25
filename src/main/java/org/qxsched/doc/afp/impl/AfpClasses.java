package org.qxsched.doc.afp.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.qxsched.doc.afp.AfpException;
import org.qxsched.doc.afp.AfpFactory;
import org.qxsched.doc.afp.AfpRecord;
import org.qxsched.doc.afp.AfpStructuredFieldDefinitions;
import org.qxsched.doc.afp.AfpTriplet;

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
 * Class {@link AfpClasses} contains all {@link AfpRecord} classes that
 * implement a specific AFP identifier.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpClasses {

	private static AfpClasses instance;

	private static Logger LOG = Logger.getLogger(AfpClasses.class);

	public synchronized static AfpClasses instance() throws AfpException {

		// Return if already instantiated
		if (instance != null) {
			return instance;
		}

		// Instantiate and return
		instance = new AfpClasses();
		return instance;
	}

	private Map<String, Class<AfpRecord>> recordAbbrev2class = new HashMap<String, Class<AfpRecord>>();
	private Map<Integer, Class<AfpRecord>> recordCode2class = new HashMap<Integer, Class<AfpRecord>>();

	private Map<Integer, Class<AfpTriplet>> tripletCode2class = new HashMap<Integer, Class<AfpTriplet>>();

	private AfpClasses() throws AfpException {
		init();
	}

	public Class<AfpRecord> getSpecificAfpRecordClass(AfpRecord record) {
		return recordCode2class.get(record.getSFIdentifier());
	}

	public Class<AfpRecord> getSpecificAfpRecordClass(Integer code) {
		return recordCode2class.get(code);
	}

	public Class<AfpTriplet> getSpecificAfpTripletClass(AfpTriplet triplet) {
		return tripletCode2class.get(triplet.getTid());
	}

	public Class<AfpTriplet> getSpecificAfpTripletClass(Integer code) {
		return tripletCode2class.get(code);
	}

	public Class<AfpRecord> getSpecificClass(String abbrev) {
		return recordAbbrev2class.get(abbrev);
	}

	private void init() throws AfpException {

		// Get definitions
		AfpStructuredFieldDefinitions defs = AfpStructuredFieldDefinitions
				.instance();

		// Get class loader
		ClassLoader ldr = Thread.currentThread().getContextClassLoader();

		// Get package directory
		Package pkg = AfpClasses.class.getPackage();
		String recClassBaseName = AfpRecord.class.getName().replaceFirst(
				"^.*\\.", "");
		String recClassBase = pkg.getName() + "." + recClassBaseName;

		// Iterate through all abbreviations and try and find classes
		Set<String> abbrevs = defs.getAbbrev();
		for (String abbrev : abbrevs) {

			// Make class name
			String recordClassName = recClassBase + abbrev;
			if (LOG.isTraceEnabled()) {
				LOG.trace("AFP record class name: " + recordClassName);
			}

			// Load class
			Class<?> recordClass = null;
			try {
				recordClass = ldr.loadClass(recordClassName);
			} catch (ClassNotFoundException e) {
			}

			// Ignore null
			if (recordClass == null) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Ignoring unfound class: " + recordClassName);
				}
				continue;
			}

			// Ignore non AfpRecords
			if (!AfpRecord.class.isAssignableFrom(recordClass)) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Ignoring non AfpRecord: " + recordClassName);
				}
				continue;
			}

			// Map
			Integer code = defs.getCode(abbrev);
			recordCode2class.put(code, (Class<AfpRecord>) recordClass);
			recordAbbrev2class.put(abbrev, (Class<AfpRecord>) recordClass);
		}

		// Try finding al triplet classes this package implements
		String tripClassBaseName = AfpTriplet.class.getName().replaceFirst(
				"^.*\\.", "");
		String tripClassBase = pkg.getName() + "." + tripClassBaseName;
		for (int i = 0; i < 0x100; i++) {

			// Make class name
			String abbrev = StringUtils.leftPad(Integer.toString(i, 16)
					.toUpperCase(), 2, '0');
			String tripClassName = tripClassBase + abbrev;
			if (LOG.isTraceEnabled()) {
				LOG.trace("AFP triplet class name: " + tripClassName);
			}

			// Load class
			Class<?> tripClass = null;
			try {
				tripClass = ldr.loadClass(tripClassName);
			} catch (ClassNotFoundException e) {
			}

			// Ignore null
			if (tripClass == null) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Ignoring unfound class: " + tripClassName);
				}
				continue;
			}

			// Ignore non AfpTriplet
			if (!AfpTriplet.class.isAssignableFrom(tripClass)) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Ignoring non AfpTriplet: " + tripClassName);
				}
				continue;
			}

			// Map
			if (LOG.isTraceEnabled()) {
				LOG.trace("Found AfpTriplet: " + tripClassName);
			}
			tripletCode2class.put(i, (Class<AfpTriplet>) tripClass);
		}
	}

	public AfpRecord instantiateSpecific(AfpRecord record, AfpFactory fact)
			throws AfpException {

		// Get record class
		Class<AfpRecord> cls = getSpecificAfpRecordClass(record);

		// Do nothing if no class
		if (cls == null) {
			LOG.warn("No specific class found for AFP identifier '"
					+ record.getSFIdentifierAbbrev() + "'/"
					+ record.getSFIdentifierString());
			return record;
		}

		// Get constructor
		Constructor<AfpRecord> constr;
		try {
			Class[] prams = { AfpRecord.class, AfpFactory.class };
			constr = cls.getConstructor(prams);
		} catch (SecurityException e) {
			throw new AfpException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOG.error("No constructor found accepting AfpRecord in class "
					+ cls.getName(), e);
			return record;
		}

		// Instantiate
		try {
			Object[] prams = { record, fact };
			AfpRecord inst = constr.newInstance(prams);
			return inst;
		} catch (IllegalArgumentException e) {
			LOG.error("Unexpected exception: " + e.getMessage(), e);
			return record;
		} catch (InstantiationException e) {
			LOG.error("Unexpected exception: " + e.getMessage(), e);
			return record;
		} catch (IllegalAccessException e) {
			LOG.error("Unexpected exception: " + e.getMessage(), e);
			return record;
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof AfpException) {
				throw (AfpException) cause;
			}
			LOG.error("Unexpected exception: " + e.getMessage(), e);
			return record;
		}
	}

	public AfpTriplet instantiateSpecific(AfpTriplet triplet, AfpFactory fact)
			throws AfpException {

		// Get triplet class
		Class<AfpTriplet> cls = getSpecificAfpTripletClass(triplet);

		// Do nothing if no class
		if (cls == null) {
			LOG.warn("No specific class found for AFP triplet 0x"
					+ Integer.toString(triplet.getTid(), 16));
			return triplet;
		}

		// Get constructor
		Constructor<AfpTriplet> constr;
		try {
			constr = cls.getConstructor(AfpTriplet.class);
		} catch (SecurityException e) {
			throw new AfpException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOG.error("No constructor found accepting AfpTriplet in class "
					+ cls.getName());
			return triplet;
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("Constructor found accepting AfpTriplet in class "
					+ cls.getName());
		}

		// Instantiate
		try {
			Object[] prams = { triplet };
			AfpTriplet inst = constr.newInstance(prams);
			return inst;
		} catch (IllegalArgumentException e) {
			LOG.error("Unexpected exception: " + e.getMessage(), e);
			return triplet;
		} catch (InstantiationException e) {
			LOG.error("Unexpected exception: " + e.getMessage(), e);
			return triplet;
		} catch (IllegalAccessException e) {
			LOG.error("Unexpected exception: " + e.getMessage(), e);
			return triplet;
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof AfpException) {
				throw (AfpException) cause;
			}
			LOG.error("Unexpected exception: " + e.getMessage(), e);
			return triplet;
		}
	}
}
