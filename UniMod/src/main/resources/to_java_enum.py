#!/usr/bin/python

import sys

usage = """usage:
to_java_enum.py unimod.csv
"""

if len(sys.argv) < 2:
	print usage
	exit(1)


path = sys.argv[1]
f = open(path, "r")

header = f.readline()

for line in f:
	cols = line.split("\t")
	cols[-1] = cols[-1][:-1]
	enumName = cols[6][1:-1]
	enumName = enumName.replace(":", "_")
	enumName = enumName.replace("-", "_")
	enumName = enumName.replace("[", "_")
	enumName = enumName.replace("]", "_")
	enumName = enumName.replace("&gt;", "to_")
	enumName = enumName.replace("+", "_and_")
	if enumName[0].isdigit():
		enumName = "_"+enumName
	enumName = enumName.replace("(", "")
	enumName = enumName.replace(")", "")
	print enumName, "(", ", ".join(cols), "),"

f.close()
