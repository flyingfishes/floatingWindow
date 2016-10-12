package com.phicomm.hu;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * Author:pdm on 2016/10/11
 * Email:aiyh0202@163.com
 */
public class MainActivity extends BaseActivity
{
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button start = (Button)findViewById(R.id.start_id);
        
        Button remove = (Button)findViewById(R.id.remove_id);
        
        start.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, FwService.class);
				startService(intent);
			}
		});
        
        remove.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(MainActivity.this, FwService.class);
				stopService(intent);
			}
		});
        
    }
}
