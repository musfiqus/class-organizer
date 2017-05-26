package bd.edu.daffodilvarsity.classorganizer;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.polaric.colorful.Colorful;

/**
 * Created by musfiqus on 4/2/2017.
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
    }
}
