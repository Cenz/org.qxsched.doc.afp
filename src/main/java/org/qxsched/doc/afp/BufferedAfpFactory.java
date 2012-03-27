package org.qxsched.doc.afp;

import java.util.ArrayList;
import java.util.List;

/**
 * Class {@link BufferedAfpFactory} provides buffer/unget function to
 * {@link AfpFactory}.
 * 
 * @author Vincenzo Zocca
 * 
 */
public class BufferedAfpFactory {

	private AfpFactory fact;

	private List<AfpRecord> getHistoryStack = new ArrayList<AfpRecord>();

	private List<AfpRecord> ungetStack = new ArrayList<AfpRecord>();

	private int ungetMaxSize;

	/**
	 * Constructor accepting a {@link AfpFactory} and the unget buffer size.
	 * 
	 * @param fact
	 *            the AFP factory to use to create AFP records.
	 * @param ungetMaxSize
	 *            the maximum size of the unget stack. The maximum times method
	 *            {@link #unget()} can be called in a row.
	 */
	public BufferedAfpFactory(AfpFactory fact, int ungetMaxSize) {
		this.fact = fact;
		this.ungetMaxSize = ungetMaxSize;
	}

	/**
	 * Returns AFP records. Objects are either 1) extracted from the ungotten
	 * object stack or 2) read created by the AFP factory.
	 * 
	 * @return an AFP record or {@code null} if none available.
	 * @throws AfpException
	 *             should one occur.
	 */
	public AfpRecord createAfpRecord() throws AfpException {
		AfpRecord ret = null;

		if (ungetStack.size() > 0) {
			ret = ungetStack.get(0);
			ungetStack.remove(0);
		} else {
			ret = fact.createAfpRecord();
		}

		if (ret == null) {
			ungetStack.clear();
			getHistoryStack.clear();
		} else {
			getHistoryStack.add(0, ret);
		}

		while (getHistoryStack.size() > ungetMaxSize) {
			getHistoryStack.remove(getHistoryStack.size() - 1);
		}

		return ret;
	}

	/**
	 * Returns the get-history stack size.
	 * 
	 * @return the get-history stack size.
	 */
	public int getHistoryStackSize() {
		return getHistoryStack.size();
	}

	/**
	 * Moves one object from the get-history stack to the unget-stack.
	 * 
	 * @throws IllegalStateException
	 *             should the get-history stack be empty when this method is
	 *             called.
	 */
	public void unget() {
		if (getHistoryStack.size() < 1) {
			throw new IllegalStateException("Empty unget buffer.");
		}
		AfpRecord rec = getHistoryStack.get(0);
		getHistoryStack.remove(0);
		ungetStack.add(0, rec);
	}

	/**
	 * Returns the unget-stack size.
	 * 
	 * @return the unget-stack size.
	 */
	public int ungetStackSize() {
		return ungetStack.size();
	}
}
