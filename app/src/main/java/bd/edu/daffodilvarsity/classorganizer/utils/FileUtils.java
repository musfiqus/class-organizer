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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

/**
 * Created by Mushfiqus Salehin on 10/16/2017.
 * musfiqus@gmail.com
 */
public final class FileUtils {
    private static final String TAG = "FileUtils";

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            boolean result = destFile.createNewFile();
            if (result) Log.d(TAG, "copyFile: New file created");
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

    public static boolean backupDatabase(Context context, String dbName) {
        File dbToBackup = context.getDatabasePath(dbName);
        File dbBackup = new File(context.getDatabasePath(dbName).getAbsolutePath()+"_bkp");
        File dbToBackupJournal = new File(context.getDatabasePath(dbName).getAbsolutePath()+"-journal");
        File dbJournalBackup = new File(context.getDatabasePath(dbName).getAbsolutePath()+"-journal_bkp");
        try {
            copyFile(dbToBackup, dbBackup);
        } catch (IOException e) {
            Log.e(TAG, "backupDatabase: \n"+e.toString());
            logAnError(context, TAG, "backupDatabase: ", e);
            return false;
        }
        try {
            copyFile(dbToBackupJournal, dbJournalBackup);
        } catch (IOException e) {
            Log.e(TAG, "backupDatabase: \n"+e.toString());
            logAnError(context, TAG, "backupDatabase: ", e);
            return false;
        }
        return true;
    }

    public static boolean restoreDatabase(Context context, String dbName) {
        File db = context.getDatabasePath(dbName);
        File dbRestore = new File(context.getDatabasePath(dbName).getAbsolutePath()+"_bkp");
        File dbJournal = new File(context.getDatabasePath(dbName).getAbsolutePath()+"-journal");
        File dbJournalRestore = new File(context.getDatabasePath(dbName).getAbsolutePath()+"-journal_bkp");
        try {
            copyFile(dbRestore, db);
        } catch (IOException e) {
            Log.e(TAG, "backupDatabase: \n"+e.toString());
            logAnError(context, TAG, "backupDatabase: ", e);
            return false;
        }
        try {
            copyFile(dbJournalRestore, dbJournal);
        } catch (IOException e) {
            Log.e(TAG, "backupDatabase: \n"+e.toString());
            logAnError(context, TAG, "backupDatabase: ", e);
            return false;
        }
        return true;
    }

    public static boolean deleteDatabase(Context context, String dbName) {
        boolean delete = true;
        String journalName = dbName+"-journal";
        context.deleteDatabase(dbName);
        File dbFile = context.getDatabasePath(dbName);
        File dbJournal = new File(context.getDatabasePath(journalName).getAbsolutePath());
        if (dbFile.exists()) {
            boolean dbDelete = dbFile.getAbsoluteFile().delete();
            if (dbDelete) {
                Log.d(TAG, "deleteDatabase: "+dbName+" deleted via file operation");
            } else {
                delete = false;
                Log.e(TAG, "deleteDatabase: unable to delete "+dbName);
            }
        } else {
            Log.d(TAG, "deleteDatabase: "+dbName+" deleted via db delete");
        }

        if (dbJournal.exists()) {
            boolean dbDelete = dbFile.getAbsoluteFile().delete();
            if (dbDelete) {
                Log.d(TAG, "deleteDatabase: "+journalName+" deleted via file operation");
            } else {
                delete = false;
                Log.e(TAG, "deleteDatabase: unable to delete "+journalName);
            }
        } else {
            Log.d(TAG, "deleteDatabase: "+journalName+" deleted via db delete");
        }
        return delete;
    }

    public static boolean checkMD5(String md5, File updateFile) {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Log.e(TAG, "MD5 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateMD5(updateFile);
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null");
            return false;
        }

        Log.v(TAG, "Calculated digest: " + calculatedDigest);
        Log.v(TAG, "Provided digest: " + md5);

        return calculatedDigest.equalsIgnoreCase(md5);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //* Google:
    //* Stackoverflow: https://stackoverflow.com/a/14922433
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            Log.i(TAG, "calculateMD5: Calculated digest for +"
                    +Uri.fromFile(updateFile).getLastPathSegment()+" is :"+output);
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }

    }

    public static void logAnError(Context context, String tag, String message, Exception exception) {
        PrefManager prefManager = new PrefManager(context);
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
        errorDetails += "Database version: "+prefManager.getDatabaseVersion()+"\n";
        errorDetails += "Campus: "+prefManager.getCampus()+" Department: "+prefManager.getDept()+" Program: "+prefManager.getProgram()+"\n";
        if (prefManager.isUserStudent()) {
            errorDetails += "Level: "+prefManager.getLevel()+1+" Term: "+prefManager.getTerm()+1+" Section: "+prefManager.getSection()+"\n";
        } else {
            errorDetails += "Teacher's initial: "+prefManager.getTeacherInitial()+" Multi program: "+prefManager.isMultiProgram()+"\n";
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


}
