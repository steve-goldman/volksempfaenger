package net.x4a42.volksempfaenger.misc;

import android.app.AlertDialog;
import android.content.Context;

public class AlertDialogBuilderFactory
{
    private final Context context;

    public AlertDialogBuilderFactory(Context context)
    {
        this.context = context;
    }

    public AlertDialog.Builder create()
    {
        return new AlertDialog.Builder(context);
    }
}
