package com.github.mael2001.providers.sms;

import com.github.mael2001.channels.SMSNotification;
import com.github.mael2001.spi.GlobalConfigAware;
import com.github.mael2001.spi.Notifier;
import com.github.mael2001.spi.ProviderConfigAware;
import com.github.mael2001.spi.RetryConfigAware;

public interface SMSProvider
		extends GlobalConfigAware, RetryConfigAware, ProviderConfigAware, Notifier<SMSNotification> {

}
