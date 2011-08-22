package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import android.os.Bundle;
import android.webkit.WebView;

public class ViewEpisodeActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.view_episode);

		WebView webView = (WebView) findViewById(R.id.webView1);

		String description = "<div xmlns=\"http://www.w3.org/1999/xhtml\"><p><a href=\"http://tim.geekheim.de/wp-content/uploads/2011/05/titanhand.jpg\"><img class=\"alignright size-medium wp-image-3579\" title=\"titanhand\" src=\"http://tim.geekheim.de/wp-content/uploads/2011/05/titanhand-224x300.jpg\" alt=\"\" width=\"224\" height=\"300\" /></a>Nicht ganz p&uuml;nktlich, daf&uuml;r aber wie immer live und das am Nachmittag eines Feiertages &#8211; das brachte uns dann glatt mal &uuml;ber 700 Liveh&ouml;rer ein. Sind wir Radio, oder was?</p><p>Ansonsten folgte die Sendung dem ausgetretenen Pfad. Holgi l&auml;sst sich nicht belehren und Tim ist anderer Meinung. Gut, dass wir wieder so viele Pakete bekommen haben, so dass wir uns voll in Auspackzeremonien verlieren konnten. Au&szlig;erdem verleihen wir erstmals den NSFW Award f&uuml;r die &uuml;berfl&uuml;ssigste Erw&auml;hnung von Adolf Hitler in der &ouml;ffentlichkeit (oder so).</p><p><strong>Themen</strong>: <a href=\"http://www.youtube.com/watch?v=xJWPli8S-hY&amp;feature=related&amp;hd=1\" target=\"_blank\">Esoterik</a>; <a href=\"http://www.youtube.com/watch?v=wdUl8pVWXsU&amp;amp;feature=player_embedded#at=58 \" target=\"_blank\">Vatertag</a>; Holgi podcastet; Behringer <a href=\"http://www.thomann.de/index.html?partner_id=93439&amp;page=de/behringer_xenyx_x1204_usb.htm\" target=\"_blank\">Xenyx X1204USB</a>* und <a href=\"http://www.thomann.de/index.html?partner_id=93439&amp;page=de/behringer_xenyx_1204_usb.htm\" target=\"_blank\">Xenyx 1204USB</a>*; <a href=\"http://www.thomann.de/index.html?partner_id=93439&amp;page=de/zoom_r24.htm\" target=\"_blank\">Zoom R24</a>*; Selbstklebender Klettverschluss von Velcro von der Rolle <a href=\"http://www.thomann.de/index.html?partner_id=93439&amp;page=de/velcro_klettverschluss_hakenband.htm\" target=\"_blank\">Hakenband</a>* und <a href=\"http://www.thomann.de/index.html?partner_id=93439&amp;page=de/velcro_klettverschluss_oesenband.htm\" target=\"_blank\">&ouml;senband</a>*; <a href=\"http://wrint.de\" target=\"_blank\">WRINT</a>; Geldspielautomaten; Holgi Schwarzenegger ist: der <a href=\"http://de.wikipedia.org/wiki/Totalisator\" target=\"_blank\">Totalisator</a>; Delphi-B&ouml;rsen; Poker; The Breakfast Club (<a href=\"http://www.amazon.de/gp/product/B000AMCFME/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B000AMCFME\" target=\"_blank\">DVD</a>*, <a href=\"http://www.amazon.de/gp/product/B004FHEKKK/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B004FHEKKK\" target=\"_blank\">Blu-ray</a>*); Schachclub; Squash; Schokolade aus der Schweiz; <a href=\"http://en.wikipedia.org/wiki/Up_to_eleven\" target=\"_blank\">Up to eleven</a>; B&uuml;cher von Daniel Suarez: <a href=\"http://www.amazon.de/gp/product/0451231899/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=0451231899\" target=\"_blank\">Freedom</a>*, <a href=\"http://www.amazon.de/gp/product/0451228731/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=0451228731\" target=\"_blank\">Daemon</a>*;<a href=\"http://www.amazon.de/gp/product/B00274S6KS/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B00274S6KS\">Let&#8217;s Make Money</a>*; <a href=\"http://www.amazon.de/gp/product/B003N5VTZO/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B003N5VTZO\">Xaver und sein ausserirdischer Freund</a>*; Home (<a href=\"http://www.amazon.de/gp/product/B0024NKZE2/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B0024NKZE2\">DVD</a>*, <a href=\"http://www.amazon.de/gp/product/B0024NKZEW/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B0024NKZEW\">Blu-ray</a>*); Tobias O. Mei&szlig;ner: Hiobs Spiel (<a href=\"http://www.amazon.de/gp/product/3821806915/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=3821806915\">Frauenm&ouml;rder: Erstes Buch</a>*, <a href=\"http://www.amazon.de/gp/product/3821857897/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=3821857897\">Traumt&auml;nzer: Zweites Buch</a>*); Games Odyssey (4 Teile: <a href=\"http://www.youtube.com/watch?v=4_cBBoZW3JI\" target=\"_blank\">Ins Universum der digitalen Spiele</a>, <a href=\"http://www.youtube.com/watch?v=urGp6dl3OU4&amp;feature=related\" target=\"_blank\">Simulierte Welten</a>, <a href=\"http://www.youtube.com/watch?v=T6tp6zkgiRQ&amp;feature=related\" target=\"_blank\">K&uuml;nstliche Abenteuer</a>, <a href=\"http://www.youtube.com/watch?v=H2Jib5VVG5U&amp;feature=related\" target=\"_blank\">Kunstwerk Computerspiel</a>); Markgr&auml;fler Gutedel; Faltfahrr&auml;der: <a href=\"http://brompton.de/\" target=\"_blank\">Brompton</a>, <a href=\"http://www.strida.com/\" target=\"_blank\">Strida</a>; <a href=\"http://www.amazon.de/gp/product/B00461FLBW/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B00461FLBW\" target=\"_blank\">Swiffer Dusters</a>*; Titan im Handgelenk; <a href=\"http://en.wikipedia.org/wiki/Operation_Mindfuck\" target=\"_blank\">Operation Mindfuck</a>; <a href=\"http://www.haha.at/sonstigetexte/sonstigetexte/Wie_man_selbst_gesund_bleibt_und_dabei_andere_in_den_Wahnsinn_treibt/\" target=\"_blank\">WiE mAn SElBsT GeSuNd BlEiBt UnD dAbEi AnDeRe LeUtE In DeN WaHnSiNn TrEiBt</a>; KZH bis DZE; Marianne; Kurzfilm: Glaub ich kaum; Space Tours; <a href=\"http://en.wikipedia.org/wiki/Gayniggers_From_Outer_Space\" target=\"_blank\">Gay Niggers from Outer Space</a>; <a href=\"http://de.wikipedia.org/wiki/Casino_Royale_(1967)\" target=\"_blank\">Casino Royale</a> mit Peter Sellers (<a href=\"http://www.amazon.de/gp/product/B00016BY5S/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B00016BY5S\" target=\"_blank\">DVD</a>*); <a href=\"http://www.youtube.com/watch?v=LayW8aq4GLw\" target=\"_blank\">Lars von Trier Bewerbungsrede f&uuml;r den NSFW Award&#8230;Liest aus dem Tagebuch Eines Massenm&ouml;rders</a>; <a href=\"http://de.wikipedia.org/wiki/Serdar_Somuncu\" target=\"_blank\">Serdar Somuncu</a> und die Lobby-Echauffage <a href=\"http://www.amazon.de/gp/product/386604156X/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=386604156X\" target=\"_blank\">&#8230;Liest aus dem Tagebuch Eines Massenm&ouml;rders</a>*; <a href=\"http://de.wikipedia.org/wiki/Dogma_95\" target=\"_blank\">Dogma 95</a>; Thomas Vinterberg: <a href=\"http://de.wikipedia.org/wiki/Das_Fest_(Film)\" target=\"_blank\">Das Fest</a> (<a href=\"http://www.amazon.de/gp/product/B00004RYLA/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B00004RYLA\" target=\"_blank\">DVD</a>*); Lars von Trier: <a href=\"http://de.wikipedia.org/wiki/Idioten\" target=\"_blank\">Idioten</a> (<a href=\"http://www.amazon.de/gp/product/B000V2SGWQ/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B000V2SGWQ\" target=\"_blank\">DVD</a>*); Jean-Jacques Beneix: <a href=\"http://de.wikipedia.org/wiki/Diva_(Film)\" target=\"_blank\">Diva</a> (<a href=\"http://www.amazon.de/gp/product/B000V2SGUS/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B000V2SGUS\" target=\"_blank\">DVD</a>*); <a href=\"http://www.youtube.com/watch?v=2hsmoo97CVA\" target=\"_blank\">La Wally: &#8220;Ebben? Ne andrò lontana&#8221;</a>; <a href=\"http://de.wikipedia.org/wiki/Hans-Joachim_Flebbe\" target=\"_blank\">Hans-Joachim Flebbe</a>; Bondage; <a href=\"http://bibeltext.com/john/19-27.htm\" target=\"_blank\">Johannes 19,27</a>; <a href=\"http://bibeltext.com/matthew/5-37.htm\" target=\"_blank\">Matth&auml;us 5,37</a>; <a href=\"http://www.ubu.com/sound/index.html\" target=\"_blank\">UbuWeb Sound</a>; Wes Harrison: <a href=\"http://blogfiles.wfmu.org/DP/2003/09/365-Days-Project-09-29-harrison-wes-fun-with-sound.mp3\" target=\"_blank\">Fun With Sound</a>; Joseph Beuys: <a href=\"http://ubumexico.centro.org.mx/sound/beuys_joseph/Beuys-Joseph_Ja-Ja-Ja.mp3\" target=\"_blank\">JA JA JA JA, NE NE NE NE</a>; Mesh Bags (<a href=\"http://www.amazon.de/gp/product/B000WV6RCC/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B000WV6RCC\" target=\"_blank\">A4</a>*, <a href=\"http://www.amazon.de/gp/product/B000WL44NG/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B000WL44NG\" target=\"_blank\">A5</a>*, <a href=\"http://www.amazon.de/gp/product/B001C6VGQ8/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B001C6VGQ8\" target=\"_blank\">A6</a>*); Lasermessger&auml;te; <a href=\"http://www.amazon.de/gp/product/B0023LWAW0/ref=as_li_ss_tl?ie=UTF8&amp;tag=nsfw-21&amp;linkCode=as2&amp;camp=1638&amp;creative=19454&amp;creativeASIN=B0023LWAW0\" target=\"_blank\">Leica Disto D2</a>*; <a href=\"http://www.youtube.com/watch?v=jG7IGiBJU4c\" target=\"_blank\">Goa Goa</a>.</p><p>* = Affiliate Links</p><p><strong>Direktlinks</strong>: MP3-Version: <a href=\"http://chaosradio.ccc.de/media/tim/nsfw/nsfw028-in-uebereinstimmung-mit-der-prophezeiung.mp3\" target=\"_blank\">Mediendatei</a>, <a href=\"http://chaosradio.ccc.de/media/tim/nsfw/nsfw028-in-uebereinstimmung-mit-der-prophezeiung.mp3.torrent\" target=\"_blank\">Torrent</a></p><p><strong>Direktflattr</strong>: <a href=\"https://flattr.com/thing/187488/timpritlove-on-Flattr\">Tim</a>, <a href=\"https://flattr.com/thing/187787/holgi-on-Flattr\">Holgi</a></p><p class=\"wp-flattr-button\"></p> <p><a href=\"http://tim.geekheim.de/?flattrss_redirect&amp;id=3575&amp;md5=7f63a4b0717b05552d0983663746d200\" title=\"Flattr\" target=\"_blank\"><img src=\"http://tim.geekheim.de/wp-content/plugins/flattr/img/flattr-badge-large.png\" alt=\"flattr this!\"/></a></p></div>";

		webView.loadData(description, "text/html", "utf-8");
	}

}
