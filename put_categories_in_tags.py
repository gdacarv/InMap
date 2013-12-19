categories = ["roupa masculina", "roupa feminina", "presentes", "sapatos", "eletronicos", "alimentacao", "entretenimento", "livraria", "banco", "servicos", "telefonia", "departamento", "saude", "unissex", "underwear", "cosmetico", "esporte", "bebe", "streetwear", "otica", "cama mesa e banho", "joalheria", "acessorio", "casa", "evento"]
inp = open("res/xml/stores.xml", "r")
outp = open("stores_with_categories.xml", "w")
while True:
    line = inp.readline()
    if(line == ""):
        break
    if( "<store>" in line):
        store = line;
        while True:
            line = inp.readline()
            if(line == ""):
                break
            elif "</store>" in line:
                store += line
                indxS = store.index("<id_storecategory>") + len("<id_storecategory>")
                indxE = store.index("</id_storecategory>")
                catId = int(store[indxS:indxE])-1
                indxS = store.index("<tags>") + len("<tags>")
                store = store[:indxS] + categories[catId] + "," + store[indxS:]
                outp.write(store)
                break
            else:
                store += line
    else:
        outp.write(line)
inp.close()
outp.close()