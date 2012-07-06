DROP VIEW "extended_podcast";
DROP VIEW "extended_episode";

ALTER TABLE "podcast" RENAME TO "podcast_backup";
CREATE TABLE "podcast" (
	"_id" INTEGER PRIMARY KEY AUTOINCREMENT,
	"feed" TEXT UNIQUE,
	"title" TEXT,
	"description" TEXT,
	"website" TEXT,
	"last_update" INTEGER
);
INSERT INTO "podcast" ("_id", "feed", "title", "description", "website")
SELECT "_id", "url", "title", "description", "website" FROM "podcast_backup";
DROP TABLE "podcast_backup";

ALTER TABLE "episode" RENAME TO "episode_backup";
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
	UNIQUE ("podcast_id", "feed_item_id")
);
INSERT INTO "episode" ("_id", "podcast_id", "title", "date", "url", "description",
"feed_item_id", "status", "duration_total", "duration_listened", "enclosure_id", "download_id")
SELECT ep."_id", ep."podcast_id", ep."title", ep."date", ep."url", ep."description",
ep."item_id", ep."state", en."duration_total", en."duration_listened", ep."enclosure_id",
en."download_id" FROM "episode_backup" ep
LEFT OUTER JOIN "enclosure" en ON ep."enclosure_id" = en."_id";
DROP TABLE "episode_backup";

ALTER TABLE "enclosure" RENAME TO "enclosure_backup";
CREATE TABLE "enclosure" (
	"_id" INTEGER PRIMARY KEY AUTOINCREMENT,
	"episode_id" INTEGER REFERENCES "episode" ("_id") ON DELETE CASCADE,
	"title" TEXT,
	"url" TEXT,
	"mime" TEXT,
	"size" INTEGER,
	UNIQUE ("episode_id", "url")
);
INSERT INTO "enclosure" ("_id", "episode_id", "title", "url", "mime", "size")
SELECT "_id", "episode_id", "title", "url", "mime", "size" FROM "enclosure_backup";
DROP TABLE "enclosure_backup";
