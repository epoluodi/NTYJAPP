package com.suypower.pms.view.plugin.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suypower.pms.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stereo on 16/4/8.
 */
public class InputView {

    public enum ChatKeyEnum {
        KEYMORE, KEYEMOJI,
    }

    public enum ChatKeyType {
        PICTURE,CAMERA, MEETTING, VODE, GPSLOCATION,SIGN,
    }


    Context context;
    LayoutInflater layoutInflater;

    LinearLayout linearLayout;
    LinearLayout LinearLayoutemoji;

    LinearLayout item1;
    LinearLayout item2;
    LinearLayout item3;
    LinearLayout item4;

    LinearLayout item5;
    LinearLayout item6;
    LinearLayout item7;
    LinearLayout item8;


    RelativeLayout selectitem1;
    RelativeLayout selectitem2;
    RelativeLayout selectitem3;
    RelativeLayout selectitem4;
    RelativeLayout selectitem5;
    RelativeLayout selectitem6;
    RelativeLayout selectitem7;
    RelativeLayout selectitem8;

    ChatKeyEnum chatKeyEnum;
    ViewPager viewPager;

    PagerAdapter pagerAdapter;
    List<ImageView> imageViewListmark;
    LinearLayout linearLayoutbottom;
    List<View> viewList;
    JSONArray emojijsonlist;

    IChat iChat;





