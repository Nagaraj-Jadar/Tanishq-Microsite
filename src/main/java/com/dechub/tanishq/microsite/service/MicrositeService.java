    package com.dechub.tanishq.microsite.service;

    import com.dechub.tanishq.microsite.dto.SubmissionStatusResponse;
    import com.dechub.tanishq.microsite.exception.QrAlreadySubmittedException;
    import com.dechub.tanishq.microsite.exception.QrNotFoundException;
    import com.dechub.tanishq.microsite.model.QRSubmission;
    import com.google.api.services.drive.model.File;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.time.Instant;

    @Service
    public class MicrositeService {

        @Autowired
        private GoogleSheetService sheetService;
        @Autowired
        private GoogleDriveService driveService;

        public SubmissionStatusResponse getSubmissionStatus(String qrId) throws IOException {
            QRSubmission submission = sheetService.findByQrId(qrId)
                    .orElseThrow(() -> new QrNotFoundException("QR ID not found: " + qrId));

            return new SubmissionStatusResponse(
                    submission.getQrId(),
                    submission.getStatus(),
                    submission.getVideoPlaybackUrl()
            );
        }

        public SubmissionStatusResponse handleVideoUpload(String qrId, MultipartFile videoFile) throws IOException {
            // 1. Check status first to ensure immutability
            QRSubmission existingSubmission = sheetService.findByQrId(qrId)
                    .orElseThrow(() -> new QrNotFoundException("QR ID not found: " + qrId));

            if (existingSubmission.getStatus() == QRSubmission.Status.SUBMITTED) {
                throw new QrAlreadySubmittedException("This QR ID has already been used to submit a video.");
            }

            // 2. Upload video to Google Drive
            File uploadedFile = driveService.uploadVideo(videoFile, qrId);

            // 3. Update the submission details
            existingSubmission.setStatus(QRSubmission.Status.SUBMITTED);
            existingSubmission.setVideoFileId(uploadedFile.getId());
            existingSubmission.setVideoPlaybackUrl(uploadedFile.getWebViewLink()); // Use webViewLink for direct playback in browser
            existingSubmission.setSubmissionTimestamp(Instant.now());

            // 4. Save the updated record to Google Sheets
            sheetService.updateSubmission(existingSubmission);

            // 5. Return the new status to the frontend
            return new SubmissionStatusResponse(
                    existingSubmission.getQrId(),
                    existingSubmission.getStatus(),
                    existingSubmission.getVideoPlaybackUrl()
            );
        }
    }