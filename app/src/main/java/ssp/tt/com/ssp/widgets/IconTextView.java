package ssp.tt.com.ssp.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


/**
 * @author
 *
 */
public class IconTextView extends AppCompatTextView {

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public IconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            setType(context);
        }
    }



    /**
     * @param context
     * @param attrs
     */
    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            setType(context);
        }
    }

    /**
     * @param context
     */
    public IconTextView(Context context) {
        super(context);
        if (!isInEditMode()) {
            setType(context);
        }
    }

    private void setType(Context context) {
        this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome-webfont.ttf"));

    }


}