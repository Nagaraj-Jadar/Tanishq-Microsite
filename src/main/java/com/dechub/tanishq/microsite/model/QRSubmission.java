package com.dechub.tanishq.microsite.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRSubmission {
    private String qrId;
    private Status status;
    private String videoFileId; // Google Drive File ID
    private String videoPlaybackUrl;
    private Instant submissionTimestamp;
    private int rowIndex; // To know which row to update in the sheet

    public enum Status {
        PENDING,
        SUBMITTED
    }
}