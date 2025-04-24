package com.jumbo.carry.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jumbo.carry.R;
import com.jumbo.carry.fragments.BuscarFragment;
import com.jumbo.carry.fragments.ChatFragment;
import com.jumbo.carry.fragments.InicioFragment;
import com.jumbo.carry.fragments.PerfilFragment;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.TokenProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.jumbo.carry.utils.MensajeVistoHelper;

public class HomeActivity extends AppCompatActivity {

    //------------------------BARRA DE NAVEGACIÓN-----------------------
    BottomNavigationView bottomNavigation;
    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        mTokenProvider= new TokenProvider();
        mAuthProvider= new AuthProvider();
        mUsersProvider= new UsersProvider();
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new InicioFragment());
        crearToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mUsersProvider!=null){
            mUsersProvider.actualizarEstado(mAuthProvider.getUid(),true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mUsersProvider!=null){
            mUsersProvider.actualizarEstado(mAuthProvider.getUid(), false);
        }
    }


    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_inicio:
                            openFragment(InicioFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_guardados:
                            openFragment(BuscarFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_chat:
                            openFragment(ChatFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_perfil:
                            openFragment(PerfilFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };
    private void crearToken(){
        mTokenProvider.crear(mAuthProvider.getUid());
    }
}