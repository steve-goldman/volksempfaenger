package net.x4a42.volksempfaenger.service.feedsync;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class LogoDownloader
{
    private final byte[]       buffer = new byte[2 << 16];
    private final InputStream  inputStream;
    private final OutputStream outputStream;

    public LogoDownloader(InputStream  inputStream,
                          OutputStream outputStream)
    {
        this.inputStream  = inputStream;
        this.outputStream = outputStream;
    }

    public void download() throws IOException
    {
        // TODO: improve on this?
        int n = inputStream.read(buffer);
        while (n != -1)
        {
            outputStream.write(buffer, 0, n);
            n = inputStream.read(buffer);
        }

        inputStream.close();
        outputStream.close();
    }
}
