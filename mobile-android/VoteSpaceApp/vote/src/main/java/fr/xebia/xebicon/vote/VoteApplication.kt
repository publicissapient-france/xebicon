package fr.xebia.xebicon.vote

import android.support.multidex.MultiDexApplication
import fr.xebia.votespaceapp.core.inject.module.ApiModule
import fr.xebia.xebicon.vote.core.inject.DaggerVoteComponent
import fr.xebia.xebicon.vote.core.inject.VoteComponent

open class VoteApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        GRAPH = DaggerVoteComponent.builder().apiModule(ApiModule(BuildConfig.BASE_URL, this)).build()
    }

    companion object {
        lateinit var GRAPH: VoteComponent
    }
}
