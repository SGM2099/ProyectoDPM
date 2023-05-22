package mx.unam.fciencias.moviles.proyectoar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

public class Creditos extends AppCompatActivity {
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos);

        imageButton = (ImageButton) findViewById(R.id.popupMenu);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(Creditos.this, imageButton);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.select_filter:
                                return true;
                            case R.id.info_filter:
                                Intent intentFilter = new Intent(Creditos.this, Informacion.class);
                                startActivity(intentFilter);
                                return true;
                            case R.id.credits:
                                Intent intentCredits = new Intent(Creditos.this, Creditos.class);
                                startActivity(intentCredits);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });

    }

}