package org.mercycorps.translationcards.activity.addDeck;


import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AutoCompleteTextView.OnDismissListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mercycorps.translationcards.MainApplication;
import org.mercycorps.translationcards.R;
import org.mercycorps.translationcards.data.Language;
import org.mercycorps.translationcards.service.LanguageService;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class EnterDeckSourceLanguageActivity extends AddDeckActivity {
    @Bind (R.id.deck_source_language_input) AutoCompleteTextView sourceLanguageInput;
    @Bind (R.id.deck_source_language_next_label) LinearLayout nextButton;
    @Bind(R.id.deck_source_language_next_text) TextView nextButtonText;
    @Bind(R.id.deck_source_language_next_image) ImageView nextButtonImage;
    @Bind(R.id.invalid_source_language_message) TextView invalidLanguageErrorView;
    private LanguageService languageService;

    @Override
    public void inflateView() {
        setContentView(R.layout.activity_deck_source_language);
    }

    @Override
    public void initStates() {
        languageService = ((MainApplication)getApplication()).getLanguageService();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                languageService.getLanguageNames()
        );

        sourceLanguageInput = (AutoCompleteTextView) findViewById(R.id.deck_source_language_input);
        sourceLanguageInput.setAdapter(adapter);
        fillSourceLanguageField();
        checkLanguageForError();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setOnDismissListener();
        } else {
            setTextWatcher();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setOnDismissListener() {
        OnDismissListener onDismissListener = new OnDismissListener() {
            @Override
            public void onDismiss() {
                checkLanguageForError();
            }
        };

        sourceLanguageInput.setOnDismissListener(onDismissListener);
    }

    private void setTextWatcher() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkLanguageForError();
            }
        };

        sourceLanguageInput.addTextChangedListener(textWatcher);
    }

    public void checkLanguageForError() {
        Integer viewVisibility = isSourceLanguageValid() ? View.GONE : View.VISIBLE;
        invalidLanguageErrorView.setVisibility(viewVisibility);
    }

    private void fillSourceLanguageField() {
        sourceLanguageInput.setText(languageService.getTitleCaseName(getContextFromIntent().getSourceLanguage()));
    }

    @Override
    public void setBitmapsForActivity() {
        setBitmap(R.id.deck_source_language_image, R.drawable.enter_phrase_image);
    }

    @OnClick(R.id.deck_source_language_next_label)
    protected void nextButtonClicked() {
        updateContextWithSourceLanguage();
        startNextActivity(this, EnterDeckDestinationLanguagesActivity.class);
    }

    @OnClick(R.id.deck_source_language_back_arrow)
    public void backButtonClicked() {
        updateContextWithSourceLanguage();
        startNextActivity(this, EnterDeckTitleActivity.class);
    }

    private void updateContextWithSourceLanguage() {
        Language language = Language.withName(sourceLanguageInput.getText().toString());
        getContextFromIntent().setSourceLanguage(language);
    }


    @OnTextChanged(R.id.deck_source_language_input)
    protected void deckSourceLanguageInputTextChanged() {
        nextButton.setClickable(isSourceLanguageValid());
        updateNextButtonColor();
    }

    private boolean isSourceLanguageValid() {
        String name = sourceLanguageInput.getText().toString();
        Language language = Language.withName(name);
        return !language.getIso().equals(LanguageService.INVALID_LANGUAGE);
    }

    private void updateNextButtonColor() {
        Integer textColor = isSourceLanguageValid() ? R.color.primaryTextColor : R.color.textDisabled;
        Integer nextArrow = isSourceLanguageValid() ? R.drawable.forward_arrow : R.drawable.forward_arrow_disabled;
        nextButtonText.setTextColor(ContextCompat.getColor(this, textColor));
        nextButtonImage.setBackgroundResource(nextArrow);
    }

}

