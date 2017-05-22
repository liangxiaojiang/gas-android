package com.joe.oil.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.lang.reflect.Field;

/**
 * PreferencesUtils, easy to get or put data
 * <ul>
 * <strong>Preference Name</strong>
 * <li>you can change preference name by {@link #PREFERENCE_NAME}</li>
 * </ul>
 * <ul>
 * <strong>Put Value</strong>
 * <li>put string {@link #putString(android.content.Context, String, String)}</li>
 * <li>put int {@link #putInt(android.content.Context, String, int)}</li>
 * <li>put long {@link #putLong(android.content.Context, String, long)}</li>
 * <li>put float {@link #putFloat(android.content.Context, String, float)}</li>
 * <li>put boolean {@link #putBoolean(android.content.Context, String, boolean)}</li>
 * </ul>
 * <ul>
 * <strong>Get Value</strong>
 * <li>get string {@link #getString(android.content.Context, String)}, {@link #getString(android.content.Context, String, String)}</li>
 * <li>get int {@link #getInt(android.content.Context, String)}, {@link #getInt(android.content.Context, String, int)}</li>
 * <li>get long {@link #getLong(android.content.Context, String)}, {@link #getLong(android.content.Context, String, long)}</li>
 * <li>get float {@link #getFloat(android.content.Context, String)}, {@link #getFloat(android.content.Context, String, float)}</li>
 * <li>get boolean {@link #getBoolean(android.content.Context, String)}, {@link #getBoolean(android.content.Context, String, boolean)}</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-3-6
 */
public class PreferencesUtils {

	public static String PREFERENCE_NAME = "oil";

