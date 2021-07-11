package cutoffs;

import java.util.HashSet;

import semantics.core.LocalState;
import semantics.core.Path;

public class CutoffInfo {
	private int cutoff;
	private HashSet<HashSet<Path>> setsOfPaths;
	private Lemma lemmaUsed;
	private HashSet<LocalState> reactingRegoin;
	private int neededCardinality;

	public static enum Lemma {
		Lemma1, Lemma2, Lemma3, MultiLemma /* when you use different lemma on branches of compound specs */, NoLemma,
		newLogic
	};

	public CutoffInfo() {
		setsOfPaths = new HashSet<HashSet<Path>>();
	}

	public CutoffInfo(int cutoff, HashSet<HashSet<Path>> setsOfPaths) {
		this.cutoff = cutoff;
		this.setsOfPaths = setsOfPaths;
	}

	public int getCutoff() {
		return cutoff;
	}

	public void setCutoff(int cutoff) {
		this.cutoff = cutoff;
	}

	public Lemma getLemmaUsed() {
		return lemmaUsed;
	}

	public void setLemmaUsed(Lemma lemmaUsed) {
		this.lemmaUsed = lemmaUsed;
	}

	public HashSet<HashSet<Path>> getSetsOfPaths() {
		return setsOfPaths;
	}

	public void setSetsOfPaths(HashSet<HashSet<Path>> setsOfPaths) {
		this.setsOfPaths = setsOfPaths;
	}

	public void addSetOfPaths(HashSet<Path> setOfPaths) {
		this.setsOfPaths.add((setOfPaths));
	}

	// stupid java
	public void addSetsOfPaths(HashSet<HashSet<Path>> setsOfPaths) {
		this.setsOfPaths.addAll(setsOfPaths);
	}

	public void setUsedRegoin(HashSet<LocalState> parametericReactingRegoin) {
		this.reactingRegoin = parametericReactingRegoin;
	}

	public void setCardNeededToEnterRegoin(int neededCardinality) {
		this.neededCardinality = neededCardinality;

	}

	public HashSet<LocalState> getRegoinUsed() {
		return reactingRegoin;
	}

	public int getNeededCardinality() {
		return neededCardinality;
	}
}
