package fr.xebia.xebicon.vote.services

import android.content.Intent
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * A service that extends FirebaseInstanceIdService to handle the creation, rotation,
 * and updating of registration tokens. This is required for sending to specific devices
 * or for creating device groups.
 */

class MyFirebaseInstanceIDService() : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        //TODO : handle refresh token
        val refreshedToken = FirebaseInstanceId.getInstance().token
        val intent = Intent(this, FirebaseRegistrationService::class.java)
        startService(intent)
    }
}