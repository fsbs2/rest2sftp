package com.fsdev.rest2sftp.config;

import com.jcraft.jsch.JSch;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Rest2SftpConfiguration {
    @Bean
    public JSch createJsch(){
        return new JSch();
    }
}
