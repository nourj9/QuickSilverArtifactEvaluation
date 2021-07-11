machine DistributedSensorNetwork

satisfies safety atmost(2, {Report})

variables
    set participants
    int[0, N] totalReplies
actions
    env rz SmokeSignal : unit
    br DetectSmoke : unit
    rz Reply : int[0, 1]
crash locations: WaitForSmoke, Ask, Idle, Collect, Report, Pick;

initial location WaitForSmoke 
    on recv(SmokeSignal) do
        totalReplies = 0;
        participants = Empty; 
        goto Ask;
    on recv(DetectSmoke) do 
        rend(Reply[0], DetectSmoke.sID);
        goto Idle;

location Ask 
    on _ do 
        broadcast(DetectSmoke);
        participants.add(self);
        goto Collect;
    on recv(DetectSmoke) reply Reply[1]

location Idle 
    on recv(DetectSmoke) reply Reply[0]
    passive pc1
        
location Collect 
    on recv(Reply) do 
        if (Reply.payld == 1){ 
            participants.add(Reply.sID);
        }
        totalReplies = totalReplies + 1;     
        if(totalReplies == N-1){ 
            goto Pick;
        }
    on recv(DetectSmoke) reply Reply[1]

location Pick 
    on PartitionCons<pc1>(participants, 2)
      win:
        /*reportFire()*/
        goto Report;
	  lose:
	    goto Idle;
    on recv(DetectSmoke) reply Reply[1]
        
location Report 
    on _ do goto Report;