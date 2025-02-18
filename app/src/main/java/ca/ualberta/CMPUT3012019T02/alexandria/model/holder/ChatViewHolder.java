package ca.ualberta.CMPUT3012019T02.alexandria.model.holder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ca.ualberta.CMPUT3012019T02.alexandria.R;

/**
 * The chat view holder
 * Sets up item ids for Recycler views, which require attributes to be public.
 */
public class ChatViewHolder extends RecyclerView.ViewHolder {

    /**
     * The Item chat.
     */
    public RelativeLayout itemChat;
    /**
     * The TextView chat receiver username.
     */
    public TextView tvChatReceiverUsername;
    /**
     * The ImageView chat user pic.
     */
    public ImageView ivChatUserPic;
    /**
     * The ImageView read status.
     */
    public ImageView ivReadStatus;

    /**
     * Instantiates a new Chat view holder.
     *
     * @param itemView the item view
     */
    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);

        itemChat = itemView.findViewById(R.id.item_chat);
        tvChatReceiverUsername = itemView.findViewById(R.id.chat_receiver_username);
        ivChatUserPic = itemView.findViewById(R.id.chat_user_pic);
        ivReadStatus = itemView.findViewById(R.id.read_status);
    }
}
