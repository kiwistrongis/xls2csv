#!/bin/python
import sys
import csv
csv1 = list(csv.DictReader(open(sys.argv[1])))
csv2 = list(csv.DictReader(open(sys.argv[2])))
set1 = set(csv1)
set2 = set(csv2)
print set1 - set2 # in 1, not in 2
print set2 - set1 # in 2, not in 1
print set1 & set2 # in both