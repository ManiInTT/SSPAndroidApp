package ssp.tt.com.ssp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.io.File;


import ssp.tt.com.ssp.GlideApp;

import static ssp.tt.com.ssp.utils.Util.EXCEPTION;


public class GlideUtils {

    public static void showImage(Context context, ImageView imageView, int placeHolder, String imageUrl) {
        try {
            if (context != null)
                GlideApp.with(context)
                        .load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(placeHolder)
                        .into(imageView);
        } catch (Exception e) {
            Log.e(EXCEPTION, e.toString());
        }
    }

    public static void showImage(Context context, ImageView imageView, Drawable placeHolder, String imageUrl) {
        try {
            if (context != null)
                GlideApp.with(context)
                        .load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(placeHolder)
                        .into(imageView);
        } catch (Exception e) {
            Log.e(EXCEPTION, e.toString());
        }
    }

    public static void showImagePNG(Context context, ImageView imageView, int placeHolder, String imageUrl) {
        try {

            DrawableTransitionOptions crossFadeTransition = DrawableTransitionOptions.with(
                    new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
            );
            if (context != null)
                GlideApp.with(context)
                        .load(imageUrl)
                        .transition(crossFadeTransition)
                        .placeholder(placeHolder)
                        .into(imageView);
        } catch (Exception e) {
            Log.e(EXCEPTION, e.toString());
        }
    }


    public static void showRoundImage(Context context, ImageView imageView, int placeHolder, String imageUrl) {
        try {
            if (context != null)
                GlideApp.with(context)
                        .load(imageUrl)
                        .thumbnail(Glide.with(context).load(placeHolder).circleCrop())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .transform(new CircleCrop())
                        .into(imageView);
        } catch (Exception e) {
            Log.e(EXCEPTION, e.toString());
        }
    }



    public static void showCrossFadeImage(Context context, ImageView imageView, String imageUrl) {
        try {
            if (context != null)
                GlideApp.with(context)
                        .load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
        } catch (Exception e) {
            Log.e(EXCEPTION, e.toString());
        }
    }



    public static void showRoundImage(Context context, ImageView imageView, String imageUrl) {
        try {
            if (context != null)
                GlideApp.with(context)
                        .load(imageUrl)
                        .transform(new CircleCrop())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
        } catch (Exception e) {
            Log.e(EXCEPTION, e.toString());
        }
    }

    public static void showRoundImage(Context context, ImageView imageView, File file) {
        try {
            if (context != null)
                GlideApp.with(context)
                        .load(file)
                        .transform(new CircleCrop())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
        } catch (Exception e) {
            Log.e(EXCEPTION, e.toString());
        }
    }
}
