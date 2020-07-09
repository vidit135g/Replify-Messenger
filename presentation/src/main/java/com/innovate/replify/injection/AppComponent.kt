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
package com.innovate.replify.injection

import com.innovate.replify.common.QKApplication
import com.innovate.replify.common.QkDialog
import com.innovate.replify.common.util.QkChooserTargetService
import com.innovate.replify.common.widget.AvatarView
import com.innovate.replify.common.widget.PagerTitleView
import com.innovate.replify.common.widget.PreferenceView
import com.innovate.replify.common.widget.QkEditText
import com.innovate.replify.common.widget.QkSwitch
import com.innovate.replify.common.widget.QkTextView
import com.innovate.replify.common.widget.RadioPreferenceView
import com.innovate.replify.feature.backup.BackupController
import com.innovate.replify.feature.blocking.BlockingController
import com.innovate.replify.feature.blocking.manager.BlockingManagerController
import com.innovate.replify.feature.blocking.messages.BlockedMessagesController
import com.innovate.replify.feature.blocking.numbers.BlockedNumbersController
import com.innovate.replify.feature.compose.editing.DetailedChipView
import com.innovate.replify.feature.conversationinfo.injection.ConversationInfoComponent
import com.innovate.replify.feature.settings.SettingsController
import com.innovate.replify.feature.settings.about.AboutController
import com.innovate.replify.feature.settings.swipe.SwipeActionsController
import com.innovate.replify.feature.themepicker.injection.ThemePickerComponent
import com.innovate.replify.feature.widget.WidgetAdapter
import com.innovate.replify.injection.android.ActivityBuilderModule
import com.innovate.replify.injection.android.BroadcastReceiverBuilderModule
import com.innovate.replify.injection.android.ServiceBuilderModule
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityBuilderModule::class,
    BroadcastReceiverBuilderModule::class,
    ServiceBuilderModule::class])
interface AppComponent {

    fun conversationInfoBuilder(): ConversationInfoComponent.Builder
    fun themePickerBuilder(): ThemePickerComponent.Builder

    fun inject(application: QKApplication)

    fun inject(controller: AboutController)
    fun inject(controller: BackupController)
    fun inject(controller: BlockedMessagesController)
    fun inject(controller: BlockedNumbersController)
    fun inject(controller: BlockingController)
    fun inject(controller: BlockingManagerController)
    fun inject(controller: SettingsController)
    fun inject(controller: SwipeActionsController)

    fun inject(dialog: QkDialog)

    fun inject(service: WidgetAdapter)

    /**
     * This can't use AndroidInjection, or else it will crash on pre-marshmallow devices
     */
    fun inject(service: QkChooserTargetService)

    fun inject(view: AvatarView)
    fun inject(view: DetailedChipView)
    fun inject(view: PagerTitleView)
    fun inject(view: PreferenceView)
    fun inject(view: RadioPreferenceView)
    fun inject(view: QkEditText)
    fun inject(view: QkSwitch)
    fun inject(view: QkTextView)

}
