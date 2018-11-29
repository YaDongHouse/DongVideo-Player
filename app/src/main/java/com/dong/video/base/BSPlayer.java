package com.dong.video.base;

import android.os.Bundle;
import android.view.ViewGroup;

import com.kk.taurus.playerbase.assist.RelationAssist;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.provider.IDataProvider;
import com.kk.taurus.playerbase.receiver.GroupValue;
import com.kk.taurus.playerbase.receiver.IReceiver;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/27
 * Time: 11:01
 */
public abstract class BSPlayer implements ISPayer {
    private RelationAssist mRelationAssist;

    private List<OnPlayerEventListener> mOnPlayerEventListeners;
    private List<OnErrorEventListener> mOnErrorEventListeners;
    private List<OnReceiverEventListener> mOnReceiverEventListeners;

    public BSPlayer() {
        mRelationAssist = onCreateRelationAssist();
        mOnPlayerEventListeners = new ArrayList<>();
        mOnErrorEventListeners = new ArrayList<>();
        mOnReceiverEventListeners = new ArrayList<>();
        onInit();
    }

    @Override
    public void addOnPlayerEventListener(OnPlayerEventListener onPlayerEventListener) {
        if (mOnPlayerEventListeners.contains(onPlayerEventListener)){
            return;
        }
        mOnPlayerEventListeners.add(onPlayerEventListener);
    }

    @Override
    public boolean removePlayerEventListener(OnPlayerEventListener onPlayerEventListener) {
        return mOnPlayerEventListeners.remove(onPlayerEventListener);
    }

    @Override
    public void addOnErrorEventListener(OnErrorEventListener onErrorEventListener) {
        if (mOnErrorEventListeners.contains(onErrorEventListener)){
            return;
        }
        mOnErrorEventListeners.add(onErrorEventListener);
    }

    @Override
    public boolean removeErrorEventListener(OnErrorEventListener onErrorEventListener) {
        return mOnErrorEventListeners.remove(onErrorEventListener);
    }

    @Override
    public void addOnReceiverEventListener(OnReceiverEventListener onReceiverEventListener) {
        if (mOnReceiverEventListeners.contains(onReceiverEventListener)){
            return;
        }
        mOnReceiverEventListeners.add(onReceiverEventListener);
    }

    @Override
    public boolean removeReceiverEventListener(OnReceiverEventListener onReceiverEventListener) {
        return mOnReceiverEventListeners.remove(onReceiverEventListener);
    }

    @Override
    public GroupValue getGroupValue() {
        IReceiverGroup receiverGroup = getReceiverGroup();
        return receiverGroup == null ? null : receiverGroup.getGroupValue();
    }

    @Override
    public void updateGroupValue(String key, Object value) {
        GroupValue groupValue = getGroupValue();
        if (groupValue != null) {
            groupValue.putObject(key, value);
        }
    }

    @Override
    public void registerOnGroupValueUpdateListener(IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener) {
        GroupValue groupValue = getGroupValue();
        if (groupValue != null) {
            groupValue.registerOnGroupValueUpdateListener(onGroupValueUpdateListener);
        }
    }

