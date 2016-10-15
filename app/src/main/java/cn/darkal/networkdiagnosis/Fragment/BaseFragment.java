package cn.darkal.networkdiagnosis.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.darkal.networkdiagnosis.Activity.MainActivity;

/**
 * Created by xuzhou on 2016/8/10.
 */
public abstract class BaseFragment extends Fragment {

    protected BackHandledInterface mBackHandledInterface;

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    public abstract boolean onBackPressed();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(getActivity() instanceof BackHandledInterface)){
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        }else{
            this.mBackHandledInterface = (BackHandledInterface)getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶
//        mBackHandledInterface.setSelectedFragment(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).changeStateBar(this);
            }
            if (mBackHandledInterface != null) {
                //告诉FragmentActivity，当前Fragment在栈顶
                mBackHandledInterface.setSelectedFragment(this);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).changeStateBar(this);
        }
    }
}
