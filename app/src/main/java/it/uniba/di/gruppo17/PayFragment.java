package it.uniba.di.gruppo17;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.ConnectionUtil;
import it.uniba.di.gruppo17.asynchttp.*;

/**
 * Fragment che consente all'utente di aggiungere del credito nel suo portafoglio
 * @author Andrea Montemurro
 */

public class PayFragment extends Fragment {

    private SharedPreferences preferences;
    private Button payBt;
    private EditText amountEt;
    private int idUtente;
    private boolean okTransaction = false; //variabile booleana che verifica se la transazione è andata a buon fine
    private static boolean mOk;
    private Double newAmount; //indica il valore del saldo del portafoglio dopo la transazione
    private URL url;

    //PAYPAL id
    private static final String PAYPAL_CLIENT_ID = "AWJQ57yZHenTTO5TnZ523QMWfDMzqhdg7VAbbOg-OfUNAarfWdvWThYjTfB8VNWcfRHQo0AmeNeLxInp";
    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;
    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PAYPAL_CLIENT_ID);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getActivity().getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        //Start the PayPal service
        Intent intent = new Intent(getContext(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_pay, container, false);

        payBt = (Button) layout.findViewById(R.id.addBalanceButton);
        amountEt = (EditText) layout.findViewById(R.id.valueAddBalance);

        return layout;
    }

    @Override
    public void onResume() {
        idUtente = preferences.getInt(Keys.ID_UTENTE, -1);
        payBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPayment();
                //setCreditDb();
            }
        });
        super.onResume();
    }

    @Override
    public void onDestroy() {
        //Destroy also the paypal serv
        getActivity().stopService(new Intent(getContext(), PayPalService.class));
        super.onDestroy();
    }

    /**
     * Metodo che gestisce pagamento paypol
     */
    private void getPayment() {
        //Getting the amount from editText
        String paymentAmount = amountEt.getText().toString();

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "EUR", "Simplified Coding Fee", PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(getContext(), PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    /**
     * Metodo richiamato dopo il risultato dell'activity paypal
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

                        setCreditDb();
                        /*Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(getContext(), ConfirmationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paymentAmount)); */

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    /**
     * Metodo che gestisce credito dopo pagamento
     */
    private void setCreditDb() {
        if (ConnectionUtil.checkInternetConn(Objects.requireNonNull(getActivity())))
        {
            Toast.makeText(getActivity(), R.string.loading_connection_msg, Toast.LENGTH_LONG);
            float amount = Float.parseFloat(amountEt.getText().toString());
            if (amount > 0.00)
            {
                //Se importo ricarica non è nullo posso effettuare la ricarica
                String strConn = Keys.SERVER + "set_portafoglio.php?id=" + idUtente + "&soldi=" + amount;
                try {
                    url = new URL(strConn);
                    // Esequo la query tramite php e prendo il risultato
                    okTransaction = new AsyncAddBalance().execute(url).get();
                } catch (InterruptedException | ExecutionException | MalformedURLException e) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.no_connection_title)
                            .setMessage(R.string.no_connection_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Faccio tornare al fragment Wallet
                                    goToFragment(new WalletFragment());
                                }
                            }).create().show();
                }

                //verifico che la transazione è andata a buon fine
                checkTransaction(okTransaction);
            }
            else
            {
                //errore sull'importo inserito
                Snackbar.make(Objects.requireNonNull(getView()), R.string.amount_lessThanZero, Snackbar.LENGTH_LONG).show();
            }

        }
        else
        {
            //errore no connessione
            //Toast.makeText(getActivity(), R.string.no_connection_message, Toast.LENGTH_LONG);
            Snackbar.make(Objects.requireNonNull(getView()), R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Metodo che fa cambiare fragment
     * @param nextFragment prossimo fragment
     */
    private void goToFragment(Fragment nextFragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.commit();
    }



    public static void afterTask(boolean ok){
      mOk=ok;
    }

    /**
     * Metodo che controlla se la transazione è stata fatta o meno
     */
    private void checkTransaction (boolean okTransaction)
    {
        if (okTransaction) {
            Toast.makeText(this.getView().getContext(), R.string.success_transaction, Toast.LENGTH_LONG).show();
            if (setWalletValue())
                goToFragment(new WalletFragment());
        } else {
            Toast.makeText(this.getView().getContext(), R.string.failure_transaction, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Metodo che aggiorna il valore del portafoglio dopo la ricarica
     * @return
     */
    private boolean setWalletValue() {
        SharedPreferences.Editor editor = preferences.edit();
        double amount = -1.0;
        try {
         /*   importo = new AsyncGetBalance().execute(new URL(
                    .concat(String.valueOf(preferences.getInt(Keys.ID_UTENTE, -1))))).get(); */
            String connectinUrl =  Keys.SERVER + "get_portafoglio.php?id=" + preferences.getInt(Keys.ID_UTENTE, -1);
            URL url = new URL(connectinUrl);
            newAmount = new AsyncGetBalance().execute(url).get();
            editor.putFloat(Keys.WALLET, (float) amount);
            editor.apply();
            return true;
        } catch (InterruptedException | ExecutionException | MalformedURLException e) {
            return false;
        }
    }


    /**
     * Metodo che abbassa la view della tastiera
     * @param context
     */
    private void hideKeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View v = ((Activity) context).getCurrentFocus();
        if (v == null)
            return;
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

}
