package ca.ualberta.CMPUT3012019T02.alexandria.model.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import ca.ualberta.CMPUT3012019T02.alexandria.R;

/**
 * Sets up item ids for Recycler views, which require attributes to be public.
 */
public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

    /**
     * The Item message.
     */
    public ConstraintLayout itemMessage;
    /**
     * The TextView content.
     */
    public TextView tvContent;
    /**
     * The TextView sender username.
     */
    public TextView tvTimeStamp;

    /**
     * Instantiates a new Message view holder.
     *
     * @param itemView the item view
     */
    public ReceivedMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        itemMessage = itemView.findViewById(R.id.item_message);
        tvTimeStamp = itemView.findViewById(R.id.message_time_received);
        tvContent = itemView.findViewById(R.id.message_content_received);
    }
}
