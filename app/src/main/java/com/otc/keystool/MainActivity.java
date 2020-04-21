package com.otc.keystool;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.textfield.TextInputEditText;
import com.otc.keystool.pax.app.IConvert;
import com.otc.keystool.pax.app.TradeApplication;
import com.otc.keystool.pax.trade.Device;
import com.pax.dal.entity.EPedType;
import com.pax.dal.exceptions.PedDevException;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.et_tlk_key)
    TextInputEditText etTlkKey;
    @BindView(R.id.btn_tlk_validate)
    Button btnTlkValidate;
    @BindView(R.id.btn_tlk_inject)
    Button btnTlkInject;
    @BindView(R.id.tv_tmk_slot)
    TextView tvTmkSlot;
    @BindView(R.id.et_tmk_key)
    TextInputEditText etTmkKey;
    @BindView(R.id.btn_tmk_validate)
    Button btnTmkValidate;
    @BindView(R.id.btn_tmk_inject)
    Button btnTmkInject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        etTlkKey.setText("F4F710AE16B5C1EF1985512616FE6432867FFD23E99408AD");

        etTlkKey.addTextChangedListener(new KeyFormatWatcher(etTlkKey));

        etTmkKey.setText("811164B0BB4126C1EE75377DE9FE5F0B");

        etTmkKey.addTextChangedListener(new KeyFormatWatcher(etTmkKey));

        tvTmkSlot.setOnClickListener(view -> {
            showMenuPopupSlot(view);
        });

        btnTlkInject.setOnClickListener(view ->{
            writeTlk();
        });

        btnTlkValidate.setOnClickListener(view ->{
            validateTlk();
        });

        btnTmkInject.setOnClickListener(view ->{
            writeTmk();
        });

        btnTmkValidate.setOnClickListener(view -> {
            validateTmk();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clean, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_menu_trash) {

            Log.i(TAG, "CLEAN KEYS ------------------------------------------------------------");
            cleanKeys();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void validateTlk() {
        String result;
        try{
            result =  TradeApplication.getConvert().bcdToStr( Device.getKCV_TLK());
            FancyToast.makeText(this, result, FancyToast.LENGTH_LONG, FancyToast.INFO,false).show();
        }
        catch (Exception ex){
            Log.e(TAG, "keyValidateTlk: ", ex);
            result = "Vacío";
            FancyToast.makeText(this, result, FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
        }
    }

    private void validateTmk() {
        String result;
        try{
            String temp = tvTmkSlot.getText().toString().split(":")[1].trim();
            int slotTmk = Integer.parseInt(temp);

            result =  TradeApplication.getConvert().bcdToStr( Device.getKCV_TMK((byte)slotTmk));
            FancyToast.makeText(this, result, FancyToast.LENGTH_LONG, FancyToast.INFO,false).show();
        }
        catch (Exception ex){
            Log.e(TAG, "validateTmk: ", ex);
            result = "Vacío";
            FancyToast.makeText(this, result, FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
        }
    }

    private void writeTlk() {

        String TLK = etTlkKey.getText().toString().trim();

        TLK = TLK.replaceAll("(\n|\r)", "");

        if (TLK.equals("") || TLK.length() != 48) {
            etTlkKey.setError("Ingresa una llave válida");
            etTlkKey.requestFocus();
            return;
        }

        Log.i(TAG, "writeTlk: " + TLK);

        byte[] bytesTLK = TradeApplication
                .getConvert()
                .strToBcd(TLK, IConvert.EPaddingPosition.PADDING_LEFT);

        if (Device.writeTLK(bytesTLK)) {
            FancyToast.makeText(MainActivity.this,"Success",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
        }else{
            FancyToast.makeText(this, "ERROR", FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
        }
    }

    private void writeTmk() {

        String temp = tvTmkSlot.getText().toString().split(":")[1].trim();
        int slotTmk = Integer.parseInt(temp);

        String TMK = etTmkKey.getText().toString().trim();

        TMK = TMK.replaceAll("(\n|\r)", "");

        if (TMK.equals("") || TMK.length() != 32) {
            etTmkKey.setError("Ingresa una llave válida");
            etTmkKey.requestFocus();
            return;
        }

        Log.i(TAG, "writeTmk: " + TMK);

        byte[] bytesTMK = TradeApplication
                .getConvert()
                .strToBcd(TMK, IConvert.EPaddingPosition.PADDING_LEFT);

        if (Device.writeTMK2(slotTmk, bytesTMK)) {
            FancyToast.makeText(MainActivity.this,"Success",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
        }else{
            FancyToast.makeText(this, "ERROR", FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
        }
    }


    public class KeyFormatWatcher implements TextWatcher {

        // Change this to what you want... ' ', '-' etc..
        private final String space = " ";
        EditText et_filed;


        public KeyFormatWatcher(EditText et_filed) {
            this.et_filed = et_filed;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String initial = s.toString();
            // remove all non-digits characters
            String processed = initial.replaceAll("\\W", "");
            processed = processed.toUpperCase();

            if (processed.length() > 8) {
                //processed = processed.replaceAll("(\\d{8})(?=\\d)", "$1 ");
                processed = processed.replaceAll("(\\w{8})(?=\\w)", "$1\n");
            }

            //Remove the listener
            et_filed.removeTextChangedListener(this);

            //Assign processed text
            et_filed.setText(processed);

            try {
                et_filed.setSelection(processed.length());
            } catch (Exception e) {
                // TODO: handle exception
            }

            //Give back the listener
            et_filed.addTextChangedListener(this);
        }
    }

    public void showMenuPopupSlot(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        popup.setOnMenuItemClickListener(item -> {

            String menuSlot = item.getTitle().toString().split(":")[1];
            int slotTmk = Integer.parseInt(menuSlot.trim());

            tvTmkSlot.setText(MessageFormat.format("SLOT : {0}", slotTmk));

            return false;
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_slot, popup.getMenu());
        popup.show();
    }

    public  boolean cleanKeys() {
        try {
            TradeApplication.getDal().getPed(EPedType.INTERNAL).erase();
            FancyToast.makeText(MainActivity.this,"Success",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
            return true;
        } catch (PedDevException e) {
            FancyToast.makeText(this, e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
            Log.w(TAG, e);
        }
        return false;
    }


}
