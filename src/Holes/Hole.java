package Holes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import lang.core.ChooseNode;
import lang.core.VarDecl;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.type.Type;
import lang.type.TypeBool;
import lang.type.TypeCard;
import lang.type.TypeInt;
import lang.type.TypeLoc;
import synthesis.SynthFun;
import synthesis.Synthesizer;
import synthesis.cvc4.CVC4SynthFun;
import synthesis.cvc4.CVC4Synthesizer;

public class Hole extends ChooseNode {
	final private int id; // should never be changed once created
	private Expression completion;
	private Type type;
	private Synthesizer snythesizer;
	private ArrayList<ExprVar> domain;
	private SynthFun synthFun;

	public Hole(int id, Type type, Collection<VarDecl> userVars) {
		this.id = id;
		if (type == null) {
			System.out.println("a hole with no type");
			Thread.dumpStack();
			System.exit(-1);
		}
		this.type = type;
		// for now, we define defaults for the domains based on the type of the hole.
		this.domain = decideDomain(userVars);
		HoleManager.addtHole(this);
	}

	private ArrayList<ExprVar> decideDomain(Collection<VarDecl> userVars) {
		ArrayList<ExprVar> domainExprs = new ArrayList<ExprVar>();
		if (type instanceof TypeInt || type instanceof TypeBool) {

			// all the int typed vars.
			for (VarDecl vd : userVars) {
				if (vd.getType() instanceof TypeInt) {
					domainExprs.add(vd.asExprVar());
				}
			}
		} else if (type instanceof TypeLoc || type instanceof TypeCard) {
			// no domain for now
		} else {
			System.err.println("Warning: unsupported return type " + type + " in  decideDomain, ignoring..");
		}
		return domainExprs;
	}

	private static AtomicInteger idCounter = new AtomicInteger(-1);

	public static int generateID() {
		return idCounter.getAndDecrement();
	}

	public int getId() {
		return id;
	}

	public ArrayList<ExprVar> getDomain() {
		return domain;
	}

	public Expression getCompletion() {
		return completion;
	}

	public void setCompletion(Expression completion) {
		this.completion = completion;
	}

//	public void setType(Type type) {
//		this.type = type;
//	}

	@Override
	public String toString() {
		return "Hole(" + id + ")" + domain + " with completion " + completion;// + " and object ID " +
																				// super.toString();
	}

	public Type getType() {
		return type;
	}

	public boolean isCompletted() {
		return (completion != null);
	}

	public void clearCompletion() {
		completion = null;
	}

	public void setSynthsisizer(Synthesizer snythesizer) {
		this.snythesizer = snythesizer;

	}

	public CVC4SynthFun getSynthsisFunction() {
		if (snythesizer instanceof CVC4Synthesizer) {
			return ((CVC4Synthesizer) snythesizer).getSynthFunForHole(this);
		}

		System.err.println("Please correct the following CVC4 output parsing error: ");
		Thread.dumpStack();
		System.exit(-1);

		return null;
	}

	public void setSynthFunction(SynthFun synthFun) {
		 this.synthFun=synthFun;
		
	}
	
	public SynthFun getSynthFun() {
		return synthFun;
	}
	
}
