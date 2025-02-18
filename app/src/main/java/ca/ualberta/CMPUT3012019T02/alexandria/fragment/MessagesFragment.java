package ca.ualberta.CMPUT3012019T02.alexandria.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ca.ualberta.CMPUT3012019T02.alexandria.R;
import ca.ualberta.CMPUT3012019T02.alexandria.adapter.ChatRecyclerViewAdapter;
import ca.ualberta.CMPUT3012019T02.alexandria.controller.ImageController;
import ca.ualberta.CMPUT3012019T02.alexandria.controller.UserController;
import ca.ualberta.CMPUT3012019T02.alexandria.model.chatroom.ChatRoomItem;
import java9.util.concurrent.CompletableFuture;

/**
 * Fragment that shows a list of clickable chat rooms to chat with another user.
 * Implements Chat
 */
public class MessagesFragment extends Fragment {

    private List<ChatRoomItem> chatRoomList = new ArrayList<>();
    private Map<String, RoundedBitmapDrawable> profileImageMap = new HashMap<>();

    private Activity activity;
    private DatabaseReference chatRoomListRef;
    private ValueEventListener chatListener;
    private ChatRecyclerViewAdapter adapter;

    private ImageController imageController = ImageController.getInstance();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }


    /**
     * set data listener on fragment creation
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String myId = UserController.getInstance().getMyId();
        chatRoomListRef = FirebaseDatabase.getInstance().getReference().child("users").child(myId).child("chatRoomList");

        // adding current users' chatRooms to chatRoomList
        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatRoomList.clear();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        ChatRoomItem chatRoom = childSnapshot.getValue(ChatRoomItem.class);
                        chatRoomList.add(chatRoom);
                        CompletableFuture.runAsync(() -> {
                            if (!isAdded()) {
                                return;
                            }
                            try {
                                String user1PicId = chatRoom.getUser1UserPic();
                                String user2PicId = chatRoom.getUser2UserPic();
                                String user1Id = chatRoom.getUser1Id();
                                String user2Id = chatRoom.getUser2Id();

                                Bitmap user1Image = null;
                                if (user1PicId != null && !user1PicId.equals("temp")) {
                                    user1Image = imageController.getImage(user1PicId)
                                            .get(5, TimeUnit.SECONDS);
                                }

                                Bitmap user2Image = null;
                                if (user2PicId != null && !user2PicId.equals("temp")) {
                                    user2Image = imageController.getImage(user2PicId)
                                            .get(5, TimeUnit.SECONDS);
                                }

                                Bitmap finalUser1Image = user1Image;
                                Bitmap finalUser2Image = user2Image;
                                if (finalUser1Image != null) {
                                    Bitmap squareBitmap1 = Bitmap.createBitmap(finalUser1Image,
                                            0, 0, Math.min(finalUser1Image.getWidth(),
                                                    finalUser1Image.getHeight()),
                                            Math.min(finalUser1Image.getWidth(),
                                                    finalUser1Image.getHeight()));

                                    RoundedBitmapDrawable drawable1 = RoundedBitmapDrawableFactory
                                            .create(getResources(), squareBitmap1);

                                    drawable1.setCornerRadius(Math.min(finalUser1Image.getWidth(),
                                            finalUser1Image.getHeight()));
                                    drawable1.setAntiAlias(true);

                                    profileImageMap.put(user1Id, drawable1);

                                }

                                if (finalUser2Image != null) {
                                    Bitmap squareBitmap2 = Bitmap.createBitmap(finalUser2Image,
                                            0, 0, Math.min(finalUser2Image.getWidth(),
                                                    finalUser2Image.getHeight()),
                                            Math.min(finalUser2Image.getWidth(),
                                                    finalUser2Image.getHeight()));

                                    RoundedBitmapDrawable drawable2 = RoundedBitmapDrawableFactory
                                            .create(getResources(), squareBitmap2);

                                    drawable2.setCornerRadius(Math.min(finalUser2Image.getWidth(),
                                            finalUser2Image.getHeight()));
                                    drawable2.setAntiAlias(true);

                                    profileImageMap.put(user2Id, drawable2);

                                }
                                getActivity().runOnUiThread(() -> {
                                    adapter.updateProfileImageMap(profileImageMap);
                                    adapter.notifyDataSetChanged();
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                    }
                } else {
                    chatRoomListRef.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw new RuntimeException("Could not load data from database " + databaseError);
            }
        };
        chatRoomListRef.addValueEventListener(chatListener);


    }

    /**
     * Assigns ChatRecyclerViewAdapter to the chat recycler view, and creates the view
     * <p>
     * {@inheritDoc}
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view of each chat room item in recycler view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, null);

        RecyclerView mRecyclerView = rootView.findViewById(R.id.chat_recycler);
        adapter = new ChatRecyclerViewAdapter(getContext(), chatRoomList, profileImageMap);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    /**
     * refreshes the chat room list every 2 seconds, with a .5 second delay on fragment start.
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        /**
         * Code adapted from https://stackoverflow.com/questions/37404521/how-to-make-listview-to-refresh-after-every-5-sec-when-data-come-from-a-server
         */

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.updateChatRoomList(chatRoomList);
                adapter.updateProfileImageMap(profileImageMap);
                adapter.notifyDataSetChanged();
                handler.postDelayed(this, 2000);
            }
        }, 500);
    }

    @Override
    public void onStop() {
        super.onStop();

        chatRoomListRef.removeEventListener(chatListener);
    }

}
