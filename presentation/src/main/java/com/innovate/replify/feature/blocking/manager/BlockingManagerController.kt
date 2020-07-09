package com.innovate.replify.feature.blocking.manager

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import androidx.core.view.isVisible
import com.innovate.replify.R
import com.innovate.replify.common.base.QkController
import com.innovate.replify.databinding.BlockingManagerControllerBinding
import com.innovate.replify.injection.appComponent
import com.innovate.replify.util.Preferences
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class BlockingManagerController : QkController<BlockingManagerView, BlockingManagerState, BlockingManagerPresenter,
        BlockingManagerControllerBinding>(BlockingManagerControllerBinding::inflate), BlockingManagerView {

    @Inject override lateinit var presenter: BlockingManagerPresenter

    private val activityResumedSubject: PublishSubject<Unit> = PublishSubject.create()

    init {
        appComponent.inject(this)
        retainViewMode = RetainViewMode.RETAIN_DETACH
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.blocking_manager_title)
        showBackButton(true)
    }

    override fun onActivityResumed(activity: Activity) {
        activityResumedSubject.onNext(Unit)
    }

    override fun render(state: BlockingManagerState) {
        binding.qksms.binding.radioButton.isChecked = state.blockingManager == Preferences.BLOCKING_MANAGER_QKSMS

        binding.callControl.binding.radioButton.isChecked = state.blockingManager == Preferences.BLOCKING_MANAGER_CC
        binding.callControl.binding.widgetFrame.isVisible = !state.callControlInstalled

        binding.shouldIAnswer.binding.radioButton.isChecked = state.blockingManager == Preferences.BLOCKING_MANAGER_SIA
        binding.shouldIAnswer.binding.widgetFrame.isVisible = !state.siaInstalled
    }

    override fun activityResumed(): Observable<*> = activityResumedSubject
    override fun qksmsClicked(): Observable<*> = binding.qksms.clicks()
    override fun callControlClicked(): Observable<*> = binding.callControl.clicks()
    override fun siaClicked(): Observable<*> = binding.shouldIAnswer.clicks()

    override fun showCopyDialog(manager: String): Single<Boolean> = Single.create { emitter ->
        AlertDialog.Builder(activity)
                .setTitle(R.string.blocking_manager_copy_title)
                .setMessage(resources?.getString(R.string.blocking_manager_copy_summary, manager))
                .setPositiveButton(R.string.button_continue) { _, _ -> emitter.onSuccess(true) }
                .setNegativeButton(R.string.button_cancel) { _, _ -> emitter.onSuccess(false) }
                .setCancelable(false)
                .show()
    }

}
