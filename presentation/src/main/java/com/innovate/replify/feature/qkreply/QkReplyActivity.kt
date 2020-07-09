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
package com.innovate.replify.feature.qkreply

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.innovate.replify.R
import com.innovate.replify.common.base.QkThemedActivity
import com.innovate.replify.common.util.extensions.*
import com.innovate.replify.databinding.QkreplyActivityBinding
import com.innovate.replify.feature.compose.MessagesAdapter
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import dagger.android.AndroidInjection
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class QkReplyActivity : QkThemedActivity(), QkReplyView {

    @Inject lateinit var adapter: MessagesAdapter
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override val menuItemIntent: Subject<Int> = PublishSubject.create()
    override val textChangedIntent by lazy { binding.message.textChanges() }
    override val changeSimIntent by lazy { binding.sim.clicks() }
    override val sendIntent by lazy { binding.send.clicks() }

    private val binding by viewBinding(QkreplyActivityBinding::inflate)
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[QkReplyViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        setFinishOnTouchOutside(prefs.qkreplyTapDismiss.get())
        setContentView(binding.root)
        window.setBackgroundDrawable(null)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        viewModel.bindView(this)

        binding.toolbar.clipToOutline = true

        binding.messages.adapter = adapter
        binding.messages.adapter?.autoScrollToStart(binding.messages)
        binding.messages.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() = binding.messages.scrollToPosition(adapter.itemCount - 1)
        })

        // These theme attributes don't apply themselves on API 21
        if (Build.VERSION.SDK_INT <= 22) {
            binding.toolbar.setBackgroundTint(resolveThemeColor(R.attr.colorPrimary))
            binding.background.setBackgroundTint(resolveThemeColor(android.R.attr.windowBackground))
            binding.messageBackground.setBackgroundTint(resolveThemeColor(R.attr.bubbleColor))
            binding.composeBackgroundGradient.setBackgroundTint(resolveThemeColor(android.R.attr.windowBackground))
            binding.composeBackgroundSolid.setBackgroundTint(resolveThemeColor(android.R.attr.windowBackground))
        }
    }

    override fun render(state: QkReplyState) {
        if (state.hasError) {
            finish()
        }

        threadId.onNext(state.threadId)

        title = state.title

        binding.toolbar.menu.findItem(R.id.expand)?.isVisible = !state.expanded
        binding.toolbar.menu.findItem(R.id.collapse)?.isVisible = state.expanded

        adapter.data = state.data

        binding.counter.text = state.remaining
        binding.counter.setVisible(binding.counter.text.isNotBlank())

        binding.sim.setVisible(state.subscription != null)
        binding.sim.contentDescription = getString(R.string.compose_sim_cd, state.subscription?.displayName)
        binding.simIndex.text = "${state.subscription?.simSlotIndex?.plus(1)}"

        binding.send.isEnabled = state.canSend
        binding.send.imageAlpha = if (state.canSend) 255 else 128
    }

    override fun setDraft(draft: String) {
        binding.message.setText(draft)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.qkreply, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menuItemIntent.onNext(item.itemId)
        return true
    }

    override fun getActivityThemeRes(black: Boolean) = when {
        black -> R.style.AppThemeDialog_Black
        else -> R.style.AppThemeDialog
    }

}