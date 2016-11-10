package fr.xebia.xebicon.vote.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import fr.xebia.xebicon.vote.BuildConfig
import fr.xebia.xebicon.vote.R
import fr.xebia.xebicon.vote.VoteApplication.Companion.GRAPH
import fr.xebia.xebicon.vote.core.api.VoteAPI
import fr.xebia.xebicon.vote.core.preferences.UserPreferences
import fr.xebia.xebicon.vote.model.KeynoteState
import fr.xebia.xebicon.vote.model.ObstacleInfo
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class KeynoteActivity : AppCompatActivity() {

    companion object {
        const val NEW_STATE = "NEW_STATE"
        const val NEW_STATE_MESSAGE = "NEW_STATE_MESSAGE"
        const val MEDIA_TYPE = "ANDROID"
        const val STATUS_MESSAGE = "STATUS_MESSAGE"
        const val OBSTACLE_INFO = "OBSTACLE_INFO"

        const val INTENT_FILTER_STATE_CHANGED = "fr.xebia.xebicon.keynote.STATE_CHANGED"

        const val TRAIN_DEPARTURE_START = "TRAIN_DEPARTURE_START"
        const val TRAIN_DEPARTURE_END = "TRAIN_DEPARTURE_END"
        const val AVAILABILITY_START = "AVAILABILITY_START"
        const val AVAILABILITY_END = "AVAILABILITY_END"
        const val OBSTACLE = "OBSTACLE"
        const val OBSTACLE_CLEARED = "OBSTACLE_CLEARED"
        const val KEYNOTE_START = "KEYNOTE_START"
        const val KEYNOTE_END = "KEYNOTE_END"
        const val HAS_VOTED = "HAS_VOTED"
        const val VOTE_TRAIN_START = "VOTE_TRAIN_START"
        const val VOTE_TRAIN_END = "VOTE_TRAIN_END"
        const val HOT_DEPLOYMENT_START = "HOT_DEPLOYMENT_START"
        const val HOT_DEPLOYMENT_END = "HOT_DEPLOYMENT_END"
        const val TRAIN_POSITION = "TRAIN_POSITION"
    }

    @Inject lateinit var userPreferences: UserPreferences
    @Inject lateinit var mVoteAPI: VoteAPI

    val myReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            consumeNotification(intent)
        }
    }

    private fun consumeNotification(intent: Intent) {
        val newState = intent.getStringExtra(NEW_STATE)
        val newStateMessage = intent.getStringExtra(NEW_STATE_MESSAGE)
        if (newState.equals(OBSTACLE)) {
            val obstacleInfo = intent.getParcelableExtra<ObstacleInfo>(OBSTACLE_INFO)
            showFragmentWithObstacle(newState, newStateMessage, obstacleInfo)
        } else {
            showFragment(newState, newStateMessage)
        }
    }

    fun showFragmentWithObstacle(currentState: String, message: String, obstacleInfo: ObstacleInfo) {
        val voteId = userPreferences.getVotedTrain()
        showStatusFragment(currentState, voteId, message, obstacleInfo)
    }

    fun showFragment(currentState: String?, message: String) {
        val voteId = userPreferences.getVotedTrain()
        when (currentState) {
            KEYNOTE_START -> {
                showStartEndFragment(true)
            }
            TRAIN_DEPARTURE_START,
            TRAIN_DEPARTURE_END,
            AVAILABILITY_END,
            HOT_DEPLOYMENT_START,
            HOT_DEPLOYMENT_END,
            OBSTACLE_CLEARED,
            VOTE_TRAIN_END,
            HAS_VOTED
            -> {
                if (voteId > 0) {
                    showStatusFragment(currentState, voteId, message)
                }
            }

            VOTE_TRAIN_START -> {
                showVoteFragment(message)
            }

            AVAILABILITY_START -> {
                if (voteId > 0) {
                    showBuyFragment()
                }
            }

            TRAIN_POSITION,
            KEYNOTE_END -> {
                userPreferences.setVotedTrain(0)
                showStartEndFragment(false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GRAPH.inject(this)
        setContentView(R.layout.activity_keynote)

        val toolbar = findViewById(R.id.keynote_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        if (intent.hasExtra(NEW_STATE)) {
            consumeNotification(intent)
        } else {
            mVoteAPI.getState()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<KeynoteState>() {

                        override fun onCompleted() {
                        }

                        override fun onError(e: Throwable?) {
                            // fail silently
                        }

                        override fun onNext(keynoteState: KeynoteState) {
                            // only keynote_start, vote_start & vote_end are managed here
                            showFragment(keynoteState.state, keynoteState.message)
                        }
                    })
        }

        val filter = IntentFilter(INTENT_FILTER_STATE_CHANGED)
        this.registerReceiver(myReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

    fun showVoteFragment(message: String) {
        supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                VoteFragment.newInstance(message), "VOTE_FRAGMENT"
        ).commitAllowingStateLoss()
    }

    fun showBuyFragment() {
        supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                BuyFragment(), "BUY_FRAGMENT"
        ).commitAllowingStateLoss()
    }

    fun showStartEndFragment(isStart: Boolean) {
        supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                StartEndFragment.newInstance(isStart), "START_END_FRAGMENT"
        ).commitAllowingStateLoss()
    }

    fun showStatusFragment(currentState: String, voteId: Int, message: String) {
        supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                StatusFragment.newInstance(currentState, voteId, message),
                "STATUS_FRAGMENT"
        ).commitAllowingStateLoss()
    }

    fun showStatusFragment(currentState: String, voteId: Int, message: String, obstacleInfo: ObstacleInfo) {
        supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                StatusFragment.newInstanceWithObstacle(currentState, voteId, message, obstacleInfo),
                "STATUS_FRAGMENT"
        ).commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (BuildConfig.DEBUG)
            menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (BuildConfig.DEBUG) {
            val id = item!!.itemId
            if (id == R.id.goToVote) {
                showVoteFragment("Veuillez voter pour votre train")
                return true
            }
            if (id == R.id.goToBuy) {
                showBuyFragment()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
