machine DistributedRobotFlocking

satisfies safety atmost(1, {Leader})
actions
    br goLeft  : unit
    br goRight : unit
    env br reset : unit 
crash locations: Initial, Disperse1, Disperse2, Disperse3, Leader, LeaderRight, LeaderLeft, Follower, FollowerRight, FollowerLeft;
  
initial location Initial
    on _  do goto Disperse1;
    on _  do goto Disperse2;
    on _  do goto Disperse3;

location Disperse1
    on PartitionCons<pcL>(All, 1)
      win:
        goto Leader;
	  lose:
	    goto Follower;
	    
location Disperse2
    on PartitionCons<pcL>(All, 1)
      win:
        goto Leader;
	  lose:
	    goto Follower;
	    
location Disperse3
    on PartitionCons<pcL>(All, 1)
      win:
        goto Leader;
	  lose:
	    goto Follower;
        
location Leader
    on _ do
    	broadcast(goLeft);
	    goto LeaderLeft;
    on _ do
    	broadcast(goRight);
	    goto LeaderRight;
    on recv(reset) do 
	    goto Initial;
        
location LeaderRight
    on _ do
    	broadcast(goLeft);
	    goto LeaderLeft;
    on recv(reset) do 
	    goto Initial;
        
location LeaderLeft
    on _ do
    	broadcast(goRight);
	    goto LeaderRight;
    on recv(reset) do 
	    goto Initial;
        
location Follower
    on recv(goLeft) do 
	    goto FollowerLeft;
    on recv(goRight) do 
	    goto FollowerRight;
    on recv(reset) do 
	    goto Initial;
        
location FollowerRight
    on recv(goLeft) do 
	    goto FollowerLeft;
    on recv(reset) do 
	    goto Initial;
        
location FollowerLeft
    on recv(goRight) do 
	    goto FollowerRight;
    on recv(reset) do 
	    goto Initial;