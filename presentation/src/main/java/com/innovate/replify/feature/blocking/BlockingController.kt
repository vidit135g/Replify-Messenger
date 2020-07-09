/*
 * Copyright (C) 2017 Moez Bhatti <innovate.bhatti@gmail.com>
 *
 * This file is part of replify.
 *
 * replify is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * replify is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with replify.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.innovate.replify.feature.blocking

import android.view.View
import com.bluelinelabs.conductor.RouterTransaction
import com.innovate.replify.R
import com.innovate.replify.common.QkChangeHandler
import com.innovate.replify.common.base.QkController
import com.innovate.replify.common.util.Colors
import com.innovate.replify.common.util.extensions.animateLayoutChanges
import com.innovate.replify.common.widget.QkSwitch
import com.innovate.replify.databinding.BlockingControllerBinding
import com.innovate.replify.feature.blocking.manager.BlockingManagerController
import com.innovate.replify.feature.blocking.messages.BlockedMessagesController
import com.innovate.replify.feature.blocking.numbers.BlockedNumbersController
import com.innovate.replify.injection.appComponent
import com.jakewharton.rxbinding2.view.clicks
import javax.inject.Inject

class BlockingController : QkController<BlockingView, BlockingState, BlockingPresenter, BlockingControllerBinding>(
        BlockingControllerBinding::inflate
), BlockingView {

    override val blockingManagerIntent by lazy { binding.blockingManager.clicks() }
    override val blockedNumbersIntent by lazy { binding.blockedNumbers.clicks() }
    override val blockedMessagesIntent by lazy { binding.blockedMessages.clicks() }
    override val dropClickedIntent by lazy { binding.drop.clicks() }

    @Inject lateinit var colors: Colors
    @Inject override lateinit var presenter: BlockingPresenter

    init {
        appComponent.inject(this)
        retainViewMode = RetainViewMode.RETAIN_DETACH
    }

    override fun onViewCreated() {
        super.onViewCreated()
        binding.parent.postDelayed({ binding.parent.animateLayoutChanges = true }, 100)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.blocking_title)
        showBackButton(true)
    }

    override fun render(state: BlockingState) {
        binding.blockingManager.summary = state.blockingManager
        binding.drop.widget<QkSwitch>().isChecked = state.dropEnabled
        binding.blockedMessages.isEnabled = !state.dropEnabled
    }

    override fun openBlockedNumbers() {
        router.pushController(RouterTransaction.with(BlockedNumbersController())
                .pushChangeHandler(QkChangeHandler())
                .popChangeHandler(QkChangeHandler()))
    }

    override fun openBlockedMessages() {
        router.pushController(RouterTransaction.with(BlockedMessagesController())
                .pushChangeHandler(QkChangeHandler())
                .popChangeHandler(QkChangeHandler()))
    }

    override fun openBlockingManager() {
        router.pushController(RouterTransaction.with(BlockingManagerController())
                .pushChangeHandler(QkChangeHandler())
                .popChangeHandler(QkChangeHandler()))
    }

}
