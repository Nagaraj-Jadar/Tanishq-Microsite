package com.dechub.tanishq.microsite.dto;

import com.dechub.tanishq.microsite.model.QRSubmission;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubmissionStatusResponse {
    private String qrId;
    private QRSubmission.Status status;
    private String videoPlaybackUrl;
}