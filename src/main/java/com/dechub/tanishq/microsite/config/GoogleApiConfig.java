package com.dechub.tanishq.microsite.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;

@Configuration
public class GoogleApiConfig {

    private static final String APPLICATION_NAME = "Tanishq Microsite";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${dechub.tanishq.key.filepath}")
    private Resource p12KeyFile;

    @Value("${dechub.tanishq.google.service.account}")
    private String serviceAccountEmail;

    @Bean
    public GoogleCredential googleCredential() throws GeneralSecurityException, IOException {
        // Load the private key from the .p12 file
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        try (InputStream keyFileStream = p12KeyFile.getInputStream()) {
            keystore.load(keyFileStream, "notasecret".toCharArray());
        }
        PrivateKey privateKey = (PrivateKey) keystore.getKey("privatekey", "notasecret".toCharArray());

        List<String> scopes = Arrays.asList(DriveScopes.DRIVE, SheetsScopes.SPREADSHEETS);

        // Build the credential using the private key and service account email
        return new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(serviceAccountEmail)
                .setServiceAccountScopes(scopes)
                .setServiceAccountPrivateKey(privateKey)
                .build();
    }

    @Bean
    public Drive googleDriveClient(GoogleCredential credential) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Bean
    public Sheets googleSheetsClient(GoogleCredential credential) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}