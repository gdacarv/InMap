package com.contralabs.inmap.salvadorshop.applicationdata;

import com.contralabs.inmap.R;

public enum InfrastructureCategory {
	EXIT(1, R.drawable.ic_infra_entrada, R.drawable.pin_infra_entrada),
	PARKING(2, R.drawable.ic_infra_estacionamento, R.drawable.pin_infra_estacionamento),
	STAIRS(3, R.drawable.ic_infra_escada, R.drawable.pin_infra_escada), 
	ELEVATOR(4, R.drawable.ic_infra_elevador, R.drawable.pin_infra_elevador), 
	TOILET(5, R.drawable.ic_infra_banheiro, R.drawable.pin_infra_banheiro), 
	ESCALATOR(6, R.drawable.ic_infra_escada_rolante, R.drawable.pin_infra_escada_rolante), 
	PHONES(7, R.drawable.ic_infra_telefone, R.drawable.pin_infra_telefone), 
	BABY_FACILITIES(8, R.drawable.ic_infra_fraldario, R.drawable.pin_infra_fraldario),
	TAXI(9, R.drawable.ic_infra_taxi, R.drawable.pin_infra_taxi),
	INFO(10, R.drawable.ic_infra_info, R.drawable.pin_infra_informacoes);
	
	private int id, menuIconRes, mapIconRes;

	private InfrastructureCategory(int id, int menuIconRes, int mapIconRes){
		this.id = id;
		this.menuIconRes = menuIconRes;
		this.mapIconRes = mapIconRes;
	}

	public int getId() {
		return id;
	}

	public int getMenuIconResId() {
		return menuIconRes;
	}

	public int getMapIconResId() {
		return mapIconRes;
	}

}
