# Road Network Simulator

**This Java program reads a road network description, builds a data structure using intersection and road objects,
and then simulates the network.**

It supports 4 kinds of intersections and roads:
- **Source Intersections**
    - Source intersections have three attributes: 
        - time before the first vehicle is created, which must be a positive floating point number
        - a number of vehicles this source will generate, which must be a positive integer
        - time between vehicles, which which must be a positive floating point number
    - Example: intersection x source 1.0 1 1.0
- **Stoplight Intersections**
    - Stoplight intersections have two attributes: 
        - time it takes to drive through the intersecion, which must be a positive floating point number
        - a period how long the light stays green, which must be a positive floating point number
    - Example: intersection x stoplight 1.0 1.0
- **NoStop Intersections**
    - NoStop intersections have only one attribute: 
        - time it takes to drive through the intersecion, which must be a positive floating point number
    - Example: intersection x nostop 1
- **Sink Intersections**
    - Sink intersections have no attributes: 
    - Example: intersection x sink
- **Roads**
    - Roads have three attributes: 
        - a source, which must be a defined intersection
        - a destination, which must be a defined intersection
        - time it takes to exit the road, which which must be a positive floating point number
    - Example: road a b 1.0

**Instructions:**

The simulation runs **only** if there were no warnings issued during the building of the model.

If the simulation encounters any errors, it will print them to the screen.

Note, the simulation never terminates because the stoplight continues to cycle after the last vehicle was crushed.

After the program is done printing to the console, press CTRL+C to terminate it.

**Example input:**
```
intersection A source 10.0 10 1.0
intersection B sink
intersection C sink
road A B 5.0
road A C 10.0
```
**Example simulation output:**
```
Vehicle entered road A C 10.0 at 10.0
Vehicle entered road A B 5.0 at 11.0
Vehicle entered road A B 5.0 at 12.0
Vehicle entered road A C 10.0 at 13.0
Vehicle entered road A B 5.0 at 14.0
Vehicle entered road A C 10.0 at 15.0
Vehicle arrived at intersection B sink at 16.0
Vehicle entered road A B 5.0 at 16.0
Vehicle arrived at intersection B sink at 17.0
Vehicle entered road A C 10.0 at 17.0
Vehicle entered road A B 5.0 at 18.0
Vehicle arrived at intersection B sink at 19.0
Vehicle entered road A B 5.0 at 19.0
Vehicle arrived at intersection C sink at 20.0
Vehicle arrived at intersection B sink at 21.0
Vehicle arrived at intersection B sink at 23.0
Vehicle arrived at intersection C sink at 23.0
Vehicle arrived at intersection B sink at 24.0
Vehicle arrived at intersection C sink at 25.0
Vehicle arrived at intersection C sink at 27.0
```

**Contents:**
```
Errors.java        -- general purpose support package for error reporting.
PRNG.java          -- general purpose pseudo-random number support.
ScanSupport.java   -- general purpose tools to enhance use of Java class Scanner
Simulator.java     -- general purpose discrete event simulation framework

Intersection.java  -- part of the road network model, intersections join roads
Road.java          -- part of the road network model, roads join intersections

RoadNetwork.java   -- the main class holding the main program

exampleAB          -- a really trivial example
example            -- a more complex example with 4 intersections

Makefile           -- automatically builds the code
```
In the above list of .java files, blank lines separate levels in the
dependency hierarchy. Each level depends on levels above it.

**Usage:**

To **build** the program, use the command "make" or "make RoadNetwork"

To **run** the tests, use the command "make tests"

To **view documentation** of the program, use the command "make javadoc"

To **clean** the directory, use the command "make clean"
