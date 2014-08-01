package com.contralabs.inmap.recommendation;

import java.util.HashMap;

public class Evaluation {
	
	public static int mIndex = 0;
	
	private static final UserModel[] mUserModels = new UserModel[] {
			new UserModel(
			"Esportista",
			new HashMap<Long, String[]>() {{
				put(Long.valueOf(4l), "saude,pe,clinica,pes,clinicas,podologia,bem estar,massagem,relaxamento,mãos,calos,calosidades,unha,unhas encravadas,G1".split(","));
				put(Long.valueOf(58l), "sapatos,L1,tênis,calçados,acessórios,caminhar,andar,correr,cooper,running,chuteiras,adventure".split(","));
				put(Long.valueOf(176l), "streetwear,L1,moda,praia,sungas,biquinis,blusas,batas,saída de praia".split(","));
				put(Long.valueOf(258l), "esporte,L2,tenis,camisas,seleção,camisetas,moletom,jaquetas,futebol,relógios,corrida,chuteiras,dri-fit".split(","));
			}},
			"corrida,bike,futebol,esporte,tensor".split(","), 
			new int[]{17, 13, 4, 17}
			), new UserModel(
			"Esportista Natureba",
			new HashMap<Long, String[]>() {{
				put(Long.valueOf(4l), "saude,pe,clinica,pes,clinicas,podologia,bem estar,massagem,relaxamento,mãos,calos,calosidades,unha,unhas encravadas,G1".split(","));
				put(Long.valueOf(58l), "sapatos,L1,tênis,calçados,acessórios,caminhar,andar,correr,cooper,running,chuteiras,adventure".split(","));
				put(Long.valueOf(176l), "streetwear,L1,moda,praia,sungas,biquinis,blusas,batas,saída de praia".split(","));
				put(Long.valueOf(258l), "esporte,L2,tenis,camisas,seleção,camisetas,moletom,jaquetas,futebol,relógios,corrida,chuteiras,dri-fit".split(","));
				put(Long.valueOf(129l), "saude,L1,produtos naturais,suplementos,esotéricos,whey protein,chás,incensos".split(","));
				put(Long.valueOf(188l), "alimentacao,L1,chás,açaí,açaí na tigela,baguete,bolinho,chocolate,café expresso,cappuccino,pãozinho,salgados,folhados,chá gelado".split(","));
				put(Long.valueOf(372l), "alimentacao,L3,refrigerantes,agua,sucos,bebidas,camarão,frango,peixe,galinha,frango,lasanhas,massas,sobremesas,espetinhos,pasteis,pastel,bolinhos,saladas,bacalhau,parmegiana".split(","));
			}}, 
			"corrida,bike,futebol,esporte,tensor,sanduiche natural,salada,frango,açaí".split(","), 
			new int[]{17, 13, 4, 6, 17}
			), new UserModel(
			"Patricinha",
			new HashMap<Long, String[]>() {{
				put(Long.valueOf(33l), "cosmetico,L1,salão de beleza,estética,serviços,serviços de beleza,bem-estar,Jacques Janine,cabelos,peles".split(","));
				put(Long.valueOf(59l), "sapatos,L1,calçados,sapatos,sapatilhas,carteiras,bolsas,sandálias,scarpins,tamancos,malas,rasteiras,peep toe,botas,plataforma,tênis".split(","));
				put(Long.valueOf(145l), "cosmetico,L1,cabelo,cabeleireiros,salão de beleza,estética,manicure,pedicure,penteados".split(","));
				put(Long.valueOf(254l), "roupa feminina,L2,vestidos,vestido,saia,saias,jaquetas,jaqueta,casaco,casacos,blusa,blusas,trench,chemise,macacao,bolsas,cintos,clutch,carteira,carteiras".split(","));
				put(Long.valueOf(72l), "roupa feminina,L1,roupas,acessórios,moda feminina,fashion,tendência,coleção,calças,bermudas,camisas,camisetas,saias,vestidos,cintos,shorts".split(","));
			}}, 
			"sapato,vestido,grife,bolsas,perfumes".split(","), 
			new int[]{16, 4, 2}
			), new UserModel(
			"Doceira",
			new HashMap<Long, String[]>() {{
				put(Long.valueOf(11l), "alimentacao,chocolates,cacau,sorvetes,cafés,chas,doces,chá,G1".split(","));
				put(Long.valueOf(27l), "departamento,L1,eletrônicos,doces,eletrodomésticos,acessórios,brinquedos,lar,americanas,lojas americanas,jogos".split(","));
				put(Long.valueOf(31l), "alimentacao,L1,Frozen Yogurt,iogurte,sorvetes,lanches,fast food,Yoggis,smoothies,blends,toppings,açaí".split(","));
				put(Long.valueOf(108l), "alimentacao,L1,chá,café,doceria,sorveteria".split(","));
			}}, 
			"doces,sorveteria,chocolates,shakes".split(","), 
			new int[]{6}
			), new UserModel("Mãe de bebê",new HashMap<Long, String[]>() {{put(Long.valueOf(17l), "G1,infantil,crianças,parques,diversão,diversões,brinquedos,pula-pula,diversão,parquinhos".split(","));put(Long.valueOf(221l), "L2,camisas,blusas,shorts,sapatos,bones,meias,calca,bermudas,camisetas,blusao,conjuntos,jaquetas,coletes,chinelo,sunga,regata".split(","));put(Long.valueOf(80l), "L1,roupas,acessórios,crianças,moda,look,roupas infantis,body,camisas,camisetas,blusas,meias,legging,mochilas,jaquetas,bolsas,casacos,calças,jardineiras,jumper".split(","));put(Long.valueOf(261l), "L2,roupas,moda,moda infantil,teen,camisa,short,bermuda,jaqueta,saia,casaco,calça,blusa,crianças,moda,coleção,camisas,shorts,blusas,bermudas,vestidos,saias,sapatos,bones,meias,calca,bermudas,camisetas,blusao,conjuntos,jaquetas,coletes,chinelo,sunga,regata".split(","));}},"brinquedo,mamadeira".split(","),new int[]{18, 7}),
			new UserModel("Executivo",new HashMap<Long, String[]>() {{put(Long.valueOf(85l), "L1,bancos,serivços financeiros,finanças,financiamento,poupança,banco,caixa eletrônico,conta,investimento,saque,extrato,depósito".split(","));put(Long.valueOf(207l), "L2,livros,dvds,coleções,teatro,revistas,cds,eventos".split(","));put(Long.valueOf(21l), "G1,correios,correspondência,encomendas,sedex,cartas,entregas,pac,telegrama,nacional,internacional,mala direta,malotes".split(","));put(Long.valueOf(159l), "L1,óculos,lentes,armações,óculos de sol,lentes de grau".split(","));put(Long.valueOf(189l), "L1,passagem,passagens,hoteis,hotel,resorts,resort,cruzeiros,viagem,viagens,guias,guia,pacotes,pacotes de viagem,hotel,hoteis,destinos,lua de mel".split(","));put(Long.valueOf(227l), "L2,calcas,camisas,calças,camisetas,regatas,jaquetas,casacos,cintos,blazers,fio tinto,polos,tricots,paletos,paletós,calçados,sapatos,camisas softs,t-shirts,jeans,bermudas,pastas,mochilas,porta terno,mala,carteira".split(","));put(Long.valueOf(88l), "L1,bancos,serivços financeiros,finanças,financiamento,poupança,banco,pagamentos,saque,depósito,extrato".split(","));put(Long.valueOf(165l), "L1,camisas,poló,terno,gravata,cinto,calça,grife,moda".split(","));put(Long.valueOf(105l), "L1,calçados,bolsas,acessórios,cintos,sapatos,sapatenis,pasta".split(","));put(Long.valueOf(224l), "L2,calcas,bermudas,shorts,blusas,camisas,jaquetas,blazers,vestidos".split(","));}},"paleto,sapato".split(","),new int[]{9,8,10,20,1}),
			new UserModel("Jovem",new HashMap<Long, String[]>() {{put(Long.valueOf(42l), "L1,jogos,games,tecnologia,videogames,tech,tecnologia,eletrônicos,desenvolvedores,consoles".split(","));put(Long.valueOf(207l), "L2,livros,dvds,coleções,teatro,revistas,cds,eventos".split(","));put(Long.valueOf(408l), "L3,jogos,video game,games,diversão,brincar,jogar,festas,sinuca,aniversário".split(","));put(Long.valueOf(409l), "L3,Cinema,filmes,estréias,pipoca,lançamentos".split(","));put(Long.valueOf(406l), "L3,hamburger,lanches,comida,fast food,big king,bk cheddar duplo,cheeseburguer,refrigerante,refil,whopper".split(","));put(Long.valueOf(120l), "L1,surf,bermudas,roupas,pranchas,esporte,skate,parafina,camisas,casacos".split(","));put(Long.valueOf(211l), "L2,roupas,biquinis,sungas,boardshorts,carteiras,bones,bermudas,short,camiseta,calças,sungas,moletons,jaquetas,regatas,underwear,cintos,mochilas,malas,saias,vestidos,macacões".split(","));}},"hamburguer,game".split(","),new int[]{7,5,8,19,6}),
			new UserModel("Dona de Casa",new HashMap<Long, String[]>() {{put(Long.valueOf(171l), "L1,eletroeletrônicos,eletrodomésticos,móveis,câmeras,cama,mesa,banho".split(","));put(Long.valueOf(78l), "L1,cozinha,panelas,talheres,forno,canecas,adega,taça,cooler,fôrmas,conjuntos,noblesse,caçarola,saladeiras,churrasqueira,amaçiador,talheres,colheres,garfos,facas,bistequeira,adaptador,cooktop,coifa,forno elétrico,umidificadores,termoventiladores,torradeira,máquina de café,aquecedor,travessas,bandeijas,frigideiras,panelas,conjuntos,patisserie,espatula,decoradores,tampa,moedores,saleiros".split(","));put(Long.valueOf(262l), "L2,utilidades para o lar,brinquedos,tecnologia,doces,acessórios,papelaria".split(","));put(Long.valueOf(6l), "ferramentas,acessorios,peças,chave de fenda,lanterna,lar,casa,utilidades,acessórios,informática,ferragens,hidráulica,elétrica,encanamento,G1,alicates,chaves,philips,fenda,estrela,martelo,torx,allen,roberston,posidrive,talheres,colher,faca,garfo,estojo,kits".split(","));put(Long.valueOf(321l), "L2,cama,mesa,banho,artigos para o lar,casa,toalhas,edredom,jogo de cama,cobertores,roupões,roupão,colchas,tapetes,piso,rosto,sabonetes,pano de copa,guardanapo,travesseiro,fronha,lençol,protetor,jogos de lençol,fronhas,duvets,mantas,almofadas,travesseiros,toalhas de mesa,jogos americanos,toalhas de banho,toalhas de rosto,lavabo,praia,pisos,tapetes,roupões,poltronas,mesas,aparador,rack,tapetes,bancos,estante,talher,talheres,luminaria,arara,toalha,abajur".split(","));}},"cama,panela".split(","),new int[]{12,24,21}),
			new UserModel("Japonês dos eletronicos",new HashMap<Long, String[]>() {{put(Long.valueOf(42l), "L1,jogos,games,tecnologia,videogames,tech,tecnologia,eletrônicos,desenvolvedores,consoles".split(","));put(Long.valueOf(93l), "L1,eletrônicos,eletrodomésticos,tecnologia,celulares,notebooks,câmeras,tv,Home Theater".split(","));put(Long.valueOf(386l), "L3,sashimi,temaki,saladas,sushis,yakissobas,yakissoba,rolinho primavera,arroz,frango,frango xadrez".split(","));put(Long.valueOf(358l), "L3,arroz,comida chinesa,sushi,temaki,almoço,yakissoba,carne acebolada,shake couve,uramaki shake gril,teriyaki,shoyo".split(","));}},"yakissoba,tablet".split(","),new int[]{6,5}),
			new UserModel("Japonês dos eletronicos hipocondriaco",new HashMap<Long, String[]>() {{put(Long.valueOf(42l), "L1,jogos,games,tecnologia,videogames,tech,tecnologia,eletrônicos,desenvolvedores,consoles".split(","));put(Long.valueOf(93l), "L1,eletrônicos,eletrodomésticos,tecnologia,celulares,notebooks,câmeras,tv,Home Theater".split(","));put(Long.valueOf(1l), "saúde,corpo,saudavel,postura,massageador,produtos médico-hospitalar,médico,G1".split(","));put(Long.valueOf(386l), "L3,sashimi,temaki,saladas,sushis,yakissobas,yakissoba,rolinho primavera,arroz,frango,frango xadrez".split(","));put(Long.valueOf(358l), "L3,arroz,comida chinesa,sushi,temaki,almoço,yakissoba,carne acebolada,shake couve,uramaki shake gril,teriyaki,shoyo".split(","));put(Long.valueOf(101l), "L1,remédios,medicamentos,saúde,farmácias".split(","));}},"yakissoba,tablet,remédios,exame,dentista".split(","),new int[]{6,21,5,13,13}),
			
	};
	
