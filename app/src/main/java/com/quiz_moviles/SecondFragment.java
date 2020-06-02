package com.quiz_moviles;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import com.quiz_moviles.Adapters.MyAdapterChecked;
import com.quiz_moviles.Estructuras.Curso;
import com.quiz_moviles.Estructuras.Datos;
import com.quiz_moviles.Estructuras.Estudiante;
import com.quiz_moviles.Estructuras.SERVICIOS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static android.content.Context.MODE_PRIVATE;

public class SecondFragment extends Fragment {

    MyAdapterChecked adapter;
    TextInputEditText cedula, nombre, apellidos, edad;
    ProgressBar progressBar;
    Boolean editar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("entendido!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).show();
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cedula = view.findViewById(R.id.cedula);
        nombre = view.findViewById(R.id.nombre);
        apellidos = view.findViewById(R.id.apellidos);
        edad = view.findViewById(R.id.edad);

        Objects.requireNonNull(getActivity()).findViewById(R.id.fab).setVisibility(View.GONE);
        adapter = new MyAdapterChecked(Datos.getInstance().getCursos());
        RecyclerView recyclerView_cursos = view.findViewById(R.id.recyclerView_cursos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView_cursos.setLayoutManager(layoutManager);
        recyclerView_cursos.setAdapter(adapter);

        progressBar = view.findViewById(R.id.progressBar);

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckedTextView checkedTextView : adapter.getCheckedTextViews()) {
                    if (!checkedTextView.isChecked()) {
                        mostrarMensaje("ERROR", "Al menos debes de seleccionar un curso");
                        return;
                    }
                }
                Estudiante estudiante = new Estudiante(
                        Objects.requireNonNull(cedula.getText()).toString(),
                        Objects.requireNonNull(nombre.getText()).toString(),
                        Objects.requireNonNull(apellidos.getText()).toString(),
                        Integer.parseInt(Objects.requireNonNull(edad.getText()).toString())
                );
                if (editar) {
                    Datos.getInstance().getEstudiantes().remove(estudiante);
                    FirstFragment newFragment = new FirstFragment();
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_FirstFragment);
                }
                insertarEstudiante(estudiante);
            }
        });

        if (Datos.getInstance().getCursos().isEmpty()) {
            adapter.updateList(Datos.getInstance().getCursos());
            adapter.notifyDataSetChanged();
        }

        final Bundle bdl = getArguments();
        if (bdl != null) {
            Estudiante estudiante = (Estudiante) bdl.getSerializable(FirstFragment.ARG_ESTUDIANTE);
            if (estudiante != null) {
                editar = true;
                cedula.setText(estudiante.getCedula());
                nombre.setText(estudiante.getNombre());
                apellidos.setText(estudiante.getApellidos());
                edad.setText(estudiante.getEdad().toString());
                // Lista de cursos por estudiante
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    for (String codigo : adapter.getSeleccionados()) {
                        if (adapter.getmDataset().get(i).getCodigo().equals(codigo)) {
                            adapter.getCheckedTextViews().get(i).setChecked(true);
                            break;
                        }
                    }
                }
                cedula.setEnabled(false);
            }
        } else
            editar = false;
    }


    private void insertarEstudiante(Estudiante estudiante) {

        String pre_sql = "insert into ESTUDIANTE(cedula, nombre, apellidos, edad)"
                + " values('%s', '%s', '%s', %d);";

        String sql = String.format(pre_sql, estudiante.getCedula(), estudiante.getNombre(), estudiante.getApellidos(), estudiante.getEdad());

        try {
            MainActivity.db.execSQL(sql);

        } catch (SQLException sqle) {
            Toast.makeText(getActivity(), sqle.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            Datos.getInstance().getEstudiantes().add(0, estudiante);
        }

    }


}
