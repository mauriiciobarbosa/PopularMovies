package com.udacity.mauricio.popularmovies.gui.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.utils.AppUtils;

/**
 * Created by mauricio-MTM on 12/16/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String language = AppUtils.getPreferenceValue(this, getString(R.string.pref_language_key), getString(R.string.pref_language_default_value));
        language = language.substring(0, 2);
        AppUtils.setLocale(this, language);
    }

    protected void restart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
