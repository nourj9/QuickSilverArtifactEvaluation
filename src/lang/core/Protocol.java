package lang.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.function.Predicate;

import lang.events.EventList;
import lang.expr.ExprVar;
import lang.handler.HandlerPartitionCons;
import lang.handler.HandlerValueCons;
import lang.specs.LivenessSpecification;
import lang.specs.SafetySpecification;
import lang.stmts.Pair;
import lang.type.TypeChooseSet;
import semantics.core.ProcessSemantics;

public class Protocol extends ChooseNode {
	private String name;
	private ArrayList<Location> locations;
	private ArrayList<Action> actions;
	private SafetySpecification safetySpecs;
	private ArrayList<LivenessSpecification> livenessSpecs;

	private ArrayList<VarDecl> allVariables = new ArrayList<VarDecl>();
	private HashMap<String, VarDecl> specialVars;
	private HashMap<String, VarDecl> userVars;

//	private ArrayList<VarDecl> variables;
//	private HashMap<String, VarDecl> specialVars;
	private HashMap<String, Action> actionMap;
	private HashMap<String, Location> locationMap;
	private HashSet<String> envInputs;
	private HashSet<String> envOutputs;
	private HashMap<String, HashSet<HandlerPartitionCons>> partitionInstMap;
	private HashMap<String, HashSet<HandlerValueCons>> valconsInstMap;
	public ArrayList<String> keyNames = new ArrayList<String>(); // storing names for var valuation for convenience
	private ProcessSemantics semantics; // the corresponding semantics
	private HashSet<String> actionCrashes = new HashSet<String>();
	private HashSet<String> locationCrashes = new HashSet<String>();

	// special location for crashes
	public Location CRASH = new Location("CRASH");
	private ArrayList<String> targetCrashLocNames = new ArrayList<String>();

