package com.demo.floatwindowdemo;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
/**
 * Author:pdm on 2016/10/12
 * Email:aiyh0202@163.com
 */
public class FloatWindowSmall extends LinearLayout {

	/**
	 * 记录小悬浮窗的宽度
	 */
	public static int viewWidth;

	/**
	 * 记录小悬浮窗的高度
	 */
	public static int viewHeight;

	/**
	 * 记录系统状态栏的高度
	 */
	private int statusBarHeight;

	/**
	 * 用于更新小悬浮窗的位置
	 */
	private WindowManager windowManager;
	/**
	 * 小悬浮窗的参数
	 */
	private WindowManager.LayoutParams mParams;

	public static WindowManager.LayoutParams smallParams;

	public FloatWindowSmall(Context context) {
		super(context);
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		//渲染布局
		LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
		View view = findViewById(R.id.small_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		TextView percentView = (TextView) findViewById(R.id.percent);
		percentView.setText(getUsedPercentValue());
		//获取屏幕的高度
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		//获取状态栏的高度
		statusBarHeight = getStatusBarHeight();
	}

	float dx, dy, mx, my;//这里分别记录手按在屏幕上相对屏幕的位置(dx,dy)，以及相对控件本身的位置(mx,my)
	float moveX, moveY;
	//这里用于up和move不冲突
	boolean isMove;
	//屏幕的宽高
	int width, height;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//记住手相对屏幕按下的位置
				dx = event.getRawX();
				dy = event.getRawY();
				//记住手相对控件按下的位置
				mx = event.getX();
				my = event.getY();
				isMove = false;//这里需要设置默认值为false，避免up部分出bug
				return false;
			case MotionEvent.ACTION_MOVE:
				//计算手移动的距离
				int x = Math.abs((int) (event.getRawX() - dx));
				int y = Math.abs((int) (event.getRawY() - dy));
				//如果x和y距离都小于10，说明用户并没打算移动，只是手触摸时产生的move
				if (x < 5 || y < 5) {
					isMove = false;
					return false;
				} else {
					isMove = true;
				}
				//计算控件移动的距离
				x = (int) (event.getRawX() - mx);
				y = (int) (event.getRawY() - my);
				mParams.x = x;

				mParams.y = y - statusBarHeight;
				//刷新
				windowManager.updateViewLayout(this, mParams);
				return true;
			case MotionEvent.ACTION_UP:
				//这里要注意边界问题
				float finalX = event.getRawX();
				float finalY = event.getRawY();
				//控制上边距
				if (finalY < getHeight()) {
					moveX = 0;
					moveY = finalX - my;
				}
				//下边距
				if (finalY > height - getHeight()) {
					moveX = finalX - mx;
					moveY = finalY - getHeight();
				}
				if (finalX - getWidth() / 2 < width / 2) {
					moveX = 0;
					moveY = finalY - my;
				} else if (finalX - getWidth() / 2 > width / 2) {
					moveX = width - getWidth();
					moveY = finalY - my;
				}
				mParams.x = (int) moveX;
				mParams.y = (int) moveY - statusBarHeight;
				if (isMove) {
					smallParams = mParams;
					windowManager.updateViewLayout(this, mParams);
				}else {
					smallParams = mParams;
					openBigWindow();
				}
				return isMove;//false为down，true为move
			default:
				break;
		}
		//这里return ture，说明本次事件已经被处理，不会传给父亲
		return false;
	}

	/**
	 * 将小悬浮窗的参数传入，用于onTouch中更新位置。
	 *
	 * @param params
	 *
	 */
	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
		smallParams = mParams;
	}

	/**
	 * 打开大悬浮窗，同时关闭小悬浮窗。
	 */
	private void openBigWindow() {
		FloatWindowManager.createBigWindow(getContext());
		FloatWindowManager.removeSmallWindow(getContext());
	}

	/**
	 * 这里通过反射的方式获取用于获取状态栏的高度。
	 *
	 * @return 返回状态栏高度的像素值。
	 */
	private int getStatusBarHeight() {
		int statusBarHeight = 0;
		if (statusBarHeight == 0) {
			try {
				//获取字节码文件对象
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				//实例化获得类对象
				Object o = c.newInstance();
				//得到相应成员变量
				Field field = c.getField("status_bar_height");
				//获得成员变量的值
				int x = (Integer) field.get(o);
				statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusBarHeight;
	}
	/**
	 * 计算已使用内存的百分比
	 *
	 * @return 返回百分比。
	 */
	public String getUsedPercentValue() {
		//内存信息文件（CPU信息文件：/proc/cpuinfo）这两个文件是linux系统用来存储内存和CPU信息的
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			//创建读取字符流缓存区
			BufferedReader br = new BufferedReader(fr, 2048);
			//读取第一行字符
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
			memoryLine = br.readLine();
			String availableMemoryLine = memoryLine.substring(memoryLine.indexOf("MemFree:"));
			br.close();
			//获取总的内存,这里需要注意的是replaceAll支持正则表达式"\\D"代表所有的字母字符，只保留数字部分
			long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
			//获取当前可用内存
			long availableSize = Integer.parseInt(availableMemoryLine.replaceAll("\\D+", ""));
			int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
			return percent + "%";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "悬浮窗";
	}
}

