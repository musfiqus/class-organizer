package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

/**
 * Created by Mushfiqus Salehin on 10/16/2017.
 * musfiqus@gmail.com
 */
public final class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static void dbDownloader(String dlURL, String filePath) throws Exception {
        File downloadFile = new File(filePath);
        if (downloadFile.exists()) {
            downloadFile.delete();
        }
        downloadFile.createNewFile();
        if (dlURL != null) {
            URL downloadURL = new URL(dlURL);
            HttpURLConnection conn = (HttpURLConnection) downloadURL
                    .openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200)
                throw new Exception("Error in connection");
            conn.connect();
            InputStream is = conn.getInputStream();
            FileOutputStream os = new FileOutputStream(downloadFile);
            byte buffer[] = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                // Write data to file
                os.write(buffer, 0, byteCount);
            }
            os.close();
            is.close();
        } else {
            throw new Exception("Error parsing null URL");
        }
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(sourceFile);
            os = new FileOutputStream(destFile);
            source = is.getChannel();
            destination = os.getChannel();

            long count = 0;
            long size = source.size();
            while ((count += destination.transferFrom(source, count, size - count)) < size)
                ;
        } catch (Exception ex) {
        } finally {
            if (source != null) {
                source.close();
            }
            if (is != null) {
                is.close();
            }
            if (destination != null) {
                destination.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}
