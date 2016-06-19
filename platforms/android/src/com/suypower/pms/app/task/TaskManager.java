package com.suypower.pms.app.task;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程管理器
 * 同一个线程管理器中，同一时刻最多只能启动一个相同名称的线程。
 * @author liuzeren
 *
 */
public class TaskManager {
	/**
	 * 线程池对象
	 */
	private ExecutorService m_ThreadPool = null;
	/**
	 * 线程池中所有的线程的映射
	 */
	private HashMap<String, Task> m_mapTask = null;
	private static final int THREAD_COUNT = 10;
	
	synchronized public void init(int nThreadNums) {
		if (1 == nThreadNums)
			m_ThreadPool = Executors.newSingleThreadExecutor();//只有一个线程的线程池，因此所有提交的任务是顺序执行
		else if (-1 == nThreadNums)
			m_ThreadPool = Executors.newCachedThreadPool();//线程池里有很多线程需要同时执行，老的可用线程将被新的任务触发重新执行，如果线程超过60秒内没执行，那么将被终止并从池中删除
		else if (0 == nThreadNums)
			m_ThreadPool = Executors.newFixedThreadPool(THREAD_COUNT);//拥有固定线程数的线程池，如果没有任务执行，那么线程会一直等待
		else
			m_ThreadPool = Executors.newFixedThreadPool(nThreadNums);
		
		m_mapTask = new HashMap<String, Task>();
	}
	
	/**
	 * 终止线程池
	 */
	synchronized public void shutdown() {
		m_ThreadPool.shutdown();
		for (Task task : m_mapTask.values())
        {
            if (task != null)
            	task.cancelTask();
        }
		m_mapTask.clear();
	}
	
	/**
	 * 新增线程
	 * @param task 线程对象
	 * @return
	 */
	synchronized public boolean addTask(Task task) {
		if (m_ThreadPool.isShutdown())
			return false;
		
		if (m_mapTask.get(task.getTaskName()) != null)//重复名称的线程不加入
			return false;
		
		task.setTaskManager(this);
		m_mapTask.put(task.getTaskName(), task);
		m_ThreadPool.execute(task);
		return true;
	}
	
	/**
	 * 根据线程名查找线程对象
	 * @param strTaskName 线程名
	 * @return
	 */
	synchronized public Task findTask(String strTaskName) {
		return m_mapTask.get(strTaskName);
	}
	
	/**
	 * 根据线程名删除线程对象
	 * @param strTaskName
	 */
	synchronized public void delTask(String strTaskName) {
		Task task = m_mapTask.get(strTaskName);
		if (task != null) {
			task.cancelTask();
			m_mapTask.remove(strTaskName);
		}
	}

	synchronized public void cancelTask(String strTaskName) {
		Task task = m_mapTask.get(strTaskName);
		if (task != null) {
			task.m_bCancel=true;

		}
	}


	/**
	 * 清空所有的线程
	 */
	synchronized public void delAllTask() {
		for (Task task : m_mapTask.values())
        {
            if (task != null)
            	task.cancelTask();
        }
		m_mapTask.clear();
	}
}
