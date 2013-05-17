#globals
default: all
freshen: clean all
clean: clean-specials
	rm -rf bin/*
	rm -rf results/*
clean-specials:
	rm -rf xls2csv.jar

#variables
cp = -cp jsrc:bin:libs/*
dest = -d bin

#groups
all: \
	bin/Driver.class \
	bin/Converter.class

#special
test: bin/Driver.class
	java $(cp) Driver
xls2csv.jar: \
		src/Manifest.txt \
		bin/Converter.class \
		bin/Driver.class
	( cd bin && jar cfe ../xls2csv.jar Driver * )
	
#top
bin/Driver.class: src/Driver.java \
		bin/Converter.class
	javac $(cp) $(dest) src/Driver.java
	
bin/Converter.class: src/Converter.java
	javac $(cp) $(dest) src/Converter.java
