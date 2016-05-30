package demo.kristine.flickrsearch.app.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CustomRecyclerView extends android.support.v7.widget.RecyclerView {

  private View mEmptyView, mLoadMoreView;
  private LinearLayoutManager mLinearLayoutManager;
  private OnScrollToEndListener mOnScrollToEndListener;

  public void setOnScrollToEndListener(OnScrollToEndListener listener) throws ClassCastException {
    try {
      mLinearLayoutManager = (LinearLayoutManager) getLayoutManager();
      if (listener != null) {
        mOnScrollToEndListener = listener;
        addOnScrollListener(mScrollListener);
      }
    } catch (ClassCastException e) {
      Log.e("utils", "OnScrollToEndListener works with LinearLayoutManager only.");
      e.printStackTrace();
    }
  }

  public void setEmptyView(View emptyView) {
    mEmptyView = emptyView;
    checkIfEmpty();
  }

  public void setLoadMoreView(View loadMoreView) {
    mLoadMoreView = loadMoreView;
  }

  public void setLoadingMore(boolean isLoadingMore) {
    if (mLoadMoreView != null) mLoadMoreView.setVisibility(isLoadingMore ? VISIBLE : GONE);
  }

  private void checkIfEmpty() {
    if (mEmptyView != null && getAdapter() != null) {
      final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
      mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
      setVisibility(emptyViewVisible ? GONE : VISIBLE);
    }
  }

  @Override
  public void setAdapter(Adapter adapter) {
    final Adapter oldAdapter = getAdapter();
    if (oldAdapter != null) oldAdapter.unregisterAdapterDataObserver(observer);
    super.setAdapter(adapter);

    if (adapter != null) adapter.registerAdapterDataObserver(observer);
    checkIfEmpty();
  }

  private final AdapterDataObserver observer = new AdapterDataObserver() {
    @Override
    public void onChanged() {
      checkIfEmpty();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      checkIfEmpty();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      checkIfEmpty();
    }
  };

  private OnScrollListener mScrollListener = new OnScrollListener() {
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      if (dy > 0) {
        int visibleItemCount = mLinearLayoutManager.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();
        int pastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();

        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
          setLoadingMore(true);
          mOnScrollToEndListener.onScrollToEnd(CustomRecyclerView.this);
        }
      }
    }
  };

  public CustomRecyclerView(Context context) {
    super(context);
  }

  public CustomRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public interface OnScrollToEndListener {
    void onScrollToEnd(CustomRecyclerView recyclerView);
  }
}
