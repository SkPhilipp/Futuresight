package com.hileco.futures.impl;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.hileco.futures.Lazyer;
import com.hileco.futures.LazyerInterceptor;

@SuppressWarnings("unchecked")
public class CGLibAsyncDelayer implements Lazyer {

	private final ExecutorService executor = Executors.newCachedThreadPool();

	public <T> T makeLazy(T instance, boolean recursing) {
		// We cannot (yet) handle recursing.
		if (recursing) {
			throw new UnsupportedOperationException();
		}
		return (T) Enhancer.create(instance.getClass(), new CGLibAsyncInterceptor(instance, this, recursing));
	}

	public <T> Future<T> submit(Callable<T> task) {
		return executor.submit(task);
	}

	public static class CGLibAsyncInterceptor extends LazyerInterceptor implements MethodInterceptor {

		public CGLibAsyncInterceptor(Object instance, Lazyer async, boolean recursing) {
			super(instance, async, recursing);
		}

		@Override
		public boolean isTransparizableMethod(Method method) {
			return true;
		}

		@Override
		public Object createTransparizedResult(Method method, Callable<Object> callable) {
			final Future<?> future = this.getDelayer().submit(callable);
			Object result = Enhancer.create(method.getReturnType(), new MethodInterceptor() {
				public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
					return method.invoke(future.get(), args);
				}
			});
			if (this.isRecursing()) {
				result = this.getDelayer().makeLazy(result, true);
			}
			return result;
		}

		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			return this.invoke(proxy, method, args);
		}

	}

}
