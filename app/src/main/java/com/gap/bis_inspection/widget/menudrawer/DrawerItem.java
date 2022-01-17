package com.gap.bis_inspection.widget.menudrawer;


public class DrawerItem {

	int ItemName;
	int imgResID;

	public DrawerItem(int itemName, int imgResID) {
		ItemName = itemName;
		this.imgResID = imgResID;
	}



	public int getItemName() {
		return ItemName;
	}

	public void setItemName(int itemName) {
		ItemName = itemName;
	}

	public int getImgResID() {
		return imgResID;
	}

	public void setImgResID(int imgResID) {
		this.imgResID = imgResID;
	}

}
