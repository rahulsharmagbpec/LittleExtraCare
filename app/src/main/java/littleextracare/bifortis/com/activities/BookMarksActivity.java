package littleextracare.bifortis.com.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

@SuppressWarnings("ALL")
public class BookMarksActivity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_book_marks, container, false);

        LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout item = (LinearLayout) view.findViewById(R.id.linearLayoutBookmark);
        View child1 = inflater1.inflate(R.layout.layout_bookmark, null);
        View child2 = inflater1.inflate(R.layout.layout_bookmark, null);

        child1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookMarksProfile.class);
                startActivity(intent);
            }
        });

        child2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookMarksProfile.class);
                startActivity(intent);
            }
        });

        item.addView(child1);
        item.addView(child2);

        return view;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_marks);

        LinearLayout item = (LinearLayout)findViewById(R.id.linearLayoutBookmark);
        View child1 = getLayoutInflater().inflate(R.layout.layout_bookmark, null);
        View child2 = getLayoutInflater().inflate(R.layout.layout_bookmark, null);

        child1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookMarksActivity.this, BookMarksProfile.class);
                startActivity(intent);
            }
        });

        child2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookMarksActivity.this, BookMarksProfile.class);
                startActivity(intent);
            }
        });

        item.addView(child1);
        item.addView(child2);
    }*/
}
