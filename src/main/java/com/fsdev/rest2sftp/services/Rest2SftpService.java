package com.fsdev.rest2sftp.services;

import com.fsdev.swagger.models.DocumentRequest;
import com.fsdev.swagger.models.DocumentResponse;
import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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

    public List<String> listDirectory(String directory) {
        try {
            this.channelSftp = connectSftpServer();
            channelSftp.connect();
            List<String> response = new ArrayList<>();
            Vector fileList = channelSftp.ls(directory);
            for(int i = 0; i< fileList.size();i++) {
                response.add(fileList.get(i).toString());
            }
            disconnectSftpServer();
            return response;
        } catch (JSchException | SftpException e) {
            throw new RuntimeException(e);
        }

    }
    public List<String> listServer(){
        try {
            this.channelSftp = connectSftpServer();
            channelSftp.connect();
            Vector ls = channelSftp.ls("/");
            return ls.stream().toList();
        } catch (JSchException e) {
            throw new RuntimeException(e);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }

    public DocumentResponse downloadDocument(String directory, String fileName) {
        try {
            DocumentResponse documentResponse = new DocumentResponse();
            this.channelSftp = connectSftpServer();
            channelSftp.connect();
            String view = channelSftp.pwd();
            channelSftp.cd(directory);
            documentResponse.setDocument(channelSftp.get(fileName).readAllBytes());
            disconnectSftpServer();
            return documentResponse;
        } catch (JSchException e) {
            throw new RuntimeException(e);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadService(DocumentRequest body) {
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
        this.jschSession = this.jSch.getSession(username, remoteHost);
        jschSession.setPassword(password);
        var config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);
        jschSession.connect();
        return (ChannelSftp) jschSession.openChannel(CHANNEL_TYPE);
    }

    private void disconnectSftpServer() {
        if (channelSftp.isConnected()) {
            channelSftp.disconnect();
        }
        if (jschSession.isConnected()) {
            jschSession.disconnect();
        }
    }
}
