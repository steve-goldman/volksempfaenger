CREATE TABLE "podcast" (
	"_id" INTEGER PRIMARY KEY AUTOINCREMENT,
	"feed" TEXT UNIQUE,
	"title" TEXT,
	"description" TEXT,
	"website" TEXT,
	"last_update" INTEGER
);

CREATE TABLE "episode" (
	"_id" INTEGER PRIMARY KEY AUTOINCREMENT,
	"podcast_id" INTEGER REFERENCES "podcast" ("_id") ON DELETE CASCADE,
	"title" TEXT,
	"date" INTEGER,
	"url" TEXT,
	"description" TEXT,
	"feed_item_id" TEXT,
	"status" INTEGER DEFAULT 0,
	"duration_total" INTEGER,
	"duration_listened" INTEGER,
	"enclosure_id" INTEGER REFERENCES "enclosure" ("_id"),
	"download_id" INTEGER,
	"hash" TEXT,
	UNIQUE ("podcast_id", "feed_item_id")
);

CREATE TABLE "enclosure" (
	"_id" INTEGER PRIMARY KEY AUTOINCREMENT,
	"episode_id" INTEGER REFERENCES "episode" ("_id") ON DELETE CASCADE,
	"title" TEXT,
	"url" TEXT,
	"mime" TEXT,
	"size" INTEGER,
	UNIQUE ("episode_id", "url")
);
