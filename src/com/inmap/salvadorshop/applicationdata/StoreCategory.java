package com.inmap.salvadorshop.applicationdata;

import com.inmap.salvadorshop.R;

public enum StoreCategory {
	CLOTHING_MAN(1, R.string.title_clothing_man, 0, R.drawable.img_cat_moda_masculina, 0xff000131),
	CLOTHING_WOMAN(2, R.string.title_clothing_woman, 0, R.drawable.img_cat_moda_feminina, 0xffde0053),
	GIFTS(3, R.string.title_gifts, 0, R.drawable.img_cat_presentes, 0xff8d157f),
	SHOES(4, R.string.title_shoes, 0, R.drawable.img_cat_sapatos, 0xff564a3e),
	ELETRONICS(5, R.string.title_eletronics, 0, R.drawable.img_cat_eletronicos, 0xff155360),
	FOOD(6, R.string.title_food, 0, R.drawable.img_cat_alimentos, 0xffc30a12),
	ENTERTAINMENT(7, R.string.title_entertainment, 0, R.drawable.img_cat_entretenimento, 0xffdf8200),
	BOOKSTORE(8, R.string.title_bookstore, 0, R.drawable.img_cat_livraria, 0xff0a8894),
	BANK(9, R.string.title_bank, 0, R.drawable.img_cat_banco, 0xff00913e),
	SERVICES(10, R.string.title_services, 0, R.drawable.img_cat_servicos, 0xff959595),
	CARRIER(11, R.string.title_carrier, 0, R.drawable.img_cat_operadoras_telefonicas, 0xff006f9d), 
	DEPARTMENT(12, R.string.title_department, 0, R.drawable.img_cat_lojas_departamentos, 0xff7a521e), 
	HEALTH(13, R.string.title_health, 0, R.drawable.img_cat_saude, 0xffa01211), 
	UNISSEX(14, R.string.title_unissex, 0, 0, 0x0), 
	UNDERWEAR(15, R.string.title_underwear, 0, 0, 0x0), 
	COSMETIC(16, R.string.title_cosmetic, 0, 0, 0x0), 
	SPORTS(17, R.string.title_sports, 0, 0, 0x0), 
	BABY(18, R.string.title_baby, 0, 0, 0x0), 
	STREET(19, R.string.title_street, 0, 0, 0x0), 
	OPTICS(20, R.string.title_optics, 0, 0, 0x0), 
	BEDBATHTABLE(21, R.string.title_bedbathtable, 0, 0, 0x0), 
	JEWELRY(22, R.string.title_jewelry, 0, 0, 0x0), 
	ACCESSORIES(23, R.string.title_accessories, 0, 0, 0x0), 
	HOME(24, R.string.title_home, 0, 0, 0x0),
	UNCATEGORIZED(25, R.string.title_uncategorized, 0, 0, 0x0);
	
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
}
