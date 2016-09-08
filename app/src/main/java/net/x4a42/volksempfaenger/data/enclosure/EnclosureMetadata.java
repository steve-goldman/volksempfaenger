package net.x4a42.volksempfaenger.data.enclosure;

import android.net.Uri;

public class EnclosureMetadata
{
    private final String Authority     = "net.x4a42.volksempfaenger";
    private final Uri    EnclosureUri  = Uri.parse("content://" + Authority + "/enclosure");

    public Uri getEnclosureUri()
    {
        return EnclosureUri;
    }
}
