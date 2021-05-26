javac -cp "gson-2.8.2.jar" Blockchain.java

fuser -k 4710/tcp
fuser -k 4711/tcp
fuser -k 4712/tcp
fuser -k 4820/tcp
fuser -k 4821/tcp
fuser -k 4823/tcp
fuser -k 4930/tcp
fuser -k 4931/tcp
fuser -k 4932/tcp

java -cp ".:gson-2.8.2.jar" Blockchain 0 & java -cp ".:gson-2.8.2.jar" Blockchain 1 & java -cp ".:gson-2.8.2.jar" Blockchain 2 && fg
