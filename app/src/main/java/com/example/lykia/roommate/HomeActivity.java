package com.example.lykia.roommate;

import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.lykia.roommate.DAOs.AnimalDAO;
import com.example.lykia.roommate.DAOs.RaceDAO;
import com.example.lykia.roommate.DAOs.RehomingDAO;
import com.example.lykia.roommate.DTOs.AnimalDTO;
import com.example.lykia.roommate.DTOs.RaceDTO;
import com.example.lykia.roommate.DTOs.RehomingDTO;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private Spinner spinnerHayvan;
    private Spinner spinnerCins;
    private Spinner spinnerCinsiyet;
    private ListView listViewAnimal;
    private BottomNavigationView navButton;

    private Spinner animalSpinner;
    private Spinner raceSpinner;
    private Spinner genderSpinner;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<RehomingDTO> pets;
    private List<AnimalDTO> animals;
    private List<RaceDTO> races;
    private MyHomeAdapter adapter;
    ArrayAdapter<CharSequence>adapterGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navButton=(BottomNavigationView)findViewById(R.id.bottomBar);
        navButton.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home_nav:
                        Intent intent=new Intent(HomeActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.search_nav:
                        Intent intent1=new Intent(HomeActivity.this,SearchActivity.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.profile_nav:
                        Intent intent2=new Intent(HomeActivity.this,OwnProfileActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                }
                return false;
            }
        });
        spinnerHayvan=(Spinner)findViewById((R.id.
                                            
                                            ));
        spinnerCins=(Spinner)findViewById((R.id.raceSpinner));
        spinnerCinsiyet=(Spinner)findViewById((R.id.genderSpinner));
        listViewAnimal=(ListView)findViewById(R.id.LVshowAnimal);

        ArrayAdapter adapterHayvan = ArrayAdapter.createFromResource(this,R.array.cinsiyet, android.R.layout.simple_spinner_item);
        adapterHayvan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHayvan.setAdapter(adapterHayvan);
        spinnerHayvan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), parent.getItemIdAtPosition(position)+"seçilen ", Toast.LENGTH_SHORT).show();

        animalSpinner= findViewById((R.id.animalSpinner));
        raceSpinner= findViewById((R.id.raceSpinner));
        genderSpinner= findViewById((R.id.genderSpinner));

        toolbar = findViewById(R.id.homeAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        recyclerView= findViewById(R.id.recycler_viewHomePage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        adapterGender=ArrayAdapter.createFromResource(this,R.array.cinsiyet,android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapterGender);

        new GetSpinnersFromDatabase().execute();
        new Background().execute();

        animalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, final long id) {
                new GetRaceSpinnerFromDatabase().execute((int) id);
                new GetPetsByAnimal();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        raceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new GetPetsByRace();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public class MyHomeAdapter extends RecyclerView.Adapter<MyHomeHolder> {

        List<RehomingDTO> pets;

        public MyHomeAdapter(List<RehomingDTO> pets) {
            this.pets = pets;
        }

        @Override
        public MyHomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_animal, parent, false);
            MyHomeHolder holder = new MyHomeHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder( MyHomeHolder holder,final int position) {
            Picasso.get().load(pets.get(position).getImagePath()).placeholder(R.drawable.default_rehoming_icon).into(holder.animalImage);
            holder.textViewAnimal.setText(pets.get(position).getRace().getAnimal().getAnimalName());
            holder.textViewRace.setText(pets.get(position).getRace().getRaceName());
            holder.textViewMonth.setText(Integer.toString(pets.get(position).getMonthOld()));
            holder.textViewGender.setText(pets.get(position).getGender());
            holder.textViewRefCode.setText(pets.get(position).getCode());

            holder.messageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, MessageActivity.class)
                            .putExtra("messageUserId", pets.get(position).getUser().getUserId()));
                }
            });

        }

        @Override
        public int getItemCount() {
            return pets.size();
        }

    }


    public class Background extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            pets = RehomingDAO.getAllRehomingPets();

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);

            adapter = new MyHomeAdapter(pets);
            recyclerView.setAdapter(adapter);
        }
    }

    public class GetPetsByAnimal extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void...voids) {

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            animals = AnimalDAO.getAnimalById();


        }
    }

    public class GetPetsByRace extends AsyncTask<Integer, Void, Void>
    {
        @Override
        protected Void doInBackground(Integer... ints) {
            races = RaceDAO.getRacesByAnimalId(ints[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);


        }
    }




    public class GetSpinnersFromDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            animals = AnimalDAO.getAllAnimals();
            races = RaceDAO.getAllRaces();

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);

            populateAnimalSpinner();
            populateRaceSpinner(races);
        }
    }

    public class GetRaceSpinnerFromDatabase extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... ints) {
            races = RaceDAO.getRacesByAnimalId(ints[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);


            populateRaceSpinner(races);
        }
    }

    private void populateAnimalSpinner() {
        List<String> list = new ArrayList<>();

        list.add("Hayvan");
        for (int i = 0; i < animals.size(); i++) {
            list.add(animals.get(i).getAnimalName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        animalSpinner.setAdapter(spinnerAdapter);
    }

    private void populateRaceSpinner(List<RaceDTO> races) {
        List<String> list = new ArrayList<>();

        list.add("Cins");
        for (int i = 0; i < races.size(); i++) {
            list.add(races.get(i).getRaceName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        raceSpinner.setAdapter(spinnerAdapter);
    }

}

class MyHomeHolder extends RecyclerView.ViewHolder {

    ImageView animalImage;
    TextView textViewAnimal;
    TextView textViewRace;
    TextView textViewGender;
    TextView textViewMonth;
    TextView textViewRefCode;
    Button messageButton;


    public MyHomeHolder(View itemView) {
        super(itemView);
        animalImage = itemView.findViewById(R.id.animalImage);
        textViewAnimal = itemView.findViewById(R.id.textAnimal);
        textViewRace = itemView.findViewById(R.id.textRace);
        textViewGender = itemView.findViewById(R.id.textGender);
        textViewMonth = itemView.findViewById(R.id.textMonth);
        textViewRefCode = itemView.findViewById(R.id.textRefCode);
        messageButton = itemView.findViewById(R.id.messageButton);

    }
}
