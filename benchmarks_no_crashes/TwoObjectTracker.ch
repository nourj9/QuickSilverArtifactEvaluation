machine two_object_tracker

satisfies safety atmost(2, {BothLeaders}) && atmost(1, {Leader2})

actions
    env br Reset : unit
    br ObserveObject1 : unit
    br ObserveObject2 : unit
    br FollowLeader2 : unit

initial location Scanning
	on _ do
		broadcast(ObserveObject1);
		goto NoticedObject1;

    on recv(ObserveObject1) do
        goto NoticedObject1;
	
location NoticedObject1
    on PartitionCons<lead1>(All, 2)
      win: goto BothLeaders;
	  lose: goto AllFollowers;

location BothLeaders
    on _ do
        broadcast(ObserveObject2);
        goto NoticedObject2;
        
    on recv(ObserveObject2) do
        goto NoticedObject2;
  
location NoticedObject2
    on PartitionCons<lead2>(lead1.winS, 1)
      win: goto Leader2;
	  lose: goto Leader1;


location Leader1
    on recv(Reset) do goto Scanning; 

location Leader2
	on _ do
		broadcast(FollowLeader2);
        
	on recv(Reset) do goto Scanning; 

location AllFollowers
	on recv(FollowLeader2) do 
		goto SplitFollowers;

    on recv(Reset) do goto Scanning; 

	passive lead2, ObserveObject2

location SplitFollowers
	on PartitionCons<couple1>(lead1.loseS, 2)
      win: goto FollowingLeader2;
	  lose: goto FollowingLeader1;

    on recv(Reset) do goto Scanning; 
	
location FollowingLeader1
	on recv(Reset) do goto Scanning;

	passive lead2, ObserveObject2

location FollowingLeader2	
	on recv(Reset) do goto Scanning; 

	passive lead1, lead2, ObserveObject2 
	
