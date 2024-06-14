package com.tournaments.grindbattles.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.databinding.ActivitySlotSelectionBinding;

public class SlotSelectionActivity extends AppCompatActivity {

    ActivitySlotSelectionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySlotSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}