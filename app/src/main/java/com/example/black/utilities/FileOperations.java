package com.example.black.utilities;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by sank on 1/23/16.
 */
public class FileOperations {
    Logger logger = Logger.getLogger(FileOperations.class.getName());
    public void deleteFile(File file) {
        file.delete();
        logger.info("File got deleted. Path : " + file.getAbsolutePath());
    }
}
