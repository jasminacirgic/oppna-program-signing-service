# coding=utf-8
import urllib
import re

def getSequenceDiagram( fileName, inputFormat, outputFormat, style = 'default' ):
    text_file = open(fileName + "." + inputFormat, "r")

    request = {}
    request["message"] = text_file.read()
    request["style"] = style
    request["apiVersion"] = "1"
    request["format"] = outputFormat

    url = urllib.urlencode(request)

    f = urllib.urlopen("http://www.websequencediagrams.com/", url)
    line = f.readline()
    f.close()

    expr = re.compile("(\?(img|pdf|png|svg)=[a-zA-Z0-9]+)")
    m = expr.search(line)

    if m == None:
        print "Invalid response from server."
        return False

    urllib.urlretrieve("http://www.websequencediagrams.com/" + m.group(0),
            fileName + "." + outputFormat )
    return True

style = "qsd" # Bestämmer vilken stilmall som skall användas för diagrammet: default | earth | modern-blue | mscgen | omegapple | qsd | rose | roundgreen | napkin
fileName = 'system-level' # Namnet på filen där sekvensbeskrivningen finns
inputFormat = 'txt' # Formatet på filen där sekvensbeskrivningen finns.
outputFormat = 'png' # Bildformatet som skall genereras: png | svg | pdf

getSequenceDiagram( fileName, inputFormat, outputFormat, style ) 
