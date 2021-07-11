machine Consortium

satisfies safety	atmost(2, {Deliberate})
			&& (atmost(0, {Decided: decision == 1}) 
				|| atmost(0, {Decided: decision == 2}))   

variables 
    int[1,2] prop 
    int[1,2] decision
	
actions 
  		br inform 		: int[1,2]
  	env br reset 		: unit
  	env rz influence 	: int[1,2]
 
crash locations: Election, Deliberate, Decided, ReplicaDone, Wait, LeaderDone;

initial location Election
    on PartitionCons<elect>(All, 2)
      win:
        goto Deliberate; 
      lose: 
        goto Wait;
 
location Deliberate
    on ValueCons<vc>(elect.winS, 1, prop) do
      decision = vc.desVal;
      goto Decided;

    on recv(influence) do
      prop = influence.payld;

location Decided
    on PartitionCons<share>(elect.winS, 1) 
      win:
		  broadcast(inform[decision]);
		  goto LeaderDone;
      lose: 
	      goto LeaderDone;

location LeaderDone
	on recv(reset) do
	    prop = 1;
	    decision = 1;
	    goto Election;
	passive inform
		    
location Wait
	on recv(inform) do
	  decision = inform.payld;
	  goto ReplicaDone;
	passive vc, share
 	
location ReplicaDone
	on recv(reset) do
	    decision = 1;
	    goto Election;
	