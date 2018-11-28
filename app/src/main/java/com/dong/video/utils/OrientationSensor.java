package com.dong.video.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.OrientationEventListener;


/**
 * @author yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/27
 * Time: 9:58
 */
public class OrientationSensor {

    private final int MSG_SENSOR = 888;


    OnOrientationListener onOrientationListener;
    private final SensorManager sensorManager;
    private final Sensor sensor;
    private final OrientationSensorListener listener;

    private final SensorManager sensorManager1;
    private final Sensor sensor1;
    private final OrientationSensorListener1 listener1;
    /**
     * 是否是竖屏
     */
    private boolean isPortrait;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SENSOR:
                    int orientation = msg.arg1;
                    if (orientation > 45 && orientation < 135){
                        if (isPortrait){
                            callbackOnLandScape(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                            isPortrait = false;
                        }
                    }else if (orientation > 135 && orientation < 225){
                        if (!isPortrait){
                            callbackOnPortrait(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            isPortrait = true;
                        }
                    }else if (orientation > 225 && orientation < 315){
                        if (isPortrait){
                            callbackOnLandScape(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            isPortrait = false;
                        }
                    }else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)){
                        if (!isPortrait){
                            callbackOnPortrait(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            isPortrait = true;
                        }
                    }
                        break;
                default:
            }
        }
    };


    public OrientationSensor(Activity activity, OnOrientationListener orientationListener) {
        this.onOrientationListener = orientationListener;
        //注册重力感应器，监听屏幕旋转
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        //加速度传感器
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new OrientationSensorListener(mHandler);

        //根据 选装之后/点击全屏之后，两者方向一致，激活sm
        sensorManager1 = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensor1 = sensorManager1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener1 = new OrientationSensorListener1();
    }

    private void callbackOnLandScape(int orientation) {
        if (onOrientationListener != null) {
            onOrientationListener.onLandScape(orientation);
        }
    }

    private void callbackOnPortrait(int orientation) {
        if (onOrientationListener != null) {
            onOrientationListener.onPortrait(orientation);
        }
    }

    /**
     * 手动横竖屏切换方向
     */
    public void toggleScreen(){
        sensorManager.unregisterListener(listener);
        sensorManager1.registerListener(listener1,sensor1,SensorManager.SENSOR_DELAY_UI);
        if (isPortrait){
            isPortrait = false;
            //切换成横屏
            callbackOnLandScape(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            isPortrait = true;
            //切换成竖屏
            callbackOnPortrait(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    public void enable() {
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void disable() {
        sensorManager.unregisterListener(listener);
        sensorManager1.unregisterListener(listener1);
    }


    public boolean isPortrait() {
        return isPortrait;
    }

    public interface OnOrientationListener {
        void onLandScape(int orientation);

        void onPortrait(int orientation);
    }

    private static final String TAG = "OrientationSensor";

    /**
     * 重力感应监听者
     */
    public class OrientationSensorListener implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNNKOWN = -1;

        private Handler rotateHandler;

        public OrientationSensorListener(Handler rotateHandler) {
            this.rotateHandler = rotateHandler;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            //从源码中复制来的 OrientationEventListener
            float[] values = event.values;
            int orientation = ORIENTATION_UNNKOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y value
            if (magnitude * 4 >= Z * Z) {
                //屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) (Math.atan2(-Y, X) * OneEightyOverPi);
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (rotateHandler != null) {
                rotateHandler.obtainMessage(MSG_SENSOR, orientation, 0).sendToTarget();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public class OrientationSensorListener1 implements SensorEventListener {

        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNNKOWN = -1;

        public OrientationSensorListener1() {
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {


        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNNKOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            //检测到当前实际是横屏
            if (orientation > 225 && orientation < 315) {
                if (isPortrait) {
                    sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
                    sensorManager1.unregisterListener(listener1);
                }
                //检测到当前实际是竖屏
            } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                if (isPortrait) {
                    sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
                    sensorManager1.unregisterListener(listener1);
                }
            }
        }
    }

}
