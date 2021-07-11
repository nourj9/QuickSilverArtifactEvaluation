# Getting Started Guide
We thank the artifact evaluators for taking the time to examine our tool. This file contains the instructions
to evaluate the QuickSilver tool for the following paper "QuickSilver: Modeling and Parameterized Verification for
Distributed Agreement-Based Systems" for OOPSLA 2021.

### Prerequisites
Oracle VirtualBox (version used: 6.1.22 r144080 (Qt5.6.3))

#### Credentials
```
username: ae  
password: oopsla21
```
### [Optional] running the virtual machine in "headless" mode:
Once the machine is imported into VirtualBox, one can run it headless and ssh into it as follows:  
1. start the VM: `VBoxManage startvm oopsla21 -type headless`
2. ssh into the VM: `ssh -Y -p 3316 ae@127.0.0.1`
3. terminate the VM: `VBoxManage controlvm oopsla21 poweroff soft`

This is more efficient and less taxing on the host machine.

### Dependencies for QuickSilver
* Java 8
* Antlr 4
* [Kinara](https://github.com/abhishekudupa/kinara) model checker 

All dependencies are installed on the provided VM.

### File structure

* `/home/ae/Quicksilver` is the main directory for Quicksilver. Highlights in that directory: 
   * `src` the Java source code.
   * `benchmarks` the benchmarks used for evaluation.
   * `logs` generated logs.
   * `Makefile`	the Makefile used for running various parts of this evaluation	
   * `README.md` a copy of this instructions file.
* `/home/ae/kinara` is the main directory for (the extended version of) the kinara finite-state model checker. 
  Highlights in that directory: 
   * `test/mc/benchmarks` contains the code generated by Quicksilver that kinara expects.
   * `test/mc/benchmarks/bin/opt` the runnable kinara models.

---
# Kick-the-tires (< 20 mins)
To check if the tool is working properly navigate to the `QuickSilver` 
directory and type `make runAll` as follows
```shell
cd /home/ae/Quicksilver
make runAll
```
The expected final output is of the form
```

Benchmark                        LoC	Phases	Cutoff	Time(s)
------------------------------------------------------------------
DistributedStore                 64	2	3	38.289
Consortium                       58	9	3	5.299
TwoObjectTracker                 69	9	3	0.539
DistributedRobotFlocking         78	7	2	0.089
DistributedLockService           38	2	2	0.047
DistributedSensorNetwork         55	3	3	0.83
SensorNetworkwithReset           63	3	3	1.314
SATSLandingProtocol              90	3	5	505.227
SATSLandingProtocol_II           99	5	5	583.316
MobileRoboticsMotionPlanning     71	5	2	0.098
MobileRoboticswithReset          83	4	2	0.18
DistributedRegister              32	1	2	0.257

Done.
``` 
###Output safe to ignore:
This is simply to observe various stages of QuickSilver.
```
...
benchmark: DistributedRegister
 phase-compatible? true
 cutoff value: 2
Compiling generated kinara models...
[stdin] Building eopt flavor...
[stdin] [mctests:dep] gen_DistributedRegister.cpp
...
[stdin] [mctests:cxx] gen_DistributedStore.cpp --> gen_DistributedStore.o
[stdin] [mctests:ld] gen_DistributedStore
...
Running the kinara model checker...
Running DistributedStore ... Done
...
```

---
# Step-by-Step Instructions

##[1/5]  Table 1. QuickSilver Performance
This step is exactly the kick-the-tires phase. The table is on page 22 in the paper. 

*Remarks*  
1. the `Phases` column deviates from the paper in 3 entries. The numbers in
   the table above are the correct numbers. The table in the paper 
   will be adjusted to reflect them.
2. The times may vary depending on the hardware used, however, the `SATSLandingProtocol_II` 
   benchmark should be the one with the longest time to run. 

##[2/5]  Value of MercuryCore
As discussed in the paper (lines 1055 - 1060), MercuryCore extends the decidability 
fragment of the GSP model. In particular, we introduce the notion of "firability awareness" that relaxes the 
phase compatibility conditions. To see this, one can run the following commands. Essentially,
this disables relaxations that MercuryCore gives over GSP, and, as a result, some of the benchmarks
are now not phase-compatible.
```shell
cd /home/ae/Quicksilver
make runNoFirability
```
The expected final output here is:
```
---------------------------------------------------
The following systems are now not phase-compatible:
Consortium
TwoObjectTracker
SATSLandingProtocol
SATSLandingProtocol_II
MobileRoboticswithReset

Done.
```
In the paper, these are the benchmarks marked with a (*) in Table 1.
##[3/5] (Optional) Value of feedback
As discussed in the paper (lines 1049 - 1054), in some cases, one may start with a Mercury system that
is not phase-compatible. In that case, QuickSilver reports back heuristically-ranked feedback to help
the system designer obtain a phase-compatible system. For instance, in the `SATSLandingProtocol` system, when processes 
are about to enter the 'Base' location, they start exchanging IDs to build the participant set using the `ToBase` broadcast 
as follows:
```
 location Ask 
    on _ do
      broadcast(ToBase);
      goto Base;
    on recv(ToBase) do           // *
      BaseS.add(ToBase.sID);     // *
      goto Base;                 // *
```
However, if the designer omitted the receive handler of that broadcast (lines with a *), then QuickSilver reports 
the following phase-compatibility violation and ranked suggestions to fix it:
```
CE to phase-compatability: CONDITION 2
State (Ask,{0}) has a globally-synchronizing 
acting transition (Ask,{0})	--ToBase!!-->	(Base,{0}) 
but no corresponding reacting transition.

Short version(2): (Ask,{0}) needs a corresponding reacting transition on ToBase

Suggestions to solve this: 
add transition (Ask,{0})	--ToBase??-->	(Base,{0})
add transition (Ask,{0})	--ToBase??-->	(Anywhere!,{})
```
If the reviewer is interested in performing such steps, please remove lines `55 - 58` in the following
file:
```
/home/ae/Quicksilver/benchmarks/SATSLandingProtocol.ch
```
and run QuickSilver to check that specific benchmark again:
```shell
cd /home/ae/Quicksilver/
make runBM NAME=SATSLandingProtocol 
```

##[4/5] (optional) Value of small cutoffs
As discussed in the paper (lines 1067 - 1073), we stress the value of obtaining small
cutoff values to enable efficient parameterized verification. increasing the number of processes 
causes the run time to grow exponentially. 

While not surprising, if the reviewer is interested in seeing such exponential growth, they can
run the following commands. Note that this might take several hours as it runs all the
benchmarks with an increasing number of processes (2 to 10).
```shell
cd /home/ae/Quicksilver/
chmod +x varyN.sh 
./varyN.sh 
```
Note: to kill the script, use `ctrl + z`.

The results can be found in the following directory, where every benchmark will have a directory 
containing all the runs that successfully completed in under 30 minutes.
```
/home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN
```
### Run a specific benchmark with a specific number of processes
Another way to verify this is to run a given benchmark with a given number of processes, and observe the 
exponential growth of time.

For example, to run the `Consortium` benchmark with `4` processes, please use the following commands:
```shell
cd /home/ae/Quicksilver/
make runBM NAME=Consortium CUTOFF=4
```
The expected final output should look like this:
```
Benchmark                        LoC	Phases	Cutoff	Time(s)
------------------------------------------------------------------
Consortium                       58	9	4	41.484

Done.
```
##[5/5]  (Optional) Usability: Verify your own system
In order to verify your own distributed system, you can express it in the Mercury syntax presented 
in the paper. Please place your system description for `machine Foo` in 
a file named `Foo.ch` in the benchmark directory:
```
/home/ae/Quicksilver/benchmarks
```
To have QuickSilver verify your system, please run the following command:
```shell
cd /home/ae/Quicksilver/
make runBM NAME=Foo
```
Note that the possible outputs for this step are:
1. QuickSilver might complain about syntax errors.
    ```
    Please correct the following parsing error: 
    line 41:0 missing ';' at 'location'
    ```
2. The Mercury model parses correctly and is not phase-compatible. In this case, QuickSilver 
returns the violated phase-compatibility condition as well as possible ways to fix it. For instance,
   in the `SATSLandingProtocol` system, the `passive` handler in location `HoldRight` indicates that
   the aircraft in location `HoldRight` "do not care" about the list of action provided. 
   ```
      location HoldRight 
        passive pc3, pc5, ToBase, AllHere, Miss, Land
   ```
   QuickSilver still needs such information to conclude that the system is phase-compatible. 
   If we, say, remove `pc3` (which is the identifier of the agreement operation performed in the `HoldLeft` location), 
   QuickSilver returns the following output. Please note the "Short version" as
   the printouts above are a little more involved for the sake of this demo.

   ```
   All feedback:
    CE to phase-compatability: CONDITION 3.1
    Acting transition (FinalApproach,{1}) --Miss!!--> (HoldLeft,{1}) ends in a state with a reacting 
    transition (HoldLeft,{1}) --pc3:l--> (HoldLeft,{1}), but acting transition
    (FinalApproach,{0})	--Miss!!-->	(HoldRight,{0}) ends in a state without such a reacting transition.
    
   Short version(3.1): (HoldRight,{0}) needs a reacting transition on pc3 (just like (HoldLeft,{1}))
   
   Suggestions to solve this:
    add transition (HoldRight,{0})	--pc3:l-->	(Anywhere!,{})
    replace transition (FinalApproach,{0}) --Miss!!--> (HoldRight,{0})
      with (FinalApproach,{0}) --Miss!!--> (FinalApproach,{0})
    ...
    ```
3. The Mercury model is phase-compatible, but not cutoff-amenable. Again, QuickSilver
returns the violated cutoff-amenability condition. For instance, in the `Consortium` system, if
   one mistakenly sent the elected processes to `Wait` and the losing processes
   to `Deliberate` as follows:
   ```
   initial location Election
    on PartitionCons<elect>(All, 2)
      win:
        goto Wait; 
      lose: 
        goto Deliberate;
   ```
   Then the tool returns the following output:
    ```
    All feedback for cutoffs:
    Lemma 2 failed: the following transitions are not independent on path:
    (Election,{1, 1, 1}) --elect:l--> (Deliberate,{1, 1, 1})
    [(Election,{1, 1, 1})	--elect:l-->	(Deliberate,{1, 1, 1})]
    ...
    ```
   indicating that, from the initial state `Election` the state `Deliberate` (which is of interest to the specification 
   of the program) was reached by a dependent transition (`elect:l` means the losing transition 
   of the Partition operation `elect`). The user then can notice that the path should not
   have included such dependent transition. 
   
4. The Mercury model is phase-compatible and cutoff-amenable. QuickSilver
   then invokes the kinara finite-state model checker with the cutoff number 
   of processes. Kinara might return a safety violation,
   or conclude that the system is safe. An example of an error kinara may return 
   is simply a trace to the error state.
   ```
    Kinara returned the following violation:
    AQS Built, contains 128 states and 263 edges.
    AQS contains one or more errors, and may not be complete.
    Building Constraints for 1 error(s)...
    Safety violation trace:
    Trace to safety violation with 3 steps:
    
    Initial State (in full)
    -----------------------------------------------------
    Consortium[ProcessIDType::0].elect_cardinality : 3
    ...
    -----------------------------------------------------
    
    Fired Guarded Command with label: (internal transition)
    Obtained next state (delta from previous state):
    -----------------------------------------------------
    Consortium[ProcessIDType::0].elect_winS[ProcessIDType::0] : true
    ....
    -----------------------------------------------------
    .
    .
    .
    Last state of trace (in full):
    -----------------------------------------------------
    Consortium[ProcessIDType::0].elect_cardinality : 3
    ...
   ```
### Changing the feedback level
In some cases, the user might benefit from more feedback. This option is not relayed to the
command line interface, but can be turned on by uncommenting this line
```
//options.phCompFeedbackLevel = PhCompFeedback.All;
```
in the following file:
```
/home/ae/Quicksilver/src/Main/VerificationMain.java
```
and compiling the tool using:
```shell
cd /home/ae/Quicksilver
make compile
```