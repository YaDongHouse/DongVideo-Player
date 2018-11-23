package com.dong.video.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dong.video.App;
import com.dong.video.R;
import com.dong.video.adapter.OnItemClickListener;
import com.dong.video.adapter.SettingAdapter;
import com.dong.video.bean.SettingItem;
import com.dong.video.cover.ControllerCover;
import com.dong.video.play.DataInter;
import com.dong.video.play.ReceiverGroupManager;
import com.dong.video.utils.PUtil;
import com.kk.taurus.playerbase.assist.InterEvent;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.IReceiver;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.render.AspectRatio;
import com.kk.taurus.playerbase.render.IRender;
import com.kk.taurus.playerbase.widget.BaseVideoView;

/**
 * @packInfo:com.dong.video.ui
 * @author: yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/14
 * Time: 10:27
 */
public class BaseVideoViewActivity extends AppCompatActivity implements OnPlayerEventListener, OnItemClickListener<SettingAdapter.SettingItemHolder,SettingItem> {


    private BaseVideoView mVideoView;
    private ReceiverGroup mReceiverGroup;

    private boolean userPause;
    private boolean isLandscape;
    private int margin;

    private boolean hasStart;
    private RecyclerView mRecycler;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_video);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mVideoView = findViewById(R.id.baseVideoView);
        mRecycler = findViewById(R.id.setting_recycler);

        margin = PUtil.dip2px(this,2);

        updateVideo(false);

        mReceiverGroup = ReceiverGroupManager.getInstance().getReceiverGroup(this);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
        mVideoView.setReceiverGroup(mReceiverGroup);
        mVideoView.setEventHandler(onVideoViewEventHandler);
        mVideoView.setOnPlayerEventListener(this);
    }

    private void updateVideo(boolean landscape){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        if (landscape){
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }else {
            layoutParams.width = PUtil.getScreenW(this)-(margin*2);
            layoutParams.height = layoutParams.width * 3/4;
            layoutParams.setMargins(margin,margin,margin,margin);
        }
        mVideoView.setLayoutParams(layoutParams);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
                mRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
                SettingAdapter adapter = new SettingAdapter(this,SettingItem.initSettingList());
                adapter.setOnItemClickListener(this);
                mRecycler.setAdapter(adapter);
                break;
                default:
        }
    }


    private OnVideoViewEventHandler onVideoViewEventHandler  = new OnVideoViewEventHandler(){
        @Override
        public void onAssistHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
            super.onAssistHandle(assist, eventCode, bundle);
            switch (eventCode){
                case InterEvent.CODE_REQUEST_PAUSE:
                    userPause = true;
                    break;
                    case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                        if (isLandscape){
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }else {
                            finish();
                        }

                case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                    setRequestedOrientation(isLandscape ?
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                    mVideoView.stop();
                    break;
                    default:
            }
        }
    };


    @Override
    public void onItemClick(SettingAdapter.SettingItemHolder holder, SettingItem item, int position) {
        int code = item.getCode();
        switch (code){
            case SettingItem.CODE_RENDER_SURFACE_VIEW:
                mVideoView.setRenderType(IRender.RENDER_TYPE_SURFACE_VIEW);
                break;
            case SettingItem.CODE_RENDER_TEXTURE_VIEW:
                mVideoView.setRenderType(IRender.RENDER_TYPE_TEXTURE_VIEW);
                break;
            case SettingItem.CODE_STYLE_ROUND_RECT:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    mVideoView.setRoundRectShape(PUtil.dip2px(this,25));
                }else{
                    Toast.makeText(this, "not support", Toast.LENGTH_SHORT).show();
                }
                break;
            case SettingItem.CODE_STYLE_OVAL_RECT:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    mVideoView.setOvalRectShape();
                }else{
                    Toast.makeText(this, "not support", Toast.LENGTH_SHORT).show();
                }
                break;
            case SettingItem.CODE_STYLE_RESET:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    mVideoView.clearShapeStyle();
                }else{
                    Toast.makeText(this, "not support", Toast.LENGTH_SHORT).show();
                }
                break;
            case SettingItem.CODE_ASPECT_16_9:
                mVideoView.setAspectRatio(AspectRatio.AspectRatio_16_9);
                break;
            case SettingItem.CODE_ASPECT_4_3:
                mVideoView.setAspectRatio(AspectRatio.AspectRatio_4_3);
                break;
            case SettingItem.CODE_ASPECT_FILL:
                mVideoView.setAspectRatio(AspectRatio.AspectRatio_FILL_PARENT);
                break;
            case SettingItem.CODE_ASPECT_MATCH:
                mVideoView.setAspectRatio(AspectRatio.AspectRatio_MATCH_PARENT);
                break;
            case SettingItem.CODE_ASPECT_FIT:
                mVideoView.setAspectRatio(AspectRatio.AspectRatio_FIT_PARENT);
                break;
            case SettingItem.CODE_ASPECT_ORIGIN:
                mVideoView.setAspectRatio(AspectRatio.AspectRatio_ORIGIN);
                break;
            case SettingItem.CODE_PLAYER_MEDIA_PLAYER:
                if(mVideoView.switchDecoder(PlayerConfig.DEFAULT_PLAN_ID)){
                    replay();
                }
                break;
            case SettingItem.CODE_PLAYER_IJK_PLAYER:
                if(mVideoView.switchDecoder(App.PLAN_ID_IJK)){
                    replay();
                }
                break;
            case SettingItem.CODE_PLAYER_EXO_PLAYER:
                if(mVideoView.switchDecoder(App.PLAN_ID_EXO)){
                    replay();
                }
                break;
            case SettingItem.CODE_SPEED_0_5:
                mVideoView.setSpeed(0.5f);
                break;
            case SettingItem.CODE_SPEED_2:
                mVideoView.setSpeed(2f);
                break;
            case SettingItem.CODE_SPEED_1:
                mVideoView.setSpeed(1f);
                break;
            case SettingItem.CODE_CONTROLLER_REMOVE:
                mReceiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER);
                Toast.makeText(this, "已移除", Toast.LENGTH_SHORT).show();
                break;
            case SettingItem.CODE_CONTROLLER_RESET:
                IReceiver receiver = mReceiverGroup.getReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER);
                if(receiver==null){
                    mReceiverGroup.addReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER, new ControllerCover(this));
                    Toast.makeText(this, "已添加", Toast.LENGTH_SHORT).show();
                }
                break;
                default:
        }
    }
    private void replay(){
        mVideoView.setDataSource(new DataSource());
        mVideoView.start();
    }


    @Override
    public void onBackPressed() {
        if(isLandscape){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            isLandscape = true;
            updateVideo(true);
        }else{
            isLandscape = false;
            updateVideo(false);
        }
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandscape);
    }

    @Override
    protected void onPause() {
        super.onPause();
        int state = mVideoView.getState();
        if(state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        if(mVideoView.isInPlaybackState()){
            mVideoView.pause();
        }else{
            mVideoView.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int state = mVideoView.getState();
        if(state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        if(mVideoView.isInPlaybackState()){
            if(!userPause)
                mVideoView.resume();
        }else{
            mVideoView.rePlay(0);
        }
        initPlay();
    }

    private void initPlay() {
        if(!hasStart){
            mVideoView.setDataSource(new DataSource(DataUtils.VIDEO_URL_01));
            mVideoView.start();
            hasStart = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }
}
