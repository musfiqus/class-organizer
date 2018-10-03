package bd.edu.daffodilvarsity.classorganizer.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = "PushNotificationService";

    private static final String KEY_TITLE = "notification_title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_ACTION = "action";
    private static final String KEY_UPDATE_RESPONSE = "json";
    private static final String VALUE_UPDATE = "update";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


    }

}
