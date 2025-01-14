package ca.ualberta.CMPUT3012019T02.alexandria.fragment.exchange;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import ca.ualberta.CMPUT3012019T02.alexandria.R;
import ca.ualberta.CMPUT3012019T02.alexandria.adapter.TabsAdapter;
import ca.ualberta.CMPUT3012019T02.alexandria.fragment.SearchFragment;
/**
 * Created as a fragment by MainActivity
 * Creates tab fragments, BorrowedFragment, AcceptedFragment,RequestedFragment
 * code for tabs adapted from https://youtu.be/bNpWGI_hGGg at 02/25/2019
 */
public class ExchangeFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_exchange,null);

        ViewPager mViewPager = rootView.findViewById(R.id.exchange_pager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = rootView.findViewById(R.id.exchange_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Button mButton = rootView.findViewById(R.id.exchange_search);
        mButton.setOnClickListener(this);

        return rootView;
    }

    /**
     * Sets up tabs in the Fragment
     * @param viewPager the child fragment view that allows swipe
     */

    private void setupViewPager(ViewPager viewPager) {
        TabsAdapter adapter = new TabsAdapter(getChildFragmentManager());
        adapter.addFragment(new BorrowedFragment(), "Borrowed");
        adapter.addFragment(new AcceptedFragment(), "Accepted");
        adapter.addFragment(new RequestedFragment(),"Requested");
        viewPager.setAdapter(adapter);
    }

    /**
     * Switches to the search fragment and adds the current to back stack
     * @param v the Fragment itself
     */
    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,
                new SearchFragment(), "Search Fragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        Objects.requireNonNull(getActivity()).findViewById(R.id.navigation).setVisibility(View.VISIBLE);
    }
}
