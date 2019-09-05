package cn.darkal.networkdiagnosis.Adapter;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.darkal.networkdiagnosis.Activity.ChangeFilterActivity;
import cn.darkal.networkdiagnosis.BR;
import cn.darkal.networkdiagnosis.Bean.ResponseFilterRule;
import cn.darkal.networkdiagnosis.R;

/**
 * Created by Darkal on 2016/9/5.
 */

public class ContentFilterAdapter extends BaseAdapter {
    ChangeFilterActivity changeFilterActivity;
    private List<ResponseFilterRule> ruleList;

    public ContentFilterAdapter(ChangeFilterActivity changeFilterActivity, List<ResponseFilterRule> ruleList) {
        this.ruleList = ruleList;
        this.changeFilterActivity = changeFilterActivity;
    }

    @Override
    public int getCount() {
        return ruleList.size();
    }

    @Override
    public Object getItem(int position) {
        return ruleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewDataBinding listItemBinding;
        if (convertView != null) {
            listItemBinding = (ViewDataBinding) convertView.getTag();
        } else {
            listItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_filter, parent, false);
            convertView = listItemBinding.getRoot();
            convertView.setTag(listItemBinding);
        }
        listItemBinding.setVariable(BR.pages, ruleList.get(position));
        listItemBinding.executePendingBindings();
//        listItemBinding.cli(new ButtonClick(MainActivity.this,position));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFilterActivity.showDialog(ruleList.get(position));
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(changeFilterActivity);
                builder.setTitle("请确认是否清除该注入项?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ruleList.remove(ruleList.get(position));
                        ContentFilterAdapter.this.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.create().show();

                return false;
            }
        });
        return convertView;
    }
}
