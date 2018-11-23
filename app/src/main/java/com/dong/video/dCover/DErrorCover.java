package com.dong.video.dCover;

import android.app.Activity;
import android.content.Context;
import android.icu.util.ValueIterator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dong.video.App;
import com.dong.video.R;
import com.dong.video.play.DataInter;
import com.dong.video.utils.PUtil;
import com.kk.taurus.playerbase.config.PConst;
import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @packInfo:com.dong.video.dCover
 * @author: yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/20
 * Time: 17:37
 */
public class DErrorCover extends BaseCover {
    final int STATUS_ERROR = -1;
    final int STATUS_UNDEFINE = 0;
    final int STATUS_MOBILE = 1;
    final int STATUS_NETWORK_ERROR = 2;

    @BindView(R.id.tv_error_info)
    TextView tvErrorInfo;
    @BindView(R.id.tv_retry)
    TextView tvRetry;
    private Unbinder unbinder;
    private int mStatus;
    private boolean mErrorShow;
    /**
     * 记录播放进度
     */
    private int mCurrPosition;

    public DErrorCover(Context context) {
        super(context);
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        handleStatusUI(NetworkUtils.getNetworkState(getContext()));
    }

    /**
     * 根据网络类型更新UI
     * @param netWorkState
     */
    private void handleStatusUI(int netWorkState) {
        if (netWorkState <0 ){
            mStatus = STATUS_NETWORK_ERROR;
            setErrorInfo("无网络！");
            setHandleInfo("重试");
            setErrorState(true);
        }else {
            if (netWorkState == PConst.NETWORK_STATE_WIFI){
             if (mErrorShow){
                 setErrorState(false);
             }
            }else {
              if (App.ignoreMobile) {
                  return;
              }
              mStatus = STATUS_MOBILE;
              setErrorInfo("您正在使用移动网络！");
              setHandleInfo("继续");
              setErrorState(true);
            }
        }
    }

    @OnClick({R.id.tv_retry})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.tv_retry:
                handleStatus();
                break;
                default:
        }
    }

    /**
     * 重试
     */
    private void handleStatus(){
        Bundle bundle = BundlePool.obtain();
        bundle.putInt(EventKey.INT_DATA,mCurrPosition);
        switch (mStatus){
            case STATUS_ERROR:
                setErrorState(false);
                requestReplay(bundle);
                break;
            case STATUS_MOBILE:
                App.ignoreMobile = true;
                setErrorState(false);
                requestResume(bundle);
                break;
            case STATUS_NETWORK_ERROR:
                setErrorState(false);
                requestReplay(bundle);
                break;
                default:
        }
    }


    @Override
    public void onProducerData(String key, Object data) {
        super.onProducerData(key, data);
        if (DataInter.Key.KEY_NETWORK_STATE.equals(key)){
            int networkState = (int) data;
            Log.d(TAG, "onProducerData:错误展示页面 接受到网络变化的事件"+networkState);
            if (networkState == PConst.NETWORK_STATE_WIFI
                    && mErrorShow
                    && PUtil.isTopActivity((Activity) getContext())){
                Bundle bundle = BundlePool.obtain();
                bundle.putInt(EventKey.INT_DATA,mCurrPosition);
                requestReplay(bundle);
            }
            handleStatusUI(networkState);
        }
    }

    @Override
    protected View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_error_cover, null);
    }

    @Override
    public int getCoverLevel() {
        return levelHigh(0);
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
                Log.d(TAG, "onPlayerEvent:错误展示页面：接受到设置数据源的事件");
                mCurrPosition = 0;
                handleStatusUI(NetworkUtils.getNetworkState(getContext()));
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE:
                mCurrPosition = bundle.getInt(EventKey.INT_ARG1);
                Log.d(TAG, "onPlayerEvent:错误展示页面：接受到定时事件-发送窗口的位置、时长，播放百分比-position="+mCurrPosition);
                break;
                default:
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {
        Log.d(TAG, "onErrorEvent:错误展示页面：接受到错误事件");
        mStatus = STATUS_ERROR;
        if (!mErrorShow){
            setErrorInfo("出错了！");
            setHandleInfo("重试");
            setErrorState(true);
        }

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    public void setErrorInfo(String msg){
        tvErrorInfo.setText(msg);
    }

    private void setHandleInfo(String msg){
        tvRetry.setText(msg);
    }

    private void setErrorState(boolean state){
        mErrorShow = state;
        setCoverVisibility(state?View.VISIBLE:View.GONE);
        if (!state){
            mStatus = STATUS_UNDEFINE;
        }else {
            Log.d(TAG, "setErrorState:错误展示页面，并发送错误事件");
            notifyReceiverEvent(DataInter.Event.EVENT_CODE_ERROR_SHOW,null);
        }
        Log.d(TAG, "setErrorState:错误展示页面，设置错误页面是否展示的共享数据 ");
        getGroupValue().putBoolean(DataInter.Key.KEY_ERROR_SHOW,state);
    }


    private static final String TAG = LogTag.TAG;
}
