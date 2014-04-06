package com.hileco.futures.impl.test;

/**
 * 
 * @author Philipp Gayret
 *
 */
public class ToughCalculationsResultImpl implements IToughCalculationsResult {

	private final int b;
	private final int a;
	private int x;

	public ToughCalculationsResultImpl(int b, int a) {
		this.b = b;
		this.a = a;
	}

	public Integer result() throws Throwable {
		return a + b;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

}