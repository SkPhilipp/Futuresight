## Futuresight

[![Build Status](https://travis-ci.org/SkPhilipp/Futuresight.svg?branch=master)](https://travis-ci.org/SkPhilipp/Futuresight)

_This project is no longer maintained and part of my source code "attic". Feel free to use it though, works fine._

Futuresight allows you to wrap an object, which returns a proxy to that object on which every call made to the returned object will be wrapped in a proxied future; Wrap an object with Futuresight, and any calls to the wrapped object will return a result immediately and run the actual call to the original object in a separate thread. ( Unless the result type is a primitive or boxed, or some special class; i.e. `int`, `Integer`, probably `String` too, etc. ).

The wrapping as proxy can also be applied recursively.

Futuresight must assume wrappable objects never throw exceptions, and never return null. Because of this you should probably very rarely, mostly never, use Futuresight.

For those still reading; just run the test.

Testcase Explained
------------------

Here's what the test case does; Theres a class called `ToughCalculationsImpl` which represents anything that takes a while to do synchronously and isn't CPU intensive ( it just sleeps for 100 milliseconds ), and then returns a result.

```java
public class ToughCalculationsImpl implements IToughCalculations {

	public IToughCalculationsResult expensiveComputation(final int a, final int b) throws Throwable {
		Thread.sleep(100);
		return new ToughCalculationsResultImpl(a, b);
	}

}
```

Then there's the result class, which doesn't do anything special, it returns the result of adding two numbers when you call `result`.

```java
// some stuff ommitted for easy explaining :-)
public class ToughCalculationsResultImpl implements IToughCalculationsResult {

	private final int b;
	private final int a;

	public ToughCalculationsResultImpl(int b, int a) {
		this.b = b;
		this.a = a;
	}

	public Integer result() throws Throwable {
		return a + b;
	}

}
```

So basically we have a class that takes an extremely long time do do practically nothing, and it blocks the thread by sleeping.

Now with FutureSight magic, we can make it run asynchronously! The test case below performs a 100 calls to `expensiveComputation`.

```java
// pick from CGLibAsyncDelayer, CGLibLazyDelayer, ProxyAsyncDelayer, ProxyLazyDelayer, they all work different
IToughCalculations calc = new CGLibAsyncDelayer().makeLazy(new ToughCalculationsImpl(), recursing);
IToughCalculationsResult[] array = new IToughCalculationsResult[100];
for (int i = 0; i < array.length; i++) {
    // this should take 100ms per call
    IToughCalculationsResult result = calc.expensiveComputation(i, i + 1);
    // actually it doesn't because we used futuresight on it, it has returned instantly
    array[i] = result;
}
for (int i = 0; i < array.length; i++) {
    // here is when futuresight will block to make sure it has an actual result, as it can no longer fake it
    Assert.assertEquals(i + i + 1, (int) array[i].result());
}
```

It takes about 120ms. Of which 100ms is to Thread.sleep and 20ms to perform magic.
