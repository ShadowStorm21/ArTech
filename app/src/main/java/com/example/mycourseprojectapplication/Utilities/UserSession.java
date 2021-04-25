package com.example.mycourseprojectapplication.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mycourseprojectapplication.Models.Card;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UserSession {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_CART = "cartvalue";
    private static final String PREF_NAME = "UserSessionPreference";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String CITY_NAME = "cityName";
    public static final String STREET_ADDRESS = "streetAddress";              // declare our variables
    public static final String STATE = "state";
    public static final String ZIP_CODE = "ZIPCode";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String isSwitchIsOn = "IsSwitchOn";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_ID = "id";
    private static final String USER_PHOTO_URL = "Photo";
    public static final String USER_LAST_LOGIN = "lastLogin";
    public static final String USER_MEMBER_SINCE = "memberSince";
    public static final String KEY_USER_CARDS = "cards";
    public static final String KEY_PRODUCT_NAME = "product";
    public static final String KEY_FILTER_TYPE = "filter";
    public static final String KEY_FILTER_TYPE_PRODUCTS = "filterProducts";
    public static final String KEY_FILTER_PRODUCT_BRAND = "brand";
    public static final String KEY_IS_SWITCH_ON_FILTER = "switchFilter";
    public static final String KEY_IS_SWITCH_ON_FILTER_PRODUCTS = "switchFilter1";
    public static final String NOTIFICATION_KEY = "notification_key";
    public static final String USER_SEARCH_QUERIES = "queries";
    public static final String IS_FIRST_TIME_LOGIN = "firsttime";
    public static final String IS_FIRST_TIME_OPEN_DRAWER = "firstTimeDrawer";
    public static final String USER_PERF_MODE = "mode";
    public static final String USER_PERF_MODE_AR = "artips";

    public void createLoginSession(String name, String email,String id,String photo){      // method to save user info on login
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_USERNAME, name);
        editor.putString(USER_PHOTO_URL,photo);
        editor.putString(KEY_USER_ID,id);
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        // commit changes
        editor.commit();
    }

    private Context mContext;
    private String mFilename;

    public UserSession(Context mContext, String mFilename) {
        this.mContext = mContext;
        this.mFilename = mFilename;

    }

    public HashMap<String, String> getUserDetails(){ // method to get the user data from the session
        HashMap<String, String> user = new HashMap<>();
        // user name
        user.put(KEY_USERNAME, sharedPreferences.getString(KEY_USERNAME, null));// user email id
        user.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));
        user.put(KEY_USER_ID, sharedPreferences.getString(KEY_USER_ID, null));
        user.put(USER_PHOTO_URL,sharedPreferences.getString(USER_PHOTO_URL,null));
        // return user
        return user;
    }

    public boolean isLoggedIn()
    {
        return sharedPreferences.getBoolean(IS_LOGIN,false);
    }
    public void setIsLogin(boolean bool)
    {
        editor.putBoolean(IS_LOGIN,bool);
        editor.commit();
    }

    public void setUserSearchQueries(HashSet<String> searchQueries)
    {
        editor.putStringSet(USER_SEARCH_QUERIES,searchQueries);
        editor.commit();
    }
    public HashSet<String> getSearchQueries()
    {
        return (HashSet<String>) sharedPreferences.getStringSet(USER_SEARCH_QUERIES,null);
    }



    public int getCartValue(){
        return sharedPreferences.getInt(KEY_CART,0);
    }

    public UserSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        this.context = context;
    }
    public void saveUserEmail(String email)
    {
        editor.putString(KEY_EMAIL,email);
        editor.commit();
    }
    public String getEmail()
    {
        return sharedPreferences.getString(KEY_EMAIL,null);
    }

    public void increaseCartValue(){
        int val = getCartValue()+1;
        editor.putInt(KEY_CART,val);
        editor.commit();

    }
    public void decreaseCartValue(){
        int val = getCartValue()-1;
        editor.putInt(KEY_CART,val);
        editor.commit();
    }

    public void setCartValue(int count){
        editor.putInt(KEY_CART,count);
        editor.commit();
    }

    public boolean isFirstTimeLogin()
    {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LOGIN,true);
    }
    public void setIsFirstTimeLogin(boolean bool)
    {
        editor.putBoolean(IS_FIRST_TIME_LOGIN,bool);
        editor.commit();
    }

    public boolean isUserPRFS_AR()
    {
        return sharedPreferences.getBoolean(USER_PERF_MODE_AR,true);
    }
    public void setUserPRFS_AR(boolean bool)
    {
        editor.putBoolean(USER_PERF_MODE_AR,bool);
        editor.commit();
    }

    public boolean isFirstTimeOpenDrawer()
    {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_OPEN_DRAWER,true);
    }
    public void setIsFirstTimeOpenDrawer(boolean bool)
    {
        editor.putBoolean(IS_FIRST_TIME_OPEN_DRAWER,bool);
        editor.commit();
    }

    public boolean isIsSwitchIsOn()
    {
        return sharedPreferences.getBoolean(isSwitchIsOn,false);
    }
    public void setIsSwitchIsOn(boolean bool)
    {
        editor.putBoolean(isSwitchIsOn,bool);
        editor.commit();
    }

    public String getFirstName() {
        return sharedPreferences.getString(FIRST_NAME,null);
    }

    public void setFirstName(String firstName) {
        editor.putString(FIRST_NAME,firstName);
        editor.commit();
    }

    public String getPrefName() {
        return sharedPreferences.getString(USER_PERF_MODE,"System Default");
    }

    public void setUserPerfMode(String pref) {
        editor.putString(USER_PERF_MODE,pref);
        editor.commit();
    }

    public String getLastName() {
        return sharedPreferences.getString(LAST_NAME,null);
    }

    public void setLastName(String lastName) {
        editor.putString(LAST_NAME,lastName);
        editor.commit();
    }

    public String getNotificationKey() {
        return sharedPreferences.getString(NOTIFICATION_KEY,null);
    }

    public void setNotificationKey(String key) {
        editor.putString(NOTIFICATION_KEY,key);
        editor.commit();
    }

    public String getCityName() {
        return sharedPreferences.getString(CITY_NAME,null);
    }

    public void setCityName(String cityName) {
        editor.putString(CITY_NAME,cityName);
        editor.commit();
    }

    public String getStreetAddress() {
        return sharedPreferences.getString(STREET_ADDRESS,null);
    }

    public void setStreetAddress(String streetAddress) {
        editor.putString(STREET_ADDRESS,streetAddress);
        editor.commit();
    }

    public String getState() {
        return sharedPreferences.getString(STATE,null);
    }

    public void setState(String state) {
        editor.putString(STATE,state);
        editor.commit();
    }

    public String getZipCode() {
        return sharedPreferences.getString(ZIP_CODE,null);
    }

    public void setZipCode(String zipCode) {
        editor.putString(ZIP_CODE,zipCode);
        editor.commit();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString(PHONE_NUMBER,null);
    }

    public void setPhoneNumber(String phoneNumber) {
        editor.putString(PHONE_NUMBER,phoneNumber);
        editor.commit();
    }
   public String getFilterType() {
        return sharedPreferences.getString(KEY_FILTER_TYPE,null);
    }

    public void setFilterType(String filterType) {
        editor.putString(KEY_FILTER_TYPE,filterType);
        editor.commit();
    }
    public String getFilterTypeProducts() {
        return sharedPreferences.getString(KEY_FILTER_TYPE_PRODUCTS,null);
    }

    public void setFilterTypeProducts(String filterType) {
        editor.putString(KEY_FILTER_TYPE_PRODUCTS,filterType);
        editor.commit();
    }
    public String getBrandProductFilter() {
        return sharedPreferences.getString(KEY_FILTER_PRODUCT_BRAND,null);
    }

    public void setProductBrandFilter(String filterType) {
        editor.putString(KEY_FILTER_PRODUCT_BRAND,filterType);
        editor.commit();
    }
    public boolean getFilterSwitchState() {
        return sharedPreferences.getBoolean(KEY_IS_SWITCH_ON_FILTER,false);
    }

    public void setFilterSwitchState(boolean status) {
        editor.putBoolean(KEY_IS_SWITCH_ON_FILTER,status);
        editor.commit();
    }

    public boolean getFilterSwitchStateProducts() {
        return sharedPreferences.getBoolean(KEY_IS_SWITCH_ON_FILTER_PRODUCTS,false);
    }

    public void setFilterSwitchStateProducts(boolean status) {
        editor.putBoolean(KEY_IS_SWITCH_ON_FILTER_PRODUCTS,status);
        editor.commit();
    }
    public String getProductName() {
        return sharedPreferences.getString(KEY_PRODUCT_NAME,null);
    }

    public void setProductName(String productName) {
        editor.putString(KEY_PRODUCT_NAME,productName);
        editor.commit();
    }


    public void saveUserCards(ArrayList<Card> cardArrayList) throws Exception {
        JSONArray jsonArray = new JSONArray();
        for(Card card : cardArrayList)
            jsonArray.put(card.toJSON());  // convert to json format

        Writer writer = null;
        try { // write to disk
            OutputStream outputStream = mContext.openFileOutput(mFilename,Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(outputStream);
            writer.write(jsonArray.toString());
        }
        finally {
            if(writer != null)
                writer.close();
        }
    }

    public ArrayList<Card> loadCards() throws Exception {
        ArrayList<Card> notesArrayList = new ArrayList<>();
        BufferedReader reader = null;
        try {
            InputStream inputStream = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                jsonString.append(line);
            }
            JSONArray jsonArray = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for(int i = 0 ; i < jsonArray.length() ; i++)
            {
                notesArrayList.add(new Card(jsonArray.getJSONObject(i)));
            }


        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if(reader != null)
                reader.close();
        }
        return notesArrayList;
    }
}
