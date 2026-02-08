package com.github.mael2001.providers.sms;

import com.github.mael2001.channels.SMSNotification;
import com.github.mael2001.client.GlobalConfigAware;
import com.github.mael2001.client.ProviderConfigAware;
import com.github.mael2001.client.RetryConfigAware;
import com.github.mael2001.dto.Notifier;

public interface SMSProvider
		extends GlobalConfigAware, RetryConfigAware, ProviderConfigAware, Notifier<SMSNotification> {

}
