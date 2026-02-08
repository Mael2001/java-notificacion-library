package com.github.mael2001.providers.push;

import com.github.mael2001.channels.PushNotification;
import com.github.mael2001.spi.GlobalConfigAware;
import com.github.mael2001.spi.Notifier;
import com.github.mael2001.spi.ProviderConfigAware;
import com.github.mael2001.spi.RetryConfigAware;

public interface PushProvider
		extends GlobalConfigAware, RetryConfigAware, ProviderConfigAware, Notifier<PushNotification> {

}
