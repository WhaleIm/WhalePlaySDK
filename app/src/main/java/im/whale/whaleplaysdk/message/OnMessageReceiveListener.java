package im.whale.whaleplaysdk.message;

/**
 * Created on 2019-08-31.
 *
 * @author ice
 */
public interface OnMessageReceiveListener {

    void onMessageReceive(String topic, String message);

}
