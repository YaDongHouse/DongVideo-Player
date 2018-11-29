package com.dong.video.base;

import android.view.ViewGroup;

import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.provider.IDataProvider;
import com.kk.taurus.playerbase.receiver.GroupValue;
import com.kk.taurus.playerbase.receiver.IReceiver;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;

/**
 * @author yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/27
 * Time: 11:02
 */
public interface ISPayer {


    int RECEIVER_GROUP_CONFIG_LIST_STATE = 2;
    int RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE = 3;
    int RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE = 4;
    int RECEIVER_GROUP_CONFIG_SILENCE_STATE = 1;


    /**
     * 增加播放事件监听
     * @param onPlayerEventListener
     */
    void addOnPlayerEventListener(OnPlayerEventListener onPlayerEventListener);

    /**
     * 移除播放事件监听
     * @param onPlayerEventListener
     * @return
     */
    boolean removePlayerEventListener(OnPlayerEventListener onPlayerEventListener);

    /**
     * 增加播放错误事件监听
     * @param onErrorEventListener
     */
    void addOnErrorEventListener(OnErrorEventListener onErrorEventListener);

    /**
     * 移除播放错误事件监听
     * @param onErrorEventListener
     * @return
     */
    boolean removeErrorEventListener(OnErrorEventListener onErrorEventListener);

    /**
     * 增加接受事件监听
     * @param onReceiverEventListener
     */
    void addOnReceiverEventListener(OnReceiverEventListener onReceiverEventListener);

    /**
     * 移除接受事件监听
     * @param onReceiverEventListener
     * @return
     */
    boolean removeReceiverEventListener(OnReceiverEventListener onReceiverEventListener);


    /**
     * 设置接收器，一般用来设置覆盖层（ControllerCover，ErrorCover等等）
     * @param receiverGroup
     */
    void setReceiverGroup(IReceiverGroup receiverGroup);

    /**
     * 获取设置的接收器
     * @return
     */
    IReceiverGroup getReceiverGroup();

    /**
     * 根据指的key移除接收器
     * @param receiverKey
     */
    void removeReceiver(String receiverKey);

    /**
     * 根据指定的key增加接收器
     * @param key
     * @param receiver
     */
    void addReceiver(String key, IReceiver receiver);

    /**
     * 为播放绑定View
     * @param userContainer
     */
    void attachContainer(ViewGroup userContainer);

    /**
     * 设置私有数据提供者
     * @param dataProvider
     */
    void setDataProvider(IDataProvider dataProvider);

    /**
     * 获取共享数据
     * @return
     */
    GroupValue getGroupValue();

    /**
     * 更新共享数据
     * @param key
     * @param value
     */
    void updateGroupValue(String key,Object value);

    /**
     * 注册共享数据变化监听器
     * @param onGroupValueUpdateListener
     */
    void registerOnGroupValueUpdateListener(IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener);

    /**
     * 反注册共享数据变化监听器
     * @param onGroupValueUpdateListener
     */
    void unregisterOnGroupValueUpdateListener(IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener);


    /**
     * 开始播放
     * @param dataSource
     */
    void play(DataSource dataSource);

    /**
     * 开始播放
     * @param dataSource
     * @param updateRender
     */
    void play(DataSource dataSource,boolean updateRender);

    /**
     * 查看是否正在播放
     * @return
     */
    boolean isInPlaybackState();

    /**
     * 是否正在播放
     * @return
     */
    boolean isPlaying();

    /**
     * 获取播放位置
     * @return
     */
    int getCurrentPosition();

    /**
     * 获取播放状态
     * @return
     */
    int getState();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 继续播放
     */
    void resume();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 重新播放
     */
    void reset();

    /**
     * 指定位置的重新播放
     */
    void rePlay(int position);

    /**
     * 释放资源
     */
    void destroy();


}
