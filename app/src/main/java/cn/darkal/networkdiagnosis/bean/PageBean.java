package cn.darkal.networkdiagnosis.bean;

/**
 * Created by xuzhou on 2016/9/5.
 */

public class PageBean {
    private int index;
    private String name;
    private String count;
    private Boolean isSelected = true;


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count+"请求";
    }

    public Integer getCountInt() {
        try{
            return Integer.parseInt(count);
        }catch (Exception e){
            return 0;
        }
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
