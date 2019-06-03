package com.rahmat.app.androidchat.feature


interface MainContract {

    interface View{

    }

    interface Presenter{
        fun sendMessage(message: String)
    }

}