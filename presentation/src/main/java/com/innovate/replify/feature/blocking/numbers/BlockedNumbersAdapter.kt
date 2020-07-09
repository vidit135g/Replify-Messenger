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

import android.view.ViewGroup
import com.innovate.replify.common.base.QkRealmAdapter
import com.innovate.replify.common.base.QkViewHolder
import com.innovate.replify.databinding.BlockedNumberListItemBinding
import com.innovate.replify.model.BlockedNumber
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class BlockedNumbersAdapter : QkRealmAdapter<BlockedNumber, BlockedNumberListItemBinding>() {

    val unblockAddress: Subject<Long> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<BlockedNumberListItemBinding> {
        return QkViewHolder(parent, BlockedNumberListItemBinding::inflate).apply {
            binding.unblock.setOnClickListener {
                val number = getItem(adapterPosition) ?: return@setOnClickListener
                unblockAddress.onNext(number.id)
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<BlockedNumberListItemBinding>, position: Int) {
        val item = getItem(position)!!

        holder.binding.number.text = item.address
    }

}
