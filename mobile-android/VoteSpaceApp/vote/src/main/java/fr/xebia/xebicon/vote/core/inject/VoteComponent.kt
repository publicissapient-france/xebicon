package fr.xebia.xebicon.vote.core.inject

import dagger.Component
import fr.xebia.votespaceapp.core.inject.module.ApiModule
import fr.xebia.xebicon.vote.services.FirebaseRegistrationService
import fr.xebia.xebicon.vote.ui.BuyFragment
import fr.xebia.xebicon.vote.ui.KeynoteActivity
import fr.xebia.xebicon.vote.ui.VoteFragment
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApiModule::class))
interface VoteComponent {

    fun inject(keynoteActivity: KeynoteActivity)

    fun inject(voteFragment: VoteFragment)

    fun inject(buyFragment: BuyFragment)

    fun inject(registrationService: FirebaseRegistrationService)
}