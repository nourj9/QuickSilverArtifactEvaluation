package semantics.core;

import java.util.ArrayList;
import java.util.HashSet;
import lang.core.Location;
import lang.expr.Expression;
import lang.expr.ExpressionBuilder;
import lang.type.TypeBool;

public class LocalState {

	/*****
	 * Member variables
	 */
	private Location loc = null;
	private VariableValuation sigma = null;
//	private ProcessSemantics semantics; // which semantics is this a local state of?

	private ArrayList<LocalTransition> outgoingTransitions = new ArrayList<LocalTransition>();
	private ArrayList<LocalTransition> incomingTransitions = new ArrayList<LocalTransition>();

	public static final LocalState ANYWHERE = new LocalState(new Location("Anywhere!"));

	// synthesis
	private boolean tentative = true;
	// private boolean existenceCondComputed = false;
	// private static int dummyStateCounter = 0;
	private Expression existenceCond;
	private ProcessSemantics semantics;

	/*****
	 * Constructors
	 */
	public LocalState() {
	}

	public LocalState(Location loc) {
		this.loc = loc;
		this.sigma = new VariableValuation();
	}

	public LocalState(Location loc, VariableValuation sigma) {
		this.loc = loc;
		this.sigma = sigma;
		this.incomingTransitions = new ArrayList<LocalTransition>();
		this.outgoingTransitions = new ArrayList<LocalTransition>();
	}

	/*****
	 * Accessors and Mutators
	 */
	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public VariableValuation getSigma() {
		return sigma;
	}

	public void setSigma(VariableValuation sigma) {
		this.sigma = sigma;
	}

	public ArrayList<LocalTransition> getOutgoingTransitions() {
		return outgoingTransitions;
	}

	public void setOutgoingTransitions(ArrayList<LocalTransition> outgoingTransitions) {
		this.outgoingTransitions = outgoingTransitions;
	}

	public ArrayList<LocalTransition> getIncomingTransitions() {
		return incomingTransitions;
	}

	public void setIncomingTransitions(ArrayList<LocalTransition> incomingTransitions) {
		this.incomingTransitions = incomingTransitions;
	}

	public boolean isOrphan() {
		return outgoingTransitions.isEmpty() && incomingTransitions.isEmpty();
	}

	@Override
	public String toString() {
		return "<loc: " + loc.getName() + ", sigma: " + sigma.toString() + ">";
	}

