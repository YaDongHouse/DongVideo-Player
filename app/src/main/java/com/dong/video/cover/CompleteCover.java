package com.dong.video.cover;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dong.video.R;
import com.dong.video.play.DataInter;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseCover;

/**
 * @packInfo:com.dong.video.cover
 * @author: yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/14
 * Time: 10:52
 */
public class CompleteCover extends BaseCover {

    private TextView mReplay;

    public CompleteCover(Context context) {
        super(context);
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        if (getGroupValue().getBoolean(DataInter.Key.KEY_COMPLETE_SHOW)){
            setPlayCompleteState(true);
        }
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        setCoverVisibility(View.GONE);
    }

    @Override
    protected View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_cover_complete,null);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        mReplay = findViewById(R.id.tv_replay);
        mReplay.setOnClickListener(mOnclickListener);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
                setPlayCompleteState(false);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                setPlayCompleteState(true);
                break;
                default:
        }
    }

    @Override
    public int getCoverLevel() {
        return levelMedium(20);
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    private View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_replay:
                    requestReplay(null);
                    break;
            }
            setPlayCompleteState(false);
        }
    };

    private void setPlayCompleteState(boolean state){
        setCoverVisibility(state?View.VISIBLE:View.GONE);
        getGroupValue().putBoolean(DataInter.Key.KEY_COMPLETE_SHOW,state);
    }

}
