package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Database;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;

/**
 * Created by Mushfiqus Salehin on 10/16/2017.
 * musfiqus@gmail.com
 */
public final class FileUtils {
    private static final String TAG = "FileUtils";

    public static void logAnError(Context context, String tag, String message, Exception exception) {
        String errorDetails = null;
        //Get version
        String appVersionName;
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            appVersionName = "App version not found";
        }
        errorDetails = "App version: "+appVersionName+"\n";
        errorDetails += "Database version: "+PreferenceGetter.getDatabaseVersion()+"\n";
        errorDetails += "Campus: "+PreferenceGetter.getCampus()+" Department: "+PreferenceGetter.getDepartment()+" Program: "+PreferenceGetter.getProgram()+"\n";
        if (PreferenceGetter.isStudent()) {
            errorDetails += "Level: "+PreferenceGetter.getLevel()+1+" Term: "+PreferenceGetter.getTerm()+1+" Section: "+PreferenceGetter.getSection()+"\n";
        } else {
            errorDetails += "Teacher's initial: "+PreferenceGetter.getInitial()+"\n";
        }
        errorDetails += "Message: "+message+"\n";
//        if (exception != null) {
//            errorDetails += "Stacktrace: \n"+exception.toString();
//            Crashlytics.log(1, tag, errorDetails);
//            Crashlytics.logException(exception);
//            return;
//        }
//        Crashlytics.log(1, tag, errorDetails);
    }

    public static Database readOfflineDatabase() {
        InputStream is = ClassOrganizer.getInstance().getResources().openRawResource(R.raw.routine);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();
        Gson gson = new Gson();
        return gson.fromJson(jsonString, new TypeToken<Database>(){}.getType());
    }

    public static Routine convertToRoutine(byte[] dayByte) {
        ByteArrayInputStream bis = new ByteArrayInputStream(dayByte);
        ObjectInput in = null;
        Routine dayData = null;
        try {
            in = new ObjectInputStream(bis);
            dayData = (Routine)in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return dayData;
    }

    public static byte[] convertToByteArray(Routine dayData) {
        byte[] data = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(dayData);
            out.flush();
            data = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return data;
    }


}
