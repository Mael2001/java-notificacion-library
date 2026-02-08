package com.github.mael2001.providers.email;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.spi.GlobalConfigAware;
import com.github.mael2001.spi.Notifier;
import com.github.mael2001.spi.ProviderConfigAware;
import com.github.mael2001.spi.RetryConfigAware;

public interface EmailProvider
		extends GlobalConfigAware, RetryConfigAware, ProviderConfigAware, Notifier<EmailNotification> {

}
