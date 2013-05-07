package com.contralabs.inmap.salvadorshop.applicationdata;

import com.contralabs.inmap.R;

public enum StoreCategory {
	CLOTHING_MAN(1, R.string.title_clothing_man, R.drawable.pin_cat_moda_masculina, R.drawable.img_cat_moda_masculina, 0xff000131, R.drawable.pin_cat_moda_masculina_all),
	CLOTHING_WOMAN(2, R.string.title_clothing_woman, R.drawable.pin_cat_moda_feminina, R.drawable.img_cat_moda_feminina, 0xffde0053, R.drawable.pin_cat_moda_feminina_all),
	GIFTS(3, R.string.title_gifts, R.drawable.pin_cat_presente, R.drawable.img_cat_presentes, 0xff8d157f, R.drawable.pin_cat_presentes_all),
	SHOES(4, R.string.title_shoes, R.drawable.pin_cat_sapatos, R.drawable.img_cat_sapatos, 0xff564a3e, R.drawable.pin_cat_sapatos_all),
	ELETRONICS(5, R.string.title_eletronics, R.drawable.pin_cat_eletronicos, R.drawable.img_cat_eletronicos, 0xff155360, R.drawable.pin_cat_eletronicos_all),
	FOOD(6, R.string.title_food, R.drawable.pin_cat_alimentos, R.drawable.img_cat_alimentos, 0xffc30a12, R.drawable.pin_cat_alimentos_all),
	ENTERTAINMENT(7, R.string.title_entertainment, R.drawable.pin_cat_entretenimento, R.drawable.img_cat_entretenimento, 0xffdf8200, R.drawable.pin_cat_entretenimento_all),
	BOOKSTORE(8, R.string.title_bookstore, R.drawable.pin_cat_livraria, R.drawable.img_cat_livraria, 0xff0a8894, R.drawable.pin_cat_livraria_all),
	BANK(9, R.string.title_bank, R.drawable.pin_cat_banco, R.drawable.img_cat_banco, 0xff00913e, R.drawable.pin_cat_banco_all),
	SERVICES(10, R.string.title_services, R.drawable.pin_cat_servicos, R.drawable.img_cat_servicos, 0xff959595, R.drawable.pin_cat_servicos_all),
	CARRIER(11, R.string.title_carrier, R.drawable.pin_cat_operadoras_telefonicas, R.drawable.img_cat_operadoras_telefonicas, 0xff006f9d, R.drawable.pin_cat_operadoras_telefonicas_all), 
	DEPARTMENT(12, R.string.title_department, R.drawable.pin_cat_lojas_departamentos, R.drawable.img_cat_lojas_departamentos, 0xff7a521e, R.drawable.pin_cat_loas_departamentos_all), 
	HEALTH(13, R.string.title_health, R.drawable.pin_cat_saude, R.drawable.img_cat_saude, 0xffa01211, R.drawable.pin_cat_saude_all), 
	UNISSEX(14, R.string.title_unissex, R.drawable.pin_cat_unisex, R.drawable.img_cat_unisex, 0xff760229, R.drawable.pin_cat_unisex_all), 
	UNDERWEAR(15, R.string.title_underwear, R.drawable.pin_cat_moda_intima, R.drawable.img_cat_moda_intima, 0xffe77bad, R.drawable.pin_cat_moda_intima_all), 
	COSMETIC(16, R.string.title_cosmetic, R.drawable.pin_cat_cosmeticos, R.drawable.img_cat_cosmeticos, 0xff8a66a4, R.drawable.pin_cat_cosmeticos_all), 
	SPORTS(17, R.string.title_sports, R.drawable.pin_cat_artigos_esportivos, R.drawable.img_cat_artigos_esportivos, 0xff016b2b, R.drawable.pin_cat_artigos_esportivos_all), 
	BABY(18, R.string.title_baby, R.drawable.pin_cat_artigos_infantis, R.drawable.img_cat_artigos_infantis, 0xff55c686, R.drawable.pin_cat_artigos_infantis_all), 
	STREET(19, R.string.title_street, R.drawable.pin_cat_moda_street, R.drawable.img_cat_moda_street, 0xff353c2c, R.drawable.pin_cat_moda_street_all), 
	OPTICS(20, R.string.title_optics, R.drawable.pin_cat_otica, R.drawable.img_cat_otica, 0xfff6b65d, R.drawable.pin_cat_otica_all), 
	BEDBATHTABLE(21, R.string.title_bedbathtable, R.drawable.pin_cat_cama_mesa, R.drawable.img_cat_cama_mesa, 0xff8dcacb, R.drawable.pin_cat_cama_mesa_all), 
	JEWELRY(22, R.string.title_jewelry, R.drawable.pin_cat_joalheria, R.drawable.img_cat_joalheria, 0xff910280, R.drawable.pin_cat_joalheria_all), 
	ACCESSORIES(23, R.string.title_accessories, R.drawable.pin_cat_bijouteria, R.drawable.img_cat_bijouteria, 0xff846427, R.drawable.pin_cat_bijouteria_all), 
	HOME(24, R.string.title_home, R.drawable.pin_cat_utilidades_casa, R.drawable.img_cat_utilidades_casa, 0xff663333, R.drawable.pin_cat_utilidades_casa_all), 
	EVENTS(25, R.string.title_events, R.drawable.pin_cat_eventos, R.drawable.img_cat_alimentos, 0xffc64832, R.drawable.pin_cat_eventos_all); // TODO Change img_cat
	
	private int id, titleRes, mapIconRes, menuIconRes, menuColor, btAllIconRes;
	
	public static final int length = values().length; 

	private StoreCategory(int id, int title_res, int mapIconRes, int menuIconRes, int menuColor, int btAllIconRes){
		this.id = id;
		this.titleRes = title_res;
		this.mapIconRes = mapIconRes;
		this.menuIconRes = menuIconRes;
		this.menuColor = menuColor;
		this.btAllIconRes = btAllIconRes;
	}
	
	public int getId() {
		return id;
	}

	public int getTitleRes() {
		return titleRes;
	}
	
	public int getMapIconResId() {
		return mapIconRes;
	}
	
	public int getMenuIconResId() {
		return menuIconRes;
	}
	
	public int getMenuColor() {
		return menuColor;
	}

	public static StoreCategory getStoreCategoryById(int id) {
		StoreCategory storeCategory = values()[id-1];
		if(storeCategory.getId() != id)
			throw new RuntimeException("getStoreCategoryById erro!");
		return storeCategory;
	}

	public int getBtAllIconResId() {
		return btAllIconRes;
	}
}
