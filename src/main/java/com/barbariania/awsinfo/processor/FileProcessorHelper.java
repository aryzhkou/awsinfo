package com.barbariania.awsinfo.processor;

public class FileProcessorHelper
{
    public static String getExtension(String filename) {
        final int firstExtensionSymbolPosition = filename.lastIndexOf(".") + 1;
        return firstExtensionSymbolPosition > 0 ? filename.substring(firstExtensionSymbolPosition) : null;
    }
}
