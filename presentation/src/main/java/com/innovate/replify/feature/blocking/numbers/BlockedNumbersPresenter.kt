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
package com.innovate.replify.feature.blocking.numbers

import com.innovate.replify.common.base.QkPresenter
import com.innovate.replify.interactor.MarkUnblocked
import com.innovate.replify.repository.BlockingRepository
import com.innovate.replify.repository.ConversationRepository
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BlockedNumbersPresenter @Inject constructor(
    private val blockingRepo: BlockingRepository,
    private val conversationRepo: ConversationRepository,
    private val markUnblocked: MarkUnblocked
) : QkPresenter<BlockedNumbersView, BlockedNumbersState>(
        BlockedNumbersState(numbers = blockingRepo.getBlockedNumbers())
) {

    override fun bindIntents(view: BlockedNumbersView) {
        super.bindIntents(view)

        view.unblockAddress()
                .doOnNext { id ->
                    blockingRepo.getBlockedNumber(id)?.address
                            ?.let(conversationRepo::getThreadId)
                            ?.let { threadId -> markUnblocked.execute(listOf(threadId)) }
                }
                .doOnNext(blockingRepo::unblockNumber)
                .subscribeOn(Schedulers.io())
                .autoDisposable(view.scope())
                .subscribe()

        view.addAddress()
                .autoDisposable(view.scope())
                .subscribe { view.showAddDialog() }

        view.saveAddress()
                .subscribeOn(Schedulers.io())
                .autoDisposable(view.scope())
                .subscribe { address -> blockingRepo.blockNumber(address) }
    }

}
