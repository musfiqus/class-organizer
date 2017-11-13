package bd.edu.daffodilvarsity.classorganizer;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;

import io.fabric.sdk.android.Fabric;
import org.polaric.colorful.Colorful;

/**
 * Created by Mushfiqus Salehin on 4/2/2017.
 * musfiqus@gmail.com
 */

public class ClassOrganizer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Colorful.defaults()
                .primaryColor(Colorful.ThemeColor.BLUE_GREY_CUSTOM)
                .accentColor(Colorful.ThemeColor.TEAL_CUSTOM)
                .translucent(false)
                .dark(false);

        Colorful.init(this);
        FirebaseApp.initializeApp(this);
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
    }
}
