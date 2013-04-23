#!/bin/bash
javac *.java -classpath miglayout-4.0-swing.jar
jar cmf Manifest.txt twrp.jar *.class
scp twrp.jar techerrata.com:~/public_html
