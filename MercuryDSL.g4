grammar MercuryDSL;

@header {
package antlr.parsing;
}

program : 'machine' ID ('satisfies' spec)? ('variables' vars += var+)? ('actions' actions += action+)? 
('crash' crashes)? 'initial' locs += loc+ EOF;


crashes : ('actions' ':' actioncrashlist=identifierList ';')? ('locations' ':' locationcrashlist=identifierList ';')?;

spec :  ('safety' safety_spec)? ('liveness' livespecs += liveness_spec+)? ;

safety_spec : '(' safety_spec ')'												#safetySpecParen
	 | 'atmost' '(' maxNum = CONSTINT ',' '{' stateDescList '}' ')'				#safetySpecAtMost
	 | 'atleast' '(' minNum = CONSTINT ',' '{' stateDescList '}' ')'			#safetySpecAtLeast
	 | l = safety_spec '&&' r = safety_spec 									#safetySpecAnd
	 | l = safety_spec '||' r = safety_spec 									#safetySpecOr
	 ;

stateDescList :	stateDesc | stateDescList ',' stateDesc ;

stateDesc :  ID (':' bexp)? ;	


liveness_spec : 'AF'  livpred 														#livenessSpecEventually  
			  | 'AG'  premise = livpred  'implies' 'F'  conclusion = livpred    	#livenessSpecReactive
			  ;
			  
livpred : '{' stateDescList  '}'	#livpredSDlist
		| 'received' '(' ID ')'		#livpredReceived
		| 'sent'	 '(' ID ')'  	#livpredSent
		;

var : id_var | int_var | set_var ;

id_var : 'id' ID ;

int_var : 'int' '[' lbound = scalar ',' ubound = scalar  ']' ID (':='  initVal = scalar)? ;

set_var : 'set' ID ;

action : 'env'? 'br' ID ':' domain      #actionBr 
	   | 'env'? 'rz' ID ':' domain 		#actionRz
	   ;

domain 	: 'unit' 					      #domainUnit   	
	   	| 'int' '[' lbound = scalar ',' ubound = scalar ']' 	#domainIntwLowerBound	   	
      ;
	   
loc : 'location' locName = ID handler*  ;	

handler : 'on' event react			#handlerNormal
        | 'passive' identifierList 		#handlerPassive
		    ;
		
react : 'do' stmt+                        #reactDo
	  | 'where' '(' bexp ')' 'do'  stmt+           #reactPredicate
      | 'reply' msg                               #reactReply
      |  'win:' win += stmt+  'lose:' lose += stmt+   #reactPartCons
      ;
		 
identifierList : ID | ID ',' identifierList ;

event : 'ValueCons' '<' chid = ID '>' '(' chset = stexp ',' card = cardinality',' propvar = prop ')'		#eventValcons
      | 'PartitionCons' '<' chid = ID '>' '(' chset = stexp ',' card = cardinality')' 					#eventPartCons
      | '_' 																						#eventEpsilon	
      | 'recv' '(' msg ')'																			#eventMsg 
      | 'passive' '(' ID ')'																			#eventPassive 
      ;							

prop  : ID   #identifier
	  | '_'  #noprop
	  ;
	  
cardinality	: CONSTINT					#cardConst 
			| h = chole					#cardHole				
			;

stmt : updateStmt 
     | sendStmt
     | controlStmt 
     ;
	 
updateStmt : ID '=' nexp ';'						#updateAssignNexp
           | ID '=' iexp ';'						#updateAssignIexp
           | ID '=' stexp ';'						#updateAssignStexp
		   | ID '.add' '(' iexp ')' ';'				#updateSetAdd
           | ID '.remove' '(' iexp ')' ';' 			#updateSetRem
		   | ID '.addAll' '(' stexp ')' ';'				#updateSetAddAll
           | ID '.removeAll' '(' stexp ')' ';' 			#updateSetRemAll
           ;
           
sendStmt : 'rend' '(' msg ',' iexp ')' ';'			#commSend
		 | 'rend' '(' msg ')' ';'					#commSendWithoutID
         | 'broadcast' '(' msg ')' ';'				#commBroadcast
		 ;
		 
msg : ID							#msgId
    | ID '[' nexp ']' 				#msgIdVal
	;
	
controlStmt : 'if' '(' cond = bexp ')' '{' ifbranch += stmt+ '}' ('else' '{'elsebranch += stmt+ '}')?	#controlIf
            | 'goto' targetLocation ';'																	#controlGoto
			;

targetLocation	: ID						#locName 
				| h = lhole					#locHole			
				;


			
iexp : ID							#iexpID
     | ID '.sID'					#iexpIdDotId
     | 'self' 						#iexpSelf
	 ;
	
nexp : '(' nexp ')'					#nexpParen
     | scalar						#nexpScalar
     | ID 							#nexpIDNormal
     | ID '.payld' 					#nexpIDVal
     | ID '.desVal'					#nexpIDWVal
     | l = nexp '*' r = nexp		#nexpMul				
     | l = nexp '/' r = nexp		#nexpDiv
     | l = nexp '+' r = nexp		#nexpAdd
     | l = nexp '-' r = nexp 		#nexpSub
     | h = ihole					#nexpHole
     ;
	 
stexp : '(' stexp ')'			#setexpParen
     | ID						#setexpId
     | 'All'              		#setexpAll
     | 'Empty'  				#setexpEmpty
     | ID '.winS'         #setexpWinners
     | ID '.loseS'        #setexpLosers
     ;
	 
bexp : 'True'						#bexpTrue
     | 'False'						#bexpFalse
     | '(' bexp ')'					#bexpParen
     | '!' bexp						#bexpNot
     | l = bexp '&&' r = bexp		#bexpAnd
     | l = bexp '||' r = bexp		#bexpOr
     | l = nexp cmpop r = nexp 		#bexpComp
	 | l = iexp eqop r = iexp 		#bexpEqI
     | l = bexp eqop r = bexp 		#bexpEqB
     | h = bhole					#bexpHole     
     ;
	 	 
cmpop : '<' | '>' | '<=' | '>=' | eqop ;

eqop : '==' | '!=' ;
 
scalar : 'N' | CONSTINT;   

			
ihole :'??' ('(' id = CONSTINT ')')?;
bhole :'??' ('(' id = CONSTINT ')')?;   
chole :'??' ('(' id = CONSTINT ')')?;
lhole :'??' ('(' id = CONSTINT ')')?;   


/* Tokens */

ID : L (L|D)* ;
CONSTINT : D+ ;

COMMENT : '/*' .*? '*/' -> skip ;

LINE_COMMENT : '//' ~[\r\n]* -> skip;

WS : [ \t\n\r]+ -> skip ;

fragment  D : [0-9] ;
fragment  L : [A-Z] | [a-z] | '_' ;
fragment LETTER : ('a'..'z' | 'A'..'Z') ;
