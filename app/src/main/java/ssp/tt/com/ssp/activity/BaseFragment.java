package ssp.tt.com.ssp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ssp.tt.com.ssp.ViewInterface;


public abstract class BaseFragment extends Fragment implements ViewInterface {

    protected Context context;
    Unbinder unbinder;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        View view = provideYourFragmentView(inflater, parent, savedInstanseState);
        context = this.getActivity();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public abstract View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState);





    public void changeActivity(Intent i) {
        startActivity(i);
    }

    public void changeActivityForResult(Intent i, int reqCode) {
        startActivityForResult(i, reqCode);
    }

    public void changeActivity(Class clz) {
        Intent i = new Intent(getContext(), clz);
        changeActivity(i);
    }

    public void changeActivityClearBackStack(Class clz) {
        Intent i = new Intent(getContext(), clz);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        changeActivity(i);
    }



    public void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public List<Fragment> getVisibleFragments() {
        return getChildFragmentManager().getFragments();
    }

    @Override
    public void onImageChosen(int requestCode, Uri uri) {
        for (Fragment fragment : getVisibleFragments()) {
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).onImageChosen(requestCode, uri);
            }
        }
    }

}