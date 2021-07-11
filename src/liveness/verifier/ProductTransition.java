package liveness.verifier;

import Verifier.GlobalTransition;
import liveness.buchi.BuchiTransition;

public class ProductTransition {

	private ProductState src;
	private ProductState dest;
	private BuchiTransition buchiTrans;
	private GlobalTransition globalTrans;

	public ProductTransition(ProductState src, ProductState dest, BuchiTransition buchiTrans,
			GlobalTransition globalTrans) {
		this.src = src;
		this.dest = dest;
		this.buchiTrans = buchiTrans;
		this.globalTrans = globalTrans;
	}

	// TODO
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}
