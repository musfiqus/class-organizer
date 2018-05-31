package bd.edu.daffodilvarsity.classorganizer.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import bd.edu.daffodilvarsity.classorganizer.BuildConfig;

public class UserInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "UserInstanceIdService";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onTokenRefresh: "+token);
        if (BuildConfig.DEBUG) {
//            String token = FirebaseInstanceId.getInstance().getToken();
//            Log.d(TAG, "onTokenRefresh: "+token);
        } else {
            Log.e(TAG, "onTokenRefresh: WEEW");
        }
    }
}
