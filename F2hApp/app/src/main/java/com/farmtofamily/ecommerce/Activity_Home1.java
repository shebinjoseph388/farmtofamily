package com.farmtofamily.ecommerce;

import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Activity_Home1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Activity_Home1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Activity_Home1 extends Fragment {
    ListView listCategory;
    ProgressBar prgLoading;
    TextView txtAlert;
    EditText edtKeyword;
    ImageButton btnSearch;

    // declare adapter object to create custom category list
    AdapterCategoryList cla;

    // create arraylist variables to store data from server
    static ArrayList<Long> Category_ID = new ArrayList<Long>();
    static ArrayList<String> Category_name = new ArrayList<String>();
    static ArrayList<String> Category_image = new ArrayList<String>();

    String CategoryAPI;
    int IOConnect = 0,n;
    String Category_name1;
    String Keyword="";


    public Activity_Home1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Activity_Home1.
     */
    // TODO: Rename and change types and number of parameters
    public static Activity_Home1 newInstance(String param1, String param2) {
        Activity_Home1 fragment = new Activity_Home1();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.category_list, container, false);
        prgLoading = (ProgressBar) v.findViewById(R.id.prgLoading);
        listCategory = (ListView) v.findViewById(R.id.listCategory);
        txtAlert = (TextView) v.findViewById(R.id.txtAlert);
        edtKeyword = (EditText) v.findViewById(R.id.edtKeyword);
        btnSearch = (ImageButton) v.findViewById(R.id.btnSearch);

        cla = new AdapterCategoryList(getActivity());
        Log.d("calling12","calling1");


        // category API url
        CategoryAPI = Constant.CategoryAPI+"?accesskey="+Constant.AccessKey;

        // call asynctask class to request data from server
        Log.d("calling13","calling1");
        new getDataTask().execute();
        btnSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // get keyword and send it to server
                try {
                    Keyword = URLEncoder.encode(edtKeyword.getText().toString(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                CategoryAPI += "&keyword=" + Keyword;
                IOConnect = 0;
                listCategory.invalidateViews();
                clearData();
                Log.d("reached3", "reached");
                new getDataTask().execute();
            }
        });
        // event listener to handle list when clicked
        listCategory.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                // go to menu page
                Intent iMenuList = new Intent(getActivity(), ActivityMenuList.class);
                iMenuList.putExtra("category_id", Category_ID.get(position));
                iMenuList.putExtra("category_name", Category_name.get(position));
                startActivity(iMenuList);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });

        return v;
    }
    // asynctask class to handle parsing json in background
    public class getDataTask extends AsyncTask<Void, Void, Void>{

        // show progressbar first
        getDataTask(){
            if(!prgLoading.isShown()){
                prgLoading.setVisibility(0);
                txtAlert.setVisibility(8);
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            // parse json data from server in background
            Log.d("calling","calling");
            parseJSONData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // when finish parsing, hide progressbar
            prgLoading.setVisibility(8);
           // tv_cart_notif.setText(n);
            // if internet connection and data available show data on list
            // otherwise, show alert text
            if((Category_ID.size() > 0) && (IOConnect == 0)){
                listCategory.setVisibility(0);
                listCategory.setAdapter(cla);
                Log.d("adpaterset","adpaterset");
            }else{
                txtAlert.setVisibility(0);
            }
        }
    }
    // clear arraylist variables before used
    void clearData(){
        Category_ID.clear();
        Category_name.clear();
        Category_image.clear();
    }
    // method to parse json data from server
    public void parseJSONData(){

        clearData();

        try {
            // request data from Category API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(CategoryAPI);
            HttpResponse response = client.execute(request);
            InputStream atomInputStream = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

            String line;
            String str = "";
            while ((line = in.readLine()) != null){
                str += line;
            }
            Log.d("dataa",""+str);
            // parse json data and store into arraylist variables
            JSONObject json = new JSONObject(str);
            JSONArray data = json.getJSONArray("data");
            Log.d("dataa",""+data);

            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);

                JSONObject category = object.getJSONObject("Category");

                Category_ID.add(Long.parseLong(category.getString("category_id")));


                Category_name.add(category.getString("name"));
                Category_image.add(category.getString("image"));
                Log.d("Category name", Category_name.get(i));

            }


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            IOConnect = 1;
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
   /* public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
