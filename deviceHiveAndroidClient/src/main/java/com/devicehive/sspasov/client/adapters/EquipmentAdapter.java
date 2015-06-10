package com.devicehive.sspasov.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dataart.android.devicehive.EquipmentData;
import com.dataart.android.devicehive.EquipmentState;
import com.devicehive.sspasov.client.R;

import java.util.List;

/**
 * Created by toni on 10.06.15.
 */
public class EquipmentAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<EquipmentData> equipmentList;
    private List<EquipmentState> equipmentStateList;

    public EquipmentAdapter(Context context, List<EquipmentData> equipmentList,
                            List<EquipmentState> equipmentStateList) {
        this.equipmentList = equipmentList;
        this.equipmentStateList = equipmentStateList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return equipmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return equipmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.equipment_list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tv_equipment_name);
            holder.code = (TextView) convertView.findViewById(R.id.tv_equipment_code);
            holder.type = (TextView) convertView.findViewById(R.id.tv_equipment_type);
            //holder.data = (TextView) convertView.findViewById(R.id.equipment_data_text_view);
            //holder.state = (TextView) convertView.findViewById(R.id.equipment_state_text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final EquipmentData equipmentData = equipmentList.get(position);
        holder.name.setText(equipmentData.getName());
        holder.code.setText(equipmentData.getCode());
        holder.type.setText(equipmentData.getType());
        //holder.data.setText(equipmentData.getData() != null ? equipmentData.getData()
        //    .toString() : "--");
        //final EquipmentState state = getEquipmentState(equipmentData);
        //holder.state.setText(equipmentStateAsString(state));
        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView code;
        TextView type;
        //TextView data;
        //TextView state;
    }

    private EquipmentState getEquipmentState(EquipmentData equipmentData) {
        for (EquipmentState state : equipmentStateList) {
            if (state.getEquipmentCode()
                    .equals(equipmentData.getCode())) {
                return state;
            }
        }
        return null;
    }

    private static String equipmentStateAsString(EquipmentState state) {
        if (state != null) {
            return "" + state.getParameters();
        } else {
            return "--";
        }
    }
}