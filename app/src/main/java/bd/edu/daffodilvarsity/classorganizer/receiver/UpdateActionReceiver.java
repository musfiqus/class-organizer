package bd.edu.daffodilvarsity.classorganizer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateActionReceiver extends BroadcastReceiver {
    private static final String TAG = "UpdateActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: update broadcast received");

    }
}
