package hoangcuongdev.com.xmpp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

import hoangcuongdev.com.xmpp.R;
import hoangcuongdev.com.xmpp.base.MyApplication;
import hoangcuongdev.com.xmpp.model.bean.Constants;
import hoangcuongdev.com.xmpp.model.bean.IntentData;
import hoangcuongdev.com.xmpp.ui.activity.MainActivity;

public class MyAndroidUtil {
    private static NotificationCompat.Builder builder = new NotificationCompat.Builder( MyApplication.getInstance().getContext() );

    public static void showNoti(int type, String subject, String notiMsg, String chatname, String nickname, Bitmap bitmap) {
        if (subject.equals( Constants.SEND_IMG ))
            builder.setContentText( "[image]" );
        else if (subject.equals( Constants.SEND_SOUND ))
            builder.setContentText( "[voice]" );
        else if (subject.equals( Constants.SEND_LOCATION ))   //适配表情
            builder.setContentText( "[position]" );
        else
            builder.setContentText( notiMsg );

        IntentData intentData = new IntentData();
        intentData.setType( type );
        intentData.setChatname( chatname );
        intentData.setNickname( nickname );
        Intent intent = new Intent();
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        intent.putExtra( "noti", intentData );
        intent.setClass( MyApplication.getInstance(), MainActivity.class );
        NotificationManager notificationManager =
                (NotificationManager) MyApplication.getInstance().getSystemService( Service.NOTIFICATION_SERVICE );
        PendingIntent pendingIntent = PendingIntent.getActivity( MyApplication.getInstance(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT );
        if (bitmap != null)
            builder.setLargeIcon( bitmap );
        builder.setSmallIcon( R.mipmap.icon_kengchat )
                .setContentTitle( nickname )
                .setAutoCancel( true )
                .setDefaults( Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL
                        | Notification.DEFAULT_SOUND )
                .setPriority( Notification.PRIORITY_MAX )
                .setWhen( System.currentTimeMillis() )
                .setContentIntent( pendingIntent );
        notificationManager.notify( 0, builder.build() );
    }
}
