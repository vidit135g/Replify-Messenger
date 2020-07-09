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

import android.view.View
import androidx.appcompat.app.AlertDialog
import com.innovate.replify.R
import com.innovate.replify.common.base.QkController
import com.innovate.replify.common.util.Colors
import com.innovate.replify.databinding.BlockedNumbersAddDialogBinding
import com.innovate.replify.databinding.BlockedNumbersControllerBinding
import com.innovate.replify.injection.appComponent
import com.innovate.replify.util.PhoneNumberUtils
import com.jakewharton.rxbinding2.view.clicks
import com.innovate.replify.common.util.extensions.setBackgroundTint
import com.innovate.replify.common.util.extensions.setTint
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class BlockedNumbersController : QkController<BlockedNumbersView, BlockedNumbersState, BlockedNumbersPresenter,
        BlockedNumbersControllerBinding>(BlockedNumbersControllerBinding::inflate), BlockedNumbersView {

    @Inject override lateinit var presenter: BlockedNumbersPresenter
    @Inject lateinit var colors: Colors
    @Inject lateinit var phoneNumberUtils: PhoneNumberUtils

    private val adapter = BlockedNumbersAdapter()
    private val saveAddressSubject: Subject<String> = PublishSubject.create()

    init {
        appComponent.inject(this)
        retainViewMode = RetainViewMode.RETAIN_DETACH
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.blocked_numbers_title)
        showBackButton(true)
    }

    override fun onViewCreated() {
        super.onViewCreated()
        binding.add.setBackgroundTint(colors.theme().theme)
        binding.add.setTint(colors.theme().textPrimary)
        adapter.emptyView = binding.empty
        binding.numbers.adapter = adapter
    }

    override fun render(state: BlockedNumbersState) {
        adapter.updateData(state.numbers)
    }

    override fun unblockAddress(): Observable<Long> = adapter.unblockAddress
    override fun addAddress(): Observable<*> = binding.add.clicks()
    override fun saveAddress(): Observable<String> = saveAddressSubject

    override fun showAddDialog() {
        val binding = BlockedNumbersAddDialogBinding.inflate(activity?.layoutInflater!!)
        val textWatcher = BlockedNumberTextWatcher(binding.input, phoneNumberUtils)
        val dialog = AlertDialog.Builder(activity!!)
                .setView(binding.root)
                .setPositiveButton(R.string.blocked_numbers_dialog_block) { _, _ ->
                    saveAddressSubject.onNext(binding.input.text.toString())
                }
                .setNegativeButton(R.string.button_cancel) { _, _ -> }
                .setOnDismissListener { textWatcher.dispose() }
        dialog.show()
    }

}
