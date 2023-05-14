package mx.unam.fciencias.moviles.proyectoar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FilterActivity extends AppCompatActivity {

    int[] images;
    String layout_name;
    MotionLayout mMotionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
    }

    private void setupCarousel() {
        Carousel carousel = findViewById(R.id.carousel);
        //TextView label = findViewById(R.id.label);
        if (carousel == null) {
            return;
        }

        //int numImages = images.length;
        int numImages = 3;

        /*Button button = findViewById(R.id.button);
        if (layout_name.equals("demo_010_carousel")) {
            button.setOnClickListener(v -> {
                int numItems = carousel.getCount();
                int lastItem = numItems - 1;
                if (carousel.getCurrentIndex() == 0) {
                    carousel.jumpToIndex(lastItem);
                } else {
                    carousel.jumpToIndex(0);
                }
            });
        }*/
        carousel.setAdapter(new Carousel.Adapter() {
            @Override
            public int count() {
                return numImages;
            }

            @Override
            public void populate(View view, int index) {
                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    imageView.setImageResource(images[index]);
                } else if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    textView.setText("#" + (index + 1));
                    //textView.setBackgroundColor(colors[index]);
                }
            }

            @Override
            public void onNewItem(int index) {
                /*if (label != null) {
                    label.setText("#" + (index + 1));
                }
                if (button != null) {
                    if (index == carousel.getCount() - 1) {
                        button.setText("Go to first item");
                    }
                    if (index == 0) {
                        button.setText("Go to last item");
                    }
                }*/
            }
        });
    };

}