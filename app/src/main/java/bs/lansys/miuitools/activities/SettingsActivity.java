package bs.lansys.miuitools.activities;

import android.os.Bundle;
import android.preference.Preference;
import miui.preference.PreferenceActivity;
import android.preference.CheckBoxPreference;
import android.provider.Settings;
import android.widget.Toast;

import bs.lansys.miuitools.R;
import bs.lansys.miuitools.utils.Consts;
import bs.lansys.miuitools.utils.Utils;
import miui.os.SystemProperties;

/**
 * Created by Administrator on 2016/10/9.
 */

public class SettingsActivity extends PreferenceActivity  implements Preference.OnPreferenceChangeListener{

    private static  String COLOR_STATUSBAR_KEY = "color_statusbar_key";
    private static String COLOR_TOAST_KEY = "color_toast_key";
    private static String THEME_AUTHOR_KEY = "theme_author_key";
    private static String AD_CRASK_KEY = "ad_crask_key";
    private static String SECOND_CLOCK_KEY = "second_clock_key";
    private static String VIDEO_ONLINE_KEY = "video_online_key";
    private static String MUSIC_ONLINE_KEY = "music_online_key";
    private static String DEBUG_MSG_KEY = "debug_msg_key";

    private boolean mChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTheme(miui.R.style.Theme_Light_Settings);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);

        CheckBoxPreference PrefColorStatusBar = (CheckBoxPreference)findPreference(COLOR_STATUSBAR_KEY);
        PrefColorStatusBar.setChecked(Utils.isMiuiFlatColorBarEnable());
        PrefColorStatusBar.setOnPreferenceChangeListener(this);

        CheckBoxPreference PrefColorToast = (CheckBoxPreference)findPreference(COLOR_TOAST_KEY);
        PrefColorToast.setChecked(Utils.isColorToastEnabled());
        PrefColorToast.setEnabled(Utils.isMiuiFlatColorBarEnable());
        PrefColorToast.setOnPreferenceChangeListener(this);

        CheckBoxPreference PrefSecClock = (CheckBoxPreference)findPreference(SECOND_CLOCK_KEY);
        boolean mShowSec = Settings.System.getInt(getApplicationContext().getContentResolver(),"show_clock_seconds", 0) != 0;
        PrefSecClock.setChecked(mShowSec);
        PrefSecClock.setOnPreferenceChangeListener(this);

        CheckBoxPreference PrefThemeAuthor = (CheckBoxPreference)findPreference(THEME_AUTHOR_KEY);
        PrefThemeAuthor.setChecked(Utils.isThemeCrack());
        PrefThemeAuthor.setOnPreferenceChangeListener(this);

        CheckBoxPreference PrefCloseMiAD = (CheckBoxPreference)findPreference(AD_CRASK_KEY);
        PrefCloseMiAD.setChecked(Utils.isADCrack());
        PrefCloseMiAD.setOnPreferenceChangeListener(this);

        CheckBoxPreference PrefMuiscOnline = (CheckBoxPreference) findPreference(MUSIC_ONLINE_KEY);
        boolean isMusicOnline = Utils.isMusicOnline();
        PrefMuiscOnline.setChecked(isMusicOnline);
        PrefMuiscOnline.setSummary(isMusicOnline ? R.string.music_online_summary : R.string.music_offline_summary);
        PrefMuiscOnline.setOnPreferenceChangeListener(this);

        CheckBoxPreference PrefVideoOnline = (CheckBoxPreference) findPreference(VIDEO_ONLINE_KEY);
        boolean isVideoOnline = Utils.isVideoOnline();
        PrefVideoOnline.setChecked(isVideoOnline);
        PrefVideoOnline.setSummary(isVideoOnline ?R.string.video_online_summary : R.string.video_offline_summary);
        PrefVideoOnline.setOnPreferenceChangeListener(this);

        CheckBoxPreference PrefDebugMsg = (CheckBoxPreference)findPreference(DEBUG_MSG_KEY);
        PrefDebugMsg.setChecked(Utils.isShowDebugMsg());
        PrefDebugMsg.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object obj){
        Boolean value = (Boolean)obj;
        String key = preference.getKey();

        if (key.equals(COLOR_STATUSBAR_KEY)){
            SystemProperties.set(Consts.MIUICOLORBAR_ENABLE_Properties, value);
            findPreference(COLOR_TOAST_KEY).setEnabled(value);
            mChanged =true;
            return true;
        }else if (key.equals(COLOR_TOAST_KEY)){
            SystemProperties.set(Consts.TOAST_COLOR_Properties, value);
            mChanged = true;
            return true;
        }else if (key.equals(THEME_AUTHOR_KEY)){
            SystemProperties.set(Consts.THEME_CRACK_Properties, value);
            //mChanged = true;
            return true;
        }else if (key.equals(AD_CRASK_KEY)){
            SystemProperties.set(Consts.AD_CRACK_Properties, value);
            //mChanged = true;
            return  true;
        }else if (key.equals(SECOND_CLOCK_KEY)){
            Settings.System.putInt(getApplicationContext().getContentResolver(), "show_clock_seconds", value ? 1 : 0);
            return true;
        }
        else if (key.equals(MUSIC_ONLINE_KEY)){
            SystemProperties.set(Consts.IS_MUSIC_ONLINE_Properties, value);
            findPreference(MUSIC_ONLINE_KEY).setSummary(value ? R.string.music_online_summary : R.string.music_offline_summary);
            return  true;
        }
        else if (key.equals(VIDEO_ONLINE_KEY)){
            SystemProperties.set(Consts.IS_VIDEO_ONLINE_Properties, value);
            findPreference(key).setSummary(value ? R.string.video_online_summary : R.string.video_offline_summary);
            return true;
        }
        else if (key.equals(DEBUG_MSG_KEY)){
            SystemProperties.set(Consts.SHOW_DEBUG_MSG_Properties, value);
            return true;
        }

        return false;
    }

    @Override
    public void onPause(){
        super.onPause();
        if (mChanged)
            Toast.makeText(this, R.string.settings_changed_message, Toast.LENGTH_SHORT).show();
    }
}