    public InputView(Context context, IChat iChat) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.iChat = iChat;
        initMoreView();
        initemojiView();
    }

    /**
     * 返回更多软件键盘
     *
     * @return
     */
    public View getInputMoreView() {
        chatKeyEnum = ChatKeyEnum.KEYMORE;
        return linearLayout;
    }

    public ChatKeyEnum getChatKeyEnum() {

        return chatKeyEnum;
    }

    /**
     * 返回表情
     *
     * @return
     */
    public View getInputEmojiView() {
        chatKeyEnum = ChatKeyEnum.KEYEMOJI;
        return LinearLayoutemoji;
    }

    /**
     * 表情
     */
    void initemojiView() {
        LinearLayoutemoji = (LinearLayout) layoutInflater.inflate(R.layout.input_emoji_view, null);
        viewPager = (ViewPager) LinearLayoutemoji.findViewById(R.id.viewpager);
        linearLayoutbottom = (LinearLayout) LinearLayoutemoji.findViewById(R.id.viewpager_mark);
        imageViewListmark = new ArrayList<>();
        viewList = new ArrayList<>();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int imglen = (width - 20 - 8 * 30) / 9;

        pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
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
                return viewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);


            }

        };

        try {
            InputStream inputStream = context.getAssets().open("emoji/emoji.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String jsonarry = new String(buffer);
            inputStream.close();
            emojijsonlist = new JSONArray(jsonarry);
            float pages = (float) emojijsonlist.length() / (float) 26;
            if (pages > (int) pages)
                pages = (int) pages + 1;
            else
                pages = (int) pages;

            int idnex = 0;
            for (int p = 0; p < pages; p++) {
                View view = LayoutInflater.from(context).inflate(R.layout.emoji_viewpager, null);
                GridLayout gridLayout = (GridLayout) view.findViewById(R.id.emojigrid);

                JSONObject jsonObject;
                for (int i = 0; i < 26; i++) {

                    ImageView imageView = new ImageView(context);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(imglen, imglen);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(layoutParams);
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
                    params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
                    params.setMargins(10, 20, 10, 20);
                    imageView.setLayoutParams(params);
                    gridLayout.addView(imageView);
                    if (idnex >= emojijsonlist.length()) {
                        continue;
                    }
                    jsonObject = emojijsonlist.getJSONObject(idnex);
                    String emojipath = String.format("emoji/%1$s.png", jsonObject.getString("emojifile"));

                    inputStream = context.getAssets().open(emojipath);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    imageView.setImageBitmap(bitmap);
                    imageView.setClickable(true);
                    imageView.setBackground(context.getResources().getDrawable(R.drawable.chat_meoji_delete_selector));
                    imageView.setOnClickListener(onClickListeneremoji);
                    imageView.setTag(idnex);
                    idnex++;
                }
                gridLayout.addView(getImgDelete(imglen));

                ImageView imageViewmark = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(7, 2, 7, 2);
                imageViewmark.setLayoutParams(params);
                imageViewmark.setBackground(context.getResources().getDrawable(R.drawable.viewpager_mark_normal));
                linearLayoutbottom.addView(imageViewmark);
                imageViewListmark.add(imageViewmark);
                viewList.add(view);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(onPageChangeListener);
        ImageView imageView = imageViewListmark.get(0);
        imageView.setBackground(context.getResources().getDrawable(R.drawable.viewpager_mark_selected));
    }


    /**
     * 表情按钮
     */
    View.OnClickListener onClickListeneremoji = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                JSONObject jsonObject;
                jsonObject = emojijsonlist.getJSONObject(((Integer) view.getTag()));
                Log.i("tag", jsonObject.getString("emojiwildcard"));
                iChat.OnEmoji(jsonObject.getString("emojiwildcard"),jsonObject.getString("emojifile"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * 删除按钮
     */
    View.OnClickListener onClickListenerdelete = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            iChat.Ondelete();
        }
    };

    /**
     * 表情滚动
     */
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            for (int p = 0; p < pagerAdapter.getCount(); p++) {
                ImageView imageView = imageViewListmark.get(p);
                if (p == i)
                    imageView.setBackground(context.getResources().getDrawable(R.drawable.viewpager_mark_selected));
                else
                    imageView.setBackground(context.getResources().getDrawable(R.drawable.viewpager_mark_normal));
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    /**
     * 表情中删除按钮
     *
     * @param length
     * @return
     */
    ImageView getImgDelete(int length) {

        ImageView imageView = new ImageView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(length, length);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(layoutParams);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
        params.setMargins(3, 15, 3, 15);
        imageView.setBackground(context.getResources().getDrawable(R.drawable.chat_meoji_delete_selector));
        imageView.setLayoutParams(params);
        imageView.setClickable(true);
        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.chat_emj_delete));
        imageView.setOnClickListener(onClickListenerdelete);
        return imageView;
    }

    /**
     * 初始化
     */
    void initMoreView() {
        linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.input_more_view, null);
        item1 = (LinearLayout) linearLayout.findViewById(R.id.item_picture);
        item2 = (LinearLayout) linearLayout.findViewById(R.id.item_camera);
        item3 = (LinearLayout) linearLayout.findViewById(R.id.item_createmeetting);
        item4 = (LinearLayout) linearLayout.findViewById(R.id.item_createvote);

        item5 = (LinearLayout) linearLayout.findViewById(R.id.item_Sign);
        item6 = (LinearLayout) linearLayout.findViewById(R.id.item_sendgps);
        item7 = (LinearLayout) linearLayout.findViewById(R.id.item_mpty1);
        item8 = (LinearLayout) linearLayout.findViewById(R.id.item_mpty2);

        item3.setVisibility(View.INVISIBLE);
        item4.setVisibility(View.INVISIBLE);

        item7.setVisibility(View.INVISIBLE);
        item8.setVisibility(View.INVISIBLE);

        selectitem1 = (RelativeLayout) item1.findViewById(R.id.selectitem);
        selectitem1.setOnClickListener(onClickListenerpicture);
        selectitem2 = (RelativeLayout) item2.findViewById(R.id.selectitem);
        selectitem2.setOnClickListener(onClickListenercamera);
        selectitem3 = (RelativeLayout) item3.findViewById(R.id.selectitem);
        selectitem3.setOnClickListener(onClickListenermeeting);
        selectitem4 = (RelativeLayout) item4.findViewById(R.id.selectitem);
        selectitem4.setOnClickListener(onClickListenervode);
        selectitem5 = (RelativeLayout) item5.findViewById(R.id.selectitem);
        selectitem5.setOnClickListener(onClickListenersign);
        selectitem6 = (RelativeLayout) item6.findViewById(R.id.selectitem);
        selectitem6.setOnClickListener(onClickListenergps);

//        selectitem7 = (RelativeLayout) item7.findViewById(R.id.selectitem);
//        selectitem7.setOnClickListener(onClickListenervode);
//        selectitem8 = (RelativeLayout) item8.findViewById(R.id.selectitem);
//        selectitem8.setOnClickListener(onClickListenergps);


        ((ImageView) item1.findViewById(R.id.item_img)).
                setBackground(context.getResources().getDrawable(R.drawable.input_picture));
        ((ImageView) item2.findViewById(R.id.item_img)).
                setBackground(context.getResources().getDrawable(R.drawable.input_camera));
        ((ImageView) item3.findViewById(R.id.item_img)).
                setBackground(context.getResources().getDrawable(R.drawable.input_meetting));
        ((ImageView) item4.findViewById(R.id.item_img)).
                setBackground(context.getResources().getDrawable(R.drawable.input_vode));
        ((ImageView) item5.findViewById(R.id.item_img)).
                setBackground(context.getResources().getDrawable(R.drawable.input_sign));
        ((ImageView) item6.findViewById(R.id.item_img)).
                setBackground(context.getResources().getDrawable(R.drawable.input_gps));

        ((TextView) item1.findViewById(R.id.item_title)).setText("照片");
        ((TextView) item2.findViewById(R.id.item_title)).setText("拍照");
        ((TextView) item3.findViewById(R.id.item_title)).setText("发起会议");
        ((TextView) item4.findViewById(R.id.item_title)).setText("发起投票");
        ((TextView) item5.findViewById(R.id.item_title)).setText("签到");
        ((TextView) item6.findViewById(R.id.item_title)).setText("发送位置");

    }


    View.OnClickListener onClickListenerpicture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            iChat.OnMoreItem(ChatKeyType.PICTURE);
        }
    };
    View.OnClickListener onClickListenercamera = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            iChat.OnMoreItem(ChatKeyType.CAMERA);
        }
    };

    View.OnClickListener onClickListenermeeting = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            iChat.OnMoreItem(ChatKeyType.MEETTING);
        }
    };
    View.OnClickListener onClickListenervode = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            iChat.OnMoreItem(ChatKeyType.VODE);
        }
    };
    View.OnClickListener onClickListenergps = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            iChat.OnMoreItem(ChatKeyType.GPSLOCATION);
        }
    };

    View.OnClickListener onClickListenersign = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            iChat.OnMoreItem(ChatKeyType.SIGN);
        }
    };
}
