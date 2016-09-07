package net.x4a42.volksempfaenger;

import android.text.Html;
import android.text.Spanned;

import org.jsoup.Jsoup;

public class HtmlConverter
{
    public Spanned toSpanned(String htmlStr)
    {
        return Html.fromHtml(htmlStr);
    }

    public String toText(String htmlStr)
    {
        return Jsoup.parse(htmlStr).text();
    }
}
