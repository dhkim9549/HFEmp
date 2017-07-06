package com.dhkim9549.hfemp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import org.json.*;

public class MainActivity extends ActionBarActivity {

    EditText editText1 = null;
    LinearLayout linearLayout2 = null;
    ArrayList listArray = null;
    String status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1 = (EditText)findViewById(R.id.editText1);
        linearLayout2 = (LinearLayout)findViewById(R.id.linearLayout2);

        Button searchButton = (Button)findViewById(R.id.button1);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (status.equals("searching")) {
                    return;
                }
                status = "searching";
                listArray = new ArrayList();
                linearLayout2.removeAllViews();

                String editText1Str = editText1.getText().toString();
                try {
                    new DownloadTask().execute(editText1Str);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int cnt = 0;
                while (status.equals("searching")) {
                    cnt++;
                    if (cnt > 100) break;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                drawList(listArray);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadTask extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... param) {

            long totalSize = 0;

            try {

                String emp_param = param[0];

                ArrayList listArray1 = getEmpList(getEmpListJson(emp_param));
                listArray.addAll(listArray1);

                status = "complete";

            } catch(Exception e) {
                listArray.add(new String(e.toString()));
                status = "error";
            }

            return totalSize;
        }
    }

    private String getEmpListJson(String param) throws Exception {

        URL url = new URL("http://app.hf.go.kr/nhfm/hfFamily/search.html");
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("accept-language", "ko");
        conn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "euc-kr");

        writer.write("inputValue=" + param);
        writer.flush();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        String json = "";

        while ((line = reader.readLine()) != null) {
            json += line;
        }
        writer.close();
        reader.close();

        return json;
    }

    public ArrayList getEmpList(String json) throws Exception {

        JSONArray ja = new JSONArray(json);

        ArrayList arrayList = new ArrayList();

        for(int i = 0; i < ja.length(); i++) {

            JSONObject jo = ja.getJSONObject(i);

            String brcd_nm = (String)jo.opt("brcd_nm");
            String han_nm = (String)jo.opt("han_nm");
            String team_cd_nm = (String)jo.opt("team_cd_nm");
            String pos_cd_nm = (String)jo.opt("pos_cd_nm");
            String office_tel_no = (String)jo.opt("office_tel_no");
            String mobile_tel_no = (String)jo.opt("mobile_tel_no");

            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setPadding(15, 10, 15, 10);
            LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            ll.setLayoutParams(llParams);

            TextView tv = new TextView(this);
            tv.setText(han_nm + " " + pos_cd_nm);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(20);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 0);
            tv.setLayoutParams(layoutParams);
            tv.setTypeface(Typeface.SANS_SERIF);
            ll.addView(tv);

            tv = new TextView(this);
            tv.setText(brcd_nm + " " + team_cd_nm);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(16);
            tv.setLayoutParams(layoutParams);
            tv.setTypeface(Typeface.SANS_SERIF);
            ll.addView(tv);

            tv = new TextView(this);
            tv.setText(office_tel_no);
            tv.setTextColor(Color.BLUE);
            tv.setTextSize(16);
            tv.setLayoutParams(layoutParams);
            tv.setTypeface(Typeface.SANS_SERIF);
            ll.addView(tv);

            tv = new TextView(this);
            tv.setText(mobile_tel_no);
            tv.setTextColor(Color.BLUE);
            tv.setTextSize(16);
            tv.setLayoutParams(layoutParams);
            tv.setTypeface(Typeface.SANS_SERIF);
            ll.addView(tv);

            arrayList.add(ll);
        }

        return arrayList;
    }

    private void drawList(ArrayList listArray) {


        LinearLayout linearLayout2 = (LinearLayout)findViewById(R.id.linearLayout2);
        linearLayout2.removeAllViews();

        Iterator it = listArray.iterator();

        while(it.hasNext()) {

            LinearLayout ll = (LinearLayout)it.next();
            linearLayout2.addView(ll);
        }
    }
}
