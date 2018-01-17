package com.example.myapplication.View;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Model.Person;
import com.example.myapplication.R;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GmailContacts extends AppCompatActivity {

    Person person;
    String personJson;
    ArrayList<Person> contacts;
    ArrayList<Person> eqcontacts;
    Map<String, String> gcontacts;

    Intent me;
    ObjectMapper mapper;
    AbsListView.OnScrollListener onScrollListener;
    FloatingActionButton fab;
    LayoutInflater layOutInflater;
    ListView contactsListView;
    ContactsListAdapter contactsListAdapter;
    Object mThis;
    Dialog addContactDialog;
    AlertDialog.Builder addContactDialogBuilder;
    AlertDialog addContactAlertDialog;
    AlertDialog.Builder confirmationDialogBuilder;
    AlertDialog confirmationAlertDialog;

    Animation fabHide, fabUnHide;
    boolean hidden = false;

    CountDownTimer timer;

    private int readContactsRequest = 10;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mThis = this;
        me = getIntent();
        personJson = me.getStringExtra("person");

        mapper = new ObjectMapper();
        try {
            person = mapper.readValue(personJson, Person.class);
        }   catch (IOException e) {
            e.printStackTrace();
        }

        if (person == null) {
            finish();
        }

        progressBar = findViewById(R.id.contactsGmailProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate();

        Toolbar tb = findViewById(R.id.contactsGmailToolbar);
        tb.setTitle("My Contacts");
        tb.setSubtitle(person.firstName + " " + person.lastName);

        contacts = new ArrayList<>();
        eqcontacts = new ArrayList<>();
        gcontacts = new LinkedHashMap<>();

        fillEqContactsList(person.id, false);

        //readContacts();

        fab = (FloatingActionButton) findViewById(R.id.fabAddContact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContactDialog();
            }
        });

        fabHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide);
        fabUnHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.unhide);

        onScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 1) {
                    if (!hidden) {
                        fab.startAnimation(fabHide);
                        fab.setClickable(false);
                        hidden = true;
                    }
                }   else {
                    startTimer(2000);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        };
    }

    private void startTimer(long time){
        if (timer != null)
            timer.cancel();

        timer = new CountDownTimer(time, time){
            public void onTick(long millisUntilDone){

            }

            public void onFinish() {
                fab.startAnimation(fabUnHide);
                fab.setClickable(true);
                hidden = false;
            }
        }.start();
    }

    public void reBuild () {
        contacts = new ArrayList<>();
        eqcontacts = new ArrayList<>();
        gcontacts = new LinkedHashMap<>();

        fillEqContactsList(person.id, true);

    }

    private class LoadPhoneContacts extends AsyncTask <Boolean, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean[] objects) {

            readContacts(objects[0]);

            return objects[0];
        }

        @Override
        protected void onPostExecute(Boolean force) {

            setContactsList (force);

            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    public void readContacts(boolean force){

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                /*if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {*/
                    //Log.i("name", name + ", ID : " + id);

                    // get the phone number
                    /*Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Log.i("phone", phone);
                    }
                    pCur.close();*/


                    // get email and type

                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        String email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        if (email.contains("gmail.com")) {
                            Log.i("name", name);
                            Log.i("Email ", email);
                            Person p = new Person();
                            p.firstName = name;
                            p.email = email;
                            gcontacts.put(email, name);
                        }
                    }
                    emailCur.close();

                    // Get note.......
                    /*String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                    if (noteCur.moveToFirst()) {
                        String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        //Log.i("Note", note);
                    }
                    noteCur.close();*/

                    //Get Postal Address....

                    /*String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] addrWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, null, null, null);
                    while(addrCur.moveToNext()) {
                        String poBox = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                        String street = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        String city = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                        String state = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                        String postalCode = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                        String country = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                        String type = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

                        // Do something with these....

                    }
                    addrCur.close();*/

                    // Get Instant Messenger.........
                    /*String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] imWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                    Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, imWhere, imWhereParams, null);
                    if (imCur.moveToFirst()) {
                        String imName = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                        String imType;
                        imType = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                    }
                    imCur.close();*/

                    // Get Organizations.........

                    /*String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, orgWhere, orgWhereParams, null);
                    if (orgCur.moveToFirst()) {
                        String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                        String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                    }
                    orgCur.close();*/
                //}
            }
        }
    }







    public void fillEqContactsList (final long pId, final boolean force) {

        PersonServices personServices = new PersonServices();

        personServices.contacts(this, pId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            eqcontacts = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Person.class));
                            if (eqcontacts != null) {

                                /*if (!force) setContactsListView();
                                else forceContactsListView();*/

                                //readContacts();
                                new LoadPhoneContacts().execute(force);


                            }
                        }   catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
    }

    public void setContactsList (boolean force) {
        for (Person p : eqcontacts) {
            contacts.add(p);
        }

        for (Map.Entry<String, String> entry : gcontacts.entrySet()) {
            if (isOnList(entry.getKey(), contacts) == null) {
                Person p = new Person();
                p.email = entry.getKey();
                p.firstName = entry.getValue();
                contacts.add(p);
            }
        }

        if (!force)
            setContactsListView();
        else
            forceContactsListView();


    }

    public Person isOnList (Person p, List<Person> list) {
        for (Person l : list) {
            if (l.email.equals(p.email)) {
                return l;
            }
        }
        return null;
    }

    public Person isOnList (String email, List<Person> list) {
        for (Person l : list) {
            if (l.email.equals(email)) {
                return l;
            }
        }
        return null;
    }

    public void forceContactsListView () {
        contactsListView = findViewById(R.id.gcontactsListView);

        contactsListAdapter = new ContactsListAdapter((Context) mThis, 0, contacts);

        contactsListView.setAdapter(contactsListAdapter);

        contactsListView.setOnScrollListener(onScrollListener);


    }

    public void setContactsListView () {
        if (contactsListView == null) {
            contactsListView = findViewById(R.id.gcontactsListView);
        }
        if (contactsListAdapter == null) {
            contactsListAdapter = new ContactsListAdapter((Context) mThis, 0, contacts);
            contactsListView.setAdapter(contactsListAdapter);
            contactsListView.setOnScrollListener(onScrollListener);
        }


    }

    public class ContactsListAdapter extends ArrayAdapter<Person> {

        View.OnClickListener removeListener;
        View.OnClickListener addListener;

        public ContactsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Person> contacts) {
            super(context, resource, contacts);

            if (layOutInflater == null)
                layOutInflater = LayoutInflater.from(GmailContacts.this);

            removeListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = (String) view.getTag();

                    showConfirmationDialog(email);
                }
            };

            addListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = (String) view.getTag();

                    addContact(email);
                }
            };
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Person p = getItem(position);
            //if (view == null) {
                view = layOutInflater.inflate(R.layout.contacts_gmail_layout, null);

                TextView name = view.findViewById(R.id.contactGmailNameTextView);
                name.setText(p.firstName + " " + p.lastName);

                TextView email = view.findViewById(R.id.contactGmailEmailTextView);
                email.setText(p.email);

                ImageButton action = view.findViewById(R.id.contactGmailImageButton);
                action.setTag(p.email);

                Log.i("contactView", p.id + " " + p.firstName + " " + p.email);

                if (p.id == 0) {
                    action.setImageResource(R.drawable.add_contact);
                    action.setOnClickListener(addListener);
                }   else {
                    action.setOnClickListener(removeListener);
                }
            //}
            return view;
        }

    }

    private void showContactDialog() {

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View addContactView = li.inflate(R.layout.contact_add_layout, null);

        EditText email = addContactView.findViewById(R.id.contactEmailEditText);
        email.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        email.setTextColor(getResources().getColor(R.color.colorGray));

        Button dialogCancelButton = addContactView.findViewById(R.id.genericCancelButton);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //addContactDialog.dismiss();
                addContactAlertDialog.cancel();
            }
        });

        Button dialogAddButton = addContactView.findViewById(R.id.genericAddlButton);
        dialogAddButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText email = addContactAlertDialog.findViewById(R.id.contactEmailEditText);
                //EditText email = addContactDialog.findViewById(R.id.contactEmailEditText);

                String addEmail = email.getText().toString().trim();
                if (email != null && !addEmail.isEmpty()){
                    addContact(addEmail);
                }
            }
        });

        addContactDialogBuilder = new AlertDialog.Builder((Context)mThis);
        addContactDialogBuilder.setView(addContactView);
        addContactDialogBuilder.setCancelable(false);
        addContactDialogBuilder.setTitle("Contact email");

        addContactAlertDialog = addContactDialogBuilder.create();
        addContactAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addContactAlertDialog.show();

        /*addContactDialog = new Dialog((Context) mThis);
        addContactDialog.setContentView(addContactView);
        addContactDialog.show();*/


    }

    private void addContact(String email) {
        progressBar.setVisibility(View.VISIBLE);
        PersonServices personServices = new PersonServices();
        personServices.setFriendByEmail(this, person.id, email,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            Person newPerson = mapper.readValue(response, Person.class);

                            if (newPerson != null) {
                                if (newPerson.error == null) {

                                    addContactAlertDialog.cancel();

                                    reBuild();

                                }   else {
                                    Toast.makeText(getBaseContext(), newPerson.error.description, Toast.LENGTH_SHORT).show();
                                }

                            }   else {
                                Toast.makeText(getBaseContext(), "Unknown error", Toast.LENGTH_SHORT).show();
                            }

                        }   catch (JsonParseException e) {
                            e.printStackTrace();
                        }   catch (JsonMappingException e) {
                            e.printStackTrace();
                        }   catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "Unknown User or Password", Toast.LENGTH_SHORT).show();
                        Log.e("error", "error response: " + error.getMessage());
                        VolleyLog.d("error", "Error: " + error.getMessage());
                    }
                });
    }


    private void removeContact(String toUnset) {
        progressBar.setVisibility(View.VISIBLE);
        PersonServices personServices = new PersonServices();
        personServices.unsetFriendsByEmail(this, person.id, toUnset,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            ArrayList<Person> result = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Person.class));

                            if (result != null && result.size() == 2) {
                                if (result.get(0).error == null && result.get(1).error == null) {

                                    reBuild();

                                    Toast.makeText(getBaseContext(), result.get(1).lastName + " unset as contact", Toast.LENGTH_SHORT).show();

                                }   else {
                                    if (result.get(0).error != null) {
                                        Toast.makeText(getBaseContext(), result.get(0).error.description, Toast.LENGTH_SHORT).show();
                                    }
                                    if (result.get(1).error != null) {
                                        Toast.makeText(getBaseContext(), result.get(0).error.description, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }   else {
                                Toast.makeText(getBaseContext(), "Unknown error", Toast.LENGTH_SHORT).show();
                            }

                        }   catch (JsonParseException e) {
                            e.printStackTrace();
                        }   catch (JsonMappingException e) {
                            e.printStackTrace();
                        }   catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "Unknown User or Password", Toast.LENGTH_SHORT).show();
                        Log.e("error", "error response: " + error.getMessage());
                        VolleyLog.d("error", "Error: " + error.getMessage());
                    }
                });
    }

    private void showConfirmationDialog(final String email) {

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View confirmationView = li.inflate(R.layout.confirmation_layout, null);

        TextView question = confirmationView.findViewById(R.id.confirmationTextView);
        question.setText(R.string.sure);

        Button dialogNoButton = confirmationView.findViewById(R.id.genericNoButton);
        dialogNoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                confirmationAlertDialog.cancel();
            }
        });

        Button dialogYesButton = confirmationView.findViewById(R.id.genericYeslButton);
        dialogYesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                removeContact(email);
                confirmationAlertDialog.cancel();
            }
        });

        confirmationDialogBuilder = new AlertDialog.Builder((Context)mThis);
        confirmationDialogBuilder.setView(confirmationView);
        confirmationDialogBuilder.setCancelable(false);
        confirmationDialogBuilder.setTitle("");

        confirmationAlertDialog = confirmationDialogBuilder.create();
        confirmationAlertDialog.show();

        /*addContactDialog = new Dialog((Context) mThis);
        addContactDialog.setContentView(addContactView);
        addContactDialog.show();*/


    }

    /*public void printAllContacts(ContactsService myService)
            throws ServiceException, IOException {
        // Request the feed
        URL feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");
        ContactFeed resultFeed = myService.getFeed(feedUrl, ContactFeed.class);
        // Print the results
        //Log.i(resultFeed.getTitle().getPlainText());
        for (ContactEntry entry : resultFeed.getEntries()) {
            if (entry.hasName()) {
                Name name = entry.getName();
                if (name.hasFullName()) {
                    String fullNameToDisplay = name.getFullName().getValue();
                    if (name.getFullName().hasYomi()) {
                        fullNameToDisplay += " (" + name.getFullName().getYomi() + ")";
                    }
                    //Log.i("\t\t" + fullNameToDisplay);
                } else {
                    //Log.i("\t\t (no full name found)");
                }
                if (name.hasNamePrefix()) {
                    //Log.i("\t\t" + name.getNamePrefix().getValue());
                } else {
                    //Log.i("\t\t (no name prefix found)");
                }
                if (name.hasGivenName()) {
                    String givenNameToDisplay = name.getGivenName().getValue();
                    if (name.getGivenName().hasYomi()) {
                        givenNameToDisplay += " (" + name.getGivenName().getYomi() + ")";
                    }
                    //Log.i("\t\t" + givenNameToDisplay);
                } else {
                    //Log.i("\t\t (no given name found)");
                }
                if (name.hasAdditionalName()) {
                    String additionalNameToDisplay = name.getAdditionalName().getValue();
                    if (name.getAdditionalName().hasYomi()) {
                        additionalNameToDisplay += " (" + name.getAdditionalName().getYomi() + ")";
                    }
                    //Log.i("\t\t" + additionalNameToDisplay);
                } else {
                    //Log.i("\t\t (no additional name found)");
                }
                if (name.hasFamilyName()) {
                    String familyNameToDisplay = name.getFamilyName().getValue();
                    if (name.getFamilyName().hasYomi()) {
                        familyNameToDisplay += " (" + name.getFamilyName().getYomi() + ")";
                    }
                    //Log.i("\t\t" + familyNameToDisplay);
                } else {
                    //Log.i("\t\t (no family name found)");
                }
                if (name.hasNameSuffix()) {
                    //Log.i("\t\t" + name.getNameSuffix().getValue());
                } else {
                    //Log.i("\t\t (no name suffix found)");
                }
            } else {
                //Log.i("\t (no name found)");
            }
            //Log.i("Email addresses:");
            for (Email email : entry.getEmailAddresses()) {
                System.out.print(" " + email.getAddress());
                if (email.getRel() != null) {
                    System.out.print(" rel:" + email.getRel());
                }
                if (email.getLabel() != null) {
                    System.out.print(" label:" + email.getLabel());
                }
                if (email.getPrimary()) {
                    System.out.print(" (primary) ");
                }
                System.out.print("\n");
            }
            //Log.i("IM addresses:");
            for (Im im : entry.getImAddresses()) {
                System.out.print(" " + im.getAddress());
                if (im.getLabel() != null) {
                    System.out.print(" label:" + im.getLabel());
                }
                if (im.getRel() != null) {
                    System.out.print(" rel:" + im.getRel());
                }
                if (im.getProtocol() != null) {
                    System.out.print(" protocol:" + im.getProtocol());
                }
                if (im.getPrimary()) {
                    System.out.print(" (primary) ");
                }
                System.out.print("\n");
            }
            //Log.i("Groups:");
            for (GroupMembershipInfo group : entry.getGroupMembershipInfos()) {
                String groupHref = group.getHref();
                //Log.i("  Id: " + groupHref);
            }
            //Log.i("Extended Properties:");
            for (ExtendedProperty property : entry.getExtendedProperties()) {
                if (property.getValue() != null) {
                    //Log.i("  " + property.getName() + "(value) = " +
                            property.getValue());
                } else if (property.getXmlBlob() != null) {
                    //Log.i("  " + property.getName() + "(xmlBlob)= " +
                            property.getXmlBlob().getBlob());
                }
            }
            Link photoLink = entry.getContactPhotoLink();
            String photoLinkHref = photoLink.getHref();
            //Log.i("Photo Link: " + photoLinkHref);
            if (photoLink.getEtag() != null) {
                //Log.i("Contact Photo's ETag: " + photoLink.getEtag());
            }
            //Log.i("Contact's ETag: " + entry.getEtag());
        }
    }*/

}
