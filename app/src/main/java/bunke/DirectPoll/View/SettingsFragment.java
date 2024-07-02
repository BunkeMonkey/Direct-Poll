package bunke.DirectPoll.View;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import bunke.DirectPoll.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}