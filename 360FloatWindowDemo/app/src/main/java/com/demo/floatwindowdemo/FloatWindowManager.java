package com.demo.floatwindowdemo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
/**
 * Author:pdm on 2016/10/12
 * Email:aiyh0202@163.com
 */
public class FloatWindowManager {
    /**
     * 小悬浮窗
     */
    private static FloatWindowSmall smallWindow;

    /**
     * 大悬浮窗
     */
    private static FloatWindowBig bigWindow;

    /**
     * 小悬浮窗的params
     */
    private static LayoutParams smallWindowParams;

    /**
     * 大悬浮窗的params
     */
    private static LayoutParams bigWindowParams;

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private static WindowManager mWindowManager;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void createSmallWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        if (smallWindow == null) {
            //这里必须先初始化小悬浮窗
            smallWindow = new FloatWindowSmall(context);
            if (smallWindowParams == null) {
                smallWindowParams = new LayoutParams();
                smallWindowParams.type = LayoutParams.TYPE_PHONE;
                smallWindowParams.format = PixelFormat.RGBA_8888;
                //如何不加这个，则会出现它一直霸占焦点，其他点击事件失效，切记
                smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;//这里相当于确定起点位置
                smallWindowParams.width = FloatWindowSmall.viewWidth;
                smallWindowParams.height = FloatWindowSmall.viewHeight;
                smallWindowParams.x = dm.widthPixels;
                smallWindowParams.y = dm.heightPixels / 2 - smallWindowParams.height / 2;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);
        }
    }

    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }

    /**
     * 创建一个大悬浮窗。位置根据小悬浮窗确定。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void createBigWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        if (bigWindow == null) {
            //这里必须先初始化大悬浮窗
            bigWindow = new FloatWindowBig(context);
        }
        //参数是变动的，所以每一次开启都必须更新
        bigWindowParams = new LayoutParams();
        bigWindowParams.type = LayoutParams.TYPE_PHONE;
        bigWindowParams.format = PixelFormat.RGBA_8888;
        bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
        bigWindowParams.width = FloatWindowBig.viewWidth;
        bigWindowParams.height = FloatWindowBig.viewHeight;
        //如何不加这个，则会出现它一直霸占焦点，其他点击事件失效，切记
        bigWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE;
        //这里是根据小悬浮窗的位置来确定大悬浮窗的位置，当然我们这里强制聚焦，所以屏蔽掉
        bigWindowParams.x = FloatWindowSmall.smallParams.x + FloatWindowSmall.viewWidth -
                bigWindowParams.width;
        bigWindowParams.y = FloatWindowSmall.smallParams.y + FloatWindowSmall.viewHeight / 2
                - bigWindowParams.height / 2;
        //如果你想实现类似苹果手机的快捷操作悬浮窗效果，可以让他显示在中间位置
//        bigWindowParams.x = dm.widthPixels/2 - bigWindowParams.width/2;
//        bigWindowParams.y = dm.heightPixels/2 - bigWindowParams.height /2;
        windowManager.addView(bigWindow, bigWindowParams);
    }

    /**
     * 将大悬浮窗从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeBigWindow(Context context) {
        if (bigWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(bigWindow);
            bigWindow = null;
        }
    }

    /**
     * 更新小悬浮窗的TextView上的数据，显示内存使用的百分比。
     */
    public static void updateUsedPercent() {
        if (smallWindow != null) {
            TextView percentView = (TextView) smallWindow.findViewById(R.id.percent);
            percentView.setText(smallWindow.getUsedPercentValue());
        }
    }

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return smallWindow != null || bigWindow != null;
    }

    /**
     * @param context 必须为getApplicationContext().
     * @return WindowManager的实例
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }
}
