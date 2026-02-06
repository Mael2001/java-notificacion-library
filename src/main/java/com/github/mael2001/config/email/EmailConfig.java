package com.github.mael2001.config.email;

import lombok.Getter;
import lombok.Setter;

public class EmailConfig {
	@Getter @Setter private String smtpServer;
	@Getter @Setter private int port;
	@Getter @Setter private String username;
	@Getter @Setter private String password;
	@Getter @Setter private boolean useSSL;
	@Getter @Setter private boolean useTLS;
	@Getter @Setter private String fromAddress;
}
