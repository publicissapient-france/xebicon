package fr.xebia.xebicon.vote.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.xebia.xebicon.vote.R
import kotlinx.android.synthetic.main.fragment_start_end.view.*

class StartEndFragment : Fragment() {

    companion object {

        val IS_START = "IS_START"

        fun newInstance(isStart: Boolean): StartEndFragment {
            val fragment = StartEndFragment()
            val bundle = Bundle()
            bundle.putBoolean(IS_START, isStart)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (activity as AppCompatActivity).supportActionBar!!.hide()

        val view = inflater!!.inflate(R.layout.fragment_start_end, container, false)
        if (arguments != null && arguments.containsKey(IS_START)) {
            val isStart = arguments.getBoolean(IS_START)
            if (isStart) {
                view.state_text_view.text = getString(R.string.screen_welcome_title)
            } else {
                view.state_text_view.text = getString(R.string.screen_end_title)
            }
        }
        return view
    }
}
