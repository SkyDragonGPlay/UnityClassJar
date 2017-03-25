package com.unity3d.player;

import java.util.*;
import java.lang.reflect.*;

final class ReflectionHelper
{
    protected static boolean LOG;
    protected static final boolean LOGV = false;
    private static a[] a;
    
    private static boolean a(final a a) {
        final a a2 = ReflectionHelper.a[a.hashCode() & ReflectionHelper.a.length - 1];
        if (!a.equals(a2)) {
            return false;
        }
        a.a = a2.a;
        return true;
    }
    
    private static void a(final a a, final Member a2) {
        a.a = a2;
        ReflectionHelper.a[a.hashCode() & ReflectionHelper.a.length - 1] = a;
    }
    
    protected static Constructor getConstructorID(final Class clazz, final String s) {
        Constructor constructor = null;
        final a a;
        if (a(a = new a(clazz, "", s))) {
            constructor = (Constructor)a.a;
        }
        else {
            final Class[] a2 = a(s);
            float n = 0.0f;
            Constructor[] constructors;
            for (int length = (constructors = clazz.getConstructors()).length, i = 0; i < length; ++i) {
                final Constructor constructor2 = constructors[i];
                final float a3;
                if ((a3 = a(Void.TYPE, constructor2.getParameterTypes(), a2)) > n) {
                    constructor = constructor2;
                    if ((n = a3) == 1.0f) {
                        break;
                    }
                }
            }
            a(a, constructor);
        }
        if (constructor == null) {
            throw new NoSuchMethodError("<init>" + s + " in class " + clazz.getName());
        }
        return constructor;
    }
    
    protected static Method getMethodID(Class superclass, final String s, final String s2, final boolean b) {
        Method method = null;
        final a a;
        if (a(a = new a(superclass, s, s2))) {
            method = (Method)a.a;
        }
        else {
            final Class[] a2 = a(s2);
            float n = 0.0f;
            while (superclass != null) {
                Method[] declaredMethods;
                for (int length = (declaredMethods = superclass.getDeclaredMethods()).length, i = 0; i < length; ++i) {
                    final Method method2 = declaredMethods[i];
                    final float a3;
                    if (b == Modifier.isStatic(method2.getModifiers()) && method2.getName().compareTo(s) == 0 && (a3 = a(method2.getReturnType(), method2.getParameterTypes(), a2)) > n) {
                        method = method2;
                        if ((n = a3) == 1.0f) {
                            break;
                        }
                    }
                }
                if (n == 1.0f || superclass.isPrimitive() || superclass.isInterface() || superclass.equals(Object.class) || superclass.equals(Void.TYPE)) {
                    break;
                }
                superclass = superclass.getSuperclass();
            }
            a(a, method);
        }
        if (method == null) {
            throw new NoSuchMethodError(String.format("no %s method with name='%s' signature='%s' in class L%s;", b ? "non-static" : "static", s, s2, superclass.getName()));
        }
        return method;
    }
    
    protected static Field getFieldID(Class superclass, final String s, final String s2, final boolean b) {
        Field field = null;
        final a a;
        if (a(a = new a(superclass, s, s2))) {
            field = (Field)a.a;
        }
        else {
            final Class[] a2 = a(s2);
            float n = 0.0f;
            while (superclass != null) {
                Field[] declaredFields;
                for (int length = (declaredFields = superclass.getDeclaredFields()).length, i = 0; i < length; ++i) {
                    final Field field2 = declaredFields[i];
                    final float a3;
                    if (b == Modifier.isStatic(field2.getModifiers()) && field2.getName().compareTo(s) == 0 && (a3 = a(field2.getType(), null, a2)) > n) {
                        field = field2;
                        if ((n = a3) == 1.0f) {
                            break;
                        }
                    }
                }
                if (n == 1.0f || superclass.isPrimitive() || superclass.isInterface() || superclass.equals(Object.class) || superclass.equals(Void.TYPE)) {
                    break;
                }
                superclass = superclass.getSuperclass();
            }
            a(a, field);
        }
        if (field == null) {
            throw new NoSuchFieldError(String.format("no %s field with name='%s' signature='%s' in class L%s;", b ? "non-static" : "static", s, s2, superclass.getName()));
        }
        return field;
    }
    
