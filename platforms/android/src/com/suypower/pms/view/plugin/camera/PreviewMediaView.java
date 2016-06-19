package com.suypower.pms.view.plugin.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.view.dlg.AlertDlg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Stereo on 16/6/2.
 */
public class PreviewMediaView extends Activity {

    public static final int REQUESTCODE = 1;
    private ViewPager viewPager;
    private String[] mediaids;
    private List<RelativeLayout> viewList;
    private int pasges = 0;
    private Boolean isdelete = false;
    private RelativeLayout relativeLayouttitle;
    private Animation animation1;
    private ImageView imageleft, imageright;
    private TextView txtnumber;
    private List<String> medialists;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previewmediaview_activity);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        medialists = new ArrayList<>();
        mediaids = getIntent().getStringArrayExtra("mediaids");
        for (int i=0;i<mediaids.length;i++)
        {
            medialists.add(mediaids[i]);
        }
        relativeLayouttitle = (RelativeLayout) findViewById(R.id.relativeLayout_title);
        imageleft = (ImageView) findViewById(R.id.title_left_menu_image);
        imageleft.setOnClickListener(onClickListenerleft);
        imageright = (ImageView) findViewById(R.id.pre_right_image);
        imageright.setOnClickListener(onClickListenerright);
        txtnumber = (TextView) findViewById(R.id.txt_number);
        viewPager.setOnPageChangeListener(onPageChangeListener);
        InitViewPager();

        viewPager.setAdapter(pagerAdapter);
        Bundle bundle = getIntent().getExtras();
        pasges = bundle.getInt("src");
        isdelete = bundle.getBoolean("isdelete");
//        viewPager.setOnPageChangeListener(onPageChangeListener);
        viewPager.setCurrentItem(pasges);
        txtnumber.setText(String.format("%1$s/%2$s", pasges + 1, pagerAdapter.getCount()));
    }



    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

            txtnumber.setText(String.format("%1$s/%2$s", i + 1, pagerAdapter.getCount()));
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };



    //点击选中图片
    View.OnClickListener onClickListenerright = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            AlertDlg alertDlg =new AlertDlg(PreviewMediaView.this, AlertDlg.AlertEnum.INFO);
            alertDlg.setContentText("确定删除？");
            alertDlg.setOkClickLiseter(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDlg.dismiss();
                    medialists.remove(viewPager.getCurrentItem());
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
//                    String[] fileindex = new String[medialists.size()];
//                    for (int i = 0; i < medialists.size(); i++) {
//                        fileindex[i] = medialists.get(i);
//                    }
//                    bundle.putStringArray("files", fileindex);
                    bundle.putInt("delindex",viewPager.getCurrentItem());
                    intent.putExtras(bundle);
                    setResult(1, intent);//删除
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });
            alertDlg.show();




//            pagerAdapter.startUpdate(viewPager);
//            viewList.remove(viewPager.getCurrentItem());
//            pagerAdapter.notifyDataSetChanged();
//            viewPager.setCurrentItem(0);
//            pagerAdapter.finishUpdate(viewPager);
        }
    };

    //返回
    View.OnClickListener onClickListenerleft = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            Intent intent = new Intent();
//            Bundle bundle = new Bundle();
//            String[] fileindex = new String[medialists.size()];
//            for (int i = 0; i < medialists.size(); i++) {
//                fileindex[i] = medialists.get(i);
//            }
//            bundle.putStringArray("files", fileindex);
//            intent.putExtras(bundle);
//            setResult(1, intent);//删除
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        }
    };

    /**
     * 初始化viewpage
     */
    void InitViewPager() {
        viewList = new ArrayList<RelativeLayout>();
        ImageView imageView;
        RelativeLayout relativeLayout;
        RelativeLayout.LayoutParams layoutParams;
        RelativeLayout.LayoutParams layoutParams2;
        for (int i = 0; i < medialists.size(); i++) {
            try {
                layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                imageView = new ImageView(this);
                relativeLayout = new RelativeLayout(this);
                relativeLayout.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                relativeLayout.addView(imageView, layoutParams2);
                imageView.setOnClickListener(onClickListenerimg);
                imageView.setClickable(true);
                imageView.setId(i);
                viewList.add(relativeLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    View.OnClickListener onClickListenerimg = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isdelete == false) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                if (relativeLayouttitle.getVisibility() == View.INVISIBLE) {
                    relativeLayouttitle.setVisibility(View.VISIBLE);
                    relativeLayouttitle.startAnimation(AnimationUtils.loadAnimation(PreviewMediaView.this, R.anim.slide_in_from_top));
                } else {
                    relativeLayouttitle.setVisibility(View.INVISIBLE);
                    relativeLayouttitle.startAnimation(AnimationUtils.loadAnimation(PreviewMediaView.this, R.anim.slide_out_to_top));
                }
            }
        }
    };


    PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return medialists.size();
        }


        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {

            return arg0 == arg1;
        }


        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return "";
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            RelativeLayout relativeLayout = viewList.get(position);
            ImageView imageView = (ImageView) relativeLayout.findViewById(position);
            String path = String.format("%1$s/%2$s.jpg", getCacheDir(), medialists.get(position));
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);
            return relativeLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                RelativeLayout relativeLayout = viewList.get(position);
                ImageView imageView = (ImageView) relativeLayout.findViewById(position);
                imageView.setImageBitmap(null);
                if (object == null)
                    object = container.getChildAt(position);
                container.removeView((View) object);
            }
            catch (Exception e)
            {e.printStackTrace();}
        }

    };


}