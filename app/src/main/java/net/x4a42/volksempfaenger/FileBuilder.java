package net.x4a42.volksempfaenger;

import java.io.File;

public class FileBuilder
{
    private final File base;

    public FileBuilder(File base)
    {
        this.base = base;
    }

    public File build(String... children)
    {
        File file = base;
        for (String child : children)
        {
            file = new File(file, child);
        }
        return file;
    }
}
