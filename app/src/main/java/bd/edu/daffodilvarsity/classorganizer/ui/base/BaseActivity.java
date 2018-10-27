package bd.edu.daffodilvarsity.classorganizer.ui.base;

import androidx.lifecycle.Lifecycle;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Aesthetic.Companion.isFirstTime()) {
//            Aesthetic.Companion.config(aesthetic -> {
//                aesthetic.colorPrimaryRes(R.color.colorPrimary).apply();
//                aesthetic.colorPrimaryDarkRes(R.color.colorPrimaryDark).apply();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    aesthetic.colorStatusBarRes(R.color.colorPrimary).apply();
//                } else {
//                    aesthetic.colorStatusBarRes(R.color.colorPrimaryDark).apply();
//                }
//                aesthetic.lightStatusBarMode(AutoSwitchMode.AUTO).apply();
//                aesthetic.textColorPrimaryRes(R.color.text_color_dark).apply();
//                aesthetic.textColorSecondaryRes(R.color.text_color_light).apply();
//                return Unit.INSTANCE;
//            });
//        }
    }

    public boolean isActive() {
        return getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
    }
}
