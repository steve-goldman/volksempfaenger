package net.x4a42.volksempfaenger;

import android.content.Context;
import android.widget.Toast;

public class ToastMaker
{
    private final Context context;

    public ToastMaker(Context context)
    {
        this.context = context;
    }

    public void showTextShort(String text)
    {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
