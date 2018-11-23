package com.dong.video.dCover;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dong.video.R;
import com.dong.video.play.DataInter;
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
 * Time: 17:10
 *
 * 用户在窗口播放时，发送关闭事件
 */
public class DCloseCover extends BaseCover {

    @BindView(R.id.iv_close)
    ImageView ivClose;

    private Unbinder bind;

    public DCloseCover(Context context) {
        super(context);
    }


    @OnClick({R.id.iv_close})
    public void onViewClick(View view){
        Log.d(LogTag.TAG, "DCloseCover:关闭页面 发送关闭事件 ");
        notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_CLOSE,null);
    }


    /**
     * 1 第一步 加载布局
     *
     * @param context
     * @return
     */
    @Override
    protected View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_cover_close, null);
    }
    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        bind = ButterKnife.bind(this, getView());
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        bind.unbind();
    }

    @Override
    public int getCoverLevel() {
        return levelMedium(10);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }
}
