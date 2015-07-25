package com.mauriciotogneri.kernel.utils;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class IoUtils
{
    public static void writeToFile(String filePath, String content, boolean append) throws IOException
    {
        File file = new File(filePath);

        if (createFile(filePath))
        {
            BufferedWriter bufferedWriter = null;

            try
            {
                FileWriter fileWriter = new FileWriter(file, append);
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(content);
                bufferedWriter.flush();
            }
            finally
            {
                closeResource(bufferedWriter);
            }
        }
    }

    public static void writeToFile(String filePath, String content) throws IOException
    {
        writeToFile(filePath, content, false);
    }

    public static synchronized boolean createFile(String filePath) throws IOException
    {
        File file = new File(filePath);
        File folder = file.getParentFile();

        boolean folderCreated = folder.exists() || folder.mkdirs();
        boolean fileCreated = file.exists() || file.createNewFile();

        return folderCreated && fileCreated;
    }

    public static void closeResource(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {
                // ignore
            }
        }
    }
}