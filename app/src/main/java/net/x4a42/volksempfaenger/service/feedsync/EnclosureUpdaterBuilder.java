package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.enclosure.EnclosureDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.enclosure.EnclosureDaoWrapper;

class EnclosureUpdaterBuilder
{
    public EnclosureUpdater build(Context context)
    {
        EnclosureDaoWrapper enclosureDao = new EnclosureDaoBuilder().build(context);
        return new EnclosureUpdater(enclosureDao);
    }
}
