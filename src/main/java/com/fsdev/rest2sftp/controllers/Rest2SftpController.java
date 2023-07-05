package com.fsdev.rest2sftp.controllers;

import com.fsdev.rest2sftp.services.Rest2SftpService;
import com.fsdev.swagger.api.V1Api;
import com.fsdev.swagger.models.DocumentRequest;
import com.fsdev.swagger.models.DocumentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Rest2SftpController implements V1Api {

    Rest2SftpService rest2SftpService;

    public Rest2SftpController(Rest2SftpService rest2SftpService) {
        this.rest2SftpService = rest2SftpService;
    }

    @Override
    public ResponseEntity<DocumentResponse> downloadFIle(String directory, String fileName) {
        var documentResponse = new DocumentResponse();
        documentResponse = rest2SftpService.downloadDocument(directory,fileName);
        return new ResponseEntity<>(documentResponse,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> listarDirectory(String directory) {
        List<String> response = rest2SftpService.listDirectory(directory);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> listarServer() {
        List<String> response = rest2SftpService.listRootDirectory();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> uploadFile(DocumentRequest body){
        rest2SftpService.uploadService(body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
