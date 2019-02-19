package kickr.gaa.ArrayAdapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmcmanus on 4/11/2018.
 */

public class CustomArrayAdapter extends ArrayAdapter implements Filterable {

    List<String> allCodes;
    List<String> originalCodes;
    StringFilter filter;

    public CustomArrayAdapter(Context context, int resource, List<String> keys) {
        super(context, resource, keys);
        allCodes = keys;
        originalCodes = keys;
    }

    public int getCount() {
        try {
            return allCodes.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public Object getItem(int position) {
        return allCodes.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new StringFilter();
    }

    private class StringFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<String> list = originalCodes;

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<String>(count);
            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allCodes = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }
}
