package com.example.android.somenews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends ArrayAdapter<News> {
    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.date)
    TextView tvDate;
    @BindView(R.id.name)
    TextView tvName;
    @BindView(R.id.section)
    TextView tvSection;

    public NewsAdapter(Context context, ArrayList<News> guardianNews) {
        super(context, 0, guardianNews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_item, parent, false);
            ButterKnife.bind(this, listItemView);

            // Get the News object located at this position in the list
            News currentNews = getItem(position);
            //setting news details to proper textView
            tvTitle.setText(currentNews.getTitle());
            tvName.setText(currentNews.getAuthorName());
            tvSection.setText(currentNews.getSectionName());
            // Setting proper format for date
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            tvDate.setText(sdf.format(currentNews.getDate()));
        }
        return listItemView;
    }
}