package com.example.icoper.testappjob;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by icoper on 17.02.17.
 */

public class EnterPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String COUNTRY_CODE_UA = "Украина : +38 0ХХ ХХХ ХХ ХХ";
    private static final String COUNTRY_CODE_USA = "Россия : +7 ХХХ ХХХ ХХХ ХХ";
    private static final String COUNTRY_CODE_RU = "США : +1 XXX XXX XXXX";
    private static final String ALERT_DIALOG_COUNTRY_TITLE = "Укажите код страны";

    private static final Integer PHONE_LENGTH = 13;

    private RecyclerView mRecyclerView;
    public LinearLayoutManager mLinearLayoutManager;

    private EditText editNumber;
    private EditText editCode;
    private Button showTipBtn;
    private Button getCodeBtn;
    private Button confirmBtn;
    private TextView userCountryCode;

    public String userCodeType;
    private ArrayList<String> countryCodeList;

    private static Context context = EnterPhoneActivity.getContext();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_phone_activity);

        // this metod is setup all view in EnterPhoneActivity
        setupView();

    }

    // setup all view in EnterPhoneActivity
    private void setupView() {

        editNumber = (EditText) findViewById(R.id.ep_edit_phone_et);
        editCode = (EditText) findViewById(R.id.ep_edit_code_et);
        showTipBtn = (Button) findViewById(R.id.ep_ask_btn);
        getCodeBtn = (Button) findViewById(R.id.ep_get_code_btn);
        confirmBtn = (Button) findViewById(R.id.ep_done_bt);
        userCountryCode = (TextView) findViewById(R.id.ep_userCountry_tv);

        showTipBtn.setOnClickListener(this);
        getCodeBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        editNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == PHONE_LENGTH) {
                    getCodeBtn.setEnabled(true);
                } else getCodeBtn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // do nothing
            }
        });

        editCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    confirmBtn.setEnabled(true);
                } else confirmBtn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // do nothing
            }
        });
    }

    /**
     * @param v it's id selected item
     *          ep_ask_btn - call metod showCounrtyCode() & show list of country type number
     *          //TODO
     */
    @Override
    public void onClick(View v) {
        int itemId = v.getId();

        switch (itemId) {
            case R.id.ep_ask_btn:
                showCountryCode();
                break;
            case R.id.ep_done_bt:
                // launch MapsActivity if all ok
                startActivity(new Intent(EnterPhoneActivity.this, MapsActivity.class));
                break;
            case R.id.ep_get_code_btn:
                showInfoDialog();
                getUserCode(editNumber.getText().toString());
                break;
        }
    }

    private void showInfoDialog() {

        AlertDialog.Builder builder =
                new AlertDialog
                        .Builder(new ContextThemeWrapper(EnterPhoneActivity.this, R.style.myDialog));

        LayoutInflater layoutInflater = LayoutInflater.from(EnterPhoneActivity.this);
        View view = layoutInflater.inflate(R.layout.dialog_info, null);
        TextView info = (TextView) view.findViewById(R.id.di_info_tv);

        info.setText(R.string.ad_info_text);
        builder.setView(view);
        builder.setNegativeButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // customize dialog theme
        AlertDialog alertDialog = builder.create();
        Drawable d = new ColorDrawable(Color.GRAY);
        d.setAlpha(200);
        alertDialog.getWindow().setBackgroundDrawable(d);

        alertDialog.show();
    }

    /**
     * @param number it's user input phone number on editNumber EditText
     */
    private void getUserCode(String number) {
        editCode.setEnabled(true);
        startService(new Intent(this, NetworkIntentService.class).putExtra("phone", number));
    }

    // show alert dialog case country code from phone number
    private void showCountryCode() {

        countryCodeList = new ArrayList<>();

        countryCodeList.add(COUNTRY_CODE_RU);
        countryCodeList.add(COUNTRY_CODE_UA);
        countryCodeList.add(COUNTRY_CODE_USA);

        LayoutInflater layoutInflater = LayoutInflater.from(EnterPhoneActivity.this);
        View view = layoutInflater.inflate(R.layout.dialog_country_code, null);

        AlertDialog.Builder builder = new AlertDialog
                .Builder(new ContextThemeWrapper(EnterPhoneActivity.this, R.style.myDialog));


        mRecyclerView = (RecyclerView) view.findViewById(R.id.ad_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(builder.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        RVAdapter adapter = new RVAdapter();
        mRecyclerView.setAdapter(adapter);

        builder.setView(view);

        // customize dialog theme
        AlertDialog alertDialog = builder.create();
        Drawable d = new ColorDrawable(Color.GRAY);
        d.setAlpha(200);
        alertDialog.getWindow().setBackgroundDrawable(d);
        alertDialog.show();
    }

    // set user country code to editText
    private void attemptUserCode(String type) {
        if (type.equals(COUNTRY_CODE_RU)) {
            userCodeType = "+7";
        } else if (type.equals(COUNTRY_CODE_UA)) {
            userCodeType = "+380";
        } else if (type.equals(COUNTRY_CODE_USA)) {
            userCodeType = "+1";
        }
        editNumber.setText(userCodeType);
    }

    // Use my custom adapter from CountryCode list in AlertDialog
    public class RVAdapter extends RecyclerView.Adapter<EnterPhoneActivity.CountryListViewHolder> {

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public CountryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.country_code_item, parent, false);
            CountryListViewHolder listViewHolder = new CountryListViewHolder(v);
            return listViewHolder;
        }

        @Override
        public void onBindViewHolder(CountryListViewHolder holder, final int position) {
            if (!countryCodeList.isEmpty()) {
                holder.codeType.setText(countryCodeList.get(position));
                holder.codeType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userCountryCode.setText(countryCodeList.get(position));
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return countryCodeList.size();
        }
    }


    public static class CountryListViewHolder extends RecyclerView.ViewHolder {

        public TextView codeType;

        public CountryListViewHolder(View itemView) {
            super(itemView);
            codeType = (TextView) itemView.findViewById(R.id.dc_country_code);
        }
    }

    // TODO

    public static Context getContext() {
        return context;
    }
}
