package fr.xebia.xebicon.vote.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.xebia.xebicon.vote.R
import fr.xebia.xebicon.vote.model.ObstacleInfo
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.AVAILABILITY_END
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.HAS_VOTED
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.HOT_DEPLOYMENT_END
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.HOT_DEPLOYMENT_START
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.OBSTACLE
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.OBSTACLE_CLEARED
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.TRAIN_DEPARTURE_END
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.TRAIN_DEPARTURE_START
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.VOTE_TRAIN_END
import kotlinx.android.synthetic.main.fragment_status.view.*

class StatusFragment : Fragment() {

    companion object {

        val CURRENT_STATE = "CURRENT_STATE"
        val VOTE_ID = "VOTE_ID"
        val STATUS_MESSAGE = "STATUS_MESSAGE"
        val OBSTACLE_INFO = "OBSTACLE_INFO"

        fun newInstance(currentState: String, voteId: Int, message: String): StatusFragment {
            val fragment = StatusFragment()
            val bundle = Bundle()
            bundle.putString(CURRENT_STATE, currentState)
            bundle.putInt(VOTE_ID, voteId)
            bundle.putString(STATUS_MESSAGE, message)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstanceWithObstacle(currentState: String, voteId: Int, message: String, obstacleInfo: ObstacleInfo): StatusFragment {
            val fragment = StatusFragment()
            val bundle = Bundle()
            bundle.putString(CURRENT_STATE, currentState)
            bundle.putInt(VOTE_ID, voteId)
            bundle.putString(STATUS_MESSAGE, message)
            bundle.putParcelable(OBSTACLE_INFO, obstacleInfo)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (activity as AppCompatActivity).supportActionBar!!.show()
        val view = inflater!!.inflate(R.layout.fragment_status, container, false)
        if (arguments != null && arguments.containsKey(CURRENT_STATE) && arguments.containsKey(VOTE_ID)) {
            val currentState = arguments.getString(CURRENT_STATE)
            val stateMessage = arguments.getString(STATUS_MESSAGE)
            val voteId = arguments.getInt(VOTE_ID)

            // view visibilities & properties
            if (currentState == HAS_VOTED || currentState == VOTE_TRAIN_END) {
                view.hashtag_text.visibility = View.INVISIBLE
            } else {
                view.hashtag_text.visibility = View.VISIBLE
            }

            if (currentState == TRAIN_DEPARTURE_START) {
                view.departure_image.setImageResource(R.drawable.train)
            } else {
                view.departure_image.setImageResource(0)
            }

            // view content
            if (voteId == 1) {
                view.hashtag_text.text = getString(R.string.hashtag_bdx)
                view.hashtag_text.background = resources.getDrawable(R.color.colorBdx, null)
                when (currentState) {
                    HAS_VOTED, VOTE_TRAIN_END -> {
                        view.departure_image_container.background = resources.getDrawable(R.drawable.check, null)
                    }
                    TRAIN_DEPARTURE_START -> {
                        view.departure_image_container.background = resources.getDrawable(R.drawable.round_shape_bdx_big, null)
                    }
                    TRAIN_DEPARTURE_END -> {
                        view.departure_image_container.background = resources.getDrawable(R.drawable.station_bdx, null)
                    }
                    AVAILABILITY_END -> {
                        view.departure_image_container.background = resources.getDrawable(R.drawable.wine_cups, null)
                    }
                    OBSTACLE_CLEARED -> {
                        view.departure_image_container.background = resources.getDrawable(R.drawable.rail_bdx, null)
                    }
                    OBSTACLE -> {
                        val obstacleInfo = arguments.getParcelable<ObstacleInfo>(OBSTACLE_INFO)
                        displayObstacleImage(view, obstacleInfo.obstacleType.toLowerCase())
                    }
                }
            } else {
                view.hashtag_text.text = getString(R.string.hashtag_lyon)
                view.hashtag_text.background = resources.getDrawable(R.color.colorLyon, null)
                when (currentState) {
                    HAS_VOTED, VOTE_TRAIN_END -> {
                        view.departure_image_container.background = resources.getDrawable(R.drawable.check, null)
                    }
                    TRAIN_DEPARTURE_START -> {
                        view.departure_image_container.background = resources.getDrawable(R.drawable.round_shape_lyon_big, null)
                    }
                    TRAIN_DEPARTURE_END -> {
                        view.departure_image_container.background = resources.getDrawable(R.drawable.station_lyon, null)
                    }
                    HOT_DEPLOYMENT_START -> {
                        view.departure_image_container.background = resources.getDrawable(R.drawable.redlight, null)
                    }
                    HOT_DEPLOYMENT_END -> {
                        view.departure_image_container.background = resources.getDrawable(R.drawable.greenlight, null)
                    }
                    OBSTACLE_CLEARED -> {
                        view.departure_image_container.background = resources.getDrawable(R.drawable.rail_lyon, null)
                    }
                    OBSTACLE -> {
                        val obstacleInfo = arguments.getParcelable<ObstacleInfo>(OBSTACLE_INFO)
                        displayObstacleImage(view, obstacleInfo.obstacleType.toLowerCase())
                    }
                }
            }
            view.status_message.text = stateMessage
        }
        return view
    }

    fun displayObstacleImage(view: View, animal: String) {
        when (animal) {
            "cow" -> {
                view.departure_image_container.background = resources.getDrawable(R.drawable.cow, null)
            }
            "chicken" -> {
                view.departure_image_container.background = resources.getDrawable(R.drawable.chicken, null)
            }
            "rabbit" -> {
                view.departure_image_container.background = resources.getDrawable(R.drawable.rabbit, null)
            }
            "pig" -> {
                view.departure_image_container.background = resources.getDrawable(R.drawable.pig, null)
            }
            "horse" -> {
                view.departure_image_container.background = resources.getDrawable(R.drawable.horse, null)
            }
        }
    }
}
