package com.core.object;

import java.util.*;

/**
 * 树行结构父类
 *
 * @author fuchen
 * @date 2013-4-11
 * @param <T>
 */
public abstract class GBTree<T extends GBTree<T>> implements Iterable<T> {
	private int mySize = 1;// 当前节点的字节点数量
	public T Parent;// 父节点
	public int Level;// 当前深度 0，1，2...
	private volatile List<T> mySubTrees;// 子节点

	protected GBTree() {
		this(null);
		mPosition = -1;
	}

	protected GBTree(T parent) {
		this(parent, -1);
		mPosition = -1;
	}

	/**
	 * Tree构造方法
	 *
	 * @param parent
	 * @param position
	 */
	protected GBTree(T parent, int position) {
		if (position == -1) {
			position = parent == null ? 0 : parent.subTrees().size();
		}
		if (parent != null
				&& (position < 0 || position > parent.subTrees().size())) {
			throw new IndexOutOfBoundsException("`position` value equals "
					+ position + " but must be in range [0; "
					+ parent.subTrees().size() + "]");
		}
		Parent = parent;
		if (parent != null) {
			Level = parent.Level + 1;
			parent.addSubTree((T) this, position);
		} else {
			Level = 0;
		}
	}

	public final int getSize() {
		return mySize;
	}

	public final boolean hasChildren() {
		return mySubTrees != null && !mySubTrees.isEmpty();
	}

	public List<T> subTrees() {
		if (mySubTrees == null) {
			return Collections.emptyList();
		}
		synchronized (mySubTrees) {
			return new ArrayList<T>(mySubTrees);
		}
	}

	/**
	 * 根据段落号获取tree
	 *
	 * @param index
	 * @return
	 * @author fuchen
	 * @date 2013-4-11
	 */
	public synchronized final T getTreeByParagraphNumber(int index) {
		if (index < 0 || index >= mySize) {
			// TODO: throw an exception?
			return null;
		}
		if (index == 0) {
			return (T) this;
		}
		--index;
		if (mySubTrees != null) {
			synchronized (mySubTrees) {
				for (T subtree : mySubTrees) {
					if (((GBTree<?>) subtree).mySize <= index) {
						index -= ((GBTree<?>) subtree).mySize;
					} else {
						return (T) subtree.getTreeByParagraphNumber(index);
					}
				}
			}
		}
		throw new RuntimeException("That's impossible!!!");
	}

	int mPosition = -1;

	public void addSubTree(T subtree) {
		addSubTree(subtree, ++mPosition);
	}

	/**
	 * 加入字节点
	 *
	 * @param subtree
	 * @param position
	 * @author fuchen
	 * @date 2013-4-11
	 */
	synchronized void addSubTree(T subtree, int position) {
		if (mySubTrees == null) {
			mySubTrees = Collections.synchronizedList(new ArrayList<T>());
		}
		final int subTreeSize = subtree.getSize();
		synchronized (mySubTrees) {
			final int thisSubTreesSize = mySubTrees.size();
			while (position < thisSubTreesSize) {
				subtree = mySubTrees.set(position++, subtree);
			}
			mySubTrees.add(subtree);
			for (GBTree<?> parent = this; parent != null; parent = parent.Parent) {
				parent.mySize += subTreeSize;
			}
		}
	}

	/**
	 * 删除自己
	 *
	 * @author fuchen
	 * @date 2013-4-11
	 */
	public void removeSelf() {
		final int subTreeSize = getSize();
		GBTree<?> parent = Parent;
		if (parent != null) {
			parent.mySubTrees.remove(this);
			for (; parent != null; parent = parent.Parent) {
				parent.mySize -= subTreeSize;
			}
		}
	}

	/**
	 * 清空所有节点
	 *
	 * @author fuchen
	 * @date 2013-4-11
	 */
	public final void clear() {
		final int subTreesSize = mySize - 1;
		if (mySubTrees != null) {
			mySubTrees.clear();
		}
		mySize = 1;
		if (subTreesSize > 0) {
			for (GBTree<?> parent = Parent; parent != null; parent = parent.Parent) {
				parent.mySize -= subTreesSize;
			}
		}
	}

	/**
	 * 迭代器
	 */
	public final TreeIterator iterator() {
		return new TreeIterator(Integer.MAX_VALUE);
	}

	public final Iterable<T> allSubTrees(final int maxLevel) {
		return new Iterable<T>() {
			public TreeIterator iterator() {
				return new TreeIterator(maxLevel);
			}
		};
	}

	/**
	 * 迭代器
	 *
	 * @author fuchen
	 * @date 2013-4-11
	 */
	private class TreeIterator implements Iterator<T> {
		private T myCurrentElement = (T) GBTree.this;
		private final LinkedList<Integer> myIndexStack = new LinkedList<Integer>();
		private final int myMaxLevel;

		TreeIterator(int maxLevel) {
			myMaxLevel = maxLevel;
		}

		public boolean hasNext() {
			return myCurrentElement != null;
		}

		public T next() {
			final T element = myCurrentElement;
			if (element.hasChildren() && element.Level < myMaxLevel) {
				myCurrentElement = (T) ((GBTree<?>) element).mySubTrees.get(0);
				myIndexStack.add(0);
			} else {
				GBTree<T> parent = element;
				while (!myIndexStack.isEmpty()) {
					final int index = myIndexStack.removeLast() + 1;
					parent = parent.Parent;
					synchronized (parent.mySubTrees) {
						if (parent.mySubTrees.size() > index) {
							myCurrentElement = parent.mySubTrees.get(index);
							myIndexStack.add(index);
							break;
						}
					}
				}
				if (myIndexStack.isEmpty()) {
					myCurrentElement = null;
				}
			}
			return element;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
