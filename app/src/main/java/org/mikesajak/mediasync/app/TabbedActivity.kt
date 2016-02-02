package org.mikesajak.mediasync.app

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_tabbed.*
import kotlinx.android.synthetic.main.fragment_tabbed.*

/**
 * Created by mike on 02.02.16.
 */
class TabbedActivity : AppCompatActivity() {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private var sectionsPagerAdapter: SectionsPagerAdapter? = null

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed)

        setSupportActionBar(toolbar)

//        sectionsPagerAdapter =
//        viewPager.adapter =

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    internal class PlaceholderFragment : Fragment() {

        companion object {
            val ARG_SECTION_NUMBER = "section_number"

            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater?.inflate(R.layout.fragment_tabbed, container, false)
            section_label.text = getString(R.string.section_format, arguments.getInt(ARG_SECTION_NUMBER))
            return rootView
        }
    }

    internal class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "SECTION 1"
                1 -> "SECTION 2"
                2 -> "SECTION 3"
                else -> null
            }
        }
    }
}