package com.dong.video.ui.listplay;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.dong.video.R;
import com.dong.video.adapter.VideoListPagerAdapter;
import com.dong.video.base.ISPayer;
import com.dong.video.base.OnHandleListener;
import com.dong.video.play.DataInter;
import com.dong.video.play.ListPlayer;
import com.dong.video.ui.fragment.VideoListFragment;
import com.dong.video.utils.OrientationSensor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/29
 * Time: 15:57
 */
public class MultiListActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private FrameLayout mFullScreenContainer;

    private boolean isLandScape;
    private boolean toDetail;

    private OrientationSensor mOrientationSensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_multi_list);

        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        mFullScreenContainer = findViewById(R.id.fullScreenContainer);

        mTabLayout.setTabTextColors(Color.BLACK, Color.BLUE);
        mTabLayout.setSelectedTabIndicatorColor(Color.BLUE);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ListPlayer.get().stop();
                ListPlayer.get().setmPlayPageIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        List<VideoListFragment> fragments = new ArrayList<>();
        fragments.add(VideoListFragment.create(0));
        fragments.add(VideoListFragment.create(1));
        fragments.add(VideoListFragment.create(2));

        VideoListPagerAdapter pagerAdapter = new VideoListPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(pagerAdapter);

        mOrientationSensor = new OrientationSensor(this, onOrientationListener);
        mOrientationSensor.enable();
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isLandScape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        mFullScreenContainer.setBackgroundColor(isLandScape?Color.BLACK:Color.TRANSPARENT);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            ListPlayer.get().setReceiverConfigState(this,ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE);
            ListPlayer.get().attachContainer(mFullScreenContainer,false);
        }
        ListPlayer.get().updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, isLandScape);
        ListPlayer.get().updateGroupValue(DataInter.Key.KEY_IS_LANDSCAPE, isLandScape);
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
    protected void onDestroy() {
        super.onDestroy();
        mOrientationSensor.disable();
        ListPlayer.get().destroy();
    }

    @Override
    public void onBackPressed() {
        if (isLandScape){
            toggleScreen();
            return;
        }
        super.onBackPressed();

    }

    private void toggleScreen() {
        setRequestedOrientation(isLandScape ?
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

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
}
