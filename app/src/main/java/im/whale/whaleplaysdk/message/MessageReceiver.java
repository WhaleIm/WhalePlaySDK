package im.whale.whaleplaysdk.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 第三方App消息接收器
 * 接收来自WhalePlay转发的传感器消息。
 * <p>
 * 原始数据有硬件传感器、相机传感器两种。
 * 通过工厂创建实例{@link Factory}
 * <p>
 * Created on 2019-08-31.
 *
 * @author ice
 */
public class MessageReceiver extends BroadcastReceiver {
    private static final String KEY_TOPIC = "topic";
    private static final String KEY_MESSAGE = "message";

    private final String ACTION_RECEIVE;
    private OnMessageReceiveListener mOnMessageReceiveListener;

    public interface OnMessageReceiveListener {
        void onMessageReceive(String topic, String message);
    }

    private MessageReceiver(String action) {
        ACTION_RECEIVE = action;
    }

    public void setOnMessageReceiveListener(OnMessageReceiveListener onMessageReceiveListener) {
        mOnMessageReceiveListener = onMessageReceiveListener;
    }

    public void register(Context context) {
        IntentFilter intentFilter = new IntentFilter(ACTION_RECEIVE);
        context.getApplicationContext().registerReceiver(this, intentFilter);
    }

    public void unregister(Context context) {
        context.getApplicationContext().unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_RECEIVE.equals(intent.getAction())) {
            String topic = intent.getStringExtra(KEY_TOPIC);
            String message = intent.getStringExtra(KEY_MESSAGE);

            if (mOnMessageReceiveListener != null) {
                mOnMessageReceiveListener.onMessageReceive(topic, message);
            }
        }
    }



    /*--------------------------------构建工厂------------------------------------------*/


    public static class Factory {
        /**
         * 接收硬件mqtt消息
         */
        private static final String ACTION_RECEIVE_DEVICE = "im.whale.action.MQTT_RECEIVE_DEVICE";

        /**
         * 接收相机mqtt消息
         */
        private static final String ACTION_RECEIVE_CAMERA = "im.whale.action.MQTT_RECEIVE_CAMERA";

        /**
         * 接收普通mqtt消息
         */
        private static final String ACTION_RECEIVE_COMMON = "im.whale.action.MQTT_RECEIVE";

        public MessageReceiver common() {
            return new MessageReceiver(ACTION_RECEIVE_COMMON);
        }

        public MessageReceiver camera() {
            return new MessageReceiver(ACTION_RECEIVE_CAMERA);
        }

        public MessageReceiver device() {
            return new MessageReceiver(ACTION_RECEIVE_DEVICE);
        }
    }

}
