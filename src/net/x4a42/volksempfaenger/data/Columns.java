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
		 * Number of episodes which are in the 'listening' state (type int)
		 */
		public static final String LISTENING_EPISODES = "listening_episodes";

		/**
		 * Time of last update (type long)
		 */
		public static final String LAST_UPDATE = "last_update";

		/**
		 * Value of the 'Expires' HTTP header of the feed if present (type long)
		 */
		public static final String HTTP_EXPIRES = "http_expires";

		/**
		 * Value of the 'Last-Modified' HTTP header of the feed if present (type
		 * long)
		 */
		public static final String HTTP_LAST_MODIFIED = "http_last_modified";

		/**
		 * HTTP entity tag of the feed if present (type String)
		 */
		public static final String HTTP_ETAG = "http_etag";

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
		 * The pathname of the file where the download is stored (type String)
		 */
		public static final String DOWNLOAD_LOCAL_FILENAME = "download_local_filename";

		/**
		 * The URI to the corresponding entry in MediaProvider for this
		 * downloaded entry (type String)
		 */
		public static final String DOWNLOAD_MEDIAPROVIDER_URI = "download_mediaprovider_uri";

		/**
		 * Title of this download (type String)
		 */
		public static final String DOWNLOAD_TITLE = "download_title";

		/**
		 * Description of this download (type String)
		 */
		public static final String DOWNLOAD_DESCRIPTION = "download_description";

		/**
		 * URI to be downloaded (type String)
		 */
		public static final String DOWNLOAD_URI = "download_uri";

		/**
		 * Current status of the download, as one of the
		 * android.app.DownloadManager.STATUS_* constants (type int)
		 */
		public static final String DOWNLOAD_STATUS = "download_status";

		/**
		 * Internet Media Type of the downloaded file (type String)
		 */
		public static final String DOWNLOAD_MEDIA_TYPE = "download_media_type";

		/**
		 * Total size of the download in bytes (type long)
		 */
		public static final String DOWNLOAD_TOTAL_SIZE_BYTES = "download_total_size";

		/**
		 * The URI to the corresponding entry in MediaProvider for this
		 * downloaded entry (type long)
		 */
		public static final String DOWNLOAD_LAST_MODIFIED_TIMESTAMP = "download_last_modified_timestamp";

		/**
		 * Number of bytes download so far (type long)
		 */
		public static final String DOWNLOAD_BYTES_DOWNLOADED_SO_FAR = "download_bytes_so_far";

		/**
		 * Uri where downloaded file will be stored (type String)
		 */
		public static final String DOWNLOAD_LOCAL_URI = "download_local_uri";

		/**
		 * Provides more detail on the status of the download (type int)
		 */
		public static final String DOWNLOAD_REASON = "download_reason";

		/**
		 * Total duration of the episode in seconds (type int)
		 */
		public static final String DURATION_TOTAL = "duration_total";

		/**
		 * Current position in the podcast in seconds (type int)
		 */
		public static final String DURATION_LISTENED = "duration_listened";

		/**
		 * Hash to increase the update speed (type String; SHA1 hex)
		 */
		public static final String HASH = "hash";

		/**
		 * Autosubmit flattr url (type String)
		 */
		public static final String FLATTR_URL = "flattr_url";

		/**
		 * Status of the flattr thing in connection with the user (type int)
		 */
		public static final String FLATTR_STATUS = "flattr_status";

		/**
		 * Number of enclosures (type int)
		 */
		public static final String ENCLOSURE_NUMBER = "enclosure_number";

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
