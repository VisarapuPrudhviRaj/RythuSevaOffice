package nk.bluefrog.rythusevaoffice.activities.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import nk.bluefrog.rythusevaoffice.R;


public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.ViewHolder> {

    private final int mValues[];
    private final String mTitle[];
    private final OnListFragmentInteractionListener mListener;


    public MenuRecyclerViewAdapter(int items[], String titles[], OnListFragmentInteractionListener listener) {
        mValues = items;
        mTitle = titles;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sample_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.iv_image.setImageResource(mValues[position]);
        holder.tv_title.setText(mTitle[position]);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView iv_image;
        public final TextView tv_title;

        ViewHolder(View view) {
            super(view);
            mView = view;
            iv_image = view.findViewById(R.id.iv_image);
            tv_title = view.findViewById(R.id.tv_title);
        }
    }
}
