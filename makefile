#globals
default: all
freshen: clean all
clean:
	rm -rf bin/*
	rm -rf results/*

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
	
#top
bin/Driver.class: src/Driver.java \
		bin/Converter.class
	javac $(cp) $(dest) src/Driver.java
	
bin/Converter.class: src/Converter.java
	javac $(cp) $(dest) src/Converter.java

