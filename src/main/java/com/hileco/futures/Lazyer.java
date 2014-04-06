package com.hileco.futures;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface Lazyer {

	public <T> T makeLazy(T instance, boolean recursive);

	<T> Future<T> submit(Callable<T> task);

}