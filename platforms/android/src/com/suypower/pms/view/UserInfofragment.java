package com.suypower.pms.view;

import android.app.DownloadManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.protocol.data.LoginResult;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.FileDownload;
import com.suypower.pms.app.task.FileUpLoad;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.app.task.Login;
import com.suypower.pms.app.task.PublishNotics;
import com.suypower.pms.app.task.UpdateUserInfo;
import com.suypower.pms.server.MsgBodyChat;
import com.suypower.pms.server.NotificationClass;
import com.suypower.pms.view.dlg.AlertSheet;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.UpdatePlugin;
import com.suypower.pms.view.plugin.camera.CameraHelper;
import com.suypower.pms.view.plugin.camera.CameraPlugin;
import com.suypower.pms.view.plugin.camera.PreviewMediaView;
import com.suypower.pms.view.plugin.camera.PreviewPhotoViewPlugin;
import com.suypower.pms.view.plugin.chat.ChatDB;
import com.suypower.pms.view.plugin.fragmeMager.FragmentName;
import com.suypower.pms.view.plugin.message.MessageDB;
import com.suypower.pms.view.seeting.NotificationConfigActivity;
import com.suypower.pms.view.seeting.Passwordset;
import com.suypower.pms.view.user.UserInfoActivity;

import org.apache.cordova.CordovaWebView;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Administrator on 14-11-30.
 */
public class UserInfofragment extends Fragment implements FragmentName {

    private String Fragment_Name = "UserInfofragment";
    private UpdatePlugin updatePlugin;

    private RelativeLayout menu_person, menu_notification, menu_clearbuffer,
            menu_checkupdate, menu_aboutsoft, menu_exit,menu_pwd;

    private TextView txtname, txtdepartname, txtpost, txtlevel, txtindate;
    private ImageView nickimg;
    private AlertSheet alertSheet;

    @Override
    public void startIMessageControl() {

    }

    @Override
    public void stopIMessageControl() {

    }

    @Override
    public void selectcustomer(String guestid, String guestname) {
        return;
    }

    @Override
    public void SelectMenu(int Menuid) {

    }

    @Override
    public void returnWeb() {

    }

    @Override
    public void onMessage(Message message) {

    }

    @Override
    public void SetFragmentName(String name) {
        Fragment_Name = name;
    }

    @Override
    public String GetFragmentName() {
        return Fragment_Name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_userinfo, container, false);

        menu_person = (RelativeLayout) rootView.findViewById(R.id.menu_person);
        menu_notification = (RelativeLayout) rootView.findViewById(R.id.menu_notification);
        menu_clearbuffer = (RelativeLayout) rootView.findViewById(R.id.menu_clearbuffer);
        menu_checkupdate = (RelativeLayout) rootView.findViewById(R.id.menu_checkupdate);
        menu_aboutsoft = (RelativeLayout) rootView.findViewById(R.id.menu_aboutsoft);
        menu_exit = (RelativeLayout) rootView.findViewById(R.id.menu_exit);
        menu_person.setOnClickListener(onClickListener_menu);
        menu_notification.setOnClickListener(onClickListener_menu);
        menu_clearbuffer.setOnClickListener(onClickListener_menu);
        menu_aboutsoft.setOnClickListener(onClickListener_menu);
        menu_exit.setOnClickListener(onClickListener_menu);
        menu_checkupdate.setOnClickListener(onClickListener_menu);
        menu_pwd=(RelativeLayout)rootView.findViewById(R.id.menu_pwdupdate);
        menu_pwd.setOnClickListener(onClickListener_menu);
        txtname = (TextView) rootView.findViewById(R.id.name);
        txtdepartname = (TextView) rootView.findViewById(R.id.department);
        txtpost = (TextView) rootView.findViewById(R.id.post);
        txtlevel = (TextView) rootView.findViewById(R.id.level);
        txtindate = (TextView) rootView.findViewById(R.id.incompanydate);
        nickimg = (ImageView) rootView.findViewById(R.id.nickimg);
        nickimg.setOnClickListener(onClickListenernickimg);
        LoginResult loginResult = SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult;
        txtname.setText(loginResult.m_strUserName);
        txtpost.setText(loginResult.m_positionName);
        txtdepartname.setText(loginResult.m_strDeparment);
//        2a2c9d55-c595-438c-b3cc-7e9328ee3bda
        if (CommonPlugin.checkFileIsExits(loginResult.m_strPhoto, "40.jpg")) {
            Bitmap bitmap = BitmapFactory.decodeFile(SuyApplication.getApplication().getCacheDir() + "/" +
                    loginResult.m_strPhoto + "40.jpg"); //将图片的长和宽缩小味原来的1/2
            nickimg.setImageBitmap(bitmap);
            nickimg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            FileDownload fileDownload = new FileDownload(interfaceTask, FileDownload.StreamFile);
            fileDownload.mediaid = loginResult.m_strPhoto;
            fileDownload.flag = loginResult.m_strPhoto;
            fileDownload.mediatype=".jpg";
            fileDownload.suffix="40";
            fileDownload.startTask();
        }

