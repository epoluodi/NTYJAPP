package com.suypower.pms.app.task;

import android.os.Message;

import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.SuyUserInfo;
import com.suypower.pms.view.plugin.AjaxHttpPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Stereo on 16/4/15.
 */
public abstract class BaseTask {

    public static final int SUCCESS=0;//成功
    public static final int FAILED=-1;//失败
    public static final int PASSWORD_ERROR=-2;//密码错误
    public static final int DOWNLOAD_FAIL=-3;//下载失败
    public static final int DOWNLOAD_FINISH_ALL=1;//全部下载完成
    public static final int DOWNLOAD_FINISH_SINGLE=2;//单个下载完成
    public static final int CREATEGROUP_FAIL=-4;//群聊创建错误
    public static final int SENDMSG_FAIL=-5;//群聊创建错误
    public static final int REMOVEGROUP_FAIL=-5;//群聊创建错误




    public static final int CommonTask = 0;//通用任务
    public static final int LoginTask = 1;//登录
    public static final int ContactsTask = 2;//联系人
    public static final int DownloadFILETask = 3;//文件下载
    public static final int IMTask = 4;//群聊任务
    public static final int UploadFileTask = 5;//文件上传任务
    public static final int PublishNotics = 6;//上传任务

    ExecutorService m_ThreadPool = null;

    SuyHttpClient m_httpClient = null;
    SuyUserInfo suyUserInfo = null;


    public BaseTask()
    {
        AjaxHttpPlugin ajaxHttpPlugin = new AjaxHttpPlugin();
        m_httpClient = ajaxHttpPlugin.initHttp();
        suyUserInfo = SuyApplication.getApplication().getSuyClient().getSuyUserInfo();
        m_ThreadPool = Executors.newFixedThreadPool(50);

    }

    public abstract void startTask();
    public abstract void stopTask();

}
