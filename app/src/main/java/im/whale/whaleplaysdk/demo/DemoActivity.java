package im.whale.whaleplaysdk.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.Random;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import im.whale.whaleplaysdk.R;
import im.whale.whaleplaysdk.demo.model.BaseResult;
import im.whale.whaleplaysdk.demo.model.CameraDataBean;
import im.whale.whaleplaysdk.demo.model.ContentBean;
import im.whale.whaleplaysdk.demo.model.DeviceSnBean;
import im.whale.whaleplaysdk.demo.model.InitBean;
import im.whale.whaleplaysdk.demo.model.InitRequestBean;
import im.whale.whaleplaysdk.demo.model.SkuDetailBean;
import im.whale.whaleplaysdk.message.MessageReceiver;

/**
 * Created on 2020-02-15.
 *
 * @author ice
 */
public class DemoActivity extends BaseFullScreenActivity {

    private static final Random MY_RANDOM = new Random();

    private static final int REQUEST_SN = 1218;
    @BindView(R.id.play_view) PlayerView mPlayView;
    @BindView(R.id.avatar_iv) ImageView mAvatarIv;
    @BindView(R.id.mock_tv) TextView mMockTv;
    @BindView(R.id.mock_camera_tv) TextView mMockCameraTv;
    @BindView(R.id.exit_tv) TextView mExitTv;
    @BindView(R.id.detail_iv) ImageView mDetailIv;
    @BindView(R.id.detail_title_tv) TextView mDetailTitleTv;
    @BindView(R.id.detail_close_iv) ImageView mDetailCloseIv;
    @BindView(R.id.detail_layout) FrameLayout mDetailLayout;

    private HttpRequester mHttpRequester;

    private MessageReceiver mDeviceMessageReceiver;