    @Override
    public void unregisterOnGroupValueUpdateListener(IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener) {
        GroupValue groupValue = getGroupValue();
        if (groupValue != null) {
            getGroupValue().unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener);
        }
    }

    @Override
    public void setReceiverGroup(IReceiverGroup receiverGroup) {
        mRelationAssist.setReceiverGroup(receiverGroup);
    }

    @Override
    public IReceiverGroup getReceiverGroup() {
        return mRelationAssist.getReceiverGroup();
    }

    @Override
    public void removeReceiver(String receiverKey) {
        IReceiverGroup receiverGroup = getReceiverGroup();
        if (receiverGroup != null) {
            receiverGroup.removeReceiver(receiverKey);
        }
    }

    @Override
    public void addReceiver(String key, IReceiver receiver) {
        IReceiverGroup receiverGroup = getReceiverGroup();
        if (receiverGroup == null){
            return;
        }
        IReceiver iReceiver = receiverGroup.getReceiver(key);
        if (iReceiver == null){
            return;
        }
        receiverGroup.addReceiver(key, receiver);
    }

    @Override
    public void attachContainer(ViewGroup userContainer) {
        attachContainer(userContainer, true);
    }

    public void attachContainer(ViewGroup userContainer, boolean updateRender) {
        mRelationAssist.attachContainer(userContainer, updateRender);
    }

    @Override
    public void play(DataSource dataSource) {
        play(dataSource, false);
    }

    @Override
    public void play(DataSource dataSource, boolean updateRender) {
        onSetDataSource(dataSource);
        attachListener();
        stop();
        mRelationAssist.setDataSource(dataSource);
        mRelationAssist.play(updateRender);
    }

    public void attachListener() {
        mRelationAssist.setOnPlayerEventListener(mInternalPlayerEventListener);
        mRelationAssist.setOnErrorEventListener(mInternalErrorEventListener);
        mRelationAssist.setOnReceiverEventListener(mInternalReceiverEventListener);
    }


    @Override
    public void setDataProvider(IDataProvider dataProvider) {
        mRelationAssist.setDataProvider(dataProvider);
    }

    @Override
    public boolean isInPlaybackState() {
        return mRelationAssist.isInPlaybackState();
    }

    @Override
    public boolean isPlaying() {
        return mRelationAssist.isPlaying();
    }

    @Override
    public int getCurrentPosition() {
        return mRelationAssist.getCurrentPosition();
    }

    @Override
    public int getState() {
        return mRelationAssist.getState();
    }

    @Override
    public void pause() {
        mRelationAssist.pause();
    }

    @Override
    public void resume() {
        mRelationAssist.resume();
    }

    @Override
    public void stop() {
        mRelationAssist.stop();
    }

    @Override
    public void reset() {
        mRelationAssist.reset();
    }

    @Override
    public void rePlay(int position) {
        mRelationAssist.rePlay(position);
    }

    @Override
    public void destroy() {
        mOnPlayerEventListeners.clear();
        mOnErrorEventListeners.clear();
        mOnReceiverEventListeners.clear();
        IReceiverGroup receiverGroup = getReceiverGroup();
        if (receiverGroup != null) {
            receiverGroup.clearReceivers();
        }
        mRelationAssist.destroy();
    }

    private OnPlayerEventListener mInternalPlayerEventListener =
            new OnPlayerEventListener() {
                @Override
                public void onPlayerEvent(int eventCode, Bundle bundle) {
                    onCallBackPlayerEvent(eventCode, bundle);
                    callBackPlayerEventListeners(eventCode, bundle);

                }
            };

    private void callBackPlayerEventListeners(int eventCode, Bundle bundle) {
        for (OnPlayerEventListener mOnPlayerEventListener : mOnPlayerEventListeners) {
            mOnPlayerEventListener.onPlayerEvent(eventCode, bundle);
        }
    }

    private OnErrorEventListener mInternalErrorEventListener =
            new OnErrorEventListener() {
                @Override
                public void onErrorEvent(int eventCode, Bundle bundle) {
                    onCallBackErrorEvent(eventCode, bundle);
                    callBackErrorEventListeners(eventCode, bundle);

                }
            };

    private void callBackErrorEventListeners(int eventCode, Bundle bundle) {
        for (OnErrorEventListener mOnErrorEventListener : mOnErrorEventListeners) {
            mOnErrorEventListener.onErrorEvent(eventCode, bundle);
        }
    }


    private OnReceiverEventListener mInternalReceiverEventListener =
            new OnReceiverEventListener() {
                @Override
                public void onReceiverEvent(int eventCode, Bundle bundle) {
                    onCallBackReceiverEvent(eventCode, bundle);
                    callBackReceiverEventListeners(eventCode, bundle);
                }
            };

    private void callBackReceiverEventListeners(int eventCode, Bundle bundle) {
        for (OnReceiverEventListener mOnReceiverEventListener : mOnReceiverEventListeners) {
            mOnReceiverEventListener.onReceiverEvent(eventCode, bundle);
        }
    }


    protected abstract RelationAssist onCreateRelationAssist();

    protected abstract void onInit();

    protected abstract void onSetDataSource(DataSource dataSource);

    protected abstract void onCallBackPlayerEvent(int eventCode, Bundle bundle);

    protected abstract void onCallBackErrorEvent(int eventCode, Bundle bundle);

    protected abstract void onCallBackReceiverEvent(int eventCode, Bundle bundle);
}
