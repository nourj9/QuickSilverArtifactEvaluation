machine SATSLandingVariant

satisfies safety 
atmost(4, {Enter}) &&
atmost(2, {HoldLeft}) &&
atmost(1, {FinalApproach: direction == 1})

  variables 
    int[0,1] direction
	int[0,N] totalReplies
    set BaseS

  actions 
    env rz Miss : unit
    env rz Land : unit
    br ToBase : unit
    rz Reply : int[0,1] /*1 = yes 0 = no*/

 initial location Fly 
    on PartitionCons<pc1>(All,4) 
      win:
        goto Enter;
      lose:
        goto Idle;
        
  location Enter 
    on PartitionCons<pc2>(pc1.winS,2) 
      win:
        direction = 1;
        goto HoldLeft;
      lose:
         direction = 0;
        goto HoldRight;
    passive pc1
        
  location HoldLeft 
    on PartitionCons<pc3>(pc2.winS,1)
      win:
        goto Ask;
      lose:
        goto Idle;
    passive pc4, pc5 
    
  location HoldRight 
    on PartitionCons<pc4>(pc2.loseS,1)
      win:
        goto Ask;
      lose:
        goto Idle;
    passive pc3, pc5
     
    
  location Ask 
    on _ do 
	  totalReplies = 0;
	  BaseS.add(self);
      broadcast(ToBase);
	  goto Collect;
	on recv(ToBase) reply Reply[1]

	  
location Collect 
    on recv(Reply) do 
        if (Reply.payld == 1){ 
            BaseS.add(Reply.sID);
        }
                
        totalReplies = totalReplies + 1;
        if(totalReplies == N-1){ 
            goto Base;
        }
	on recv(ToBase) reply Reply[1]
              
  location Base
    on PartitionCons<pc5>(BaseS,1)
      win:
       	BaseS = Empty;
        goto FinalApproach;
      lose:
       	BaseS = Empty;
        goto Ask;
	on recv(ToBase) reply Reply[1]
    passive pc3,pc4
        
  location FinalApproach 
    on recv(Miss) do 
	    goto Ask;
    on recv(Land) do 
      goto Runway;
    passive pc3, pc4, pc5
      
  location Idle 
    passive pc2, pc3, pc4, pc5
    on recv(ToBase) reply Reply[0]

  location Runway
    passive pc3, pc4, pc5 
    on recv(ToBase) reply Reply[0]  
    
    on _ do
     goto Runway; 
    