    private String mSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);

        mDeviceMessageReceiver = MessageReceiver.Factory.device();
        mDeviceMessageReceiver.setOnMessageReceiveListener((topic, message) -> {
            try {
                JSONObject json = JSONObject.parseObject(message);
                final String sn = json.getString("sn");
                if (TextUtils.isEmpty(sn)) {
                    return;
                }
                sensorNotify(sn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        mDeviceMessageReceiver.register(this);

        mExitTv.setOnClickListener(v -> onBackPressed());

        mDetailCloseIv.setOnClickListener(v -> mDetailLayout.setVisibility(View.GONE));

        //在没有传感器接入的情况下，可以点击按钮简单模拟传感器的触发。当然，也可以点击WhalePlay右上角全局悬浮窗的模拟按钮。
        mMockTv.setOnClickListener(v -> {
            sensorNotify("WWA301924F8BDCE9618");
        });

        //在没有人脸识别摄像头接入的情况下，可以点击按钮简单模拟人脸靠近。当然，也可以点击WhalePlay右上角全局悬浮窗的靠近按钮。
        mMockCameraTv.setOnClickListener(v -> {
            CameraDataBean cameraDataBean = new CameraDataBean();
            CameraDataBean.PersonBean personBean = new CameraDataBean.PersonBean();
            personBean.age = MY_RANDOM.nextInt(60);
            personBean.sex = MY_RANDOM.nextBoolean() ? "male" : "female";
            personBean.faceId = "xxxx";
            personBean.id = MY_RANDOM.nextBoolean() ? "0" : "123";
            personBean.picturePath = "https://icemono.oss-cn-hangzhou.aliyuncs.com/images/art-sprite-girl.jpg";
            cameraDataBean.data = personBean;
            faceIn(cameraDataBean);
        });

        mHttpRequester = new HttpRequester();

        /*申请获取配置信息*/
        Intent intent = new Intent("im.whale.action.GET_CONFIG");
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "请先安装 WhalePlay ", Toast.LENGTH_LONG).show();
        } else {
            startActivityForResult(intent, REQUEST_SN); //REQUEST_SN为自定义int常数
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDeviceMessageReceiver.unregister(this);
        releaseAvatarAnim();
    }

    /**
     * 人脸靠近，简单展示头像
     */
    private void faceIn(CameraDataBean cameraDataBean) {
        if (cameraDataBean == null || cameraDataBean.data == null) {
            return;
        }

        Glide.with(this)
                .load(cameraDataBean.getAvatar())
                .circleCrop()
                .into(mAvatarIv);

        mAvatarIv.animate()
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setDuration(AVATAR_ANIM_MS)
                .setInterpolator(new OvershootInterpolator(1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mAvatarIv.postDelayed(mAvatarDelayRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (mAvatarIv.getScaleX() != 0 || mAvatarIv.getAlpha() != 0) {
                                    mAvatarIv.animate().cancel();
                                    mAvatarIv.animate()
                                            .scaleX(0)
                                            .scaleY(0)
                                            .alpha(0)
                                            .setDuration(AVATAR_ANIM_MS)
                                            .setInterpolator(new AccelerateInterpolator())
                                            .setListener(null)
                                            .start();
                                }
                            }
                        }, 3_000);
                    }
                })
                .start();
    }

    private static final long AVATAR_ANIM_MS = 200;


    private Runnable mAvatarDelayRunnable;

    private void releaseAvatarAnim() {
        mAvatarIv.animate().cancel();
        if (mAvatarDelayRunnable != null)
            mAvatarIv.removeCallbacks(mAvatarDelayRunnable);
    }

    /**
     * 传感器触发。根据传感器sn获取SKU信息并展示。
     *
     * @param sn 传感器的sn
     */
    private void sensorNotify(String sn) {
        mDetailLayout.setVisibility(View.VISIBLE);

        DeviceSnBean deviceSnBean = new DeviceSnBean();
        deviceSnBean.device_sn = sn;
        mHttpRequester.post("https://edge.meetwhale.com/sprite/v1/sku/info", JSON.toJSONString(deviceSnBean), result -> {
            try {
                if (isFinishing())
                    return;
                BaseResult baseResult = JSON.parseObject(result, BaseResult.class);
                if (baseResult.isSuccess()) {
                    SkuDetailBean skuDetailBean = JSON.parseObject(result).getObject("data", SkuDetailBean.class);
                    for (ContentBean contentBean : skuDetailBean.contents) {
                        if (contentBean.isImage()) {
                            Glide.with(this)
                                    .load(contentBean.getSourceUrl())
                                    .into(mDetailIv);
                            break;
                        }
                    }
                    mDetailTitleTv.setText(skuDetailBean.name);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //获取SKU信息失败
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_SN) {
            if (resultCode == RESULT_OK && data != null) {
                /*获取到配置信息*/
                mSn = data.getStringExtra("device_sn"); //设备的sn号
                if (TextUtils.isEmpty(mSn)) {
                    Toast.makeText(this, "sn获取失败", Toast.LENGTH_LONG).show();
                    finish();
                }
                getHomePageVideo();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取待机视频
     */
    private void getHomePageVideo() {
        InitRequestBean initRequestBean = new InitRequestBean();
        initRequestBean.device_sn = mSn;
        mHttpRequester.post("https://edge.meetwhale.com/sprite/v1/init", JSON.toJSONString(initRequestBean), result -> {
            try {
                if (isFinishing())
                    return;
                BaseResult baseResult = JSON.parseObject(result, BaseResult.class);
                if (baseResult.isSuccess()) {
                    InitBean initBean = JSON.parseObject(result).getObject("data", InitBean.class);
                    initVideo(initBean.init_video.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
                //获取视频失败
            }
        });
    }

    private VideoHelper mVideoHelper;

    private void initVideo(String videoUrl) {
        mVideoHelper = new VideoHelper(getLifecycle(), true);
//        mVideoHelper.getPlayer().setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
//        mPlayView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
        mPlayView.setPlayer(mVideoHelper.getPlayer());

        try {
            mVideoHelper.showVideo(videoUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
