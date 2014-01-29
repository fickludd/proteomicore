


# H ("H", 1, 1.00794, new double[]{1.00782503207,2.0141017778,3.0160492777}, new double[]{0.999885,0.000115,0.0}, new int[]{1, 2, 3});

f = open("Atoms.txt", "r")
out = open("Atoms.java", "w")

line = f.readline()

SYMBOL = "symbol"
ATOMIC_NUMBER = "anum"
STANDARD_ATOMIC_WEIGHT = "std_aweight"
RELATIVE_ATOMIC_MASSES = "rel_amass"
OCCURENCES = "occurence"
MASS_NUMBERS = "massnum"

elements = {}

def parseFloat(x):
    if len(x) > 1:
        if x.find("(") > -1:
            return float(x.split("(")[0])
        elif x.find("[") > -1:
            return float(x[1:-2])
        else:
            return float(x)
    else:
        return 0.0

while line != "":
    atomicNumber = 1
    symbol = ""
    massNumber = 1
    standardAtomicWeight = 1.0
    relAtomMass = 1.0
    occurence = 1.0
    while line != "\n" and line != "":
        if line.startswith("Atomic Number"):
            atomicNumber = int(line.split(" = ")[1])
            
        elif line.startswith("Atomic Symbol"):
            symbol = line.split(" = ")[1][0:-1]
            
        elif line.startswith("Mass Number"):
            massNumber = int(line.split(" = ")[1])
            
        elif line.startswith("Standard Atomic Weight"):
            standardAtomicWeight = parseFloat(line.split(" = ")[1])
            
        elif line.startswith("Isotopic Composition"):
            occurence = parseFloat(line.split(" = ")[1])
            
        elif line.startswith("Relative Atomic Mass"):
            relAtomMass = parseFloat(line.split(" = ")[1])
            
        line = f.readline()
        
    if atomicNumber not in elements:
        elements[atomicNumber] = {}
        elements[atomicNumber][MASS_NUMBERS] = []
        elements[atomicNumber][RELATIVE_ATOMIC_MASSES] = []
        elements[atomicNumber][OCCURENCES] = []
        
    if atomicNumber == massNumber or not elements[atomicNumber].has_key(SYMBOL):
        elements[atomicNumber][SYMBOL] = symbol
        elements[atomicNumber][STANDARD_ATOMIC_WEIGHT] = standardAtomicWeight
    
    elements[atomicNumber][MASS_NUMBERS].append(massNumber)
    elements[atomicNumber][RELATIVE_ATOMIC_MASSES].append(relAtomMass)
    elements[atomicNumber][OCCURENCES].append(occurence)
    if line == "\n":
        line = f.readline()

for (k, e) in elements.iteritems():
    out.write('%s \t("%s", \t%d, \t%f, \tnew double[]{%s}, new double[]{%s}, new int[]{%s}),\n' % 
                (e[SYMBOL], e[SYMBOL], k, e[STANDARD_ATOMIC_WEIGHT],
                 str(e[RELATIVE_ATOMIC_MASSES])[1:-1],
                 str(e[OCCURENCES])[1:-1],
                 str(e[MASS_NUMBERS])[1:-1]
                 ))

out.close()
