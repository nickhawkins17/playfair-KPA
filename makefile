JCC = javac
JCR = java
JFLAGS = -g

all:
	$(JCC) $(JFLAGS) *.java

run:
	$(JCR) Driver

clean:
	$(RM) *.class