package com.android.inputmethod.pinyin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.view.WindowManager;

/**
 * Created by root on 9/18/17.
 */

public class KeyboardMapDialog extends Dialog{
    private Bitmap mBitmapW;
    private ViewGroup mViewGroup;
    private  DragView mCurrentView;
    private Button mButtonSave, mButtonEnable;
    private boolean mEnable = false;
    public List<DragView> mDragViewList = new ArrayList<>();
    private Context mContext;
    private WindowManager mWindowManager;

    public KeyboardMapDialog(Context context, WindowManager windowManager) {
        super(context);
        mContext = context;
        mWindowManager = windowManager;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String key = String.valueOf((char)event.getUnicodeChar()).toUpperCase();
        mDragViewList.remove(mCurrentView);
        mViewGroup.removeView(mCurrentView);
        DragView newDragView = createNewDragView(key, mWUpX, mWUpY, false);
        newDragView.key = key;
        mDragViewList.add(newDragView);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mViewGroup = (ViewGroup) View.inflate(mContext, R.layout.mapping_keyboard, null);
        mButtonSave = (Button) mViewGroup.findViewById(R.id.bt);
        mButtonEnable = (Button) mViewGroup.findViewById(R.id.enable);
        ButtonClickListener buttonClickListener = new ButtonClickListener();
        mButtonSave.setOnClickListener(buttonClickListener);
        mButtonEnable.setOnClickListener(buttonClickListener);

        createNewDragView("", 0, 0, true);
    }

    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt:
                    dismiss();
                    break;
                case R.id.enable:
                    mEnable = !mEnable;
                    mButtonEnable.setText(mContext.getString(
                        mEnable ? R.string.keyboard_disable : R.string.keyboard_enable));
                    ((PinyinIME) mContext).setIsKeyboardMap(mEnable);
                    break;
            }
        }
    }

    public boolean isEnable() {
        return mEnable;
    }

    public DragView createNewDragView(String key, int x, int y, boolean isCanReCreate) {
        mBitmapW = buildBitmap(key);
        mCurrentView = new DragView(mContext, mBitmapW, x, y, isCanReCreate);
        mViewGroup.addView(mCurrentView);
        setContentView(mViewGroup);
        return mCurrentView;
    }

    public void showDialog() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.alpha = 0.3f;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        show();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        params.x = 0;
        params.y = 0;
        params.width = mWindowManager.getDefaultDisplay().getWidth();
        params.height = mWindowManager.getDefaultDisplay().getHeight();
        dialogWindow.setAttributes(params);
    }

    public Bitmap buildBitmap(String key) {
        final TextView textView = new TextView(mContext);
        textView.setText(key);
        textView.setTextSize(50);
        textView.setTextColor(Color.GREEN);
        textView.setBackgroundResource(R.drawable.mapping_key_bg_shape);
        textView.setGravity(Gravity.CENTER);
        textView.setDrawingCacheEnabled(true);
        textView.measure(0, 0);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        textView.buildDrawingCache();
        Bitmap bitmap = textView.getDrawingCache();
        return bitmap;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public int mWUpX, mWUpY;
    public class DragView extends View {
        public int mMotionX;
        public int mMotionY;
        private Paint mPaint;
        private Bitmap mBitmap;
        private boolean mIsCanReCreate = false;
        public String  key = "";

        public DragView(Context context, Bitmap bitmap, int x, int y, boolean canReCreate) {
            super(context);
            mPaint = new Paint();
            mBitmap = bitmap;
            mMotionX = x;
            mMotionY = y;
            mIsCanReCreate = canReCreate;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(mBitmap, mMotionX, mMotionY, mPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            boolean isMove = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mCurrentView = this;
                    if (event.getX() <= mMotionX + mBitmap.getWidth()
                            && event.getX() >= mMotionX
                            && event.getY() <= mMotionY + mBitmap.getHeight()
                            && event.getY() >= mMotionY) {
                        isMove = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    isMove = true;
                    mMotionX = (int) event.getX() - mBitmap.getWidth() / 2;
                    mMotionY = (int) event.getY() - mBitmap.getHeight() / 2;
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    if (mIsCanReCreate) {
                        createNewDragView("", 0, 0, mIsCanReCreate);
                    }
                    mWUpX = mMotionX;
                    mWUpY = mMotionY;
                    break;
            }
            return isMove;
        }
    }
}
