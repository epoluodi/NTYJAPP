package com.suypower.pms.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 配置文件快速访问类
 * @author liuzeren
 *
 */
public final class Config {





	/**
	 * 配置SD数据存储根据目录
	 */
	public static final String ROOT_PATH = "root_path";
	
	/**
	 * 日志输入路径
	 */
	public static final String LOG_PATH = "root_path";
	
	public static String getString(String key) {
        return ConfigManager.getConfigManager().getConfig().getProperty(key);
    }

    public static String getString(String key, String def) {
        return ConfigManager.getConfigManager().getConfig().getProperty(key, def);
    }

    public static int getInt(String key, int def) {
        String value = ConfigManager.getConfigManager().getConfig().getProperty(key, "");
        
        if(TextUtils.isEmpty(value))
        {
        	return def;
        }
        else
        {
        	try
        	{
        		return Integer.parseInt(value);
        	}
        	catch(NumberFormatException e)
        	{
        		return def;
        	}
        }
    }

    public static long getLong(String key, long def) {
    	String value = ConfigManager.getConfigManager().getConfig().getProperty(key, "");
        
        if(TextUtils.isEmpty(value))
        {
        	return def;
        }
        else
        {
        	try
        	{
        		return Long.parseLong(value);
        	}
        	catch(NumberFormatException e)
        	{
        		return def;
        	}
        }
    }

    public static float getFloat(String key, float def) {
    	String value = ConfigManager.getConfigManager().getConfig().getProperty(key, "");
        
        if(TextUtils.isEmpty(value))
        {
        	return def;
        }
        else
        {
        	try
        	{
        		return Float.parseFloat(value);
        	}
        	catch(NumberFormatException e)
        	{
        		return def;
        	}
        }
    }

    public static double getDouble(String key, double def) {
    	String value = ConfigManager.getConfigManager().getConfig().getProperty(key, "");
        
        if(TextUtils.isEmpty(value))
        {
        	return def;
        }
        else
        {
        	try
        	{
        		return Double.parseDouble(value);
        	}
        	catch(NumberFormatException e)
        	{
        		return def;
        	}
        }
    }

    public static boolean getBoolean(String key, boolean def) {
    	String value = ConfigManager.getConfigManager().getConfig().getProperty(key, "");
        
        if(TextUtils.isEmpty(value))
        {
        	return def;
        }
        else
        {
        	try
        	{
        		return Boolean.parseBoolean(value);
        	}
        	catch(NumberFormatException e)
        	{
        		return def;
        	}
        }
    }

    public static void setProperty(String key, String value) {
        ConfigManager.getConfigManager().getConfig().setProperty(key, value);
    }


	/**
	 * 获取本地用户信息
	 * @param context
	 * @param key
     * @return
     */
	public static String getKeyShareVarForString(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
		return sharedPreferences.getString(key, "null");
	}


	public static Boolean getKeyShareVarForBoolean(Context context,String key)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, false);
	}

	public static int getKeyShareVarForint(Context context,String key)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, -1);
	}

	public static long getKeyShareVarForLong(Context context,String key)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		return sharedPreferences.getLong(key, -1);
	}
	/**
	 * 设置信息
	 * @param context
	 * @param key
	 * @param value
     */
	public static void setKeyShareVar(Context context,String key,String value)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key,value);
		editor.commit();
	}

	public static void setKeyShareVar(Context context,String key,int value)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key,value);
		editor.commit();
	}

	public static void setKeyShareVar(Context context,String key,boolean value)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key,value);
		editor.commit();
	}
	/**
	 * 删除信息
	 * @param context
	 * @param key
     */
	public static void delKeyShareVar(Context context,String key)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}


}
