package com.cnaude.purpleirc.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileHelpers {

    public static String loadFirstLineFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        
        if (!file.exists() || !file.canRead()) {
            throw new IOException("File does not exist or is not readable.");
        }

        String firstLine;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            firstLine = bufferedReader.readLine();
        }
        return firstLine;
    }
}
