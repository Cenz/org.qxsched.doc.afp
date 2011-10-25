package org.qxsched.doc.afp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
 * Class {@link AfpRecordGroup} groups records in an hierarchy.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class AfpRecordGroup implements AfpRecord {

	private AfpRecord afpBeginGroup;
	private AfpRecord afpEndGroup;
	private AfpStructuredFieldDefinitions afpStructuredFieldDefinitions;
	private boolean closed = false;
	private List<AfpRecord> memberList = new ArrayList<AfpRecord>();
	private AfpRecordGroup parent;

	public AfpRecordGroup(AfpRecord afpBeginGroup, AfpRecordGroup parent)
			throws AfpException {

		this.afpBeginGroup = afpBeginGroup;
		this.parent = parent;

		// Get AfpStructuredFieldDefinitions
		afpStructuredFieldDefinitions = AfpStructuredFieldDefinitions
				.instance();

		// Check if record is a begin record
		if (!afpStructuredFieldDefinitions.isGroupBegin(afpBeginGroup
				.getSFIdentifier())) {
			throw new AfpException(
					"Supplied AFP record is not a group-begin record.");
		}
	}

	public void addMember(AfpRecord rec) {
		memberList.add(rec);
	}

	public List<AfpRecord> getMemberList() {
		return memberList;
	}

	public void close(AfpRecord afpEndGroup) throws AfpException {

		// Throw exception if already closed
		if (closed) {
			throw new AfpException(
					"Cannot close and already closed record group: "
							+ getSFIdentifierAbbrev());
		}

		// Check if begin-group and end-group match
		Integer groupEndExpect = afpStructuredFieldDefinitions
				.getGroupEnd(afpBeginGroup.getSFIdentifier());
		if (afpEndGroup.getSFIdentifier() != groupEndExpect) {
			String groupEndExpectAbbrev = afpStructuredFieldDefinitions
					.getAbbreviation(groupEndExpect);
			throw new AfpException(
					"Supplied AFP record is not correct group-end record. Expected "
							+ groupEndExpectAbbrev + " but got "
							+ afpEndGroup.getSFIdentifierAbbrev());
		}

		// OK, remember record and set closed flag
		this.afpEndGroup = afpEndGroup;
		closed = true;

	}

	public byte[] getData() {
		return null;
	}

	public int getFlags() {
		return 0;
	}

	public int getLength() {
		return 0;
	}

	public AfpRecordGroup getParent() {
		return parent;
	}

	public int getReserved() {
		return 0;
	}

	public int getSFIdentifier() {
		return afpBeginGroup.getSFIdentifier();
	}

	public String getSFIdentifierAbbrev() {
		return afpBeginGroup.getSFIdentifierAbbrev();
	}

	public String getSFIdentifierString() {
		return afpBeginGroup.getSFIdentifierString();
	}

	public void setData(byte[] data) throws AfpException {
		throw new AfpException("Method not supported in class "
				+ AfpRecordGroup.class.getName());
	}

	public void setFlags(int flags) throws AfpException {
		throw new AfpException("Method not supported in class "
				+ AfpRecordGroup.class.getName());
	}

	public void setReserved(int reserved) throws AfpException {
		throw new AfpException("Method not supported in class "
				+ AfpRecordGroup.class.getName());
	}

	public void setSFIdentifier(int identifier) throws AfpException {
		throw new AfpException("Method not supported in class "
				+ AfpRecordGroup.class.getName());
	}

	public void write(BufferedWriter out, AfpReadWriteProperties props,
			int level) throws IOException, AfpException {

		// Throw exception if not closed
		if (!closed) {
			throw new AfpException("Cannot write an open group: "
					+ getSFIdentifierAbbrev());
		}

		// Write begin group record
		afpBeginGroup.write(out, props, level);

		// Write members
		for (AfpRecord member : memberList) {
			member.write(out, props, level + 1);
		}

		// Write end group record
		afpEndGroup.write(out, props, level);
	}

	public void write(OutputStream out, AfpReadWriteProperties props)
			throws IOException, AfpException {

		// Throw exception if not closed
		if (!closed) {
			throw new AfpException("Cannot write an open group: "
					+ getSFIdentifierAbbrev());
		}

		// Write begin group record
		afpBeginGroup.write(out, props);

		// Write members
		for (AfpRecord member : memberList) {
			member.write(out, props);
		}

		// Write end group record
		afpEndGroup.write(out, props);

	}

	public void writeData(BufferedWriter out, AfpReadWriteProperties props,
			String prefix) throws IOException, AfpException {
	}

}
