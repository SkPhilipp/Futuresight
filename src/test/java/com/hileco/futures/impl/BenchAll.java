package com.hileco.futures.impl;

import org.junit.Test;

import com.hileco.futures.Lazyer;

public class BenchAll {

	@Test
	public void test() throws Throwable {

		Lazyer[] asyncs = new Lazyer[] { new CGLibAsyncDelayer(), new CGLibLazyDelayer(), new ProxyAsyncDelayer(), new ProxyLazyDelayer() };
		for (Lazyer async : asyncs) {
			new Bench(async).bench(false, false, false);
			new Bench(async).bench(false, true, false);
			new Bench(async).bench(true, false, false);
			new Bench(async).bench(true, true, false);
			new Bench(async).bench(false, false, true);
			new Bench(async).bench(false, true, true);
			new Bench(async).bench(true, false, true);
			new Bench(async).bench(true, true, true);
		}

	}

}
