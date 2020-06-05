package com.quiz_moviles.Activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import androidx.navigation.fragment.NavHostFragment;

import com.quiz_moviles.Adapters.MyAdapter;
import com.quiz_moviles.LogicaNegocio.Datos;
import com.quiz_moviles.LogicaNegocio.Estudiante;
import com.quiz_moviles.R;

import java.util.Objects;

public class FirstFragment extends Fragment {

    static final String ARG_ESTUDIANTE = "estudiante";
    static final String ARG_POSITION = "position";
    MyAdapter myAdapter;
    FloatingActionButton floatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @SuppressLint("RestrictedApi")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.VISIBLE);
        Objects.requireNonNull(getActivity()).findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter(Datos.getInstance().getEstudiantes());
        recyclerView.setAdapter(myAdapter);

        MySwipeHelper simpleCallback =
                new MySwipeHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, myAdapter, recyclerView, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        // LLAMAMOS AL LISTAR
        listar();

    }

    public void removeEstudiante(int position, Estudiante aux) {
        myAdapter.getmDataset().add(position, aux);
        Datos.getInstance().getEstudiantes().add(position, aux);
        myAdapter.notifyItemInserted(position);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        floatingActionButton.setVisibility(View.VISIBLE);
        myAdapter.notifyDataSetChanged();
    }

    public void moveToSecondFragment(Estudiante estudiante, int position) {
        SecondFragment newFragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putSerializable(FirstFragment.ARG_ESTUDIANTE, estudiante);
        args.putInt(FirstFragment.ARG_POSITION, position);
        newFragment.setArguments(args);
        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void listar() {
        Datos.getInstance().getEstudiantes().clear();

        String mySQL = "SELECT cedula, nombre, apellidos, edad FROM ESTUDIANTE";

        Cursor cursor = MainActivity.db.rawQuery(mySQL, null);

        int idCedula = cursor.getColumnIndex("cedula");
        int idNombre = cursor.getColumnIndex("nombre");
        int idApellidos = cursor.getColumnIndex("apellidos");
        int idEdad = cursor.getColumnIndex("edad");

        while (cursor.moveToNext()) {
            Datos.getInstance().getEstudiantes().add(new Estudiante(
                    cursor.getString(idCedula),
                    cursor.getString(idNombre),
                    cursor.getString(idApellidos),
                    cursor.getInt(idEdad)
            ));
        }

        myAdapter.updateList(Datos.getInstance().getEstudiantes());

    }
}
