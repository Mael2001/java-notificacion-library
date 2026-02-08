package com.github.mael2001.providers.push;

import com.github.mael2001.channels.PushNotification;
import com.github.mael2001.client.GlobalConfigAware;
import com.github.mael2001.client.ProviderConfigAware;
import com.github.mael2001.client.RetryConfigAware;
import com.github.mael2001.dto.Notifier;

public interface PushProvider
		extends GlobalConfigAware, RetryConfigAware, ProviderConfigAware, Notifier<PushNotification> {

}
