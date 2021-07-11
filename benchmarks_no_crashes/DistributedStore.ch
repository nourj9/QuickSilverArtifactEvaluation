machine DistributedStore

satisfies safety atmost(1, {Leader}) && 
  (atmost(0, {Replica: stored != 1}) || atmost(0, {Replica: stored != 2}))

variables 
    int[1,5] cmd
    int[1,2] stored
    
/*
set1 = 1
set2 = 2
read = 3
dec  = 4
inc  = 5
*/
actions
    env rz doCmd      : int[1,5]
    env rz ackCmd     : int[1,5]
    env rz ret     	  : int[1,2]
    
initial location Candidate 
    on PartitionCons<elect>(All, 1) 
      win:
      	goto Leader;
    lose:
        goto Replica;
        
location Leader
	on recv(doCmd) do
        cmd = doCmd.payld;
        if(cmd <= 2 && stored != cmd){
            goto RepCmd;
        }
        if(cmd == 3){
            rend(ret[stored], read.sID);
        } else {
            goto RepCmd;
        }
      
location RepCmd
    on ValueCons<vcCmd>(All,1,cmd) do
      if(vcCmd.desVal <= 2) {  /* set */
          stored = vcCmd.desVal;
          rend(ackCmd[cmd], setv.sID);
      }
      if(vcCmd.desVal == 4) { /* increment */
          stored = stored + 1;
          rend(ackCmd[cmd], inc.sID);
      }
      if(vcCmd.desVal == 5) { /* decrement */
          stored = stored - 1;
          rend(ackCmd[cmd], dec.sID);
      }
      cmd = 1;  /* reset the command */
      goto Leader;
         
location Replica
    on ValueCons<vcCmd>(All,1,_) do
      if(vcCmd.desVal <= 2) {  /* set */
          stored = vcCmd.desVal;
      }
      if(vcCmd.desVal == 4) { /* increment */
          stored = stored + 1;
      }
      if(vcCmd.desVal == 5) { /* decrement */
          stored = stored - 1;
      }
      