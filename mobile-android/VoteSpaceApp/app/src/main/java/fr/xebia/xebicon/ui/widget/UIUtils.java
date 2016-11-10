package fr.xebia.xebicon.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.utils.Compatibility;

/**
 * https://github.com/google/iosched/blob/master/android/src/main/java/com/google/samples/apps/iosched/util/UIUtils.java
 */
public class UIUtils {

    private static final int[] STATE_CHECKED = new int[]{android.R.attr.state_checked};
    private static final int[] STATE_UNCHECKED = new int[]{};

    private static final Handler HANDLER = new Handler();
    public static final float SESSION_BG_COLOR_SCALE_FACTOR = 0.65f;
    public static final float SESSION_PHOTO_SCRIM_ALPHA = 0.75f;
    private static final int[] RES_IDS_ACTION_BAR_SIZE = {android.R.attr.actionBarSize};

    public static int setColorAlpha(int color, float alpha) {
        int alphaInt = Math.min(Math.max((int) (alpha * 255.0f), 0), 255);
        return Color.argb(alphaInt, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static void setOrAnimatePlusCheckIcon(Context context, final ImageView imageView, boolean isCheck, boolean allowAnimate) {
        if (!Compatibility.isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
            compatSetOrAnimatePlusCheckIcon(context, imageView, isCheck, allowAnimate);
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if (!(drawable instanceof AnimatedStateListDrawable)) {
            drawable = context.getResources().getDrawable(R.drawable.add_schedule_fab_icon_anim);
            imageView.setImageDrawable(drawable);
        }
        imageView.setColorFilter(isCheck ?
                context.getResources().getColor(R.color.theme_accent_1) : Color.WHITE);
        if (allowAnimate) {
            imageView.setImageState(isCheck ? STATE_UNCHECKED : STATE_CHECKED, false);
            drawable.jumpToCurrentState();
            imageView.setImageState(isCheck ? STATE_CHECKED : STATE_UNCHECKED, false);
        } else {
            imageView.setImageState(isCheck ? STATE_CHECKED : STATE_UNCHECKED, false);
            drawable.jumpToCurrentState();
        }
    }

    public static void compatSetOrAnimatePlusCheckIcon(Context context, final ImageView imageView, boolean isCheck,
                                                boolean allowAnimate) {

        final int imageResId = isCheck
                ? R.drawable.add_schedule_button_icon_checked
                : R.drawable.add_schedule_button_icon_unchecked;

        if (imageView.getTag() != null) {
            if (imageView.getTag() instanceof Animator) {
                Animator anim = (Animator) imageView.getTag();
                anim.end();
                imageView.setAlpha(1f);
            }
        }

        if (allowAnimate && isCheck) {
            int duration = context.getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            Animator outAnimator = ObjectAnimator.ofFloat(imageView, View.ALPHA, 0f);
            outAnimator.setDuration(duration / 2);
            outAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imageView.setImageResource(imageResId);
                }
            });

            AnimatorSet inAnimator = new AnimatorSet();
            outAnimator.setDuration(duration);
            inAnimator.playTogether(
                    ObjectAnimator.ofFloat(imageView, View.ALPHA, 1f),
                    ObjectAnimator.ofFloat(imageView, View.SCALE_X, 0f, 1f),
                    ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 0f, 1f)
            );

            AnimatorSet set = new AnimatorSet();
            set.playSequentially(outAnimator, inAnimator);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imageView.setTag(null);
                }
            });
            imageView.setTag(set);
            set.start();
        } else {
            new Handler().post(() -> imageView.setImageResource(imageResId));
        }
    }

    public static int scaleSessionColorToDefaultBG(int color) {
        return scaleColor(color, SESSION_BG_COLOR_SCALE_FACTOR, false);
    }

    public static int scaleColor(int color, float factor, boolean scaleAlpha) {
        return Color.argb(scaleAlpha ? (Math.round(Color.alpha(color) * factor)) : Color.alpha(color),
                Math.round(Color.red(color) * factor), Math.round(Color.green(color) * factor),
                Math.round(Color.blue(color) * factor));
    }

    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
