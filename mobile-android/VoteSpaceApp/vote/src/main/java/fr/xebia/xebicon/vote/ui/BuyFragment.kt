package fr.xebia.xebicon.vote.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import fr.xebia.xebicon.vote.R
import fr.xebia.xebicon.vote.VoteApplication.Companion.GRAPH
import fr.xebia.xebicon.vote.core.api.VoteAPI
import fr.xebia.xebicon.vote.model.Article
import fr.xebia.xebicon.vote.model.BuyInfo
import kotlinx.android.synthetic.main.fragment_buy.view.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class BuyFragment : Fragment() {

    @Inject lateinit var mVoteAPI: VoteAPI

    var itemToBuy: Article = Article.none

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        GRAPH.inject(this)
        (activity as AppCompatActivity).supportActionBar!!.show()
        val view = inflater!!.inflate(R.layout.fragment_buy, container, false)

        view.buy_pauillac.alpha = .3f
        view.buy_margaux.alpha = .3f
        view.buy_pessac.alpha = .3f

        view.buy_pauillac.setOnClickListener {
            itemToBuy = Article.pauillac

            view.buy_pauillac.animate().alpha(1f).duration = 200
            view.buy_margaux.animate().alpha(.3f).duration = 200
            view.buy_pessac.animate().alpha(.3f).duration = 200

            view.buy_button.isEnabled = true
        }

        view.buy_margaux.setOnClickListener {
            itemToBuy = Article.margaux

            view.buy_pauillac.animate().alpha(.3f).duration = 200
            view.buy_margaux.animate().alpha(1f).duration = 200
            view.buy_pessac.animate().alpha(.3f).duration = 200

            view.buy_button.isEnabled = true
        }

        view.buy_pessac.setOnClickListener {
            itemToBuy = Article.pessac

            view.buy_pauillac.animate().alpha(.3f).duration = 200
            view.buy_margaux.animate().alpha(.3f).duration = 200
            view.buy_pessac.animate().alpha(1f).duration = 200

            view.buy_button.isEnabled = true
        }

        view.buy_button.setOnClickListener {
            mVoteAPI.buy(BuyInfo(itemToBuy))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ success ->
                        Toast.makeText(activity, "Achat effectué: " + itemToBuy, Toast.LENGTH_SHORT)
                                .show()
                    }, { error ->
                        Toast.makeText(activity, error.message, Toast.LENGTH_SHORT)
                                .show()
                    })
        }

        return view
    }
}
