package ca.ualberta.CMPUT3012019T02.alexandria.fragment.exchange;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ca.ualberta.CMPUT3012019T02.alexandria.R;
import ca.ualberta.CMPUT3012019T02.alexandria.activity.MainActivity;
import ca.ualberta.CMPUT3012019T02.alexandria.adapter.BookDataAdapter;
import ca.ualberta.CMPUT3012019T02.alexandria.adapter.BookRecyclerViewAdapter;
import ca.ualberta.CMPUT3012019T02.alexandria.model.BookListItem;

/**
 * Fragment for filtering book list that has the status of Requested
 */

public class RequestedFragment extends Fragment implements Observer {

    private List<BookListItem> bookListings = new ArrayList<>();
    private BookRecyclerViewAdapter bookAdapter;
    private MainActivity mainActivity;
    private View view;

    /**
     * Sets up the RecyclerView for the Fragment
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_exchange_requested,null);

        RecyclerView mRecyclerView = rootView.findViewById(R.id.requested_recycler);
        bookAdapter = new BookRecyclerViewAdapter(
                getContext(), bookListings,"UserBookFragment");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(bookAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BookDataAdapter.getInstance().addObserver(this);
        update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (mainActivity == null) {
            return;
        }
        mainActivity.runOnUiThread(() -> {
            bookListings.clear();
            for (BookListItem book : BookDataAdapter.getInstance().getMyBorrowedBooksList()) {
                if (book.getStatus().equals("requested")) {
                    bookListings.add(book);
                }
            }
            bookAdapter.notifyDataSetChanged();

            //spinner
            if (!mainActivity.isFetching()) {
                RecyclerView mRecyclerView = view.findViewById(R.id.requested_recycler);
                TextView emptyView = view.findViewById(R.id.empty_view);
                ProgressBar spinner = view.findViewById(R.id.requested_spinner);

                spinner.setVisibility(View.GONE);

                if (bookListings.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}


