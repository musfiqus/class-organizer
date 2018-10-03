package bd.edu.daffodilvarsity.classorganizer.utils;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.design.circularreveal.CircularRevealCompat;
import android.support.design.circularreveal.CircularRevealWidget;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.R;

public class ViewUtils {
    public static float dpFromPx(final float px) {
        return px / ClassOrganizer.getInstance().getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final float dp) {
        return dp * ClassOrganizer.getInstance().getResources().getDisplayMetrics().density;
    }

    public static CircularProgressDrawable getProgressDrawable(float stroke, float radius, int color) {
        Context context = ClassOrganizer.getInstance();
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_ATOP);
        circularProgressDrawable.setStrokeWidth(pxFromDp(stroke));
        circularProgressDrawable.setCenterRadius(pxFromDp(radius));
        return circularProgressDrawable;
    }

    public static ArrayList<String> capitalizeList(List<String> list) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (String s : list) {
            if (s != null) {
                s = InputHelper.capitalizeFirstLetter(s);
            }
            stringArrayList.add(s);
        }
        return stringArrayList;
    }

    public static ArrayList<String> upperCaseList(List<String> list) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (String s : list) {
            if (s != null) {
                s = s.toUpperCase();
            }
            stringArrayList.add(s);
        }
        return stringArrayList;
    }

    public static List<Integer> getRandomColors(int size) {
        List<Integer> colors = new ArrayList<>();
        colors.add(R.color.md_yellow_600);
        colors.add(R.color.md_blue_grey_700);
        colors.add(R.color.md_light_green_A400);
        colors.add(R.color.md_deep_purple_A400);
        colors.add(R.color.md_blue_A400);
        colors.add(R.color.md_pink_600);
        for (int i = 0, j = 0; i < (size-colors.size()); i++) {
            if (j == colors.size()) {
                j = 0;
            }
            colors.add(colors.get(j));
            j++;

        }
        return colors;
    }

    public static void circularRevealView(View viewToReveal, View clippingView, Animator.AnimatorListener animatorListener, boolean reveal) {
        // get the center for the clipping circle
        int cx = (clippingView.getLeft() + clippingView.getRight()) / 2;
        int cy = (clippingView.getTop() + clippingView.getBottom()) / 2;

        // get the final radius for the clipping circle
        int dx = Math.max(cx, viewToReveal.getWidth() - cx);
        int dy = Math.max(cy, viewToReveal.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        CircularRevealWidget revealWidget;
        if (viewToReveal instanceof CircularRevealWidget) {
            revealWidget = (CircularRevealWidget) viewToReveal;
        } else {
            return;
        }
        // Android native animator
        Animator animator = CircularRevealCompat.createCircularReveal(revealWidget, cx, cy, reveal ? 0: finalRadius, reveal ? finalRadius: 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        if (animatorListener != null) {
            animator.addListener(animatorListener);
        }
        animator.start();
    }

    public static void animateMute(View imageMuted, View imageUnmuted, boolean isMuted) {
        imageMuted.setVisibility(View.VISIBLE);
        imageUnmuted.setVisibility(View.VISIBLE);

        imageUnmuted.animate().scaleX(isMuted ? 0 : 1).scaleY(isMuted ? 0 : 1).alpha(isMuted ? 0 : 1).start();
        imageMuted.animate().scaleX(isMuted ? 1 : 0).scaleY(isMuted ? 1 : 0).alpha(isMuted ? 1 : 0).start();
    }

    public static Intent getOpenFacebookIntent(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo("com.facebook.katana",0);
            if (ai.enabled) {
                return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/385913368514734"));
            } else {
                return new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/classorganizerdiu"));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/classorganizerdiu"));
        }
    }

    public static int fetchAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }
}
