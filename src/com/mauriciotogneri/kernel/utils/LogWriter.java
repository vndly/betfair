package com.mauriciotogneri.kernel.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter
{
    private final BufferedWriter bufferedWriter;

    public LogWriter(String filePath) throws IOException
    {
        bufferedWriter = getBufferedWriter(filePath);
    }

    private BufferedWriter getBufferedWriter(String filePath) throws IOException
    {
        BufferedWriter result = null;
        File file = new File(filePath);

        if (IoUtils.createFile(filePath))
        {
            FileWriter fileWriter = new FileWriter(file, true);
            result = new BufferedWriter(fileWriter);
        }

        return result;
    }

    public void write(String content) throws IOException
    {
        bufferedWriter.write(content);
        bufferedWriter.flush();
    }

    public void close()
    {
        IoUtils.closeResource(bufferedWriter);
    }
}