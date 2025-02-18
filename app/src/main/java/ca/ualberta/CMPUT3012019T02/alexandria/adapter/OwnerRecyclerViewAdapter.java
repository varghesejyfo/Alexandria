package ca.ualberta.CMPUT3012019T02.alexandria.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ca.ualberta.CMPUT3012019T02.alexandria.R;
import ca.ualberta.CMPUT3012019T02.alexandria.fragment.UserBookFragment;
import ca.ualberta.CMPUT3012019T02.alexandria.model.OwnerListItem;
import ca.ualberta.CMPUT3012019T02.alexandria.model.holder.OwnerViewHolder;

/**
 * Set up RecyclerView for Book Catalogue
 * Code based on code from https://youtu.be/T_QfRU-A3s4 on 03/04/2019
 */
public class OwnerRecyclerViewAdapter extends RecyclerView.Adapter<OwnerViewHolder> {

    private Context mContext;
    private List<OwnerListItem> mOwnerListItem;

    /**
     * Instantiates a new Owner recycler view adapter.
     *
     * @param mContext    the application context
     * @param mOwnerListItem   the owner list
     */
    public OwnerRecyclerViewAdapter(Context mContext,
                                    List<OwnerListItem> mOwnerListItem) {
        this.mContext = mContext;
        this.mOwnerListItem = mOwnerListItem;
    }

    @NonNull
    @Override
    public OwnerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View mView;

        //sets up view
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_owner, viewGroup, false);
        OwnerViewHolder mViewHolder = new OwnerViewHolder(mView);

        //list click to switch fragments
        mViewHolder.itemOwner.setOnClickListener((View v) -> {
            UserBookFragment frag = new UserBookFragment();
            frag.setArguments(dataBundler(mViewHolder));

            //switch fragments with bundled data
            FragmentTransaction fragmentTransaction =
                    ((FragmentActivity) mView.getContext()).getSupportFragmentManager()
                            .beginTransaction();

            fragmentTransaction.replace(R.id.fragment_container, frag, "Owner Book");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerViewHolder myViewHolder, int position) {

        Bitmap bitmap = mOwnerListItem.get(position).getOwnerPic();
        RoundedBitmapDrawable drawable = null;
        if (bitmap != null) {
            Bitmap squareBitmap = Bitmap.createBitmap(bitmap, 0, 0, Math.min(bitmap.getWidth(), bitmap.getHeight()), Math.min(bitmap.getWidth(), bitmap.getHeight()));
            drawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), squareBitmap);
            drawable.setCornerRadius(Math.min(bitmap.getWidth(), bitmap.getHeight()));
            drawable.setAntiAlias(true);
        }

        if (drawable != null) {
            myViewHolder.ownerPic.setBackgroundResource(0);
        } else {
            myViewHolder.ownerPic.setBackgroundResource(R.drawable.ic_profile);
        }
        myViewHolder.ownerPic.setImageDrawable(drawable);
        myViewHolder.ownerUsername.setText(mOwnerListItem.get(position).getOwnerUsername());

        if (mOwnerListItem.get(position).getStatus() != null) {
            switch (mOwnerListItem.get(position).getStatus()) {
                case "available":
                    myViewHolder.statusIcon.setImageResource(R.drawable.ic_status_available);
                    break;
                case "requested":
                    myViewHolder.statusIcon.setImageResource(R.drawable.ic_status_requested);
                    break;
                default:
                    throw new RuntimeException("Status out of bounds");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mOwnerListItem.size();
    }

    //bundles data for fragment switching
    private Bundle dataBundler(OwnerViewHolder mViewHolder) {
        Bundle b = new Bundle();

        b.putString("isbn", mOwnerListItem.get(mViewHolder.getAdapterPosition()).getIsbn());
        b.putString("ownerId", mOwnerListItem.get(mViewHolder.getAdapterPosition()).getOwnerId());
        b.putString("status", mOwnerListItem.get(mViewHolder.getAdapterPosition()).getStatus());

        return b;
    }

}
