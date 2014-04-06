package com.hileco.futures;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public abstract class LazyerInterceptor implements InvocationHandler {

	private final boolean recursing;
	private final Lazyer delayer;
	private final Object instance;

	public LazyerInterceptor(Object instance, Lazyer delayer, boolean recursing) {
		this.delayer = delayer;
		this.instance = instance;
		this.recursing = recursing;
	}

	abstract public boolean isTransparizableMethod(Method method);

	abstract public Object createTransparizedResult(Method method, Callable<Object> callable);

	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		if (method.getReturnType().equals(Void.TYPE)) {
			delayer.submit(new Callable<Object>() {
				public Object call() throws Exception {
					return method.invoke(instance, args);
				}
			});
			return null;
		}
		if (this.isTransparizableMethod(method)) {
			Object transparentResult = this.createTransparizedResult(method, new Callable<Object>() {
				public Object call() throws Exception {
					return method.invoke(instance, args);
				}
			});
			if (recursing) {
				transparentResult = delayer.makeLazy(transparentResult, recursing);
			}
			return transparentResult;
		} else {
			return method.invoke(instance, args);
		}
	}

	public boolean isRecursing() {
		return this.recursing;
	}

	public Lazyer getDelayer() {
		return this.delayer;
	}

	public Object getInstance() {
		return this.instance;
	}

}