	public Protocol() {

	}

//	public void fillProtocol(String name, //
//			ArrayList<Location> locations, //
//			ArrayList<Action> actions, //
//			HashMap<String, VarDecl> userVars, //
//			Specification specs, //
//			HashMap<String, VarDecl> specialVars, //
//			HashMap<String, Action> actionMap, //
//			HashMap<String, Location> locationMap, //
//			HashSet<String> envInputs, //
//			HashSet<String> envOutputs, //
//			HashMap<String, HashSet<HandlerPartitionCons>> partitionInstMap, //
//			HashMap<String, HashSet<HandlerValueCons>> valconsInstMap) {
//		this.name = name;
//		this.locations = locations;
//		this.actions = actions;
//		this.specs = specs;
//		this.specialVars = specialVars;
//		this.userVars = userVars;
//		this.actionMap = actionMap;
//		this.locationMap = locationMap;
//		this.envInputs = envInputs;
//		this.envOutputs = envOutputs;
//		this.partitionInstMap = partitionInstMap;
//		this.valconsInstMap = valconsInstMap;
//		// add all variables in one place.
//		// if an ordering of the variables is needed, do it here.
//		ArrayList<VarDecl> vars = new ArrayList<VarDecl>();
//		vars.addAll(userVars.values());
//		vars.addAll(specialVars.values());
//		this.allVariables = vars;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Location> getLocations() {
		return locations;
	}

	public void setLocations(ArrayList<Location> locations) {
		this.locations = locations;
	}

	public ArrayList<Action> getActions() {
		return actions;
	}

	public void setActions(ArrayList<Action> actions) {
		this.actions = actions;
	}

	public ArrayList<VarDecl> getAllVariables() {
		return allVariables;
	}

	public void setAllVariables(ArrayList<VarDecl> variables) {
		this.allVariables = variables;
	}

	public ArrayList<VarDecl> getNonSetVariables() {
		ArrayList<VarDecl> result = new ArrayList<>();
		for (VarDecl varDecl : allVariables) {
			if (!varDecl.getType().equals(TypeChooseSet.T())) {
				result.add(varDecl);
			}
		}
		return result;
	}

	public SafetySpecification getSafetySpecs() {
		return safetySpecs;
	}

	public void setLivenessSpecs(ArrayList<LivenessSpecification> specs) {
		this.livenessSpecs = specs;
	}

	public ArrayList<LivenessSpecification> getLivenessSpecs() {
		return livenessSpecs;
	}

	public void setSafetySpecs(SafetySpecification specs) {
		this.safetySpecs = specs;
	}

	public void addLocation(Location loc) {

		loc.setProtocol(this);

		locationMap.put(loc.getName(), loc);
		locations.add(loc);
	}

	public void deleteLocation(Location loc) {
		locationMap.remove(loc.getName());
		locations.remove(loc);
	}

	@Override
	public String toString() {
		String toRet = "protocol " + name + "\n";
		toRet = toRet + "\n";

		toRet = toRet + "satisfies " + safetySpecs + "\n";
		toRet = toRet + "\n";
		if (!userVars.isEmpty()) {
			toRet = toRet + "variables\n";
			for (VarDecl v : userVars.values()) {
				toRet = toRet + " " + v + "\n";
			}
			toRet = toRet + "\n";
		}
		if (!actions.isEmpty()) {
			toRet = toRet + "actions\n";
			for (Action a : actions) {
				toRet = toRet + " " + a.printDeclaration() + "\n";
			}
			toRet = toRet + "\n";
		}

		if (hasCrashes()) {
			toRet = toRet + "crashes\n";
		}

		if (hasActionCrashes()) {
			for (String a : actionCrashes) {
				toRet = toRet + " " + a;
			}
			toRet = toRet + "\n";
		}
		if (hasLocationCrashes()) {
			for (String a : locationCrashes) {
				toRet = toRet + " " + a;
			}
			toRet = toRet + "\n";
		}

		toRet = toRet + "initial ";
		for (Location location : locations) {
			toRet = toRet + location;
		}

		return toRet;
	}

	public void addLocationsAndAdjustSpecs(HashMap<Location, ArrayList<Location>> locsToAdd) {
		for (Entry<Location, ArrayList<Location>> entry : locsToAdd.entrySet()) {
			Location parentLoc = entry.getKey();
			ArrayList<Location> newlocs = entry.getValue();
			for (Location newloc : newlocs) {
				addLocation(newloc);
			}

			// take care of the Specs
			if (safetySpecs != null) {
				safetySpecs.findReplace(parentLoc, newlocs);
			}

			// add the new locations to locationCrashes if the original location crashes
			// from there.
			if (locationCrashes.contains(parentLoc.getName())) {
				for (Location newloc : newlocs) {
					locationCrashes.add(newloc.getName());
				}
			}
		}
	}

	public void addLocations(HashMap<Location, ArrayList<Location>> locsToAdd) {
		for (Entry<Location, ArrayList<Location>> entry : locsToAdd.entrySet()) {
			Location parentLoc = entry.getKey();
			ArrayList<Location> newlocs = entry.getValue();
			for (Location newloc : newlocs) {
				addLocation(newloc);
			}

			// add the new locations to locationCrashes if the original location crashes
			// from there.
			if (locationCrashes.contains(parentLoc.getName())) {
				for (Location newloc : newlocs) {
					locationCrashes.add(newloc.getName());
				}
			}
		}
	}

	public Location getInitLocation() {
		return locations.get(0);
	}

	public void deleteLocationsAndCheckSpecs(HashSet<Location> locs) {
		locations.removeAll(locs);
		safetySpecs.complainIfLocIsUsed(locs);
	}

	public int getTotalNumOfHandlers() {
		int total = 0;

		for (Location loc : locations) {
			total = total + loc.getHandlers().size();
		}

		return total;
	}

	public HashMap<String, VarDecl> getSpecialVars() {
		return specialVars;
	}

	public HashMap<String, VarDecl> getUserVars() {
		return userVars;
	}

	public HashMap<String, Location> getLocationMap() {
		return locationMap;
	}

	public HashSet<String> getEnvInputs() {
		return envInputs;
	}

	public HashSet<String> getEnvOutputs() {
		return envOutputs;
	}

	public HashMap<String, Action> getActionMap() {
		return actionMap;
	}

	public HashMap<String, HashSet<HandlerPartitionCons>> getPartitionInstMap() {
		return partitionInstMap;
	}

	public HashMap<String, HashSet<HandlerValueCons>> getValconsInstMap() {
		return valconsInstMap;
	}

	public ArrayList<String> getVarNames() {
		return keyNames;
	}

	public String getVarNamesString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean first = true;
		for (String key : keyNames) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(key);
			first = false;
		}
		sb.append("}");
		return sb.toString();
	}

	public void deleteVariables(HashSet<ExprVar> varsToDelete) {
		for (ExprVar var : varsToDelete) {
			deleteVariable(var);
		}
	}

	public void deleteVariable(ExprVar var) {
		allVariables.removeIf(new Predicate<VarDecl>() {

			@Override
			public boolean test(VarDecl vd) {
				return vd.asExprVar().equals(var);
			}
		});

		userVars.remove(var.getName());
		specialVars.remove(var.getName());

		safetySpecs.complainIfVarIsUsed(var);

	}

	public void addSpecialVariable(VarDecl vd) {
		specialVars.put(vd.getName(), vd);
		allVariables.add(vd);
	}

	public ProcessSemantics getSemantics() {
		if (semantics == null) {
			System.err.println("Error: calling getSemantics on a protocol that does not have a semantics!");
			Thread.dumpStack();
			System.exit(-1);
		}
		return semantics;
	}

	public void setSemantics(ProcessSemantics semantics) {
		this.semantics = semantics;
	}

	public void removeAction(Action action) {
		actions.remove(action);
		actionMap.remove(action.getName());
	}

	public void addActions(Collection<Action> actions) {
		for (Action action : actions) {
			this.actions.add(action);
			this.actionMap.put(action.getName(), action);
		}

	}

	public void replaceActions(ArrayList<Pair<Action, Collection<Action>>> replacements) {
		for (Pair<Action, Collection<Action>> pair : replacements) {
			removeAction(pair.getFirst());
			addActions(pair.getSecond());

			// edit crash list, if it exits
			if (actionCrashes != null) {
				if (actionCrashes.contains(pair.getFirst().getName())) {
					actionCrashes.remove(pair.getFirst().getName());
					for (Action action : pair.getSecond()) {
						actionCrashes.add(action.getName());
					}
				}
			}
		}
	}

	public void setSpecialVars(HashMap<String, VarDecl> specialVars) {
		this.specialVars = specialVars;
	}

	public void setUserVars(HashMap<String, VarDecl> userVars) {
		this.userVars = userVars;

		allVariables.clear();
		// LATER sort here if needed
		allVariables.addAll(userVars.values());
	}

	public void setActionMap(HashMap<String, Action> actionMap) {
		this.actionMap = actionMap;
	}

	public void setLocationMap(HashMap<String, Location> locationMap) {
		this.locationMap = locationMap;
	}

	public void setEnvInputs(HashSet<String> envInputs) {
		this.envInputs = envInputs;
	}

	public void setEnvOutputs(HashSet<String> envOutputs) {
		this.envOutputs = envOutputs;
	}

	public void setPartitionInstMap(HashMap<String, HashSet<HandlerPartitionCons>> partitionInstMap) {
		this.partitionInstMap = partitionInstMap;
	}

	public void setValconsInstMap(HashMap<String, HashSet<HandlerValueCons>> valconsInstMap) {
		this.valconsInstMap = valconsInstMap;
	}

