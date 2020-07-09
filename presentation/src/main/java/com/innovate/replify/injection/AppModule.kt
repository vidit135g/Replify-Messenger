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

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModelProvider
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.innovate.replify.blocking.BlockingClient
import com.innovate.replify.blocking.BlockingManager
import com.innovate.replify.common.ViewModelFactory
import com.innovate.replify.common.util.NotificationManagerImpl
import com.innovate.replify.common.util.ShortcutManagerImpl
import com.innovate.replify.feature.conversationinfo.injection.ConversationInfoComponent
import com.innovate.replify.feature.themepicker.injection.ThemePickerComponent
import com.innovate.replify.listener.ContactAddedListener
import com.innovate.replify.listener.ContactAddedListenerImpl
import com.innovate.replify.manager.ActiveConversationManager
import com.innovate.replify.manager.ActiveConversationManagerImpl
import com.innovate.replify.manager.AlarmManager
import com.innovate.replify.manager.AlarmManagerImpl
import com.innovate.replify.manager.AnalyticsManager
import com.innovate.replify.manager.AnalyticsManagerImpl
import com.innovate.replify.manager.ChangelogManager
import com.innovate.replify.manager.ChangelogManagerImpl
import com.innovate.replify.manager.KeyManager
import com.innovate.replify.manager.KeyManagerImpl
import com.innovate.replify.manager.NotificationManager
import com.innovate.replify.manager.PermissionManager
import com.innovate.replify.manager.PermissionManagerImpl
import com.innovate.replify.manager.RatingManager
import com.innovate.replify.manager.ReferralManager
import com.innovate.replify.manager.ReferralManagerImpl
import com.innovate.replify.manager.ShortcutManager
import com.innovate.replify.manager.WidgetManager
import com.innovate.replify.manager.WidgetManagerImpl
import com.innovate.replify.mapper.CursorToContact
import com.innovate.replify.mapper.CursorToContactGroup
import com.innovate.replify.mapper.CursorToContactGroupImpl
import com.innovate.replify.mapper.CursorToContactGroupMember
import com.innovate.replify.mapper.CursorToContactGroupMemberImpl
import com.innovate.replify.mapper.CursorToContactImpl
import com.innovate.replify.mapper.CursorToConversation
import com.innovate.replify.mapper.CursorToConversationImpl
import com.innovate.replify.mapper.CursorToMessage
import com.innovate.replify.mapper.CursorToMessageImpl
import com.innovate.replify.mapper.CursorToPart
import com.innovate.replify.mapper.CursorToPartImpl
import com.innovate.replify.mapper.CursorToRecipient
import com.innovate.replify.mapper.CursorToRecipientImpl
import com.innovate.replify.mapper.RatingManagerImpl
import com.innovate.replify.repository.BackupRepository
import com.innovate.replify.repository.BackupRepositoryImpl
import com.innovate.replify.repository.BlockingRepository
import com.innovate.replify.repository.BlockingRepositoryImpl
import com.innovate.replify.repository.ContactRepository
import com.innovate.replify.repository.ContactRepositoryImpl
import com.innovate.replify.repository.ConversationRepository
import com.innovate.replify.repository.ConversationRepositoryImpl
import com.innovate.replify.repository.MessageRepository
import com.innovate.replify.repository.MessageRepositoryImpl
import com.innovate.replify.repository.ScheduledMessageRepository
import com.innovate.replify.repository.ScheduledMessageRepositoryImpl
import com.innovate.replify.repository.SyncRepository
import com.innovate.replify.repository.SyncRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(subcomponents = [
    ConversationInfoComponent::class,
    ThemePickerComponent::class])
class AppModule(private var application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context = application

    @Provides
    fun provideContentResolver(context: Context): ContentResolver = context.contentResolver

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideRxPreferences(preferences: SharedPreferences): RxSharedPreferences {
        return RxSharedPreferences.create(preferences)
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    @Provides
    fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory = factory

    // Listener

    @Provides
    fun provideContactAddedListener(listener: ContactAddedListenerImpl): ContactAddedListener = listener

    // Manager

    @Provides
    fun provideActiveConversationManager(manager: ActiveConversationManagerImpl): ActiveConversationManager = manager

    @Provides
    fun provideAlarmManager(manager: AlarmManagerImpl): AlarmManager = manager

    @Provides
    fun provideAnalyticsManager(manager: AnalyticsManagerImpl): AnalyticsManager = manager

    @Provides
    fun blockingClient(manager: BlockingManager): BlockingClient = manager

    @Provides
    fun changelogManager(manager: ChangelogManagerImpl): ChangelogManager = manager

    @Provides
    fun provideKeyManager(manager: KeyManagerImpl): KeyManager = manager

    @Provides
    fun provideNotificationsManager(manager: NotificationManagerImpl): NotificationManager = manager

    @Provides
    fun providePermissionsManager(manager: PermissionManagerImpl): PermissionManager = manager

    @Provides
    fun provideRatingManager(manager: RatingManagerImpl): RatingManager = manager

    @Provides
    fun provideShortcutManager(manager: ShortcutManagerImpl): ShortcutManager = manager

    @Provides
    fun provideReferralManager(manager: ReferralManagerImpl): ReferralManager = manager

    @Provides
    fun provideWidgetManager(manager: WidgetManagerImpl): WidgetManager = manager

    // Mapper

    @Provides
    fun provideCursorToContact(mapper: CursorToContactImpl): CursorToContact = mapper

    @Provides
    fun provideCursorToContactGroup(mapper: CursorToContactGroupImpl): CursorToContactGroup = mapper

    @Provides
    fun provideCursorToContactGroupMember(mapper: CursorToContactGroupMemberImpl): CursorToContactGroupMember = mapper

    @Provides
    fun provideCursorToConversation(mapper: CursorToConversationImpl): CursorToConversation = mapper

    @Provides
    fun provideCursorToMessage(mapper: CursorToMessageImpl): CursorToMessage = mapper

    @Provides
    fun provideCursorToPart(mapper: CursorToPartImpl): CursorToPart = mapper

    @Provides
    fun provideCursorToRecipient(mapper: CursorToRecipientImpl): CursorToRecipient = mapper

    // Repository

    @Provides
    fun provideBackupRepository(repository: BackupRepositoryImpl): BackupRepository = repository

    @Provides
    fun provideBlockingRepository(repository: BlockingRepositoryImpl): BlockingRepository = repository

    @Provides
    fun provideContactRepository(repository: ContactRepositoryImpl): ContactRepository = repository

    @Provides
    fun provideConversationRepository(repository: ConversationRepositoryImpl): ConversationRepository = repository

    @Provides
    fun provideMessageRepository(repository: MessageRepositoryImpl): MessageRepository = repository

    @Provides
    fun provideScheduledMessagesRepository(repository: ScheduledMessageRepositoryImpl): ScheduledMessageRepository = repository

    @Provides
    fun provideSyncRepository(repository: SyncRepositoryImpl): SyncRepository = repository

}