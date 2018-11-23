package com.dong.video.dCover;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dong.video.R;
import com.dong.video.play.DataInter;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseCover;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @packInfo:com.dong.video.dCover
 * @author: yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/20
 * Time: 17:24
 */
public class DCompleteCover extends BaseCover {

    @BindView(R.id.tv_replay)
    TextView tvReplay;
    private Unbinder unbinder;

    public DCompleteCover(Context context) {
        super(context);
    }

    @OnClick({R.id.tv_replay})
    public void onViewClick(View view){
        Log.d(LogTag.TAG, "播放完成重新播放 ");
        requestReplay(null);
        setPlayCompleteState(false);
    }

    private void setPlayCompleteState(boolean state){
        setCoverVisibility(state? View.VISIBLE:View.GONE);
        Log.d(LogTag.TAG, "播放完成页面 setPlayCompleteState:state"+state);
        getGroupValue().putBoolean(DataInter.Key.KEY_COMPLETE_SHOW,state);
    }

    @Override
    protected View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_cover_complete, null);
    }

    @Override
    public int getCoverLevel() {
        return levelMedium(20);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        unbinder = ButterKnife.bind(this, getView());
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        unbinder.unbind();
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                Log.d(LogTag.TAG, "onPlayerEvent 播放完成页面，onPlayerEvent:接到设置数据源事件 ");
                setPlayCompleteState(false);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
                Log.d(LogTag.TAG, "onPlayerEvent 播放完成页面，onPlayerEvent:接到开始播放事件 ");
                setPlayCompleteState(false);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                Log.d(LogTag.TAG, "onPlayerEvent 播放完成页面，onPlayerEvent:接到播放完成事件 ");
                setPlayCompleteState(true);
                break;
                default:
        }


    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }
}
