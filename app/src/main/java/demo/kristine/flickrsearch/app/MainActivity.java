package demo.kristine.flickrsearch.app;

import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import java.util.ArrayList;
import java.util.List;

import demo.kristine.flickrsearch.R;
import demo.kristine.flickrsearch.app.adapters.PhotoGridAdapter;
import demo.kristine.flickrsearch.util.ConnectivityUtil;

public class MainActivity extends AppCompatActivity {

  private View mMainLoading;
  private EditText mSearchEdit;
  private PhotoGridAdapter mAdapter;
  private RecyclerView mRecyclerView;
  private final List<Photo> mPhotos = new ArrayList<>();
  private final List<String> mImageUrls = new ArrayList<>();
  private Snackbar mNoInternerSnackbar;
  private Flickr mFlickr;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mFlickr = new Flickr(Config.FLICKR_API_KEY);
    initViews();
  }

  private void initViews() {
    mSearchEdit = (EditText) findViewById(R.id.edit_search);
    mSearchEdit.setOnKeyListener(mSearchKeyListener);

    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_search_results);
    mAdapter = new PhotoGridAdapter(getApplicationContext(), mImageUrls);
    mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    mRecyclerView.setAdapter(mAdapter);

    mMainLoading = findViewById(R.id.main_loading);
  }

  private void showSettingsActivity() {
    Intent intent = new Intent(Settings.ACTION_SETTINGS);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  private void onEnterNewSearch() {
    if (!mSearchEdit.getText().toString().isEmpty()) {
      if (ConnectivityUtil.isNetworkConnected(getApplicationContext())) {
        if (mNoInternerSnackbar != null && mNoInternerSnackbar.isShown())
          mNoInternerSnackbar.dismiss();
        startSearch();
      } else {
        mNoInternerSnackbar = Snackbar.make(findViewById(R.id.main_container), R.string.error_no_internet, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.action_settings, new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                showSettingsActivity();
              }
            });
        mNoInternerSnackbar.show();
      }
    }
  }

  private void startSearch() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        showMainLoadingView(true);
        mPhotos.clear();
        mImageUrls.clear();
        SearchParameters parameters = new SearchParameters();
        parameters.setText(mSearchEdit.getText().toString());

        try {
          PhotoList photoList = mFlickr.getPhotosInterface().search(parameters, 0, 0);
          if (photoList != null) {
            for (int i = 0; i < photoList.size(); i++) {
              mPhotos.add(photoList.get(i));
              mImageUrls.add(photoList.get(i).getSmall320Url());
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          Snackbar.make(findViewById(R.id.main_container), "Ooops! Search Error.", Snackbar.LENGTH_LONG).show();
        }

        mMainLoading.post(new Runnable() {
          @Override
          public void run() {
            mAdapter.notifyDataSetChanged();
          }
        });
        showMainLoadingView(false);
      }
    }).start();
  }

  private void showMainLoadingView(final boolean show) {
    mMainLoading.post(new Runnable() {
      @Override
      public void run() {
        mMainLoading.setVisibility(show ? View.VISIBLE : View.GONE);
      }
    });
  }

  private View.OnKeyListener mSearchKeyListener = new View.OnKeyListener() {
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
      if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        onEnterNewSearch();
        return true;
      }
      return false;
    }
  };
}
