package fr.xebia.xebicon.vote.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import fr.xebia.xebicon.vote.VoteApplication
import fr.xebia.xebicon.vote.core.api.VoteAPI
import fr.xebia.xebicon.vote.model.RegisterInfo
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class FirebaseRegistrationService : IntentService("MyFirebaseRegistrationService") {

    @Inject lateinit var mVoteAPI: VoteAPI

    init {
        VoteApplication.GRAPH.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("RegistrationService: ", "REGISTER")

        if (refreshedToken != null){
            mVoteAPI.register(RegisterInfo(refreshedToken.toString()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ success ->
                    }, { error ->
                    })
        }

    }
}