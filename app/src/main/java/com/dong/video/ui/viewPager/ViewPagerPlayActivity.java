package com.dong.video.ui.viewPager;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dong.video.R;
import com.dong.video.bean.VideoBean;
import com.dong.video.ui.BaseVideoViewActivity;
import com.dong.video.ui.DataUtils;
import com.jiajunhui.xapp.medialoader.MediaLoader;
import com.jiajunhui.xapp.medialoader.bean.VideoItem;
import com.jiajunhui.xapp.medialoader.bean.VideoResult;
import com.jiajunhui.xapp.medialoader.callback.OnVideoLoaderCallBack;
import com.kk.taurus.playerbase.assist.RelationAssist;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.log.PLog;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * @author yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/26
 * Time: 17:27
 */
public class ViewPagerPlayActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TextView mTvTitle;
    private RelationAssist mRelationAssist;

    private List<VideoBean> mItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_play);
        mViewPager = findViewById(R.id.viewPager);
        mTvTitle = findViewById(R.id.tv_title_darkMode);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        findViewById(R.id.iv_back_darkMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRelationAssist = new RelationAssist(this);
        mRelationAssist.setOnPlayerEventListener(new OnPlayerEventListener() {
            @Override
            public void onPlayerEvent(int eventCode, Bundle bundle) {
                switch (eventCode){
                    case OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE:
                        if(bundle!=null){
                            PLog.d("timerUpdate","curr = " + bundle.getInt(EventKey.INT_ARG1) + ",duration = " + bundle.getInt(EventKey.INT_ARG2));
                        }
                        break;
                }
            }
        });
        mRelationAssist.getSuperContainer().setBackgroundColor(Color.BLACK);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                playPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request();
    }

    private void playPosition(int position) {
        VideoBean bean = mItems.get(position);
        mTvTitle.setText(bean.getDisplayName());
        FrameLayout container = mViewPager.findViewWithTag(bean.getPath());
        mRelationAssist.attachContainer(container, true);
        mRelationAssist.setDataSource(new DataSource(bean.getPath()));
        mRelationAssist.play();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mRelationAssist.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRelationAssist.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRelationAssist.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void permissionSuccess() {
        MediaLoader.getLoader().loadVideos(this, new OnVideoLoaderCallBack() {
            @Override
            public void onResult(VideoResult result) {
                List<VideoItem> items = result.getItems();


                Collections.sort(items, new MCompartor());
                List<VideoBean> videoBeans = new ArrayList<>();
                videoBeans.add(new VideoBean("神奇的珊瑚",
                        "http://tp.yiaedu.com/showimg.php?url=http://uploads.xuexila.com/allimg/1703/867-1F330164643.jpg",
                        "https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"));

                videoBeans.add(new VideoBean("不想从被子里出来",
                        "http://www.17qq.com/img_qqtouxiang/76490995.jpeg",
                        "https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/12/SD70VQJ74_sd.mp4"));


                mItems.addAll(videoBeans);
                PlayPagerAdapter playPagerAdapter = new PlayPagerAdapter(ViewPagerPlayActivity.this, mItems);
                mViewPager.setAdapter(playPagerAdapter);
                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        playPosition(0);
                    }
                });
            }
        });
    }

    @PermissionFail(requestCode = 100)
    public void permissionFailure() {
        Toast.makeText(this, "权限拒绝，无法正常使用", Toast.LENGTH_SHORT).show();
    }

    public class MCompartor implements Comparator<VideoItem> {

        @Override
        public int compare(VideoItem o1, VideoItem o2) {
            if (o1.getSize() > o2.getSize()) {
                return -1;
            }
            if (o1.getSize() < o2.getSize()) {
                return 1;
            }
            return 0;
        }
    }

}
