package com.dechub.tanishq.microsite.controller;

import com.dechub.tanishq.microsite.dto.SubmissionStatusResponse;
import com.dechub.tanishq.microsite.service.MicrositeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/microsite/qr")
@CrossOrigin(origins = "*") // Allows your frontend to call this API
public class MicrositeController {

    @Autowired
    private MicrositeService micrositeService;

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionStatusResponse> getStatus(@PathVariable String id) throws IOException {
        SubmissionStatusResponse response = micrositeService.getSubmissionStatus(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{id}/upload", consumes = "multipart/form-data")
    public ResponseEntity<SubmissionStatusResponse> uploadVideo(
            @PathVariable String id,
            @RequestParam("video") MultipartFile videoFile) throws IOException {

        // Basic validationa
        if (videoFile.isEmpty() || !videoFile.getContentType().startsWith("video/")) {
            return ResponseEntity.badRequest().build();
        }

        SubmissionStatusResponse response = micrositeService.handleVideoUpload(id, videoFile);
        return ResponseEntity.ok(response);
    }
}