package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class activity_create_new extends AppCompatActivity {
    //members
    private volatile boolean isExist = false;
    private DocumentReference docRef;
    private static final String TAG = "saveToDataBase";
    private static final String ACTIVITIES_COLLECTION = "Activities/";
    Map<String, Object> dataToSave = new HashMap<String, Object>();
    //  Button buttonAge;
    Button buttonMinAge;
    Button buttonMaxAge;
    Button buttonCity;
    Button buttonSportType;
    String className = "activity_create_new";
    boolean clicked = false;
    boolean clicked_city = false;
    String ageThatChosen = "CHOOSE";
    String cityThatChosen = "CHOOSE";
    String sportThatChosen = "CHOOSE";
    View viewToPass;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String user_name;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chats");
    private String activityNameText;
    private ArrayList<String> cities;
    AutoCompleteTextView actv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
        buttonSportType = (Button) findViewById(R.id.sport_type_button);

        buttonSportType.setText(sportThatChosen);
        loadCities();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, cities);
        //Getting the instance of AutoCompleteTextView
        actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        //will start working from first character
        actv.setThreshold(1);
        //setting the adapter data into the AutoCompleteTextView
        actv.setAdapter(adapter);
        actv.setTextColor(Color.RED);

        //add the city when the user click on button
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                if(parent.getItemAtPosition(i)!=null) {
                    Log.d(TAG, parent.getItemAtPosition(i).toString());
                    cityThatChosen = parent.getItemAtPosition(i).toString();
                }
                else{
                    Log.d(TAG, "null");
                }
            }
        });

