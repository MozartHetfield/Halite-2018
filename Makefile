build:
	javac hlt/*.java *.java -d .
run:
	java MyBot
clean:
	rm *.class hlt/*.class
