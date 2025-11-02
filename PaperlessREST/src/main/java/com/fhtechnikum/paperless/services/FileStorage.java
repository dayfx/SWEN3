package com.fhtechnikum.paperless.services;

public interface FileStorage {
    /**
     * Upload a file to object storage
     * @param objectName The name/key for the object
     * @param file The file data as byte array
     */
    void upload(String objectName, byte[] file);

    /**
     * Download a file from object storage
     * @param objectName The name/key of the object
     * @return The file data as byte array
     */
    byte[] download(String objectName);

    /**
     * Delete a file from object storage
     * @param objectName The name/key of the object to delete
     */
    void delete(String objectName);
}
