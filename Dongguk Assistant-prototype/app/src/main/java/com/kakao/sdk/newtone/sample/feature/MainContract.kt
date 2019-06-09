package com.kakao.sdk.newtone.sample.feature


interface MainContract {

    interface View{

    }

    interface Presenter{
        fun sendMessage(message: String)
    }

}