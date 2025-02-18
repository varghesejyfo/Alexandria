package ca.ualberta.CMPUT3012019T02.alexandria.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import ca.ualberta.CMPUT3012019T02.alexandria.R;
import ca.ualberta.CMPUT3012019T02.alexandria.controller.ImageController;
import ca.ualberta.CMPUT3012019T02.alexandria.controller.UserController;
import ca.ualberta.CMPUT3012019T02.alexandria.model.user.UserProfile;

/**
 * Activity for my profile of a current user
 * Shows its name, username, email address,
 * provides and edit my profile activity
 */
public class ViewMyProfileActivity extends AppCompatActivity {

    private UserProfile myUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_profile);

        // toolbar
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);    // remove default title

        // back button
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * sets user details as in user profile: name, username, email and image
     */
    @Override
    public void onResume() {
        super.onResume();

        TextView textView_username = findViewById(R.id.my_profile_username);
        TextView textView_name = findViewById(R.id.my_profile_name);
        TextView textView_email = findViewById(R.id.my_profile_email);
        ImageView image_avatar = findViewById(R.id.my_profile_image);

        // sets user's strings
        UserController userController = UserController.getInstance();
        userController.getMyProfile().handleAsync((result, error) -> {
            if(error == null) {
                // Update ui here
                myUserProfile = result;

                String username = myUserProfile.getUsername();
                String name = myUserProfile.getName();
                String email = myUserProfile.getEmail();
                String photoId = myUserProfile.getPicture();
                runOnUiThread(() -> {
                    textView_username.setText(username);
                    textView_name.setText(name);
                    textView_email.setText(email);

                    // sets user image
                    if (photoId == null) {
                        stopSpinner();
                    } else {
                        ImageController imageController = ImageController.getInstance();
                        imageController.getImage(photoId).handleAsync((resultImage, errorImage) -> {
                            if (errorImage == null) {
                                Bitmap bitmap = resultImage;

                                if (bitmap != null) {
                                    Bitmap squareBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                            Math.min(bitmap.getWidth(), bitmap.getHeight()),
                                            Math.min(bitmap.getWidth(), bitmap.getHeight()));

                                    RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory
                                            .create(getResources(), squareBitmap);
                                    drawable.setCornerRadius(Math.min(
                                            bitmap.getWidth(), bitmap.getHeight()));
                                    drawable.setAntiAlias(true);

                                    runOnUiThread(() -> {
                                        image_avatar.setBackgroundResource(0);
                                        image_avatar.setImageDrawable(drawable);
                                        stopSpinner();
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        image_avatar.setBackgroundResource(R.drawable.ic_profile);
                                    });
                                }
                            } else {
                                showError(errorImage.getMessage());
                            }
                            return null;
                        });
                    }
                });
            }
            else {
                // Show error message
                showError("Profile is not recognized");
                myUserProfile = new UserProfile("Unknown","Unknown",
                        "Unknown",null,"Unknown");
                textView_name.setText(myUserProfile.getName());
                textView_username.setText(myUserProfile.getUsername());
                textView_email.setText(myUserProfile.getEmail());
            }
            return null;
        });
    }

    /**
     * Removes spinner
     */
    private void stopSpinner() {
        ProgressBar spinner = findViewById(R.id.view_my_profile_spinner);
        spinner.setVisibility(View.GONE);

        ConstraintLayout mainContent = findViewById(R.id.view_my_profile_main_content);
        mainContent.setVisibility(View.VISIBLE);
    }

    /**
     * shows error toast
     * @param message error message
     */
    private void showError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }

    /**
     * inflate menu for user profile options
     * @param menu menu
     * @return boolean of successes
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * ellipses menu switch
     * @param item item clicked
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //menu switch
            case R.id.edit_profile:
                // edit activity
                Intent startEditProfile = new Intent(this,
                        EditMyProfileActivity.class);
                startActivity(startEditProfile);
                break;

            case R.id.sign_out_profile:
                // Sing Out
                UserController.getInstance().deauthenticate();
                Intent start_new_main_intent = new Intent(this,
                        MainActivity.class);
                start_new_main_intent.setFlags(start_new_main_intent.FLAG_ACTIVITY_NEW_TASK |
                        start_new_main_intent.FLAG_ACTIVITY_CLEAR_TASK);  // clear activity  history
                startActivity(start_new_main_intent);
                break;

            case R.id.profile_setting:
                // open menu
                break;

            default:
                throw new RuntimeException("Unknown option");
        }
        return super.onOptionsItemSelected(item);
    }
}
