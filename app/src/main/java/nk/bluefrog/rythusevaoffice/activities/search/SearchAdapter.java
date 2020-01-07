package nk.bluefrog.rythusevaoffice.activities.search;

import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;

/**
 * Created by rajkumar on 24/12/18.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> implements Filterable {

    Context context;
    LinearLayout ll_nodata;
    private List<SearchModel> searchFilterdata;
    private List<SearchModel> searchFilterdataList;
    private SearchRVAdapterListener listener;

    private boolean isHighLightWords = true;
    private String mSearchWord;

    public SearchAdapter(Context context, List<SearchModel> searchFilterdata, SearchRVAdapterListener listener, LinearLayout ll_nodata) {
        this.context = context;
        this.searchFilterdata = searchFilterdata;
        this.listener = listener;
        this.ll_nodata = ll_nodata;
        this.searchFilterdataList = new ArrayList<>();
    }

    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_farmer_search, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        SearchModel model = searchFilterdataList.get(position);

        holder.tv_Name.setText(model.getName());
        holder.tv_AadhaarID.setText(String.format("Aadhaar : %s", model.getAdharID()));
        holder.tv_mobile.setText(model.getMobile());
       //Constants.loadImageElseWhite(context, model.getImgUrl(), holder.iv_mainupload, holder.progressBarMain);

        loadImageElseWhite(model.getImgUrl(), holder.iv_mainupload,holder.progressBarMain);


        if (mSearchWord != null && isHighLightWords) {
            holder.tv_Name.setText(StringsHelper.highlightLCS(model.name, getmSearchWord(), getColor(R.color.red)));
        } else {
            holder.tv_Name.setText(model.name);

        }

    }


    public void loadImageElseWhite(String image, ImageView imageView, final ProgressBar progress) {
        System.out.println("image:" + image);


        try {
            if ((image != null) && image.length() > 0) {

                if(!image.contentEquals("NA")) {

                    Glide.with(context).load(image).
                            listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    progress.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    progress.setVisibility(View.GONE);
                                    return false;
                                }
                            }).thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).error(R.mipmap.ic_launcher)
                            .into(imageView);
                }else{
                    Glide.with(context)
                            .load("")
                            .placeholder(R.mipmap.ic_launcher)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(imageView);
                    progress.setVisibility(View.GONE);
                }
            } else {
                //progress.setVisibility(View.GONE);
                Glide.with(context)
                        .load("")
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
            }
        } catch (IllegalArgumentException e) {

        }

    }

    private int getColor(@ColorRes int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(colorResId, null);
        } else {
            return context.getResources().getColor(colorResId);
        }
    }


    public String getmSearchWord() {
        return mSearchWord;
    }

    public SearchAdapter setmSearchWord(String mSearchWord) {
        this.mSearchWord = mSearchWord;
        return this;
    }

    @Override
    public int getItemCount() {
        return searchFilterdataList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    setmSearchWord(charString);
                    searchFilterdataList = new ArrayList<>();
                } else {
                    setmSearchWord(charString);
                    List<SearchItems> searchItems = new ArrayList<>();
                   // List<SearchModel> filteredList = new ArrayList<>();
                    for (SearchModel row : searchFilterdata) {
                        int per = StringsHelper.lock_match(row.getName().toLowerCase(), charString.toLowerCase());

                       // String nameData = StringsHelper.lcs(row.getName().toLowerCase(), charString.toLowerCase());

                        if (per > 0 || row.getMobile().contains(charSequence) ) {
                            //filteredList.add(row);
                            searchItems.add(new SearchItems(per,row));
                        }

                       /* if (row.getName().toLowerCase().contains(charSequence.toString().toLowerCase()) || row.getMobile().contains(charSequence) || row.getAdharID().contains(charSequence)) {
                            filteredList.add(row);
                        }*/
                    }

                    Collections.sort(searchItems);

                    for (int i = 0; i <searchItems.size() ; i++) {
                        if(!searchFilterdataList.contains(searchItems.get(i).searchModel))
                        searchFilterdataList.add(searchItems.get(i).searchModel);
                    }



                    //searchFilterdataList = filteredList;


                }



                FilterResults filterResults = new FilterResults();
                filterResults.values = searchFilterdataList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                searchFilterdataList = (ArrayList<SearchModel>) filterResults.values;

                if (searchFilterdataList.size() > 0) {
                    ll_nodata.setVisibility(View.GONE);
                } else {
                    ll_nodata.setVisibility(View.VISIBLE);
                }

                notifyDataSetChanged();

                //mRecyclerView.getRecycledViewPool().clear();
            }
        };
    }


    public interface SearchRVAdapterListener {
        void onSelected(SearchModel contact);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView iv_mainupload;
        ProgressBar progressBarMain;
        TextView tv_Name, tv_AadhaarID, tv_mobile;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_mainupload = itemView.findViewById(R.id.iv_mainupload);
            progressBarMain = itemView.findViewById(R.id.progressBarMain);
            tv_Name = itemView.findViewById(R.id.tv_Name);
            tv_AadhaarID = itemView.findViewById(R.id.tv_AadhaarID);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onSelected(searchFilterdataList.get(getAdapterPosition()));
                }
            });

        }
    }
}
