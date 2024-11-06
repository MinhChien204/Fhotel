package learn.fpoly.fhotel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import learn.fpoly.fhotel.R;

public class RateUsDialog extends Dialog {

    private float userRate = 0;


    public RateUsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_us_dialog_layout);

        final AppCompatButton lateBtn = findViewById(R.id.lateBtn);
        final AppCompatButton rateNowBtn = findViewById(R.id.rateNowBtn);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        final ImageView ratingImage = findViewById(R.id.ratingImage);

        rateNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        lateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (v <= 1) {
                    ratingImage.setImageResource(R.drawable.emoji_angry);
                } else if (v <= 2) {
                    ratingImage.setImageResource(R.drawable.emoji_2s);
                } else if (v <= 3) {
                    ratingImage.setImageResource(R.drawable.emoji_3s);
                } else if (v <= 4) {
                    ratingImage.setImageResource(R.drawable.emoji_4s);
                } else if (v <= 5) {
                    ratingImage.setImageResource(R.drawable.emoji_5s);
                }

                // Animate the rating image whenever the rating changes
                animateImage(ratingImage);

                userRate = v;
            }
        });
    }

    // Change parameter type to ImageView
    private void animateImage(ImageView ratingImage) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0, 1f, 0, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);
    }
}
