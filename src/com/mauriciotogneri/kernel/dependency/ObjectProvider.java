package com.mauriciotogneri.kernel.dependency;

import com.mauriciotogneri.kernel.logs.LogWriter;

import java.io.IOException;

public interface ObjectProvider
{
    LogWriter getErrorLog() throws IOException;
}
