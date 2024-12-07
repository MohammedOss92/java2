package com.sarrawi.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class Translate2 extends AppCompatActivity {

    private ImageView clear;
    private ImageView translate;
    private ImageView swap;
    private EditText text;
    private Spinner from;
    private Spinner to;
    private Map<String, String> langs = new HashMap<>();
    private ArrayList<com.sarrawi.myapplication.Param> params = new ArrayList<>();

    private View translatePro = null;
    private ListView resultList;
    private ArrayList<com.sarrawi.myapplication.ResultRow> results = new ArrayList<>();
    private ResultAdapter resultAdapter;
    private Context context;
    private Random rand = new Random();
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        db = new DB(this);
        db.open();
        results = db.getResults();
        context = this;
        fillData();

        resultList = findViewById(R.id.result);
        resultAdapter = new ResultAdapter(this, results);
        resultList.setAdapter(resultAdapter);

        resultList.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.action_delete_title)
                    .setMessage(R.string.action_delete_msg)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        db.deleteResult(results.get(pos).id);
                        results.remove(pos);
                        resultAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> {})
                    .setIcon(R.drawable.ic_menu_delete)
                    .show();
            return true;
        });

        translatePro = findViewById(R.id.translatePro);
        from = findViewById(R.id.fromSpin);
        to = findViewById(R.id.toSpin);

        from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                text.setHint(from.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        text = findViewById(R.id.text);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.lang_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        from.setAdapter(adapter);
        to.setAdapter(adapter);
        from.setSelection(0);
        to.setSelection(1);

        clear = findViewById(R.id.clear);
        clear.setOnClickListener(v -> text.setText(""));

        translate = findViewById(R.id.translate);
        translate.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(text.getText().toString())) {
                if (isOnline()) {
                    new JSONParse().execute();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.connection_faild, Toast.LENGTH_SHORT).show();
                }
            }
        });

        swap = findViewById(R.id.swap);
        swap.setOnClickListener(v -> {
            int i = from.getSelectedItemPosition();
            from.setSelection(to.getSelectedItemPosition(), true);
            to.setSelection(i, true);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.translate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear) {
            results.clear();
            db.clear();
            resultAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            translate.setVisibility(View.INVISIBLE);
            translatePro.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            String urlString = "";
            try {
                urlString = "https://translate.google.com/translate_a/t?client=p&q="
                        + URLEncoder.encode(text.getText().toString(), "UTF-8")
                        + "&hl=" + langs.get(to.getSelectedItem().toString())
                        + "&sl=" + langs.get(from.getSelectedItem().toString())
                        + "&tl=" + langs.get(to.getSelectedItem().toString())
                        + "&ie=UTF-8&oe=UTF-8&multires=0";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            JSONObject json = jParser.getJSONFromUrl(urlString, params);

            if (json == null) {
                // إذا كانت الاستجابة فارغة أو غير صالحة، سجل الخطأ
                Log.e("Translate", "Received null response from server");
            }

            return json;
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            translatePro.setVisibility(View.INVISIBLE);
            translate.setVisibility(View.VISIBLE);

            if (json == null) {
                // إذا كانت النتيجة null، يمكنك إظهار رسالة للمستخدم
                Toast.makeText(getApplicationContext(), "خطأ في الحصول على البيانات من الخادم", Toast.LENGTH_SHORT).show();
                return;
            }

            String result1 = "";
            try {
                // تحقق إذا كانت الـ JSONArray موجودة قبل محاولة الوصول إليها
                if (json.has("sentences")) {
                    JSONArray data = json.getJSONArray("sentences");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        result1 += item.getString("trans");
                    }
                } else {
                    // في حال لم تكن "sentences" موجودة في الاستجابة
                    Log.e("Translate", "No 'sentences' array in response");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // إضافة البيانات إلى قاعدة البيانات وعرض النتيجة
            int id = rand.nextInt();
            db.insertResult(id, from.getSelectedItem().toString(), to.getSelectedItem().toString(),
                    text.getText().toString(), result1, langs.get(from.getSelectedItem().toString()),
                    langs.get(to.getSelectedItem().toString()));
            results.add(new com.sarrawi.myapplication.ResultRow(id, from.getSelectedItem().toString(), to.getSelectedItem().toString(),
                    text.getText().toString(), result1, langs.get(to.getSelectedItem().toString()),
                    langs.get(from.getSelectedItem().toString())));
            resultAdapter.notifyDataSetChanged();
            resultList.setSelection(resultList.getAdapter().getCount() - 1);
        }

    }

    static class JSONParser {
        public JSONObject getJSONFromUrl(String url, ArrayList<Param> params) {
            HttpURLConnection urlConnection = null;
            StringBuilder postData = new StringBuilder();
            JSONObject jObj = null;

            try {
                URL obj = new URL(url);
                urlConnection = (HttpURLConnection) obj.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // بناء بيانات الـ POST
                for (Iterator<Param> iterator = params.iterator(); iterator.hasNext(); ) {
                    Param param = iterator.next();
                    postData.append(param.getName()).append("=").append(param.getValue());
                    if (iterator.hasNext()) postData.append("&");
                }

                // إرسال البيانات عبر الاتصال
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = postData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // قراءة الاستجابة وتحويلها إلى JSON
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    jObj = new JSONObject(response.toString());
                }

            } catch (IOException | JSONException e) {
                Log.e("JSONParser", "Error while fetching or parsing JSON", e);
            }

            return jObj;
        }

    }

    private void fillData() {
        langs.put("Afrikaans", "af");
        langs.put("Albanian", "sq");
        langs.put("Arabic", "ar");
        langs.put("Armenian", "hy");
        langs.put("Azerbaijani", "az");
        langs.put("Basque", "eu");
        langs.put("Bengali", "bn");
        langs.put("Belarusian", "be");
        langs.put("Bosnian", "bs");
        langs.put("Bulgarian", "bg");
        langs.put("Catalan", "ca");
        langs.put("Cebuano", "ceb");
        langs.put("Chinese Simplified", "zh-CN");
        langs.put("Chinese Traditional", "zh-TW");
        langs.put("Croatian", "hr");
        langs.put("Czech", "cs");
        langs.put("Danish", "da");
        langs.put("Dutch", "nl");
        langs.put("English", "en");
        langs.put("Esperanto", "eo");
        langs.put("Estonian", "et");
        langs.put("Filipino", "tl");
        langs.put("Finnish", "fi");
        langs.put("French", "fr");
        langs.put("Galician", "gl");
        langs.put("Georgian", "ka");
        langs.put("German", "de");
        langs.put("Greek", "el");
        langs.put("Gujarati", "gu");
        langs.put("Haitian Creole", "ht");
        langs.put("Hausa", "ha");
        langs.put("Hebrew", "iw");
        langs.put("Hindi", "hi");
        langs.put("Hmong", "hmn");
        langs.put("Hungarian", "hu");
        langs.put("Icelandic", "is");
        langs.put("Igbo", "ig");
        langs.put("Indonesian", "id");
        langs.put("Irish", "ga");
        langs.put("Italian", "it");
        langs.put("Japanese", "ja");
        langs.put("Javanese", "jw");
        langs.put("Kannada", "kn");
        langs.put("Khmer", "km");
        langs.put("Korean", "ko");
        langs.put("Lao", "lo");
        langs.put("Latin", "la");
        langs.put("Latvian", "lv");
        langs.put("Lithuanian", "lt");
        langs.put("Macedonian", "mk");
        langs.put("Malay", "ms");
        langs.put("Maltese", "mt");
        langs.put("Maori", "mi");
        langs.put("Marathi", "mr");
        langs.put("Mongolian", "mn");
        langs.put("Nepali", "ne");
        langs.put("Norwegian", "no");
        langs.put("Persian", "fa");
        langs.put("Polish", "pl");
        langs.put("Portuguese", "pt");
        langs.put("Punjabi", "pa");
        langs.put("Romanian", "ro");
        langs.put("Russian", "ru");
        langs.put("Serbian", "sr");
        langs.put("Slovak", "sk");
        langs.put("Slovenian", "sl");
        langs.put("Somali", "so");
        langs.put("Spanish", "es");
        langs.put("Swahili", "sw");
        langs.put("Swedish", "sv");
        langs.put("Tamil", "ta");
        langs.put("Telugu", "te");
        langs.put("Thai", "th");
        langs.put("Turkish", "tr");
        langs.put("Ukrainian", "uk");
        langs.put("Urdu", "ur");
        langs.put("Vietnamese", "vi");
        langs.put("Welsh", "cy");
        langs.put("Yiddish", "yi");
        langs.put("Yoruba", "yo");
        langs.put("Zulu", "zu");
    }



    static class ResultRow {
        int id;
        String fromLang;
        String toLang;
        String originalText;
        String translatedText;
        String fromLangCode;
        String toLangCode;

        public ResultRow(int id, String fromLang, String toLang, String originalText, String translatedText, String fromLangCode, String toLangCode) {
            this.id = id;
            this.fromLang = fromLang;
            this.toLang = toLang;
            this.originalText = originalText;
            this.translatedText = translatedText;
            this.fromLangCode = fromLangCode;
            this.toLangCode = toLangCode;
        }
    }

    // Assuming DB and ResultAdapter classes are defined elsewhere in your code.
}
