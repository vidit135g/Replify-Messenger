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

import com.innovate.replify.feature.widget.WidgetProvider
import com.innovate.replify.injection.scope.ActivityScope
import com.innovate.replify.receiver.BlockThreadReceiver
import com.innovate.replify.receiver.BootReceiver
import com.innovate.replify.receiver.DefaultSmsChangedReceiver
import com.innovate.replify.receiver.DeleteMessagesReceiver
import com.innovate.replify.receiver.MarkArchivedReceiver
import com.innovate.replify.receiver.MarkReadReceiver
import com.innovate.replify.receiver.MarkSeenReceiver
import com.innovate.replify.receiver.MmsReceivedReceiver
import com.innovate.replify.receiver.MmsReceiver
import com.innovate.replify.receiver.MmsSentReceiver
import com.innovate.replify.receiver.MmsUpdatedReceiver
import com.innovate.replify.receiver.NightModeReceiver
import com.innovate.replify.receiver.RemoteMessagingReceiver
import com.innovate.replify.receiver.SendScheduledMessageReceiver
import com.innovate.replify.receiver.SmsDeliveredReceiver
import com.innovate.replify.receiver.SmsProviderChangedReceiver
import com.innovate.replify.receiver.SmsReceiver
import com.innovate.replify.receiver.SmsSentReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindBlockThreadReceiver(): BlockThreadReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindBootReceiver(): BootReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindDefaultSmsChangedReceiver(): DefaultSmsChangedReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindDeleteMessagesReceiver(): DeleteMessagesReceiver

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindMarkArchivedReceiver(): MarkArchivedReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMarkReadReceiver(): MarkReadReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMarkSeenReceiver(): MarkSeenReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMmsReceivedReceiver(): MmsReceivedReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMmsReceiver(): MmsReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMmsSentReceiver(): MmsSentReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMmsUpdatedReceiver(): MmsUpdatedReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindNightModeReceiver(): NightModeReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindRemoteMessagingReceiver(): RemoteMessagingReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSendScheduledMessageReceiver(): SendScheduledMessageReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSmsDeliveredReceiver(): SmsDeliveredReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSmsProviderChangedReceiver(): SmsProviderChangedReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSmsReceiver(): SmsReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSmsSentReceiver(): SmsSentReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindWidgetProvider(): WidgetProvider

}