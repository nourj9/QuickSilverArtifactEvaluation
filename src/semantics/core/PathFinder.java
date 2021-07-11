package semantics.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Core.Options;

public class PathFinder {

	private ProcessSemantics semantics;
	private HashMap<LocalState, HashMap<LocalState, HashSet<Path>>> pathCache;
	private Options options;

	public PathFinder(ProcessSemantics semantics, Options options) {
		this.semantics = semantics;
		pathCache = new HashMap<LocalState, HashMap<LocalState, HashSet<Path>>>();
		this.options = options;
	}

	public void cleanCaches() {
		// todo: fill in
		pathCache = new HashMap<LocalState, HashMap<LocalState, HashSet<Path>>>();
	}

	// Helper functions for getting all non-free usable paths, both serial and
	// parallel.
	public HashSet<Path> getAllNonFreeUsablePaths_Serial(LocalState initState, HashSet<LocalState> targetStates,
			boolean earlyTerminate) {

		HashSet<Path> toRet = new HashSet<Path>();
		for (LocalState targetState : targetStates) {
			toRet.addAll(getAllNonFreeUsablePaths_Serial(initState, targetState, earlyTerminate));
		}
		return toRet;
	}

	public HashSet<Path> getAllNonFreeUsablePaths_Parallel(LocalState initState, HashSet<LocalState> targetStates,
			boolean earlyTerminate) {

		ArrayList<Thread> threads = new ArrayList<Thread>();
		Lock addingLock = new ReentrantLock();

		HashSet<Path> toRet = new HashSet<Path>();
		// LATER have a hard limit on thread number?
		for (LocalState targetState : targetStates) {

			Thread newThread = new Thread(new Runnable() {

				@Override
				public void run() {

					// System.out.println("XX debug: " + this.hashCode());
					// long start = System.currentTimeMillis();
					HashSet<Path> paths = getAllNonFreeUsablePaths_Serial(initState, targetState, earlyTerminate);

					addingLock.lock();
					toRet.addAll(paths);
					addingLock.unlock();

					// long end = System.currentTimeMillis();
					// long duration = end - start;
					// System.out.println("Thread" + this.hashCode() + " took " + duration + "ms");
				}
			});
			newThread.start();
			threads.add(newThread);
		}

		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return toRet;
	}

	public HashSet<Path> getAllNonFreeUsablePaths_Serial(LocalState initState, LocalState targetState,
			boolean earlyTerminate) {

		HashSet<Path> allPaths = new HashSet<Path>();
		LinkedList<Path> pathsInProgress = new LinkedList<Path>();

		for (LocalTransition tr : initState.getOutgoingTransitions()) {
			Path p = new Path();
			if (p.addTransitionIfNotPresent(tr)) {
				pathsInProgress.add(p);
			}
		}

		while (!pathsInProgress.isEmpty()) {
			// System.out.println("XX debug: 2");

			Path currentPath = pathsInProgress.remove(0);

			if (currentPath.getLastState().equals(targetState)) {
				if (currentPath.containsNonFreeUsableTransitions()) {
					allPaths.add(currentPath);

					// in case we only want one of them, not all.
					if (earlyTerminate) {
						return allPaths;
					}

				}
			} else {
				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {
					if (tr.isUnusable()) {
						// System.err.println("path:"+ currentPath +" dropped because "+tr +" is
						// unuseable");
						continue;
					}
					if (tr.isReset()) {
						// System.err.println("path:"+ currentPath +" dropped because "+tr +" is
						// reset");
						continue;
					}

					Path p = new Path(currentPath);
					// System.out.println("XX debug: " + p);
					if (p.addTransitionIfNotPresent(tr)) {
						// System.out.println("XX debug: 3");
						pathsInProgress.add(p);
					}
				}
			}
		}

		return allPaths;
	}

	//
	//
	//
	//
	//
	//
	//
	//
	// Helper functions for getting all paths
	public HashSet<Path> getAllPaths_Serial(LocalState initState, HashSet<LocalState> targetStates) {

		HashSet<Path> toRet = new HashSet<Path>();
		for (LocalState targetState : targetStates) {
			// System.out.println("XX debug: 1");
			toRet.addAll(getAllPaths_Serial(initState, targetState));
		}
		return toRet;
	}

	public HashSet<Path> getAllPaths_Parallel(LocalState initState, HashSet<LocalState> targetStates) {

		ArrayList<Thread> threads = new ArrayList<Thread>();
		Lock addingLock = new ReentrantLock();

		HashSet<Path> toRet = new HashSet<Path>();
		// LATER have a hard limit on thread number?
		for (LocalState targetState : targetStates) {

			Thread newThread = new Thread(new Runnable() {

				@Override
				public void run() {

					// System.out.println("XX debug: " + this.hashCode());
					// long start = System.currentTimeMillis();
					HashSet<Path> paths = getAllPaths_Serial(initState, targetState);

					addingLock.lock();
					toRet.addAll(paths);
					addingLock.unlock();

					// long end = System.currentTimeMillis();
					// long duration = end - start;
					// System.out.println("Thread" + this.hashCode() + " took " + duration + "ms");
				}
			});
			newThread.start();
			threads.add(newThread);
		}

		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return toRet;
	}

