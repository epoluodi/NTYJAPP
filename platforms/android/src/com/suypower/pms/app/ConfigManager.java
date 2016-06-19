package com.suypower.pms.app;

import java.io.IOException;
import java.util.Properties;

/**
 * 配置管理器
 * 主要用来管理system_config.properties
 * @author liuzeren
 *
 */
public class ConfigManager {

	private static ConfigManager mConfigManager;
	
	/**
	 * 数据集
	 */
	private Properties properties = null;
	
	/**
	 * system_config.properties
	 */
	private final static String SYSTEM_CONFIG_PROPERTIES = "/assets/system_config.properties";
		
	private ConfigManager()
	{
		loadConfig();
	}
	
	public static ConfigManager getConfigManager()
	{
		if(mConfigManager == null)
		{
			synchronized(ConfigManager.class)
			{
				mConfigManager = new ConfigManager();
			}
		}
		
		return mConfigManager;
	}
	
	public void loadConfig()
	{
		loadConfig(SYSTEM_CONFIG_PROPERTIES);
	}
	
	public void loadConfig(String path)
	{
		properties = new Properties();
		try {
			properties.load(ConfigManager.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Properties getConfig()
	{
		return properties;
	}
}
