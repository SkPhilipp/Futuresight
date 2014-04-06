package com.hileco.futures.impl.test;

/**
 * 
 * @author Philipp Gayret
 *
 */
public class ToughCalculationsImpl implements IToughCalculations {

	public IToughCalculationsResult expensiveComputation(final int a, final int b) throws Throwable {
		Thread.sleep(100);
		return new ToughCalculationsResultImpl(a, b);
	}

}