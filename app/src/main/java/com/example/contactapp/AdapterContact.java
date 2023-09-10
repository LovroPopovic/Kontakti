package com.example.contactapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ContactViewHolder> {

    private final Context context;
    private final ArrayList<ModelContact> contactList;
    private final DbHelper dbHelper;



    public AdapterContact(Context context, ArrayList<ModelContact> contactList) {
        this.context = context;
        this.contactList = contactList;
        dbHelper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_contact_item, parent, false);
        ContactViewHolder vh = new ContactViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {

        ModelContact modelContact = contactList.get(position);

        //svi podaci
        String id = modelContact.getId();
        String image = modelContact.getImage();
        String name = modelContact.getName();
        String phone = modelContact.getPhone();
        String email = modelContact.getEmail();
        String note = modelContact.getNote();
        String addedTime = modelContact.getAddedTime();
        String updatedTime = modelContact.getUpdatedTime();

        //ucitavamo podatke u pogled
        holder.contactName.setText(name);
        if (image.equals("")) {
            holder.contactImage.setImageResource(R.drawable.ic_baseline_person_24);
        } else {
            holder.contactImage.setImageURI(Uri.parse(image));
        }

        //handle click listener
        holder.contactDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //handler za klikanje na iteme
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create intent to move to contactsDetails Activity with contact id as reference
                Intent intent = new Intent(context, ContactDetails.class);
                intent.putExtra("contactId", id);
                context.startActivity(intent); // now get data from details Activity
                Toast.makeText(context, "Heelo", Toast.LENGTH_SHORT).show();
            }
        });

        // handler za edit button
        holder.contactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create intent to move AddEditActivity to update data
                Intent intent = new Intent(context, AddEditContact.class);
                //pass the value of current position
                intent.putExtra("ID", id);
                intent.putExtra("NAME", name);
                intent.putExtra("PHONE", phone);
                intent.putExtra("EMAIL", email);
                intent.putExtra("NOTE", note);
                intent.putExtra("ADDEDTIME", addedTime);
                intent.putExtra("UPDATEDTIME", updatedTime);
                intent.putExtra("IMAGE", image);


                intent.putExtra("isEditMode", true);

                //start intent
                context.startActivity(intent);


            }
        });

        // handler za brisanje kontaka
        holder.contactDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //koristimo bazu za beisanje
                dbHelper.deleteContact(id);

                //refresh mainActivity-a kako bi se ucitali kontakti bez obrisanog
                ((MainActivity) context).onResume();

            }
        });


    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {


        ImageView contactImage, contactDial;
        TextView contactName, contactEdit, contactDelete;
        RelativeLayout relativeLayout;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            //inicijalizacija pogleda
            contactImage = itemView.findViewById(R.id.contact_image);
            contactDial = itemView.findViewById(R.id.contact_number_dial);
            contactName = itemView.findViewById(R.id.contact_name);
            contactDelete = itemView.findViewById(R.id.contact_delete);
            contactEdit = itemView.findViewById(R.id.contact_edit);
            relativeLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
