package com.phicomm.hu;


import java.lang.reflect.Method;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity 
{
	/*//定义浮动窗口布局
	//LinearLayout mFloatLayout;
	//创建浮动窗口设置布局参数的对象
	WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
    WindowManager mWindowManager;*/
    //** Called when the activity is first created. 
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //获取WindowManagerImpl.CompatModeWrapper
       /* mWindowManager = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //设置window type
		//wmParams.type = LayoutParams.TYPE_PHONE; 
		//设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888; 
        wmParams.flags = 
//          LayoutParams.FLAG_NOT_TOUCH_MODAL |
          LayoutParams.FLAG_NOT_FOCUSABLE
//          LayoutParams.FLAG_NOT_TOUCHABLE
          ;
        //调整悬浮窗口至右侧中间
        wmParams.gravity = Gravity.LEFT | Gravity.TOP; 
        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;

        // 设置悬浮窗口长宽数据
        wmParams.width = 200;
        wmParams.height = 80;
        
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        
        //浮动窗口布局视图按钮
        Button mFloatView = (Button)mFloatLayout.findViewById(R.id.float_id);
       
        mFloatView.setOnTouchListener(new OnTouchListener() 
        {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				// TODO Auto-generated method stub
				 wmParams.x = (Integer) event.getRawX()-mFloatLayout.getWidth()/2;
	             wmParams.y = (Integer) event.getRawY()-mFloatLayout.getHeight()/2-25;
	             mWindowManager.updateViewLayout(mFloatLayout, wmParams);
				return true;
			}
		});*/
        
        //FloatingWindowActivity的布局视图按钮
        Button start = (Button)findViewById(R.id.start_id);
        
        Button remove = (Button)findViewById(R.id.remove_id);
        
        start.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, FxService.class);
				startService(intent);
				//finish();
			}
		});
        
        remove.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				//uninstallApp("com.phicomm.hu");
				Intent intent = new Intent(MainActivity.this, FxService.class);
				stopService(intent);
			}
		});
        
    }
    
    private void uninstallApp(String packageName)
    {
    	Uri packageURI = Uri.parse("package:"+packageName);
    	Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
    	startActivity(uninstallIntent);
        //setIntentAndFinish(true, true);
    }
    
   /* private void forceStopApp(String packageName) 
    {
    	 ActivityManager am = (ActivityManager)getSystemService(
                 Context.ACTIVITY_SERVICE);
    		 am.forceStopPackage(packageName);
    	 
    	Class c = Class.forName("com.android.settings.applications.ApplicationsState");
    	Method m = c.getDeclaredMethod("getInstance", Application.class);
    	
    	  //mState = ApplicationsState.getInstance(this.getApplication());
    }*/
}