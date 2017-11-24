default:

test:
	mvn -U test

build:
	mvn clean test assembly:assembly

clean:
	mvn clean

# phonies

.PHONY: default test build clean
