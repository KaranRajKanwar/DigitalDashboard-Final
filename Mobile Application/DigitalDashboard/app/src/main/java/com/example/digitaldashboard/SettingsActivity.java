package com.example.digitaldashboard;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.Toast;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .add(R.id.idSettings, new SettingFragment())
                .commit();
    }

    public static class SettingFragment extends PreferenceFragment {
        int i = 0;

        @Override
        public void onCreate(@Nullable Bundle savedInstancesState) {
            super.onCreate(savedInstancesState);
            addPreferencesFromResource(R.xml.settings);


            final Preference langpref = (Preference) findPreference("langpref");
            langpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    return true;
                }
            });


            final Preference secretmenu = (Preference) findPreference("secretmenu");
            secretmenu.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //                    i++;
                    //                    if (i == 5) {
                    //                        i = 0;
                    Toast.makeText(getActivity(), "THIS IS THE SECRET MENU!", Toast.LENGTH_LONG).show();
                    return false;
                }
            });


            final CheckBoxPreference units = (CheckBoxPreference) getPreferenceManager().findPreference("units");
            units.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("true")) {
                        Toast.makeText(getActivity(), "Imperial units enabled ",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "English units enabled ",
                                Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }
    }
}