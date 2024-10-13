package com.nat.app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nat.app.R;
import com.nat.app.adapters.CustomExpandableListAdapter;
import com.nat.app.adapters.HomeAdapter;
import com.nat.app.databases.FirebaseDatabaseSingleton;
import com.nat.app.models.BangTinItem;
import com.nat.app.models.QuestionModel;
import com.nat.app.utils.Constants;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {
    RecyclerView recyclerView;

    LinearLayout feed_back_emial, feature, rating, use;

    TextView user_name;

    public HomeFragment() {
        //
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);
        recyclerView = v.findViewById(R.id.bangtin);
        feed_back_emial = v.findViewById(R.id.feed_back_emial);
        feature = v.findViewById(R.id.feature);
        rating = v.findViewById(R.id.rating);
        use = v.findViewById(R.id.use);

        user_name = v.findViewById(R.id.user_name);
        user_name.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());

        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://khangdienhcm.com/gioi-thieu-ve-khang-dien/"));
                startActivity(browserIntent);
            }
        });

        feature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://khangdienhcm.com/du-an-jamila-khang-dien/"));
                startActivity(browserIntent);
            }
        });

        use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + "JAMILA-er đăng ký sử dụng dịch vụ" + "&body=" + "Hồ bơi/BBQ ngoài trời/Gym" + "&to=" + "nguyenanhtuan1232@gmail.com");
                mailIntent.setData(data);
                startActivity(Intent.createChooser(mailIntent, "Send mail..."));
            }
        });
        feed_back_emial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + "JAMILA-er phản ánh" + "&body=" + "" + "&to=" + "nguyenanhtuan1232@gmail.com");
                mailIntent.setData(data);
                startActivity(Intent.createChooser(mailIntent, "Send mail..."));
            }
        });
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);


        FirebaseDatabaseSingleton.getInstance().getDatabaseReference().child("home").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BangTinItem> questionModels = new ArrayList<>();
                for (DataSnapshot snapshotI : snapshot.getChildren()) {

                    BangTinItem model = snapshotI.getValue(BangTinItem.class);

                    if (model != null) {
                        questionModels.add(model);
                    }
                }
                HomeAdapter adapter = new HomeAdapter(getContext(), questionModels);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageCarousel carousel = v.findViewById(R.id.carousel);
        carousel.registerLifecycle(getLifecycle());

        List<CarouselItem> list = new ArrayList<>();

// Image URL with caption
        list.add(
                new CarouselItem(
                        "https://khangdienhcm.com/wp-content/uploads/2017/04/DU-AN-CAN-HO-JAMILA-KHANG-DIEN-HCM.jpg",
                        "Jamila Summary"
                )
        );
        list.add(
                new CarouselItem(
                        "https://khangdienhcm.com/wp-content/uploads/2017/04/VI-TRI-DU-AN-JAMILA-KHANG-DIEN-HOUSE.gif",
                        "Address on map"
                )
        );
        list.add(
                new CarouselItem(
                        "https://khangdienhcm.com/wp-content/uploads/2017/04/TIEN-ICH-DU-AN-JAMILA-KHANG-DIEN-HT-GROUP.jpg",
                        "Feature on project"
                )
        );
        list.add(
                new CarouselItem(
                        "https://khangdienhcm.com/wp-content/uploads/2017/04/MAT-BANG-DU-AN-JAMILA-KHANG-DIEN.jpg",
                        "Blocks definition"
                )
        );
        list.add(
                new CarouselItem(
                        "https://khangdienhcm.com/wp-content/uploads/2018/02/TIEN-DO-DU-AN-JAMILA-KHANG-DIEN-THANG-02-KHANG-DIEN-HCM-01.jpg",
                        "Construction"
                )
        );
        list.add(
                new CarouselItem(
                        "https://khangdienhcm.com/wp-content/uploads/2018/02/TIEN-DO-DU-AN-JAMILA-KHANG-DIEN-THANG-02-KHANG-DIEN-HCM-02.jpg",
                        "Construction"
                )
        );
        list.add(
                new CarouselItem(
                        "https://khangdienhcm.com/wp-content/uploads/2018/02/TIEN-DO-DU-AN-JAMILA-KHANG-DIEN-THANG-02-KHANG-DIEN-HCM-04.jpg",
                        "Construction"
                )
        );
        list.add(
                new CarouselItem(
                        "https://khangdienhcm.com/wp-content/uploads/2018/02/TIEN-DO-DU-AN-JAMILA-KHANG-DIEN-THANG-02-KHANG-DIEN-HCM-03.jpg",
                        "Construction"
                )
        );
        carousel.setData(list);
        return v;
    }
}
