package com.hileco.futures.impl;

import org.junit.Assert;

import com.hileco.futures.Lazyer;
import com.hileco.futures.impl.test.IToughCalculations;
import com.hileco.futures.impl.test.IToughCalculationsResult;
import com.hileco.futures.impl.test.ToughCalculationsImpl;

/**
 * Allows you to test your {@link Lazyer} implementation's mettle.
 * 
 * Your async implementation has swag when it perform a fast "new Bench(async).bench(true, true, true)";
 * 
 * @author Philipp Gayret
 *
 */
public class Bench {

	private static final int CALCULATIONS = 10;

	private final Lazyer async;

	public Bench(Lazyer async) {
		this.async = async;
	}

	public void benchInner(boolean recursing, boolean verifying, boolean asClass) throws Throwable {
		IToughCalculations calc = async.makeLazy(new ToughCalculationsImpl(), recursing);
		if (asClass) {
			ToughCalculationsImpl.class.cast(calc);
		}
		IToughCalculationsResult[] array = new IToughCalculationsResult[CALCULATIONS];
		for (int i = 0; i < array.length; i++) {
			IToughCalculationsResult result = calc.expensiveComputation(i, i + 1);
			if (recursing) {
				result.setX(i);
			}
			array[i] = result;
		}
		if (verifying) {
			for (int i = 0; i < array.length; i++) {
				Assert.assertEquals(i + i + 1, (int) array[i].result());
			}
		}

	}

	static {
		System.out.println("CLASS;TIME;RECURSING;VERIFYING;ASCLASS;");
	}

	public void bench(boolean recursing, boolean verifying, boolean asClass) {
		System.out.println(async.getClass().getSimpleName() + ";STARTING;" + recursing + ";" + verifying + ";" + asClass + ";");
		try {
			long now = System.currentTimeMillis();
			this.benchInner(recursing, verifying, asClass);
			long total = System.currentTimeMillis() - now;
			System.out.println(async.getClass().getSimpleName() + ";" + total + ";" + recursing + ";" + verifying + ";" + asClass + ";");
		} catch (Throwable t) {
			System.out.println(async.getClass().getSimpleName() + ";ERRED;" + recursing + ";" + verifying + ";" + asClass + ";");
		}
	}

}
