package com.suypower.pms.app;

/**
 * 用户登陆信息
 * @author liuzeren
 *
 */
public class LoginAccountInfo {
	/**
	 * 用户名
	 */
	public String m_strUser = "";
	/**
	 * 密码
	 */
	public String m_strPwd = "";
	/**
	 * 登录状态
	 */
	public int m_nStatus;
	/**
	 * 记住密码
	 */
	public boolean m_bRememberPwd;	
	/**
	 * 自动登录
	 */
	public boolean m_bAutoLogin;
	
	public String toString() {
		return m_strUser + "," + m_strPwd
				+ "," + m_nStatus 
				+ "," + (m_bRememberPwd?1:0) 
				+ "," + (m_bAutoLogin?1:0) + "\n";
	}
}
