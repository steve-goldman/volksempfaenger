ATTACH DATABASE ':memory:' AS DownloadManager;

CREATE TABLE DownloadManager.download (
	_id INTEGER PRIMARY KEY,          -- COLUMN_ID
	local_filename TEXT,              -- COLUMN_LOCAL_FILENAME
	mediaprovider_uri TEXT,           -- COLUMN_MEDIAPROVIDER_URI
	title TEXT,                       -- COLUMN_TITLE
	description TEXT,                 -- COLUMN_DESCRIPTION
	uri TEXT,                         -- COLUMN_URI
	status INTEGER,                   -- COLUMN_STATUS
	media_type TEXT,                  -- COLUMN_MEDIA_TYPE
	total_size INTEGER,               -- COLUMN_TOTAL_SIZE_BYTES
	last_modified_timestamp INTEGER,  -- COLUMN_LAST_MODIFIED_TIMESTAMP
	bytes_so_far INTEGER,             -- COLUMN_BYTES_DOWNLOADED_SO_FAR
	local_uri TEXT,                   -- COLUMN_LOCAL_URI
	reason INTEGER                    -- COLUMN_REASON
);
