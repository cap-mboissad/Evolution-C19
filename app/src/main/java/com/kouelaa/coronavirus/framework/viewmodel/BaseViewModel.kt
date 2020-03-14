package com.kouelaa.coronavirus.framework.viewmodel

import androidx.lifecycle.ViewModel

import kotlinx.coroutines.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import timber.log.Timber

import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(
    private val dispatcher: CoroutineDispatcher
) : ViewModel(), CoroutineScope {

    private var handler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable -> baseHandleException(throwable) }

    private var job: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = dispatcher + job + handler


    override fun onCleared() {
        super.onCleared()
        job.cancelChildren() // Cancel job on activity destroy. After destroy all children jobs will be cancelled automatically
    }


    private fun baseHandleException(throwable: Throwable) {
        Timber.d(throwable)
        handleException()
    }

    // Must be overridden by all ViewModels
    abstract fun handleException()

}

fun BaseViewModel.currentTimeStamp(): Long {
    return System.currentTimeMillis() / 1000
}

fun getYesterdayDate(): LocalDateTime {
    // TODO-(13/03/20)-kheirus: regler le probleme quand l'appli est a minuit
    return LocalDate.now().atStartOfDay().minusDays(2)
}