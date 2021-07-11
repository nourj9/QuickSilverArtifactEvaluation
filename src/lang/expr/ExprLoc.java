package lang.expr;

import lang.core.Location;
import lang.core.Protocol;
import lang.type.TypeLoc;
import semantics.core.VariableValuation;

public class ExprLoc extends Expression {
	private String locName;
	//private Protocol p;

	public ExprLoc(String locName) {
		//this.p = p;
		this.locName = locName;
		this.type = TypeLoc.T();
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof ExprLoc && this.locName.equals(((ExprLoc) other).locName));
	}

	public String getTargetLocName() {
		return locName;
	}

	public Location getTargetLoc(Protocol p) {
		return p.getLocationMap().get(locName);
	}

	public void setTargetLoc(Location newTarget) {
		locName = newTarget.getName();
	}

	public int hashCode() {
		return locName.toString().hashCode();
	}

	@Override
	public ExprLoc clone() {
		return new ExprLoc(locName);
	}

	@Override
	public String toString() {
		return locName;
	}

	@Override
	public Object eval(VariableValuation variableValuation) {
		return locName;
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {

	}

	@Override
	public Expression elevateHolesAndPartialEval(VariableValuation variableValuation) {
		return this;
	}

	//public Location getTargetLoc() {
	//	return p.getLocationMap().get(locName);
	//}

}