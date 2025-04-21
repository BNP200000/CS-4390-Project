JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $<

CLASSES = \
        Server.java \
        Client.java \
        Infix.java \
        ConnectedClient.java 

default: classes

classes: $(CLASSES:.java=.class)

run-server: Server.class
	java Server

run-client: Client.class
ifndef NAME
	$(error NAME is not set. Usage: make run-client NAME=YourName)
endif
	java Client $(NAME)

clean:
	rm -f *.class
