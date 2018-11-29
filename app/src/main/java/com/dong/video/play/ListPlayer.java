package com.dong.video.play;

import android.content.Context;
import android.os.Bundle;

import com.dong.video.App;
import com.dong.video.base.BSPlayer;
import com.dong.video.base.OnHandleListener;
import com.dong.video.dCover.DGestureCover;
import com.kk.taurus.playerbase.assist.OnAssistPlayEventHandler;
import com.kk.taurus.playerbase.assist.RelationAssist;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;

/**
 * @author yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/27
 * Time: 11:00
 */
public class ListPlayer extends BSPlayer {

    private static ListPlayer i = null;

    private int mPlayPageIndex = 0;

    private OnHandleListener onHandleListener;


    private ListPlayer() {
    }

    public static ListPlayer get() {
        if (i == null) {
            synchronized (ListPlayer.class) {
                if (i == null) {
                    i = new ListPlayer();
                }
            }
        }
        return i;
    }

    public void setReceiverConfigState(Context context, int configState) {
        IReceiverGroup receiverGroup = getReceiverGroup();
        if (receiverGroup == null) {
            setReceiverGroup(ReceiverGroupManager.getInstance().getLiteReceiverGroup(context));
        }

        switch (configState) {
            //是列表时移除手势覆盖页，防止与滑动冲突
            case RECEIVER_GROUP_CONFIG_LIST_STATE:
                removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
                break;
            //进入详情页面时增加手势覆盖页
            case RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE:
                //进入全屏播放时增加手势覆盖页
            case RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE:
                addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, new DGestureCover(context));
                break;
            default:
        }

    }


    @Override
    protected RelationAssist onCreateRelationAssist() {
        RelationAssist assist = new RelationAssist(App.get().getApplicationContext());
        assist.setEventAssistHandler(new OnAssistPlayEventHandler());
        return assist;
    }


    @Override
    protected void onInit() {

    }

    @Override
    protected void onSetDataSource(DataSource dataSource) {

    }

    @Override
    protected void onCallBackPlayerEvent(int eventCode, Bundle bundle) {

    }

    @Override
    protected void onCallBackErrorEvent(int eventCode, Bundle bundle) {

    }

    /**
     * 接受到事件的首个处理这
     *
     * @param eventCode
     * @param bundle
     */
    @Override
    protected void onCallBackReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            //返回事件
            case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                if (onHandleListener != null) {
                    onHandleListener.onBack();
                }
                break;
            //全屏切换
            case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                if (onHandleListener != null) {
                    onHandleListener.onToggleScreen();
                }
                break;
            //错误页面事件
            case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                reset();
                break;
            default:
        }
    }


    @Override
    public void destroy() {
        super.destroy();
        i = null;
        onHandleListener = null;
    }

    public void setOnHandleListener(OnHandleListener onHandleListener) {
        this.onHandleListener = onHandleListener;
    }

    public int getmPlayPageIndex() {
        return mPlayPageIndex;
    }

    public void setmPlayPageIndex(int mPlayPageIndex) {
        this.mPlayPageIndex = mPlayPageIndex;
    }
}
