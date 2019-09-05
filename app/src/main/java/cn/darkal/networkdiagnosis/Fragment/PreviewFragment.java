package cn.darkal.networkdiagnosis.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.darkal.networkdiagnosis.Activity.HarDetailActivity;
import cn.darkal.networkdiagnosis.Activity.MainActivity;
import cn.darkal.networkdiagnosis.R;
import cn.darkal.networkdiagnosis.SysApplication;
import cn.darkal.networkdiagnosis.View.RecycleViewDivider;

public class PreviewFragment extends BaseFragment {

    static PreviewFragment previewFragment;
    @BindView(R.id.rv_preview)
    RecyclerView recyclerView;
    HarLog harLog;
    List<HarEntry> harEntryList = new ArrayList<>();
    PreviewAdapter previewAdapter;
    Boolean isHiddenHID = false;

    public static PreviewFragment getInstance() {
        if (previewFragment == null) {
            previewFragment = new PreviewFragment();
        }
        return previewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        ButterKnife.bind(this, view);

        if (SysApplication.isInitProxy) {
            harLog = ((SysApplication) getActivity().getApplication()).proxy.getHar().getLog();
            harEntryList.addAll(harLog.getEntries());
        }
        recyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), RecycleViewDivider.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(previewAdapter = new PreviewAdapter());

        if (((MainActivity) getActivity()).searchView != null) {
            ((MainActivity) getActivity()).searchView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
//            ((MainActivity) getActivity()).navigationView.setCheckedItem(R.id.nav_preview);
            notifyHarChange();
        }
    }

    public void notifyHarChange() {
        if (previewAdapter != null) {
            harLog = ((MainActivity) getActivity()).getFiltedHar().getLog();
            harEntryList.clear();
            harEntryList.addAll(harLog.getEntries());
            previewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public void filterItem(CharSequence s) {
        if (previewAdapter != null) {
            previewAdapter.getFilter().filter(s);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // 这里为了解决返回后焦点在搜索栏的bug
        if (recyclerView != null) {
            recyclerView.requestFocus();
            if (((MainActivity) getActivity()).searchView != null) {
                filterItem(((MainActivity) getActivity()).searchView.getQuery());
            }
        }
    }

    private class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.MyViewHolder> implements Filterable {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_preview, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            HarEntry harEntry = harEntryList.get(position);
            holder.rootView.setOnClickListener(new ClickListner(harEntry));
            holder.tv.setText(harEntry.getRequest().getUrl());
            if (harEntry.getResponse().getStatus() > 400) {
                holder.iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_black_24dp));
            } else if (harEntry.getResponse().getStatus() > 300) {
                holder.iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_black_24dp));
            } else if (harEntry.getResponse().getContent().getMimeType().contains("image")) {
                holder.iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_black_24dp));
            } else {
                holder.iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_description_black_24dp));
            }
            holder.detailTextView.setText("Status:" + harEntry.getResponse().getStatus() +
                    " Size:" + harEntry.getResponse().getBodySize() +
                    "Bytes Time:" + harEntry.getTime() + "ms");
        }

        @Override
        public int getItemCount() {
            return harEntryList.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    harLog = ((MainActivity) getActivity()).getFiltedHar().getLog();
                    //初始化过滤结果对象
                    FilterResults results = new FilterResults();
                    //假如搜索为空的时候，将复制的数据添加到原始数据，用于继续过滤操作
                    if (results.values == null) {
                        harEntryList.clear();
                        harEntryList.addAll(harLog.getEntries());
                    }
                    //关键字为空的时候，搜索结果为复制的结果
                    if (constraint == null || constraint.length() == 0) {
                        results.values = harLog.getEntries();
                        results.count = harLog.getEntries().size();
                    } else {
                        String prefixString = constraint.toString();
                        final int count = harEntryList.size();
                        //用于存放暂时的过滤结果
                        final ArrayList<HarEntry> newValues = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            final HarEntry value = harEntryList.get(i);
                            String url = value.getRequest().getUrl();
                            // 假如含有关键字的时候，添加
                            if (url.contains(prefixString)) {
                                newValues.add(value);
                            } else {
                                //过来空字符开头
                                String[] words = prefixString.split(" ");

                                for (String word : words) {
                                    if (url.contains(word)) {
                                        newValues.add(value);
                                        break;
                                    }
                                }
                            }
                        }
                        results.values = newValues;
                        results.count = newValues.size();
                    }
                    return results;//过滤结果
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    harEntryList.clear();//清除原始数据
                    if (results.values instanceof List) {
                        harEntryList.addAll((List<HarEntry>) results.values);//将过滤结果添加到这个对象
                    }
                    if (results.count > 0) {
                        previewAdapter.notifyDataSetChanged();//有关键字的时候刷新数据
                    } else {
                        //关键字不为零但是过滤结果为空刷新数据
                        if (constraint.length() != 0) {
                            previewAdapter.notifyDataSetChanged();
                            return;
                        }
                        //加载复制的数据，即为最初的数据
                        harEntryList.addAll(harLog.getEntries());
                        previewAdapter.notifyDataSetChanged();
                    }
                }
            };
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;
            TextView detailTextView;
            View rootView;
            ImageView iconView;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.tv_url);
                detailTextView = (TextView) view.findViewById(R.id.tv_detail);
                rootView = view;
                iconView = (ImageView) view.findViewById(R.id.iv_icon);
            }
        }
    }

    public class ClickListner implements View.OnClickListener {
        HarEntry harEntry;

        public ClickListner(HarEntry harEntry) {
            this.harEntry = harEntry;
        }

        @Override
        public void onClick(View view) {
            if (harLog.getEntries().indexOf(harEntry) >= 0) {
                isHiddenHID = true;
                Intent intent = new Intent(getContext(), HarDetailActivity.class);
                intent.putExtra("pos", ((SysApplication) getActivity().getApplication()).proxy.
                        getHar().getLog().getEntries().indexOf(harEntry));
                getActivity().startActivity(intent);
            }
        }
    }
}
