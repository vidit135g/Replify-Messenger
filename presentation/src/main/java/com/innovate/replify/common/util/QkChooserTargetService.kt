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
package com.innovate.replify.common.util

import android.content.ComponentName
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.Build
import android.service.chooser.ChooserTarget
import android.service.chooser.ChooserTargetService
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import com.innovate.replify.R
import com.innovate.replify.feature.compose.ComposeActivity
import com.innovate.replify.injection.appComponent
import com.innovate.replify.model.Conversation
import com.innovate.replify.repository.ConversationRepository
import com.innovate.replify.util.GlideApp
import com.innovate.replify.util.tryOrNull
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.M)
class QkChooserTargetService : ChooserTargetService() {

    @Inject lateinit var conversationRepo: ConversationRepository

    override fun onCreate() {
        appComponent.inject(this)
        super.onCreate()
    }

    override fun onGetChooserTargets(targetActivityName: ComponentName?, matchedFilter: IntentFilter?): List<ChooserTarget> {
        return conversationRepo.getTopConversations()
                .take(3)
                .map(this::createShortcutForConversation)
    }

    private fun createShortcutForConversation(conversation: Conversation): ChooserTarget {
        val icon = when (conversation.recipients.size) {
            1 -> {
                val photoUri = conversation.recipients.first()?.contact?.photoUri
                val request = GlideApp.with(this)
                        .asBitmap()
                        .circleCrop()
                        .load(photoUri)
                        .submit()
                val bitmap = tryOrNull(false) { request.get() }

                if (bitmap != null) Icon.createWithBitmap(bitmap)
                else Icon.createWithResource(this, R.mipmap.ic_shortcut_person)
            }

            else -> Icon.createWithResource(this, R.mipmap.ic_shortcut_people)
        }

        val componentName = ComponentName(this, ComposeActivity::class.java)

        return ChooserTarget(conversation.getTitle(), icon, 1f, componentName, bundleOf("threadId" to conversation.id))
    }

}