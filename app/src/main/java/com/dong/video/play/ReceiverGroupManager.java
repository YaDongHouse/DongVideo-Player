package com.dong.video.play;

import android.content.Context;

import com.dong.video.dCover.DCompleteCover;
import com.dong.video.dCover.DControllerCover;
import com.dong.video.dCover.DErrorCover;
import com.dong.video.dCover.DGestureCover;
import com.dong.video.dCover.DLoadingCover;
import com.kk.taurus.playerbase.receiver.GroupValue;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;

import static com.dong.video.play.DataInter.ReceiverKey.KEY_COMPLETE_COVER;
import static com.dong.video.play.DataInter.ReceiverKey.KEY_CONTROLLER_COVER;
import static com.dong.video.play.DataInter.ReceiverKey.KEY_ERROR_COVER;
import static com.dong.video.play.DataInter.ReceiverKey.KEY_GESTURE_COVER;
import static com.dong.video.play.DataInter.ReceiverKey.KEY_LOADING_COVER;

/**
 * @packInfo:com.dong.video.play
 * @author: yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/14
 * Time: 10:34
 */
public class ReceiverGroupManager {

    private static ReceiverGroupManager intance;

    private ReceiverGroupManager() {

    }

    public static ReceiverGroupManager getInstance() {
        if (null == intance){
            synchronized (ReceiverGroupManager.class){
                if (null == intance){
                    intance = new ReceiverGroupManager();
                }
            }
        }
        return intance;
    }

    public ReceiverGroup getLittleReceiverGroup(Context context){
        return getLiteReceiverGroup(context, null);
    }

    public ReceiverGroup getLittleReceiverGroup(Context context, GroupValue groupValue){
        ReceiverGroup receiverGroup = new ReceiverGroup(groupValue);
        receiverGroup.addReceiver(KEY_LOADING_COVER, new DLoadingCover(context));
        receiverGroup.addReceiver(KEY_COMPLETE_COVER, new DCompleteCover(context));
        receiverGroup.addReceiver(KEY_ERROR_COVER, new DErrorCover(context));
        return receiverGroup;
    }

    public ReceiverGroup getLiteReceiverGroup(Context context){
        return getLiteReceiverGroup(context, null);
    }

    public ReceiverGroup getLiteReceiverGroup(Context context, GroupValue groupValue){
        ReceiverGroup receiverGroup = new ReceiverGroup(groupValue);
        receiverGroup.addReceiver(KEY_LOADING_COVER, new DLoadingCover(context));
        receiverGroup.addReceiver(KEY_CONTROLLER_COVER, new DControllerCover(context));
        receiverGroup.addReceiver(KEY_COMPLETE_COVER, new DCompleteCover(context));
        receiverGroup.addReceiver(KEY_ERROR_COVER, new DErrorCover(context));
        return receiverGroup;
    }

    public ReceiverGroup getReceiverGroup(Context context){
        return getReceiverGroup(context, null);
    }

    public ReceiverGroup getReceiverGroup(Context context, GroupValue groupValue){
        ReceiverGroup receiverGroup = new ReceiverGroup(groupValue);
        receiverGroup.addReceiver(KEY_LOADING_COVER, new DLoadingCover(context));
        receiverGroup.addReceiver(KEY_CONTROLLER_COVER, new DControllerCover(context));
        receiverGroup.addReceiver(KEY_GESTURE_COVER, new DGestureCover(context));
        receiverGroup.addReceiver(KEY_COMPLETE_COVER, new DCompleteCover(context));
        receiverGroup.addReceiver(KEY_ERROR_COVER, new DErrorCover(context));
        return receiverGroup;
    }



}
