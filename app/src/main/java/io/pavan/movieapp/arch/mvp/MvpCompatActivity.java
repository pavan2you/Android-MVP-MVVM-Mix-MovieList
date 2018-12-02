package io.pavan.movieapp.arch.mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.reflect.Constructor;

/**
 * Created by pavan on 02/12/18
 *
 * 1. It clearly separates the business intelligence by platform specific challenges with the help
 * of <code>Presenter</code>
 *
 * 2. It obeys the pure mvp rule by implementing <code>IView</code> and letting itself behaving as a
 * dumb business view.
 */
public abstract class MvpCompatActivity extends AppCompatActivity implements IView {

    protected Presenter<? extends IView> mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPresenter(savedInstanceState);
        onCreateView(savedInstanceState);
    }

    protected void createPresenter(Bundle savedInstanceState) {
        try {
            @SuppressLint("WrongConstant")
            ActivityInfo ai = getPackageManager().getActivityInfo(getComponentName(),
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);

            Bundle bundle = ai.metaData;
            if (bundle != null) {
                String presenter = bundle.getString("presenter");
                if (presenter != null) {
                    onCreatePresenter(presenter, savedInstanceState);
                }
            }

            if (mPresenter == null) {
                mPresenter = new NullPresenter(this);
            }

            mPresenter.onCreate();
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void onCreatePresenter(String presenterClassName, Bundle savedInstanceState) {
        Class<Presenter<IView>> presenterClass;
        try {
            if (presenterClassName.startsWith(".")) {
                presenterClassName = getPackageName() + presenterClassName;
            }
            presenterClass = (Class<Presenter<IView>>) Class.forName(presenterClassName);
            Constructor[] constructors = presenterClass.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                Class<?>[] pTypes  = constructor.getParameterTypes();
                if (pTypes.length > 1) {
                    continue;
                }

                mPresenter = presenterClass.cast(constructor.newInstance(this));
                if (mPresenter != null) {
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onCreateView(Bundle savedInstanceState) {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        Log.d(getClass().getSimpleName(), "onStart");
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(getClass().getSimpleName(), "onResume");
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(getClass().getSimpleName(), "onPause");
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(getClass().getSimpleName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        Log.d(getClass().getSimpleName(), "onStop");
        mPresenter.onStop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (mPresenter instanceof NullPresenter) {
            super.onBackPressed();
        }
        else {
            mPresenter.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Presenter<?> getPresenter() {
        return mPresenter;
    }
}
