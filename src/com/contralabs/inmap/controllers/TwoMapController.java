package com.contralabs.inmap.controllers;

import com.contralabs.inmap.interfaces.MapController;
import com.contralabs.inmap.interfaces.MapItem;
import com.contralabs.inmap.interfaces.MapItemsListener;

public class TwoMapController implements MapController {

	private MapController aMapController, bMapController;

	public static MapController createInstance(MapController aMapController, MapController bMapController) {
		return aMapController != null ? bMapController != null ? new TwoMapController(aMapController, bMapController)
				: aMapController
				: bMapController != null ? bMapController
				: null;
			
	}
	
	private TwoMapController(MapController aMapController,
			MapController bMapController) {
		this.aMapController = aMapController;
		this.bMapController = bMapController;
	}

	@Override
	public MapItem[] getMapItems() {
		MapItem[] aMapItens = aMapController.getMapItems(),
			bMapItens = bMapController.getMapItems();
		
		if(aMapItens == null){
			if(bMapItens == null)
				return null;
			return bMapItens;
		}
		if(bMapItens == null)
			return aMapItens;
		return concat(aMapItens, bMapItens);
	}

	@Override
	public void setMapItemsListener(MapItemsListener listener) {
		aMapController.setMapItemsListener(listener);
		bMapController.setMapItemsListener(listener);
	}

	private MapItem[] concat(MapItem[] A, MapItem[] B) {
		MapItem[] C= new MapItem[A.length+B.length];
		System.arraycopy(A, 0, C, 0, A.length);
		System.arraycopy(B, 0, C, A.length, B.length);

		return C;
	}
}
