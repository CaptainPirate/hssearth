package com.hss.saveEarth.game;

import java.util.Timer;
import java.util.TimerTask;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hss.saveEarth.R;
import com.umeng.analytics.MobclickAgent;

public class InitActivity extends Activity implements OnClickListener{
Button playGameBtn,infoGameBtn;

/**绘制动画对象**/  
AnimationDrawable animationDrawable = null; 
ImageView logo;
TextView logo_text;
	private Timer timer;
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case 100:
				initAD();
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题�?
		 
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息�?
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init);
		AdManager.getInstance(this).init("7cb5131e250aee87", "dd54ba69514ac82f", false);
		SpotManager.getInstance(this).loadSpotAds();
		// 插屏出现动画效果�?:ANIM_NONE为无动画�?:ANIM_SIMPLE为简单动画效果，2:ANIM_ADVANCE为高级动画效�?
		SpotManager.getInstance(this).setAnimationType(SpotManager.ANIM_ADVANCE);
		// 设置插屏动画的横竖屏展示方式，如果设置了横屏，则在有广告资源的情况下会是优先使用横屏图�?
		SpotManager.getInstance(this).setSpotOrientation(
				SpotManager.ORIENTATION_PORTRAIT);
		playGameBtn = (Button) findViewById(R.id.playGameBtn);
		infoGameBtn = (Button) findViewById(R.id.infoGameBtn);
		playGameBtn.setOnClickListener(this);
		infoGameBtn.setOnClickListener(this);
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Message message = new Message();
				message.what = 100;
				handler.sendMessage(message);
			}
		}, 3000);
		
		logo_text = (TextView) findViewById(R.id.logo_text);
		//Shader shader =new LinearGradient(0, 0, 50, 120, 0xffFF83FA, 0xfff42946, TileMode.CLAMP);//158fed
		Shader shader =new LinearGradient(0, 0, 50, 120, 0xff469cdf, 0xff5ae6f7, TileMode.CLAMP);//158fed
		logo_text.getPaint().setShader(shader);
		logo = (ImageView) findViewById(R.id.logoanim);
		animationDrawable = (AnimationDrawable) logo.getBackground();
		animationDrawable.start();
		
	}

	private void initAD(){
		// 展示插播广告，可以不调用loadSpot独立使用
				SpotManager.getInstance(InitActivity.this).showSpotAds(
						InitActivity.this, new SpotDialogListener() {
							@Override
							public void onShowSuccess() {
								Log.i("YoumiAdDemo", "展示成功");
							}

							@Override
							public void onShowFailed() {
								Log.i("YoumiAdDemo", "展示失败");
							}

							@Override
							public void onSpotClosed() {
								Log.i("YoumiAdDemo", "展示关闭");
							}

						}); // //
		 
	}
	@SuppressLint("NewApi")
	private void into() { 
		Intent mainIntent = new Intent(InitActivity.this, GameActivity.class);
		InitActivity.this.startActivity(mainIntent);
		InitActivity.this.finish();
		overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		 switch (v.getId()) {
		case R.id.playGameBtn:
			into();//hss tag begin
			break;
			
		case R.id.infoGameBtn:
//			Intent mainIntent = new Intent(InitActivity.this, InfoActivity.class);
//			InitActivity.this.startActivity(mainIntent);
			ContextThemeWrapper themedContext;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				themedContext = new ContextThemeWrapper(InitActivity.this,
						android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
			} else {
				themedContext = new ContextThemeWrapper(InitActivity.this,
						android.R.style.Theme_Light_NoTitleBar);
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(themedContext);
			builder.setTitle("游戏规则");
			builder.setMessage(getResources().getString(R.string.game_guize));
			builder.setPositiveButton("确定", null);
//			builder.setNegativeButton("取消", null);
			builder.create().show();
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					Message message = new Message();
					message.what = 100;
					handler.sendMessage(message);
				}
			}, 1000);
			break;
		default:
			break;
		}
		
	}
	@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			MobclickAgent.onResume(this);
			initAD();
			/**
			 * 设置为竖�?
			 */
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
	@Override
	protected void onPause() {
		super.onPause();
		initAD();
		MobclickAgent.onPause(this);
		}
	// 请务必加上词句，否则进入网页广告后无法进去原sdk
			@Override
			protected void onActivityResult(int requestCode, int resultCode, Intent data) {
				super.onActivityResult(requestCode, resultCode, data);

				if (resultCode == 10045) {
//					Intent intent = new Intent(SplashSpotActivity.this, YoumiNormalAdsDemo.class);
//					startActivity(intent);
					finish();
				}
			}

//			@Override
//			public void onBackPressed() {
//				// 取消后�?�?
//			}
//
//			@Override
//			protected void onResume() {
//
//				/**
//				 * 设置为竖�?
//				 */
//				if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//				}
//				super.onResume();
//			}

			@Override
			public void onConfigurationChanged(Configuration newConfig) {
				super.onConfigurationChanged(newConfig);
				if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					// land
				} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					// port
				}
			}
}
