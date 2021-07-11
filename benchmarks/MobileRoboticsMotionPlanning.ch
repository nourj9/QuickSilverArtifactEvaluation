machine MobileRoboticsMotionPlanning

satisfies safety atmost(1, {Plan})

variables
    set participants
    int[0,N] totalReplies

actions
    env rz TaskSignal : unit
    br Coordinate : unit
    br Done : unit
    rz Reply : int[0,1] /*1 = yes 0 = no*/

crash locations: WaitForTasks, Ask, Idle, Collect, Pick, Pause, Plan, Execute;

initial location WaitForTasks 
    on recv(TaskSignal) do
        totalReplies = 0;
        participants = Empty; 
        goto Ask;
    on recv(Coordinate) do 
        rend(Reply[0], Coordinate.sID);
        goto Idle;

location Ask 
    on _ do 
        broadcast(Coordinate);
        participants.add(self);
        goto Collect;     
    on recv(Coordinate) reply Reply[1]

location Idle 
    on recv(Coordinate) do
        rend(Reply[0], Coordinate.sID);
	passive Done, pc1

location Collect 
    on recv(Reply) do 
        if (Reply.payld == 1) {
            participants.add(Reply.sID);
        }   
        totalReplies = totalReplies + 1;
        if(totalReplies == N-1) { 
            goto Pick;
        }
    on recv(Coordinate) reply Reply[1]

location Pick 
    on PartitionCons<pc1>(participants, 1)
      win:
        goto Plan;
	  lose:
	    goto Pause;
    on recv(Coordinate) reply Reply[1]
    passive Done

location Pause
	on recv(Done) do
		participants.remove(Done.sID);
		goto Pick;

location Plan
	on _ do
		participants = Empty;
		broadcast(Done);
		goto Execute;
		
location Execute
	passive pc1, Done
	on _ do goto Execute;