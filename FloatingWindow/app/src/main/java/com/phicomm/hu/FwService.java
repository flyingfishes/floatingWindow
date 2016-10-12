package com.phicomm.hu;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
 * Author:pdm on 2016/10/11
 * Email:aiyh0202@163.com
 */
public class FwService extends Service {
    private static ActivityManager mActivityManager;
    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    static WindowManager  mWindowManager;
    Button mFloatView;
    private static final String TAG = "FwService";
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "oncreat");
        createFloatView();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private int width, height;

    private void createFloatView() {
        wmParams = new LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //获取屏幕的高度
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        //设置window type
        wmParams.type = LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;

        //调整悬浮窗显示的停靠位置为右侧侧置顶，方便实现触摸滑动
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = width;
        wmParams.y = height/2;

        //设置悬浮窗口长宽数据
        wmParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        wmParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);

        mFloatView = (Button) mFloatLayout.findViewById(R.id.float_id);
//        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        //设置layout大小
//        mFloatLayout.measure(w, h);
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
        mFloatView.setText(getUsedPercentValue(getApplicationContext()));
        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new OnTouchListener() {
            float dx, dy, mx, my;
            float moveX, moveY;
            //这里用于up和move不冲突
            boolean isMove;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //记住手按下的位置
                        dx = event.getRawX();
                        dy = event.getRawY();
//                        mx = v.getWidth()/2;
//                        my = v.getHeight()/2;
                        //计算手相对控件本身按下的位置
                        mx = event.getX();
                        my = event.getY();
                        isMove = false;//这里需要设置默认值为false，避免up部分出bug
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        //计算手移动的距离
                        int x = Math.abs((int) (event.getRawX() - dx));
                        int y = Math.abs((int) (event.getRawY() - dy));
                        //如果x和y距离都小于5，说明用户并没打算移动，只是手触摸时产生的move
                        if (x < 5 || y < 5) {
                            isMove = false;
                            return false;
                        } else {
                            isMove = true;
                        }
                        //计算控件移动的距离
                        x = (int) (event.getRawX() - mx);
                        y = (int) (event.getRawY() - my);
                        wmParams.x = x;
                        //25为状态栏的高度
                        wmParams.y = y;
                        //刷新
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        return true;
                    case MotionEvent.ACTION_UP:
                        //这里要注意边界问题
                        float finalX = event.getRawX();
                        float finalY = event.getRawY();
                        //控制上边距
                        if (finalY < v.getHeight()) {
                            moveX = 0;
                            moveY = finalX - my;
                        }
                        //下边距
                        if (finalY > height - v.getHeight()) {
                            moveX = finalX - mx;
                            moveY = finalY - v.getHeight();
                        }
                        //判断控件改停留在左边距还是右边距
                        if (finalX - v.getWidth() / 2 < width / 2) {
                            moveX = 0;
                            moveY = finalY - my;
                        } else if (finalX - v.getWidth() / 2 > width / 2) {
                            moveX = width - v.getWidth();
                            moveY = finalY - my;
                        }
                        wmParams.x = (int) moveX;
                        wmParams.y = (int) moveY;
                        if (isMove) {
                            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        }
                        return isMove;//false为down，true为move
                    default:
                        break;
                }
                //这里return ture，说明本次事件已经被处理，不会传给父亲
                return false;
            }
        });

        mFloatView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(FwService.this, "onClick", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 计算已使用内存的百分比，并返回。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 已使用内存的百分比，以字符串形式返回。
     */
    public static String getUsedPercentValue(Context context) {
        //内存信息文件（CPU信息文件：/proc/cpuinfo）这两个文件是linux系统用来存储内存和CPU信息的
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            //创建读取字符流缓存区
            BufferedReader br = new BufferedReader(fr, 2048);
            //读取第一行字符
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            //获取总的内存,这里需要注意的是replaceAll支持正则表达式"\\D"代表所有的字母字符，只保留数字部分
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            //获取当前可用内存
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            return percent + "%";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "悬浮窗";
    }
    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 当前可用内存。
     */
    private static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        long currentMemory = mi.availMem;
        return currentMemory;
    }
    /**
     * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return ActivityManager的实例，用于获取手机可用内存。
     */
    private static ActivityManager getActivityManager(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }

}
