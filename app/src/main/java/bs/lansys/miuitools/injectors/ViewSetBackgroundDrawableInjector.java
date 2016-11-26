package bs.lansys.miuitools.injectors;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import bs.lansys.miuitools.utils.Utils;
import de.robv.android.xposed.XC_MethodHook;

public class ViewSetBackgroundDrawableInjector extends XC_MethodHook{
	private static String DecorViewClass = "com.android.internal.policy.impl.PhoneWindow$DecorView";
	
	@Override
	protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
		if (!Utils.isMiuiFlatColorBarEnable()) return;

		View v = (View) mhparams.thisObject;
		
		Drawable background = (Drawable) mhparams.args[0];
		
		if (v.getClass().getName().equals(DecorViewClass)){
			if (background == null ){
				mhparams.args[0] = new ColorDrawable(Color.TRANSPARENT);
			}
		}
	}
}
