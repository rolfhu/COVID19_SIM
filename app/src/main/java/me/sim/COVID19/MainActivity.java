package me.sim.COVID19;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import sim.app.Controller;
import sim.app.PatientMgr;
import sim.app.PopulationMgr;
import sim.app.TagMgr;
import sim.worlds.FactoryMgr;

public class MainActivity extends AppCompatActivity {

    private Controller m_MainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        FactoryMgr.getInstance().createFactory("China");

        m_MainController = Controller.getInstance();
        m_MainController.Init(this);

        for (int i=0;i<80;i++)
        {
            //m_MainController.runOneDay();
        }

        PatientMgr patMgr = PatientMgr.getInstance();
        PopulationMgr popMgr = PopulationMgr.getInstance();
        TagMgr tagMgr = TagMgr.getInstance();
    }

}
