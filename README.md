## Futuresight

Futuresight allows you to wrap an object, which returns a proxy to that object on which every call made to the returned object will be wrapped in a proxied future; Wrap an object with Futuresight, and any calls to the wrapped object will return a result immediately and run the actual call to the original object in a separate thread. ( Unless the result type is a primitive or boxed, or some special class; i.e. `int`, `Integer`, probably `String` too, etc. ).

The wrapping as proxy can also be applied recursively.

Futuresight must assume wrappable objects never throw exceptions, and never return null. Because of this you should probably very rarely, mostly never, use Futuresight.

For those still reading; just run the test.