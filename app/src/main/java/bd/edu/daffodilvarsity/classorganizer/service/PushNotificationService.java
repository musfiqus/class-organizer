package bd.edu.daffodilvarsity.classorganizer.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import bd.edu.daffodilvarsity.classorganizer.data.UpdateResponse;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.UpdateNotificationHelper;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = "PushNotificationService";

    private static final String KEY_TITLE = "notification_title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_ACTION = "action";
    private static final String KEY_UPDATE_RESPONSE = "json";
    private static final String VALUE_UPDATE = "update";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {
            String action = remoteMessage.getData().get(KEY_ACTION);
            if (action != null) {
                if (action.equalsIgnoreCase(VALUE_UPDATE)) {
                    Log.d(TAG, "onMessageReceived: Update notification");
                    publishUpdateNotification(remoteMessage.getData());
                }
            }
        } else {
            Log.w(TAG, "onMessageReceived: No data bundled with push");
        }

    }

    private void publishUpdateNotification(Map<String, String> data) {
        boolean broken = false;
        String title = data.get(KEY_TITLE);
        String message = data.get(KEY_MESSAGE);

        String json = data.get(KEY_UPDATE_RESPONSE);
        UpdateResponse updateResponse = null;
        if (title == null) {
            broken = true;
        }
        if (message == null) {
            broken = true;
        }

        if (json == null) {
            broken = true;
        } else {
            Gson gson = new Gson();
            updateResponse = gson.fromJson(json, UpdateResponse.class);
            if (updateResponse == null) {
                broken = true;
            }
        }
        if (!broken) {
            Log.d(TAG, "publishUpdateNotification: update notification received");
            PrefManager prefManager = new PrefManager(getApplicationContext());
            if (prefManager.getDatabaseVersion() < updateResponse.getVersion()) {
                UpdateNotificationHelper notificationHelper = new UpdateNotificationHelper(getApplicationContext(), updateResponse);
                notificationHelper.showUpdateNotification(title, message);
                Log.d(TAG, "publishUpdateNotification: update notification pushed");
            } else {
                Log.d(TAG, "publishUpdateNotification: database already on latest version");
            }

        } else {
            Log.e(TAG, "publishUpdateNotification: Broken push data");
        }

    }
}
