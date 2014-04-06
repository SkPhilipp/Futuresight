package com.hileco.futures.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.hileco.futures.Lazyer;
import com.hileco.futures.LazyerInterceptor;

public class ProxyLazyDelayer implements Lazyer {

	private static final ClassLoader cl = ProxyLazyDelayer.class.getClassLoader();
	private final ExecutorService executor = Executors.newCachedThreadPool();

	@SuppressWarnings("unchecked")
	public <T> T makeLazy(T instance, boolean recursing) {
		return (T) Proxy.newProxyInstance(cl, instance.getClass().getInterfaces(), new LazyAsyncInterceptor(instance, this, recursing));
	}

	public <T> Future<T> submit(Callable<T> task) {
		return executor.submit(task);
	}

	public static class LazyAsyncInterceptor extends LazyerInterceptor {

		private final Collection<Class<?>> interfaces;

		public LazyAsyncInterceptor(Object instance, Lazyer async, boolean recursing) {
			super(instance, async, recursing);
			this.interfaces = Arrays.asList(instance.getClass().getInterfaces());
		}

		@Override
		public boolean isTransparizableMethod(Method method) {
			return interfaces.contains(method.getDeclaringClass()) && method.getReturnType().isInterface();
		}

		@Override
		public Object createTransparizedResult(Method method, final Callable<Object> callable) {
			Object result = Proxy.newProxyInstance(ProxyAsyncDelayer.class.getClassLoader(), new Class<?>[] { method.getReturnType() }, new InvocationHandler() {
				private Object instance = null;

				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
					// TODO: We need some sort of locking / waiting mechanic here so we can be sure the callable will only be called at most once.
					if (instance == null) {
						instance = callable.call();
					}
					return method.invoke(instance, args);
				}
			});
			if (this.isRecursing()) {
				result = this.getDelayer().makeLazy(result, true);
			}
			return result;
		}

	}

}
