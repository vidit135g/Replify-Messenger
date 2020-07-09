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
package com.innovate.replify.feature.compose.part

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.innovate.replify.common.base.QkViewHolder
import com.innovate.replify.common.util.Colors
import com.innovate.replify.model.Message
import com.innovate.replify.model.MmsPart
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

abstract class PartBinder<Binding : ViewBinding>(
    val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> Binding
) {

    val clicks: Subject<Long> = PublishSubject.create()

    abstract var theme: Colors.Theme

    fun <T: ViewBinding> bindPart(
        holder: QkViewHolder<T>,
        part: MmsPart,
        message: Message,
        canGroupWithPrevious: Boolean,
        canGroupWithNext: Boolean
    ): Boolean {
        val castHolder = holder as? QkViewHolder<Binding>

        if (!canBindPart(part) || castHolder == null) {
            return false
        }

        bindPartInternal(castHolder, part, message, canGroupWithPrevious, canGroupWithNext)

        return true
    }

    abstract fun canBindPart(part: MmsPart): Boolean

    protected abstract fun bindPartInternal(
        holder: QkViewHolder<Binding>,
        part: MmsPart,
        message: Message,
        canGroupWithPrevious: Boolean,
        canGroupWithNext: Boolean
    )

}
