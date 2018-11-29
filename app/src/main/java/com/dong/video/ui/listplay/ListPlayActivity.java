package com.dong.video.ui.listplay;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.dong.video.R;
import com.dong.video.adapter.ListAdapter;
import com.dong.video.base.ISPayer;
import com.dong.video.base.OnHandleListener;
import com.dong.video.bean.VideoBean;
import com.dong.video.play.DataInter;
import com.dong.video.play.ListPlayer;
import com.dong.video.ui.DataUtils;
import com.dong.video.utils.OrientationSensor;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.player.IPlayer;

/**
 * @author yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/26
 * Time: 19:02
 */
public class ListPlayActivity extends AppCompatActivity implements ListAdapter.OnListListener {
    private RecyclerView mRecycler;
    private FrameLayout mPlayerContainer;

    private boolean isLandScape;
    private boolean toDetail;
    private OrientationSensor mOrientationSensor;
    private ListAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
        if (getActionBar()!=null) {
            getActionBar().hide();
        }
        //隐藏用于显示信号/网络/事件/电量的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_list);
        mRecycler = findViewById(R.id.recycler);
        mPlayerContainer = findViewById(R.id.listPlayContainer);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(this, DataUtils.getVideoList(), mRecycler);
        mAdapter.setOnListListener(this);
        mRecycler.setAdapter(mAdapter);
        //利用加速度传感器 计算横竖屏
        mOrientationSensor = new OrientationSensor(this, onOrientationListener);
        mOrientationSensor.enable();

    }

    private static final String TAG = "ListPlayActivity";

    private OrientationSensor.OnOrientationListener onOrientationListener =
            new OrientationSensor.OnOrientationListener() {
                @Override
                public void onLandScape(int orientation) {
                    if (ListPlayer.get().isInPlaybackState()) {
                        setRequestedOrientation(orientation);
                    }
                }

                @Override
                public void onPortrait(int orientation) {
                    if (ListPlayer.get().isInPlaybackState()) {
                        setRequestedOrientation(orientation);
                    }
                }
            };


    /**
     * 手动横竖屏切换
     */
    private void toggleScreen() {
        setRequestedOrientation(isLandScape ?
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isLandScape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPlayerContainer.setBackgroundColor(Color.BLACK);
            ListPlayer.get().attachContainer(mPlayerContainer, false);
            ListPlayer.get().setReceiverConfigState(this, ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE);
        } else {
            mPlayerContainer.setBackgroundColor(Color.TRANSPARENT);
            mRecycler.post(new Runnable() {
                @Override
                public void run() {
                    ListAdapter.VideoItemHolder currentHolder = mAdapter.getCurrentHolder();
                    if (currentHolder != null) {
                        ListPlayer.get().attachContainer(currentHolder.layoutContainer, false);
                        ListPlayer.get().setReceiverConfigState(ListPlayActivity.this, ISPayer.RECEIVER_GROUP_CONFIG_LIST_STATE);
                    }
                }
            });
        }
        //更新共享事件 控制页面是否显示头部
        ListPlayer.get().updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, isLandScape);
        //更新共享事件，是否横竖屏展示
        ListPlayer.get().updateGroupValue(DataInter.Key.KEY_IS_LANDSCAPE, isLandScape);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListPlayer.get().updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, isLandScape);
        ListPlayer.get().setOnHandleListener(new OnHandleListener() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onToggleScreen() {
                toggleScreen();
            }
        });
        if (!toDetail && ListPlayer.get().isInPlaybackState()) {
            ListPlayer.get().resume();
        }
        toDetail = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        int state = ListPlayer.get().getState();
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE) {
            return;
        }
        if (!toDetail) {
            ListPlayer.get().pause();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mOrientationSensor.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mOrientationSensor.disable();
    }

    @Override
    public void onBackPressed() {
        if (isLandScape) {
            toggleScreen();
            return;
        }
        super.onBackPressed();

    }

    @Override
    public void onTitleClick(ListAdapter.VideoItemHolder holder, VideoBean item, int position) {
        toDetail = true;
        //TODO
        mAdapter.reset();
    }

    @Override
    public void playItem(ListAdapter.VideoItemHolder holder, VideoBean item, int position) {
        ListPlayer.get().setReceiverConfigState(this, ISPayer.RECEIVER_GROUP_CONFIG_LIST_STATE);
        ListPlayer.get().attachContainer(holder.layoutContainer);
        ListPlayer.get().play(new DataSource(item.getPath()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrientationSensor.disable();
        ListPlayer.get().destroy();
    }
}
