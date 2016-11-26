package bs.lansys.miuitools.injectors;

import java.io.File;

import android.content.Context;
import bs.lansys.miuitools.utils.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import miui.drm.DrmManager;

public class ThemeInjector {
	public static void doDrmManagerHook() {
		XposedHelpers.findAndHookMethod(DrmManager.class, "isLegal", Context.class, String.class,
				"miui.drm.DrmManager$RightObject", new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
						if (Utils.isThemeCrack())
							mhparams.setResult(DrmManager.DrmResult.DRM_SUCCESS);
					}
				});

		XposedHelpers.findAndHookMethod(DrmManager.class, "isLegal", Context.class, File.class, File.class,
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
						if (Utils.isThemeCrack())
							mhparams.setResult(DrmManager.DrmResult.DRM_SUCCESS);
					}
				});

		XposedHelpers.findAndHookMethod(DrmManager.class, "isLegal", Context.class, String.class, File.class,
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
						if (Utils.isThemeCrack())
							mhparams.setResult(DrmManager.DrmResult.DRM_SUCCESS);
					}
				});

		XposedHelpers.findAndHookMethod(DrmManager.class, "isRightsFileLegal", File.class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				if (Utils.isThemeCrack())
					mhparams.setResult(true);
			}
		});

		XposedHelpers.findAndHookMethod(DrmManager.class, "isPermanentRights", File.class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				if (Utils.isThemeCrack())
					mhparams.setResult(true);
			}
		});
		XposedHelpers.findAndHookMethod(DrmManager.class, "isPermanentRights", "miui.drm.DrmManager$RightObject",
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
						if (Utils.isThemeCrack())
							mhparams.setResult(true);
					}
				});
	}
	
	public static void doThemeManagerHook(ClassLoader loader) {
		String ClassName = Utils.getMIUIVersion() >= 8 ? "com.android.thememanager.util.ThemeOperationHandler"
				: "miui.resourcebrowser.view.ResourceOperationHandler";

		Class<?> clazz = XposedHelpers.findClass(ClassName, loader);

		XposedHelpers.findAndHookMethod(clazz, "isAuthorizedResource", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				if (Utils.isThemeCrack())
					mhparams.setResult(true);
			}
		});

		XposedHelpers.findAndHookMethod(clazz, "isLegal", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				if (Utils.isThemeCrack())
					mhparams.setResult(true);
			}
		});

		XposedHelpers.findAndHookMethod(clazz, "isPermanentRights", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				if (Utils.isThemeCrack())
					mhparams.setResult(true);
			}
		});
	}
	
}