/*
        buttonAge.setOnClickListener(new View.OnClickListener() {
            //@override
            public void onClick(View v) {
                EditText activityName = (EditText) findViewById(R.id.activity_name);
                String activityNameText = activityName.getText().toString();
                Intent intent = new Intent(activity_create_new.this, age_range.class);
                intent.putExtra("ACTIVITY", className);
                intent.putExtra("ACTIVITY_NAME", activityNameText);
                intent.putExtra("CITY", cityThatChosen);
                intent.putExtra("SPORTS", sportThatChosen);
                startActivity(intent);
                clicked = true;

            }
        });

 */

        //set the city name
        buttonSportType.setOnClickListener(new View.OnClickListener() {
            //@override
            public void onClick(View v) {
                EditText activityName = (EditText) findViewById(R.id.password);
                String activityNameText = activityName.getText().toString();
                Intent intent = new Intent(activity_create_new.this, sport_type.class);
                intent.putExtra("ACTIVITY", className);
                intent.putExtra("ACTIVITY_NAME", activityNameText);
                intent.putExtra("AGE", ageThatChosen);
                intent.putExtra("CITY", cityThatChosen);
                startActivity(intent);
                clicked = true;

            }
        });

        if (!clicked) {
            ageThatChosen = getIntent().getStringExtra("AGE");
            // buttonAge.setText(ageThatChosen);
            cityThatChosen = getIntent().getStringExtra("CITY");
            //buttonCity.setText(cityThatChosen);
            actv.setText(cityThatChosen);

            sportThatChosen = getIntent().getStringExtra("SPORTS");
            buttonSportType.setText(sportThatChosen);
        }
        if (!clicked_city) {
            cityThatChosen = getIntent().getStringExtra("CITY");
            //buttonCity.setText(cityThatChosen);
            actv.setText(cityThatChosen);
            ageThatChosen = getIntent().getStringExtra("AGE");
            // buttonAge.setText(ageThatChosen);
            sportThatChosen = getIntent().getStringExtra("SPORTS");
            buttonSportType.setText(sportThatChosen);
        }
        EditText activityName = (EditText) findViewById(R.id.password);
        String name = getIntent().getStringExtra("ACTIVITY_NAME");
        activityName.setText(name);
    }




    public void backButton(View view) {
        Intent intent = new Intent(this, select_action.class);
        startActivity(intent);
    }

    /**
     * save the data to DB
     * @param view the screen to show on
     * @throws InterruptedException exception
     */
    public void SaveData(View view) throws InterruptedException {
        viewToPass = view;
        EditText activityName = (EditText) findViewById(R.id.password);
        activityNameText = activityName.getText().toString();
        if (activityNameText.isEmpty()){
            Log.d(TAG, "check1!");
            Snackbar.make(view, "Plese enter an activity name ",
                    Snackbar.LENGTH_LONG)
                    .show();
        }else {
            createChatRoom();
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            rootRef.collection(ACTIVITIES_COLLECTION).document(activityNameText).update("Exist", "").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "document updated");
                    Snackbar.make(viewToPass, "Please choose a different name, this name already exist",
                            Snackbar.LENGTH_LONG)
                            .show();

                    isExist = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "no such doc");
                    EditText description = (EditText) findViewById(R.id.descriptionToFill);
                    String descriptionText = description.getText().toString();
                    if (descriptionText.equals("")) {
                        Snackbar.make(viewToPass, "Description is required",
                                Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        createNewDoc();
                        isExist = false;
                    }

                }
            });
        }


    }

    /**
     * create the activity chat
     */
    public void createChatRoom() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();
        DocumentReference userNAme = db.collection("Users").document(email);
        userNAme.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_name = document.getString("name");
                        createChat();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getString("name"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * create the chat to the activity
     */
    public void createChat() {
        Map<String, Object> map = new HashMap<String, Object>();
        // Map<String, Object> mapUser = new HashMap<String, Object>();
        // DatabaseReference UsersList = root.child(activityNameText).child("participants");
        // mapUser.put(user_name,"");
        map.put(activityNameText, "");
        root.updateChildren(map);
        // UsersList.updateChildren(mapUser);
    }

    /**
     * get the new activity details ans save it in a list to the DB
     */
    public void createNewDoc() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();
        EditText activityName = (EditText) findViewById(R.id.password);
        String activityNameText = activityName.getText().toString();
        EditText maxPlayers = (EditText) findViewById(R.id.max_Players);
        //EditText details = (EditText) findViewById(R.id.details);
        CheckBox payment = (CheckBox) findViewById(R.id.payment);
        EditText description = (EditText) findViewById(R.id.descriptionToFill);
        String descriptionText = description.getText().toString();
        EditText minAge = (EditText) findViewById(R.id.min_age);
        String minAgeText = minAge.getText().toString();
        EditText maxAge = (EditText) findViewById(R.id.max_age);
        String maxAgeText = maxAge.getText().toString();

        String ageRange = minAgeText + "-" + maxAgeText;

        String maxPlayersText = maxPlayers.getText().toString();
        String paymentText;
        if (payment.isChecked()) {
            paymentText = "true";
        } else {
            paymentText = "false";
        }

        String collectionName = ACTIVITIES_COLLECTION + activityNameText;

        docRef = FirebaseFirestore.getInstance().document(collectionName);

        dataToSave.put("activityName", activityNameText);
        dataToSave.put("maxPlayers", maxPlayersText);
        dataToSave.put("payment", paymentText);
        dataToSave.put("ageRange", ageRange);
        dataToSave.put("minAge", minAgeText);
        dataToSave.put("maxAge", maxAgeText);
        dataToSave.put("city", cityThatChosen);
        dataToSave.put("sportType", sportThatChosen);
        dataToSave.put("description", descriptionText);
        dataToSave.put("manager_email", email);

        docRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "document saved!");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "document faild");
            }
        });

        docRef.update("participantes", FieldValue.arrayUnion(email))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "array updated");
                        getToTheNextScreen();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "not updated");

            }
        });

    }

    /**
     * get to the next screen: the manager activity screen
     */
    private void getToTheNextScreen() {
        EditText activityName = (EditText) findViewById(R.id.password);
        String activityNameText = activityName.getText().toString();
        EditText description = (EditText) findViewById(R.id.descriptionToFill);
        String descriptionText = description.getText().toString();
        Intent intent = new Intent(activity_create_new.this, manager.class);
        intent.putExtra("ACTIVITY", className);
        intent.putExtra("ACTIVITY_NAME", activityNameText);
        intent.putExtra("DESCRIPTION", descriptionText);

        startActivity(intent);
    }

    /**
     * Load the cities names into a list
     */
    private void loadCities() {

        cities = new ArrayList<>();

        cities.add("ABU JUWEI'ID");
        cities.add("ABU GHOSH");
        cities.add("ABU SINAN");
        cities.add("ABU SUREIHAN");
        cities.add("ABU ABDUN");
        cities.add("ABU AMMAR");
        cities.add("ABU AMRE");
        cities.add("ABU QUREINAT");
        cities.add("ABU QUREINAT");
        cities.add("ABU RUBEI'A");
        cities.add("ABU RUQAYYEQ");
        cities.add("ABU TULUL");
        cities.add("IBTIN");
        cities.add("AVTALYON");
        cities.add("AVI'EL");
        cities.add("AVIVIM");
        cities.add("AVIGEDOR");
        cities.add("AVIHAYIL");
        cities.add("AVITAL");
        cities.add("AVI'EZER");
        cities.add("ABBIRIM");
        cities.add("EVEN YEHUDA");
        cities.add("EVEN MENAHEM");
        cities.add("EVEN SAPPIR");
        cities.add("EVEN SHEMU'EL");
        cities.add("AVNE ETAN");
        cities.add("AVNE HEFEZ");
        cities.add("AVENAT");
        cities.add("AVSHALOM");
        cities.add("ADORA");
        cities.add("ADDIRIM");
        cities.add("ADAMIT");
        cities.add("ADDERET");
        cities.add("UDIM");
        cities.add("ODEM");
        cities.add("OHAD");
        cities.add("UMM AL-FAHM");
        cities.add("UMM AL-QUTUF");
        cities.add("UMM BATIN");
        cities.add("OMEN");
        cities.add("OMEZ");
        cities.add("OFAQIM");
        cities.add("OR HAGANUZ");
        cities.add("OR HANER");
        cities.add("OR YEHUDA");
        cities.add("OR AQIVA");
        cities.add("ORA");
        cities.add("OROT");
        cities.add("ORTAL");
        cities.add("URIM");
        cities.add("ORANIM");
        cities.add("ORANIT");
        cities.add("USHA");
        cities.add("AZOR");
        cities.add("AHAWA");
        cities.add("AHUZZAM");
        cities.add("AHUZZAT BARAQ");
        cities.add("AHIHUD");
        cities.add("AHITUV");
        cities.add("AHISAMAKH");
        cities.add("AHI'EZER");
        cities.add("ATRASH");
        cities.add("IBBIM");
        cities.add("EYAL");
        cities.add("AYYELET HASHAHAR");
        cities.add("ELON");
        cities.add("ELOT");
        cities.add("ILANIYYA");
        cities.add("ELAT");
        cities.add("IRUS");
        cities.add("ITAMAR");
        cities.add("ETAN");
        cities.add("ETANIM");
        cities.add("IKSAL");
        cities.add("AL SAYYID");
        cities.add("AL-AZY");
        cities.add("AL-ARYAN");
        cities.add("EL-ROM");
        cities.add("ALUMMA");
        cities.add("ALUMMOT");
        cities.add("ALLON HAGALIL");
        cities.add("ELON MORE");
        cities.add("ALLON SHEVUT");
        cities.add("ALLONE ABBA");
        cities.add("ALLONE HABASHAN");
        cities.add("ALLONE YIZHAQ");
        cities.add("ALLONIM");
        cities.add("ELI AL");
        cities.add("ELIAV");
        cities.add("ELYAKHIN");
        cities.add("ELIFAZ");
        cities.add("ELIFELET");
        cities.add("ELYAQIM");
        cities.add("ELYASHIV");
        cities.add("ELISHAMA");
        cities.add("ALMAGOR");
        cities.add("ALMOG");
        cities.add("EL'AD");
        cities.add("EL'AZAR");
        cities.add("ALFE MENASHE");
        cities.add("ELQOSH");
        cities.add("ELQANA");
        cities.add("EMUNIM");
        cities.add("AMIRIM");
        cities.add("AMNUN");
        cities.add("AMAZYA");
        cities.add("ANI'AM");
        cities.add("ASAD");
        cities.add("ASEFAR");
        cities.add("I'BILLIN");
        cities.add("A'SAM");
        cities.add("AFEINISH");
        cities.add("AFIQ");
        cities.add("AFIQIM");
        cities.add("AFEQ");
        cities.add("EFRAT");
        cities.add("ARBEL");
        cities.add("ARGAMAN");
        cities.add("EREZ");
        cities.add("ARI'EL");
        cities.add("ARSUF");
        cities.add("ESHBOL");
        cities.add("NAHAL ESHBAL");
        cities.add("ASHDOD");
        cities.add("ASHDOT YA'AQOV(IHUD)");
        cities.add("ASHDOT YA'AQOV(ME'UH");
        cities.add("ESHHAR");
        cities.add("ESHKOLOT");
        cities.add("ESHEL HANASI");
        cities.add("ASHALIM");
        cities.add("ASHQELON");
        cities.add("ASHERAT");
        cities.add("ESHTA'OL");
        cities.add("ETGAR");
        cities.add("BAQA AL-GHARBIYYE");
        cities.add("BE'ER ORA");
        cities.add("BEER GANNIM");
        cities.add("BE'ER TUVEYA");
        cities.add("BE'ER YA'AQOV");
        cities.add("BE'ER MILKA");
        cities.add("BE'ER SHEVA");
        cities.add("BE'EROT YIZHAQ");
        cities.add("BE'EROTAYIM");
        cities.add("BE'ERI");
        cities.add("BUSTAN HAGALIL");
        cities.add("BU'EINE-NUJEIDAT");
        cities.add("BUQ'ATA");
        cities.add("BURGETA");
        cities.add("BAHAN");
        cities.add("BITHA");
        cities.add("BIZZARON");
        cities.add("BIR EL-MAKSUR");
        cities.add("BIR HADAGE");
        cities.add("BIRIYYA");
        cities.add("BET OREN");
        cities.add("BET EL");
        cities.add("BET EL'AZARI");
        cities.add("BET ALFA");
        cities.add("BET ARYE");
        cities.add("BET BERL");
        cities.add("BEIT JANN");
        cities.add("BET GUVRIN");
        cities.add("BET GAMLI'EL");
        cities.add("BET DAGAN");
        cities.add("BET HAGADDI");
        cities.add("BET HALEVI");
        cities.add("BET HILLEL");
        cities.add("BET HAEMEQ");
        cities.add("BET HAARAVA");
        cities.add("BET HASHITTA");
        cities.add("BET ZEID");
        cities.add("BET ZAYIT");
        cities.add("BET ZERA");
        cities.add("BET HORON");
        cities.add("BET HERUT");
        cities.add("BET HILQIYYA");
        cities.add("BET HANAN");
        cities.add("BET HANANYA");
        cities.add("BET HASHMONAY");
        cities.add("BET YEHOSHUA");
        cities.add("BET YOSEF");
        cities.add("BET YANNAY");
        cities.add("BET YIZHAQ-SH. HEFER");
        cities.add("BET LEHEM HAGELILIT");
        cities.add("BET ME'IR");
        cities.add("BET NEHEMYA");
        cities.add("BET NIR");
        cities.add("BET NEQOFA");
        cities.add("BET OVED");
        cities.add("BET UZZI'EL");
        cities.add("BET EZRA");
        cities.add("BET ARIF");
        cities.add("BET ZEVI");
        cities.add("BET QAMA");
        cities.add("BET QESHET");
        cities.add("BET RABBAN");
        cities.add("BET RIMMON");
        cities.add("BET SHE'AN");
        cities.add("BET SHEMESH");
        cities.add("BET SHE'ARIM");
        cities.add("BET SHIQMA");
        cities.add("BITAN AHARON");
        cities.add("BETAR ILLIT");
        cities.add("BALFURIYYA");
        cities.add("BEN ZAKKAY");
        cities.add("BEN AMMI");
        cities.add("BEN SHEMEN(K.NO'AR)");
        cities.add("BEN SHEMEN (MOSHAV)");
        cities.add("BENE BERAQ");
        cities.add("BNE DKALIM");
        cities.add("BENE DAROM");
        cities.add("BENE DEROR");
        cities.add("BENE YEHUDA");
        cities.add("BENE NEZARIM");
        cities.add("BENE ATAROT");
        cities.add("BENE AYISH");
        cities.add("BENE ZIYYON");
        cities.add("BENE RE'EM");
        cities.add("BENAYA");
        cities.add("BINYAMINA");
        cities.add("BASMA");
        cities.add("BASMAT TAB'UN");
        cities.add("BI NE");
        cities.add("BAZRA");
        cities.add("BEZET");
        cities.add("BEQOA");
        cities.add("BEQA'OT");
        cities.add("BAR GIYYORA");
        cities.add("BAR YOHAY");
        cities.add("BRUKHIN");
        cities.add("BEROR HAYIL");
        cities.add("BEROSH");
        cities.add("BERAKHA");
        cities.add("BEREKHYA");
        cities.add("BAR'AM");
        cities.add("BARAQ");
        cities.add("BARQAY");
        cities.add("BARQAN");
        cities.add("BAREQET");
        cities.add("BAT HADAR");
        cities.add("BAT HEN");
        cities.add("BAT HEFER");
        cities.add("BAT HAZOR");
        cities.add("BAT YAM");
        cities.add("BAT AYIN");
        cities.add("BAT SHELOMO");
        cities.add("JUDEIDE-MAKER");
        cities.add("JULIS");
        cities.add("JALJULYE");
        cities.add("JUNNABIB");
        cities.add("JISR AZ-ZARQA");
        cities.add("JISH(GUSH HALAV)");
        cities.add("JAAT");
        cities.add("GE'ULE TEMAN");
        cities.add("GE'ULIM");
        cities.add("GE'ALYA");
        cities.add("GEVULOT");
        cities.add("GEVIM");
        cities.add("GEVA");
        cities.add("GEVA BINYAMIN");
        cities.add("GEVA KARMEL");
        cities.add("GIV'OLIM");
        cities.add("GIV'ON HAHADASHA");
        cities.add("GEVA'OT BAR");
        cities.add("GIV'AT AVNI");
        cities.add("GIV'AT ELA");
        cities.add("GIV'AT BRENNER");
        cities.add("GIV'AT HASHELOSHA");
        cities.add("GIV'AT ZE'EV");
        cities.add("GIV'AT HEN");
        cities.add("GIV'AT HAYYIM (IHUD)");
        cities.add("GIV'AT HAYYIM(ME'UHA");
        cities.add("GIV'AT YO'AV");
        cities.add("GIV'AT YE'ARIM");
        cities.add("GIV'AT YESHA'YAHU");
        cities.add("GIV'AT KOAH");
        cities.add("GIV'AT NILI");
        cities.add("GIV'AT OZ");
        cities.add("GIV'AT SHEMU'EL");
        cities.add("GIV'AT SHEMESH");
        cities.add("GIV'AT SHAPPIRA");
        cities.add("GIV'ATI");
        cities.add("GIV'ATAYIM");
        cities.add("GEVAR'AM");
        cities.add("GEVAT");
        cities.add("GADOT");
        cities.add("GADISH");
        cities.add("GID'ONA");
        cities.add("GEDERA");
        cities.add("GONEN");
        cities.add("GOREN");
        cities.add("GORNOT HAGALIL");
        cities.add("GAZIT");
        cities.add("GEZER");
        cities.add("GE'A");
        cities.add("GIBBETON");
        cities.add("GIZO");
        cities.add("GILON");
        cities.add("GILAT");
        cities.add("GINNOSAR");
        cities.add("GINNEGAR");
        cities.add("GINNATON");
        cities.add("GITTA");
        cities.add("GITTIT");
        cities.add("GAL'ON");
        cities.add("GILGAL");
        cities.add("GELIL YAM");
        cities.add("EVEN YIZHAQ(GAL'ED)");
        cities.add("GIMZO");
        cities.add("GAN HADAROM");
        cities.add("GAN HASHOMERON");
        cities.add("GAN HAYYIM");
        cities.add("GAN YOSHIYYA");
        cities.add("GAN YAVNE");
        cities.add("GAN NER");
        cities.add("GAN SOREQ");
        cities.add("GAN SHELOMO");
        cities.add("GAN SHEMU'EL");
        cities.add("GANNOT");
        cities.add("GANNOT HADAR");
        cities.add("GANNE HADAR");
        cities.add("GANNE TAL");
        cities.add("GANNE YOHANAN");
        cities.add("GANNE MODIIN");
        cities.add("GANNE AM");
        cities.add("GANNE TIQWA");
        cities.add("GA'ASH");
        cities.add("GA'TON");
        cities.add("GEFEN");
        cities.add("GEROFIT");
        cities.add("GESHUR");
        cities.add("GESHER");
        cities.add("GESHER HAZIW");
        cities.add("GAT(QIBBUZ)");
        cities.add("GAT RIMMON");
        cities.add("DALIYAT AL-KARMEL");
        cities.add("DEVORA");
        cities.add("DABBURYE");
        cities.add("DEVIRA");
        cities.add("DAVERAT");
        cities.add("DEGANYA ALEF");
        cities.add("DEGANYA BET");
        cities.add("DOVEV");
        cities.add("DOLEV");
        cities.add("DOR");
        cities.add("DOROT");
        cities.add("DAHI");
        cities.add("DEIR AL-ASAD");
        cities.add("DEIR HANNA");
        cities.add("DEIR RAFAT");
        cities.add("DIMONA");
        cities.add("DISHON");
        cities.add("DALIYYA");
        cities.add("DALTON");
        cities.add("DEMEIDE");
        cities.add("DAN");
        cities.add("DAFNA");
        cities.add("DEQEL");
        cities.add("DERIG'AT");
        cities.add("HAON");
        cities.add("HABONIM");
        cities.add("HAGOSHERIM");
        cities.add("HADAR AM");
        cities.add("HOD HASHARON");
        cities.add("HODIYYA");
        cities.add("HODAYOT");
        cities.add("HAWASHLA");
        cities.add("HUZAYYEL");
        cities.add("HOSHA'AYA");
        cities.add("HAZOREA");
        cities.add("HAZORE'IM");
        cities.add("HAHOTERIM");
        cities.add("HAYOGEV");
        cities.add("HILLA");
        cities.add("HAMA'PIL");
        cities.add("HASOLELIM");
        cities.add("HAOGEN");
        cities.add("HAR ADAR");
        cities.add("HAR GILLO");
        cities.add("HAR AMASA");
        cities.add("HAR'EL");
        cities.add("HARDUF");
        cities.add("HERZELIYYA");
        cities.add("HARARIT");
        cities.add("WERED YERIHO");
        cities.add("WARDON");
        cities.add("ZABARGA");
        cities.add("ZAVDI'EL");
        cities.add("ZOHAR");
        cities.add("ZIQIM");
        cities.add("ZETAN");
        cities.add("ZIKHRON YA'AQOV");
        cities.add("ZEKHARYA");
        cities.add("ZEMER");
        cities.add("ZIMRAT");
        cities.add("ZANOAH");
        cities.add("ZERU'A");
        cities.add("ZARZIR");
        cities.add("ZERAHYA");
        cities.add("KHAWALED");
        cities.add("KHAWALED");
        cities.add("HAVAZZELET HASHARON");
        cities.add("HEVER");
        cities.add("HAGOR");
        cities.add("HAGGAI");
        cities.add("HOGLA");
        cities.add("HAD-NES");
        cities.add("HADID");
        cities.add("HADERA");
        cities.add("HUJEIRAT (DAHRA)");
        cities.add("HULDA");
        cities.add("HOLON");
        cities.add("HOLIT");
        cities.add("HULATA");
        cities.add("HOSEN");
        cities.add("HUSSNIYYA");
        cities.add("HOFIT");
        cities.add("HUQOQ");
        cities.add("HURA");
        cities.add("HURFEISH");
        cities.add("HORESHIM");
        cities.add("HAZON");
        cities.add("HIBBAT ZIYYON");
        cities.add("HINNANIT");
        cities.add("HAIFA");
        cities.add("HERUT");
        cities.add("HALUZ");
        cities.add("HELEZ");
        cities.add("HAMAM");
        cities.add("HEMED");
        cities.add("HAMADYA");
        cities.add("NAHAL HEMDAT");
        cities.add("HAMRA");
        cities.add("HANNI'EL");
        cities.add("HANITA");
        cities.add("HANNATON");
        cities.add("HASPIN");
        cities.add("HAFEZ HAYYIM");
        cities.add("HEFZI-BAH");
        cities.add("HAZAV");
        cities.add("HAZEVA");
        cities.add("HAZOR HAGELILIT");
        cities.add("HAZOR-ASHDOD");
        cities.add("HAZAR BE'EROTAYIM");
        cities.add("HAZROT HULDA");
        cities.add("HAZROT YASAF");
        cities.add("HAZROT KOAH");
        cities.add("HAZERIM");
        cities.add("HEREV LE'ET");
        cities.add("HARUZIM");
        cities.add("HARISH");
        cities.add("HERMESH");
        cities.add("HARASHIM");
        cities.add("HASHMONA'IM");
        cities.add("TIBERIAS");
        cities.add("TUBA-ZANGARIYYE");
        cities.add("TUR'AN");
        cities.add("TAYIBE");
        cities.add("TAYIBE(BAEMEQ)");
        cities.add("TIRE");
        cities.add("TIRAT YEHUDA");
        cities.add("TIRAT KARMEL");
        cities.add("TIRAT ZEVI");
        cities.add("TAL SHAHAR");
        cities.add("TAL-EL");
        cities.add("TELALIM");
        cities.add("TALMON");
        cities.add("TAMRA");
        cities.add("TAMRA (YIZRE'EL)");
        cities.add("TENE");
        cities.add("TEFAHOT");
        cities.add("YANUH-JAT");
        cities.add("YEVUL");
        cities.add("YAVNE'EL");
        cities.add("YAVNE");
        cities.add("YAGUR");
        cities.add("YAGEL");
        cities.add("YAD BINYAMIN");
        cities.add("YAD HASHEMONA");
        cities.add("YAD HANNA");
        cities.add("YAD MORDEKHAY");
        cities.add("YAD NATAN");
        cities.add("YAD RAMBAM");
        cities.add("YEDIDA");
        cities.add("YEHUD-MONOSON");
        cities.add("YAHEL");
        cities.add("YUVAL");
        cities.add("YUVALIM");
        cities.add("YODEFAT");
        cities.add("YONATAN");
        cities.add("YOSHIVYA");
        cities.add("YIZRE'EL");
        cities.add("YEHI'AM");
        cities.add("YOTVATA");
        cities.add("YITAV");
        cities.add("YAKHINI");
        cities.add("YANUV");
        cities.add("YINNON");
        cities.add("YESUD HAMA'ALA");
        cities.add("YESODOT");
        cities.add("YAS'UR");
        cities.add("YA'AD");
        cities.add("YA'EL");
        cities.add("YE'AF");
        cities.add("YA'ARA");
        cities.add("YAFI");
        cities.add("YAFIT");
        cities.add("YIF'AT");
        cities.add("YIFTAH");
        cities.add("YIZHAR");
        cities.add("YAZIZ");
        cities.add("YAQUM");
        cities.add("YAQIR");
        cities.add("YOQNE'AM(MOSHAVA)");
        cities.add("YOQNE'AM ILLIT");
        cities.add("YIR'ON");
        cities.add("YARDENA");
        cities.add("YEROHAM");
        cities.add("JERUSALEM");
        cities.add("YARHIV");
        cities.add("YIRKA");
        cities.add("YARQONA");
        cities.add("YESHA");
        cities.add("YISH'I");
        cities.add("YASHRESH");
        cities.add("YATED");
        cities.add("KABUL");
        cities.add("KAOKAB ABU AL-HIJA");
        cities.add("KABRI");
        cities.add("KADOORIE");
        cities.add("KADDITA");
        cities.add("KOKHAV HASHAHAR");
        cities.add("KOKHAV YA'IR");
        cities.add("KOKHAV YA'AQOV");
        cities.add("KOKHAV MIKHA'EL");
        cities.add("KORAZIM");
        cities.add("KAHAL");
        cities.add("KOCHLEA");
        cities.add("KISSUFIM");
        cities.add("KISHOR");
        cities.add("KELIL");
        cities.add("KALLANIT");
        cities.add("KEMEHIN");
        cities.add("KAMMON");
        cities.add("KANNOT");
        cities.add("KANAF");
        cities.add("KINNERET(MOSHAVA)");
        cities.add("KINNERET(QEVUZA)");
        cities.add("KUSEIFE");
        cities.add("KESALON");
        cities.add("KISRA-SUMEI");
        cities.add("KA'ABIYYE-TABBASH-HA");
        cities.add("KEFAR AVIV");
        cities.add("KEFAR ADUMMIM");
        cities.add("KEFAR URIYYA");
        cities.add("KEFAR AHIM");
        cities.add("KEFAR BIALIK");
        cities.add("KEFAR BILU");
        cities.add("KEFAR BLUM");
        cities.add("KEFAR BIN NUN");
        cities.add("KAFAR BARA");
        cities.add("KEFAR BARUKH");
        cities.add("KEFAR GID'ON");
        cities.add("KEFAR GALLIM");
        cities.add("KEFAR GLIKSON");
        cities.add("KEFAR GIL'ADI");
        cities.add("KEFAR DANIYYEL");
        cities.add("KEFAR HAORANIM");
        cities.add("KEFAR HAHORESH");
        cities.add("KEFAR HAMAKKABI");
        cities.add("KEFAR HANAGID");
        cities.add("KEFAR HANO'AR HADATI");
        cities.add("KEFAR HANASI");
        cities.add("KEFAR HESS");
        cities.add("KEFAR HARO'E");
        cities.add("KEFAR HARIF");
        cities.add("KEFAR VITKIN");
        cities.add("KEFAR WARBURG");
        cities.add("KEFAR WERADIM");
        cities.add("KEFAR ZOHARIM");
        cities.add("KEFAR ZETIM");
        cities.add("KEFAR HABAD");
        cities.add("KEFAR HOSHEN");
        cities.add("KEFAR HITTIM");
        cities.add("KEFAR HAYYIM");
        cities.add("KEFAR HANANYA");
        cities.add("KEFAR HASIDIM ALEF");
        cities.add("KEFAR HASIDIM BET");
        cities.add("KEFAR HARUV");
        cities.add("KEFAR TRUMAN");
        cities.add("KAFAR YASIF");
        cities.add("YEDIDYA");
        cities.add("KEFAR YEHOSHUA");
        cities.add("KEFAR YONA");
        cities.add("KEFAR YEHEZQEL");
        cities.add("KEFAR YA'BEZ");
        cities.add("KAFAR KAMA");
        cities.add("KAFAR KANNA");
        cities.add("KEFAR MONASH");
        cities.add("KEFAR MAYMON");
        cities.add("KEFAR MALAL");
        cities.add("KAFAR MANDA");
        cities.add("KEFAR MENAHEM");
        cities.add("KEFAR MASARYK");
        cities.add("KAFAR MISR");
        cities.add("KEFAR MORDEKHAY");
        cities.add("KEFAR NETTER");
        cities.add("KEFAR SZOLD");
        cities.add("KEFAR SAVA");
        cities.add("KEFAR SILVER");
        cities.add("KEFAR SIRKIN");
        cities.add("KEFAR AVODA");
        cities.add("KEFAR AZZA");
        cities.add("KEFAR EZYON");
        cities.add("KEFAR PINES");
        cities.add("KAFAR QASEM");
        cities.add("KEFAR KISH");
        cities.add("KAFAR QARA");
        cities.add("KEFAR ROSH HANIQRA");
        cities.add("KEFAR ROZENWALD(ZAR.");
        cities.add("KEFAR RUPPIN");
        cities.add("KEFAR RUT");
        cities.add("KEFAR SHAMMAY");
        cities.add("KEFAR SHEMU'EL");
        cities.add("KEFAR SHEMARYAHU");
        cities.add("KEFAR TAVOR");
        cities.add("KEFAR TAPPUAH");
        cities.add("KARE DESHE");
        cities.add("KARKOM");
        cities.add("KEREM BEN ZIMRA");
        cities.add("KEREM BEN SHEMEN");
        cities.add("KEREM YAVNE(YESHIVA)");
        cities.add("KEREM MAHARAL");
        cities.add("KEREM SHALOM");
        cities.add("KARME YOSEF");
        cities.add("KARME ZUR");
        cities.add("KARME QATIF");
        cities.add("KARMI'EL");
        cities.add("KARMIYYA");
        cities.add("KERAMIM");
        cities.add("KARMEL");
        cities.add("LAVON");
        cities.add("LAVI");
        cities.add("LIVNIM");
        cities.add("LAHAV");
        cities.add("LAHAVOT HABASHAN");
        cities.add("LAHAVOT HAVIVA");
        cities.add("LEHAVIM");
        cities.add("LOD");
        cities.add("LUZIT");
        cities.add("LOHAME HAGETA'OT");
        cities.add("LOTEM");
        cities.add("LOTAN");
        cities.add("LIMAN");
        cities.add("LAKHISH");
        cities.add("LAPPID");
        cities.add("LAPPIDOT");
        cities.add("LAQYE");
        cities.add("MA'OR");
        cities.add("ME'IR SHEFEYA");
        cities.add("MEVO BETAR");
        cities.add("MEVO DOTAN");
        cities.add("MEVO HORON");
        cities.add("MEVO HAMMA");
        cities.add("MEVO MODI'IM");
        cities.add("MEVO'OT YAM");
        cities.add("MEVO'OT YERIHO");
        cities.add("MABBU'IM");
        cities.add("MIVTAHIM");
        cities.add("MAVQI'IM");
        cities.add("MEVASSERET ZIYYON");
        cities.add("MAJD AL-KURUM");
        cities.add("MAJDAL SHAMS");
        cities.add("MUGHAR");
        cities.add("MEGADIM");
        cities.add("MIGDAL");
        cities.add("MIGDAL HAEMEQ");
        cities.add("MIGDAL OZ");
        cities.add("MIGDALIM");
        cities.add("MEGIDDO");
        cities.add("MAGGAL");
        cities.add("MAGEN");
        cities.add("MAGEN SHA'UL");
        cities.add("MAGSHIMIM");
        cities.add("MIDRAKH OZ");
        cities.add("MIDRESHET BEN GURION");
        cities.add("MIDRESHET RUPPIN");
        cities.add("MODI'IN ILLIT");
        cities.add("MODI'IN-MAKKABBIM-RE");
        cities.add("MOLEDET");
        cities.add("MOZA ILLIT");
        cities.add("MUQEIBLE");
        cities.add("MORAN");
        cities.add("MORESHET");
        cities.add("MAZOR");
        cities.add("MAZKERET BATYA");
        cities.add("MIZRA");
        cities.add("MAZRA'A");
        cities.add("MEHOLA");
        cities.add("MAHANE HILLA");
        cities.add("MAHANE TALI");
        cities.add("MAHANE YEHUDIT");
        cities.add("MAHANE YOKHVED");
        cities.add("MAHANE YAFA");
        cities.add("MAHANE YATTIR");
        cities.add("MAHANE MIRYAM");
        cities.add("MAHANE TEL NOF");
        cities.add("MAHANAYIM");
        cities.add("MAHSEYA");
        cities.add("METULA");
        cities.add("MATTA");
        cities.add("ME AMMI");
        cities.add("METAV");
        cities.add("MEISER");
        cities.add("MEZAR");
        cities.add("MERAV");
        cities.add("MERON");
        cities.add("MESHAR");
        cities.add("METAR");
        cities.add("MEKHORA");
        cities.add("MAKCHUL");
        cities.add("MIKHMORET");
        cities.add("MIKHMANNIM");
        cities.add("MELE'A");
        cities.add("MELILOT");
        cities.add("MALKIYYA");
        cities.add("MALKISHUA");
        cities.add("MENUHA");
        cities.add("MANOF");
        cities.add("MANOT");
        cities.add("MENAHEMYA");
        cities.add("MENNARA");
        cities.add("MANSHIYYET ZABDA");
        cities.add("MASSAD");
        cities.add("MASSADA");
        cities.add("MESILLOT");
        cities.add("MESILLAT ZIYYON");
        cities.add("MASLUL");
        cities.add("MAS'ADE");
        cities.add("MAS'UDIN AL-'AZAZME");
        cities.add("MA'BAROT");
        cities.add("MA'GALIM");
        cities.add("MA'AGAN");
        cities.add("MA'AGAN MIKHA'EL");
        cities.add("MA'OZ HAYYIM");
        cities.add("MA'ON");
        cities.add("ME'ONA");
        cities.add("MI'ELYA");
        cities.add("MA'YAN BARUKH");
        cities.add("MA'YAN ZEVI");
        cities.add("MA'ALE ADUMMIM");
        cities.add("MA'ALE EFRAYIM");
        cities.add("MA'ALE GILBOA");
        cities.add("MA'ALE GAMLA");
        cities.add("MA'ALE HAHAMISHA");
        cities.add("MA'ALE LEVONA");
        cities.add("MA'ALE MIKHMAS");
        cities.add("MA'ALE IRON");
        cities.add("MA'ALE AMOS");
        cities.add("MA'ALE SHOMERON");
        cities.add("MA'ALOT-TARSHIHA");
        cities.add("MA'ANIT");
        cities.add("MA'AS");
        cities.add("MEFALLESIM");
        cities.add("MEZADOT YEHUDA");
        cities.add("MAZZUVA");
        cities.add("MAZLIAH");
        cities.add("MIZPA");
        cities.add("MIZPE AVIV");
        cities.add("MITSPE ILAN");
        cities.add("MIZPE YERIHO");
        cities.add("MIZPE NETOFA");
        cities.add("MIZPE RAMON");
        cities.add("MIZPE SHALEM");
        cities.add("MEZER");
        cities.add("MIQWE YISRA'EL");
        cities.add("MARGALIYYOT");
        cities.add("MEROM GOLAN");
        cities.add("MERHAV AM");
        cities.add("MERHAVYA(MOSHAV)");
        cities.add("MERHAVYA(QIBBUZ)");
        cities.add("MERKAZ SHAPPIRA");
        cities.add("MASH'ABBE SADE");
        cities.add("MISGAV DOV");
        cities.add("MISGAV AM");
        cities.add("MESHHED");
        cities.add("MASSU'A");
        cities.add("MASSUOT YIZHAQ");
        cities.add("MASKIYYOT");
        cities.add("MISHMAR AYYALON");
        cities.add("MISHMAR DAWID");
        cities.add("MISHMAR HAYARDEN");
        cities.add("MISHMAR HANEGEV");
        cities.add("MISHMAR HAEMEQ");
        cities.add("MISHMAR HASHIV'A");
        cities.add("MISHMAR HASHARON");
        cities.add("MISHMAROT");
        cities.add("MISHMERET");
        cities.add("MASH'EN");
        cities.add("MATTAN");
        cities.add("MATTAT");
        cities.add("MATTITYAHU");
        cities.add("NE'OT GOLAN");
        cities.add("NE'OT HAKIKKAR");
        cities.add("NE'OT MORDEKHAY");
        cities.add("SHIZZAFON");
        cities.add("NA'URA");
        cities.add("NEVATIM");
        cities.add("NEGBA");
        cities.add("NEGOHOT");
        cities.add("NEHORA");
        cities.add("NAHALAL");
        cities.add("NAHARIYYA");
        cities.add("NOV");
        cities.add("NOGAH");
        cities.add("NEVE TSUF");
        cities.add("NAVE");
        cities.add("NEWE AVOT");
        cities.add("NEWE UR");
        cities.add("NEWE ATIV");
        cities.add("NEWE ILAN");
        cities.add("NEWE ETAN");
        cities.add("NEWE DANIYYEL");
        cities.add("NEWE ZOHAR");
        cities.add("NEWE ZIV");
        cities.add("NEWE HARIF");
        cities.add("NEWE YAM");
        cities.add("NEWE YAMIN");
        cities.add("NEWE YARAQ");
        cities.add("NEWE MIVTAH");
        cities.add("NEWE MIKHA'EL");
        cities.add("NEWE SHALOM");
        cities.add("NO'AM");
        cities.add("NOF AYYALON");
        cities.add("NOF HAGALIL");
        cities.add("NOFIM");
        cities.add("NOFIT");
        cities.add("NOFEKH");
        cities.add("NOQEDIM");
        cities.add("NORDIYYA");
        cities.add("NURIT");
        cities.add("NEHUSHA");
        cities.add("NAHAL OZ");
        cities.add("NAHALA");
        cities.add("NAHALI'EL");
        cities.add("NEHALIM");
        cities.add("NAHAM");
        cities.add("NAHEF");
        cities.add("NAHSHOLIM");
        cities.add("NAHSHON");
        cities.add("NAHSHONIM");
        cities.add("NETU'A");
        cities.add("NATUR");
        cities.add("NETA");
        cities.add("NETA'IM");
        cities.add("NATAF");
        cities.add("NEIN");
        cities.add("NILI");
        cities.add("NIZZAN");
        cities.add("NIZZAN B");
        cities.add("NIZZANA (QEHILAT HIN");
        cities.add("NIZZANE SINAY");
        cities.add("NIZZANE OZ");
        cities.add("NIZZANIM");
        cities.add("NIR ELIYYAHU");
        cities.add("NIR BANIM");
        cities.add("NIR GALLIM");
        cities.add("NIR DAWID (TEL AMAL)");
        cities.add("NIR HEN");
        cities.add("NIR YAFE");
        cities.add("NIR YIZHAQ");
        cities.add("NIR YISRA'EL");
        cities.add("NIR MOSHE");
        cities.add("NIR OZ");
        cities.add("NIR AM");
        cities.add("NIR EZYON");
        cities.add("NIR AQIVA");
        cities.add("NIR ZEVI");
        cities.add("NIRIM");
        cities.add("NIRIT");
        cities.add("NIMROD");
        cities.add("NES HARIM");
        cities.add("NES AMMIM");
        cities.add("NES ZIYYONA");
        cities.add("NE'URIM");
        cities.add("NA'ALE");
        cities.add("NAAMA");
        cities.add("NA'AN");
        cities.add("NA'ARAN");
        cities.add("NASASRA");
        cities.add("NEZER HAZZANI");
        cities.add("NEZER SERENI");
        cities.add("NAZARETH");
        cities.add("NESHER");
        cities.add("NETIV HAGEDUD");
        cities.add("NETIV HALAMED-HE");
        cities.add("NETIV HAASARA");
        cities.add("NETIV HASHAYYARA");
        cities.add("NETIVOT");
        cities.add("NETANYA");
        cities.add("SAJUR");
        cities.add("SASA");
        cities.add("SAVYON");
        cities.add("SEGULA");
        cities.add("SAWA'ID(HAMRIYYE)");
        cities.add("SAWA'ID (KAMANE)");
        cities.add("SULAM");
        cities.add("SUSEYA");
        cities.add("SUFA");
        cities.add("SAKHNIN");
        cities.add("SAYYID");
        cities.add("SALLAMA");
        cities.add("SAL'IT");
        cities.add("SAMAR");
        cities.add("SA'AD");
        cities.add("SA'WA");
        cities.add("SA'AR");
        cities.add("SAPPIR");
        cities.add("SITRIYYA");
        cities.add("GHAJAR");
        cities.add("AVDON");
        cities.add("EVRON");
        cities.add("AGUR");
        cities.add("ADI");
        cities.add("ADANIM");
        cities.add("UZA");
        cities.add("UZEIR");
        cities.add("OLESH");
        cities.add("OMER");
        cities.add("OFER");
        cities.add("OFRA");
        cities.add("OZEM");
        cities.add("UQBI (BANU UQBA)");
        cities.add("EZUZ");
        cities.add("EZER");
        cities.add("AZRI'EL");
        cities.add("AZARYA");
        cities.add("AZRIQAM");
        cities.add("ATAWNE");
        cities.add("ATERET");
        cities.add("IDDAN");
        cities.add("EILABUN");
        cities.add("AYANOT");
        cities.add("ILUT");
        cities.add("EN AYYALA");
        cities.add("EIN AL-ASAD");
        cities.add("EN GEV");
        cities.add("EN GEDI");
        cities.add("EN DOR");
        cities.add("EN HABESOR");
        cities.add("EN HOD");
        cities.add("EN HAHORESH");
        cities.add("EN HAMIFRAZ");
        cities.add("EN HANAZIV");
        cities.add("EN HAEMEQ");
        cities.add("EN HASHOFET");
        cities.add("EN HASHELOSHA");
        cities.add("EN WERED");
        cities.add("EN ZIWAN");
        cities.add("EIN HOD");
        cities.add("EN HAZEVA");
        cities.add("EN HAROD (IHUD)");
        cities.add("EN HAROD(ME'UHAD)");
        cities.add("EN YAHAV");
        cities.add("EN YA'AQOV");
        cities.add("EN KAREM-B.S.HAQLA'I");
        cities.add("EN KARMEL");
        cities.add("EIN MAHEL");
        cities.add("EIN NAQQUBA");
        cities.add("EN IRON");
        cities.add("EN ZURIM");
        cities.add("EIN QINIYYE");
        cities.add("EIN RAFA");
        cities.add("EN SHEMER");
        cities.add("EN SARID");
        cities.add("EN TAMAR");
        cities.add("ENAT");
        cities.add("IR OVOT");
        cities.add("AKKO");
        cities.add("ALUMIM");
        cities.add("ELI");
        cities.add("ALE ZAHAV");
        cities.add("ALMA");
        cities.add("ALMON");
        cities.add("AMUQQA");
        cities.add("AMMIHAY");
        cities.add("AMMINADAV");
        cities.add("AMMI'AD");
        cities.add("AMMI'OZ");
        cities.add("AMMIQAM");
        cities.add("AMIR");
        cities.add("IMMANU'EL");
        cities.add("AMQA");
        cities.add("ENAV");
        cities.add("ISIFYA");
        cities.add("AFULA");
        cities.add("EZ EFRAYIM");
        cities.add("ATSMON-SEGEV");
        cities.add("ARRABE");
        cities.add("ARAMSHA");
        cities.add("ARRAB AL NAIM");
        cities.add("ARAD");
        cities.add("ARUGOT");
        cities.add("AR'ARA");
        cities.add("AR'ARA-BANEGEV");
        cities.add("ASERET");
        cities.add("ATLIT");
        cities.add("OTNI'EL");
        cities.add("PARAN");
        cities.add("PEDU'EL");
        cities.add("PEDUYIM");
        cities.add("PEDAYA");
        cities.add("PORIYYA-KEFAR AVODA");
        cities.add("PORIYYA-NEWE OVED");
        cities.add("PORIYYA ILLIT");
        cities.add("FUREIDIS");
        cities.add("PORAT");
        cities.add("PATTISH");
        cities.add("PELEKH");
        cities.add("PALMAHIM");
        cities.add("PENE HEVER");
        cities.add("PESAGOT");
        cities.add("FASSUTA");
        cities.add("PA'AME TASHAZ");
        cities.add("PEZA'EL");
        cities.add("PEQI'IN (BUQEI'A)");
        cities.add("PEQI'IN HADASHA");
        cities.add("PARDES HANNA-KARKUR");
        cities.add("PARDESIYYA");
        cities.add("PAROD");
        cities.add("PERAZON");
        cities.add("PERI GAN");
        cities.add("PETAH TIQWA");
        cities.add("PETAHYA");
        cities.add("ZE'ELIM");
        cities.add("ZVIYYA");
        cities.add("ZIV'ON");
        cities.add("ZOVA");
        cities.add("ZOHAR");
        cities.add("ZOFIYYA");
        cities.add("ZUFIN");
        cities.add("ZOFIT");
        cities.add("ZOFAR");
        cities.add("SHOSHANNAT HAAMAQIM(");
        cities.add("MAHANE BILDAD");
        cities.add("ZUR HADASSA");
        cities.add("ZUR YIZHAQ");
        cities.add("ZUR MOSHE");
        cities.add("ZUR NATAN");
        cities.add("ZURI'EL");
        cities.add("ZURIT");
        cities.add("ZIPPORI");
        cities.add("ZELAFON");
        cities.add("SANDALA");
        cities.add("ZAFRIYYA");
        cities.add("ZAFRIRIM");
        cities.add("ZEFAT");
        cities.add("ZERUFA");
        cities.add("ZOR'A");
        cities.add("QABBO'A");
        cities.add("QEVUZAT YAVNE");
        cities.add("QEDUMIM");
        cities.add("QADIMA-ZORAN");
        cities.add("QEDMA");
        cities.add("QIDMAT ZEVI");
        cities.add("QEDAR");
        cities.add("QIDRON");
        cities.add("QADDARIM");
        cities.add("QUDEIRAT AS-SANI");
        cities.add("QAWA'IN");
        cities.add("QOMEMIYYUT");
        cities.add("QORANIT");
        cities.add("QETURA");
        cities.add("QESARIYYA");
        cities.add("QELAHIM");
        cities.add("QALYA");
        cities.add("QALANSAWE");
        cities.add("QELA");
        cities.add("QAZIR");
        cities.add("QAZRIN");
        cities.add("QIRYAT ONO");
        cities.add("QIRYAT ARBA");
        cities.add("QIRYAT ATTA");
        cities.add("QIRYAT BIALIK");
        cities.add("QIRYAT GAT");
        cities.add("QIRYAT TIV'ON");
        cities.add("QIRYAT YAM");
        cities.add("QIRYAT YE'ARIM");
        cities.add("QIRYAT YE'ARIM(INSTI");
        cities.add("QIRYAT MOTZKIN");
        cities.add("QIRYAT MAL'AKHI");
        cities.add("QIRYAT NETAFIM");
        cities.add("QIRYAT ANAVIM");
        cities.add("QIRYAT EQRON");
        cities.add("QIRYAT SHELOMO");
        cities.add("QIRYAT SHEMONA");
        cities.add("QARNE SHOMERON");
        cities.add("QESHET");
        cities.add("RAME");
        cities.add("RAS AL-EIN");
        cities.add("RAS ALI");
        cities.add("ROSH HAAYIN");
        cities.add("ROSH PINNA");
        cities.add("ROSH ZURIM");
        cities.add("RISHON LEZIYYON");
        cities.add("REVAVA");
        cities.add("REVADIM");
        cities.add("REVIVIM");
        cities.add("RAVID");
        cities.add("REGBA");
        cities.add("REGAVIM");
        cities.add("RAHAT");
        cities.add("REWAHA");
        cities.add("REWAYA");
        cities.add("RUAH MIDBAR");
        cities.add("RUHAMA");
        cities.add("RUMMANE");
        cities.add("RUMAT HEIB");
        cities.add("RO'I");
        cities.add("ROTEM");
        cities.add("REHOV");
        cities.add("REHOVOT");
        cities.add("REHELIM");
        cities.add("REIHANIYYE");
        cities.add("REHAN");
        cities.add("REINE");
        cities.add("RIMMONIM");
        cities.add("RINNATYA");
        cities.add("REKHASIM");
        cities.add("RAM-ON");
        cities.add("RAMOT");
        cities.add("RAMOT HASHAVIM");
        cities.add("RAMOT ME'IR");
        cities.add("RAMOT MENASHE");
        cities.add("RAMOT NAFTALI");
        cities.add("RAMLA");
        cities.add("RAMAT GAN");
        cities.add("RAMAT DAWID");
        cities.add("RAMAT HAKOVESH");
        cities.add("RAMAT HASHOFET");
        cities.add("RAMAT HASHARON");
        cities.add("RAMAT YOHANAN");
        cities.add("RAMAT YISHAY");
        cities.add("RAMAT MAGSHIMIM");
        cities.add("RAMAT ZEVI");
        cities.add("RAMAT RAZI'EL");
        cities.add("RAMAT RAHEL");
        cities.add("RANNEN");
        cities.add("RE'IM");
        cities.add("RA'ANANA");
        cities.add("RAQEFET");
        cities.add("RISHPON");
        cities.add("RESHAFIM");
        cities.add("RETAMIM");
        cities.add("SHE'AR YASHUV");
        cities.add("SHAVE DAROM");
        cities.add("SHAVE ZIYYON");
        cities.add("SHAVE SHOMERON");
        cities.add("SHIBLI");
        cities.add("SEGEV-SHALOM");
        cities.add("SEDE ILAN");
        cities.add("SEDE ELIYYAHU");
        cities.add("SEDE ELI'EZER");
        cities.add("SEDE BOQER");
        cities.add("SEDE DAWID");
        cities.add("SEDE WARBURG");
        cities.add("SEDE YO'AV");
        cities.add("SEDE YA'AQOV");
        cities.add("SEDE YIZHAQ");
        cities.add("SEDE MOSHE");
        cities.add("SEDE NAHUM");
        cities.add("SEDE NEHEMYA");
        cities.add("SEDE NIZZAN");
        cities.add("SEDE UZZIYYAHU");
        cities.add("SEDE ZEVI");
        cities.add("SEDOT YAM");
        cities.add("SEDOT MIKHA");
        cities.add("SEDE AVRAHAM");
        cities.add("SEDE HEMED");
        cities.add("SEDE TERUMOT");
        cities.add("SHEDEMA");
        cities.add("SHADMOT DEVORA");
        cities.add("SHADMOT MEHOLA");
        cities.add("SEDEROT");
        cities.add("SHO'EVA");
        cities.add("SHUVA");
        cities.add("SHOVAL");
        cities.add("SHOHAM");
        cities.add("SHOMERA");
        cities.add("SHOMERIYYA");
        cities.add("SHOQEDA");
        cities.add("SHORESH");
        cities.add("SHORASHIM");
        cities.add("SHOSHANNAT HAAMAQIM");
        cities.add("SHEZOR");
        cities.add("SHAHAR");
        cities.add("SHAHARUT");
        cities.add("SHIBBOLIM");
        cities.add("NAHAL SHITTIM");
        cities.add("SHEIKH DANNUN");
        cities.add("SHILO");
        cities.add("SHILAT");
        cities.add("SHEKHANYA");
        cities.add("SHALWA");
        cities.add("SHALVA BAMIDBAR");
        cities.add("SHELUHOT");
        cities.add("SHELOMI");
        cities.add("SHLOMIT");
        cities.add("SHAMIR");
        cities.add("SHIM'A");
        cities.add("SHAMERAT");
        cities.add("SHIMSHIT");
        cities.add("SHANI");
        cities.add("SENIR");
        cities.add("SHA'AB");
        cities.add("SE'ORIM");
        cities.add("SHA'AL");
        cities.add("SHA'ALVIM");
        cities.add("SHA'AR EFRAYIM");
        cities.add("SHA'AR HAGOLAN");
        cities.add("SHA'AR HAAMAQIM");
        cities.add("SHA'AR MENASHE");
        cities.add("SHA'ARE TIQWA");
        cities.add("SHEFAYIM");
        cities.add("SHAFIR");
        cities.add("SHEFER");
        cities.add("SHEFAR'AM");
        cities.add("SHAQED");
        cities.add("SHEQEF");
        cities.add("SHARONA");
        cities.add("LI-ON");
        cities.add("SARID");
        cities.add("SHARSHERET");
        cities.add("SHETULA");
        cities.add("SHETULIM");
        cities.add("TE'ASHUR");
        cities.add("TIDHAR");
        cities.add("TUVAL");
        cities.add("TOMER");
        cities.add("TUSHIYYA");
        cities.add("TIMMORIM");
        cities.add("TIROSH");
        cities.add("TEL AVIV - YAFO");
        cities.add("TEL YOSEF");
        cities.add("TEL YIZHAQ");
        cities.add("TEL MOND");
        cities.add("TEL ADASHIM");
        cities.add("TEL QAZIR");
        cities.add("TEL SHEVA");
        cities.add("TEL TE'OMIM");
        cities.add("TELEM");
        cities.add("TALME ELIYYAHU");
        cities.add("TALME EL'AZAR");
        cities.add("TALME BILU");
        cities.add("TALME YOSEF");
        cities.add("TALME YEHI'EL");
        cities.add("TALME YAFE");
        cities.add("TELAMIM");
        cities.add("TIMRAT");
        cities.add("TENUVOT");
        cities.add("TA'OZ");
        cities.add("TIFRAH");
        cities.add("TEQUMA");
        cities.add("TEQOA");
        cities.add("TARABIN AS-SANI");
        cities.add("TARUM");
        Collections.sort(cities);
    }
}





