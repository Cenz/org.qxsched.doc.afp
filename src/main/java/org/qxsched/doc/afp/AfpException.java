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
 * Exception {@link AfpException} is thrown when an AFP specific -e.g. scan or
 * parse error- exception occurs.
 * 
 * @author Vincenzo Zocca
 */
public class AfpException extends Exception {

	private static final long serialVersionUID = 1L;

	public AfpException() {
		super();
	}

	public AfpException(String message, Throwable cause) {
		super(message, cause);
	}

	public AfpException(String message) {
		super(message);
	}

	public AfpException(Throwable cause) {
		super(cause);
	}
}
