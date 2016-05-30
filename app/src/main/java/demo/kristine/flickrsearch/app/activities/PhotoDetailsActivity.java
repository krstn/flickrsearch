package demo.kristine.flickrsearch.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import demo.kristine.flickrsearch.R;
import demo.kristine.flickrsearch.app.Config;
import demo.kristine.flickrsearch.app.adapters.PhotoGridAdapter;
import demo.kristine.flickrsearch.util.ConnectivityUtil;

public class PhotoDetailsActivity extends AppCompatActivity {

  public static final String EXTRA_PHOTO = "extra_photo";
  private ImageView mPhotoImg;
  private Photo mPhoto;
  private Flickr mFlickr;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Serializable extra = getIntent().getSerializableExtra(EXTRA_PHOTO);
    if (extra != null) {
      mPhoto = (Photo) extra;
      mFlickr = new Flickr(Config.FLICKR_API_KEY);

      setContentView(R.layout.activity_photo_details);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(mPhoto.getTitle());
      initViews();
    } else finish();
  }

  private void initViews() {
    mPhotoImg = (ImageView) findViewById(R.id.img_photo);
    ((TextView) findViewById(R.id.txt_title)).setText(mPhoto.getTitle());
    ((TextView) findViewById(R.id.txt_user)).setText(mPhoto.getOwner().getUsername());

    Glide.with(getApplicationContext()).load(mPhoto.getMedium800Url())
        .thumbnail(0.5f)
        .crossFade()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(((ImageView) findViewById(R.id.img_photo)));

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          final Photo photo = mFlickr.getPhotosInterface().getPhoto(mPhoto.getId());
          mPhotoImg.post(new Runnable() {
            @Override
            public void run() {
              if (photo.getOwner().getUsername() != null)
                ((TextView) findViewById(R.id.txt_user)).setText("by " + photo.getOwner().getUsername());
              if (photo.getDatePosted() != null)
                ((TextView) findViewById(R.id.txt_date)).setText(new SimpleDateFormat("MMMM d, yyyy").format(photo.getDatePosted()));
            }
          });
        } catch (Exception e) {
          e.printStackTrace();
          Snackbar.make(findViewById(R.id.main_container), "Ooops! Search Error.", Snackbar.LENGTH_LONG).show();
        }
      }
    }).start();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

}
