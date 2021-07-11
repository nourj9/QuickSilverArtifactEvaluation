package lang.specs;

import lang.core.ChooseNode;
import lang.core.Location;
import lang.core.Protocol;
import lang.expr.ExprConstant;
import lang.expr.Expression;
import lang.type.TypeBool;

public class StateDesc extends ChooseNode {

	private String locName;
	private Expression predicate;

	public StateDesc(String locName, Expression predicate) {
		this.locName = locName;
		this.predicate = predicate;
	}

	public StateDesc(String locName) {
		this(locName, new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T()));
	}

	public Location getLoc(Protocol p) {
		return p.getLocationMap().get(locName);
	}

	public String getLocName() {
		return locName;
	}

	public void setLoc(String locName) {
		this.locName = locName;
	}

	public Expression getPredicate() {
		return predicate;
	}

	public void setPredicate(Expression predicate) {
		this.predicate = predicate;
	}

	@Override
	public String toString() {
		return locName + " : " + predicate;
	}

	public StateDesc smartClone() {
		return new StateDesc(locName, predicate.clone());
	}

}
