package com.dong.video.dCover;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dong.video.R;
import com.dong.video.play.DataInter;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.player.OnTimerUpdateListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.touch.OnTouchGestureListener;
import com.kk.taurus.playerbase.utils.TimeUtil;

import java.io.Serializable;
import java.sql.Time;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/21
 * Time: 16:54
 */
public class DControllerCover extends BaseCover implements OnTimerUpdateListener, OnTouchGestureListener {

    private final int MSG_CODE_DELAY_HIDDEN_CONTROLLER = 101;

    @BindView(R.id.cover_player_controller_top_container)
    LinearLayout mTopContainer;
    @BindView(R.id.cover_player_controller_bottom_container)
    LinearLayout mBottomContainer;
    @BindView(R.id.cover_player_controller_text_view_video_title)
    TextView mTopTitle;
    @BindView(R.id.cover_player_controller_image_view_play_state)
    ImageView mStateIcon;
    @BindView(R.id.cover_player_controller_text_view_curr_time)
    TextView mCurrTime;
    @BindView(R.id.cover_player_controller_text_view_total_time)
    TextView mTotalTime;
    @BindView(R.id.cover_player_controller_image_view_switch_screen)
    ImageView mSwitchScreen;
    @BindView(R.id.cover_player_controller_seek_bar)
    SeekBar mSeekBar;


    private Unbinder unbinder;
    private boolean mGestureEnable;
    private boolean mControllerTopEnable;
    private ObjectAnimator mBottomAnimator;
    private ObjectAnimator mTopAnimation;
    private int mSeekProgress = -1;
    private boolean mTimerUpdateProgressEnable;
    private int mBufferPercentage;
    private String mTimeFormat;

