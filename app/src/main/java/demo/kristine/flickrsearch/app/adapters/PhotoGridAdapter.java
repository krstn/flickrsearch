package demo.kristine.flickrsearch.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import demo.kristine.flickrsearch.R;

/**
 * Created by kristine on 5/30/16.
 */
public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoGridAdapter.ViewHolder> {

  private List<String> mImageUrls;
  private OnItemClickListener mOnItemClickListener;
  private Context mContext;

  public PhotoGridAdapter(Context context, List<String> imageUrl) {
    mContext = context;
    mImageUrls = imageUrl;
  }

  public void setOnItemClickListener(OnItemClickListener listener) {
    mOnItemClickListener = listener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_grid, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, final int position) {
    Glide.with(mContext).load(mImageUrls.get(position))
        .thumbnail(0.5f)
        .crossFade()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(holder.thumbnail);

    if (mOnItemClickListener != null) {
      holder.thumbnail.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          mOnItemClickListener.onItemClick(view, position);
        }
      });
    }
  }

  @Override
  public int getItemCount() {
    return mImageUrls.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView thumbnail;

    public ViewHolder(View view) {
      super(view);
      thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(View view, int position);
  }
}