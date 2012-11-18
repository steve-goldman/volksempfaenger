package net.x4a42.volksempfaenger.data;

public class Constants {
	public static final int EPISODE_STATE_NEW = 0;
	public static final int EPISODE_STATE_DOWNLOADING = 1;
	public static final int EPISODE_STATE_READY = 2;
	public static final int EPISODE_STATE_LISTENING = 3;
	public static final int EPISODE_STATE_LISTENED = 4;

	// not on flattr
	public static final int FLATTR_STATE_NONE = 0;
	// unflattred
	public static final int FLATTR_STATE_NEW = 1;
	// user has flattred the episode, but the actual flattr action did not
	// happen yet
	public static final int FLATTR_STATE_PENDING = 10;
	// the episode got flattred successfuly
	public static final int FLATTR_STATE_FLATTRED = 20;
}
