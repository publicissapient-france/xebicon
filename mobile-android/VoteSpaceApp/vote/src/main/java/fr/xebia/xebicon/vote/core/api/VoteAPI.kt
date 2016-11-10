package fr.xebia.xebicon.vote.core.api

import fr.xebia.xebicon.vote.model.BuyInfo
import fr.xebia.xebicon.vote.model.KeynoteState
import fr.xebia.xebicon.vote.model.RegisterInfo
import fr.xebia.xebicon.vote.model.VoteInfo
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import rx.Observable

interface VoteAPI {

    @POST("/vote/station/")
    fun createVote(@Body voteInfo: VoteInfo): Observable<Response<ResponseBody>>

    @POST("/purchase/")
    fun buy(@Body buyInfo: BuyInfo): Observable<Response<ResponseBody>>

    @POST("/register/")
    fun register(@Body registerInfo: RegisterInfo): Observable<Response<ResponseBody>>

    @GET("/state/")
    fun getState(): Observable<KeynoteState>
}
