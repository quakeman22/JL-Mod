package javax.microedition.shell;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import javax.microedition.midlet.MIDlet;

/* JADX INFO: loaded from: classes3.dex */
public class MemoryScanner {
    public static final int TYPE_CHANGED = 4;
    public static final int TYPE_DECREASED = 3;
    public static final int TYPE_EXACT = 0;
    public static final int TYPE_INCREASED = 2;
    public static final int TYPE_UNCHANGED = 5;
    public static final int TYPE_UNKNOWN = 1;
    private static ClassLoader appClassLoader;
    private static long scanId = 0;

    public static class Result {
        public Class<?> arrayComponentType;
        public Object container;
        public String displayPath;
        public Field field;
        public int index = -1;
        public long lastValue;
        public long scanId;

        public long readValue() {
            if (this.arrayComponentType != null) {
                return MemoryScanner.readArrayElement(this.container, this.index, this.arrayComponentType);
            }
            try {
                this.field.setAccessible(true);
                Class<?> type = this.field.getType();
                return type == Integer.TYPE ? this.field.getInt(this.container) : type == Short.TYPE ? this.field.getShort(this.container) : type == Byte.TYPE ? this.field.getByte(this.container) : type == Character.TYPE ? this.field.getChar(this.container) : type == Long.TYPE ? this.field.getLong(this.container) : type == Float.TYPE ? (long) this.field.getFloat(this.container) : type == Double.TYPE ? (long) this.field.getDouble(this.container) : (type == Boolean.TYPE && this.field.getBoolean(this.container)) ? 1L : 0L;
            } catch (Exception e) {
            }
            return 0L;
        }

        public void writeValue(long value) {
            if (this.arrayComponentType != null) {
                MemoryScanner.writeArrayElement(this.container, this.index, this.arrayComponentType, value);
                return;
            }
            try {
                boolean z = true;
                this.field.setAccessible(true);
                Class<?> type = this.field.getType();
                if (type == Integer.TYPE) {
                    this.field.setInt(this.container, (int) value);
                } else if (type == Short.TYPE) {
                    this.field.setShort(this.container, (short) value);
                } else if (type == Byte.TYPE) {
                    this.field.setByte(this.container, (byte) value);
                } else if (type == Character.TYPE) {
                    this.field.setChar(this.container, (char) value);
                } else if (type == Long.TYPE) {
                    this.field.setLong(this.container, value);
                } else if (type == Float.TYPE) {
                    this.field.setFloat(this.container, value);
                } else if (type == Double.TYPE) {
                    this.field.setDouble(this.container, value);
                } else if (type == Boolean.TYPE) {
                    Field field = this.field;
                    Object obj = this.container;
                    if (value == 0) {
                        z = false;
                    }
                    field.setBoolean(obj, z);
                }
            } catch (Exception e) {
            }
        }

        public String toString() {
            if (this.arrayComponentType != null) {
                return this.displayPath + " [" + this.arrayComponentType.getSimpleName() + "] = " + readValue();
            }
            String typeName = this.field.getType().getSimpleName();
            return this.displayPath + " [" + typeName + "] = " + readValue();
        }
    }

    public static List<Result> search(MIDlet midlet, long value, int type) throws Throwable {
        scanId++;
        if (midlet == null) {
            return new ArrayList();
        }
        appClassLoader = midlet.getClass().getClassLoader();
        List<Result> results = new ArrayList<>();
        IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<>();
        if (type == 1) {
            scanObject(midlet, "midlet", results, visited, null);
        } else {
            scanObject(midlet, "midlet", results, visited, Long.valueOf(value));
        }
        return results;
    }

    public static List<Result> refine(List<Result> previous, int refineType, long newValue) {
        scanId++;
        List<Result> results = new ArrayList<>();
        for (Result r : previous) {
            long cur = r.readValue();
            long prev = r.lastValue;
            boolean match = true;
            switch (refineType) {
                case 0:
                    if (cur != newValue) {
                        match = false;
                    }
                    break;
                case 1:
                default:
                    match = false;
                    break;
                case 2:
                    if (cur <= prev) {
                        match = false;
                    }
                    break;
                case 3:
                    if (cur >= prev) {
                        match = false;
                    }
                    break;
                case 4:
                    if (cur == prev) {
                        match = false;
                    }
                    break;
                case 5:
                    if (cur != prev) {
                        match = false;
                    }
                    break;
            }
            if (match) {
                r.scanId = scanId;
                results.add(r);
            }
        }
        return results;
    }

    public static void updateValues(List<Result> results) {
        for (Result r : results) {
            r.lastValue = r.readValue();
        }
    }

    private static boolean isAppObject(Object obj) {
        ClassLoader loader;
        return (obj == null || (loader = obj.getClass().getClassLoader()) == null || loader != appClassLoader) ? false : true;
    }

