package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;


import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private EditText mProductNameEditText;
    private EditText mPriceEditText;
    private TextView mQuantityEditText;

    private Spinner mSupplierSpinner;

    private EditText mPhoneEditText;

    private Uri mCurrentInventoryUri;

    private int mSupplier = InventoryEntry.SUPPLIER_UNKNOWN;

    private boolean mInventoryHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mInventoryHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        if (mCurrentInventoryUri == null) {

            setTitle(getString(R.string.add_a_product));
            invalidateOptionsMenu();

        } else {

            setTitle(getString(R.string.edit_a_product));
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        mProductNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mPhoneEditText = findViewById(R.id.edit_supplier_number);
        mSupplierSpinner = findViewById(R.id.spinner_supplier);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mSupplierSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    public void increment(View view) {
        String productQuantity = mQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(productQuantity);
        quantity = quantity + 1;
        displayQuantity(quantity);
    }

    public void decrement(View view) {
        String productQuantity = mQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(productQuantity);
        if (quantity == 1) {
            Toast.makeText(this, "You cannot have less than 1", Toast.LENGTH_SHORT).show();
            return;
        }
        if (quantity == 0) {
            Toast.makeText(this, "You cannot have minus amount of order", Toast.LENGTH_SHORT).show();
            return;
        }
        quantity = quantity - 1;
        displayQuantity(quantity);

    }

    private void displayQuantity(int quantity) {

        TextView quantityTextView = (TextView) findViewById(R.id.edit_product_quantity);
        quantityTextView.setText("" + quantity);

    }

    private void setupSpinner() {

        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_supplier_options, android.R.layout.simple_spinner_item);
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mSupplierSpinner.setAdapter(supplierSpinnerAdapter);

        mSupplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_one))) {
                        mSupplier = InventoryEntry.SUPPLIER_ONE;
                    } else if (selection.equals(getString(R.string.supplier_two))) {
                        mSupplier = InventoryEntry.SUPPLIER_TWO;
                    } else if (selection.equals(getString(R.string.supplier_three))) {
                        mSupplier = InventoryEntry.SUPPLIER_THREE;
                    } else {
                        mSupplier = InventoryEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplier = InventoryEntry.SUPPLIER_UNKNOWN;
            }
        });
    }

    private void saveInventory() {
        String productNameString = mProductNameEditText.getText().toString().trim();
        String productPriceString = mPriceEditText.getText().toString().trim();
        String supplierNumberString = mPhoneEditText.getText().toString().trim();
        String productQuantity = mQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(productQuantity);

        int phone = 0;
        if (!TextUtils.isEmpty(supplierNumberString)) {
            phone = Integer.parseInt(supplierNumberString);
        }

        if (mCurrentInventoryUri == null && TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(productPriceString)
                && TextUtils.isEmpty(supplierNumberString)
                && mSupplier == InventoryEntry.SUPPLIER_UNKNOWN) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPriceString);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, mSupplier);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NUMBER, phone);

        if (mCurrentInventoryUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Error saving the inventory", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Inventory saved with row id: " + newUri, Toast.LENGTH_SHORT).show();
            }

        } else {
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.error_updating_product), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.product_is_updated), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveInventory();
                finish();
                return true;
            case R.id.action_delete:
                showDeletedConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mInventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_NUMBER};

        return new CursorLoader(this,
                mCurrentInventoryUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NUMBER);

            String productName = cursor.getString(productNameColumnIndex);
            String productPrice = cursor.getString(productPriceColumnIndex);
            String quantity = cursor.getString(productQuantityColumnIndex);
            int supplier = cursor.getInt(supplierNameColumnIndex);
            String supplierNumber = cursor.getString(supplierNumberColumnIndex);

            mProductNameEditText.setText(productName);
            mPriceEditText.setText(productPrice);
            mQuantityEditText.setText(quantity);
            mPhoneEditText.setText(supplierNumber);

            switch (supplier) {
                case InventoryEntry.SUPPLIER_ONE:
                    mSupplierSpinner.setSelection(1);
                    break;
                case InventoryEntry.SUPPLIER_TWO:
                    mSupplierSpinner.setSelection(2);
                    break;
                case InventoryEntry.SUPPLIER_THREE:
                    mSupplierSpinner.setSelection(3);
                    break;
                case InventoryEntry.SUPPLIER_UNKNOWN:
                    mSupplierSpinner.setSelection(0);
                    break;
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mPhoneEditText.setText("");
        mSupplierSpinner.setSelection(0);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard_button_message, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing_button_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeletedConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteInventory();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * @Override public void onSaveInstanceState(Bundle savedInstanceState) {
     * savedInstanceState.putInt("Quantity", quantity);
     * super.onSaveInstanceState(savedInstanceState);
     * }
     * @Override public void onRestoreInstanceState(Bundle savedInstanceState) {
     * super.onRestoreInstanceState(savedInstanceState);
     * quantity = savedInstanceState.getInt("Quantity");
     * displayQuantity(quantity);
     * }
     **/

    private void deleteInventory() {
        if (mCurrentInventoryUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.error_delete_product), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.product_deleted), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}

