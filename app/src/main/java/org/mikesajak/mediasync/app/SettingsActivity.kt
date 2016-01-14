package org.mikesajak.mediasync.app

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.text.TextUtils

/**
 * Created by mike on 07.01.16.
 */
class SettingsActivity : PreferenceActivity() {

    // TODO: this is sample settings activity - make it real!!!

    companion object {
        fun isXLargeTablet(context: Context) =
            (context.resources.configuration.screenLayout
                    and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE

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

            preference.onPreferenceChangeListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.context).getString(preference.key, ""))
        }


        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        class GeneralPreferenceFragment : PreferenceFragment() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                addPreferencesFromResource(R.xml.pref_general)

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

}
