package com.mozz.http;

import java.util.List;

public class HttpTask implements Runnable {

	static final int WAIT = 0;
	static final int EXECUTING = 1;
	static final int DONE = 2;

	int status = WAIT;

	private List<HttpTask> mTasks;

	public HttpTask(List<HttpTask> tasks) {
		mTasks = tasks;
		synchronized (mTasks) {
			mTasks.add(this);
		}

	}

	@Override
	public void run() {
		synchronized (this) {
			status = EXECUTING;

			synchronized (mTasks) {
				mTasks.remove(this);
			}
		}
	}

	synchronized int getRunStatus() {
		return status;
	}
}
