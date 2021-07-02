default:

test:
	mvn -U test

build:
	mvn clean test assembly:assembly

clean:
	mvn clean

publish-ecr: publish_acr.sh
	./publish_acr.sh

# phonies

.PHONY: default test build clean