	public HashSet<Path> getAllPaths_Serial(LocalState initState, LocalState targetState) {

		HashSet<Path> allPaths = new HashSet<Path>();
		LinkedList<Path> pathsInProgress = new LinkedList<Path>();

		for (LocalTransition tr : initState.getOutgoingTransitions()) {
			Path p = new Path();
			if (p.addTransitionIfNotPresent(tr)) {
				pathsInProgress.add(p);
			}
		}

		while (!pathsInProgress.isEmpty()) {

			Path currentPath = pathsInProgress.remove(0);

			if (currentPath.getLastState().equals(targetState)) {
//				if (currentPath.containsNonFreeUsableTransitions()) {
				allPaths.add(currentPath);
//				}
			} else {
				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {
//					if (tr.isUnusable()) {
//						continue;
//					}

					if (tr.isReset()) {
						continue;
					}

					Path p = new Path(currentPath);
					if (p.addTransitionIfNotPresent(tr)) {
						pathsInProgress.add(p);
					}
				}
			}
		}

		return allPaths;
	}

	//
	//
	//
	//
	//
	//
	//
	//
	// Helper functions for getting all simple paths
	public HashSet<Path> getAllSimplePaths_Serial(LocalState initState, HashSet<LocalState> targetStates) {
		HashSet<Path> toRet = new HashSet<Path>();
		for (LocalState targetState : targetStates) {
			toRet.addAll(getAllSimplePaths_Serial(initState, targetState));
		}
		return toRet;
	}

	public HashSet<Path> getAllSimplePaths_Serial(LocalState initState, LocalState targetState) {

		HashSet<Path> allPaths = new HashSet<Path>();
		ArrayList<Path> pathsInProgress = new ArrayList<Path>();

		for (LocalTransition tr : initState.getOutgoingTransitions()) {
			Path p = new Path();
			if (p.addTransition(tr)) {
				pathsInProgress.add(p);
			}
		}

		while (!pathsInProgress.isEmpty()) {
			Path currentPath = pathsInProgress.remove(0);

			if (currentPath.getLastState().equals(targetState)) {
				allPaths.add(currentPath);
			} else {
				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {

					if (tr.isReset()) {
						continue;
					}

					Path p = new Path(currentPath);
					if (p.addTransition(tr)) {
						pathsInProgress.add(p);
					}
				}
			}
		}

		return allPaths;

	}

	//
	//
	//
	//
	//
	//
	//
	//
	// Helper functions for getting all simple free paths

	public HashSet<Path> getAllSimpleFreePaths_Serial(LocalState initState, HashSet<LocalState> targetStates) {
		HashSet<Path> toRet = new HashSet<Path>();
		for (LocalState targetState : targetStates) {
			toRet.addAll(getAllSimpleFreePaths_Serial(initState, targetState));
		}
		return toRet;
	}

	public static HashSet<Path> getAllSimpleFreePaths_Serial(LocalState initState, LocalState targetState) {

		HashSet<Path> freePaths = new HashSet<Path>();
		ArrayList<Path> pathsInProgress = new ArrayList<Path>();

		for (LocalTransition tr : initState.getOutgoingTransitions()) {
			if (tr.isFree()) {
				Path p = new Path();
				if (p.addTransition(tr)) {
					pathsInProgress.add(p);
				}
			}
		}

		while (!pathsInProgress.isEmpty()) {
			Path currentPath = pathsInProgress.remove(0);

			if (currentPath.getLastState().equals(targetState)) {
				freePaths.add(currentPath);
			} else {
				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {

					if (tr.isReset()) {
						continue;
					}

					if (tr.isFree()) {

						Path p = new Path(currentPath);
						if (p.addTransition(tr)) {
							pathsInProgress.add(p);
						}
					}
				}
			}
		}

		return freePaths;
	}

	public HashSet<Path> getAllSimpleInternalPathsFromTo(LocalState initState, LocalState targetState) {

		HashSet<Path> simpleInternalPaths = new HashSet<Path>();
		ArrayList<Path> pathsInProgress = new ArrayList<Path>();

		for (LocalTransition tr : initState.getOutgoingTransitions()) {
			if (tr.isFree()) {
				Path p = new Path();
				if (p.addTransition(tr)) {
					pathsInProgress.add(p);
				}
			}
		}

		while (!pathsInProgress.isEmpty()) {
			Path currentPath = pathsInProgress.remove(0);

			if (currentPath.getLastState().equals(targetState)) {
				simpleInternalPaths.add(currentPath);
			} else {
				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {

					if (tr.isEnvironmentTransition()) {

						Path p = new Path(currentPath);
						if (p.addTransition(tr)) {
							pathsInProgress.add(p);
						}
					}
				}
			}
		}

		return simpleInternalPaths;

	}

	public HashSet<Path> getAllNonFreeUsablePaths(LocalState initState, HashSet<LocalState> targetStates,
			boolean earlyTerminate) {
		if (options.pathFinder_useThreadsForPathFinder) {
			return getAllNonFreeUsablePaths_Parallel(initState, targetStates, earlyTerminate);
		} else {
			return getAllNonFreeUsablePaths_Serial(initState, targetStates, earlyTerminate);
		}
	}

	public HashSet<Path> getAllPaths(LocalState initState, HashSet<LocalState> targetStates) {
		if (options.pathFinder_useThreadsForPathFinder) {
			return getAllPaths_Parallel(initState, targetStates);
		} else {
			return getAllPaths_Serial(initState, targetStates);
		}
	}

	public HashSet<Path> getAllSimplePaths(LocalState initState, HashSet<LocalState> targetStates) {
		// no parallel version for now.
		return getAllSimplePaths_Serial(initState, targetStates);
	}

	public HashSet<Path> getAllSimpleFreePaths(LocalState initState, HashSet<LocalState> targetStates) {
		// no parallel version for now.
		return getAllSimpleFreePaths_Serial(initState, targetStates);
	}
}
