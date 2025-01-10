package card.game.app;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Hide the ActionBar (Header)
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }
    setContentView(R.layout.activity_splash);

    // Initialize the Lottie animation view
    LottieAnimationView animationView = findViewById(R.id.splashAnimation);
    animationView.setRepeatCount(LottieDrawable.INFINITE); // Loop animation
    animationView.playAnimation(); // Start animation

    // Delay to keep the splash screen visible for a while, regardless of app state
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
      }
    }, 4000); // 4-second delay for splash screen, adjust as needed
  }
}

