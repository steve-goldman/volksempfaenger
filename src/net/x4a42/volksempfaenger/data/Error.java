package net.x4a42.volksempfaenger.data;

public class Error {
	public static class DuplicatePodcast extends RuntimeException {
		private static final long serialVersionUID = 4089896807412873203L;
	}

	public static class InsertError extends RuntimeException {
		private static final long serialVersionUID = -8855626746614378215L;
	}
}