    public DControllerCover(Context context) {
        super(context);
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        DataSource dataSource = getGroupValue().get(DataInter.Key.KEY_DATA_SOURCE);
        setTitle(dataSource);

        boolean topEnable = getGroupValue().getBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
        mControllerTopEnable = topEnable;
        if (!topEnable){
            setTopContainerState(false);
        }
        boolean screenSwitchEanble = getGroupValue().getBoolean(DataInter.Key.KEY_CONTROLLER_SCREEN_SWITCH_ENABLE, true);
        setScreenSwitchEnable(screenSwitchEanble);
    }


    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        mTopContainer.setVisibility(View.GONE);
        mBottomContainer.setVisibility(View.GONE);
        removeDelayHiddenMessage();
    }

    @Override
    protected View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_cover_controller, null);
    }

    private void setControllerState(boolean state) {
        if (state) {
            sendDelayHiddenMessage();
        } else {
            removeDelayHiddenMessage();
        }
        setTopContainerState(state);
        setBottomContainerState(state);
    }

    private void setTopContainerState(final boolean state) {
        if (mControllerTopEnable) {
            mTopContainer.clearAnimation();
            cancelTopAnimation();
            //注意这里，透明度，如果是展示 就从0到1  如果是隐藏 就从1到0
            mTopAnimation = ObjectAnimator.ofFloat(mTopContainer,
                    "alpha", state ? 0 : 1, state ? 1 : 0).setDuration(300);
            mTopAnimation.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    Log.d(TAG, "mTopAnimation-onAnimationStart:控制页面" + state);
                    if (state) {
                        mTopContainer.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Log.d(TAG, "mTopAnimation-onAnimationEnd:控制页面" + state);
                    if (!state) {
                        mTopContainer.setVisibility(View.GONE);
                    }
                }
            });
            mTopAnimation.start();
        } else {
            mTopContainer.setVisibility(View.GONE);
        }

    }

    private void cancelTopAnimation() {
        if (mTopAnimation != null) {
            mTopAnimation.cancel();
            mTopAnimation.removeAllListeners();
            mTopAnimation.removeAllUpdateListeners();
        }
    }

    private void cancelBottomAnimation() {
        if (mBottomAnimator != null) {
            mBottomAnimator.cancel();
            mBottomAnimator.removeAllUpdateListeners();
            mBottomAnimator.removeAllListeners();
        }
    }


    private void setBottomContainerState(final boolean state) {
        Log.d(TAG, "setBottomContainerState:控制页面" + state);
        mBottomContainer.clearAnimation();
        cancelBottomAnimation();
        mBottomAnimator = ObjectAnimator.ofFloat(mBottomContainer,
                "alpha", state ? 0 : 1, state ? 1 : 0).setDuration(300);
        mBottomAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                Log.d(TAG, "mBottomAnimator-onAnimationStart:控制页面" + state);
                if (state) {
                    mBottomContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.d(TAG, "mBottomAnimator-onAnimationEnd:控制页面" + state);
                if (!state) {
                    mBottomContainer.setVisibility(View.GONE);
                }
            }
        });

        mBottomAnimator.start();
        if (state) {
            Log.d(TAG, "控制页面 开始计时:requestNotifyTimer...");
            requestNotifyTimer();
        } else {
            Log.d(TAG, "控制页面 结束计时:requestStopTimer...");
            requestStopTimer();
        }
    }


    /**
     * 利用Handler做延时，默认只展示5秒然后自动关闭
     */
    private void sendDelayHiddenMessage() {
        removeDelayHiddenMessage();
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_HIDDEN_CONTROLLER, 5000);
    }

    private void removeDelayHiddenMessage() {
        mHandler.removeMessages(MSG_CODE_DELAY_HIDDEN_CONTROLLER);
    }

    /**
     * 快进的时候调节播放进度
     *
     * @param progress
     */
    private void sendSeekEvent(int progress) {
        mTimerUpdateProgressEnable = false;
        mSeekProgress = progress;
        mHandler.removeCallbacks(mSeekEventRunnable);
        mHandler.postDelayed(mSeekEventRunnable, 300);
    }

    @OnClick({
            R.id.cover_player_controller_image_view_back_icon,
            R.id.cover_player_controller_image_view_play_state,
            R.id.cover_player_controller_image_view_switch_screen})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.cover_player_controller_image_view_back_icon:
                Log.d(TAG, "onViewClick:控制页面 点击返回按钮 ");
                notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_BACK,null);
                break;
            case R.id.cover_player_controller_image_view_play_state:
                boolean selected = mStateIcon.isSelected();
                Log.d(TAG, "onViewClick:控制页面 暂停播放按钮 "+selected);
                if (selected){
                    requestResume(null);
                }else {
                    requestPause(null);
                }
                mStateIcon.setSelected(!selected);
                break;
            case R.id.cover_player_controller_image_view_switch_screen:
                Log.d(TAG, "onViewClick:控制页面 发送横竖屏的切换消息 ");
                notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN,null);
                break;
                default:

        }
    }



    private void setTitle(DataSource dataSource) {
        if (dataSource != null) {
            String title = dataSource.getTitle();
            if (!TextUtils.isEmpty(title)) {
                setTitle(title);
                return;
            }
            String data = dataSource.getData();
            if (!TextUtils.isEmpty(data)) {
                setTitle(data);
            }
        }
    }

    private void setTitle(String text) {
        mTopTitle.setText(text);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        unbinder = ButterKnife.bind(this,getView());
        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        getGroupValue().registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        unbinder.unbind();
        cancelTopAnimation();
        cancelBottomAnimation();

        getGroupValue().unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                Log.d(TAG, "onPlayerEvent:控制页面 PLAYER_EVENT_ON_DATA_SOURCE_SET ");
                mBufferPercentage = 0;
                mTimeFormat = null;
                updateUI(0, 0);
                DataSource data = (DataSource) bundle.getSerializable(EventKey.SERIALIZABLE_DATA);
                getGroupValue().putObject(DataInter.Key.KEY_DATA_SOURCE, data);
                setTitle(data);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE:
                int status = bundle.getInt(EventKey.INT_DATA);
                Log.d(TAG, "onPlayerEvent:控制页面 PLAYER_EVENT_ON_STATUS_CHANGE 状态变化 "+status);
                if (status == IPlayer.STATE_PAUSED) {
                    Log.d(TAG, "onPlayerEvent:控制页面 PLAYER_EVENT_ON_STATUS_CHANGE STATE_PAUSED ");
                    mStateIcon.setSelected(true);
                } else if (status == IPlayer.STATE_STARTED) {
                    Log.d(TAG, "onPlayerEvent:控制页面 PLAYER_EVENT_ON_STATUS_CHANGE STATE_STARTED ");
                    mStateIcon.setSelected(false);
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
                Log.d(TAG, "onPlayerEvent:控制页面 PLAYER_EVENT_ON_VIDEO_RENDER_START ");
                mTimerUpdateProgressEnable = true;
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE:
                Log.d(TAG, "onPlayerEvent:控制页面 PLAYER_EVENT_ON_SEEK_COMPLETE ");
                mTimerUpdateProgressEnable = true;
                break;
            default:
        }
    }

    @Override
    public Bundle onPrivateEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case DataInter.PrivateEvent.EVENT_CODE_UPDATE_SEEK:
                Log.d(TAG, "onPrivateEvent:控制页面 接受到手势页面传来的进度调整 ");
                if (bundle != null) {
                    int curr = bundle.getInt(EventKey.INT_ARG1);
                    int duration = bundle.getInt(EventKey.INT_ARG2);
                    Log.d(TAG, "onPrivateEvent:控制页面 " +
                            "接受到手势页面传来的进度调整c = " + curr + "-d" + duration);
                    updateUI(curr, duration);
                }
                break;
            default:
        }

        return null;
    }

    private void updateUI(int curr, int duration) {
        setSeekProgress(curr, duration);
        setCurrTime(curr);
        setTotalTime(duration);

    }

    private void setTotalTime(int duration) {
        mTotalTime.setText(TimeUtil.getTime(mTimeFormat, duration));
    }

    private void setCurrTime(int curr) {
        mCurrTime.setText(TimeUtil.getTime(mTimeFormat, curr));
    }

    /**
     * 设置底部的播放进度
     *
     * @param curr
     * @param duration
     */
    private void setSeekProgress(int curr, int duration) {
        mSeekBar.setMax(duration);
        mSeekBar.setProgress(curr);
        float secondProgress = mBufferPercentage * 1.0f / 100 * duration;
        setSecondProgress((int) secondProgress);
    }

    private void setSecondProgress(int secondProgress) {
        mSeekBar.setSecondaryProgress(secondProgress);
    }

    private boolean isControllerShow() {
        return mBottomContainer.getVisibility() == View.VISIBLE;
    }

    private void toggleController() {
        if (isControllerShow()) {
            setControllerState(false);
        } else {
            setControllerState(true);
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onTimerUpdate(int curr, int duration, int bufferPercentage) {
        Log.d(TAG, "onTimerUpdate 控制页面 :curr=" + curr + "duration=" + duration + "bufferPer=" + bufferPercentage);
        if (!mTimerUpdateProgressEnable) {
            return;
        }
        if (mTimeFormat == null) {
            mTimeFormat = TimeUtil.getFormat(duration);
        }
        mBufferPercentage = bufferPercentage;
        updateUI(curr, duration);
    }

    @Override
    public void onSingleTapUp(MotionEvent event) {
        if (!mGestureEnable) {
            return;
        }
        toggleController();
    }


    @Override
    public void onDoubleTap(MotionEvent event) {

    }

    @Override
    public void onDown(MotionEvent event) {

    }

    @Override
    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mGestureEnable) {
            return;
        }

    }

    @Override
    public void onEndGesture() {

    }

    @Override
    public int getCoverLevel() {
        return levelLow(1);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CODE_DELAY_HIDDEN_CONTROLLER:
                    Log.d(TAG, "handleMessage:控制页面 接受到隐藏界面的通知 ");
                    setControllerState(false);
                    break;
                default:
            }
        }
    };

    private static final String TAG = LogTag.TAG;

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                updateUI(progress, seekBar.getMax());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendSeekEvent(seekBar.getProgress());
        }
    };

    public void setGestureEnable(boolean mGestureEnable) {
        this.mGestureEnable = mGestureEnable;
    }

    private void setSwitchScreenIcon(boolean isFullScreen) {
        mSwitchScreen.setImageResource(isFullScreen
                ? R.mipmap.icon_exit_full_screen
                : R.mipmap.icon_full_screen);
    }

    private void setScreenSwitchEnable(boolean screenSwitchEnable) {
        mSwitchScreen.setVisibility(screenSwitchEnable
                ? View.VISIBLE
                : View.GONE);
    }


    private IReceiverGroup.OnGroupValueUpdateListener mOnGroupValueUpdateListener = new IReceiverGroup.OnGroupValueUpdateListener() {
        @Override
        public String[] filterKeys() {
            return new String[]{
                    DataInter.Key.KEY_COMPLETE_SHOW,
                    DataInter.Key.KEY_IS_LANDSCAPE,
                    DataInter.Key.KEY_DATA_SOURCE,
                    DataInter.Key.KEY_TIMER_UPDATE_ENABLE,
                    DataInter.Key.KEY_CONTROLLER_TOP_ENABLE};
        }

        @Override
        public void onValueUpdate(String key, Object value) {
            if (key.equals(DataInter.Key.KEY_COMPLETE_SHOW)) {
                boolean show = (boolean) value;
                Log.d(TAG, "onValueUpdate: 控制页面 KEY_COMPLETE_SHOW"+show);
                //隐藏控制页面
                if (show) {
                    setControllerState(false);
                }
                //播放完成界面不允许滑动手势
                setGestureEnable(!show);
            } else if (key.equals(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE)) {
                mControllerTopEnable = (boolean) value;
                Log.d(TAG, "onValueUpdate: 控制页面 KEY_CONTROLLER_TOP_ENABLE"+mControllerTopEnable);
                if (!mControllerTopEnable) {
                    setTopContainerState(false);
                }
            } else if (key.equals(DataInter.Key.KEY_IS_LANDSCAPE)) {
                boolean screenEnable = (boolean) value;
                Log.d(TAG, "onValueUpdate: 控制页面 KEY_IS_LANDSCAPE"+screenEnable);
                setSwitchScreenIcon(screenEnable);
            } else if (key.equals(DataInter.Key.KEY_TIMER_UPDATE_ENABLE)){
                mTimerUpdateProgressEnable = (boolean) value;
                Log.d(TAG, "onValueUpdate: 控制页面 KEY_IS_LANDSCAPE"+mTimerUpdateProgressEnable);
            }else if (key.equals(DataInter.Key.KEY_DATA_SOURCE)){
                DataSource dataSource = (DataSource) value;
                Log.d(TAG, "onValueUpdate: 控制页面 KEY_DATA_SOURCE"+dataSource.getData());
                setTitle(dataSource);
            }
        }
    };
    private Runnable mSeekEventRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSeekProgress < 0) {
                return;
            }
            Bundle bundle = BundlePool.obtain();
            bundle.putInt(EventKey.INT_DATA, mSeekProgress);
            requestSeek(bundle);
        }
    };
}
