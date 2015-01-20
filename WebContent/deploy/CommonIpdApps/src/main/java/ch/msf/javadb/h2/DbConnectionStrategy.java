package ch.msf.javadb.h2;

public enum DbConnectionStrategy {
	
	/*CREATE_DB, not useful because it create db if does not exist, so idem USE_OR_CREATE_DB */ USE_OR_CREATE_DB, FORCE_CREATE_DB, MUST_USE_EXISTING_DB;

}
