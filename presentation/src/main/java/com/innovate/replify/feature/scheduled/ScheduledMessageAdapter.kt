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
package com.innovate.replify.feature.scheduled

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.innovate.replify.common.base.QkRealmAdapter
import com.innovate.replify.common.base.QkViewHolder
import com.innovate.replify.common.util.DateFormatter
import com.innovate.replify.databinding.ScheduledMessageListItemBinding
import com.innovate.replify.model.Contact
import com.innovate.replify.model.Recipient
import com.innovate.replify.model.ScheduledMessage
import com.innovate.replify.repository.ContactRepository
import com.innovate.replify.util.PhoneNumberUtils
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class ScheduledMessageAdapter @Inject constructor(
    private val context: Context,
    private val contactRepo: ContactRepository,
    private val dateFormatter: DateFormatter,
    private val phoneNumberUtils: PhoneNumberUtils
) : QkRealmAdapter<ScheduledMessage, ScheduledMessageListItemBinding>() {

    private val contacts by lazy { contactRepo.getContacts() }
    private val contactCache = ContactCache()
    private val imagesViewPool = RecyclerView.RecycledViewPool()

    val clicks: Subject<Long> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<ScheduledMessageListItemBinding> {
        return QkViewHolder(parent, ScheduledMessageListItemBinding::inflate).apply {
            binding.attachments.adapter = ScheduledMessageAttachmentAdapter(context)
            binding.attachments.setRecycledViewPool(imagesViewPool)

            binding.root.setOnClickListener {
                val message = getItem(adapterPosition) ?: return@setOnClickListener
                clicks.onNext(message.id)
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<ScheduledMessageListItemBinding>, position: Int) {
        val message = getItem(position) ?: return

        // GroupAvatarView only accepts recipients, so map the phone numbers to recipients
        holder.binding.avatars.recipients = message.recipients.map { address -> Recipient(address = address) }

        holder.binding.recipients.text = message.recipients.joinToString(",") { address ->
            contactCache[address]?.name?.takeIf { it.isNotBlank() } ?: address
        }

        holder.binding.date.text = dateFormatter.getScheduledTimestamp(message.date)
        holder.binding.body.text = message.body

        val adapter = holder.binding.attachments.adapter as ScheduledMessageAttachmentAdapter
        adapter.data = message.attachments.map(Uri::parse)
        holder.binding.attachments.isVisible = message.attachments.isNotEmpty()
    }

    /**
     * Cache the contacts in a map by the address, because the messages we're binding don't have
     * a reference to the contact.
     */
    private inner class ContactCache : HashMap<String, Contact?>() {

        override fun get(key: String): Contact? {
            if (super.get(key)?.isValid != true) {
                set(key, contacts.firstOrNull { contact ->
                    contact.numbers.any {
                        phoneNumberUtils.compare(it.address, key)
                    }
                })
            }

            return super.get(key)?.takeIf { it.isValid }
        }

    }

}
