package lang.stmts;


public class Pair<T1, T2> {
	private T1 first;
	private T2 second;

	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}

	public T1 getFirst() {
		return first;
	}

	public void setFirst(T1 first) {
		this.first = first;
	}

	public T2 getSecond() {
		return second;
	}

	public void setSecond(T2 second) {
		this.second = second;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (!(obj instanceof Pair))
			return false;
		Pair<T1, T2> other = ((Pair<T1, T2>) obj);
		return other.first.equals(this.first) && //
				other.second.equals(this.second);
	}

}
