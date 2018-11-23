package com.dong.video.dCover;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dong.video.R;
import com.dong.video.play.DataInter;
import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;
import com.kk.taurus.playerbase.touch.OnTouchGestureListener;
import com.kk.taurus.playerbase.utils.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kk.taurus.playerbase.event.OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START;

/**
 * @author yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/21
 * Time: 10:11
 */
public class DGestureCover extends BaseCover implements OnTouchGestureListener {

    @BindView(R.id.cover_player_gesture_operation_volume_box)
    LinearLayout mVolumeBox;
    @BindView(R.id.cover_player_gesture_operation_brightness_box)
    LinearLayout mBrightnessBox;
    @BindView(R.id.cover_player_gesture_operation_volume_icon)
    ImageView mVolumeIcon;
    @BindView(R.id.cover_player_gesture_operation_volume_text)
    TextView mVolumeText;
    @BindView(R.id.cover_player_gesture_operation_brightness_text)
    TextView mBrightnessText;
    @BindView(R.id.cover_player_gesture_operation_fast_forward_box)
    LinearLayout mFastForwardBox;
    @BindView(R.id.cover_player_gesture_operation_fast_forward_text_view_step_time)
    TextView mFastForwardStepTime;
    @BindView(R.id.cover_player_gesture_operation_fast_forward_text_view_progress_time)
    TextView mFastForwardProgressTime;


    /**
     * 用来标记是否启用手势
     */
    private boolean mGestureEnable = true;


    private AudioManager audioManager;
    private int mMaxVolume;

    private Unbinder unbinder;

    private boolean mHorizontalSlide;

    private boolean firstTouch;

    private int volume;

    /**
     * 用于记录整个View的宽高
     */
    private int mWidth ,mHeight;
    /**
     * 标记是否是右侧竖直滑动
     */
    private boolean rightVerticalSlide;
    /**
     * 标记是否是水平滑动
     */
    private boolean horizontalSlide;
    private long newPosition;
    private float brightness;
    private int mSeekProgress;

    public DGestureCover(Context context) {
        super(context);
    }

