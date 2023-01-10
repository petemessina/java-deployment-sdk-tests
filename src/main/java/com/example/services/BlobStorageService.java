package com.example.services;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public class BlobStorageService {
    
    private final BlobServiceClient _blobServiceClient;

    public BlobStorageService(
        String endpoint,
        String sasToken
    ) {
        _blobServiceClient = new BlobServiceClientBuilder()
        .endpoint(endpoint)
        .sasToken(sasToken)
        .buildClient();
    }

    public String getContent(
        String containerName,
        String fileName
    ) throws Exception {
        
        BlobContainerClient blobContainerClient = this._blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
        BinaryData data = blobClient.downloadContent();
        byte[] fileBytes = data.toBytes();
        
        return new String(fileBytes, "US-ASCII");
    }
}
