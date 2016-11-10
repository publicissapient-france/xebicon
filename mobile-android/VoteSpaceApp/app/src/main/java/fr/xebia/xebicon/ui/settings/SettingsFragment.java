package fr.xebia.xebicon.ui.settings;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import fr.xebia.xebicon.R;
import timber.log.Timber;

public class SettingsFragment extends PreferenceFragment {

    public static final String TAG = "SettingsFragment";

    public static final String KEY_VERSION = "version_key";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = getActivity();
        addPreferencesFromResource(R.xml.settings);
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            Preference versionPref = findPreference(KEY_VERSION);
            versionPref.setSummary(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.d(e, "Couldn't get package name");
        }
    }
}
