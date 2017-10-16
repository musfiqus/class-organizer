package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;

/**
 * Created by Mushfiqus Salehin on 10/5/2017.
 * musfiqus@gmail.com
 */

public class MasterDBOnline extends MasterDBOffline {
    public static final String UPDATED_DATABASE_NAME = "masterdb_online.db";
    private static MasterDBOnline mInstance = null;

    private MasterDBOnline(Context context) {
        super(context, UPDATED_DATABASE_NAME, new PrefManager(context).getDatabaseVersion());
        setForcedUpgrade();
    }

    public static MasterDBOnline getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance != null) {
            mInstance = null;
        }
        mInstance = new MasterDBOnline(context.getApplicationContext());
        return mInstance;
    }
}