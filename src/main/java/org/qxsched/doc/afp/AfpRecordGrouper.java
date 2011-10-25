package org.qxsched.doc.afp;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;

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
 * Class {@link AfpRecordGrouper} recognizes record groups and generates
 * {@link AfpRecordGroup} objects.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpRecordGrouper {

	private static Logger LOG = Logger.getLogger(AfpRecordGrouper.class);

	private AfpStructuredFieldDefinitions afpStructuredFieldDefinitions;
	private AfpFactory factory;
	private Set<Integer> noGroup = new HashSet<Integer>();

	/**
	 * Constructor accepting the AFP record factory.
	 * 
	 * @param factory
	 *            the AFP record factory to use.
	 * @throws AfpException
	 *             if an AFP exception occurs.
	 */
	public AfpRecordGrouper(AfpFactory factory) throws AfpException {
		this(factory, null);
	}

	/**
	 * Constructor accepting the AFP record factory.
	 * 
	 * @param factory
	 *            the AFP record factory to use.
	 * @param noGroup
	 *            set containing the SFIDs to NOT consider for grouping.
	 * @throws AfpException
	 *             if an AFP exception occurs.
	 */
	public AfpRecordGrouper(AfpFactory factory, Set<Integer> noGroup)
			throws AfpException {
		this.factory = factory;
		afpStructuredFieldDefinitions = AfpStructuredFieldDefinitions
				.instance();
		if (noGroup != null) {
			this.noGroup = noGroup;
		}
	}

	public AfpRecord getAfpRecord() throws AfpException {

		// Group record to return
		AfpRecordGroup currRecordGroup = null;

		// Stack of SFIdentifiers to wait for
		Stack<Integer> waitSFIdEnd = new Stack<Integer>();

		while (true) {

			// Read record
			AfpRecord rec = factory.createAfpRecord();

			// Return if no more records
			if (rec == null) {
				if (currRecordGroup == null) {
					return null;
				} else {
					throw new AfpException("No ending found. TODO: Elaborate.");
				}
			}

			if (LOG.isTraceEnabled()) {
				LOG.trace("Stack size: " + waitSFIdEnd.size());
			}

			// Is this a group record begin or end?
			Integer sFId = rec.getSFIdentifier();
			String sFIdAbbrev = rec.getSFIdentifierAbbrev();
			boolean isGroupRecordBegin = noGroup.contains(sFId) ? false
					: afpStructuredFieldDefinitions.isGroupBegin(sFId);
			boolean isGroupRecordEnd = noGroup.contains(sFId) ? false
					: afpStructuredFieldDefinitions.isGroupEnd(sFId);
			if (LOG.isDebugEnabled()) {
				if (isGroupRecordBegin) {
					LOG.debug("Got group BEGIN: " + sFIdAbbrev);
				} else if (isGroupRecordEnd) {
					LOG.debug("Got group END  : " + sFIdAbbrev);
				} else {
					if (LOG.isTraceEnabled()) {
						LOG.trace("Got non-group  : " + sFIdAbbrev);
					}
				}
			}

			// Handle simple records
			if (!isGroupRecordBegin && !isGroupRecordEnd) {
				if (currRecordGroup == null) {
					if (LOG.isTraceEnabled()) {
						LOG.trace("Simple, isolated record  : " + sFIdAbbrev);
					}
					return rec;
				} else {
					if (LOG.isTraceEnabled()) {
						LOG.trace("Add simple record to group: " + sFIdAbbrev);
					}
					currRecordGroup.addMember(rec);
					continue;
				}
			}

			// Handle group record begin
			if (isGroupRecordBegin) {

				// Instantiate currRecordGroup if necessary
				if (LOG.isDebugEnabled()) {
					LOG.debug("New " + AfpRecordGroup.class.getSimpleName()
							+ ": " + sFIdAbbrev);
				}
				AfpRecordGroup newRecordGroup = new AfpRecordGroup(rec,
						currRecordGroup);
				if (currRecordGroup != null) {
					currRecordGroup.addMember(newRecordGroup);
				}
				currRecordGroup = newRecordGroup;

				// Get structured field identifier for end of group
				Integer sFIdEnd = afpStructuredFieldDefinitions.getGroupEnd(rec
						.getSFIdentifier());

				// Push sFIdEnd
				waitSFIdEnd.push(sFIdEnd);
				continue;
			}

			// Handle group record end. Expect stack not to be empty
			if (waitSFIdEnd.size() == 0) {
				throw new AfpException("Group end before begin: '" + sFId + "'");
			}

			// Handle group record end. Expect correct SFIdentifier
			Integer sFIdEndExp = waitSFIdEnd.pop();
			if (!sFIdEndExp.equals(sFId)) {
				throw new AfpException("Expected SFID '" + sFIdEndExp
						+ "' but got '" + sFId + "'");
			}

			// Close group
			currRecordGroup.close(rec);

			// If stack is empty, return
			if (waitSFIdEnd.size() == 0) {
				return currRecordGroup;
			} else {
				currRecordGroup = currRecordGroup.getParent();
			}
		}
	}
}
