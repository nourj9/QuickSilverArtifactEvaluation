machine DistributedRegister

satisfies safety atmost(0, {Ret: stored == 1}) || atmost(0, {Ret: stored == 2})
variables 
    int[1,2] local
    int[1,2] stored
actions 
    env rz put : int[1,2]
    env rz get : unit
    env rz ret : int[1,2]
crash locations: Idle, Ret, Agree;

initial location Idle
    on ValueCons<vc1>(All,1,local) do
	    stored = vc1.desVal;
        local = vc1.desVal;
    on recv(get) do
		goto Ret;
	on recv(put) do
        local = put.payld;
        if(stored != local){
            goto Agree;
        }
location Ret
	on _ do
	  rend(ret[stored],get.sID);
	  goto Idle;
location Agree
    on ValueCons<vc1>(All,1,local) do
        stored = vc1.desVal;
        local = vc1.desVal;
        goto Idle;