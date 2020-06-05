package it.uniba.di.gruppo17;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

/**
 * @author Pasquale
 * Dialog SignUp avvenuto con successo
 */
public class SignUpDialog {

    private Activity activity;
    private AlertDialog dialog;

    public SignUpDialog(Activity myActivity) {
        activity = myActivity;
    }

    public void startDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog_signup,null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();

    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
