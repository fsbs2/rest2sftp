package com.fsdev.rest2sftp.services;

import com.fsdev.swagger.models.DocumentRequest;
import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class Rest2SftpService {
    private final String CHANNEL_TYPE = "sftp";

    @Value("${sftp.user}")
    private String username;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.remoteHost}")
    private String remoteHost;

    JSch jSch;

    Session jschSession;

    ChannelSftp channelSftp;

    public Rest2SftpService(JSch jSch) {
        this.jSch = jSch;
    }

    public void uploadService(DocumentRequest body)  {
        try {
            this.channelSftp = connectSftpServer();
            channelSftp.connect();
            channelSftp.cd(body.getDirectory());
            InputStream inputStream = new ByteArrayInputStream(body.getDocument());
            channelSftp.put(inputStream, body.getFileName());
            disconnectSftpServer();
        } catch (JSchException e) {
            throw new RuntimeException(e);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }


    private ChannelSftp connectSftpServer() throws JSchException {
        this.jschSession = this.jSch.getSession(username,remoteHost);
        jschSession.setPassword(password);
        var config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);
        jschSession.connect();
        return (ChannelSftp) jschSession.openChannel(CHANNEL_TYPE);
    }

    private void disconnectSftpServer(){
        if(channelSftp.isConnected()){
            channelSftp.disconnect();
        }
        if(jschSession.isConnected()){
            jschSession.disconnect();
        }
    }

}
