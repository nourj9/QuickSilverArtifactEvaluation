package semantics.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lang.specs.AtMostSpec;

public class PathFinder_oldImpl {

	private ProcessSemantics semantics;
	private HashMap<LocalState, HashMap<LocalState, HashSet<Path>>> pathCache;

	public PathFinder_oldImpl(ProcessSemantics semantics) {
		this.semantics = semantics;
		pathCache = new HashMap<LocalState, HashMap<LocalState, HashSet<Path>>>();
	}

	public void cleanCaches() {
		// todo: fill in
		pathCache = new HashMap<LocalState, HashMap<LocalState, HashSet<Path>>>();
	}

	// Helper function
	public HashSet<Path> getAllNonFreeUsablePaths_Serial(LocalState initState, HashSet<LocalState> targetStates) {

		HashSet<Path> toRet = new HashSet<Path>();
		for (LocalState targetState : targetStates) {
			// System.out.println("XX debug: 1");
			toRet.addAll(getAllNonFreeUsablePaths_Serial(initState, targetState));
		}
		return toRet;
	}

	public HashSet<Path> getAllNonFreeUsablePaths_Parallel(LocalState initState, HashSet<LocalState> targetStates) {

		ArrayList<Thread> threads = new ArrayList<Thread>();
		Lock addingLock = new ReentrantLock();

		HashSet<Path> toRet = new HashSet<Path>();
		for (LocalState targetState : targetStates) {

			Thread newThread = new Thread(new Runnable() {

				@Override
				public void run() {

					// System.out.println("XX debug: " + this.hashCode());
					// long start = System.currentTimeMillis();
					HashSet<Path> paths = getAllNonFreeUsablePaths_Serial(initState, targetState);

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

//	public HashSet<Path> getAllPathsParallel(LocalState initState, LocalState targetState) {
//
//		//System.out.println("Here: " + initState.getOutgoingTransitions().size());
//		
//		HashSet<Path> allPaths = new HashSet<Path>();
//		Lock allPathsLock = new ReentrantLock();
//
//		Vector<Thread> threads = new Vector<Thread>();
//
//		for (LocalTransition tr : initState.getOutgoingTransitions()) {
//
//			Path p = new Path();
//			p.addTransition(tr);
//
//			Thread t = new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					//System.out.println("START inner " + this.hashCode());
//					HashSet<Path> paths = getAllPaths(p, targetState);
//					
//					allPathsLock.lock();
//					allPaths.addAll(paths);
//					allPathsLock.unlock();
//					
//					//System.out.println("DONE inner " + this.hashCode());
//				}
//			});
//
//			t.start();
//			threads.add(t);
//		}
//
//		for (Thread thread : threads) {
//			try {
//				thread.join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//
//		// Get all paths of length 1 from initState and to targetState
//		// then all paths one step longer form those, etc.
//		return allPaths;
//	}

//	public HashSet<Path> getAllPathsParallelO(LocalState initState, LocalState targetState, int numThreads) {
//
//		Lock allPathsLock = new ReentrantLock();
//		// Lock pipLock = new ReentrantLock();
//
//		HashSet<Path> allPaths = new HashSet<Path>();
//		Vector<Path> pathsInProgress = new Vector<Path>();
//
//		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
//
//		for (LocalTransition tr : initState.getOutgoingTransitions()) {
//			Path p = new Path();
//			if (p.addTransition(tr)) {
//				pathsInProgress.add(p);
//			}
//			executor.execute(new Runnable() {
//
//				@Override
//				public void run() {
//					// pipLock.lock();
//					Path currentPath = pathsInProgress.remove(0);
//					// pipLock.unlock();
//
//					if (currentPath.getLastState().equals(targetState)) {
//						allPathsLock.lock();
//						allPaths.add(currentPath);
//						allPathsLock.unlock();
//					} else {
//						for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {
//
//							Path p = new Path(currentPath);
//							// System.out.println("XX debug: " + p);
//							if (p.addTransitionIfNotPresent(tr)) {
//								// System.out.println("XX debug: 3");
//								// pipLock.lock();
//								pathsInProgress.add(p);
//								executor.execute(new Runnable() {
//
//									@Override
//									public void run() {
//										// pipLock.lock();
//										Path currentPath = pathsInProgress.remove(0);
//										// pipLock.unlock();
//
//										if (currentPath.getLastState().equals(targetState)) {
//											allPathsLock.lock();
//											allPaths.add(currentPath);
//											allPathsLock.unlock();
//										} else {
//											for (LocalTransition tr : currentPath.getLastState()
//													.getOutgoingTransitions()) {
//
//												Path p = new Path(currentPath);
//												// System.out.println("XX debug: " + p);
//												if (p.addTransitionIfNotPresent(tr)) {
//													// System.out.println("XX debug: 3");
//													// pipLock.lock();
//													pathsInProgress.add(p);
//													// pipLock.unlock();
//												}
//											}
//										}
//									}
//								});
//							}
//						}
//					}
//				}
//			});
//		}
//
//		// Get all paths of length 1 from initState and to targetState
//		// then all paths one step longer form those, etc.
//		return allPaths;
//	}

	public HashSet<Path> getAllPaths_Serial(Path pathInProgress, LocalState targetState) {

		HashSet<Path> allPaths = new HashSet<Path>();
		ArrayList<Path> pathsInProgress = new ArrayList<Path>();

		for (LocalTransition tr : pathInProgress.getLastState().getOutgoingTransitions()) {
			Path p = new Path(pathInProgress);
			if (p.addTransitionIfNotPresent(tr)) {
				pathsInProgress.add(p);
			}
		}

		while (!pathsInProgress.isEmpty()) {
			// System.out.println("XX debug: 2");

			Path currentPath = pathsInProgress.remove(0);

			if (currentPath.getLastState().equals(targetState)) {
				allPaths.add(currentPath);
			} else {
				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {

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

	public HashSet<Path> getAllNonFreeUsablePaths_Serial(LocalState initState, LocalState targetState) {

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

//	public Path getANonFreeUsablePath(LocalState initState, LocalState targetState) {
//
//		LinkedList<Path> pathsInProgress = new LinkedList<Path>();
//
//		for (LocalTransition tr : initState.getOutgoingTransitions()) {
//			Path p = new Path();
//			if (p.addTransitionIfNotPresent(tr)) {
//				pathsInProgress.add(p);
//			}
//		}
//
//		while (!pathsInProgress.isEmpty()) {
//			// System.out.println("XX debug: 2");
//
//			Path currentPath = pathsInProgress.removeLast();
//
//			if (currentPath.getLastState().equals(targetState)) {
//				if(!currentPath.containsNonFreeUsableTransitions()) {
//					return currentPath;
//				}
//			} else {
//				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {
//
//					Path p = new Path(currentPath);
//					// System.out.println("XX debug: " + p);
//					if (p.addTransitionIfNotPresent(tr)) {
//						// System.out.println("XX debug: 3");
//						pathsInProgress.add(p);
//					}
//				}
//			}
//		}
//
//		return null;
//
//	}

//	public HashSet<Path> getAllPathsDnC(LocalState initState, LocalState targetState) {
//		if (initState.equals(targetState)) {
//			return new HashSet<Path>();
//		}
//
//		System.out.println("Path from " + initState.toSmallString() + " to " + targetState.toSmallString());
//
//		if (pathCache.containsKey(initState) && pathCache.get(initState).containsKey(targetState)) {
//			return pathCache.get(initState).get(targetState);
//		}
//
//		HashSet<Path> result = new HashSet<Path>();
//
//		for (LocalTransition trans : initState.getOutgoingTransitions()) {
//			if (trans.getDest().equals(targetState)) {
//				Path simplePath = new Path();
//				simplePath.addTransition(trans);
//				result.add(simplePath);
//			}
//		}
//
//		for (LocalState state : semantics.getStates()) {
//			if (state.equals(initState) || state.equals(targetState)) {
//				continue;
//			}
//
//			HashSet<Path> firstSegments = getAllNonFreeUsablePaths(initState, state);
//			HashSet<Path> secondSegments = getAllNonFreeUsablePaths(state, targetState);
//
//			for (Path first : firstSegments) {
//				if (first.getTransitions().isEmpty()) {
//					continue;
//				}
//				for (Path second : secondSegments) {
//					if (second.getTransitions().isEmpty()) {
//						continue;
//					}
//					result.add(first.join(second));
//				}
//			}
//		}
//
//		HashMap<LocalState, HashSet<Path>> cachedResult = new HashMap<LocalState, HashSet<Path>>();
//		cachedResult.put(targetState, result);
//		pathCache.put(initState, cachedResult);
//
//		return result;
//	}
//	public HashSet<Path> getAllPaths_Serial(Path pathInProgress, LocalState targetState) {
//
//		HashSet<Path> allPaths = new HashSet<Path>();
//		ArrayList<Path> pathsInProgress = new ArrayList<Path>();
//
//		for (LocalTransition tr : pathInProgress.getLastState().getOutgoingTransitions()) {
//			Path p = new Path(pathInProgress);
//			if (p.addTransitionIfNotPresent(tr)) {
//				pathsInProgress.add(p);
//			}
//		}
//
//		while (!pathsInProgress.isEmpty()) {
//			// System.out.println("XX debug: 2");
//
//			Path currentPath = pathsInProgress.remove(0);
//
//			if (currentPath.getLastState().equals(targetState)) {
//				allPaths.add(currentPath);
//			} else {
//				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {
//
//					Path p = new Path(currentPath);
//					// System.out.println("XX debug: " + p);
//					if (p.addTransitionIfNotPresent(tr)) {
//						// System.out.println("XX debug: 3");
//						pathsInProgress.add(p);
//					}
//				}
//			}
//		}
//
//		return allPaths;
//
//	}
	
}



//package cutoffs;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//
//import semantics.core.LocalState;
//import semantics.core.LocalTransition;
//
//public class PathUtils {
//
////	public static HashSet<Path> getAllSimpleFreePathsFromTo(LocalState initState, LocalState targetState) {
////
////		HashSet<Path> freePaths = new HashSet<Path>();
////		ArrayList<Path> pathsInProgress = new ArrayList<Path>();
////
////		for (LocalTransition tr : initState.getOutgoingTransitions()) {
////			if (tr.isFree()) {
////				Path p = new Path();
////				if (p.addTransition(tr)) {
////					pathsInProgress.add(p);
////				}
////			}
////		}
////
////		while (!pathsInProgress.isEmpty()) {
////			Path currentPath = pathsInProgress.remove(0);
////
////			if (currentPath.getLastState().equals(targetState)) {
////				freePaths.add(currentPath);
////			} else {
////				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {
////
////					if (tr.isFree()) {
////
////						Path p = new Path(currentPath);
////						if (p.addTransition(tr)) {
////							pathsInProgress.add(p);
////						}
////					}
////				}
////			}
////		}
////
////		return freePaths;
////	}
//
////	public static HashSet<Path> getAllFreePaths(LocalState initState, HashSet<LocalState> targetStates) {
////		HashSet<Path> toRet = new HashSet<Path>();
////		for (LocalState targetState : targetStates) {
////			toRet.addAll(getAllFreePaths(initState, targetState));
////		}
////		return toRet;
////	}
//
//
//	// public static HashSet<Path> getAllPathsincludingLoopsFromTo(LocalState
//	// initState, HashSet<LocalState> targetStates) {
//	// HashSet<Path> toRet = new HashSet<Path>();
//	// for (LocalState targetState : targetStates) {
//	// toRet.addAll(getAllPathsincludingLoopsFromTo(initState, targetState));
//	// }
//	// return toRet;
//	// }
//
////	public static HashSet<Path> getAllPaths(LocalState initState, HashSet<LocalState> targetStates) {
////		HashSet<Path> toRet = new HashSet<Path>();
////		for (LocalState targetState : targetStates) {
////			System.out.println("XX debug: 1");
////			toRet.addAll(getAllPaths(initState, targetState));
////		}
////		return toRet;
////	}
//
////	public static HashSet<Path> getAllPaths(LocalState initState, LocalState targetState) {
////
////		HashSet<Path> allPaths = new HashSet<Path>();
////		ArrayList<Path> pathsInProgress = new ArrayList<Path>();
////
////		for (LocalTransition tr : initState.getOutgoingTransitions()) {
////			Path p = new Path();
////			if (p.addTransitionIfNotPresent(tr)) {
////				pathsInProgress.add(p);
////			}
////		}
////
////		while (!pathsInProgress.isEmpty()) {
////		//	System.out.println("XX debug: 2");
////
////			Path currentPath = pathsInProgress.remove(0);
////
////			if (currentPath.getLastState().equals(targetState)) {
////				allPaths.add(currentPath);
////			} else {
////				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {
////
////					Path p = new Path(currentPath);
////					//System.out.println("XX debug: " + p);
////					if (p.addTransitionIfNotPresent(tr)) {
////						//System.out.println("XX debug: 3");
////						pathsInProgress.add(p);
////					}
////				}
////			}
////		}
////
////		return allPaths;
////
////	}
//
////	public static HashSet<Path> getAllFreePaths(LocalState initState, LocalState targetState) {
////
////		HashSet<Path> allPaths = new HashSet<Path>();
////		ArrayList<Path> pathsInProgress = new ArrayList<Path>();
////
////		for (LocalTransition tr : initState.getOutgoingTransitions()) {
////			Path p = new Path();
////			if (tr.isFree()) {
////				if (p.addTransitionIfNotPresent(tr)) {
////					pathsInProgress.add(p);
////				}
////			}
////		}
////
////		while (!pathsInProgress.isEmpty()) {
////			Path currentPath = pathsInProgress.remove(0);
////
////			if (currentPath.getLastState().equals(targetState)) {
////				allPaths.add(currentPath);
////			} else {
////				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {
////
////					if (tr.isFree()) {
////						Path p = new Path(currentPath);
////						if (p.addTransitionIfNotPresent(tr)) {
////							pathsInProgress.add(p);
////						}
////					}
////				}
////			}
////		}
////
////		return allPaths;
////
////	}
//
////	public static HashSet<Path> getAllSimpleInternalPathsFromTo(LocalState initState, LocalState targetState) {
////
////		HashSet<Path> simpleInternalPaths = new HashSet<Path>();
////		ArrayList<Path> pathsInProgress = new ArrayList<Path>();
////
////		for (LocalTransition tr : initState.getOutgoingTransitions()) {
////			if (tr.isFree()) {
////				Path p = new Path();
////				if (p.addTransition(tr)) {
////					pathsInProgress.add(p);
////				}
////			}
////		}
////
////		while (!pathsInProgress.isEmpty()) {
////			Path currentPath = pathsInProgress.remove(0);
////
////			if (currentPath.getLastState().equals(targetState)) {
////				simpleInternalPaths.add(currentPath);
////			} else {
////				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {
////
////					if (tr.isEnvironmentTransition()) {
////
////						Path p = new Path(currentPath);
////						if (p.addTransition(tr)) {
////							pathsInProgress.add(p);
////						}
////					}
////				}
////			}
////		}
////
////		return simpleInternalPaths;
////
////	}
//
//	// public static boolean allPathsFromToAreFree(LocalState initState,
//	// HashSet<LocalState> targetStates) {
//	//
//	// for (LocalState targetState : targetStates) {
//	// if (!allPathsFromToAreFreegetAllPaths(initState, targetState)) {
//	// return false;
//	// }
//	// }
//	// return true;
//	// }
//	//
//	// public static boolean allPathsFromToAreFreegetAllPaths(LocalState initState,
//	// LocalState targetState) {
//	//
//	// ArrayList<Path> pathsInProgress = new ArrayList<Path>();
//	//
//	// for (LocalTransition tr : initState.getOutgoingTransitions()) {
//	// Path p = new Path();
//	// if (p.addTransitionIfNotPresent(tr)) {
//	// if (!tr.isFree()) {
//	// return false;
//	// }
//	// pathsInProgress.add(p);
//	// }
//	// }
//	//
//	// while (!pathsInProgress.isEmpty()) {
//	// Path currentPath = pathsInProgress.remove(0);
//	//
//	// if (currentPath.getLastState().equals(targetState)) {
//	// // do nothing, just remove it.
//	// } else {
//	// for (LocalTransition tr :
//	// currentPath.getLastState().getOutgoingTransitions()) {
//	//
//	// Path p = new Path(currentPath);
//	// if (p.addTransitionIfNotPresent(tr)) {
//	// if (!tr.isFree()) {
//	// return false;
//	// }
//	// pathsInProgress.add(p);
//	// }
//	// }
//	// }
//	// }
//	//
//	// return true;
//	//
//	// }
//
//	// public static HashSet<Path> getAllPathsincludingLoopsFromTo(LocalState
//	// initState, LocalState targetState) {
//	//
//	// HashSet<Path> allPaths = new HashSet<Path>();
//	// ArrayList<Path> pathsInProgress = new ArrayList<Path>();
//	//
//	// for (LocalTransition tr : initState.getOutgoingTransitions()) {
//	// Path p = new Path();
//	// if (p.addTransitionEvenSelfLoops(tr)) {
//	// pathsInProgress.add(p);
//	// }
//	// }
//	//
//	// while (!pathsInProgress.isEmpty()) {
//	// Path currentPath = pathsInProgress.remove(0);
//	//
//	// if (currentPath.getLastState().equals(targetState)) {
//	// allPaths.add(currentPath);
//	// } else {
//	// for (LocalTransition tr :
//	// currentPath.getLastState().getOutgoingTransitions()) {
//	//
//	// Path p = new Path(currentPath);
//	// if (p.addTransitionEvenSelfLoops(tr)) {
//	// pathsInProgress.add(p);
//	// }
//	// }
//	// }
//	// }
//	//
//	// return allPaths;
//	//
//	// }
//
//}
