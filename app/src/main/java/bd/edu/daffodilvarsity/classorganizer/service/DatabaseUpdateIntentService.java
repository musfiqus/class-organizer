package bd.edu.daffodilvarsity.classorganizer.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mushfiqus Salehin on 5/28/2017.
 * musfiqus@gmail.com
 */

public class DatabaseUpdateIntentService extends IntentService {
    public static final int DOWNLOAD_ERROR = 10;
    public static final int DOWNLOAD_SUCCESS = 11;
    public static final String TAG_DATABASE_VERSION = "db_version";
    public static final String TAG_RECEIVER = "receiver";
    public static final String TAG_DATABASE_NAME = "db_name";
    public static final String TAG_DB_URL = "db_url";
    public static final String TAG_FILE_PATH = "filePath";

    public DatabaseUpdateIntentService() {
        super("DatabaseUpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra(TAG_RECEIVER);
        int dbVersion = intent.getIntExtra(TAG_DATABASE_VERSION, 0);
        final String dbName = intent.getStringExtra(TAG_DATABASE_NAME);
        final String dbURL = intent.getStringExtra(TAG_DB_URL);
        Bundle bundle = new Bundle();
        File downloadFile = new File(getDatabasePath(dbName).getAbsolutePath());
        if (downloadFile.exists()) {
            downloadFile.delete();
        }
        try {
            downloadFile.createNewFile();
            URL downloadURL = new URL(dbURL);
            HttpURLConnection conn = (HttpURLConnection) downloadURL
                    .openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200)
                throw new Exception("Error in connection");
            InputStream is = conn.getInputStream();
            FileOutputStream os = new FileOutputStream(downloadFile);
            byte buffer[] = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                os.write(buffer, 0, byteCount);
            }
            os.close();
            is.close();

            String filePath = downloadFile.getPath();
            bundle.putString(TAG_FILE_PATH, filePath);
            bundle.putString(TAG_DATABASE_NAME, dbName);
            bundle.putInt(TAG_DATABASE_VERSION, dbVersion);
            receiver.send(DOWNLOAD_SUCCESS, bundle);

        } catch (Exception e) {
            receiver.send(DOWNLOAD_ERROR, Bundle.EMPTY);
            e.printStackTrace();
        }
    }
}