        return rootView;
    }


    View.OnClickListener onClickListenernickimg = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alertSheet = new AlertSheet(getActivity());
            alertSheet.addSheetButton("拍照", onClickListenertakepicture);
            alertSheet.addSheetButton("从相机中选择", onClickListenerchoosepicture);
            alertSheet.show();
        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bundle bundle;
        if (CameraHelper.JSCallCamera == requestCode)//拍照回调
        {
            //拍照返回信息
            if (resultCode == -1) {

                Bitmap bitmap = BitmapFactory.decodeFile(CameraHelper.PhotoPath);
                bitmap = CameraPlugin.reSizeImage(bitmap, 128, 128);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                bitmap.recycle();
                String mediaid = CameraPlugin.copyCacheFile(baos.toByteArray());
                updatenickimg(mediaid);
                return;
            }
        }
        if (requestCode == PreviewPhotoViewPlugin.JSCallPreviewPhtoto) {
            if (resultCode == 1) {
                bundle = data.getExtras();
                String[] files = bundle.getStringArray("files");
                for (int i = 0; i < files.length; i++) {
                    Log.i("选择照片", files[i]);
                    Bitmap bitmap = BitmapFactory.decodeFile(getActivity().getCacheDir() + File.separator
                            + files[i] + ".jpg");
                    bitmap = CameraPlugin.reSizeImage(bitmap, 128, 128);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    bitmap.recycle();
                    String mediaid = CameraPlugin.copyCacheFile(baos.toByteArray());
                    updatenickimg(mediaid);
                }
                return;
            }
        }


    }


    private void updatenickimg(String mediaid) {
        CustomPopWindowPlugin.ShowPopWindow(nickimg, getActivity().getLayoutInflater(), "正在上传");
        FileUpLoad fileUpLoad = new FileUpLoad(interfaceTask, FileUpLoad.UPLOADFILE);
        fileUpLoad.mediaid = mediaid;
        fileUpLoad.flag = mediaid;
        fileUpLoad.mediatype="01";
        fileUpLoad.IsNotics = true;
        fileUpLoad.startTask();
    }

    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {

            if (message.what == BaseTask.UploadFileTask) {

                if (message.arg2 == FileUpLoad.UPLOADFILE) {
                    if (message.arg1 == BaseTask.SUCCESS) {

                        UpdateUserInfo updateUserInfo= new UpdateUserInfo(interfaceTask,UpdateUserInfo.UPDATENICKIMG);
                        updateUserInfo.oldvalues = SuyApplication.getApplication().getSuyClient()
                                .getSuyUserInfo().m_loginResult.m_strPhoto;
                        updateUserInfo.newvalue = message.obj.toString();
                        updateUserInfo.startTask();

                    } else {
                        CustomPopWindowPlugin.CLosePopwindow();
                        Toast.makeText(getActivity(), "发布失败", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }

            if (message.what == BaseTask.UPDATEUSERINFO) {
                CustomPopWindowPlugin.CLosePopwindow();
                if (message.arg2 == UpdateUserInfo.UPDATENICKIMG) {
                    if (message.arg1 == BaseTask.SUCCESS) {
                        SuyApplication.getApplication().getSuyClient()
                                .getSuyUserInfo().m_loginResult.m_strPhoto = message.obj.toString();
                        String mediaid = message.obj.toString();
                        Bitmap bitmap = BitmapFactory.decodeFile(getActivity().getCacheDir() +
                                File.separator + mediaid + ".jpg");
                        nickimg.setImageBitmap(bitmap);
                        nickimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_SHORT).show();
                        FileDownload fileDownload = new FileDownload(null, FileDownload.StreamFile);
                        fileDownload.mediaid = SuyApplication.getApplication().getSuyClient()
                                .getSuyUserInfo().m_loginResult.m_strPhoto;
                        fileDownload.flag = SuyApplication.getApplication().getSuyClient()
                                .getSuyUserInfo().m_loginResult.m_strPhoto;
                        fileDownload.mediatype=".jpg";
                        fileDownload.suffix="40";
                        fileDownload.startTask();

                    } else {

                        Toast.makeText(getActivity(), "更新失败", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }

            if (message.what == BaseTask.DownloadFILETask) {
                if (message.arg2 == FileDownload.StreamFile) {
                    if (message.arg1 == BaseTask.SUCCESS) {

                        Bitmap bitmap = BitmapFactory.decodeFile(SuyApplication.getApplication().getCacheDir() + "/" +
                                message.obj.toString() + "40.jpg"); //将图片的长和宽缩小味原来的1/2
                        if (bitmap!=null) {
                            nickimg.setImageBitmap(bitmap);
                            nickimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    }
                }
                return;
            }

        }
    };

    /**
     * 拍照
     */
    View.OnClickListener onClickListenertakepicture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alertSheet.dismiss();
            CameraPlugin cameraPlugin = new CameraPlugin((BaseActivityPlugin) getActivity());
            cameraPlugin.takePictureForNaative();
        }
    };

    /**
     * 从相机中选择
     */
    View.OnClickListener onClickListenerchoosepicture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alertSheet.dismiss();
            CameraPlugin cameraPlugin = new CameraPlugin((BaseActivityPlugin) getActivity());
            cameraPlugin.openPreviewPhotoForNavtive(1);
        }
    };

    /**
     * 点击个人信息item
     */
    View.OnClickListener onClickListener_menu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;

            switch (view.getId()) {
                case R.id.menu_person://个人信息
                    intent = new Intent(getActivity(), UserInfoActivity.class);
                    intent.putExtra("IsSelf", true);//自己打开
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    break;
                case R.id.menu_pwdupdate:
                    intent = new Intent(getActivity(), Passwordset.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case R.id.menu_notification://通知设置
                    intent = new Intent(getActivity(), NotificationConfigActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case R.id.menu_clearbuffer:
                    NotificationClass.Clear_Notify();
                    CustomPopWindowPlugin.ShowPopWindow(menu_clearbuffer,
                            getActivity().getLayoutInflater(), "正在清理");
                    File[] cachefile = getActivity().getCacheDir().listFiles();
                    for (File file : cachefile) {
                        file.delete();
                    }
                    CustomPopWindowPlugin.CLosePopwindow();
//                    MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
//                    messageDB.delMessage();
//                    ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
//                    chatDB.delChatLog();
                    Toast.makeText(getActivity(), "清理完成", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.menu_checkupdate:
                    CustomPopWindowPlugin.ShowPopWindow(menu_checkupdate, getActivity().getLayoutInflater(), "更新检查");
                    /**
                     * 更新检查
                     */
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updatePlugin = new UpdatePlugin(getActivity(), new UpdatePlugin.IUpdateResult() {
                                @Override
                                public void CheckResult(int state, String msg) {

                                }

                                @Override
                                public void DownloadResult(int state, String msg) {
                                    //更新
                                    if (state == 1) {
                                        updatePlugin.downloadAPK(UpdatePlugin.UPDATEURL);
                                    }
                                }
                            });

                            boolean r = updatePlugin.CheckVerison();
                            if (r) {
                                try {
                                    JSONObject jsonObject = (JSONObject) updatePlugin.getData();
                                    Log.i("url", jsonObject.getString("versionNum"));
                                    handler.sendEmptyMessage(0);
                                    return;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            handler.sendEmptyMessage(1);

                        }
                    }).start();
                    break;
                case R.id.menu_exit:
                    NotificationClass.Clear_Notify();
                    ((MainTabView) getActivity()).logoutUser();
                    break;
            }
        }
    };


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CustomPopWindowPlugin.CLosePopwindow();
            if (msg.what == 1) {
                Toast.makeText(getActivity(), "已经是最新版本", Toast.LENGTH_SHORT).show();
                return;
            }
            try {

                String _verstr = ((JSONObject) updatePlugin.getData()).getString("versionStr");
                if (((JSONObject) updatePlugin.getData()).getString("isForce").equals("1"))
                    updatePlugin.showAlertDialog(getActivity(), _verstr, true);
                else
                    updatePlugin.showAlertDialog(getActivity(), _verstr, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


}
