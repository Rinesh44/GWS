package com.example.android.gurkha;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Shaakya on 5/31/2017.
 */

public class listAdapter extends BaseAdapter implements Filterable {
    private Activity context;
    static ArrayList<Contacts> Person;
    private LayoutInflater inflater;
    private ValueFilter valueFilter;
    private ArrayList<Contacts> mStringFilterList;


    public listAdapter(Activity context, ArrayList<Contacts> Person) {
        super();
        this.context = context;
        this.Person = Person;
        mStringFilterList = Person;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getFilter();
    }

    @Override
    public int getCount() {
        return Person.size();
    }

    @Override
    public Object getItem(int position) {
        return Person.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder {
        TextView tname, tsurname;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item, null);
            holder.tname = (TextView) convertView.findViewById(R.id.name);
            holder.tsurname = (TextView) convertView.findViewById(R.id.surname);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.tname.setText("" + Person.get(position).getName());
        holder.tsurname.setText("" + "" + Person.get(position).getSurname());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {

            valueFilter = new ValueFilter();
        }

        return valueFilter;
    }

    private class ValueFilter extends Filter {

        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Contacts> filterList = new ArrayList<Contacts>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        Contacts contacts = new Contacts();
                        contacts.setName(mStringFilterList.get(i).getName());
                        contacts.setId(mStringFilterList.get(i).getSurname());
                        filterList.add(contacts);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;
        }


        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            Person = (ArrayList<Contacts>) results.values;
            notifyDataSetChanged();
        }

    }

}

