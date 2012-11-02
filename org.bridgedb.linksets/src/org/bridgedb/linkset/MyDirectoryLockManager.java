/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2008.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.bridgedb.linkset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.security.AccessControlException;

import info.aduna.concurrent.locks.Lock;
import org.bridgedb.rdf.IDMapperLinksetException;

/**
 * Used to create a lock in a directory.
 * 
 * @author James Leigh
 * @author Arjohn Kampman
 */
public class MyDirectoryLockManager {

	private static final String LOCK_DIR_NAME = "lock";

	private static final String LOCK_FILE_NAME = "locked";

	private static final String INFO_FILE_NAME = "process";


	private final File dir;

	public MyDirectoryLockManager(File dir) {
		this.dir = dir;
	}

	public String getLocation() {
		return dir.toString();
	}

	private File getLockDir() {
		return new File(dir, LOCK_DIR_NAME);
	}

	/**
	 * Determines if the directory is locked.
	 * 
	 * @return <code>true</code> if the directory is already locked.
	 */
	public boolean isLocked() {
		return getLockDir().exists();
	}

	/**
	 * Creates a lock in a directory if it does not yet exist.
	 * 
	 * @return a newly acquired lock or null if the directory is already locked.
	 */
	public Lock tryLock() throws IDMapperLinksetException {
		File lockDir = getLockDir();

		if (lockDir.exists()) {
			removeInvalidLock(lockDir);
		}

		if (!lockDir.mkdir()) {
			return null;
		}

		Lock lock = null;

		try {
			File infoFile = new File(lockDir, INFO_FILE_NAME);
			File lockedFile = new File(lockDir, LOCK_FILE_NAME);

			RandomAccessFile raf = new RandomAccessFile(lockedFile, "rw");
			try {
				FileLock fileLock = raf.getChannel().lock();
				lock = createLock(raf, fileLock);
				sign(infoFile);
			}
			catch (IOException e) {
				if (lock != null) {
					// Also closes raf
					lock.release();
				}
				else {
					raf.close();
				}
				throw e;
			}
		}
		catch (IOException e) {
			throw new IDMapperLinksetException ("error creating lock" , e);
		}

		return lock;
	}

	/**
	 * Creates a lock in a directory if it does not yet exist.
	 * 
	 * @return a newly acquired lock.
	 * @throws SailLockedException
	 *         if the directory is already locked.
	 */
	public Lock lockOrFail()
		throws IDMapperLinksetException
	{
		Lock lock = tryLock();

		if (lock != null) {
			return lock;
		}

		String requestedBy = getProcessName();
		String lockedBy = getLockedBy();

		if (lockedBy != null) {
			throw new IDMapperLinksetException("lockedBy != null");
		}

		lock = tryLock();
		if (lock != null) {
			return lock;
		}

		throw new IDMapperLinksetException("lock = null");
	}

	/**
	 * Revokes a lock owned by another process.
	 * 
	 * @return <code>true</code> if a lock was successfully revoked.
	 */
	public boolean revokeLock() {
		File lockDir = getLockDir();
		File lockedFile = new File(lockDir, LOCK_FILE_NAME);
		File infoFile = new File(lockDir, INFO_FILE_NAME);
		lockedFile.delete();
		infoFile.delete();
		return lockDir.delete();
	}

	private void removeInvalidLock(File lockDir) {
		try {
			boolean revokeLock = false;

			File lockedFile = new File(lockDir, LOCK_FILE_NAME);
			RandomAccessFile raf = new RandomAccessFile(lockedFile, "rw");
			try {
				FileLock fileLock = raf.getChannel().tryLock();

				if (fileLock != null) {
					fileLock.release();
					revokeLock = true;
				}
			}
			catch (OverlappingFileLockException exc) {
				// lock is still valid
			}
			finally {
				raf.close();
			}

			if (revokeLock) {
				revokeLock();
			}
		}
		catch (IOException e) {
            e.printStackTrace();
		}
	}

	private String getLockedBy() {
		try {
			File lockDir = getLockDir();
			File infoFile = new File(lockDir, INFO_FILE_NAME);
			BufferedReader reader = new BufferedReader(new FileReader(infoFile));
			try {
				return reader.readLine();
			}
			finally {
				reader.close();
			}
		}
		catch (IOException e) {
			return null;
		}
	}

	private Lock createLock(final RandomAccessFile raf, final FileLock fileLock) {
		return new Lock() {

			private boolean active = true;

			private Thread hook;
			{
				try {
					Thread hook = new Thread(new Runnable() {

						public void run() {
							delete();
						}
					});
					Runtime.getRuntime().addShutdownHook(hook);
					this.hook = hook;
				}
				catch (AccessControlException e) {
					// okay, just remember to close it yourself
				}
			}

			public boolean isActive() {
				return active;
			}

			public void release() {
				active = false;
				try {
					if (hook != null) {
						Runtime.getRuntime().removeShutdownHook(hook);
					}
				}
				catch (IllegalStateException e) {
					// already shutting down
				}
				catch (AccessControlException e) {
				}
				delete();
			}

			void delete() {
				try {
					fileLock.release();
					raf.close();
				}
				catch (ClosedChannelException e) {
					// already closed by jvm
				}
				catch (IOException e) {
				}

				revokeLock();
			}
		};
	}

	private void sign(File infoFile)
		throws IOException
	{
		FileWriter out = new FileWriter(infoFile);
		try {
			out.write(getProcessName());
			out.flush();
		}
		finally {
			out.close();
		}
	}

	private String getProcessName() {
		return ManagementFactory.getRuntimeMXBean().getName();
	}
}
