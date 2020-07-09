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
package com.innovate.replify.feature.compose.editing

import android.content.Context
import android.view.ViewGroup
import com.innovate.replify.R
import com.innovate.replify.common.base.QkAdapter
import com.innovate.replify.common.base.QkViewHolder
import com.innovate.replify.common.util.extensions.forwardTouches
import com.innovate.replify.databinding.PhoneNumberListItemBinding
import com.innovate.replify.extensions.Optional
import com.innovate.replify.model.PhoneNumber
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class PhoneNumberPickerAdapter @Inject constructor(
    private val context: Context
) : QkAdapter<PhoneNumber, PhoneNumberListItemBinding>() {

    val selectedItemChanges: Subject<Optional<Long>> = BehaviorSubject.create()

    private var selectedItem: Long? = null
        set(value) {
            data.indexOfFirst { number -> number.id == field }.takeIf { it != -1 }?.run(::notifyItemChanged)
            field = value
            data.indexOfFirst { number -> number.id == field }.takeIf { it != -1 }?.run(::notifyItemChanged)
            selectedItemChanges.onNext(Optional(value))
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<PhoneNumberListItemBinding> {
        return QkViewHolder(parent, PhoneNumberListItemBinding::inflate).apply {
            binding.number.binding.radioButton.forwardTouches(itemView)

            binding.root.setOnClickListener {
                val phoneNumber = getItem(adapterPosition)
                selectedItem = phoneNumber.id
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<PhoneNumberListItemBinding>, position: Int) {
        val phoneNumber = getItem(position)

        holder.binding.number.binding.radioButton.isChecked = phoneNumber.id == selectedItem
        holder.binding.number.binding.titleView.text = phoneNumber.address
        holder.binding.number.binding.summaryView.text = when (phoneNumber.isDefault) {
            true -> context.getString(R.string.compose_number_picker_default, phoneNumber.type)
            false -> phoneNumber.type
        }
    }

    override fun onDatasetChanged() {
        super.onDatasetChanged()
        selectedItem = data.find { number -> number.isDefault }?.id ?: data.firstOrNull()?.id
    }

}
