LIB_ANTLR := lib/antlr-4.8-complete.jar
MERCURY_SCRIPT := MercuryDSL.g4
CVC4OUT_SCRIPT := CVC4Output.g4

MSG= \
"\nuse:\n \
- \"make runAll\" to run all the benchmarks  \n \
- \"make runNoFirability\" to run all the benchmarks \n \
- \"make runBM NAME=<benchmark name>\" to run a specific benchmark \n \
- \"make runBM NAME=<benchmark name> CUTOFF=<cutoff value>\" to run a specific benchmark with a specified cutoff\n \
- \"make dsl\" to build the Mercury parser \n \
- \"make compile\" to compile the code  \n \
- \"make clean\" to clean the log files"
all: 
	@echo $(MSG)
	
dsl:
	rm -rf build
	mkdir build
	java -cp $(LIB_ANTLR) org.antlr.v4.Tool -visitor -o build $(MERCURY_SCRIPT)
	cp -R ./build/* ./src/antlr/parsing/

compile:
	rm -rf classes
	mkdir classes
	javac -cp $(LIB_ANTLR) -d classes \
		src/antlr/parsing/*.java \
		src/CExample/*.java \
		src/CEgeneralizations/*.java \
		src/Core/*.java \
		src/Main/*.java \
		src/cutoffs/*.java\
	   	src/feedback/*.java\
		src/Holes/*.java\
	    src/lang/core/*.java\
	    src/lang/events/*.java\
	    src/lang/expr/*.java\
	    src/lang/handler/*.java\
	    src/lang/specs/*.java\
	    src/lang/stmts/*.java\
	    src/lang/type/*.java\
	    src/liveness/buchi/*.java\
	    src/liveness/verifier/*.java\
	    src/ScriptGenerator/*.java\
	    src/semantics/analysis/*.java\
	    src/semantics/core/*.java\
	    src/synthesis/*.java\
	    src/synthesis/cvc4/*.java\
	    src/synthesis/cvc4/parser/*.java\
	    src/utils/*.java\
	    src/trace/*.java\
	    src/Verifier/*.java
runAll:    
	java -cp $(LIB_ANTLR):classes Main/VerificationMain runAllBMs

runNoFirability:    
	java -cp $(LIB_ANTLR):classes Main/VerificationMain runWithoutFirabilityAwareness

runBM:    
	java -cp $(LIB_ANTLR):classes Main/VerificationMain runBM $(NAME) $(CUTOFF)

clean:
	rm -rf logs/*


