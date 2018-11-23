package com.dong.video.dCover;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dong.video.R;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;

/**
 * @packInfo:com.dong.video.dCover
 * @author: yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/21
 * Time: 9:45
 */
public class DLoadingCover extends BaseCover {

    public DLoadingCover(Context context) {
        super(context);
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        if (playerStateGetter != null && isInPlaybackState(playerStateGetter)) {
            Log.d(TAG, "onCoverAttachedToWindow 等待页面:playState-" + playerStateGetter.getState() + "isBuffering-" + playerStateGetter.isBuffering());
            setLoadingState(playerStateGetter.isBuffering());
        }
    }

    private boolean isInPlaybackState(PlayerStateGetter playerStateGetter) {
        int state = playerStateGetter.getState();
        return state != IPlayer.STATE_END
                && state != IPlayer.STATE_ERROR
                && state != IPlayer.STATE_IDLE
                && state != IPlayer.STATE_INITIALIZED
                && state != IPlayer.STATE_STOPPED;
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START
                || eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET
                || eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START
                || eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO) {
            Log.d(TAG, "onPlayerEvent:等待页面 接受到播放事件：-展示" + eventCode);
            setLoadingState(true);
            return;
        }
        if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START
                || eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END
                || eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_STOP
                || eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_ERROR
                || eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE) {
            Log.d(TAG, "onPlayerEvent:等待页面 接受到播放事件：-隐藏" + eventCode);
            setLoadingState(false);
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {
        Log.d(TAG, "onErrorEvent:等待界面 ");
        setLoadingState(false);
    }

    private static final String TAG = LogTag.TAG;

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }


    private void setLoadingState(boolean show) {
        setCoverVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_loading_cover, null);
    }

    @Override
    public int getCoverLevel() {
        return levelMedium(1);
    }

}
