package ddd_datastructure;

public class KeyVertexVertex {
	private int key1;
	private int key2;

	/**
	 * @param key1
	 * @param key2
	 */
	public KeyVertexVertex(int key1, int key2) {
		super();
		this.key1 = key1;
		this.key2 = key2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key1;
		result = prime * result + key2;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof KeyVertexVertex))
			return false;
		KeyVertexVertex other = (KeyVertexVertex) obj;
		if (key1 != other.key1)
			return false;
		if (key2 != other.key2)
			return false;
		return true;
	}

	/**
	 * @return the key1
	 */
	public int getKey1() {
		return key1;
	}

	/**
	 * @param key1
	 *            the key1 to set
	 */
	public void setKey1(int key1) {
		this.key1 = key1;
	}

	/**
	 * @return the key2
	 */
	public int getKey2() {
		return key2;
	}

	/**
	 * @param key2
	 *            the key2 to set
	 */
	public void setKey2(int key2) {
		this.key2 = key2;
	}

}
