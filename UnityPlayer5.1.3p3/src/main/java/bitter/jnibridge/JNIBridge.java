package bitter.jnibridge;

import java.lang.reflect.*;

public class JNIBridge
{
    static Object newInterfaceProxy(final long n, final Class clazz) {
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new a(n));
    }
    
    static void disableInterfaceProxy(final Object o) {
        ((a)Proxy.getInvocationHandler(o)).a();
    }
    
    static native Object invoke(final long p0, final Method p1, final Object[] p2);
    
    private static final class a implements InvocationHandler
    {
        private Object a;
        private long b;
        
        public a(final long b) {
            this.a = new Object[0];
            this.b = b;
        }
        
        @Override
        public final Object invoke(final Object o, final Method method, final Object[] array) {
            synchronized (this.a) {
                if (this.b == 0L) {
                    return null;
                }
                return JNIBridge.invoke(this.b, method, array);
            }
        }
        
        public final void a() {
            synchronized (this.a) {
                this.b = 0L;
            }
        }
    }
}
