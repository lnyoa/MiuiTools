package bs.lansys.miuitools.hooks;

import android.content.Context;
import android.os.SystemProperties;

import java.util.Map;

import bs.lansys.miuitools.injectors.SecondsClockInjector;
import bs.lansys.miuitools.injectors.SettingsInjector;
import bs.lansys.miuitools.injectors.SinaImmersiveInjector;
import bs.lansys.miuitools.utils.Consts;
import bs.lansys.miuitools.utils.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by lan on 16-10-18.
 */

public final class PackageHooks {

    public static class Settings {

        public static void HookOtherPersonalSettingsOnCreate(ClassLoader loader){
                SettingsInjector.AfterOtherPersonalSettingsOnCreate(loader);
        }

    }

    public static class ADConfigs {
        private static void HookDownloadProviderUiAD(ClassLoader loader){
            final Class<?> Adconfig =  XposedHelpers.findClass(Consts.CLASS_ADCONFIG, loader);
            XposedHelpers.findAndHookMethod(Adconfig, "OSSupportAD", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
                    if (Utils.isADCrack())
                        mhparams.setResult(false);
                }
            });
        }

        private static void HookDownloadProviderUiCloudConfigHelper(ClassLoader loader){
            XposedHelpers.findAndHookMethod(Consts.CLASS_DOWNLOAD_CloudConfigHelper, loader, "supportRank", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    XposedBridge.log("Hook--->" + Consts.CLASS_DOWNLOAD_CloudConfigHelper + ":supportRank");
                    methodHookParam.setResult(false);
                }
            });

            XposedHelpers.findAndHookMethod(Consts.CLASS_DOWNLOAD_CloudConfigHelper, loader, "isShouldShowAd", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    XposedBridge.log("Hook--->" + Consts.CLASS_DOWNLOAD_CloudConfigHelper + ":isShouldShowAd");
                    if (Utils.isADCrack()){
                        methodHookParam.setResult(false);
                    }
                }
            });
        }

        private static void HookDownloadProviderUiBuildUtils(ClassLoader loader){
            XposedHelpers.findAndHookMethod(Consts.CLASS_DOWNLOAD_BuildUtils, loader, "isInternationalBuilder", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    XposedBridge.log("Hook--->" + Consts.CLASS_DOWNLOAD_BuildUtils + ":isInternationalBuilder");
                    methodHookParam.setResult(true);
                }
            });

            XposedHelpers.findAndHookMethod(Consts.CLASS_DOWNLOAD_BuildUtils, loader, "isCmTestBuilder", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    XposedBridge.log("Hook--->" + Consts.CLASS_DOWNLOAD_BuildUtils + ":isCmTestBuilder");
                    methodHookParam.setResult(true);
                }
            });

        }

        public static void HookDownloadPrividerUi(ClassLoader loader){

            if (Utils.getMIUIVersion() < 8) {
                HookDownloadProviderUiAD(loader);
            }else {
                HookDownloadProviderUiBuildUtils(loader);
                HookDownloadProviderUiCloudConfigHelper(loader);
            }
        }

        public static void HookWeatherToolUtils(ClassLoader loader){
            final Class<?> clazz = XposedHelpers.findClass(Consts.CLASS_WEATHER_ToolUtils,loader);
            XposedHelpers.findAndHookMethod(clazz, "checkCommericalStatue", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (Utils.isADCrack()) methodHookParam.setResult(false);
                }
            });
        }

        public static void HookCleanmasterDataModel(ClassLoader loader){
            final Class<?> clazz = XposedHelpers.findClass(Consts.CLASS_CLEANMASTER_DataModel,loader);
            XposedHelpers.findAndHookMethod(clazz, "post", Map.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (Utils.isADCrack()) methodHookParam.setResult("");
                }
            });
        }




    }

    public static class SystemUI{

        public static void HookSecondsClock(ClassLoader loader){
            final Class<?> clock = XposedHelpers.findClass(Consts.CLASS_SYSTEMUI_CLOCK, loader);

            XposedBridge.hookAllConstructors(clock, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    Object obj = methodHookParam.thisObject;
                    XposedHelpers.setAdditionalInstanceField(obj, "mSecondsClockInjector",  new SecondsClockInjector(obj));
                }
            });

            XposedHelpers.findAndHookMethod(clock, "onAttachedToWindow", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    Object obj = methodHookParam.thisObject;
                    SecondsClockInjector mSecondsClockInjector = (SecondsClockInjector)XposedHelpers.getAdditionalInstanceField(obj, "mSecondsClockInjector");
                    mSecondsClockInjector.RunAfterOnAttachedToWindow();
                }
            });

            XposedHelpers.findAndHookMethod(clock, "updateClock", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    Object obj = methodHookParam.thisObject;
                    SecondsClockInjector mSecondsClockInjector = (SecondsClockInjector)XposedHelpers.getAdditionalInstanceField(obj, "mSecondsClockInjector");
                    mSecondsClockInjector.RunBeforeUpdateClock();
                    methodHookParam.setResult(null);
                }
            });
        }

    }

    public static class SinaWeiBo{

        public static void Hook(ClassLoader loader){
            final Class<?> mGreyScaleUtils = XposedHelpers.findClass(Consts.CLASS_SINAWB_GreyScaleUtils, loader);
            XposedBridge.hookAllMethods(mGreyScaleUtils, "isFeatureEnabled", new SinaImmersiveInjector());
        }
    }

    public static class Music{

        public static void HookMuiscConfiguration(ClassLoader loader){
            final Class<?> clazz = XposedHelpers.findClass(Consts.CLASS_MUSIC_Configuration,loader);
            XposedHelpers.findAndHookMethod(clazz, "isSupportOnline", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (!Utils.isMusicOnline()) methodHookParam.setResult(false);
                }
            });
        }
    }

    public static class Video{

        private static void HookIDataORM(ClassLoader loader){
            final Class<?> clazz = XposedHelpers.findClass(Consts.CLASS_VIDEO_iDataORM, loader);

            XposedHelpers.findAndHookMethod(clazz, "enabledAds", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (Utils.isADCrack())
                        methodHookParam.setResult(false);
                }
            });

            XposedHelpers.findAndHookMethod(clazz, "isMiPushOn", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (Utils.isADCrack() || !Utils.isVideoOnline()){
                        methodHookParam.setResult(false);
                    }
                }
            });


        }

        private static void HookAndroidUtils(ClassLoader loader){
            final Class<?> clazz = XposedHelpers.findClass(Consts.CLASS_VIDEO_AndroidUtils,loader);
            final Class<?> iDataORM = XposedHelpers.findClass(Consts.CLASS_VIDEO_iDataORM, loader);

            XposedHelpers.findAndHookMethod(clazz, "isNetworkConncected", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (!Utils.isVideoOnline()){
                        Class<?>[] args_type = {Context.class, boolean.class};
                        XposedHelpers.callStaticMethod(iDataORM, "setMiPushOn",args_type, methodHookParam.args[0], false);
                        methodHookParam.setResult(false);
                    }
                }
            });
        }

        private static void HookMiuiVideoConfig(ClassLoader loader){
            final Class<?> clazz = XposedHelpers.findClass(Consts.CLASS_VIDEO_MiuiVideoConfig, loader);

            XposedHelpers.findAndHookMethod(clazz, "isOnlineVideoOn", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (!Utils.isVideoOnline()){
                        methodHookParam.setResult(false);
                    }
                }
            });
        }

        public static void  Hook(ClassLoader loader){
            HookIDataORM(loader);
            HookAndroidUtils(loader);
            //HookMiuiVideoConfig(loader);
        }
    }
}
