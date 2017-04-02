package bd.edu.daffodilvarsity.classorganizer;

import android.app.Application;

import org.polaric.colorful.Colorful;

/**
 * Created by musfiqus on 4/2/2017.
 */

public class ClassOrganizer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Colorful.defaults()
                .primaryColor(Colorful.ThemeColor.BLUE_GREY)
                .accentColor(Colorful.ThemeColor.BLUE)
                .translucent(true)
                .dark(false);

        Colorful.init(this);
    }
}
