package net.x4a42.volksempfaenger.data;

import android.provider.BaseColumns;

public class Columns {

	/**
	 * Class with constants representing the columns in
	 * content://net.x4a42.volksempfaenger/podcast
	 */
	public static class Podcast {

		/**
		 * Unique ID (type long)
		 */
		public static final String _ID = BaseColumns._ID;

		/**
		 * Podcast name (type String)
		 */
		public static final String TITLE = "title";

		/**
		 * Podcast description (type String)
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * Feed URL (type String)
		 */
		public static final String FEED = "feed";

		/**
		 * Website URL (type String)
		 */
		public static final String WEBSITE = "website";

		/**
		 * Number of new episodes (type int)
		 */
		public static final String NEW_EPISODES = "new_episodes";

		/**
		 * Time of last update (type long)
		 */
		public static final String LAST_UPDATE = "last_update";

	}

	/**
	 * Class with constants representing the columns in
	 * content://net.x4a42.volksempfaenger/episode
	 */
	public static class Episode {

		/**
		 * Unique ID (type int)
		 */
		public static final String _ID = BaseColumns._ID;

		/**
		 * Epsiode title (type String)
		 */
		public static final String TITLE = "title";

		/**
		 * Episode release date (type long)
		 */
		public static final String DATE = "date";

		/**
		 * Episode URL (this is not the enclosure URL; type String)
		 */
		public static final String URL = "url";

		/**
		 * Episode description/shownotes (type String)
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * Item ID in the feed (type String)
		 */
		public static final String FEED_ITEM_ID = "feed_item_id";

		/**
		 * Episode Status (type int)
		 * 
		 * TODO: document values
		 */
		public static final String STATUS = "status";

		/**
		 * ID of the podcast this episode belongs to (type long)
		 */
		public static final String PODCAST_ID = "podcast_id";

		/**
		 * Title of the podcast (type String)
		 */
		public static final String PODCAST_TITLE = "podcast_title";

		/**
		 * Description of the podcast (type String)
		 */
		public static final String PODCAST_DESCRIPTION = "podcast_description";

		/**
		 * URL of the podcast feed (type String)
		 */
		public static final String PODCAST_FEED = "podcast_feed";

		/**
		 * URL of the podcast website (type String)
		 */
		public static final String PODCAST_WEBSITE = "podcast_website";

		/**
		 * ID of the selected enclosure (type long)
		 */
		public static final String ENCLOSURE_ID = "enclosure_id";

		/**
		 * Title of the selected enclosure (type String)
		 */
		public static final String ENCLOSURE_TITLE = "enclosure_title";

		/**
		 * URL of the selected enclosure (type String)
		 */
		public static final String ENCLOSURE_URL = "enclosure_url";

		/**
		 * MIME type of the selected enclosure (type String)
		 */
		public static final String ENCLOSURE_MIME = "enclosure_mime";

		/**
		 * Size of the enclosure in bytes as it says in the feed (type long)
		 */
		public static final String ENCLOSURE_SIZE = "enclosure_size";

		/**
		 * ID of the download in DownloadManager (type long)
		 */
		public static final String DOWNLOAD_ID = "download_id";

		/**
		 * Local path of the downloaded file (type String)
		 */
		public static final String DOWNLOAD_FILE = "download_file";

		/**
		 * URI of the downloaded file (type String)
		 */
		public static final String DOWNLOAD_URI = "download_uri";

		/**
		 * Status of the download (type int)
		 * 
		 * Values: see android.app.DownloadManager.STATUS_*
		 */
		public static final String DOWNLOAD_STATUS = "download_status";

		/**
		 * Total size of the download in bytes as reported by DownloadManager
		 * (type long)
		 */
		public static final String DOWNLOAD_TOTAL = "download_total";

		/**
		 * Number of bytes already downloaded (type long)
		 */
		public static final String DOWNLOAD_DONE = "download_done";

		/**
		 * Total duration of the episode in seconds (type int)
		 */
		public static final String DURATION_TOTAL = "duration_total";

		/**
		 * Current position in the podcast in seconds (type int)
		 */
		public static final String DURATION_LISTENED = "duration_listened";
	}

	/**
	 * Class with constants representing the columns in
	 * content://net.x4a42.volksempfaenger/enclosure
	 */
	public static class Enclosure {

		/**
		 * Unique ID (type long)
		 */
		public static final String _ID = BaseColumns._ID;

		/**
		 * ID of the episode this enclosure belongs to (type long)
		 */
		public static final String EPISODE_ID = "episode_id";

		/**
		 * Title of the enclosure (as it says in the feed; type String)
		 */
		public static final String TITLE = "title";

		/**
		 * URL of the enclosure (as it says in the feed; type String)
		 */
		public static final String URL = "url";

		/**
		 * MIME type of the enclosure (as it says in the feed; type String)
		 */
		public static final String MIME = "mime";

		/**
		 * Title of the enclosure (as it says in the feed; type long)
		 */
		public static final String SIZE = "size";

	}

}
