package synthesis;

import java.util.ArrayList;

import Holes.Hole;
import lang.expr.ExprFunDecl;
import lang.expr.ExprVar;
import lang.type.Type;

// a wrapper class around the hole to keep track of things like the name, etc.
public abstract class SynthFun {
	protected Hole hole;
	protected String funName;
	protected ArrayList<Type> domTypes;
	protected ArrayList<ExprVar> domain;
	protected Type type;

	public SynthFun(String funName, Hole hole) {
		this.funName = funName;
		this.hole = hole;
		this.type = hole.getType();
		this.domain = hole.getDomain();
		this.domTypes = buildDomTypes();
		// back link to hole.
		hole.setSynthFunction(this);
	}

	public Hole getHole() {
		return hole;
	}

	public String getFunName() {
		return funName;
	}

	public ExprFunDecl getFunDecl() {
		return new ExprFunDecl(funName, domTypes, type);
	}

	public Type getType() {
		return hole.getType();
	}

	public ArrayList<Type> getDomainTypes() {
		return domTypes;
	}

	private ArrayList<Type> buildDomTypes() {

		ArrayList<Type> domTypes = new ArrayList<Type>();

		for (ExprVar exprVar : domain) {
			domTypes.add(exprVar.getType());
		}
		return domTypes;
	}

	public ArrayList<ExprVar> getDomain() {
		return domain;
	}

}
