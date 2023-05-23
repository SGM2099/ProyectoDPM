package mx.unam.fciencias.moviles.proyectoar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

public class Informacion extends AppCompatActivity {

    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);

        Bundle b = getIntent().getExtras();
        String filtro = b.getString("Filter");
        Log.i("Filtro", filtro);
        if(filtro.equals("lupus_eritematoso.deepar")){
            TextView text = findViewById(R.id.filter_main_info);
            text.setText(R.string.lupus_eritmatoso);
            text = findViewById(R.id.information_title);
            text.setText("Lupus Eritematoso");
        } else if( filtro.equals("ictericia.deepar")) {
            TextView text = findViewById(R.id.filter_main_info);
            text.setText(R.string.ictericia);
            text = findViewById(R.id.information_title);
            text.setText("Ictercicia");
        } else {
            TextView text = findViewById(R.id.information_title);
            text.setText("No Filter");
        }




        imageButton = (ImageButton) findViewById(R.id.popupMenu);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(Informacion.this, imageButton);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.select_filter:
                                Intent intent = new Intent(Informacion.this, FilterActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.credits:
                                Intent intentCredits = new Intent(Informacion.this, Creditos.class);
                                startActivity(intentCredits);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

    }
}