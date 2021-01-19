package com.example.forcatapp.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumbers = new HashMap<>();
    private final HashMap<Integer, String> mFragmentNames = new HashMap<>();

    public SectionStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmentName){
        mFragmentList.add(fragment);
        mFragments.put(fragment,mFragmentList.size()-1);
        mFragmentNumbers.put(fragmentName,mFragmentList.size()-1);
        mFragmentNames.put(mFragmentList.size()-1, fragmentName);
    }
    /*
    * 프래그먼트의 이름으로 프래그먼트 리스트의 번호를 리턴함
    @param fragmentName 프래그먼트의 이름
    @return mFragmentNumbers(이름) 프래그먼트의 번호
     */
    public Integer getFragmentNumbers(String fragmentName){
        if(mFragmentNumbers.containsKey(fragmentName)){
            return mFragmentNumbers.get(fragmentName);
        } else {
            return null;
        }
    }
    /*
 * 프래그먼트 오브젝트로 프래그먼트 리스트의 번호를 리턴함
 @param fragmentName 프래그먼트의 이름
 @return mFragmentNumbers(이름) 프래그먼트의 번호
  */
    public Integer getFragmentNumbers(Fragment fragment){
        if(mFragmentNumbers.containsKey(fragment)){
            return mFragmentNumbers.get(fragment);
        } else {
            return null;
        }
    }

    /*
* 프래그먼트의 번호에 해당하는 프래그먼트의 이름을 리턴함
@param fragmentNumber 프래그먼트의 번호
@return mFragmentNames(번호) 프래그먼트의 이름
*/
    public String getFragmentName(Integer fragmentNumber){
        if(mFragmentNames.containsKey(fragmentNumber)){
            return mFragmentNames.get(fragmentNumber);
        } else {
            return null;
        }
    }
}
