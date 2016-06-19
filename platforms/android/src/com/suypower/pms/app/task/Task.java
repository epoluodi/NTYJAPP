package com.suypower.pms.app.task;


/**
 * 系统线程基类，系统中所有的线程必须继承该类
 * @author liuzeren
 *
 */
public class Task  implements Runnable {
	/**
	 * 线程所属的任务管理器
	 */
	protected TaskManager m_taskMgr;
	/**
	 * 线程名称（唯一名称），用来限制同一线程的多次启动
	 */
	protected String m_strTaskName;
	/**
	 * 取消线程的执行
	 */
	protected boolean m_bCancel;
	/**
	 * 判断线程是否还在执行
	 */
	protected boolean m_bRunning;
		
	public Task(String strTaskName) {
		m_taskMgr = null;
		m_strTaskName = strTaskName;
		m_bCancel = false;
		m_bRunning = false;
	}
	
	public TaskManager getTaskManager() {
		return m_taskMgr;
	}
	
	public void setTaskManager(TaskManager taskMgr) {
		m_taskMgr = taskMgr;
	}

	public String getTaskName() {
		return m_strTaskName;
	}
	
	public void setTaskName(String strTaskName) {
		m_strTaskName = strTaskName;
	}
	
	public void cancelTask() {
		m_bCancel = true;
	}
	
	public boolean isRunning() {
		return m_bRunning;
	}
		
    @Override
    public void run() {
    	if (m_bCancel)
    		return;
    	
    	m_bRunning = true;
    	doTask();
    	m_bRunning = false;
    	
    	if (m_taskMgr != null)
    		m_taskMgr.delTask(m_strTaskName);//线程执行完毕，需要从线程管理器中将该线程删除掉
    	
    	m_bCancel = false;
    }
    
    /**
     * 线程处理类
     */
    public void doTask() {
    	
    }
}
