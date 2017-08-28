package yun.homeguard.strayanimals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Handler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    private PetArrayadapter adapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv=(ListView)findViewById(R.id.pet_lv);

        adapter=new PetArrayadapter(this,new ArrayList<Pets>());
        lv.setAdapter(adapter);

        getFromFirebase();
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    List<Pets> pets = (List<Pets>)msg.obj;
                    refreshPetList(pets);
                    break;
                }
            }
        }
    };

    private void refreshPetList(List<Pets> pets) {
        adapter.clear();
        adapter.addAll(pets);

    }

    private void getFromFirebase(){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myref=database.getReference("");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new FirebaseThread(dataSnapshot).start();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private Bitmap getImgBitmap(String imgURL){
        try{
            URL url=new URL(imgURL);
            Bitmap bitmap= BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;//***************
    }

    class FirebaseThread extends Thread{
        private DataSnapshot dataSnapshot;
        public FirebaseThread(DataSnapshot dataSnapshot){
            this.dataSnapshot=dataSnapshot;
        }

        public void run(){
            List<Pets> petsList=new ArrayList<>();

            for(DataSnapshot ds:dataSnapshot.getChildren()){
                DataSnapshot dsAnimalskind=ds.child("animal_kind");
                DataSnapshot dsShelterName=ds.child("shelter_name");
                DataSnapshot dsImg=ds.child("album_file");

                String kind=(String) dsAnimalskind.getValue();
                String shelter=(String)dsShelterName.getValue();
                String img=(String)dsImg.getValue();
                Bitmap petimg=getImgBitmap(img);

                Pets pet=new Pets();
                pet.setKind(kind);
                pet.setShelter(shelter);
                pet.setImg(petimg);
                petsList.add(pet);

                Message msg=new Message();
                msg.what=1;
                msg.obj=petsList;
                handler.sendMessage(msg);
            }
        }
    }

    class PetArrayadapter extends ArrayAdapter<Pets>{

        Context context;

        public PetArrayadapter(Context context,ArrayList<Pets> items){
            super(context,0,items);
            this.context=context;
        }

        public View getView(int position, View convertView, ViewGroup parent){

            LayoutInflater inflater=LayoutInflater.from(context);

            LinearLayout itemlayout=null;
            if(convertView==null){
                itemlayout=(LinearLayout)inflater.inflate(R.layout.pet_item,null);
            }else{
                itemlayout=(LinearLayout) convertView;
            }
            Pets item=(Pets)getItem(position);
            TextView pet_kind=(TextView)itemlayout.findViewById(R.id.petkind_tv);
            pet_kind.setText(item.getKind());
            TextView pet_shelter=(TextView)itemlayout.findViewById(R.id.petshelter_tv);
            pet_shelter.setText(item.getShelter());
            ImageView pet_img=(ImageView) itemlayout.findViewById(R.id.pet_Img);
            pet_img.setImageBitmap(item.getImg());
            return itemlayout;
        }
    }
}

