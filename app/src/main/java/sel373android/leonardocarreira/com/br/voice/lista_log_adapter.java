package sel373android.leonardocarreira.com.br.voice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by leonardoboscocarreira on 16/06/17.
 */
public class lista_log_adapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listHeader;
    private Map<String, ArrayList<String>> listHashMap;

    public lista_log_adapter(Context context, List<String> listHeader, Map<String, ArrayList<String>> listHashMap) {
        this.listHeader = listHeader;
        this.listHashMap = listHashMap;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return listHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(listHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String ttitle = (String) getGroup(groupPosition);
        convertView = LayoutInflater.from(context).inflate(R.layout.expandable_log_header, null);
        TextView tvGroup = (TextView) convertView.findViewById(R.id.logGroup);
        tvGroup.setText(ttitle);
        tvGroup.setTextSize(20);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convetView, ViewGroup parent) {
        String  item = (String) getChild(groupPosition, childPosition);
        convetView = LayoutInflater.from(context).inflate(R.layout.expandable_log_item, null);
        TextView tvChild = (TextView) convetView.findViewById(R.id.logItem);
        tvChild.setText(item);
        return convetView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
