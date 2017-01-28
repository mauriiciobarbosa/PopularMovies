package com.udacity.mauricio.popularmovies.gui.view;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.udacity.mauricio.popularmovies.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.actv_settings)
public class SettingsActivity extends BaseActivity {

    @ViewById
    protected Toolbar toolbar;

    @AfterViews
    protected void init() {
        toolbar.setTitle(getString(R.string.title_preferences));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.content, new MovieSettingsFragment())
                .commit();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
