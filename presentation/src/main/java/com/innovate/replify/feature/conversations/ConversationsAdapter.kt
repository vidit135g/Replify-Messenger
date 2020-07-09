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
package com.innovate.replify.feature.conversations

import android.content.Context
import android.graphics.Typeface
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.innovate.replify.R
import com.innovate.replify.common.Navigator
import com.innovate.replify.common.base.QkRealmAdapter
import com.innovate.replify.common.base.QkViewHolder
import com.innovate.replify.common.util.Colors
import com.innovate.replify.common.util.DateFormatter
import com.innovate.replify.common.util.extensions.resolveThemeColor
import com.innovate.replify.common.util.extensions.setTint
import com.innovate.replify.databinding.ConversationListItemBinding
import com.innovate.replify.model.Conversation
import com.innovate.replify.util.PhoneNumberUtils
import javax.inject.Inject

class ConversationsAdapter @Inject constructor(
    private val colors: Colors,
    private val context: Context,
    private val dateFormatter: DateFormatter,
    private val navigator: Navigator,
    private val phoneNumberUtils: PhoneNumberUtils
) : QkRealmAdapter<Conversation, ConversationListItemBinding>() {

    init {
        // This is how we access the threadId for the swipe actions
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<ConversationListItemBinding> {
        return QkViewHolder(parent, ConversationListItemBinding::inflate).apply {
            if (viewType == 1) {
                val textColorPrimary = parent.context.resolveThemeColor(android.R.attr.textColorPrimary)

                binding.title.setTypeface(binding.title.typeface, Typeface.BOLD)

                binding.snippet.setTypeface(binding.snippet.typeface, Typeface.BOLD)
                binding.snippet.setTextColor(textColorPrimary)
                binding.snippet.maxLines = 5

                binding.unread.isVisible = true

                binding.date.setTypeface(binding.date.typeface, Typeface.BOLD)
                binding.date.setTextColor(textColorPrimary)
            }

            binding.root.setOnClickListener {
                val conversation = getItem(adapterPosition) ?: return@setOnClickListener
                when (toggleSelection(conversation.id, false)) {
                    true -> binding.root.isActivated = isSelected(conversation.id)
                    false -> navigator.showConversation(conversation.id)
                }
            }

            binding.root.setOnLongClickListener {
                val conversation = getItem(adapterPosition) ?: return@setOnLongClickListener true
                toggleSelection(conversation.id)
                binding.root.isActivated = isSelected(conversation.id)
                true
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<ConversationListItemBinding>, position: Int) {
        val conversation = getItem(position) ?: return

        holder.binding.root.isActivated = isSelected(conversation.id)

        holder.binding.avatars.recipients = conversation.recipients
        holder.binding.title.collapseEnabled = conversation.recipients.size > 1
        holder.binding.title.text = conversation.getTitle()
        holder.binding.date.text = conversation.date.takeIf { it > 0 }?.let(dateFormatter::getConversationTimestamp)
        holder.binding.snippet.text = when {
            conversation.draft.isNotEmpty() -> context.getString(R.string.main_draft, conversation.draft)
            conversation.me -> context.getString(R.string.main_sender_you, conversation.snippet)
            else -> conversation.snippet
        }
        holder.binding.pinned.isVisible = conversation.pinned

        // If the last message wasn't incoming, then the colour doesn't really matter anyway
        val lastMessage = conversation.lastMessage
        val recipient = when {
            conversation.recipients.size == 1 || lastMessage == null -> conversation.recipients.firstOrNull()
            else -> conversation.recipients.find { recipient ->
                phoneNumberUtils.compare(recipient.address, lastMessage.address)
            }
        }

        holder.binding.unread.setTint(colors.theme(recipient).theme)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: -1
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.unread == false) 0 else 1
    }
}
