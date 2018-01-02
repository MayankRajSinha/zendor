package mayank.example.zendor.onClickBuyer;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.onClickSeller.ledgerAdapter;
import mayank.example.zendor.onClickSeller.sellerLedger;

/**
 * A simple {@link Fragment} subclass.
 */
public class buyerLedger extends Fragment {

    public static String BUYER_ID = "buyer_id";
    private String buyer_id;
    private ListView buyerListView;
    private TextView cb;
    private ArrayList<sellerLedger.ledgerClass> ledgerList;
    private TextView buyerNameAndZone;
    private ImageView back;
    private LoadingClass lc;


    public buyerLedger() {

    }

    public static buyerLedger newInstance(String buyer_id){
        buyerLedger fragment = new buyerLedger();
        Bundle bundle = new Bundle();
        bundle.putString(BUYER_ID, buyer_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buyer_id = getArguments().getString(BUYER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_ledger, container, false);

        buyerListView = view.findViewById(R.id.ledgerView);
        cb = view.findViewById(R.id.cb);
        buyerNameAndZone = view.findViewById(R.id.sellerNameAndZone);
        ledgerList = new ArrayList<>();
        lc = new LoadingClass(getActivity());

        getBuyerLedger();

        return view;
    }

    private void getBuyerCb(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String CB = json.getString("current_balance");
                    cb.setText(CB);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("buyer id", buyer_id);
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id",buyer_id);
                return parameters;
            }
        };
        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void getBuyerLedger(){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.BUYER_LEDGER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray ledgerArray = json.getJSONArray("ledger");
                    for(int i =0;i<ledgerArray.length();i++){
                        JSONObject ledger = ledgerArray.getJSONObject(i);
                        String date = ledger.getString("date");
                        String pid = ledger.getString("pid");
                        String dc = ledger.getString("dc");
                        String balance = ledger.getString("balance");
                        ledgerList.add(new sellerLedger.ledgerClass(date, pid, dc, '\u20B9'+balance));
                    }
                    String name = json.getString("buyer_name");
                    buyerNameAndZone.setText(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                    lc.dismissDialog();
                }

                ledgerAdapter adapter = new ledgerAdapter(getActivity(),0, ledgerList);
                buyerListView.setAdapter(adapter);
                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id",buyer_id);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }


}