package com.example.mycourseprojectapplication.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mycourseprojectapplication.Fragments.BottomSheetDialog;
import com.example.mycourseprojectapplication.Models.Card;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.CardLab;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.microblink.blinkcard.MicroblinkSDK;
import com.microblink.blinkcard.entities.recognizers.Recognizer;
import com.microblink.blinkcard.entities.recognizers.RecognizerBundle;
import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardRecognizer;
import com.microblink.blinkcard.uisettings.ActivityRunner;
import com.microblink.blinkcard.uisettings.BlinkCardUISettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class AddCardActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName(); // this is used for debugging
    private boolean showingGray = true;
    private AnimatorSet inSet;
    private AnimatorSet outSet;
    private Card card;
    private ProgressBar progress_circle;
    private TextView textViewCardNumber,textViewCardHolder,textViewDate,textViewCCV;
    private TextInputEditText editTextCardNumber,editTextCardHolderName,editTextCCV,editTextYear;
    private TextInputLayout textInputLayoutCN,textInputLayoutCHN,textInputLayoutCCV,textInputLayoutYear,textInputLayoutMonth;
    private MaterialAutoCompleteTextView editTextMonth;                                                              // declare our variables
    private CardView cardGray,cardBlue;
    private static final String PATTERN_VISA = "^4[0-9 ]*";
    private static final String PATTERN_MASTER = "^5[0-9 ]*";
    private static final String PATTERN_DIGITS = "[0-9]";
    private static final String PATTERN_SPECIAL_SYM = "[!@#$%&*()_+=|<>?{}/~-]";
    private static final String PATTERN_SPECIAL_SYM_DATE = "[!@#$%&*()_+=|<>?{}~-]";
    private ConstraintLayout constraintLayout;
    private ImageView cardTypeImageView;
    private Button buttonAddCard,buttonUpdateCard,buttonDeleteCard;
    private final int REQUEST_CODE_SCAN_CARD = 1;
    private ArrayList<Card> cardArrayList;
    private BlinkCardRecognizer mRecognizer;
    private RecognizerBundle mRecognizerBundle;
    private int colorPrimary = 0;
    private int colorError = 0;
    private ColorStateList csPrimary = null;
    private ColorStateList csError = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        new ConnectionDetector(this);  // check if user has Internet connection

        MicroblinkSDK.setLicenseFile("licene.mblic", this);
        mRecognizer = new BlinkCardRecognizer();

        // bundle recognizers into RecognizerBundle
        mRecognizerBundle = new RecognizerBundle(mRecognizer);
        card = new Card();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // setup the action toolbar with activity title and back icon functionality
        setTitle("Add card");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        colorError = ContextCompat.getColor(this, R.color.error_color);

        csPrimary = ColorStateList.valueOf(colorPrimary);
        csError = ColorStateList.valueOf(colorError);

        textInputLayoutCN = findViewById(R.id.input_layout_card_number);
        textInputLayoutCHN = findViewById(R.id.input_layout_card_holder);
        textInputLayoutCCV = findViewById(R.id.input_layout_cvv_code);
        textInputLayoutYear = findViewById(R.id.input_layout_expired_date);
        editTextCardNumber = findViewById(R.id.input_edit_card_number);
        editTextCardHolderName = findViewById(R.id.input_edit_card_holder);
        editTextCCV = findViewById(R.id.input_edit_cvv_code);
        editTextYear = findViewById(R.id.input_edit_expired_date);
        cardBlue = findViewById(R.id.card_blue);
        cardGray = findViewById(R.id.card_gray);
        progress_circle = findViewById(R.id.progress_circle);
        textViewCardNumber = findViewById(R.id.text_card_number);
        textViewCardHolder = findViewById(R.id.text_card_holder);
        textViewCCV = findViewById(R.id.text_cvv_code);
        textViewDate = findViewById(R.id.text_expired_date);
        constraintLayout = findViewById(R.id.constraintCard);
        ImageView helpCcvGray = findViewById(R.id.icon_help_gray);
        ImageView helpCcvBlue = findViewById(R.id.icon_help_blue);
        cardTypeImageView = findViewById(R.id.imageViewType);
        buttonAddCard = findViewById(R.id.buttonAddCard);
        buttonUpdateCard = findViewById(R.id.buttonUpdateCard);
        buttonDeleteCard = findViewById(R.id.buttonDeleteCard);

        cardArrayList = CardLab.get(AddCardActivity.this).getmCards();

        helpCcvGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddCardActivity.this, "The CVV Number (\"Card Verification Value\") is a 3 digit number on your credit and debit cards", Toast.LENGTH_LONG).show();
            }
        });
        helpCcvBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddCardActivity.this, "The CVV Number (\"Card Verification Value\") is a 3 digit number on your credit and debit cards", Toast.LENGTH_LONG).show();
            }
        });


        editTextCardNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() == 0)
                {
                    flipToGray();
                }
                if (s.length() != 0) {
                     flipToBlue();
                    textInputLayoutCN.setError("");
                    textInputLayoutCN.setStartIconTintList(csPrimary);
                    textViewCardNumber.setText(s.toString());
                    if(Pattern.compile(PATTERN_VISA).matcher(s.toString()).matches())
                    {
                        int colorFrom = getResources().getColor(R.color.blue_color);
                        int colorTo = getResources().getColor(R.color.blue_color_gradient);
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        colorAnimation.setDuration(0); // milliseconds
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                constraintLayout.setBackgroundColor((int) animator.getAnimatedValue());
                            }

                        });
                       cardTypeImageView.setImageDrawable(getDrawable(R.drawable.ic_billing_visa_logo));
                        colorAnimation.start();
                    }
                    else if(Pattern.compile(PATTERN_MASTER).matcher(s.toString()).matches())
                    {
                        int colorFrom = getResources().getColor(R.color.blue_color_gradient);
                        int colorTo = getResources().getColor(R.color.purple_color_gradient);
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        colorAnimation.setDuration(0); // milliseconds
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                constraintLayout.setBackgroundColor((int) animator.getAnimatedValue());
                            }

                        });
                        cardTypeImageView.setImageDrawable(getDrawable(R.drawable.ic_billing_mastercard_logo));
                        colorAnimation.start();
                    }
                    else
                    {
                        int colorFrom = getResources().getColor(R.color.blue_color_gradient);
                        int colorTo = getResources().getColor(R.color.red1);
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        colorAnimation.setDuration(0); // milliseconds
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                constraintLayout.setBackgroundColor((int) animator.getAnimatedValue());
                            }

                        });
                       cardTypeImageView.setImageDrawable(getDrawable(R.drawable.ic_outline_credit_card_24));
                        colorAnimation.start();
                    }

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

                for (int i = 4; i < s.length(); i += 5) {
                    if (s.toString().charAt(i) != ' ') {
                        s.insert(i, " ");
                    }
                }
                textInputLayoutCN.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));

            }


        });

        editTextYear.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() != 0)
                {
                    textViewDate.setText(s.toString());
                    textInputLayoutYear.setStartIconTintList(csPrimary);
                    textInputLayoutYear.setError("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 2 && s.toString().charAt(2) != '/') {
                    s.insert(2, "/");
                }
            }
        });

        editTextCardHolderName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() != 0)
                {
                    textViewCardHolder.setText(s.toString());
                    textInputLayoutCHN.setError("");
                    textInputLayoutCHN.setStartIconTintList(csPrimary);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextCCV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().length() != 0)
                    {
                        textViewCCV.setText(s.toString());
                        textInputLayoutCCV.setError("");
                        textInputLayoutCCV.setStartIconTintList(csPrimary);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textInputLayoutCN.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putBoolean("cardNumber",true);
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");
            }
        });
        textInputLayoutCHN.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("cardHolder",true);
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");
            }
        });
        textInputLayoutYear.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("cardDate",true);
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");
            }
        });
        textInputLayoutCCV.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("cardCCV",true);
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");
            }
        });


        editTextCardNumber.requestFocus();
        inSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_in);
        outSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_filp_out);

        buttonUpdateCard.setVisibility(View.INVISIBLE);
        buttonAddCard.setVisibility(View.VISIBLE);
        buttonDeleteCard.setVisibility(View.INVISIBLE);

        buttonAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateCardNumber())
                {
                        if(validateExpiredDate())
                        {
                            if(validateCardHolder())
                            {
                                if (validateCCV())
                                {
                                    if(checkForRedundantCards())
                                    {
                                        submit();
                                    }
                                }

                            }
                        }
                }

            }
        });

        Intent intent = getIntent();
        Card myCard = (Card) intent.getSerializableExtra("card");

        if(myCard != null)
        {
            editTextCardNumber.setText(myCard.getCardNumber());
            editTextCardHolderName.setText(myCard.getCardHolder());
            editTextYear.setText(myCard.getExpiredDate());
            editTextCCV.setText(myCard.getCvvCode());
            setTitle("Update your card");
            buttonUpdateCard.setVisibility(View.VISIBLE);
            buttonAddCard.setVisibility(View.INVISIBLE);
            buttonDeleteCard.setVisibility(View.VISIBLE);

            buttonUpdateCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Card deletedCard = CardLab.get(AddCardActivity.this).getCard(myCard.getCardNumber());
                    CardLab.get(AddCardActivity.this).deleteNote(deletedCard);
                    try
                    {
                        CardLab.get(AddCardActivity.this).saveCards();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    if(validateCardNumber())
                    {
                        if(validateExpiredDate())
                        {
                            if(validateCardHolder())
                            {
                                if (validateCCV())
                                {
                                    if(checkForRedundantCards())
                                    {
                                        Card newCard = new Card(editTextCardNumber.getText().toString(),editTextYear.getText().toString(),editTextCardHolderName.getText().toString(),editTextCCV.getText().toString());
                                        CardLab.get(AddCardActivity.this).addCard(newCard);
                                        try
                                        {
                                            CardLab.get(AddCardActivity.this).saveCards();
                                            finish();
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });

            buttonDeleteCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new MaterialAlertDialogBuilder(AddCardActivity.this)
                            .setTitle("Alert")
                            .setMessage("Are you sure you want to delete this card?")
                            .setCancelable(true)
                            .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Card deletedCard = CardLab.get(AddCardActivity.this).getCard(myCard.getCardNumber());
                                    CardLab.get(AddCardActivity.this).deleteNote(deletedCard);
                                    try
                                    {
                                        CardLab.get(AddCardActivity.this).saveCards();
                                        finish();
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                        }
                    }).show();
                }
            });
        }

        textInputLayoutCN.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkPermissions()) // check for camera permission
                {
                    scanCard(); // scan card
                }
                else // ask for permission
                {
                    requestPermissions();
                }
            }
        });


    }
    private boolean checkForRedundantCards()
    {
        for(Card card : cardArrayList)
        {
            if(editTextCardNumber.getText().toString().equals(card.getCardNumber()))
            {
                textInputLayoutCN.setError("Card Already exists!");
                textInputLayoutCN.setStartIconTintList(csError);
                return false;
            }
        }
        return true;
    }

    private boolean validateCardNumber()
    {
        Pattern patternSym = Pattern.compile(PATTERN_SPECIAL_SYM, Pattern.CASE_INSENSITIVE);
        if(editTextCardNumber.getText().toString().trim().isEmpty())
        {
            textInputLayoutCN.setError("Card number should not be empty!");
            textInputLayoutCN.setStartIconTintList(csError);
            return false;
        }
        if(patternSym.matcher(editTextCardNumber.getText().toString().trim()).find())
        {
            textInputLayoutCN.setError("Card number should not contain special characters!");
            textInputLayoutCN.setStartIconTintList(csError);
            return false;
        }
        String cardNumber = editTextCardNumber.getText().toString().replaceAll(" ","");
        if(cardNumber.length() != 16)
        {
             textInputLayoutCN.setError("Card number should be 16 digits!");
            textInputLayoutCN.setStartIconTintList(csError);
             return false;
        }
        if(cardNumber.startsWith("4") || cardNumber.startsWith("5"))
        {
            textInputLayoutCN.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));
            textInputLayoutCN.setError("");
            return true;
        }
        else
        {
            textInputLayoutCN.setError("Invalid card number!");
            textInputLayoutCN.setStartIconTintList(csError);
            return false;
        }

    }
    private boolean validateCardHolder()
    {
        if(editTextCardHolderName.getText().toString().trim().isEmpty())
        {
            textInputLayoutCHN.setError("Card holder name should not be empty!");
            return false;
        }
        if(editTextCardHolderName.getText().toString().trim().length() > 8)
        {
            textInputLayoutCHN.setError("Card holder name should be less than 8 characters");
            return false;
        }
        Pattern patternDigits = Pattern.compile(PATTERN_DIGITS, Pattern.CASE_INSENSITIVE);
        Pattern patternSym = Pattern.compile(PATTERN_SPECIAL_SYM, Pattern.CASE_INSENSITIVE);
        if(patternDigits.matcher(editTextCardHolderName.getText().toString().trim()).find())
        {
            textInputLayoutCHN.setError("Card holder name should not contain numbers!");
            return false;
        }
        if(patternSym.matcher(editTextCardHolderName.getText().toString().trim()).find())
        {
            textInputLayoutCHN.setError("Card holder name should not contain special characters!");
            return false;
        }

        if(editTextCardHolderName.getText().toString().indexOf(" ") > 1)
        {
            textInputLayoutCHN.setError("Card holder name should not contain whitespace!");
            return false;
        }
        textInputLayoutCHN.setError("");
        return true;
    }
    private boolean validateExpiredYear()
    {
        Pattern patternSym = Pattern.compile(PATTERN_SPECIAL_SYM_DATE, Pattern.CASE_INSENSITIVE);
        if(editTextYear.getText().toString().trim().isEmpty())
        {
            textInputLayoutYear.setError("Expired year should not be empty!");
            return false;
        }
        if(editTextYear.getText().toString().trim().length() != 5)
        {
            textInputLayoutYear.setError("Expired year is incorrect");
            return false;
        }
        if(patternSym.matcher(editTextYear.getText().toString()).find())
        {
            textInputLayoutYear.setError("Expired date should not contain special characters!");
            return false;
        }
        int counter = 0;
        for(int i=0; i<editTextYear.getText().toString().length();i++) {
            char ch = editTextYear.getText().toString().charAt(i);
            if(ch == '/')
            {
                counter++;
            }
        }
        if(counter > 1)
        {
            Log.i(TAG,counter+"");
            textInputLayoutYear.setError("Expired date should not contain special characters!");
            return false;
        }
        textInputLayoutYear.setError("");
        return true;

    }
    private boolean validateExpMonth() {
        Pattern patternSym = Pattern.compile(PATTERN_SPECIAL_SYM_DATE, Pattern.CASE_INSENSITIVE);
        if(editTextYear.getText().toString().trim().isEmpty())
        {
            textInputLayoutYear.setError("Expired month should not be empty!");
            return false;
        }
        if(patternSym.matcher(editTextYear.getText().toString().trim()).find())
        {
            textInputLayoutYear.setError("Expired date should not contain special characters!");
            return false;
        }
        int month = Integer.parseInt(editTextYear.getText().toString().substring(0,2));
        if(month < 1 || month > 12)
        {
            textInputLayoutYear.setError("Expired month is invalid!");
            return false;
        }
        textInputLayoutYear.setError("");
        return true;
    }
    private boolean validateExpiredDate() {

        if (validateExpiredYear())
        {
            if (validateExpMonth())
            {
                Calendar calendar = Calendar.getInstance();
                int currentYear = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR)).substring(2));
                int currentMonth = calendar.get(Calendar.MONTH)+1;
                int year = Integer.parseInt(editTextYear.getText().toString().substring(3, 5));
                Log.i(TAG, currentYear + "");
                Log.i(TAG, year + "");
                Log.i(TAG, currentMonth + "");
                int month = Integer.parseInt(editTextYear.getText().toString().substring(0, 2));

                if (year > currentYear)
                {
                    if (year - currentYear <= 5)
                    {
                        return true;
                    }
                    else
                     {
                        textInputLayoutYear.setError("Expired year is invalid!");
                     }

                }
                else if (year == currentYear)
                {
                    if (month > currentMonth)
                    {
                        return true;
                    } else
                    {
                        textInputLayoutYear.setError("Expired month is invalid!");
                        return false;
                    }
                }
                else
                {
                    textInputLayoutYear.setError("Expired year is invalid!");
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        return false;
    }
    private boolean validateCCV()
    {
        if(editTextCCV.getText().toString().trim().isEmpty())
        {
            textInputLayoutCCV.setError("CCV code should not be empty!");
            return false;
        }
        if(editTextCCV.getText().toString().trim().length() != 3)
        {
            textInputLayoutCCV.setError("CCV code should be 3 digits only!");
            return false;
        }
        textInputLayoutCCV.setError("");
        return true;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }


    private void submit() {

        Card card = new Card(editTextCardNumber.getText().toString(),editTextYear.getText().toString(),editTextCardHolderName.getText().toString(),editTextCCV.getText().toString());
        ArrayList<Card> cardArrayList = new ArrayList<>();
        cardArrayList.add(card);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                hideKeyboard(textInputLayoutCCV);
                progress_circle.setVisibility(View.VISIBLE);
                CardLab cardLab = new CardLab(AddCardActivity.this);
                cardLab.addCard(card);
                try
                {
                    cardLab.saveCards();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finish();
            }
        }, 500);
    }

    private void reset() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        progress_circle.setVisibility(View.GONE);
        flipToGray();
        editTextCardNumber.setText("");
        editTextCCV.setText("");
        editTextCardHolderName.setText("");
        editTextYear.setText("");
        textViewCardNumber.setText(getResources().getText(R.string.label_card_number));
        textViewCardHolder.setText(getResources().getText(R.string.label_card_holder));
        textViewCCV.setText(getResources().getText(R.string.label_cvv_code));
        textViewDate.setText(getResources().getText(R.string.label_expired_date));
        editTextCardNumber.requestFocus();
        showKeyboard(editTextCardNumber);
    }

    private void flipToGray() {
        if (!showingGray && !outSet.isRunning() && !inSet.isRunning())
        {
            showingGray = true;
            cardBlue.setCardElevation(0);
            cardGray.setCardElevation(0);
            outSet.setTarget(cardBlue);
            outSet.start();
            inSet.setTarget(cardGray);
            inSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    cardGray.setCardElevation(convertDpToPixel(12, AddCardActivity.this));
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            inSet.start();
        }
    }

    private void flipToBlue() {
        if (showingGray && !outSet.isRunning() && !inSet.isRunning())
        {
            showingGray = false;
            cardGray.setCardElevation(0);
            cardBlue.setCardElevation(0);
            outSet.setTarget(cardGray);
            outSet.start();
            inSet.setTarget(cardBlue);
            inSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    cardBlue.setCardElevation(convertDpToPixel(12, AddCardActivity.this));
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            inSet.start();
        }
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                reset();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkPermissions() // method to check camera permissions
    {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() // method to request camera permissions
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // on permission request result

        if(requestCode == 1000) // check if it is the same request code
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) // if permission is granted
            {
                scanCard();     // scan the card

            }
            else
            { // if not request the permission again
                requestPermissions();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void scanCard() // method that opens an activity to scan the credit/debit card
    {
        BlinkCardUISettings settings = new BlinkCardUISettings(mRecognizerBundle);
        ActivityRunner.startActivityForResult(this, 1, settings);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // on activity result from scanCard method
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) // check it is the same request code
        {
            if (resultCode == Activity.RESULT_OK && data != null)   // check if the result we are getting are ok
            {
                    mRecognizerBundle.loadFromIntent(data);
                    BlinkCardRecognizer.Result result = mRecognizer.getResult();
                    if (result.getResultState() == Recognizer.Result.State.Valid)
                    {
                        editTextCardNumber.setText(result.getCardNumber());
                        editTextCardHolderName.setText(result.getOwner());    // set the values of card in the fields
                        editTextCCV.setText(result.getCvv());
                        editTextYear.setText(result.getExpiryDate().getOriginalDateString());
                    }
            }
            else if (resultCode == Activity.RESULT_CANCELED) // if user canceled the activity
            {
                Log.i(TAG, "Scan canceled");
            }
            else
            {
                Log.i(TAG, "Scan failed"); // if scan failed
            }

        }
    }
}







