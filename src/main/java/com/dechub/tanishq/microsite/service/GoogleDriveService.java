package com.dechub.tanishq.microsite.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
public class GoogleDriveService {

    @Autowired
    private Drive driveClient;

    @Value("${dechub.tanishq.greeting.drive.id}")
    private String uploadFolderId;

    public File uploadVideo(MultipartFile videoFile, String qrId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(qrId + "_" + System.currentTimeMillis() + ".mp4");
        fileMetadata.setParents(Collections.singletonList(uploadFolderId));

        InputStreamContent mediaContent = new InputStreamContent(
                videoFile.getContentType(),
                videoFile.getInputStream()
        );

        File uploadedFile = driveClient.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink, webContentLink")
                .execute();

        // Make the file publicly viewable
        Permission permission = new Permission().setType("anyone").setRole("reader");
        driveClient.permissions().create(uploadedFile.getId(), permission).execute();

        return uploadedFile;
    }
}