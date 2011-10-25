package org.qxsched.doc.afp;

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
 * Class {@link ByteDiffer} is a test utility.
 * 
 * @author Vincenzo Zocca
 */
public class ByteDiffer {

	public static String diff(byte[] a, byte[] b) {

		StringBuffer err = new StringBuffer();

		if (a.length != b.length) {
			err.append("Array sizes differ: ");
			err.append(a.length);
			err.append(" vs.");
			err.append(b.length);
			err.append(". ");
		}

		for (int i = 0; i < a.length && i < b.length; i++) {
			err.append("Bytes differ starting from index: ");
			err.append(i);
			err.append(". ");
		}

		if (err.length() == 0) {
			return null;
		} else {
			return err.toString();
		}
	}
}
