package com.suypower.pms.app;

import android.os.Handler;
import android.os.Message;


import com.suypower.pms.app.protocol.data.LoginResult;


/**
 * @author 系统用户信息
 *
 */
public class SuyUserInfo {
	
	private static final String TAG = SuyUserInfo.class.getSimpleName();



	/**
	 * 用户名
	 */
	public String userName;
	/**
	 * 密码
	 */
	public String password;
	
	/**
	 * 用户状态
	 */
	public int m_nStatus;
	/**
	 * 登陆状态
	 */
	public int m_nLoginStatus;
	

	/**
	 * 验证码
	 */
	public String m_strVerifyCode = "";
	
	private Handler m_handlerProxy;
	private Handler m_handlerCallBack;
	/**
	 * 用户登陆返回结果
	 */
	public LoginResult m_loginResult;
	
	/**
	 * 锁1
	 */
	private Object m_objLock1;
	/**
	 * 锁2
	 */
	private Object m_objLock2;
	
	public SuyUserInfo()
	{
		userName = "";
		password = "";
		m_strVerifyCode = "";
		
		m_nStatus = SuyStatus.OFFLINE;
		m_nLoginStatus = SuyStatus.ONLINE;
		m_loginResult = new LoginResult();
		m_objLock1 = new Object();
		m_objLock2 = new Object();

		m_handlerProxy = null;
		m_handlerCallBack = null;
	}
	
	public void setProxyHandler(Handler handler) {
		synchronized (m_objLock1) {
			m_handlerProxy = handler;	
		}
	}
	
	public void setNullProxyHandler(Handler handler) {
		if (null == handler)
			return;
		
		synchronized (m_objLock1) {
			// 只有代理Handler相同的情况下才允许置空
			if (handler == m_handlerProxy) {
				m_handlerProxy.removeCallbacksAndMessages(null);
				m_handlerProxy = null;
			}
		}
	}
	
	public boolean sendProxyMsg(int nMsgId, 
			int nArg1, int nArg2, Object obj) {
		synchronized (m_objLock1) {
			if (m_handlerProxy != null) {
				Message msg = m_handlerProxy.obtainMessage();
				msg.what = nMsgId;
				msg.arg1 = nArg1;
				msg.arg2 = nArg2;
				msg.obj = obj;
				return m_handlerProxy.sendMessage(msg);
			}
			return false;
		}
	}
	
	public boolean sendProxyMsg(int nMsgId, 
			int nArg1, int nArg2, Object obj, boolean bWait) {
		boolean bRet = false;
		try {
			synchronized (m_objLock1) {
				if (m_handlerProxy != null) {
					Message msg = m_handlerProxy.obtainMessage();
					msg.what = nMsgId;
					msg.arg1 = nArg1;
					msg.arg2 = nArg2;
					msg.obj = obj;
					bRet = m_handlerProxy.sendMessage(msg);
					
					if (bWait)
						m_objLock1.wait();
				}
			}
		} catch (InterruptedException e) {

		}
		return bRet;
	}
		
	public void notifyProxyMsg() {
		synchronized (m_objLock1) {
			m_objLock1.notify();
		}
	}

	public void setCallBackHandler(Handler handler) {
		synchronized (m_objLock2) {
			m_handlerCallBack = handler;	
		}
	}
	
	public void setNullCallBackHandler(Handler handler) {
		if (null == handler)
			return;
		
		synchronized (m_objLock2) {
			if (handler == m_handlerCallBack) {
				m_handlerCallBack.removeCallbacksAndMessages(null);
				m_handlerCallBack = null;
			}
		}
	}
	
	public boolean sendCallBackMsg(int nMsgId, 
			int nArg1, int nArg2, Object obj) {
		synchronized (m_objLock2) {
			if (m_handlerCallBack != null) {
				Message msg = m_handlerCallBack.obtainMessage();
				msg.what = nMsgId;
				msg.arg1 = nArg1;
				msg.arg2 = nArg2;
				msg.obj = obj;
				return m_handlerCallBack.sendMessage(msg);
			}
			return false;
		}
	}

	public boolean isNullCallBack() {
		synchronized (m_objLock2) {
			return (null == m_handlerCallBack);
		}
	}
}
