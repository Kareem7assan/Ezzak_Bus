package com.aait.ezakbus.utils;

import android.widget.EditText;

/**
 * Created by human on 8/9/17.
 */

public class Validation {

    private static final boolean isValid(String input){
        boolean valid=true;
        if (input.trim().isEmpty()){
            valid=false;
        }
        return valid;
    }
    public static final boolean checkError(EditText editText) {
        if(!isValid(editText.getText().toString())) {
            editText.setError("sorry fill Empty form");
            return false;
            }
        else {
            return true;
        }

    }
    public static final boolean checkPassSize(EditText editText) {
        if(editText.getText().toString().length()<=5) {
            editText.setError("password should be more 6 characters");
            return false;
        }
        else {
            return true;
        }

    }
    public static final boolean checkNamePref(EditText editText) {
        if(editText.getText().toString().matches("^[a-zA-Z].*")) {

            return true;
        }
        else {
            editText.setError("user name shouldn't start's with number or special character");
            return false;
        }

    }
    public static final boolean checkMatch(EditText pass,EditText confirm_pass){
         boolean isMatch=false;
        if (pass.getText().toString().matches(confirm_pass.getText().toString())){
            isMatch=true;
        }
        else {
            confirm_pass.setError("invalid confirmation");
        }

        return isMatch;
    }
}
