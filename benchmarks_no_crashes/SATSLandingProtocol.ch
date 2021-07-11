machine sats

satisfies safety 
atmost(4, {Enter}) &&
atmost(2, {HoldLeft}) &&
atmost(1, {FinalApproach: direction == 1})

/*
satisfies 
atmost(4, {Enter}) &&
atmost(2, {HoldLeft}) &&
atmost(2, {HoldRight}) &&
atmost(1, {FinalApproach})
*/

  variables 
    int[0, 1] direction
    set BaseS
  actions 
    env br Miss : unit
    env br Land : unit
    br ToBase : unit
    br AllHere : unit
     
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
         BaseS = Empty;
        goto Ask;
      lose:
        goto HoldLeft;
    passive pc4, pc5, ToBase, AllHere, Miss, Land
    
  location HoldRight 
    on PartitionCons<pc4>(pc2.loseS,1)  
      win:
        BaseS = Empty;
        goto Ask;
      lose:
        goto HoldRight;
    passive pc3, pc5, ToBase, AllHere, Miss, Land

 location Ask 
    on _ do
      broadcast(ToBase);
      goto Base;
    on recv(ToBase) do 
      BaseS.add(ToBase.sID);
      goto Base;
      
  location Base
    on _ do
      BaseS.add(self);
      broadcast(AllHere);
      goto Base;
    on PartitionCons<pc5>(BaseS,1)
      win:
        goto FinalApproach;
      lose:
       	BaseS = Empty;
        goto Ask;
    on recv(AllHere) do 
      BaseS.add(AllHere.sID);
      goto Base;
    passive pc3, pc4, ToBase, Miss, Land
           
  location FinalApproach 
    on recv(Miss) do 
      if(direction == 1){ 
        goto HoldLeft;
      }else{ 
        goto HoldRight;
      }
        
    on recv(Land) do 
      goto Runway;
    passive pc3, pc4, pc5,ToBase, AllHere
      
  location Idle 
    passive pc2, pc3, pc4, pc5, ToBase, AllHere, Miss, Land
    
  location Runway
    passive pc3, pc4, pc5, ToBase, AllHere, Miss, Land
    