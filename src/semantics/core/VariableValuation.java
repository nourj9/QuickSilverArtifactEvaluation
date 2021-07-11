package semantics.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.stmts.Statement;
import lang.stmts.StmtAssign;
import lang.stmts.StmtBlock;

public class VariableValuation {
	private LinkedHashMap<String, Object> valueMap;

	public VariableValuation() {
		valueMap = new LinkedHashMap<String, Object>();
	}

	public VariableValuation(LinkedHashMap<String, Object> valuation) {
		valueMap = valuation;
	}

	@SuppressWarnings("unchecked")
	public VariableValuation clone() {
		LinkedHashMap<String, Object> newValMap;
		if (valueMap == null) {
			newValMap = new LinkedHashMap<String, Object>();
		} else {
			newValMap = (LinkedHashMap<String, Object>) valueMap.clone();
		}
		return new VariableValuation(newValMap);
	}

	public void setValue(String s, Expression expr) {

		valueMap.put(s, expr.eval(this));
	}

	public Object getValue(String s) {
		return valueMap.get(s);
	}

	public boolean evalGuard(Expression exp) {
		return (Boolean) (exp.eval(this));
	}

	public VariableValuation evalUpdate(StmtBlock stmts) throws Exception {

		// todo: this preserves insertion order.
		LinkedHashMap<String, Object> newValuation = new LinkedHashMap<String, Object>();
		newValuation.putAll(valueMap);

		// We assume there are no conditionals as this point
		for (Statement stmt : stmts.getStmts()) {
			if (!(stmt instanceof StmtAssign)) {
				System.out.println("Error: Encountered non-assignment statement in update evaluation:" + stmt);
				System.exit(1);
			}

			StmtAssign currAssign = (StmtAssign) stmt;
			ExprVar lhs = (ExprVar) (currAssign.getLHS());
			Expression rhs = currAssign.getRHS();

			// If you update a variable twice, you just get the last one.
			// newValuation.put(lhs.getName(), rhs.eval(this));
			newValuation.put(lhs.getName(), lhs.getType().coerceToType(rhs.eval(this)));
		}

		return new VariableValuation(newValuation);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean first = true;
		for (String key : valueMap.keySet()) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(key + " -> " + valueMap.get(key));
			first = false;
		}
		sb.append("}");
		return sb.toString();
	}

	public String toSmallString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean first = true;
		for (String key : valueMap.keySet()) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(valueMap.get(key));
			first = false;
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof VariableValuation) {
			VariableValuation other = (VariableValuation) obj;
			return this.valueMap.equals(other.valueMap);
		}
		return false;
	}

	public void fillNames(ArrayList<String> names) {
		for (String key : valueMap.keySet()) {
			names.add(key);
		}
	}


	public String graphvizString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : valueMap.keySet()) {
			if (!first) {
				sb.append("_");
			}
			sb.append(valueMap.get(key));
			first = false;
		}
		return sb.toString();
	}

}
