package cn.darkal.networkdiagnosis.Adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.darkal.networkdiagnosis.Bean.PageBean;
import cn.darkal.networkdiagnosis.BR;
import cn.darkal.networkdiagnosis.R;

/**
 * Created by Darkal on 2016/9/5.
 */

public class PageFilterAdapter extends BaseAdapter{

    public PageFilterAdapter(List<PageBean> pageBeenList){
        this.pageBeenList = pageBeenList;
    }

    private List<PageBean> pageBeenList;

    @Override
    public int getCount() {
        return pageBeenList.size();
    }

    @Override
    public Object getItem(int position) {
        return pageBeenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return pageBeenList.get(position).getIndex();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewDataBinding listItemBinding;
        if (convertView != null) {
            listItemBinding = (ViewDataBinding) convertView.getTag();
        } else {
            listItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_pages, parent, false);
            convertView = listItemBinding.getRoot();
            convertView.setTag(listItemBinding);
        }
        listItemBinding.setVariable(BR.pages,pageBeenList.get(position));
        listItemBinding.executePendingBindings();
//        listItemBinding.setButtonclick(new ButtonClick(MainActivity.this,position));
        return convertView;
    }
}
