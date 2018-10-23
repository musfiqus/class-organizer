package bd.edu.daffodilvarsity.classorganizer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bd.edu.daffodilvarsity.classorganizer.service.MuteJobIntentService;

public class MuteActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MuteJobIntentService.enqueueWork(context, intent);
    }
}