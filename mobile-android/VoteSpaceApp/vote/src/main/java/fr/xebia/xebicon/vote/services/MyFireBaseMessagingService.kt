package fr.xebia.xebicon.vote.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import fr.xebia.xebicon.vote.R
import fr.xebia.xebicon.vote.model.ObstacleInfo
import fr.xebia.xebicon.vote.ui.KeynoteActivity
import fr.xebia.xebicon.vote.ui.KeynoteActivity.Companion.INTENT_FILTER_STATE_CHANGED

class MyFireBaseMessagingService() : FirebaseMessagingService() {

    companion object {
        val TAG = "FireBaseMessaging"
        const val KEYNOTE_STATE = "keynoteState"
        val MESSAGE = "message"
        val BLOCKED = "blocked"
        val OBSTACLE_TYPE = "obstacleType"

        fun extractData(data: Bundle?): Bundle? {
            if (data != null && data.containsKey(KEYNOTE_STATE)) {
                val newState = data.getString(KEYNOTE_STATE)
                if (newState != null && (
                        newState.equals(KeynoteActivity.TRAIN_DEPARTURE_START)
                                || newState.equals(KeynoteActivity.TRAIN_DEPARTURE_END)
                                || newState.equals(KeynoteActivity.AVAILABILITY_START)
                                || newState.equals(KeynoteActivity.AVAILABILITY_END)
                                || newState.equals(KeynoteActivity.OBSTACLE)
                                || newState.equals(KeynoteActivity.OBSTACLE_CLEARED)
                                || newState.equals(KeynoteActivity.KEYNOTE_START)
                                || newState.equals(KeynoteActivity.KEYNOTE_END)
                                || newState.equals(KeynoteActivity.VOTE_TRAIN_START)
                                || newState.equals(KeynoteActivity.VOTE_TRAIN_END)
                                || newState.equals(KeynoteActivity.HOT_DEPLOYMENT_START)
                                || newState.equals(KeynoteActivity.HOT_DEPLOYMENT_END)
                                || newState.equals(KeynoteActivity.TRAIN_POSITION)

                        )) {
                    var newStateMessage: String = ""
                    if (data.getString(MESSAGE) != null) {
                        newStateMessage = data.getString(MESSAGE)
                    }
                    Log.d(TAG, "Push notification received")
                    Log.d(TAG, "Push notification state: " + newState)
                    Log.d(TAG, "Push notification message: " + newStateMessage)

                    val bundle = Bundle()
                    bundle.putString(KeynoteActivity.NEW_STATE, newState)
                    bundle.putString(KeynoteActivity.NEW_STATE_MESSAGE, newStateMessage)

                    if (newState.equals(KeynoteActivity.OBSTACLE)) {
                        Log.d(TAG, "obstacle type: " + data.getString(OBSTACLE_TYPE))
                        val obstacleInfo = ObstacleInfo(data.getString(BLOCKED).toBoolean(), data.getString(OBSTACLE_TYPE))
                        bundle.putParcelable(KeynoteActivity.OBSTACLE_INFO, obstacleInfo)
                    }

                    return bundle
                }
            }

            return null
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        //FIXME: Use extractData method

        val data = remoteMessage?.data
        if (data != null && data.containsKey(KEYNOTE_STATE)) {
            val newState = data[KEYNOTE_STATE]
            if (newState != null && (
                    newState.equals(KeynoteActivity.TRAIN_DEPARTURE_START)
                            || newState.equals(KeynoteActivity.TRAIN_DEPARTURE_END)
                            || newState.equals(KeynoteActivity.AVAILABILITY_START)
                            || newState.equals(KeynoteActivity.AVAILABILITY_END)
                            || newState.equals(KeynoteActivity.OBSTACLE)
                            || newState.equals(KeynoteActivity.OBSTACLE_CLEARED)
                            || newState.equals(KeynoteActivity.KEYNOTE_START)
                            || newState.equals(KeynoteActivity.KEYNOTE_END)
                            || newState.equals(KeynoteActivity.VOTE_TRAIN_START)
                            || newState.equals(KeynoteActivity.VOTE_TRAIN_END)
                            || newState.equals(KeynoteActivity.HOT_DEPLOYMENT_START)
                            || newState.equals(KeynoteActivity.HOT_DEPLOYMENT_END)
                            || newState.equals(KeynoteActivity.TRAIN_POSITION)

                    )) {
                var newStateMessage = data[MESSAGE]
                if (newStateMessage.isNullOrBlank()) {
                    newStateMessage = newState
                }
                Log.d(TAG, "Push notification received")
                Log.d(TAG, "Push notification state: " + newState)
                Log.d(TAG, "Push notification message: " + newStateMessage)
                val intent = Intent(INTENT_FILTER_STATE_CHANGED)
                intent.putExtra(KeynoteActivity.NEW_STATE, newState)
                intent.putExtra(KeynoteActivity.NEW_STATE_MESSAGE, newStateMessage)

                if (newState.equals(KeynoteActivity.OBSTACLE)) {
                    Log.d(TAG, "obstacle type: " + data[OBSTACLE_TYPE])
                    val obstacleInfo = ObstacleInfo(data[BLOCKED]!!.toBoolean(), data[OBSTACLE_TYPE] as String)
                    intent.putExtra(KeynoteActivity.OBSTACLE_INFO, obstacleInfo)
                }
                sendBroadcast(intent)
            } else {
                sendNotification(this, remoteMessage?.notification?.body)
            }
        }
    }

    fun sendNotification(context: Context, messageBody: String?) {
        val pendingIntent = PendingIntent.
                getActivity(context,
                        0,
                        Intent(context, KeynoteActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_train)
                .setContentTitle(context.getString(R.string.push_notification_title))
                .setContentText(messageBody ?: "")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(0, notificationBuilder.build())
    }
}
