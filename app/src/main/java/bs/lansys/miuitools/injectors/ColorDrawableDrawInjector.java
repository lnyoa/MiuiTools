package bs.lansys.miuitools.injectors;

import static bs.lansys.miuitools.utils.FlatColorBarStaticVariables.mStatusBarSize;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import bs.lansys.miuitools.helpers.BitmapCanvas;
import bs.lansys.miuitools.utils.ColorBarUtils;
import bs.lansys.miuitools.utils.FlatColorBarStaticVariables;
import bs.lansys.miuitools.utils.Utils;
import de.robv.android.xposed.XC_MethodHook;

public class ColorDrawableDrawInjector extends XC_MethodHook{

	@Override
	protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
		if (!Utils.isMiuiFlatColorBarEnable()) return;

		Canvas canvas = (Canvas) mhparams.args[0];
		
		if (!(canvas instanceof BitmapCanvas)){
			ColorDrawable mColorDrawable = (ColorDrawable) mhparams.thisObject;
			int color = mColorDrawable.getColor();
			Rect bound = mColorDrawable.getBounds();
			if (Math.abs(mStatusBarSize.height()-bound.height()) >= 2 
					|| (bound.width() != mStatusBarSize.width())){
				
				if (mStatusBarSize.height() == bound.height()  && ColorBarUtils.isTranslucentMask(color)){
					Log.e("xlog", "Bound= <" + bound.left +", " + bound.top + ", " + bound.right + ", " + bound.bottom + ">");
					mhparams.setResult(null);
				}
			}
		}
	}
}