    private static float a(final Class clazz, final Class clazz2) {
        if (clazz.equals(clazz2)) {
            return 1.0f;
        }
        if (!clazz.isPrimitive() && !clazz2.isPrimitive()) {
            try {
                if (clazz.asSubclass(clazz2) != null) {
                    return 0.5f;
                }
            }
            catch (ClassCastException ex) {}
            try {
                if (clazz2.asSubclass(clazz) != null) {
                    return 0.1f;
                }
            }
            catch (ClassCastException ex2) {}
        }
        return 0.0f;
    }
    
    private static float a(final Class clazz, Class[] array, final Class[] array2) {
        if (array2.length == 0) {
            return 0.1f;
        }
        if (((array == null) ? 0 : array.length) + 1 != array2.length) {
            return 0.0f;
        }
        float n = 1.0f;
        int n2 = 0;
        if (array != null) {
            for (int length = (array = array).length, i = 0; i < length; ++i) {
                n *= a(array[i], array2[n2++]);
            }
        }
        return n * a(clazz, array2[array2.length - 1]);
    }
    
    private static Class[] a(final String s) {
        final int[] array = { 0 };
        final ArrayList<Class> list = new ArrayList<Class>();
        Class a;
        while (array[0] < s.length() && (a = a(s, array)) != null) {
            list.add(a);
        }
        int n = 0;
        final Class[] array2 = new Class[list.size()];
        final Iterator<Class> iterator = list.iterator();
        while (iterator.hasNext()) {
            array2[n++] = iterator.next();
        }
        return array2;
    }
    
    private static Class a(String s, final int[] array) {
        while (array[0] < s.length()) {
            final char char1;
            if ((char1 = s.charAt(array[0]++)) != '(' && char1 != ')') {
                if (char1 == 'L') {
                    final int index;
                    if ((index = s.indexOf(59, array[0])) != -1) {
                        s = s.substring(array[0], index);
                        array[0] = index + 1;
                        s = s.replace('/', '.');
                        Class<?> forName;
                        try {
                            forName = Class.forName(s);
                        }
                        catch (ClassNotFoundException ex) {
                            break;
                        }
                        return forName;
                    }
                    break;
                }
                else {
                    if (char1 == 'Z') {
                        return Boolean.TYPE;
                    }
                    if (char1 == 'I') {
                        return Integer.TYPE;
                    }
                    if (char1 == 'F') {
                        return Float.TYPE;
                    }
                    if (char1 == 'V') {
                        return Void.TYPE;
                    }
                    if (char1 == 'B') {
                        return Byte.TYPE;
                    }
                    if (char1 == 'S') {
                        return Short.TYPE;
                    }
                    if (char1 == 'J') {
                        return Long.TYPE;
                    }
                    if (char1 == 'D') {
                        return Double.TYPE;
                    }
                    if (char1 == '[') {
                        return Array.newInstance(a(s, array), 0).getClass();
                    }
                    UnityLog.Log(5, "! parseType; " + char1 + " is not known!");
                    break;
                }
            }
        }
        return null;
    }
    
    private static native Object nativeProxyInvoke(final int p0, final String p1, final Object[] p2);
    
    private static native void nativeProxyFinalize(final int p0);
    
    protected static Object newProxyInstance(final int n, final Class clazz) {
        return newProxyInstance(n, new Class[] { clazz });
    }
    
    protected static Object newProxyInstance(final int n, final Class[] array) {
        return Proxy.newProxyInstance(ReflectionHelper.class.getClassLoader(), array, new InvocationHandler() {
            @Override
            public final Object invoke(final Object o, final Method method, final Object[] array) {
                return nativeProxyInvoke(n, method.getName(), array);
            }
            
            @Override
            protected final void finalize() {
                try {
                    nativeProxyFinalize(n);
                }
                finally {
                    try {
                        super.finalize();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        });
    }
    
    static {
        ReflectionHelper.LOG = false;
        ReflectionHelper.a = new a[4096];
    }
    
    private static final class a
    {
        private final Class b;
        private final String c;
        private final String d;
        private final int e;
        public volatile Member a;
        
        a(final Class b, final String c, final String d) {
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = 31 * (31 * (527 + this.b.hashCode()) + this.c.hashCode()) + this.d.hashCode();
        }
        
        @Override
        public final int hashCode() {
            return this.e;
        }
        
        @Override
        public final boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof a) {
                final a a = (a)o;
                return this.e == a.e && this.d.equals(a.d) && this.c.equals(a.c) && this.b.equals(a.b);
            }
            return false;
        }
    }
}
