package fr.xebia.xebicon.vote.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import fr.xebia.xebicon.vote.R
import fr.xebia.xebicon.vote.VoteApplication.Companion.GRAPH
import fr.xebia.xebicon.vote.core.api.VoteAPI
import fr.xebia.xebicon.vote.core.preferences.UserPreferences
import fr.xebia.xebicon.vote.model.VoteInfo
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.HAS_VOTED
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.MEDIA_TYPE
import kotlinx.android.synthetic.main.fragment_vote.view.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class VoteFragment : Fragment() {

    companion object {
        fun newInstance(message: String): VoteFragment {
            val fragment = VoteFragment()
            val bundle = Bundle()
            bundle.putString(KeynoteActivity.STATUS_MESSAGE, message)
            fragment.arguments = bundle
            return fragment
        }
    }

    var voteId = 0
    var voteCity: String = ""

    @Inject lateinit var mVoteAPI: VoteAPI
    @Inject lateinit var userPreferences: UserPreferences

    val refreshedToken = FirebaseInstanceId.getInstance().token
    var parentActivity: KeynoteActivity? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        GRAPH.inject(this)
        (activity as AppCompatActivity).supportActionBar!!.show()
        val view = inflater!!.inflate(R.layout.fragment_vote, container, false)
        if (arguments != null && arguments.containsKey(StatusFragment.STATUS_MESSAGE)) {
            view.vote_status_message.text = arguments.getString(StatusFragment.STATUS_MESSAGE)
        }
        view.button_bdx.alpha = .5f
        view.btx_lyon.alpha = .5f

        parentActivity = activity as KeynoteActivity?

        view.button_bdx.setOnClickListener {
            voteId = 1
            voteCity = getString(R.string.bordeaux)
            view.button_bdx.animate().alpha(1f).duration = 200
            view.btx_lyon.animate().alpha(.5f).duration = 200

            view.vote_button.isEnabled = true
        }

        view.btx_lyon.setOnClickListener {
            voteId = 2
            voteCity = getString(R.string.lyon)
            view.button_bdx.animate().alpha(.5f).duration = 200
            view.btx_lyon.animate().alpha(1f).duration = 200
            view.vote_button.isEnabled = true
        }

        view.vote_button.setOnClickListener {
            mVoteAPI.createVote(VoteInfo(refreshedToken!!, MEDIA_TYPE, voteId))
                    .subscribeOn(Schedulers.io())//io / new thread / trampoline  ?
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ success ->
                        Toast.makeText(activity, "Vous avez voté : " + voteCity, Toast.LENGTH_SHORT)
                                .show()
                        parentActivity?.showStatusFragment(HAS_VOTED, voteId, getString(R.string.has_voted))
                        userPreferences.setVotedTrain(voteId)
                    }, { error ->
                        Toast.makeText(activity, error.message, Toast.LENGTH_SHORT)
                                .show()
                    })
        }

        return view
    }
}