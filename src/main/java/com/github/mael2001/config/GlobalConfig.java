package com.github.mael2001.config;

import lombok.Getter;
import lombok.Setter;

public class GlobalConfig {
	@Getter @Setter int connectionTimeout;
	@Getter @Setter int readTimeout;
	@Getter @Setter boolean enableAsync;
}
