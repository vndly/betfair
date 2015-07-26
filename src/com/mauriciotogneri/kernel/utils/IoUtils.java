package com.mauriciotogneri.kernel.utils;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class IoUtils
{
    public static synchronized String readFile(String filePath) throws IOException
    {
        byte[] data = new byte[0];

        FileInputStream fileInputStream = null;

        try
        {
            File file = new File(filePath);
            fileInputStream = new FileInputStream(new File(filePath));
            data = new byte[(int) file.length()];
            fileInputStream.read(data);
        }
        finally
        {
            closeResource(fileInputStream);
        }

        return new String(data, "UTF-8");
    }

    public static synchronized void writeFile(String filePath, String content, boolean append) throws IOException
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

    public static synchronized void writeFile(String filePath, String content) throws IOException
    {
        writeFile(filePath, content, false);
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