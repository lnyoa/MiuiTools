package bs.lansys.miuitools.injectors;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import java.util.Locale;

import bs.lansys.miuitools.activities.SettingsActivity;
import bs.lansys.miuitools.utils.Consts;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import miui.preference.PreferenceFragment;

/**
 * Created by lan on 16-10-18.
 */

public class SettingsInjector {

    public static final XC_MethodHook OtherPersonalSettings_OnCreate = new XC_MethodHook(){
        @Override
        protected void afterHookedMethod(MethodHookParam mparam) throws Throwable{
            PreferenceScreen OtherPersonalSettings = ((PreferenceFragment)mparam.thisObject).getPreferenceScreen();
            Context context = ((PreferenceFragment)mparam.thisObject).getActivity();
            PreferenceCategory preferenceCategory = new PreferenceCategory(context);
            Preference preference = new Preference(context);

            Locale locale = Resources.getSystem().getConfiguration().locale;
            if (locale.getCountry().equals("CN") || locale.getCountry().equals("TW") || locale.getCountry().equals("HK")) {
                preference.setTitle("MIUI附加设置");
            }else{
                preference.setTitle("Additions");
            }
            preference.setKey("miui_additions");

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClassName(Consts.PACKAGE_NAME, SettingsActivity.class.getCanonicalName());
            preference.setIntent(intent);

            OtherPersonalSettings.addPreference(preferenceCategory);
            OtherPersonalSettings.addPreference(preference);
        }
    };

    public static  void AfterOtherPersonalSettingsOnCreate(ClassLoader loader){
        XposedHelpers.findAndHookMethod(Consts.CLASS_OTHERPERSONALSETTINGS, loader, "onCreate", Bundle.class, OtherPersonalSettings_OnCreate);
    }
}
