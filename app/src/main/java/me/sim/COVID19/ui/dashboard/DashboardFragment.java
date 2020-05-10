package me.sim.COVID19.ui.dashboard;

import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import me.sim.COVID19.R;
import sim.app.CollectDataMgr;
import sim.app.Controller;
import sim.tags.stage.DeadStage;
import sim.tags.stage.ImmuneStage;
import sim.tags.stage.IncubationStage;
import sim.tags.stage.IntensiveStage;
import sim.tags.stage.OnsetStage;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        LineChart chart = (LineChart) root.findViewById(R.id.chart);

        ArrayList<Object> dataIncubation = CollectDataMgr.getInstance().getDataListByName("Area_中国_"+ IncubationStage.getFullName());
        ArrayList<Object> dataIntensive = CollectDataMgr.getInstance().getDataListByName("Area_中国_"+ IntensiveStage.getFullName());
        ArrayList<Object> dataOnset = CollectDataMgr.getInstance().getDataListByName("Area_中国_"+ OnsetStage.getFullName());
        ArrayList<Object> dataImmune = CollectDataMgr.getInstance().getDataListByName("Area_中国_"+ ImmuneStage.getFullName());
        ArrayList<Object> dataDead = CollectDataMgr.getInstance().getDataListByName("Area_中国_"+ DeadStage.getFullName());

        List<Entry> entriesInfected = new ArrayList<Entry>();
        List<Entry> entriesCured = new ArrayList<Entry>();
        List<Entry> entriesDead = new ArrayList<Entry>();

        for(int i = 1; i< Controller.getInstance().getSimDays(); i++)
        {
            Integer nIncubation = (Integer) dataIncubation.get(i);
            Integer nIntensive = (Integer) dataIntensive.get(i);
            Integer nOnset = (Integer) dataOnset.get(i);
            Integer nImmune = (Integer) dataImmune.get(i);
            Integer nDead = (Integer) dataDead.get(i);

            entriesInfected.add(new Entry(i, (nIncubation+nIntensive+nOnset+nImmune+nDead)));
            entriesCured.add(new Entry(i, (nImmune)));
            entriesDead.add(new Entry(i, (nDead)));
        }

        LineDataSet dataSetInfected = new LineDataSet(entriesInfected, "已感染"); // add entries to dataset
        dataSetInfected.setColor(0xFFFF0000);
        dataSetInfected.setCircleColor(0xFFFF0000);
        dataSetInfected.setValueTextColor(0xFFFF0000);

        LineDataSet dataSetCured = new LineDataSet(entriesCured, "已治愈"); // add entries to dataset
        dataSetCured.setColor(0xFF00FF00);
        dataSetCured.setCircleColor(0xFF00FF00);
        dataSetCured.setValueTextColor(0xFF00FF00);

        LineDataSet dataSetDead = new LineDataSet(entriesDead, "已死亡"); // add entries to dataset
        dataSetDead.setColor(0xFF000000);
        dataSetDead.setCircleColor(0xFF000000);
        dataSetDead.setValueTextColor(0xFF000000);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSetInfected);
        dataSets.add(dataSetCured);
        dataSets.add(dataSetDead);

        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.setBorderColor(0xFFFF8F00);
        chart.setDrawBorders(true);
        chart.invalidate();

        return root;
    }
}
