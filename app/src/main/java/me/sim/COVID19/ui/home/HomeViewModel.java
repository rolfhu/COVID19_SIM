package me.sim.COVID19.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private MutableLiveData<String> mProgressText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        mProgressText = new MutableLiveData<>();
        mProgressText.setValue("当前日期");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getProgressText() {
        return mProgressText;
    }

    public void setProgressText(String strText)
    {
        mProgressText.setValue(strText);
    }
}