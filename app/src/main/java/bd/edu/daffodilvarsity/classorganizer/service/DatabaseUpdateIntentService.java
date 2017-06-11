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

import bd.edu.daffodilvarsity.classorganizer.utils.UpdatedDatabaseHelper;

/**
 * Created by Mushfiqus Salehin on 5/28/2017.
 * musfiqus@gmail.com
 */

public class DatabaseUpdateIntentService extends IntentService {
    public static final int DOWNLOAD_ERROR = 10;
    public static final int DOWNLOAD_SUCCESS = 11;
    private static final String DATABASE_URL = "https://mushfiqussalehin.me/routinedb/updated.db";

    public DatabaseUpdateIntentService() {
        super("DatabaseUpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        int dbVersion = intent.getIntExtra("db_version", 0);
        Bundle bundle = new Bundle();
        File downloadFile = new File(getDatabasePath(UpdatedDatabaseHelper.UPDATED_DATABASE_NAME).getAbsolutePath());
        if (downloadFile.exists()) {
            downloadFile.delete();
        }
        try {
            downloadFile.createNewFile();
            URL downloadURL = new URL(DATABASE_URL);
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
            bundle.putString("filePath", filePath);
            bundle.putInt("db_version", dbVersion);
            receiver.send(DOWNLOAD_SUCCESS, bundle);

        } catch (Exception e) {
            receiver.send(DOWNLOAD_ERROR, Bundle.EMPTY);
            e.printStackTrace();
        }
    }
}
