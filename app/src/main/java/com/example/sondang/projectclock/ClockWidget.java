package com.example.sondang.projectclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.widget.RemoteViews;
import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class ClockWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        CharSequence widgetText = context.getString(Integer.parseInt("irect"));
//        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Intent serviceIntent = new Intent(context, UpdateTimeService.class);
            serviceIntent.setAction(UpdateTimeService.UPDATE_TIME);
            context.startService(serviceIntent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Intent serviceIntent = new Intent(context, UpdateTimeService.class);
        serviceIntent.setAction(UpdateTimeService.UPDATE_TIME);
        context.startService(serviceIntent);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        context.stopService(new Intent(context, UpdateTimeService.class));
    }

    public static final class UpdateTimeService extends Service {
        static final String UPDATE_TIME = "com.example.sondang.projectclock.action.UPDATE_TIME";

        private Calendar mCalendar;
        private final static IntentFilter mIntentFilter = new IntentFilter();

        static {
            mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
            mIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
            mIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        }

        @Override
        public void onCreate() {
            super.onCreate();

            mCalendar = Calendar.getInstance();
            registerReceiver(mTimeChangedReceiver, mIntentFilter);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            unregisterReceiver(mTimeChangedReceiver);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            super.onStartCommand(intent, flags, startId);

            if (intent != null) {
                if (UPDATE_TIME.equals(intent.getAction())) {
                    updateTime();
                }
            }

            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateTime();
            }
        };

        private void updateTime() {
            mCalendar.setTimeInMillis(System.currentTimeMillis());

            String hm = "";
            String hour = DateFormat.format(getString(R.string.hour_format), mCalendar).toString();
            String minute = DateFormat.format(getString(R.string.minute_format), mCalendar).toString();
            hm = hour + "\n" + minute;

            RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.clock_widget);
            mRemoteViews.setTextViewText(R.id.appwidget_hour, DateFormat.format(getString(R.string.hour_format), mCalendar));
            mRemoteViews.setTextViewText(R.id.appwidget_minute, DateFormat.format(getString(R.string.minute_format), mCalendar));

            ComponentName mComponentName = new ComponentName(this, ClockWidget.class);
            AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(this);
            mAppWidgetManager.updateAppWidget(mComponentName, mRemoteViews);
        }
    }
}

