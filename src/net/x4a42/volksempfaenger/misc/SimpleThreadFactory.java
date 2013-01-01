package net.x4a42.volksempfaenger.misc;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleThreadFactory implements ThreadFactory {

	private String baseName;
	private ThreadGroup group;
	private AtomicInteger threadNumber = new AtomicInteger(1);

	public SimpleThreadFactory(String name) {
		this.baseName = name;
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
				.getThreadGroup();

	}

	public Thread newThread(Runnable r) {
		String threadName = baseName + " #" + threadNumber.getAndIncrement();
		Thread t = new Thread(group, r, threadName, 0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
}
