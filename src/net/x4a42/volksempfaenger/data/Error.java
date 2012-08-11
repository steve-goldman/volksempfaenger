package net.x4a42.volksempfaenger.data;

public class Error {
	public static class DuplicateException extends RuntimeException {
		private static final long serialVersionUID = 1086272387665239716L;
	}

	public static class InsertException extends RuntimeException {
		private static final long serialVersionUID = -6053844176390280567L;
	}
}
