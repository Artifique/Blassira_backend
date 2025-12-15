package com.example.Blasira_Backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Interface defining the contract for a file storage service.
 * This abstraction allows for different implementations (e.g., local, cloud).
 */
public interface StorageService {

    /**
     * Initializes the storage.
     */
    void init();

    /**
     * Stores a file.
     *
     * @param file The file to store.
     * @return The unique filename of the stored file.
     */
    String store(MultipartFile file);

    /**
     * Loads all stored files.
     *
     * @return A Stream of Paths.
     */
    Stream<Path> loadAll();

    /**
     * Loads a file by its filename.
     *
     * @param filename The name of the file.
     * @return The Path to the file.
     */
    Path load(String filename);

    /**
     * Loads a file as a Spring Resource.
     *
     * @param filename The name of the file.
     * @return The file as a Resource.
     */
    Resource loadAsResource(String filename);

    /**
     * Deletes all stored files.
     */
    void deleteAll();

}
