package bd.edu.daffodilvarsity.classorganizer.ui.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Status;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SetupActivity extends IntroActivity {
    private static final String TAG = "SetupActivity";

    @BindView(R.id.mi_frame_parent)
    CoordinatorLayout mParent;
    private SetupViewModel mViewModel;

    private boolean isWelcomeMode = PreferenceGetter.isFirstTimeLaunch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setButtonBackFunction(BUTTON_BACK_FUNCTION_BACK);
//        setButtonBackVisible(false);
        ButterKnife.bind(this);
        setupView();


    }

    private void setupView() {
        mViewModel = ViewModelProviders.of(this).get(SetupViewModel.class);

        if (isWelcomeMode) {
            addSlide(new FragmentSlide.Builder()
                    .background(R.color.md_white_1000)
                    .backgroundDark(R.color.md_grey_300)
                    .fragment(new UpdateFragment())
                    .build());
        }

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_white_1000)
                .backgroundDark(R.color.md_grey_300)
                .fragment(new UserTypeFragment())
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_white_1000)
                .backgroundDark(R.color.md_grey_300)
                .fragment(new CampusFragment())
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_white_1000)
                .backgroundDark(R.color.md_grey_300)
                .fragment(new DepartmentFragment())
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_white_1000)
                .backgroundDark(R.color.md_grey_300)
                .fragment(new LevelFragment())
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_white_1000)
                .backgroundDark(R.color.md_grey_300)
                .fragment(new SectionFragment())
                .build());


        setNavigationPolicy(new NavigationPolicy() {
            @Override
            public boolean canGoForward(int i) {
                if (i == (isWelcomeMode ? 1 : 0) && InputHelper.isEmpty(mViewModel.getUserTypeListener().getValue())) {
                    return false;
                }
                if (i == (isWelcomeMode ? 2 : 1) && mViewModel.getSelectedCampusListener().getValue() == null){
                    return false;
                }
                String userType = mViewModel.getUserTypeListener().getValue();
                if (!InputHelper.isEmpty(userType)) {
                    if (userType.equals(SetupViewModel.USER_TEACHER)) {
                        if (i == (isWelcomeMode ? 3 : 2)) {
                            if (InputHelper.isEmpty(mViewModel.getSelectedDepartmentListener().getValue())) {
                                return false;
                            }
                        }
                        if (i == (isWelcomeMode ? 4 : 3)) {
                            if (InputHelper.isEmpty(mViewModel.getSelectedInitialListener().getValue())) {
                                return false;
                            }
                        }
                    } else if (userType.equals(SetupViewModel.USER_STUDENT)) {
                        if (i == (isWelcomeMode ? 3 : 2)) {
                            if (InputHelper.isEmpty(mViewModel.getSelectedDepartmentListener().getValue())) {
                                return false;
                            }
                            if (InputHelper.isEmpty(mViewModel.getSelectedProgramListener().getValue())) {
                                return false;
                            }
                        }
                        if (i == (isWelcomeMode ? 4 : 3)) {
                            if (mViewModel.getSelectedLevelListener().getValue() == null) {
                                return false;
                            }
                            if (mViewModel.getSelectedTermListener().getValue() == null) {
                                return false;
                            }
                        }
                        if (i == (isWelcomeMode ? 5 : 4)) {
                            if (InputHelper.isEmpty(mViewModel.getSelectedSectionListener().getValue())) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean canGoBackward(int i) {
                return true;
            }
        });

        addOnNavigationBlockedListener((i, i1) -> {
            if (i == (isWelcomeMode ? 1 : 0)) {
                Snackbar.make(mParent, R.string.user_not_selected, Snackbar.LENGTH_SHORT).show();
            }
            if (i == (isWelcomeMode ? 2 : 1)) {
                Snackbar.make(mParent, R.string.select_campus_to_continue, Snackbar.LENGTH_SHORT).show();
            }
            if (i == (isWelcomeMode ? 3 : 2) && mViewModel.getUserType().equals(SetupViewModel.USER_STUDENT)) {
                Snackbar.make(mParent, "Select department & program to continue", Snackbar.LENGTH_SHORT).show();
            }
            if (i == (isWelcomeMode ? 4 : 3) && mViewModel.getUserType().equals(SetupViewModel.USER_STUDENT)) {
                Snackbar.make(mParent, "Select level & term to continue", Snackbar.LENGTH_SHORT).show();
            }
            if (i == (isWelcomeMode ? 3 : 2) && mViewModel.getUserType().equals(SetupViewModel.USER_TEACHER)) {
                Snackbar.make(mParent, "Select department to continue", Snackbar.LENGTH_SHORT).show();
            }
            if (i == (isWelcomeMode ? 4 : 3) && mViewModel.getUserType().equals(SetupViewModel.USER_TEACHER)) {
                Snackbar.make(mParent, "Select initial to continue", Snackbar.LENGTH_SHORT).show();
            }
            if (i == (isWelcomeMode ? 5 : 4)) {
                if (mViewModel.getUserType().equals(SetupViewModel.USER_STUDENT) && InputHelper.isEmpty(mViewModel.getSection())) {
                    Snackbar.make(mParent, "Select section to continue", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (mViewModel.getValidationListener().getValue() != null
                            && mViewModel.getValidationListener().getValue().getStatus() == Status.ERROR) {
                        Snackbar.make(mParent, "No routines found!", Snackbar.LENGTH_SHORT).show();
                        mViewModel.validate();
                    } else {
                        mViewModel.validate();
                    }
                }
            }
        });

    }

}