	/**
	 * put string preferences
	 *
	 * @param context
	 * @param key     The name of the preference to modify
	 * @param value   The new value for the preference
	 * @return True if the new values were successfully written to persistent storage.
	 */
	public static boolean putString (Context context, String key, String value) {
		SharedPreferences settings = context.getSharedPreferences (PREFERENCE_NAME, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = settings.edit ();
		editor.putString (key, value);
		return editor.commit ();
	}

	/**
	 * get string preferences
	 *
	 * @param context
	 * @param key     The name of the preference to retrieve
	 * @return The preference value if it exists, or null. Throws ClassCastException if there is a preference with this
	 * name that is not a string
	 * @see #getString(android.content.Context, String, String)
	 */
	public static String getString (Context context, String key) {
		return getString (context, key, null);
	}

	/**
	 * get string preferences
	 *
	 * @param context
	 * @param key          The name of the preference to retrieve
	 * @param defaultValue Value to return if this preference does not exist
	 * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
	 * this name that is not a string
	 */
	public static String getString (Context context, String key, String defaultValue) {
		SharedPreferences settings = context.getSharedPreferences (PREFERENCE_NAME, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
		return settings.getString (key, defaultValue);
	}

	/**
	 * put int preferences
	 *
	 * @param context
	 * @param key     The name of the preference to modify
	 * @param value   The new value for the preference
	 * @return True if the new values were successfully written to persistent storage.
	 */
	public static boolean putInt (Context context, String key, int value) {
		SharedPreferences settings = context.getSharedPreferences (PREFERENCE_NAME, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = settings.edit ();
		editor.putInt (key, value);
		return editor.commit ();
	}

	/**
	 * get int preferences
	 *
	 * @param context
	 * @param key     The name of the preference to retrieve
	 * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
	 * name that is not a int
	 * @see #getInt(android.content.Context, String, int)
	 */
	public static int getInt (Context context, String key) {
		return getInt (context, key, - 1);
	}

	/**
	 * get int preferences
	 *
	 * @param context
	 * @param key          The name of the preference to retrieve
	 * @param defaultValue Value to return if this preference does not exist
	 * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
	 * this name that is not a int
	 */
	public static int getInt (Context context, String key, int defaultValue) {
		SharedPreferences settings = context.getSharedPreferences (PREFERENCE_NAME, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
		return settings.getInt (key, defaultValue);
	}

	/**
	 * put long preferences
	 *
	 * @param context
	 * @param key     The name of the preference to modify
	 * @param value   The new value for the preference
	 * @return True if the new values were successfully written to persistent storage.
	 */
	public static boolean putLong (Context context, String key, long value) {
		SharedPreferences settings = context.getSharedPreferences (PREFERENCE_NAME, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = settings.edit ();
		editor.putLong (key, value);
		return editor.commit ();
	}

	/**
	 * get long preferences
	 *
	 * @param context
	 * @param key     The name of the preference to retrieve
	 * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
	 * name that is not a long
	 * @see #getLong(android.content.Context, String, long)
	 */
	public static long getLong (Context context, String key) {
		return getLong (context, key, - 1);
	}

	/**
	 * get long preferences
	 *
	 * @param context
	 * @param key          The name of the preference to retrieve
	 * @param defaultValue Value to return if this preference does not exist
	 * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
	 * this name that is not a long
	 */
	public static long getLong (Context context, String key, long defaultValue) {
		SharedPreferences settings = context.getSharedPreferences (PREFERENCE_NAME, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
		return settings.getLong (key, defaultValue);
	}

	/**
	 * put float preferences
	 *
	 * @param context
	 * @param key     The name of the preference to modify
	 * @param value   The new value for the preference
	 * @return True if the new values were successfully written to persistent storage.
	 */
	public static boolean putFloat (Context context, String key, float value) {
		SharedPreferences settings = context.getSharedPreferences (PREFERENCE_NAME, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = settings.edit ();
		editor.putFloat (key, value);
		return editor.commit ();
	}

	/**
	 * get float preferences
	 *
	 * @param context
	 * @param key     The name of the preference to retrieve
	 * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
	 * name that is not a float
	 * @see #getFloat(android.content.Context, String, float)
	 */
	public static float getFloat (Context context, String key) {
		return getFloat (context, key, - 1);
	}

	/**
	 * get float preferences
	 *
	 * @param context
	 * @param key          The name of the preference to retrieve
	 * @param defaultValue Value to return if this preference does not exist
	 * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
	 * this name that is not a float
	 */
	public static float getFloat (Context context, String key, float defaultValue) {
		SharedPreferences settings = context.getSharedPreferences (PREFERENCE_NAME, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
		return settings.getFloat (key, defaultValue);
	}

	/**
	 * put boolean preferences
	 *
	 * @param context
	 * @param key     The name of the preference to modify
	 * @param value   The new value for the preference
	 * @return True if the new values were successfully written to persistent storage.
	 */
	public static boolean putBoolean (Context context, String key, boolean value) {
		SharedPreferences settings = context.getSharedPreferences (PREFERENCE_NAME, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = settings.edit ();
		editor.putBoolean (key, value);
		return editor.commit ();
	}

	/**
	 * get boolean preferences, default is false
	 *
	 * @param context
	 * @param key     The name of the preference to retrieve
	 * @return The preference value if it exists, or false. Throws ClassCastException if there is a preference with this
	 * name that is not a boolean
	 * @see #getBoolean(android.content.Context, String, boolean)
	 */
	public static boolean getBoolean (Context context, String key) {
		return getBoolean (context, key, false);
	}

	/**
	 * get boolean preferences
	 *
	 * @param context
	 * @param key          The name of the preference to retrieve
	 * @param defaultValue Value to return if this preference does not exist
	 * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
	 * this name that is not a boolean
	 */
	public static boolean getBoolean (Context context, String key, boolean defaultValue) {
		SharedPreferences settings = context.getSharedPreferences (PREFERENCE_NAME, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
		return settings.getBoolean (key, defaultValue);
	}

//	private Context mContext;
//
//	public PreferencesUtils (Context mContext) {
//		this.mContext = mContext;
//	}

/**
 *利用反射去存储对对象到SharedPreferences
 */

	/**
	 * 存储单个属性
	 *
	 * @param field
	 * @param sp
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static void saveField (Field field, SharedPreferences sp, Object object) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible (true);
		Class fildType = field.getType ();
		if (String.class == fildType || Character.class == fildType) {
			sp.edit ().putString (field.getName (), String.valueOf (field.get (object))).commit ();
		} else if (Integer.TYPE == fildType || Integer.class == fildType) {
			sp.edit ().putInt (field.getName (), field.getInt (object)).commit ();
		} else if (boolean.class == fildType) {
			sp.edit ().putBoolean (field.getName (), field.getBoolean (object)).commit ();
		} else if (Long.class == fildType) {
			sp.edit ().putLong (field.getName (), field.getLong (object)).commit ();
		} else if (Float.class == fildType) {
			sp.edit ().putFloat (field.getName (), field.getFloat (object)).commit ();
		}

		//sp.edit().putString(field.getUserName(), String.valueOf(field.get(object))).commit();
	}

	/**
	 * 拿到单个属性
	 *
	 * @param field
	 * @param sp
	 * @return
	 */
	private String getFieldFromSp (Field field, SharedPreferences sp) {
		field.setAccessible (true);
		Class fildType = field.getType ();
		if (String.class == fildType || Character.class == fildType) {
			return sp.getString (field.getName (), "");
		} else if (Integer.TYPE == fildType || Integer.class == fildType) {
			return String.valueOf (sp.getInt (field.getName (), 0));
		} else if (Boolean.class == fildType) {
			return String.valueOf (sp.getBoolean (field.getName (), false));
		} else if (Long.class == fildType) {
			return String.valueOf (sp.getLong (field.getName (), 0L));
		} else if (Float.class == fildType) {
			return String.valueOf (sp.getFloat (field.getName (), 0F));
		}
		return "";

	}

	/**
	 * 数据存储
	 *
	 * @param sp
	 * @param classzz
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void saveObject (Context mContext, Object object) throws IllegalArgumentException, IllegalAccessException {
		SharedPreferences sp = mContext.getSharedPreferences (object.getClass ().getSimpleName ().toLowerCase (), 2);//1:read 2:write
		Field[] fields = object.getClass ().getDeclaredFields ();
		for (Field field : fields) {
			saveField (field, sp, object);
		}
	}

	/**
	 * 获得存储对象
	 *
	 * @param classzz
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
//	public Object getObject (Context mContext, Class<?> classzz) throws InstantiationException, IllegalAccessException {
//		Object object = classzz.newInstance ();
//		SharedPreferences sp = mContext.getSharedPreferences (classzz.getSimpleName (), 1);//1:read 2:write
//		Field[] fields = object.getClass ().getDeclaredFields ();
//		for (Field field : fields) {
//			try {
//				FieldUtils.setValueToFiled (field, object, getFieldFromSp (field, sp));
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace ();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace ();
//			}
//		}
//		return object;
//	}

}
