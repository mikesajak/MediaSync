package org.mikesajak.mediasync.app

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.*
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.settings_layout.*

/**
 * Created by mike on 07.01.16.
 */
class SettingsActivity : PreferenceActivity() {


    // TODO: this is sample settings activity - make it real!!!

    companion object {
        val TAG = "SettingsActivity"

        private val ALWAYS_SIMPLE_PREFS = false;

        fun isXLargeTablet(context: Context) =
            (context.resources.configuration.screenLayout
                    and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE

        /**
         * Determines whether the simplified settings UI should be shown. This is
         * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
         * doesn't have newer APIs like {@link PreferenceFragment}, or the device
         * doesn't have an extra-large screen. In these cases, a single-pane
         * "simplified" settings UI should be shown.
         */
        private fun isSimplePreferences(context: Context): Boolean {
            return ALWAYS_SIMPLE_PREFS
                    || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                    || !isXLargeTablet(context);
        }

        fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, value ->
                preference.summary = when (preference) {
                        is ListPreference -> {
                            val index = preference.findIndexOfValue(value.toString())
                            if (index >= 0) preference.entries[index] else null
                        }

                        is RingtonePreference ->
                            if (TextUtils.isEmpty(value.toString()))
                                preference.context.resources.getText(R.string.pref_ringtone_silent)
                            else {
                                val ringtone = RingtoneManager.getRingtone(preference.context, Uri.parse(value.toString()))
                                ringtone?.getTitle(preference.context)
                            }

                        else -> value.toString()
                    }
                true
            }

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.context)

            preference.onPreferenceChangeListener.onPreferenceChange(preference,
                    sharedPreferences.getString(preference.key, ""))

            sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
                preference.onPreferenceChangeListener.onPreferenceChange(preference,
                        sharedPreferences.getString(preference.key, ""))
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        class PrefsFragment : PreferenceFragment() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                val category = arguments.getString("category")
                when (category) {
                    "category_general" -> addPreferencesFromResource(R.xml.pref_general)
                    "category_nofification" -> addPreferencesFromResource(R.xml.pref_notification)
                    "category_data_sync" -> addPreferencesFromResource(R.xml.pref_data_sync)
                }
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        class GeneralPreferenceFragment : PreferenceFragment() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                addPreferencesFromResource(R.xml.pref_general)
//                activity.title = getString(R.string.pref_header_general)

                // bind the summaries of EditText/List/Diarog/Ringtone preferences
                // to their values. When their values change, their summaries are updated
                // to reflect the new value, per the Android Design guidelines
                SettingsActivity.bindPreferenceSummaryToValue(findPreference("example_text"))
                SettingsActivity.bindPreferenceSummaryToValue(findPreference("example_list"))
            }
        }

        /**
         * This fragment show notification preferences only. It is used when the activity is showing a two-pane settings UI.
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        class NotificationPreferenceFragment : PreferenceFragment() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                addPreferencesFromResource(R.xml.pref_notification)
//                activity.title = getString(R.string.pref_header_notifications)

                // Bind the summaries of EditText/List/Dialog/Ringtone preferences
                // to their values. When their values change, their summaries are
                // updated to reflect the new value, per the Android Design
                // guidelines.
                SettingsActivity.bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"))

            }
        }

        /**
         * This fragment shows data and sync preferences only. It is used when the
         * activity is showing a two-pane settings UI.
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        class DataSyncPreferenceFragment : PreferenceFragment() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                addPreferencesFromResource(R.xml.pref_data_sync)

                // Bind the summaries of EditText/List/Dialog/Ringtone preferences
                // to their values. When their values change, their summaries are
                // updated to reflect the new value, per the Android Design
                // guidelines.
                SettingsActivity.bindPreferenceSummaryToValue(findPreference("sync_frequency"))
                SettingsActivity.bindPreferenceSummaryToValue(findPreference("select_wifi"))

                val selectWifiPreference = findPreference("select_wifi")
                selectWifiPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val intent = Intent(activity, HomeWifiSelectionActivity::class.java)
                    startActivityForResult(intent, HomeWifiSelectionActivity.SELECT_WIFI_REQUEST)
                    false
                }

            }

            override fun onActivityCreated(savedInstanceState: Bundle?) {
                super.onActivityCreated(savedInstanceState)
                activity.title = getString(R.string.pref_header_data_sync)
            }

            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                super.onActivityResult(requestCode, resultCode, data)
                Log.d(TAG, "onActivityResult($requestCode, $resultCode, $data)")
                if (resultCode == Activity.RESULT_OK) {

                    val selectedWifi = data?.getStringExtra(HomeWifiSelectionActivity.WIFI_SELECTION_RESULT)

                    preferenceManager.sharedPreferences.edit()
                            .putString("select_wifi", selectedWifi)
                            .commit()
                }
            }
        }
    }

    override fun onIsMultiPane() = isXLargeTablet(this)

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    override fun isValidFragment(fragmentName: String?): Boolean {
        return when (fragmentName) {
            GeneralPreferenceFragment::class.java.name -> true
            NotificationPreferenceFragment::class.java.name -> true
            DataSyncPreferenceFragment::class.java.name -> true
            else -> super.isValidFragment(fragmentName)
        }
    }

//    override fun onPostCreate(savedInstanceState: Bundle?) {
//        super.onPostCreate(savedInstanceState)
//        setContentView(R.layout.settings_layout)
//
//        prepareLayout()
//    }

    private fun prepareLayout() {
        val root = findViewById(android.R.id.content) as ViewGroup
        val content = root.getChildAt(0)
        val toolbarContainer = View.inflate(this, R.layout.settings_layout, null) as LinearLayout

        root.removeAllViews()
        toolbarContainer.addView(content)
        root.addView(toolbarContainer)

        toolbar.title = this.title
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
        toolbar.setNavigationOnClickListener { view -> finish() }
    }

//    /**
//     * Shows the simplified settings UI if the device configuration if the
//     * device configuration dictates that a simplified, single-pane UI should be
//     * shown.
//     */
//    private fun setupSimplePreferencesScreen() {
//        if (!isSimplePreferences(this)) {
//            return;
//        }
//
//        // In the simplified UI, fragments are not used at all and we instead
//        // use the older PreferenceActivity APIs.
//
//        // Add 'general' preferences.
//        addPreferencesFromResource(R.xml.pref_general);
//
//        // Add 'notifications' preferences, and a corresponding header.
//        PreferenceCategory fakeHeader = new PreferenceCategory(this);
//        fakeHeader.setTitle(R.string.pref_header_notifications);
//        getPreferenceScreen().addPreference(fakeHeader);
//        addPreferencesFromResource(R.xml.pref_notification);
//
//        // Add 'data and sync' preferences, and a corresponding header.
//        fakeHeader = new PreferenceCategory(this);
//        fakeHeader.setTitle(R.string.pref_header_data_sync);
//        getPreferenceScreen().addPreference(fakeHeader);
//        addPreferencesFromResource(R.xml.pref_data_sync);
//
//        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
//        // their values. When their values change, their summaries are updated
//        // to reflect the new value, per the Android Design guidelines.
//        bindPreferenceSummaryToValue(findPreference("example_text"));
//        bindPreferenceSummaryToValue(findPreference("example_list"));
//        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        bindPreferenceSummaryToValue(findPreference("sync_frequency"));
//    }


}
