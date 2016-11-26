package bs.lansys.miuitools.injectors;

import static bs.lansys.miuitools.utils.ColorBarUtils.setMiuiColorBar;
import static bs.lansys.miuitools.utils.FlatColorBarStaticVariables.mStatusBarSize;
import static bs.lansys.miuitools.utils.FlatColorBarStaticVariables.mToastBackColor;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.internal.app.WindowDecorActionBar;

import bs.lansys.miuitools.helpers.BitmapCanvas;
import bs.lansys.miuitools.utils.ColorBarUtils;
import bs.lansys.miuitools.utils.FlatColorBarStaticVariables;
import bs.lansys.miuitools.utils.Utils;
import bs.lansys.miuitools.utils.WindowType;
import de.robv.android.xposed.XposedHelpers;

public class ActivityInjector {

	private Activity mActivity;
	private WindowType mWindowType = WindowType.UNKOWN;
	
	private boolean needGetColorInFocus = true;
	
	public ActivityInjector(Object activity){
		mActivity = (Activity) activity;
	}

	public void hookAfterOnPostCreate() {
		if (!Utils.isMiuiFlatColorBarEnable()) return;
		final Window window = mActivity.getWindow();

		mWindowType = ColorBarUtils.getWindowType(mActivity);

		final ViewGroup decor = (ViewGroup) window.getDecorView();
		
		mStatusBarSize.set(0, 0, ColorBarUtils.getStatusBarWidth(mActivity), ColorBarUtils.getStatusBarHeight(mActivity));
		
		XposedHelpers.setAdditionalInstanceField(decor, "xactivity", mActivity);
		XposedHelpers.setAdditionalInstanceField(decor, "mWindowType", mWindowType);
	}
	
	public void hookAfterPerformResume() {
		if (!Utils.isMiuiFlatColorBarEnable()) return;
		if (mWindowType == WindowType.UNKOWN){
			mWindowType = ColorBarUtils.getWindowType(mActivity);
			final ViewGroup decor = (ViewGroup) mActivity.getWindow().getDecorView();
			XposedHelpers.setAdditionalInstanceField(decor, "xactivity", mActivity);
			XposedHelpers.setAdditionalInstanceField(decor, "mWindowType", mWindowType);
		}

		if (mWindowType == WindowType.NORMAL){
			ActionBar actionBar = mActivity.getActionBar();
			if (actionBar != null && actionBar.isShowing() && actionBar instanceof WindowDecorActionBar) {
				int color = ColorBarUtils.getActionBarColor(mActivity);
				if (color != Color.TRANSPARENT){
					ColorBarUtils.setStatusBarColor(mActivity, color);
					ColorBarUtils.setActionBarColor(mActivity, color);
					mToastBackColor = color;
					needGetColorInFocus = false;
				}
			}
		}
	}
	
	public void hookAfterOnPause() {
		if (mWindowType != WindowType.UNKOWN && mWindowType != WindowType.MIUISTYLE && mWindowType == WindowType.FLOAT) {
			final View decor = mActivity.getWindow().getDecorView();
			BitmapCanvas newCanvas = (BitmapCanvas) XposedHelpers.getAdditionalInstanceField(decor, "newCanvas");
			if (newCanvas!=null){
				newCanvas.recycle();
				XposedHelpers.setAdditionalInstanceField(decor, "newCanvas", null);
			}
		}
	}
	
	
	public void hookAfterOnWindowFocusChanged(boolean focus) {
		if (!focus || !needGetColorInFocus) return;
		if (!Utils.isMiuiFlatColorBarEnable()) return;
		if (mWindowType == WindowType.UNKOWN){
			mWindowType = ColorBarUtils.getWindowType(mActivity);
			final ViewGroup decor = (ViewGroup) mActivity.getWindow().getDecorView();
			XposedHelpers.setAdditionalInstanceField(decor, "xactivity", mActivity);
			XposedHelpers.setAdditionalInstanceField(decor, "mWindowType", mWindowType);
		}

		if (mWindowType == WindowType.NORMAL || mWindowType == WindowType.MIUISTYLE) {
			final Bitmap bitmap = ColorBarUtils.loadBitmapFromActivity(mActivity, 15, true);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setMiuiColorBar(mActivity, mWindowType, bitmap);
					bitmap.recycle();
				}
			},200L);
		}
		if (Utils.isShowDebugMsg())
			Toast.makeText(mActivity, "WindowType is " + mWindowType , Toast.LENGTH_SHORT).show();
	}
	
	public Intent hookBeforeStartActivities(Intent intent){
		if (Utils.isHaveSuperSu(mActivity)){
			ComponentName RootAcquiredActivity = new ComponentName("com.android.updater", "com.miui.permcenter.root.RootAcquiredActivity");
			Intent PERMISSION_CENTER_SECURITY_WEB_VIEW = new Intent("miui.intent.action.PERMISSION_CENTER_SECURITY_WEB_VIEW");
			ComponentName cp = intent.getComponent();

			if ((cp != null && cp.equals(RootAcquiredActivity)) || intent.equals(PERMISSION_CENTER_SECURITY_WEB_VIEW)){
				ComponentName cn = new ComponentName("eu.chainfire.supersu", "eu.chainfire.supersu.MainActivity");
				Intent i = new Intent().setComponent(cn);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				return i;
			}
		}
		
		return null;
	}
}
