package bs.lansys.miuitools.hooks;

import java.security.MessageDigest;
import java.util.ArrayList;

import com.android.server.pm.PackageManagerService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import bs.lansys.miuitools.injectors.ActivityInjector;
import bs.lansys.miuitools.injectors.ArrayListAddInjector;
import bs.lansys.miuitools.injectors.ColorDrawableDrawInjector;
import bs.lansys.miuitools.injectors.MiuiGlobalActionsInjectors;
import bs.lansys.miuitools.injectors.ThemeInjector;
import bs.lansys.miuitools.injectors.ToastInjector;
import bs.lansys.miuitools.injectors.ViewDrawInjector;
import bs.lansys.miuitools.injectors.ViewSetBackgroundDrawableInjector;
import bs.lansys.miuitools.utils.Consts;
import bs.lansys.miuitools.utils.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class framework {
	
	public static void doActivityHook() {
		XposedBridge.hookAllConstructors(Activity.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				XposedHelpers.setAdditionalInstanceField(param.thisObject, "mActivityInjector", new ActivityInjector(param.thisObject));
			}
		});
		
		XposedHelpers.findAndHookMethod(Activity.class, "onPostCreate", Bundle.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				ActivityInjector mActivityInjector = (ActivityInjector) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "mActivityInjector");
				mActivityInjector.hookAfterOnPostCreate();
			}
		});
		
		XposedHelpers.findAndHookMethod(Activity.class, "performResume", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				ActivityInjector mActivityInjector = (ActivityInjector) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "mActivityInjector");
				mActivityInjector.hookAfterPerformResume();
			}
		});
		
		XposedHelpers.findAndHookMethod(Activity.class, "onPause", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				ActivityInjector mActivityInjector = (ActivityInjector) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "mActivityInjector");
				mActivityInjector.hookAfterOnPause();
			}
		});
		
		
		XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				ActivityInjector mActivityInjector = (ActivityInjector) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "mActivityInjector");
				mActivityInjector.hookAfterOnWindowFocusChanged((Boolean)mhparams.args[0]);
			}
		});
		
		XposedHelpers.findAndHookMethod(Activity.class, "startActivity", Intent.class, new XC_MethodHook(){
			
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				ActivityInjector mActivityInjector = (ActivityInjector) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "mActivityInjector");
				Intent i = mActivityInjector.hookBeforeStartActivities((Intent)mhparams.args[0]);
				if (i != null) mhparams.args[0] = i;
			}
		});
		
	}
	
	public static void doSecurityServerHook(ClassLoader loader) {
		final Class<?> pms = XposedHelpers.findClass("com.android.server.pm.PackageManagerService", loader);
		XposedBridge.hookAllMethods(pms, "compareSignatures", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param)
                    throws Throwable {
                    param.setResult(PackageManager.SIGNATURE_MATCH);
                }
            });
		
		final Class<?> sms = XposedHelpers.findClass("com.miui.server.SecurityManagerService", loader);
		XposedBridge.hookAllMethods(sms, "checkSystemSelfProtection", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param)
                    throws Throwable {
                    param.setResult(null);
                }
            });
		
		XposedBridge.hookAllMethods(MessageDigest.class, "isEqual", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param)
                    throws Throwable {
                    param.setResult(true);
                }
            });
	}
	
	public static void  doDrmManagerHook(){
		ThemeInjector.doDrmManagerHook();
	}
	
	public static void doViewHook(){
	
		XposedHelpers.findAndHookMethod(View.class, "draw", Canvas.class, new ViewDrawInjector());
		XposedHelpers.findAndHookMethod(View.class, "setBackgroundDrawable", Drawable.class, new ViewSetBackgroundDrawableInjector());
	}
	
	public static void doColorDrawableHook(){
		XposedHelpers.findAndHookMethod(ColorDrawable.class, "draw", Canvas.class, new ColorDrawableDrawInjector());
	}
	
	
	public static void doToastHook(){
		XposedBridge.hookAllConstructors(Toast.class,new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable{
				XposedHelpers.setAdditionalInstanceField(param.thisObject, "mToastInjector", new ToastInjector(param.thisObject));
			}
		});
		
		XposedHelpers.findAndHookMethod(Toast.class, "show", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				ToastInjector mToastInjector= (ToastInjector) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "mToastInjector");
				mToastInjector.HookbeforeShow();
			}
		});
		
		XposedHelpers.findAndHookMethod(Toast.class, "makeText", Context.class, CharSequence.class, Integer.TYPE, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				Toast mToast = ToastInjector.HookAfterMakeText((Context)mhparams.args[0], (CharSequence)mhparams.args[1], (Integer)mhparams.args[2]);
				if (mToast != null) mhparams.setResult(mToast);
			}
		});
	}

	public static void doArrayListHook(){
		XposedBridge.hookAllMethods(ArrayList.class, "add", new ArrayListAddInjector());
	}
	
	public static void doMiuiGlobalActionsHook(ClassLoader loader){
		final Class<?> mga = XposedHelpers.findClass(Consts.CLASS_MiuiGlobalActions1, loader);
		XposedBridge.hookAllMethods(mga, "onCommand", new MiuiGlobalActionsInjectors());
		
	}
}