	public static String asString(Location loc, VariableValuation sigma) {
		return "<loc: " + loc.getName() + ", sigma: " + sigma.toString() + ">";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public String toSmallString() {
		return "(" + loc.getName() + "," + sigma.toSmallString() + ")";
	}

	/*
	 * public String toDetailString() { StringBuilder sb = new StringBuilder();
	 * sb.append("(" + loc.getName() + "," + sigma.toSmallString() + ")");
	 * sb.append("  Incoming:"); for(LocalTransition lt : incomingTransitions) {
	 * 
	 * } sb.append("  Incoming:"); return sb.toString(); }
	 */

	public boolean isTentative() {
		return tentative;
	}

	public void setTentative(boolean tentative) {
		this.tentative = tentative;
	}

	public void setConcrete(boolean concrete) {
		setTentative(!concrete);
	}

	@Override
	public boolean equals(Object ls) {

		if (ls == null) {
			return false;
		}

		if (this == ls) {
			return true;
		}

		if (ls instanceof LocalState) {
			LocalState other = (LocalState) ls;
			return this.loc.getName().equals(other.loc.getName()) && this.sigma.equals(other.sigma);
		}
		return false;
	}

//	public static LocalState generateDummyState() {
//		Location dummyLoc = new Location("dummy" + (dummyStateCounter++));
//		TreatyVisitor.getLocationMap().put(dummyLoc.getName(), dummyLoc);
//		return new LocalState(dummyLoc);
//	}

	public String graphvizString() {

		String lname = loc.getName();
//		if (loc.isUserProvidedLocation()) {
//			lname = loc.getName().substring(0, 3);
//		} else {
//			String[] parts = loc.getName().split("_");
//			lname = parts[0].substring(0, 1) + parts[1].substring(0, 1);
//		}

		return lname.toUpperCase() + "_" + sigma.graphvizString();
	}

	public void addOutgoingTransition(LocalTransition tr) {
		outgoingTransitions.add(tr);
	}

	public void addIncomingTransition(LocalTransition tr) {
		incomingTransitions.add(tr);
	}

	// all states that are reachable with one step, i,e, on the other end of an
	// outgoing transition.
	public HashSet<LocalState> getNextStates() {
		HashSet<LocalState> nextStates = new HashSet<LocalState>();
		for (LocalTransition tr : outgoingTransitions) {
			nextStates.add(tr.getDest());
		}
		return nextStates;
	}

	public boolean isConcrete() {
		return !tentative;
	}

	public void markConcrete() {
		this.tentative = false;
	}

	public void markTentative() {
		this.tentative = true;
	}

	public void setExistenceCondition(Expression existenceCond) {
		this.existenceCond = existenceCond;
	}

	public Expression getExistenceCondition() {

		// have we computed it before? yes? return it, no, compute it.
		if (existenceCond == null) {

			if (isConcrete()) {
				existenceCond = TypeBool.T().makeTrue();
			} else {
				// return a disjunction of existence conditions of these paths.
				LocalState initState = semantics.getInitialState();
				HashSet<Path> simplePaths = semantics.getPathFinder().getAllSimplePaths_Serial(initState, this);

				// System.out.println("xx current state: "+toSmallString());
				// for (Path path : simplePaths) {
				// System.out.println(path);
				// };
				// System.out.println("xx -------");

				Expression disjunction = TypeBool.T().makeFalse();
				for (Path path : simplePaths) {
					disjunction = ExpressionBuilder.makeDisjunction(disjunction,
							semantics.getExistenceChecker().exists(path));
				}
				existenceCond = disjunction;
			}
		}

		return existenceCond;
	}

	public void setSemantics(ProcessSemantics semantics) {
		this.semantics = semantics;
	}

	public ProcessSemantics getSemantics() {
		return semantics;
	}

	// "railroad state": a state that can only be left by an internal transition
	public boolean isRailRoadState() {
		return outgoingTransitions.size() == 1 && outgoingTransitions.get(0).isInternal(); // add env pairwise?
	}

	// checks a self loop that is either a legit one, or a self loop through a path
	// of trimmable internal transitions.
	public boolean hasRailRoadSelfLoopOn(LocalAction inAction) {

		for (LocalTransition tr : outgoingTransitions) {
			if (tr.getLocalAction().equals(inAction)) { // only look at same action stuff.

				// legit self loop? ok
				if (tr.isSelfLoop()) {
					return true;
				}

				// acts as a self loop? ok
				LocalState nextState = tr.getDest();
				while (nextState.isRailRoadState()) {
					LocalState nextDestState = nextState.getOutgoingTransitions().get(0).getDest();

					// we looped back?
					if (nextDestState.equals(this)) {
						return true;
					}

					// no more railroad transitions? break;
					if (!nextDestState.isRailRoadState()) {
						break;
					}

					// avoid infinite loops? (needs more than this for longer paths..)
					if (nextState.equals(nextDestState)) {
						break;
					} else {
						nextState = nextDestState;
					}

				}

			}
		}

		return false;
	}

//	public ProcessSemantics getSemantics() {
//		if (semantics == null) {
//			System.err.println("Error: calling getSemantics on a local state that does not have a semantics!");
//			Thread.dumpStack();
//			System.exit(-1);
//		}
//		return semantics;
//	}
//
//	public void setSemantics(ProcessSemantics semantics) {
//		this.semantics = semantics;
//	}

}
