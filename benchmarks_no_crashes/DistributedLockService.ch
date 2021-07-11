machine DistributedLockService

satisfies safety atmost(1, {Leader})

actions 
    br stepdown      : unit 
    br forward       : unit 
    env rz write     : unit 
    env rz read      : unit 
    env rz ackWrite  : unit
    env rz ackRead   : unit
    env rz timeout   : unit

initial location Candidate 
    on PartitionCons<elect>(All, 1) 
      win:
      	goto Leader;
    lose:
        goto Replica;

location Replica
    on recv (forward) do
        goto Replica;   
           
    on recv (stepdown) do 
        goto Candidate;

location Leader
     on recv (read) do
        rend(ackRead, read.sID);

    on recv (write) do
        broadcast(forward); 
        rend(ackWrite, write.sID);
        goto Leader;

    on recv (timeout) do
        broadcast(stepdown);
        goto Candidate;