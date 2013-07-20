#globals
default: all
freshen: clean all
clean: clean-specials
	rm -rf bin/*
	rm -rf output/*
clean-specials:
	rm -rf xls2csv.jar
	rm -f failure.log
freshen: clean all

#variables
version = -source 1.7
cp = -cp src:bin:libs/*
dest = -d bin

#groups
all: \
	bin/Driver.class \
	bin/Configuration.class \
	bin/Controller.class \
	bin/Converter.class \
	bin/Gui.class \
	bin/ResourceManager.class

#special
test: bin/Driver.class
	java $(cp) Driver input output
xls2csv.jar: \
		bin/Driver.class
	cd bin && \
	for file in $$(ls ../libs/*.jar); do jar xf $$file; done
	cd bin && \
	jar cfe ../xls2csv.jar Driver *
	
#top
bin/Driver.class: src/Driver.java \
		bin/Configuration.class \
		bin/Controller.class \
		bin/Converter.class \
		bin/Gui.class \
		bin/ResourceManager.class
	javac $(cp) $(dest) src/Driver.java

bin/Configuration.class: src/Configuration.java \
		bin/Converter.class \
		bin/Gui.class
	javac $(cp) $(dest) src/Configuration.java

bin/Controller.class: src/Controller.java \
		bin/Converter.class \
		bin/Gui.class \
		bin/WorkerTerminationEvent.class \
		bin/WorkerTerminationListener.class
	javac $(cp) $(dest) src/Controller.java
	
bin/Converter.class: src/Converter.java \
		bin/WorkerTerminationEvent.class \
		bin/WorkerTerminationListener.class
	javac $(cp) $(dest) src/Converter.java

bin/Gui.class: src/Gui.java \
		bin/ResourceManager.class
	javac $(cp) $(dest) src/Gui.java

bin/ResourceManager.class: src/ResourceManager.java
	javac $(cp) $(dest) src/ResourceManager.java

bin/WorkerTerminationEvent.class: \
		src/WorkerTerminationEvent.java
	javac $(cp) $(dest) src/WorkerTerminationEvent.java

bin/WorkerTerminationListener.class: \
		src/WorkerTerminationListener.java \
		bin/WorkerTerminationEvent.class
	javac $(cp) $(dest) src/WorkerTerminationListener.java
#other tests
test1: bin/Driver.class
	java $(cp) Driver tests/1170a0a_ty2e0.xls output/1170a0a_ty2e0.csv

test2: bin/Driver.class
	java $(cp) Driver tests/1170b0a_ty2e0.xls output/1170b0a_ty2e0.csv