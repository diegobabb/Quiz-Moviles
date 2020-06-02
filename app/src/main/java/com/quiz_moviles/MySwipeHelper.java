package com.quiz_moviles;

import android.annotation.SuppressLint;
import android.database.SQLException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.quiz_moviles.Adapters.MyAdapter;
import com.quiz_moviles.Estructuras.Datos;
import com.quiz_moviles.Estructuras.Estudiante;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MySwipeHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private FirstFragment fragment;

    public MySwipeHelper(int dragDirs, int swipeDirs, MyAdapter adapter, RecyclerView recyclerView, FirstFragment fragment) {
        super(dragDirs, swipeDirs);
        this.recyclerView = recyclerView;
        this.adapter = adapter;
        this.fragment = fragment;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        switch (direction) {
            case ItemTouchHelper.LEFT:
                final Estudiante aux = adapter.getmDataset().remove(position);
                eliminarEstudiante(aux, position);
               /* Snackbar.make(recyclerView, aux.getNombre(), Snackbar.LENGTH_LONG)
                        .setAction("Deshacer", new View.OnClickListener() {
                            private boolean flag = true;

                            @Override
                            public void onClick(View view) {
                                if (flag) {
                                    restaurar(aux);
                                    adapter.notifyItemInserted(position);
                                }
                                flag = false;
                            }
                        }).addCallback(new Snackbar.Callback() {

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            fragment.removeEstudiante(position, aux);
                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        snackbar.show();
                    }
                }).show();*/
                break;
            case ItemTouchHelper.RIGHT:
                fragment.moveToSecondFragment(adapter.getmDataset().get(position), position);
                break;
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                            boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(Color.parseColor(fragment.getString(R.color.delete)))
                .addSwipeLeftActionIcon(R.drawable.ic_delete_24dp)
                .addSwipeRightBackgroundColor(Color.parseColor(fragment.getString(R.color.edit)))
                .addSwipeRightActionIcon(R.drawable.ic_edit_24dp).create().decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    private void eliminarEstudiante(Estudiante estudiante, int position) {
        String pre_sql = "DELETE FROM ESTUDIANTE WHERE cedula = '%s'";
        String pre_sql_matriculados = "DELETE FROM ESTUDIANTExCURSO WHERE cedula = '%s'";

        String sql = String.format(pre_sql, estudiante.getCedula());
        String sql_eliminar_matriculados = String.format(pre_sql, estudiante.getCedula());

        try {
            MainActivity.db.execSQL(sql);
            MainActivity.db.execSQL(sql_eliminar_matriculados);
            Datos.getInstance().getEstudiantes().remove(estudiante);
            adapter.notifyItemRemoved(position);
        } catch (SQLException sqle) {
            Log.d("ERROR ELIMINANDO", sqle.getMessage());
        }
    }

    /*
    private void restaurar(Estudiante estudiante) {

        String pre_sql = "insert into ESTUDIANTE(cedula, nombre, apellidos, edad)"
                + " values('%s', '%s', '%s', %d);";

        String sql = String.format(pre_sql, estudiante.getCedula(), estudiante.getNombre(), estudiante.getApellidos(), estudiante.getEdad());

        try {
            MainActivity.db.execSQL(sql);
            Datos.getInstance().getEstudiantes().add(0, estudiante);

        } catch (SQLException sqle) {
            Log.d("ERROR RESTAURADO", sqle.getMessage());
        }
    }

     */
}