    private static boolean isPrimitiveType(Class<?> type) {
        return type == Integer.TYPE || type == Short.TYPE || type == Byte.TYPE || type == Character.TYPE || type == Long.TYPE || type == Float.TYPE || type == Double.TYPE || type == Boolean.TYPE;
    }

    private static boolean matchValue(Field field, Object container, Long target) {
        long val;
        try {
            Class<?> type = field.getType();
            if (type == Integer.TYPE) {
                val = field.getInt(container);
            } else if (type == Short.TYPE) {
                val = field.getShort(container);
            } else if (type == Byte.TYPE) {
                val = field.getByte(container);
            } else if (type == Character.TYPE) {
                val = field.getChar(container);
            } else if (type == Long.TYPE) {
                val = field.getLong(container);
            } else if (type == Float.TYPE) {
                val = (long) field.getFloat(container);
            } else if (type == Double.TYPE) {
                val = (long) field.getDouble(container);
            } else {
                if (type != Boolean.TYPE) {
                    return false;
                }
                val = field.getBoolean(container) ? 1L : 0L;
            }
            if (target != null) {
                if (val != target.longValue()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void scanObject(Object obj, String path, List<Result> results, IdentityHashMap<Object, Boolean> visited, Long target) throws Throwable {
        Class<?> cls;
        Class<?> cls2;
        Field[] fieldArr;
        Throwable th;
        if (obj == null || visited.containsKey(obj) || (obj instanceof String) || (obj instanceof Number) || (obj instanceof Boolean) || (obj instanceof Character)) {
            return;
        }
        if (obj.getClass().isArray()) {
            int len = Array.getLength(obj);
            Class<?> compType = obj.getClass().getComponentType();
            if (compType == null || !compType.isPrimitive()) {
                for (int i = 0; i < len; i++) {
                    Object elem = Array.get(obj, i);
                    if (elem != null && !elem.getClass().isPrimitive()) {
                        scanObject(elem, path + "[" + i + "]", results, visited, target);
                    }
                }
                return;
            }
            for (int i2 = 0; i2 < len; i2++) {
                long val = readArrayElement(obj, i2, compType);
                if (target == null || val == target.longValue()) {
                    Result r = new Result();
                    r.container = obj;
                    r.index = i2;
                    r.arrayComponentType = compType;
                    r.displayPath = path + "[" + i2 + "]";
                    r.lastValue = val;
                    r.scanId = scanId;
                    results.add(r);
                }
            }
            return;
        }
        visited.put(obj, Boolean.TRUE);
        Class<?> cls3 = obj.getClass();
        while (cls3 != null && cls3 != Object.class) {
            boolean z = true;
            if (isAppObject(obj)) {
                Field[] declaredFields = cls3.getDeclaredFields();
                int length = declaredFields.length;
                int i3 = 0;
                while (i3 < length) {
                    Field field = declaredFields[i3];
                    int mod = field.getModifiers();
                    if (!Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        cls2 = cls3;
                        fieldArr = declaredFields;
                        if (!Modifier.isStatic(mod) && !Modifier.isFinal(mod)) {
                            try {
                                field.setAccessible(true);
                                if (isPrimitiveType(field.getType())) {
                                    boolean matches = matchValue(field, obj, target);
                                    if (matches) {
                                        long val2 = readFieldValue(field, obj);
                                        Result r2 = new Result();
                                        r2.container = obj;
                                        r2.field = field;
                                        r2.displayPath = path + "." + field.getName();
                                        r2.lastValue = val2;
                                        long val3 = scanId;
                                        r2.scanId = val3;
                                        results.add(r2);
                                    }
                                } else {
                                    Object child = field.get(obj);
                                    if (child != null && isAppObject(child)) {
                                        scanObject(child, path + "." + field.getName(), results, visited, target);
                                    }
                                }
                            } catch (Exception e) {
                            } catch (Throwable th2) {
                                try {
                                    field.setAccessible(false);
                                    throw th2;
                                } catch (Exception e2) {
                                    throw th2;
                                }
                            }
                            try {
                                field.setAccessible(false);
                            } catch (Exception e3) {
                            }
                        }
                    } else {
                        try {
                            field.setAccessible(z);
                            Object staticVal = field.get(null);
                            if (staticVal != null && isAppObject(staticVal)) {
                                scanObject(staticVal, cls3.getSimpleName() + "." + field.getName(), results, visited, target);
                            }
                        } catch (Exception e4) {
                        } catch (Throwable th3) {
                            th = th3;
                            try {
                                field.setAccessible(false);
                                throw th;
                            } catch (Exception e5) {
                                throw th;
                            }
                        }
                        try {
                            if (isPrimitiveType(field.getType())) {
                                try {
                                    field.setAccessible(true);
                                    Class<?> ft = field.getType();
                                    cls2 = cls3;
                                    try {
                                        try {
                                            Class<?> cls4 = Integer.TYPE;
                                            long val4 = ft == cls4 ? field.getInt(null) : ft == Short.TYPE ? field.getShort(null) : ft == Byte.TYPE ? field.getByte(null) : ft == Character.TYPE ? field.getChar(null) : ft == Long.TYPE ? field.getLong(null) : ft == Float.TYPE ? (long) field.getFloat(null) : ft == Double.TYPE ? (long) field.getDouble(null) : ft == Boolean.TYPE ? field.getBoolean(null) ? 1L : 0L : 0L;
                                            if (target == null || val4 == target.longValue()) {
                                                Result r3 = new Result();
                                                fieldArr = declaredFields;
                                                try {
                                                    r3.container = null;
                                                    r3.field = field;
                                                    r3.displayPath = cls2.getSimpleName() + "." + field.getName() + " (static)";
                                                    r3.lastValue = val4;
                                                    long val5 = scanId;
                                                    r3.scanId = val5;
                                                    results.add(r3);
                                                } catch (Exception e6) {
                                                }
                                            } else {
                                                fieldArr = declaredFields;
                                            }
                                        } catch (Throwable th4) {
                                            th = th4;
                                            field.setAccessible(false);
                                            throw th;
                                        }
                                    } catch (Exception e7) {
                                        fieldArr = declaredFields;
                                    }
                                } catch (Exception e8) {
                                    cls2 = cls3;
                                    fieldArr = declaredFields;
                                }
                            } else {
                                cls2 = cls3;
                                fieldArr = declaredFields;
                            }
                            try {
                                field.setAccessible(false);
                            } catch (Exception e9) {
                            }
                        } catch (Throwable th5) {
                            th = th5;
                        }
                    }
                    i3++;
                    cls3 = cls2;
                    declaredFields = fieldArr;
                    z = true;
                }
                cls = cls3;
            } else {
                cls = cls3;
                for (Field field2 : cls.getDeclaredFields()) {
                    int mod2 = field2.getModifiers();
                    if (!Modifier.isStatic(mod2) && !Modifier.isFinal(mod2)) {
                        try {
                            field2.setAccessible(true);
                            Object child2 = field2.get(obj);
                            if (child2 != null && isAppObject(child2)) {
                                scanObject(child2, path + "." + field2.getName(), results, visited, target);
                            }
                            try {
                                field2.setAccessible(false);
                            } catch (Exception e10) {
                            }
                        } catch (Exception e11) {
                            try {
                                field2.setAccessible(false);
                            } catch (Exception e12) {
                            }
                        } catch (Throwable th6) {
                            try {
                                field2.setAccessible(false);
                                throw th6;
                            } catch (Exception e13) {
                                throw th6;
                            }
                        }
                    }
                }
            }
            cls3 = cls.getSuperclass();
        }
    }

    private static long readFieldValue(Field field, Object container) {
        try {
            Class<?> type = field.getType();
            return type == Integer.TYPE ? field.getInt(container) : type == Short.TYPE ? field.getShort(container) : type == Byte.TYPE ? field.getByte(container) : type == Character.TYPE ? field.getChar(container) : type == Long.TYPE ? field.getLong(container) : type == Float.TYPE ? (long) field.getFloat(container) : type == Double.TYPE ? (long) field.getDouble(container) : (type == Boolean.TYPE && field.getBoolean(container)) ? 1L : 0L;
        } catch (Exception e) {
        }
        return 0L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long readArrayElement(Object array, int index, Class<?> compType) {
        try {
            return compType == Integer.TYPE ? Array.getInt(array, index) : compType == Short.TYPE ? Array.getShort(array, index) : compType == Byte.TYPE ? Array.getByte(array, index) : compType == Character.TYPE ? Array.getChar(array, index) : compType == Long.TYPE ? Array.getLong(array, index) : compType == Float.TYPE ? (long) Array.getFloat(array, index) : compType == Double.TYPE ? (long) Array.getDouble(array, index) : (compType == Boolean.TYPE && Array.getBoolean(array, index)) ? 1L : 0L;
        } catch (Exception e) {
        }
        return 0L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void writeArrayElement(Object array, int index, Class<?> compType, long value) {
        try {
            if (compType == Integer.TYPE) {
                Array.setInt(array, index, (int) value);
            } else if (compType == Short.TYPE) {
                Array.setShort(array, index, (short) value);
            } else if (compType == Byte.TYPE) {
                Array.setByte(array, index, (byte) value);
            } else if (compType == Character.TYPE) {
                Array.setChar(array, index, (char) value);
            } else if (compType == Long.TYPE) {
                Array.setLong(array, index, value);
            } else if (compType == Float.TYPE) {
                Array.setFloat(array, index, value);
            } else if (compType == Double.TYPE) {
                Array.setDouble(array, index, value);
            } else if (compType == Boolean.TYPE) {
                Array.setBoolean(array, index, value != 0);
            }
        } catch (Exception e) {
        }
    }
}
