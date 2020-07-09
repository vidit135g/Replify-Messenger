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
package com.innovate.replify.common.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.innovate.replify.R
import com.innovate.replify.common.Navigator
import com.innovate.replify.common.util.Colors
import com.innovate.replify.common.util.extensions.setBackgroundTint
import com.innovate.replify.common.util.extensions.setTint
import com.innovate.replify.common.util.extensions.viewBinding
import com.innovate.replify.databinding.AvatarViewBinding
import com.innovate.replify.injection.appComponent
import com.innovate.replify.model.Recipient
import com.innovate.replify.util.GlideApp
import javax.inject.Inject

class AvatarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    @Inject lateinit var colors: Colors
    @Inject lateinit var navigator: Navigator
    private lateinit var theme: Colors.Theme

    private val binding = viewBinding(AvatarViewBinding::inflate)

    private var lookupKey: String? = null
    private var name: String? = null
    private var photoUri: String? = null
    private var lastUpdated: Long? = null

    init {
        if (!isInEditMode) {
            appComponent.inject(this)
            theme = colors.theme()
        }

        setBackgroundResource(R.drawable.circle)
        clipToOutline = true
    }

    /**
     * Use the [contact] information to display the avatar.
     */
    fun setRecipient(recipient: Recipient?) {
        lookupKey = recipient?.contact?.lookupKey
        name = recipient?.contact?.name
        photoUri = recipient?.contact?.photoUri
        lastUpdated = recipient?.contact?.lastUpdate
        theme = colors.theme(recipient)
        updateView()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (!isInEditMode) {
            updateView()
        }
    }

    private fun updateView() {
        // Apply theme
        setBackgroundTint(theme.theme)
        binding.initial.setTextColor(theme.textPrimary)
        binding.icon.setTint(theme.textPrimary)

        if (name?.isNotEmpty() == true) {
            val initials = name
                    ?.substringBefore(',')
                    ?.split(" ").orEmpty()
                    .filter { subname -> subname.isNotEmpty() }
                    .map { subname -> subname[0].toString() }

            binding.initial.text = if (initials.size > 1) initials.first() + initials.last() else initials.first()
            binding.icon.visibility = GONE
        } else {
            binding.initial.text = null
            binding.icon.visibility = VISIBLE
        }

        binding.photo.setImageDrawable(null)
        photoUri?.let { photoUri ->
            GlideApp.with(binding.photo)
                    .load(photoUri)
                    .into(binding.photo)
        }
    }
}
