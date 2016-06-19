package com.suypower.pms.app.task;

import org.apache.http.client.HttpClient;

import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.SuyUserInfo;

/**
 * http请求线程主父线程
 * @author liuzeren
 *
 */
public class HttpTask extends Task {
	protected SuyHttpClient m_httpClient = null;
	public SuyUserInfo suyUserInfo = null;
	
	public HttpTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName);
		m_httpClient = new SuyHttpClient(httpClient);
	}

	@Override
	public void cancelTask() {
		super.cancelTask();

			if (m_httpClient != null)
				m_httpClient.closeRequest();


	}
	
	/**
	 * 向用户发送服务器请求返回的结果信息
	 * 包括登陆结果等
	 * @param nMsgId
	 * @param nArg1
	 * @param nArg2
	 * @param obj
	 * @return
	 */
	protected boolean sendMessage(int nMsgId, 
			int nArg1, int nArg2, Object obj) {
		if (suyUserInfo != null)
			return suyUserInfo.sendProxyMsg(nMsgId, nArg1, nArg2, obj);
		else
			return false;
	}

	protected boolean sendCallBackMessage(int nMsgId,
								  int nArg1, int nArg2, Object obj) {
		if (suyUserInfo != null)
			return suyUserInfo.sendCallBackMsg(nMsgId, nArg1, nArg2, obj);
		else
			return false;
	}


	protected boolean sendMessage(int nMsgId, 
			int nArg1, int nArg2, Object obj, boolean bWait) {
		if (suyUserInfo != null)
			return suyUserInfo.sendProxyMsg(nMsgId, nArg1, nArg2, obj, bWait);
		else
			return false;
	}
}