	private static final long[][] mShouldRecommend = new long[][] { 
			new long[] { 16l, 209l, 325l, 116l, 138l, 65l, 200l, 97l, 120l, 194l, 101l, 107l },
			new long[] { 209l, 325l, 16l, 116l, 412l, 65l, 398l, 138l, 200l, 198l, 120l, 194l },
			new long[] { 69l, 148l, 163l, 140l, 186l, 283l, 319l, 195l, 341l, 64l, 168l, 143l },
			new long[] { 150l, 67l, 375l, 109l, 378l, 188l, 380l, 15l, 172l, 198l, 279l, 153l },
			new long[] { 267l, 322l, 46l, 350l, 408l, 409l, 52l, 73l, 44l, 50l, 47l, 38l },
			new long[] { 244l, 70l, 86l, 334l, 121l, 287l, 331l, 103l, 177l, 274l, 178l, 68l },
			new long[] { 399l, 403l, 65l, 368l, 334l, 223l, 385l, 376l, 271l, 20l, 30l, 93l },
			new long[] { 328l, 114l, 49l, 238l, 45l, 25l, 74l, 213l, 197l, 34l, 89l, 149l },
			new long[] { 404l, 394l, 104l, 30l, 206l, 361l, 179l, 90l, 28l, 334l, 125l, 134l },
			new long[] { 404l, 394l, 104l, 30l, 129l, 361l, 137l, 90l, 4l, 334l, 125l, 3l },
			
			};
	
	public static UserModel getEvaluationModel(){
		if(mIndex < 0 || mIndex >= getEvaluationDataSize())
			return null;
		return mUserModels[mIndex];
	}
	
	public static long[] getEvaluationShouldRecommendStores(){
		if(mIndex < 0 || mIndex >= getEvaluationDataSize())
			return null;
		return mShouldRecommend[mIndex];
	}
	
	public static int getEvaluationDataSize(){
		return mUserModels.length;
	}
}
