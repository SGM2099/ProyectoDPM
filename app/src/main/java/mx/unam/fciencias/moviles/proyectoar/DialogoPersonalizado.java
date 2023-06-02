package mx.unam.fciencias.moviles.proyectoar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogoPersonalizado extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        dialogBuilder.setCancelable(true)
                .setView(layoutInflater.inflate(R.layout.dialogo_personalizado, null))
                .setPositiveButton(R.string.entendido, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), R.string.after_dialog, Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                    }
                });

        return dialogBuilder.create();

    }

}
