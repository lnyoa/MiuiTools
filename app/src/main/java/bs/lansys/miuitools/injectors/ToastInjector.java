package bs.lansys.miuitools.injectors;

import static bs.lansys.miuitools.utils.FlatColorBarStaticVariables.mToastBackColor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.SystemProperties;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import bs.lansys.miuitools.helpers.ImageToastView;
import bs.lansys.miuitools.utils.ColorBarUtils;
import bs.lansys.miuitools.utils.Consts;
import bs.lansys.miuitools.utils.Utils;

public class ToastInjector {
	private static int Animation_resID;
	private static int message_resID;
	
	Toast nToast;
	
	public ToastInjector(Object toast){
		nToast = (Toast) toast;
	}
	
	public void HookbeforeShow(){
		if (!Utils.isMiuiFlatColorBarEnable()) return;
		if (!Utils.isColorToastEnabled()) return;

		View v = nToast.getView();
		if (v == null) return;
		Context mContext = v.getContext();
		if (Animation_resID== 0) {
			Animation_resID = mContext.getResources().getIdentifier("Animation.InputMethod", "style", "android");
		}
		
		int txtcolor = ColorBarUtils.isBrightColor(mToastBackColor)? Color.BLACK :Color.WHITE;

		if ((v instanceof ImageToastView)){
			((ImageToastView) v).setColor(txtcolor, mToastBackColor, 1.0f);
			nToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 1);
			((ImageToastView) v).setToastAnimation(Animation_resID);
        }else{
        	if (message_resID == 0) {
        		message_resID = mContext.getResources().getIdentifier("message", "id", "android");
            }
            TextView tv = (TextView)v.findViewById(message_resID);
            if (tv == null) {
                tv = ColorBarUtils.getTextView(v);
             }
            
            if (tv != null){
            	Drawable icon = mContext.getPackageManager().getApplicationIcon(mContext.getApplicationInfo());
            	ImageToastView itv = new ImageToastView(mContext, tv);
                itv.initToast(icon, null, txtcolor, mToastBackColor, 1.0f);
                itv.setToastAnimation(Animation_resID);
                nToast.setView(itv);
                nToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 1);
            }
        }
	}
	
	public static Toast HookAfterMakeText(Context context, CharSequence txt, int duration){
		if (!Utils.isMiuiFlatColorBarEnable()) return null;
		if (!Utils.isColorToastEnabled()) return null;
		Toast toast = new Toast(context);
		Drawable icon = context.getPackageManager().getApplicationIcon(context.getApplicationInfo());
	    if (Animation_resID == 0) {
	    	Animation_resID = context.getResources().getIdentifier("Animation.InputMethod", "style", "android");
	    }
	    
	    int txtcolor = ColorBarUtils.isBrightColor(mToastBackColor)? Color.BLACK :Color.WHITE;
	    
	    ImageToastView itv = new ImageToastView(context, null);
	    itv.initToast(icon, txt, txtcolor, mToastBackColor, 1.0f);
	    itv.setToastAnimation(Animation_resID);
	    toast.setView(itv);
	    toast.setDuration(duration);
	    toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 1);
	    return toast;
	}
}
