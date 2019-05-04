# Makefile

RoadNetwork:
	javac @classes

tests: tests RoadNetwork
	echo "=== Running simple example ==="
	java RoadNetwork exampleAB
	echo "=== Running another example === (warning, it does not terminate)"
	java RoadNetwork example

javadoc:
	javadoc @classes

clean:
	rm -f *.class
	rm -f *.html
	rm -f package-list
	rm -f script.js
	rm -f stylesheet.css
