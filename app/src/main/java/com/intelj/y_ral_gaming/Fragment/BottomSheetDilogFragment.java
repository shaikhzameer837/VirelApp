package com.intelj.y_ral_gaming.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.intelj.y_ral_gaming.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BottomSheetDilogFragment extends BottomSheetDialogFragment {
    private ViewsSliderAdapter mAdapter;
    private TextView[] dots;
    private int[] layouts;
    View rootView;
    ViewPager2 viewPager;
    ViewPager viewPager1;
    Set<String> mylist;
    LinearLayout layoutDots;
    Button btnNext;
    public BottomSheetDilogFragment(Set<String> mylist) {
        this.mylist = mylist;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.bootmsheetdialog_fragment, container, false);
//        viewPager = rootView.findViewById(R.id.view_pager);
        viewPager1 = rootView.findViewById(R.id.view_pager1);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getParentFragmentManager());
        viewPagerAdapter.add(new BottomSheetFragment1(),"hello");
        viewPagerAdapter.add(new BottomSheetFragment2(),"hello");
        viewPager1.setAdapter(viewPagerAdapter);
        btnNext = rootView.findViewById(R.id.btn_next);
        init();
        return rootView;
    }
    private void init() {
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.activity_demo,
                R.layout.fragment_bottom_sheet2/*,
                R.layout.slide_three,
                R.layout.slide_four*/};

        /*mAdapter = new ViewsSliderAdapter();
        viewPager.setAdapter(mAdapter);
        viewPager.registerOnPageChangeCallback(pageChangeCallback);*/



//        btnSkip.setOnClickListener(v -> launchHomeScreen());
//
        btnNext.setOnClickListener(v -> {
            // checking for last page
            // if last page home screen will be launched
            int current = getItem(+1);
            if (current < layouts.length) {
                // move to next screen
                viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        });
//
//        iconMore.setOnClickListener(view -> {
//            showMenu(view);
//        });

        // adding bottom dots
//        addBottomDots(0);
    }

    /*
     * Adds bottom dots indicator
     * */
    /*private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getActivity());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }*/

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        Toast.makeText(getActivity(), "Done", Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

   /* private void showMenu(View view) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.pager_transformers, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_orientation) {
                viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
            } else {
                viewPager.setPageTransformer(Utils.getTransformer(item.getItemId()));
                viewPager.setCurrentItem(0);
                viewPager.getAdapter().notifyDataSetChanged();
            }
            return false;
        });
        popup.show();
    }*/

    /*
     * ViewPager page change listener
     */
    ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
//            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
//            if (position == layouts.length - 1) {
//                // last page. make button text to GOT IT
//                btnNext.setText(getString(R.string.start));
//                btnSkip.setVisibility(View.GONE);
//            } else {
//                // still pages are left
//                btnNext.setText(getString(R.string.next));
//                btnSkip.setVisibility(View.VISIBLE);
//            }
        }
    };

    public class ViewsSliderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public ViewsSliderAdapter() {
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(viewType, parent, false);
            return new SliderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemViewType(int position) {
            return layouts[position];
        }

        @Override
        public int getItemCount() {
            return layouts.length;
        }

        public class SliderViewHolder extends RecyclerView.ViewHolder {
            public TextView title, year, genre;

            public SliderViewHolder(View view) {
                super(view);
            }
        }
    }



    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm)
        {
            super(fm);
        }

        public void add(Fragment fragment, String title)
        {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull @Override public Fragment getItem(int position)
        {
            return fragments.get(position);
        }

        @Override public int getCount()
        {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            return fragmentTitle.get(position);
        }
    }

}
