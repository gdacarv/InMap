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
			)};
	
	private static final long[][] mShouldRecommendEsportista = new long[][] { 
			new long[] { 16l, 209l, 325l, 116l, 138l, 65l, 200l, 97l, 120l, 194l, 101l, 107l },
			new long[] { 209l, 325l, 16l, 116l, 412l, 65l, 398l, 138l, 200l, 198l, 120l, 194l },
			new long[] { 69l, 148l, 163l, 140l, 186l, 283l, 319l, 195l, 341l, 64l, 168l, 143l } 
			};
	
	public static UserModel getEvaluationModel(){
		return mUserModels[mIndex];
	}
	
	public static long[] getEvaluationShouldRecommendStores(){
		return mShouldRecommendEsportista[mIndex];
	}
	
	public static int getEvaluationDataSize(){
		return mUserModels.length;
	}
}
