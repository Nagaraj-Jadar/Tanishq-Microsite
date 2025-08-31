package com.dechub.tanishq.microsite.service;

import com.dechub.tanishq.microsite.exception.QrNotFoundException;
import com.dechub.tanishq.microsite.model.QRSubmission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GoogleSheetService {

    @Autowired
    private Sheets sheetsClient;

    @Value("${dechub.tanishq.greeting.sheet.id}")
    private String spreadsheetId;

    @Value("${google.sheets.sheet.name}")
    private String sheetName;
    public Optional<QRSubmission> findByQrId(String qrId) throws IOException {
        String range = sheetName + "!A:E";

        ValueRange response = sheetsClient.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            return Optional.empty();
        }

        // Start from 1 to skip header row
        for (int i = 1; i < values.size(); i++) {
            List<Object> row = values.get(i);

            if (row != null && !row.isEmpty() &&
                    row.get(0).toString().trim().equalsIgnoreCase(qrId.trim())) {

                // Get status or default to PENDING
                String statusString;
                if (row.size() > 1 && row.get(1) != null && !row.get(1).toString().trim().isEmpty()) {
                    statusString = row.get(1).toString().trim().toUpperCase();
                } else {
                    statusString = "PENDING";
                    // Auto-update sheet to store PENDING if missing
                    String updateRange = String.format("%s!B%d", sheetName, i + 1);
                    ValueRange body = new ValueRange().setValues(Collections.singletonList(
                            Collections.singletonList(statusString)
                    ));
                    sheetsClient.spreadsheets().values()
                            .update(spreadsheetId, updateRange, body)
                            .setValueInputOption("USER_ENTERED")
                            .execute();
                }

                return Optional.of(QRSubmission.builder()
                        .rowIndex(i + 1) // Sheet rows are 1-based
                        .qrId(row.get(0).toString().trim())
                        .status(QRSubmission.Status.valueOf(statusString))
                        .videoFileId(row.size() > 2 && row.get(2) != null ? row.get(2).toString() : null)
                        .videoPlaybackUrl(row.size() > 3 && row.get(3) != null ? row.get(3).toString() : null)
                        .build());
            }
        }
        return Optional.empty();
    }

    public void updateSubmission(QRSubmission submission) throws IOException {
        String range = String.format("%s!B%d:E%d", sheetName, submission.getRowIndex(), submission.getRowIndex());

        List<Object> valuesToUpdate = Arrays.asList(
                submission.getStatus().toString(),
                submission.getVideoFileId(),
                submission.getVideoPlaybackUrl(),
                submission.getSubmissionTimestamp().toString()
        );

        ValueRange body = new ValueRange().setValues(Collections.singletonList(valuesToUpdate));

        sheetsClient.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }
}