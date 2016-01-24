package com.hss.savaEarth.impl;

import com.hss.savaEarth.Vibration;

import android.os.Vibrator;
import android.content.Context;

/**
 * 震动
 * @author Administrator
 *
 */
public class AndroidVibration implements Vibration
{
	Vibrator vibrator;

	public AndroidVibration(Context context)
	{
		vibrator = 
			(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public void vibrate(int time)
	{
			vibrator.vibrate((long)(time));
	}
}
