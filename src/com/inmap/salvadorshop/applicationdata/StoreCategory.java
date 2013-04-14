package com.inmap.salvadorshop.applicationdata;

import com.inmap.salvadorshop.R;

public enum StoreCategory {
	CLOTHING_MAN(1, R.string.title_clothing_man, R.drawable.pin_cat_moda_masculina, R.drawable.img_cat_moda_masculina, 0xff000131),
	CLOTHING_WOMAN(2, R.string.title_clothing_woman, R.drawable.pin_cat_moda_feminina, R.drawable.img_cat_moda_feminina, 0xffde0053),
	GIFTS(3, R.string.title_gifts, R.drawable.pin_cat_presentes, R.drawable.img_cat_presentes, 0xff8d157f),
	SHOES(4, R.string.title_shoes, R.drawable.pin_cat_sapatos, R.drawable.img_cat_sapatos, 0xff564a3e),
	ELETRONICS(5, R.string.title_eletronics, R.drawable.pin_cat_eletronicos, R.drawable.img_cat_eletronicos, 0xff155360),
	FOOD(6, R.string.title_food, R.drawable.pin_cat_alimentos, R.drawable.img_cat_alimentos, 0xffc30a12),
	ENTERTAINMENT(7, R.string.title_entertainment, R.drawable.pin_cat_entretenimento, R.drawable.img_cat_entretenimento, 0xffdf8200),
	BOOKSTORE(8, R.string.title_bookstore, R.drawable.pin_cat_livraria, R.drawable.img_cat_livraria, 0xff0a8894),
	BANK(9, R.string.title_bank, R.drawable.pin_cat_banco, R.drawable.img_cat_banco, 0xff00913e),
	SERVICES(10, R.string.title_services, R.drawable.pin_cat_servicos, R.drawable.img_cat_servicos, 0xff959595),
	CARRIER(11, R.string.title_carrier, R.drawable.pin_cat_operadoras_telefonicas, R.drawable.img_cat_operadoras_telefonicas, 0xff006f9d), 
	DEPARTMENT(12, R.string.title_department, R.drawable.pin_cat_lojas_departamentos, R.drawable.img_cat_lojas_departamentos, 0xff7a521e), 
	HEALTH(13, R.string.title_health, R.drawable.pin_cat_saude, R.drawable.img_cat_saude, 0xffa01211), 
	UNISSEX(14, R.string.title_unissex, R.drawable.pin_cat_unisex, R.drawable.img_cat_unisex, 0xff760229), 
	UNDERWEAR(15, R.string.title_underwear, R.drawable.pin_cat_moda_intima, R.drawable.img_cat_moda_intima, 0xffe77bad), 
	COSMETIC(16, R.string.title_cosmetic, R.drawable.pin_cat_cosmeticos, R.drawable.img_cat_cosmeticos, 0xff8a66a4), 
	SPORTS(17, R.string.title_sports, R.drawable.pin_cat_artigos_esportivos, R.drawable.img_cat_artigos_esportivos, 0xff016b2b), 
	BABY(18, R.string.title_baby, R.drawable.pin_cat_artigos_infantis, R.drawable.img_cat_artigos_infantis, 0xff55c686), 
	STREET(19, R.string.title_street, R.drawable.pin_cat_moda_street, R.drawable.img_cat_moda_street, 0xff353c2c), 
	OPTICS(20, R.string.title_optics, R.drawable.pin_cat_otica, R.drawable.img_cat_otica, 0xfff6b65d), 
	BEDBATHTABLE(21, R.string.title_bedbathtable, R.drawable.pin_cat_cama_mesa, R.drawable.img_cat_cama_mesa, 0xff8dcacb), 
	JEWELRY(22, R.string.title_jewelry, R.drawable.pin_cat_joalheria, R.drawable.img_cat_joalheria, 0xff910280), 
	ACCESSORIES(23, R.string.title_accessories, R.drawable.pin_cat_bijouteria, R.drawable.img_cat_bijouteria, 0xff846427), 
	HOME(24, R.string.title_home, R.drawable.pin_cat_utilidades_casa, R.drawable.img_cat_utilidades_casa, 0xff663333);
	
	private int id, titleRes, mapIconRes, menuIconRes, menuColor;
	
	public static final int length = values().length; 

	private StoreCategory(int id, int title_res, int mapIconRes, int menuIconRes, int menuColor){
		this.id = id;
		this.titleRes = title_res;
		this.mapIconRes = mapIconRes;
		this.menuIconRes = menuIconRes;
		this.menuColor = menuColor;
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
}
