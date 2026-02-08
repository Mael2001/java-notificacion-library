package com.github.mael2001.providers.email;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.client.GlobalConfigAware;
import com.github.mael2001.client.ProviderConfigAware;
import com.github.mael2001.client.RetryConfigAware;
import com.github.mael2001.dto.Notifier;

public interface EmailProvider
		extends GlobalConfigAware, RetryConfigAware, ProviderConfigAware, Notifier<EmailNotification> {

}
