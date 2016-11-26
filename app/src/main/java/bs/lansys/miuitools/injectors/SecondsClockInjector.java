package bs.lansys.miuitools.injectors;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import miui.date.DateUtils;

/**
 * Created by lan on 16-10-18.
 */

public class SecondsClockInjector {


    private Handler mHandler = new Handler();
    private Context mContext;
    private TextView Clock;
    private Runnable mTicker = new Runnable() {
        @Override
        public void run() {
            UpdateClock();
            Long now = System.currentTimeMillis();
            Long next = 1000 - now % 1000;
            mHandler.postDelayed(mTicker, next);
        }
    };

    private ContentObserver mShowSecondsObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            int value = Settings.System.getInt(mContext.getApplicationContext().getContentResolver(), "show_clock_seconds", 0);
            boolean mShowSeconds = value != 0;
            StartSticker(mShowSeconds);
            if (!mShowSeconds) UpdateClock();
        }
    };



    public SecondsClockInjector(Object clock){
        Clock = (TextView)clock;
        mContext = Clock.getContext();
        initClock();
    }

    private void initClock(){
        ContentResolver cr = mContext.getApplicationContext().getContentResolver();
        cr.registerContentObserver(Settings.System.getUriFor("show_clock_seconds"), false, mShowSecondsObserver);
    }

    private final String FormatDateTime(long times){
        String miuiTime = DateUtils.formatDateTime(times, 12);

        ContentResolver cr = mContext.getApplicationContext().getContentResolver();
        boolean mShowSeconds = Settings.System.getInt(cr, "show_clock_seconds", 0) != 0;
        if (mShowSeconds){
            String fulltime = getFullTime(times);
            String regex =  "([0-9]{1}|0[0-9]{1}|1[0-9]{1}|2[0-4]{1}):([0-5][0-9])";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(miuiTime);
            while (m.find()){
                String found = m.group();
                miuiTime = miuiTime.replace(found, fulltime);
            }
        }
        return miuiTime;
    }

    private final String getFullTime(long times) {
        String format = "h:mm:ss";
        if (DateFormat.is24HourFormat(mContext)) {
            format = "HH:mm:ss";
        }
        return new SimpleDateFormat(format).format(new Date(times));
    }

    private void UpdateClock(){
        Clock.setText(FormatDateTime(System.currentTimeMillis()));
    }

    protected void StartSticker(boolean lstart){
        if (lstart) {
            mTicker.run();
        }else{
            mHandler.removeCallbacks(mTicker);
        }
    }

    public void RunAfterOnAttachedToWindow(){
        ContentResolver cr = mContext.getApplicationContext().getContentResolver();
        boolean mShowSeconds = Settings.System.getInt(cr, "show_clock_seconds", 0) != 0;
        StartSticker(mShowSeconds);
    }

    public void RunBeforeUpdateClock(){
        UpdateClock();
    }
}
