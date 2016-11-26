package bs.lansys.miuitools.injectors;

import static bs.lansys.miuitools.utils.ColorBarUtils.setMiuiColorBar;
import static bs.lansys.miuitools.utils.FlatColorBarStaticVariables.mToastBackColor;

import java.lang.reflect.Method;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.android.internal.app.WindowDecorActionBar;

import bs.lansys.miuitools.helpers.BitmapCanvas;
import bs.lansys.miuitools.utils.BitMapColor;
import bs.lansys.miuitools.utils.ColorBarUtils;
import bs.lansys.miuitools.utils.Consts;
import bs.lansys.miuitools.utils.FlatColorBarStaticVariables;
import bs.lansys.miuitools.utils.Utils;
import bs.lansys.miuitools.utils.WindowType;
import bs.lansys.miuitools.utils.BitMapColor.Type;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class ViewDrawInjector extends XC_MethodHook{

	@Override
	protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
		if (!Utils.isMiuiFlatColorBarEnable()) return;
		//if (FlatColorBarStaticVariables.hasHookSina == 1) return;

		View mView = (View) mhparams.thisObject;

		Canvas canvas = (Canvas) mhparams.args[0];

		if (canvas instanceof BitmapCanvas)
			return;
		
		WindowType mWindowType = (WindowType) XposedHelpers.getAdditionalInstanceField(mView, "mWindowType");

		mWindowType = (mWindowType == null ? WindowType.UNKOWN : mWindowType);

		if (mWindowType != WindowType.UNKOWN && mWindowType != WindowType.MIUISTYLE && mWindowType != WindowType.FLOAT) {
			Activity mActivity = (Activity) XposedHelpers.getAdditionalInstanceField(mView, "xactivity");
			ActionBar  actionBar = mActivity.getActionBar();
			if (actionBar != null) {
				if (actionBar.isShowing() && actionBar instanceof WindowDecorActionBar) {
					int color = ColorBarUtils.getActionBarColor(mActivity);
					ColorBarUtils.setStatusBarColor(mActivity, color);
					ColorBarUtils.setActionBarColor(mActivity, color);
					mToastBackColor = color;
				}
				return;
			}


			BitmapCanvas newCanvas = (BitmapCanvas) XposedHelpers.getAdditionalInstanceField(mView, "newCanvas");

			if (newCanvas == null) {
				newCanvas = new BitmapCanvas(mView, canvas);
			}

			// Get an image of the view
			// Do not use the "getDrawingCache" because
			// We do not need image of the entire view.
			mView.draw(newCanvas);
			
			setMiuiColorBar(mActivity, mWindowType, newCanvas.getBitmap());

			XposedHelpers.setAdditionalInstanceField(mView, "newCanvas", newCanvas);
			
			// We must mask the view as dirty, or we will never see it
			// flush
			//mView.invalidate();
			
			//((Method) mhparams.method).invoke(mhparams.thisObject, canvas);
		}
	}
}