    @Override
    protected View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_gesture_cover, null);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        unbinder = ButterKnife.bind(this, getView());
        initAudioManager(getContext());
    }

    /**
     * 初始化声音管理器
     *
     * @param context
     */
    private void initAudioManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        unbinder.unbind();
    }

    @Override
    public int getCoverLevel() {
        return levelLow(0);
    }

    public void setGestureEnable(boolean mGestureEnable) {
        this.mGestureEnable = mGestureEnable;
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        getGroupValue().registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mWidth = getView().getWidth();
                mHeight = getView().getHeight();
                getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        getGroupValue().unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case PLAYER_EVENT_ON_VIDEO_RENDER_START:
                Log.d(TAG, "onPlayerEvent:手势页面 接受到播放事件：PLAYER_EVENT_ON_VIDEO_RENDER_START ");
                setGestureEnable(true);
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

    public void setVolumeBoxState(boolean state) {
        if (mVolumeBox != null) {
            mVolumeBox.setVisibility(state ? View.VISIBLE : View.GONE);
        }
    }

    public void setVolumeIcon(int resId) {
        if (mVolumeIcon != null) {
            mVolumeIcon.setImageResource(resId);
        }
    }

    public void setVolumeText(String text) {
        if (mVolumeText != null) {
            mVolumeText.setText(text);
        }
    }

    public void setBrightnessBoxState(boolean state) {
        if (mBrightnessBox != null) {
            mBrightnessBox.setVisibility(state ? View.VISIBLE : View.GONE);
        }
    }

    public void setBrightnessText(String text) {
        if (mBrightnessText != null) {
            mBrightnessText.setText(text);
        }
    }

    private void setFastForwardState(boolean state) {
        mFastForwardBox.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setFastForwardStepTime(String text) {
        mFastForwardStepTime.setText(text);
    }

    private void setFastForwardProgressTime(String text) {
        mFastForwardProgressTime.setText(text);
    }

    private int getVolume() {
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (volume < 0) {
            volume = 0;
        }
        return volume;
    }


    @Override
    public void onSingleTapUp(MotionEvent event) {
        Log.d(TAG, "onSingleTapUp:手势页面，单击 ");
    }

    @Override
    public void onDoubleTap(MotionEvent event) {
        Log.d(TAG, "onDoubleTap:手势页面，双击 ");
    }

    @Override
    public void onDown(MotionEvent event) {
        Log.d(TAG, "onDown:手势页面，按下 ");
        mHorizontalSlide = false;
        firstTouch = true;
        volume = getVolume();
    }

    @Override
    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mGestureEnable) {
            return;
        }
        float mOldX = e1.getX(), mOldY = e1.getY();
        float deltaY = mOldY - e2.getY();
        float deltaX = mOldX - e2.getX();
        if (firstTouch) {
            horizontalSlide = Math.abs(distanceX) >= Math.abs(distanceY);
            rightVerticalSlide = mOldX > mWidth *0.5f;
            firstTouch = false;
        }
        if (horizontalSlide){
            Log.d(TAG, "onScroll手势页面 :水平滑动，"+deltaX+"width-"+mWidth);
            onHorizontalSlide(-deltaX/mWidth);
        }else {
            Log.d(TAG, "onScroll手势页面 :竖直滑动，"+deltaY+"height-"+mHeight);
            if (Math.abs(deltaY) > mHeight){
                return;
            }
            if (rightVerticalSlide){
                onRightVerticalSlide(deltaY/mHeight);
            }else {
                onLeftVerticalSlide(deltaY/mHeight);
            }
        }
    }

    /**
     * 获取时长
     * @return
     */
    private int getDuration(){
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        return playerStateGetter == null?0:playerStateGetter.getDuration();
    }

    /**
     * 获取当前播放位置
     * @return
     */
    private int getCurrentPosition(){
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        return playerStateGetter == null ?0:playerStateGetter.getCurrentPosition();
    }

    private void onHorizontalSlide(float percent){
        if (getDuration() <= 0){
            return;
        }
        mHorizontalSlide = true;
        //水平滑动时 控制页面暂停进度刷新
        if (getGroupValue().getBoolean(DataInter.Key.KEY_TIMER_UPDATE_ENABLE)){
            getGroupValue().putBoolean(DataInter.Key.KEY_TIMER_UPDATE_ENABLE,false);
        }
        long position = getCurrentPosition();
        long duration = getDuration();
        //播放位置，没有播放一半的 取一半，已经播放一半多的 按剩余的算
        long deltaMax = Math.min(getDuration() / 2, duration - position);
        long delta = (long) (deltaMax * percent);
        Log.d(TAG, "onHorizontalSlide 手势页面:deltaMax-"+deltaMax+"delta-"+delta);
        newPosition = delta + position;
        if (newPosition > duration){
            newPosition = duration;
        }else if (newPosition <=0){
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) (delta/1000);
        if (showDelta!= 0){
            //发私有事件给控制覆盖页 新的位置以及资源时长
            Bundle mBundle = BundlePool.obtain();
            mBundle.putInt(EventKey.INT_ARG1, (int) newPosition);
            mBundle.putInt(EventKey.INT_ARG2, (int) duration);
            notifyReceiverPrivateEvent(
                    DataInter.ReceiverKey.KEY_CONTROLLER_COVER,
                    DataInter.PrivateEvent.EVENT_CODE_UPDATE_SEEK,
                    mBundle);
            setFastForwardState(true);
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
            setFastForwardStepTime(text+"s");
            String progressText = TimeUtil.getTimeSmartFormat(newPosition) + "/" + TimeUtil.getTimeSmartFormat(duration);
            setFastForwardProgressTime(progressText);
        }
    }

    private void onRightVerticalSlide(float percent){
        mHorizontalSlide = false;
        int index = (int)(percent * mMaxVolume)+volume;
        if (index > mMaxVolume){
            index = mMaxVolume;
        }else if (index < 0){
            index = 0;
        }
        Log.d(TAG, "onRightVerticalSlide:手势页面 index"+index);
        //改变声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,index,0);
        //变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i+"%";
        if (i == 0){
            s = "OFF";
        }
        setVolumeIcon(i == 0?R.mipmap.ic_volume_off_white:R.mipmap.ic_volume_up_white);
        setBrightnessBoxState(false);
        setFastForwardState(false);
        setVolumeBoxState(true);
        setVolumeText(s);
    }

    private void onLeftVerticalSlide(float percent){
        mHorizontalSlide = false;
        Activity activity = getActivity();
        if (activity == null){
            return;
        }
        if (brightness < 0){
            brightness = activity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f){
                brightness = 0.50f;
            }else if (brightness < 0.01f){
                brightness = 0.01f;
            }
        }
        setVolumeBoxState(false);
        setFastForwardState(false);
        setBrightnessBoxState(true);
        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness  = brightness + percent;
        Log.d(TAG, "onLeftVerticalSlide:手势页面 修正前"+lpa.screenBrightness);
        if (lpa.screenBrightness > 1.0f){
            lpa.screenBrightness = 1.0f;
        }else if (lpa.screenBrightness < 0.01f){
            lpa.screenBrightness = 0.01f;
        }
        Log.d(TAG, "onLeftVerticalSlide:手势页面 修正后"+lpa.screenBrightness);
        setBrightnessText(((int)(lpa.screenBrightness * 100))+"%");
        activity.getWindow().setAttributes(lpa);
    }

    private Activity getActivity(){
        Context context = getContext();
        if (context instanceof Activity){
            return (Activity) context;
        }
        return null;
    }



    @Override
    public void onEndGesture() {
        Log.d(TAG, "onDown:手势页面，手势结束 ");
        volume = -1;
        brightness = -1f;
        setVolumeBoxState(false);
        setBrightnessBoxState(false);
        setFastForwardState(false);
        if (newPosition >= 0 && mHorizontalSlide){
            sendSeekEvent((int)newPosition);
            newPosition = 0;
        }else {
            getGroupValue().putBoolean(DataInter.Key.KEY_TIMER_UPDATE_ENABLE,true);
        }
        mHorizontalSlide = false;
    }

    private void sendSeekEvent(int progress){
        getGroupValue().putBoolean(DataInter.Key.KEY_TIMER_UPDATE_ENABLE,false);
        mSeekProgress = progress;
        mHandler.removeCallbacks(mSeekEventRunnable);
        mHandler.postDelayed(mSeekEventRunnable,300);
    }

    private Runnable mSeekEventRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSeekProgress <0 ){
                return;
            }
            Bundle obtain = BundlePool.obtain();
            obtain.putInt(EventKey.INT_DATA,mSeekProgress);
            requestSeek(obtain);
        }
    };

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private IReceiverGroup.OnGroupValueUpdateListener mOnGroupValueUpdateListener = new IReceiverGroup.OnGroupValueUpdateListener() {
        @Override
        public String[] filterKeys() {
            return new String[]{DataInter.Key.KEY_COMPLETE_SHOW};
        }

        @Override
        public void onValueUpdate(String key, Object value) {
            //在播放完成界面不允许做手势
            if (key.equals(DataInter.Key.KEY_COMPLETE_SHOW)){
                setGestureEnable(!(boolean)value);
            }
        }
    };
    private static final String TAG = LogTag.TAG;


}
