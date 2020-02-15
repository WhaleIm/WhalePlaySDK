package im.whale.whaleplaysdk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import im.whale.whaleplaysdk.demo.DemoActivity;
import im.whale.whaleplaysdk.message.MessageReceiver;

/**
 * 该页面示范了如何注册 MessageReceiver 以获取传感器数据; 如何通过Intent获取设备sn。
 * 您设备上必须安装有WhalePlay才行。
 * 如果没有硬件传感器，你可以使用 WhalePlay 右上角悬浮按钮「模拟」common 数据。
 * <p>
 * Created on 2019-08-31.
 *
 * @author ice
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SN = 1218;

    private MessageReceiver mCommonMessageReceiver;
    private MessageReceiver mCameraMessageReceiver;
    private MessageReceiver mDeviceMessageReceiver;

    private TextView mInfoTv, mMessageTv;
    private CheckBox mCommonCheckBox, mCameraCheckBox, mDeviceCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInfoTv = findViewById(R.id.info_tv);
        mMessageTv = findViewById(R.id.message_tv);
        findViewById(R.id.demo_btn).setOnClickListener(v -> {
//            mCommonMessageReceiver.unregister(this);
//            mCameraMessageReceiver.unregister(this);
//            mDeviceMessageReceiver.unregister(this);

            mCommonCheckBox.setChecked(false);
            mCameraCheckBox.setChecked(false);
            mDeviceCheckBox.setChecked(false);

            startActivity(new Intent(MainActivity.this, DemoActivity.class));
        });

        mCommonCheckBox = findViewById(R.id.common_check_box);
        mCameraCheckBox = findViewById(R.id.camera_check_box);
        mDeviceCheckBox = findViewById(R.id.device_check_box);

        mCommonMessageReceiver = MessageReceiver.Factory.common();
        mCameraMessageReceiver = MessageReceiver.Factory.camera();
        mDeviceMessageReceiver = MessageReceiver.Factory.device();

        mCommonCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    registerCommon();
                else
                    mCommonMessageReceiver.unregister(MainActivity.this);
            }
        });

        mCameraCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    registerCamera();
                else
                    mCameraMessageReceiver.unregister(MainActivity.this);
            }
        });

        mDeviceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    registerDevice();
                else
                    mDeviceMessageReceiver.unregister(MainActivity.this);
            }
        });

        /*申请获取配置信息*/
        Intent intent = new Intent("im.whale.action.GET_CONFIG");
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "请先安装 WhalePlay ", Toast.LENGTH_LONG).show();
            mInfoTv.setText("! 请安装WhalePlay以正常使用当前App");
        } else {
            startActivityForResult(intent, REQUEST_SN); //REQUEST_SN为自定义int常数
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_SN) {
            if (resultCode == RESULT_OK && data != null) {
                /*获取到配置信息*/
                String deviceSn = data.getStringExtra("device_sn"); //设备的sn号

                mInfoTv.setText("Device SN: " + deviceSn);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* 记得取消注册，以防止内存泄漏 */
        mCommonMessageReceiver.unregister(this);
        mCameraMessageReceiver.unregister(this);
        mDeviceMessageReceiver.unregister(this);
    }

    /**
     * 监听常规业务数据
     */
    private void registerCommon() {
        mCommonMessageReceiver.register(this);
        mCommonMessageReceiver.setOnMessageReceiveListener(new MessageReceiver.OnMessageReceiveListener() {
            @Override
            public void onMessageReceive(String topic, String message) {
                showMessage("Common " + topic + "\n" + message);
            }
        });
    }

    /**
     * 监听相机数据
     */
    private void registerCamera() {
        mCameraMessageReceiver.register(this);
        mCameraMessageReceiver.setOnMessageReceiveListener(new MessageReceiver.OnMessageReceiveListener() {
            @Override
            public void onMessageReceive(String topic, String message) {
                showMessage("Camera " + topic + "\n" + message);
            }
        });
    }

    /**
     * 监听其他硬件数据
     */
    private void registerDevice() {
        mDeviceMessageReceiver.register(this);
        mDeviceMessageReceiver.setOnMessageReceiveListener(new MessageReceiver.OnMessageReceiveListener() {
            @Override
            public void onMessageReceive(String topic, String message) {
                showMessage("Device " + topic + "\n" + message);
            }
        });
    }

    private void showMessage(String text) {
        mMessageTv.append("\n\n" + new Date().toString() + "\n" + text);

        mMessageTv.post(new Runnable() {
            @Override
            public void run() {
                ScrollView scrollView = (ScrollView) mMessageTv.getParent();
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


}
