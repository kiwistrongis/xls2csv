#globals
default: all
freshen: clean all
clean: clean-specials
	rm -rf bin/*
	rm -rf output/*
clean-specials:
	rm -rf xls2csv.jar
freshen: clean all

#variables
cp = -cp jsrc:bin:libs/*
dest = -d bin

#groups
all: \
	bin/Driver.class \
	bin/Configuration.class \
	bin/Controller.class \
	bin/Converter.class \
	bin/Gui.class

#special
test: bin/Driver.class
	java $(cp) Driver input output
xls2csv.jar: \
		src/Manifest.txt \
		bin/Converter.class \
		bin/Driver.class
	( cd bin && jar cfme ../xls2csv.jar \
		../src/Manifest.txt Driver * )
	
#top
bin/Driver.class: src/Driver.java \
		bin/Configuration.class \
		bin/Controller.class \
		bin/Converter.class \
		bin/Gui.class
	javac $(cp) $(dest) src/Driver.java

bin/Configuration.class: src/Configuration.java \
		bin/Converter.class
	javac $(cp) $(dest) src/Configuration.java

bin/Controller.class: src/Controller.java \
		bin/Converter.class \
		bin/Gui.class
	javac $(cp) $(dest) src/Controller.java
	
bin/Converter.class: src/Converter.java
	javac $(cp) $(dest) src/Converter.java

bin/Gui.class: src/Gui.java
	javac $(cp) $(dest) src/Gui.java

#tests
test1: bin/Driver.class
	java $(cp) Driver tests/1170a0a_ty2e0.xls output/1170a0a_ty2e0.csv

test2: bin/Driver.class
	java $(cp) Driver tests/1170b0a_ty2e0.xls output/1170b0a_ty2e0.csv