package bs.lansys.miuitools;

import android.os.SystemProperties;

import bs.lansys.miuitools.hooks.PackageHooks;
import bs.lansys.miuitools.hooks.framework;
import bs.lansys.miuitools.injectors.ThemeInjector;
import bs.lansys.miuitools.utils.Consts;
import bs.lansys.miuitools.utils.Utils;
import de.robv.android.xposed.IXposedHookCmdInit;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import miui.os.Build;

public class MIUITools implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources{

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		// TODO Auto-generated method stub
		
		ClassLoader loader = lpparam.classLoader;
		String packageName = lpparam.packageName;
		if (packageName.equals(Consts.PKG_THEMEMANAGER)) {
			ThemeInjector.doThemeManagerHook(loader);
		}
		else if (packageName.equals(Consts.PKG_ANDROID)){
			framework.doSecurityServerHook(loader);
			framework.doMiuiGlobalActionsHook(loader);
		}
		else if (packageName.equals(Consts.PKG_DownloadProviderUi)){
			PackageHooks.ADConfigs.HookDownloadPrividerUi(loader);
		}
		else if (packageName.equals(Consts.PKG_SETTINGS)){
			PackageHooks.Settings.HookOtherPersonalSettingsOnCreate(loader);
		}
		else if (packageName.equals(Consts.PKG_SYSTEMUI)){
			PackageHooks.SystemUI.HookSecondsClock(loader);
		}
		else if (packageName.equals(Consts.PKG_SINAWB)){
			PackageHooks.SinaWeiBo.Hook(loader);
		}
		else if (packageName.equals(Consts.PKG_WEATHER)){
			PackageHooks.ADConfigs.HookWeatherToolUtils(loader);
		}
		else if (packageName.equals(Consts.PKG_CLEANMASTER)){
			PackageHooks.ADConfigs.HookCleanmasterDataModel(loader);
		}
		else if (packageName.equals(Consts.PKG_MUSIC)){
			PackageHooks.Music.HookMuiscConfiguration(loader);
		}
		else if (packageName.equals(Consts.PKG_VIDEO)){
			PackageHooks.Video.Hook(loader);
		}
		
		XposedHelpers.setStaticObjectField(Build.class, "IS_STABLE_VERSION", false);
		Class<?> clazz = XposedHelpers.findClass("android.os.Build$VERSION", null);
		XposedHelpers.setStaticObjectField(clazz, "INCREMENTAL", Utils.getIncremental() + ".by.lansys");
	}

	@Override
	public void initZygote(StartupParam spparam) throws Throwable {
		// TODO Auto-generated method stub
		framework.doActivityHook();
		framework.doViewHook();
		framework.doToastHook();
		framework.doColorDrawableHook();
		framework.doDrmManagerHook();
		framework.doArrayListHook();
	}

	@Override
	public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable{
		if (resparam.packageName.equals("com.android.systemui")) {
			resparam.res.setReplacement(resparam.packageName, "bool", "config_show_statusbar_search", Boolean.valueOf(false));
		}
	}

}
