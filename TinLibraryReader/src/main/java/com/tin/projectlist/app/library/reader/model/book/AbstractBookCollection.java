package com.tin.projectlist.app.library.reader.model.book;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractBookCollection implements IBookCollection {
	private final List<Listener> myListeners = Collections.synchronizedList(new LinkedList<Listener>());

	public void addListener(Listener listener) {
		if (!myListeners.contains(listener)) {
			myListeners.add(listener);
		}
	}

	public void removeListener(Listener listener) {
		myListeners.remove(listener);
	}

	protected boolean hasListeners() {
		return !myListeners.isEmpty();
	}

	protected void fireBookEvent(BookEvent event, Book book) {
		synchronized (myListeners) {
			for (Listener l : myListeners) {
				l.onBookEvent(event, book);
			}
		}
	}

	protected void fireBuildEvent(Status status) {
		synchronized (myListeners) {
			for (Listener l : myListeners) {
				l.onBuildEvent(status);
			}
		}
	}
}
