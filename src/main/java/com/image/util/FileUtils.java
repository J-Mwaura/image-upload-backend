package com.image.util;

import com.image.FileConstant.FileConstant;

/**
 * @author atjkm
 *
 */

public class FileUtils {
	
    public static String extractFileNameWithoutExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int lastDotIndex = filename.lastIndexOf(FileConstant.DOT);
        return lastDotIndex > 0 ? filename.substring(0, lastDotIndex) : filename;
    }

}

