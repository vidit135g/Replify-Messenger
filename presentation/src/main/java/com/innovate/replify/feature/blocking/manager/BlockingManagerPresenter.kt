package com.innovate.replify.feature.blocking.manager

import android.content.Context
import com.innovate.replify.R
import com.innovate.replify.blocking.BlockingClient
import com.innovate.replify.blocking.CallControlBlockingClient
import com.innovate.replify.blocking.QksmsBlockingClient
import com.innovate.replify.blocking.ShouldIAnswerBlockingClient
import com.innovate.replify.common.Navigator
import com.innovate.replify.common.base.QkPresenter
import com.innovate.replify.manager.AnalyticsManager
import com.innovate.replify.repository.ConversationRepository
import com.innovate.replify.util.Preferences
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BlockingManagerPresenter @Inject constructor(
    private val analytics: AnalyticsManager,
    private val callControl: CallControlBlockingClient,
    private val context: Context,
    private val conversationRepo: ConversationRepository,
    private val navigator: Navigator,
    private val prefs: Preferences,
    private val qksms: QksmsBlockingClient,
    private val shouldIAnswer: ShouldIAnswerBlockingClient
) : QkPresenter<BlockingManagerView, BlockingManagerState>(BlockingManagerState(
        blockingManager = prefs.blockingManager.get(),
        callControlInstalled = callControl.isAvailable(),
        siaInstalled = shouldIAnswer.isAvailable()
)) {

    init {
        disposables += prefs.blockingManager.asObservable()
                .subscribe { manager -> newState { copy(blockingManager = manager) } }
    }

    override fun bindIntents(view: BlockingManagerView) {
        super.bindIntents(view)

        view.activityResumed()
                .map { callControl.isAvailable() }
                .distinctUntilChanged()
                .autoDisposable(view.scope())
                .subscribe { available -> newState { copy(callControlInstalled = available) } }

        view.activityResumed()
                .map { shouldIAnswer.isAvailable() }
                .distinctUntilChanged()
                .autoDisposable(view.scope())
                .subscribe { available -> newState { copy(siaInstalled = available) } }

        view.qksmsClicked()
                .observeOn(Schedulers.io())
                .map { getAddressesToBlock(qksms) }
                .switchMap { numbers -> qksms.block(numbers).andThen(Observable.just(Unit)) } // Hack
                .autoDisposable(view.scope())
                .subscribe {
                    analytics.setUserProperty("Blocking Manager", "replify")
                    prefs.blockingManager.set(Preferences.BLOCKING_MANAGER_QKSMS)
                }

        view.callControlClicked()
                .filter {
                    val installed = callControl.isAvailable()
                    if (!installed) {
                        analytics.track("Install Call Control")
                        navigator.installCallControl()
                    }

                    val enabled = prefs.blockingManager.get() == Preferences.BLOCKING_MANAGER_CC
                    installed && !enabled
                }
                .observeOn(Schedulers.io())
                .map { getAddressesToBlock(callControl) }
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap { numbers ->
                    when (numbers.size) {
                        0 -> Observable.just(true)
                        else -> view.showCopyDialog(context.getString(R.string.blocking_manager_call_control_title))
                                .toObservable()
                    }
                }
                .doOnNext { newState { copy() } } // Radio button may have been selected when it shouldn't, fix it
                .filter { it }
                .observeOn(Schedulers.io())
                .map { getAddressesToBlock(callControl) } // This sucks. Can't wait to use coroutines
                .switchMap { numbers -> callControl.block(numbers).andThen(Observable.just(Unit)) } // Hack
                .autoDisposable(view.scope())
                .subscribe {
                    callControl.getAction("callcontrol").blockingGet()
                    analytics.setUserProperty("Blocking Manager", "Call Control")
                    prefs.blockingManager.set(Preferences.BLOCKING_MANAGER_CC)
                }

        view.siaClicked()
                .filter {
                    val installed = shouldIAnswer.isAvailable()
                    if (!installed) {
                        analytics.track("Install SIA")
                        navigator.installSia()
                    }

                    val enabled = prefs.blockingManager.get() == Preferences.BLOCKING_MANAGER_SIA
                    installed && !enabled
                }
                .autoDisposable(view.scope())
                .subscribe {
                    analytics.setUserProperty("Blocking Manager", "SIA")
                    prefs.blockingManager.set(Preferences.BLOCKING_MANAGER_SIA)
                }
    }

    private fun getAddressesToBlock(client: BlockingClient) = conversationRepo.getBlockedConversations()
            .fold(listOf<String>(), { numbers, conversation -> numbers + conversation.recipients.map { it.address } })
            .filter { number -> client.getAction(number).blockingGet() !is BlockingClient.Action.Block }

}
