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
package com.innovate.replify.injection.android

import com.innovate.replify.feature.backup.BackupActivity
import com.innovate.replify.feature.blocking.BlockingActivity
import com.innovate.replify.feature.compose.ComposeActivity
import com.innovate.replify.feature.compose.ComposeActivityModule
import com.innovate.replify.feature.contacts.ContactsActivity
import com.innovate.replify.feature.contacts.ContactsActivityModule
import com.innovate.replify.feature.conversationinfo.ConversationInfoActivity
import com.innovate.replify.feature.gallery.GalleryActivity
import com.innovate.replify.feature.gallery.GalleryActivityModule
import com.innovate.replify.feature.main.MainActivity
import com.innovate.replify.feature.main.MainActivityModule
import com.innovate.replify.feature.notificationprefs.NotificationPrefsActivity
import com.innovate.replify.feature.notificationprefs.NotificationPrefsActivityModule
import com.innovate.replify.feature.plus.PlusActivity
import com.innovate.replify.feature.plus.PlusActivityModule
import com.innovate.replify.feature.qkreply.QkReplyActivity
import com.innovate.replify.feature.qkreply.QkReplyActivityModule
import com.innovate.replify.feature.scheduled.ScheduledActivity
import com.innovate.replify.feature.scheduled.ScheduledActivityModule
import com.innovate.replify.feature.settings.SettingsActivity
import com.innovate.replify.injection.scope.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [PlusActivityModule::class])
    abstract fun bindPlusActivity(): PlusActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindBackupActivity(): BackupActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ComposeActivityModule::class])
    abstract fun bindComposeActivity(): ComposeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ContactsActivityModule::class])
    abstract fun bindContactsActivity(): ContactsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindConversationInfoActivity(): ConversationInfoActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [GalleryActivityModule::class])
    abstract fun bindGalleryActivity(): GalleryActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [NotificationPrefsActivityModule::class])
    abstract fun bindNotificationPrefsActivity(): NotificationPrefsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [QkReplyActivityModule::class])
    abstract fun bindQkReplyActivity(): QkReplyActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ScheduledActivityModule::class])
    abstract fun bindScheduledActivity(): ScheduledActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindSettingsActivity(): SettingsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindBlockingActivity(): BlockingActivity

}
