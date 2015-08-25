package net.thinkbase.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 和反射相关的工具函数
 * @author thinkbase.net
 */
public class ReflectUtil {

	public static Object duplicateObject(Object src) {
		return duplicateObject(src, null);
	}

	public static Object duplicateObject(Object src, final ClassLoader cl) {
		//使用序列化机制完全复制一个对象
		Object dest;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(src);
			byte[] res = bos.toByteArray();
			bos.close();
			oos.close();

			ByteArrayInputStream bis = new ByteArrayInputStream(res);
			ObjectInputStream ois = null;
			if (null == cl) {
				ois = new ObjectInputStream(bis);
			}
			else {
				ois = new ObjectInputStream(bis) {
					@Override
					protected Class< ? > resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
						return cl.loadClass(desc.getName());
					}
				};
			}
			dest = ois.readObject();
			bis.close();
			ois.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return dest;
	}

	@SuppressWarnings("unchecked")
	public static Field getField(Class clazz, String name) throws NoSuchFieldException {
		Field field = _getField(clazz, name);
		return field;
	}

	@SuppressWarnings("unchecked")
	private static final Field _getField(final Class clazz, final String name) throws NoSuchFieldException {
		Class cls = clazz;
		try {
			Field field = cls.getDeclaredField(name);
			return field;
		}
		catch (NoSuchFieldException e) {
			cls = cls.getSuperclass();
			if (null == cls) {
				throw e;
			}
			return _getField(cls, name);
		}
	}

	public static final void setFieldIntValue(Object obj, String name, int intValue) {
		try {
			Field field = getField(obj.getClass(), name);
			field.setAccessible(true);
			field.setInt(obj, intValue);
		}
		catch (NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static final void setFieldValue(Object obj, String name, Object value) {
		try {
			Field field = getField(obj.getClass(), name);
			field.setAccessible(true);
			field.set(obj, value);
		}
		catch (NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static final Object getFieldValue(Object obj, String name) {
		try {
			Field field = getField(obj.getClass(), name);
			field.setAccessible(true);
			return field.get(obj);
		}
		catch (NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static Method getMethod(Class clazz, String methodName, Object[] args) throws NoSuchMethodException {
		Method mthd;
		if (args != null && args.length > 0) {
			// We need to get the argument type list so that we can differentiate override methods
			Class[] argTypes = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					// Check the primitive types first
					if (args[i] instanceof Boolean) {
						argTypes[i] = Boolean.TYPE;
					}
					else if (args[i] instanceof Character) {
						argTypes[i] = Character.TYPE;
					}
					else if (args[i] instanceof Byte) {
						argTypes[i] = Byte.TYPE;
					}
					else if (args[i] instanceof Short) {
						argTypes[i] = Short.TYPE;
					}
					else if (args[i] instanceof Integer) {
						argTypes[i] = Integer.TYPE;
					}
					else if (args[i] instanceof Long) {
						argTypes[i] = Long.TYPE;
					}
					else if (args[i] instanceof Float) {
						argTypes[i] = Float.TYPE;
					}
					else if (args[i] instanceof Double) {
						argTypes[i] = Double.TYPE;
					}
					else if (args[i] instanceof Void) {
						argTypes[i] = Void.TYPE;
					}
					else {
						argTypes[i] = args[i].getClass();
					}
				}
				else {
					argTypes[i] = Object.class;
				}
			}
			mthd = _getMethod(clazz, methodName, argTypes, null);
		}
		else {
			mthd = _getMethod(clazz, methodName, null, null);
		}
		return mthd;
	}

	@SuppressWarnings("unchecked")
	private static final Method _getMethod(final Class clazz, final String name, final Class[] argTypes, NoSuchMethodException firstNsme)
			throws NoSuchMethodException {
		Class cls = clazz;
		try {
			return cls.getDeclaredMethod(name, argTypes);
		}
		catch (NoSuchMethodException e) {
			if (null == firstNsme) {
				firstNsme = e; //记住整个方法查找链中第一个 NoSuchMethodException, 以便此后可以抛出这个 NoSuchMethodException
			}
			cls = cls.getSuperclass();
			if (null == cls) {
				throw firstNsme; //这里抛出的是整个查找链中第一个 NoSuchMethodException, 便于对错误进行准确定位
			}
			return _getMethod(cls, name, argTypes, firstNsme);
		}
	}

	public static Object callMethod(Object obj, String methodName, Object[] args) {
		try {
			Method mthd;
			mthd = getMethod(obj.getClass(), methodName, args);
			mthd.setAccessible(true);
			return mthd.invoke(obj, args);
		}
		catch (NoSuchMethodException ex) {
			throw new RuntimeException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		catch (InvocationTargetException ex) {
			throw new RuntimeException(ex.getTargetException());
		}
	}

	public static Class classForName(String name) throws ClassNotFoundException {
		try {
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			if (contextClassLoader != null) {
				return contextClassLoader.loadClass(name);
			}
			else {
				return Class.forName(name);
			}
		}
		catch (Exception e) {
			return Class.forName(name);
		}
	}

}
