package im.whale.whaleplaysdk.demo;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseFullScreenActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    private View mRootView;
    private boolean isSoftKeyboardOpened = false;
    private int mMiniKeyboardHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMiniKeyboardHeight = 150;
        hideSystemUI();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideSystemUI(getWindow());
    }

    protected void hideSystemUI() {
        hideSystemUI(getWindow());
        mRootView = getWindow().getDecorView();
        if (mRootView != null)
            mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    protected void windowFocus(boolean hasFocus) {
        if (hasFocus)
            hideSystemUI(getWindow());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        windowFocus(hasFocus);
    }


    @Override
    public void onGlobalLayout() {
        if (mRootView == null)
            return;

        /* 输入框收起后隐藏导航栏 */
        final Rect r = new Rect();
        //r will be populated with the coordinates of your view that area still visible.
        mRootView.getWindowVisibleDisplayFrame(r);

        final int heightDiff = mRootView.getRootView().getHeight() - (r.bottom - r.top);
        if (!isSoftKeyboardOpened && heightDiff > mMiniKeyboardHeight) { // if more than 100 pixels, its probably a keyboard...
            isSoftKeyboardOpened = true;
            onSofKeyboardOpen();
        } else if (isSoftKeyboardOpened && heightDiff < mMiniKeyboardHeight) {
            isSoftKeyboardOpened = false;
            onSofKeyboardClose();
        }
    }

    protected void onSofKeyboardOpen() {
        // open
    }

    protected void onSofKeyboardClose() {
        // close
        hideSystemUI(getWindow());
    }

    public static void hideSystemUI(Window window) {
        if (window == null)
            return;
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}