//	public void createAllVars() {
//		// add all variables in one place.
//		// if an ordering of the variables is needed, do it here.
//		ArrayList<VarDecl> vars = new ArrayList<VarDecl>();
//		vars.addAll(userVars.values());
//		vars.addAll(specialVars.values());
//		this.allVariables = vars;
//	}

	public void addSpecialVarsToAllVars() {
		allVariables.addAll(specialVars.values());
	}

	public void setActionCrashes(HashSet<String> actionCrashList) {
		this.actionCrashes = actionCrashList;

	}

	public void setLocationCrashes(HashSet<String> locationCrashes) {
		this.locationCrashes = locationCrashes;

	}

	public HashSet<String> getActionCrashs() {
		return actionCrashes;

	}

	public HashSet<String> getLocationCrashs() {
		return locationCrashes;

	}

	public void addAction(Action action) {
		actions.add(action);
		actionMap.put(action.getName(), action);
	}

	public boolean hasActionCrashes() {
		return !actionCrashes.isEmpty();
	}

	public boolean hasLocationCrashes() {
		return !locationCrashes.isEmpty();
	}

	public boolean hasCrashes() {
		return hasActionCrashes() || hasLocationCrashes();
	}

	public ArrayList<String> getTargetCrashLocNames() {
		return targetCrashLocNames ;
	}
}
