package ca.ualberta.CMPUT3012019T02.alexandria.model.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import ca.ualberta.CMPUT3012019T02.alexandria.R;

/**
 * Sets up item ids for Recycler views, which require attributes to be public.
 */
public class SentLocationMessageViewHolder extends RecyclerView.ViewHolder {

    /**
     * The Item message.
     */
    public ConstraintLayout itemMessage;

    /**
     * The TextView sender username.
     */
    public TextView tvTimeStamp;

    /**
     * The ImageView image
     */
    public ImageView ivLocationImage;

    /**
     * Instantiates a new Message view holder.
     *
     * @param itemView the item view
     */
    public SentLocationMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        itemMessage = itemView.findViewById(R.id.item_location_message_sent);
        tvTimeStamp = itemView.findViewById(R.id.message_time_sent);
        ivLocationImage = itemView.findViewById(R.id.image_sent_location);
    }
}
