package com.quiz_moviles;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import com.quiz_moviles.Adapters.MyAdapterChecked;
import com.quiz_moviles.Estructuras.Curso;
import com.quiz_moviles.Estructuras.Datos;
import com.quiz_moviles.Estructuras.Estudiante;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        listarCursos();
        adapter = new MyAdapterChecked(Datos.getInstance().getCursos());
        RecyclerView recyclerView_cursos = view.findViewById(R.id.recyclerView_cursos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView_cursos.setLayoutManager(layoutManager);
        recyclerView_cursos.setAdapter(adapter);

        progressBar = view.findViewById(R.id.progressBar);

        // Lista TODOS los cursos


        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Estudiante estudiante = new Estudiante(
                        Objects.requireNonNull(cedula.getText()).toString(),
                        Objects.requireNonNull(nombre.getText()).toString(),
                        Objects.requireNonNull(apellidos.getText()).toString(),
                        Integer.parseInt(Objects.requireNonNull(edad.getText()).toString())
                );
                if (editar) {
                    modificarEstudiante(estudiante);
                    asignarEstudianteCursos(estudiante, adapter.getSeleccionados());
                    FirstFragment newFragment = new FirstFragment();
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    insertarEstudiante(estudiante);
                    asignarEstudianteCursos(estudiante, adapter.getSeleccionados());
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_FirstFragment);
                }
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
                getCursosEstudiante(estudiante);
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
            Datos.getInstance().getEstudiantes().add(0, estudiante);

        } catch (SQLException sqle) {
            Toast.makeText(getActivity(), sqle.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void modificarEstudiante(Estudiante estudiante) {
        String pre_sql = "UPDATE ESTUDIANTE SET nombre = '%s', apellidos = '%s', edad = %d WHERE cedula = '%s'";


        String sql = String.format(pre_sql, estudiante.getNombre(), estudiante.getApellidos(), estudiante.getEdad(), estudiante.getCedula());

        try {
            MainActivity.db.execSQL(sql);
            Datos.getInstance().getEstudiantes().remove(estudiante);
            Datos.getInstance().getEstudiantes().add(0, estudiante);

        } catch (SQLException sqle) {
            Toast.makeText(getActivity(), sqle.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void listarCursos() {

        Datos.getInstance().getCursos().clear();

        String mySQL = "SELECT codigo, descripcion, creditos FROM CURSO";

        Cursor cursor = MainActivity.db.rawQuery(mySQL, null);

        int idCodigo = cursor.getColumnIndex("codigo");
        int idDescrip = cursor.getColumnIndex("descripcion");
        int idCreditos = cursor.getColumnIndex("creditos");

        while (cursor.moveToNext()) {
            Datos.getInstance().getCursos().add(new Curso(
                    cursor.getString(idCodigo),
                    cursor.getString(idDescrip),
                    cursor.getInt(idCreditos)
            ));
        }
    }

    private void asignarEstudianteCursos(Estudiante estudiante, List<String> cursos) {

        String prepare_eliminar = "DELETE FROM ESTUDIANTExCURSO WHERE cedula = '%s'";
        String sql_eliminar = String.format(prepare_eliminar, estudiante.getCedula());

        String prepare_insertar = "INSERT INTO ESTUDIANTExCURSO (cedula, codigo) values ('%s', '%s'); ";
        String sql_insertar = "";

        try {
            MainActivity.db.execSQL(sql_eliminar);

            for (String codigo : cursos) {
                sql_insertar = String.format(prepare_insertar, estudiante.getCedula(), codigo);
                MainActivity.db.execSQL(sql_insertar);
            }

        } catch (SQLException sqle) {
            Log.d("ERROR ELIMINANDO", sqle.getMessage());
        }
    }


    // Solo devuelve los indices de los cursos checkeados
    private void getCursosEstudiante(Estudiante estudiante) {

        String PREmySQL =
                "SELECT  ESTUDIANTExCURSO.codigo AS cod " +
                        "FROM ESTUDIANTExCURSO " +
                        "INNER JOIN ESTUDIANTE ON ESTUDIANTExCURSO.cedula = ESTUDIANTE.cedula " +
                        "INNER JOIN CURSO ON ESTUDIANTExCURSO.codigo = CURSO.codigo " +
                        "WHERE ESTUDIANTExCURSO.cedula = '%s'";

        String mySQL = String.format(PREmySQL, estudiante.getCedula());

        Cursor cursor = MainActivity.db.rawQuery(mySQL, null);

        int cod_indice = cursor.getColumnIndex("cod");
        ArrayList<String> seleccionados = new ArrayList<>();

        while (cursor.moveToNext()) {
            seleccionados.add(cursor.getString(cod_indice));
        }

        adapter.setSeleccionados(seleccionados);

        adapter.notifyDataSetChanged();
    }

}
