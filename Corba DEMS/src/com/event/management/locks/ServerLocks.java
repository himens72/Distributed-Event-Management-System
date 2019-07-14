package com.event.management.locks;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerLocks {
	public static ReadWriteLock lockOttwawaServer = new ReentrantReadWriteLock();
	public static ReadWriteLock lockTorontoServer = new ReentrantReadWriteLock();
	public static ReadWriteLock lockMontrealServer = new ReentrantReadWriteLock();

	public static ReadWriteLock lockOttwawaServerData = new ReentrantReadWriteLock();
	public static ReadWriteLock lockTorontoServerData = new ReentrantReadWriteLock();
	public static ReadWriteLock lockMontrealServerData = new ReentrantReadWriteLock();

	public static ReadWriteLock lockDiffLibraryData = new ReentrantReadWriteLock();
}
