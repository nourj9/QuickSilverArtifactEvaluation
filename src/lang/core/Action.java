package lang.core;

import lang.expr.Expression;
import lang.type.Type;
import lang.type.TypeInt;
import lang.type.TypeUnit;

public class Action extends ChooseNode {

	public static enum CommType {
		BROADCAST, PAIRWISE, UNKNOWN
	};

	private CommType commType;
	private boolean isEnv;
	private String name;
	private Expression value;
	private Type domain;

	public Action(String name, Boolean isEnv, Type domain, CommType ctype) {
		this(name, isEnv, domain, ctype, null);
	}

	// public Action(String name, Boolean isEnv, Type domain, CommType ctype) {
	// this(name, isEnv, domain,ctype, null);
	// }

	public Action(String name, Boolean isEnv, Type domain, CommType ctype, Expression value) {
		this.name = name;
		this.isEnv = isEnv;
		this.domain = domain;
		this.value = value;
		this.commType = ctype;
	}

	public Expression getValue() {
		return value;
	}

	public CommType getCommType() {
		return commType;
	}

	public void setCommType(CommType commType) {
		this.commType = commType;
	}

	public boolean isEnv() {
		return isEnv;
	}

	public String getName() {
		return name;
	}

	public Type getDomain() {
		return domain;
	}

	@Override
	public String toString() {
		return name + ((value == null) ? "" : "[" + value + "]");
	}

	public String printDeclaration() {
		String t = "unknown";
		switch (commType) {
		case BROADCAST:
			t = "br";
			break;
		case PAIRWISE:
			t = "rz";
			break;
		default:
			break;
		}

		return (isEnv ? "env " : "") + t + " " + name + " : " + domain;
	}

	public boolean isUnitDom() {
		return domain instanceof TypeUnit;
	}

	public boolean isIntDom() {
		return domain instanceof TypeInt;
	}

	public boolean isBr() {
		return commType == CommType.BROADCAST;
	}

	public boolean isRz() {
		return commType == CommType.PAIRWISE;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Action)) {
			return false;
		}
		
		Action otherAct = (Action) other;
		return this.name.equals(otherAct.name) &&
				this.commType.equals(otherAct.commType) &&
				this.domain.equals(otherAct.domain) &&
				(this.isEnv == otherAct.isEnv) ;
	}
	
	public int hashCode() {
		return name.hashCode();
	}

}
