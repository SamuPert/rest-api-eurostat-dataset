package com.samupert.univpm.eurostat.dataset;

import com.samupert.univpm.eurostat.common.service.FileService;
import com.samupert.univpm.eurostat.exception.DownloadFileException;
import com.samupert.univpm.eurostat.exception.GunzipFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Provides the configuration for the dataset resource.
 */
@Configuration
@Slf4j
public class DatasetConfiguration {

    /**
     * Creates a {@link RestTemplate} used to download the dataset.
     *
     * @return The {@link RestTemplate} used to download the dataset
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Creates a {@link Resource} pointing to the dataset.
     *
     * @param fileService The {@link FileService} used to download the dataset
     * @param datasetUrl The URL of the dataset.
     * @return A  {@link Resource} pointing to the dataset.
     */
    @Bean(value = "CsvDatasetResource")
    public Resource csvDatasetResource(FileService fileService, @Value("${dataset.url}") String datasetUrl) {
        log.info("Downloading dataset.");
        try {
            File zippedDataset = fileService.download(datasetUrl, "dataset", ".csv.gz.tmp");
            log.info("Compressed dataset size: {} bytes.", zippedDataset.length());
            log.info("Extracting dataset.");
            File unzippedFile = fileService.gunzipFile(zippedDataset, "dataset", ".csv.tmp");
            log.info("Extracted dataset size: {} bytes.", unzippedFile.length());
            return new FileSystemResource(unzippedFile);
        } catch (DownloadFileException | GunzipFileException | FileNotFoundException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
