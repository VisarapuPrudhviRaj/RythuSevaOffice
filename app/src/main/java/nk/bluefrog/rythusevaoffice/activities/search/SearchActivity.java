package nk.bluefrog.rythusevaoffice.activities.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Locale;

import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.SearchRVAdapterListener {

    RecyclerView rvFilterList;
    ImageView voice_icon, iv_clear;
    EditText et_voicetext;
    SearchAdapter searchAdapter;
    DBHelper dbHelper;
    String mobilePattern = "[0-9 ]{10}";
    private int REQ_CODE_SPEECH_INPUT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        dbHelper = new DBHelper(this);

        findViews();
    }

    private void findViews() {
        rvFilterList = findViewById(R.id.rvFilterList);
        voice_icon = findViewById(R.id.voice_icon);
        iv_clear = findViewById(R.id.iv_clear);
        et_voicetext = findViewById(R.id.et_voicetext);

        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_voicetext.setText("");


            }
        });

        voice_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        et_voicetext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    iv_clear.setVisibility(View.VISIBLE);
                    voice_icon.setVisibility(View.VISIBLE);
                } else {
                    iv_clear.setVisibility(View.GONE);
                    voice_icon.setVisibility(View.VISIBLE);
                }
                searchAdapter.getFilter().filter(s);
                if (searchAdapter.getItemCount() > 0)
                    rvFilterList.smoothScrollToPosition(0);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchAdapter = new SearchAdapter(this, dbHelper.getSearchResults(this), this, ((LinearLayout) findViewById(R.id.tv_nodata)));
        rvFilterList.setLayoutManager(new RecyclerViewNoBugLinearLayoutManager(this));
        rvFilterList.setHasFixedSize(true);
        rvFilterList.setItemAnimator(new DefaultItemAnimator());
        rvFilterList.setAdapter(searchAdapter);


        if (getIntent() != null && getIntent().getExtras() != null) {
            String voiceWord = getIntent().getStringExtra("voiceWord").trim();

            if (voiceWord.matches(mobilePattern)) {
                et_voicetext.setText(voiceWord.replace(" ", ""));
            } else {
                et_voicetext.setText(voiceWord);
            }
            //et_voicetext.setText(voiceWord.replace(" ",""));
        }


    }

    private void startVoiceInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.hello_officer_how_can_i_help_you));
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {

            if (resultCode == RESULT_OK) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (result.get(0).matches(mobilePattern)) {
                    et_voicetext.setText(result.get(0).trim().replace(" ", ""));
                } else {
                    et_voicetext.setText(result.get(0));
                }

            } else {
                et_voicetext.setText("");
            }
        }
    }

    @Override
    public void onSelected(SearchModel contact) {
        if (Helper.isNetworkAvailable(SearchActivity.this)) {
            startActivity(contact.getIntent());
        } else {
            Helper.AlertMesg(SearchActivity.this, getString(R.string.no_internet_available));
        }
    }

    public class RecyclerViewNoBugLinearLayoutManager extends LinearLayoutManager {
        public RecyclerViewNoBugLinearLayoutManager(Context context) {
            super(context);
        }

        public RecyclerViewNoBugLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public RecyclerViewNoBugLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                //try catch
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }
    }
}
