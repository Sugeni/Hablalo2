package isware.uneg.es.chateando.Logica;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import isware.uneg.es.chateando.R;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_body = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();

        String from_user_id = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(notification_title)
                .setContentText(notification_body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Accion de la notificacion

        Intent accion = new Intent(click_action );
        accion.putExtra("user_id", from_user_id);

        PendingIntent accionPendintIntent =  PendingIntent.getActivity(this, 0,
                accion, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent( accionPendintIntent);

        //Id de la notificacion
        int mNotificationId = (int )System.currentTimeMillis();

        //Get a instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //Built the notification and Isues it
        mNotifyMgr.notify( mNotificationId, mBuilder.build());

    }
}
