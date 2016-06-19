package com.suypower.pms.app;

/**
 * 封装用户登陆返回标记信息
 * @author liuzeren
 *
 */
public class SuyLoginResultCode {
	/**
	 * 登录成功
	 */
	public final static int SUCCESS = 0;	
	/**
	 * 登录失败
	 */
	public final static int FAILED = 1;
	/**
	 * 密码错误
	 */
	public final static int PASSWORD_ERROR = 2;	
	/**
	 * 需要输入验证码
	 */
	public final static int NEED_VERIFY_CODE = 3;
	/**
	 * 验证码错误
	 */
	public final static int VERIFY_CODE_ERROR = 4;
	/**
	 * 用户取消登录
	 */
	public final static int USER_CANCEL_LOGIN = 5;
}
