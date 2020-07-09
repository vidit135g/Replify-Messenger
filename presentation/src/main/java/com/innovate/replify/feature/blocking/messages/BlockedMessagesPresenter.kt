/*
 * Copyright (C) 2019 Moez Bhatti <innovate.bhatti@gmail.com>
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
package com.innovate.replify.feature.blocking.messages

import com.innovate.replify.R
import com.innovate.replify.blocking.BlockingClient
import com.innovate.replify.common.Navigator
import com.innovate.replify.common.base.QkPresenter
import com.innovate.replify.interactor.DeleteConversations
import com.innovate.replify.repository.ConversationRepository
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject

class BlockedMessagesPresenter @Inject constructor(
    conversationRepo: ConversationRepository,
    private val blockingClient: BlockingClient,
    private val deleteConversations: DeleteConversations,
    private val navigator: Navigator
) : QkPresenter<BlockedMessagesView, BlockedMessagesState>(BlockedMessagesState(
        data = conversationRepo.getBlockedConversationsAsync()
)) {

    override fun bindIntents(view: BlockedMessagesView) {
        super.bindIntents(view)

        view.menuReadyIntent
                .autoDisposable(view.scope())
                .subscribe { newState { copy() } }

        view.optionsItemIntent
                .withLatestFrom(view.selectionChanges) { itemId, conversations ->
                    when (itemId) {
                        R.id.block -> {
                            view.showBlockingDialog(conversations, false)
                            view.clearSelection()
                        }
                        R.id.delete -> {
                            view.showDeleteDialog(conversations)
                        }
                    }

                }
                .autoDisposable(view.scope())
                .subscribe()

        view.confirmDeleteIntent
                .autoDisposable(view.scope())
                .subscribe { conversations ->
                    deleteConversations.execute(conversations)
                    view.clearSelection()
                }

        view.conversationClicks
                .autoDisposable(view.scope())
                .subscribe { threadId -> navigator.showConversation(threadId) }

        view.selectionChanges
                .autoDisposable(view.scope())
                .subscribe { selection -> newState { copy(selected = selection.size) } }

        view.backClicked
                .withLatestFrom(state) { _, state ->
                    when (state.selected) {
                        0 -> view.goBack()
                        else -> view.clearSelection()
                    }
                }
                .autoDisposable(view.scope())
                .subscribe()
    }

}
