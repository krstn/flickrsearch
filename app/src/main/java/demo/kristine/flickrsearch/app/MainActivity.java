package demo.kristine.flickrsearch.app;

import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import demo.kristine.flickrsearch.R;
import demo.kristine.flickrsearch.util.ConnectivityUtil;

public class MainActivity extends AppCompatActivity {

  private View mMainLoading;
  private EditText mSearchEdit;
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
        SearchParameters parameters = new SearchParameters();
        parameters.setText(mSearchEdit.getText().toString());
        try {
          showMainLoadingView(true);
          PhotoList photoList = mFlickr.getPhotosInterface().search(parameters, Config.SEARCH_RESULTS_COUNT, 1);
          if (photoList != null) {
            Log.d("flickr", "result " + photoList.size());
          }
        } catch (Exception e) {
          e.printStackTrace();
          Snackbar.make(findViewById(R.id.main_container), "Ooops! Search Error.", Snackbar.LENGTH_LONG).show();
        }
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
        onEnterNewSearch();
        return true;
      }
      return false;
    }
  };
}
