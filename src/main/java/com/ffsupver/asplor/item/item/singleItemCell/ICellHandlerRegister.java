package com.ffsupver.asplor.item.item.singleItemCell;

import appeng.api.storage.StorageCells;

public class ICellHandlerRegister {
    public static void register(){
        StorageCells.addCellHandler(new SingleItemCellItem.CellHandler());
    }
}
