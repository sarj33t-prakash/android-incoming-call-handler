package me.rail.incomingcallhandler.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import me.rail.incomingcallhandler.R;

public class CallService extends Service {

    private static WindowManager windowManager;
    @SuppressLint("StaticFieldLeak")
    private static ViewGroup windowLayout;

    private static final float WINDOW_WIDTH_RATIO = 0.8f;
    private WindowManager.LayoutParams params;
    private float x;
    private float y;

    public CallService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Call App",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("")
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public ComponentName startForegroundService(Intent service) {
        if (service.getStringExtra("number") != null) {
            String number = service.getStringExtra("number");
            /// Toast.makeText(getApplicationContext(), number, Toast.LENGTH_SHORT).show();
            if(number != null){
                showWindow(getApplicationContext(), number);
                Toast.makeText(getApplicationContext(), "Maulik is calling", Toast.LENGTH_SHORT).show();
            }
        }
        return super.startForegroundService(service);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getStringExtra("number") != null) {
            String number = intent.getStringExtra("number");
            /// Toast.makeText(getApplicationContext(), number, Toast.LENGTH_SHORT).show();
            if(number != null){
                showWindow(getApplicationContext(), number);
                Toast.makeText(getApplicationContext(), "Maulik is calling", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showWindow(final Context context, String phone) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowLayout = (ViewGroup) View.inflate(context, R.layout.window_call_info, null);
        getLayoutParams();
        setOnTouchListener();

        TextView nameTextView = windowLayout.findViewById(R.id.name);
        TextView numberTextView = windowLayout.findViewById(R.id.number);
        nameTextView.setText("Maulik");
        numberTextView.setText(phone);
        Button cancelButton = windowLayout.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(view -> closeWindow());

        windowManager.addView(windowLayout, params);
    }

    private void getLayoutParams() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                getWindowsTypeParameter(),
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;
        params.format = 1;
        params.width = getWindowWidth();
    }

    private int getWindowsTypeParameter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        return WindowManager.LayoutParams.TYPE_PHONE;
    }

    private int getWindowWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return (int) (WINDOW_WIDTH_RATIO * (double) metrics.widthPixels);
    }

    private void setOnTouchListener() {
        windowLayout.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getRawX();
                    y = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateWindowLayoutParams(event);
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                default:
                    break;
            }
            return false;
        });
    }

    private void updateWindowLayoutParams(MotionEvent event) {
        params.x = params.x - (int) (x - event.getRawX());
        params.y = params.y - (int) (y - event.getRawY());
        windowManager.updateViewLayout(windowLayout, params);
        x = event.getRawX();
        y = event.getRawY();
    }

    private void closeWindow() {
        if (windowLayout != null) {
            windowManager.removeView(windowLayout);
            windowLayout = null;
        }
    }
}