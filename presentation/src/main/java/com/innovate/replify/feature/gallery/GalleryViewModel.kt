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
package com.innovate.replify.feature.gallery

import android.content.Context
import com.innovate.replify.R
import com.innovate.replify.common.Navigator
import com.innovate.replify.common.base.QkViewModel
import com.innovate.replify.common.util.extensions.makeToast
import com.innovate.replify.extensions.mapNotNull
import com.innovate.replify.interactor.SaveImage
import com.innovate.replify.manager.PermissionManager
import com.innovate.replify.repository.ConversationRepository
import com.innovate.replify.repository.MessageRepository
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject
import javax.inject.Named

class GalleryViewModel @Inject constructor(
    conversationRepo: ConversationRepository,
    @Named("partId") private val partId: Long,
    private val context: Context,
    private val messageRepo: MessageRepository,
    private val navigator: Navigator,
    private val saveImage: SaveImage,
    private val permissions: PermissionManager
) : QkViewModel<GalleryView, GalleryState>(GalleryState()) {

    init {
        disposables += Flowable.just(partId)
                .mapNotNull(messageRepo::getMessageForPart)
                .mapNotNull { message -> message.threadId }
                .doOnNext { threadId -> newState { copy(parts = messageRepo.getPartsForConversation(threadId)) } }
                .doOnNext { threadId ->
                    newState {
                        copy(title = conversationRepo.getConversation(threadId)?.getTitle())
                    }
                }
                .subscribe()
    }

    override fun bindView(view: GalleryView) {
        super.bindView(view)

        // When the screen is touched, toggle the visibility of the navigation UI
        view.screenTouched()
                .withLatestFrom(state) { _, state -> state.navigationVisible }
                .map { navigationVisible -> !navigationVisible }
                .autoDisposable(view.scope())
                .subscribe { navigationVisible -> newState { copy(navigationVisible = navigationVisible) } }

        // Save image to device
        view.optionsItemSelected()
                .filter { itemId -> itemId == R.id.save }
                .filter { permissions.hasStorage().also { if (!it) view.requestStoragePermission() } }
                .withLatestFrom(view.pageChanged()) { _, part -> part.id }
                .autoDisposable(view.scope())
                .subscribe { partId -> saveImage.execute(partId) { context.makeToast(R.string.gallery_toast_saved) } }

        // Share image externally
        view.optionsItemSelected()
                .filter { itemId -> itemId == R.id.share }
                .filter { permissions.hasStorage().also { if (!it) view.requestStoragePermission() } }
                .withLatestFrom(view.pageChanged()) { _, part -> part.id }
                .autoDisposable(view.scope())
                .subscribe { partId -> messageRepo.savePart(partId)?.let(navigator::shareFile) }
    }

}
