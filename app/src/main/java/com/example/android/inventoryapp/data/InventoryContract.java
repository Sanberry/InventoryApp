package com.example.android.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.content.ContentResolver;


public class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";
    private InventoryContract() {
    }

    public static final class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_INVENTORY);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public final static String TABLE_NAME = "inventory";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier";
        public final static String COLUMN_SUPPLIER_NUMBER = "number";

        public static final int SUPPLIER_UNKNOWN = 0;
        public static final int SUPPLIER_ONE = 1;
        public static final int SUPPLIER_TWO = 2;
        public static final int SUPPLIER_THREE = 3;

        public static boolean isValidSupplier(int supplier) {
            if(supplier == SUPPLIER_UNKNOWN || supplier == SUPPLIER_ONE || supplier == SUPPLIER_TWO || supplier == SUPPLIER_THREE) {
                return true;
            }
            return false;
        }

    }
}
